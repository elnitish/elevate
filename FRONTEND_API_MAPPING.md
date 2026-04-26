# 🔌 Frontend-Backend API Mapping

## Complete API Integration Guide

This document maps every frontend page to its required backend APIs.

---

## 📋 **AUTHENTICATION MODULE**

### 1. Login Page (`/login`)
**APIs Required:**
- `POST /auth/userLogin`
  - Request: `{ tenantId, username, password }`
  - Response: `{ sessionToken, user details }`

**State Updates:**
- Store session token
- Store user info (name, role, tenantId)
- Redirect to dashboard

---

### 2. Tenant Registration (`/register/tenant`)
**APIs Required:**
- `POST /auth/tenantRegister`
  - Request: `{ name, email, planType }`
  - Response: `{ tenantId, message }`

**Flow:**
- Submit form → Create tenant → Show success → Redirect to user registration

---

### 3. User Registration (`/register/user`)
**APIs Required:**
- `POST /auth/createUser`
  - Request: `{ tenantId, username, email, password, role }`
  - Response: `{ userId, message }`

**Flow:**
- Submit form → Create user → Show success → Redirect to login

---

## 🏠 **DASHBOARD**

### Main Dashboard (`/dashboard`)
**APIs Required:**
1. `GET /reports/finance/monthly-overview/{yearMonth}`
   - Get financial metrics for current month
   
2. `GET /stock/alerts/low-stock?threshold=10`
   - Get low stock count
   
3. `GET /hr/leaves?status=PENDING`
   - Get pending leave requests count
   
4. `GET /customers/getAllCustomers`
   - Get total customers count
   
5. `GET /finance/getAllInvoices?status=PENDING`
   - Get pending invoices count

**Widgets to Display:**
- Total Revenue (from monthly overview)
- Total Expenses (from monthly overview)
- Profit/Loss (calculated)
- Pending Invoices (count)
- Low Stock Items (count + clickable)
- Total Employees (from employees API)
- Pending Leaves (count)
- Total Customers (count)

---

## 💰 **FINANCE MODULE**

### 1. Invoices List (`/finance/invoices`)
**APIs Required:**
- `GET /finance/getAllInvoices`
  - Optional query: `?status=PENDING|PAID|CANCELLED`
  
**Actions:**
- View: Navigate to detail page
- Edit: Navigate to edit page
- Delete: Show confirmation, then call delete API (if exists)
- Update Status: `PUT /finance/invoices/{id}/{status}`

---

### 2. Create Invoice (`/finance/invoices/new`)
**APIs Required:**
1. `GET /customers/getAllCustomers` (for dropdown)
2. `GET /insc/getAllProducts` (for line items dropdown)
3. `POST /finance/createInvoice`
   - Request:
   ```json
   {
     "name": "Customer Name",
     "email": "customer@email.com",
     "phone": "1234567890",
     "customerId": 1,
     "date": "2026-02-05",
     "status": "PENDING",
     "items": [
       {
         "productId": "prod-123",
         "quantity": 5,
         "unitPrice": 100.00
       }
     ]
   }
   ```

**Flow:**
- Load customers & products → Fill form → Calculate totals → Submit → Redirect to list

---

### 3. Payments List (`/finance/payments`)
**APIs Required:**
- `GET /payments/getAllPayments`

**Filters:**
- By Invoice: `GET /payments/getPaymentByInvoice/{invoiceId}`
- By Date Range: Filter client-side or add backend filter

**Actions:**
- View: Show details modal
- Delete: `DELETE /payments/deletePayment/{paymentId}`

---

### 4. Create Payment (`/finance/payments/new`)
**APIs Required:**
1. `GET /finance/getAllInvoices?status=PENDING` (for dropdown)
2. `POST /payments/createPayment`
   - Request:
   ```json
   {
     "invoiceId": 1,
     "customerId": 1,
     "amount": 500.00,
     "method": "CASH",
     "transactionRef": "TXN123"
   }
   ```

---

### 5. Expenses List (`/finance/expenses`)
**APIs Required:**
- `GET /finance/expenses`

**Filters:**
- By Status: `GET /finance/expenses/by-status/{status}`
- By Category: `GET /finance/expenses/by-category/{category}`
- By Date Range: `GET /finance/expenses/by-date-range?startDate=&endDate=`

**Actions:**
- Create: `POST /finance/expenses`
- Update: `PUT /finance/expenses/{expenseId}`
- Delete: `DELETE /finance/expenses/{expenseId}`
- Approve: `POST /finance/expenses/{expenseId}/approve`
- Reject: `POST /finance/expenses/{expenseId}/reject`

