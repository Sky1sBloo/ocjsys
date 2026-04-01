# OCJSys API Routes

This document was generated from the current Spring controllers and security config in `src/main/java`.

## Base URL

- Local app: `http://localhost:8080`
- API prefix used by this app: `/api`

## Authentication Model

- The API uses JWT authentication (Bearer token) for protected routes.
- Login returns both `jwtToken` and `refreshToken`.
- Security rules are defined in `src/main/java/com/sky1sbloo/ocjsys/auth/SecurityConfig.java`.

### Access Levels Used Below

- `Public`: no JWT required
- `Auth`: authenticated JWT required
- `Authority: <NAME>`: JWT required and must have that authority

## Route Index

| Method | Path                                       | Access                            | Controller Method                                   |
|--------|--------------------------------------------|-----------------------------------|-----------------------------------------------------|
| POST   | `/api/auth/login`                          | Public                            | `AuthenticationController.login`                    |
| POST   | `/api/auth/register`                       | Public                            | `AuthenticationController.register`                 |
| POST   | `/api/auth/refresh`                        | Auth                              | `AuthenticationController.refreshToken`             |
| POST   | `/api/auth/logout`                         | Public                            | `AuthenticationController.logout`                   |
| PUT    | `/api/auth/role`                           | Authority: `CHANGE_USER_ROLE`     | `AuthenticationController.setUserRole`              |
| GET    | `/api/code/problems`                       | Auth                              | `CodeProblemController.getProblems`                 |
| GET    | `/api/code/problems/{id}`                  | Auth                              | `CodeProblemController.getProblem`                  |
| POST   | `/api/code/problems`                       | Authority: `CREATE_CODE_PROBLEMS` | `CodeProblemController.createProblem`               |
| PUT    | `/api/code/problems`                       | Authority: `CREATE_CODE_PROBLEMS` | `CodeProblemController.updateProblem`               |
| GET    | `/api/code/problems/templates/{problemId}` | Auth                              | `SolutionTemplateController.getSolutionTemplate`    |
| POST   | `/api/code/problems/templates`             | Authority: `CREATE_CODE_PROBLEMS` | `SolutionTemplateController.addSolutionTemplate`    |
| PUT    | `/api/code/problems/templates/{problemId}` | Authority: `CREATE_CODE_PROBLEMS` | `SolutionTemplateController.updateSolutionTemplate` |
| DELETE | `/api/code/problems/templates`             | Authority: `CREATE_CODE_PROBLEMS` | `SolutionTemplateController.deleteSolutionTemplate` |
| POST   | `/api/code/submissions`                    | Auth                              | `CodeSubmissionController.submitCode`               |
| POST   | `/api/code/submissions/run`                | Auth                              | `CodeSubmissionController.runCode`                  |
| GET    | `/api/users/profile`                       | Auth                              | `UserProfileController.getUserInfo`                 |
| GET    | `/api/users/`                              | Auth                              | `UserProfileController.getUsers`                    |
| GET    | `/actuator/health`                         | Public                            | Spring Actuator                                     |
| GET    | `/actuator/info`                           | Public                            | Spring Actuator                                     |

---

## Auth Routes

### `POST /api/auth/login`

- Access: `Public`
- Body (`LoginRequest`):

```json
{
  "username": "string",
  "password": "string"
}
```

- Success response (`LoginResponse`):

```json
{
  "id": 1,
  "username": "root",
  "name": "Root User",
  "authorities": ["CREATE_CODE_PROBLEMS"],
  "jwtToken": "<jwt>",
  "refreshToken": "<refresh-token>"
}
```

### `POST /api/auth/register`

- Access: `Public`
- Body (`RegisterRequest`):

```json
{
  "username": "string",
  "password": "string",
  "name": "string"
}
```

- Success: `201 Created`
- Response (`RegisterResponse`):

```json
{
  "id": 1,
  "username": "new-user"
}
```

### `POST /api/auth/refresh`

- Access: `Auth`
- Body (`RefreshRequest`):

