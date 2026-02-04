# CRM (Customer Relationship Management) API Documentation

## Overview
The CRM module manages customer data, customer balances, and customer ledger entries (transaction history). All endpoints require authentication via the `Session-Key` header.

**Base URL**: `http://localhost:8080`

**Authentication**: All endpoints require `Session-Key: <uuid>` header

**Tenant Context**: Tenant ID is automatically extracted from the session token; all queries are scoped to the authenticated user's tenant.

---

## Table of Contents
1. [Customer Management](#customer-management)
2. [Customer Balance](#customer-balance)
3. [Customer Ledger](#customer-ledger)
4. [DTOs Reference](#dtos-reference)
5. [Business Logic](#business-logic)

---

## Customer Management

### Base Path: `/customers`

### 1. Create Customer
**POST** `/customers/createCustomer`

Creates a new customer and automatically initializes their balance record.

**Headers**:
```http
Session-Key: <uuid>
Content-Type: application/json
```

**Request Body**: `CustomerReqDTO`
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St, City, State, ZIP",
  "source": "Website",
  "notes": "VIP customer"
}
```

**Field Constraints**:
- `name`: Required, max 255 characters
- `email`: Optional, max 255 characters
- `phone`: Optional, max 20 characters (must be unique per tenant)
- `address`: Optional, TEXT
- `source`: Optional, max 100 characters (e.g., "Website", "Referral", "Direct")
- `notes`: Optional, TEXT

**Success Response (201)**:
```json
{
  "message": "Customer created successfully",
  "code": 201,
  "data": {
    "id": 1,
    "tenantId": "11111111-2222-3333-4444-555555555555",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St, City, State, ZIP",
    "source": "Website",
    "notes": "VIP customer",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
}
```

**Error Responses**:
- `409`: Customer with this phone already exists

---

### 2. Get All Customers
**GET** `/customers/getAllCustomers`

Retrieves all customers for the authenticated tenant.

**Headers**:
```http
Session-Key: <uuid>
```

**Success Response (200)**:
```json
{
  "message": "Customers retrieved successfully",
  "code": 200,
  "data": [
    {
      "id": 1,
      "tenantId": "11111111-2222-3333-4444-555555555555",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "1234567890",
      "address": "123 Main St",
      "source": "Website",
      "notes": "VIP customer",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    },
    {
      "id": 2,
      "tenantId": "11111111-2222-3333-4444-555555555555",
      "name": "Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "0987654321",
      "address": "456 Oak Ave",
      "source": "Referral",
      "notes": null,
      "createdAt": "2025-01-16T14:20:00",
      "updatedAt": "2025-01-16T14:20:00"
    }
  ]
}
```

---

### 3. Get Customer By ID
**GET** `/customers/getCustomerById/{id}`

Retrieves a single customer by their ID.

**Headers**:
```http
Session-Key: <uuid>
```

**Path Parameters**:
- `id` (Long): Customer ID

**Example**: `GET /customers/getCustomerById/1`

**Success Response (200)**:
```json
{
  "message": "Customer retrieved successfully",
  "code": 200,
  "data": {
    "id": 1,
    "tenantId": "11111111-2222-3333-4444-555555555555",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St",
    "source": "Website",
    "notes": "VIP customer",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
}
```

**Error Responses**:
- `404`: Customer not found

---

### 4. Update Customer
**PUT** `/customers/updateCustomer/{id}`

Updates an existing customer. Only provided fields will be updated (partial update supported).

**Headers**:
```http
Session-Key: <uuid>
Content-Type: application/json
```

**Path Parameters**:
- `id` (Long): Customer ID

**Request Body**: `CustomerReqDTO` (all fields optional for update)
```json
{
  "name": "John Doe Jr.",
  "email": "john.doe.jr@example.com",
  "phone": "1234567890",
  "address": "789 New Street",
  "source": "Website",
  "notes": "Updated notes"
}
```

**Success Response (200)**:
```json
{
  "message": "Customer updated successfully",
  "code": 200,
  "data": {
    "id": 1,
    "tenantId": "11111111-2222-3333-4444-555555555555",
    "name": "John Doe Jr.",
    "email": "john.doe.jr@example.com",
    "phone": "1234567890",
    "address": "789 New Street",
    "source": "Website",
    "notes": "Updated notes",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-17T09:15:00"
  }
}
```

**Error Responses**:
- `404`: Customer not found

---

### 5. Delete Customer
**DELETE** `/customers/deleteCustomer/{id}`

Deletes a customer permanently.

**Headers**:
```http
Session-Key: <uuid>
```

**Path Parameters**:
- `id` (Long): Customer ID

**Example**: `DELETE /customers/deleteCustomer/1`

**Success Response (200)**:
```json
{
  "message": "Customer deleted successfully",
  "code": 200,
  "data": null
}
```

**Error Responses**:
- `404`: Customer not found

---

## Customer Balance

### Base Path: `/customerBalance`

Customer balances track the financial relationship with each customer using double-entry accounting:
- **Total Debit**: Amount customer owes (invoices)
- **Total Credit**: Amount paid by customer (payments)
- **Balance**: Total Debit - Total Credit (computed field)

### 1. Get All Customer Balances (Paginated)
**GET** `/customerBalance/getAllBalances`

Retrieves balances for all customers in the tenant with pagination support.

**Headers**:
```http
Session-Key: <uuid>
```

**Query Parameters** (Optional):
- `page` (Integer): Page number (0-indexed, default: 0)
- `size` (Integer): Page size (default: 20)

**Example**: `GET /customerBalance/getAllBalances?page=0&size=10`

**Success Response (200)**:
```json
{
  "message": "Customer balances retrieved",
  "code": 200,
  "data": {
    "content": [
      {
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 1,
        "totalDebit": 5000.00,
        "totalCredit": 3000.00,
        "balance": 2000.00
      },
      {
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 2,
        "totalDebit": 1500.00,
        "totalCredit": 1500.00,
        "balance": 0.00
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### 2. Get Balance for Specific Customer
**GET** `/customerBalance/getBalanceByCustomer/{customerId}`

Retrieves balance for a single customer.

**Headers**:
```http
Session-Key: <uuid>
```

**Path Parameters**:
- `customerId` (Long): Customer ID

**Example**: `GET /customerBalance/getBalanceByCustomer/1`

**Success Response (200)**:
```json
{
  "message": "Customer balance retrieved",
  "code": 200,
  "data": {
    "tenantId": "11111111-2222-3333-4444-555555555555",
    "customerId": 1,
    "totalDebit": 5000.00,
    "totalCredit": 3000.00,
    "balance": 2000.00
  }
}
```

**Error Responses**:
- `404`: Customer balance not found

**Understanding Balance**:
- **Positive Balance**: Customer owes money (outstanding invoices)
- **Zero Balance**: Customer account is settled
- **Negative Balance**: Credit balance (overpayment, rare)

---

## Customer Ledger

### Base Path: `/customerLedger`

The customer ledger records every financial transaction (invoices and payments) in chronological order.

### 1. Get All Ledger Entries (Paginated)
**GET** `/customerLedger/getAllEntries`

Retrieves all ledger entries for the tenant with pagination.

**Headers**:
```http
Session-Key: <uuid>
```

**Query Parameters** (Optional):
- `page` (Integer): Page number (0-indexed, default: 0)
- `size` (Integer): Page size (default: 20)

**Example**: `GET /customerLedger/getAllEntries?page=0&size=20`

**Success Response (200)**:
```json
{
  "message": "Ledger entries retrieved",
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 1,
        "referenceType": "INVOICE",
        "referenceId": 101,
        "entryType": "DEBIT",
        "amount": 5000.00,
        "description": "Invoice created",
        "createdAt": "2025-01-15T10:30:00"
      },
      {
        "id": 2,
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 1,
        "referenceType": "INVOICE",
        "referenceId": 101,
        "entryType": "CREDIT",
        "amount": 3000.00,
        "description": "Payment received",
        "createdAt": "2025-01-16T14:20:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### 2. Get Ledger Entries by Customer (Paginated)
**GET** `/customerLedger/getEntriesByCustomerId/{customerId}`

Retrieves all ledger entries for a specific customer.

**Headers**:
```http
Session-Key: <uuid>
```

**Path Parameters**:
- `customerId` (Long): Customer ID

**Query Parameters** (Optional):
- `page` (Integer): Page number (0-indexed, default: 0)
- `size` (Integer): Page size (default: 20)

**Example**: `GET /customerLedger/getEntriesByCustomerId/1?page=0&size=10`

**Success Response (200)**:
```json
{
  "message": "Customer ledger entries retrieved",
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 1,
        "referenceType": "INVOICE",
        "referenceId": 101,
        "entryType": "DEBIT",
        "amount": 5000.00,
        "description": "Invoice created",
        "createdAt": "2025-01-15T10:30:00"
      },
      {
        "id": 2,
        "tenantId": "11111111-2222-3333-4444-555555555555",
        "customerId": 1,
        "referenceType": "INVOICE",
        "referenceId": 101,
        "entryType": "CREDIT",
        "amount": 3000.00,
        "description": "Payment received",
        "createdAt": "2025-01-16T14:20:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

## DTOs Reference

### CustomerReqDTO
Used for creating and updating customers.

```java
{
  "name": String,          // Required, max 255 chars
  "email": String,         // Optional, max 255 chars
  "phone": String,         // Optional, max 20 chars (unique per tenant)
  "address": String,       // Optional, TEXT
  "source": String,        // Optional, max 100 chars
  "notes": String          // Optional, TEXT
}
```

### CustomerResDTO
Response DTO for customer data.

```java
{
  "id": Long,
  "tenantId": String,
  "name": String,
  "email": String,
  "phone": String,
  "address": String,
  "source": String,
  "notes": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### CustomerBalanceResDTO
Response DTO for customer balance.

```java
{
  "tenantId": String,
  "customerId": Long,
  "totalDebit": BigDecimal,    // Amount owed by customer
  "totalCredit": BigDecimal,   // Amount paid by customer
  "balance": BigDecimal        // totalDebit - totalCredit (computed)
}
```

### CustomerLedgerResDTO
Response DTO for ledger entries.

```java
{
  "id": Long,
  "tenantId": String,
  "customerId": Long,
  "referenceType": String,     // "INVOICE" or "PAYMENT"
  "referenceId": Long,         // ID of the invoice/payment
  "entryType": String,         // "DEBIT" or "CREDIT"
  "amount": BigDecimal,
  "description": String,
  "createdAt": LocalDateTime
}
```

---

## Business Logic

### Customer Creation Flow
1. Frontend sends `POST /customers/createCustomer` with customer data
2. Backend validates unique phone number per tenant
3. Creates customer record
4. **Automatically creates a balance record** with zero debit/credit
5. Returns customer details

### Balance Tracking
- **Balance is automatically managed** by the system when:
  - Invoices are created → DEBIT entry added
  - Payments are received → CREDIT entry added
  - Payments are deleted → DEBIT entry added (reversal)
- **Balance formula**: `balance = totalDebit - totalCredit`
- **Computed at database level** (not updatable via API)

### Ledger Entry Types
- **ReferenceType**:
  - `INVOICE`: Entry related to an invoice
  - `PAYMENT`: Entry related to a payment
- **EntryType**:
  - `DEBIT`: Increases amount owed (invoices)
  - `CREDIT`: Decreases amount owed (payments)

### Data Relationships
- Each customer has ONE balance record (composite key: tenantId + customerId)
- Each customer can have MANY ledger entries
- Ledger entries are created automatically by invoice/payment operations
- Balance is updated automatically when ledger entries are created

---

## Error Handling

All endpoints return the standard `ApiResponse` wrapper:

```json
{
  "message": "Error description",
  "code": 4xx or 5xx,
  "data": null
}
```

**Common Error Codes**:
- `400`: Bad Request (validation failure)
- `401`: Unauthorized (missing/invalid session key)
- `404`: Resource not found
- `409`: Conflict (duplicate phone number)
- `500`: Internal server error

---

## Pagination Response Structure

Paginated endpoints return:

```json
{
  "content": [...],           // Array of results
  "page": 0,                  // Current page number (0-indexed)
  "size": 20,                 // Page size
  "totalElements": 45,        // Total number of records
  "totalPages": 3             // Total number of pages
}
```

**Frontend Pagination Best Practices**:
- Default page size is 20
- Pages are 0-indexed (first page = 0)
- Use `totalPages` to render page navigation
- Use `totalElements` for displaying "Showing X of Y results"

---

## JavaFX Integration Notes

### Customer Management Screen
**Features to Implement**:
1. **Customer List View**:
   - Table with columns: ID, Name, Email, Phone, Source, Created Date
   - Call `GET /customers/getAllCustomers`
   - Add search/filter functionality (client-side)
   - Double-click to view details

2. **Create Customer Form**:
   - Fields: Name (required), Email, Phone, Address (textarea), Source (dropdown), Notes (textarea)
   - Validate name is not blank before submission
   - Call `POST /customers/createCustomer`
   - Show success message and refresh list

3. **Edit Customer Form**:
   - Pre-populate with existing data
   - Call `PUT /customers/updateCustomer/{id}`
   - Support partial updates

4. **Delete Customer**:
   - Confirmation dialog: "Are you sure you want to delete {customerName}?"
   - Call `DELETE /customers/deleteCustomer/{id}`
   - Refresh list after deletion

### Customer Balance Dashboard
**Features to Implement**:
1. **Balance Summary Cards**:
   - Total Outstanding: Sum of all positive balances
   - Total Customers with Outstanding Balance
   - Average Balance per Customer

2. **Balance List View**:
   - Table with columns: Customer Name, Total Debit, Total Credit, Balance
   - Call `GET /customerBalance/getAllBalances` with pagination
   - Color code: Red for high balances, Green for settled, Yellow for low
   - Click customer to view ledger details

3. **Pagination Controls**:
   - Previous/Next buttons
   - Page number selector
   - Page size dropdown (10, 20, 50, 100)

### Customer Ledger View
**Features to Implement**:
1. **Ledger Table**:
   - Columns: Date, Type (Invoice/Payment), Entry (Debit/Credit), Amount, Description
   - Running balance column (calculate client-side)
   - Filter by date range
   - Call `GET /customerLedger/getEntriesByCustomerId/{customerId}`

2. **Ledger Summary**:
   - Total Debits, Total Credits, Net Balance
   - Visual timeline of transactions

---

## Sample JavaFX Code Structure

### Models
```java
public class Customer {
    private Long id;
    private String tenantId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String source;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

public class CustomerBalance {
    private String tenantId;
    private Long customerId;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal balance;
}

public class CustomerLedger {
    private Long id;
    private String tenantId;
    private Long customerId;
    private String referenceType;
    private Long referenceId;
    private String entryType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}
```

### Service Class
```java
public class CustomerService {
    private final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private String sessionToken;
    
    public List<Customer> getAllCustomers() throws Exception {
        // GET /customers/getAllCustomers
    }
    
    public Customer createCustomer(Customer customer) throws Exception {
        // POST /customers/createCustomer
    }
    
    public Customer updateCustomer(Long id, Customer customer) throws Exception {
        // PUT /customers/updateCustomer/{id}
    }
    
    public void deleteCustomer(Long id) throws Exception {
        // DELETE /customers/deleteCustomer/{id}
    }
    
    public PaginatedResponse<CustomerBalance> getCustomerBalances(int page, int size) {
        // GET /customerBalance/getAllBalances?page={page}&size={size}
    }
    
    public PaginatedResponse<CustomerLedger> getCustomerLedger(Long customerId, int page, int size) {
        // GET /customerLedger/getEntriesByCustomerId/{customerId}?page={page}&size={size}
    }
}
```

---

## Testing Checklist

- [ ] Create customer with all fields
- [ ] Create customer with only required fields
- [ ] Get all customers (verify list)
- [ ] Get customer by ID
- [ ] Update customer (full update)
- [ ] Update customer (partial update)
- [ ] Delete customer
- [ ] Try to create customer with duplicate phone (should fail 409)
- [ ] Get balance for customer with transactions
- [ ] Get balance for newly created customer (should be 0/0/0)
- [ ] Get paginated balances (verify page navigation)
- [ ] Get customer ledger entries
- [ ] Verify balance = totalDebit - totalCredit
- [ ] Test unauthorized access (no Session-Key header)
- [ ] Test access to other tenant's customer (should fail 404)

---

**End of CRM API Documentation**

