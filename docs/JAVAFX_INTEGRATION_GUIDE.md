# Elevate ERP - JavaFX Integration Guide

## Quick Reference for Frontend Developers

This guide provides a comprehensive overview of the Elevate ERP backend APIs for JavaFX desktop application development.

---

## Base Configuration

### Server Details
- **Base URL**: `http://localhost:8080`
- **Default Port**: 8080 (Spring Boot default)
- **Content-Type**: `application/json`
- **Authentication**: Session-based with UUID tokens

### Authentication Header
```http
Session-Key: <36-character-uuid>
```

---

## Module Overview

Elevate ERP consists of four main modules:

| Module | Base Path | Description |
|--------|-----------|-------------|
| **AUTH** | `/auth` | Authentication, user management, tenant management |
| **CRM** | `/customers`, `/customerBalance`, `/customerLedger` | Customer relationship management |
| **FNA** | `/invoices`, `/payments` | Financial management (invoices, payments) |
| **HRS** | `/employees`, `/attendance`, `/payroll` | Human resources management |
| **INSC** | `/products`, `/stock`, `/suppliers` | Inventory & supply chain |

---

## Authentication Flow (AUTH Module)

### Public Endpoints (No Session-Key Required)
1. **Tenant Registration**: `POST /auth/tenantRegister`
2. **User Creation**: `POST /auth/createUser`
3. **User Login**: `POST /auth/userLogin`

### Login Flow
```
1. User enters: tenantId, username, password
2. POST /auth/userLogin
3. Receive: { sessionToken, user }
4. Store sessionToken in memory
5. Include "Session-Key: <sessionToken>" in all subsequent requests
```

### Sample Login Request
```json
POST http://localhost:8080/auth/userLogin
Content-Type: application/json

{
  "tenantId": "11111111-2222-3333-4444-555555555555",
  "username": "john.doe",
  "password": "yourPassword"
}
```

### Sample Login Response
```json
{
  "message": "User found successfully",
  "code": 200,
  "data": {
    "user": {
      "id": "user-uuid",
      "tenantId": "11111111-2222-3333-4444-555555555555",
      "username": "john.doe",
      "email": "john@example.com",
      "role": "USER"
    },
    "sessionToken": "2d5f6a20-2bb7-4f62-9e9b-2f0b3a97b8ba"
  }
}
```

### Logout
```
POST /auth/userLogout
Session-Key: <sessionToken>
```

**Full Documentation**: [auth.md](./auth.md)

---

## CRM Module (Customer Management)

### Customer Operations

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/customers/createCustomer` | POST | Create new customer |
| `/customers/getAllCustomers` | GET | List all customers |
| `/customers/getCustomerById/{id}` | GET | Get customer details |
| `/customers/updateCustomer/{id}` | PUT | Update customer |
| `/customers/deleteCustomer/{id}` | DELETE | Delete customer |

### Customer Balance

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/customerBalance/getAllBalances` | GET | Get all balances (paginated) |
| `/customerBalance/getBalanceByCustomer/{customerId}` | GET | Get specific customer balance |

### Customer Ledger

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/customerLedger/getAllEntries` | GET | Get all ledger entries (paginated) |
| `/customerLedger/getEntriesByCustomerId/{customerId}` | GET | Get customer ledger (paginated) |

### Sample Customer Creation
```json
POST http://localhost:8080/customers/createCustomer
Session-Key: <token>
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "source": "Website",
  "notes": "VIP customer"
}
```

**Full Documentation**: [crm.md](./crm.md)

---

## FNA Module (Financial Management)

### Invoice Operations

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/invoices/createInvoice` | POST | Create invoice |
| `/invoices/getAllInvoices` | GET | List all invoices (paginated) |
| `/invoices/getInvoiceById/{id}` | GET | Get invoice details |
| `/invoices/updateInvoice/{id}` | PUT | Update invoice |
| `/invoices/deleteInvoice/{id}` | DELETE | Delete invoice |

