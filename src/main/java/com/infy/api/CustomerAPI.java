package com.infy.api;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.CustomerDTO;
import com.infy.exception.InfyBankException;
import com.infy.service.CustomerService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/infybank")
public class CustomerAPI {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private Environment environment;

	//SpringREST Heteos
	@GetMapping(value = "/customers/{customerId}")
	public EntityModel<CustomerDTO> getCustomer(@PathVariable Integer customerId) throws InfyBankException {
		CustomerDTO customer = customerService.getCustomer(customerId);
		Link selfLink = linkTo(methodOn(CustomerAPI.class).getCustomer(customerId)).withSelfRel();
		Link customersLink = linkTo(methodOn(CustomerAPI.class).getAllCustomers()).withRel("customers");
		//customer.add(selfLink);
		return EntityModel.of(customer,selfLink,customersLink);
	}

	//SpringREST Heteos
	@GetMapping(value = "/customers")
	public CollectionModel<EntityModel<CustomerDTO>> getAllCustomers() throws InfyBankException {
		List<EntityModel<CustomerDTO>> customers = customerService.getAllCustomers().stream().map(
				customer-> {
					try {
						return EntityModel.of(customer, linkTo(methodOn(CustomerAPI.class).getCustomer(customer.getCustomerId())).withSelfRel());
					} catch (InfyBankException e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList());
		Link link = linkTo(methodOn(CustomerAPI.class).getAllCustomers()).withRel("customers");
		return CollectionModel.of(customers, link);
	}
	@PostMapping(value = "/customers")
	public ResponseEntity<String> addCustomer(@RequestBody CustomerDTO customer) throws InfyBankException {
		Integer customerId = customerService.addCustomer(customer);
		String successMessage = environment.getProperty("API.INSERT_SUCCESS") + customerId;
		return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
	}
	@PutMapping(value = "/customers/{customerId}")
	public ResponseEntity<String> updateCustomer(@PathVariable Integer customerId, @RequestBody CustomerDTO customer)
			throws InfyBankException {
		customerService.updateCustomer(customerId, customer.getEmailId());
		String successMessage = environment.getProperty("API.UPDATE_SUCCESS");
		return new ResponseEntity<>(successMessage, HttpStatus.OK);
	}
	@DeleteMapping(value = "/customers/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) throws InfyBankException {
		customerService.deleteCustomer(customerId);
		String successMessage = environment.getProperty("API.DELETE_SUCCESS");
		return new ResponseEntity<>(successMessage, HttpStatus.OK);
	}
}

