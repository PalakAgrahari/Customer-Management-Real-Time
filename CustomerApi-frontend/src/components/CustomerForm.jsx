import { useState } from "react";
import { saveOrUpdateCustomer, addCustomerKafka } from "../api/customerApi";
import { toast } from "react-toastify";

export default function CustomerForm({ refresh }) {
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    name: "",
    email: "",
    phoneNumber: "",
    customerCode: "",
    client: "",
    enable: true
  });

  const onChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  };

  const validate = () => {
    if (form.phoneNumber.length !== 10) {
      toast.error("Phone number must be 10 digits");
      return false;
    }
    return true;
  };

  const saveCustomer = async () => {
    if (!validate()) return;

    try {
      setLoading(true);
      const res = await saveOrUpdateCustomer(form);
      toast.success(res.data.message);
      refresh();
    } catch (err) {
      toast.error(err?.response?.data?.message || "Failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Add / Update Customer</h2>

      <div className="grid">
        <input name="name" placeholder="Name" onChange={onChange} />
        <input name="email" placeholder="Email" onChange={onChange} />
        <input name="phoneNumber" placeholder="Phone" onChange={onChange} />
        <input name="customerCode" placeholder="Customer Code" onChange={onChange} />
        <input name="client" placeholder="Client ID" onChange={onChange} />
      </div>

      <label>
        <input type="checkbox" name="enable" onChange={onChange} /> Enabled
      </label>

      <br /><br />

      <button onClick={saveCustomer} disabled={loading}>
        {loading ? "Saving..." : "Save / Update"}
      </button>

      <button
        className="secondary"
        onClick={() => addCustomerKafka(form)}
        style={{ marginLeft: 10 }}
      >
        Publish via Kafka
      </button>
    </div>
  );
}
