# API Documentation

## Authentication Endpoints

### POST /api/auth/signin
Login with username/email and password.

**Request:**
```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "user@example.com",
  "authorities": ["ROLE_USER"]
}
```

### POST /api/auth/signup
Register a new user account.

**Request:**
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

## Gold Price Endpoints

### GET /api/gold-prices/current
Get current gold buying and selling prices.

**Response:**
```json
{
  "id": 1,
  "buyPrice": 2000.00,
  "sellPrice": 1960.00,
  "currency": "USD",
  "unit": "troy_ounce",
  "timestamp": "2023-10-15T10:30:00",
  "isActive": true
}
```

## Transaction Endpoints

### POST /api/transactions/buy
Buy gold with specified amount.

**Headers:** `Authorization: Bearer <token>`

**Request:** `amount=2.5` (query parameter)

### POST /api/transactions/sell
Sell gold with specified amount.

**Headers:** `Authorization: Bearer <token>`

**Request:** `amount=1.0` (query parameter)

### GET /api/transactions/history
Get user's transaction history with pagination.

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)

## Error Responses

All endpoints return appropriate HTTP status codes:
- 200: Success
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

Error responses include a message describing the issue.