### Payment Operations

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/payments/createPayment` | POST | Record payment |
| `/payments/getAllPayments` | GET | List all payments (paginated) |
| `/payments/getPaymentById/{id}` | GET | Get payment details |
| `/payments/deletePayment/{id}` | DELETE | Delete/reverse payment |

**Full Documentation**: [fna.md](./fna.md)

---

## Common Response Wrapper

All endpoints return responses wrapped in `ApiResponse<T>`:

```json
{
  "message": "Human-readable message",
  "code": 200,
  "data": { ... }
}
```

### Success Codes
- `200`: OK (read/update/delete successful)
- `201`: Created (resource created successfully)

### Error Codes
- `400`: Bad Request (validation failure)
- `401`: Unauthorized (missing/invalid session)
- `404`: Not Found (resource doesn't exist)
- `409`: Conflict (duplicate data, e.g., phone number)
- `500`: Internal Server Error

---

## Pagination Structure

Paginated endpoints return:

```json
{
  "message": "Success message",
  "code": 200,
  "data": {
    "content": [...],        // Array of results
    "page": 0,               // Current page (0-indexed)
    "size": 20,              // Items per page
    "totalElements": 45,     // Total records
    "totalPages": 3          // Total pages
  }
}
```

**Query Parameters**:
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)

**Example**: `GET /customers/getAllCustomers?page=0&size=10`

---

## JavaFX Project Structure

### Recommended Package Structure
```
com.yourcompany.elevate.javafx
├── Main.java
├── controllers/
│   ├── LoginController.java
│   ├── DashboardController.java
│   ├── CustomerController.java
│   ├── InvoiceController.java
│   └── ...
├── models/
│   ├── User.java
│   ├── Customer.java
│   ├── Invoice.java
│   └── ...
├── services/
│   ├── ApiClient.java
│   ├── AuthService.java
│   ├── CustomerService.java
│   └── ...
├── utils/
│   ├── SessionManager.java
│   ├── HttpUtil.java
│   └── AlertUtil.java
└── views/
    ├── login.fxml
    ├── dashboard.fxml
    ├── customers.fxml
    └── ...
```

---

## Essential Classes to Build

### 1. SessionManager (Singleton)
```java
public class SessionManager {
    private static SessionManager instance;
    private String sessionToken;
    private User currentUser;
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setSession(String token, User user) {
        this.sessionToken = token;
        this.currentUser = user;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isAuthenticated() {
        return sessionToken != null && !sessionToken.isEmpty();
    }
    
    public void clearSession() {
        this.sessionToken = null;
        this.currentUser = null;
    }
}
```

### 2. ApiClient (Base HTTP Client)
```java
public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    
    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public <T> ApiResponse<T> get(String endpoint, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Session-Key", SessionManager.getInstance().getSessionToken())
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseResponse(response.body(), responseType);
    }
    
    public <T> ApiResponse<T> post(String endpoint, Object body, Class<T> responseType) throws Exception {
        String jsonBody = new ObjectMapper().writeValueAsString(body);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json");
        
        // Add Session-Key if authenticated
        String token = SessionManager.getInstance().getSessionToken();
        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Session-Key", token);
        }
        
        HttpRequest request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseResponse(response.body(), responseType);
    }
    
    // Similar methods for PUT, DELETE
    
    private <T> ApiResponse<T> parseResponse(String json, Class<T> dataType) {
        ObjectMapper mapper = new ObjectMapper();
        // Configure for LocalDateTime parsing
        mapper.registerModule(new JavaTimeModule());
        
        // Parse the ApiResponse wrapper
        JavaType type = mapper.getTypeFactory()
            .constructParametricType(ApiResponse.class, dataType);
        
        return mapper.readValue(json, type);
    }
}
```

### 3. AuthService
```java
public class AuthService {
    private final ApiClient apiClient;
    
    public AuthService() {
        this.apiClient = new ApiClient();
    }
    
    public LoginResponse login(String tenantId, String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(tenantId, username, password);
        ApiResponse<Map<String, Object>> response = apiClient.post("/auth/userLogin", request, Map.class);
        
        if (response.getCode() == 200) {
            // Extract user and sessionToken from response.data
            Map<String, Object> data = response.getData();
            String sessionToken = (String) data.get("sessionToken");
            Map<String, Object> userData = (Map<String, Object>) data.get("user");
            
            User user = mapToUser(userData);
            SessionManager.getInstance().setSession(sessionToken, user);
            
            return new LoginResponse(true, response.getMessage(), user);
        } else {
            return new LoginResponse(false, response.getMessage(), null);
        }
    }
    
