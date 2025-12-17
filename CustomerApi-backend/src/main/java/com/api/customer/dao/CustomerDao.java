package com.api.customer.dao;

import com.api.customer.model.CustomerModel;

import java.util.List;

public interface CustomerDao {
    List<CustomerModel> getCustomers(int pageNo);
    CustomerModel getCustomerById(int id);
    CustomerModel addCustomer(CustomerModel customer);
    CustomerModel getCustomerByCode(String customerCode);
    List<CustomerModel> getCustomersByClientId(int client);
}
