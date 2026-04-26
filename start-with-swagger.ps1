# Quick Start Script for Elevate with OpenAPI Documentation

Write-Host "🚀 Starting Elevate Application with OpenAPI Documentation..." -ForegroundColor Green
Write-Host ""

# Start the Spring Boot application
Write-Host "📦 Starting Spring Boot application..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot'; mvn spring-boot:run"

# Wait for application to start
Write-Host "⏳ Waiting for application to start (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Open Swagger UI in default browser
Write-Host "🌐 Opening Swagger UI in your browser..." -ForegroundColor Green
Start-Process "http://localhost:8080/swagger-ui.html"

Write-Host ""
Write-Host "✅ Done! Your API documentation should now be open in your browser." -ForegroundColor Green
Write-Host ""
Write-Host "📚 Available URLs:" -ForegroundColor Cyan
Write-Host "   - Swagger UI:    http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "   - OpenAPI JSON:  http://localhost:8080/api-docs" -ForegroundColor White
Write-Host "   - OpenAPI YAML:  http://localhost:8080/api-docs.yaml" -ForegroundColor White
Write-Host ""
Write-Host "🧪 Test Credentials:" -ForegroundColor Cyan
Write-Host "   Tenant ID: test-tenant-001" -ForegroundColor White
Write-Host "   Username:  admin" -ForegroundColor White
Write-Host "   Password:  admin@123" -ForegroundColor White
Write-Host ""
