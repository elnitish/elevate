# 🗺️ Elevate Frontend Sitemap

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         ELEVATE BUSINESS MANAGEMENT                          │
│                              Web Application                                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                ┌─────────────────────┴─────────────────────┐
                │                                           │
        ┌───────▼────────┐                         ┌───────▼────────┐
        │  PUBLIC PAGES  │                         │ PROTECTED PAGES│
        └───────┬────────┘                         └───────┬────────┘
                │                                           │
        ┌───────┴────────┐                         ┌───────┴────────────────────────────────┐
        │                │                         │                                        │
    ┌───▼───┐      ┌────▼────┐              ┌────▼────┐  ┌────────┐  ┌──────────┐  ┌──────────┐
    │ Login │      │Register │              │Dashboard│  │ Finance│  │Inventory │  │    HR    │
    └───────┘      └────┬────┘              └─────────┘  └────┬───┘  └────┬─────┘  └────┬─────┘
                        │                                      │           │             │
                  ┌─────┴─────┐                         ┌──────┴──────┐   │      ┌──────┴──────┐
                  │           │                         │             │   │      │             │
            ┌─────▼─────┐ ┌──▼──┐                 ┌────▼────┐   ┌────▼───▼──┐  │      ┌──────▼──────┐
            │  Tenant   │ │User │                 │Invoices │   │  Products │  │      │  Employees  │
            │ Register  │ │Reg. │                 └────┬────┘   └────┬──────┘  │      └──────┬──────┘
            └───────────┘ └─────┘                      │             │         │             │
                                                   ┌────┴────┐   ┌────┴─────┐  │      ┌──────┴──────┐
                                                   │         │   │          │  │      │             │
                                              ┌────▼────┐ ┌──▼──▼──┐  ┌────▼──▼──┐ ┌──▼──┐   ┌─────▼─────┐
                                              │Payments │ │Categories│ │Stock Lvls│ │Attend│   │  Leaves   │
                                              └────┬────┘ └─────────┘ └────┬─────┘ └──────┘   └─────┬─────┘
                                                   │                        │                        │
                                              ┌────▼────┐            ┌──────▼──────┐         ┌──────▼──────┐
                                              │Expenses │            │Stock Movemnt│         │Performance  │
                                              └────┬────┘            └──────┬──────┘         │   Reviews   │
                                                   │                        │                └─────────────┘
                                              ┌────▼────┐            ┌──────▼──────┐
                                              │ Payroll │            │ Low Stock   │
                                              └────┬────┘            │   Alerts    │
                                                   │                 └──────┬──────┘
                                              ┌────▼────┐            ┌──────▼──────┐
                                              │ Reports │            │  Suppliers  │
                                              └─────────┘            └──────┬──────┘
                                                                     ┌──────▼──────┐
                                                                     │  Purchase   │
                                                                     │   Orders    │
                                                                     └─────────────┘

                ┌────────────────────────────────────────────────────────────┐
                │                                                            │
         ┌──────▼──────┐                                           ┌────────▼────────┐
         │     CRM     │                                           │    SETTINGS     │
         └──────┬──────┘                                           └────────┬────────┘
                │                                                           │
    ┌───────────┴───────────┐                              ┌────────────────┼────────────────┐
    │                       │                              │                │                │
┌───▼────┐          ┌───────▼────────┐              ┌─────▼─────┐   ┌──────▼──────┐  ┌─────▼─────┐
│Customers│         │     Leads      │              │  Profile  │   │   Company   │  │   Users   │
└───┬────┘          │  (Not Ready)   │              │  Settings │   │  Settings   │  │Management │
    │               └────────────────┘              └───────────┘   └─────────────┘  └───────────┘
    │
    ├─────────┬─────────┐
    │         │         │
┌───▼────┐ ┌──▼──┐ ┌───▼────┐
│ Ledger │ │Bal. │ │Pipeline│
│        │ │     │ │(Not Rdy)│
└────────┘ └─────┘ └────────┘
```

---

## 📊 Module Hierarchy

### 🔐 **AUTHENTICATION** (Public)
```
/
├── /login
├── /register
│   ├── /register/tenant
│   └── /register/user
```

### 🏠 **DASHBOARD** (Protected)
```
/dashboard
└── Main Dashboard
    ├── Financial Overview Widget
    ├── Inventory Overview Widget
    ├── HR Overview Widget
    ├── CRM Overview Widget
    ├── Revenue vs Expenses Chart
    ├── Stock Levels Chart
    └── Recent Activities List
