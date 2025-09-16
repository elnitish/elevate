Elevate Backend GET API Endpoints Documentation

---

## 1️⃣ /auth/getUser  
- **Service:** Auth  
- **Response Type:** `ApiResponse<UserClassReqDTO>`  
- **Data Fields:**  
  - `username` (String)  
  - `password` (String)  
  - `role` (String)  
- **Notes:** Single user object inside `data`.

---

## 2️⃣ /fna/getAllInvoices  
- **Service:** Finance & Accounting  
- **Response Type:** `ApiResponse<List<InvoiceClass>>`  
- **InvoiceClass Fields:**  
  - `invoiceId` (Long)  
  - `name` (String)  
  - `email` (String)  
  - `totalAmount` (BigDecimal)  
  - `remainingAmount` (BigDecimal)  
  - `status` (Enum: PENDING, PAID, CANCELLED)  
  - `date` (Date)  
  - `items` (List<InvoiceItemsClass>)  
- **InvoiceItemsClass Fields:**  
  - `id` (Long)  
  - `product` (ProductClass)  
  - `quantity` (Integer)  
- **Notes:** Returns a list of invoices, each containing a list of items.

---

## 3️⃣ /fna/getInvoiceStatus/{id}  
- **Service:** Finance & Accounting  
- **Response Type:** `ApiResponse<InvoiceClass.Status>`  
- **Notes:** Returns the status of a single invoice (Enum: PENDING, PAID, CANCELLED).

---

## 4️⃣ /fna/getAllPayments  
- **Service:** Finance & Accounting  
- **Response Type:** `ApiResponse<List<PaymentClass>>`  
- **PaymentClass Fields:**  
  - `id` (Long)  
  - `invoiceID` (Long)  
  - `totalAmount` (BigDecimal)  
  - `paymentDate` (LocalDateTime)  
  - `method` (Enum: CASH, CARD, BANK_TRANSFER, UPI)  
- **Notes:** Returns a list of payments.

---

## 5️⃣ /insc/getAllProducts  
- **Service:** Inventory & Supply Chain  
- **Response Type:** `ApiResponse<List<ProductClass>>`  
- **ProductClass Fields:**  
  - `id` (Long)  
  - `name` (String)  
  - `description` (String)  
  - `price` (BigDecimal)  
- **Notes:** Returns a list of products.

---

## 6️⃣ /insc/getAllProductStock  
- **Service:** Inventory & Supply Chain  
- **Response Type:** `ApiResponse<List<StockLevelClass>>`  
- **StockLevelClass Fields:**  
  - `id` (Long)  
  - `product` (ProductClass)  
  - `quantity` (Integer)  
- **Notes:** Returns stock levels of all products.

---

## 7️⃣ /insc/getAllSupplier  
- **Service:** Inventory & Supply Chain  
- **Response Type:** `ApiResponse<List<SupplierClass>>`  
- **SupplierClass Fields:**  
  - `id` (Long)  
  - `name` (String)  
  - `contactInfo` (String)  
  - `email` (String)  
  - `address` (String)  
- **Notes:** Returns a list of suppliers.

---

## 8️⃣ /insc/getAllPurchaseOrder  
- **Service:** Inventory & Supply Chain  
- **Response Type:** `ApiResponse<List<PurchaseOrderClass>>`  
- **PurchaseOrderClass Fields:**  
  - `id` (Long)  
  - `supplierId` (Long)  
  - `orderDate` (LocalDate)  
  - `status` (Enum: PENDING, RECEIVED, CANCELLED)  
  - `items` (List<PurchaseOrderItemClass>)  
- **PurchaseOrderItemClass Fields:**  
  - `id` (Long)  
  - `productId` (Long)  
  - `quantity` (Integer)  
  - `unitPrice` (Double)  
- **Notes:** Returns a list of purchase orders, each containing a list of items.

---

### **Summary Table**
| Endpoint | Data Type / Class | Single/List |
|----------|-----------------|------------|
| /auth/getUser | UserClassReqDTO | Single |
| /fna/getAllInvoices | InvoiceClass | List |
| /fna/getInvoiceStatus/{id} | InvoiceClass.Status | Single |
| /fna/getAllPayments | PaymentClass | List |
| /insc/getAllProducts | ProductClass | List |
| /insc/getAllProductStock | StockLevelClass | List |
| /insc/getAllSupplier | SupplierClass | List |
| /insc/getAllPurchaseOrder | PurchaseOrderClass | List |

