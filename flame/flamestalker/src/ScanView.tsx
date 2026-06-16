import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { ChangeEvent, PointerEvent as ReactPointerEvent, WheelEvent as ReactWheelEvent } from "react";
import { apiFetch } from "./api";

interface ScanViewProps {
  selectedShopId: number | null;
  selectedShopName?: string;
  shops?: ShopOption[];
  onSelectShop?: (shopId: number) => void;
}

interface ShopOption {
  shop_id: number;
  name?: string;
  pos?: {
    dbname?: string;
  };
  expense?: {
    dbname?: string;
  };
}

interface ReceiptJob {
  id: number;
  status: string;
  error: string;
  started_at: string;
  finished_at: string;
  stage: string;
  processed_pages: number;
  total_pages: number;
  current_page: number;
  draft_receipts: number;
  current_image_path: string;
  payload?: unknown;
}

interface ReceiptQueueItem {
  id: number;
  shop_id: number;
  source_file_name: string;
  receipt_code_prefix: string;
  mime_type: string;
  image_path: string;
  source_path: string;
  ocr_status: string;
  review_status: string;
  page_count: number;
  scanned_at: string;
  updated_at: string;
  draft_count: number;
  draft_total_cost: number;
  draft_status: string;
  first_page_id: number;
  job?: ReceiptJob | null;
}

interface ReceiptPage {
  id: number;
  page_no: number;
  image_path: string;
  width: number;
  height: number;
  created_at: string;
}

interface HistoricalProductMatch {
  id: number;
  name: string;
  category: string;
  supplier_name?: string;
  supplier_tin?: string;
  score?: number;
}

interface ReceiptDraftItem {
  id?: number;
  line_no: number;
  name: string;
  category: string;
  quantity: number;
  unit_price: number;
  line_discount_percent?: number;
  line_discount_amount?: number;
  line_subtotal_amount?: number;
  line_tax_amount?: number;
  total_price: number;
  match_product_id?: number | null;
  historical_matches?: HistoricalProductMatch[];
  validation_errors?: unknown;
  validation_warnings?: unknown;
}

interface ReceiptDraft {
  id?: number;
  receipt_index: number;
  receipt_code?: string;
  supplier: {
    name: string;
    tin: string;
    site: string;
    contact_info: string;
  };
  purchase_order: {
    invoice_id: string;
    purchase_date: string;
    total_cost: number;
    subtotal_amount: number;
    tax_amount: number;
    discount_amount: number;
    rounding_amount: number;
    grand_total: number;
    line_total_basis: string;
  };
  status: string;
  validation_errors?: unknown;
  validation_warnings?: unknown;
  purchase_items: ReceiptDraftItem[];
}

interface ReceiptDetailResponse {
  shop_id: number;
  scan: {
    id: number;
    shop_id: number;
    image_path: string;
    source_path: string;
    source_file_name: string;
    receipt_code_prefix: string;
    mime_type: string;
    file_sha256: string;
    page_count: number;
    scan_type: string;
    ocr_status: string;
    ocr_model?: string;
    extracted_text?: string;
    raw_response?: unknown;
    parsed_json?: unknown;
    ocr_error?: string;
    review_status: string;
    scanned_at: string;
    updated_at: string;
  };
  pages: ReceiptPage[];
  drafts: ReceiptDraft[];
  category_options?: string[];
  review?: {
    review_note?: string;
    reviewed_by?: string;
  } | null;
  job?: ReceiptJob | null;
}

interface ReceiptRunOcrResponse {
  accepted?: boolean;
  already_running?: boolean;
  job_id?: number;
  ocr_status?: string;
  message?: string;
  job?: ReceiptJob | null;
  error?: string;
}

interface ReceiptRunAllJobPayload {
  stage?: string;
  total_receipts?: number;
  processed_receipts?: number;
  current_index?: number;
  current_ocr_id?: number;
  current_receipt_label?: string;
  posted?: number;
  needs_review?: number;
  failed?: number;
  skipped?: number;
  results?: Array<{
    ocr_id?: number;
    label?: string;
    status?: string;
    message?: string;
  }>;
}

interface ReceiptRunAllJob {
  id: number;
  status: string;
  stage?: string;
  error?: string;
  started_at?: string;
  finished_at?: string;
  payload?: ReceiptRunAllJobPayload;
}

interface ReceiptRunAllResponse {
  accepted?: boolean;
  already_running?: boolean;
  job_id?: number;
  total_receipts?: number;
  job?: ReceiptRunAllJob | null;
  message?: string;
  error?: string;
}

interface ReceiptUploadProcessJobPayload extends ReceiptRunAllJobPayload {
  final_result?: string;
  final_message?: string;
  final_ocr_status?: string;
  receipt_ids?: number[];
}

interface ReceiptUploadProcessJob extends ReceiptRunAllJob {
  job_kind?: string;
  ocr_id?: number;
  ocr_status?: string;
  payload?: ReceiptUploadProcessJobPayload;
}

interface ReceiptUploadProcessResponse {
  accepted?: boolean;
  already_running?: boolean;
  duplicate?: boolean;
  uploaded?: boolean;
  ocr_id?: number;
  existing_ocr_id?: number;
  job_id?: number;
  page_id?: number;
  page_count?: number;
  receipt_code_prefix?: string;
  ocr_status?: string;
  file_name?: string;
  job?: ReceiptUploadProcessJob | null;
  message?: string;
  error?: string;
}

interface ReceiptDeleteResponse {
  deleted?: boolean;
  deleted_page?: boolean;
  deleted_receipt?: boolean;
  page_id?: number;
  page_no?: number;
  remaining_pages?: number;
  error?: string;
  source_file_name?: string;
}

interface ReceiptActionResponse {
  approved?: boolean;
  posted?: boolean;
  reopened?: boolean;
  reprocessed?: boolean;
  already_posted?: boolean;
  draft_count?: number;
  order_count?: number;
  item_count?: number;
  ocr_status?: string;
  error?: string;
}

interface UploadDraft {
  file: File;
  uploadName: string;
}

const FILTERS = [
  { value: "", label: "All" },
  { value: "uploaded", label: "Uploaded" },
  { value: "processing", label: "Processing" },
  { value: "needs_review", label: "Needs Review" },
  { value: "extracted", label: "Extracted" },
  { value: "approved", label: "Approved" },
  { value: "posted", label: "Posted" },
  { value: "failed", label: "Failed" },
];

function arrayBufferToBase64(buffer: ArrayBuffer) {
  const bytes = new Uint8Array(buffer);
  const chunkSize = 0x8000;
  let binary = "";
  for (let i = 0; i < bytes.length; i += chunkSize) {
    const chunk = bytes.subarray(i, Math.min(i + chunkSize, bytes.length));
    binary += String.fromCharCode(...chunk);
  }
  return btoa(binary);
}

function localDateStamp(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function compactDateStamp(dateStamp: string) {
  return dateStamp.replaceAll("-", "");
}

function extensionFromFile(file: File) {
  const nameMatch = file.name.match(/(\.[A-Za-z0-9]+)$/);
  if (nameMatch) return nameMatch[1].toLowerCase();
  if (file.type === "image/jpeg") return ".jpg";
  if (file.type === "image/png") return ".png";
  if (file.type === "image/webp") return ".webp";
  if (file.type === "image/bmp") return ".bmp";
  if (file.type === "application/pdf") return ".pdf";
  return "";
}

function isImageUpload(file: File) {
  const extension = extensionFromFile(file);
  return file.type.startsWith("image/") || [".jpg", ".jpeg", ".png", ".webp", ".bmp"].includes(extension);
}

function ensureUploadExtension(name: string, file: File) {
  const trimmed = name.trim();
  if (!trimmed) return "";
  if (/\.[A-Za-z0-9]+$/.test(trimmed)) return trimmed;
  return `${trimmed}${extensionFromFile(file)}`;
}

function receiptSequenceFromName(value: string, dateStamp: string) {
  const compactDate = compactDateStamp(dateStamp);
  const escapedDate = dateStamp.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const compactMatch = value.match(new RegExp(`^${compactDate}(?:[_-](\\d+))?(?:\\.|$)`, "i"));
  if (compactMatch) return compactMatch[1] ? Number(compactMatch[1]) || 0 : 1;
  const filenameMatch = value.match(new RegExp(`^${escapedDate}(?:[_-](\\d+))?(?:\\.|$)`, "i"));
  if (filenameMatch) return filenameMatch[1] ? Number(filenameMatch[1]) || 0 : 1;
  return 0;
}

function nextPhotoSequenceSeed(queue: ReceiptQueueItem[], shopId: number | null, dateStamp: string) {
  let maxSequence = 0;
  for (const item of queue) {
    maxSequence = Math.max(
      maxSequence,
      receiptSequenceFromName(item.receipt_code_prefix || "", dateStamp),
      receiptSequenceFromName(item.source_file_name || "", dateStamp)
    );
  }
  if (shopId !== null) {
    const stored = Number(window.localStorage.getItem(`flame.scanUploadSeq.${shopId}.${dateStamp}`) || "0");
    if (Number.isFinite(stored)) {
      maxSequence = Math.max(maxSequence, stored);
    }
  }
  return maxSequence;
}

function photoSequenceStorageKey(shopId: number | null, dateStamp: string) {
  if (shopId === null) return "";
  return `flame.scanUploadSeq.${shopId}.${dateStamp}`;
}

function emptyDraft(receiptIndex: number): ReceiptDraft {
  return {
    receipt_index: receiptIndex,
    supplier: { name: "", tin: "", site: "", contact_info: "" },
    purchase_order: {
      invoice_id: "",
      purchase_date: "",
      total_cost: 0,
      subtotal_amount: 0,
      tax_amount: 0,
      discount_amount: 0,
      rounding_amount: 0,
      grand_total: 0,
      line_total_basis: "unknown",
    },
    status: "draft",
    validation_errors: [],
    validation_warnings: [],
    purchase_items: [{
      line_no: 1,
      name: "",
      category: "Others",
      quantity: 0,
      unit_price: 0,
      line_discount_percent: 0,
      line_discount_amount: 0,
      line_subtotal_amount: 0,
      line_tax_amount: 0,
      total_price: 0,
      match_product_id: null,
      historical_matches: [],
      validation_errors: [],
      validation_warnings: [],
    }],
  };
}

function draftSaveSignature(drafts: ReceiptDraft[], reviewNote: string, reviewedBy: string) {
  return JSON.stringify({
    review_note: reviewNote,
    reviewed_by: reviewedBy,
    drafts: drafts.map((draft) => ({
      id: draft.id ?? null,
      receipt_index: draft.receipt_index,
      receipt_code: draft.receipt_code || "",
      status: draft.status || "",
      supplier: {
        name: draft.supplier?.name || "",
        tin: draft.supplier?.tin || "",
        site: draft.supplier?.site || "",
        contact_info: draft.supplier?.contact_info || "",
      },
      purchase_order: {
        invoice_id: draft.purchase_order?.invoice_id || "",
        purchase_date: draft.purchase_order?.purchase_date || "",
        total_cost: Number(draft.purchase_order?.total_cost || 0),
        subtotal_amount: Number(draft.purchase_order?.subtotal_amount || 0),
        tax_amount: Number(draft.purchase_order?.tax_amount || 0),
        discount_amount: Number(draft.purchase_order?.discount_amount || 0),
        rounding_amount: Number(draft.purchase_order?.rounding_amount || 0),
        grand_total: Number(draft.purchase_order?.grand_total ?? draft.purchase_order?.total_cost ?? 0),
        line_total_basis: draft.purchase_order?.line_total_basis || "unknown",
      },
      purchase_items: (draft.purchase_items || []).map((item) => ({
        id: item.id ?? null,
        line_no: Number(item.line_no || 0),
        name: item.name || "",
        category: item.category || "Others",
        quantity: Number(item.quantity || 0),
        unit_price: Number(item.unit_price || 0),
        line_discount_percent: Number(item.line_discount_percent || 0),
        line_discount_amount: Number(item.line_discount_amount || 0),
        line_subtotal_amount: Number(item.line_subtotal_amount || 0),
        line_tax_amount: Number(item.line_tax_amount || 0),
        total_price: Number(item.total_price || 0),
        match_product_id: item.match_product_id ?? null,
      })),
    })),
  });
}

function normalizeReceiptDrafts(drafts: ReceiptDraft[]) {
  return drafts.map((draft) => ({
    ...draft,
    receipt_code: draft.receipt_code || "",
    purchase_order: {
      invoice_id: draft.purchase_order?.invoice_id || "",
      purchase_date: draft.purchase_order?.purchase_date || "",
      total_cost: Number(draft.purchase_order?.total_cost || 0),
      subtotal_amount: Number(draft.purchase_order?.subtotal_amount || 0),
      tax_amount: Number(draft.purchase_order?.tax_amount || 0),
      discount_amount: Number(draft.purchase_order?.discount_amount || 0),
      rounding_amount: Number(draft.purchase_order?.rounding_amount || 0),
      grand_total: Number(draft.purchase_order?.grand_total ?? draft.purchase_order?.total_cost ?? 0),
      line_total_basis: draft.purchase_order?.line_total_basis || "unknown",
    },
    purchase_items: Array.isArray(draft.purchase_items)
      ? draft.purchase_items.map((item, index) => ({
          ...item,
          line_no: item.line_no ?? index + 1,
          category: item.category || "Others",
          quantity: Number(item.quantity || 0),
          unit_price: Number(item.unit_price || 0),
          line_discount_percent: Number(item.line_discount_percent || 0),
          line_discount_amount: Number(item.line_discount_amount || 0),
          line_subtotal_amount: Number(item.line_subtotal_amount || 0),
          line_tax_amount: Number(item.line_tax_amount || 0),
          total_price: Number(item.total_price || 0),
        }))
      : [],
  }));
}

function asMessages(value: unknown): string[] {
  if (!Array.isArray(value)) return [];
  return value.map((entry) => String(entry)).filter(Boolean);
}

function itemNeedsAttention(item: ReceiptDraftItem) {
  return asMessages(item.validation_errors).length > 0;
}

function itemHasWarnings(item: ReceiptDraftItem) {
  return asMessages(item.validation_warnings).length > 0;
}

function toNumber(value: string) {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : 0;
}

function optionalNumberValue(value: number | undefined) {
  return value && Number.isFinite(value) ? value : "";
}

function findDraftIndexForPage(pageNo: number, drafts: ReceiptDraft[]) {
  if (!Number.isFinite(pageNo) || pageNo <= 0 || !drafts.length) return null;

  const byReceiptIndex = drafts.findIndex((draft) => draft.receipt_index === pageNo - 1);
  if (byReceiptIndex >= 0) return byReceiptIndex;

  const orderedIndex = pageNo - 1;
  if (orderedIndex >= 0 && orderedIndex < drafts.length) return orderedIndex;

  return null;
}

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}

