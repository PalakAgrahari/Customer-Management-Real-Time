import { useEffect, useState } from "react";
import { getCustomers } from "../api/customerApi";
import Navbar from "../components/Navbar";
import CustomerTable from "../components/CustomerTable";
import CustomerModal from "../components/CustomerModal";
import Pagination from "../components/Pagination";
import { toast } from "react-toastify";
import { deleteCustomer } from "../api/customerApi";
import { toastOnce } from "../utils/toastOnce";



export default function CustomerPage() {
  const [customers, setCustomers] = useState([]);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [hasNext, setHasNext] = useState(false);


  const PAGE_SIZE = 5;

  const fetchData = async () => {
    try {
      const res = await getCustomers(page);

      const records = res.data?.data?.records || [];
      const hasNextFromApi = res.data?.data?.hasNext || false;

      setCustomers(records);
      setHasNext(hasNextFromApi);

    } catch (err) {
      if (!err.response) {
        toastOnce("error", "Backend not reachable");
      } else if (err.response.data?.message !== "No customers found") {
        toastOnce("error", err.response.data.message);
      }

      setCustomers([]);
      setHasNext(false);
    }
  };





  const handleDelete = async (customer) => {
    if (!window.confirm(`Delete ${customer.name}?`)) return;

    setCustomers(prev => prev.filter(c => c.id !== customer.id));

    try {
      await deleteCustomer(customer.id);
      toastOnce("success", "Customer deleted");
    } catch {
      toastOnce("error", "Delete failed");
      fetchData();
    }
  };




  useEffect(() => {
    fetchData();
  }, [page]);

  return (
    <div className="max-w-6xl mx-auto p-6">
      <Navbar onAdd={() => { setSelected(null); setModalOpen(true); }} />

      <CustomerTable
        customers={customers}
        onEdit={(c) => { setSelected(c); setModalOpen(true); }}
        onDelete={handleDelete}
      />

      <Pagination page={page} setPage={setPage} hasNext={hasNext} />

      <CustomerModal
        open={modalOpen}
        customer={selected}
        onClose={() => setModalOpen(false)}
        refresh={fetchData}
      />
    </div>
  );
}
