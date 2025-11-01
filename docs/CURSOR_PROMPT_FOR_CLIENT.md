# Cursor AI Prompt: Complete JWT Authentication Setup

## Copy this prompt and paste it into Cursor:

---

**I have a Spring Boot client service that needs JWT authentication. I've already added 3 files:**

1. `PublicKeyResponse.java` (DTO)
2. `PublicKeyLoaderService.java` (Service to load public key)
3. `JwtValidationService.java` (Service to validate tokens)
4. `JwtAuthenticationFilter.java` (Filter for requests)

**Please help me complete the setup:**

1. **Verify SecurityConfig.java exists and configure it:**
   - Add Spring Security configuration
   - Integrate JwtAuthenticationFilter
   - Set up STATELESS session management
   - Configure public endpoints (exclude /actuator, /health, /public)
   - All other endpoints should require authentication

2. **Check and update application.properties:**
   - Add `auth.service.url=http://localhost:9090`
   - Add `auth.service.public-key-endpoint=/api/auth/public-key`

3. **Verify pom.xml has these dependencies:**
   - `jjwt-api` (0.11.5)
   - `jjwt-impl` (0.11.5)
   - `jjwt-jackson` (0.11.5)
   - `spring-boot-starter-security`
   - `spring-boot-starter-web`

4. **Update JwtAuthenticationFilter if needed:**
   - Make sure it properly handles missing/invalid tokens
   - Ensure public endpoints are correctly excluded
   - Add proper error responses with JSON format

5. **Add @Component or @Service annotations** if any are missing

6. **Verify the filter order** - JwtAuthenticationFilter should run before Spring Security's UsernamePasswordAuthenticationFilter

7. **Test the setup:**
   - Ensure public key loads on startup
   - Verify protected endpoints require Authorization header
   - Check that public endpoints work without authentication

**Make sure ALL APIs (except public ones) require JWT token authentication.**

---

## Alternative Shorter Prompt:

---

**I have a Spring Boot service with JwtAuthenticationFilter, PublicKeyLoaderService, and JwtValidationService already added. Please:**

1. Create/update SecurityConfig to integrate JWT authentication
2. Configure all APIs to require authentication except: /actuator/**, /health, /public/**, /swagger/**, /api-docs/**
3. Verify application.properties has auth service URL configured
4. Check pom.xml has JWT dependencies
5. Ensure JwtAuthenticationFilter is properly registered and works for all protected endpoints
6. Make sure the public key loads on startup and is used for token validation

**Goal: All APIs should require "Authorization: Bearer <token>" header except the excluded public endpoints.**

---


