### Auth Module API

Endpoints are prefixed with `/auth`. Unless excluded by the interceptor, requests must include header `Session-Key: <uuid>`.

Excluded (no session required): `/auth/tenantRegister`, `/auth/createUser`, `/auth/userLogin`.

- POST `/tenantRegister`
  - Body: `TenantReqDTO`
  - Response: `ApiResponse<?>`
- POST `/createUser`
  - Body: `UserClassReqDTO`
  - Response: `ApiResponse<?>`
- POST `/userLogin`
  - Body: `UserClassReqDTO`
  - Response: `ApiResponse<?>` (contains session info)
- GET `/allUsers`
  - Auth: requires `Session-Key`. Uses `tenantID` from request attributes
  - Response: `ApiResponse<?>`
- POST `/userLogout`
  - Auth: requires `Session-Key`
  - Response: `ApiResponse<?>`

DTOs
- TenantReqDTO
  - `name` string (2..255, required)
  - `email` string (email)
  - `planType` string (defaults to "FREE")
- UserClassReqDTO
  - `tenantId` string (UUID length 36, required)
  - `username` string (3..100, required)
  - `email` string (email)
  - `password` string (min 6, required)
  - `role` string (defaults to "USER")

Common Response Wrapper
- ApiResponse<T>
  - `message` string
  - `code` number (HTTP status code)
  - `data` T

Security
- `SessionAuthentication` interceptor validates `Session-Key` using `SessionService` and sets request attributes: `sessionKey`, `tenantID`, `role`.
- Global CORS allows `http://localhost:3000` with GET/POST/PUT/DELETE.