```json
{
  "refreshToken": "string"
}
```

- Success response (`RefreshResponse`):

```json
{
  "token": "<new-jwt>"
}
```

### `POST /api/auth/logout`

- Access: `Public`
- Body (`LogoutRequest`):

```json
{
  "refreshToken": "string"
}
```

- Success: `200 OK` with message body

### `PUT /api/auth/role`

- Access: `Authority: CHANGE_USER_ROLE`
- Query params:
  - `id` (long): user id
  - `roles` (repeatable): role enum names
- Example:
  - `/api/auth/role?id=2&roles=USER&roles=CREATE_CODE_PROBLEMS`
- Success: `204 No Content`

---

## Code Problem Routes

### `GET /api/code/problems`

- Access: `Auth`
- Optional query params:
  - `owner` (string)
  - `title` (string)
  - `tags` (repeatable)
  - `difficulties` (repeatable)
- Returns: `Set<CodeProblemResponseDto>`

### `GET /api/code/problems/{id}`

- Access: `Auth`
- Path param: `id` (long)
- Returns: `CodeProblemResponseDto`

### `POST /api/code/problems`

- Access: `Authority: CREATE_CODE_PROBLEMS`
- Body (`CodeProblemCreateDto`):

```json
{
  "title": "string",
  "tags": ["array", "of", "strings"],
  "difficulty": "EASY",
  "description": "string",
  "solution": "string",
  "solutionTemplates": [
    {
      "language": "JAVA",
      "sourceCode": "string",
      "verifierSourceCode": "string"
    }
  ]
}
```

- Success: `201 Created`

### `PUT /api/code/problems`

- Access: `Authority: CREATE_CODE_PROBLEMS`
- Body (`CodeProblemEditDto`):

```json
{
  "id": 1,
  "title": "string",
  "tags": ["array", "of", "strings"],
  "difficulty": "EASY",
  "description": "string",
  "solution": "string"
}
```

- Success: `200 OK`

---

## Solution Template Routes

### `GET /api/code/problems/templates/{problemId}`

- Access: `Auth`
- Path param: `problemId` (long)
- Optional query param:
  - `language` (`CodeLanguage` enum)
- Behavior:
  - without `language`: returns all templates for the problem
  - with `language`: returns one template for that language

### `POST /api/code/problems/templates`

- Access: `Authority: CREATE_CODE_PROBLEMS`
- Body (`SolutionTemplateDto`):

```json
{
  "problemId": 1,
  "language": "JAVA",
  "sourceCode": "string",
  "verifierSourceCode": "string"
}
```

- Success: `201 Created`

### `PUT /api/code/problems/templates/{problemId}`

- Access: `Authority: CREATE_CODE_PROBLEMS`
- Path param: `problemId` (long)
- Body (`SolutionTemplateEditDto`):

```json
{
  "language": "JAVA",
  "sourceCode": "string",
  "verifierSourceCode": "string"
}
```

- Success: `204 No Content`

### `DELETE /api/code/problems/templates`

- Access: `Authority: CREATE_CODE_PROBLEMS`
- Inputs are bound from request parameters (`SolutionTemplateGetDto`):
  - `problemId` (long)
  - `language` (`CodeLanguage` enum)
- Example:
  - `/api/code/problems/templates?problemId=10&language=JAVA`
- Success: `204 No Content`

---

## Code Submission Routes

### `POST /api/code/submissions`

- Access: `Auth`
- Body (`CodeSubmissionDto`):

```json
{
  "problemId": 1,
  "sourceCode": "string",
  "language": "JAVA"
}
```

- Success: `201 Created`

### `POST /api/code/submissions/run`

- Access: `Auth`
- Body (`CodeSubmissionDto`): same shape as submit
- Success: `200 OK` with output text

---

## User Routes

### `GET /api/users/profile`

- Access: `Auth`
- Returns: `UserProfileDto`

### `GET /api/users/`

- Access: `Auth`
- Returns: `UserListDto` (`users: UserProfileDto[]`)

---
