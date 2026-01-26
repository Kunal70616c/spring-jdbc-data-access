package sh.surge.kunal.banking.utils;

import java.util.List;
import java.util.Random;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sh.surge.kunal.banking.configurations.AppConfig;
import sh.surge.kunal.banking.models.Customer;
import sh.surge.kunal.banking.repositories.CustomerRepositoryImpl;
import com.github.javafaker.Faker;

public class CustomerApp {

    public static void main(String[] args) {
        Faker faker = new Faker();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        CustomerRepositoryImpl customerRepository = context.getBean(CustomerRepositoryImpl.class);

        // --- STEP 1: ADD DATA ---
        // If the database is empty, we MUST add a customer first, 
        // otherwise the random selection below will crash.
        Customer newCustomer = context.getBean(Customer.class);
        // Note: If your DB is AUTO_INCREMENT, MySQL will ignore this manual ID 
        // or throw an error if your SQL string includes it.
        newCustomer.setAccountNo(faker.number().numberBetween(1000000000L, 9999999999L));
        newCustomer.getFullName().setFirstName(faker.name().firstName());
        newCustomer.getFullName().setMiddleName(faker.name().firstName()); // nameWithMiddle can be long
        newCustomer.getFullName().setLastName(faker.name().lastName());
        newCustomer.setEmail(faker.internet().emailAddress());
        newCustomer.setContactNo(Long.parseLong(faker.phoneNumber().subscriberNumber(10)));
        newCustomer.setPassword(faker.internet().password(8, 10, true, true, true));

        System.out.println("Attempting to add customer...");
        boolean isAdded = customerRepository.addCustomer(newCustomer);

        if(isAdded) {
            System.out.println("Customer added successfully!");
        } else {
            System.out.println("Failed to add customer. Check your SQL console for errors.");
        }

        // --- STEP 2: FETCH DATA SAFELY ---
        List<Customer> allCustomers = customerRepository.getAllCustomers();

        if (allCustomers.isEmpty()) {
            System.out.println("No customers found in database. Cannot perform random selection.");
        } else {
            List<Long> accountNos = allCustomers.stream()
                    .map(Customer::getAccountNo)
                    .toList();

            // Safety check: size is now guaranteed to be > 0
            long randomNo = accountNos.get(new Random().nextInt(accountNos.size()));

            Customer fetchedCustomer = customerRepository.getCustomerById(randomNo);
            System.out.println("\nFetched Random Customer Details:");
            System.out.println(fetchedCustomer);
        }

        context.close();
    }
}
