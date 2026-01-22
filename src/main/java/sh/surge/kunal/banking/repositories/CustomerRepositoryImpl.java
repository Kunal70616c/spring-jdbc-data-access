package sh.surge.kunal.banking.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sh.surge.kunal.banking.models.Customer;
import sh.surge.kunal.banking.models.FullName;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Value("${addCustomer}")
    private String addCustomerQuery;
    @Value("${selectAllCustomers}")
    private String selectAllCustomersQuery;
    @Value("${selectCustomerById}")
    private String selectCustomerByIdQuery;
    @Value("${updateCustomerContactNo}")
    private String updateCustomerQuery;
    @Value("${deleteCustomerById}")
    private String deleteCustomerQuery;
    @Autowired 
    ObjectProvider<Customer> customerProvider;
    @Autowired 
    ObjectProvider<FullName> fullNameProvider;
   
	
	@Override
	public boolean addCustomer(Customer customer) {
		// addCustomerQuery is used to add a customer to the database
		int rows=jdbcTemplate.update(addCustomerQuery, 
				customer.getAccountNo(), customer.getFullName().getFirstName(),
				customer.getFullName().getMiddleName(),
				customer.getFullName().getLastName(), 
				customer.getEmail(), customer.getPassword(),customer.getContactNo());
		
		return rows>0;
	}

	@Override
	public Customer getCustomerById(long accountNo) {
		// selectCustomerByIdQuery is used to get a customer from the database
		return jdbcTemplate.queryForObject(selectCustomerByIdQuery, this::mapRowToCustomer, 
				accountNo);
	}

	@Override
	public List<Customer> getAllCustomers() {
		// selectAllCustomersQuery is used to get all customers from the database
		return jdbcTemplate.query(selectAllCustomersQuery, this::mapRowToCustomer);
	}

	@Override
	public boolean updateCustomer(Customer customer) {
		// updateCustomerQuery is used to update a customer in the database
		return jdbcTemplate.update(updateCustomerQuery, customer.getContactNo(),
				customer.getAccountNo())>0;
	}

	@Override
	public boolean deleteCustomer(long accountNo) {
		// deleteCustomerQuery is used to delete a customer from the database
		return jdbcTemplate.update(deleteCustomerQuery, accountNo)>0;
	}
	
	
	private Customer mapRowToCustomer(ResultSet rs, int rowNum) throws SQLException {
        // Customer is used to map the database row to a Customer object
		Customer customer = customerProvider.getObject();
        // FullName is used to map the database row to a FullName object
	    FullName fullName = fullNameProvider.getObject();
        // customer.setFullName(fullName) is used to set the customer's full name
		customer.setFullName(fullName);
		customer.setAccountNo(rs.getLong("account_no"));
		customer.getFullName().setFirstName(rs.getString("first_name"));
		customer.getFullName().setMiddleName(rs.getString("middle_name"));
		customer.getFullName().setLastName(rs.getString("last_name"));
		customer.setEmail(rs.getString("email"));
		customer.setPassword(rs.getString("password"));
		customer.setContactNo(rs.getLong("contact_no"));
		return customer; // return the customer object
		
	}

}
