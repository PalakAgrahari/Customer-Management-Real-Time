export default function CustomerTable({ customers, onEdit, onDelete }) {

  return (
    <div className="overflow-x-auto bg-white rounded-lg shadow">
      <table className="min-w-full">
        <thead className="bg-gray-100">
          <tr>
            {["Name", "Email", "Phone", "Client", "Code", "Status", "Actions"].map(h => (
              <th key={h} className="px-4 py-3 text-left text-sm font-semibold">{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>

          {customers.map(c => (
            <tr key={c.id} className="border-t hover:bg-gray-50">
              <td className="px-4 py-2">{c.name}</td>
              <td className="px-4 py-2">{c.email}</td>
              <td className="px-4 py-2">{c.phoneNumber}</td>
              <td className="px-4 py-2">{c.client}</td>
              <td className="px-4 py-2">{c.customerCode}</td>
              <td className="px-4 py-2">
                <span className={`px-2 py-1 rounded text-xs ${c.enable ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}>
                  {c.enable ? "Active" : "Inactive"}
                </span>
              </td>
              <td className="px-4 py-2 flex gap-2">
                <button onClick={() => onEdit(c)} className="text-blue-600">Edit</button>
                <button onClick={() => onDelete(c)} className="text-red-600">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