function summarizeJobProgress(job?: ReceiptJob | null) {
  if (!job) return "";
  if (job.stage === "queued") return "queued";
  if (job.stage === "render") {
    if (job.total_pages > 0) {
      return `${job.processed_pages}/${job.total_pages} previews`;
    }
    return "rendering previews";
  }
  if (job.stage === "ocr" && job.total_pages > 0) {
    return `${job.processed_pages}/${job.total_pages} pages`;
  }
  if (job.stage === "completed" && job.draft_receipts > 0) {
    return `${job.draft_receipts} drafts`;
  }
  if (job.stage === "failed") return "failed";
  if (job.stage) return job.stage;
  if (job.status === "started") return "processing";
  return "";
}

function liveJobMessage(job?: ReceiptJob | null, ocrStatus?: string) {
  const normalizedStatus = (ocrStatus || "").trim().toLowerCase();
  if (!job) {
    return normalizedStatus === "processing" ? "OCR is processing…" : null;
  }
  if (job.stage === "queued") return "OCR queued.";
  if (job.stage === "render") {
    if (job.total_pages > 0) {
      return `Rendering PDF previews ${job.processed_pages}/${job.total_pages}.`;
    }
    return "Rendering PDF pages for previews…";
  }
  if (job.stage === "ocr" && job.total_pages > 0) {
    return `OCR processing ${job.processed_pages}/${job.total_pages} page(s).`;
  }
  if (job.stage === "completed") {
    return `OCR complete. ${job.draft_receipts} draft receipt(s) staged.`;
  }
  if (job.stage === "failed") {
    return job.error ? `OCR failed: ${job.error}` : "OCR failed.";
  }
  if (normalizedStatus === "processing" || job.status === "started") {
    return "OCR is processing…";
  }
  return null;
}

function isFinalReceiptStatus(status?: string) {
  const normalized = (status || "").trim().toLowerCase();
  return ["needs_review", "extracted", "approved", "posted", "failed"].includes(normalized);
}

function isFinishedOcrStatus(status?: string) {
  const normalized = (status || "").trim().toLowerCase();
  return normalized === "needs_review" || normalized === "extracted" || normalized === "failed";
}

function statusFilterForFinishedOcr(status?: string) {
  const normalized = (status || "").trim().toLowerCase();
  if (isFinishedOcrStatus(normalized)) {
    return normalized;
  }
  return "";
}

function statusFilterForReceiptStatus(status?: string) {
  const normalized = (status || "").trim().toLowerCase();
  if (["uploaded", "processing", "needs_review", "extracted", "approved", "posted", "failed"].includes(normalized)) {
    return normalized;
  }
  return "";
}

