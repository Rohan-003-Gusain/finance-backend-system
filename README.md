# Finance Data Processing and Access Control Backend
### Spring Boot REST API with role-based access control, transaction management, and dashboard analytics
---

## Objective

The **Finance Backend System** is a RESTful backend application built using **Spring Boot**.

It provides a complete finance data management workflow where **Admins** can manage users, create and manage transactions, while **Analysts** can view and filter financial data, and **Viewers** can access dashboard summaries.

This project follows **clean architecture principles** using DTOs, MapStruct mappers, layered services, role-based security, and soft delete pattern.

---

## Assignment Mapping
 
This project fulfills all core requirements of the assignment:
 
| Requirement | Implementation |
|---|---|
| User & Role Management | `UserController`, `UserService`, `Role` enum (ADMIN / ANALYST / VIEWER) |
| Financial Records Management | `TransactionController`, `TransactionService`, soft delete |
| Dashboard Summary APIs | `DashboardController` – summary, category-wise, monthly trend |
| Access Control Logic | Spring Security + JWT + `@PreAuthorize` per endpoint |
| Validation & Error Handling | Jakarta Validation + `GlobalExceptionHandler` |
| Data Persistence | JPA / Hibernate with relational database |
| Pagination | Custom `PageResponse` DTO used for clean paginated response |
| Soft Delete | `deleted` flag on `TransactionEntity` |
| API Documentation | Swagger UI with Bearer auth support |
| Authentication | JWT token-based authentication |
 
---

## Technologies Used

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security + JWT Authentication
- **Database:** PostgreSQL / H2 (JPA / Hibernate)
- **Build Tool:** Maven
- **API Documentation:** Swagger UI (SpringDoc OpenAPI)
- **Mapping:** MapStruct (DTO ↔ Entity)
- **Validation:** Jakarta Bean Validation
- **Architecture:** Client → Controller → Service → Repository → Database

---

## Features

### Authentication & Authorization
- Login using email & password
- JWT-based stateless authentication
- Role-based API access (`ADMIN`, `ANALYST`, `VIEWER`)
- Inactive user login blocked at authentication level

### Transaction Management
- Create transactions with amount, type, category, date, and note
- Soft delete — data is never permanently removed, `deleted` flag is set to `true`
- Clean paginated response using custom PageResponse DTO (no default Spring Page structure)
- Filter by `RecordType` (INCOME / EXPENSE)
- Filter by category

### User Management
- Admin can create users with specific roles
- Update user role dynamically
- Update user status (ACTIVE / INACTIVE)
- Duplicate email check on user creation → returns `409 Conflict`

### Dashboard & Analytics
- User-wise total income, total expense, net balance summary
- User-wise category breakdown (only expenses)
- User-wise monthly income & expense analytics

### Auto Admin Initialization
| Field | Value |
|---|---|
| Email | `admin@gmail.com` |
| Password | `admin12345` |

> Admin is auto-created on application startup if not already present.

---

## API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/login` | Public | Login & get JWT token |

### User Management *(Admin only)*
| Method | Endpoint | Description |
|---|---|---|
| POST | `/users` | Create new user |
| GET | `/users` | Get all users |
| GET | `/users/{id}` | Get user by ID |
| PUT | `/users/{id}/role` | Update user role |
| PUT | `/users/{id}/status` | Update user status |

### Transaction Management
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/transactions` | ADMIN, ANALYST | Create transaction (ANALYST → only EXPENSE) |
| GET | `/transactions?page=0&size=5` | ADMIN, ANALYST | Get all (ADMIN → all, ANALYST → only own expense) |
| GET | `/transactions/{id}` | ADMIN, ANALYST | Get by ID (ANALYST → only own) |
| GET | `/transactions/filter/type?type=INCOME` | ADMIN, ANALYST | Filter by type |
| GET | `/transactions/filter/category?category=Food` | ADMIN, ANALYST | Filter by category |
| PUT | `/transactions/{id}` | ADMIN, ANALYST | Update (ANALYST → only own expense) |
| DELETE | `/transactions/{id}` | ADMIN, ANALYST | Soft delete (ANALYST → only own expense) |

### Dashboard *(All roles)*
| Method | Endpoint | Description |
|---|---|---|
| GET | `/dashboard/summary` | User-wise total income, expense, balance |
| GET | `/dashboard/category-wise` | User-wise expense grouped by category |
| GET | `/dashboard/monthly-dashboard` | User-wise monthly income & expense |
---

## Data Models

### UserEntity
| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| name | String | User's full name |
| email | String | Unique email |
| password | String | BCrypt encrypted |
| role | Enum | ADMIN / ANALYST / VIEWER |
| status | Enum | ACTIVE / INACTIVE |

### TransactionEntity
| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| amount | Double | Transaction amount |
| type | Enum | INCOME / EXPENSE |
| category | String | Category name |
| date | LocalDate | Transaction date |
| note | String | Optional note |
| deleted | Boolean | Soft delete flag (default: false) |
| user | UserEntity | Owner of the transaction |

---

## Validation & Error Handling

All input is validated using Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Positive`, `@PastOrPresent`).

