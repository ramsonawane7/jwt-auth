# Complete JWT Authentication Setup Checklist

## ✅ Step-by-Step Verification

### 1. Dependencies Check (pom.xml)

```xml
<!-- Verify these exist -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. Application Properties (application.properties)

```properties
# Add these
auth.service.url=http://localhost:9090
auth.service.public-key-endpoint=/api/auth/public-key
```

### 3. SecurityConfig.java - MUST HAVE

```java
package com.yourservice.config;

import com.yourservice.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/actuator/**",
                                "/health",
                                "/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // ALL OTHER endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

### 4. File Structure Verification

```
src/main/java/com/yourservice/
├── config/
│   └── SecurityConfig.java ✅
├── dto/
│   └── PublicKeyResponse.java ✅
├── filter/
│   └── JwtAuthenticationFilter.java ✅
└── service/
    ├── PublicKeyLoaderService.java ✅
    └── JwtValidationService.java ✅
```

### 5. JwtAuthenticationFilter Verification

Ensure it has:
- ✅ `@Component` annotation
- ✅ Extends `OncePerRequestFilter`
- ✅ Autowires `JwtValidationService`
- ✅ Checks for `Authorization: Bearer <token>` header
- ✅ Sets authentication in SecurityContextHolder
- ✅ Handles invalid tokens with proper error response

### 6. PublicKeyLoaderService Verification

Ensure it has:
- ✅ `@Service` annotation
- ✅ `@PostConstruct` method to load key on startup
- ✅ `@Value` annotations for configuration
- ✅ Proper error handling if auth service is unavailable

### 7. JwtValidationService Verification

Ensure it has:
- ✅ `@Service` annotation
- ✅ Initializes JwtParser with public key
- ✅ Has `validateToken()` method
- ✅ Has `getUsernameFromToken()` method

---

## 🧪 Testing Checklist

### Test 1: Service Startup
- [ ] Service starts without errors
- [ ] Public key loads successfully (check logs)
- [ ] No exceptions about missing public key

### Test 2: Public Endpoints
```bash
# Should work without token
curl http://localhost:YOUR_PORT/health
curl http://localhost:YOUR_PORT/actuator/info
```

### Test 3: Protected Endpoints Without Token
```bash
# Should return 401 Unauthorized
curl http://localhost:YOUR_PORT/api/your-endpoint
```

### Test 4: Protected Endpoints With Valid Token
```bash
# Get token from auth service
TOKEN=$(curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"12345"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# Use token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:YOUR_PORT/api/your-endpoint
```

### Test 5: Protected Endpoints With Invalid Token
```bash
# Should return 401 Unauthorized
curl -H "Authorization: Bearer invalid-token" \
  http://localhost:YOUR_PORT/api/your-endpoint
```

---

## 🔧 Common Issues & Fixes

### Issue: "Public key not loaded"
**Fix:** 
- Check auth service is running
- Verify `auth.service.url` in properties
- Check network connectivity

### Issue: "403 Forbidden" on protected endpoints
**Fix:**
- Verify SecurityConfig is properly configured
- Check filter is added before UsernamePasswordAuthenticationFilter
- Ensure requestMatchers are in correct order

### Issue: "401 Unauthorized" even with valid token
**Fix:**
- Verify token format: `Bearer <token>` (with space)
- Check public key loaded correctly
- Verify token is not expired
- Check logs for validation errors

### Issue: Public endpoints also require authentication
**Fix:**
- Update `isPublicEndpoint()` in JwtAuthenticationFilter
- Update requestMatchers in SecurityConfig
- Ensure public endpoints are listed BEFORE `.anyRequest().authenticated()`

---

## 📝 Quick Verification Command

Run this after setup:
```bash
# 1. Start your service
# 2. Check logs for: "Public key loaded successfully"
# 3. Test public endpoint (should work)
curl http://localhost:YOUR_PORT/health
# 4. Test protected endpoint without token (should fail)
curl http://localhost:YOUR_PORT/api/test
# 5. Test protected endpoint with token (should work)
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:YOUR_PORT/api/test
```

---

## ✅ Final Checklist

- [ ] All dependencies added
- [ ] Properties configured
- [ ] SecurityConfig created/updated
- [ ] All files have proper annotations
- [ ] Public key loads on startup
- [ ] Public endpoints work without auth
- [ ] Protected endpoints require token
- [ ] Invalid tokens are rejected
- [ ] Valid tokens work correctly


