import { toast } from "react-toastify";

const activeToasts = new Set();

export function toastOnce(type, message, options = {}) {
    if (activeToasts.has(message)) return;

    activeToasts.add(message);

    toast[type](message, {
        ...options,
        onClose: () => activeToasts.delete(message),
    });
}