A centralized `GlobalExceptionHandler` handles all exceptions and returns consistent JSON error responses.

### Error Response Format
```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Transaction not found",
  "path": "/transactions/99"
}
```

### Exception Mapping
| Exception | HTTP Status |
|---|---|
| `BadRequestException` | 400 Bad Request |
| `MethodArgumentNotValidException` | 400 Bad Request |
| `UnauthorizedException` | 401 Unauthorized |
| `AccessDeniedException` | 403 Forbidden |
| `ResourceNotFoundException` | 404 Not Found |
| `ConflictException` | 409 Conflict |
| `RuntimeException` | 500 Internal Server Error |

---

## Assumptions & Design Decisions

- **Soft Delete:** Transactions are never permanently deleted. A `deleted` boolean flag is used so historical data is preserved.
- **Transaction Ownership:** Every transaction is linked to the currently authenticated user via `SecurityContextHolder`.
- **Role Hierarchy:** ADMIN has full access, ANALYST has read access to transactions and dashboard, VIEWER can only see dashboard.
- **Default Admin:** A seed admin account is auto-created at startup via `DataInitializer` using `@PostConstruct`.
- **JWT Stateless Auth:** No server-side sessions are maintained. Every request is authenticated via the JWT token in the `Authorization` header.
- **Duplicate Email:** Creating a user with an existing email returns `409 Conflict` instead of a raw DB constraint error.
- Dashboard APIs return user-wise analytics instead of combined data
- Monthly dashboard includes both income and expense per month
- **Role-Based Access:**
  - ADMIN can create, update, and delete users & transactions (INCOME & EXPENSE)
  - ANALYST can:
    - Create only EXPENSE transactions
    - Update/Delete only their own EXPENSE
    - View transactions and apply filters
  - VIEWER can only access dashboard APIs
- **Role Hierarchy:**
  - ADMIN → Full access (users + transactions + dashboard)
  - ANALYST → Own expense management + read access
  - VIEWER → Dashboard-only access
- **Transaction Ownership:** ANALYST users can only modify transactions they own.
---

## Optional Enhancements Implemented

- ✅ JWT Authentication
- ✅ Pagination for transaction listing
- ✅ Soft delete
- ✅ Swagger UI with Bearer token support
- ✅ Input validation with meaningful error messages
- ✅ Role-based access control at endpoint level
- ✅ Auto admin seeding on startup
- ✅ Centralized exception handling with consistent error format

---

## Future Enhancements
- Change Password API for users
- Forgot Password (reset via email)
- Role-based password policies

---

## Project Structure

```
com.finance
├── config
│   ├── DataInitializer.java
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── SwaggerController.java
│   ├── TransactionController.java
│   └── UserController.java
├── dto
│   ├── request/
│   │   ├── CreateTransactionRequest.java
│   │   ├── LoginRequest.java
│   │   ├── UpdateTransactionRequest.java
│   │   ├── UpdateUserRoleRequest.java
│   │   ├── UpdateUserStatusRequest.java
│   │   └── UserRequest.java
│   ├── response/
│   │   ├── AuthResponse.java
│   │   ├── CategoryItem.java
│   │   ├── ExpenseResponse.java
│   │   ├── MonthlyExpenseResponse.java
│   │   ├── PageResponse.java
│   │   ├── TransactionResponse.java
│   │   ├── UserCategoryResponse.java
│   │   ├── UserExpenseResponse.java
│   │   ├── UserMonthlyDashboardResponse.java
│   │   ├── UserResponse.java
│   │   └── UserSummaryResponse.java
│   └── ApiErrorDTO.java
├── exception
│   ├── AccessDeniedException.java
│   ├── BadRequestException.java
│   ├── ConflictException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── mapper
│   ├── TransactionMapper.java
│   └── UserMapper.java
├── model
│   ├── TransactionEntity.java
│   ├── UserEntity.java
│   └── enums/
│       ├── RecordType.java
│       ├── Role.java
│       └── UserStatus.java
├── repository
│   ├── TransactionRepository.java
│   └── UserRepository.java
├── service
│   ├── AuthService.java
│   ├── DashboardService.java
│   ├── TransactionService.java
│   ├── UserService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── DashboardServiceImpl.java
│       ├── TransactionServiceImpl.java
│       ├── UserDetailsServiceImpl.java
│       └── UserServiceImpl.java
├── FinanaceBackendSystemApplication.java
└── GlobalExceptionHandler.java
```

---

## How to Run

1. Clone the repository
```bash
   git clone <your-repo-url>
```

2. Configure `src/main/resources/application.properties`:
```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   jwt.secret=your_secret_key
   jwt.expiration=86400000
```

3. Run the application:
```bash
   mvn spring-boot:run
```

4. Access Swagger UI at:
```
   http://localhost:8080/swagger-ui.html
```

5. Login via `/login` to get JWT token, then use **Authorize** button in Swagger to set Bearer token