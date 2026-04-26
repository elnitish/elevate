package com.elevate.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.elevate.auth.entity.AuthCredentials;
import com.elevate.auth.entity.TenantClass;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.repository.AuthCredentialsRepository;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.auth.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final AuthCredentialsRepository authCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(TenantRepository tenantRepository, UserRepository userRepository,
                          AuthCredentialsRepository authCredentialsRepository, PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.authCredentialsRepository = authCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default test tenant if it doesn't exist
        String tenantId = "test-tenant-001";
        
        if (tenantRepository.findById(tenantId).isEmpty()) {
            TenantClass tenant = new TenantClass();
            tenant.setId(tenantId);
            tenant.setName("Test Organization");
            tenant.setEmail("test@organization.com");
            tenant.setPlanType(TenantClass.PlanType.FREE);
            
            tenantRepository.save(tenant);
            System.out.println("✅ Default tenant created: " + tenant.getName());
        } else {
            System.out.println("ℹ️ Default tenant already exists");
        }

        // Create default admin user if it doesn't exist
        String userId = "test-admin-user";
        if (userRepository.findById(userId).isEmpty()) {
            UserClass user = new UserClass();
            user.setId(userId);
            user.setTenantId(tenantId);
            user.setUsername("admin");
            user.setEmail("admin@test.com");
            user.setRole(UserClass.UserRole.ADMIN);
            
            userRepository.save(user);
            System.out.println("✅ Default admin user created: admin");
        } else {
            System.out.println("ℹ️ Default admin user already exists");
        }

        // Create default auth credentials if they don't exist
        if (authCredentialsRepository.findByTenantIdAndUsername(tenantId, "admin").isEmpty()) {
            AuthCredentials authCredentials = new AuthCredentials();
            authCredentials.setTenantId(tenantId);
            authCredentials.setUsername("admin");
            // Password: admin@123
            authCredentials.setPasswordHash(passwordEncoder.encode("admin@123"));
            
            authCredentialsRepository.save(authCredentials);
            System.out.println("✅ Default credentials created (username: admin, password: admin@123)");
        } else {
            System.out.println("ℹ️ Default credentials already exist");
        }

        // Create default test user if it doesn't exist
        String testUserId = "test-user-001";
        if (userRepository.findById(testUserId).isEmpty()) {
            UserClass testUser = new UserClass();
            testUser.setId(testUserId);
            testUser.setTenantId(tenantId);
            testUser.setUsername("testuser");
            testUser.setEmail("testuser@test.com");
            testUser.setRole(UserClass.UserRole.USER);
            
            userRepository.save(testUser);
            System.out.println("✅ Default test user created: testuser");
        } else {
            System.out.println("ℹ️ Default test user already exists");
        }

        // Create test user credentials if they don't exist
        if (authCredentialsRepository.findByTenantIdAndUsername(tenantId, "testuser").isEmpty()) {
            AuthCredentials testAuthCredentials = new AuthCredentials();
            testAuthCredentials.setTenantId(tenantId);
            testAuthCredentials.setUsername("testuser");
            // Password: test@123
            testAuthCredentials.setPasswordHash(passwordEncoder.encode("test@123"));
            
            authCredentialsRepository.save(testAuthCredentials);
            System.out.println("✅ Test user credentials created (username: testuser, password: test@123)");
        } else {
            System.out.println("ℹ️ Test user credentials already exist");
        }

        System.out.println("\n--- Default Test Data Ready ---");
        System.out.println("Tenant ID: " + tenantId);
        System.out.println("Admin - Username: admin, Password: admin@123");
        System.out.println("User  - Username: testuser, Password: test@123");
        System.out.println("-------------------------------\n");
    }
}
