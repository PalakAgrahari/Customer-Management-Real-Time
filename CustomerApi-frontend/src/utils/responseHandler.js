import { toast } from "react-toastify";

export const handleResponse = (res) => {
  if (res.data?.status) {
    toast.success(res.data.message);
    return res.data.data;
  } else {
    toast.error(res.data.message);
    throw new Error(res.data.message);
  }
};
