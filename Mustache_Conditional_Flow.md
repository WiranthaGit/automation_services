# Mustache Template Conditional Flow

This document explains how conditional logic works in the Mustache templates used by the DTOGenerator to generate Java code from OpenAPI specifications.

## Overview of Mustache Conditional Syntax

Mustache templates use the following syntax for conditional logic:

- `{{#variable}}` - Include content if variable exists/is true
- `{{^variable}}` - Include content if variable doesn't exist/is false
- `{{#array}}` - Iterate over each item in the array
- `{{variable}}` - Insert the value of variable

## Conditional Flow in model.mustache

The model.mustache template is used to generate DTO (Data Transfer Object) classes. Here's how conditions affect the generated code:

### Decision Point 1: Is this a Response DTO?
**Condition:** `isResponseDTO` flag
**Set by:** `createModelForTemplate` method
```java
boolean isResponseDTO = forceResponseDTO || className.endsWith("ResponseDTO");
model.put("isResponseDTO", isResponseDTO);
```

**Outcomes:**
- If true:
  - Adds import for BaseResponseDTO
  - Class extends BaseResponseDTO
  - Package is set to RESPONSE_DTO_PACKAGE
- If false:
  - Class is standalone (doesn't extend anything)
  - Package is set to REQUEST_DTO_PACKAGE

### Decision Point 2: Are there vendor extensions?
**Condition:** `vendorExtensions` flag
**Set by:** Not explicitly set in the code we examined

**Outcomes:**
- If true: Uses vendor-specific class definition
- If false: Uses standard class definition

### Decision Point 3: Does the class have properties?
**Condition:** `vars` array
**Set by:** `createModelForTemplate` method processes schema properties
```java
List<Map<String, Object>> vars = new ArrayList<>();
// ... process properties ...
model.put("vars", vars);
```

**Outcomes:**
- If properties exist: Generates private fields for each property
- If no properties: Class has no fields

### Decision Point 4: Do properties have descriptions?
**Condition:** `description` in each property
**Set by:** `createModelForTemplate` method
```java
var.put("description", prop.getValue().getDescription());
```

**Outcomes:**
- If description exists: Adds JavaDoc comment for the property
- If no description: No comment is added

### Decision Point 5: Are there imports needed?
**Condition:** `imports` array
**Set by:** `createModelForTemplate` method adds imports based on property types
```java
Set<String> imports = new HashSet<>();
// ... add imports based on types ...
List<Map<String, String>> importsList = new ArrayList<>();
for (String importItem : imports) {
    Map<String, String> importMap = new HashMap<>();
    importMap.put("import", importItem);
    importsList.add(importMap);
}
model.put("imports", importsList);
```

**Outcomes:**
- If imports exist: Adds import statements for each required class
- If no imports: No additional import statements

## Conditional Flow in service.mustache

The service.mustache template is used to generate service classes for API operations. Here's how conditions affect the generated code:

### Decision Point 1: Are there operations for this service?
**Condition:** `operations` array
**Set by:** `generateServicesFromYaml` method groups operations by tag

**Outcomes:**
- If operations exist: Generates methods for each operation
- If no operations: Empty service class

### Decision Point 2: Does the operation have a request body?
**Condition:** `hasRequestBody` flag
**Set by:** `processOperation` method
```java
boolean hasRequestBody = operation.getRequestBody() != null;
operationMap.put("hasRequestBody", hasRequestBody);
```

**Outcomes:**
- If true:
  - Generates an overloaded method that accepts a request body
  - Adds code to serialize the request body
- If false:
  - Generates a method without request body parameter
  - Uses a different API call pattern

### Decision Point 3: Is the request body an array?
**Condition:** `isArrayRequestBody` flag
**Set by:** `processOperation` method
```java
if ("array".equals(schema.getType())) {
    // ... process array schema ...
    operationMap.put("isArrayRequestBody", true);
} else {
    // ... process object schema ...
    operationMap.put("isArrayRequestBody", false);
}
```

**Outcomes:**
- If true: Method accepts a List<Type> parameter
- If false: Method accepts a single Type parameter

### Decision Point 4: Does the operation have path parameters?
**Condition:** `hasPathParams` flag
**Set by:** `processOperation` method
```java
operationMap.put("hasPathParams", !pathParams.isEmpty());
```

**Outcomes:**
- If true:
  - Adds path parameters to method signature
  - Adds code to replace path parameters in the URL
- If false: Uses the path as-is

### Decision Point 5: Does the operation have query parameters?
**Condition:** `hasQueryParams` flag
**Set by:** `processOperation` method
```java
operationMap.put("hasQueryParams", !queryParams.isEmpty());
```

**Outcomes:**
- If true:
  - Adds Map<String, Object> queryParams parameter to method signature
  - Passes query parameters to the API call
- If false: No query parameter handling

## Conditional Flow in relativeurls.mustache and basepathurls.mustache

These templates are simpler and primarily use iteration rather than complex conditional logic:

### Decision Point 1: Are there paths/servers defined?
**Condition:** `paths`/`servers` arrays
**Set by:** `generateRelativeURLsFromYaml`/`generateBasePathURLsFromYaml` methods

**Outcomes:**
- If paths/servers exist: Generates constants for each path/server
- If no paths/servers: Empty interface

## Complete Conditional Flow Diagram

```
┌─────────────────────┐
│  DTOGenerator.main  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Generate DTOs   │  │ Generate        │  │ Generate        │  │
│  │ from YAML       │  │ Services        │  │ Constants       │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ For each schema │  │ Group operations│  │ For each path/  │  │
│  └────────┬────────┘  │ by tag          │  │ server          │  │
│           │           └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Create model    │  │ For each tag    │  │ Create model    │  │
│  │ with conditions │  └────────┬────────┘  │ with paths/     │  │
│  └────────┬────────┘           │           │ servers         │  │
│           │                    │           └────────┬────────┘  │
│           │                    ▼                    │           │
│           │          ┌─────────────────┐            │           │
│           │          │ For each        │            │           │
│           │          │ operation       │            │           │
│           │          └────────┬────────┘            │           │
│           │                   │                     │           │
│           │                   ▼                     │           │
│           │          ┌─────────────────┐            │           │
│           │          │ Process         │            │           │
│           │          │ operation with  │            │           │
│           │          │ conditions      │            │           │
│           │          └────────┬────────┘            │           │
│           │                   │                     │           │
│           ▼                   ▼                     ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Apply template  │  │ Apply template  │  │ Apply template  │  │
│  │ with conditions:│  │ with conditions:│  │ with conditions:│  │
│  │ - isResponseDTO │  │ - hasRequestBody│  │ - paths/servers │  │
│  │ - vendorExt     │  │ - isArrayReqBody│  │ iteration       │  │
│  │ - vars          │  │ - hasPathParams │  └────────┬────────┘  │
│  │ - description   │  │ - hasQueryParams│           │           │
│  │ - imports       │  └────────┬────────┘           │           │
│  └────────┬────────┘           │                    │           │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Generate DTO    │  │ Generate Service│  │ Generate        │  │
│  │ Java Code       │  │ Java Code       │  │ Constants       │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## How Conditions Affect Generated Code

### Example 1: Response DTO vs Request DTO

**Condition:** `isResponseDTO`

**Response DTO (isResponseDTO = true):**
```java
import com.cloud.api.dto.BaseResponseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDTO extends BaseResponseDTO {
    // Properties
}
```

**Request DTO (isResponseDTO = false):**
```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    // Properties
}
```

### Example 2: Service Method with Different Request Body Types

**Conditions:** `hasRequestBody`, `isArrayRequestBody`

**Method with Object Request Body (hasRequestBody = true, isArrayRequestBody = false):**
```java
public Response addPet(Pet body, Headers headers, Class<?> classType) throws Exception {
    String requestBody = objectMapper.writeValueAsString(body);
    return addPet(requestBody, headers, classType, Method.POST);
}
```

**Method with Array Request Body (hasRequestBody = true, isArrayRequestBody = true):**
```java
public Response createUsersWithListInput(List<User> body, Headers headers, Class<?> classType) throws Exception {
    String requestBody = objectMapper.writeValueAsString(body);
    return createUsersWithListInput(requestBody, headers, classType, Method.POST);
}
```

**Method without Request Body (hasRequestBody = false):**
```java
public Response getUserByName(String username, Headers headers, Class<?> classType) throws Exception {
    return getUserByName(username, headers, classType, Method.GET);
}
```

### Example 3: Path Parameter Handling

**Condition:** `hasPathParams`

**Method with Path Parameters (hasPathParams = true):**
```java
public Response getUserByName(String username, Headers headers, Class<?> classType, Method method) throws Exception {
    String path = RelativeURLs.USER_USERNAME;
    path = path.replace("{username}", username);
    setRequest(BasePathURLs.SERVER_PETSTORE3_SWAGGER_IO_API_V3, path, headers);
    Response response = makeRequest(headers, method);
    // ...
}
```

**Method without Path Parameters (hasPathParams = false):**
```java
public Response getInventory(Headers headers, Class<?> classType, Method method) throws Exception {
    String path = RelativeURLs.STORE_INVENTORY;
    setRequest(BasePathURLs.SERVER_PETSTORE3_SWAGGER_IO_API_V3, path, headers);
    Response response = makeRequest(headers, method);
    // ...
}
```