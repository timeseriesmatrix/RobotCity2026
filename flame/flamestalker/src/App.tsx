// src/App.tsx
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { FormEvent } from "react";

type ActiveView = "nodes" | "data" | "scan";
type DataMode = "sql" | "purchased";

interface HistoryItem {
  id: string;
  title: string;
  input: string; // raw prompt or text
  sql: string;
  error: string;
  result: ServerResult | null;
  source: "text" | "audio";
  model: string;
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
  sql: string;
  error: string;
  source: string;
  model: string;
  result: ServerResult;
}

interface ShopInfo {
  shop_id: number;
  name?: string;
  host?: string;
  port?: string;
  dbname?: string;
  user?: string;
  password?: string;
  conninfo?: string;
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
  invoice_id: string;
  purchase_date: string;
  supplier: string;
  product: string;
  quantity: number;
  unit_price_cents: number;
  total_price_cents: number;
}

interface PurchasedSummary {
  time_range: { start: string; end: string };
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
  table: string;
  row_count: number;
  columns: DbColumnInfo[];
}

interface DbSchemaOverview {
  database: string;
  schema: string;
  tables: DbTableInfo[];
  error?: string;
}

interface OcrScan {
  id: number;
  image_path: string;
  scan_type: string;
  extracted_text: string;
  scanned_at: string;
  shop_id: number;
  position?: number;
  total?: number;
  has_prev?: boolean;
  has_next?: boolean;
}

function buildPurchasedItemsSql(range: { start: string; end: string }) {
  return [
    "SELECT",
    "  pi.id AS id,",
    "  COALESCE(po.ocr_id, 0) AS ocr_id,",
    "  COALESCE(po.invoice_id, '') AS invoice_id,",
    "  po.purchase_date::text AS purchase_date,",
    "  COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,",
    "  COALESCE(NULLIF(p.name, ''), 'Unknown') AS product,",
    "  COALESCE(pi.quantity, 0) AS quantity,",
    "  COALESCE(pi.unit_price, 0) AS unit_price_cents,",
    "  COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0)) AS total_price_cents",
    "FROM purchase_items pi",
    "JOIN purchase_orders po ON po.id = pi.purchase_id",
    "LEFT JOIN suppliers s ON s.id = po.supplier_id",
    "LEFT JOIN products p ON p.id = pi.product_id",
    `WHERE po.purchase_date::timestamp BETWEEN '${range.start.replace("T", " ")}'::timestamp AND '${range.end.replace("T", " ")}'::timestamp`,
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
  const start = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
  return { start: toLocalInputValue(start), end: toLocalInputValue(now) };
}

function defaultSummaryRangeToday() {
  const now = new Date();
  const start = new Date(now);
  const end = new Date(now);
  start.setHours(0, 0, 0, 0);
  end.setHours(23, 59, 0, 0);
  return { start: toLocalInputValue(start), end: toLocalInputValue(end) };
}

function defaultScanRangeToday() {
  const now = new Date();
  const start = new Date(now);
  start.setHours(1, 0, 0, 0);
  return { start: toLocalInputValue(start), end: toLocalInputValue(now) };
}

const MONTH_LABELS = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];
const WEEKDAY_LABELS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
const TIME_MINUTES = [0, 15, 30, 45];

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

function formatPickerLabel(value: string) {
  const date = parseLocalDateTime(value);
  if (!date) return "Not set";
  return `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())} ${pad2(
    date.getHours()
  )}:${pad2(date.getMinutes())}`;
}

function startOfMonth(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), 1);
}

function addMonths(date: Date, delta: number) {
  return new Date(date.getFullYear(), date.getMonth() + delta, 1);
}

function buildCalendarGrid(viewDate: Date) {
  const year = viewDate.getFullYear();
  const month = viewDate.getMonth();
  const first = new Date(year, month, 1);
  const weekdayIndex = (first.getDay() + 6) % 7; // Monday start
  const start = new Date(year, month, 1 - weekdayIndex);
  const cells: { date: Date; inMonth: boolean; isToday: boolean }[] = [];
  for (let i = 0; i < 42; i += 1) {
    const cellDate = new Date(start.getFullYear(), start.getMonth(), start.getDate() + i);
    const isToday = (() => {
      const now = new Date();
      return (
        cellDate.getFullYear() === now.getFullYear() &&
        cellDate.getMonth() === now.getMonth() &&
        cellDate.getDate() === now.getDate()
      );
    })();
    cells.push({ date: cellDate, inMonth: cellDate.getMonth() === month, isToday });
  }
  return cells;
}

const DEFAULT_OCR_DIR = "/home/liam/Data/wokandflame/ocr";
const API_BASE = `${window.location.protocol}//${window.location.hostname}:20000`;

