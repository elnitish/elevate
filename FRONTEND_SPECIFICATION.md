# 🎨 Elevate Frontend Specification

## Complete Frontend Architecture & Pages

Based on the backend implementation, here's a comprehensive specification for the frontend application.

---

## 🏗️ **Technology Stack Recommendations**

### **Option 1: Modern React SPA** (Recommended)
- **Framework**: React 18+ with TypeScript
- **Routing**: React Router v6
- **State Management**: Redux Toolkit or Zustand
- **UI Library**: Material-UI (MUI) or Ant Design
- **Forms**: React Hook Form + Yup validation
- **API Client**: Axios with interceptors
- **Charts**: Recharts or Chart.js
- **Tables**: TanStack Table (React Table v8)
- **Date Handling**: date-fns or Day.js
- **Build Tool**: Vite

### **Option 2: Next.js** (For SEO/SSR needs)
- **Framework**: Next.js 14+ with App Router
- **Everything else**: Same as Option 1

---

## 🎯 **Application Structure**

```
elevate-frontend/
├── public/
├── src/
│   ├── components/          # Reusable components
│   │   ├── common/         # Buttons, Inputs, Cards, etc.
│   │   ├── layout/         # Sidebar, Header, Footer
│   │   ├── forms/          # Form components
│   │   └── tables/         # Table components
│   ├── pages/              # Page components
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── finance/
│   │   ├── inventory/
│   │   ├── hr/
│   │   ├── crm/
│   │   └── settings/
│   ├── services/           # API services
│   ├── store/              # State management
│   ├── hooks/              # Custom hooks
│   ├── utils/              # Utilities
│   ├── types/              # TypeScript types
│   └── App.tsx
```

---

## 📱 **Complete Page Structure**

### **1. AUTHENTICATION MODULE** 🔐

#### **1.1 Login Page** (`/login`)
**Features:**
- Tenant ID input
- Username/Email input
- Password input
- "Remember Me" checkbox
- "Forgot Password" link
- Login button
- Error messages display

**API Endpoint:**
- `POST /auth/userLogin`

**State to Store:**
- Session token (in localStorage/sessionStorage)
- User details (name, role, tenantId)
- Permissions

---

#### **1.2 Tenant Registration** (`/register/tenant`)
**Features:**
- Company/Tenant name
- Email
- Plan type selection (Basic, Pro, Enterprise)
- Submit button

**API Endpoint:**
- `POST /auth/tenantRegister`

---

#### **1.3 User Registration** (`/register/user`)
**Features:**
- Tenant ID (from admin)
- Username
- Email
- Password
- Role selection (Admin, Manager, Employee)
- Submit button

**API Endpoint:**
- `POST /auth/createUser`

---

### **2. DASHBOARD MODULE** 📊

#### **2.1 Main Dashboard** (`/dashboard`)
**Layout:**
- Top metrics cards (4-6 cards)
- Charts section (2-3 charts)
- Recent activities list
- Quick actions

**Widgets:**
1. **Financial Overview Card**
   - Total Revenue (this month)
   - Total Expenses (this month)
   - Profit/Loss
   - Pending Invoices count

2. **Inventory Overview Card**
   - Total Products
   - Low Stock Items count (clickable)
   - Recent Stock Movements
   - Purchase Orders pending

3. **HR Overview Card**
   - Total Employees
   - Attendance Today
   - Pending Leave Requests
   - Upcoming Reviews

4. **CRM Overview Card**
   - Total Customers
   - Active Leads
   - Recent Interactions
   - Conversion Rate

**Charts:**
- Revenue vs Expenses (Line/Bar chart - last 6 months)
- Stock Levels (Top 10 products - Bar chart)
- Employee Attendance Trend (Line chart)

**Recent Activities:**
- Last 10 transactions/actions across all modules
- Timestamp, user, action, module

**API Endpoints:**
- `GET /reports/finance/monthly-overview/{yearMonth}`
- `GET /stock/alerts/low-stock`
- `GET /hr/leaves` (filter by status=PENDING)
- `GET /customers/getAllCustomers`

---

### **3. FINANCE MODULE** 💰

#### **3.1 Invoices Page** (`/finance/invoices`)
**Layout:**
- Filter bar (Status, Date range, Customer)
- Create Invoice button
- Invoices table
- Pagination

