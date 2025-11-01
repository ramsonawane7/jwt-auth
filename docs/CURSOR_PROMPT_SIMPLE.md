# Simple Cursor Prompt - Copy & Paste This

---

**I need to complete JWT authentication setup for all APIs in my Spring Boot service. I already have these files:**

- PublicKeyResponse.java
- PublicKeyLoaderService.java  
- JwtValidationService.java
- JwtAuthenticationFilter.java

**Please:**

1. **Create or update SecurityConfig.java** to:
   - Enable Spring Security
   - Register JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
   - Set STATELESS session management
   - Allow public access to: /actuator/**, /health, /public/**, /swagger/**, /api-docs/**
   - Require authentication for ALL other endpoints

2. **Verify application.properties** has:
   - `auth.service.url=http://localhost:9090`
   - `auth.service.public-key-endpoint=/api/auth/public-key`

3. **Check pom.xml** has JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson version 0.11.5)

4. **Verify all files have correct annotations** (@Service, @Component, @Configuration)

5. **Ensure JwtAuthenticationFilter** properly validates tokens and rejects requests without valid "Authorization: Bearer <token>" header

**Goal: All APIs must require JWT token authentication except the excluded public endpoints. The public key should load automatically on startup from the auth service.**

---


