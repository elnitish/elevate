### Inventory & Supply Chain (INSC) API

Authentication: All endpoints require header `Session-Key` (validated by interceptor). `tenantID` is resolved from the session and attached to the request.

Controllers

ProductController (base `/insc`)
- POST `/createProduct`
  - Body: `ProductReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getAllProducts`
  - Response: `ApiResponse<?>` (products for tenant)
- GET `/getProductById/{productId}`
  - Path: `productId` string (UUID)
  - Response: `ApiResponse<?>`
- PUT `/updateProduct`
  - Body: `UpdateProductReqDTO`
  - Response: `ApiResponse<?>`
- DELETE `/deleteProductById/{productId}`
  - Path: `productId`
  - Response: `ApiResponse<?>`

CategoryController (base `/category`)
- POST `/createCategory`
  - Body: `CategoryReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getAllCategory`
  - Response: `ApiResponse<?>`
- PUT `/updateCategory`
  - Body: `UpdateCategoryReqDTO`
  - Response: `ApiResponse<?>`
- DELETE `/deleteCategory/{categoryId}`
  - Path: `categoryId`
  - Response: `ApiResponse<?>`

PurchaseOrderController (base `/purchaseOrder`)
- POST `/createNewPurchaseOrder`
  - Body: `PurchaseOrderReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getAllPurchaseOrder`
  - Response: `ApiResponse<?>`
- GET `/getPurchaseOrderById/{purchaseOrderId}`
  - Path: `purchaseOrderId` string
  - Response: `ApiResponse<?>`
- PUT `/updatePurchaseOrderStatus/{purchaseOrderId}/{status}`
  - Path: `purchaseOrderId`, `status`
  - Response: `ApiResponse<?>`
- POST `/createNewSupplier`
  - Body: `SupplierClassReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getPurchaseOrderBySupplier/{supplierId}`
  - Path: `supplierId`
  - Response: `ApiResponse<?>`
- GET `/getPurchaseOrderByStatus/{status}`
  - Path: `status`
  - Response: `ApiResponse<?>`

DTOs (selected)
- ProductReqDTO: product fields for creation.
- UpdateProductReqDTO: product update fields.
- CategoryReqDTO / UpdateCategoryReqDTO: category create/update.
- PurchaseOrderReqDTO: purchase order creation payload.
- SupplierClassReqDTO: supplier creation payload.

Response Wrapper
- `ApiResponse<T>` with `message`, `code`, `data`.

