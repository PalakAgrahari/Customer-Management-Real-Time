import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/customers/v1",
  headers: { "Content-Type": "application/json" }
});

export const getCustomers = (pageNumber) =>
  API.post("/get-customers", { pageNumber });

export const getCustomerById = (id) =>
  API.post("/get-customer-by-id", { id });

export const saveOrUpdateCustomer = (data) =>
  API.post("/save-or-update-customers", data);

export const addCustomerKafka = (data) =>
  API.post("/add-customer-by-kafka", data);

export const getCustomersByClient = (client) =>
  API.get("/get-customers-by-client-id", { data: { client } });

export const loadRedis = () =>
  API.get("/load-redis");

export const deleteCustomer = (id) =>
  API.delete(`/delete-customer/${id}`);
