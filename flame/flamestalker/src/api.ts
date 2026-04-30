export const API_BASE = `${window.location.protocol}//${window.location.hostname}:20000`;

const AUTH_STORAGE_KEY = "flamestalker.auth.session";
const AUTH_EXPIRED_EVENT = "flamestalker-auth-expired";

export type UserRole = "root" | "user";

export interface AuthUser {
  username: string;
  display_name?: string;
  role: UserRole;
}

export interface StoredAuthSession {
  token: string;
  user: AuthUser;
}

export function getStoredAuthSession(): StoredAuthSession | null {
  try {
    const raw = window.localStorage.getItem(AUTH_STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as StoredAuthSession | null;
    if (!parsed?.token || !parsed?.user?.username || !parsed?.user?.role) return null;
    return parsed;
  } catch {
    return null;
  }
}

export function setStoredAuthSession(session: StoredAuthSession | null) {
  if (!session) {
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
    return;
  }
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
}

export function clearStoredAuthSession() {
  setStoredAuthSession(null);
}

export function notifyAuthExpired() {
  window.dispatchEvent(new CustomEvent(AUTH_EXPIRED_EVENT));
}

export function listenForAuthExpired(handler: () => void) {
  const listener = () => handler();
  window.addEventListener(AUTH_EXPIRED_EVENT, listener);
  return () => window.removeEventListener(AUTH_EXPIRED_EVENT, listener);
}

async function isAuthFailureResponse(response: Response) {
  try {
    const data = await response.clone().json();
    const error = String(data?.error ?? "").toLowerCase();
    return error.includes("authentication required") || error.includes("session expired");
  } catch {
    return false;
  }
}

export async function apiFetch(path: string, init: RequestInit = {}) {
  const session = getStoredAuthSession();
  const headers = new Headers(init.headers ?? {});
  if (session?.token) {
    headers.set("Authorization", `Bearer ${session.token}`);
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    cache: "no-store",
    headers,
  });

  if (response.status === 401 && await isAuthFailureResponse(response)) {
    clearStoredAuthSession();
    notifyAuthExpired();
  }

  return response;
}

export async function anonymousApiFetch(path: string, init: RequestInit = {}) {
  return fetch(`${API_BASE}${path}`, {
    ...init,
    cache: "no-store",
  });
}
