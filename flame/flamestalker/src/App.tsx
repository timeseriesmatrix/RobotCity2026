// src/App.tsx
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { FormEvent, ReactNode, RefObject } from "react";
import ScanView from "./ScanView";
import SettingsView from "./SettingsView";
import {
  anonymousApiFetch,
  apiFetch,
  clearStoredAuthSession,
  getStoredAuthSession,
  listenForAuthExpired,
  setStoredAuthSession,
} from "./api";
import type { AuthUser, StoredAuthSession } from "./api";

type ActiveView = "nodes" | "data" | "scan" | "settings";
type DataMode = "sql" | "purchased" | "ai";
type SqlSourceKind = "pos" | "expense";

interface HistoryItem {
  id: string;
  title: string;
  input: string;
  sql: string;
  error: string;
  result: ServerResult | null;
  createdAt: number;
  updatedAt: number;
}

interface ServerResult {
  rows?: Array<Record<string, unknown>>;
  row_count?: number;
  affected_rows?: number;
  command_tag?: string;
  error?: string;
}

interface ApiResponse {
  shop_id?: number;
  source_kind?: SqlSourceKind;
  sql: string;
  error: string;
  result: ServerResult;
}

interface AiDataQueryEntry {
  source_kind: SqlSourceKind;
  title: string;
  sql: string;
  error: string;
  result: ServerResult;
}

interface AiDataQueryResponse {
  shop_id?: number;
  question: string;
  time_range: { start: string; end: string };
  answer_title: string;
  notes: string;
  queries: AiDataQueryEntry[];
  error?: string;
}

interface ShopInfo {
  shop_id: number;
  name?: string;
  description?: string;
  timezone?: string;
  categories?: string[];
  host?: string;
  port?: string;
  dbname?: string;
  user?: string;
  conninfo?: string;
  pos?: ShopSourceInfo;
  expense?: ShopSourceInfo;
}

interface ShopSourceInfo {
  host?: string;
  port?: string;
  dbname?: string;
  user?: string;
  password?: string;
  user_env?: string;
  pass_env?: string;
  conninfo?: string;
  configured?: boolean;
}

interface SummaryTotals {
  revenue_cents: number;
  revenue: number;
  closing_revenue_cents?: number;
  closing_revenue?: number;
  paid_revenue_cents?: number;
  paid_revenue?: number;
  orders: number;
  aov: number;
  items: number;
  avg_items_per_order: number;
}

interface ProductSummary {
  name: string;
  quantity: number;
  revenue_cents: number;
  revenue: number;
}

interface SummaryBreakdown {
  order_type?: string;
  payment_method?: string;
  orders: number;
  revenue_cents: number;
  revenue: number;
}

interface PeakHour {
  hour: string;
  orders: number;
  revenue_cents: number;
  revenue: number;
}

interface ShopOrderSummary {
  order_id: number;
  order_time: string;
  total_cents: number;
  order_type: string;
  items: number;
  order_items?: ShopOrderItem[];
}

interface ShopOrderItem {
  name: string;
  quantity: number;
  unit_price_cents: number;
  line_total_cents: number;
}

interface ShopSummary {
  shop_id: number;
  time_range: { start: string; end: string };
  totals: SummaryTotals;
  top_sellers_by_quantity: ProductSummary[];
  top_revenue_products: ProductSummary[];
  bottom_sellers_by_quantity: ProductSummary[];
  bottom_revenue_products: ProductSummary[];
  peak_hours: PeakHour[];
  order_types: SummaryBreakdown[];
  payment_methods: SummaryBreakdown[];
  all_orders?: ShopOrderSummary[];
  error?: string;
}

interface PurchasedTotals {
  orders: number;
  items: number;
  total_cost_cents: number;
  avg_order_cost_cents: number;
  suppliers: number;
  products: number;
}

interface PurchasedAggregate {
  name: string;
  orders?: number;
  quantity?: number;
  total_cost_cents: number;
}

interface PurchasedRecentOrder {
  purchase_id: number;
  invoice_id: string;
  supplier: string;
  purchase_date: string;
  total_cost_cents: number;
}

interface PurchasedSelectedItem {
  id: number;
  purchase_id: number;
  ocr_id: number;
  ocr_page_id: number;
  invoice_id: string;
  purchase_date: string;
  supplier: string;
  product: string;
  quantity: number;
  unit_price_cents: number;
  total_price_cents: number;
}

interface PurchasedSummaryFilters {
  product_name: string;
  supplier_name: string;
}

interface PurchasedSummary {
  shop_id?: number;
  shop_name?: string;
  database?: string;
  time_range: { start: string; end: string };
  filters?: PurchasedSummaryFilters;
  totals: PurchasedTotals;
  top_suppliers: PurchasedAggregate[];
  top_products: PurchasedAggregate[];
  recent_orders: PurchasedRecentOrder[];
  selected_items: PurchasedSelectedItem[];
  error?: string;
}

interface DbColumnInfo {
  name: string;
  data_type: string;
  is_nullable: string;
}

interface DbTableInfo {
  schema_name?: string;
  table: string;
  row_count: number;
  columns: DbColumnInfo[];
}

interface DbSchemaOverview {
  shop_id?: number;
  source_kind?: SqlSourceKind;
  database: string;
  schema: string;
  tables: DbTableInfo[];
  error?: string;
}

interface LoginResponse {
  token?: string;
  user?: AuthUser;
  error?: string;
}

function pickDefaultShopId(shops: ShopInfo[], defaultShopId?: number | null) {
  if (defaultShopId && shops.some((shop) => shop.shop_id === defaultShopId)) {
    return defaultShopId;
  }
  return shops[0]?.shop_id ?? null;
}

function escapeSqlLiteral(value: string) {
  return value.replace(/'/g, "''");
}

function buildPurchasedItemsSql(range: { start: string; end: string }, shopId?: number, filters?: PurchasedSummaryFilters) {
  const productName = (filters?.product_name || "").trim();
  const supplierName = (filters?.supplier_name || "").trim();
  return [
    "SELECT",
    "  pi.id AS id,",
    "  COALESCE(po.ocr_id, 0) AS ocr_id,",
    "  COALESCE(pi.ocr_page_id, po.ocr_page_id, 0) AS ocr_page_id,",
    "  CONCAT(COALESCE(po.ocr_id, 0), '#', COALESCE(pi.ocr_page_id, po.ocr_page_id, 0)) AS picture_id,",
    "  COALESCE(po.invoice_id, '') AS invoice_id,",
    "  po.purchase_date::text AS purchase_date,",
    "  COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,",
    "  COALESCE(NULLIF(p.name, ''), 'Unknown') AS product,",
    "  COALESCE(pi.quantity, 0) AS quantity,",
    "  COALESCE(pi.unit_price, 0) AS unit_price,",
    "  COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0)) AS total_price_cents",
    "FROM tracker.purchase_items pi",
    "JOIN tracker.purchase_orders po ON po.id = pi.purchase_id",
    "LEFT JOIN tracker.suppliers s ON s.id = po.supplier_id",
    "LEFT JOIN tracker.products p ON p.id = pi.product_id",
    `WHERE po.purchase_date BETWEEN '${range.start.slice(0, 10)}'::date AND '${range.end.slice(0, 10)}'::date`,
    ...(shopId ? [`  AND po.shop_id = ${shopId}`] : []),
    ...(productName ? [`  AND COALESCE(NULLIF(p.name, ''), 'Unknown') ILIKE '%${escapeSqlLiteral(productName)}%'`] : []),
    ...(supplierName ? [`  AND COALESCE(NULLIF(s.name, ''), 'Unknown') ILIKE '%${escapeSqlLiteral(supplierName)}%'`] : []),
    "ORDER BY po.purchase_date DESC, po.id DESC, p.name ASC",
  ].join("\n");
}

function createId() {
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function toLocalInputValue(date: Date) {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 16);
}

function defaultSummaryRange() {
  const now = new Date();
  const start = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0, 0);
  const end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 0, 0);
  return { start: toLocalInputValue(start), end: toLocalInputValue(end) };
}

function defaultSummaryRangeToday() {
  const now = new Date();
  const start = new Date(now);
  const end = new Date(now);
  start.setHours(0, 0, 0, 0);
  end.setHours(23, 59, 0, 0);
  return { start: toLocalInputValue(start), end: toLocalInputValue(end) };
}

function pad2(value: number) {
  return value.toString().padStart(2, "0");
}

function parseLocalDateTime(value: string) {
  if (!value) return null;
  const [datePart, timePart] = value.split("T");
  if (!datePart || !timePart) return null;
  const [year, month, day] = datePart.split("-").map(Number);
  const [hour, minute] = timePart.split(":").map(Number);
  if (!year || !month || !day || Number.isNaN(hour) || Number.isNaN(minute)) return null;
  return new Date(year, month - 1, day, hour, minute, 0, 0);
}

function formatLocalDateTime(date: Date) {
  return `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())}T${pad2(
    date.getHours()
  )}:${pad2(date.getMinutes())}`;
}

function formatCurrency(cents: number | undefined | null) {
  const value = typeof cents === "number" ? cents : 0;
  const vatu = Math.round(value);
  return `VT ${vatu.toLocaleString(undefined, { maximumFractionDigits: 0 })}`;
}

function parseNumericCell(value: unknown) {
  if (typeof value === "number") return Number.isFinite(value) ? value : null;
  if (typeof value !== "string") return null;
  const trimmed = value.trim();
  if (!trimmed || /[A-Za-z]/.test(trimmed)) return null;
  const normalized = trimmed.replace(/,/g, "");
  if (!/^-?\d+(\.\d+)?$/.test(normalized)) return null;
  const parsed = Number(normalized);
  return Number.isFinite(parsed) ? parsed : null;
}

function humanizeColumnName(column: string) {
  return column
    .replace(/_/g, " ")
    .replace(/\b\w/g, (char) => char.toUpperCase());
}

function isIdentifierLikeColumn(column: string) {
  const lower = column.toLowerCase();
  return (
    lower === "id" ||
    lower.endsWith("_id") ||
    lower.includes("ticket_id") ||
    lower.includes("purchase_id") ||
    lower.includes("terminal_id") ||
    lower.includes("invoice") ||
    lower.includes("date") ||
    lower.includes("time")
  );
}

function isMoneyLikeColumn(column: string) {
  const lower = column.toLowerCase();
  return (
    lower.includes("total") ||
    lower.includes("price") ||
    lower.includes("cost") ||
    lower.includes("amount") ||
    lower.includes("revenue") ||
    lower.includes("subtotal") ||
    lower.includes("tax") ||
    lower.includes("discount") ||
    lower.includes("paid") ||
    lower.includes("due")
  );
}

function isAveragePriceColumn(column: string) {
  const lower = column.toLowerCase();
  return (lower.includes("unit_price") || lower.includes("item_price")) && !lower.includes("total");
}

function summarizeQueryRows(rows: Array<Record<string, unknown>>) {
  if (!rows.length) {
    return [{ label: "Rows", value: "0", hint: "Returned", priority: 0 }];
  }

  const columns = Object.keys(rows[0] || {});
  const stats = columns
    .filter((column) => !isIdentifierLikeColumn(column))
    .map((column) => {
      const values = rows
        .map((row) => parseNumericCell(row[column]))
        .filter((value): value is number => value !== null);
      if (!values.length) return null;

      const lower = column.toLowerCase();
      const nonEmpty = rows.filter((row) => row[column] !== null && row[column] !== undefined && String(row[column]).trim() !== "").length;
      if (values.length < Math.max(1, Math.ceil(nonEmpty * 0.7))) return null;

      const sum = values.reduce((total, value) => total + value, 0);
      const average = sum / values.length;
      const moneyLike = isMoneyLikeColumn(column);
      const averagePrice = isAveragePriceColumn(column);
      const labelPrefix = averagePrice ? "Avg" : "Sum";
      const value = moneyLike
        ? formatCurrency(averagePrice ? average : sum)
        : (averagePrice ? average : sum).toLocaleString(undefined, { maximumFractionDigits: 2 });
      let priority = 8;
      if (lower === "quantity" || lower === "item_count" || lower.includes("quantity")) priority = 1;
      else if (lower === "total_price" || lower.includes("total_price")) priority = 2;
      else if (lower.includes("total") || lower.includes("revenue") || lower.includes("cost")) priority = 3;
      else if (lower.includes("amount") || lower.includes("tax") || lower.includes("discount")) priority = 4;
      else if (averagePrice) priority = 5;

      return {
        label: `${labelPrefix} ${humanizeColumnName(column)}`,
        value,
        hint: `${values.length} value${values.length === 1 ? "" : "s"}`,
        priority,
      };
    })
    .filter((entry): entry is { label: string; value: string; hint: string; priority: number } => entry !== null)
    .sort((a, b) => a.priority - b.priority || a.label.localeCompare(b.label))
    .slice(0, 6);

  return [
    { label: "Rows", value: rows.length.toLocaleString(), hint: "Returned", priority: 0 },
    ...stats,
  ];
}

