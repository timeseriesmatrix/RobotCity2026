import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { AuthUser } from "./api";
import { apiFetch } from "./api";

interface ShopSourceDraft {
  host: string;
  port: string;
  dbname: string;
  user: string;
  password: string;
  user_env: string;
  pass_env: string;
  conninfo: string;
}

type ShopSourceKind = "pos" | "expense";

interface ShopDraft {
  shop_id: number;
  name: string;
  description: string;
  timezone: string;
  categories: string[];
  pos: ShopSourceDraft;
  expense: ShopSourceDraft;
}

interface UserDraft {
  username: string;
  display_name: string;
  password: string;
}

interface SettingsStateResponse {
  default_shop_id?: number;
  shops?: ShopDraft[];
  users?: Array<{ username?: string; display_name?: string }>;
  root_user?: { username?: string; display_name?: string };
  paths?: { shop_config?: string; users_config?: string };
  status_message?: string;
  error?: string;
}

interface SettingsViewProps {
  active: boolean;
  currentUser: AuthUser | null;
  onShopsSaved?: (shops: ShopDraft[], defaultShopId: number | null) => void;
}

interface SourceActionState {
  testing?: boolean;
  initiating?: boolean;
  message?: string;
  error?: string;
}

function emptySource(): ShopSourceDraft {
  return {
    host: "localhost",
    port: "5432",
    dbname: "",
    user: "",
    password: "",
    user_env: "",
    pass_env: "",
    conninfo: "",
  };
}

function emptyShop(nextShopId: number): ShopDraft {
  return {
    shop_id: nextShopId,
    name: `Shop ${nextShopId}`,
    description: "",
    timezone: "Pacific/Efate",
    categories: ["Others"],
    pos: emptySource(),
    expense: emptySource(),
  };
}

function normalizeCategories(rawCategories: string[] | null | undefined): string[] {
  const categories: string[] = ["Others"];
  for (const rawCategory of rawCategories || []) {
    const trimmed = (rawCategory || "").trim();
    if (!trimmed) continue;
    const normalized = trimmed.toLowerCase();
    if (normalized === "other" || normalized === "others" || normalized === "misc" || normalized === "miscellaneous" || normalized === "uncategorized") {
      continue;
    }
    if (categories.some((category) => category.toLowerCase() === normalized)) {
      continue;
    }
    categories.push(trimmed);
  }
  return [categories[0], ...categories.slice(1).sort((lhs, rhs) => lhs.localeCompare(rhs))];
}

function normalizeSourceForSave(source: Partial<ShopSourceDraft> | null | undefined): ShopSourceDraft {
  return {
    ...emptySource(),
    ...(source || {}),
  };
}

function normalizeShopsForSave(shops: ShopDraft[]): ShopDraft[] {
  return shops.map((shop) => ({
    shop_id: Number(shop.shop_id || 0),
    name: shop.name,
    description: shop.description,
    timezone: shop.timezone,
    categories: normalizeCategories(shop.categories),
    pos: normalizeSourceForSave(shop.pos),
    expense: normalizeSourceForSave(shop.expense),
  }));
}

function normalizeUsersForSave(users: UserDraft[]): UserDraft[] {
  return users.map((user) => ({
    username: user.username,
    display_name: user.display_name,
    password: user.password,
  }));
}

function settingsSignature(shops: ShopDraft[], users: UserDraft[], defaultShopId: number | null): string {
  return JSON.stringify({
    default_shop_id: defaultShopId || 0,
    shops: normalizeShopsForSave(shops),
    users: normalizeUsersForSave(users),
  });
}

