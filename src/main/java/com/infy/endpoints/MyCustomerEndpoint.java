package com.infy.endpoints;


import com.infy.dto.CustomerDTO;
import com.infy.entity.Customer;
import com.infy.exception.InfyBankException;
import com.infy.repository.CustomerRepository;
import com.infy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Endpoint(id = "customers")
public class MyCustomerEndpoint {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Environment environment;

    @ReadOperation
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        customers.forEach((customer) -> {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(customer.getCustomerId());
            customerDTO.setName(customer.getName());
            customerDTO.setEmailId(customer.getEmailId());
            customerDTO.setDateOfBirth(customer.getDateOfBirth());

            customerDTOS.add(customerDTO);
        });
        return customerDTOS;
    }

    @WriteOperation
    public String updateCustomer(Integer customerId, String emailId) throws InfyBankException{
        Optional<Customer> optional = customerRepository.findById(customerId);
        Customer customer = optional.orElseThrow(() -> new InfyBankException("Service.CUSTOMER_NOT_FOUND"));
        customer.setEmailId(emailId);

        return environment.getProperty("API.UPDATE_SUCCESS");
        
    }

    @DeleteOperation
    public String deleteCustomer(@Selector Integer customerId){
        customerRepository.deleteById(customerId);
        return environment.getProperty("API.DELETE_SUCCESS");
    }
}