function formatOrderDateTime(value: string | undefined) {
  if (!value) return "Unknown time";
  const parsed = new Date(value);
  if (!Number.isNaN(parsed.getTime())) {
    return parsed.toLocaleString();
  }
  return value.replace("T", " ");
}

function hourLabelFromOrderTime(value: string | undefined) {
  if (!value) return "Unknown";
  const normalized = value.replace("T", " ");
  const match = normalized.match(/\b(\d{2}):\d{2}/);
  return match ? `${match[1]}:00` : "Unknown";
}

function deriveSummaryListsFromOrders(orders: ShopOrderSummary[]) {
  const productMap = new Map<string, { quantity: number; revenue_cents: number }>();
  const hourMap = new Map<string, { orders: number; revenue_cents: number }>();

  for (const order of orders) {
    const hour = hourLabelFromOrderTime(order.order_time);
    const hourEntry = hourMap.get(hour) ?? { orders: 0, revenue_cents: 0 };
    hourEntry.orders += 1;
    hourEntry.revenue_cents += order.total_cents || 0;
    hourMap.set(hour, hourEntry);

    for (const item of order.order_items || []) {
      const key = item.name || "Unknown";
      const productEntry = productMap.get(key) ?? { quantity: 0, revenue_cents: 0 };
      productEntry.quantity += item.quantity || 0;
      productEntry.revenue_cents += item.line_total_cents || 0;
      productMap.set(key, productEntry);
    }
  }

  const peak_hours: PeakHour[] = Array.from(hourMap.entries())
    .map(([hour, value]) => ({
      hour,
      orders: value.orders,
      revenue_cents: value.revenue_cents,
      revenue: value.revenue_cents,
    }))
    .sort((a, b) => b.orders - a.orders || b.revenue_cents - a.revenue_cents || a.hour.localeCompare(b.hour))
    .slice(0, 10);

  const products: ProductSummary[] = Array.from(productMap.entries()).map(([name, value]) => ({
    name,
    quantity: value.quantity,
    revenue_cents: value.revenue_cents,
    revenue: value.revenue_cents,
  }));

  const top_sellers_by_quantity = [...products]
    .sort((a, b) => b.quantity - a.quantity || b.revenue_cents - a.revenue_cents || a.name.localeCompare(b.name))
    .slice(0, 10);

  const top_revenue_products = [...products]
    .sort((a, b) => b.revenue_cents - a.revenue_cents || b.quantity - a.quantity || a.name.localeCompare(b.name))
    .slice(0, 10);

  return {
    peak_hours,
    top_sellers_by_quantity,
    top_revenue_products,
  };
}