function formatCurrency(cents: number | undefined | null) {
  const value = typeof cents === "number" ? cents : 0;
  const vatu = Math.round(value);
  return `VT ${vatu.toLocaleString(undefined, { maximumFractionDigits: 0 })}`;
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

export default function App() {
  const [sqlItem, setSqlItem] = useState<HistoryItem | null>(null);
  const [dataMode, setDataMode] = useState<DataMode>("sql");
  const [showDbInfo, setShowDbInfo] = useState(false);
  const [dbInfo, setDbInfo] = useState<DbSchemaOverview | null>(null);
  const [dbInfoLoading, setDbInfoLoading] = useState(false);
  const [dbInfoError, setDbInfoError] = useState<string | null>(null);
  const [purchasedSummary, setPurchasedSummary] = useState<PurchasedSummary | null>(null);
  const [purchasedLoading, setPurchasedLoading] = useState(false);
  const [purchasedError, setPurchasedError] = useState<string | null>(null);
  const [purchasedRange, setPurchasedRange] = useState(() => defaultSummaryRange());
  const [showPurchasedPicker, setShowPurchasedPicker] = useState(false);
  const [purchasedPickerField, setPurchasedPickerField] = useState<"start" | "end">("start");
  const [purchasedCalendarMonth, setPurchasedCalendarMonth] = useState(() =>
    startOfMonth(parseLocalDateTime(defaultSummaryRange().start) || new Date())
  );
  const [activeView, setActiveView] = useState<ActiveView>("nodes");
  const [shops, setShops] = useState<ShopInfo[]>([]);
  const [selectedShopId, setSelectedShopId] = useState<number | null>(null);
  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  const [syncResult, setSyncResult] = useState<{ at: number; body: unknown } | null>(null);
  const [syncLoading, setSyncLoading] = useState(false);
  const [summaryData, setSummaryData] = useState<ShopSummary | null>(null);
  const [summaryRange, setSummaryRange] = useState(() => defaultSummaryRangeToday());
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const [hasAutoSummaryRun, setHasAutoSummaryRun] = useState(false);
  const [mobileNodesAsideOpen, setMobileNodesAsideOpen] = useState(false);
  const [mobileNodesCommandsOpen, setMobileNodesCommandsOpen] = useState(false);
  const [nodesDisplayMaxHeight, setNodesDisplayMaxHeight] = useState<number | null>(null);
  const [showSummaryPicker, setShowSummaryPicker] = useState(false);
  const [summaryPickerField, setSummaryPickerField] = useState<"start" | "end">("start");
  const [summaryCalendarMonth, setSummaryCalendarMonth] = useState(() =>
    startOfMonth(parseLocalDateTime(defaultSummaryRangeToday().start) || new Date())
  );
  const [nodeDisplayMode, setNodeDisplayMode] = useState<"sync" | "summary">("sync");

  const [promptText, setPromptText] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [statusMsg, setStatusMsg] = useState<string | null>(null);
  const [imagePreview, setImagePreview] = useState<{ src: string; path: string } | null>(null);
  const [imageZoom, setImageZoom] = useState(1);
  const [showImagePanel, setShowImagePanel] = useState(false);
  const [scanRecord, setScanRecord] = useState<OcrScan | null>(null);
  const [scanImage, setScanImage] = useState<string>("");
  const [scanText, setScanText] = useState<string>("");
  const [scanDirty, setScanDirty] = useState(false);
  const [scanLoading, setScanLoading] = useState(false);
  const [ingestLoading, setIngestLoading] = useState(false);
  const [scanError, setScanError] = useState<string | null>(null);
  const [scanStatus, setScanStatus] = useState<string | null>(null);
  const [scanZoom, setScanZoom] = useState(1);
  const [showScanPicker, setShowScanPicker] = useState(false);
  const [scanRange, setScanRange] = useState<{ start: string; end: string }>({ start: "", end: "" });
  const [scanRangeDraft, setScanRangeDraft] = useState(() => defaultScanRangeToday());
  const [showOcrDialog, setShowOcrDialog] = useState(false);
  const [ocrDir, setOcrDir] = useState(DEFAULT_OCR_DIR);
  const [ocrLoading, setOcrLoading] = useState(false);
  const nodesDisplayRef = useRef<HTMLElement | null>(null);
  const hasScanChanges = useMemo(
    () => !!(scanRecord && scanText !== (scanRecord.extracted_text || "")),
    [scanRecord, scanText]
  );
  const isUpdateDisabled = useMemo(
    () => !scanRecord || scanLoading || ocrLoading || !hasScanChanges,
    [scanRecord, scanLoading, ocrLoading, hasScanChanges]
  );
  const promptInputRef = useRef<HTMLTextAreaElement | null>(null);

  const activeItem = sqlItem;

  const selectedShop = useMemo(
    () => shops.find((shop) => shop.shop_id === selectedShopId) ?? null,
    [shops, selectedShopId]
  );

  const updateNodesDisplayMaxHeight = useCallback(() => {
    const displayEl = nodesDisplayRef.current;
    if (!displayEl) return;
    const inputShell = document.querySelector(".input-shell");
    const inputTop = inputShell instanceof HTMLElement ? inputShell.getBoundingClientRect().top : window.innerHeight;
    const displayTop = displayEl.getBoundingClientRect().top;
    const available = inputTop - displayTop - 12;
    setNodesDisplayMaxHeight(available > 0 ? available : null);
  }, []);

  const scanRangeLabel = useMemo(() => {
    if (!scanRange.start || !scanRange.end) return "";
    return `${scanRange.start.replace("T", " ")} → ${scanRange.end.replace("T", " ")}`;
  }, [scanRange]);

  const scanPagination = useMemo(() => {
    if (!scanRecord) {
      return { position: 0, total: 0, hasPrev: false, hasNext: false };
    }
    const positionRaw = Number(scanRecord.position ?? 0);
    const totalRaw = Number(scanRecord.total ?? 0);
    const position = positionRaw > 0 ? positionRaw : 1;
    const total = totalRaw > 0 ? totalRaw : position;
    const hasPrev = scanRecord.has_prev ?? position > 1;
    const hasNext = scanRecord.has_next ?? position < total;
    return {
      position: position > 0 ? position : 0,
      total: total > 0 ? total : 0,
      hasPrev,
      hasNext,
    };
  }, [scanRecord]);

  const {
    position: scanPagePosition,
    total: scanPageTotal,
    hasPrev: scanHasPrev,
    hasNext: scanHasNext,
  } = scanPagination;

  const resizePrompt = useCallback(() => {
    const el = promptInputRef.current;
    if (!el) return;
    el.style.height = "0px";
    const next = Math.min(el.scrollHeight, 240);
    el.style.height = `${Math.max(56, next)}px`;
  }, []);

  useEffect(() => {
    resizePrompt();
  }, [promptText, resizePrompt]);

  useEffect(() => {
    const loadShops = async () => {
      try {
        const res = await fetch(`${API_BASE}/shop_databases.json`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        const incoming = Array.isArray(data)
          ? data
          : Array.isArray((data as { shops?: ShopInfo[] })?.shops)
          ? (data as { shops?: ShopInfo[] }).shops ?? []
          : [];
        if (Array.isArray(incoming)) {
          const normalized = incoming
            .filter((shop) => Number.isFinite(Number(shop?.shop_id)))
            .map((shop) => ({
              ...shop,
              shop_id: Number(shop.shop_id),
              name: shop.name || `Shop ${shop.shop_id}`,
            }));
          setShops(normalized);
          setNodeStatus(null);
        } else {
          setNodeStatus("shop_databases.json payload is not a JSON array");
        }
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load shop list";
        setNodeStatus(msg);
      }
    };
    loadShops();
  }, []);

  useEffect(() => {
    if (!shops.length) return;
    if (selectedShopId === null || !shops.some((shop) => shop.shop_id === selectedShopId)) {
      const preferred = shops.find((shop) => shop.shop_id === 1);
      setSelectedShopId((preferred || shops[0]).shop_id);
    }
  }, [shops, selectedShopId]);

  useEffect(() => {
    setSummaryData(null);
    setSummaryError(null);
    setNodeDisplayMode("sync");
  }, [selectedShopId]);

  useEffect(() => {
    if (!showSummaryPicker) return;
    const activeValue = summaryPickerField === "start" ? summaryRange.start : summaryRange.end;
    const activeDate = parseLocalDateTime(activeValue) || new Date();
    setSummaryCalendarMonth(startOfMonth(activeDate));
  }, [showSummaryPicker, summaryPickerField, summaryRange.start, summaryRange.end]);

  useEffect(() => {
    if (!showPurchasedPicker) return;
    const activeValue = purchasedPickerField === "start" ? purchasedRange.start : purchasedRange.end;
    const activeDate = parseLocalDateTime(activeValue) || new Date();
    setPurchasedCalendarMonth(startOfMonth(activeDate));
  }, [showPurchasedPicker, purchasedPickerField, purchasedRange.start, purchasedRange.end]);

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

  const handleSyncShops = useCallback(async () => {
    if (!selectedShop) {
      setNodeStatus("Select a shop to sync first");
      return;
    }
    setSyncLoading(true);
    setNodeDisplayMode("sync");
    setNodeStatus("Syncing POS data…");
    setSyncResult(null);
    try {
      const res = await fetch(`${API_BASE}/sync_pos_shops`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_ids: [selectedShop.shop_id], reset_pos: true }),
      });
      const data = await res.json();
      if (!res.ok || (data && typeof data === "object" && "error" in data && data.error)) {
        const msg =
          (data && typeof data === "object" && "error" in data && typeof data.error === "string"
            ? data.error
            : `HTTP ${res.status}`);
        setNodeStatus(msg);
        setSyncResult({ at: Date.now(), body: data });
        return;
      }
      setNodeStatus("Sync complete");
      setSyncResult({ at: Date.now(), body: data });
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Sync failed";
      setNodeStatus(msg);
      setSyncResult({ at: Date.now(), body: { error: msg } });
    } finally {
      setSyncLoading(false);
    }
  }, [selectedShop]);

  const handleSummaryRequest = useCallback(async () => {
    if (!selectedShop) {
      setSummaryError("Select a shop first");
      setNodeDisplayMode("summary");
      return;
    }
    if (summaryRange.start && summaryRange.end && new Date(summaryRange.start) > new Date(summaryRange.end)) {
      setSummaryError("Start time must be before end time");
      setNodeDisplayMode("summary");
      return;
    }
    setSummaryLoading(true);
    setSummaryError(null);
    setNodeDisplayMode("summary");
    try {
      const res = await fetch(`${API_BASE}/shop_summary`, {
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

  useEffect(() => {
    if (hasAutoSummaryRun) return;
    if (activeView !== "nodes") return;
    if (!selectedShop) return;
    setHasAutoSummaryRun(true);
    void handleSummaryRequest();
  }, [activeView, selectedShop, hasAutoSummaryRun, handleSummaryRequest]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const trimmed = promptText.trim();
    if (!trimmed) return;

    setIsLoading(true);
    setStatusMsg("Running SQL query…");

    const id = createId();
    const title = trimmed.length > 40 ? trimmed.slice(0, 37).trimEnd() + "…" : trimmed;

    let apiData: ApiResponse | null = null;

    try {
      const res = await fetch(`${API_BASE}/openai_sql`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ input_text: trimmed.startsWith("!") ? trimmed : `!${trimmed}` }),
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
        source: "text",
        model: "client",
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
      source: "text",
      model: apiData?.model ?? "client",
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    setSqlItem(item);
    setPromptText("");
  }

  const loadDbInfo = useCallback(async () => {
    setDbInfoLoading(true);
    setDbInfoError(null);
    try {
      const res = await fetch(`${API_BASE}/db_schema_overview`);
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
  }, []);

  const handlePurchasedGenerate = useCallback(async () => {
    if (!purchasedRange.start || !purchasedRange.end) {
      setPurchasedError("Start and end are required.");
      return;
    }
    if (new Date(purchasedRange.start) > new Date(purchasedRange.end)) {
      setPurchasedError("Start time must be before end time.");
      return;
    }
    setPurchasedLoading(true);
    setPurchasedError(null);
    try {
      const res = await fetch(`${API_BASE}/purchased_summary`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          start_time: purchasedRange.start,
          end_time: purchasedRange.end,
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
  }, [purchasedRange.end, purchasedRange.start]);

  useEffect(() => {
    if (dataMode !== "sql" || !showDbInfo || dbInfo || dbInfoLoading) return;
    void loadDbInfo();
  }, [dataMode, showDbInfo, dbInfo, dbInfoLoading, loadDbInfo]);

  const loadScan = useCallback(
    async (direction: "next" | "prev" | "current" = "next", opts?: { range?: { start: string; end: string }; reset?: boolean }) => {
      const range = opts?.range || scanRange;
      if (!range.start || !range.end) {
        setScanError("Choose a time range and tap LOAD first.");
        setScanStatus(null);
        return;
      }

      if (opts?.reset) {
        setScanRecord(null);
        setScanImage("");
        setScanText("");
        setScanDirty(false);
      }

      const currentId = direction === "next" || direction === "prev" ? (scanRecord?.id ?? 0) : (scanRecord?.id ?? 0);
      setScanLoading(true);
      setScanError(null);
      const rangeLabel = `${range.start.replace("T", " ")} → ${range.end.replace("T", " ")}`;
      setScanStatus(direction === "current" ? `Refreshing (${rangeLabel})…` : `Loading scan (${rangeLabel})…`);
      try {
        const res = await fetch(`${API_BASE}/scan_nav`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ current_id: currentId, direction, start_time: range.start, end_time: range.end }),
        });
        const data = await res.json();
        if (!res.ok || !data.scan) {
          const msg =
            (data && typeof data === "object" && data.error) ||
            `HTTP ${res.status}`;
          setScanError(String(msg));
          setScanStatus(null);
          return;
        }

        const scan = data.scan as OcrScan;
        const text =
          typeof scan.extracted_text === "string"
            ? scan.extracted_text
            : JSON.stringify(scan.extracted_text ?? "", null, 2);

        setScanRecord(scan);
        setScanText(text);
        setScanDirty(false);
        setScanZoom(1);
        setScanImage(data.image_base64 ? `data:image/*;base64,${data.image_base64}` : "");
        const imageNote =
          data && typeof data.error === "string" && data.error ? data.error : null;
        setScanStatus(imageNote || `Viewing scan #${scan.id}`);
        setScanError(null);
        setScanRange(range);
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load scan";
        setScanError(msg);
        setScanStatus(null);
      } finally {
        setScanLoading(false);
      }
    },
    [scanRecord, scanRange]
  );

  const applyScanRange = useCallback(() => {
    if (!scanRangeDraft.start || !scanRangeDraft.end) {
      setScanError("Select start and end time.");
      return;
    }
    if (new Date(scanRangeDraft.start) > new Date(scanRangeDraft.end)) {
      setScanError("Start time must be before end time.");
      return;
    }
    setScanError(null);
    setShowScanPicker(false);
    setScanRange(scanRangeDraft);
    loadScan("next", { range: scanRangeDraft, reset: true });
  }, [loadScan, scanRangeDraft]);

  const handleScanUpdate = useCallback(async () => {
    if (!scanRecord || !scanDirty) return;
    setScanLoading(true);
    setScanError(null);
    setScanStatus("Updating…");
    try {
      const res = await fetch(`${API_BASE}/scan_update`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id: scanRecord.id, extracted_text: scanText }),
      });
      const data = await res.json();
      if (!res.ok || (data && typeof data === "object" && data.error)) {
        const msg =
          (data && typeof data === "object" && data.error) ||
          `HTTP ${res.status}`;
        setScanError(String(msg));
        setScanStatus(null);
        return;
      }
      setScanRecord((prev) => (prev ? { ...prev, extracted_text: scanText } : prev));
      setScanDirty(false);
      setScanStatus("Saved");
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to update scan";
      setScanError(msg);
      setScanStatus(null);
    } finally {
      setScanLoading(false);
    }
  }, [scanRecord, scanDirty, scanText]);

  const handleScanDelete = useCallback(async () => {
    if (!scanRecord) return;
    const confirmMsg = scanDirty
      ? `Delete scan #${scanRecord.id}? Unsaved text edits will be lost.`
      : `Delete scan #${scanRecord.id}? This removes it from OCR_SCANS and deletes the image file.`;
    if (!window.confirm(confirmMsg)) return;

    const deletedId = scanRecord.id;
    const nextDirection: "next" | "prev" | null = scanHasNext ? "next" : scanHasPrev ? "prev" : null;
    setScanLoading(true);
    setScanError(null);
    setScanStatus(`Deleting scan #${deletedId}…`);
    try {
      const res = await fetch(`${API_BASE}/scan_delete`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id: deletedId }),
      });
      const data = await res.json();
      if (!res.ok || (data && typeof data === "object" && data.error)) {
        const msg =
          (data && typeof data === "object" && data.error) ||
          `HTTP ${res.status}`;
        setScanError(String(msg));
        setScanStatus(null);
        return;
      }

      const fileStatus = data && typeof data.file_status === "string" ? data.file_status : "";
      if (nextDirection) {
        await loadScan(nextDirection, { range: scanRange, reset: true });
        if (fileStatus) {
          setScanStatus((prev) => (prev ? `${prev} · ${fileStatus}` : fileStatus));
        }
      } else {
        setScanRecord(null);
        setScanImage("");
        setScanText("");
        setScanDirty(false);
        setScanZoom(1);
        setScanStatus(fileStatus ? `Deleted scan #${deletedId}. ${fileStatus}` : `Deleted scan #${deletedId}.`);
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to delete scan";
      setScanError(msg);
      setScanStatus(null);
    } finally {
      setScanLoading(false);
    }
  }, [scanRecord, scanDirty, scanHasNext, scanHasPrev, loadScan, scanRange]);

  const handleIngest = useCallback(async () => {
    if (!scanRange.start) {
      setScanError("Select a start time (LOAD) before ingesting.");
      return;
    }
    setIngestLoading(true);
    setScanError(null);
    setScanStatus("Ingesting OCR scans…");
    try {
      const res = await fetch(`${API_BASE}/ingest_from_ocr`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          since: scanRange.start,
          end_time: scanRange.end,
          scan_type: scanRecord?.scan_type ?? "",
          product_type: "ingredient",
        }),
      });
      const data = await res.json();
      if (!res.ok || (data && typeof data === "object" && data.error)) {
        const msg = (data && typeof data === "object" && data.error) || `HTTP ${res.status}`;
        setScanError(String(msg));
        setScanStatus(null);
        return;
      }
      const processed = Number(data.processed ?? 0);
      const total = Number(data.total ?? 0);
      const failed = Number(data.failed ?? 0);
      const skipped = Number(data.skipped ?? 0);
      const parts = [`Ingested ${processed}/${total} scans`];
      if (skipped) parts.push(`${skipped} skipped`);
      if (failed) parts.push(`${failed} failed`);
      setScanStatus(parts.join(" · ") + ".");
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to ingest scans";
      setScanError(msg);
      setScanStatus(null);
    } finally {
      setIngestLoading(false);
    }
  }, [scanRange, scanRecord]);

  const handleRunOcr = useCallback(async () => {
    const dir = (ocrDir || "").trim() || DEFAULT_OCR_DIR;
    setOcrLoading(true);
    setScanError(null);
    setScanStatus(`Running OCR for ${dir}…`);
    try {
      let data: { error?: string; dir?: string; message?: string } | null = null;
      const res = await fetch(`${API_BASE}/gpt_ocr_pdfs`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ dir }),
      });
      try {
        data = (await res.json()) as { error?: string; dir?: string; message?: string };
      } catch {
        data = null;
      }

      if (!res.ok || (data && data.error)) {
        const msg = data?.error ?? `HTTP ${res.status}`;
        setScanError(msg);
        setScanStatus(null);
        return;
      }

      const dirUsed = data && typeof data.dir === "string" && data.dir ? data.dir : dir;
      const note = data && typeof data.message === "string" && data.message ? data.message : "OCR complete";
      setScanStatus(`${note} (${dirUsed})`);
      setShowOcrDialog(false);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to run OCR";
      setScanError(msg);
      setScanStatus(null);
    } finally {
      setOcrLoading(false);
    }
  }, [ocrDir]);

  // Do not auto-load scan data; user triggers LOAD with a time range

  return (
    <div className={`app-shell view-${activeView}`} style={{ minHeight: "100vh" }}>
      <ActivityRail activeView={activeView} onChange={setActiveView} />

      {activeView === "nodes" ? (
        <>
          <button
            type="button"
            className="mobile-sidebar-toggle"
            onClick={() => setMobileNodesAsideOpen((prev) => !prev)}
            aria-expanded={mobileNodesAsideOpen}
          >
            {mobileNodesAsideOpen ? "Hide Nodes Panel" : "Show Nodes Panel"}
          </button>
          <aside className={"sidebar nodes-sidebar" + (mobileNodesAsideOpen ? " mobile-open" : " mobile-collapsed")}>
            <header className="sidebar-header">
              <div className="app-logo">🔥</div>
              <div className="app-title">
                <div className="app-name">flamestalker</div>
                <div className="app-subtitle">ERP visual console</div>
              </div>
            </header>

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
                    {shop.dbname ? ` · ${shop.dbname}` : ""}
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
          </aside>

          <main className="main nodes-main">
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
                Manage POS databases and sync into flametrack. Data comes from shop_databases.json.
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
                          status={nodeStatus}
                          syncResult={syncResult}
                          displayMode={nodeDisplayMode}
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
                    <button
                      type="button"
                      className="mobile-command-toggle"
                      onClick={() => setMobileNodesCommandsOpen((prev) => !prev)}
                      aria-expanded={mobileNodesCommandsOpen}
                    >
                      {mobileNodesCommandsOpen ? "Hide Commands" : "Show Commands"}
                    </button>
                    <div className={"mobile-command-panel" + (mobileNodesCommandsOpen ? " is-open" : "")}>
                      <div className="command-bar">
                        <div>
                          <div className="command-title">Commands</div>
                          <div className="command-subtitle">FloreantPOS → flametrack</div>
                        </div>
                        <div className="command-actions" style={{ position: "relative" }}>
                          <div className="command-actions">
                            <button
                              type="button"
                              className="secondary-button"
                              onClick={() => setShowSummaryPicker((prev) => !prev)}
                              disabled={!selectedShop || summaryLoading}
                            >
                              {summaryLoading ? "Loading…" : "SUMMARY"}
                            </button>
                            {showSummaryPicker && (
                              <SummaryRangePicker
                                range={summaryRange}
                                activeField={summaryPickerField}
                                calendarMonth={summaryCalendarMonth}
                                onActiveFieldChange={setSummaryPickerField}
                                onCalendarMonthChange={setSummaryCalendarMonth}
                                onChangeField={(field, nextDate) =>
                                  setSummaryRange((prev) => ({ ...prev, [field]: formatLocalDateTime(nextDate) }))
                                }
                                onCancel={() => setShowSummaryPicker(false)}
                                onApply={handleSummaryRequest}
                                disabled={summaryLoading}
                              />
                            )}
                          </div>
                          <button
                            type="button"
                            className="submit-button"
                            onClick={handleSyncShops}
                            disabled={!selectedShop || syncLoading}
                          >
                            {syncLoading ? "Syncing…" : "SYNC"}
                          </button>
                        </div>
                      </div>
                      <div className="input-hint">
                        {nodeStatus
                          ? nodeStatus
                          : selectedShop
                          ? `Target: ${selectedShop.name} (shop_id ${selectedShop.shop_id})`
                          : "Load a shop to sync data"}
                      </div>
                    </div>
                  </section>
                </div>
              </div>
            </div>
          </main>
        </>
      ) : activeView === "data" ? (
        <>
          <aside className="sidebar">
            <header className="sidebar-header">
              <div className="app-logo">🔥</div>
              <div className="app-title">
                <div className="app-name">flamestalker</div>
                <div className="app-subtitle">ERP visual console</div>
              </div>
            </header>

            <div className="sidebar-section-label">Data menu</div>
            <nav className="nodes-list" aria-label="Data view menu">
              <button
                type="button"
                className={"node-item" + (dataMode === "sql" ? " node-item--active" : "")}
                onClick={() => setDataMode("sql")}
              >
                <div className="node-item-title">SQL query</div>
                <div className="node-item-meta">Run SQL on flametrack</div>
              </button>
              <button
                type="button"
                className={"node-item" + (dataMode === "purchased" ? " node-item--active" : "")}
                onClick={() => setDataMode("purchased")}
              >
                <div className="node-item-title">Purchase summary</div>
                <div className="node-item-meta">Summary from flametrack purchase data</div>
              </button>
            </nav>

            <footer className="sidebar-footer">
              <span className="sidebar-footer-label">Connected to</span>
              <span className="sidebar-footer-value">flametrack only</span>
            </footer>
          </aside>

          <main className="main">
            <header className="main-header">
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
                <div className="main-header-title">
                  <span className="pill">Display</span>
                  <h1>Data view</h1>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  {dataMode === "sql" && (
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => {
                        setShowDbInfo((prev) => !prev);
                        if (!showDbInfo && !dbInfo && !dbInfoLoading) void loadDbInfo();
                      }}
                    >
                      {showDbInfo ? "Hide DB Info" : "Show DB Info"}
                    </button>
                  )}
                  <button
                    type="button"
                    className="secondary-button"
                    onClick={() => setShowImagePanel((prev) => !prev)}
                    style={{ minWidth: 130 }}
                  >
                    {showImagePanel ? "Hide OCR window" : "Show OCR window"}
                  </button>
                </div>
              </div>
              <div className="main-header-subtitle">
                {dataMode === "purchased"
                  ? "Purchase summary from flametrack in selected time span."
                  : "Type SQL then click OK to run directly in flametrack."}
              </div>
            </header>

            <div className="main-body">
              <div
                className="content-grid"
                style={{
                  display: "grid",
                  gridTemplateColumns: showImagePanel ? "minmax(0, 1fr) minmax(360px, 45vw)" : "1fr",
                  gap: "16px",
                }}
              >
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
                  <section className="main-display">
                    {dataMode === "purchased" ? (
                      <PurchasedSummaryPanel
                        summary={purchasedSummary}
                        error={purchasedError}
                        loading={purchasedLoading}
                        onStatus={setStatusMsg}
                        onPreviewImage={handlePreviewImage}
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
                          <DisplayPanel item={activeItem} onStatus={setStatusMsg} onPreviewImage={handlePreviewImage} />
                        ) : (
                          <div className="placeholder">SQL query mode executes the command directly on flametrack.</div>
                        )}
                      </div>
                    )}
                  </section>

                  <section className="input-shell">
                    {dataMode === "purchased" ? (
                      <div className="command-bar">
                        <div>
                          <div className="command-title">Purchase summary</div>
                          <div className="command-subtitle">
                            {(purchasedRange.start || "start").replace("T", " ")} → {(purchasedRange.end || "end").replace("T", " ")}
                          </div>
                        </div>
                        <div className="command-actions" style={{ position: "relative" }}>
                          <button
                            type="button"
                            className="secondary-button"
                            onClick={() => setShowPurchasedPicker((prev) => !prev)}
                            disabled={purchasedLoading}
                          >
                            Time span
                          </button>
                          {showPurchasedPicker && (
                            <SummaryRangePicker
                              range={purchasedRange}
                              activeField={purchasedPickerField}
                              calendarMonth={purchasedCalendarMonth}
                              onActiveFieldChange={setPurchasedPickerField}
                              onCalendarMonthChange={setPurchasedCalendarMonth}
                              onChangeField={(field, nextDate) =>
                                setPurchasedRange((prev) => ({ ...prev, [field]: formatLocalDateTime(nextDate) }))
                              }
                              onCancel={() => setShowPurchasedPicker(false)}
                              onApply={handlePurchasedGenerate}
                              disabled={purchasedLoading}
                            />
                          )}
                          <button
                            type="button"
                            className="submit-button"
                            onClick={handlePurchasedGenerate}
                            disabled={purchasedLoading}
                          >
                            {purchasedLoading ? "Generating…" : "Generate"}
                          </button>
                        </div>
                      </div>
                    ) : (
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
                          <span>SQL query mode executes the command directly on flametrack.</span>
                          {statusMsg && <span className="status-msg">{statusMsg}</span>}
                        </div>
                      </form>
                    )}
                  </section>
                </div>

                {showImagePanel && (
                  <aside
                    className="image-panel"
                    style={{
                      background: "rgba(10,14,20,0.6)",
                      borderRadius: 12,
                      boxShadow: "0 8px 30px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.04)",
                      padding: 12,
                      display: "flex",
                      flexDirection: "column",
                      overflow: "hidden",
                      minWidth: 0
                    }}
                  >
                    <div
                      className="image-panel-header"
                      style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 8, gap: 8 }}
                    >
                      <div style={{ color: "#cfd8e3", fontWeight: 600 }}>OCR Image</div>
                      <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
                        <button
                          type="button"
                          className="secondary-button"
                          onClick={() => setImageZoom((z) => Math.min(3, parseFloat((z + 0.1).toFixed(2))))}
                          disabled={!imagePreview}
                        >
                          +
                        </button>
                        <button
                          type="button"
                          className="secondary-button"
                          onClick={() => setImageZoom((z) => Math.max(0.3, parseFloat((z - 0.1).toFixed(2))))}
                          disabled={!imagePreview}
                        >
                          −
                        </button>
                        <button
                          type="button"
                          className="secondary-button"
                          onClick={() => setImageZoom(1)}
                          disabled={!imagePreview}
                        >
                          100%
                        </button>
                        <button
                          type="button"
                          className="secondary-button"
                          onClick={() => {
                            setImagePreview(null);
                            setShowImagePanel(false);
                            setImageZoom(1);
                          }}
                          disabled={!imagePreview}
                        >
                          Close
                        </button>
                      </div>
                    </div>
                    {imagePreview ? (
                      <div
                        className="image-frame"
                        style={{
                          background: "rgba(0,0,0,0.35)",
                          borderRadius: 10,
                          padding: 8,
                          overflow: "auto",
                          flex: 1,
                          display: "flex",
                          flexDirection: "column",
                          gap: 6,
                          minHeight: 0
                        }}
                      >
                        <div style={{ color: "#9fb3c8", fontSize: 12, wordBreak: "break-all" }}>{imagePreview.path}</div>
                        <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
                          <img
                            src={imagePreview.src}
                            alt="OCR"
                            style={{
                              maxWidth: "100%",
                              maxHeight: "calc(100vh - 260px)",
                              objectFit: "contain",
                              borderRadius: 8,
                              boxShadow: "0 6px 22px rgba(0,0,0,0.35)",
                              transform: `scale(${imageZoom})`,
                              transformOrigin: "center center",
                              transition: "transform 120ms ease"
                            }}
                          />
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
                          background: "rgba(0,0,0,0.2)"
                        }}
                      >
                        Click an ocr_id to preview the scan.
                      </div>
                    )}
                  </aside>
                )}
              </div>
            </div>
          </main>
        </>
      ) : (
        <>
          <aside className="sidebar scan-sidebar">
            <header className="sidebar-header">
              <div className="app-logo">🔥</div>
              <div className="app-title">
                <div className="app-name">flamestalker</div>
                <div className="app-subtitle">OCR correction</div>
              </div>
            </header>
            <div className="sidebar-section-label">OCR scans</div>
            <div className="history-empty">Use PREV/NEXT to load receipt scans and fix extracted_text.</div>
            {scanRecord && (
              <div className="scan-meta-card">
                <div className="scan-meta-row">
                  <span className="scan-meta-label">Scan id</span>
                  <span className="scan-meta-value">#{scanRecord.id}</span>
                </div>
                <div className="scan-meta-row">
                  <span className="scan-meta-label">Scanned at</span>
                  <span className="scan-meta-value">{scanRecord.scanned_at || "—"}</span>
                </div>
                <div className="scan-meta-row">
                  <span className="scan-meta-label">Shop</span>
                  <span className="scan-meta-value">{scanRecord.shop_id}</span>
                </div>
                <div className="scan-meta-row">
                  <span className="scan-meta-label">Type</span>
                  <span className="scan-meta-value">{scanRecord.scan_type || "receipt"}</span>
                </div>
              </div>
            )}
            <footer className="sidebar-footer">
              <span className="sidebar-footer-label">Source</span>
              <span className="sidebar-footer-value">table ocr_scans</span>
            </footer>
          </aside>

          <main className="main scan-main">
            <header className="main-header">
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
                <div className="main-header-title">
                  <span className="pill">Review</span>
                  <h1>Scan view</h1>
                </div>
                <div className="header-note">
                  {scanRecord ? `Scan #${scanRecord.id}` : scanRangeLabel ? scanRangeLabel : "Load a time window to review scans"}
                </div>
              </div>
              <div className="main-header-subtitle">
                Check extracted_text against each receipt image, make corrections, then update.
              </div>
            </header>

            <div className="main-body scan-body">
              <div className="scan-grid">
                <div className="scan-pane">
                  <div className="scan-pane-header">
                    <div>
                      <div className="card-title">Extracted text</div>
                      <div className="card-subtitle">Editable · keep the structure the same</div>
                    </div>
                    {scanDirty && <span className="pill pill--muted">Unsaved</span>}
                  </div>
                  <textarea
                    className="scan-textarea"
                    value={scanText}
                    onChange={(e) => {
                      const next = e.target.value;
                      setScanText(next);
                      setScanDirty(!!scanRecord && next !== (scanRecord.extracted_text || ""));
                    }}
                    placeholder="Load a scan to see its extracted_text."
                    disabled={scanLoading || !scanRecord}
                    spellCheck={false}
                    wrap="off"
                  />
                </div>

                <div className="scan-pane">
                  <div className="scan-pane-header">
                    <div>
                      <div className="card-title">Receipt image</div>
                      <div className="card-subtitle">{scanRecord?.image_path || "Waiting for scan"}</div>
                    </div>
                    <div className="scan-zoom-controls">
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => setScanZoom((z) => Math.max(0.4, parseFloat((z - 0.1).toFixed(2))))}
                        disabled={!scanImage}
                        title="Zoom out"
                      >
                        −
                      </button>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => setScanZoom((z) => Math.min(3, parseFloat((z + 0.1).toFixed(2))))}
                        disabled={!scanImage}
                        title="Zoom in"
                      >
                        +
                      </button>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => setScanZoom(1)}
                        disabled={!scanImage || scanZoom === 1}
                        title="Reset zoom"
                      >
                        100%
                      </button>
                    </div>
                  </div>
                  <div className="scan-image-frame">
                    {scanImage ? (
                      <img
                        src={scanImage}
                        alt={scanRecord?.image_path || "OCR receipt"}
                        className="scan-image"
                        style={{ transform: `scale(${scanZoom})` }}
                      />
                    ) : (
                      <div className="placeholder">No image loaded.</div>
                    )}
                  </div>
                </div>
              </div>

              <div className="scan-command-shell">
                <div className="scan-command-bar">
                  <div className="scan-command-info">
                    {scanRecord && (
                      <span className="scan-page-indicator">
                        Page {scanPagePosition || 1} / {scanPageTotal || 1}
                      </span>
                    )}
                    {scanError && <span className="status-msg">{scanError}</span>}
                    {!scanError && scanStatus && <span className="status-msg">{scanStatus}</span>}
                    {!scanError && !scanStatus && scanRecord && (
                      <span className="status-msg">
                        {scanRecord.scanned_at ? `Scanned at ${scanRecord.scanned_at}` : "Loaded from ocr_scans"}
                      </span>
                    )}
                    {!scanError && !scanStatus && !scanRecord && (
                      <span className="status-msg">
                        {scanRangeLabel ? `Range: ${scanRangeLabel}` : "Load scans for a time window to begin."}
                      </span>
                    )}
                  </div>
                  <div className="scan-actions">
                    <div style={{ position: "relative" }}>
                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() => {
                          setScanRangeDraft(scanRange.start && scanRange.end ? scanRange : defaultScanRangeToday());
                          setShowScanPicker((prev) => !prev);
                        }}
                        disabled={scanLoading || ocrLoading}
                      >
                        {scanLoading ? "Loading…" : "LOAD"}
                      </button>
                      {showScanPicker && (
                        <div className="summary-popover" style={{ minWidth: 280, bottom: "calc(100% + 8px)" }}>
                          <div className="summary-popover-title">Choose time range</div>
                          <label className="summary-field">
                            <span>Start</span>
                            <input
                              type="datetime-local"
                              value={scanRangeDraft.start}
                              onChange={(e) => setScanRangeDraft((prev) => ({ ...prev, start: e.target.value }))}
                            />
                          </label>
                          <label className="summary-field">
                            <span>End</span>
                            <input
                              type="datetime-local"
                              value={scanRangeDraft.end}
                              onChange={(e) => setScanRangeDraft((prev) => ({ ...prev, end: e.target.value }))}
                            />
                          </label>
                          <div className="summary-popover-actions">
                            <button
                              type="button"
                              className="secondary-button"
                              onClick={() => setShowScanPicker(false)}
                            >
                              Cancel
                            </button>
                            <button
                              type="button"
                              className="submit-button"
                              onClick={applyScanRange}
                              disabled={scanLoading}
                            >
                              {scanLoading ? "Working…" : "Apply"}
                            </button>
                          </div>
                        </div>
                      )}
                    </div>
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => loadScan("prev")}
                      disabled={
                        scanLoading ||
                        ocrLoading ||
                        !scanRange.start ||
                        !scanRange.end ||
                        !scanRecord ||
                        !scanHasPrev
                      }
                    >
                      PREV
                    </button>
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => loadScan("next")}
                      disabled={
                        scanLoading ||
                        ocrLoading ||
                        !scanRange.start ||
                        !scanRange.end ||
                        !scanRecord ||
                        !scanHasNext
                      }
                    >
                      NEXT
                    </button>
                    <div style={{ position: "relative" }}>
                      <button
                        type="button"
                        className="submit-button"
                        onClick={() => setShowOcrDialog((prev) => !prev)}
                        disabled={scanLoading || ocrLoading}
                      >
                        {ocrLoading ? "OCR…" : "OCR"}
                      </button>
                      {showOcrDialog && (
                        <div className="summary-popover" style={{ minWidth: 360, bottom: "calc(100% + 8px)", right: 0 }}>
                          <div className="summary-popover-title">Run GPT OCR on PDFs</div>
                          <label className="summary-field">
                            <span>Directory</span>
                            <input
                              type="text"
                              value={ocrDir}
                              onChange={(e) => setOcrDir(e.target.value)}
                              placeholder={DEFAULT_OCR_DIR}
                            />
                          </label>
                          <div className="summary-popover-actions">
                            <button
                              type="button"
                              className="secondary-button"
                              onClick={() => setShowOcrDialog(false)}
                              disabled={ocrLoading}
                            >
                              Cancel
                            </button>
                            <button
                              type="button"
                              className="submit-button"
                              onClick={handleRunOcr}
                              disabled={ocrLoading || !ocrDir.trim()}
                            >
                              {ocrLoading ? "Running…" : "Run OCR"}
                            </button>
                          </div>
                        </div>
                      )}
                    </div>
                    <button
                      type="button"
                      className="submit-button"
                      onClick={handleScanUpdate}
                      disabled={isUpdateDisabled}
                    >
                      UPDATE
                    </button>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={handleScanDelete}
                      disabled={scanLoading || ingestLoading || ocrLoading || !scanRecord}
                    >
                      DELETE
                    </button>
                    <button
                      type="button"
                      className="submit-button"
                      onClick={handleIngest}
                      disabled={scanLoading || ingestLoading || ocrLoading || !scanRange.start || !scanRange.end}
                    >
                      {ingestLoading ? "INGESTING…" : "INGEST"}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </main>
        </>
      )}
    </div>
  );

  async function handlePreviewImage(ocrId: number) {
    setShowImagePanel(true); // auto-open the OCR window when an ocr_id is clicked
    setStatusMsg("Loading OCR image…");
    try {
      const res = await fetch(`${API_BASE}/ocr_image`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ocr_id: ocrId }),
      });
      const data = await res.json();
      if (!res.ok || data.error) {
        setStatusMsg(data.error || `HTTP ${res.status}`);
        return;
      }
      const src = `data:image/*;base64,${data.image_base64}`;
      setImagePreview({ src, path: data.image_path });
      setImageZoom(1);
      setStatusMsg(null);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to load image";
      setStatusMsg(msg);
    }
  }
}