---

### 6. Payroll List (`/finance/payroll`)
**APIs Required:**
- `GET /finance/payroll`

**Filters:**
- By Employee: `GET /finance/payroll/employee/{employeeId}`
- By Status: `GET /finance/payroll/by-status/{status}`
- By Month: `GET /finance/payroll/by-month/{yearMonth}`

**Actions:**
- Create: `POST /finance/payroll`
- Update: `PUT /finance/payroll/{payrollId}`
- Delete: `DELETE /finance/payroll/{payrollId}`
- Submit: `POST /finance/payroll/{payrollId}/submit`
- Approve: `POST /finance/payroll/{payrollId}/approve`
- Process: `POST /finance/payroll/{payrollId}/process`
- Pay: `POST /finance/payroll/{payrollId}/pay`

---

### 7. Finance Reports (`/finance/reports`)
**APIs Required:**
1. **Profit & Loss:**
   - `GET /reports/finance/profit-loss/{yearMonth}`
   
2. **Monthly Overview:**
   - `GET /reports/finance/monthly-overview/{yearMonth}`
   
3. **Year-to-Date:**
   - `GET /reports/finance/year-to-date?year={year}`

---

## 📦 **INVENTORY MODULE**

### 1. Products List (`/inventory/products`)
**APIs Required:**
- `GET /insc/getAllProducts`
- `GET /category/getAllCategory` (for filter dropdown)

**Actions:**
- Create: `POST /insc/createProduct`
- Update: `PUT /insc/updateProduct`
- Delete: `DELETE /insc/deleteProductById/{productId}`
- View: `GET /insc/getProductById/{productId}`

---

### 2. Categories (`/inventory/categories`)
**APIs Required:**
- `GET /category/getAllCategory`
- `POST /category/createCategory`
- `PUT /category/updateCategory`
- `DELETE /category/deleteCategory/{categoryId}`

---

### 3. Stock Levels (`/inventory/stock`)
**APIs Required:**
- `GET /stock/levels` (all stock levels)
- `GET /stock/levels/product/{productId}` (single product)
- `PUT /stock/levels/adjust` (manual adjustment)

**Display:**
- Product Name (join with products)
- Current Stock
- Last Updated
- Status (color-coded: green/yellow/red)

---

### 4. Stock Movements (`/inventory/stock/movements`)
**APIs Required:**
- `GET /stock/movements` (all movements)

**Filters:**
- By Product: `GET /stock/movements/product/{productId}`
- By Type: `GET /stock/movements/type/{type}` (IN or OUT)
- By Date Range: `GET /stock/movements/date-range?startDate=&endDate=`
- By PO: `GET /stock/movements/purchase-order/{purchaseOrderId}`
- By Invoice: `GET /stock/movements/invoice/{invoiceId}`

---

### 5. Low Stock Alerts (`/inventory/alerts`)
**APIs Required:**
- `GET /stock/alerts/low-stock?threshold=10`

**Display:**
- Product Name
- Current Stock
- Threshold
- Shortfall
- Alert Level (OUT_OF_STOCK/CRITICAL/LOW)

**Actions:**
- Create PO: Navigate to PO creation with pre-filled product
- Adjust Stock: Open adjustment modal

---

### 6. Suppliers (`/inventory/suppliers`)
**APIs Required:**
- `POST /purchaseOrder/createNewSupplier`
- ⚠️ **Missing:** GET, UPDATE, DELETE endpoints for suppliers

**Workaround:**
- Store suppliers locally after creation
- Or add backend endpoints

---

### 7. Purchase Orders (`/inventory/purchase-orders`)
**APIs Required:**
- `GET /purchaseOrder/getAllPurchaseOrder`

**Filters:**
- By Supplier: `GET /purchaseOrder/getPurchaseOrderBySupplier/{supplierId}`
- By Status: `GET /purchaseOrder/getPurchaseOrderByStatus/{status}`
- By ID: `GET /purchaseOrder/getPurchaseOrderById/{purchaseOrderId}`

**Actions:**
- Create: `POST /purchaseOrder/createNewPurchaseOrder`
- Update Status: `PUT /purchaseOrder/updatePurchaseOrderStatus/{purchaseOrderId}/{status}`

---

## 👥 **HR MODULE**

### 1. Employees (`/hr/employees`)
**APIs Required:**
- `GET /api/hr/employees`
- `POST /api/hr/employees`
- `GET /api/hr/employees/{id}`
- `PUT /api/hr/employees/{id}`
- `DELETE /api/hr/employees/{id}`