function LoginScreen({
  loading,
  error,
  onLogin,
}: {
  loading: boolean;
  error: string | null;
  onLogin: (username: string, password: string) => Promise<void>;
}) {
  const [username, setUsername] = useState("root");
  const [password, setPassword] = useState("");

  const handleSubmit = useCallback(
    async (event: FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      await onLogin(username.trim(), password);
    },
    [onLogin, password, username]
  );

  return (
    <div className="login-shell">
      <form className="login-card" onSubmit={handleSubmit}>
        <div className="login-brand">
          <div className="app-logo">🔥</div>
          <div>
            <div className="app-name">flamestalker</div>
            <div className="app-subtitle">ERP access gate</div>
          </div>
        </div>
        <div className="login-copy">
          Sign in to unlock nodes, data, scan, and settings views. Root can manage settings. Normal users cannot.
        </div>
        <label className="settings-field">
          <span>Username</span>
          <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
        </label>
        <label className="settings-field">
          <span>Password</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error && <div className="status-msg">{error}</div>}
        <div className="login-actions">
          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? "Signing in…" : "Login"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default function App() {
  const [authSession, setAuthSession] = useState<StoredAuthSession | null>(() => getStoredAuthSession());
  const [authLoading, setAuthLoading] = useState(() => Boolean(getStoredAuthSession()));
  const [authError, setAuthError] = useState<string | null>(null);
  const [sqlItem, setSqlItem] = useState<HistoryItem | null>(null);
  const [dataMode, setDataMode] = useState<DataMode>("purchased");
  const [showDbInfo, setShowDbInfo] = useState(false);
  const [dbInfo, setDbInfo] = useState<DbSchemaOverview | null>(null);
  const [dbInfoLoading, setDbInfoLoading] = useState(false);
  const [dbInfoError, setDbInfoError] = useState<string | null>(null);
  const [sqlSourceKind, setSqlSourceKind] = useState<SqlSourceKind>("expense");
  const [purchasedSummary, setPurchasedSummary] = useState<PurchasedSummary | null>(null);
  const [purchasedLoading, setPurchasedLoading] = useState(false);
  const [purchasedError, setPurchasedError] = useState<string | null>(null);
  const [purchasedRange, setPurchasedRange] = useState(() => defaultSummaryRange());
  const [showPurchasedPicker, setShowPurchasedPicker] = useState(false);
  const [purchasedProductName, setPurchasedProductName] = useState("");
  const [purchasedSupplierName, setPurchasedSupplierName] = useState("");
  const [aiQueryText, setAiQueryText] = useState("");
  const [aiQueryResult, setAiQueryResult] = useState<AiDataQueryResponse | null>(null);
  const [aiQueryLoading, setAiQueryLoading] = useState(false);
  const [aiQueryError, setAiQueryError] = useState<string | null>(null);
  const [aiQueryRange, setAiQueryRange] = useState(() => defaultSummaryRangeToday());
  const [showAiQueryPicker, setShowAiQueryPicker] = useState(false);
  const [dataShopPickerOpen, setDataShopPickerOpen] = useState(false);
  const [activeView, setActiveView] = useState<ActiveView>("nodes");
  const [shops, setShops] = useState<ShopInfo[]>([]);
  const [defaultShopId, setDefaultShopId] = useState<number | null>(null);
  const [selectedShopId, setSelectedShopId] = useState<number | null>(null);
  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  const [summaryData, setSummaryData] = useState<ShopSummary | null>(null);
  const [summaryRange, setSummaryRange] = useState(() => defaultSummaryRangeToday());
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const [hasAutoSummaryRun, setHasAutoSummaryRun] = useState(false);
  const [mobileNodesAsideOpen, setMobileNodesAsideOpen] = useState(false);
  const [mobileNodesCommandsOpen, setMobileNodesCommandsOpen] = useState(false);
  const [mobileDataAsideOpen, setMobileDataAsideOpen] = useState(false);
  const [mobileDataCommandsOpen, setMobileDataCommandsOpen] = useState(false);
  const [nodesDisplayMaxHeight, setNodesDisplayMaxHeight] = useState<number | null>(null);
  const [showSummaryPicker, setShowSummaryPicker] = useState(false);

  const [promptText, setPromptText] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [statusMsg, setStatusMsg] = useState<string | null>(null);
  const [imagePreview, setImagePreview] = useState<{ src: string; path: string } | null>(null);
  const [imagePreviewLoading, setImagePreviewLoading] = useState(false);
  const [imagePreviewTarget, setImagePreviewTarget] = useState<{ ocrId: number; pageId: number | null } | null>(null);
  const [imagePreviewShown, setImagePreviewShown] = useState<{ ocrId: number; pageId: number | null } | null>(null);
  const [imageZoom, setImageZoom] = useState(1);
  const [showImagePanel, setShowImagePanel] = useState(false);
  const [isPhoneLayout, setIsPhoneLayout] = useState(() =>
    typeof window !== "undefined" ? window.matchMedia("(max-width: 640px)").matches : false
  );
  const [pendingImagePanelFocus, setPendingImagePanelFocus] = useState(false);
  const [pendingImageContentFocus, setPendingImageContentFocus] = useState(false);
  const [dataCommandClearance, setDataCommandClearance] = useState(105);
  const nodesDisplayRef = useRef<HTMLElement | null>(null);
  const dataInputShellRef = useRef<HTMLElement | null>(null);
  const dataImagePanelRef = useRef<HTMLElement | null>(null);
  const dataImageFrameRef = useRef<HTMLDivElement | null>(null);
  const promptInputRef = useRef<HTMLTextAreaElement | null>(null);
  const imagePreviewRequestSeqRef = useRef(0);

  const activeItem = sqlItem;

  const selectedShop = useMemo(
    () => shops.find((shop) => shop.shop_id === selectedShopId) ?? null,
    [shops, selectedShopId]
  );
  const currentUser = authSession?.user ?? null;
  const canAccessSettings = currentUser?.role === "root";
  const imagePreviewTargetLabel = imagePreviewTarget
    ? `ocr_id ${imagePreviewTarget.ocrId}${imagePreviewTarget.pageId ? ` · page ${imagePreviewTarget.pageId}` : ""}`
    : null;
  const imagePreviewShownLabel = imagePreviewShown
    ? `ocr_id ${imagePreviewShown.ocrId}${imagePreviewShown.pageId ? ` · page ${imagePreviewShown.pageId}` : ""}`
    : null;
  const dataModeHasCommands = dataMode === "sql" || dataMode === "purchased" || dataMode === "ai";

  const updateNodesDisplayMaxHeight = useCallback(() => {
    const displayEl = nodesDisplayRef.current;
    if (!displayEl) return;
    const inputShell = document.querySelector(".input-shell");
    const inputTop = inputShell instanceof HTMLElement ? inputShell.getBoundingClientRect().top : window.innerHeight;
    const displayTop = displayEl.getBoundingClientRect().top;
    const available = inputTop - displayTop - 12;
    setNodesDisplayMaxHeight(available > 0 ? available : null);
  }, []);

  const updateDataCommandClearance = useCallback(() => {
    const inputShell = dataInputShellRef.current;
    const shellHeight = inputShell ? inputShell.getBoundingClientRect().height : 90;
    const next = Math.ceil(shellHeight + 15);
    setDataCommandClearance((prev) => (prev === next ? prev : next));
  }, []);

  const resizePrompt = useCallback(() => {
    const el = promptInputRef.current;
    if (!el) return;
    el.style.height = "0px";
    const next = Math.min(el.scrollHeight, 240);
    el.style.height = `${Math.max(56, next)}px`;
  }, []);

  useEffect(() => {
    if (!pendingImagePanelFocus || !showImagePanel) return;
    const panel = dataImagePanelRef.current;
    if (!panel) return;
    const isPhone = window.matchMedia("(max-width: 640px)").matches;
    const run = () => {
      panel.scrollIntoView({ behavior: isPhone ? "smooth" : "auto", block: "start", inline: "nearest" });
      if (typeof panel.focus === "function") {
        panel.focus({ preventScroll: true });
      }
      setPendingImagePanelFocus(false);
    };
    const raf = window.requestAnimationFrame(run);
    return () => window.cancelAnimationFrame(raf);
  }, [pendingImagePanelFocus, showImagePanel]);

  const focusLoadedImagePreview = useCallback(() => {
    if (!pendingImageContentFocus || !showImagePanel) return;
    const frame = dataImageFrameRef.current;
    const target = frame ?? dataImagePanelRef.current;
    if (!target) return;
    const isPhone = window.matchMedia("(max-width: 640px)").matches;
    const run = () => {
      target.scrollIntoView({ behavior: isPhone ? "smooth" : "auto", block: "start", inline: "nearest" });
      if (typeof target.focus === "function") {
        target.focus({ preventScroll: true });
      }
      setPendingImageContentFocus(false);
    };
    const raf1 = window.requestAnimationFrame(() => {
      const raf2 = window.requestAnimationFrame(run);
      return () => window.cancelAnimationFrame(raf2);
    });
    return () => window.cancelAnimationFrame(raf1);
  }, [pendingImageContentFocus, showImagePanel]);

  useEffect(() => {
    if (!pendingImageContentFocus || !showImagePanel || imagePreviewLoading || !imagePreview) return;
    const timer = window.setTimeout(() => {
      focusLoadedImagePreview();
    }, 40);
    return () => window.clearTimeout(timer);
  }, [pendingImageContentFocus, showImagePanel, imagePreviewLoading, imagePreview, focusLoadedImagePreview]);

  useEffect(() => {
    resizePrompt();
  }, [promptText, resizePrompt]);

  useEffect(() => {
    const stopListening = listenForAuthExpired(() => {
      setAuthSession(null);
      setAuthError("Session expired. Please sign in again.");
      setActiveView("nodes");
    });
    return stopListening;
  }, []);

  useEffect(() => {
    const restore = async () => {
      const stored = getStoredAuthSession();
      if (!stored?.token) {
        setAuthLoading(false);
        return;
      }
      setAuthLoading(true);
      try {
        const res = await apiFetch("/auth_session");
        const data = (await res.json()) as LoginResponse;
        if (!res.ok || data.error || !data.token || !data.user) {
          throw new Error(data.error || `HTTP ${res.status}`);
        }
        const session = { token: data.token, user: data.user };
        setStoredAuthSession(session);
        setAuthSession(session);
        setAuthError(null);
      } catch {
        clearStoredAuthSession();
        setAuthSession(null);
      } finally {
        setAuthLoading(false);
      }
    };
    void restore();
  }, []);

  const handleLogin = useCallback(async (username: string, password: string) => {
    setAuthLoading(true);
    setAuthError(null);
    try {
      const res = await anonymousApiFetch("/auth_login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      const data = (await res.json()) as LoginResponse;
      if (!res.ok || data.error || !data.token || !data.user) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const session = { token: data.token, user: data.user };
      setStoredAuthSession(session);
      setAuthSession(session);
      setAuthError(null);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Login failed";
      clearStoredAuthSession();
      setAuthSession(null);
      setAuthError(msg);
    } finally {
      setAuthLoading(false);
    }
  }, []);

  const handleLogout = useCallback(() => {
    clearStoredAuthSession();
    setAuthSession(null);
    setAuthError(null);
    setActiveView("nodes");
    setShops([]);
    setDefaultShopId(null);
    setSelectedShopId(null);
    setSqlItem(null);
  }, []);

  useEffect(() => {
    if (!canAccessSettings && activeView === "settings") {
      setActiveView("nodes");
    }
  }, [activeView, canAccessSettings]);

  useEffect(() => {
    if (!authSession) {
      setShops([]);
      setDefaultShopId(null);
      setSelectedShopId(null);
      return;
    }
  }, [authSession]);

  useEffect(() => {
    if (!authSession) return;
    const loadShops = async () => {
      try {
        const res = await apiFetch("/shop_databases.json");
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        const incoming = Array.isArray(data)
          ? data
          : Array.isArray((data as { shops?: ShopInfo[] })?.shops)
          ? (data as { shops?: ShopInfo[] }).shops ?? []
          : [];
        const nextDefaultShopId =
          !Array.isArray(data) &&
          Number.isFinite(Number((data as { default_shop_id?: number }).default_shop_id)) &&
          Number((data as { default_shop_id?: number }).default_shop_id) > 0
            ? Number((data as { default_shop_id?: number }).default_shop_id)
            : null;
        if (Array.isArray(incoming)) {
          const normalized = incoming
            .filter((shop) => Number.isFinite(Number(shop?.shop_id)))
            .map((shop) => ({
              ...shop,
              shop_id: Number(shop.shop_id),
              name: shop.name || `Shop ${shop.shop_id}`,
            }));
          setShops(normalized);
          setDefaultShopId(nextDefaultShopId);
          setNodeStatus(null);
        } else {
          setNodeStatus("shop_databases.json payload is not a JSON array");
        }
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load shop list";
        setNodeStatus(msg);
      }
    };
    void loadShops();
  }, [authSession]);

  useEffect(() => {
    if (!shops.length) return;
    if (selectedShopId === null || !shops.some((shop) => shop.shop_id === selectedShopId)) {
      const preferred = pickDefaultShopId(shops, defaultShopId);
      if (preferred !== null) setSelectedShopId(preferred);
    }
  }, [defaultShopId, shops, selectedShopId]);

  useEffect(() => {
    setSummaryData(null);
    setSummaryError(null);
    setPurchasedSummary(null);
    setPurchasedError(null);
    setAiQueryResult(null);
    setAiQueryError(null);
    setDbInfo(null);
    setDbInfoError(null);
  }, [selectedShopId]);

  useEffect(() => {
    setDbInfo(null);
    setDbInfoError(null);
  }, [sqlSourceKind]);

  useEffect(() => {
    if (activeView !== "nodes") return;
    updateNodesDisplayMaxHeight();
    const onResize = () => updateNodesDisplayMaxHeight();
    window.addEventListener("resize", onResize);
    const inputShell = document.querySelector(".input-shell");
    const resizeObserver =
      "ResizeObserver" in window && inputShell
        ? new ResizeObserver(() => updateNodesDisplayMaxHeight())
        : null;
    if (resizeObserver && inputShell) resizeObserver.observe(inputShell);

    return () => {
      window.removeEventListener("resize", onResize);
      resizeObserver?.disconnect();
    };
  }, [activeView, updateNodesDisplayMaxHeight]);

  useEffect(() => {
    if (activeView !== "data") return;
    updateDataCommandClearance();
    const onResize = () => updateDataCommandClearance();
    window.addEventListener("resize", onResize);
    const inputShell = dataInputShellRef.current;
    const resizeObserver =
      "ResizeObserver" in window && inputShell
        ? new ResizeObserver(() => updateDataCommandClearance())
        : null;
    if (resizeObserver && inputShell) resizeObserver.observe(inputShell);

    return () => {
      window.removeEventListener("resize", onResize);
      resizeObserver?.disconnect();
    };
  }, [
    activeView,
    aiQueryText,
    dataMode,
    mobileDataCommandsOpen,
    promptText,
    showAiQueryPicker,
    showPurchasedPicker,
    statusMsg,
    updateDataCommandClearance,
  ]);

  useEffect(() => {
    const media = window.matchMedia("(max-width: 640px)");
    const update = () => setIsPhoneLayout(media.matches);
    update();
    media.addEventListener("change", update);
    return () => media.removeEventListener("change", update);
  }, []);

  const handleSummaryRequest = useCallback(async () => {
    if (!selectedShop) {
      setSummaryError("Select a shop first");
      return;
    }
    if (summaryRange.start && summaryRange.end && new Date(summaryRange.start) > new Date(summaryRange.end)) {
      setSummaryError("Start time must be before end time");
      return;
    }
    setSummaryLoading(true);
    setSummaryError(null);
    try {
      const res = await apiFetch("/shop_summary", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShop.shop_id,
          start_time: summaryRange.start,
          end_time: summaryRange.end,
        }),
      });
      const data = await res.json();
      if (!res.ok || (data && typeof data === "object" && data.error)) {
        const msg =
          (data && typeof data === "object" && data.error) ||
          `HTTP ${res.status}`;
        setSummaryError(String(msg));
        setSummaryData(null);
        return;
      }
      setSummaryData(data as ShopSummary);
      setSummaryError(null);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to fetch summary";
      setSummaryError(msg);
      setSummaryData(null);
    } finally {
      setSummaryLoading(false);
      setShowSummaryPicker(false);
    }
  }, [selectedShop, summaryRange]);

  const handleSummaryButtonClick = useCallback(() => {
    if (showSummaryPicker) {
      void handleSummaryRequest();
      return;
    }
    setShowSummaryPicker(true);
  }, [handleSummaryRequest, showSummaryPicker]);

  useEffect(() => {
    if (hasAutoSummaryRun) return;
    if (activeView !== "nodes") return;
    if (!selectedShop) return;
    setHasAutoSummaryRun(true);
    void handleSummaryRequest();
  }, [activeView, selectedShop, hasAutoSummaryRun, handleSummaryRequest]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (selectedShopId === null) {
      setStatusMsg("Select a shop first.");
      return;
    }
    const trimmed = promptText.trim();
    if (!trimmed) return;

    setIsLoading(true);
    setStatusMsg("Running SQL query…");

    const id = createId();
    const title = trimmed.length > 40 ? trimmed.slice(0, 37).trimEnd() + "…" : trimmed;

    let apiData: ApiResponse | null = null;

    try {
      const res = await apiFetch("/execute_sql", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          source_kind: sqlSourceKind,
          sql: trimmed,
        }),
      });

      if (!res.ok) {
        throw new Error(`HTTP ${res.status} ${res.statusText}`);
      }

      apiData = (await res.json()) as ApiResponse;
      setStatusMsg(null);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Request failed";
      setStatusMsg(msg);
      apiData = {
        sql: "",
        error: msg,
        result: { error: msg },
      };
    } finally {
      setIsLoading(false);
    }

    const combinedError = apiData?.error || apiData?.result?.error || "";

    const item: HistoryItem = {
      id,
      title,
      input: trimmed,
      sql: apiData?.sql ?? "",
      error: combinedError,
      result: apiData?.result ?? null,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    setSqlItem(item);
    setPromptText("");
  }

  const loadDbInfo = useCallback(async () => {
    if (selectedShopId === null) {
      setDbInfoError("Select a shop first.");
      return;
    }
    setDbInfoLoading(true);
    setDbInfoError(null);
    try {
      const res = await apiFetch("/db_schema_overview", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_id: selectedShopId, source_kind: sqlSourceKind }),
      });
      const data = (await res.json()) as DbSchemaOverview;
      if (!res.ok || data.error) {
        const msg = data.error || `HTTP ${res.status}`;
        setDbInfoError(msg);
        return;
      }
      setDbInfo(data);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to load DB info";
      setDbInfoError(msg);
    } finally {
      setDbInfoLoading(false);
    }
  }, [selectedShopId, sqlSourceKind]);

  const handlePurchasedGenerate = useCallback(async () => {
    if (selectedShopId === null) {
      setPurchasedError("Select a shop first.");
      return;
    }
    if (!purchasedRange.start || !purchasedRange.end) {
      setPurchasedError("Start and end are required.");
      return;
    }
    if (new Date(purchasedRange.start) > new Date(purchasedRange.end)) {
      setPurchasedError("Start time must be before end time.");
      return;
    }
    const productName = purchasedProductName.trim();
    const supplierName = purchasedSupplierName.trim();
    setPurchasedLoading(true);
    setPurchasedError(null);
    try {
      const res = await apiFetch("/purchased_summary", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          start_time: purchasedRange.start,
          end_time: purchasedRange.end,
          product_name: productName,
          supplier_name: supplierName,
        }),
      });
      const data = (await res.json()) as PurchasedSummary;
      if (!res.ok || data.error) {
        const msg = data.error || `HTTP ${res.status}`;
        setPurchasedError(msg);
        setPurchasedSummary(null);
        return;
      }
      setPurchasedSummary(data);
      setShowPurchasedPicker(false);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to load purchased summary";
      setPurchasedError(msg);
    } finally {
      setPurchasedLoading(false);
    }
  }, [purchasedProductName, purchasedRange.end, purchasedRange.start, purchasedSupplierName, selectedShopId]);

  const handleAiQueryGenerate = useCallback(async () => {
    if (selectedShopId === null) {
      setAiQueryError("Select a shop first.");
      return;
    }
    const question = aiQueryText.trim();
    if (!question) {
      setAiQueryError("Enter a sentence for the AI query.");
      return;
    }
    if (!aiQueryRange.start || !aiQueryRange.end) {
      setAiQueryError("Start and end are required.");
      return;
    }
    if (new Date(aiQueryRange.start) > new Date(aiQueryRange.end)) {
      setAiQueryError("Start time must be before end time.");
      return;
    }

    setAiQueryLoading(true);
    setAiQueryError(null);
    setStatusMsg("AI is generating SQL and querying POS/EXP databases…");
    try {
      const res = await apiFetch("/ai_data_query", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          question,
          start_time: aiQueryRange.start,
          end_time: aiQueryRange.end,
        }),
      });
      const data = (await res.json()) as AiDataQueryResponse;
      if (!res.ok || data.error) {
        const msg = data.error || `HTTP ${res.status}`;
        setAiQueryError(msg);
        setAiQueryResult(data.queries?.length ? data : null);
        return;
      }
      setAiQueryResult(data);
      setShowAiQueryPicker(false);
      setStatusMsg(null);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to run AI data query";
      setAiQueryError(msg);
      setStatusMsg(msg);
    } finally {
      setAiQueryLoading(false);
    }
  }, [aiQueryRange.end, aiQueryRange.start, aiQueryText, selectedShopId]);

  useEffect(() => {
    if (activeView !== "data" || dataMode !== "purchased") return;
    if (selectedShopId === null) return;
    if (purchasedLoading || purchasedSummary || purchasedError) return;
    void handlePurchasedGenerate();
  }, [activeView, dataMode, selectedShopId, purchasedLoading, purchasedSummary, purchasedError, handlePurchasedGenerate]);

  useEffect(() => {
    if (dataMode !== "sql" || !showDbInfo || dbInfo || dbInfoLoading) return;
    void loadDbInfo();
  }, [dataMode, showDbInfo, dbInfo, dbInfoLoading, loadDbInfo]);

  useEffect(() => {
    if (activeView !== "data") {
      setDataShopPickerOpen(false);
    }
  }, [activeView]);

  const handleSettingsShopsSaved = useCallback((nextShops: ShopInfo[], nextDefaultShopId: number | null) => {
    const normalized = nextShops
      .filter((shop) => Number.isFinite(Number(shop?.shop_id)))
      .map((shop) => ({
        ...shop,
        shop_id: Number(shop.shop_id),
        name: shop.name || `Shop ${shop.shop_id}`,
      }));
    setShops(normalized);
    setDefaultShopId(nextDefaultShopId);
    setSelectedShopId((current) => {
      if (current && normalized.some((shop) => shop.shop_id === current)) return current;
      return pickDefaultShopId(normalized, nextDefaultShopId);
    });
  }, []);

  if (authLoading) {
    return (
      <div className="login-shell">
        <div className="login-card">
          <div className="placeholder">Checking session…</div>
        </div>
      </div>
    );
  }

  if (!authSession) {
    return <LoginScreen loading={authLoading} error={authError} onLogin={handleLogin} />;
  }

  const ocrImagePanel = showImagePanel ? (
    <OcrImagePanel
      panelRef={dataImagePanelRef}
      frameRef={dataImageFrameRef}
      imagePreview={imagePreview}
      imagePreviewLoading={imagePreviewLoading}
      targetLabel={imagePreviewTargetLabel}
      shownLabel={imagePreviewShownLabel}
      imageZoom={imageZoom}
      onZoomIn={() => setImageZoom((z) => Math.min(3, parseFloat((z + 0.1).toFixed(2))))}
      onZoomOut={() => setImageZoom((z) => Math.max(0.3, parseFloat((z - 0.1).toFixed(2))))}
      onResetZoom={() => setImageZoom(1)}
      onClose={() => {
        setImagePreview(null);
        setImagePreviewLoading(false);
        setImagePreviewTarget(null);
        setImagePreviewShown(null);
        setShowImagePanel(false);
        setPendingImagePanelFocus(false);
        setPendingImageContentFocus(false);
        setImageZoom(1);
      }}
      onImageLoad={focusLoadedImagePreview}
    />
  ) : null;

  const nodesSidebarBody = (
    <>
      <div className="sidebar-section-label">Nodes</div>
      <nav className="nodes-list" aria-label="POS shop connections">
        {shops.map((shop) => (
          <button
            key={shop.shop_id}
            type="button"
            className={"node-item" + (selectedShop?.shop_id === shop.shop_id ? " node-item--active" : "")}
            onClick={() => setSelectedShopId(shop.shop_id)}
          >
            <div className="node-item-title">{shop.name || `Shop ${shop.shop_id}`}</div>
            <div className="node-item-meta">
              id {shop.shop_id}
              {shop.pos?.dbname ? ` · POS ${shop.pos.dbname}` : shop.dbname ? ` · POS ${shop.dbname}` : ""}
              {shop.expense?.dbname ? ` · EXP ${shop.expense.dbname}` : ""}
              {shop.host ? ` · ${shop.host}` : ""}
            </div>
          </button>
        ))}
        {!shops.length && (
          <div className="history-empty">Add shop_databases.json entries to see shops here.</div>
        )}
      </nav>

      <footer className="sidebar-footer">
        <span className="sidebar-footer-label">Sources</span>
        <span className="sidebar-footer-value">shop_databases.json</span>
      </footer>
    </>
  );

  const dataSidebarBody = (
    <>
      <div className="sidebar-section-label">Data menu</div>
      <nav className="nodes-list" aria-label="Data view menu">
        <button
          type="button"
          className={"node-item" + (dataMode === "purchased" ? " node-item--active" : "")}
          onClick={() => {
            setDataMode("purchased");
            setMobileDataAsideOpen(false);
          }}
        >
          <div className="node-item-title">Purchase summary</div>
          <div className="node-item-meta">Summary from the selected shop expense tracker</div>
        </button>
        <button
          type="button"
          className={"node-item" + (dataMode === "ai" ? " node-item--active" : "")}
          onClick={() => {
            setDataMode("ai");
            setMobileDataAsideOpen(false);
          }}
        >
          <div className="node-item-title">AI query</div>
          <div className="node-item-meta">Ask POS and expense databases together</div>
        </button>
        <button
          type="button"
          className={"node-item" + (dataMode === "sql" ? " node-item--active" : "")}
          onClick={() => {
            setDataMode("sql");
            setMobileDataAsideOpen(false);
          }}
        >
          <div className="node-item-title">SQL query</div>
          <div className="node-item-meta">Run SQL on the selected shop source</div>
        </button>
      </nav>

      <div className="sidebar-shop-selector">
        {dataShopPickerOpen && shops.length > 0 && (
          <div className="sidebar-shop-picker" role="listbox" aria-label="Select shop">
            {shops.map((shop) => (
              <button
                key={shop.shop_id}
                type="button"
                className={"node-item" + (selectedShop?.shop_id === shop.shop_id ? " node-item--active" : "")}
                onClick={() => {
                  setSelectedShopId(shop.shop_id);
                  setDataShopPickerOpen(false);
                  setMobileDataAsideOpen(false);
                }}
              >
                <div className="node-item-title">{shop.name || `Shop ${shop.shop_id}`}</div>
                <div className="node-item-meta">
                  id {shop.shop_id}
                  {shop.pos?.dbname ? ` · POS ${shop.pos.dbname}` : shop.dbname ? ` · POS ${shop.dbname}` : ""}
                  {shop.expense?.dbname ? ` · EXP ${shop.expense.dbname}` : ""}
                </div>
              </button>
            ))}
          </div>
        )}
        <button
          type="button"
          className="sidebar-footer sidebar-footer--button"
          onClick={() => setDataShopPickerOpen((open) => !open)}
          disabled={!shops.length}
          aria-expanded={dataShopPickerOpen}
          aria-label="Select shop"
        >
          <span className="sidebar-footer-label">Connected to</span>
          <span className="sidebar-footer-value">
            {selectedShop ? `${selectedShop.name} · ${sqlSourceKind}` : "Select a shop"}
          </span>
        </button>
      </div>
    </>
  );

  return (
    <div className={`app-shell view-${activeView}`} style={{ minHeight: "100vh" }}>
      <ActivityRail
        activeView={activeView}
        onChange={setActiveView}
        canAccessSettings={canAccessSettings}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      {activeView === "nodes" ? (
        <>
          <aside className="sidebar nodes-sidebar">
            <header className="sidebar-header">
              <div className="app-logo">🔥</div>
              <div className="app-title">
                <div className="app-name">flamestalker</div>
                <div className="app-subtitle">ERP visual console</div>
              </div>
            </header>
            {nodesSidebarBody}
          </aside>

          <main className="main nodes-main">
            <button
              type="button"
              className="mobile-sidebar-toggle hideable-toggle"
              onClick={() => setMobileNodesAsideOpen((prev) => !prev)}
              aria-expanded={mobileNodesAsideOpen}
            >
              {mobileNodesAsideOpen ? "Hide Nodes Panel" : "Show Nodes Panel"}
            </button>
            {mobileNodesAsideOpen ? (
              <section className="mobile-inline-panel nodes-mobile-panel">
                {nodesSidebarBody}
              </section>
            ) : null}
            <header className="main-header">
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
                <div className="main-header-title">
                  <span className="pill">Nodes</span>
                  <h1>Nodes view</h1>
                </div>
                <div className="header-note">
                  {shops.length ? `${shops.length} shop${shops.length === 1 ? "" : "s"} configured` : "No shops loaded"}
                </div>
              </div>
              <div className="main-header-subtitle">
                Operate shop POS and expense databases directly. Data comes from shop_databases.json.
              </div>
            </header>

            <div className="main-body">
              <div className="content-grid">
                <div
                  className="content-left"
                  style={{
                    display: "grid",
                    gridTemplateRows: "1fr auto",
                    gap: "16px",
                    minWidth: 0,
                    minHeight: 0
                  }}
                >
                  <section
                    className="main-display"
                    ref={nodesDisplayRef}
                    style={nodesDisplayMaxHeight ? { maxHeight: nodesDisplayMaxHeight } : undefined}
                  >
                    {selectedShop ? (
                      <div className="nodes-display-grid">
                        <NodesPanel
                          shop={selectedShop}
                          summary={summaryData}
                          summaryError={summaryError}
                          summaryLoading={summaryLoading}
                          summaryRange={summaryRange}
                          onSummaryRefresh={handleSummaryRequest}
                        />
                      </div>
                    ) : (
                      <div className="placeholder">
                        Select or add a shop to begin.
                      </div>
                    )}
                  </section>

                  <section className="input-shell">
                    <div className={"mobile-command-panel" + (mobileNodesCommandsOpen ? " is-open" : "")}>
                      <div className="command-bar">
                        <div>
                          <div className="command-title">Commands</div>
                          <div className="command-subtitle">Refresh the selected shop summary</div>
                        </div>
                        <div className="command-actions" style={{ position: "relative" }}>
                          <button
                            type="button"
                            className="secondary-button action-button"
                            onClick={handleSummaryButtonClick}
                            disabled={!selectedShop || summaryLoading}
                          >
                            {summaryLoading ? "Loading…" : "SUMMARY"}
                          </button>
                          {showSummaryPicker && (
                            <SummaryRangePicker
                              range={summaryRange}
                              onChangeField={(field, nextDate) =>
                                setSummaryRange((prev) => ({ ...prev, [field]: formatLocalDateTime(nextDate) }))
                              }
                              onCancel={() => setShowSummaryPicker(false)}
                              onApply={handleSummaryRequest}
                              disabled={summaryLoading}
                            />
                          )}
                        </div>
                      </div>
                      <div className="input-hint">
                        {nodeStatus
                          ? nodeStatus
                          : selectedShop
                          ? `Target: ${selectedShop.name} (shop_id ${selectedShop.shop_id})`
                          : "Load a shop to inspect data"}
                      </div>
                    </div>
                    <button
                      type="button"
                      className="mobile-command-toggle hideable-toggle"
                      onClick={() => setMobileNodesCommandsOpen((prev) => !prev)}
                      aria-expanded={mobileNodesCommandsOpen}
                    >
                      {mobileNodesCommandsOpen ? "Hide Commands" : "Show Commands"}
                    </button>
                  </section>
                </div>
              </div>
            </div>
          </main>
        </>
      ) : activeView === "data" ? (
        <>
          <aside className="sidebar data-sidebar">
            <header className="sidebar-header">
              <div className="app-logo">🔥</div>
              <div className="app-title">
                <div className="app-name">flamestalker</div>
                <div className="app-subtitle">ERP visual console</div>
              </div>
            </header>
            {dataSidebarBody}
          </aside>

          <main className="main">
            <button
              type="button"
              className="mobile-sidebar-toggle hideable-toggle"
              onClick={() => setMobileDataAsideOpen((prev) => !prev)}
              aria-expanded={mobileDataAsideOpen}
            >
              {mobileDataAsideOpen ? "Hide Data Panel" : "Show Data Panel"}
            </button>
            {mobileDataAsideOpen ? (
              <section className="mobile-inline-panel data-mobile-panel">
                {dataSidebarBody}
              </section>
            ) : null}
            <header className="main-header">
              <div className="data-header-row" style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
                <div className="main-header-title">
                  <span className="pill">Display</span>
                  <h1>Data view</h1>
                </div>
                <div className="data-header-actions" style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  {dataMode === "sql" && (
                    <>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => setSqlSourceKind("pos")}
                        disabled={sqlSourceKind === "pos"}
                      >
                        POS DB
                      </button>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => setSqlSourceKind("expense")}
                        disabled={sqlSourceKind === "expense"}
                      >
                        EXP DB
                      </button>
                      <button
                        type="button"
                        className="secondary-button panel-toggle-button"
                        onClick={() => {
                          setShowDbInfo((prev) => !prev);
                          if (!showDbInfo && !dbInfo && !dbInfoLoading) void loadDbInfo();
                        }}
                      >
                        {showDbInfo ? "Hide DB Info" : "Show DB Info"}
                      </button>
                    </>
                  )}
                </div>
              </div>
              <div className="main-header-subtitle">
                {dataMode === "purchased"
                  ? "Purchase summary from the selected shop expense tracker in the selected time span."
                  : dataMode === "ai"
                  ? "Ask a business question; AI generates read-only SQL for POS and expense databases."
                  : `Type SQL then click OK to run directly in the selected ${sqlSourceKind.toUpperCase()} database.`}
              </div>
            </header>

            <div className="main-body">
              <div
                className="content-grid data-content-grid"
                style={{
                  display: "grid",
                  gridTemplateColumns: showImagePanel && !isPhoneLayout ? "minmax(0, 1fr) minmax(360px, 45vw)" : "1fr",
                  gap: "16px",
                  paddingBottom: dataModeHasCommands ? dataCommandClearance : 0,
                  scrollPaddingBottom: dataModeHasCommands ? dataCommandClearance : 0,
                }}
              >
                <div
                  className="content-left data-content-left"
                  style={{
                    display: "grid",
                    gridTemplateRows: dataModeHasCommands ? "1fr auto" : "1fr",
                    gap: "16px",
                    minWidth: 0,
                    minHeight: 0
                  }}
                >
                  <section className="main-display data-main-display" style={{ scrollPaddingBottom: dataModeHasCommands ? dataCommandClearance : 0 }}>
                    {dataMode === "purchased" ? (
                      <PurchasedSummaryPanel
                        shopName={selectedShop?.name}
                        summary={purchasedSummary}
                        range={purchasedRange}
                        filters={{ product_name: purchasedProductName, supplier_name: purchasedSupplierName }}
                        error={purchasedError}
                        loading={purchasedLoading}
                        onGenerate={handlePurchasedGenerate}
                        onStatus={setStatusMsg}
                        onPreviewImage={handlePreviewImage}
                        inlinePreviewPanel={isPhoneLayout ? ocrImagePanel : null}
                      />
                    ) : dataMode === "ai" ? (
                      <AiDataQueryPanel
                        shopId={selectedShopId}
                        shopName={selectedShop?.name}
                        result={aiQueryResult}
                        question={aiQueryText}
                        range={aiQueryRange}
                        error={aiQueryError}
                        loading={aiQueryLoading}
                        onStatus={setStatusMsg}
                        onPreviewImage={handlePreviewImage}
                        inlinePreviewPanel={isPhoneLayout ? ocrImagePanel : null}
                      />
                    ) : (
                      <div style={{ display: "flex", flexDirection: "column", gap: 12, minHeight: 0 }}>
                        {showDbInfo && (
                          <DbInfoPanel
                            dbInfo={dbInfo}
                            loading={dbInfoLoading}
                            error={dbInfoError}
                            onRefresh={loadDbInfo}
                          />
                        )}
                        {activeItem ? (
                          <DisplayPanel
                            item={activeItem}
                            onStatus={setStatusMsg}
                            onPreviewImage={handlePreviewImage}
                            inlinePreviewPanel={isPhoneLayout ? ocrImagePanel : null}
                            shopId={selectedShopId}
                            sourceKind={sqlSourceKind}
                          />
                        ) : (
                          <div className="placeholder">SQL query mode executes the command directly on the selected shop source database.</div>
                        )}
                      </div>
                    )}
                    {dataModeHasCommands && (
                      <div
                        className="data-command-clearance"
                        aria-hidden="true"
                        style={{ height: dataCommandClearance, minHeight: dataCommandClearance }}
                      />
                    )}
                  </section>

                  {dataMode === "purchased" && (
                    <section className="input-shell" ref={dataInputShellRef}>
                      <div className={"mobile-command-panel" + (mobileDataCommandsOpen ? " is-open" : "")}>
                        <div className="command-bar purchase-summary-command-bar">
                          <div>
                            <div className="command-title">Commands</div>
                            <div className="command-subtitle">Refresh purchase summary range</div>
                          </div>
                          <div className="purchase-summary-filters">
                            <label className="command-filter-field">
                              <span>Product name</span>
                              <input
                                value={purchasedProductName}
                                onChange={(event) => {
                                  setPurchasedProductName(event.target.value);
                                }}
                                onKeyDown={(event) => {
                                  if (event.key === "Enter") void handlePurchasedGenerate();
                                }}
                                placeholder="All products"
                                disabled={purchasedLoading}
                              />
                            </label>
                            <label className="command-filter-field">
                              <span>Supplier name</span>
                              <input
                                value={purchasedSupplierName}
                                onChange={(event) => {
                                  setPurchasedSupplierName(event.target.value);
                                }}
                                onKeyDown={(event) => {
                                  if (event.key === "Enter") void handlePurchasedGenerate();
                                }}
                                placeholder="All suppliers"
                                disabled={purchasedLoading}
                              />
                            </label>
                          </div>
                          <div className="command-actions" style={{ position: "relative" }}>
                            <button
                              type="button"
                              className="secondary-button action-button"
                              onClick={() => setShowPurchasedPicker((prev) => !prev)}
                              disabled={!selectedShop || purchasedLoading}
                            >
                              {purchasedLoading ? "Loading…" : "SUMMARY"}
                            </button>
                            {showPurchasedPicker && (
                              <SummaryRangePicker
                                range={purchasedRange}
                                onChangeField={(field, nextDate) =>
                                  setPurchasedRange((prev) => ({ ...prev, [field]: formatLocalDateTime(nextDate) }))
                                }
                                onCancel={() => setShowPurchasedPicker(false)}
                                onApply={handlePurchasedGenerate}
                                disabled={purchasedLoading}
                              />
                            )}
                          </div>
                        </div>
                        <div className="input-hint">
                          {purchasedError
                            ? purchasedError
                            : selectedShop
                            ? `Target: ${selectedShop.name} expense summary${
                                purchasedProductName.trim() || purchasedSupplierName.trim()
                                  ? ` · ${[
                                      purchasedProductName.trim() ? `Product: ${purchasedProductName.trim()}` : "",
                                      purchasedSupplierName.trim() ? `Supplier: ${purchasedSupplierName.trim()}` : "",
                                    ].filter(Boolean).join(" · ")}`
                                  : ""
                              }`
                            : "Select a shop first."}
                        </div>
                      </div>
                      <button
                        type="button"
                        className="mobile-command-toggle hideable-toggle"
                        onClick={() => setMobileDataCommandsOpen((prev) => !prev)}
                        aria-expanded={mobileDataCommandsOpen}
                      >
                        {mobileDataCommandsOpen ? "Hide Commands" : "Show Commands"}
                      </button>
                    </section>
                  )}

                  {dataMode === "ai" && (
                    <section className="input-shell" ref={dataInputShellRef}>
                      <form
                        className="input-form ai-query-command-form"
                        onSubmit={(event) => {
                          event.preventDefault();
                          void handleAiQueryGenerate();
                        }}
                      >
                        <div className={"mobile-command-panel" + (mobileDataCommandsOpen ? " is-open" : "")}>
                          <div className="command-bar ai-query-command-bar">
                            <div className="ai-query-command-copy">
                              <div className="command-title">AI query</div>
                              <textarea
                                className="prompt-input ai-query-input"
                                placeholder="Ask a question about sales, purchases, suppliers, products, or profit..."
                                value={aiQueryText}
                                onChange={(event) => setAiQueryText(event.target.value)}
                                rows={2}
                                disabled={aiQueryLoading}
                              />
                            </div>
                            <div className="command-actions ai-query-actions" style={{ position: "relative" }}>
                              <button
                                type="button"
                                className="secondary-button action-button"
                                onClick={() => setShowAiQueryPicker((prev) => !prev)}
                                disabled={!selectedShop || aiQueryLoading}
                              >
                                Time span
                              </button>
                              <button
                                type="submit"
                                className="submit-button"
                                disabled={!selectedShop || aiQueryLoading || !aiQueryText.trim()}
                              >
                                {aiQueryLoading ? "Running…" : "ASK"}
                              </button>
                              {showAiQueryPicker && (
                                <SummaryRangePicker
                                  range={aiQueryRange}
                                  onChangeField={(field, nextDate) =>
                                    setAiQueryRange((prev) => ({ ...prev, [field]: formatLocalDateTime(nextDate) }))
                                  }
                                  onCancel={() => setShowAiQueryPicker(false)}
                                  onApply={() => setShowAiQueryPicker(false)}
                                  disabled={aiQueryLoading}
                                />
                              )}
                            </div>
                          </div>
                          <div className="input-hint">
                            {aiQueryError
                              ? aiQueryError
                              : selectedShop
                              ? `Target: ${selectedShop.name} · POS and EXP · ${aiQueryRange.start.replace("T", " ")} → ${aiQueryRange.end.replace("T", " ")}`
                              : "Select a shop first."}
                            {statusMsg && <span className="status-msg">{statusMsg}</span>}
                          </div>
                        </div>
                      </form>
                      <button
                        type="button"
                        className="mobile-command-toggle hideable-toggle"
                        onClick={() => setMobileDataCommandsOpen((prev) => !prev)}
                        aria-expanded={mobileDataCommandsOpen}
                      >
                        {mobileDataCommandsOpen ? "Hide Commands" : "Show Commands"}
                      </button>
                    </section>
                  )}

                  {dataMode === "sql" && (
                    <section className="input-shell" ref={dataInputShellRef}>
                      <form className="input-form" onSubmit={handleSubmit}>
                        <div className="input-top">
                          <textarea
                            className="prompt-input"
                            placeholder="Type SQL command..."
                            value={promptText}
                            onChange={(e) => setPromptText(e.target.value)}
                            onInput={resizePrompt}
                            rows={1}
                            ref={promptInputRef}
                          />
                          <button className="submit-button" type="submit" disabled={isLoading}>
                            {isLoading ? "Running…" : "OK"}
                          </button>
                        </div>
                        <div className="input-hint">
                          <span>
                            {selectedShop
                              ? `Target: ${selectedShop.name} · ${sqlSourceKind.toUpperCase()} database`
                              : "Select a shop first."}
                          </span>
                          {statusMsg && <span className="status-msg">{statusMsg}</span>}
                        </div>
                      </form>
                    </section>
                  )}
                </div>

                {!isPhoneLayout && ocrImagePanel}
              </div>
            </div>
          </main>
        </>
      ) : activeView === "scan" ? (
        <ScanView
          selectedShopId={selectedShopId}
          selectedShopName={selectedShop?.name}
          shops={shops}
          onSelectShop={setSelectedShopId}
        />
      ) : (
        <SettingsView
          active={activeView === "settings"}
          currentUser={currentUser}
          onShopsSaved={handleSettingsShopsSaved}
        />
      )}
    </div>
  );

  async function handlePreviewImage(ocrId: number, pageId?: number) {
    if (selectedShopId === null) {
      setStatusMsg("Select a shop first.");
      return;
    }
    if (!Number.isFinite(ocrId) || ocrId <= 0) {
      setStatusMsg("This row has no valid ocr_id to preview.");
      return;
    }
    const target = { ocrId, pageId: pageId && pageId > 0 ? pageId : null };
    const requestSeq = ++imagePreviewRequestSeqRef.current;
    setPendingImagePanelFocus(true);
    setPendingImageContentFocus(true);
    setImagePreviewTarget(target);
    setImagePreviewLoading(true);
    setShowImagePanel(true); // auto-open the OCR window when an ocr_id is clicked
    setStatusMsg(`Loading OCR image for ${target.ocrId}${target.pageId ? ` / page ${target.pageId}` : ""}…`);
    try {
      const endpoint = pageId && pageId > 0 ? "/receipt_page_image" : "/ocr_image";
      const payload = pageId && pageId > 0
        ? { shop_id: selectedShopId, page_id: pageId }
        : { shop_id: selectedShopId, ocr_id: ocrId };
      const res = await apiFetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      const data = await res.json();
      if (requestSeq !== imagePreviewRequestSeqRef.current) {
        return;
      }
      if (!res.ok || data.error) {
        setImagePreviewLoading(false);
        setPendingImageContentFocus(false);
        setStatusMsg(data.error || `HTTP ${res.status}`);
        return;
      }
      const src = `data:image/*;base64,${data.image_base64}`;
      setImagePreview({ src, path: data.image_path });
      setImagePreviewShown(target);
      setImagePreviewLoading(false);
      setImageZoom(1);
      setStatusMsg(null);
    } catch (err) {
      if (requestSeq !== imagePreviewRequestSeqRef.current) {
        return;
      }
      setImagePreviewLoading(false);
      setPendingImageContentFocus(false);
      const msg = err instanceof Error ? err.message : "Failed to load image";
      setStatusMsg(msg);
    }
  }
}

