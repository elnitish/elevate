# Elevate API Documentation

This document provides comprehensive documentation for all REST API endpoints in the Elevate application. The application is organized into four main modules: Authentication (AUTH), Financial (FNA), Human Resources (HRS), and Inventory/Supply Chain (INSC).

**Important**: All endpoints (except login/register) require a session token in the `X-Session-Token` header for authentication. The tenant ID is automatically extracted from the session token.

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Financial APIs](#financial-apis)
3. [Human Resources APIs](#human-resources-apis)
4. [Inventory & Supply Chain APIs](#inventory--supply-chain-apis)

---

## Authentication APIs

Base URL: `/api/auth`

### User Authentication

#### POST `/api/auth/login`
- **Description**: Authenticate a user and return session token
- **Request Body**: `UserClassReqDTO`
- **Response**: `ApiResponse<?>` with user details and session token
- **Status Codes**: 200 (Success), 401 (Unauthorized), 400 (Bad Request)

#### POST `/api/auth/register`
- **Description**: Register a new tenant (organization)
- **Request Body**: `TenantReqDTO`
- **Response**: `ApiResponse<?>` with tenant details
- **Status Codes**: 201 (Created), 400 (Bad Request), 409 (Conflict)

#### POST `/api/auth/users`
- **Description**: Create a new user within a tenant
- **Request Body**: `UserCreateReqDTO`
- **Response**: `ApiResponse<?>` with user details
- **Status Codes**: 201 (Created), 400 (Bad Request), 409 (Conflict)

### Session Management

#### GET `/api/auth/validate-token`
- **Description**: Validate a session token
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with validation result
- **Status Codes**: 200 (Valid), 401 (Invalid/Expired)

#### POST `/api/auth/logout`
- **Description**: Logout user and invalidate session token
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with logout confirmation
- **Status Codes**: 200 (Success), 404 (Token not found)

### User Management

#### GET `/api/auth/users`
- **Description**: Get all users for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of users
- **Status Codes**: 200 (Success), 401 (Unauthorized)

### Tenant Management

#### GET `/api/auth/tenant`
- **Description**: Get current tenant details
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with tenant details
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### PUT `/api/auth/tenant`
- **Description**: Update current tenant information
- **Headers**: `X-Session-Token` (String)
- **Request Body**: `TenantDTO`
- **Response**: `ApiResponse<?>` with updated tenant details
- **Status Codes**: 200 (Success), 401 (Unauthorized), 400 (Bad Request)

---

## Financial APIs

### Invoice Management

#### POST `/api/finance/invoices`
- **Description**: Create a new invoice
- **Request Body**: `InvoiceReqDTO` (Validated)
- **Response**: `ApiResponse<?>` with invoice details
- **Status Codes**: 201 (Created), 400 (Bad Request), 422 (Validation Error)

#### GET `/api/finance/invoices`
- **Description**: Get all invoices for the current tenant, optionally filtered by status
- **Headers**: `X-Session-Token` (String)
- **Query Parameters**: `status` (String, optional) - Filter by invoice status
- **Response**: `ApiResponse<?>` with list of invoices
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### PUT `/api/finance/invoices/{id}/{status}`
- **Description**: Update invoice status
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: 
  - `id` (Long) - Invoice ID
  - `status` (String) - New status
- **Response**: `ApiResponse<?>` with updated invoice
- **Status Codes**: 200 (Success), 404 (Invoice not found), 400 (Bad Request)

#### GET `/api/finance/invoices/{id}/status`
- **Description**: Get invoice status by ID
- **Path Parameters**: `id` (Long) - Invoice ID
- **Response**: `ApiResponse<?>` with invoice status
- **Status Codes**: 200 (Success), 404 (Invoice not found)

### Payment Management

#### POST `/api/finance/payments`
- **Description**: Create a new payment
- **Request Body**: `PaymentClassReqDTO`
- **Response**: `ApiResponse<?>` with payment details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/finance/payments`
- **Description**: Get all payments
- **Response**: `ApiResponse<?>` with list of payments
- **Status Codes**: 200 (Success)

### Payment Controller (RESTful)

#### POST `/api/payments`
- **Description**: Create a new payment (RESTful endpoint)
- **Request Body**: `PaymentReqDTO`
- **Response**: `ApiResponse<?>` with payment details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/payments/invoice/{invoiceId}`
- **Description**: Get payments for a specific invoice
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceId` (Long)
- **Response**: `ApiResponse<?>` with list of payments
- **Status Codes**: 200 (Success), 404 (Invoice not found)

#### GET `/api/payments/{paymentId}`
- **Description**: Get payment by ID
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `paymentId` (String)
- **Response**: `ApiResponse<?>` with payment details
- **Status Codes**: 200 (Success), 404 (Payment not found)

#### GET `/api/payments`
- **Description**: Get all payments for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of payments
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### GET `/api/payments/invoice/{invoiceId}/summary`
- **Description**: Get payment summary for an invoice
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceId` (Long)
- **Response**: `ApiResponse<?>` with payment summary
- **Status Codes**: 200 (Success), 404 (Invoice not found)

#### PUT `/api/payments/{paymentId}`
- **Description**: Update payment details
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `paymentId` (String)
- **Request Body**: `PaymentReqDTO`
- **Response**: `ApiResponse<?>` with updated payment
- **Status Codes**: 200 (Success), 404 (Payment not found), 400 (Bad Request)

#### DELETE `/api/payments/{paymentId}`
- **Description**: Delete a payment
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `paymentId` (String)
- **Response**: `ApiResponse<?>` with deletion confirmation
- **Status Codes**: 200 (Success), 404 (Payment not found)

### Invoice Items Management

#### POST `/api/invoice-items`
- **Description**: Create a new invoice item
- **Request Body**: `InvoiceItemReqDTO`
- **Response**: `ApiResponse<?>` with invoice item details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/invoice-items/invoice/{invoiceId}`
- **Description**: Get invoice items for a specific invoice
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceId` (Long)
- **Response**: `ApiResponse<?>` with list of invoice items
- **Status Codes**: 200 (Success), 404 (Invoice not found)

#### GET `/api/invoice-items/product/{productId}`
- **Description**: Get invoice items for a specific product
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `productId` (String)
- **Response**: `ApiResponse<?>` with list of invoice items
- **Status Codes**: 200 (Success), 404 (Product not found)

#### GET `/api/invoice-items/{invoiceItemId}`
- **Description**: Get invoice item by ID
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceItemId` (String)
- **Response**: `ApiResponse<?>` with invoice item details
- **Status Codes**: 200 (Success), 404 (Invoice item not found)

#### GET `/api/invoice-items`
- **Description**: Get all invoice items for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of invoice items
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### PUT `/api/invoice-items/{invoiceItemId}`
- **Description**: Update invoice item
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceItemId` (String)
- **Request Body**: `InvoiceItemReqDTO`
- **Response**: `ApiResponse<?>` with updated invoice item
- **Status Codes**: 200 (Success), 404 (Invoice item not found), 400 (Bad Request)

#### DELETE `/api/invoice-items/{invoiceItemId}`
- **Description**: Delete an invoice item
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `invoiceItemId` (String)
- **Response**: `ApiResponse<?>` with deletion confirmation
- **Status Codes**: 200 (Success), 404 (Invoice item not found)

### Customer Management

#### POST `/api/finance/customers`
- **Description**: Create a new customer
- **Request Body**: `CustomerClass`
- **Response**: `ApiResponse<?>` with customer details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/finance/customers`
- **Description**: Get all customers
- **Response**: `ApiResponse<?>` with list of customers
- **Status Codes**: 200 (Success)

---

## Human Resources APIs

Base URL: `/api/hr`

### Employee Management

#### GET `/api/hr/employees`
- **Description**: Get all employees
- **Response**: `ApiResponse<?>` with list of employees
- **Status Codes**: 200 (Success)

#### GET `/api/hr/employees/{id}`
- **Description**: Get employee by ID
- **Path Parameters**: `id` (Long) - Employee ID
- **Response**: `ApiResponse<?>` with employee details
- **Status Codes**: 200 (Success), 404 (Employee not found)

#### POST `/api/hr/employees`
- **Description**: Add a new employee
- **Request Body**: `EmployeeClass`
- **Response**: `ApiResponse<?>` with employee details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### PUT `/api/hr/employees/{id}`
- **Description**: Update employee details
- **Path Parameters**: `id` (Long) - Employee ID
- **Request Body**: `EmployeeClass`
- **Response**: `ApiResponse<?>` with updated employee details
- **Status Codes**: 200 (Success), 404 (Employee not found), 400 (Bad Request)

#### DELETE `/api/hr/employees/{id}`
- **Description**: Delete an employee
- **Path Parameters**: `id` (Long) - Employee ID
- **Response**: `ApiResponse<?>` with deletion confirmation
- **Status Codes**: 200 (Success), 404 (Employee not found)

### Attendance Management

#### POST `/api/hr/attendance`
- **Description**: Mark attendance for an employee
- **Request Body**: `EmployeeAttendanceDTO`
- **Response**: `ApiResponse<?>` with attendance record
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/hr/attendance/employee/{id}`
- **Description**: Get attendance records for a specific employee
- **Path Parameters**: `id` (Long) - Employee ID
- **Response**: `ApiResponse<?>` with attendance records
- **Status Codes**: 200 (Success), 404 (Employee not found)

#### GET `/api/hr/attendance`
- **Description**: Get all attendance records
- **Response**: `ApiResponse<?>` with list of attendance records
- **Status Codes**: 200 (Success)

---

## Inventory & Supply Chain APIs

### Product Management

#### POST `/api/inventory/products`
- **Description**: Create new products (bulk creation)
- **Request Body**: `List<ProductClass>` (Validated)
- **Response**: `ApiResponse<?>` with created products
- **Status Codes**: 201 (Created), 400 (Bad Request), 422 (Validation Error)

#### GET `/api/inventory/products`
- **Description**: Get all products
- **Response**: `ApiResponse<?>` with list of products
- **Status Codes**: 200 (Success)

#### GET `/api/inventory/stock`
- **Description**: Get all product stock levels
- **Response**: `ApiResponse<?>` with stock information
- **Status Codes**: 200 (Success)

### Product Controller (RESTful)

#### POST `/api/products`
- **Description**: Create a new product (RESTful endpoint)
- **Request Body**: `ProductReqDTO`
- **Response**: `ApiResponse<?>` with product details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/products`
- **Description**: Get products for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of products
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### GET `/api/products/category/{categoryId}`
- **Description**: Get products by category for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `categoryId` (String)
- **Response**: `ApiResponse<?>` with list of products
- **Status Codes**: 200 (Success), 404 (Category not found)

#### GET `/api/products/{productId}`
- **Description**: Get product by ID for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `productId` (String)
- **Response**: `ApiResponse<?>` with product details
- **Status Codes**: 200 (Success), 404 (Product not found)

#### PUT `/api/products/{productId}`
- **Description**: Update product details
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `productId` (String)
- **Request Body**: `ProductReqDTO`
- **Response**: `ApiResponse<?>` with updated product
- **Status Codes**: 200 (Success), 404 (Product not found), 400 (Bad Request)

#### DELETE `/api/products/{productId}`
- **Description**: Delete a product
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `productId` (String)
- **Response**: `ApiResponse<?>` with deletion confirmation
- **Status Codes**: 200 (Success), 404 (Product not found)

### Supplier Management

#### POST `/api/inventory/suppliers`
- **Description**: Add a new supplier
- **Request Body**: `SupplierClass`
- **Response**: `ApiResponse<?>` with supplier details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/inventory/suppliers`
- **Description**: Get all suppliers
- **Response**: `ApiResponse<?>` with list of suppliers
- **Status Codes**: 200 (Success)

### Purchase Order Management

#### POST `/api/inventory/purchase-orders`
- **Description**: Create a new purchase order
- **Request Body**: `PurchaseOrderReqDTO`
- **Response**: `ApiResponse<?>` with purchase order details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### PUT `/api/inventory/purchase-orders/status`
- **Description**: Update purchase order status
- **Headers**: `X-Session-Token` (String)
- **Request Body**: `UpdatePurchaseOrderStatusReqDTO`
- **Response**: `ApiResponse<?>` with updated purchase order
- **Status Codes**: 200 (Success), 404 (Purchase order not found), 400 (Bad Request)

#### GET `/api/inventory/purchase-orders`
- **Description**: Get all purchase orders for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of purchase orders
- **Status Codes**: 200 (Success), 401 (Unauthorized)

### Purchase Order Controller (RESTful)

#### POST `/api/purchase-orders`
- **Description**: Create a new purchase order (RESTful endpoint)
- **Request Body**: `PurchaseOrderReqDTO`
- **Response**: `ApiResponse<?>` with purchase order details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/purchase-orders`
- **Description**: Get purchase orders for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of purchase orders
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### GET `/api/purchase-orders/{purchaseOrderId}`
- **Description**: Get purchase order by ID
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `purchaseOrderId` (String)
- **Response**: `ApiResponse<?>` with purchase order details
- **Status Codes**: 200 (Success), 404 (Purchase order not found)

#### PUT `/api/purchase-orders/{purchaseOrderId}/status`
- **Description**: Update purchase order status
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `purchaseOrderId` (String)
- **Query Parameters**: `status` (String) - New status
- **Response**: `ApiResponse<?>` with updated purchase order
- **Status Codes**: 200 (Success), 404 (Purchase order not found), 400 (Bad Request)

#### GET `/api/purchase-orders/supplier/{supplierId}`
- **Description**: Get purchase orders by supplier
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `supplierId` (String)
- **Response**: `ApiResponse<?>` with list of purchase orders
- **Status Codes**: 200 (Success), 404 (Supplier not found)

#### GET `/api/purchase-orders/status/{status}`
- **Description**: Get purchase orders by status
- **Headers**: `X-Session-Token` (String)
- **Path Parameters**: `status` (String)
- **Response**: `ApiResponse<?>` with list of purchase orders
- **Status Codes**: 200 (Success), 404 (No orders found)

### Stock Movement Management

#### GET `/api/inventory/stock-movements`
- **Description**: Get all stock movements for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of stock movements
- **Status Codes**: 200 (Success), 401 (Unauthorized)

### Category Management

#### POST `/api/categories`
- **Description**: Create a new category
- **Request Body**: `CategoryReqDTO`
- **Response**: `ApiResponse<?>` with category details
- **Status Codes**: 201 (Created), 400 (Bad Request)

#### GET `/api/categories`
- **Description**: Get categories for the current tenant
- **Headers**: `X-Session-Token` (String)
- **Response**: `ApiResponse<?>` with list of categories
- **Status Codes**: 200 (Success), 401 (Unauthorized)

#### GET `/api/categories/{categoryId}`
- **Description**: Get category by ID
- **Path Parameters**: `categoryId` (String)
- **Response**: `ApiResponse<?>` with category details
- **Status Codes**: 200 (Success), 404 (Category not found)

#### PUT `/api/categories/{categoryId}`
- **Description**: Update category details
- **Path Parameters**: `categoryId` (String)
- **Request Body**: `CategoryReqDTO`
- **Response**: `ApiResponse<?>` with updated category
- **Status Codes**: 200 (Success), 404 (Category not found), 400 (Bad Request)

#### DELETE `/api/categories/{categoryId}`
- **Description**: Delete a category
- **Path Parameters**: `categoryId` (String)
- **Response**: `ApiResponse<?>` with deletion confirmation
- **Status Codes**: 200 (Success), 404 (Category not found)

---

## Common Response Format

All API endpoints return responses in the following format:

```json
{
  "message": "Success message or error description",
  "code": 200,
  "data": {
    // Response data (varies by endpoint)
  }
}
```

## Status Codes

- **200**: Success
- **201**: Created
- **400**: Bad Request
- **401**: Unauthorized
- **404**: Not Found
- **409**: Conflict
- **422**: Validation Error
- **500**: Internal Server Error

## Authentication

Most endpoints require authentication via session tokens. Include the session token in the `X-Session-Token` header for all requests except login and register.

## Multi-tenancy

All tenant-aware endpoints automatically extract the tenant ID from the session token. No need to pass tenant ID in requests - it's handled automatically by the backend.

## API Base URLs

- **Authentication**: `/api/auth`
- **Finance**: `/api/finance`, `/api/payments`, `/api/invoice-items`
- **Human Resources**: `/api/hr`
- **Inventory**: `/api/inventory`, `/api/products`, `/api/purchase-orders`, `/api/categories`

---

*This documentation covers all REST API endpoints in the Elevate application as of the current version. For the most up-to-date information, refer to the source code and any additional documentation provided by the development team.*