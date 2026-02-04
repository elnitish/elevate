### Finance (FNA) API

Authentication: All endpoints require header `Session-Key`. The interceptor adds `tenantID` to the request.

InvoiceController (base `/finance`)
- POST `/createInvoice`
  - Body: `InvoiceReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getAllInvoices`
  - Query (optional): `status` string (e.g., PAID, UNPAID)
  - Response: `ApiResponse<?>`
- PUT `/invoices/{id}/{status}`
  - Path: `id` Long, `status` String
  - Response: `ApiResponse<?>`
- GET `/getInvoiceById`
  - Query: `id` String
  - Response: `ApiResponse<?>`

PaymentController (base `/payments`)
- POST `/createPayment`
  - Body: `PaymentReqDTO`
  - Response: `ApiResponse<?>`
- GET `/getPaymentByInvoice/{invoiceId}`
  - Path: `invoiceId` Long
  - Response: `ApiResponse<?>`
- GET `/getPaymentById/{paymentId}`
  - Path: `paymentId` String
  - Response: `ApiResponse<?>`
- GET `/getAllPayments`
  - Response: `ApiResponse<?>`
- DELETE `/deletePayment/{paymentId}`
  - Path: `paymentId` String
  - Response: `ApiResponse<?>`

DTOs (selected)
- InvoiceReqDTO
  - `name` string (1..255, required)
  - `email` string
  - `phone` string (1..20, required)
  - `date` string (yyyy-MM-dd, required)
  - `status` enum `InvoiceClass.Status` (required)
  - `items` `List<InvoiceItemReqDTO>` (required)
- InvoiceItemReqDTO
  - `invoiceId` Long (required)
  - `productId` UUID string (length 36, required)
  - `quantity` integer (>=1)
  - `unitPrice` decimal (>0)
- PaymentReqDTO
  - `invoiceId` string (required)
  - `amount` decimal (>0)
  - `method` string (required)
  - `transactionRef` string (<=100)

Response Wrapper
- `ApiResponse<T>` with `message`, `code`, `data`.

