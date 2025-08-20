# RunDTOGenerator Flow Chart

## Overview
This flow chart describes the process of generating DTOs, services, and constants from a Swagger/OpenAPI specification using the RunDTOGenerator.

```
┌─────────────────────┐
│  RunDTOGenerator    │
│  (Entry Point)      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  DTOGenerator.main  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Generate DTOs   │  │ Generate        │  │ Generate        │  │
│  │ from YAML       │  │ Services        │  │ RelativeURLs    │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Parse OpenAPI   │  │ Parse OpenAPI   │  │ Parse OpenAPI   │  │
│  │ specification   │  │ specification   │  │ specification   │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Get schemas     │  │ Get paths       │  │ Get paths       │  │
│  │ from components │  │                 │  │                 │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Create output   │  │ Create output   │  │ Create output   │  │
│  │ directories     │  │ directory       │  │ directory       │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Load model      │  │ Load service    │  │ Load            │  │
│  │ template        │  │ template        │  │ relativeurls    │  │
│  └────────┬────────┘  └────────┬────────┘  │ template        │  │
│           │                    │           └────────┬────────┘  │
│           ▼                    │                    │           │
│  ┌─────────────────┐           │                    │           │
│  │ For each schema │           │                    │           │
│  └────────┬────────┘           │                    │           │
│           │                    │                    │           │
│           ▼                    │                    │           │
│  ┌─────────────────┐           │                    │           │
│  │ Create model    │           │                    │           │
│  │ for template    │           │                    │           │
│  └────────┬────────┘           │                    │           │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Generate Java   │  │ Group operations│  │ Process each    │  │
│  │ code using      │  │ by tag          │  │ path            │  │
│  │ template        │  └────────┬────────┘  └────────┬────────┘  │
│  └────────┬────────┘           │                    │           │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Write Java code │  │ For each tag    │  │ Generate        │  │
│  │ to file         │  └────────┬────────┘  │ constant name   │  │
│  └─────────────────┘           │           └────────┬────────┘  │
│                                │                    │           │
│                                ▼                    │           │
│                     ┌─────────────────┐             │           │
│                     │ Create model    │             │           │
│                     │ for template    │             │           │
│                     └────────┬────────┘             │           │
│                                │                    │           │
│                                ▼                    ▼           │
│                     ┌─────────────────┐  ┌─────────────────┐    │
│                     │ Generate Java   │  │ Create model    │    │
│                     │ code using      │  │ for template    │    │
│                     │ template        │  └────────┬────────┘    │
│                     └────────┬────────┘           │             │
│                                │                  │             │
│                                ▼                  ▼             │
│                     ┌─────────────────┐  ┌─────────────────┐    │
│                     │ Write Java code │  │ Generate Java   │    │
│                     │ to file         │  │ code using      │    │
│                     └─────────────────┘  │ template        │    │
│                                          └────────┬────────┘    │
│                                                   │             │
│                                                   ▼             │
│                                          ┌─────────────────┐    │
│                                          │ Write Java code │    │
│                                          │ to file         │    │
│                                          └─────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
           │
           ▼
┌─────────────────────┐
│ Generate            │
│ BasePathURLs        │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Parse OpenAPI       │
│ specification       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Get servers         │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Create output       │
│ directory           │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Load basepathurls   │
│ template            │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Process each server │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Generate constant   │
│ name                │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Create model for    │
│ template            │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Generate Java code  │
│ using template      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Write Java code     │
│ to file             │
└─────────────────────┘
```

## Detailed Process Description

1. **RunDTOGenerator (Entry Point)**
   - The process begins with RunDTOGenerator.java, which calls DTOGenerator.main()

2. **DTOGenerator.main**
   - Calls four generator methods in sequence:
     - generateDTOsFromYaml()
     - generateServicesFromYaml()
     - generateRelativeURLsFromYaml()
     - generateBasePathURLsFromYaml()

3. **Generate DTOs from YAML**
   - Parses the OpenAPI specification from swagger.yaml
   - Gets the schemas from the components section
   - Creates output directories for DTOs
   - Loads the model.mustache template
   - For each schema:
     - Creates a model for the template
     - Generates Java code using the template
     - Writes the Java code to a file in the appropriate directory

4. **Generate Services from YAML**
   - Parses the OpenAPI specification from swagger.yaml
   - Gets the paths from the specification
   - Creates the service output directory
   - Loads the service.mustache template
   - Groups operations by tag
   - For each tag:
     - Creates a model for the template
     - Generates Java code using the template
     - Writes the Java code to a file

5. **Generate RelativeURLs from YAML**
   - Parses the OpenAPI specification from swagger.yaml
   - Gets the paths from the specification
   - Creates the constants output directory
   - Loads the relativeurls.mustache template
   - For each path:
     - Generates a constant name
     - Adds the path information to a list
   - Creates a model for the template
   - Generates Java code using the template
   - Writes the Java code to a file

6. **Generate BasePathURLs from YAML**
   - Parses the OpenAPI specification from swagger.yaml
   - Gets the servers from the specification
   - Creates the constants output directory
   - Loads the basepathurls.mustache template
   - For each server:
     - Generates a constant name
     - Adds the server information to a list
   - Creates a model for the template
   - Generates Java code using the template
   - Writes the Java code to a file

## Generated Files

1. **DTO Classes**
   - Request DTOs in src/main/java/com/example/dto/RequestDTO/
   - Response DTOs in src/main/java/com/example/dto/ResponseDTO/

2. **Service Classes**
   - Service classes in src/main/java/com/example/service/

3. **Constants**
   - RelativeURLs.java in src/main/java/com/example/constants/
   - BasePathURLs.java in src/main/java/com/example/constants/