**Table Columns:**
- Invoice ID
- Customer Name
- Date
- Total Amount
- Status (PENDING/PAID/CANCELLED)
- Actions (View, Edit, Delete, Update Status)

**API Endpoints:**
- `GET /finance/getAllInvoices`
- `POST /finance/createInvoice`
- `PUT /finance/invoices/{id}/{status}`

---

#### **3.2 Create/Edit Invoice** (`/finance/invoices/new`, `/finance/invoices/:id/edit`)
**Form Fields:**
- Customer selection (dropdown or search)
- Customer Name (if new)
- Email
- Phone
- Invoice Date
- Status
- **Line Items Table:**
  - Product selection
  - Quantity
  - Unit Price
  - Total (auto-calculated)
  - Add/Remove row buttons
- **Summary:**
  - Subtotal
  - Tax (if applicable)
  - Grand Total
- Save/Submit button

**API Endpoints:**
- `POST /finance/createInvoice`
- `GET /insc/getAllProducts` (for product dropdown)
- `GET /customers/getAllCustomers` (for customer dropdown)

---

#### **3.3 Payments Page** (`/finance/payments`)
**Layout:**
- Filter bar (Date range, Invoice, Customer)
- Create Payment button
- Payments table
- Pagination

**Table Columns:**
- Payment ID
- Invoice ID (clickable)
- Customer Name
- Amount
- Payment Method
- Transaction Reference
- Date
- Actions (View, Delete)

**API Endpoints:**
- `GET /payments/getAllPayments`
- `POST /payments/createPayment`
- `DELETE /payments/deletePayment/{paymentId}`

---

#### **3.4 Create Payment** (`/finance/payments/new`)
**Form Fields:**
- Invoice selection (dropdown - show unpaid invoices)
- Customer (auto-filled from invoice)
- Amount
- Payment Method (Cash, Card, Bank Transfer, UPI, etc.)
- Transaction Reference
- Payment Date
- Notes
- Submit button

**API Endpoints:**
- `POST /payments/createPayment`
- `GET /finance/getAllInvoices?status=PENDING`

---

#### **3.5 Expenses Page** (`/finance/expenses`)
**Layout:**
- Filter bar (Status, Category, Date range)
- Create Expense button
- Expenses table
- Pagination

**Table Columns:**
- Expense ID
- Category
- Description
- Amount
- Date
- Status (PENDING/APPROVED/REJECTED)
- Actions (View, Edit, Delete, Approve, Reject)

**API Endpoints:**
- `GET /finance/expenses`
- `POST /finance/expenses`
- `PUT /finance/expenses/{expenseId}`
- `POST /finance/expenses/{expenseId}/approve`
- `POST /finance/expenses/{expenseId}/reject`

---

#### **3.6 Create/Edit Expense** (`/finance/expenses/new`, `/finance/expenses/:id/edit`)
**Form Fields:**
- Category (Rent, Salary, Utilities, Travel, etc.)
- Description
- Amount
- Expense Date
- Reference Number
- Status (PENDING/APPROVED/REJECTED)
- Attachment (optional - file upload)
- Submit button

---

#### **3.7 Payroll Page** (`/finance/payroll`)
**Layout:**
- Filter bar (Month, Status, Employee)
- Create Payroll button
- Payroll table
- Bulk actions (Approve selected, Process selected)

**Table Columns:**
- Payroll ID
- Employee Name
- Month (YYYY-MM)
- Gross Salary
- Deductions
- Net Salary
- Status (DRAFT/SUBMITTED/APPROVED/PROCESSED/PAID)
- Actions (View, Edit, Approve, Process, Pay)

**API Endpoints:**
- `GET /finance/payroll`
- `POST /finance/payroll`
- `POST /finance/payroll/{payrollId}/approve`
- `POST /finance/payroll/{payrollId}/process`
- `POST /finance/payroll/{payrollId}/pay`

---

#### **3.8 Create/Edit Payroll** (`/finance/payroll/new`, `/finance/payroll/:id/edit`)
**Form Fields:**
- Employee selection
- Month (YYYY-MM)
- Basic Salary
- **Allowances:**
  - Dearness Allowance
  - House Rent Allowance
  - Other Allowances
- **Deductions:**
  - Income Tax
  - Provident Fund
  - Professional Tax
  - Other Deductions
