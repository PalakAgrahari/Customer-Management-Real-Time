export default function Pagination({ page, setPage, hasNext }) {
  return (
    <div className="flex justify-center gap-4 mt-6">
      <button
        disabled={page === 1}
        onClick={() => setPage(p => p - 1)}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        Prev
      </button>

      <span className="font-medium">Page {page}</span>

      <button
        disabled={!hasNext}
        onClick={() => setPage(p => p + 1)}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        Next
      </button>
    </div>
  );
}