```

### 💰 **FINANCE** (Protected)
```
/finance
├── /finance/invoices
│   ├── List View
│   ├── /finance/invoices/new
│   └── /finance/invoices/:id/edit
├── /finance/payments
│   ├── List View
│   └── /finance/payments/new
├── /finance/expenses
│   ├── List View
│   └── /finance/expenses/new
├── /finance/payroll
│   ├── List View
│   └── /finance/payroll/new
└── /finance/reports
    ├── Profit & Loss
    ├── Monthly Overview
    └── Year-to-Date
```

### 📦 **INVENTORY** (Protected)
```
/inventory
├── /inventory/products
│   ├── List View
│   ├── /inventory/products/new
│   └── /inventory/products/:id/edit
├── /inventory/categories
│   └── List View (inline editing)
├── /inventory/stock
│   ├── Stock Levels View
│   └── Adjust Stock Modal
├── /inventory/stock/movements
│   └── Movements History
├── /inventory/alerts
│   └── Low Stock Alerts
├── /inventory/suppliers
│   └── List View
└── /inventory/purchase-orders
    ├── List View
    └── /inventory/purchase-orders/new
```

### 👥 **HR** (Protected)
```
/hr
├── /hr/employees
│   ├── List View
│   ├── /hr/employees/new
│   └── /hr/employees/:id/edit
├── /hr/attendance
│   ├── Mark Attendance
│   └── Attendance History
├── /hr/leaves
│   ├── List View
│   └── /hr/leaves/new
└── /hr/performance
    ├── List View
    └── /hr/performance/new
```

### 🤝 **CRM** (Protected)
```
/crm
├── /crm/customers
│   ├── List View
│   ├── /crm/customers/new
│   ├── /crm/customers/:id/edit
│   ├── /crm/customers/:id/ledger
│   └── /crm/customers/:id/balance
├── /crm/leads ⚠️ (Backend Not Ready)
│   └── List View / Kanban
└── /crm/pipeline ⚠️ (Backend Not Ready)
    └── Kanban Board
```

### ⚙️ **SETTINGS** (Protected)
```
/settings
├── /settings/profile
│   └── User Profile & Password
├── /settings/company
│   └── Company Information
└── /settings/users
    └── User Management
```

---

## 🔗 Navigation Flow

### Primary Navigation (Sidebar)
```
┌─────────────────────────┐
│ 🏠 Dashboard            │
├─────────────────────────┤
│ 💰 Finance              │
│   ├─ Invoices           │
│   ├─ Payments           │
│   ├─ Expenses           │
│   ├─ Payroll            │
│   └─ Reports            │
├─────────────────────────┤
│ 📦 Inventory            │
│   ├─ Products           │
│   ├─ Categories         │
│   ├─ Stock Levels       │
│   ├─ Stock Movements    │
│   ├─ Low Stock Alerts   │
│   ├─ Suppliers          │
│   └─ Purchase Orders    │
├─────────────────────────┤
│ 👥 HR                   │
│   ├─ Employees          │
│   ├─ Attendance         │
│   ├─ Leave Requests     │
│   └─ Performance        │
├─────────────────────────┤
│ 🤝 CRM                  │
│   ├─ Customers          │
│   ├─ Leads              │
│   └─ Pipeline           │
├─────────────────────────┤
│ ⚙️ Settings             │
│   ├─ Profile            │
│   ├─ Company            │
│   └─ Users              │
└─────────────────────────┘
```

### Top Header Navigation
```
┌────────────────────────────────────────────────────────────┐
│ [Logo] Elevate     [Search...]    [🔔 3]  [👤 Admin ▼]    │
└────────────────────────────────────────────────────────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │ Profile           │
                                    │ Settings          │
                                    │ Logout            │
                                    └───────────────────┘