- **Calculated Fields:**
  - Gross Salary (auto-calculated)
  - Total Deductions (auto-calculated)
  - Net Salary (auto-calculated)
- Notes
- Status
- Submit button

---

#### **3.9 Finance Reports** (`/finance/reports`)
**Report Types:**
1. **Profit & Loss Report**
   - Month selection
   - Revenue breakdown
   - Expense breakdown
   - Net Profit/Loss
   - Chart visualization

2. **Monthly Overview**
   - Month selection
   - Total Revenue
   - Total Expenses
   - Invoice summary
   - Payment summary
   - Expense summary

3. **Year-to-Date Report**
   - Year selection
   - Cumulative revenue
   - Cumulative expenses
   - Month-by-month breakdown
   - Trend charts

**API Endpoints:**
- `GET /reports/finance/profit-loss/{yearMonth}`
- `GET /reports/finance/monthly-overview/{yearMonth}`
- `GET /reports/finance/year-to-date?year={year}`

---

### **4. INVENTORY MODULE** 📦

#### **4.1 Products Page** (`/inventory/products`)
**Layout:**
- Search bar
- Filter by Category
- Create Product button
- Products table/grid
- Pagination

**Table Columns:**
- Product ID
- Product Name
- Category
- Cost Price
- Selling Price
- Current Stock
- Stock Status (In Stock/Low Stock/Out of Stock)
- Actions (View, Edit, Delete)

**API Endpoints:**
- `GET /insc/getAllProducts`
- `POST /insc/createProduct`
- `PUT /insc/updateProduct`
- `DELETE /insc/deleteProductById/{productId}`

---

#### **4.2 Create/Edit Product** (`/inventory/products/new`, `/inventory/products/:id/edit`)
**Form Fields:**
- Product Name
- Category (dropdown)
- Description
- Cost Price
- Selling Price
- Initial Stock (only for new products)
- Product Image (optional)
- Submit button

---

#### **4.3 Categories Page** (`/inventory/categories`)
**Layout:**
- Create Category button
- Categories table
- Inline editing

**Table Columns:**
- Category ID
- Category Name
- Product Count
- Actions (Edit, Delete)

**API Endpoints:**
- `GET /category/getAllCategory`
- `POST /category/createCategory`
- `PUT /category/updateCategory`
- `DELETE /category/deleteCategory/{categoryId}`

---

#### **4.4 Stock Levels** (`/inventory/stock`)
**Layout:**
- Search/Filter bar
- Low Stock Alert banner (if any)
- Stock Levels table
- Export button

**Table Columns:**
- Product ID
- Product Name
- Current Stock
- Last Updated
- Stock Status (color-coded)
- Actions (Adjust Stock, View Movements)

**API Endpoints:**
- `GET /stock/levels`
- `GET /stock/levels/product/{productId}`
- `PUT /stock/levels/adjust`

---

#### **4.5 Stock Adjustment Modal**
**Form Fields:**
- Product (pre-filled)
- Current Stock (display only)
- New Stock Quantity
- Reason (Damaged, Lost, Correction, etc.)
- Reference
- Submit button

---

#### **4.6 Stock Movements** (`/inventory/stock/movements`)
**Layout:**
- Filter bar (Type, Product, Date range)
- Stock Movements table
- Export button

**Table Columns:**
- Movement ID
- Product Name
- Type (IN/OUT)
- Quantity
- Reference (PO/Invoice ID)
- Date
- Actions (View Details)

**API Endpoints:**
- `GET /stock/movements`
- `GET /stock/movements/product/{productId}`
- `GET /stock/movements/type/{type}`
- `GET /stock/movements/date-range`

---

#### **4.7 Low Stock Alerts** (`/inventory/alerts`)
**Layout:**
- Threshold setting
- Alerts table
- Quick action buttons (Create PO, Adjust Stock)

**Table Columns:**
- Product Name
- Current Stock
- Threshold
- Shortfall
- Alert Level (OUT_OF_STOCK/CRITICAL/LOW)
- Actions (Create PO, Adjust Stock)

**API Endpoints:**
- `GET /stock/alerts/low-stock?threshold={threshold}`

---

#### **4.8 Suppliers Page** (`/inventory/suppliers`)
**Layout:**
- Create Supplier button
- Suppliers table
- Search bar