function waitFor(ms: number) {
  return new Promise<void>((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

async function postScanJson<T extends { error?: string }>(path: string, body: unknown): Promise<T> {
  const res = await apiFetch(path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  const data = (await res.json()) as T;
  if (!res.ok || data.error) {
    throw new Error(data.error || `HTTP ${res.status}`);
  }
  return data;
}

function runAllJobStatusMessage(job?: ReceiptRunAllJob | null) {
  if (!job) return "";
  const payload = job.payload ?? {};
  const total = Number(payload.total_receipts || 0);
  const processed = Number(payload.processed_receipts || 0);
  const posted = Number(payload.posted || 0);
  const needsReview = Number(payload.needs_review || 0);
  const failed = Number(payload.failed || 0);
  const skipped = Number(payload.skipped || 0);
  const counts = `posted ${posted}, needs review ${needsReview}, failed ${failed}${skipped ? `, skipped ${skipped}` : ""}`;
  if (job.status === "completed") {
    return `Run all complete: ${counts}.`;
  }
  if (job.status === "failed") {
    return `Run all failed: ${job.error || "batch error"}. ${counts}.`;
  }
  const currentLabel = payload.current_receipt_label || (payload.current_ocr_id ? `receipt #${payload.current_ocr_id}` : "");
  const stage = (payload.stage || job.stage || "").replace(/_/g, " ");
  const progress = total > 0 ? `${processed}/${total}` : `${processed}`;
  return `Run all ${progress}${stage ? ` ${stage}` : ""}${currentLabel ? `: ${currentLabel}` : ""}. ${counts}.`;
}

function uploadProcessJobStatusMessage(job?: ReceiptUploadProcessJob | null) {
  if (!job) return "";
  const payload = job.payload ?? {};
  const result = payload.final_result || job.ocr_status || "";
  const message = payload.final_message || job.error || "";
  if (job.status === "completed") {
    return `Upload process complete${result ? `: ${result.replace(/_/g, " ")}` : ""}${message ? `. ${message}` : "."}`;
  }
  if (job.status === "failed") {
    return `Upload process failed: ${job.error || message || "job error"}.`;
  }
  const currentLabel = payload.current_receipt_label || (payload.current_ocr_id ? `receipt #${payload.current_ocr_id}` : "");
  const stage = (payload.stage || job.stage || "").replace(/_/g, " ");
  return `Upload process${stage ? ` ${stage}` : ""}${currentLabel ? `: ${currentLabel}` : ""}.`;
}

function formatReviewStatus(status?: string) {
  const normalized = (status || "").trim().toLowerCase();
  if (!normalized || normalized === "pending") return "not reviewed yet";
  if (normalized === "reviewed") return "reviewed";
  return status || "not reviewed yet";
}

function buildOptimisticReceiptJob(
  jobId: number | undefined,
  startedAt: string,
  totalPages: number,
  draftReceipts: number,
  currentJob?: ReceiptJob | null
): ReceiptJob {
  return {
    id: jobId ?? currentJob?.id ?? 0,
    status: "started",
    error: "",
    started_at: startedAt,
    finished_at: "",
    stage: "queued",
    processed_pages: currentJob?.processed_pages ?? 0,
    total_pages: currentJob?.total_pages || totalPages,
    current_page: currentJob?.current_page ?? 0,
    draft_receipts: currentJob?.draft_receipts ?? draftReceipts,
    current_image_path: currentJob?.current_image_path ?? "",
    payload: currentJob?.payload,
  };
}

export default function ScanView({ selectedShopId, selectedShopName, shops = [], onSelectShop }: ScanViewProps) {
  const [queue, setQueue] = useState<ReceiptQueueItem[]>([]);
  const [queueCounts, setQueueCounts] = useState<Record<string, number>>({});
  const [queueLoading, setQueueLoading] = useState(false);
  const [queueError, setQueueError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState("");
  const [selectedReceiptId, setSelectedReceiptId] = useState<number | null>(null);
  const [detail, setDetail] = useState<ReceiptDetailResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);
  const [drafts, setDrafts] = useState<ReceiptDraft[]>([]);
  const [savedDraftSignature, setSavedDraftSignature] = useState(draftSaveSignature([], "", ""));
  const [selectedDraftIndex, setSelectedDraftIndex] = useState<number | null>(null);
  const [reviewNote, setReviewNote] = useState("");
  const [reviewedBy, setReviewedBy] = useState("");
  const [selectedPageId, setSelectedPageId] = useState<number | null>(null);
  const [pageImage, setPageImage] = useState<{ src: string; path: string } | null>(null);
  const [pageLoading, setPageLoading] = useState(false);
  const [pageError, setPageError] = useState<string | null>(null);
  const [imageScale, setImageScale] = useState(1);
  const [imageOffset, setImageOffset] = useState({ x: 0, y: 0 });
  const [imageDragging, setImageDragging] = useState(false);
  const [uploadDrafts, setUploadDrafts] = useState<UploadDraft[]>([]);
  const [uploadRenameOpen, setUploadRenameOpen] = useState(false);
  const [fileInputResetKey, setFileInputResetKey] = useState(0);
  const [uploading, setUploading] = useState(false);
  const [runningOcr, setRunningOcr] = useState(false);
  const [runningAllUploads, setRunningAllUploads] = useState(false);
  const [runAllJobId, setRunAllJobId] = useState<number | null>(null);
  const [processingUpload, setProcessingUpload] = useState(false);
  const [uploadProcessJobId, setUploadProcessJobId] = useState<number | null>(null);
  const [deletingReceipt, setDeletingReceipt] = useState(false);
  const [savingDrafts, setSavingDrafts] = useState(false);
  const [approvingReceipt, setApprovingReceipt] = useState(false);
  const [postingReceipt, setPostingReceipt] = useState(false);
  const [reprocessingReceipt, setReprocessingReceipt] = useState(false);
  const [reopeningReceipt, setReopeningReceipt] = useState(false);
  const [deletingPage, setDeletingPage] = useState(false);
  const [statusMsg, setStatusMsg] = useState<string | null>(null);
  const [shopPickerOpen, setShopPickerOpen] = useState(false);
  const [mobileInboxOpen, setMobileInboxOpen] = useState(false);
  const [mobileCommandOpen, setMobileCommandOpen] = useState(false);
  const [moreActionsOpen, setMoreActionsOpen] = useState(false);
  const selectedReceiptIdRef = useRef<number | null>(null);
  const selectedPageIdRef = useRef<number | null>(null);
  const draftCardRefs = useRef<Array<HTMLDivElement | null>>([]);
  const imageFrameRef = useRef<HTMLDivElement | null>(null);
  const imageRef = useRef<HTMLImageElement | null>(null);
  const moreActionsRef = useRef<HTMLDivElement | null>(null);
  const dragStateRef = useRef<{
    active: boolean;
    pointerId: number | null;
    startX: number;
    startY: number;
    originX: number;
    originY: number;
  }>({
    active: false,
    pointerId: null,
    startX: 0,
    startY: 0,
    originX: 0,
    originY: 0,
  });
  const detailRequestSeqRef = useRef(0);
  const queueRequestSeqRef = useRef(0);
  const pageRequestSeqRef = useRef(0);
  const runAllPollingJobIdRef = useRef<number | null>(null);
  const uploadProcessPollingJobIdRef = useRef<number | null>(null);
  const lastQueueShopIdRef = useRef<number | null>(null);
  const skipNextStatusQueueRefreshRef = useRef(false);
  const statusFilterRef = useRef("");
  const autoOpenFinishedReceiptIdRef = useRef<number | null>(null);
  const queueVisibleLoadCountRef = useRef(0);
  const detailVisibleLoadCountRef = useRef(0);

  const selectedQueueItem = useMemo(
    () => queue.find((item) => item.id === selectedReceiptId) ?? null,
    [queue, selectedReceiptId]
  );
  const categoryOptions = useMemo(() => {
    const seen = new Map<string, string>();
    for (const option of detail?.category_options ?? []) {
      const trimmed = (option || "").trim();
      if (!trimmed) continue;
      const key = trimmed.toLowerCase();
      if (!seen.has(key)) {
        seen.set(key, trimmed);
      }
    }
    if (!seen.has("others")) {
      seen.set("others", "Others");
    }
    return Array.from(seen.values());
  }, [detail?.category_options]);
  const currentDraftSignature = useMemo(
    () => draftSaveSignature(drafts, reviewNote, reviewedBy),
    [drafts, reviewNote, reviewedBy]
  );
  const hasUnsavedDraftChanges = currentDraftSignature !== savedDraftSignature;
  const effectiveReceiptStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? "";
  const normalizedEffectiveReceiptStatus = (effectiveReceiptStatus || "").trim().toLowerCase();
  const receiptStatusIsFinal = isFinalReceiptStatus(effectiveReceiptStatus);
  const activeJob = detail?.job ?? selectedQueueItem?.job ?? null;
  const activeJobIsRunning =
    activeJob?.status === "started" ||
    activeJob?.stage === "queued" ||
    activeJob?.stage === "render" ||
    activeJob?.stage === "ocr";
  const isReceiptProcessing = Boolean(
    selectedReceiptId &&
      (
        normalizedEffectiveReceiptStatus === "processing" ||
        (!receiptStatusIsFinal && activeJobIsRunning)
      )
  );
  const liveProgressLabel = useMemo(
    () => liveJobMessage(activeJob, effectiveReceiptStatus),
    [activeJob, effectiveReceiptStatus]
  );
  const hasProcessingReceipts = useMemo(
    () =>
      Number(queueCounts.processing || 0) > 0 ||
      queue.some((item) => {
        const itemStatus = (item.ocr_status || "").trim().toLowerCase();
        const itemJobIsRunning =
          item.job?.status === "started" ||
          item.job?.stage === "queued" ||
          item.job?.stage === "render" ||
          item.job?.stage === "ocr";
        return itemStatus === "processing" || (!isFinalReceiptStatus(itemStatus) && itemJobIsRunning);
      }),
    [queue, queueCounts]
  );

  const clampImageOffset = useCallback((nextOffset: { x: number; y: number }, nextScale = imageScale) => {
    const frame = imageFrameRef.current;
    const image = imageRef.current;
    if (!frame || !image || nextScale <= 1) {
      return { x: 0, y: 0 };
    }

    const maxX = Math.max(0, (image.clientWidth * nextScale - frame.clientWidth) / 2);
    const maxY = Math.max(0, (image.clientHeight * nextScale - frame.clientHeight) / 2);
    return {
      x: clamp(nextOffset.x, -maxX, maxX),
      y: clamp(nextOffset.y, -maxY, maxY),
    };
  }, [imageScale]);

  const resetImageView = useCallback(() => {
    dragStateRef.current.active = false;
    dragStateRef.current.pointerId = null;
    setImageDragging(false);
    setImageScale(1);
    setImageOffset({ x: 0, y: 0 });
  }, []);

  const applyImageScale = useCallback((nextScale: number) => {
    const boundedScale = clamp(nextScale, 1, 5);
    setImageScale(boundedScale);
    setImageOffset((current) => clampImageOffset(current, boundedScale));
  }, [clampImageOffset]);

  const zoomImage = useCallback((delta: number) => {
    applyImageScale(imageScale + delta);
  }, [applyImageScale, imageScale]);

  useEffect(() => {
    selectedReceiptIdRef.current = selectedReceiptId;
  }, [selectedReceiptId]);

  useEffect(() => {
    statusFilterRef.current = statusFilter;
  }, [statusFilter]);

  useEffect(() => {
    selectedPageIdRef.current = selectedPageId;
  }, [selectedPageId]);

  useEffect(() => {
    resetImageView();
  }, [resetImageView, selectedPageId, pageImage?.src]);

  const loadQueue = useCallback(
    async (
      preferredReceiptId?: number | null,
      options?: { silent?: boolean; ocrStatus?: string; fallbackToFirst?: boolean; preserveMissingSelection?: boolean }
    ) => {
      if (selectedShopId === null) return;
      const requestSeq = ++queueRequestSeqRef.current;
      const silent = options?.silent === true;
      const queueStatus = options?.ocrStatus ?? statusFilterRef.current;
      if (!silent) {
        queueVisibleLoadCountRef.current += 1;
        setQueueLoading(true);
      }
      setQueueError(null);
      try {
        const res = await apiFetch("/receipt_queue", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ shop_id: selectedShopId, ocr_status: queueStatus, limit: 100 }),
        });
        const data = (await res.json()) as { items?: ReceiptQueueItem[]; counts?: Record<string, number>; error?: string };
        if (!res.ok || data.error) {
          throw new Error(data.error || `HTTP ${res.status}`);
        }
        const items = Array.isArray(data.items) ? data.items : [];
        if (queueRequestSeqRef.current !== requestSeq) {
          return;
        }
        const currentReceiptId = selectedReceiptIdRef.current;
        const receiptIdToPreserve = preferredReceiptId ?? currentReceiptId;
        const shouldPreserveMissingSelection = Boolean(
          options?.preserveMissingSelection &&
            receiptIdToPreserve &&
            !items.some((item) => item.id === receiptIdToPreserve)
        );
        setQueue((current) => {
          if (!shouldPreserveMissingSelection || !receiptIdToPreserve) {
            return items;
          }
          const preservedItem = current.find((item) => item.id === receiptIdToPreserve);
          return preservedItem ? [preservedItem, ...items] : items;
        });
        setQueueCounts(data.counts ?? {});
        let nextSelectedReceiptId: number | null = null;
        if (preferredReceiptId && items.some((item) => item.id === preferredReceiptId)) {
          nextSelectedReceiptId = preferredReceiptId;
        } else if (currentReceiptId && items.some((item) => item.id === currentReceiptId)) {
          nextSelectedReceiptId = currentReceiptId;
        } else if (shouldPreserveMissingSelection && receiptIdToPreserve) {
          nextSelectedReceiptId = receiptIdToPreserve;
        } else if (options?.fallbackToFirst && items.length > 0) {
          nextSelectedReceiptId = items[0].id;
        }
        setSelectedReceiptId(nextSelectedReceiptId);
        return nextSelectedReceiptId;
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load receipt queue";
        if (queueRequestSeqRef.current === requestSeq) {
          setQueueError(msg);
        }
        return null;
      } finally {
        if (!silent) {
          queueVisibleLoadCountRef.current = Math.max(0, queueVisibleLoadCountRef.current - 1);
          if (queueVisibleLoadCountRef.current === 0) {
            setQueueLoading(false);
          }
        }
      }
    },
    [selectedShopId]
  );

  const openFinishedOcrReceipt = useCallback(
    (finishedDetail: ReceiptDetailResponse, finishedDrafts: ReceiptDraft[]) => {
      const receiptId = finishedDetail.scan.id;
      const nextStatus = (finishedDetail.scan.ocr_status || "").trim().toLowerCase();
      if (!isFinishedOcrStatus(nextStatus)) {
        return false;
      }

      const nextFilter = statusFilterForFinishedOcr(nextStatus);
      const updatedAt = finishedDetail.scan.updated_at || new Date().toISOString();
      const completedJob =
        finishedDetail.job && nextStatus !== "failed"
          ? {
              ...finishedDetail.job,
              status: finishedDetail.job.status === "failed" ? finishedDetail.job.status : "completed",
              stage: finishedDetail.job.stage === "failed" ? finishedDetail.job.stage : "completed",
              finished_at: finishedDetail.job.finished_at || updatedAt,
            }
          : finishedDetail.job ?? null;
      const draftTotalCost = finishedDrafts.reduce(
        (sum, draft) => sum + Number(draft.purchase_order?.grand_total ?? draft.purchase_order?.total_cost ?? 0),
        0
      );
      const detailItem: ReceiptQueueItem = {
        id: finishedDetail.scan.id,
        shop_id: finishedDetail.scan.shop_id,
        source_file_name: finishedDetail.scan.source_file_name,
        receipt_code_prefix: finishedDetail.scan.receipt_code_prefix,
        mime_type: finishedDetail.scan.mime_type,
        image_path: finishedDetail.scan.image_path,
        source_path: finishedDetail.scan.source_path,
        ocr_status: nextStatus,
        review_status: finishedDetail.scan.review_status,
        page_count: finishedDetail.scan.page_count,
        scanned_at: finishedDetail.scan.scanned_at,
        updated_at: updatedAt,
        draft_count: finishedDrafts.length,
        draft_total_cost: draftTotalCost,
        draft_status: finishedDrafts[0]?.status || "",
        first_page_id: finishedDetail.pages[0]?.id ?? 0,
        job: completedJob,
      };

      autoOpenFinishedReceiptIdRef.current = null;
      selectedReceiptIdRef.current = receiptId;
      statusFilterRef.current = nextFilter;
      skipNextStatusQueueRefreshRef.current = true;
      setSelectedReceiptId(receiptId);
      setStatusFilter(nextFilter);
      setMobileInboxOpen(false);
      setMobileCommandOpen(false);
      setQueue((current) => {
        const currentItem = current.find((item) => item.id === receiptId);
        const finishedItem: ReceiptQueueItem = {
          ...(currentItem ?? detailItem),
          ...detailItem,
        };
        return [
          finishedItem,
          ...current.filter((item) => item.id !== receiptId && (!nextFilter || item.ocr_status === nextFilter)),
        ];
      });
      setStatusMsg(
        nextFilter
          ? `OCR finished. Receipt #${receiptId} moved to ${FILTERS.find((filter) => filter.value === nextFilter)?.label || nextFilter}.`
          : `OCR finished for receipt #${receiptId}.`
      );
      void loadQueue(receiptId, { silent: true, ocrStatus: nextFilter, preserveMissingSelection: true });
      return true;
    },
    [loadQueue]
  );

  const loadDetail = useCallback(
    async (ocrId: number, options?: { silent?: boolean }) => {
      if (selectedShopId === null) return;
      const requestSeq = ++detailRequestSeqRef.current;
      const silent = options?.silent === true;
      if (!silent) {
        detailVisibleLoadCountRef.current += 1;
        setDetailLoading(true);
      }
      setDetailError(null);
      try {
        const res = await apiFetch("/receipt_detail", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ shop_id: selectedShopId, ocr_id: ocrId }),
        });
        const data = (await res.json()) as ReceiptDetailResponse & { error?: string };
        if (!res.ok || data.error) {
          throw new Error(data.error || `HTTP ${res.status}`);
        }
        if (detailRequestSeqRef.current !== requestSeq || selectedReceiptIdRef.current !== ocrId) {
          return;
        }
        const pages = Array.isArray(data.pages) ? data.pages : [];
        const nextDrafts = normalizeReceiptDrafts(Array.isArray(data.drafts) ? data.drafts : []);
        const nextReviewNote = data.review?.review_note ?? "";
        const nextReviewedBy = data.review?.reviewed_by ?? "";
        setDetail(data);
        setDrafts(nextDrafts);
        setSavedDraftSignature(draftSaveSignature(nextDrafts, nextReviewNote, nextReviewedBy));
        draftCardRefs.current = [];
        setReviewNote(nextReviewNote);
        setReviewedBy(nextReviewedBy);
        setSelectedPageId((current) => {
          if (current && pages.some((page) => page.id === current)) return current;
          return pages[0]?.id ?? null;
        });
        if (
          selectedReceiptIdRef.current === ocrId &&
          (statusFilterRef.current === "processing" || autoOpenFinishedReceiptIdRef.current === ocrId)
        ) {
          openFinishedOcrReceipt(data, nextDrafts);
        }
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load receipt detail";
        if (detailRequestSeqRef.current === requestSeq && selectedReceiptIdRef.current === ocrId) {
          setDetailError(msg);
        }
      } finally {
        if (!silent) {
          detailVisibleLoadCountRef.current = Math.max(0, detailVisibleLoadCountRef.current - 1);
          if (detailVisibleLoadCountRef.current === 0 && selectedReceiptIdRef.current === ocrId) {
            setDetailLoading(false);
          }
        }
      }
    },
    [openFinishedOcrReceipt, selectedShopId]
  );

  useEffect(() => {
    setQueue([]);
    setQueueCounts({});
    selectedReceiptIdRef.current = null;
    selectedPageIdRef.current = null;
    autoOpenFinishedReceiptIdRef.current = null;
    setSelectedReceiptId(null);
    setDetail(null);
    setDrafts([]);
    setSavedDraftSignature(draftSaveSignature([], "", ""));
    setSelectedDraftIndex(null);
    setReviewNote("");
    setReviewedBy("");
    setSelectedPageId(null);
    setPageImage(null);
    setPageError(null);
    setStatusMsg(null);
    setRunningAllUploads(false);
    setRunAllJobId(null);
    setProcessingUpload(false);
    setUploadProcessJobId(null);
    runAllPollingJobIdRef.current = null;
    uploadProcessPollingJobIdRef.current = null;
    if (selectedShopId !== null) {
      void loadQueue();
    }
  }, [loadQueue, selectedShopId]);

  useEffect(() => {
    setMobileInboxOpen(false);
    setShopPickerOpen(false);
    setMobileCommandOpen(false);
    setMoreActionsOpen(false);
  }, [selectedShopId]);

  useEffect(() => {
    if (!moreActionsOpen) return;

    const handlePointerDown = (event: PointerEvent) => {
      const target = event.target;
      if (target instanceof Node && moreActionsRef.current?.contains(target)) {
        return;
      }
      setMoreActionsOpen(false);
    };

    window.addEventListener("pointerdown", handlePointerDown);
    return () => {
      window.removeEventListener("pointerdown", handlePointerDown);
    };
  }, [moreActionsOpen]);

  useEffect(() => {
    if (selectedShopId === null) return;
    const shopChanged = lastQueueShopIdRef.current !== selectedShopId;
    lastQueueShopIdRef.current = selectedShopId;
    if (shopChanged) {
      return;
    }
    if (skipNextStatusQueueRefreshRef.current) {
      skipNextStatusQueueRefreshRef.current = false;
      return;
    }
    const currentReceiptId = selectedReceiptIdRef.current;
    void loadQueue(currentReceiptId, { fallbackToFirst: currentReceiptId !== null });
  }, [loadQueue, selectedShopId, statusFilter]);

  useEffect(() => {
    if (selectedReceiptId === null) {
      setDetailLoading(false);
      setDetail(null);
      setDrafts([]);
      setSavedDraftSignature(draftSaveSignature([], "", ""));
      setSelectedDraftIndex(null);
      setSelectedPageId(null);
      setPageImage(null);
      return;
    }
    void loadDetail(selectedReceiptId);
  }, [loadDetail, selectedReceiptId]);

  useEffect(() => {
    if (selectedShopId === null || selectedReceiptId === null || !detail || detail.scan.id !== selectedReceiptId) {
      return;
    }
    const detailStatus = (detail.scan.ocr_status || "").trim().toLowerCase();
    if (!statusFilter || !detailStatus || detailStatus === statusFilter) {
      return;
    }
    if (statusFilter === "processing") {
      return;
    }
    void loadQueue(null, { silent: true, fallbackToFirst: true });
  }, [detail, loadQueue, selectedReceiptId, selectedShopId, statusFilter]);

  useEffect(() => {
    if (!drafts.length) {
      setSelectedDraftIndex(null);
      return;
    }

    const selectedPage = detail?.pages.find((page) => page.id === selectedPageId) ?? null;
    const mappedDraftIndex = selectedPage ? findDraftIndexForPage(selectedPage.page_no, drafts) : null;

    setSelectedDraftIndex((current) => {
      if (mappedDraftIndex !== null) return mappedDraftIndex;
      if (current !== null && current >= 0 && current < drafts.length) return current;
      return 0;
    });
  }, [detail?.pages, drafts, selectedPageId]);

  useEffect(() => {
    if (selectedDraftIndex === null) return;
    const target = draftCardRefs.current[selectedDraftIndex];
    if (!target) return;
    target.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" });
    target.focus({ preventScroll: true });
  }, [selectedDraftIndex]);

  const handleImagePointerDown = useCallback((event: ReactPointerEvent<HTMLDivElement>) => {
    if (event.pointerType === "mouse" && event.button !== 0) return;
    if (!pageImage || imageScale <= 1) return;
    event.preventDefault();
    dragStateRef.current = {
      active: true,
      pointerId: event.pointerId,
      startX: event.clientX,
      startY: event.clientY,
      originX: imageOffset.x,
      originY: imageOffset.y,
    };
    setImageDragging(true);
  }, [imageOffset.x, imageOffset.y, imageScale, pageImage]);

  const stopImageDrag = useCallback(() => {
    dragStateRef.current = {
      active: false,
      pointerId: null,
      startX: 0,
      startY: 0,
      originX: 0,
      originY: 0,
    };
    setImageDragging(false);
  }, []);

  useEffect(() => {
    if (!imageDragging) {
      return;
    }

    const handleWindowPointerMove = (event: PointerEvent) => {
      const drag = dragStateRef.current;
      if (!drag.active || drag.pointerId !== event.pointerId) return;
      const nextOffset = {
        x: drag.originX + (event.clientX - drag.startX),
        y: drag.originY + (event.clientY - drag.startY),
      };
      setImageOffset(clampImageOffset(nextOffset));
    };

    const handleWindowPointerEnd = (event: PointerEvent) => {
      const drag = dragStateRef.current;
      if (!drag.active || drag.pointerId !== event.pointerId) return;
      stopImageDrag();
    };

    window.addEventListener("pointermove", handleWindowPointerMove);
    window.addEventListener("pointerup", handleWindowPointerEnd);
    window.addEventListener("pointercancel", handleWindowPointerEnd);

    return () => {
      window.removeEventListener("pointermove", handleWindowPointerMove);
      window.removeEventListener("pointerup", handleWindowPointerEnd);
      window.removeEventListener("pointercancel", handleWindowPointerEnd);
    };
  }, [clampImageOffset, imageDragging, stopImageDrag]);

  const handleImageWheel = useCallback((event: ReactWheelEvent<HTMLDivElement>) => {
    if (!pageImage) return;
    event.preventDefault();
    const delta = event.deltaY < 0 ? 0.2 : -0.2;
    applyImageScale(imageScale + delta);
  }, [applyImageScale, imageScale, pageImage]);

  useEffect(() => {
    const loadPage = async () => {
      if (selectedShopId === null || selectedPageId === null) {
        setPageLoading(false);
        setPageImage(null);
        return;
      }
      const requestSeq = ++pageRequestSeqRef.current;
      const pageId = selectedPageId;
      setPageLoading(true);
      setPageError(null);
      try {
        const res = await apiFetch("/receipt_page_image", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ shop_id: selectedShopId, page_id: pageId }),
        });
        const data = (await res.json()) as { image_base64?: string; image_path?: string; error?: string };
        if (!res.ok || data.error) {
          throw new Error(data.error || `HTTP ${res.status}`);
        }
        if (pageRequestSeqRef.current !== requestSeq || selectedPageIdRef.current !== pageId) {
          return;
        }
        const src = data.image_base64 ? `data:image/*;base64,${data.image_base64}` : "";
        setPageImage(src ? { src, path: data.image_path ?? "" } : null);
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to load page image";
        if (pageRequestSeqRef.current === requestSeq && selectedPageIdRef.current === pageId) {
          setPageError(msg);
          setPageImage(null);
        }
      } finally {
        if (pageRequestSeqRef.current === requestSeq && selectedPageIdRef.current === pageId) {
          setPageLoading(false);
        }
      }
    };
    void loadPage();
  }, [selectedPageId, selectedShopId]);

  useEffect(() => {
    if (selectedShopId === null || selectedReceiptId === null || !isReceiptProcessing) {
      return;
    }
    let cancelled = false;
    let queuePollCounter = 0;

    const poll = async () => {
      while (!cancelled && selectedReceiptIdRef.current === selectedReceiptId) {
        const pending: Promise<unknown>[] = [
          loadDetail(selectedReceiptId, { silent: true }),
        ];
        queuePollCounter += 1;
        if (queuePollCounter % 4 === 1) {
          pending.push(loadQueue(selectedReceiptId, { silent: true, preserveMissingSelection: true }));
        }
        await Promise.allSettled(pending);
        if (cancelled || selectedReceiptIdRef.current !== selectedReceiptId) {
          break;
        }
        await new Promise<void>((resolve) => {
          window.setTimeout(resolve, 2500);
        });
      }
    };

    void poll();
    return () => {
      cancelled = true;
    };
  }, [
    isReceiptProcessing,
    loadDetail,
    loadQueue,
    selectedReceiptId,
    selectedShopId,
  ]);

  useEffect(() => {
    if (selectedShopId === null || !hasProcessingReceipts || isReceiptProcessing) {
      return;
    }

    let cancelled = false;

    const poll = async () => {
      while (!cancelled) {
        await loadQueue(selectedReceiptIdRef.current, { silent: true, preserveMissingSelection: true });
        if (cancelled) {
          break;
        }
        await new Promise<void>((resolve) => {
          window.setTimeout(resolve, 2500);
        });
      }
    };

    void poll();
    return () => {
      cancelled = true;
    };
  }, [hasProcessingReceipts, isReceiptProcessing, loadQueue, selectedShopId]);

  useEffect(() => {
    if (statusFilter !== "processing" || !selectedReceiptId || !detail) {
      return;
    }

    const nextStatus = (detail.scan.ocr_status || "").trim().toLowerCase();
    if (!isFinishedOcrStatus(nextStatus)) {
      return;
    }

    openFinishedOcrReceipt(detail, drafts);
  }, [detail, drafts, openFinishedOcrReceipt, selectedReceiptId, statusFilter]);

  const pollUploadProcessJob = useCallback(
    async (jobId: number, ocrId: number) => {
      if (selectedShopId === null || !jobId || !ocrId) return;
      if (uploadProcessPollingJobIdRef.current === jobId) return;
      uploadProcessPollingJobIdRef.current = jobId;
      setUploadProcessJobId(jobId);
      setProcessingUpload(true);

      try {
        let finalStatus = "";
        while (uploadProcessPollingJobIdRef.current === jobId) {
          const data = await postScanJson<ReceiptUploadProcessResponse>("/receipt_upload_process_status", {
            shop_id: selectedShopId,
            job_id: jobId,
          });
          const job = data.job ?? null;
          if (!job) {
            throw new Error("Upload process job was not found.");
          }

          finalStatus = job.ocr_status || job.payload?.final_ocr_status || finalStatus;
          setStatusMsg(uploadProcessJobStatusMessage(job));
          selectedReceiptIdRef.current = ocrId;
          setSelectedReceiptId(ocrId);

          await loadQueue(ocrId, {
            silent: job.status === "started",
            ocrStatus: job.status === "started" ? statusFilterRef.current : statusFilterForReceiptStatus(finalStatus),
            preserveMissingSelection: true,
          });

          if (job.status !== "started") {
            break;
          }
          await waitFor(2500);
        }

        const nextFilter = statusFilterForReceiptStatus(finalStatus);
        if (nextFilter) {
          skipNextStatusQueueRefreshRef.current = true;
          statusFilterRef.current = nextFilter;
          setStatusFilter(nextFilter);
        }
        selectedReceiptIdRef.current = ocrId;
        setSelectedReceiptId(ocrId);
        await loadQueue(ocrId, {
          ocrStatus: nextFilter || statusFilterRef.current,
          fallbackToFirst: false,
          preserveMissingSelection: true,
        });
        await loadDetail(ocrId);
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to poll upload process";
        setDetailError(msg);
        setStatusMsg(null);
      } finally {
        if (uploadProcessPollingJobIdRef.current === jobId) {
          uploadProcessPollingJobIdRef.current = null;
        }
        setProcessingUpload(false);
        setUploadProcessJobId(null);
      }
    },
    [loadDetail, loadQueue, selectedShopId]
  );

  const handleFilePick = useCallback((event: ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files || []);
    if (!files.length) return;

    const dateStamp = localDateStamp();
    let nextPhotoSequence = nextPhotoSequenceSeed(queue, selectedShopId, dateStamp);
    const nextDrafts = files.map((file) => {
      if (!isImageUpload(file)) {
        return { file, uploadName: file.name };
      }
      nextPhotoSequence += 1;
      return {
        file,
        uploadName: `${dateStamp}_${String(nextPhotoSequence).padStart(2, "0")}${extensionFromFile(file) || ".jpg"}`,
      };
    });

    const sequenceKey = photoSequenceStorageKey(selectedShopId, dateStamp);
    if (sequenceKey && nextPhotoSequence > 0) {
      window.localStorage.setItem(sequenceKey, String(nextPhotoSequence));
    }

    setUploadDrafts(nextDrafts);
    setUploadRenameOpen(true);
    setQueueError(null);
  }, [queue, selectedShopId]);

  const handleUploadNameChange = useCallback((index: number, value: string) => {
    setUploadDrafts((current) =>
      current.map((draft, draftIndex) =>
        draftIndex === index ? { ...draft, uploadName: value } : draft
      )
    );
  }, []);

  const clearUploadDrafts = useCallback(() => {
    setUploadDrafts([]);
    setUploadRenameOpen(false);
    setFileInputResetKey((value) => value + 1);
  }, []);

  const handleUpload = useCallback(async () => {
    if (selectedShopId === null) {
      setQueueError("Select a shop first.");
      return;
    }
    if (!uploadDrafts.length) {
      setQueueError("Choose one or more files first.");
      return;
    }
    if (uploadDrafts.some((draft) => !draft.uploadName.trim())) {
      setQueueError("Every selected file needs an upload name.");
      setUploadRenameOpen(true);
      return;
    }

    setUploading(true);
    setQueueError(null);
    let lastUploadedOcrId: number | null = null;
    let lastDuplicateOcrId: number | null = null;
    let uploadedCount = 0;
    let duplicateCount = 0;
    const uploadedNames: string[] = [];
    const duplicateNames: string[] = [];
    try {
      for (const [index, draft] of uploadDrafts.entries()) {
        const uploadFile = draft.file;
        const uploadName = ensureUploadExtension(draft.uploadName, uploadFile);
        setStatusMsg(`Uploading ${index + 1}/${uploadDrafts.length}: ${uploadName}…`);
        const buffer = await uploadFile.arrayBuffer();
        const content_base64 = arrayBufferToBase64(buffer);
        const res = await apiFetch("/receipt_upload", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            shop_id: selectedShopId,
            file_name: uploadName,
            mime_type: uploadFile.type || "application/octet-stream",
            content_base64,
          }),
        });
        const data = (await res.json()) as {
          ocr_id?: number;
          duplicate?: boolean;
          existing_ocr_id?: number;
          receipt_code_prefix?: string;
          ocr_status?: string;
          file_name?: string;
          message?: string;
          error?: string;
        };
        const ocrId = data.ocr_id || data.existing_ocr_id || null;
        if (!res.ok || data.error || !ocrId) {
          throw new Error(`${uploadFile.name}: ${data.error || `HTTP ${res.status}`}`);
        }
        if (data.duplicate) {
          duplicateCount += 1;
          lastDuplicateOcrId = ocrId;
          duplicateNames.push(
            `${uploadName}${data.receipt_code_prefix ? ` (${data.receipt_code_prefix})` : ""}`
          );
        } else {
          uploadedCount += 1;
          lastUploadedOcrId = ocrId;
          uploadedNames.push(uploadName);
        }
      }

      const today = localDateStamp();
      const maxUploadedSequence = uploadDrafts.reduce(
        (max, draft) => Math.max(max, receiptSequenceFromName(ensureUploadExtension(draft.uploadName, draft.file), today)),
        0
      );
      const sequenceKey = photoSequenceStorageKey(selectedShopId, today);
      if (sequenceKey && maxUploadedSequence > 0) {
        window.localStorage.setItem(sequenceKey, String(maxUploadedSequence));
      }

      setUploadDrafts([]);
      setUploadRenameOpen(false);
      setFileInputResetKey((value) => value + 1);
      statusFilterRef.current = "";
      setStatusFilter("");
      const receiptToSelect = lastUploadedOcrId ?? lastDuplicateOcrId;
      if (receiptToSelect !== null) {
        await loadQueue(receiptToSelect);
        setSelectedReceiptId(receiptToSelect);
        await loadDetail(receiptToSelect);
      }
      const uploadedText = uploadedNames.length
        ? `Uploaded ${uploadedCount}: ${uploadedNames.join(", ")}.`
        : `Uploaded ${uploadedCount} file(s).`;
      const duplicateText = duplicateNames.length
        ? ` Refused duplicates: ${duplicateNames.join(", ")}.`
        : "";
      setStatusMsg(
        duplicateCount
          ? `${uploadedText}${duplicateText}`
          : uploadedText
      );
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to upload receipt";
      setQueueError(msg);
      setStatusMsg(null);
    } finally {
      setUploading(false);
    }
  }, [loadDetail, loadQueue, selectedShopId, uploadDrafts]);

  const handleUploadAndProcess = useCallback(async () => {
    if (selectedShopId === null) {
      setQueueError("Select a shop first.");
      return;
    }
    if (uploadDrafts.length !== 1) {
      setQueueError("Upload and process works with one receipt photo at a time.");
      setUploadRenameOpen(true);
      return;
    }
    const draft = uploadDrafts[0];
    if (!draft.uploadName.trim()) {
      setQueueError("The selected file needs an upload name.");
      setUploadRenameOpen(true);
      return;
    }

    const uploadFile = draft.file;
    const uploadName = ensureUploadExtension(draft.uploadName, uploadFile);
    setUploading(true);
    setProcessingUpload(true);
    setQueueError(null);
    setDetailError(null);
    setStatusMsg(`Uploading ${uploadName} for backend processing…`);

    try {
      const buffer = await uploadFile.arrayBuffer();
      const content_base64 = arrayBufferToBase64(buffer);
      const data = await postScanJson<ReceiptUploadProcessResponse>("/receipt_upload_and_process", {
        shop_id: selectedShopId,
        file_name: uploadName,
        mime_type: uploadFile.type || "application/octet-stream",
        content_base64,
      });

      const today = localDateStamp();
      const uploadedSequence = receiptSequenceFromName(uploadName, today);
      const sequenceKey = photoSequenceStorageKey(selectedShopId, today);
      if (sequenceKey && uploadedSequence > 0) {
        window.localStorage.setItem(sequenceKey, String(uploadedSequence));
      }

      setUploadDrafts([]);
      setUploadRenameOpen(false);
      setFileInputResetKey((value) => value + 1);
      setMobileInboxOpen(false);
      setMobileCommandOpen(false);

      const ocrId = Number(data.ocr_id || data.existing_ocr_id || 0);
      if (!ocrId) {
        throw new Error(data.message || "Backend upload did not return a receipt id.");
      }

      if (data.duplicate || !data.accepted) {
        const duplicateFilter = statusFilterForReceiptStatus(data.ocr_status) || "";
        statusFilterRef.current = duplicateFilter;
        setStatusFilter(duplicateFilter);
        selectedReceiptIdRef.current = ocrId;
        setSelectedReceiptId(ocrId);
        await loadQueue(ocrId, { ocrStatus: duplicateFilter, preserveMissingSelection: true });
        await loadDetail(ocrId);
        setStatusMsg(data.message || "Upload refused because this receipt already exists.");
        setProcessingUpload(false);
        return;
      }

      const jobId = Number(data.job_id || data.job?.id || 0);
      if (!jobId) {
        throw new Error(data.message || "Backend process job did not start.");
      }

      skipNextStatusQueueRefreshRef.current = true;
      statusFilterRef.current = "processing";
      setStatusFilter("processing");
      selectedReceiptIdRef.current = ocrId;
      setSelectedReceiptId(ocrId);
      setUploadProcessJobId(jobId);
      setStatusMsg(data.job ? uploadProcessJobStatusMessage(data.job) : data.message || "Backend upload process started.");
      await loadQueue(ocrId, { ocrStatus: "", preserveMissingSelection: true });
      void loadDetail(ocrId, { silent: true });
      void pollUploadProcessJob(jobId, ocrId);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to upload and process receipt";
      setQueueError(msg);
      setStatusMsg(null);
      setProcessingUpload(false);
    } finally {
      setUploading(false);
    }
  }, [loadDetail, loadQueue, pollUploadProcessJob, selectedShopId, uploadDrafts]);

  const uploadRenameDialog = uploadRenameOpen && uploadDrafts.length > 0 ? (
    <div className="scan-upload-dialog-backdrop">
      <section className="scan-upload-dialog" role="dialog" aria-modal="true" aria-labelledby="scan-upload-dialog-title">
        <div className="scan-upload-dialog-header">
          <div>
            <div id="scan-upload-dialog-title" className="card-title">Name receipt uploads</div>
            <div className="card-subtitle">
              Photo names default to today plus a daily number. Edit names before uploading.
            </div>
          </div>
          <button type="button" className="secondary-button" onClick={clearUploadDrafts} disabled={uploading || processingUpload}>
            Cancel
          </button>
        </div>
        <div className="scan-upload-dialog-list">
          {uploadDrafts.map((draft, index) => {
            const finalName = ensureUploadExtension(draft.uploadName, draft.file);
            return (
              <label key={`${draft.file.name}-${index}`} className="scan-upload-name-row">
                <span>
                  <strong>{isImageUpload(draft.file) ? "Photo" : "File"} {index + 1}</strong>
                  <small>{draft.file.name}</small>
                </span>
                <input
                  value={draft.uploadName}
                  onChange={(event) => handleUploadNameChange(index, event.target.value)}
                  placeholder={isImageUpload(draft.file) ? `${localDateStamp()}_01` : draft.file.name}
                  disabled={uploading}
                />
                <small className="scan-upload-final-name">Upload name: {finalName || "missing"}</small>
              </label>
            );
          })}
        </div>
        <div className="scan-upload-dialog-actions">
          <button type="button" className="secondary-button" onClick={clearUploadDrafts} disabled={uploading || processingUpload}>
            Clear
          </button>
          <button
            type="button"
            className="submit-button scan-upload-process-button"
            onClick={handleUploadAndProcess}
            disabled={
              uploading ||
              processingUpload ||
              uploadDrafts.length !== 1 ||
              uploadDrafts.some((draft) => !draft.uploadName.trim())
            }
            title={uploadDrafts.length !== 1 ? "Upload and process handles one receipt at a time" : undefined}
          >
            {processingUpload ? "Processing…" : "Upload and process receipt"}
          </button>
          <button
            type="button"
            className="submit-button"
            onClick={handleUpload}
            disabled={uploading || processingUpload || uploadDrafts.some((draft) => !draft.uploadName.trim())}
          >
            {uploading ? "Uploading…" : uploadDrafts.length > 1 ? `Upload ${uploadDrafts.length} receipts` : "Upload receipt"}
          </button>
        </div>
      </section>
    </div>
  ) : null;

  const visibleUploadedReceiptCount = queue.filter((item) => (item.ocr_status || "").trim().toLowerCase() === "uploaded").length;
  const uploadedReceiptCount = Math.max(Number(queueCounts.uploaded || 0), visibleUploadedReceiptCount);
  const selectedShopLabel = selectedShopName || (selectedShopId !== null ? `shop_id ${selectedShopId}` : "Select shop");

  const jumpReceiptToStatus = useCallback(
    (
      receiptId: number,
      nextStatus: string,
      options?: {
        reviewStatus?: string;
        draftStatus?: string;
        job?: ReceiptJob | null;
      }
    ) => {
      const updatedAt = new Date().toISOString();
      if (statusFilter !== nextStatus) {
        skipNextStatusQueueRefreshRef.current = true;
      }
      selectedReceiptIdRef.current = receiptId;
      statusFilterRef.current = nextStatus;
      if (nextStatus === "processing") {
        autoOpenFinishedReceiptIdRef.current = receiptId;
      } else if (autoOpenFinishedReceiptIdRef.current === receiptId) {
        autoOpenFinishedReceiptIdRef.current = null;
      }
      setSelectedReceiptId(receiptId);
      setStatusFilter(nextStatus);

      const draftTotalCost = drafts.reduce(
        (sum, draft) => sum + Number(draft.purchase_order?.grand_total ?? draft.purchase_order?.total_cost ?? 0),
        0
      );

      setQueue((current) => {
        const currentItem = current.find((item) => item.id === receiptId);
        const selectedItem = selectedQueueItem?.id === receiptId ? selectedQueueItem : null;
        const detailItem =
          detail?.scan.id === receiptId
            ? {
                id: detail.scan.id,
                shop_id: detail.scan.shop_id,
                source_file_name: detail.scan.source_file_name,
                receipt_code_prefix: detail.scan.receipt_code_prefix,
                mime_type: detail.scan.mime_type,
                image_path: detail.scan.image_path,
                source_path: detail.scan.source_path,
                ocr_status: detail.scan.ocr_status,
                review_status: detail.scan.review_status,
                page_count: detail.scan.page_count,
                scanned_at: detail.scan.scanned_at,
                updated_at: detail.scan.updated_at,
                draft_count: drafts.length,
                draft_total_cost: draftTotalCost,
                draft_status: drafts[0]?.status || "",
                first_page_id: detail.pages[0]?.id ?? 0,
                job: detail.job ?? null,
              }
            : null;
        const baseItem = currentItem ?? selectedItem ?? detailItem;
        if (!baseItem) {
          return current;
        }

        const optimisticItem: ReceiptQueueItem = {
          ...baseItem,
          ocr_status: nextStatus,
          review_status: options?.reviewStatus ?? baseItem.review_status,
          draft_status: options?.draftStatus ?? baseItem.draft_status,
          job: options && "job" in options ? options.job : baseItem.job,
          updated_at: updatedAt,
        };
        return [
          optimisticItem,
          ...current.filter((item) => item.id !== receiptId && (!nextStatus || item.ocr_status === nextStatus)),
        ];
      });

      setDetail((current) => {
        if (!current || current.scan.id !== receiptId) return current;
        return {
          ...current,
          scan: {
            ...current.scan,
            ocr_status: nextStatus,
            review_status: options?.reviewStatus ?? current.scan.review_status,
            updated_at: updatedAt,
          },
          job: options && "job" in options ? options.job : current.job,
        };
      });

      if (options?.draftStatus) {
        setDrafts((current) => current.map((draft) => ({ ...draft, status: options.draftStatus || draft.status })));
      }
    },
    [detail, drafts, selectedQueueItem, statusFilter]
  );

  const pollRunAllJob = useCallback(
    async (jobId: number) => {
      if (selectedShopId === null || !jobId) return;
      if (runAllPollingJobIdRef.current === jobId) return;
      runAllPollingJobIdRef.current = jobId;
      setRunAllJobId(jobId);
      setRunningAllUploads(true);

      try {
        while (runAllPollingJobIdRef.current === jobId) {
          const data = await postScanJson<ReceiptRunAllResponse>("/receipt_run_all_status", {
            shop_id: selectedShopId,
            job_id: jobId,
          });
          const job = data.job ?? null;
          if (!job) {
            throw new Error("Run all batch job was not found.");
          }

          setStatusMsg(runAllJobStatusMessage(job));
          await loadQueue(selectedReceiptIdRef.current, {
            silent: job.status === "started",
            ocrStatus: statusFilterRef.current,
            preserveMissingSelection: true,
          });

          if (job.status !== "started") {
            break;
          }
          await waitFor(2500);
        }
      } catch (err) {
        const msg = err instanceof Error ? err.message : "Failed to poll Run all";
        setDetailError(msg);
        setStatusMsg(null);
      } finally {
        if (runAllPollingJobIdRef.current === jobId) {
          runAllPollingJobIdRef.current = null;
        }
        setRunningAllUploads(false);
      }
    },
    [loadQueue, selectedShopId]
  );

  const handleRunAllUploaded = useCallback(async () => {
    if (selectedShopId === null || runningAllUploads) return;
    if (uploadedReceiptCount <= 0) {
      setStatusMsg("Run all: no uploaded receipts to process.");
      return;
    }

    const confirmed = window.confirm(
      `Run all will process ${uploadedReceiptCount} uploaded receipt(s) for ${selectedShopLabel} and auto-post clean receipts to production. Continue?`
    );
    if (!confirmed) return;

    setRunningAllUploads(true);
    setDetailError(null);
    setQueueError(null);
    setStatusMsg("Run all: starting backend batch…");
    selectedReceiptIdRef.current = null;
    selectedPageIdRef.current = null;
    setSelectedReceiptId(null);
    setSelectedPageId(null);
    setMobileInboxOpen(false);
    setMobileCommandOpen(false);

    try {
      const data = await postScanJson<ReceiptRunAllResponse>("/receipt_run_all_uploaded", {
        shop_id: selectedShopId,
      });
      const jobId = Number(data.job_id || data.job?.id || 0);
      if (!jobId) {
        setStatusMsg(data.message || "Run all: no uploaded receipts to process.");
        await loadQueue(null, { ocrStatus: statusFilterRef.current, fallbackToFirst: false });
        return;
      }
      setRunAllJobId(jobId);
      setStatusMsg(data.job ? runAllJobStatusMessage(data.job) : data.message || "Run all batch started.");
      void pollRunAllJob(jobId);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to start Run all";
      setDetailError(msg);
      setStatusMsg(null);
      setRunningAllUploads(false);
    }
  }, [loadQueue, pollRunAllJob, runningAllUploads, selectedShopId, selectedShopLabel, uploadedReceiptCount]);

  useEffect(() => {
    if (selectedShopId === null) return;
    let cancelled = false;

    const resumeActiveRunAll = async () => {
      try {
        const data = await postScanJson<ReceiptRunAllResponse>("/receipt_run_all_status", {
          shop_id: selectedShopId,
        });
        const job = data.job ?? null;
        if (cancelled || !job || job.status !== "started") return;
        setRunAllJobId(job.id);
        setRunningAllUploads(true);
        setStatusMsg(runAllJobStatusMessage(job));
        void pollRunAllJob(job.id);
      } catch {
        // Active batch discovery is best-effort; normal queue loading still works.
      }
    };

    void resumeActiveRunAll();
    return () => {
      cancelled = true;
    };
  }, [pollRunAllJob, selectedShopId]);

  const handleRunOcr = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;
    const previousStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? statusFilter;
    const previousReviewStatus = detail?.scan.review_status ?? selectedQueueItem?.review_status;
    const previousDraftStatus = drafts[0]?.status ?? selectedQueueItem?.draft_status;
    const previousJob = detail?.job ?? selectedQueueItem?.job ?? null;
    const startedAt = new Date().toISOString();
    const initialOptimisticJob = buildOptimisticReceiptJob(
      undefined,
      startedAt,
      detail?.pages.length || detail?.scan.page_count || selectedQueueItem?.page_count || 0,
      drafts.length || selectedQueueItem?.draft_count || 0,
      detail?.job ?? selectedQueueItem?.job ?? null
    );
    jumpReceiptToStatus(selectedReceiptId, "processing", { job: initialOptimisticJob });
    setRunningOcr(true);
    setStatusMsg(`Running OCR for receipt #${selectedReceiptId}…`);
    setDetailError(null);
    try {
      const res = await apiFetch("/receipt_run_ocr", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_id: selectedShopId, ocr_id: selectedReceiptId }),
      });
      const data = (await res.json()) as ReceiptRunOcrResponse;
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const optimisticJob = buildOptimisticReceiptJob(
        data.job_id,
        startedAt,
        detail?.pages.length || detail?.scan.page_count || selectedQueueItem?.page_count || 0,
        drafts.length || selectedQueueItem?.draft_count || 0,
        detail?.job ?? selectedQueueItem?.job ?? null
      );
      jumpReceiptToStatus(selectedReceiptId, "processing", { job: optimisticJob });
      setStatusMsg(data.message || `OCR started for receipt #${selectedReceiptId}.`);
      void loadQueue(selectedReceiptId, { silent: true, ocrStatus: "processing", preserveMissingSelection: true });
      void loadDetail(selectedReceiptId, { silent: true });
    } catch (err) {
      if (previousStatus) {
        jumpReceiptToStatus(selectedReceiptId, previousStatus, {
          reviewStatus: previousReviewStatus,
          draftStatus: previousDraftStatus,
          job: previousJob,
        });
      }
      const msg = err instanceof Error ? err.message : "Failed to run OCR";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setRunningOcr(false);
    }
  }, [detail, drafts, jumpReceiptToStatus, loadDetail, loadQueue, selectedQueueItem, selectedReceiptId, selectedShopId, statusFilter]);

  const handleReprocessReceipt = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;
    const previousStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? statusFilter;
    const previousReviewStatus = detail?.scan.review_status ?? selectedQueueItem?.review_status;
    const previousDraftStatus = drafts[0]?.status ?? selectedQueueItem?.draft_status;
    const previousJob = detail?.job ?? selectedQueueItem?.job ?? null;
    const startedAt = new Date().toISOString();
    const initialOptimisticJob = buildOptimisticReceiptJob(
      undefined,
      startedAt,
      detail?.pages.length || detail?.scan.page_count || selectedQueueItem?.page_count || 0,
      drafts.length || selectedQueueItem?.draft_count || 0,
      detail?.job ?? selectedQueueItem?.job ?? null
    );
    jumpReceiptToStatus(selectedReceiptId, "processing", { job: initialOptimisticJob });
    setReprocessingReceipt(true);
    setStatusMsg(`Reprocessing receipt #${selectedReceiptId}…`);
    setDetailError(null);
    try {
      const res = await apiFetch("/receipt_reprocess", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_id: selectedShopId, ocr_id: selectedReceiptId }),
      });
      const data = (await res.json()) as ReceiptRunOcrResponse;
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const optimisticJob = buildOptimisticReceiptJob(
        data.job_id,
        startedAt,
        detail?.pages.length || detail?.scan.page_count || selectedQueueItem?.page_count || 0,
        drafts.length || selectedQueueItem?.draft_count || 0,
        detail?.job ?? selectedQueueItem?.job ?? null
      );
      jumpReceiptToStatus(selectedReceiptId, "processing", { job: optimisticJob });
      setStatusMsg(data.message || `Reprocessing started for receipt #${selectedReceiptId}.`);
      void loadQueue(selectedReceiptId, { silent: true, ocrStatus: "processing", preserveMissingSelection: true });
      void loadDetail(selectedReceiptId, { silent: true });
    } catch (err) {
      if (previousStatus) {
        jumpReceiptToStatus(selectedReceiptId, previousStatus, {
          reviewStatus: previousReviewStatus,
          draftStatus: previousDraftStatus,
          job: previousJob,
        });
      }
      const msg = err instanceof Error ? err.message : "Failed to reprocess receipt";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setReprocessingReceipt(false);
    }
  }, [detail, drafts, jumpReceiptToStatus, loadDetail, loadQueue, selectedQueueItem, selectedReceiptId, selectedShopId, statusFilter]);

  const handleSaveDrafts = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null || isReceiptProcessing || !hasUnsavedDraftChanges) return;
    setSavingDrafts(true);
    setStatusMsg(`Saving drafts for receipt #${selectedReceiptId}…`);
    setDetailError(null);
    try {
      const res = await apiFetch("/receipt_save_draft", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          ocr_id: selectedReceiptId,
          drafts,
          review_note: reviewNote,
          reviewed_by: reviewedBy,
        }),
      });
      const data = (await res.json()) as { draft_count?: number; ocr_status?: string; error?: string };
      if (!res.ok || data.error) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const nextHint =
        data.ocr_status === "extracted"
          ? "Next: approve, then post."
          : "Draft still needs review before approval.";
      setStatusMsg(`Saved ${data.draft_count ?? drafts.length} draft receipt(s). ${nextHint}`);
      const nextQueueStatus =
        data.ocr_status === "extracted" || data.ocr_status === "needs_review"
          ? data.ocr_status
          : statusFilter;
      jumpReceiptToStatus(selectedReceiptId, nextQueueStatus, {
        reviewStatus: nextQueueStatus === "extracted" ? "reviewed" : undefined,
        draftStatus: nextQueueStatus === "extracted" ? "ready" : "needs_review",
      });
      const nextSelectedReceiptId = await loadQueue(selectedReceiptId, {
        ocrStatus: nextQueueStatus,
        fallbackToFirst: false,
        preserveMissingSelection: true,
      });
      if (nextSelectedReceiptId !== null && nextSelectedReceiptId !== undefined) {
        await loadDetail(nextSelectedReceiptId);
      } else {
        await loadDetail(selectedReceiptId);
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to save drafts";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setSavingDrafts(false);
    }
  }, [drafts, hasUnsavedDraftChanges, isReceiptProcessing, jumpReceiptToStatus, loadDetail, loadQueue, reviewNote, reviewedBy, selectedReceiptId, selectedShopId, statusFilter]);

  const handleApproveReceipt = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;
    const previousStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? statusFilter;
    const previousReviewStatus = detail?.scan.review_status ?? selectedQueueItem?.review_status;
    const previousDraftStatus = drafts[0]?.status ?? selectedQueueItem?.draft_status;
    const previousJob = detail?.job ?? selectedQueueItem?.job ?? null;
    jumpReceiptToStatus(selectedReceiptId, "approved", { reviewStatus: "approved", draftStatus: "approved" });
    setApprovingReceipt(true);
    setStatusMsg(`Approving receipt #${selectedReceiptId}…`);
    setDetailError(null);
    try {
      const res = await apiFetch("/receipt_approve", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          ocr_id: selectedReceiptId,
          approved_by: reviewedBy,
          review_note: reviewNote,
        }),
      });
      const data = (await res.json()) as ReceiptActionResponse;
      if (!res.ok || data.error || !data.approved) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const nextQueueStatus = data.already_posted ? "posted" : "approved";
      jumpReceiptToStatus(selectedReceiptId, nextQueueStatus, {
        reviewStatus: "approved",
        draftStatus: nextQueueStatus,
      });
      setStatusMsg(
        data.already_posted
          ? `Receipt #${selectedReceiptId} was already posted.`
          : `Approved ${data.draft_count ?? drafts.length} draft receipt(s). Next: post to final purchase tables.`
      );
      const nextSelectedReceiptId = await loadQueue(selectedReceiptId, {
        ocrStatus: nextQueueStatus,
        fallbackToFirst: false,
        preserveMissingSelection: true,
      });
      if (nextSelectedReceiptId !== null && nextSelectedReceiptId !== undefined) {
        await loadDetail(nextSelectedReceiptId);
      } else {
        await loadDetail(selectedReceiptId);
      }
    } catch (err) {
      if (previousStatus) {
        jumpReceiptToStatus(selectedReceiptId, previousStatus, {
          reviewStatus: previousReviewStatus,
          draftStatus: previousDraftStatus,
          job: previousJob,
        });
      }
      const msg = err instanceof Error ? err.message : "Failed to approve receipt";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setApprovingReceipt(false);
    }
  }, [detail, drafts, jumpReceiptToStatus, loadDetail, loadQueue, reviewNote, reviewedBy, selectedQueueItem, selectedReceiptId, selectedShopId, statusFilter]);

  const handlePostReceipt = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;
    const previousStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? statusFilter;
    const previousReviewStatus = detail?.scan.review_status ?? selectedQueueItem?.review_status;
    const previousDraftStatus = drafts[0]?.status ?? selectedQueueItem?.draft_status;
    const previousJob = detail?.job ?? selectedQueueItem?.job ?? null;
    jumpReceiptToStatus(selectedReceiptId, "posted", { reviewStatus: "approved", draftStatus: "posted" });
    setPostingReceipt(true);
    setStatusMsg(`Posting receipt #${selectedReceiptId}…`);
    setDetailError(null);
    try {
      const res = await apiFetch("/receipt_post", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          ocr_id: selectedReceiptId,
          posted_by: reviewedBy,
        }),
      });
      const data = (await res.json()) as ReceiptActionResponse;
      if (!res.ok || data.error || !data.posted) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      jumpReceiptToStatus(selectedReceiptId, "posted", { reviewStatus: "approved", draftStatus: "posted" });
      setStatusMsg(`Posted ${data.order_count ?? 0} purchase order(s) and ${data.item_count ?? 0} item line(s) to final tables.`);
      const nextSelectedReceiptId = await loadQueue(selectedReceiptId, {
        ocrStatus: "posted",
        fallbackToFirst: false,
        preserveMissingSelection: true,
      });
      if (nextSelectedReceiptId !== null && nextSelectedReceiptId !== undefined) {
        await loadDetail(nextSelectedReceiptId);
      } else {
        await loadDetail(selectedReceiptId);
      }
    } catch (err) {
      if (previousStatus) {
        jumpReceiptToStatus(selectedReceiptId, previousStatus, {
          reviewStatus: previousReviewStatus,
          draftStatus: previousDraftStatus,
          job: previousJob,
        });
      }
      const msg = err instanceof Error ? err.message : "Failed to post receipt";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setPostingReceipt(false);
    }
  }, [detail, drafts, jumpReceiptToStatus, loadDetail, loadQueue, reviewedBy, selectedQueueItem, selectedReceiptId, selectedShopId, statusFilter]);

  const handleDeleteReceipt = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;

    const receiptLabel = selectedQueueItem?.source_file_name || `receipt #${selectedReceiptId}`;
    if (!window.confirm(`Delete ${receiptLabel}?\n\nThis removes the uploaded receipt, OCR pages, drafts, and tracker job rows.`)) {
      return;
    }

    setDeletingReceipt(true);
    setDetailError(null);
    setStatusMsg(`Deleting ${receiptLabel}…`);
    try {
      const res = await apiFetch("/receipt_delete", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_id: selectedShopId, ocr_id: selectedReceiptId }),
      });
      const data = (await res.json()) as ReceiptDeleteResponse;
      if (!res.ok || data.error || !data.deleted) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      setDetail(null);
      setDrafts([]);
      setSavedDraftSignature(draftSaveSignature([], "", ""));
      setSelectedDraftIndex(null);
      setSelectedPageId(null);
      setPageImage(null);
      setStatusMsg(`Deleted ${data.source_file_name || receiptLabel}.`);
      await loadQueue();
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to delete receipt";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setDeletingReceipt(false);
    }
  }, [loadQueue, selectedQueueItem?.source_file_name, selectedReceiptId, selectedShopId]);

  const handleDeletePage = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null || selectedPageId === null) return;

    const selectedPage = detail?.pages.find((page) => page.id === selectedPageId) ?? null;
    const pageLabel = selectedPage ? `Page ${selectedPage.page_no}` : "this page";
    const receiptLabel = selectedQueueItem?.source_file_name || `receipt #${selectedReceiptId}`;
    if (!window.confirm(`Delete ${pageLabel} from ${receiptLabel}?\n\nThis removes the selected OCR page and its draft. Remaining pages will be renumbered. Unsaved draft edits on this receipt will be discarded and reloaded from the server.`)) {
      return;
    }

    setDeletingPage(true);
    setDetailError(null);
    setStatusMsg(`Deleting ${pageLabel} from ${receiptLabel}…`);
    try {
      const res = await apiFetch("/receipt_delete_page", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ shop_id: selectedShopId, ocr_id: selectedReceiptId, page_id: selectedPageId }),
      });
      const data = (await res.json()) as ReceiptDeleteResponse;
      if (!res.ok || data.error || !data.deleted) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }

      if (data.deleted_receipt) {
        setDetail(null);
        setDrafts([]);
        setSavedDraftSignature(draftSaveSignature([], "", ""));
        setSelectedDraftIndex(null);
        setSelectedPageId(null);
        setPageImage(null);
        setStatusMsg(`Deleted ${pageLabel}. ${data.source_file_name || receiptLabel} is now empty and was removed.`);
        await loadQueue();
        return;
      }

      setStatusMsg(`Deleted ${pageLabel}. ${data.remaining_pages ?? 0} page(s) remain.`);
      await loadQueue(selectedReceiptId);
      await loadDetail(selectedReceiptId);
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to delete page";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setDeletingPage(false);
    }
  }, [detail?.pages, loadDetail, loadQueue, selectedPageId, selectedQueueItem?.source_file_name, selectedReceiptId, selectedShopId]);

  const handleReopenReceipt = useCallback(async () => {
    if (selectedShopId === null || selectedReceiptId === null) return;

    const receiptLabel = selectedQueueItem?.source_file_name || `receipt #${selectedReceiptId}`;
    if (!window.confirm(`Reopen posted receipt ${receiptLabel}?\n\nThis removes the final posted purchase rows for this receipt and returns it to review mode.`)) {
      return;
    }

    const previousStatus = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? statusFilter;
    const previousReviewStatus = detail?.scan.review_status ?? selectedQueueItem?.review_status;
    const previousDraftStatus = drafts[0]?.status ?? selectedQueueItem?.draft_status;
    const previousJob = detail?.job ?? selectedQueueItem?.job ?? null;
    jumpReceiptToStatus(selectedReceiptId, "needs_review", { reviewStatus: "pending", draftStatus: "needs_review" });
    setReopeningReceipt(true);
    setDetailError(null);
    setStatusMsg(`Reopening posted receipt #${selectedReceiptId}…`);
    try {
      const res = await apiFetch("/receipt_reopen", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          shop_id: selectedShopId,
          ocr_id: selectedReceiptId,
          reopened_by: reviewedBy,
          review_note: reviewNote,
        }),
      });
      const data = (await res.json()) as ReceiptActionResponse & {
        removed_orders?: number;
        removed_items?: number;
        message?: string;
      };
      if (!res.ok || data.error || !data.reopened) {
        throw new Error(data.error || `HTTP ${res.status}`);
      }
      const nextQueueStatus = data.ocr_status || "needs_review";
      jumpReceiptToStatus(selectedReceiptId, nextQueueStatus, {
        reviewStatus: "reviewed",
        draftStatus: nextQueueStatus === "extracted" ? "ready" : "needs_review",
      });
      setStatusMsg(
        data.message ||
          `Reopened receipt #${selectedReceiptId}. Removed ${data.removed_orders ?? 0} order(s) and ${data.removed_items ?? 0} item line(s) from final tables.`
      );
      const nextSelectedReceiptId = await loadQueue(selectedReceiptId, {
        ocrStatus: nextQueueStatus,
        fallbackToFirst: false,
        preserveMissingSelection: true,
      });
      if (nextSelectedReceiptId !== null && nextSelectedReceiptId !== undefined) {
        await loadDetail(nextSelectedReceiptId);
      } else {
        await loadDetail(selectedReceiptId);
      }
    } catch (err) {
      if (previousStatus) {
        jumpReceiptToStatus(selectedReceiptId, previousStatus, {
          reviewStatus: previousReviewStatus,
          draftStatus: previousDraftStatus,
          job: previousJob,
        });
      }
      const msg = err instanceof Error ? err.message : "Failed to reopen posted receipt";
      setDetailError(msg);
      setStatusMsg(null);
    } finally {
      setReopeningReceipt(false);
    }
  }, [detail, drafts, jumpReceiptToStatus, loadDetail, loadQueue, reviewNote, reviewedBy, selectedQueueItem, selectedReceiptId, selectedShopId, statusFilter]);

  const updateSupplierField = useCallback((draftIndex: number, field: keyof ReceiptDraft["supplier"], value: string) => {
    setDrafts((current) =>
      current.map((draft, index) =>
        index === draftIndex ? { ...draft, supplier: { ...draft.supplier, [field]: value } } : draft
      )
    );
  }, []);

  const updateOrderField = useCallback(
    (draftIndex: number, field: keyof ReceiptDraft["purchase_order"], value: string) => {
      setDrafts((current) =>
        current.map((draft, index) =>
          index === draftIndex
            ? {
                ...draft,
                purchase_order: {
                  ...draft.purchase_order,
                  [field]:
                    field === "invoice_id" || field === "purchase_date" || field === "line_total_basis"
                      ? value
                      : toNumber(value),
                  ...(field === "grand_total" ? { total_cost: toNumber(value) } : {}),
                },
              }
            : draft
        )
      );
    },
    []
  );

  const updateItemField = useCallback(
    (draftIndex: number, itemIndex: number, field: keyof ReceiptDraftItem, value: string) => {
      setDrafts((current) =>
        current.map((draft, index) => {
          if (index !== draftIndex) return draft;
          return {
            ...draft,
            purchase_items: draft.purchase_items.map((item, idx) =>
              idx === itemIndex
                ? {
                    ...item,
                    ...(field === "name" || field === "category" ? { match_product_id: null } : {}),
                    ...(field === "line_discount_percent" ? { line_discount_amount: 0 } : {}),
                    [field]:
                      field === "name" || field === "category"
                        ? value
                        : field === "line_no"
                        ? toNumber(value)
                        : toNumber(value),
                  }
                : item
            ),
          };
        })
      );
    },
    []
  );

  const addDraft = useCallback(() => {
    setDrafts((current) => [...current, emptyDraft(current.length)]);
  }, []);

  const addItem = useCallback((draftIndex: number) => {
    setDrafts((current) =>
      current.map((draft, index) =>
        index === draftIndex
          ? {
              ...draft,
              purchase_items: [
                ...draft.purchase_items,
                {
                  line_no: draft.purchase_items.length + 1,
                  name: "",
                  category: "Others",
                  quantity: 0,
                  unit_price: 0,
                  line_discount_percent: 0,
                  line_discount_amount: 0,
                  line_subtotal_amount: 0,
                  line_tax_amount: 0,
                  total_price: 0,
                  match_product_id: null,
                  historical_matches: [],
                  validation_errors: [],
                  validation_warnings: [],
                },
              ],
            }
          : draft
      )
    );
  }, []);

  const updateItemName = useCallback(
    (draftIndex: number, itemIndex: number, value: string) => {
      setDrafts((current) =>
        current.map((draft, index) => {
          if (index !== draftIndex) return draft;
          return {
            ...draft,
            purchase_items: draft.purchase_items.map((item, idx) => {
              if (idx !== itemIndex) return item;
              const trimmedValue = value.trim();
              const selectedMatch = (item.historical_matches || []).find(
                (entry) => entry.name.trim().toLowerCase() === trimmedValue.toLowerCase()
              );
              if (!selectedMatch) {
                return {
                  ...item,
                  name: value,
                  match_product_id: null,
                };
              }
              const nextCategory =
                selectedMatch.category && categoryOptions.includes(selectedMatch.category)
                  ? selectedMatch.category
                  : item.category || "Others";
              return {
                ...item,
                name: selectedMatch.name || value,
                category: nextCategory,
                match_product_id: selectedMatch.id,
              };
            }),
          };
        })
      );
    },
    [categoryOptions]
  );

  const removeItem = useCallback((draftIndex: number, itemIndex: number) => {
    setDrafts((current) =>
      current.map((draft, index) => {
        if (index !== draftIndex) return draft;
        const nextItems = draft.purchase_items.filter((_, idx) => idx !== itemIndex).map((item, idx) => ({ ...item, line_no: idx + 1 }));
        return { ...draft, purchase_items: nextItems };
      })
    );
  }, []);

  const queueStatusLabel = queueError || detailError || (isReceiptProcessing ? liveProgressLabel : null) || statusMsg || liveProgressLabel;
  const selectedProgress = summarizeJobProgress(activeJob);
  const selectedPageCount = detail?.scan.page_count || selectedQueueItem?.page_count || activeJob?.total_pages || 0;
  const selectedReceiptStatusLabel = detail?.scan.ocr_status ?? selectedQueueItem?.ocr_status ?? "";
  const selectedReceiptCodePrefix = detail?.scan.receipt_code_prefix ?? selectedQueueItem?.receipt_code_prefix ?? "";
  const activeScanActionLabel =
    uploading
      ? uploadDrafts.length > 1
        ? `Uploading ${uploadDrafts.length} receipts`
        : "Uploading receipt"
      : runningAllUploads
        ? runAllJobId
          ? `Running all uploaded receipts #${runAllJobId}`
          : "Running all uploaded receipts"
        : processingUpload
          ? uploadProcessJobId
            ? `Processing uploaded receipt #${uploadProcessJobId}`
            : "Processing uploaded receipt"
        : runningOcr
          ? "Starting OCR"
          : reprocessingReceipt
            ? "Reprocessing receipt"
            : savingDrafts
              ? "Saving drafts"
              : approvingReceipt
                ? "Approving receipt"
                : postingReceipt
                  ? "Posting receipt"
                  : reopeningReceipt
                    ? "Reopening posted receipt"
                    : deletingPage
                      ? "Deleting page"
                      : deletingReceipt
                        ? "Deleting receipt"
                        : "";
  const isScanActionBusy = Boolean(activeScanActionLabel);
  const scanBusyNotice = activeScanActionLabel
    ? {
        title: `${activeScanActionLabel} is running`,
        detail: "Please wait. On phone screens the app may pause until this action finishes.",
      }
    : isReceiptProcessing
      ? {
          title: liveProgressLabel || "OCR is processing",
          detail: "This can take a while. Keep this receipt open; it will update automatically when finished.",
        }
      : null;
  const scanCommandPanelOpen = mobileCommandOpen || Boolean(scanBusyNotice);
  const showRunAllUploaded = statusFilter === "uploaded" && !selectedReceiptId && (runningAllUploads || uploadedReceiptCount > 0);
  const canRunAllUploaded =
    selectedShopId !== null &&
    showRunAllUploaded &&
    !runningAllUploads &&
    !isScanActionBusy &&
    uploadedReceiptCount > 0;
  const canRunInitialOcr =
    !!selectedReceiptId &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    (selectedReceiptStatusLabel === "uploaded" || selectedReceiptStatusLabel === "failed" || selectedReceiptStatusLabel === "rejected");
  const canReprocessReceipt =
    !!selectedReceiptId &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    selectedReceiptStatusLabel !== "posted" &&
    selectedReceiptStatusLabel !== "uploaded";
  const canReopenReceipt =
    !!selectedReceiptId &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    selectedReceiptStatusLabel === "posted";
  const canApproveReceipt =
    !!selectedReceiptId &&
    drafts.length > 0 &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    selectedReceiptStatusLabel !== "approved" &&
    selectedReceiptStatusLabel !== "posted" &&
    selectedReceiptStatusLabel !== "failed";
  const canPostReceipt =
    !!selectedReceiptId &&
    drafts.length > 0 &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    selectedReceiptStatusLabel === "approved";
  const canSaveDrafts =
    !!selectedReceiptId &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    hasUnsavedDraftChanges;
  const canDeletePage =
    !!selectedReceiptId &&
    !!selectedPageId &&
    !isScanActionBusy &&
    !isReceiptProcessing &&
    selectedReceiptStatusLabel !== "posted";
  useEffect(() => {
    setMoreActionsOpen(false);
  }, [isScanActionBusy, selectedReceiptId, selectedReceiptStatusLabel]);

  const primaryScanAction =
    showRunAllUploaded
      ? {
          label: runningAllUploads ? "Running all…" : "Run all",
          onClick: handleRunAllUploaded,
          disabled: runningAllUploads || !canRunAllUploaded,
          title: undefined,
        }
      : selectedReceiptStatusLabel === "uploaded" || selectedReceiptStatusLabel === "failed" || selectedReceiptStatusLabel === "rejected"
      ? {
          label: runningOcr ? "OCR…" : "Run OCR",
          onClick: handleRunOcr,
          disabled: runningOcr || !canRunInitialOcr,
          title: undefined,
        }
      : canSaveDrafts || savingDrafts
        ? {
            label: savingDrafts ? "Saving…" : "Save drafts",
            onClick: handleSaveDrafts,
            disabled: savingDrafts || !canSaveDrafts,
            title: selectedReceiptId && !hasUnsavedDraftChanges ? "No draft changes to save" : undefined,
          }
        : selectedReceiptStatusLabel === "extracted" && (canApproveReceipt || approvingReceipt)
          ? {
              label: approvingReceipt ? "Approving…" : "Approve",
              onClick: handleApproveReceipt,
              disabled: approvingReceipt || !canApproveReceipt,
              title: undefined,
            }
          : selectedReceiptStatusLabel === "approved" && (canPostReceipt || postingReceipt)
            ? {
                label: postingReceipt ? "Posting…" : "Post",
                onClick: handlePostReceipt,
                disabled: postingReceipt || !canPostReceipt,
                title: undefined,
              }
            : null;
  const moreScanActions = [
    {
      label: reprocessingReceipt ? "Reprocessing…" : "Reprocess",
      onClick: handleReprocessReceipt,
      disabled: reprocessingReceipt || !canReprocessReceipt,
      danger: false,
      visible: Boolean(
        selectedReceiptId &&
          selectedReceiptStatusLabel !== "uploaded" &&
          selectedReceiptStatusLabel !== "failed" &&
          selectedReceiptStatusLabel !== "rejected" &&
          selectedReceiptStatusLabel !== "posted"
      ),
    },
    {
      label: "Add draft",
      onClick: addDraft,
      disabled: !selectedReceiptId || isScanActionBusy || isReceiptProcessing,
      danger: false,
      visible: Boolean(selectedReceiptId && (selectedReceiptStatusLabel === "needs_review" || selectedReceiptStatusLabel === "extracted")),
    },
    {
      label: reopeningReceipt ? "Reopening…" : "Reopen posted",
      onClick: handleReopenReceipt,
      disabled: reopeningReceipt || !canReopenReceipt,
      danger: false,
      visible: selectedReceiptStatusLabel === "posted",
    },
    {
      label: deletingReceipt ? "Deleting…" : "Delete receipt",
      onClick: handleDeleteReceipt,
      disabled: isScanActionBusy || isReceiptProcessing || !selectedReceiptId || selectedReceiptStatusLabel === "posted",
      danger: true,
      visible: Boolean(selectedReceiptId && selectedReceiptStatusLabel !== "posted"),
    },
  ].filter((action) => action.visible);
  const previewPlaceholder =
    activeJob?.stage === "render" || (detail?.scan.ocr_status === "processing" && !detail?.pages.length)
      ? "Rendering PDF pages for previews…"
      : "Run OCR to generate page previews for PDFs.";
  const selectedReceiptLabel =
    selectedReceiptCodePrefix ||
    detail?.scan.source_file_name ||
    selectedQueueItem?.source_file_name ||
    (selectedReceiptId ? `receipt #${selectedReceiptId}` : "receipt");
  const canSelectShop = !!onSelectShop && shops.length > 0;
  const shopFooter = (
    <div className="scan-shop-selector">
      {shopPickerOpen && canSelectShop && (
        <div className="scan-shop-picker" role="listbox" aria-label="Select shop">
          {shops.map((shop) => (
            <button
              key={shop.shop_id}
              type="button"
              className={"node-item" + (shop.shop_id === selectedShopId ? " node-item--active" : "")}
              onClick={() => {
                onSelectShop?.(shop.shop_id);
                setShopPickerOpen(false);
              }}
            >
              <div className="node-item-title">{shop.name || `Shop ${shop.shop_id}`}</div>
              <div className="node-item-meta">
                id {shop.shop_id}
                {shop.pos?.dbname ? ` · POS ${shop.pos.dbname}` : ""}
                {shop.expense?.dbname ? ` · EXP ${shop.expense.dbname}` : ""}
              </div>
            </button>
          ))}
        </div>
      )}
      <button
        type="button"
        className="sidebar-footer sidebar-footer--button"
        onClick={() => setShopPickerOpen((open) => !open)}
        disabled={!canSelectShop}
        aria-expanded={shopPickerOpen}
        aria-label="Select shop"
      >
        <span className="sidebar-footer-label">Shop</span>
        <span className="sidebar-footer-value">{selectedShopLabel}</span>
      </button>
    </div>
  );

  const scanSidebarBody = (
    <>
      <div className="sidebar-section-label">Receipt inbox</div>
      <div style={{ display: "grid", gap: 10 }}>
        <div className="scan-file-actions">
          <label className="secondary-button scan-file-button">
            Choose Files
            <input
              key={`files-${fileInputResetKey}`}
              type="file"
              accept=".pdf,image/*"
              multiple
              onChange={handleFilePick}
            />
          </label>
          <label className="secondary-button scan-file-button">
            Take Photo
            <input
              key={`photo-${fileInputResetKey}`}
              type="file"
              accept="image/*"
              capture="environment"
              onChange={handleFilePick}
            />
          </label>
        </div>
        {uploadDrafts.length > 0 && (
          <div className="node-item-meta">
            Selected {uploadDrafts.length} file{uploadDrafts.length === 1 ? "" : "s"}:{" "}
            {uploadDrafts.slice(0, 3).map((draft) => ensureUploadExtension(draft.uploadName, draft.file)).join(", ")}
            {uploadDrafts.length > 3 ? `, +${uploadDrafts.length - 3} more` : ""}
          </div>
        )}
        <button
          type="button"
          className="submit-button"
          onClick={() => setUploadRenameOpen(true)}
          disabled={uploading || processingUpload || !uploadDrafts.length}
        >
          {uploading || processingUpload ? "Uploading…" : uploadDrafts.length > 1 ? `Review ${uploadDrafts.length} uploads` : "Review upload"}
        </button>
        <div className="scan-guidance-note">
          Choose one or more receipt files. Keep one logical receipt per file.
          <br />
          If one receipt spans several pages or photos, stitch them vertically into one long image before upload.
          <br />
          Use multi-page PDFs only when each page is a separate receipt.
        </div>
        <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
          {FILTERS.map((filter) => (
            <button
              key={filter.value || "all"}
              type="button"
              className={"secondary-button scan-filter-tab" + (statusFilter === filter.value ? " is-active" : "")}
              onClick={() => {
                statusFilterRef.current = filter.value;
                setStatusFilter(filter.value);
              }}
              disabled={queueLoading}
            >
              {filter.label} {queueCounts[filter.value || ""] ? `(${queueCounts[filter.value || ""]})` : ""}
            </button>
          ))}
        </div>
        <button type="button" className="secondary-button action-button" onClick={() => void loadQueue(selectedReceiptId)} disabled={queueLoading}>
          {queueLoading ? "Refreshing…" : "Refresh queue"}
        </button>
      </div>

      <div style={{ display: "grid", gap: 10, marginTop: 16, overflowY: "auto" }}>
        {queueLoading && <div className="history-empty">Loading receipt queue…</div>}
        {!queueLoading && !queue.length && <div className="history-empty">No receipts in this queue yet.</div>}
        {queue.map((item) => (
          <button
            key={item.id}
            type="button"
            className={"node-item" + (selectedReceiptId === item.id ? " node-item--active" : "")}
            onClick={() => {
              if (selectedReceiptId === item.id) {
                selectedReceiptIdRef.current = null;
                selectedPageIdRef.current = null;
                setSelectedReceiptId(null);
                setMobileInboxOpen(false);
                return;
              }
              setSelectedReceiptId(item.id);
              setMobileInboxOpen(false);
            }}
          >
            <div className="node-item-title">{item.receipt_code_prefix || `#${item.id}`} · {item.source_file_name || "receipt"}</div>
            <div className="node-item-meta">
              {item.ocr_status}
              {item.job ? ` · ${summarizeJobProgress(item.job)}` : ""}
              {" · "}drafts {item.draft_count}
              {" · "}pages {item.page_count || item.job?.total_pages || 0}
            </div>
          </button>
        ))}
        {!queueLoading && selectedReceiptId !== null && !selectedQueueItem && detail && (
          <div className="history-empty">{selectedReceiptLabel} is outside the current filter.</div>
        )}
      </div>

      {shopFooter}
    </>
  );

  if (selectedShopId === null) {
    return (
      <>
        <aside className="sidebar scan-sidebar">
          <header className="sidebar-header">
            <div className="app-logo">🔥</div>
            <div className="app-title">
              <div className="app-name">flamestalker</div>
              <div className="app-subtitle">Receipt workflow</div>
            </div>
          </header>
          {shopFooter}
        </aside>
        <main className="main scan-main">
          <button
            type="button"
            className="mobile-sidebar-toggle hideable-toggle"
            onClick={() => setMobileInboxOpen((open) => !open)}
            aria-expanded={mobileInboxOpen}
          >
            {mobileInboxOpen ? "Hide Receipt Inbox" : "Show Receipt Inbox"}
          </button>
          {mobileInboxOpen ? (
            <section className="mobile-inline-panel scan-mobile-panel">
              {shopFooter}
            </section>
          ) : null}
          <header className="main-header">
            <div className="main-header-title">
              <span className="pill">Review</span>
              <h1>Scan view</h1>
            </div>
            <div className="main-header-subtitle">Select a shop first.</div>
          </header>
        </main>
      </>
    );
  }

  return (
    <>
      <aside className="sidebar scan-sidebar">
        <header className="sidebar-header">
          <div className="app-logo">🔥</div>
          <div className="app-title">
            <div className="app-name">flamestalker</div>
            <div className="app-subtitle">Receipt workflow</div>
          </div>
        </header>
        {scanSidebarBody}
      </aside>

      <main className="main scan-main">
        <button
          type="button"
          className="mobile-sidebar-toggle hideable-toggle"
          onClick={() => setMobileInboxOpen((open) => !open)}
          aria-expanded={mobileInboxOpen}
        >
          {mobileInboxOpen ? "Hide Receipt Inbox" : "Show Receipt Inbox"}
        </button>
        {mobileInboxOpen ? (
          <section className="mobile-inline-panel scan-mobile-panel">
            {scanSidebarBody}
          </section>
        ) : null}
        <header className="main-header">
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
            <div className="main-header-title">
              <span className="pill">Review</span>
              <h1>Scan view</h1>
            </div>
            <div className="header-note">
              {selectedReceiptId
                ? `${selectedReceiptCodePrefix || `Receipt #${selectedReceiptId}`} · ${selectedReceiptStatusLabel || "unknown"}${selectedProgress ? ` · ${selectedProgress}` : ""}`
                : "Select a receipt from the inbox"}
            </div>
          </div>
          <div className="main-header-subtitle">
            Upload receipts, run OCR on one receipt at a time, then review and save structured purchase drafts.
          </div>
        </header>

        <div className="main-body scan-body">
          <div className="scan-command-shell scan-command-shell--docked">
            <div className={"scan-command-panel" + (scanCommandPanelOpen ? " is-open" : "") + (scanBusyNotice ? " has-running-action" : "")}>
              <div className="scan-command-bar">
                <div className="scan-command-info">
                  <span className="status-msg">{queueStatusLabel || "Queue-driven workflow active."}</span>
                  {scanBusyNotice ? (
                    <div className="scan-action-notice" role="status" aria-live="polite">
                      <span className="scan-action-spinner" aria-hidden="true" />
                      <span className="scan-action-copy">
                        <strong>{scanBusyNotice.title}</strong>
                        <span>{scanBusyNotice.detail}</span>
                      </span>
                    </div>
                  ) : null}
                </div>
                <div className="scan-actions">
                  {primaryScanAction ? (
                    <button
                      type="button"
                      className="submit-button scan-primary-action"
                      onClick={() => void primaryScanAction.onClick()}
                      disabled={primaryScanAction.disabled}
                      title={primaryScanAction.title}
                    >
                      {primaryScanAction.label}
                    </button>
                  ) : null}
                  <div className="scan-more-actions" ref={moreActionsRef}>
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => setMoreActionsOpen((open) => !open)}
                      disabled={!moreScanActions.length || isScanActionBusy || isReceiptProcessing}
                      aria-haspopup="menu"
                      aria-expanded={moreActionsOpen}
                    >
                      More
                    </button>
                    {moreActionsOpen && moreScanActions.length > 0 ? (
                      <div className="scan-more-menu" role="menu">
                        {moreScanActions.map((action) => (
                          <button
                            key={action.label}
                            type="button"
                            className={"scan-more-item" + (action.danger ? " scan-more-item--danger" : "")}
                            onClick={() => {
                              setMoreActionsOpen(false);
                              void action.onClick();
                            }}
                            disabled={action.disabled}
                            role="menuitem"
                          >
                            {action.label}
                          </button>
                        ))}
                      </div>
                    ) : null}
                  </div>
                </div>
              </div>
            </div>
            <button
              type="button"
              className="mobile-command-toggle hideable-toggle"
              onClick={() => setMobileCommandOpen((open) => !open)}
              disabled={Boolean(scanBusyNotice)}
              aria-expanded={scanCommandPanelOpen}
            >
              {scanBusyNotice ? "Action running" : mobileCommandOpen ? "Hide Actions" : "Show Actions"}
            </button>
          </div>

          {!selectedReceiptId && <div className="placeholder">Select a receipt from the queue to review it.</div>}
          {selectedReceiptId && detailLoading && <div className="placeholder">Loading receipt detail…</div>}
          {selectedReceiptId && !detailLoading && detail && (
            <div className="content-grid scan-review-grid">
              <section className="scan-pane scan-pane--preview">
                <div className="scan-pane-header">
                  <div>
                    <div className="card-title">Receipt pages</div>
                    <div className="card-subtitle">{detail.scan.source_file_name || detail.scan.source_path}</div>
                  </div>
                  <div className="scan-pane-header-actions">
                    <span className="pill pill--muted">{detail.scan.ocr_status}</span>
                    {selectedReceiptStatusLabel !== "posted" && detail.pages.length > 0 ? (
                      <button
                        type="button"
                        className="danger-button scan-page-delete-button"
                        onClick={handleDeletePage}
                        disabled={deletingPage || !canDeletePage}
                      >
                        {deletingPage ? "Deleting page…" : "Delete page"}
                      </button>
                    ) : null}
                  </div>
                </div>
                <div className="card-subtitle" style={{ marginBottom: 12 }}>
                  Pages {selectedPageCount}
                  {selectedProgress ? ` · ${selectedProgress}` : ""}
                </div>
                <div className="scan-zoom-controls" style={{ marginBottom: 12 }}>
                  <button type="button" className="secondary-button" onClick={() => zoomImage(-0.2)} disabled={!pageImage || imageScale <= 1}>
                    -
                  </button>
                  <button type="button" className="secondary-button" onClick={() => zoomImage(0.2)} disabled={!pageImage || imageScale >= 5}>
                    +
                  </button>
                  <button type="button" className="secondary-button" onClick={resetImageView} disabled={!pageImage || (imageScale === 1 && imageOffset.x === 0 && imageOffset.y === 0)}>
                    Reset
                  </button>
                  <span className="scan-meta-label" style={{ minWidth: 56, textAlign: "right" }}>
                    {Math.round(imageScale * 100)}%
                  </span>
                </div>
                {pageImage && (
                  <div className="card-subtitle" style={{ marginBottom: 12 }}>
                    {imageScale > 1 ? "Hold left mouse and drag to move the receipt." : "Zoom in above 100% to drag the receipt."}
                  </div>
                )}
                <div className="scan-page-tabs">
                  {detail.pages.map((page) => (
                    <button
                      key={page.id}
                      type="button"
                      className={"secondary-button scan-page-tab" + (selectedPageId === page.id ? " is-active" : "")}
                      onClick={() => {
                        setSelectedPageId(page.id);
                        const mappedDraftIndex = findDraftIndexForPage(page.page_no, drafts);
                        if (mappedDraftIndex !== null) {
                          setSelectedDraftIndex(mappedDraftIndex);
                        }
                      }}
                    >
                      Page {page.page_no}
                    </button>
                  ))}
                </div>
                <div
                  ref={imageFrameRef}
                  className="scan-image-frame"
                  style={{
                    overflow: "hidden",
                    cursor: pageImage ? (imageDragging ? "grabbing" : imageScale > 1 ? "grab" : "default") : "default",
                    touchAction: "none",
                  }}
                  onPointerDown={handleImagePointerDown}
                  onWheel={handleImageWheel}
                  onDoubleClick={resetImageView}
                >
                  {pageLoading && <div className="placeholder">Loading page image…</div>}
                  {!pageLoading && pageError && <div className="placeholder">{pageError}</div>}
                  {!pageLoading && !pageError && pageImage && (
                    <img
                      ref={imageRef}
                      src={pageImage.src}
                      alt={pageImage.path}
                      className="scan-image"
                      draggable={false}
                      style={{
                        transform: `translate(${imageOffset.x}px, ${imageOffset.y}px) scale(${imageScale})`,
                        transformOrigin: "center center",
                        userSelect: "none",
                        pointerEvents: "none",
                      }}
                    />
                  )}
                  {!pageLoading && !pageError && !pageImage && (
                    <div className="placeholder">{previewPlaceholder}</div>
                  )}
                </div>
                <div className="scan-preview-meta">
                  <div className="scan-meta-card">
                    <div className="scan-meta-row">
                      <span className="scan-meta-label">Source path</span>
                      <span className="scan-meta-value">{detail.scan.source_path || detail.scan.image_path}</span>
                    </div>
                    <div className="scan-meta-row">
                      <span className="scan-meta-label">Scanned at</span>
                      <span className="scan-meta-value">{detail.scan.scanned_at || "—"}</span>
                    </div>
                    <div className="scan-meta-row">
                      <span className="scan-meta-label">Review status</span>
                      <span className="scan-meta-value">{formatReviewStatus(detail.scan.review_status)}</span>
                    </div>
                  </div>
                  {detail.scan.ocr_status !== "processing" && (detail.scan.review_status || "").trim().toLowerCase() === "pending" && (
                    <div className="scan-meta-card" style={{ color: "#9fb3c8" }}>
                      OCR is finished. This receipt has not been manually reviewed/saved yet.
                    </div>
                  )}
                  <details>
                    <summary style={{ cursor: "pointer", color: "#cfd8e3" }}>Structured OCR JSON</summary>
                    <textarea
                      className="scan-textarea"
                      readOnly
                      value={detail.scan.extracted_text || JSON.stringify(detail.scan.parsed_json ?? [], null, 2)}
                      style={{ minHeight: 200, marginTop: 8 }}
                    />
                  </details>
                </div>
              </section>

              <section className="scan-pane scan-pane--drafts">
                <div className="scan-pane-header">
                  <div>
                    <div className="card-title">Structured drafts</div>
                    <div className="card-subtitle">Edit supplier, order, and item fields before approval/posting.</div>
                  </div>
                </div>
                <div className="scan-drafts-scroll">
                  {!drafts.length && <div className="placeholder">Run OCR to create editable purchase drafts.</div>}
                  {drafts.map((draft, draftIndex) => (
                    <div
                      key={`${draft.id ?? "draft"}-${draftIndex}`}
                      ref={(element) => {
                        draftCardRefs.current[draftIndex] = element;
                      }}
                      className="scan-meta-card"
                      tabIndex={selectedDraftIndex === draftIndex ? 0 : -1}
                      style={{
                        display: "grid",
                        gap: 12,
                        scrollMarginTop: 12,
                        border: selectedDraftIndex === draftIndex ? "1px solid rgba(255, 126, 54, 0.75)" : undefined,
                        boxShadow: selectedDraftIndex === draftIndex ? "0 0 0 1px rgba(255, 126, 54, 0.28)" : undefined,
                      }}
                    >
                      <div className="scan-draft-card-header">
                        <div className="card-title">
                          {draft.receipt_code || `Draft ${draftIndex + 1}`}
                          {draft.receipt_index >= 0 ? ` · Page ${draft.receipt_index + 1}` : ""}
                        </div>
                        <span className="pill pill--muted">{draft.status || "draft"}</span>
                      </div>

                      <div className="scan-draft-grid">
                        <label className="summary-field">
                          <span>Supplier name</span>
                          <input value={draft.supplier.name || ""} onChange={(e) => updateSupplierField(draftIndex, "name", e.target.value)} />
                        </label>
                        <label className="summary-field">
                          <span>Supplier TIN</span>
                          <input value={draft.supplier.tin || ""} onChange={(e) => updateSupplierField(draftIndex, "tin", e.target.value)} />
                        </label>
                        <label className="summary-field">
                          <span>Supplier site</span>
                          <input value={draft.supplier.site || ""} onChange={(e) => updateSupplierField(draftIndex, "site", e.target.value)} />
                        </label>
                        <label className="summary-field">
                          <span>Contact info</span>
                          <input value={draft.supplier.contact_info || ""} onChange={(e) => updateSupplierField(draftIndex, "contact_info", e.target.value)} />
                        </label>
                        <label className="summary-field">
                          <span>Invoice id</span>
                          <input
                            value={draft.purchase_order.invoice_id || ""}
                            onChange={(e) => updateOrderField(draftIndex, "invoice_id", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Purchase date</span>
                          <input
                            type="date"
                            value={draft.purchase_order.purchase_date || ""}
                            onChange={(e) => updateOrderField(draftIndex, "purchase_date", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Subtotal</span>
                          <input
                            type="number"
                            step="0.01"
                            value={draft.purchase_order.subtotal_amount ?? 0}
                            onChange={(e) => updateOrderField(draftIndex, "subtotal_amount", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Tax</span>
                          <input
                            type="number"
                            step="0.01"
                            value={draft.purchase_order.tax_amount ?? 0}
                            onChange={(e) => updateOrderField(draftIndex, "tax_amount", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Discount</span>
                          <input
                            type="number"
                            step="0.01"
                            value={draft.purchase_order.discount_amount ?? 0}
                            onChange={(e) => updateOrderField(draftIndex, "discount_amount", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Rounding</span>
                          <input
                            type="number"
                            step="0.01"
                            value={draft.purchase_order.rounding_amount ?? 0}
                            onChange={(e) => updateOrderField(draftIndex, "rounding_amount", e.target.value)}
                          />
                        </label>
                        <label className="summary-field">
                          <span>Line totals</span>
                          <select
                            value={draft.purchase_order.line_total_basis || "unknown"}
                            onChange={(e) => updateOrderField(draftIndex, "line_total_basis", e.target.value)}
                          >
                            <option value="unknown">Unknown</option>
                            <option value="inclusive">Tax inclusive</option>
                            <option value="exclusive">Tax exclusive</option>
                          </select>
                        </label>
                        <label className="summary-field">
                          <span>Grand total</span>
                          <input
                            type="number"
                            step="0.01"
                            value={draft.purchase_order.grand_total ?? draft.purchase_order.total_cost ?? 0}
                            onChange={(e) => updateOrderField(draftIndex, "grand_total", e.target.value)}
                          />
                        </label>
                      </div>

                      {asMessages(draft.validation_errors).length > 0 && (
                        <div className="status-msg status-msg--error">
                          {asMessages(draft.validation_errors).join(" · ")}
                        </div>
                      )}
                      {asMessages(draft.validation_warnings).length > 0 && (
                        <div className="status-msg status-msg--warning">
                          {asMessages(draft.validation_warnings).join(" · ")}
                        </div>
                      )}

                      <div className="scan-draft-items">
                        {draft.purchase_items.map((item, itemIndex) => {
                          return (
                          <div
                            key={`${draftIndex}-${itemIndex}`}
                            className={`scan-draft-item-row${itemNeedsAttention(item) ? " scan-draft-item-row--attention" : itemHasWarnings(item) ? " scan-draft-item-row--warning" : ""}`}
                          >
                            <label className="summary-field">
                              <span>Item</span>
                              <input
                                value={item.name || ""}
                                list={(item.historical_matches || []).length > 0 ? `historical-item-${draftIndex}-${itemIndex}` : undefined}
                                onChange={(e) => updateItemName(draftIndex, itemIndex, e.target.value)}
                              />
                              {(item.historical_matches || []).length > 0 && (
                                <datalist id={`historical-item-${draftIndex}-${itemIndex}`}>
                                  {(item.historical_matches || []).map((match) => (
                                    <option
                                      key={match.id}
                                      value={match.name}
                                      label={
                                        match.category && match.category !== "Others"
                                          ? `${match.category}${match.score ? ` · ${Math.round(match.score * 100)}%` : ""}`
                                          : match.score
                                          ? `${Math.round(match.score * 100)}%`
                                          : undefined
                                      }
                                    />
                                  ))}
                                </datalist>
                              )}
                            </label>
                            <label className="summary-field">
                              <span>Category</span>
                              <select
                                value={categoryOptions.includes(item.category || "Others") ? (item.category || "Others") : "Others"}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "category", e.target.value)}
                              >
                                {categoryOptions.map((category) => (
                                  <option key={category} value={category}>
                                    {category}
                                  </option>
                                ))}
                              </select>
                            </label>
                            <label className="summary-field">
                              <span>Qty</span>
                              <input
                                type="number"
                                step="0.01"
                                value={item.quantity ?? 0}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "quantity", e.target.value)}
                              />
                            </label>
                            <label className="summary-field">
                              <span>Disc %</span>
                              <input
                                type="number"
                                step="0.01"
                                value={optionalNumberValue(item.line_discount_percent)}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "line_discount_percent", e.target.value)}
                              />
                            </label>
                            <label className="summary-field">
                              <span>Unit</span>
                              <input
                                type="number"
                                step="0.01"
                                value={item.unit_price ?? 0}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "unit_price", e.target.value)}
                              />
                            </label>
                            <label className="summary-field">
                              <span>Tax</span>
                              <input
                                type="number"
                                step="0.01"
                                value={optionalNumberValue(item.line_tax_amount)}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "line_tax_amount", e.target.value)}
                              />
                            </label>
                            <label className="summary-field">
                              <span>Total</span>
                              <input
                                type="number"
                                step="0.01"
                                value={item.total_price ?? 0}
                                onChange={(e) => updateItemField(draftIndex, itemIndex, "total_price", e.target.value)}
                              />
                            </label>
                            <button type="button" className="danger-button" onClick={() => removeItem(draftIndex, itemIndex)}>
                              Remove
                            </button>
                            {itemNeedsAttention(item) && (
                              <div className="status-msg status-msg--error" style={{ gridColumn: "1 / -1" }}>
                                {asMessages(item.validation_errors).join(" · ")}
                              </div>
                            )}
                            {!itemNeedsAttention(item) && itemHasWarnings(item) && (
                              <div className="status-msg status-msg--warning" style={{ gridColumn: "1 / -1" }}>
                                {asMessages(item.validation_warnings).join(" · ")}
                              </div>
                            )}
                          </div>
                        )})}
                        <button type="button" className="secondary-button" onClick={() => addItem(draftIndex)}>
                          Add item
                        </button>
                      </div>
                    </div>
                  ))}

                  <div className="scan-meta-card" style={{ display: "grid", gap: 10 }}>
                    <div className="card-title">Review note</div>
                    <label className="summary-field">
                      <span>Reviewed by</span>
                      <input value={reviewedBy} onChange={(e) => setReviewedBy(e.target.value)} placeholder="operator name" />
                    </label>
                    <label className="summary-field">
                      <span>Note</span>
                      <textarea
                        className="scan-textarea"
                        value={reviewNote}
                        onChange={(e) => setReviewNote(e.target.value)}
                        style={{ minHeight: 120 }}
                      />
                    </label>
                  </div>
                </div>
              </section>
            </div>
          )}
        </div>
      </main>
      {uploadRenameDialog}
    </>
  );
}
