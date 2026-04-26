# Elevate ERP — Frontend Development Guide

> Everything you need to build the frontend from scratch.
> Covers: pages, routing, API contracts, JSON payloads, auth flow, state management, and UI components.

---

## Table of Contents

1. [API Fundamentals](#1-api-fundamentals)
2. [Authentication Flow](#2-authentication-flow)
3. [Page Map & Routing](#3-page-map--routing)
4. [API Contract Reference](#4-api-contract-reference)
5. [Enum Constants Reference](#5-enum-constants-reference)
6. [Suggested UI Components](#6-suggested-ui-components)
7. [State Management Guide](#7-state-management-guide)
8. [Error Handling](#8-error-handling)

---

## 1. API Fundamentals

### Base URL

```
Development: http://localhost:8080
Swagger UI:  http://localhost:8080/swagger-ui.html
```

### Every Response Has This Shape

```json
{
  "message": "Success message or error description",
  "code": 200,
  "data": { }
}
```

Always check `code` for success (200/201) or failure (400/404/409/500). Display `message` to the user on errors.

### Paginated Responses

Endpoints with `?page=0&size=20` return:

```json
{
  "message": "...",
  "code": 200,
  "data": {
    "content": [ ... ],
    "totalElements": 156,
    "totalPages": 8,
    "number": 0,
    "size": 20,
    "first": true,
    "last": false
  }
}
```

### Required Headers

| Header | Value | When |
|--------|-------|------|
| `Content-Type` | `application/json` | All POST/PUT requests |
| `Session-Key` | `<uuid-token>` | **Every request** except `/auth/*` |

### HTTP Client Setup Example (Axios)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
});

// Add session token to every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('sessionToken');
  if (token) config.headers['Session-Key'] = token;
  return config;
});

// Handle 401 globally
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 2. Authentication Flow

### Step 1: Register Tenant (First-Time Only)

```
POST /auth/tenantRegister
```
```json
// Request
{
  "name": "My Business",
  "email": "admin@mybusiness.com",
  "planType": "FREE"
}

// Response
{
  "message": "Tenant created successfully",
  "code": 200,
  "data": {
    "id": "a1b2c3d4-...",
    "name": "My Business",
    "email": "admin@mybusiness.com",
    "planType": "FREE"
  }
}
```

### Step 2: Create First User

```
POST /auth/createUser
```
```json
// Request
{
  "tenantId": "a1b2c3d4-...",
  "username": "admin",
  "email": "admin@mybusiness.com",
  "password": "securepassword",
  "role": "ADMIN"
}
```

### Step 3: Login

```
POST /auth/userLogin
```
```json
// Request
{
  "tenantId": "a1b2c3d4-...",
  "username": "admin",
  "password": "securepassword"
}

// Response — SAVE THIS TOKEN
{
  "message": "Login successful",
  "code": 200,
  "data": {
    "sessionToken": "f8e7d6c5-...",
    "user": {
      "id": "...",
      "tenantId": "a1b2c3d4-...",
      "username": "admin",
      "email": "admin@mybusiness.com",
      "role": "ADMIN"
    }
  }
}
```

**Store in localStorage:**
```javascript
localStorage.setItem('sessionToken', data.sessionToken);
localStorage.setItem('tenantId', data.user.tenantId);
localStorage.setItem('username', data.user.username);
localStorage.setItem('role', data.user.role);
```

### Step 4: Logout

```
POST /auth/userLogout
// Header: Session-Key: <token>
```

### Auth Page Flow

```
/register  →  Register Tenant + Create Admin User  →  /login
/login     →  Login  →  Store token  →  /dashboard
Any 401    →  Clear storage  →  /login
```

---

## 3. Page Map & Routing

### Recommended Route Structure

```
/login                          Login page
/register                       Tenant registration + first user

/dashboard                      Main dashboard (summary, charts, alerts)

/customers                      Customer list
/customers/new                  Create customer form
/customers/:id                  Customer detail (profile + ledger + balance)

/products                       Product list (with barcode/SKU search)
/products/new                   Create product form
/products/:id                   Product detail + stock info

/categories                     Category list + inline create/edit

/warehouses                     Warehouse list
/warehouses/:id                 Warehouse detail + stock in this warehouse
/stock                          Stock levels (filter by warehouse)
/stock/movements                Stock movement history
/stock/transfers                Warehouse transfer list
/stock/transfers/new            Create transfer
/stock/alerts                   Low stock alerts

/invoices                       Invoice list (filter by status)
/invoices/new                   Create invoice form
/invoices/:id                   Invoice detail (items + payments + status)

/payments                       Payment list
/payments/new                   Record payment (select invoice)

/purchase-orders                PO list (filter by status/supplier)
/purchase-orders/new            Create PO form
/purchase-orders/:id            PO detail

/suppliers                      Supplier list + create

/expenses                       Expense list (filter by status/category/date)
/expenses/new                   Create expense form

/pricing                        Price lists overview
/pricing/new                    Create price list
/pricing/:id                    Price list items (add/remove tiers)
/pricing/resolve                Price resolver tool (check effective price)
/discounts                      Discount list + create

/employees                      Employee list
/employees/new                  Create employee form
/employees/:id                  Employee detail

/attendance                     Mark attendance + history

/leave-requests                 Leave request list
/leave-requests/new             Create leave request
/leave-requests/:id             Leave request detail (approve/reject)

/payroll                        Payroll list (filter by month/status)
/payroll/new                    Create payroll entry
/payroll/:id                    Payroll detail (approve/process/pay)

/performance-reviews            Review list
/performance-reviews/new        Create review form
/performance-reviews/:id        Review detail

/reports/profit-loss            P&L report (select month)
/reports/monthly-overview       Monthly finance overview
/reports/expenses               Expense summary by category
/reports/aging                  Aging report (receivables)
/reports/inventory-valuation    Inventory valuation

/settings/users                 User management (admin only)
```

### Sidebar Navigation Structure

```
Dashboard

SALES & CUSTOMERS
├── Customers
├── Invoices
├── Payments
└── Pricing & Discounts

INVENTORY
├── Products
├── Categories
├── Warehouses
├── Stock Levels
├── Stock Movements
├── Low Stock Alerts
├── Warehouse Transfers
├── Purchase Orders
└── Suppliers

FINANCE
├── Expenses
├── Payroll
└── Reports
    ├── Profit & Loss
    ├── Monthly Overview
    ├── Expense Summary
    ├── Aging Report
    └── Inventory Valuation

HR
├── Employees
├── Attendance
├── Leave Requests
└── Performance Reviews

SETTINGS
└── Users
```

---

## 4. API Contract Reference

### 4.1 Customers

#### Create Customer
```
POST /customers/createCustomer
```
```json
{
  "name": "Acme Wholesale Ltd",
  "email": "contact@acme.com",
  "phone": "+919876543210",
  "address": "123 Business Park, Mumbai",
  "source": "Referral",
  "notes": "Premium wholesale client",
  "customerType": "WHOLESALE",
  "creditLimit": 500000.00,
  "paymentTermsDays": 30,
  "taxId": "GSTIN12345678"
}
```

**Response:**
```json
{
  "code": 201,
  "message": "Customer created successfully",
  "data": {
    "id": 1,
    "tenantId": "a1b2c3d4-...",
    "name": "Acme Wholesale Ltd",
    "email": "contact@acme.com",
    "phone": "+919876543210",
    "address": "123 Business Park, Mumbai",
    "source": "Referral",
    "notes": "Premium wholesale client",
    "customerType": "WHOLESALE",
    "creditLimit": 500000.00,
    "paymentTermsDays": 30,
    "taxId": "GSTIN12345678",
    "createdAt": "2026-04-05T10:30:00",
    "updatedAt": "2026-04-05T10:30:00"
  }
}
```

#### Other Customer Endpoints
| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/customers/getAllCustomers` |
| Get one | GET | `/customers/getCustomerById/{id}` |
| Update | PUT | `/customers/updateCustomer/{id}` (same body as create, all fields optional) |
| Delete | DELETE | `/customers/deleteCustomer/{id}` |
| Get balance | GET | `/customerBalance/getBalanceByCustomer/{id}` |
| All balances | GET | `/customerBalance/getAllBalances?page=0&size=20` |
| Customer ledger | GET | `/customerLedger/getEntriesByCustomerId/{id}?page=0&size=20` |

**Customer Balance Response:**
```json
{
  "tenantId": "...",
  "customerId": 1,
  "totalDebit": 50000.00,
  "totalCredit": 30000.00,
  "balance": 20000.00
}
```

**Customer Ledger Entry Response:**
```json
{
  "id": 1,
  "customerId": 1,
  "referenceType": "INVOICE",
  "referenceId": 42,
  "entryType": "DEBIT",
  "amount": 15000.00,
  "description": "Invoice #INV-20260405-0001",
  "createdAt": "2026-04-05T10:30:00"
}
```

---

### 4.2 Products

#### Create Product
```
POST /insc/createProduct
```
```json
{
  "categoryId": "uuid-of-category",
  "name": "Widget Pro",
  "description": "Premium widget for industrial use",
  "sku": "WDG-PRO-001",
  "barcode": "8901234567890",
  "unit": "PCS",
  "costPrice": 100.00,
  "sellingPrice": 150.00
}
```

**Response:**
```json
{
  "code": 200,
  "data": {
    "id": "uuid-of-product",
    "tenantId": "...",
    "categoryId": "uuid-of-category",
    "sku": "WDG-PRO-001",
    "barcode": "8901234567890",
    "name": "Widget Pro",
    "description": "Premium widget for industrial use",
    "unit": "PCS",
    "costPrice": 100.00,
    "sellingPrice": 150.00,
    "isActive": true,
    "createdAt": "...",
    "updatedAt": "..."
  }
}
```

#### Other Product Endpoints
| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/insc/getAllProducts` |
| Get one | GET | `/insc/getProductById/{id}` |
| Update | PUT | `/insc/updateProduct` (body: `{ id, name?, description?, costPrice?, sellingPrice?, categoryId? }`) |
| Delete | DELETE | `/insc/deleteProductById/{id}` |
| Barcode lookup | GET | `/insc/barcode/{barcode}` |
| SKU lookup | GET | `/insc/sku/{sku}` |

#### Categories
| Action | Method | URL | Body |
|--------|--------|-----|------|
| Create | POST | `/category/createCategory` | `{ "name": "Electronics" }` |
| List all | GET | `/category/getAllCategory` | — |
| Update | PUT | `/category/updateCategory` | `{ "id": "uuid", "name": "New Name" }` |
| Delete | DELETE | `/category/deleteCategory/{id}` | — |

---

### 4.3 Warehouses

#### Create Warehouse
```
POST /warehouses
```
```json
{
  "name": "North Warehouse",
  "code": "WH-NORTH",
  "address": "Industrial Area, Delhi",
  "isDefault": false
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/warehouses` |
| Get one | GET | `/warehouses/{id}` |
| Update | PUT | `/warehouses/{id}` |
| Deactivate | PUT | `/warehouses/{id}/deactivate` |

---

### 4.4 Stock Management

#### Stock Levels
```
GET /stock/levels?warehouseId=uuid&page=0&size=20
```
```json
{
  "data": {
    "content": [
      {
        "id": "uuid",
        "productId": "uuid",
        "productName": "Widget Pro",
        "warehouseId": "uuid",
        "warehouseName": "Main Warehouse",
        "quantity": 150,
        "reorderPoint": 20,
        "reorderQuantity": 50,
        "updatedAt": "...",
        "isLowStock": false,
        "lowStockThreshold": 20
      }
    ],
    "totalElements": 45,
    "totalPages": 3
  }
}
```

#### Low Stock Alerts
```
GET /stock/alerts/low-stock?threshold=10&page=0&size=20
```
```json
{
  "data": {
    "content": [
      {
        "productId": "uuid",
        "productName": "Widget Basic",
        "currentStock": 3,
        "threshold": 10,
        "shortfall": 7,
        "alertLevel": "CRITICAL"
      }
    ]
  }
}
```

#### Stock Movements
| Action | Method | URL |
|--------|--------|-----|
| All movements | GET | `/stock/movements?page=0&size=20` |
| By product | GET | `/stock/movements/product/{productId}?page=0&size=20` |
| By type | GET | `/stock/movements/type/IN` or `/stock/movements/type/OUT` |
| By date range | GET | `/stock/movements/date-range?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59` |
| By PO | GET | `/stock/movements/purchase-order/{poId}` |
| By invoice | GET | `/stock/movements/invoice/{invoiceId}` |

#### Adjust Stock
```
PUT /stock/levels/adjust
```
```json
{
  "productId": "uuid-of-product",
  "quantity": 100,
  "reference": "Physical count adjustment",
  "reason": "Annual inventory audit"
}
```

#### Warehouse Transfers
```
POST /stock/transfers
```
```json
{
  "fromWarehouseId": "uuid-source",
  "toWarehouseId": "uuid-destination",
  "productId": "uuid-product",
  "quantity": 50,
  "notes": "Restocking north warehouse"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List transfers | GET | `/stock/transfers?status=PENDING&page=0&size=20` |
| Get one | GET | `/stock/transfers/{id}` |
| Complete | PUT | `/stock/transfers/{id}/complete` |
| Cancel | PUT | `/stock/transfers/{id}/cancel` |

**Transfer Response:**
```json
{
  "id": "uuid",
  "fromWarehouseId": "uuid",
  "fromWarehouseName": "Main Warehouse",
  "toWarehouseId": "uuid",
  "toWarehouseName": "North Warehouse",
  "productId": "uuid",
  "productName": "Widget Pro",
  "quantity": 50,
  "status": "PENDING",
  "initiatedBy": "session-key",
  "notes": "Restocking",
  "createdAt": "...",
  "completedAt": null
}
```

---

### 4.5 Invoices

#### Create Invoice
```
POST /finance/createInvoice
```
```json
{
  "customerId": 1,
  "name": "Acme Wholesale Ltd",
  "email": "contact@acme.com",
  "phone": "+919876543210",
  "date": "2026-04-05",
  "status": "PENDING",
  "taxRate": 18.00,
  "discountAmount": 500.00,
  "paymentTermsDays": 30,
  "notes": "Bulk order - Q2 2026",
  "items": [
    { "productId": "uuid-product-1", "quantity": 10 },
    { "productId": "uuid-product-2", "quantity": 25 }
  ]
}
```

**Response:**
```json
{
  "code": 201,
  "data": {
    "invoiceId": 42,
    "invoiceNumber": "INV-20260405-0042",
    "tenantId": "...",
    "customerId": 1,
    "name": "Acme Wholesale Ltd",
    "status": "PENDING",
    "subtotal": 5250.00,
    "discountAmount": 500.00,
    "taxRate": 18.00,
    "taxAmount": 855.00,
    "totalAmount": 5605.00,
    "remainingAmount": 5605.00,
    "date": "2026-04-05",
    "dueDate": "2026-05-05",
    "paymentTermsDays": 30,
    "notes": "Bulk order - Q2 2026",
    "createdAt": "...",
    "updatedAt": "..."
  }
}
```

**Note:** Prices are auto-resolved via the Pricing Engine. You do NOT send `unitPrice` — the backend picks the best price for each product based on the customer type and quantity.

#### Other Invoice Endpoints
| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/finance/getAllInvoices` |
| Filter by status | GET | `/finance/getAllInvoices?status=PENDING` |
| Get one | GET | `/finance/getInvoiceById?id=42` |
| Update status | PUT | `/finance/invoices/42/PAID` |
| Cancel | PUT | `/finance/invoices/42/cancel` |
| Overdue list | GET | `/finance/invoices/overdue` |

---

### 4.6 Payments

#### Record Payment
```
POST /payments/createPayment
```
```json
{
  "invoiceId": 42,
  "customerId": 1,
  "amount": 3000.00,
  "method": "BANK_TRANSFER",
  "transactionRef": "TXN-2026040500123"
}
```

**Response:**
```json
{
  "code": 201,
  "data": {
    "id": "uuid",
    "tenantId": "...",
    "invoiceId": 42,
    "customerId": 1,
    "amount": 3000.00,
    "paymentDate": "2026-04-05T14:30:00",
    "method": "BANK_TRANSFER",
    "transactionRef": "TXN-2026040500123"
  }
}
```

The invoice status will auto-update:
- `remainingAmount == 0` → status becomes `PAID`
- `remainingAmount > 0 && < totalAmount` → status becomes `PARTIALLY_PAID`

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/payments/getAllPayments` |
| By invoice | GET | `/payments/getPaymentByInvoice/{invoiceId}` |
| Get one | GET | `/payments/getPaymentById/{id}` |
| Delete (reverse) | DELETE | `/payments/deletePayment/{id}` |

---

### 4.7 Purchase Orders

#### Create PO
```
POST /purchaseOrder/createNewPurchaseOrder
```
```json
{
  "supplierId": "uuid-of-supplier",
  "orderDate": "2026-04-05",
  "items": [
    { "productId": "uuid-product-1", "quantity": 100, "unitPrice": 80.00 },
    { "productId": "uuid-product-2", "quantity": 200, "unitPrice": 45.50 }
  ]
}
```

#### Create Supplier
```
POST /purchaseOrder/createNewSupplier
```
```json
{
  "name": "Global Parts Inc",
  "email": "sales@globalparts.com",
  "phone": "+911234567890",
  "address": "456 Industrial Zone, Chennai"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all POs | GET | `/purchaseOrder/getAllPurchaseOrder` |
| Get one | GET | `/purchaseOrder/getPurchaseOrderById/{id}` |
| Update status | PUT | `/purchaseOrder/updatePurchaseOrderStatus/{id}/RECEIVED` |
| By supplier | GET | `/purchaseOrder/getPurchaseOrderBySupplier/{supplierId}` |
| By status | GET | `/purchaseOrder/getPurchaseOrderByStatus/PENDING` |

---

### 4.8 Pricing

#### Create Price List
```
POST /price-lists
```
```json
{
  "name": "Wholesale Pricing Q2-2026",
  "customerType": "WHOLESALE",
  "isDefault": false,
  "effectiveFrom": "2026-04-01",
  "effectiveTo": "2026-06-30"
}
```

#### Add Price Tier
```
POST /price-lists/{priceListId}/items
```
```json
{
  "productId": "uuid-of-product",
  "unitPrice": 120.00,
  "minQuantity": 1,
  "maxQuantity": 9,
  "discountPercent": 0
}
```
```json
{
  "productId": "uuid-of-product",
  "unitPrice": 110.00,
  "minQuantity": 10,
  "maxQuantity": 49,
  "discountPercent": 5.0
}
```
```json
{
  "productId": "uuid-of-product",
  "unitPrice": 100.00,
  "minQuantity": 50,
  "maxQuantity": null,
  "discountPercent": 10.0
}
```

#### Resolve Price (for frontend display before invoicing)
```
GET /price-lists/resolve?productId=uuid&customerId=1&quantity=25
```
```json
{
  "data": {
    "productId": "uuid",
    "productName": "Widget Pro",
    "basePrice": 150.00,
    "resolvedPrice": 104.50,
    "discountPercent": 5.0,
    "priceListName": "Wholesale Pricing Q2-2026",
    "source": "CUSTOMER_TYPE"
  }
}
```

#### Create Discount
```
POST /discounts
```
```json
{
  "name": "Summer Sale 2026",
  "discountType": "PERCENTAGE",
  "value": 15.00,
  "appliesTo": "INVOICE",
  "minOrderAmount": 5000.00,
  "validFrom": "2026-06-01",
  "validTo": "2026-08-31"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/price-lists` |
| Get items | GET | `/price-lists/{id}/items` |
| Delete item | DELETE | `/price-lists/{id}/items/{itemId}` |
| Deactivate | PUT | `/price-lists/{id}/deactivate` |
| All discounts | GET | `/discounts` |
| Active only | GET | `/discounts/active` |
| Deactivate | PUT | `/discounts/{id}/deactivate` |

---

### 4.9 Expenses

#### Create Expense
```
POST /finance/expenses
```
```json
{
  "amount": 15000.00,
  "category": "RENT",
  "description": "Office rent for April 2026",
  "expenseDate": "2026-04-01",
  "referenceNumber": "RENT-APR-2026",
  "status": "PENDING"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/finance/expenses` |
| Get one | GET | `/finance/expenses/{id}` |
| By status | GET | `/finance/expenses/by-status/APPROVED` |
| By category | GET | `/finance/expenses/by-category/RENT` |
| By dates | GET | `/finance/expenses/by-date-range?startDate=2026-04-01&endDate=2026-04-30` |
| Update | PUT | `/finance/expenses/{id}` |
| Delete | DELETE | `/finance/expenses/{id}` |
| Approve | POST | `/finance/expenses/{id}/approve` |
| Reject | POST | `/finance/expenses/{id}/reject` |

---

### 4.10 HR — Employees

#### Create Employee
```
POST /api/hr/employees
```
```json
{
  "name": "John Doe",
  "email": "john@company.com",
  "phone": "+919876543210",
  "designation": "SOFTWARE_ENGINEER",
  "department": "Engineering",
  "dateOfJoining": "2025-01-15",
  "salary": 75000.00
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/api/hr/employees` |
| Get one | GET | `/api/hr/employees/{id}` |
| Update | PUT | `/api/hr/employees/{id}` |
| Delete | DELETE | `/api/hr/employees/{id}` |

#### Attendance
```
POST /api/hr/attendance
```
```json
{
  "employeeId": 1,
  "date": "2026-04-05",
  "status": "PRESENT"
}
```

| Action | Method | URL |
|--------|--------|-----|
| All attendance | GET | `/api/hr/attendance` |
| By employee | GET | `/api/hr/attendance/employee/{id}` |

---

### 4.11 HR — Leave Requests

#### Create Leave Request
```
POST /hr/leaves
```
```json
{
  "employeeId": 1,
  "leaveType": "CASUAL_LEAVE",
  "startDate": "2026-04-10",
  "endDate": "2026-04-12",
  "reason": "Family function"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/hr/leaves` |
| Get one | GET | `/hr/leaves/{id}` |
| By employee | GET | `/hr/leaves/employee/{id}` |
| By emp+status | GET | `/hr/leaves/employee/{id}/status/PENDING` |
| By dates | GET | `/hr/leaves/by-date-range?startDate=...&endDate=...` |
| Update | PUT | `/hr/leaves/{id}` |
| Delete | DELETE | `/hr/leaves/{id}` |
| Approve | POST | `/hr/leaves/{id}/approve?approvedBy=admin&comments=Approved` |
| Reject | POST | `/hr/leaves/{id}/reject?approvedBy=admin&comments=Insufficient leave balance` |

---

### 4.12 HR — Payroll

#### Create Payroll
```
POST /finance/payroll
```
```json
{
  "employeeId": 1,
  "yearMonth": "2026-04",
  "salary": 75000.00,
  "dearnessAllowance": 5000.00,
  "houseRentAllowance": 10000.00,
  "otherAllowances": 3000.00,
  "incomeTax": 7500.00,
  "providentFund": 4500.00,
  "professionalTax": 200.00,
  "otherDeductions": 0,
  "notes": "April 2026 salary"
}
```

**Response includes computed fields:**
```json
{
  "grossSalary": 93000.00,
  "basic": 75000.00,
  "netSalary": 80800.00
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/finance/payroll` |
| Get one | GET | `/finance/payroll/{id}` |
| By employee | GET | `/finance/payroll/employee/{id}` |
| By status | GET | `/finance/payroll/by-status/DRAFT` |
| By month | GET | `/finance/payroll/by-month/2026-04` |
| Update | PUT | `/finance/payroll/{id}` |
| Delete | DELETE | `/finance/payroll/{id}` |
| Submit | POST | `/finance/payroll/{id}/submit` |
| Approve | POST | `/finance/payroll/{id}/approve` |
| Process | POST | `/finance/payroll/{id}/process` |
| Mark Paid | POST | `/finance/payroll/{id}/pay` |

---

### 4.13 HR — Performance Reviews

#### Create Review
```
POST /hr/performance-reviews
```
```json
{
  "employeeId": 1,
  "reviewerName": "Jane Manager",
  "reviewDate": "2026-04-01",
  "rating": "EXCELLENT",
  "workQuality": "Consistently delivers high-quality code",
  "communication": "Clear and proactive communicator",
  "teamwork": "Great team player",
  "punctuality": "Always on time",
  "overallComments": "Outstanding performance this quarter",
  "improvementAreas": "Could take on more leadership tasks",
  "strengths": "Technical expertise, problem solving"
}
```

| Action | Method | URL |
|--------|--------|-----|
| List all | GET | `/hr/performance-reviews` |
| Get one | GET | `/hr/performance-reviews/{id}` |
| By employee | GET | `/hr/performance-reviews/employee/{id}` |
| By dates | GET | `/hr/performance-reviews/by-date-range?startDate=...&endDate=...` |
| Update | PUT | `/hr/performance-reviews/{id}` |
| Delete | DELETE | `/hr/performance-reviews/{id}` |

---

### 4.14 Dashboard & Reports

#### Dashboard Summary
```
GET /dashboard/summary
```
```json
{
  "data": {
    "currentMonthRevenue": 450000.00,
    "outstandingReceivables": 125000.00,
    "totalInvoices": 45,
    "paidInvoices": 30,
    "pendingInvoices": 10,
    "overdueInvoices": 5,
    "lowStockProductCount": 8,
    "currentMonthExpenses": 180000.00,
    "cashFlow": 270000.00
  }
}
```

#### Revenue Trend
```
GET /dashboard/revenue-trend?months=6
```
```json
{
  "data": [
    { "month": "2025-11", "revenue": 320000.00, "expenses": 150000.00, "netIncome": 170000.00 },
    { "month": "2025-12", "revenue": 380000.00, "expenses": 160000.00, "netIncome": 220000.00 },
    { "month": "2026-01", "revenue": 350000.00, "expenses": 155000.00, "netIncome": 195000.00 },
    { "month": "2026-02", "revenue": 400000.00, "expenses": 170000.00, "netIncome": 230000.00 },
    { "month": "2026-03", "revenue": 420000.00, "expenses": 175000.00, "netIncome": 245000.00 },
    { "month": "2026-04", "revenue": 450000.00, "expenses": 180000.00, "netIncome": 270000.00 }
  ]
}
```

#### Aging Report
```
GET /dashboard/aging-report
```
```json
{
  "data": {
    "currentAmount": 50000.00,
    "days1to30": 35000.00,
    "days31to60": 20000.00,
    "days61to90": 12000.00,
    "over90Days": 8000.00,
    "totalOutstanding": 125000.00,
    "customerBreakdown": [
      {
        "customerId": 1,
        "customerName": "Acme Wholesale",
        "currentAmount": 20000.00,
        "days1to30": 15000.00,
        "days31to60": 0,
        "days61to90": 0,
        "over90Days": 0,
        "total": 35000.00
      }
    ]
  }
}
```

#### Finance Reports
| Report | Method | URL |
|--------|--------|-----|
| P&L | GET | `/reports/finance/profit-loss/2026-04` |
| Monthly overview | GET | `/reports/finance/monthly-overview/2026-04` |
| Expense summary | GET | `/reports/finance/expense-summary/2026-04` |
| Year-to-date | GET | `/reports/finance/year-to-date?year=2026` |
| Inventory valuation | GET | `/reports/inventory/valuation` |

**Inventory Valuation Response:**
```json
{
  "data": {
    "totalProducts": 150,
    "totalUnits": 25000,
    "totalCostValue": 1875000.00,
    "totalRetailValue": 2500000.00,
    "potentialProfit": 625000.00
  }
}
```

---

## 5. Enum Constants Reference

Use these as dropdown/select options in your forms.

```javascript
export const ENUMS = {
  // Customer types
  CUSTOMER_TYPE: ['B2C', 'B2B', 'WHOLESALE', 'RETAIL'],

  // Invoice status
  INVOICE_STATUS: ['DRAFT', 'PENDING', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED'],

  // Payment methods
  PAYMENT_METHOD: ['CASH', 'CARD', 'BANK_TRANSFER', 'UPI'],

  // Expense status
  EXPENSE_STATUS: ['PENDING', 'APPROVED', 'REJECTED', 'PAID'],

  // Purchase order status
  PO_STATUS: ['PENDING', 'RECEIVED', 'CANCELLED'],

  // Stock movement types
  STOCK_MOVEMENT_TYPE: ['IN', 'OUT', 'TRANSFER', 'ADJUSTMENT'],

  // Warehouse transfer status
  TRANSFER_STATUS: ['PENDING', 'IN_TRANSIT', 'COMPLETED', 'CANCELLED'],

  // Discount types
  DISCOUNT_TYPE: ['PERCENTAGE', 'FIXED_AMOUNT'],
  DISCOUNT_APPLIES_TO: ['INVOICE', 'LINE_ITEM'],

  // Leave types
  LEAVE_TYPE: [
    'SICK_LEAVE', 'CASUAL_LEAVE', 'EARNED_LEAVE',
    'MATERNITY_LEAVE', 'PATERNITY_LEAVE', 'UNPAID_LEAVE',
    'PERSONAL_LEAVE', 'OTHERS'
  ],

  // Leave status
  LEAVE_STATUS: ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'],

  // Attendance
  ATTENDANCE_STATUS: ['PRESENT', 'ABSENT', 'HALF_DAY', 'LEAVE'],

  // Payroll status
  PAYROLL_STATUS: ['DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'PROCESSED', 'PAID', 'CANCELLED'],

  // Tenant plans
  PLAN_TYPE: ['FREE', 'PRO', 'ENTERPRISE'],

  // User roles
  ROLE: ['ADMIN', 'USER'],

  // Low stock alert levels (read-only, from API)
  ALERT_LEVEL: ['LOW', 'CRITICAL', 'OUT_OF_STOCK'],

  // Price resolution source (read-only, from API)
  PRICE_SOURCE: ['PRICE_LIST', 'CUSTOMER_TYPE', 'PRODUCT_DEFAULT'],

  // Finance trend (read-only, from API)
  TREND: ['INCREASING', 'DECREASING', 'STABLE'],

  // Ledger entries
  ENTRY_TYPE: ['DEBIT', 'CREDIT'],
  REFERENCE_TYPE: ['INVOICE', 'PAYMENT']
};
```

---

## 6. Suggested UI Components

### Dashboard Page
- **KPI Cards** — Revenue, Outstanding, Invoice Count, Low Stock, Cash Flow
- **Revenue Trend Chart** — Line/bar chart from `/dashboard/revenue-trend`
- **Aging Report Pie/Bar Chart** — From `/dashboard/aging-report`
- **Low Stock Alert Table** — From `/stock/alerts/low-stock`
- **Recent Invoices Table** — From `/finance/getAllInvoices` (top 5)

### Invoice Creation Page
- Customer selector dropdown (from `/customers/getAllCustomers`)
- Date picker for invoice date
- Tax rate input
- Discount amount input
- **Line items table:**
  - Product selector (from `/insc/getAllProducts`)
  - Quantity input
  - Resolved price display (call `/price-lists/resolve` on product+qty change)
  - Line total (auto-calculated)
- Subtotal, discount, tax, total display (auto-calculated)
- Submit button

### Stock Management Page
- Warehouse filter dropdown (from `/warehouses`)
- Stock levels table with search
- Color-coded rows: green (OK), yellow (low), red (critical/out)
- Quick adjust modal
- Link to movements history per product

### Payment Recording
- Invoice selector (show unpaid invoices only)
- Auto-display: total amount, already paid, remaining
- Amount input (max = remaining)
- Payment method dropdown
- Transaction reference input

---

## 7. State Management Guide

### What to Store Globally (Context/Redux/Zustand)

```javascript
// Auth state
{
  sessionToken: string | null,
  tenantId: string | null,
  username: string | null,
  role: 'ADMIN' | 'USER' | null,
  isAuthenticated: boolean
}
```

### What to Fetch Fresh Per Page (API calls)

Everything else — customers, products, invoices, stock, reports. Use SWR/React Query for caching + revalidation.

### Suggested React Query Keys

```javascript
const queryKeys = {
  customers:      ['customers'],
  customerById:   (id) => ['customers', id],
  products:       ['products'],
  categories:     ['categories'],
  warehouses:     ['warehouses'],
  stockLevels:    (warehouseId, page) => ['stock-levels', warehouseId, page],
  lowStock:       ['low-stock'],
  invoices:       (status, page) => ['invoices', status, page],
  invoiceById:    (id) => ['invoices', id],
  payments:       (invoiceId) => ['payments', invoiceId],
  priceLists:     ['price-lists'],
  dashboard:      ['dashboard'],
  revenueTrend:   (months) => ['revenue-trend', months],
  agingReport:    ['aging-report'],
};
```

---

## 8. Error Handling

### Error Response Shape

```json
{
  "message": "Customer with this phone already exists",
  "code": 409,
  "data": null
}
```

### Common Error Codes

| Code | Meaning | Frontend Action |
|------|---------|-----------------|
| 200 | Success | Show data |
| 201 | Created | Show success toast, redirect to list |
| 400 | Bad request / validation | Show field-level errors |
| 401 | Unauthorized | Redirect to `/login` |
| 404 | Not found | Show "not found" message |
| 409 | Conflict (duplicate) | Show "already exists" message |
| 500 | Server error | Show generic error toast |

### Validation Error (400 from `@Valid`)

```json
{
  "message": "name: Product name is required, costPrice: Cost price must be greater than or equal to 0",
  "code": 400,
  "data": null
}
```

Parse the `message` string by splitting on `, ` to show per-field errors.

### Suggested Toast/Notification Pattern

```javascript
function handleApiResponse(response) {
  const { code, message, data } = response.data;

  if (code >= 200 && code < 300) {
    toast.success(message);
    return data;
  } else {
    toast.error(message);
    throw new Error(message);
  }
}
```