**Table Columns:**
- Supplier ID
- Name
- Email
- Phone
- Address
- Actions (View, Edit, Delete)

**API Endpoints:**
- `POST /purchaseOrder/createNewSupplier`
- (Note: GET endpoints for suppliers may need to be added to backend)

---

#### **4.9 Purchase Orders** (`/inventory/purchase-orders`)
**Layout:**
- Filter bar (Status, Supplier, Date range)
- Create PO button
- Purchase Orders table
- Pagination

**Table Columns:**
- PO ID
- Supplier Name
- Order Date
- Total Amount
- Status (PENDING/APPROVED/RECEIVED/CANCELLED)
- Actions (View, Edit, Update Status)

**API Endpoints:**
- `GET /purchaseOrder/getAllPurchaseOrder`
- `POST /purchaseOrder/createNewPurchaseOrder`
- `PUT /purchaseOrder/updatePurchaseOrderStatus/{purchaseOrderId}/{status}`

---

#### **4.10 Create/Edit Purchase Order** (`/inventory/purchase-orders/new`)
**Form Fields:**
- Supplier selection
- Order Date
- Status
- **Line Items Table:**
  - Product selection
  - Quantity
  - Unit Price
  - Total (auto-calculated)
  - Add/Remove row buttons
- **Summary:**
  - Subtotal
  - Tax (if applicable)
  - Grand Total
- Submit button

---

### **5. HR MODULE** 👥

#### **5.1 Employees Page** (`/hr/employees`)
**Layout:**
- Search bar
- Filter by Department/Designation
- Add Employee button
- Employees table/grid
- Pagination

**Table Columns:**
- Employee ID
- Name
- Email
- Phone
- Designation
- Department
- Date of Joining
- Salary
- Actions (View, Edit, Delete)

**API Endpoints:**
- `GET /api/hr/employees`
- `POST /api/hr/employees`
- `PUT /api/hr/employees/{id}`
- `DELETE /api/hr/employees/{id}`

---

#### **5.2 Add/Edit Employee** (`/hr/employees/new`, `/hr/employees/:id/edit`)
**Form Fields:**
- Name
- Email
- Phone
- Designation (CASHIER, ACCOUNTANT, SALES_EXECUTIVE, DELIVERY, HELPER)
- Department
- Date of Joining
- Salary
- Photo (optional)
- Submit button

---

#### **5.3 Attendance Page** (`/hr/attendance`)
**Layout:**
- Date selector (default: today)
- Mark Attendance section
- Attendance table
- Summary statistics

**Mark Attendance:**
- Employee selection
- Status (PRESENT, HALF_DAY, ABSENT)
- Date
- Submit button

**Table Columns:**
- Employee Name
- Date
- Status
- Marked By
- Timestamp

**API Endpoints:**
- `GET /api/hr/attendance`
- `POST /api/hr/attendance`
- `GET /api/hr/attendance/employee/{id}`

---

#### **5.4 Leave Requests** (`/hr/leaves`)
**Layout:**
- Filter bar (Status, Employee, Date range)
- Create Leave Request button
- Leave Requests table
- Tabs (All, Pending, Approved, Rejected)

**Table Columns:**
- Request ID
- Employee Name
- Leave Type (Sick, Casual, Vacation, etc.)
- Start Date
- End Date
- Days
- Reason
- Status (PENDING/APPROVED/REJECTED)
- Actions (View, Approve, Reject, Delete)

**API Endpoints:**
- `GET /hr/leaves`
- `POST /hr/leaves`
- `PUT /hr/leaves/{leaveRequestId}`
- `POST /hr/leaves/{leaveRequestId}/approve`
- `POST /hr/leaves/{leaveRequestId}/reject`
- `DELETE /hr/leaves/{leaveRequestId}`

---

#### **5.5 Create Leave Request** (`/hr/leaves/new`)
**Form Fields:**
- Employee selection
- Leave Type
- Start Date
- End Date
- Reason
- Submit button

---

#### **5.6 Performance Reviews** (`/hr/performance`)
**Layout:**
- Filter bar (Employee, Date range)
- Create Review button
- Reviews table
- Pagination

**Table Columns:**
- Review ID
- Employee Name
- Reviewer Name
- Review Date
- Rating
- Actions (View, Edit, Delete)