```

---

## 🎯 User Journey Examples

### Journey 1: Create Invoice & Record Payment
```
Login → Dashboard → Finance → Invoices → Create Invoice → 
Fill Form → Submit → View Invoice → 
Go to Payments → Create Payment → 
Select Invoice → Submit → Success
```

### Journey 2: Manage Low Stock
```
Login → Dashboard → See Low Stock Alert → 
Click Alert → View Low Stock Products → 
Click "Create PO" → Select Supplier → 
Add Products → Submit PO → 
PO Status: PENDING → Approve PO → 
PO Status: APPROVED → Receive Stock → 
Stock Levels Updated → Alert Cleared
```

### Journey 3: Process Payroll
```
Login → Dashboard → Finance → Payroll → 
Create Payroll → Select Employee → 
Enter Salary Details → Calculate → 
Submit (Status: DRAFT) → 
Approve (Status: APPROVED) → 
Process (Status: PROCESSED) → 
Pay (Status: PAID) → Success
```

### Journey 4: Manage Leave Request
```
Login → Dashboard → HR → Leave Requests → 
Create Leave → Select Employee → 
Enter Dates & Reason → Submit → 
Manager Reviews → Approve/Reject → 
Employee Notified → Success
```

---

## 📱 Responsive Layouts

### Desktop (>1024px)
- Full sidebar visible
- Multi-column layouts
- Large tables
- Side-by-side forms

### Tablet (641px - 1024px)
- Collapsible sidebar
- 2-column layouts
- Scrollable tables
- Stacked forms

### Mobile (<640px)
- Hidden sidebar (hamburger menu)
- Single column
- Card-based tables
- Vertical forms
- Bottom navigation for key actions

---

## 🎨 Page Templates

### List Page Template
```
┌─────────────────────────────────────────────────────────┐
│ Breadcrumb: Home > Module > Page                        │
├─────────────────────────────────────────────────────────┤
│ [Page Title]                          [+ Create Button] │
├─────────────────────────────────────────────────────────┤
│ [Search] [Filter 1▼] [Filter 2▼] [Date Range]          │
├─────────────────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────────────────┐ │
│ │ Column 1 │ Column 2 │ Column 3 │ Column 4 │ Actions│ │
│ ├──────────┼──────────┼──────────┼──────────┼────────┤ │
│ │ Data 1   │ Data 2   │ Data 3   │ Data 4   │ [Edit] │ │
│ │ Data 1   │ Data 2   │ Data 3   │ Data 4   │ [Edit] │ │
│ └─────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────┤
│ Showing 1-10 of 100        [< Prev] [1] [2] [3] [Next >]│
└─────────────────────────────────────────────────────────┘
```

### Form Page Template
```
┌─────────────────────────────────────────────────────────┐
│ Breadcrumb: Home > Module > Page > Create/Edit          │
├─────────────────────────────────────────────────────────┤
│ [Page Title]                                             │
├─────────────────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────────────────┐ │
│ │ Field Label 1:  [Input Field                     ]  │ │
│ │ Field Label 2:  [Input Field                     ]  │ │
│ │ Field Label 3:  [Dropdown ▼                      ]  │ │
│ │ Field Label 4:  [Date Picker                     ]  │ │
│ │                                                      │ │
│ │ [Cancel Button]              [Save/Submit Button]   │ │
│ └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### Dashboard Template
```
┌─────────────────────────────────────────────────────────┐
│ Welcome back, [User Name]!                               │
├─────────────────────────────────────────────────────────┤
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│ │ Card 1   │ │ Card 2   │ │ Card 3   │ │ Card 4   │   │
│ │ $50,000  │ │ 150 Items│ │ 25 Emp   │ │ 100 Cust │   │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
├─────────────────────────────────────────────────────────┤
│ ┌─────────────────────────┐ ┌───────────────────────┐  │
│ │ Revenue vs Expenses     │ │ Stock Levels          │  │
│ │ [Line Chart]            │ │ [Bar Chart]           │  │
│ └─────────────────────────┘ └───────────────────────┘  │
├─────────────────────────────────────────────────────────┤
│ Recent Activities                                        │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ • Invoice #123 created - 2 hours ago                │ │
│ │ • Payment received - 3 hours ago                    │ │
│ │ • Low stock alert: Product A - 5 hours ago          │ │
│ └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

**This sitemap provides a complete visual overview of the frontend structure! 🗺️**