    public void logout() throws Exception {
        apiClient.post("/auth/userLogout", null, Void.class);
        SessionManager.getInstance().clearSession();
    }
}
```

---

## Error Handling Strategy

### 1. Display User-Friendly Messages
```java
public void handleApiError(ApiResponse<?> response) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Operation Failed");
    alert.setContentText(response.getMessage());
    alert.showAndWait();
}
```

### 2. Handle Network Errors
```java
try {
    ApiResponse<Customer> response = customerService.createCustomer(customer);
    if (response.getCode() >= 200 && response.getCode() < 300) {
        showSuccess("Customer created successfully!");
    } else {
        handleApiError(response);
    }
} catch (IOException e) {
    showError("Network error. Please check your connection.");
} catch (Exception e) {
    showError("Unexpected error: " + e.getMessage());
}
```

### 3. Session Expiration Handling
```java
if (response.getCode() == 401) {
    showError("Session expired. Please login again.");
    SessionManager.getInstance().clearSession();
    navigateToLogin();
}
```

---

## Validation Best Practices

### Client-Side Validation
Validate inputs before making API calls to reduce unnecessary network traffic:

```java
private boolean validateCustomerForm() {
    if (nameField.getText().trim().isEmpty()) {
        showError("Customer name is required");
        return false;
    }
    
    if (nameField.getText().length() > 255) {
        showError("Customer name must be less than 255 characters");
        return false;
    }
    
    if (!phoneField.getText().isEmpty() && phoneField.getText().length() > 20) {
        showError("Phone number must be less than 20 characters");
        return false;
    }
    
    return true;
}
```

---

## Dependencies for JavaFX Project

### Maven Dependencies
```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21</version>
    </dependency>
    
    <!-- HTTP Client (Java 11+) is built-in -->
    
    <!-- JSON Processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
        <version>2.16.0</version>
    </dependency>
    
    <!-- Lombok (Optional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## Testing Your Integration

### 1. Test Authentication
- [ ] Login with valid credentials
- [ ] Login with invalid credentials (wrong password)
- [ ] Login with non-existent user
- [ ] Logout and verify session is cleared

### 2. Test CRUD Operations (Example: Customers)
- [ ] Create customer with all fields
- [ ] Create customer with only required fields
- [ ] View customer list
- [ ] Update customer
- [ ] Delete customer
- [ ] Handle duplicate phone validation

### 3. Test Pagination
- [ ] Navigate through pages
- [ ] Change page size
- [ ] Verify total counts

### 4. Test Error Scenarios
- [ ] Network disconnection
- [ ] Session expiration (401)
- [ ] Invalid input (400)
- [ ] Resource not found (404)
- [ ] Duplicate data (409)

---

## Security Considerations

### 1. Session Token Storage
- **In Memory**: Store in `SessionManager` singleton
- **Persistent**: If needed, encrypt before saving to disk
- **Never**: Store in plain text files

### 2. Password Handling
- Never log passwords
- Clear password fields after login
- Use PasswordField (masked input)

### 3. HTTPS in Production
- Update BASE_URL to `https://` in production
- Validate SSL certificates

---

## Performance Tips

### 1. Lazy Loading
- Load data on-demand
- Use pagination for large lists
- Cache frequently accessed data

### 2. Async Operations
```java
Task<List<Customer>> loadCustomersTask = new Task<>() {
    @Override
    protected List<Customer> call() throws Exception {
        return customerService.getAllCustomers();
    }
};

loadCustomersTask.setOnSucceeded(event -> {
    List<Customer> customers = loadCustomersTask.getValue();
    customerTable.getItems().setAll(customers);
});

loadCustomersTask.setOnFailed(event -> {
    showError("Failed to load customers");
});

new Thread(loadCustomersTask).start();
```

### 3. Connection Pooling
Reuse HttpClient instance instead of creating new ones for each request.

---

## Next Steps

1. **Start with Authentication**:
   - Build login screen
   - Implement session management
   - Test login/logout flow

2. **Build Core Modules**:
   - Customer management (CRM)
   - Invoice management (FNA)
   - Basic dashboard

3. **Add Advanced Features**:
   - Search and filtering
   - Reporting
   - Data export
   - Charts and analytics

---

## Resources

- **Full API Documentation**:
  - [Authentication (AUTH)](./auth.md)
  - [Customer Management (CRM)](./crm.md)
  - [Financial Management (FNA)](./fna.md)
  - [HR Management (HRS)](./hrs.md) *(coming soon)*
  - [Inventory (INSC)](./insc.md) *(coming soon)*

- **Backend Repository**: [Link to your repo]
- **JavaFX Documentation**: https://openjfx.io/
- **Jackson JSON**: https://github.com/FasterXML/jackson

---

**Happy Coding! 🚀**

If you encounter any issues or need clarification on any endpoint, refer to the module-specific documentation or reach out to the backend team.