**API Endpoints:**
- `GET /hr/performance-reviews`
- `POST /hr/performance-reviews`
- `PUT /hr/performance-reviews/{reviewId}`
- `DELETE /hr/performance-reviews/{reviewId}`

---

#### **5.7 Create/Edit Performance Review** (`/hr/performance/new`)
**Form Fields:**
- Employee selection
- Reviewer Name
- Review Date
- Rating (1-5 stars or A-E)
- **Performance Criteria:**
  - Work Quality
  - Communication
  - Teamwork
  - Punctuality
- Overall Comments
- Improvement Areas
- Strengths
- Submit button

---

### **6. CRM MODULE** 🤝

#### **6.1 Customers Page** (`/crm/customers`)
**Layout:**
- Search bar
- Filter by Source
- Add Customer button
- Customers table
- Pagination

**Table Columns:**
- Customer ID
- Name
- Email
- Phone
- Address
- Source (Website, Referral, Walk-in, etc.)
- Created Date
- Actions (View, Edit, Delete, View Ledger, View Balance)

**API Endpoints:**
- `GET /customers/getAllCustomers`
- `POST /customers/createCustomer`
- `PUT /customers/updateCustomer/{id}`
- `DELETE /customers/deleteCustomer/{id}`

---

#### **6.2 Add/Edit Customer** (`/crm/customers/new`, `/crm/customers/:id/edit`)
**Form Fields:**
- Name
- Email
- Phone
- Address
- Source
- Notes
- Submit button

---

#### **6.3 Customer Ledger** (`/crm/customers/:id/ledger`)
**Layout:**
- Customer details header
- Date range filter
- Ledger entries table
- Summary (Total Debit, Total Credit, Balance)

**Table Columns:**
- Date
- Transaction Type (Invoice, Payment, Credit Note, etc.)
- Reference ID
- Debit
- Credit
- Balance
- Description

**API Endpoints:**
- `GET /customerLedger/getEntriesByCustomerId/{customerId}`
- `GET /customerLedger/getAllEntries`

---

#### **6.4 Customer Balance** (`/crm/customers/:id/balance`)
**Layout:**
- Customer details
- Current balance (color-coded: green for credit, red for debit)
- Recent transactions
- Payment history chart

**API Endpoints:**
- `GET /customerBalance/getBalanceByCustomer/{customerId}`

---

#### **6.5 Leads Page** (`/crm/leads`) ⚠️ **NOT YET IMPLEMENTED IN BACKEND**
**Layout:**
- Filter bar (Status, Source, Date range)
- Add Lead button
- Leads table
- Kanban view toggle

**Table Columns:**
- Lead ID
- Name
- Email
- Phone
- Source
- Status (New, Contacted, Qualified, Lost, Converted)
- Assigned To
- Created Date
- Actions (View, Edit, Convert to Customer, Delete)

**Future API Endpoints:**
- `GET /crm/leads`
- `POST /crm/leads`
- `PUT /crm/leads/{id}`
- `POST /crm/leads/{id}/convert`

---

#### **6.6 Sales Pipeline** (`/crm/pipeline`) ⚠️ **NOT YET IMPLEMENTED IN BACKEND**
**Layout:**
- Kanban board with stages
- Drag-and-drop functionality
- Deal cards with value

**Stages:**
- Prospecting
- Qualification
- Proposal
- Negotiation
- Closed Won
- Closed Lost

---

### **7. SETTINGS MODULE** ⚙️

#### **7.1 Profile Settings** (`/settings/profile`)
**Form Fields:**
- Name
- Email
- Phone
- Change Password section
- Save button

---

#### **7.2 Company Settings** (`/settings/company`)
**Form Fields:**
- Company Name
- Email
- Phone
- Address
- Logo upload
- Tax ID
- Currency
- Save button

---

#### **7.3 User Management** (`/settings/users`)
**Layout:**
- Add User button
- Users table
- Role management

**Table Columns:**
- User ID
- Username
- Email
- Role
- Status (Active/Inactive)
- Last Login
- Actions (Edit, Deactivate, Delete)

**API Endpoints:**
- `GET /auth/allUsers`
- `POST /auth/createUser`

---

## 🎨 **Common UI Components**

