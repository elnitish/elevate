# Elevate ERP — Technical Documentation

> **Enterprise-grade multi-tenant business management platform for retailers and wholesalers**
> Spring Boot 3.5.5 | Java 21 | MySQL 8 | Flyway | Caffeine Cache

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Technology Stack](#2-technology-stack)
3. [Module Structure](#3-module-structure)
4. [Authentication & Multi-Tenancy](#4-authentication--multi-tenancy)
5. [Database Schema](#5-database-schema)
6. [API Reference](#6-api-reference)
7. [Business Flows](#7-business-flows)
8. [Pricing Engine](#8-pricing-engine)
9. [Reporting & Analytics](#9-reporting--analytics)
10. [Performance Architecture](#10-performance-architecture)
11. [Configuration Reference](#11-configuration-reference)
12. [Deployment Guide](#12-deployment-guide)

---

## 1. Architecture Overview

```
                    +-----------------------+
                    |    REST API Layer      |
                    |   (Controllers + DTOs) |
                    +-----------+-----------+
                                |
          +---------------------+---------------------+
          |                     |                      |
+---------v--------+  +---------v--------+  +----------v---------+
| Session Auth     |  | Global Exception |  | Logging Filter     |
| (HandlerIntercept)|  | Handler          |  | (Request/Response) |
+------------------+  +------------------+  +--------------------+
                                |
                    +-----------v-----------+
                    |    Service Layer       |
                    | (Business Logic +     |
                    |  @Transactional +     |
                    |  @Cacheable)          |
                    +-----------+-----------+
                                |
          +---------------------+---------------------+
          |                     |                      |
+---------v--------+  +---------v--------+  +----------v---------+
| Spring Data JPA  |  | Event Publisher   |  | Pricing Engine     |
| (Repositories)   |  | (Async Listeners) |  | (Price Resolution) |
+------------------+  +------------------+  +--------------------+
          |
+---------v---------+
| MySQL 8 + Flyway  |
| (33 migrations)   |
+-------------------+
```

### Key Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Multi-tenancy | Row-level `tenant_id` | Simple, no schema-per-tenant overhead |
| Authentication | Session tokens (not JWT) | Revocable, server-validated per request |
| ID strategy | UUID (CHAR 36) for most entities | Distributed ID generation, no collisions |
| Pricing | Strategy pattern with priority chain | Customer-type > default > product price |
| Stock safety | Optimistic locking + `@Retryable` | High concurrency without pessimistic locks |
| Caching | Caffeine (in-process) | Zero-infra, swappable to Redis later |
| Async | Spring Events + `@TransactionalEventListener` | Decoupled post-commit processing |

---

## 2. Technology Stack

### Core Framework
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.5.5 | Application framework |
| Java | 21 | Runtime (LTS) |
| Spring Data JPA | 3.5.x | ORM / Repository pattern |
| Spring Security | 3.5.x | Security framework (custom session auth) |
| Spring Validation | 3.5.x | Bean validation (`@Valid`) |

### Database & Migration
| Component | Version | Purpose |
|-----------|---------|---------|
| MySQL | 8.x | Primary database |
| Flyway | managed | Schema versioning (V1-V33) |
| HikariCP | managed | Connection pooling |

### Caching & Resilience
| Component | Version | Purpose |
|-----------|---------|---------|
| Caffeine | managed | In-process cache (5-min TTL) |
| Spring Retry | managed | Optimistic lock retry (3 attempts) |

### API & Docs
| Component | Version | Purpose |
|-----------|---------|---------|
| SpringDoc OpenAPI | 2.3.0 | Swagger UI at `/swagger-ui.html` |

### Data Mapping
| Component | Version | Purpose |
|-----------|---------|---------|
| MapStruct | 1.5.5 | Type-safe DTO mapping |
| Lombok | managed | Boilerplate reduction |

---

## 3. Module Structure

```
com.elevate
├── ElevateApplication.java          # @SpringBootApplication + @EnableRetry + @EnableScheduling
├── OpenApiConfiguration.java        # Swagger/OpenAPI setup
│
├── auth/                            # Authentication & Multi-Tenancy
│   ├── configuration/               #   SessionAuthentication interceptor
│   ├── controllers/                 #   /auth/* endpoints
│   ├── dto/                         #   ApiResponse<T>, UserReq/ResDTO, TenantReq/ResDTO
│   ├── entity/                      #   TenantClass, UserClass, AuthCredentials, SessionToken
│   ├── repository/                  #   JPA repositories
│   └── service/                     #   UserService, TenantService, SessionService
│
├── config/                          # Global Configuration
│   ├── AsyncConfig.java             #   Thread pool (4 core, 10 max)
│   ├── CacheConfig.java             #   Caffeine cache manager (5 caches)
│   ├── GlobalExceptionHandler.java  #   @RestControllerAdvice
│   ├── InsufficientStockException.java
│   ├── LoggingFilter.java           #   HTTP request/response logging
│   ├── TenantContext.java           #   Tenant ID extraction utility
│   └── WebConfig.java               #   Interceptor registration
│
├── crm/                             # Customer Relationship Management
│   ├── controller/                  #   CustomerController, BalanceController, LedgerController
│   ├── dto/                         #   CustomerReq/ResDTO, LedgerReq/ResDTO, BalanceResDTO
│   ├── entity/                      #   CustomerClass (B2C/B2B/WHOLESALE/RETAIL), Ledger, Balance
│   ├── repository/                  #   Paginated queries
│   └── service/                     #   CustomerService, LedgerService, BalanceService
│
├── fna/                             # Finance & Accounting
│   ├── controller/                  #   InvoiceController, PaymentController, ExpenseController,
│   │                                #   PayrollController, FinanceReportController, DashboardController
│   ├── dto/                         #   19 DTOs (Invoice, Payment, Expense, Payroll, Dashboard, Aging)
│   ├── entity/                      #   InvoiceClass, InvoiceItemsClass, PaymentClass, ExpenseClass, PayrollClass
│   ├── event/                       #   InvoiceCreatedEvent, InvoiceEventListener (async)
│   ├── repository/                  #   SQL aggregation queries (SUM, COUNT)
│   └── service/                     #   InvoiceService, PaymentService, FinanceReportService,
│                                    #   DashboardService, OverdueInvoiceScheduler
│
├── hrs/                             # Human Resources
│   ├── controller/                  #   EmployeeController, LeaveRequestController, PerformanceReviewController
│   ├── dto/                         #   EmployeeAttendanceDTO, LeaveReq/ResDTO, PerformanceReq/ResDTO
│   ├── entity/                      #   EmployeeClass, LeaveRequestClass, PerformanceReviewClass, Attendance
│   ├── repository/
│   └── service/
│
├── insc/                            # Inventory & Supply Chain
│   ├── controller/                  #   ProductController, CategoryController, StockController,
│   │                                #   PurchaseOrderController, WarehouseController, InventoryReportController
│   ├── dto/                         #   21 DTOs (Product, Category, Stock, PO, Warehouse, Transfer, Valuation)
│   ├── entity/                      #   ProductClass, CategoryClass, StockLevelClass, StockMovementClass,
│   │                                #   PurchaseOrderClass, SupplierClass, WarehouseClass, WarehouseTransferClass
│   ├── repository/                  #   JOIN FETCH queries, paginated, warehouse-aware
│   └── service/                     #   ProductService, StockLevelService, StockMovementService,
│                                    #   WarehouseService, WarehouseTransferService, InventoryReportService
│
└── pricing/                         # Pricing Engine
    ├── controller/                  #   PriceListController, DiscountController
    ├── dto/                         #   PriceListReq/ResDTO, PriceListItemReq/ResDTO, DiscountReq/ResDTO, ResolvedPriceDTO
    ├── entity/                      #   PriceListClass, PriceListItemClass, DiscountClass
    ├── repository/                  #   Quantity-tier matching queries
    └── service/                     #   PricingService (resolution engine), PriceListService, DiscountService
```

**File counts**: 193 Java files | 33 SQL migrations | 61 DTOs | ~115 REST endpoints

---

## 4. Authentication & Multi-Tenancy

### Session-Based Auth Flow

```
Client                        Server
  │                              │
  │  POST /auth/tenantRegister   │
  │─────────────────────────────>│  Creates Tenant + Admin User
  │  { tenantId, userId }        │
  │<─────────────────────────────│
  │                              │
  │  POST /auth/userLogin        │
  │  { tenantId, username, pwd } │
  │─────────────────────────────>│  Validates credentials
  │  { sessionToken }            │  Creates SessionToken record
  │<─────────────────────────────│
  │                              │
  │  GET /any-endpoint           │
  │  Header: Session-Key: <tok>  │
  │─────────────────────────────>│  SessionAuthentication interceptor:
  │  { data }                    │  1. Validates token exists
  │<─────────────────────────────│  2. Sets request.tenantID, role
  │                              │  3. All queries filter by tenantID
```

### Tenant Isolation

Every database table includes `tenant_id`. Every service method receives `tenantId` from the request and uses it in all queries. There is no cross-tenant data leakage by design.

```java
// TenantContext utility (prevents casing bugs)
String tenantId = TenantContext.getTenantId(request);
```

### Roles
- `ADMIN` — Full access within tenant
- `USER` — Standard access within tenant

---

## 5. Database Schema

### Flyway Migrations (V1-V33)

| Phase | Migrations | Purpose |
|-------|-----------|---------|
| Original | V1-V19 | Core schema: auth, customers, invoices, products, stock, suppliers, POs, expenses, HR |
| Phase 0: Foundation | V20-V21 | Performance indexes on all tables, optimistic locking (`version` column) |
| Phase 1: Inventory | V22-V26 | Warehouses, SKU/barcode, multi-warehouse stock, batch tracking, transfers |
| Phase 2: Pricing | V27-V30 | Customer types, price lists, price list items, discounts |
| Phase 3: Invoicing | V31-V33 | Invoice enhancements (due date, tax, status), item-level tax, payment notes |

### Entity Relationship Diagram

```
tenants (1) ──────── (*) users
   │                      │
   │                      └── auth_credentials
   │                      └── session_tokens
   │
   ├── (*) customers
   │       ├── customer_balance (1:1)
   │       ├── customer_ledger (1:*)
   │       ├── invoices (1:*)
   │       │     ├── invoice_items (1:*)
   │       │     └── payments (1:*)
   │       └── [customer_type: B2C/B2B/WHOLESALE/RETAIL]
   │
   ├── (*) categories
   │       └── (*) products
   │             ├── [sku, barcode, unit, is_active]
   │             ├── stock_levels (1:* per warehouse)
   │             ├── stock_movements (1:*)
   │             ├── invoice_items (1:*)
   │             └── purchase_order_items (1:*)
   │
   ├── (*) warehouses
   │       ├── stock_levels (1:*)
   │       ├── stock_movements (1:*)
   │       └── warehouse_transfers (source/dest)
   │
   ├── (*) suppliers
   │       └── purchase_orders (1:*)
   │             └── purchase_order_items (1:*)
   │
   ├── (*) price_lists
   │       └── price_list_items (1:*)
   │
   ├── (*) discounts
   │
   ├── (*) expenses
   │
   ├── (*) employees
   │       ├── payroll (1:*)
   │       ├── leave_requests (1:*)
   │       ├── performance_reviews (1:*)
   │       └── attendance (1:*)
```

### Key Indexes (V20)

All foreign keys and frequently-filtered columns are indexed:
- `stock_levels(tenant_id, product_id, warehouse_id)` — UNIQUE
- `invoices(tenant_id, status)`, `(tenant_id, date)`, `(tenant_id, due_date)`
- `stock_movements(tenant_id, type)`, `(tenant_id, date)`
- `payments(tenant_id, invoice_id)`, `(tenant_id, payment_date)`
- `products(tenant_id, sku)` — UNIQUE, `(tenant_id, barcode)`

---

## 6. API Reference

### Authentication (`/auth`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/tenantRegister` | None | Register new tenant organization |
| POST | `/auth/createUser` | None | Create user within tenant |
| POST | `/auth/userLogin` | None | Login, returns session token |
| GET | `/auth/allUsers` | Session | List all users in tenant |
| POST | `/auth/userLogout` | Session | Invalidate session |

### Customers (`/customers`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/customers/createCustomer` | Create customer (B2C/B2B/WHOLESALE/RETAIL) |
| GET | `/customers/getAllCustomers` | List all customers |
| GET | `/customers/getCustomerById/{id}` | Get customer by ID |
| PUT | `/customers/updateCustomer/{id}` | Update customer (includes type, credit limit, payment terms) |
| DELETE | `/customers/deleteCustomer/{id}` | Delete customer |

**Customer Balance** (`/customerBalance`):
| GET | `/customerBalance/getAllBalances` | Paginated balances |
| GET | `/customerBalance/getBalanceByCustomer/{id}` | Single customer balance |

**Customer Ledger** (`/customerLedger`):
| GET | `/customerLedger/getEntriesByCustomerId/{id}` | Paginated ledger entries |
| GET | `/customerLedger/getAllEntries` | All ledger entries |

### Products (`/insc`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/insc/createProduct` | Create product (with optional SKU, barcode, unit) |
| GET | `/insc/getAllProducts` | List all products (cached) |
| GET | `/insc/getProductById/{id}` | Get product (cached) |
| PUT | `/insc/updateProduct` | Update product (evicts cache) |
| DELETE | `/insc/deleteProductById/{id}` | Delete product (evicts cache) |
| GET | `/insc/barcode/{barcode}` | **POS scan** — lookup by barcode |
| GET | `/insc/sku/{sku}` | Lookup by SKU |

### Categories (`/category`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/category/createCategory` | Create category (evicts cache) |
| GET | `/category/getAllCategory` | List categories (cached) |
| PUT | `/category/updateCategory` | Update category |
| DELETE | `/category/deleteCategory/{id}` | Delete category |

### Warehouses (`/warehouses`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/warehouses` | Create warehouse (code auto-uppercased) |
| GET | `/warehouses` | List all warehouses (cached) |
| GET | `/warehouses/{id}` | Get warehouse by ID |
| PUT | `/warehouses/{id}` | Update warehouse |
| PUT | `/warehouses/{id}/deactivate` | Deactivate (cannot deactivate default) |

### Stock Management (`/stock`)

**Stock Levels:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/stock/levels?warehouseId=&page=&size=` | Paginated stock levels (optional warehouse filter) |
| GET | `/stock/levels/product/{id}` | Stock for specific product |
| PUT | `/stock/levels/adjust` | Manual stock adjustment |
| GET | `/stock/alerts/low-stock?threshold=` | Low stock alerts (uses per-product reorder point or custom threshold) |

**Stock Movements:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/stock/movements?page=&size=` | All movements (paginated) |
| GET | `/stock/movements/product/{id}` | Movements by product |
| GET | `/stock/movements/type/{IN\|OUT}` | Movements by type |
| GET | `/stock/movements/date-range?startDate=&endDate=` | Movements by date range |
| GET | `/stock/movements/purchase-order/{id}` | Movements by PO |
| GET | `/stock/movements/invoice/{id}` | Movements by invoice |

**Warehouse Transfers:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/stock/transfers` | Create transfer (PENDING) |
| PUT | `/stock/transfers/{id}/complete` | Complete transfer (moves stock, records movements) |
| PUT | `/stock/transfers/{id}/cancel` | Cancel transfer |
| GET | `/stock/transfers?status=&page=&size=` | List transfers |
| GET | `/stock/transfers/{id}` | Get transfer by ID |

### Purchase Orders (`/purchaseOrder`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/purchaseOrder/createNewPurchaseOrder` | Create PO with items |
| GET | `/purchaseOrder/getAllPurchaseOrder` | List all POs |
| GET | `/purchaseOrder/getPurchaseOrderById/{id}` | Get PO by ID |
| PUT | `/purchaseOrder/updatePurchaseOrderStatus/{id}/{status}` | Update PO status |
| POST | `/purchaseOrder/createNewSupplier` | Create supplier |
| GET | `/purchaseOrder/getPurchaseOrderBySupplier/{id}` | POs by supplier |
| GET | `/purchaseOrder/getPurchaseOrderByStatus/{status}` | POs by status |

### Invoices (`/finance`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/finance/createInvoice` | Create invoice (pricing engine resolves prices, auto-calculates tax, due date) |
| GET | `/finance/getAllInvoices?status=` | List invoices (optional status filter) |
| GET | `/finance/getInvoiceById?id=` | Get invoice by ID |
| PUT | `/finance/invoices/{id}/{status}` | Update invoice status |
| PUT | `/finance/invoices/{id}/cancel` | Cancel invoice |
| GET | `/finance/invoices/overdue` | List overdue invoices |

**Invoice Status Lifecycle:**
```
DRAFT ──> PENDING ──> PARTIALLY_PAID ──> PAID
                  ──> OVERDUE (auto, daily scheduler)
                  ──> CANCELLED
```

### Payments (`/payments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/payments/createPayment` | Create payment (validates no overpayment, auto-updates invoice status) |
| GET | `/payments/getPaymentByInvoice/{id}` | Payments for invoice |
| GET | `/payments/getPaymentById/{id}` | Get payment by ID |
| GET | `/payments/getAllPayments` | List all payments |
| DELETE | `/payments/deletePayment/{id}` | Delete payment (reverses ledger + invoice status) |

### Expenses (`/finance/expenses`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/finance/expenses` | Create expense |
| GET | `/finance/expenses` | List all expenses |
| GET | `/finance/expenses/{id}` | Get expense |
| GET | `/finance/expenses/by-status/{status}` | Filter by status |
| GET | `/finance/expenses/by-category/{category}` | Filter by category |
| GET | `/finance/expenses/by-date-range` | Filter by date range |
| PUT | `/finance/expenses/{id}` | Update expense |

### Payroll (`/finance/payroll`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/finance/payroll` | Create payroll entry |
| GET | `/finance/payroll` | List all payrolls |
| GET | `/finance/payroll/{id}` | Get payroll by ID |
| GET | `/finance/payroll/employee/{id}` | Payrolls by employee |
| GET | `/finance/payroll/by-status/{status}` | Payrolls by status |
| GET | `/finance/payroll/by-month/{yearMonth}` | Payrolls by month |
| PUT | `/finance/payroll/{id}` | Update payroll |
| DELETE | `/finance/payroll/{id}` | Delete payroll |

### Pricing (`/price-lists`, `/discounts`)

**Price Lists:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/price-lists` | Create price list (optional customer type, date range) |
| GET | `/price-lists` | List all price lists |
| GET | `/price-lists/{id}` | Get price list |
| POST | `/price-lists/{id}/items` | Add item with quantity tier pricing |
| GET | `/price-lists/{id}/items` | List items |
| DELETE | `/price-lists/{id}/items/{itemId}` | Remove item |
| PUT | `/price-lists/{id}/deactivate` | Deactivate price list |
| GET | `/price-lists/resolve?productId=&customerId=&quantity=` | **Resolve effective price** |

**Discounts:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/discounts` | Create discount (PERCENTAGE/FIXED_AMOUNT) |
| GET | `/discounts` | List all discounts |
| GET | `/discounts/active` | Active discounts only |
| PUT | `/discounts/{id}/deactivate` | Deactivate discount |

### Dashboard & Reports (`/dashboard`, `/reports`)

**Dashboard:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Current month: revenue, receivables, invoice counts, low stock, cash flow |
| GET | `/dashboard/revenue-trend?months=6` | Monthly revenue/expense/net for last N months |
| GET | `/dashboard/aging-report` | Receivables aging: current, 1-30, 31-60, 61-90, 90+ days with per-customer breakdown |

**Finance Reports:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reports/finance/profit-loss/{yearMonth}` | P&L report (SQL aggregation) |
| GET | `/reports/finance/monthly-overview/{yearMonth}` | Monthly finance overview |
| GET | `/reports/finance/expense-summary/{yearMonth}` | Expense breakdown by category |
| GET | `/reports/finance/year-to-date?year=` | Year-to-date P&L |

**Inventory Reports:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reports/inventory/valuation` | Total cost value, retail value, potential profit |

### Human Resources (`/api/hr`, `/hr`)

**Employees & Attendance:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hr/employees` | List employees |
| POST | `/api/hr/employees` | Create employee |
| GET | `/api/hr/employees/{id}` | Get employee |
| PUT | `/api/hr/employees/{id}` | Update employee |
| DELETE | `/api/hr/employees/{id}` | Delete employee |
| POST | `/api/hr/attendance` | Mark attendance |
| GET | `/api/hr/attendance` | List attendance |
| GET | `/api/hr/attendance/employee/{id}` | Employee attendance |

**Leave Requests & Performance Reviews:** Full CRUD with filtering by employee, status, date range.

---

## 7. Business Flows

### Invoice Creation (end-to-end)

```
1. Client sends POST /finance/createInvoice
   │
2. Validate customer exists (CRM)
   │
3. Calculate payment terms:
   │  Request terms > Customer terms > 0
   │  dueDate = invoiceDate + paymentTermsDays
   │
4. For each item:
   │  a. Validate product exists
   │  b. Check stock availability (StockLevelService)
   │  c. Resolve price via PricingService:
   │     Priority: customer-type price list > default price list > product.sellingPrice
   │  d. Calculate per-item tax
   │  e. Deduct stock (optimistic lock + retry)
   │
5. Calculate invoice totals:
   │  subtotal = SUM(qty * unitPrice)
   │  discountAmount = invoice-level discount
   │  taxableAmount = subtotal - discount
   │  taxAmount = taxableAmount * taxRate / 100
   │  totalAmount = taxableAmount + taxAmount
   │
6. Generate invoice number: INV-YYYYMMDD-NNNN
   │
7. Save invoice + items (single @Transactional)
   │
8. Publish InvoiceCreatedEvent
   │  └── Async listener (after commit):
   │      ├── Record stock movements (OUT)
   │      └── Add customer ledger DEBIT entry
   │
9. Return InvoiceResDTO
```

### Payment Flow

```
1. Validate invoice + customer match
2. Validate amount <= remainingAmount (no overpayment)
3. Update invoice remainingAmount
4. Auto-update status:
   │  remaining == 0  ──> PAID
   │  remaining > 0   ──> PARTIALLY_PAID
5. Record customer ledger CREDIT entry
```

### Warehouse Transfer

```
1. POST /stock/transfers  (status: PENDING)
2. PUT  /stock/transfers/{id}/complete
   │  a. Decrease stock in source warehouse (optimistic lock)
   │  b. Increase stock in destination warehouse
   │  c. Record 2 stock movements (type: TRANSFER)
   │  d. Status → COMPLETED
```

### Overdue Detection (Automated)

```
@Scheduled(cron = "0 0 1 * * *")  ←── Daily at 1 AM
  │
  SELECT invoices WHERE status IN (PENDING, PARTIALLY_PAID)
    AND due_date < CURRENT_DATE
  │
  UPDATE status = OVERDUE
```

---

## 8. Pricing Engine

### Resolution Priority

```
PricingService.resolvePrice(tenantId, productId, customerId, quantity)

  1. Customer-type price list
     │  Find active price lists matching customer's type (B2B, WHOLESALE, etc.)
     │  Match by productId + quantity tier (minQuantity <= qty <= maxQuantity)
     │  Apply discount percent from tier
     │
  2. Default price list
     │  Find tenant's default active price list
     │  Same tier matching logic
     │
  3. Product selling price (fallback)
     │  product.getSellingPrice()
```

### Quantity Tiers Example

| Price List | Product | Min Qty | Max Qty | Unit Price | Discount |
|-----------|---------|---------|---------|------------|----------|
| Wholesale | Widget  | 1       | 9       | 10.00      | 0%       |
| Wholesale | Widget  | 10      | 49      | 9.00       | 5%       |
| Wholesale | Widget  | 50      | NULL    | 8.00       | 10%      |

Buying 25 widgets as a WHOLESALE customer resolves to: 9.00 * 0.95 = 8.55 per unit.

### Cache

Price resolution is cached with key `tenantId:productId:customerId:quantity` (5-min TTL). Cache eviction happens when price lists are modified.

---

## 9. Reporting & Analytics

### SQL Aggregation (Phase 4 optimization)

All reports use database-level `SUM()` / `COUNT()` queries instead of loading full entity lists:

```java
// Before (O(n) memory):
List<PaymentClass> payments = paymentRepo.findByTenantIdAndDateBetween(...);
BigDecimal total = payments.stream().map(PaymentClass::getAmount).reduce(BigDecimal::add);

// After (O(1) memory):
BigDecimal total = paymentRepo.sumAmountByTenantAndDateBetween(tenantId, start, end);
```

### Dashboard Summary (`GET /dashboard/summary`)

Returns in a single response:
- Current month revenue (from payments)
- Outstanding receivables (unpaid invoice remaining amounts)
- Invoice counts (total, paid, pending, overdue)
- Low stock product count
- Current month expenses
- Cash flow (revenue - expenses)

### Aging Report (`GET /dashboard/aging-report`)

Buckets unpaid invoices by days past due:

| Bucket | Criteria |
|--------|----------|
| Current | Not yet due (`dueDate >= today`) |
| 1-30 days | 1-30 days past due |
| 31-60 days | 31-60 days past due |
| 61-90 days | 61-90 days past due |
| 90+ days | More than 90 days past due |

Includes per-customer breakdown.

### Revenue Trend (`GET /dashboard/revenue-trend?months=6`)

Returns monthly revenue, expenses, and net income for the last N months. Useful for charts.

---

## 10. Performance Architecture

### Caching (Caffeine)

| Cache Name | TTL | Max Size | Cached By | Evicted By |
|-----------|-----|----------|-----------|------------|
| `products` | 5 min | 1000 | `returnAllProducts(tenantId)` | create/update/delete product |
| `productById` | 5 min | 1000 | `getProductById(productId)` | update/delete product |
| `categories` | 5 min | 1000 | `getCategoriesByTenant(tenantId)` | create/update/delete category |
| `warehouses` | 5 min | 1000 | `getAllWarehouses(tenantId)` | create/update warehouse |
| `priceResolution` | 5 min | 1000 | `resolvePrice(tenant:product:customer:qty)` | price list changes |

**Migration to Redis**: Replace `CaffeineCacheManager` bean with `RedisCacheManager` — all `@Cacheable` / `@CacheEvict` annotations work unchanged.

### Async Processing

```
InvoiceService.createNewInvoice()
  │  ── save invoice (synchronous, in @Transactional)
  │  ── publishEvent(InvoiceCreatedEvent)
  │  ── return response to client  ←── FAST
  │
  └── InvoiceEventListener (after commit, async thread pool):
       ├── Record stock movements
       └── Record ledger entries
```

Thread pool: 4 core / 10 max / 50 queue capacity.

### Optimistic Locking (Stock Safety)

```java
@Entity
public class StockLevelClass {
    @Version
    private Long version;  // Auto-incremented by Hibernate on each save
}

@Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
public boolean decreaseStock(...) {
    // If two concurrent requests try to modify the same stock level,
    // one succeeds and one gets OptimisticLockException → automatic retry
}
```

### Query Optimizations

- **`hibernate.default_batch_fetch_size=20`** — reduces N+1 on lazy collections globally
- **`@BatchSize(size=20)`** on `InvoiceClass.items` and `PriceListClass.items`
- **JOIN FETCH** queries in StockLevel and StockMovement repositories
- **`@Transactional(readOnly=true)`** on all read-only service methods (enables future read-replica routing)
- **HikariCP**: 20 max connections, 5 min idle, 300s idle timeout

### Pagination

All list endpoints support `?page=0&size=20` with max size capped at 100. Returns Spring `Page<T>` with metadata (totalElements, totalPages, etc.).

---

## 11. Configuration Reference

### application.properties

```properties
# Application
spring.application.name=Elevate
server.address=0.0.0.0

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/elevate
spring.datasource.username=root
spring.datasource.password=<your-password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.default_batch_fetch_size=20

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# Security
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

# Swagger UI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

### Required Headers

| Header | Value | Required For |
|--------|-------|-------------|
| `Session-Key` | UUID session token | All endpoints except `/auth/**` |
| `Content-Type` | `application/json` | POST/PUT requests |

---

## 12. Deployment Guide

### Prerequisites

- Java 21+
- MySQL 8.0+
- Maven 3.9+

### Setup

```bash
# 1. Create MySQL database
mysql -u root -p -e "CREATE DATABASE elevate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. Update application.properties with your DB credentials

# 3. Build
mvn clean package -DskipTests

# 4. Run (Flyway auto-applies all 33 migrations)
java -jar target/Elevate-0.0.1-SNAPSHOT.jar

# 5. Access
#    API:        http://localhost:8080
#    Swagger UI: http://localhost:8080/swagger-ui.html
#    API Docs:   http://localhost:8080/api-docs
```

### Production Recommendations

| Area | Recommendation |
|------|---------------|
| Database | Use connection pooling (HikariCP configured), add read replicas for reports |
| Caching | Swap Caffeine to Redis for multi-instance deployments |
| Security | Move DB password to environment variable or secrets manager |
| Monitoring | Add Spring Boot Actuator + Micrometer for metrics |
| Logging | Configure structured JSON logging for ELK/Datadog |
| API Gateway | Put behind Nginx/Kong for rate limiting, SSL termination |
| Scaling | Stateless design (session tokens in DB) allows horizontal scaling |

---

## Build Statistics

| Metric | Value |
|--------|-------|
| Java files | 193 |
| Flyway migrations | 33 (V1-V33) |
| REST endpoints | ~115 |
| Entity classes | 27 |
| DTO classes | 61 |
| Spring Boot | 3.5.5 |
| Java | 21 |
| Build status | SUCCESS (0 errors) |
