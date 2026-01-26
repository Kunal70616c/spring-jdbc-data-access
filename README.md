# Spring JDBC Data Access

A comprehensive guide to implementing data access in Spring applications using JDBC, JdbcTemplate, and HikariCP connection pooling with a Banking Application example.

## Repository
[GitHub - Spring JDBC Data Access](https://github.com/Kunal70616c/spring-jdbc-data-access.git)

## Table of Contents
- [What is Spring JDBC?](#what-is-spring-jdbc)
- [Why Use Spring JDBC?](#why-use-spring-jdbc)
- [JDBC vs Other Technologies](#jdbc-vs-other-technologies)
- [Core Components](#core-components)
- [Project Structure](#project-structure)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Implementation Details](#implementation-details)
- [Running the Application](#running-the-application)
- [Best Practices](#best-practices)

---

## What is Spring JDBC?

**Spring JDBC** is a module of the Spring Framework that provides a simplified approach to database access using JDBC (Java Database Connectivity). It eliminates much of the boilerplate code required in traditional JDBC programming while maintaining the flexibility and control of SQL.

### Traditional JDBC Problems

Traditional JDBC requires verbose, repetitive code:

```java
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    conn = dataSource.getConnection();
    stmt = conn.prepareStatement("SELECT * FROM customer WHERE account_no = ?");
    stmt.setLong(1, accountNo);
    rs = stmt.executeQuery();
    // Process ResultSet...
} catch (SQLException e) {
    // Handle exception
} finally {
    if (rs != null) try { rs.close(); } catch (SQLException e) {}
    if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
    if (conn != null) try { conn.close(); } catch (SQLException e) {}
}
```

### Spring JDBC Solution

Spring JDBC simplifies this to:

```java
Customer customer = jdbcTemplate.queryForObject(
    "SELECT * FROM customer WHERE account_no = ?",
    this::mapRowToCustomer,
    accountNo
);
```

---

## Why Use Spring JDBC?

### 1. **Simplified Resource Management**
Spring automatically handles:
- Opening and closing connections
- Creating and closing statements
- Handling ResultSets
- Exception translation to unchecked exceptions

### 2. **Reduced Boilerplate Code**
No need for try-catch-finally blocks or manual resource cleanup.

### 3. **Better Exception Handling**
Converts checked `SQLException` to Spring's `DataAccessException` hierarchy (unchecked exceptions).

### 4. **SQL Control**
Full control over SQL queries, unlike ORM frameworks that generate SQL automatically.

### 5. **Performance**
Lower overhead compared to full ORM solutions like Hibernate for simple CRUD operations.

### 6. **Easy Integration**
Seamlessly integrates with Spring's dependency injection and transaction management.

### 7. **Connection Pooling**
Easy integration with connection pool libraries like HikariCP for optimal database performance.

---

## JDBC vs Other Technologies

### Spring JDBC vs Plain JDBC

| Feature | Plain JDBC | Spring JDBC |
|---------|-----------|-------------|
| Boilerplate Code | High | Low |
| Resource Management | Manual | Automatic |
| Exception Handling | Checked exceptions | Unchecked exceptions |
| Transaction Management | Manual | Declarative |
| Connection Pooling | Manual setup | Easy integration |

### Spring JDBC vs JPA/Hibernate

| Feature | Spring JDBC | JPA/Hibernate |
|---------|-------------|---------------|
| SQL Control | Full control | Generated SQL |
| Learning Curve | Easy | Steep |
| Performance | Fast for simple queries | Overhead for simple operations |
| Object Mapping | Manual | Automatic |
| Lazy Loading | Not supported | Supported |
| Caching | Manual | Built-in |
| Best For | Simple CRUD, complex queries | Complex object graphs |

### When to Use Spring JDBC

✅ **Use Spring JDBC when:**
- You need full control over SQL
- Working with legacy databases
- Performance is critical for simple operations
- Database schema doesn't map well to objects
- You want lightweight data access
- Writing complex, optimized SQL queries

❌ **Consider JPA/Hibernate when:**
- Working with complex object relationships
- Need lazy loading and caching
- Database schema maps cleanly to objects
- Portability across databases is important
- Team prefers working with objects over SQL

---

## Core Components

### 1. DataSource

The `DataSource` represents the database connection. Spring JDBC uses it to obtain connections.

```java
@Bean
public HikariDataSource getDataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(url);
    dataSource.setUsername(userName);
    dataSource.setPassword(password);
    dataSource.setDriverClassName(driverClassName);
    dataSource.setMaximumPoolSize(10);
    return dataSource;
}
```

### 2. JdbcTemplate

The central class in Spring JDBC that simplifies database access.

**Key Methods:**
- `update()`: For INSERT, UPDATE, DELETE operations
- `query()`: For SELECT operations returning multiple rows
- `queryForObject()`: For SELECT operations returning a single row
- `queryForList()`: For simple queries returning primitive types
- `execute()`: For DDL statements

### 3. RowMapper

Functional interface that maps database rows to Java objects.

```java
private Customer mapRowToCustomer(ResultSet rs, int rowNum) throws SQLException {
    Customer customer = customerProvider.getObject();
    FullName fullName = fullNameProvider.getObject();
    customer.setFullName(fullName);
    customer.setAccountNo(rs.getLong("account_no"));
    customer.getFullName().setFirstName(rs.getString("first_name"));
    // ... more mappings
    return customer;
}
```

### 4. HikariCP Connection Pool

HikariCP is a high-performance JDBC connection pool that:
- Maintains a pool of database connections
- Reuses connections instead of creating new ones
- Dramatically improves performance
- Handles connection lifecycle automatically

**Why Connection Pooling?**
- Creating database connections is expensive (time and resources)
- Connection pooling reuses existing connections
- Reduces database load and improves application performance
- Essential for production applications

---

## Project Structure

```
sh.surge.kunal.banking/
├── configurations/
│   └── AppConfig.java                  # Spring configuration with DataSource & JdbcTemplate
├── models/
│   ├── Account.java                    # Abstract account entity
│   ├── CurrentAccount.java             # Current account implementation
│   ├── SavingsAccount.java             # Savings account implementation
│   ├── Customer.java                   # Customer entity (prototype scope)
│   └── FullName.java                   # Name value object (prototype scope)
├── repositories/
│   ├── CustomerRepository.java         # Repository interface
│   └── CustomerRepositoryImpl.java     # JDBC implementation with JdbcTemplate
├── services/
│   └── CustomerService.java            # Business logic layer
└── utils/
    └── CustomerApp.java                # Demo application for CRUD operations

resources/
└── application.properties              # Database config and SQL queries
```

---

## Database Setup

### MySQL Installation & Setup

#### 1. Download MySQL

**Windows:**
- Visit [MySQL Downloads](https://dev.mysql.com/downloads/installer/)
- Download MySQL Installer (mysql-installer-community)
- Run installer and choose "Developer Default"
- Set root password during installation

**macOS:**
```bash
# Using Homebrew
brew install mysql

# Start MySQL
brew services start mysql

# Secure installation
mysql_secure_installation
```

**Linux (Ubuntu/Debian):**
```bash
# Install MySQL
sudo apt update
sudo apt install mysql-server

# Start MySQL service
sudo systemctl start mysql

# Secure installation
sudo mysql_secure_installation
```

#### 2. Create Database and Table

```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE bankingdb2026;

-- Use the database
USE bankingdb2026;

-- Create customer table
CREATE TABLE customer (
    account_no BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    contact_no BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Verify table structure
DESCRIBE customer;

-- Optional: Insert sample data
INSERT INTO customer (account_no, first_name, middle_name, last_name, email, password, contact_no)
VALUES (1234567890, 'John', 'Michael', 'Doe', 'john.doe@email.com', 'Pass@123', 9876543210);

-- Verify data
SELECT * FROM customer;
```

#### 3. Configure Database Access

Update `application.properties` with your MySQL credentials:

```properties
url=jdbc:mysql://localhost:3306/bankingdb2026
mysqlusername=root
mysqlpassword=your_password_here
driver-class-name=com.mysql.cj.jdbc.Driver
```

**Common MySQL Ports:**
- Default: 3306
- If changed during installation, update the URL accordingly

**Connection String Format:**
```
jdbc:mysql://[host]:[port]/[database]?[parameters]
```

#### 4. MySQL Workbench (Optional GUI Tool)

For easier database management:
- Download [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
- Connect using root credentials
- Visual interface for running queries and managing databases

---

## Configuration

### 1. Application Properties

All database configuration and SQL queries are externalized in `application.properties`:

```properties
# Database Configuration
url=jdbc:mysql://localhost:3306/bankingdb2026
mysqlusername=root
mysqlpassword=
driver-class-name=com.mysql.cj.jdbc.Driver

# SQL Queries
addCustomer=INSERT INTO customer (account_no,first_name,middle_name,last_name,email,password,contact_no) VALUES (?, ?, ?,?, ?, ?, ?)
selectAllCustomers=SELECT * FROM customer
selectCustomerById=SELECT * FROM customer WHERE account_no=?
updateCustomerContactNo=UPDATE customer SET contact_no=? WHERE account_no=?
deleteCustomerById=DELETE FROM customer WHERE account_no=?
```

**Benefits of Externalizing Queries:**
- Easy to modify SQL without changing Java code
- Different queries for different environments
- Centralized query management
- Better version control for SQL changes

---

### 2. Spring Configuration Class

```java
@Configuration
@ComponentScan(basePackages = "sh.surge.kunal.banking")
@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
@Data
public class AppConfig {
    @Value("${url}")
    private String url;
    @Value("${mysqlusername}")
    private String userName;
    @Value("${mysqlpassword}")
    private String password;
    @Value("${driver-class-name}")
    private String driverClassName;
    
    @Bean
    public HikariDataSource getDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(HikariDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

**Key Configuration Elements:**

1. **@PropertySource**: Loads properties from application.properties
2. **@Value**: Injects property values into fields
3. **HikariDataSource Bean**: 
   - High-performance connection pool
   - `setMaximumPoolSize(10)`: Maintains up to 10 connections
   - Automatically manages connection lifecycle
4. **JdbcTemplate Bean**: 
   - Takes DataSource as dependency
   - Provides simplified database operations

---

## Implementation Details

### 1. Model Classes

#### Customer Entity

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
@Scope("prototype")
public class Customer {
    Logger logger = LoggerFactory.getLogger(Customer.class);
    protected long accountNo;
    @Autowired 
    protected FullName fullName;   
    protected long contactNo;
    protected String email;
    protected String password;
}
```

**Important: @Scope("prototype")**
- Creates a new instance every time the bean is requested
- Essential for creating multiple customer objects
- Default scope is singleton (single shared instance)

#### FullName Value Object

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
public class FullName {
    private String firstName;
    private String middleName;
    private String lastName;
}
```

---

### 2. Repository Layer

#### Repository Interface

```java
public interface CustomerRepository {
    boolean addCustomer(Customer customer);
    Customer getCustomerById(long accountNo);
    List<Customer> getAllCustomers();
    boolean updateCustomer(Customer customer);
    boolean deleteCustomer(long accountNo);
}
```

**Why Use Interface?**
- Abstraction: Business logic doesn't depend on implementation details
- Testability: Easy to create mock implementations
- Flexibility: Can swap implementations (JDBC, JPA, MongoDB)
- Best practice: Programming to interfaces, not implementations

---

#### Repository Implementation

```java
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
```

**ObjectProvider Usage:**
- `ObjectProvider<T>` is used to get prototype-scoped beans
- `customerProvider.getObject()` creates a new Customer instance
- Necessary because Customer and FullName are prototype-scoped
- Prevents sharing state between different customer objects

---

### 3. CRUD Operations

#### CREATE - Adding a Customer

```java
@Override
public boolean addCustomer(Customer customer) {
    int rows = jdbcTemplate.update(addCustomerQuery, 
        customer.getAccountNo(), 
        customer.getFullName().getFirstName(),
        customer.getFullName().getMiddleName(),
        customer.getFullName().getLastName(), 
        customer.getEmail(), 
        customer.getPassword(),
        customer.getContactNo()
    );
    return rows > 0;
}
```

**How it works:**
1. `jdbcTemplate.update()` executes INSERT/UPDATE/DELETE
2. Parameters replace `?` placeholders in order
3. Returns number of rows affected
4. Returns `true` if at least one row was inserted

**SQL Executed:**
```sql
INSERT INTO customer (account_no, first_name, middle_name, last_name, email, password, contact_no) 
VALUES (?, ?, ?, ?, ?, ?, ?)
```

---

#### READ - Getting Single Customer

```java
@Override
public Customer getCustomerById(long accountNo) {
    return jdbcTemplate.queryForObject(
        selectCustomerByIdQuery, 
        this::mapRowToCustomer, 
        accountNo
    );
}
```

**How it works:**
1. `queryForObject()` expects exactly one result
2. Uses RowMapper to convert ResultSet to Customer
3. Throws exception if no rows or multiple rows found
4. Method reference `this::mapRowToCustomer` provides mapping logic

---

#### READ - Getting All Customers

```java
@Override
public List<Customer> getAllCustomers() {
    return jdbcTemplate.query(selectAllCustomersQuery, this::mapRowToCustomer);
}
```

**How it works:**
1. `query()` returns a List of objects
2. Applies RowMapper to each row
3. Returns empty list if no results (never null)
4. Efficient for retrieving multiple records

---

#### UPDATE - Updating Customer

```java
@Override
public boolean updateCustomer(Customer customer) {
    return jdbcTemplate.update(
        updateCustomerQuery, 
        customer.getContactNo(),
        customer.getAccountNo()
    ) > 0;
}
```

**SQL Executed:**
```sql
UPDATE customer SET contact_no = ? WHERE account_no = ?
```

**Note:** This implementation only updates contact number. You can extend it to update all fields:

```sql
UPDATE customer SET 
    first_name = ?, 
    middle_name = ?, 
    last_name = ?, 
    email = ?, 
    password = ?, 
    contact_no = ? 
WHERE account_no = ?
```

---

#### DELETE - Deleting Customer

```java
@Override
public boolean deleteCustomer(long accountNo) {
    return jdbcTemplate.update(deleteCustomerQuery, accountNo) > 0;
}
```

**SQL Executed:**
```sql
DELETE FROM customer WHERE account_no = ?
```

---

### 4. RowMapper Implementation

The RowMapper is crucial for converting database rows to Java objects:

```java
private Customer mapRowToCustomer(ResultSet rs, int rowNum) throws SQLException {
    // Get new instances (prototype scope)
    Customer customer = customerProvider.getObject();
    FullName fullName = fullNameProvider.getObject();
    
    // Set the relationship
    customer.setFullName(fullName);
    
    // Map database columns to object properties
    customer.setAccountNo(rs.getLong("account_no"));
    customer.getFullName().setFirstName(rs.getString("first_name"));
    customer.getFullName().setMiddleName(rs.getString("middle_name"));
    customer.getFullName().setLastName(rs.getString("last_name"));
    customer.setEmail(rs.getString("email"));
    customer.setPassword(rs.getString("password"));
    customer.setContactNo(rs.getLong("contact_no"));
    
    return customer;
}
```

**RowMapper Signature:**
```java
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
```

**Parameters:**
- `ResultSet rs`: Current row from database query
- `int rowNum`: Row number (starts at 0)

**Best Practices:**
- Always check for null values when using `getString()`, `getLong()`, etc.
- Use appropriate getter methods based on column types
- Handle nested objects properly (like FullName)
- Consider using `rs.wasNull()` for primitive types that might be NULL

---

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ installed and running
- Database `bankingdb2026` created

### Installation & Setup

```bash
# Clone the repository
git clone https://github.com/Kunal70616c/spring-jdbc-data-access.git

# Navigate to project directory
cd spring-jdbc-data-access

# Update application.properties with your MySQL credentials
# Edit: src/main/resources/application.properties

# Build the project
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="sh.surge.kunal.banking.utils.CustomerApp"
```

---

### CustomerApp - Complete Demo

```java
public class CustomerApp {
    public static void main(String[] args) {
        Faker faker = new Faker();
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);
        CustomerRepositoryImpl customerRepository = 
            context.getBean(CustomerRepositoryImpl.class);

        // CREATE - Add new customer
        Customer newCustomer = context.getBean(Customer.class);
        newCustomer.setAccountNo(faker.number().numberBetween(1000000000L, 9999999999L));
        newCustomer.getFullName().setFirstName(faker.name().firstName());
        newCustomer.getFullName().setMiddleName(faker.name().firstName());
        newCustomer.getFullName().setLastName(faker.name().lastName());
        newCustomer.setEmail(faker.internet().emailAddress());
        newCustomer.setContactNo(Long.parseLong(faker.phoneNumber().subscriberNumber(10)));
        newCustomer.setPassword(faker.internet().password(8, 10, true, true, true));

        System.out.println("Attempting to add customer...");
        boolean isAdded = customerRepository.addCustomer(newCustomer);

        if(isAdded) {
            System.out.println("Customer added successfully!");
        } else {
            System.out.println("Failed to add customer.");
        }

        // READ - Get all customers
        List<Customer> allCustomers = customerRepository.getAllCustomers();

        if (allCustomers.isEmpty()) {
            System.out.println("No customers found in database.");
        } else {
            // READ - Get random customer by ID
            List<Long> accountNos = allCustomers.stream()
                    .map(Customer::getAccountNo)
                    .toList();
            
            long randomNo = accountNos.get(new Random().nextInt(accountNos.size()));
            Customer fetchedCustomer = customerRepository.getCustomerById(randomNo);
            
            System.out.println("\nFetched Random Customer Details:");
            System.out.println(fetchedCustomer);
        }

        context.close();
    }
}
```

---

### Expected Output

```
Attempting to add customer...
Customer added successfully!

Fetched Random Customer Details:
Customer(
    logger=..., 
    accountNo=8765432109, 
    fullName=FullName(firstName=Sarah, middleName=Elizabeth, lastName=Johnson), 
    contactNo=5551234567, 
    email=sarah.johnson@email.com, 
    password=SecurePass123
)
```

---

## Understanding the Flow

### 1. Application Startup
```
1. Spring scans @ComponentScan packages
2. Loads application.properties
3. Creates HikariDataSource bean (connection pool)
4. Creates JdbcTemplate bean (depends on DataSource)
5. Creates CustomerRepositoryImpl (depends on JdbcTemplate)
6. Injects SQL queries from properties file
```

### 2. CRUD Operation Flow

**Adding a Customer:**
```
CustomerApp
    ↓
CustomerRepositoryImpl.addCustomer()
    ↓
JdbcTemplate.update()
    ↓
HikariCP gets connection from pool
    ↓
Executes INSERT query with parameters
    ↓
Returns number of rows affected
    ↓
Connection returned to pool
```

**Querying Customers:**
```
CustomerApp
    ↓
CustomerRepositoryImpl.getAllCustomers()
    ↓
JdbcTemplate.query()
    ↓
HikariCP gets connection from pool
    ↓
Executes SELECT query
    ↓
For each row: calls mapRowToCustomer()
    ↓
Returns List<Customer>
    ↓
Connection returned to pool
```

---

## Best Practices

### 1. Connection Pool Configuration

```java
dataSource.setMaximumPoolSize(10);        // Max connections
dataSource.setMinimumIdle(5);             // Min idle connections
dataSource.setConnectionTimeout(30000);   // 30 seconds
dataSource.setIdleTimeout(600000);        // 10 minutes
dataSource.setMaxLifetime(1800000);       // 30 minutes
```

**Guidelines:**
- Max pool size = (available_memory / memory_per_connection)
- Start with 10 connections, adjust based on load
- Monitor pool metrics in production

---

### 2. SQL Query Management

✅ **Good Practice:**
```properties
# Externalize queries in properties file
selectCustomerById=SELECT * FROM customer WHERE account_no=?
```

❌ **Avoid:**
```java
// Hardcoding SQL in Java code
String sql = "SELECT * FROM customer WHERE account_no=?";
```

---

### 3. Exception Handling

```java
try {
    Customer customer = customerRepository.getCustomerById(accountNo);
    System.out.println("Customer found: " + customer);
} catch (EmptyResultDataAccessException e) {
    System.out.println("No customer found with account number: " + accountNo);
} catch (DataAccessException e) {
    System.err.println("Database error: " + e.getMessage());
}
```

**Common Spring JDBC Exceptions:**
- `EmptyResultDataAccessException`: No results found for queryForObject()
- `IncorrectResultSizeDataAccessException`: Multiple results when expecting one
- `DataIntegrityViolationException`: Constraint violations
- `DataAccessException`: General database errors

---

### 4. Parameterized Queries (Prevent SQL Injection)

✅ **Good - Parameterized:**
```java
jdbcTemplate.queryForObject(
    "SELECT * FROM customer WHERE account_no = ?",
    this::mapRowToCustomer,
    accountNo  // Parameter binding
);
```

❌ **Bad - String Concatenation:**
```java
// NEVER DO THIS - SQL Injection vulnerability!
String sql = "SELECT * FROM customer WHERE account_no = " + accountNo;
jdbcTemplate.query(sql, this::mapRowToCustomer);
```

---

### 5. Resource Management

Spring JDBC automatically handles:
- ✅ Opening connections
- ✅ Closing connections
- ✅ Releasing resources
- ✅ Exception translation

You don't need try-finally blocks!

---

### 6. Transaction Management

For multiple database operations, use transactions:

```java
@Transactional
public void transferMoney(long fromAccount, long toAccount, double amount) {
    // Deduct from source account
    jdbcTemplate.update(
        "UPDATE account SET balance = balance - ? WHERE account_no = ?",
        amount, fromAccount
    );
    
    // Add to destination account
    jdbcTemplate.update(
        "UPDATE account SET balance = balance + ? WHERE account_no = ?",
        amount, toAccount
    );
    
    // Both operations succeed or both fail (rollback)
}
```

**Enable Transactions:**
```java
@Configuration
@EnableTransactionManagement
public class AppConfig {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

---

### 7. Batch Operations

For inserting multiple records efficiently:

```java
public int[] batchInsertCustomers(List<Customer> customers) {
    String sql = "INSERT INTO customer (account_no, first_name, last_name, email, password, contact_no) VALUES (?, ?, ?, ?, ?, ?)";
    
    return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Customer customer = customers.get(i);
            ps.setLong(1, customer.getAccountNo());
            ps.setString(2, customer.getFullName().getFirstName());
            ps.setString(3, customer.getFullName().getLastName());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPassword());
            ps.setLong(6, customer.getContactNo());
        }
        
        @Override
        public int getBatchSize() {
            return customers.size();
        }
    });
}
```

**Benefits:**
- Single database round trip
- Significantly faster for bulk operations
- Reduced network overhead

---

## Performance Tips

### 1. Use Connection Pooling
Always use HikariCP or another connection pool in production. Never create connections directly.

### 2. Limit Result Sets
```java
// Add LIMIT to queries returning many rows
SELECT * FROM customer LIMIT 100
```

### 3. Use Batch Operations
For bulk inserts/updates, use `batchUpdate()` instead of multiple `update()` calls.

### 4. Index Database Columns
```sql
CREATE INDEX idx_email ON customer(email);
CREATE INDEX idx_contact ON customer(contact_no);
```

### 5. Monitor Connection Pool
Log HikariCP metrics to identify connection leaks and tune pool size.

---

## Common Troubleshooting

### Issue: Connection Refused

```
java.sql.SQLException: Connection refused
```

**Solutions:**
- Verify MySQL is running: `sudo systemctl status mysql`
- Check port number (default 3306)
- Verify firewall settings
- Test connection: `mysql -u root -p`

---

### Issue: Access Denied

```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**Solutions:**
- Verify username and password in application.properties
- Grant privileges: `GRANT ALL PRIVILEGES ON bankingdb2026.* TO 'root'@'localhost';`
- Flush privileges: `FLUSH PRIVILEGES;`

---

### Issue: Table Doesn't Exist

```
java.sql.SQLSyntaxErrorException: Table 'bankingdb2026.customer' doesn't exist
```

**Solutions:**
- Verify database name: `SHOW DATABASES;`
- Verify table exists: `SHOW TABLES;`
- Create table using schema provided above

---

### Issue: Connection Pool Exhausted

```
java.sql.SQLTransientConnectionException: HikariPool - Connection is not available
```

**Solutions:**
- Increase pool size: `dataSource.setMaximumPoolSize(20);`
- Check for connection leaks (unclosed connections)
- Monitor active connections
- Reduce connection timeout if too long

---

## Testing Your Implementation

### Manual Testing with CustomerApp

```bash
# Run the application
mvn exec:java -Dexec.mainClass="sh.surge.kunal.banking.utils.CustomerApp"

# Check database
mysql -u root -p
USE bankingdb2026;
SELECT * FROM customer;
```

### Testing Individual Operations

Modify CustomerApp to test specific operations:

```java
// Test UPDATE
Customer customer = customerRepository.getCustomerById(1234567890L);
customer.setContactNo(9999999999L);
boolean updated = customerRepository.updateCustomer(customer);
System.out.println("Updated: " + updated);

// Test DELETE
boolean deleted = customerRepository.deleteCustomer(1234567890L);
System.out.println("Deleted: " + deleted);

// Test READ All
List<Customer> all = customerRepository.getAllCustomers();
System.out.println("Total customers: " + all.size());
all.forEach(System.out::println);
```

---

## Advantages of This Implementation

1. **Clean Architecture**: Separation of concerns (Repository, Service, Configuration)
2. **Maintainability**: SQL queries externalized in properties file
3. **Testability**: Repository interface allows easy mocking
4. **Performance**: HikariCP provides optimal connection pooling
5. **Flexibility**: Easy to swap implementations or add caching
6. **Scalability**: Connection pool handles concurrent requests efficiently
7. **Type Safety**: Compile-time checking with Java objects
8. **Exception Handling**: Spring's DataAccessException hierarchy

---

## When to Use This Pattern

✅ **Use Spring JDBC when:**
- You need fine-grained control over SQL
- Performance is critical
- Working with legacy databases
- Complex queries with joins and aggregations
- Database schema doesn't map well to OOP
- Team is comfortable with SQL

✅ **Consider adding:**
- Caching layer (Redis, Caffeine)
- Read replicas for scaling reads
- Database monitoring (slow query log)
- Connection pool metrics

---

## Extending This Implementation

### Add Pagination

```java
public List<Customer> getCustomers(int page, int size) {
    int offset = page * size;
    return jdbcTemplate.query(
        "SELECT * FROM customer LIMIT ? OFFSET ?",
        this::mapRowToCustomer,
        size, offset
    );
}
```

### Add Search Functionality

```java
public List<Customer> searchByName(String name) {
    return jdbcTemplate.query(
        "SELECT * FROM customer WHERE first_name LIKE ? OR last_name LIKE ?",
        this::mapRowToCustomer,
        "%" + name + "%",
        "%" + name + "%"
    );
}
```

### Add Custom Validation

```java
public boolean customerExists(long accountNo) {
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM customer WHERE account_no = ?",
        Integer.class,
        accountNo
    );
    return count != null && count > 0;
}
```

---

## Migration Path

### From Spring JDBC to Spring Data JPA

If you decide to migrate to JPA later:

1. Keep the Repository interface unchanged
2. Create new JPA implementation
3. Switch implementations without changing business logic

```java
@Repository
public class CustomerJpaRepository implements CustomerRepository {
    @Autowired
    private EntityManager entityManager;
    
    // Implement methods using JPA
}
```

---

## Conclusion

This Spring JDBC implementation demonstrates a clean, efficient approach to database access in Spring applications. By using:
- **JdbcTemplate** for simplified database operations
- **HikariCP** for high-performance connection pooling
- **Repository Pattern** for clean architecture
- **Externalized Configuration** for maintainability

You get a robust, production-ready data access layer that's easy to understand, maintain, and extend.

---

## Additional Resources

- [Spring JDBC Documentation](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html)
- [HikariCP GitHub](https://github.com/brettwooldridge/HikariCP)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Spring Framework Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/)

---

## License

This project is open-source and available for educational purposes.

## Author

Kunal - [GitHub Profile](https://github.com/Kunal70616c)