---

### 2. Attendance (`/hr/attendance`)
**APIs Required:**
- `GET /api/hr/attendance` (all attendance)
- `GET /api/hr/attendance/employee/{id}` (by employee)
- `POST /api/hr/attendance` (mark attendance)

**Mark Attendance Request:**
```json
{
  "employeeId": 1,
  "status": "PRESENT",
  "date": "2026-02-05"
}
```

---

### 3. Leave Requests (`/hr/leaves`)
**APIs Required:**
- `GET /hr/leaves` (all leaves)

**Filters:**
- By Employee: `GET /hr/leaves/employee/{employeeId}`
- By Status: `GET /hr/leaves/employee/{employeeId}/status/{status}`
- By Date Range: `GET /hr/leaves/by-date-range?startDate=&endDate=`

**Actions:**
- Create: `POST /hr/leaves`
- Update: `PUT /hr/leaves/{leaveRequestId}`
- Delete: `DELETE /hr/leaves/{leaveRequestId}`
- Approve: `POST /hr/leaves/{leaveRequestId}/approve?approvedBy=&comments=`
- Reject: `POST /hr/leaves/{leaveRequestId}/reject?approvedBy=&comments=`

---

### 4. Performance Reviews (`/hr/performance`)
**APIs Required:**
- `GET /hr/performance-reviews` (all reviews)

**Filters:**
- By Employee: `GET /hr/performance-reviews/employee/{employeeId}`
- By Date Range: `GET /hr/performance-reviews/by-date-range?startDate=&endDate=`

**Actions:**
- Create: `POST /hr/performance-reviews`
- Update: `PUT /hr/performance-reviews/{reviewId}`
- Delete: `DELETE /hr/performance-reviews/{reviewId}`
- View: `GET /hr/performance-reviews/{reviewId}`

---

## 🤝 **CRM MODULE**

### 1. Customers (`/crm/customers`)
**APIs Required:**
- `GET /customers/getAllCustomers`
- `POST /customers/createCustomer`
- `GET /customers/getCustomerById/{id}`
- `PUT /customers/updateCustomer/{id}`
- `DELETE /customers/deleteCustomer/{id}`

---

### 2. Customer Ledger (`/crm/customers/:id/ledger`)
**APIs Required:**
- `GET /customerLedger/getEntriesByCustomerId/{customerId}?page=&size=`
- `GET /customerLedger/getAllEntries?page=&size=`

---

### 3. Customer Balance (`/crm/customers/:id/balance`)
**APIs Required:**
- `GET /customerBalance/getBalanceByCustomer/{customerId}`
- `GET /customerBalance/getAllBalances?page=&size=`

---

### 4. Leads ⚠️ **NOT IMPLEMENTED**
**Future APIs:**
- `GET /crm/leads`
- `POST /crm/leads`
- `PUT /crm/leads/{id}`
- `DELETE /crm/leads/{id}`
- `POST /crm/leads/{id}/convert` (convert to customer)

---

### 5. Sales Pipeline ⚠️ **NOT IMPLEMENTED**
**Future APIs:**
- `GET /crm/pipeline`
- `PUT /crm/pipeline/{dealId}/stage` (move deal to stage)

---

## ⚙️ **SETTINGS MODULE**

### 1. Profile Settings (`/settings/profile`)
**APIs Required:**
- `GET /auth/allUsers` (get current user)
- ⚠️ **Missing:** Update user profile endpoint
- ⚠️ **Missing:** Change password endpoint

**Workaround:**
- Use existing user data
- Add backend endpoints for update

---

### 2. Company Settings (`/settings/company`)
**APIs Required:**
- ⚠️ **Missing:** GET/UPDATE company settings endpoints

**Workaround:**
- Store in tenant table
- Add backend endpoints

---

### 3. User Management (`/settings/users`)
**APIs Required:**
- `GET /auth/allUsers`
- `POST /auth/createUser`
- ⚠️ **Missing:** Update, Delete, Deactivate user endpoints

---

## 🔔 **NOTIFICATIONS**

### In-App Notifications
**APIs Required:**
- ⚠️ **Missing:** Notification endpoints

**Workaround:**
- Poll for updates:
  - Low stock alerts: `GET /stock/alerts/low-stock`
  - Pending leaves: `GET /hr/leaves?status=PENDING`
  - Pending expenses: `GET /finance/expenses/by-status/PENDING`
  - Pending payrolls: `GET /finance/payroll/by-status/SUBMITTED`