function ActivityRail({
  activeView,
  onChange,
}: {
  activeView: ActiveView;
  onChange: (view: ActiveView) => void;
}) {
  const items: Array<{ id: ActiveView; label: string; icon: string }> = [
    { id: "nodes", label: "Nodes view", icon: "🗂" },
    { id: "data", label: "Data view", icon: "📊" },
    { id: "scan", label: "Scan view", icon: "🧾" },
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
            <span className="activity-label">{item.label}</span>
          </button>
        ))}
      </div>
    </nav>
  );
}

function NodesPanel({
  shop,
  status,
  syncResult,
  displayMode,
  summary,
  summaryError,
  summaryLoading,
  summaryRange,
  onSummaryRefresh,
}: {
  shop: ShopInfo;
  status: string | null;
  syncResult: { at: number; body: unknown } | null;
  displayMode: "sync" | "summary";
  summary: ShopSummary | null;
  summaryError: string | null;
  summaryLoading: boolean;
  summaryRange: { start: string; end: string };
  onSummaryRefresh: () => void;
}) {
  const detailItems = [
    { label: "Host", value: shop.host || "—" },
    { label: "Port", value: shop.port || "—" },
    { label: "Database", value: shop.dbname || "—" },
    { label: "User", value: shop.user || "—" },
  ];
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

  const renderSync = () =>
    syncResult ? (
      <>
        <pre className="json-block">{JSON.stringify(syncResult.body, null, 2)}</pre>
        <div className="node-footnote">Updated {new Date(syncResult.at).toLocaleTimeString()}</div>
      </>
    ) : (
      <div className="placeholder">Run SYNC to ingest POS data.</div>
    );

  const renderSummary = () => (
    <div className="summary-body">
      {summaryError && <div className="status-msg">{summaryError}</div>}
      {summaryLoading && <div className="placeholder">Loading summary…</div>}
      {!summaryLoading && hasSummary && totals && (
        <>
          <div className="summary-metrics">
            <SummaryStat
              label="Total revenue"
              value={`${formatCurrency(totals.revenue_cents)} (${formatCurrency(closingDateRevenueCents)})`}
              hint={`${totals.orders} orders`}
            />
            <SummaryStat label="Average order value" value={formatCurrency(totals.aov)} hint="AOV" />
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
    </div>
  );

  return (
    <div className="nodes-panel">
      <div className="card">
        <div className="card-header">
          <div className="card-title">{shop.name || `Shop ${shop.shop_id}`}</div>
          <div className="card-subtitle">Shop id {shop.shop_id}</div>
        </div>
        <div className="card-body node-grid">
          <div className="node-grid-row">
            {detailItems.map((item) => (
              <div key={item.label} className="node-chip">
                <div className="node-chip-label">{item.label}</div>
                <div className="node-chip-value">{item.value}</div>
              </div>
            ))}
          </div>
          {shop.conninfo && (
            <div className="node-conninfo">
              <div className="display-query-label">Conninfo</div>
              <code className="sql-block">{shop.conninfo}</code>
            </div>
          )}
        </div>
      </div>
      <div className="card">
        <div
          className="card-header"
          style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 10 }}
        >
          <div>
            <div className="card-title">{displayMode === "sync" ? "Latest command" : "Shop summary"}</div>
            <div className="card-subtitle">
              {displayMode === "sync"
                ? status || "Ready"
                : `${shop.name || `Shop ${shop.shop_id}`} · ${rangeLabel}`}
            </div>
          </div>
          {displayMode === "summary" && (
            <button
              type="button"
              className="secondary-button"
              onClick={onSummaryRefresh}
              disabled={summaryLoading}
              style={{ minWidth: 110 }}
            >
              {summaryLoading ? "Loading…" : "Refresh"}
            </button>
          )}
        </div>
        <div className={"card-body" + (displayMode === "summary" ? " summary-body" : "")}>
          {displayMode === "sync" ? renderSync() : renderSummary()}
        </div>
      </div>
    </div>
  );
}

