package com.api.customer.service;

import com.api.customer.cache.CustomerCache;
import com.api.customer.dao.CustomerDaoImplementation;
import com.api.customer.dto.request.AddRequestDto;
import com.api.customer.dto.response.ResponseDto;
import com.api.customer.dto.response.ValidResponseDto;
import com.api.customer.model.CustomerModel;
import com.api.customer.util.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    CustomerDaoImplementation customerDaoImplementation;

    @Autowired
    KafkaTemplate<String, CustomerModel> kafkaTemplate;

    @Value("${spring.kafka.test.name}")
    String topicName;

    @Autowired

    CustomerCache customerCache;
    public static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public ResponseEntity<ResponseDto> getCustomers(int pageNo) {
        ResponseDto dto = new ResponseDto();
        int pageSize = 5;

        try {
            if (pageNo <= 0) {
                dto.setMessage("Invalid Input");
                dto.setStatus(false);
                dto.setData(null);
                return ResponseEntity.badRequest().body(dto);
            }

            List<CustomerModel> values = customerDaoImplementation.getCustomers(pageNo);

            boolean hasNext = values.size() == pageSize;

            // Wrap response
            dto.setStatus(true);
            dto.setMessage(values.isEmpty() ? "No customers found" : "Customers fetched");

            dto.setData(
                    Map.of(
                            "records", values,
                            "hasNext", hasNext
                    )
            );

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            logger.error("Values could not be found", e);
            dto.setStatus(false);
            dto.setMessage("Values could not be found");
            dto.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
        }
    }


    public ResponseEntity<ResponseDto> deleteCustomer(int id) {
        ResponseDto dto = new ResponseDto();
        try {
            CustomerModel customer = customerDaoImplementation.getCustomerById(id);
            if (customer == null) {
                dto.setStatus(false);
                dto.setMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
            }

            customerDaoImplementation.deleteCustomer(id);
            customerCache.removeCustomer(id);

            dto.setStatus(true);
            dto.setMessage("Customer deleted successfully");
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            dto.setStatus(false);
            dto.setMessage("Failed to delete customer");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(dto);
        }
    }

    public ResponseEntity<ResponseDto> getCustomerById(int id) {
        ResponseDto dto = new ResponseDto();
        CustomerModel customer = customerCache.getCustomerByCustomerId(id);
        try {
            if (customer != null) {
                dto.setData(customer);
                dto.setStatus(true);
                dto.setMessage("Success from cache");
                return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
            }
            customer = customerDaoImplementation.getCustomerById(id);
            if (customer == null) {
                dto.setMessage("No such id exists");
                dto.setStatus(false);
                dto.setData(null);
                logger.error("No such Id exists");
                return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
            }
            dto.setData(customer);
            dto.setStatus(true);
            dto.setMessage("Success from database");
            customerCache.putCustomerToMaps(customer);
            return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            dto.setStatus(false);
            dto.setMessage("Data could not be found." + e);
            dto.setData(null);
            return new ResponseEntity<>(dto, HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<ResponseDto> addCustomersByKafka(AddRequestDto req) {
        ResponseDto dto = new ResponseDto();
        try {
            CustomerModel existing = customerDaoImplementation.getCustomerByCode(req.getCustomerCode());
            ValidResponseDto valid = Validate.isValid(req);

            if (existing != null) {
                if (valid.isValidData()) {
                    CustomerModel c = new CustomerModel();
                    c.setName(req.getName());
                    c.setEnable(req.isEnable());
                    c.setPhoneNumber(req.getPhoneNumber());
                    c.setEmail(req.getEmail());
                    c.setId(existing.getId());
                    c.setCreateDate(Validate.setDate());
                    c.setCustomerCode(req.getCustomerCode());
                    c.setClient(req.getClient());
                    c.setLastModifiedDate(Validate.setDate());
                    kafkaTemplate.send(topicName, c);
                    dto.setStatus(true);
                    dto.setData(c);
                    dto.setMessage("Successfully Published customer");
                    return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
                }
                dto.setMessage(valid.getMessage());
                dto.setStatus(valid.isValidData());
                dto.setData(null);
                return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
            }

            if (valid.isValidData()) {
                CustomerModel c = new CustomerModel();
                c.setCustomerCode(req.getCustomerCode());
                c.setName(req.getName());
                c.setEnable(req.isEnable());
                c.setClient(req.getClient());
                c.setPhoneNumber(req.getPhoneNumber());
                c.setEmail(req.getEmail());
                c.setCreateDate(Validate.setDate());
                c.setLastModifiedDate(Validate.setDate());
                kafkaTemplate.send(topicName, c);
                dto.setStatus(true);
                dto.setData(c);
                dto.setMessage("Customer published Successfully");
                return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
            }

            dto.setStatus(false);
            dto.setMessage(valid.getMessage());
            dto.setData(null);
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);

        } catch (Exception e) {
            logger.error(e.getMessage());
            dto.setStatus(false);
            dto.setMessage("Customer can't be added, an exception encountered" + e);
            dto.setData(null);
            return new ResponseEntity<>(dto, HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<ResponseDto> saveOrUpdateCustomer(AddRequestDto addRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        try {
            CustomerModel existingCustomer = customerDaoImplementation.getCustomerByCode(addRequestDto.getCustomerCode());
            if (existingCustomer != null) {
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setId(existingCustomer.getId());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    CustomerModel resultantCustomerModel = customerDaoImplementation.addCustomer(customerModel);
                    customerCache.putCustomerToMaps(resultantCustomerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(resultantCustomerModel);
                    responseDto.setMessage("Successfully updated customer");
                    return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
                } else {
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setStatus(isValid.isValidData());
                    responseDto.setData(null);
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
            } else {
                String message = null;
                ValidResponseDto isValid = Validate.isValid(addRequestDto);
                if (isValid.isValidData()) {
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setCustomerCode(addRequestDto.getCustomerCode());
                    customerModel.setName(addRequestDto.getName());
                    customerModel.setEnable(addRequestDto.isEnable());
                    customerModel.setClient(addRequestDto.getClient());
                    customerModel.setPhoneNumber(addRequestDto.getPhoneNumber());
                    customerModel.setEmail(addRequestDto.getEmail());
                    customerModel.setCreateDate(Validate.setDate());
                    customerModel.setLastModifiedDate(Validate.setDate());
                    CustomerModel resultantCustomerModel = customerDaoImplementation.addCustomer(customerModel);
                    customerCache.putCustomerToMaps(resultantCustomerModel);
                    responseDto.setStatus(true);
                    responseDto.setData(resultantCustomerModel);
                    responseDto.setMessage("Successfully added customer");
                    return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
                } else {
                    responseDto.setMessage(isValid.getMessage());
                    responseDto.setStatus(isValid.isValidData());
                    responseDto.setData(null);
                    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
                }
            }
        }
        catch (Exception e){
            logger.error(e.getMessage());
            responseDto.setData(null);
            responseDto.setStatus(false);
            responseDto.setMessage("An exception occured"+e);
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_GATEWAY);
        }
    }

    @Transactional
    public void saveAddedCustomer(CustomerModel customerModel) {
        try {
            customerDaoImplementation.addCustomer(customerModel);
            customerCache.putCustomerToMaps(customerModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public ResponseEntity<ResponseDto> getCustomersByClientId(int client) {
        ResponseDto dto = new ResponseDto();
        try {
            List<CustomerModel> customers = customerCache.getCustomerByClientId(client);
            if (customers != null && customers.size() > 0) {
                dto.setData(customers);
                dto.setStatus(true);
                dto.setMessage("Success from cache");
                return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
            }
            customers = customerDaoImplementation.getCustomersByClientId(client);
            if (customers == null || customers.size() == 0) {
                dto.setMessage("No such id exists");
                dto.setStatus(false);
                dto.setData(null);
                logger.error("No such Id exists");
                return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
            }
            for (CustomerModel c : customers) customerCache.putCustomerToMaps(c);
            dto.setData(customers);
            dto.setStatus(true);
            dto.setMessage("Success from database");
            return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            dto.setData(null);
            dto.setStatus(false);
            dto.setMessage("An unknown exception encountered" + e);
            return new ResponseEntity<>(dto, HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<String> loadRedis() {
        List<CustomerModel> data = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 5;
        try {
            while (true) {
                List<CustomerModel> values = customerDaoImplementation.getCustomers(pageNo);
                data.addAll(values);
                if (values.size() != pageSize) break;
                pageNo++;
            }
            for (CustomerModel c : data) customerCache.putCustomerToMaps(c);
            return ResponseEntity.ok("Data Loaded Successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("Data Not Loaded Successfully");
        }
    }
}