---

## 📊 **API Summary**

### ✅ **Fully Implemented Modules:**
- Authentication (4 endpoints)
- Finance (30 endpoints)
- Inventory Products (23 endpoints)
- Stock Tracking (11 endpoints)
- HR (29 endpoints)
- CRM Customers (9 endpoints)
- Reports (4 endpoints)

### ⚠️ **Partially Implemented:**
- Suppliers (only CREATE, missing GET/UPDATE/DELETE)
- User Management (only CREATE/LIST, missing UPDATE/DELETE)

### ❌ **Not Implemented:**
- CRM Leads
- CRM Sales Pipeline
- Notifications
- Company Settings
- User Profile Update
- Change Password

---

## 🔐 **Authentication Headers**

### All Protected Endpoints Require:
```javascript
headers: {
  'Session-Key': '<session-token>',
  'Content-Type': 'application/json'
}
```

### Session Management:
1. Store token in `localStorage` or `sessionStorage`
2. Include in all API requests
3. Handle 401 responses (redirect to login)
4. Clear token on logout

---

## 🛠️ **API Service Structure**

### Recommended Service Files:

```typescript
// services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add session token to all requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('sessionToken');
  if (token) {
    config.headers['Session-Key'] = token;
  }
  return config;
});

// Handle 401 responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('sessionToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

```typescript
// services/auth.service.ts
import api from './api';

export const authService = {
  login: (data) => api.post('/auth/userLogin', data),
  logout: () => api.post('/auth/userLogout'),
  registerTenant: (data) => api.post('/auth/tenantRegister', data),
  createUser: (data) => api.post('/auth/createUser', data),
  getAllUsers: () => api.get('/auth/allUsers')
};
```

```typescript
// services/finance.service.ts
import api from './api';

export const financeService = {
  // Invoices
  getAllInvoices: (status?) => api.get(`/finance/getAllInvoices${status ? `?status=${status}` : ''}`),
  getInvoiceById: (id) => api.get(`/finance/getInvoiceById?id=${id}`),
  createInvoice: (data) => api.post('/finance/createInvoice', data),
  updateInvoiceStatus: (id, status) => api.put(`/finance/invoices/${id}/${status}`),
  
  // Payments
  getAllPayments: () => api.get('/payments/getAllPayments'),
  getPaymentById: (id) => api.get(`/payments/getPaymentById/${id}`),
  createPayment: (data) => api.post('/payments/createPayment', data),
  deletePayment: (id) => api.delete(`/payments/deletePayment/${id}`),
  
  // Expenses
  getAllExpenses: () => api.get('/finance/expenses'),
  createExpense: (data) => api.post('/finance/expenses', data),
  updateExpense: (id, data) => api.put(`/finance/expenses/${id}`, data),
  deleteExpense: (id) => api.delete(`/finance/expenses/${id}`),
  approveExpense: (id) => api.post(`/finance/expenses/${id}/approve`),
  rejectExpense: (id) => api.post(`/finance/expenses/${id}/reject`),
  
  // Payroll
  getAllPayrolls: () => api.get('/finance/payroll'),
  createPayroll: (data) => api.post('/finance/payroll', data),
  updatePayroll: (id, data) => api.put(`/finance/payroll/${id}`, data),
  approvePayroll: (id) => api.post(`/finance/payroll/${id}/approve`),
  processPayroll: (id) => api.post(`/finance/payroll/${id}/process`),
  payPayroll: (id) => api.post(`/finance/payroll/${id}/pay`),
  
  // Reports
  getProfitLoss: (yearMonth) => api.get(`/reports/finance/profit-loss/${yearMonth}`),
  getMonthlyOverview: (yearMonth) => api.get(`/reports/finance/monthly-overview/${yearMonth}`),
  getYearToDate: (year) => api.get(`/reports/finance/year-to-date?year=${year}`)
};
```

---

## 📝 **Error Handling**

### Standard Error Response:
```json
{
  "message": "Error description",
  "code": 400,
  "data": null
}
```

### Frontend Error Handling:
```typescript
try {
  const response = await api.post('/endpoint', data);
  // Handle success
  showSuccessToast(response.data.message);
} catch (error) {
  // Handle error
  if (error.response) {
    showErrorToast(error.response.data.message);
  } else {
    showErrorToast('Network error. Please try again.');
  }
}
```

---

**This mapping provides complete API integration details for every frontend page! 🔌**
