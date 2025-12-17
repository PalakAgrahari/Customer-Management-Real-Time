package com.api.customer.cache;

import com.api.customer.model.CustomerModel;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class CustomerCache {

    private static final String CUSTOMER_ID_TO_CUSTOMER_MAP = "CUSTOMER_ID_TO_CUSTOMER_MAP";
    private static final String CLIENT_ID_TO_CUSTOMER_MAP = "CLIENT_ID_TO_CUSTOMER_MAP";

    @Autowired
    RedissonClient redissonClient;

    public CustomerModel getCustomerByCustomerId(int id) {
        RMap<Integer, CustomerModel> map = redissonClient.getMap(CUSTOMER_ID_TO_CUSTOMER_MAP);
        return map.get(id);
    }

    public List<CustomerModel> getCustomerByClientId(int client) {
        RMap<Integer, List<CustomerModel>> map = redissonClient.getMap(CLIENT_ID_TO_CUSTOMER_MAP);
        return map.get(client);
    }

    public void putCustomerToMaps(CustomerModel customerModel) {
        RMap<Integer, CustomerModel> customerIdMap = redissonClient.getMap(CUSTOMER_ID_TO_CUSTOMER_MAP);
        customerIdMap.put(customerModel.getId(), customerModel);

        RMap<Integer, List<CustomerModel>> clientMap = redissonClient.getMap(CLIENT_ID_TO_CUSTOMER_MAP);
        List<CustomerModel> customerModelList = clientMap.get(customerModel.getClient());

        if (customerModelList == null) {
            customerModelList = new ArrayList<>();
        } else {
            customerModelList.removeIf(c -> c.getId() == customerModel.getId());
        }

        customerModelList.add(customerModel);
        clientMap.put(customerModel.getClient(), customerModelList);
    }

    public void removeCustomer(int id) {

        // 1️⃣ Remove from CUSTOMER_ID_TO_CUSTOMER_MAP
        RMap<Integer, CustomerModel> customerIdMap =
                redissonClient.getMap(CUSTOMER_ID_TO_CUSTOMER_MAP);

        CustomerModel removedCustomer = customerIdMap.remove(id);

        if (removedCustomer == null) {
            return; // nothing to remove
        }

        RMap<Integer, List<CustomerModel>> clientMap =
                redissonClient.getMap(CLIENT_ID_TO_CUSTOMER_MAP);

        List<CustomerModel> customerList = clientMap.get(removedCustomer.getClient());

        if (customerList != null) {
            customerList.removeIf(c -> c.getId() == id);

            clientMap.put(removedCustomer.getClient(), customerList);
        }
    }

}
