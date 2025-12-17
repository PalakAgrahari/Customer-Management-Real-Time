import { useEffect, useState } from "react";
import { saveOrUpdateCustomer } from "../api/customerApi";
import { toast } from "react-toastify";

export default function CustomerModal({ open, onClose, customer, refresh }) {
    const [form, setForm] = useState({
        name: "",
        email: "",
        phoneNumber: "",
        customerCode: "",
        client: "",
        enable: true
    });



    useEffect(() => {
        if (customer) {
            setForm({
                name: customer.name || "",
                email: customer.email || "",
                phoneNumber: customer.phoneNumber || "",
                customerCode: customer.customerCode || "",
                client: customer.client || "",
                enable: customer.enable ?? true,
            });
        } else {
            setForm({
                name: "",
                email: "",
                phoneNumber: "",
                customerCode: "",
                client: "",
                enable: true,
            });
        }
    }, [customer, open]);



    if (!open) return null;

    const onChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm({ ...form, [name]: type === "checkbox" ? checked : value });
    };

    const save = async () => {
        try {
            const res = await saveOrUpdateCustomer(form);
            toast.success(res.data.message);
            refresh();
            onClose();
        } catch (err) {
            toast.error(err?.response?.data?.message || "Update failed");
        }
    };

    return (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50">

            <div className="bg-white rounded-xl shadow-xl w-full max-w-xl p-6 animate-fadeIn">

                <div className="flex justify-between items-center mb-5">
                    <h2 className="text-lg font-semibold text-gray-800">
                        {customer ? "Edit Customer" : "Add Customer"}
                    </h2>
                    <button
                        onClick={onClose}
                        className="text-gray-500 hover:text-gray-800 text-xl"
                    >
                        ✕
                    </button>
                </div>

                {/* Form */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">

                    <div>
                        <label className="label">Name</label>
                        <input
                            name="name"
                            value={form.name}
                            onChange={onChange}
                            className="input"
                            placeholder="Enter full name"
                        />
                    </div>

                    <div>
                        <label className="label">Email</label>
                        <input
                            name="email"
                            value={form.email}
                            onChange={onChange}
                            className="input"
                            placeholder="example@mail.com"
                        />
                    </div>

                    <div>
                        <label className="label">Phone Number</label>
                        <input
                            name="phoneNumber"
                            value={form.phoneNumber}
                            onChange={onChange}
                            className="input"
                            placeholder="10-digit phone"
                        />
                    </div>

                    <div>
                        <label className="label">Customer Code</label>
                        <input
                            name="customerCode"
                            value={form.customerCode}
                            onChange={onChange}
                            className="input"
                            placeholder="Unique code"
                        />
                    </div>

                    <div>
                        <label className="label">Client ID</label>
                        <input
                            name="client"
                            value={form.client}
                            onChange={onChange}
                            className="input"
                            placeholder="Client ID"
                        />
                    </div>

                    <div className="flex items-center gap-2 mt-6">
                        <input
                            type="checkbox"
                            name="enable"
                            checked={form.enable}
                            onChange={onChange}
                            className="h-4 w-4 accent-indigo-600"
                        />
                        <span className="text-sm text-gray-700">Enabled</span>
                    </div>
                </div>

                {/* Footer */}
                <div className="flex justify-end gap-3 mt-6">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 border rounded-md hover:bg-gray-100"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={save}
                        className="px-5 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
                    >
                        Save Changes
                    </button>
                </div>
            </div>
        </div>
    );
}