### **Layout Components:**
1. **Sidebar Navigation**
   - Dashboard
   - Finance (expandable)
     - Invoices
     - Payments
     - Expenses
     - Payroll
     - Reports
   - Inventory (expandable)
     - Products
     - Categories
     - Stock Levels
     - Stock Movements
     - Low Stock Alerts
     - Suppliers
     - Purchase Orders
   - HR (expandable)
     - Employees
     - Attendance
     - Leave Requests
     - Performance Reviews
   - CRM (expandable)
     - Customers
     - Leads
     - Pipeline
   - Settings

2. **Top Header**
   - Company logo
   - Search bar (global)
   - Notifications bell (with badge)
   - User profile dropdown
     - Profile
     - Settings
     - Logout

3. **Breadcrumbs**
   - Show current page path

---

### **Reusable Components:**
1. **DataTable**
   - Sortable columns
   - Pagination
   - Row selection
   - Bulk actions
   - Export (CSV, Excel, PDF)
   - Column visibility toggle

2. **FormInput**
   - Text, Number, Email, Password, Tel
   - Validation messages
   - Required indicator

3. **Select/Dropdown**
   - Single/Multi select
   - Searchable
   - Async loading

4. **DatePicker**
   - Single date
   - Date range
   - Month/Year picker

5. **Modal/Dialog**
   - Confirmation dialogs
   - Form modals
   - Info modals

6. **Cards**
   - Metric cards
   - Info cards
   - Action cards

7. **Charts**
   - Line chart
   - Bar chart
   - Pie chart
   - Donut chart

8. **Alerts/Notifications**
   - Success
   - Error
   - Warning
   - Info

9. **Loading States**
   - Skeleton loaders
   - Spinners
   - Progress bars

10. **Empty States**
    - No data illustrations
    - Call-to-action buttons

---

## 🔐 **Authentication Flow**

### **Session Management:**
1. Store session token in localStorage/sessionStorage
2. Include `Session-Key` header in all API requests
3. Implement token refresh mechanism
4. Auto-logout on token expiration
5. Redirect to login on 401 responses

### **Protected Routes:**
- All routes except `/login`, `/register/*` require authentication
- Implement route guards
- Role-based access control for certain pages

---

## 📊 **State Management Structure**

### **Global State:**
```typescript
{
  auth: {
    user: User | null,
    token: string | null,
    isAuthenticated: boolean,
    tenantId: string | null
  },
  ui: {
    sidebarOpen: boolean,
    theme: 'light' | 'dark',
    notifications: Notification[]
  },
  finance: {
    invoices: Invoice[],
    payments: Payment[],
    expenses: Expense[],
    payrolls: Payroll[]
  },
  inventory: {
    products: Product[],
    categories: Category[],
    stockLevels: StockLevel[],
    lowStockAlerts: LowStockAlert[]
  },
  hr: {
    employees: Employee[],
    attendance: Attendance[],
    leaves: LeaveRequest[],
    reviews: PerformanceReview[]
  },
  crm: {
    customers: Customer[],
    ledger: LedgerEntry[],
    balance: CustomerBalance[]
  }
}
```

---

## 🎯 **Priority Implementation Order**

### **Phase 1: Core Functionality** (2-3 weeks)
1. ✅ Authentication (Login, Logout)
2. ✅ Dashboard (Basic metrics)
3. ✅ Sidebar Navigation
4. ✅ Finance: Invoices (List, Create, View)
5. ✅ Finance: Payments (List, Create)
6. ✅ Inventory: Products (List, Create, Edit)
7. ✅ Inventory: Stock Levels (View)

### **Phase 2: Extended Features** (2-3 weeks)
8. ✅ Finance: Expenses (Full CRUD + Approval)
9. ✅ Finance: Payroll (Full CRUD + Workflow)
10. ✅ Inventory: Stock Movements (View, Filter)
11. ✅ Inventory: Low Stock Alerts
12. ✅ Inventory: Purchase Orders (Full CRUD)
13. ✅ HR: Employees (Full CRUD)
14. ✅ HR: Attendance (Mark, View)
15. ✅ HR: Leave Requests (Full CRUD + Approval)

### **Phase 3: Advanced Features** (2-3 weeks)
16. ✅ HR: Performance Reviews
17. ✅ CRM: Customers (Full CRUD)
18. ✅ CRM: Customer Ledger & Balance
19. ✅ Finance: Reports (All types)
20. ✅ Settings: Profile, Company, Users
21. ✅ Notifications System
22. ✅ Export functionality (PDF, Excel)

