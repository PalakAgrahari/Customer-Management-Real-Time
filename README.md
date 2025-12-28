# Customer Management: Empowering Businesses with Real-Time Data Processing

A full-stack, event-driven Customer Management System designed to handle real-time customer data efficiently. The application exposes RESTful APIs for customer operations, processes events asynchronously using Kafka, and leverages Redis caching for high-performance data access, along with a modern React frontend.

---

## 🛠 Tech Stack

### Frontend
- React.js
- Axios
- Tailwind CSS

### Backend
- Java
- Spring Boot
- Rest API

### Data & Messaging
- MySQL
- Redis
- Apache Kafka

---

## ✨ Key Features

- Customer CRUD operations (Create, Read, Update, Delete)
- Event-driven customer processing using Kafka
- Redis-based caching for faster data retrieval
- Backend-driven pagination and validation
- Responsive React UI with modals, tables, and pagination
- Optimistic UI updates with proper error handling

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|------|--------|------------|
| POST | `/customers/v1/get-customers` | Fetch customers with pagination |
| POST | `/customers/v1/get-customer-by-id` | Get customer by customer ID |
| POST | `/customers/v1/add-customer-by-kafka` | Add or update customer via Kafka |
| POST | `/customers/v1/save-or-update-customers` | Add or update customer directly |
| GET  | `/customers/v1/get-customers-by-client-id` | Fetch customers by client ID |
| GET  | `/customers/v1/load-redis` | Load database data into Redis cache |

---

## 📄 Pagination

Customer listing supports backend-driven pagination.  
Each request accepts a page number and returns a fixed number of records per page, ensuring efficient data retrieval for large datasets.

**Example Request:**
```json
{
  "pageNumber": 2
}
