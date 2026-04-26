# OpenAPI Documentation Guide for Elevate

## 🎉 OpenAPI Documentation Successfully Configured!

Your Elevate backend now has full OpenAPI 3.0 documentation with Swagger UI.

---

## 📍 Access Points

Once your application is running, you can access the documentation at:

### **Swagger UI (Interactive Documentation)**
```
http://localhost:8080/swagger-ui.html
```
- Interactive API explorer
- Try out API endpoints directly from the browser
- View request/response schemas
- Test authentication

### **OpenAPI JSON Specification**
```
http://localhost:8080/api-docs
```
- Raw OpenAPI 3.0 JSON specification
- Can be imported into Postman, Insomnia, etc.

### **OpenAPI YAML Specification**
```
http://localhost:8080/api-docs.yaml
```
- YAML format of the OpenAPI specification

---

## 🚀 How to Start

1. **Start your Spring Boot application:**
   ```bash
   mvn spring-boot:run
   ```
   Or run the `ElevateApplication` main class from your IDE.

2. **Open your browser and navigate to:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **You'll see:**
   - All your REST endpoints organized by controller
   - Request/response schemas
   - Try-it-out functionality for each endpoint
   - Model definitions

---

## 📦 What Was Added

### 1. **Maven Dependency** (pom.xml)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2. **OpenAPI Configuration** (OpenApiConfiguration.java)
- Custom API metadata (title, version, description)
- Server configurations (local and production)
- Contact information
- License details

### 3. **Application Properties** (application.properties)
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
```

---

## 🎨 Enhancing Documentation (Optional)

You can add more detailed annotations to your controllers and DTOs for better documentation:

### **Controller-Level Annotations**

```java
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and authorization APIs")
public class ApiAuth {
    // ... your code
}
```

### **Method-Level Annotations**

```java
@PostMapping("/userLogin")
@Operation(
    summary = "User Login",
    description = "Authenticate user and receive session token",
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        )
    }
)
public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody UserClassReqDTO userClassReqDTO) {
    // ... your code
}
```

### **DTO/Model Annotations**

```java
@Schema(description = "User login request")
public class UserClassReqDTO {
    
    @Schema(description = "Tenant ID", example = "test-tenant-001", required = true)
    private String tenantId;
    
    @Schema(description = "Username", example = "admin", required = true)
    private String username;
    
    @Schema(description = "Password", example = "admin@123", required = true)
    private String password;
}
```

### **Parameter Annotations**

```java
@GetMapping("/expenses/{id}")
public ResponseEntity<ApiResponse<?>> getExpenseById(
    @Parameter(description = "Expense ID", required = true, example = "1")
    @PathVariable Long expenseId
) {
    // ... your code
}
```

---

## 🔧 Configuration Options

### Current Configuration Explained:

| Property | Value | Description |
|----------|-------|-------------|
| `springdoc.api-docs.path` | `/api-docs` | Path to access OpenAPI JSON |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Path to access Swagger UI |
| `springdoc.swagger-ui.operationsSorter` | `method` | Sort endpoints by HTTP method |
| `springdoc.swagger-ui.tagsSorter` | `alpha` | Sort tags alphabetically |
| `springdoc.swagger-ui.tryItOutEnabled` | `true` | Enable "Try it out" by default |

### Additional Options You Can Add:

```properties
# Group APIs by package
springdoc.packages-to-scan=com.elevate

# Custom API groups
springdoc.group-configs[0].group=auth
springdoc.group-configs[0].paths-to-match=/auth/**

springdoc.group-configs[1].group=finance
springdoc.group-configs[1].paths-to-match=/finance/**

# Disable default responses
springdoc.default-produces-media-type=application/json
springdoc.default-consumes-media-type=application/json
```

---

## 📤 Exporting Documentation

### **1. Export as JSON**
```bash
curl http://localhost:8080/api-docs > elevate-api.json
```

### **2. Export as YAML**
```bash
curl http://localhost:8080/api-docs.yaml > elevate-api.yaml
```

### **3. Import to Postman**
1. Open Postman
2. Click "Import"
3. Select "Link" tab
4. Enter: `http://localhost:8080/api-docs`
5. Click "Continue" → "Import"

### **4. Generate Client SDKs**
Use the OpenAPI Generator to create client libraries:
```bash
# Install OpenAPI Generator
npm install @openapitools/openapi-generator-cli -g

# Generate JavaScript client
openapi-generator-cli generate -i http://localhost:8080/api-docs -g javascript -o ./client/javascript

# Generate Python client
openapi-generator-cli generate -i http://localhost:8080/api-docs -g python -o ./client/python
```

---

## 🔐 Security Configuration

Currently, your API has security disabled for development. When you enable security, you can add security schemes:

```java
@Bean
public OpenAPI elevateOpenAPI() {
    return new OpenAPI()
        .info(info)
        .servers(servers)
        .addSecurityItem(new SecurityRequirement().addList("Session-Key"))
        .components(new Components()
            .addSecuritySchemes("Session-Key", new SecurityScheme()
                .name("Session-Key")
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
            )
        );
}
```

---

## 🎯 Best Practices

1. **Add descriptions to all endpoints** - Use `@Operation` annotation
2. **Document all parameters** - Use `@Parameter` annotation
3. **Define response codes** - Use `@ApiResponse` annotations
4. **Add examples** - Use `@Schema(example = "...")` in DTOs
5. **Group related endpoints** - Use `@Tag` annotation
6. **Document error responses** - Include 4xx and 5xx responses
7. **Keep it updated** - Update docs when you change APIs

---

## 📚 Additional Resources

- **SpringDoc Documentation**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/
- **Swagger UI**: https://swagger.io/tools/swagger-ui/
- **OpenAPI Generator**: https://openapi-generator.tech/

---

## 🐛 Troubleshooting

### **Swagger UI not loading?**
- Check if the application is running on port 8080
- Verify the path: `http://localhost:8080/swagger-ui.html`
- Check console for errors

### **Endpoints not showing?**
- Ensure controllers have `@RestController` annotation
- Check if packages are being scanned
- Verify `@RequestMapping` paths

### **Want to customize the UI?**
Add to `application.properties`:
```properties
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.filter=true
```

---

## ✅ Quick Test

1. Start the application
2. Go to: http://localhost:8080/swagger-ui.html
3. Find "Authentication" section
4. Click on `POST /auth/userLogin`
5. Click "Try it out"
6. Enter test credentials:
   ```json
   {
     "tenantId": "test-tenant-001",
     "username": "admin",
     "password": "admin@123"
   }
   ```
7. Click "Execute"
8. You should see a 200 response with a session token!

---

**Your OpenAPI documentation is now ready to use! 🎉**