### **Phase 4: Polish & Optimization** (1-2 weeks)
23. ✅ Responsive design (mobile/tablet)
24. ✅ Dark mode
25. ✅ Performance optimization
26. ✅ Error handling & validation
27. ✅ Loading states & animations
28. ✅ Testing (Unit, Integration, E2E)

---

## 📱 **Responsive Design Breakpoints**

```css
/* Mobile */
@media (max-width: 640px) { }

/* Tablet */
@media (min-width: 641px) and (max-width: 1024px) { }

/* Desktop */
@media (min-width: 1025px) { }
```

**Mobile Adaptations:**
- Collapsible sidebar (hamburger menu)
- Stack cards vertically
- Simplified tables (card view)
- Bottom navigation for key actions

---

## 🎨 **Design System**

### **Colors:**
```css
/* Primary */
--primary: #3B82F6;
--primary-dark: #2563EB;
--primary-light: #60A5FA;

/* Success */
--success: #10B981;

/* Warning */
--warning: #F59E0B;

/* Error */
--error: #EF4444;

/* Neutral */
--gray-50: #F9FAFB;
--gray-100: #F3F4F6;
--gray-200: #E5E7EB;
--gray-500: #6B7280;
--gray-900: #111827;
```

### **Typography:**
```css
/* Headings */
h1: 2.5rem (40px)
h2: 2rem (32px)
h3: 1.5rem (24px)
h4: 1.25rem (20px)

/* Body */
body: 1rem (16px)
small: 0.875rem (14px)
```

---

## 🔔 **Notifications**

### **Types:**
1. **Toast Notifications** (temporary)
   - Success messages
   - Error messages
   - Info messages

2. **In-App Notifications** (persistent)
   - Low stock alerts
   - Pending approvals
   - Payment reminders
   - Leave requests

3. **Badge Counts**
   - Sidebar menu items
   - Notification bell

---

## 📤 **Export Functionality**

### **Export Formats:**
- CSV (for data tables)
- Excel (for reports)
- PDF (for invoices, payslips, reports)

### **Libraries:**
- **CSV**: `react-csv`
- **Excel**: `xlsx`
- **PDF**: `jsPDF` or `react-pdf`

---

## 🧪 **Testing Strategy**

### **Unit Tests:**
- Component rendering
- Utility functions
- State management

### **Integration Tests:**
- API integration
- Form submissions
- Navigation flows

### **E2E Tests:**
- Critical user journeys
- Login → Create Invoice → Create Payment
- Login → Add Product → Create PO → Receive Stock

### **Tools:**
- **Unit/Integration**: Jest + React Testing Library
- **E2E**: Playwright or Cypress

---

## 🚀 **Deployment**

### **Build:**
```bash
npm run build
```

### **Hosting Options:**
- Vercel (recommended for Next.js)
- Netlify
- AWS S3 + CloudFront
- Azure Static Web Apps

### **Environment Variables:**
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Elevate
VITE_APP_VERSION=2.0.0
```

---

## 📋 **Summary**

### **Total Pages: ~50+**
- Authentication: 3 pages
- Dashboard: 1 page
- Finance: 9 pages
- Inventory: 10 pages
- HR: 7 pages
- CRM: 6 pages
- Settings: 3 pages

### **Total Components: ~100+**
- Layout: 10
- Common: 30
- Forms: 20
- Tables: 15
- Charts: 10
- Modals: 15

### **Estimated Development Time:**
- **Solo Developer**: 8-12 weeks
- **Team of 2-3**: 6-8 weeks
- **Team of 4+**: 4-6 weeks

---

## ✅ **Next Steps**

1. **Choose Tech Stack** (React + TypeScript recommended)
2. **Set up Project** (Vite + React + TypeScript)
3. **Install Dependencies** (MUI, React Router, Axios, etc.)
4. **Create Project Structure**
5. **Implement Authentication** (Login, Session management)
6. **Build Layout** (Sidebar, Header, Dashboard)
7. **Implement Modules** (Follow priority order)
8. **Testing & Deployment**

---

**This specification provides a complete blueprint for building a production-ready frontend for the Elevate Business Management System!** 🚀