function SummaryStat({ label, value, hint }: { label: string; value: string; hint?: string }) {
  return (
    <div className="summary-stat">
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
  activeField,
  calendarMonth,
  onActiveFieldChange,
  onCalendarMonthChange,
  onChangeField,
  onCancel,
  onApply,
  disabled,
}: {
  range: { start: string; end: string };
  activeField: "start" | "end";
  calendarMonth: Date;
  onActiveFieldChange: (field: "start" | "end") => void;
  onCalendarMonthChange: (next: Date) => void;
  onChangeField: (field: "start" | "end", next: Date) => void;
  onCancel: () => void;
  onApply: () => void;
  disabled: boolean;
}) {
  const activeValue = activeField === "start" ? range.start : range.end;
  const activeDate = parseLocalDateTime(activeValue) || new Date();
  const grid = buildCalendarGrid(calendarMonth);
  const monthLabel = `${MONTH_LABELS[calendarMonth.getMonth()]} ${calendarMonth.getFullYear()}`;

  const setActiveDate = (next: Date) => {
    onChangeField(activeField, next);
  };

  const handleDaySelect = (day: Date) => {
    const next = new Date(activeDate);
    next.setFullYear(day.getFullYear(), day.getMonth(), day.getDate());
    setActiveDate(next);
  };

  const handleHourSelect = (hour: number) => {
    const next = new Date(activeDate);
    next.setHours(hour);
    setActiveDate(next);
  };

  const handleMinuteSelect = (minute: number) => {
    const next = new Date(activeDate);
    next.setMinutes(minute);
    setActiveDate(next);
  };

  return (
    <div className="summary-popover summary-popover--range">
      <div className="summary-popover-title">Choose time range</div>
      <div className="time-range-tabs">
        <button
          type="button"
          className={"time-range-tab" + (activeField === "start" ? " time-range-tab--active" : "")}
          onClick={() => onActiveFieldChange("start")}
        >
          <span className="time-range-tab-label">Start</span>
          <span className="time-range-tab-value">{formatPickerLabel(range.start)}</span>
        </button>
        <button
          type="button"
          className={"time-range-tab" + (activeField === "end" ? " time-range-tab--active" : "")}
          onClick={() => onActiveFieldChange("end")}
        >
          <span className="time-range-tab-label">End</span>
          <span className="time-range-tab-value">{formatPickerLabel(range.end)}</span>
        </button>
      </div>

      <div className="time-range-panel">
        <div className="calendar">
          <div className="calendar-header">
            <button
              type="button"
              className="calendar-nav"
              onClick={() => onCalendarMonthChange(addMonths(calendarMonth, -1))}
            >
              Prev
            </button>
            <div className="calendar-title">{monthLabel}</div>
            <button
              type="button"
              className="calendar-nav"
              onClick={() => onCalendarMonthChange(addMonths(calendarMonth, 1))}
            >
              Next
            </button>
          </div>
          <div className="calendar-weekdays">
            {WEEKDAY_LABELS.map((label) => (
              <div key={label} className="calendar-weekday">
                {label}
              </div>
            ))}
          </div>
          <div className="calendar-grid">
            {grid.map((cell) => {
              const isSelected =
                cell.date.getFullYear() === activeDate.getFullYear() &&
                cell.date.getMonth() === activeDate.getMonth() &&
                cell.date.getDate() === activeDate.getDate();
              return (
                <button
                  key={`${cell.date.getFullYear()}-${cell.date.getMonth()}-${cell.date.getDate()}`}
                  type="button"
                  className={
                    "calendar-day" +
                    (cell.inMonth ? "" : " calendar-day--muted") +
                    (cell.isToday ? " calendar-day--today" : "") +
                    (isSelected ? " calendar-day--selected" : "")
                  }
                  onClick={() => handleDaySelect(cell.date)}
                >
                  {cell.date.getDate()}
                </button>
              );
            })}
          </div>
        </div>

        <div className="time-grid">
          <div className="time-grid-section">
            <div className="time-grid-title">Hour</div>
            <div className="time-grid-buttons time-grid-buttons--hours">
              {Array.from({ length: 24 }, (_, idx) => idx).map((hour) => (
                <button
                  key={hour}
                  type="button"
                  className={
                    "time-chip" + (activeDate.getHours() === hour ? " time-chip--active" : "")
                  }
                  onClick={() => handleHourSelect(hour)}
                >
                  {pad2(hour)}
                </button>
              ))}
            </div>
          </div>
          <div className="time-grid-section">
            <div className="time-grid-title">Minute</div>
            <div className="time-grid-buttons time-grid-buttons--minutes">
              {TIME_MINUTES.map((minute) => (
                <button
                  key={minute}
                  type="button"
                  className={
                    "time-chip" + (activeDate.getMinutes() === minute ? " time-chip--active" : "")
                  }
                  onClick={() => handleMinuteSelect(minute)}
                >
                  {pad2(minute)}
                </button>
              ))}
            </div>
          </div>
        </div>
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
    <div className="card">
      <div className="card-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 8 }}>
        <div>
          <div className="card-title">Database info</div>
          <div className="card-subtitle">
            {(dbInfo?.database || "flametrack") + " / " + (dbInfo?.schema || "public")}
          </div>
        </div>
        <button type="button" className="secondary-button" onClick={onRefresh} disabled={loading}>
          {loading ? "Loading..." : "Refresh"}
        </button>
      </div>
      <div className="card-body">
        {error && <div className="status-msg">{error}</div>}
        {!error && loading && <div className="placeholder" style={{ minHeight: 120 }}>Loading database schema...</div>}
        {!error && !loading && dbInfo && (
          <div className="summary-lists">
            {dbInfo.tables.map((table) => (
              <div className="summary-list" key={table.table}>
                <div className="summary-list-title">
                  {table.table} ({table.row_count} rows)
                </div>
                <ul>
                  {table.columns.map((col) => (
                    <li key={table.table + "." + col.name} className="summary-list-item">
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
  summary,
  error,
  loading,
  onStatus,
  onPreviewImage,
}: {
  summary: PurchasedSummary | null;
  error: string | null;
  loading: boolean;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number) => void;
}) {
  if (loading) return <div className="placeholder">Generating purchase summary...</div>;
  if (error) return <ErrorCard message={error} />;
  if (!summary) return <div className="placeholder">Click Generate to view purchase summary.</div>;

  const selectedRows = (summary.selected_items || []).map((item) => {
    const { purchase_id: _purchaseId, ...rest } = item;
    return rest;
  });

  return (
    <div className="display-panel">
      <div className="card">
        <div className="card-header">
          <div className="card-title">Purchase summary</div>
          <div className="card-subtitle">
            {summary.time_range?.start || "start"} → {summary.time_range?.end || "end"}
          </div>
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

      <div className="card">
        <div className="card-body">
          {selectedRows.length ? (
            <ResultTable
              rows={selectedRows}
              sql={buildPurchasedItemsSql(summary.time_range)}
              onStatus={onStatus}
              onPreviewImage={onPreviewImage}
              lockedColumns={["invoice_id", "purchase_date", "supplier", "product"]}
            />
          ) : (
            <div className="placeholder">No items in selected range.</div>
          )}
        </div>
      </div>
    </div>
  );
}

function DisplayPanel({
  item,
  onStatus,
  onPreviewImage,
}: {
  item: HistoryItem;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number) => void;
}) {
  const hasRows = item.result?.rows && item.result.rows.length > 0;
  return (
    <div className="display-panel">
      <header className="display-header">
        <div className="display-title">{item.title}</div>
        <div className="display-meta">
          Source: <strong>{item.source}</strong> · Model:{" "}
          <strong>{item.model}</strong>
        </div>
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
          <ResultTable rows={item.result?.rows ?? []} sql={item.sql} onStatus={onStatus} onPreviewImage={onPreviewImage} />
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

function ResultTable({
  rows,
  sql,
  onStatus,
  onPreviewImage,
  lockedColumns,
}: {
  rows: Array<Record<string, unknown>>;
  sql?: string;
  onStatus?: (msg: string | null) => void;
  onPreviewImage?: (ocrId: number) => void;
  lockedColumns?: string[];
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
  const saveDisabled = !editMode || saving || !hasChanges;
  const lockedCols = useMemo(() => new Set(["ocr_id", ...(lockedColumns || [])]), [lockedColumns]);

  const cancelEdits = useCallback(() => {
    setEditMode(false);
    setEditingCell(null);
    setDraftRows(rows);
    setChanges({});
    onStatus?.(null);
  }, [rows, onStatus]);

  const handleSaveChanges = useCallback(async () => {
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
      const res = await fetch(`${API_BASE}/table_update`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
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
            const res2 = await fetch(`${API_BASE}/openai_sql`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ input_text: "!" + sql }),
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
  }, [changes, draftRows, keyColumn, onStatus, rows, sql, tableName]);

  useEffect(() => {
    setDraftRows(rows);
    setChanges({});
    setTableName(inferTableFromSql(sql ?? "") || tableName);
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
    const tableEl = tableWrapperRef.current;
    if (!tableEl) return;
    const inputShell = document.querySelector(".input-shell");
    const inputTop = inputShell instanceof HTMLElement ? inputShell.getBoundingClientRect().top : window.innerHeight;
    const tableTop = tableEl.getBoundingClientRect().top;
    const available = inputTop - tableTop - 36; // leave breathing room above the input bar + status text
    setTableMaxHeight(available > 0 ? available : null);
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

  if (!rows.length) {
    return <div className="placeholder">No results.</div>;
  }

  const columns = Object.keys(rows[0]);
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
        const res = await fetch(`${API_BASE}/table_delete`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
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
              const res2 = await fetch(`${API_BASE}/openai_sql`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ input_text: "!" + sql }),
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
    [draftRows, keyColumn, onStatus, rows, sql, tableName]
  );

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
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
          <button
            type="button"
            className="secondary-button"
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
      </div>
      <div className="card-body card-body--results">
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
                          if (col === "ocr_id") {
                            if (onPreviewImage) {
                              const val = row[col];
                              const num = typeof val === "number" ? val : Number(val);
                              if (!Number.isNaN(num)) onPreviewImage(num);
                            }
                            return;
                          }
                        }}
                        onDoubleClick={() => {
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
  const match = /from\s+([a-zA-Z0-9_]+)/i.exec(sql);
  if (match && match[1]) return match[1];
  return null;
}
