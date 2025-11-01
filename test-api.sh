#!/bin/bash

echo "=== Testing Employee Management Auth API ==="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:9090"
USERNAME="admin"
PASSWORD="12345"

echo "1. Testing Login Endpoint..."
echo "URL: POST $BASE_URL/api/auth/login"
echo "Body: {\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}"
echo ""

RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')

if [ "$HTTP_CODE" == "200" ]; then
  echo -e "${GREEN}✓ Login Successful!${NC}"
  echo "Response: $BODY"
  echo ""
  TOKEN=$(echo "$BODY" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
  if [ ! -z "$TOKEN" ]; then
    echo -e "${GREEN}JWT Token received:${NC}"
    echo "$TOKEN" | cut -c1-50
    echo "..."
  fi
elif [ "$HTTP_CODE" == "403" ]; then
  echo -e "${RED}✗ Access Forbidden (403)${NC}"
  echo "The endpoint might be blocked by security configuration."
  echo "Please ensure the application has been restarted with the latest SecurityConfig."
elif [ "$HTTP_CODE" == "401" ] || [ "$HTTP_CODE" == "400" ]; then
  echo -e "${RED}✗ Authentication Failed ($HTTP_CODE)${NC}"
  echo "Response: $BODY"
  echo ""
  echo -e "${YELLOW}Possible issues:${NC}"
  echo "1. User '$USERNAME' does not exist in database"
  echo "2. Password is incorrect"
  echo "3. Password in database is plain text but system expects BCrypt hash"
  echo ""
  echo "Try creating/updating the user with:"
  echo "  curl -X POST $BASE_URL/api/admin/users -H 'Content-Type: application/json' -d '{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}'"
else
  echo -e "${RED}✗ Unexpected Error ($HTTP_CODE)${NC}"
  echo "Response: $BODY"
fi

echo ""
echo "=== Test Complete ==="