function ActivityRail({
  activeView,
  onChange,
  canAccessSettings,
  currentUser,
  onLogout,
}: {
  activeView: ActiveView;
  onChange: (view: ActiveView) => void;
  canAccessSettings: boolean;
  currentUser: AuthUser | null;
  onLogout: () => void;
}) {
  const items: Array<{ id: ActiveView; label: string; shortLabel: string; icon: string }> = [
    { id: "nodes", label: "Nodes view", shortLabel: "Nodes", icon: "🗂" },
    { id: "data", label: "Data view", shortLabel: "Data", icon: "📊" },
    { id: "scan", label: "Scan view", shortLabel: "Scan", icon: "🧾" },
    ...(canAccessSettings ? [{ id: "settings" as const, label: "Settings", shortLabel: "Settings", icon: "⚙️" }] : []),
  ];

  return (
    <nav className="activity-rail" aria-label="Primary navigation">
      <div className="activity-rail-buttons">
        {items.map((item) => (
          <button
            key={item.id}
            type="button"
            className={"activity-button" + (activeView === item.id ? " activity-button--active" : "")}
            aria-pressed={activeView === item.id}
            onClick={() => onChange(item.id)}
          >
            <span className="activity-icon" aria-hidden="true">
              {item.icon}
            </span>
            <span className="activity-label" data-short-label={item.shortLabel}>{item.label}</span>
          </button>
        ))}
      </div>
      <div className="activity-rail-footer">
        <div className="activity-user-chip">
          <div className="activity-user-name">{currentUser?.display_name || currentUser?.username || "User"}</div>
          <div className="activity-user-role">{currentUser?.role || "user"}</div>
        </div>
        <button type="button" className="activity-logout" onClick={onLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

function NodesPanel({
  shop,
  summary,
  summaryError,
  summaryLoading,
  summaryRange,
  onSummaryRefresh,
}: {
  shop: ShopInfo;
  summary: ShopSummary | null;
  summaryError: string | null;
  summaryLoading: boolean;
  summaryRange: { start: string; end: string };
  onSummaryRefresh: () => void;
}) {
  const hasSummary = summary && !summary.error;
  const totals = hasSummary ? summary?.totals : null;
  const closingDateRevenueCents = totals?.closing_revenue_cents ?? totals?.revenue_cents ?? 0;
  const rangeLabel =
    (summaryRange?.start ? summaryRange.start.replace("T", " ") : "start") +
    " → " +
    (summaryRange?.end ? summaryRange.end.replace("T", " ") : "now");
  const derivedLists = useMemo(
    () => deriveSummaryListsFromOrders(summary?.all_orders || []),
    [summary?.all_orders]
  );
  const peakHours = derivedLists.peak_hours.length ? derivedLists.peak_hours : summary?.peak_hours || [];
  const topSellers = derivedLists.top_sellers_by_quantity.length
    ? derivedLists.top_sellers_by_quantity
    : summary?.top_sellers_by_quantity || [];
  const topRevenueProducts = derivedLists.top_revenue_products.length
    ? derivedLists.top_revenue_products
    : summary?.top_revenue_products || [];

  const listFromProducts = (items: ProductSummary[], fallback: string) =>
    (items || []).map((item) => ({
      label: item.name,
      value: `${item.quantity} sold`,
      secondary: formatCurrency(item.revenue_cents),
    })) || [{ label: fallback, value: "", secondary: "" }];

  const renderSummary = () => (
    <>
      {summaryError && <div className="status-msg">{summaryError}</div>}
      {summaryLoading && <div className="placeholder">Loading summary…</div>}
      {!summaryLoading && hasSummary && totals && (
        <>
          <div className="summary-metrics">
            <SummaryStat
              label="Total revenue"
              value={`${formatCurrency(totals.revenue_cents)} (${formatCurrency(closingDateRevenueCents)})`}
              hint={`${totals.orders} orders`}
              className="summary-stat--wide-mobile"
            />
            <SummaryStat label="Average order value" value={formatCurrency(totals.aov)} hint="AOV" className="summary-stat--wide-mobile" />
            <SummaryStat label="Total items" value={`${totals.items}`} hint={`Avg ${totals.avg_items_per_order.toFixed(2)} / order`} />
            <SummaryStat label="Peak hour" value={peakHours[0]?.hour || "—"} hint={peakHours[0] ? `${peakHours[0].orders} orders` : "Run summary"} />
          </div>
          <div className="summary-lists">
            <SummaryList title="Peak hours" items={peakHours.map((hour) => ({
              label: hour.hour,
              value: `${hour.orders} orders`,
              secondary: formatCurrency(hour.revenue_cents),
            }))} />
            <SummaryList title="Top 10 sellers (qty)" items={listFromProducts(topSellers, "No sales")} />
            <SummaryList title="Top 10 revenue products" items={topRevenueProducts.map((item) => ({
              label: item.name,
              value: formatCurrency(item.revenue_cents),
              secondary: `${item.quantity} sold`,
            }))} />
          </div>
          <OrderList orders={summary.all_orders || []} />
        </>
      )}
      {!summaryLoading && !hasSummary && !summaryError && (
        <div className="placeholder">Run SUMMARY to see performance for this shop.</div>
      )}
    </>
  );

  return (
    <div className="nodes-panel">
      <div className="card">
        <div
          className="card-header"
          style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 10 }}
        >
          <div>
            <div className="card-title">Shop summary</div>
            <div className="card-subtitle">{shop.name || `Shop ${shop.shop_id}`} · {rangeLabel}</div>
          </div>
          <button
            type="button"
            className="secondary-button action-button"
            onClick={onSummaryRefresh}
            disabled={summaryLoading}
            style={{ minWidth: 110 }}
          >
            {summaryLoading ? "Loading…" : "Refresh"}
          </button>
        </div>
        <div className="card-body summary-body">{renderSummary()}</div>
      </div>
    </div>
  );
}

function SummaryStat({ label, value, hint, className }: { label: string; value: string; hint?: string; className?: string }) {
  return (
    <div className={"summary-stat" + (className ? ` ${className}` : "")}>
      <div className="summary-stat-label">{label}</div>
      <div className="summary-stat-value">{value}</div>
      {hint && <div className="summary-stat-hint">{hint}</div>}
    </div>
  );
}

function SummaryList({
  title,
  items,
  maxHeight,
  fill,
}: {
  title: string;
  items: Array<{ label: string; value: string; secondary?: string }>;
  maxHeight?: number;
  fill?: boolean;
}) {
  return (
    <div className={"summary-list" + (fill ? " summary-list--fill" : "")}>
      <div className="summary-list-title">{title}</div>
      <ul style={maxHeight ? { maxHeight, overflowY: "auto" } : undefined}>
        {items && items.length ? (
          items.map((item, idx) => (
            <li key={item.label + idx} className="summary-list-item">
              <div className="summary-list-text">
                <div className="summary-list-label">{item.label}</div>
                {item.secondary && <div className="summary-list-sub">{item.secondary}</div>}
              </div>
              <div className="summary-list-value">{item.value}</div>
            </li>
          ))
        ) : (
          <li className="summary-list-empty">No data in range.</li>
        )}
      </ul>
    </div>
  );
}

function OrderList({ orders }: { orders: ShopOrderSummary[] }) {
  return (
    <div className="summary-list summary-list--fill">
      <div className="summary-list-title">All orders ({orders.length})</div>
      <ul className="summary-order-list">
        {orders.length ? (
          orders.map((order) => (
            <li key={order.order_id + "." + order.order_time}>
              <details className="summary-order-item">
                <summary className="summary-list-item summary-order-summary">
                  <div className="summary-list-text">
                    <div className="summary-list-label">Order #{order.order_id}</div>
                    <div className="summary-list-sub">
                      {formatOrderDateTime(order.order_time)} · {order.items} items · {order.order_type || "dine-in"}
                    </div>
                  </div>
                  <div className="summary-list-value">{formatCurrency(order.total_cents)}</div>
                </summary>
                <ul className="summary-order-lines">
                  {(order.order_items || []).length ? (
                    (order.order_items || []).map((item, idx) => (
                      <li key={order.order_id + "." + item.name + "." + idx} className="summary-list-item">
                        <div className="summary-list-text">
                          <div className="summary-list-label">{item.name}</div>
                          <div className="summary-list-sub">
                            {item.quantity} x {formatCurrency(item.unit_price_cents)}
                          </div>
                        </div>
                        <div className="summary-list-value">{formatCurrency(item.line_total_cents)}</div>
                      </li>
                    ))
                  ) : (
                    <li className="summary-list-empty">No items found for this order.</li>
                  )}
                </ul>
              </details>
            </li>
          ))
        ) : (
          <li className="summary-list-empty">No data in range.</li>
        )}
      </ul>
    </div>
  );
}

function SummaryRangePicker({
  range,
  onChangeField,
  onCancel,
  onApply,
  disabled,
}: {
  range: { start: string; end: string };
  onChangeField: (field: "start" | "end", next: Date) => void;
  onCancel: () => void;
  onApply: () => void;
  disabled: boolean;
}) {
  const handleInputChange = (field: "start" | "end", value: string) => {
    const nextDate = parseLocalDateTime(value);
    if (!nextDate) return;
    onChangeField(field, nextDate);
  };

  return (
    <div className="summary-popover summary-popover--range">
      <div className="summary-popover-title">Choose time range</div>
      <div className="time-range-fields">
        <label className="time-range-field">
          <span>Start</span>
          <input
            className="time-range-input"
            type="datetime-local"
            step={60}
            value={range.start}
            onChange={(event) => handleInputChange("start", event.target.value)}
          />
        </label>
        <label className="time-range-field">
          <span>End</span>
          <input
            className="time-range-input"
            type="datetime-local"
            step={60}
            value={range.end}
            onChange={(event) => handleInputChange("end", event.target.value)}
          />
        </label>
      </div>

      <div className="summary-popover-actions">
        <button type="button" className="secondary-button" onClick={onCancel}>
          Cancel
        </button>
        <button type="button" className="submit-button" onClick={onApply} disabled={disabled}>
          {disabled ? "Working…" : "OK"}
        </button>
      </div>
    </div>
  );
}

function DbInfoPanel({
  dbInfo,
  loading,
  error,
  onRefresh,
}: {
  dbInfo: DbSchemaOverview | null;
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
}) {
  return (
    <div className="card hideable-window">
      <div className="card-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 8 }}>
        <div>
          <div className="card-title">Database info</div>
          <div className="card-subtitle">
            {(dbInfo?.database || "—") + " / " + (dbInfo?.schema || "—")}
          </div>
        </div>
        <button type="button" className="secondary-button action-button" onClick={onRefresh} disabled={loading}>
          {loading ? "Loading..." : "Refresh"}
        </button>
      </div>
      <div className="card-body">
        {error && <div className="status-msg">{error}</div>}
        {!error && loading && <div className="placeholder" style={{ minHeight: 120 }}>Loading database schema...</div>}
        {!error && !loading && dbInfo && (
          <div className="summary-lists">
            {dbInfo.tables.map((table) => (
              <div className="summary-list" key={(table.schema_name || "public") + "." + table.table}>
                <div className="summary-list-title">
                  {(table.schema_name ? `${table.schema_name}.` : "") + table.table} ({table.row_count} rows)
                </div>
                <ul>
                  {table.columns.map((col) => (
                    <li key={(table.schema_name || "public") + "." + table.table + "." + col.name} className="summary-list-item">
                      <div className="summary-list-text">
                        <div className="summary-list-label">{col.name}</div>
                        <div className="summary-list-sub">{col.data_type}</div>
                      </div>
                      <div className="summary-list-value">{col.is_nullable === "YES" ? "NULL" : "NOT NULL"}</div>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function PurchasedSummaryPanel({
  shopName,
  summary,
  range,
  filters,
  error,
  loading,
  onGenerate,
  onStatus,
  onPreviewImage,
  inlinePreviewPanel,
}: {
  shopName?: string;
  summary: PurchasedSummary | null;
  range: { start: string; end: string };
  filters: PurchasedSummaryFilters;
  error: string | null;
  loading: boolean;
  onGenerate: () => void;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number, pageId?: number) => void;
  inlinePreviewPanel?: ReactNode;
}) {
  const rangeLabel =
    `${(summary?.time_range?.start || range.start || "start").replace("T", " ")} → ${(summary?.time_range?.end || range.end || "end").replace("T", " ")}`;
  const activeFilters = summary?.filters || filters;
  const filterParts = [
    activeFilters.product_name.trim() ? `Product: ${activeFilters.product_name.trim()}` : "",
    activeFilters.supplier_name.trim() ? `Supplier: ${activeFilters.supplier_name.trim()}` : "",
  ].filter(Boolean);
  const filterLabel = filterParts.length ? ` · ${filterParts.join(" · ")}` : "";
  const refreshButton = (
    <button
      type="button"
      className="secondary-button action-button"
      onClick={onGenerate}
      disabled={loading}
      style={{ minWidth: 110 }}
    >
      {loading ? "Loading…" : "Refresh"}
    </button>
  );

  if (loading) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">Purchase summary</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}${filterLabel}`}</div>
            </div>
            {refreshButton}
          </div>
          <div className="card-body summary-body">
            <div className="placeholder">Generating purchase summary...</div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">Purchase summary</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}${filterLabel}`}</div>
            </div>
            {refreshButton}
          </div>
          <div className="card-body summary-body">
            <ErrorCard message={error} />
          </div>
        </div>
      </div>
    );
  }

  if (!summary) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">Purchase summary</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}${filterLabel}`}</div>
            </div>
            {refreshButton}
          </div>
          <div className="card-body summary-body">
            <div className="placeholder">Run SUMMARY to see purchases for this shop.</div>
          </div>
        </div>
      </div>
    );
  }

  const selectedRows = (summary.selected_items || []).map((item) => ({
    id: item.id,
    ocr_id: item.ocr_id,
    ocr_page_id: item.ocr_page_id,
    picture_id: `${item.ocr_id || 0}#${item.ocr_page_id || 0}`,
    purchase_date: item.purchase_date,
    product: item.product,
    supplier: item.supplier,
    unit_price: item.unit_price_cents,
    quantity: item.quantity,
  }));

  return (
    <div className="display-panel">
      <div className="card card--summary">
        <div className="card-header summary-card-header">
          <div className="summary-card-heading">
            <div className="card-title">Purchase summary</div>
            <div className="card-subtitle">
              {(summary.shop_name || shopName || summary.database || "Selected shop")} · {rangeLabel}{filterLabel}
            </div>
          </div>
          {refreshButton}
        </div>
        <div className="card-body summary-body">
          <div className="summary-metrics">
            <SummaryStat label="Total cost" value={formatCurrency(summary.totals?.total_cost_cents)} hint={`${summary.totals?.orders || 0} orders`} />
            <SummaryStat label="Average order cost" value={formatCurrency(summary.totals?.avg_order_cost_cents)} hint="AOC" />
            <SummaryStat label="Items purchased" value={`${summary.totals?.items || 0}`} hint={`${summary.totals?.products || 0} products`} />
            <SummaryStat label="Suppliers" value={`${summary.totals?.suppliers || 0}`} hint="In selected range" />
          </div>
          <div className="summary-lists">
            <SummaryList
              title="Top suppliers"
              items={(summary.top_suppliers || []).map((item) => ({
                label: item.name,
                value: formatCurrency(item.total_cost_cents),
                secondary: `${item.orders || 0} orders`,
              }))}
            />
            <SummaryList
              title="Top purchased products"
              items={(summary.top_products || []).map((item) => ({
                label: item.name,
                value: `${item.quantity || 0}`,
                secondary: formatCurrency(item.total_cost_cents),
              }))}
            />
          </div>
        </div>
      </div>

      {selectedRows.length ? (
        <ResultTable
          rows={selectedRows}
          sql={buildPurchasedItemsSql(summary.time_range, summary.shop_id, activeFilters)}
          shopId={summary.shop_id ?? null}
          sourceKind="expense"
          onStatus={onStatus}
          onPreviewImage={onPreviewImage}
          displayColumns={["picture_id", "purchase_date", "product", "supplier", "unit_price", "quantity"]}
          lockedColumns={["id", "picture_id", "purchase_date", "supplier", "product"]}
          inlinePreviewPanel={inlinePreviewPanel}
        />
      ) : (
        <div className="card">
          <div className="card-header">
            <div className="card-title">Purchased items</div>
            <div className="card-subtitle">No line items in the selected range.</div>
          </div>
          <div className="card-body">
            <div className="placeholder">No items in selected range.</div>
          </div>
        </div>
      )}
    </div>
  );
}

function AiDataQueryPanel({
  shopId,
  shopName,
  result,
  question,
  range,
  error,
  loading,
  onStatus,
  onPreviewImage,
  inlinePreviewPanel,
}: {
  shopId: number | null;
  shopName?: string;
  result: AiDataQueryResponse | null;
  question: string;
  range: { start: string; end: string };
  error: string | null;
  loading: boolean;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number, pageId?: number) => void;
  inlinePreviewPanel?: ReactNode;
}) {
  const rangeLabel =
    `${(result?.time_range?.start || range.start || "start").replace("T", " ")} → ${(result?.time_range?.end || range.end || "end").replace("T", " ")}`;
  const title = result?.answer_title || "AI query";

  if (loading) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">AI query</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}`}</div>
            </div>
          </div>
          <div className="card-body summary-body">
            <div className="placeholder">Generating SQL and querying POS/EXP databases...</div>
          </div>
        </div>
      </div>
    );
  }

  if (!result && error) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">AI query</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}`}</div>
            </div>
          </div>
          <div className="card-body summary-body">
            <ErrorCard message={error} />
          </div>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="display-panel">
        <div className="card card--summary">
          <div className="card-header summary-card-header">
            <div className="summary-card-heading">
              <div className="card-title">AI query</div>
              <div className="card-subtitle">{`${shopName || "Selected shop"} · ${rangeLabel}`}</div>
            </div>
          </div>
          <div className="card-body summary-body">
            <div className="placeholder">Type a question below, choose a time span, then run ASK.</div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="display-panel ai-query-display">
      <section className="display-query">
        <div className="display-query-label">{title}</div>
        <div className="display-query-text">{result.question || question}</div>
        <div className="display-meta">{`${shopName || "Selected shop"} · ${rangeLabel}`}</div>
        {result.notes ? <div className="display-meta">{result.notes}</div> : null}
        {error ? <div className="status-msg">{error}</div> : null}
      </section>

      {(result.queries || []).map((query, index) => {
        const rows = query.result?.rows ?? [];
        return (
          <section className="ai-query-result" key={`${query.source_kind}-${index}-${query.title}`}>
            <div className="display-query">
              <div className="display-query-label">{`${query.source_kind.toUpperCase()} · ${query.title || `Query ${index + 1}`}`}</div>
              <code className="sql-block">{query.sql}</code>
            </div>
            {query.error || query.result?.error ? (
              <ErrorCard message={query.error || query.result?.error || "Query failed"} />
            ) : rows.length ? (
              <>
                <QueryResultSummary rows={rows} />
                <ResultTable
                  rows={rows}
                  sql={query.sql}
                  shopId={shopId}
                  sourceKind={query.source_kind}
                  onStatus={onStatus}
                  onPreviewImage={onPreviewImage}
                  inlinePreviewPanel={inlinePreviewPanel}
                  readOnly
                />
              </>
            ) : (
              <>
                <QueryResultSummary rows={rows} />
                <div className="card">
                  <div className="card-header">
                    <div className="card-title">Query results</div>
                    <div className="card-subtitle">{query.source_kind.toUpperCase()}</div>
                  </div>
                  <div className="card-body">
                    <div className="placeholder">No rows returned.</div>
                  </div>
                </div>
              </>
            )}
          </section>
        );
      })}
    </div>
  );
}

function QueryResultSummary({ rows }: { rows: Array<Record<string, unknown>> }) {
  const summaries = summarizeQueryRows(rows);
  return (
    <div className="card ai-query-summary-card">
      <div className="card-header">
        <div>
          <div className="card-title">Query summary</div>
          <div className="card-subtitle">Aggregated from returned rows</div>
        </div>
      </div>
      <div className="card-body">
        <div className="summary-metrics ai-query-summary-metrics">
          {summaries.map((item) => (
            <SummaryStat key={item.label} label={item.label} value={item.value} hint={item.hint} />
          ))}
        </div>
      </div>
    </div>
  );
}

function DisplayPanel({
  item,
  onStatus,
  onPreviewImage,
  inlinePreviewPanel,
  shopId,
  sourceKind,
}: {
  item: HistoryItem;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number, pageId?: number) => void;
  inlinePreviewPanel?: ReactNode;
  shopId: number | null;
  sourceKind: SqlSourceKind;
}) {
  const hasRows = item.result?.rows && item.result.rows.length > 0;
  return (
    <div className="display-panel">
      <header className="display-header">
        <div className="display-title">{item.title}</div>
      </header>

      <section className="display-query">
        <div className="display-query-label">Request</div>
        <div className="display-query-text">{item.input}</div>
        {item.sql && (
          <div className="display-query-sql">
            <div className="display-query-label">SQL</div>
            <code className="sql-block">{item.sql}</code>
          </div>
        )}
      </section>

      <section className="display-content">
        {item.error ? (
          <ErrorCard message={item.error || item.result?.error || "Error"} />
        ) : hasRows ? (
          <ResultTable
            rows={item.result?.rows ?? []}
            sql={item.sql}
            shopId={shopId}
            sourceKind={sourceKind}
            onStatus={onStatus}
            onPreviewImage={onPreviewImage}
            inlinePreviewPanel={inlinePreviewPanel}
          />
        ) : (
          <div className="placeholder">No rows returned.</div>
        )}
      </section>
    </div>
  );
}

function ErrorCard({ message }: { message: string }) {
  return (
    <div className="card card--error">
      <div className="card-header">
        <div className="card-title">Error</div>
      </div>
      <div className="card-body">
        <p className="error-text">{message}</p>
      </div>
    </div>
  );
}

function OcrImagePanel({
  panelRef,
  frameRef,
  imagePreview,
  imagePreviewLoading,
  targetLabel,
  shownLabel,
  imageZoom,
  onZoomIn,
  onZoomOut,
  onResetZoom,
  onClose,
  onImageLoad,
}: {
  panelRef: RefObject<HTMLElement | null>;
  frameRef: RefObject<HTMLDivElement | null>;
  imagePreview: { src: string; path: string } | null;
  imagePreviewLoading: boolean;
  targetLabel: string | null;
  shownLabel: string | null;
  imageZoom: number;
  onZoomIn: () => void;
  onZoomOut: () => void;
  onResetZoom: () => void;
  onClose: () => void;
  onImageLoad: () => void;
}) {
  return (
    <aside
      className="image-panel hideable-window"
      ref={panelRef}
      tabIndex={-1}
      style={{
        display: "flex",
        flexDirection: "column",
        overflow: "hidden",
        minWidth: 0,
      }}
    >
      <div
        className="image-panel-header"
        style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 8, gap: 8 }}
      >
        <div style={{ display: "grid", gap: 2, minWidth: 0 }}>
          <div style={{ color: "#cfd8e3", fontWeight: 600 }}>OCR Image</div>
          <div style={{ color: imagePreviewLoading ? "#fbbf24" : "#8fa5bf", fontSize: 12, overflowWrap: "anywhere" }}>
            {imagePreviewLoading && targetLabel
              ? `Loading ${targetLabel}${shownLabel ? ` · showing ${shownLabel} until ready` : ""}`
              : shownLabel
              ? `Showing ${shownLabel}`
              : "Click an ocr_id to preview the scan."}
          </div>
        </div>
        <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
          <button
            type="button"
            className="secondary-button"
            onClick={onZoomIn}
            disabled={!imagePreview}
          >
            +
          </button>
          <button
            type="button"
            className="secondary-button"
            onClick={onZoomOut}
            disabled={!imagePreview}
          >
            −
          </button>
          <button
            type="button"
            className="secondary-button"
            onClick={onResetZoom}
            disabled={!imagePreview}
          >
            100%
          </button>
          <button
            type="button"
            className="secondary-button"
            onClick={onClose}
            disabled={!imagePreview}
          >
            Close
          </button>
        </div>
      </div>
      {imagePreview ? (
        <div
          className="image-frame"
          ref={frameRef}
          tabIndex={-1}
          style={{
            background: "rgba(0,0,0,0.35)",
            borderRadius: 10,
            padding: 8,
            overflow: "auto",
            flex: 1,
            display: "flex",
            flexDirection: "column",
            gap: 6,
            minHeight: 0,
          }}
        >
          {imagePreviewLoading && targetLabel && (
            <div
              style={{
                padding: "8px 10px",
                borderRadius: 10,
                border: "1px solid rgba(245, 158, 11, 0.35)",
                background: "rgba(120, 53, 15, 0.22)",
                color: "#fbbf24",
                fontSize: 12,
              }}
            >
              Loading {targetLabel}. The image below is still the previous preview until the new one finishes loading.
            </div>
          )}
          <div style={{ color: "#9fb3c8", fontSize: 12, wordBreak: "break-all" }}>{imagePreview.path}</div>
          <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", position: "relative" }}>
            <img
              src={imagePreview.src}
              alt="OCR"
              onLoad={onImageLoad}
              style={{
                maxWidth: "100%",
                maxHeight: "calc(100vh - 260px)",
                objectFit: "contain",
                borderRadius: 8,
                boxShadow: "0 6px 22px rgba(0,0,0,0.35)",
                opacity: imagePreviewLoading ? 0.35 : 1,
                transform: `scale(${imageZoom})`,
                transformOrigin: "center center",
                transition: "transform 120ms ease",
              }}
            />
            {imagePreviewLoading && (
              <div
                style={{
                  position: "absolute",
                  inset: 0,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  pointerEvents: "none",
                }}
              >
                <div
                  style={{
                    padding: "10px 14px",
                    borderRadius: 12,
                    border: "1px solid rgba(245, 158, 11, 0.35)",
                    background: "rgba(15, 23, 42, 0.9)",
                    color: "#fbbf24",
                    fontSize: 13,
                    fontWeight: 600,
                  }}
                >
                  Loading new OCR image…
                </div>
              </div>
            )}
          </div>
        </div>
      ) : (
        <div
          style={{
            flex: 1,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#70839b",
            fontSize: 14,
            border: "1px dashed rgba(255,255,255,0.15)",
            borderRadius: 10,
            background: "rgba(0,0,0,0.2)",
          }}
        >
          {imagePreviewLoading && targetLabel
            ? `Loading ${targetLabel}…`
            : "Click an ocr_id to preview the scan."}
        </div>
      )}
    </aside>
  );
}

