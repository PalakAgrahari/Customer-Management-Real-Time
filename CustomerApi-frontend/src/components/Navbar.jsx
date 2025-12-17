export default function Navbar({ onAdd }) {
    return (
        <div className="flex justify-between items-center bg-indigo-600 text-white px-6 py-4 rounded-lg mb-6">
            <h1 className="text-xl font-semibold">Customer Management</h1>
            <button
                onClick={onAdd}
                className="bg-white text-indigo-600 px-4 py-2 rounded-md font-medium hover:bg-indigo-100"
            >
                + Add Customer
            </button>
        </div>
    );
}
