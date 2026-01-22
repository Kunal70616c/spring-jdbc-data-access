package sh.surge.kunal.banking.repositories;

import java.util.List;

import sh.surge.kunal.banking.models.Customer;

public interface CustomerRepository {
	
	boolean addCustomer(Customer customer);
	Customer getCustomerById(long accountNo);
	List<Customer> getAllCustomers();
	boolean updateCustomer(Customer customer);
	boolean deleteCustomer(long accountNo);

}
