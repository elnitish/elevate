# 📦 Stock Tracking Feature - Complete Implementation

## Overview
The Stock Tracking feature provides comprehensive inventory management capabilities including:
- **Stock Levels** - Real-time inventory quantities for all products
- **Stock Movements** - Complete audit trail of all stock IN/OUT transactions
- **Low Stock Alerts** - Automatic alerts for products below threshold

---

## 🎯 Features Implemented

### 1. **Stock Levels Management**
Track current inventory quantities for all products with automatic updates.

### 2. **Stock Movements Tracking**
Complete history of all stock transactions:
- **IN movements**: Purchase orders, stock receipts
- **OUT movements**: Sales invoices, stock issues
- Automatic recording when invoices/POs are created

### 3. **Low Stock Alerts**
Intelligent alerting system with three severity levels:
- **OUT_OF_STOCK**: Quantity = 0
- **CRITICAL**: Quantity < threshold/2
- **LOW**: Quantity < threshold

---

## 📡 API Endpoints

### **Stock Levels**

#### 1. Get All Stock Levels
```http
GET /stock/levels
```
**Description**: Retrieve current stock levels for all products

**Response**:
```json
{
  "message": "Stock levels retrieved successfully",
  "code": 200,
  "data": [
    {
      "id": "uuid",
      "productId": "product-uuid",
      "productName": "Product Name",
      "quantity": 150,
      "updatedAt": "2026-02-05T21:00:00",
      "isLowStock": false,
      "lowStockThreshold": 10
    }
  ]
}
```

---

#### 2. Get Stock Level by Product
```http
GET /stock/levels/product/{productId}
```
**Description**: Get current stock level for a specific product

**Path Parameters**:
- `productId` (string, required): Product UUID

**Response**: Same as above (single object)

---

#### 3. Adjust Stock Level
```http
PUT /stock/levels/adjust
```
**Description**: Manually adjust stock level (for corrections, damages, etc.)

**Request Body**:
```json
{
  "productId": "product-uuid",
  "quantity": 100,
  "reference": "Stock correction",
  "reason": "Damaged goods removed"
}
```

**Response**:
```json
{
  "message": "Stock level adjusted successfully",
  "code": 200,
  "data": null
}
```

---

### **Low Stock Alerts**

#### 4. Get Low Stock Alerts
```http
GET /stock/alerts/low-stock?threshold=10
```
**Description**: Get products with stock below threshold

**Query Parameters**:
- `threshold` (integer, optional, default: 10): Stock threshold level

**Response**:
```json
{
  "message": "Low stock alerts retrieved successfully",
  "code": 200,
  "data": [
    {
      "productId": "product-uuid",
      "productName": "Product Name",
      "currentStock": 5,
      "threshold": 10,
      "shortfall": 5,
      "alertLevel": "LOW"
    }
  ]
}
```

**Alert Levels**:
- `OUT_OF_STOCK`: quantity = 0
- `CRITICAL`: quantity < threshold/2
- `LOW`: quantity < threshold

---

### **Stock Movements**

#### 5. Get All Stock Movements
```http
GET /stock/movements
```
**Description**: Retrieve all stock movements (IN/OUT)

**Response**:
```json
{
  "message": "Stock movements retrieved successfully",
  "code": 200,
  "data": [
    {
      "id": "movement-uuid",
      "productId": "product-uuid",
      "productName": "Product Name",
      "purchaseOrderId": "po-uuid",
      "invoiceId": null,
      "type": "IN",
      "quantity": 50,
      "date": "2026-02-05T10:30:00",
      "reference": "Purchase Order: PO-001"
    }
  ]
}
```

---

#### 6. Get Stock Movements by Product
```http
GET /stock/movements/product/{productId}
```
**Description**: Get all movements for a specific product

**Path Parameters**:
- `productId` (string, required): Product UUID

---

#### 7. Get Stock Movements by Type
```http
GET /stock/movements/type/{type}
```
**Description**: Filter movements by type (IN or OUT)

**Path Parameters**:
- `type` (string, required): Movement type - `IN` or `OUT`

**Example**: `/stock/movements/type/IN`

---

#### 8. Get Stock Movements by Date Range
```http
GET /stock/movements/date-range?startDate=2026-02-01T00:00:00&endDate=2026-02-05T23:59:59
```
**Description**: Get movements within a date range

**Query Parameters**:
- `startDate` (ISO DateTime, required): Start date
- `endDate` (ISO DateTime, required): End date

---

#### 9. Get Stock Movements by Purchase Order
```http
GET /stock/movements/purchase-order/{purchaseOrderId}
```
**Description**: Get all movements related to a purchase order

**Path Parameters**:
- `purchaseOrderId` (string, required): Purchase Order UUID

---

#### 10. Get Stock Movements by Invoice
```http
GET /stock/movements/invoice/{invoiceId}
```
**Description**: Get all movements related to an invoice

**Path Parameters**:
- `invoiceId` (string, required): Invoice ID

---

## 🔄 Automatic Stock Updates

### **When Purchase Order is Created/Received**
Stock movements are automatically recorded:
- **Type**: IN
- **Quantity**: From PO items
- **Reference**: "Purchase Order: {PO-ID}"

### **When Invoice is Created**
Stock movements are automatically recorded:
- **Type**: OUT
- **Quantity**: From invoice items
- **Reference**: "Invoice: {Invoice-ID}"