export default function SettingsView({ active, currentUser, onShopsSaved }: SettingsViewProps) {
  const [shops, setShops] = useState<ShopDraft[]>([]);
  const [defaultShopId, setDefaultShopId] = useState<number | null>(null);
  const [users, setUsers] = useState<UserDraft[]>([]);
  const [savedSettingsSignature, setSavedSettingsSignature] = useState("");
  const [paths, setPaths] = useState<{ shop_config?: string; users_config?: string }>({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [statusMsg, setStatusMsg] = useState<string | null>(null);
  const [hasLoaded, setHasLoaded] = useState(false);
  const [categoryInputs, setCategoryInputs] = useState<Record<string, string>>({});
  const [sourceActionStates, setSourceActionStates] = useState<Record<string, SourceActionState>>({});

  const isRoot = currentUser?.role === "root";
  const onShopsSavedRef = useRef(onShopsSaved);
  const shopCardRefs = useRef<Array<HTMLDivElement | null>>([]);
  const pendingScrollShopIdRef = useRef<number | null>(null);

  useEffect(() => {
    onShopsSavedRef.current = onShopsSaved;
  }, [onShopsSaved]);

  useEffect(() => {
    setHasLoaded(false);
    setSavedSettingsSignature("");
    setDefaultShopId(null);
  }, [currentUser?.username]);

  const loadSettings = useCallback(async () => {
    if (!isRoot) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch("/settings_state");
      const data = (await res.json()) as SettingsStateResponse;
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      const nextShops = Array.isArray(data.shops)
        ? data.shops.map((shop) => ({
            shop_id: Number(shop.shop_id || 0),
            name: shop.name || "",
            description: shop.description || "",
            timezone: shop.timezone || "",
            categories: normalizeCategories(Array.isArray((shop as { categories?: string[] }).categories) ? (shop as { categories?: string[] }).categories : []),
            pos: { ...emptySource(), ...(shop.pos || {}) },
            expense: { ...emptySource(), ...(shop.expense || {}) },
          }))
        : [];

      const nextUsers = Array.isArray(data.users)
        ? data.users.map((user) => ({
            username: user.username || "",
            display_name: user.display_name || "",
            password: "",
          }))
        : [];
      const nextDefaultShopId =
        Number.isFinite(Number(data.default_shop_id)) && Number(data.default_shop_id) > 0
          ? Number(data.default_shop_id)
          : null;

      setShops(nextShops);
      setDefaultShopId(nextDefaultShopId);
      setCategoryInputs({});
      setUsers(nextUsers);
      setSavedSettingsSignature(settingsSignature(nextShops, nextUsers, nextDefaultShopId));
      setPaths(data.paths || {});
      setStatusMsg(null);
      setSourceActionStates({});
      onShopsSavedRef.current?.(nextShops, nextDefaultShopId);
      setHasLoaded(true);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to load settings";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, [isRoot]);

  useEffect(() => {
    if (!active || !isRoot || hasLoaded) return;
    void loadSettings();
  }, [active, hasLoaded, isRoot, loadSettings]);

  const nextShopId = useMemo(() => {
    const maxId = shops.reduce((max, shop) => Math.max(max, shop.shop_id || 0), 0);
    return maxId + 1;
  }, [shops]);

  const currentSettingsSignature = useMemo(
    () => settingsSignature(shops, users, defaultShopId),
    [defaultShopId, shops, users]
  );
  const settingsChanged = hasLoaded && currentSettingsSignature !== savedSettingsSignature;

  const updateShop = useCallback((index: number, updater: (shop: ShopDraft) => ShopDraft) => {
    setShops((prev) => prev.map((shop, i) => (i === index ? updater(shop) : shop)));
  }, []);

  const updateUser = useCallback((index: number, updater: (user: UserDraft) => UserDraft) => {
    setUsers((prev) => prev.map((user, i) => (i === index ? updater(user) : user)));
  }, []);

  const sourceActionKey = useCallback((shopIndex: number, kind: ShopSourceKind) => `${shopIndex}:${kind}`, []);

  const addCategory = useCallback((shopIndex: number) => {
    const key = String(shops[shopIndex]?.shop_id || shopIndex);
    const nextCategory = (categoryInputs[key] || "").trim();
    if (!nextCategory) return;
    updateShop(shopIndex, (current) => ({
      ...current,
      categories: normalizeCategories([...(current.categories || []), nextCategory]),
    }));
    setCategoryInputs((prev) => ({ ...prev, [key]: "" }));
  }, [categoryInputs, shops, updateShop]);

  const removeCategory = useCallback((shopIndex: number, category: string) => {
    if (category === "Others") return;
    updateShop(shopIndex, (current) => ({
      ...current,
      categories: normalizeCategories((current.categories || []).filter((entry) => entry.toLowerCase() !== category.toLowerCase())),
    }));
  }, [updateShop]);

  const handleAddShop = useCallback(() => {
    const next = emptyShop(nextShopId);
    pendingScrollShopIdRef.current = next.shop_id;
    setShops((prev) => [...prev, next]);
  }, [nextShopId]);

  useEffect(() => {
    const pendingShopId = pendingScrollShopIdRef.current;
    if (!pendingShopId) return;
    const shopIndex = shops.findIndex((shop) => shop.shop_id === pendingShopId);
    if (shopIndex < 0) return;

    window.requestAnimationFrame(() => {
      const target = shopCardRefs.current[shopIndex];
      if (!target) return;
      target.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" });
      target.focus({ preventScroll: true });
    });
    pendingScrollShopIdRef.current = null;
  }, [shops]);

  const handleSave = useCallback(async () => {
    if (!isRoot || saving || !settingsChanged) return;
    setSaving(true);
    setError(null);
    setStatusMsg("Saving settings…");
    try {
      const payload = {
        default_shop_id: defaultShopId || 0,
        shops: normalizeShopsForSave(shops),
        users: normalizeUsersForSave(users),
      };

      const res = await apiFetch("/settings_save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      const data = (await res.json()) as SettingsStateResponse;
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      const nextShops = Array.isArray(data.shops)
        ? data.shops.map((shop) => ({
            shop_id: Number(shop.shop_id || 0),
            name: shop.name || "",
            description: shop.description || "",
            timezone: shop.timezone || "",
            categories: normalizeCategories(Array.isArray((shop as { categories?: string[] }).categories) ? (shop as { categories?: string[] }).categories : []),
            pos: { ...emptySource(), ...(shop.pos || {}) },
            expense: { ...emptySource(), ...(shop.expense || {}) },
          }))
        : [];
      const nextUsers = Array.isArray(data.users)
        ? data.users.map((user) => ({
            username: user.username || "",
            display_name: user.display_name || "",
            password: "",
          }))
        : [];
      const nextDefaultShopId =
        Number.isFinite(Number(data.default_shop_id)) && Number(data.default_shop_id) > 0
          ? Number(data.default_shop_id)
          : null;

      setShops(nextShops);
      setDefaultShopId(nextDefaultShopId);
      setCategoryInputs({});
      setUsers(nextUsers);
      setSavedSettingsSignature(settingsSignature(nextShops, nextUsers, nextDefaultShopId));
      setPaths(data.paths || {});
      setStatusMsg((data as SettingsStateResponse & { status_message?: string }).status_message || "Settings saved.");
      setSourceActionStates({});
      onShopsSavedRef.current?.(nextShops, nextDefaultShopId);
      setHasLoaded(true);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to save settings";
      setError(msg);
      setStatusMsg(null);
    } finally {
      setSaving(false);
    }
  }, [isRoot, saving, settingsChanged, shops, users]);

  const handleTestConnection = useCallback(async (shopIndex: number, kind: ShopSourceKind) => {
    if (!isRoot) return;
    const shop = shops[shopIndex];
    if (!shop) return;
    const key = sourceActionKey(shopIndex, kind);

    setSourceActionStates((prev) => ({
      ...prev,
      [key]: {
        ...prev[key],
        testing: true,
        error: "",
        message: "Testing connection…",
      },
    }));

    try {
      const res = await apiFetch("/settings_save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          action: "test_connection",
          source_kind: kind,
          source: shop[kind],
        }),
      });
      const data = (await res.json()) as { message?: string; error?: string };
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      setSourceActionStates((prev) => ({
        ...prev,
        [key]: {
          ...prev[key],
          testing: false,
          error: "",
          message: data.message || "Connection succeeded.",
        },
      }));
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Connection test failed";
      setSourceActionStates((prev) => ({
        ...prev,
        [key]: {
          ...prev[key],
          testing: false,
          error: msg,
          message: "",
        },
      }));
    }
  }, [isRoot, shops, sourceActionKey]);

  const handleInitExpenseDb = useCallback(async (shopIndex: number) => {
    if (!isRoot) return;
    const shop = shops[shopIndex];
    if (!shop) return;
    const key = sourceActionKey(shopIndex, "expense");

    setSourceActionStates((prev) => ({
      ...prev,
      [key]: {
        ...prev[key],
        initiating: true,
        error: "",
        message: "Creating or initiating expense DB…",
      },
    }));

    try {
      const res = await apiFetch("/settings_save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          action: "init_expense_db",
          source: shop.expense,
        }),
      });
      const data = (await res.json()) as { message?: string; error?: string };
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      setSourceActionStates((prev) => ({
        ...prev,
        [key]: {
          ...prev[key],
          initiating: false,
          error: "",
          message: data.message || "Expense database and tracker schema are ready.",
        },
      }));
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Expense DB init failed";
      setSourceActionStates((prev) => ({
        ...prev,
        [key]: {
          ...prev[key],
          initiating: false,
          error: msg,
          message: "",
        },
      }));
    }
  }, [isRoot, shops, sourceActionKey]);

  if (!isRoot) {
    return (
      <main className="main">
        <header className="main-header">
          <div className="main-header-title">
            <span className="pill">Admin</span>
            <h1>Settings</h1>
          </div>
          <div className="main-header-subtitle">Root access is required.</div>
        </header>
        <div className="main-body">
          <div className="placeholder">Sign in as root to manage shops, users, and database connections.</div>
        </div>
      </main>
    );
  }

  return (
    <main className="main">
      <header className="main-header">
        <div className="settings-header-row">
          <div>
            <div className="main-header-title">
              <span className="pill">Admin</span>
              <h1>Settings</h1>
            </div>
            <div className="main-header-subtitle">
              Root can manage shops, normal users, and shop database connections here.
            </div>
          </div>
          <div className="settings-header-actions">
            <button type="button" className="secondary-button action-button" onClick={() => void loadSettings()} disabled={loading || saving}>
              {loading ? "Reloading…" : "Reload"}
            </button>
            <button type="button" className="submit-button" onClick={handleSave} disabled={saving || loading || !settingsChanged}>
              {saving ? "Saving…" : "Save settings"}
            </button>
          </div>
        </div>
      </header>

      <div className="main-body settings-main-body">
        <div className="settings-layout">
          <section className="settings-side-column">
            <div className="card">
              <div className="card-header">
                <div>
                  <div className="card-title">Access</div>
                  <div className="card-subtitle">Root login is fixed in backend. Normal users are stored in `users.json`.</div>
                </div>
              </div>
              <div className="card-body settings-meta">
                <div><strong>Root user:</strong> root</div>
                <div><strong>Shop config:</strong> {paths.shop_config || "—"}</div>
                <div><strong>User config:</strong> {paths.users_config || "—"}</div>
                {statusMsg && <div className="status-msg">{statusMsg}</div>}
                {error && <div className="status-msg">{error}</div>}
              </div>
            </div>

            <div className="card">
              <div className="card-header card-header--results">
                <div>
                  <div className="card-title">Users</div>
                  <div className="card-subtitle">Create normal users. Leave password blank on existing users to keep it unchanged.</div>
                </div>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => setUsers((prev) => [...prev, { username: "", display_name: "", password: "" }])}
                >
                  Add user
                </button>
              </div>
              <div className="card-body settings-stack">
                {!users.length && <div className="placeholder">No normal users configured.</div>}
                {users.map((user, index) => (
                  <div key={`user-${index}`} className="settings-user-row">
                    <div className="settings-grid settings-grid--users">
                      <label className="settings-field">
                        <span>Username</span>
                        <input
                          value={user.username}
                          onChange={(e) => updateUser(index, (current) => ({ ...current, username: e.target.value }))}
                        />
                      </label>
                      <label className="settings-field">
                        <span>Display name</span>
                        <input
                          value={user.display_name}
                          onChange={(e) => updateUser(index, (current) => ({ ...current, display_name: e.target.value }))}
                        />
                      </label>
                      <label className="settings-field">
                        <span>Password</span>
                        <input
                          type="password"
                          value={user.password}
                          onChange={(e) => updateUser(index, (current) => ({ ...current, password: e.target.value }))}
                        />
                      </label>
                    </div>
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => setUsers((prev) => prev.filter((_, i) => i !== index))}
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </section>

          <section className="card settings-primary-card">
            <div className="card-header">
              <div className="settings-section-header">
                <div>
                  <div className="card-title">Shops</div>
                  <div className="card-subtitle">Create shops, connect POS / expense databases, and add AI OCR hints for each shop.</div>
                </div>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={handleAddShop}
                >
                  Add shop
                </button>
              </div>
            </div>
            <div className="card-body settings-stack">
              {!shops.length && <div className="placeholder">No shops configured.</div>}
              {shops.map((shop, index) => (
                <div
                  key={`shop-${shop.shop_id}-${index}`}
                  ref={(element) => {
                    shopCardRefs.current[index] = element;
                  }}
                  className="settings-shop-card"
                  tabIndex={-1}
                >
                  <div className="settings-shop-header">
                    <div className="settings-shop-title-row">
                      <div className="settings-shop-title">Shop {shop.shop_id || "New"}</div>
                      {defaultShopId === shop.shop_id && <span className="pill">Default shop</span>}
                    </div>
                    <div className="settings-shop-actions">
                      <button
                        type="button"
                        className={`secondary-button${defaultShopId === shop.shop_id ? " is-active" : ""}`}
                        onClick={() => setDefaultShopId(shop.shop_id || null)}
                        disabled={defaultShopId === shop.shop_id}
                      >
                        {defaultShopId === shop.shop_id ? "Default shop" : "Make default"}
                      </button>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => {
                          setShops((prev) => prev.filter((_, i) => i !== index));
                          setDefaultShopId((current) => (current === shop.shop_id ? null : current));
                        }}
                      >
                        Remove
                      </button>
                    </div>
                  </div>

                  <div className="settings-grid settings-grid--shop">
                    <label className="settings-field">
                      <span>Shop ID</span>
                      <input
                        type="number"
                        value={shop.shop_id || ""}
                        onChange={(e) => {
                          const nextShopId = Number(e.target.value);
                          setDefaultShopId((current) => (current === shop.shop_id ? nextShopId || null : current));
                          updateShop(index, (current) => ({ ...current, shop_id: nextShopId }));
                        }}
                      />
                    </label>
                    <label className="settings-field">
                      <span>Name</span>
                      <input
                        value={shop.name}
                        onChange={(e) => updateShop(index, (current) => ({ ...current, name: e.target.value }))}
                      />
                    </label>
                    <label className="settings-field">
                      <span>Timezone</span>
                      <input
                        value={shop.timezone}
                        onChange={(e) => updateShop(index, (current) => ({ ...current, timezone: e.target.value }))}
                      />
                    </label>
                    <label className="settings-field settings-field--wide">
                      <span>Description for AI receipt OCR</span>
                      <textarea
                        className="settings-textarea"
                        value={shop.description}
                        placeholder="Optional shop notes: expected suppliers, brands, item naming conventions, receipt language, or branch-specific context."
                        onChange={(e) => updateShop(index, (current) => ({ ...current, description: e.target.value }))}
                        rows={3}
                      />
                    </label>
                  </div>

                  <div className="settings-category-panel">
                    <div className="settings-category-header">
                      <div className="settings-source-title">Allowed product categories</div>
                      <div className="card-subtitle">
                        OCR and review can only use these categories. `Others` is permanent. Adding a category triggers AI backfill for products still in `Others`; deleting a category moves matching products back to `Others`.
                      </div>
                    </div>
                    <div className="settings-category-list">
                      {normalizeCategories(shop.categories).map((category) => (
                        <div key={`${shop.shop_id}-${category}`} className="settings-category-chip">
                          <span>{category}</span>
                          <button
                            type="button"
                            className="secondary-button"
                            onClick={() => removeCategory(index, category)}
                            disabled={category === "Others"}
                          >
                            {category === "Others" ? "Default" : "Delete"}
                          </button>
                        </div>
                      ))}
                    </div>
                    <div className="settings-category-add">
                      <input
                        value={categoryInputs[String(shop.shop_id || index)] || ""}
                        placeholder="New category name"
                        onChange={(e) =>
                          setCategoryInputs((prev) => ({
                            ...prev,
                            [String(shop.shop_id || index)]: e.target.value,
                          }))
                        }
                        onKeyDown={(e) => {
                          if (e.key === "Enter") {
                            e.preventDefault();
                            addCategory(index);
                          }
                        }}
                      />
                      <button type="button" className="secondary-button" onClick={() => addCategory(index)}>
                        Add category
                      </button>
                    </div>
                  </div>

                  <div className="settings-source-grid">
                    {(["pos", "expense"] as const).map((kind) => {
                      const sourceState = sourceActionStates[sourceActionKey(index, kind)] || {};
                      return (
                        <div key={kind} className="settings-source-card">
                          <div className="settings-source-header">
                            <div className="settings-source-title">{kind === "pos" ? "POS database" : "Expense database"}</div>
                            <div className="settings-source-actions">
                              <button
                                type="button"
                                className="secondary-button"
                                onClick={() => void handleTestConnection(index, kind)}
                                disabled={saving || loading || sourceState.testing || sourceState.initiating}
                              >
                                {sourceState.testing ? "Testing…" : "Test Connection"}
                              </button>
                              {kind === "expense" && (
                                <button
                                  type="button"
                                  className="secondary-button"
                                  onClick={() => void handleInitExpenseDb(index)}
                                  disabled={saving || loading || sourceState.testing || sourceState.initiating}
                                >
                                {sourceState.initiating ? "Creating…" : "Create/init DB"}
                                </button>
                              )}
                            </div>
                          </div>
                          {(sourceState.message || sourceState.error) && (
                            <div
                              className={`settings-source-status ${
                                sourceState.error ? "settings-source-status--error" : "settings-source-status--success"
                              }`}
                            >
                              {sourceState.error || sourceState.message}
                            </div>
                          )}
                          <div className="settings-grid">
                            <label className="settings-field">
                              <span>Host</span>
                              <input
                                value={shop[kind].host}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], host: e.target.value },
                                  }))
                                }
                              />
                            </label>
                            <label className="settings-field">
                              <span>Port</span>
                              <input
                                value={shop[kind].port}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], port: e.target.value },
                                  }))
                                }
                              />
                            </label>
                            <label className="settings-field">
                              <span>DB name</span>
                              <input
                                value={shop[kind].dbname}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], dbname: e.target.value },
                                  }))
                                }
                              />
                            </label>
                            <label className="settings-field">
                              <span>User</span>
                              <input
                                value={shop[kind].user}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], user: e.target.value },
                                  }))
                                }
                              />
                            </label>
                            <label className="settings-field">
                              <span>Password</span>
                              <input
                                type="password"
                                value={shop[kind].password}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], password: e.target.value },
                                  }))
                                }
                              />
                            </label>
                            <label className="settings-field settings-field--wide">
                              <span>Conninfo</span>
                              <input
                                value={shop[kind].conninfo}
                                onChange={(e) =>
                                  updateShop(index, (current) => ({
                                    ...current,
                                    [kind]: { ...current[kind], conninfo: e.target.value },
                                  }))
                                }
                              />
                            </label>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}
