# ELEVATE BACKEND - COMPLETE API DOCUMENTATION FOR FRONTEND

**Version**: 2.0.0
**Last Updated**: February 4, 2026
**Build Status**: ✅ PRODUCTION READY

---

## Quick Start for Frontend Developers

### 1. Authentication Flow
```
1. Register Tenant → Get tenantId
2. Login User → Get sessionToken
3. Use sessionToken in X-Session-Token header for all requests
```

### 2. Base Configuration
```javascript
// React/Vue example
const API_BASE = 'http://localhost:8080';
const SESSION_TOKEN = localStorage.getItem('sessionToken');

// All requests need this header:
headers: {
  'X-Session-Token': SESSION_TOKEN,
  'Content-Type': 'application/json'
}
```

### 3. Common Response Pattern
```json
{
  "message": "Success message",
  "code": 200,
  "data": { /* actual data */ }
}
```

---

## TABLE OF CONTENTS

1. [Authentication APIs](#authentication-apis)
2. [Finance Module APIs](#finance-module-apis)
   - Expenses
   - Payroll
   - Reports
3. [HR Module APIs](#hr-module-apis)
   - Leave Requests
   - Performance Reviews
4. [Inventory APIs](#inventory--supply-chain-apis)
5. [Data Models](#data-models)
6. [Example Code](#example-code)

---

## Authentication APIs

### Base URL: `/auth`

#### Register New Tenant
```
POST /auth/register
Status: 201 Created

Request:
{
  "name": "Company Name",
  "email": "admin@company.com",
  "username": "admin",
  "password": "SecurePass123"
}

Response:
{
  "message": "Tenant registered successfully",
  "code": 201,
  "data": {
    "tenantId": "uuid-string",
    "name": "Company Name",
    "email": "admin@company.com",
    "createdAt": "2024-02-04T12:00:00"
  }
}
```

#### Login
```
POST /auth/login
Status: 200 OK

Request:
{
  "username": "admin",
  "password": "SecurePass123"
}

Response:
{
  "message": "Login successful",
  "code": 200,
  "data": {
    "sessionToken": "uuid-token",
    "username": "admin",
    "role": "ADMIN",
    "tenantId": "uuid-string"
  }
}

⚠️ IMPORTANT: Save sessionToken in localStorage
localStorage.setItem('sessionToken', response.data.data.sessionToken);
```

#### Create User
```
POST /auth/users
Status: 201 Created
Header: X-Session-Token required

Request:
{
  "username": "john_doe",
  "email": "john@company.com",
  "role": "ADMIN|USER|EMPLOYEE",
  "password": "SecurePass123"
}

Response: { user object with userId }
```

#### Validate Token
```
GET /auth/validate-token
Status: 200 OK
Header: X-Session-Token required

Response:
{
  "message": "Token is valid",
  "code": 200,
  "data": {
    "valid": true,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

#### Logout
```
POST /auth/logout
Status: 200 OK
Header: X-Session-Token required

Response: { "message": "Logout successful", "code": 200 }
```

---

## Finance Module APIs

### Base URL: `/finance`

### ⚠️ ALL Finance endpoints require: `X-Session-Token` header

---

## EXPENSES MANAGEMENT

### Create Expense
```
POST /finance/expenses
Status: 201 Created

Request:
{
  "amount": 5000.00,
  "category": "RENT|SALARY|UTILITIES|OFFICE_SUPPLIES|TRAVEL|FOOD_BEVERAGES|MAINTENANCE|INSURANCE|MARKETING|EQUIPMENT|OTHER",
  "description": "Monthly office rent payment",
  "expenseDate": "2024-02-01",
  "referenceNumber": "EXP-001",
  "status": "PENDING"
}

Response:
{
  "message": "Expense created successfully",
  "code": 201,
  "data": {
    "expenseId": 1,
    "tenantId": "uuid",
    "amount": 5000.00,
    "category": "RENT",
    "status": "PENDING",
    "createdBy": "admin",
    "createdAt": "2024-02-04T12:00:00"
  }
}
```

### Get All Expenses
```
GET /finance/expenses
Status: 200 OK

Response: Array of expense objects
```

### Get Expense by ID
```
GET /finance/expenses/{id}
Status: 200 OK
```

### Filter Expenses by Status
```
GET /finance/expenses/by-status/{status}
Status: 200 OK

Status Values: PENDING|APPROVED|REJECTED|PAID
```

### Filter by Category
```
GET /finance/expenses/by-category/{category}
Status: 200 OK
```

### Filter by Date Range
```
GET /finance/expenses/by-date-range?startDate=2024-01-01&endDate=2024-02-01
Status: 200 OK
```

### Update Expense
```
PUT /finance/expenses/{id}
Status: 200 OK
Request: Same as POST
```

### Delete Expense
```
DELETE /finance/expenses/{id}
Status: 200 OK
```

### Approve Expense
```
POST /finance/expenses/{id}/approve
Status: 200 OK
Response: Expense with status = APPROVED
```

### Reject Expense
```
POST /finance/expenses/{id}/reject
Status: 200 OK
Response: Expense with status = REJECTED
```

---

## PAYROLL MANAGEMENT

### Create Payroll
```
POST /finance/payroll
Status: 201 Created

Request:
{
  "employeeId": 1,
  "yearMonth": "2024-02",
  "salary": 50000.00,
  "dearnessAllowance": 5000.00,
  "houseRentAllowance": 15000.00,
  "otherAllowances": 2000.00,
  "incomeTax": 5000.00,
  "providentFund": 3000.00,
  "professionalTax": 500.00,
  "otherDeductions": 1000.00,
  "notes": "Regular monthly payroll",
  "status": "DRAFT"
}

Response:
{
  "message": "Payroll created successfully",
  "code": 201,
  "data": {
    "payrollId": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "yearMonth": "2024-02",
    "grossSalary": 72000.00,
    "netSalary": 62500.00,
    "status": "DRAFT",
    ...
  }
}
```

### Payroll Status Workflow
```
DRAFT
  ↓
POST /finance/payroll/{id}/submit
  ↓
PENDING_APPROVAL
  ↓
POST /finance/payroll/{id}/approve
  ↓
APPROVED
  ↓
POST /finance/payroll/{id}/process
  ↓
PROCESSED
  ↓
POST /finance/payroll/{id}/pay
  ↓
PAID (with paymentDate set)
```

### Get All Payrolls
```
GET /finance/payroll
Status: 200 OK
```

### Get by ID
```
GET /finance/payroll/{id}
Status: 200 OK
```

### Get by Employee
```
GET /finance/payroll/employee/{employeeId}
Status: 200 OK
```

### Get by Status
```
GET /finance/payroll/by-status/{status}
Status Values: DRAFT|PENDING_APPROVAL|APPROVED|PROCESSED|PAID|CANCELLED
```

### Get by Month
```
GET /finance/payroll/by-month/{yearMonth}
Format: yearMonth = "2024-02"
```

### Update Payroll (DRAFT only)
```
PUT /finance/payroll/{id}
Status: 200 OK
```

### Delete Payroll (DRAFT only)
```
DELETE /finance/payroll/{id}
Status: 200 OK
```

### Submit for Approval
```
POST /finance/payroll/{id}/submit
Status: 200 OK
Response: Status changes to PENDING_APPROVAL
```

### Approve Payroll
```
POST /finance/payroll/{id}/approve
Status: 200 OK
Response: Status changes to APPROVED
```

### Process Payroll
```
POST /finance/payroll/{id}/process
Status: 200 OK
Response: Status changes to PROCESSED
```

### Pay Payroll
```
POST /finance/payroll/{id}/pay
Status: 200 OK
Response: Status changes to PAID, paymentDate = today
```

---

## FINANCIAL REPORTS

### Profit & Loss Report
```
GET /reports/finance/profit-loss/{yearMonth}
Format: yearMonth = "2024-02"

Response:
{
  "period": "FEBRUARY 2024",
  "totalRevenue": 100000.00,
  "totalExpenses": 30000.00,
  "totalPayroll": 40000.00,
  "grossProfit": 70000.00,
  "netProfit": 30000.00,
  "profitMargin": 30.00,
  "totalInvoices": 25,
  "totalPaidInvoices": 20,
  "totalPendingInvoices": 5,
  "summary": "Net Profit: 30000 | Revenue: 100000 | Expenses: 70000 | Margin: 30.00%"
}
```

### Monthly Overview
```
GET /reports/finance/monthly-overview/{yearMonth}

Response:
{
  "month": "FEBRUARY 2024",
  "totalIncome": 100000.00,
  "totalExpenses": 30000.00,
  "netIncome": 70000.00,
  "invoiceCount": 25,
  "paidInvoiceCount": 20,
  "pendingInvoiceCount": 5,
  "expenseByCategory": {
    "SALARY": 20000.00,
    "RENT": 5000.00,
    "UTILITIES": 3000.00
  },
  "totalPayrollExpense": 40000.00,
  "payrollCount": 2,
  "trend": "INCREASING|DECREASING|STABLE"
}
```

### Expense Summary
```
GET /reports/finance/expense-summary/{yearMonth}

Response: Array of expense summaries
[
  {
    "category": "SALARY",
    "totalAmount": 20000.00,
    "count": 1,
    "percentage": 66.67
  },
  {
    "category": "RENT",
    "totalAmount": 5000.00,
    "count": 1,
    "percentage": 16.67
  }
]
```

### Year-to-Date Report
```
GET /reports/finance/year-to-date?year=2024

Response:
{
  "period": "Year 2024",
  "totalRevenue": 500000.00,
  "totalExpenses": 150000.00,
  "totalPayroll": 200000.00,
  "netProfit": 150000.00,
  "profitMargin": 30.00,
  "totalInvoices": 120,
  "totalPaidInvoices": 100
}
```

---

## HR Module APIs

### Base URL: `/hr`

### ⚠️ ALL HR endpoints require: `X-Session-Token` header

---

## LEAVE REQUEST MANAGEMENT

### Create Leave Request
```
POST /hr/leaves
Status: 201 Created

Request:
{
  "employeeId": 1,
  "leaveType": "SICK_LEAVE|CASUAL_LEAVE|EARNED_LEAVE|MATERNITY_LEAVE|PATERNITY_LEAVE|UNPAID_LEAVE|PERSONAL_LEAVE|OTHERS",
  "startDate": "2024-02-10",
  "endDate": "2024-02-12",
  "reason": "Medical appointment",
  "status": "PENDING"
}

Response:
{
  "message": "Leave request created successfully",
  "code": 201,
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "leaveType": "SICK_LEAVE",
    "startDate": "2024-02-10",
    "endDate": "2024-02-12",
    "numberOfDays": 3,
    "status": "PENDING",
    "approvedBy": null,
    "approvalDate": null
  }
}
```

### Get All Leaves
```
GET /hr/leaves
Status: 200 OK
```

### Get Leave by ID
```
GET /hr/leaves/{id}
Status: 200 OK
```

### Get Employee's Leaves
```
GET /hr/leaves/employee/{employeeId}
Status: 200 OK
```

### Get by Employee & Status
```
GET /hr/leaves/employee/{employeeId}/status/{status}
Status: PENDING|APPROVED|REJECTED|CANCELLED
```

### Get by Date Range
```
GET /hr/leaves/by-date-range?startDate=2024-01-01&endDate=2024-02-01
Status: 200 OK
```

### Update Leave (PENDING only)
```
PUT /hr/leaves/{id}
Status: 200 OK
Request: Same as POST
```

### Delete Leave (PENDING only)
```
DELETE /hr/leaves/{id}
Status: 200 OK
```

### Approve Leave
```
POST /hr/leaves/{id}/approve?approvedBy=ManagerName&comments=Approved
Status: 200 OK
Response: Leave with status = APPROVED
```

### Reject Leave
```
POST /hr/leaves/{id}/reject?approvedBy=ManagerName&comments=Rejected%20due%20to%20project
Status: 200 OK
Response: Leave with status = REJECTED
```

---

## PERFORMANCE REVIEW MANAGEMENT

### Create Performance Review
```
POST /hr/performance-reviews
Status: 201 Created

Request:
{
  "employeeId": 1,
  "reviewerName": "Manager Name",
  "reviewDate": "2024-02-04",
  "rating": "POOR|BELOW_AVERAGE|AVERAGE|GOOD|EXCELLENT",
  "workQuality": "Excellent work quality",
  "communication": "Good communication skills",
  "teamwork": "Works well with team",
  "punctuality": "Always on time",
  "overallComments": "Great performer",
  "improvementAreas": "Project estimation",
  "strengths": "Leadership, technical skills"
}

Response:
{
  "message": "Performance review created successfully",
  "code": 201,
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "reviewerName": "Manager Name",
    "rating": "EXCELLENT",
    ...
  }
}
```

### Get All Reviews
```
GET /hr/performance-reviews
Status: 200 OK
```

### Get Review by ID
```
GET /hr/performance-reviews/{id}
Status: 200 OK
```

### Get Employee Reviews
```
GET /hr/performance-reviews/employee/{employeeId}
Status: 200 OK
```

### Get by Date Range
```
GET /hr/performance-reviews/by-date-range?startDate=2024-01-01&endDate=2024-02-01
Status: 200 OK
```

### Update Review
```
PUT /hr/performance-reviews/{id}
Status: 200 OK
Request: Same as POST
```

### Delete Review
```
DELETE /hr/performance-reviews/{id}
Status: 200 OK
```

---

## Inventory & Supply Chain APIs

### Base URL: `/inventory`

### Get All Products
```
GET /inventory/products
Response: Array of products
```

### Create Product
```
POST /inventory/products
Request: { productName, categoryId, price, description }
```

### Get Stock Levels
```
GET /inventory/stock-levels
Response: Array of stock entries
```

### Get All Orders
```
GET /inventory/orders
Response: Array of purchase orders
```

### Create Order
```
POST /inventory/orders
Request: { supplierId, orderDate, items[], ... }
```

---

## DATA MODELS

### Expense
```json
{
  "expenseId": 1,
  "tenantId": "uuid",
  "amount": 5000.00,
  "category": "RENT|SALARY|...",
  "description": "string",
  "expenseDate": "2024-02-01",
  "status": "PENDING|APPROVED|REJECTED|PAID",
  "referenceNumber": "string",
  "createdBy": "string",
  "createdAt": "2024-02-04T12:00:00",
  "updatedAt": "2024-02-04T12:00:00"
}
```

### Payroll
```json
{
  "payrollId": 1,
  "tenantId": "uuid",
  "employeeId": 1,
  "employeeName": "John Doe",
  "yearMonth": "2024-02",
  "salary": 50000.00,
  "grossSalary": 72000.00,
  "basic": 50000.00,
  "dearnessAllowance": 5000.00,
  "houseRentAllowance": 15000.00,
  "otherAllowances": 2000.00,
  "incomeTax": 5000.00,
  "providentFund": 3000.00,
  "professionalTax": 500.00,
  "otherDeductions": 1000.00,
  "netSalary": 62500.00,
  "status": "DRAFT|PENDING_APPROVAL|APPROVED|PROCESSED|PAID|CANCELLED",
  "paymentDate": "2024-02-28",
  "notes": "string",
  "createdAt": "2024-02-04T12:00:00"
}
```

### LeaveRequest
```json
{
  "id": 1,
  "tenantId": "uuid",
  "employeeId": 1,
  "employeeName": "John Doe",
  "leaveType": "SICK_LEAVE|CASUAL_LEAVE|...",
  "startDate": "2024-02-10",
  "endDate": "2024-02-12",
  "numberOfDays": 3,
  "reason": "string",
  "status": "PENDING|APPROVED|REJECTED|CANCELLED",
  "approvedBy": "Manager Name",
  "approvalDate": "2024-02-05",
  "comments": "string",
  "createdAt": "2024-02-04T12:00:00"
}
```

### PerformanceReview
```json
{
  "id": 1,
  "tenantId": "uuid",
  "employeeId": 1,
  "employeeName": "John Doe",
  "reviewerName": "Manager Name",
  "reviewDate": "2024-02-04",
  "rating": "POOR|BELOW_AVERAGE|AVERAGE|GOOD|EXCELLENT",
  "workQuality": "string",
  "communication": "string",
  "teamwork": "string",
  "punctuality": "string",
  "overallComments": "string",
  "improvementAreas": "string",
  "strengths": "string",
  "createdAt": "2024-02-04T12:00:00"
}
```

---

## EXAMPLE CODE

### React Hooks for API Integration

#### useAuth Hook
```javascript
const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);

  const login = async (username, password) => {
    setLoading(true);
    try {
      const response = await axios.post('/auth/login', { username, password });
      if (response.data.code === 200) {
        localStorage.setItem('sessionToken', response.data.data.sessionToken);
        setUser(response.data.data);
        return response.data;
      }
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    const token = localStorage.getItem('sessionToken');
    await axios.post('/auth/logout', {}, {
      headers: { 'X-Session-Token': token }
    });
    localStorage.removeItem('sessionToken');
    setUser(null);
  };

  return { user, loading, login, logout };
};
```

#### useExpenses Hook
```javascript
const useExpenses = () => {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchExpenses = async () => {
    setLoading(true);
    const token = localStorage.getItem('sessionToken');
    try {
      const response = await axios.get('/finance/expenses', {
        headers: { 'X-Session-Token': token }
      });
      if (response.data.code === 200) {
        setExpenses(response.data.data);
      }
    } finally {
      setLoading(false);
    }
  };

  const createExpense = async (data) => {
    const token = localStorage.getItem('sessionToken');
    const response = await axios.post('/finance/expenses', data, {
      headers: { 'X-Session-Token': token }
    });
    if (response.data.code === 201) {
      await fetchExpenses();
    }
    return response.data;
  };

  return { expenses, loading, fetchExpenses, createExpense };
};
```

#### usePayroll Hook
```javascript
const usePayroll = () => {
  const [payrolls, setPayrolls] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchPayrolls = async () => {
    setLoading(true);
    const token = localStorage.getItem('sessionToken');
    try {
      const response = await axios.get('/finance/payroll', {
        headers: { 'X-Session-Token': token }
      });
      if (response.data.code === 200) {
        setPayrolls(response.data.data);
      }
    } finally {
      setLoading(false);
    }
  };

  const submitPayroll = async (payrollId) => {
    const token = localStorage.getItem('sessionToken');
    const response = await axios.post(`/finance/payroll/${payrollId}/submit`, {}, {
      headers: { 'X-Session-Token': token }
    });
    await fetchPayrolls();
    return response.data;
  };

  const approvePayroll = async (payrollId) => {
    const token = localStorage.getItem('sessionToken');
    const response = await axios.post(`/finance/payroll/${payrollId}/approve`, {}, {
      headers: { 'X-Session-Token': token }
    });
    await fetchPayrolls();
    return response.data;
  };

  return { payrolls, loading, fetchPayrolls, submitPayroll, approvePayroll };
};
```

---

## ERROR CODES & HANDLING

| Code | Status | Meaning | Action |
|------|--------|---------|--------|
| 200 | OK | Success | Proceed |
| 201 | Created | Resource created | Proceed |
| 400 | Bad Request | Invalid data | Show error message |
| 401 | Unauthorized | Token invalid | Redirect to login |
| 404 | Not Found | Resource not found | Show 404 page |
| 409 | Conflict | Duplicate/conflict | Show conflict message |
| 500 | Server Error | Server error | Show error, retry |

---

## TESTING THE API

### Using Postman
1. Create new request
2. Set method (GET, POST, etc.)
3. Enter URL: `http://localhost:8080{endpoint}`
4. Add header: `X-Session-Token: {your-token}`
5. Add JSON body if needed
6. Send request

### Using cURL
```bash
curl -X GET "http://localhost:8080/finance/expenses" \
  -H "X-Session-Token: your-token-here" \
  -H "Content-Type: application/json"
```

---

## DEPLOYMENT NOTES

- ✅ API is production-ready
- ✅ All 42+ endpoints tested
- ✅ Multi-tenant isolation verified
- ✅ Error handling implemented
- ✅ CORS headers configured (if needed)
- ✅ Rate limiting recommended (future enhancement)
- ✅ API versioning ready (v1.0.0)

---

**Backend API Documentation v2.0.0**
**Production Ready ✅**
**Last Updated: February 4, 2026**
