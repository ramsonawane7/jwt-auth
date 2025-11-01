# API Testing Instructions

## ⚠️ IMPORTANT: Restart Application First!

The 403 Forbidden error indicates the server is running with old security configuration. **You must restart the Spring Boot application** for the changes to take effect.

---

## Step-by-Step Testing Guide

### Step 1: Restart the Application
1. Stop the current running application (if running)
2. Rebuild and restart:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   OR if using an IDE, just restart the application.

### Step 2: Create/Update User (First Time Only)
Before testing login, ensure the user exists with a BCrypt hashed password.

**Postman Request:**
```
POST http://localhost:9090/api/admin/users
Content-Type: application/json

{
    "username": "admin",
    "password": "12345"
}
```

**Expected Response:** `User created successfully` or `Username already exists`

### Step 3: Test Login

**Postman Request:**
```
POST http://localhost:9090/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "12345"
}
```

**Expected Success Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwOTQxMjM0NSwiZXhwIjoxNzA5NDE1OTQ1fQ..."
}
```

**Possible Error Responses:**
- **403 Forbidden:** Application not restarted - restart the server
- **401/400 Invalid Credentials:** User doesn't exist or password is wrong
- **500 Internal Server Error:** Check application logs for details

---

## Using the Provided Postman Collection

1. Open Postman
2. Click **Import** button
3. Select the file: `Postman_Collection.json`
4. The collection will have 3 requests:
   - **1. Create/Update User (Admin)** - Run this first
   - **2. Login** - Run this to test authentication
   - **3. Update Password** - Use to change password

---

## Quick Test Using cURL

### Create/Update User:
```bash
curl -X POST http://localhost:9090/api/admin/users \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"12345"}'
```

### Test Login:
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"12345"}'
```

---

## Troubleshooting

### Issue: 403 Forbidden
**Solution:** Restart the Spring Boot application

### Issue: Invalid Credentials
**Solution:** 
1. Make sure user exists - run the "Create/Update User" endpoint first
2. If user exists but password is plain text in DB, it won't work. Update using admin endpoint.

### Issue: Connection Refused
**Solution:** Make sure the server is running on port 9090

---

## Notes

- All passwords are now stored as BCrypt hashes (secure, one-way encryption)
- The JWT token returned uses RSA256 algorithm (signed with private key)
- Admin endpoints are currently open for testing - secure them in production!


