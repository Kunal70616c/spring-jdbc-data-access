package sh.surge.kunal.banking.services;

import org.springframework.stereotype.Service;

import sh.surge.kunal.banking.models.Customer;

@Service
public class CustomerService {
	
	
	public void addCustomer(Customer customer) {
	    // Logic to add customer to the database
	    System.out.println("Customer added: "+customer);
	}

}