---

## 📊 Database Schema

### **stock_levels Table**
```sql
CREATE TABLE stock_levels (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE KEY (tenant_id, product_id)
);
```

### **stock_movements Table**
```sql
CREATE TABLE stock_movements (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    purchase_order_id VARCHAR(36),
    invoice_id VARCHAR(255),
    type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    date TIMESTAMP NOT NULL,
    reference VARCHAR(255),
    INDEX idx_tenant_product (tenant_id, product_id),
    INDEX idx_date (date)
);
```

---

## 🧪 Testing Scenarios

### **Scenario 1: Check Initial Stock Levels**
```bash
GET /stock/levels
```
Expected: Empty list or products with 0 quantity

---

### **Scenario 2: Create Purchase Order (Stock IN)**
1. Create a purchase order with products
2. Check stock movements:
   ```bash
   GET /stock/movements/type/IN
   ```
3. Verify stock levels increased:
   ```bash
   GET /stock/levels
   ```

---

### **Scenario 3: Create Invoice (Stock OUT)**
1. Create an invoice with products
2. Check stock movements:
   ```bash
   GET /stock/movements/type/OUT
   ```
3. Verify stock levels decreased:
   ```bash
   GET /stock/levels
   ```

---

### **Scenario 4: Low Stock Alerts**
1. Adjust stock to low level:
   ```bash
   PUT /stock/levels/adjust
   {
     "productId": "product-uuid",
     "quantity": 5,
     "reason": "Testing low stock"
   }
   ```
2. Check alerts:
   ```bash
   GET /stock/alerts/low-stock?threshold=10
   ```
3. Expected: Product appears in alerts with "LOW" level

---

### **Scenario 5: Out of Stock**
1. Adjust stock to 0:
   ```bash
   PUT /stock/levels/adjust
   {
     "productId": "product-uuid",
     "quantity": 0,
     "reason": "Testing out of stock"
   }
   ```
2. Check alerts:
   ```bash
   GET /stock/alerts/low-stock?threshold=10
   ```
3. Expected: Product appears with "OUT_OF_STOCK" level

---

### **Scenario 6: Stock Movement History**
1. View all movements for a product:
   ```bash
   GET /stock/movements/product/{productId}
   ```
2. Expected: Complete history of IN/OUT transactions

---

### **Scenario 7: Date Range Filtering**
```bash
GET /stock/movements/date-range?startDate=2026-02-01T00:00:00&endDate=2026-02-05T23:59:59
```
Expected: Movements within the specified date range

---

## 🎨 Use Cases

### **1. Inventory Manager Dashboard**
- View all stock levels
- Monitor low stock alerts
- Track stock movements

### **2. Purchase Manager**
- Check stock levels before ordering
- View purchase order stock receipts
- Track supplier deliveries

### **3. Sales Manager**
- Check product availability
- View sales impact on stock
- Monitor fast-moving items

### **4. Warehouse Manager**
- Perform stock adjustments
- Track stock movements
- Manage stock corrections

---

## 🔐 Security

All endpoints require:
- Valid session token (via `Session-Key` header)
- Tenant isolation (automatic via `tenantId` from session)

---

## 📈 Business Benefits

1. **Real-time Visibility**: Always know current stock levels
2. **Audit Trail**: Complete history of all stock transactions
3. **Proactive Alerts**: Never run out of stock unexpectedly
4. **Accuracy**: Automatic updates reduce manual errors
5. **Compliance**: Full traceability for audits

---

## 🚀 Quick Start

### **1. Check Stock Levels**
```bash
curl -X GET http://localhost:8080/stock/levels \
  -H "Session-Key: your-session-token"
```

### **2. Get Low Stock Alerts**
```bash
curl -X GET "http://localhost:8080/stock/alerts/low-stock?threshold=10" \
  -H "Session-Key: your-session-token"
```

### **3. View Stock Movements**
```bash
curl -X GET http://localhost:8080/stock/movements \
  -H "Session-Key: your-session-token"
```

### **4. Adjust Stock Level**
```bash
curl -X PUT http://localhost:8080/stock/levels/adjust \
  -H "Session-Key: your-session-token" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product-uuid",
    "quantity": 100,
    "reason": "Stock correction"
  }'
```

---

## ✅ Implementation Checklist

- ✅ Stock level tracking
- ✅ Stock movement recording
- ✅ Low stock alerts with severity levels
- ✅ Automatic stock updates on PO/Invoice
- ✅ Manual stock adjustments
- ✅ Filter by product, type, date range
- ✅ Complete audit trail
- ✅ OpenAPI documentation
- ✅ Multi-tenant support
- ✅ Validation and error handling

---

## 🎯 Next Steps

1. **Test all endpoints** using Swagger UI
2. **Create sample data** (products, POs, invoices)
3. **Verify automatic stock updates** work correctly
4. **Test low stock alerts** with different thresholds
5. **Review stock movement history** for accuracy

---

## 📝 Notes

- Stock levels are automatically created when products are created
- Stock movements are automatically recorded for POs and invoices
- Low stock threshold can be customized per query
- All operations are tenant-isolated for security
- Stock adjustments create an audit trail

---

**Stock Tracking Feature is now COMPLETE and ready for testing! 🎉**