function ResultTable({
  rows,
  sql,
  shopId,
  sourceKind,
  onStatus,
  onPreviewImage,
  displayColumns,
  lockedColumns,
  inlinePreviewPanel,
  readOnly = false,
}: {
  rows: Array<Record<string, unknown>>;
  sql?: string;
  shopId: number | null;
  sourceKind: SqlSourceKind;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number, pageId?: number) => void;
  displayColumns?: string[];
  lockedColumns?: string[];
  inlinePreviewPanel?: ReactNode;
  readOnly?: boolean;
}) {
  const [editMode, setEditMode] = useState(false);
  const [draftRows, setDraftRows] = useState(rows);
  const [changes, setChanges] = useState<Record<number, Record<string, unknown>>>({});
  const [tableName, setTableName] = useState(inferTableFromSql(sql ?? "") || "");
  const [keyColumn] = useState("id");
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [saveMsg, setSaveMsg] = useState<string | null>(null);
  const [editingCell, setEditingCell] = useState<{ row: number; col: string } | null>(null);
  const [selectedRow, setSelectedRow] = useState<number | null>(null);
  const [sortConfig, setSortConfig] = useState<{ col: string; dir: "asc" | "desc" } | null>(null);
  const [tableMaxHeight, setTableMaxHeight] = useState<number | null>(null);
  const tableWrapperRef = useRef<HTMLDivElement | null>(null);
  const editInputRef = useRef<HTMLInputElement | null>(null);
  const hasChanges = Object.keys(changes).length > 0;
  const saveDisabled = readOnly || !editMode || saving || !hasChanges;
  const lockedCols = useMemo(() => new Set(["ocr_id", "ocr_page_id", "page_id", ...(lockedColumns || [])]), [lockedColumns]);

  const cancelEdits = useCallback(() => {
    setEditMode(false);
    setEditingCell(null);
    setDraftRows(rows);
    setChanges({});
    onStatus?.(null);
  }, [rows, onStatus]);

  const handleSaveChanges = useCallback(async () => {
    if (readOnly) return;
    if (shopId === null) {
      setSaveMsg("Select a shop first");
      return;
    }
    const payloadRows = Object.entries(changes).map(([idx, cols]) => {
      const rowIdx = Number(idx);
      const key = draftRows[rowIdx]?.[keyColumn];
      return { key, changes: cols };
    }).filter((r) => r.key !== undefined);

    if (!payloadRows.length) {
      setSaveMsg("No changes to save");
      return;
    }

    let targetTable = tableName;
    if (!targetTable) {
      const promptTable = window.prompt("Table name for updates", "suppliers");
      if (!promptTable) return;
      targetTable = promptTable;
      setTableName(promptTable);
    }

    setSaving(true);
    setSaveMsg(null);
    onStatus?.("Saving changes…");
    try {
      const res = await apiFetch("/table_update", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: shopId,
          source_kind: sourceKind,
          table: targetTable,
          key_column: keyColumn,
          rows: payloadRows,
        }),
      });
      const data = await res.json();
      if (!res.ok || data.error) {
        const msg = data.error || `HTTP ${res.status}`;
        setSaveMsg(msg);
        onStatus?.(msg);
      } else {
        setSaveMsg(`Saved ${data.updated} row(s)`);
        onStatus?.(null);
        setChanges({});
        setEditMode(false);
        setEditingCell(null);
        // Refresh view: re-run the last SQL
        if (sql) {
          try {
            const res2 = await apiFetch("/execute_sql", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ shop_id: shopId, source_kind: sourceKind, sql }),
            });
            const data2 = await res2.json();
            if (res2.ok && !data2.error && data2.result?.rows) {
              setDraftRows(data2.result.rows);
            } else {
              setDraftRows(rows);
            }
          } catch {
            setDraftRows(rows);
          }
        } else {
          setDraftRows(rows);
        }
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to save";
      setSaveMsg(msg);
      onStatus?.(msg);
    } finally {
      setSaving(false);
    }
  }, [changes, draftRows, keyColumn, onStatus, readOnly, rows, shopId, sourceKind, sql, tableName]);

  useEffect(() => {
    setDraftRows(rows);
    setChanges({});
    setTableName((current) => inferTableFromSql(sql ?? "") || current);
    setEditingCell(null);
    setEditMode(false);
    setSelectedRow(null);
  }, [rows, sql]);

  useEffect(() => {
    if (editMode && editingCell && editInputRef.current) {
      editInputRef.current.focus();
      editInputRef.current.select();
    }
  }, [editMode, editingCell]);

  useEffect(() => {
    if (!editMode) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        e.preventDefault();
        cancelEdits();
      } else if (e.key === "Enter" && hasChanges) {
        e.preventDefault();
        handleSaveChanges();
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [editMode, hasChanges, cancelEdits, handleSaveChanges]);

  const updateTableMaxHeight = useCallback(() => {
    if (window.matchMedia("(max-width: 640px)").matches) {
      setTableMaxHeight(null);
      return;
    }
    const tableEl = tableWrapperRef.current;
    if (!tableEl) return;
    const inputShell = document.querySelector(".input-shell");
    const inputTop = inputShell instanceof HTMLElement ? inputShell.getBoundingClientRect().top : window.innerHeight;
    const tableTop = tableEl.getBoundingClientRect().top;
    const available = inputTop - tableTop - 36; // leave breathing room above the input bar + status text
    const minimumVisibleHeight = 180;
    setTableMaxHeight(Math.max(minimumVisibleHeight, available));
  }, []);

  useEffect(() => {
    updateTableMaxHeight();
    const onResize = () => updateTableMaxHeight();
    window.addEventListener("resize", onResize);
    const inputShell = document.querySelector(".input-shell");
    const resizeObserver =
      "ResizeObserver" in window && inputShell
        ? new ResizeObserver(() => updateTableMaxHeight())
        : null;
    if (resizeObserver && inputShell) resizeObserver.observe(inputShell);

    return () => {
      window.removeEventListener("resize", onResize);
      resizeObserver?.disconnect();
    };
  }, [updateTableMaxHeight]);

  useEffect(() => {
    updateTableMaxHeight();
  }, [rows.length, updateTableMaxHeight]);

  const columns = displayColumns?.length ? displayColumns : rows.length ? Object.keys(rows[0]) : [];
  const sortedRowIndexes = useMemo(() => {
    const indexes = draftRows.map((_, idx) => idx);
    if (!sortConfig) return indexes;
    const { col, dir } = sortConfig;
    indexes.sort((a, b) => {
      const av = draftRows[a]?.[col];
      const bv = draftRows[b]?.[col];
      const as = av === null || av === undefined ? "" : String(av);
      const bs = bv === null || bv === undefined ? "" : String(bv);
      const an = Number(as);
      const bn = Number(bs);
      let cmp = 0;
      if (!Number.isNaN(an) && !Number.isNaN(bn) && as.trim() !== "" && bs.trim() !== "") {
        cmp = an - bn;
      } else {
        cmp = as.localeCompare(bs, undefined, { sensitivity: "base" });
      }
      return dir === "asc" ? cmp : -cmp;
    });
    return indexes;
  }, [draftRows, sortConfig]);

  const handleCellChange = (rowIdx: number, col: string, value: string) => {
    setEditMode(true);
    setDraftRows((prev) => {
      const next = [...prev];
      const newRow = { ...next[rowIdx] };

      const original = rows[rowIdx]?.[col];
      let newVal: unknown = value;
      if (typeof original === "number") {
        newVal = value === "" ? null : Number(value);
      }

      newRow[col] = newVal;
      next[rowIdx] = newRow;
      return next;
    });

    setChanges((prev) => {
      const orig = rows[rowIdx]?.[col];
      const origStr = formatCell(orig);
      const currentStr = value;
      const next = { ...prev };
      if (origStr === currentStr) {
        if (next[rowIdx]) {
          delete next[rowIdx][col];
          if (Object.keys(next[rowIdx]).length === 0) delete next[rowIdx];
        }
      } else {
        const rowChanges = { ...(next[rowIdx] || {}) };
        const original = rows[rowIdx]?.[col];
        let newVal: unknown = value;
        if (typeof original === "number") newVal = value === "" ? null : Number(value);
        rowChanges[col] = newVal;
        next[rowIdx] = rowChanges;
      }
      return next;
    });
  };

  const handleDeleteRow = useCallback(
    async (rowIdx: number) => {
      if (readOnly) return;
      if (shopId === null) {
        setSaveMsg("Select a shop first");
        return;
      }
      const key = draftRows[rowIdx]?.[keyColumn];
      if (key === undefined) {
        setSaveMsg("Selected row has no key to delete");
        return;
      }

      let targetTable = tableName;
      if (!targetTable) {
        const promptTable = window.prompt("Table name for delete", "suppliers");
        if (!promptTable) return;
        targetTable = promptTable;
        setTableName(promptTable);
      }

      setDeleting(true);
      setSaveMsg(null);
      onStatus?.("Deleting row…");
      try {
        const res = await apiFetch("/table_delete", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            shop_id: shopId,
            source_kind: sourceKind,
            table: targetTable,
            key_column: keyColumn,
            keys: [key],
          }),
        });
        const data = await res.json();
        if (!res.ok || data.error) {
          const msg = data.error || `HTTP ${res.status}`;
          setSaveMsg(msg);
          onStatus?.(msg);
        } else {
          const deleted = Number(data.deleted ?? 0);
          setSaveMsg(`Deleted ${deleted} row(s)`);
          onStatus?.(null);
          setSelectedRow(null);

          // Refresh: if SQL exists, re-run, else remove from local draft
          if (sql) {
            try {
              const res2 = await apiFetch("/execute_sql", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ shop_id: shopId, source_kind: sourceKind, sql }),
              });
              const data2 = await res2.json();
              if (res2.ok && !data2.error && data2.result?.rows) {
                setDraftRows(data2.result.rows);
              } else {
                setDraftRows(rows.filter((_, i) => i !== rowIdx));
              }
            } catch {
              setDraftRows(rows.filter((_, i) => i !== rowIdx));
            }
          } else {
            setDraftRows(rows.filter((_, i) => i !== rowIdx));
          }
          setChanges({});
          setEditMode(false);
        }
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to delete";
        setSaveMsg(msg);
        onStatus?.(msg);
      } finally {
        setDeleting(false);
      }
    },
    [draftRows, keyColumn, onStatus, readOnly, rows, shopId, sourceKind, sql, tableName]
  );

  if (!rows.length) {
    return <div className="placeholder">No results.</div>;
  }

  return (
    <div className="card">
      <div
        className="card-header card-header--results"
        style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}
      >
        <div>
          <div className="card-title">Query results</div>
          <div className="card-subtitle">
            {rows.length} row{rows.length === 1 ? "" : "s"}
          </div>
        </div>
        {!readOnly && (
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <button
              type="button"
              className="danger-button"
              disabled={deleting || selectedRow === null}
              onClick={() => {
                if (selectedRow === null) return;
                handleDeleteRow(selectedRow);
              }}
              style={{ minWidth: 120 }}
            >
              {deleting ? "Deleting…" : "Delete row"}
            </button>
            <button
              type="button"
              className="submit-button"
              disabled={saveDisabled}
              onClick={handleSaveChanges}
              style={{ opacity: saveDisabled ? 0.6 : 1, cursor: saveDisabled ? "not-allowed" : "pointer", minWidth: 140 }}
            >
              {saving ? "Saving…" : "Save changes"}
            </button>
          </div>
        )}
      </div>
      <div className="card-body card-body--results">
        {inlinePreviewPanel ? <div className="result-preview-slot">{inlinePreviewPanel}</div> : null}
        <div
          className="table-wrapper"
          ref={tableWrapperRef}
          style={tableMaxHeight ? { maxHeight: tableMaxHeight } : undefined}
        >
          <table>
            <thead>
              <tr>
                {columns.map((col) => (
                  <th
                    key={col}
                    style={{ cursor: "pointer", userSelect: "none" }}
                    onClick={() =>
                      setSortConfig((prev) =>
                        prev && prev.col === col
                          ? { col, dir: prev.dir === "asc" ? "desc" : "asc" }
                          : { col, dir: "asc" }
                      )
                    }
                  >
                    {col}
                    {sortConfig?.col === col ? (sortConfig.dir === "asc" ? " ↑" : " ↓") : ""}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {sortedRowIndexes.map((rowIdx) => {
                const row = draftRows[rowIdx];
                const isSelected = selectedRow === rowIdx;
                return (
                  <tr
                    key={rowIdx}
                    className={"table-row" + (isSelected ? " table-row--selected" : "")}
                    onClick={() => setSelectedRow(rowIdx)}
                  >
                    {columns.map((col) => (
                      <td
                        key={col}
                        onClick={() => {
                          setSelectedRow(rowIdx);
                          if (col === "picture_id" || col === "ocr_id" || col === "ocr_page_id") {
                            if (onPreviewImage) {
                              const ocrVal = row["ocr_id"];
                              const pageVal = row["ocr_page_id"] ?? row["page_id"];
                              if (ocrVal === null || ocrVal === undefined || ocrVal === "") {
                                onStatus?.("This row has no ocr_id to preview.");
                                return;
                              }
                              const ocrNum = typeof ocrVal === "number" ? ocrVal : Number(ocrVal);
                              const pageNum = typeof pageVal === "number" ? pageVal : Number(pageVal);
                              if (Number.isFinite(ocrNum) && ocrNum > 0) {
                                onPreviewImage(ocrNum, Number.isFinite(pageNum) && pageNum > 0 ? pageNum : undefined);
                              } else {
                                onStatus?.("This row has no valid ocr_id to preview.");
                              }
                            }
                            return;
                          }
                        }}
                        onDoubleClick={() => {
                          if (readOnly) return;
                          setSelectedRow(rowIdx);
                          if (lockedCols.has(col)) return;
                          setEditMode(true);
                          setEditingCell({ row: rowIdx, col });
                        }}
                        className={
                          editingCell && editingCell.row === rowIdx && editingCell.col === col
                            ? "cell--editing"
                            : lockedCols.has(col)
                            ? "cell--locked"
                            : undefined
                        }
                      >
                        {editMode &&
                        editingCell &&
                        editingCell.row === rowIdx &&
                        editingCell.col === col &&
                        !lockedCols.has(col) ? (
                          <input
                            className="cell-input"
                            value={formatCellInput(row[col])}
                            ref={
                              editingCell && editingCell.row === rowIdx && editingCell.col === col
                                ? editInputRef
                                : undefined
                            }
                            onChange={(e) => handleCellChange(rowIdx, col, e.target.value)}
                          />
                        ) : (
                          formatCell(row[col])
                        )}
                      </td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        {saveMsg && <div className="status-msg">{saveMsg}</div>}
      </div>
    </div>
  );
}

function formatCell(value: unknown) {
  if (value === null || value === undefined) return "—";
  if (typeof value === "object") return JSON.stringify(value);
  return String(value);
}

function formatCellInput(value: unknown) {
  if (value === null || value === undefined) return "";
  if (typeof value === "object") return JSON.stringify(value);
  return String(value);
}

function inferTableFromSql(sql: string): string | null {
  const match = /from\s+([a-zA-Z0-9_.]+)/i.exec(sql);
  if (match && match[1]) return match[1];
  return null;
}
