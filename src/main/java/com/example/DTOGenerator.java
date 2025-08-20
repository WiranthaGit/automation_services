package com.example;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Utility class to generate DTO classes from a YAML specification.
 */
public class DTOGenerator {

    private static final String YAML_FILE_PATH = "src/main/resources/java/swagger.yaml";
    private static final String DTO_TEMPLATE_FILE_PATH = "src/main/resources/java/model.mustache";
    private static final String SERVICE_TEMPLATE_FILE_PATH = "src/main/resources/java/service.mustache";
    private static final String RELATIVE_URLS_TEMPLATE_FILE_PATH = "src/main/resources/java/relativeurls.mustache";
    private static final String BASE_PATH_URLS_TEMPLATE_FILE_PATH = "src/main/resources/java/basepathurls.mustache";
    private static final String BASE_DTO_PACKAGE = "com.example.dto";
    private static final String REQUEST_DTO_PACKAGE = "com.example.dto.RequestDTO";
    private static final String RESPONSE_DTO_PACKAGE = "com.example.dto.ResponseDTO";
    private static final String SERVICE_PACKAGE = "com.example.service";
    private static final String CONSTANTS_PACKAGE = "com.example.constants";
    private static final String BASE_DTO_OUTPUT_DIR = "src/main/java/com/example/dto";
    private static final String REQUEST_DTO_OUTPUT_DIR = "src/main/java/com/example/dto/RequestDTO";
    private static final String RESPONSE_DTO_OUTPUT_DIR = "src/main/java/com/example/dto/ResponseDTO";
    private static final String SERVICE_OUTPUT_DIR = "src/main/java/com/example/service";
    private static final String CONSTANTS_OUTPUT_DIR = "src/main/java/com/example/constants";

    public static void main(String[] args) {
        try {
            generateDTOsFromYaml();
            generateBasePathURLsFromYaml();
            generateServicesFromYaml();
            generateRelativeURLsFromYaml();
            System.out.println("DTO, BasePathURLs, Service, and RelativeURLs generation completed successfully!");
        } catch (Exception e) {
            System.err.println("Error generating DTOs, BasePathURLs, Services, or RelativeURLs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates or updates DTO classes from the YAML specification
     * 
     * @throws IOException if an error occurs
     */
    private static void generateDTOsFromYaml() throws IOException {
        System.out.println("Generating DTOs from YAML specification...");
        
        // Parse the OpenAPI specification from YAML
        OpenAPI openAPI = new OpenAPIV3Parser().read(YAML_FILE_PATH);
        
        // Get the schemas from the components section
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();
        
        // Create the base output directory if it doesn't exist
        File baseOutputDir = new File(BASE_DTO_OUTPUT_DIR);
        if (!baseOutputDir.exists()) {
            baseOutputDir.mkdirs();
        }
        
        // Create the request DTO output directory if it doesn't exist
        File requestOutputDir = new File(REQUEST_DTO_OUTPUT_DIR);
        if (!requestOutputDir.exists()) {
            requestOutputDir.mkdirs();
        }
        
        // Create the response DTO output directory if it doesn't exist
        File responseOutputDir = new File(RESPONSE_DTO_OUTPUT_DIR);
        if (!responseOutputDir.exists()) {
            responseOutputDir.mkdirs();
        }
        
        // Load the Mustache template for DTOs
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache dtoMustache = mf.compile(DTO_TEMPLATE_FILE_PATH);
        
        // Generate a DTO class for each schema
        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            String className = entry.getKey();
            Schema schema = entry.getValue();
            
            // Skip if the class is already a ResponseDTO
            if (className.endsWith("ResponseDTO")) {
                // Create a model for the Mustache template
                Map<String, Object> model = createModelForTemplate(className, schema, true);
                
                // Generate the Java code using the Mustache template
                StringWriter writer = new StringWriter();
                dtoMustache.execute(writer, model).flush();
                String javaCode = writer.toString();
                
                // Write the Java code to a file
                String filePath = RESPONSE_DTO_OUTPUT_DIR + "/" + className + ".java";
                File file = new File(filePath);
                
                // Check if the file already exists
                if (file.exists()) {
                    System.out.println("Updating existing response DTO class: " + className);
                } else {
                    System.out.println("Creating new response DTO class: " + className);
                }
                
                FileUtils.writeStringToFile(file, javaCode, "UTF-8");
            } else {
                // Create request DTO
                Map<String, Object> requestModel = createModelForTemplate(className, schema, false);
                StringWriter requestWriter = new StringWriter();
                dtoMustache.execute(requestWriter, requestModel).flush();
                String requestJavaCode = requestWriter.toString();
                
                // Write the request DTO to a file
                String requestFilePath = REQUEST_DTO_OUTPUT_DIR + "/" + className + ".java";
                File requestFile = new File(requestFilePath);
                
                // Check if the file already exists
                if (requestFile.exists()) {
                    System.out.println("Updating existing request DTO class: " + className);
                } else {
                    System.out.println("Creating new request DTO class: " + className);
                }
                
                FileUtils.writeStringToFile(requestFile, requestJavaCode, "UTF-8");
                
                // Create response DTO with the same name
                Map<String, Object> responseModel = createModelForTemplate(className, schema, true);
                StringWriter responseWriter = new StringWriter();
                dtoMustache.execute(responseWriter, responseModel).flush();
                String responseJavaCode = responseWriter.toString();
                
                // Write the response DTO to a file
                String responseFilePath = RESPONSE_DTO_OUTPUT_DIR + "/" + className + ".java";
                File responseFile = new File(responseFilePath);
                
                // Check if the file already exists
                if (responseFile.exists()) {
                    System.out.println("Updating existing response DTO class: " + className);
                } else {
                    System.out.println("Creating new response DTO class: " + className);
                }
                
                FileUtils.writeStringToFile(responseFile, responseJavaCode, "UTF-8");
            }
        }
    }
    
    /**
     * Creates a model for the Mustache template
     * 
     * @param className the name of the class
     * @param schema the schema from the YAML specification
     * @param forceResponseDTO flag to force creating a response DTO model
     * @return a map containing the model for the Mustache template
     */
    private static Map<String, Object> createModelForTemplate(String className, Schema schema, boolean forceResponseDTO) {
        Map<String, Object> model = new HashMap<>();
        Set<String> imports = new HashSet<>();
        
        // Determine if this is a response DTO
        boolean isResponseDTO = forceResponseDTO || className.endsWith("ResponseDTO");
        
        // Set the package based on whether it's a request or response DTO
        String packageName;
        if (isResponseDTO) {
            packageName = RESPONSE_DTO_PACKAGE;
        } else {
            packageName = REQUEST_DTO_PACKAGE;
        }
        
        // Add basic information
        model.put("package", packageName);
        model.put("classname", className);
        model.put("description", schema.getDescription());
        
        // Set isResponseDTO flag
        model.put("isResponseDTO", isResponseDTO);
        
        // Add properties
        List<Map<String, Object>> vars = new ArrayList<>();
        Map<String, Schema> properties = schema.getProperties();
        
        if (properties != null) {
            for (Map.Entry<String, Schema> prop : properties.entrySet()) {
                Map<String, Object> var = new HashMap<>();
                var.put("name", prop.getKey());
                var.put("baseName", prop.getKey());
                var.put("description", prop.getValue().getDescription());
                
                // Map OpenAPI types to Java types
                Schema propSchema = prop.getValue();
                String type = propSchema.getType();
                String format = propSchema.getFormat();
                String dataType;
                
                if ("integer".equals(type)) {
                    if ("int64".equals(format)) {
                        dataType = "Long";
                    } else {
                        dataType = "Integer";
                    }
                } else if ("number".equals(type)) {
                    if ("float".equals(format)) {
                        dataType = "Float";
                    } else {
                        dataType = "Double";
                    }
                } else if ("boolean".equals(type)) {
                    dataType = "Boolean";
                } else if ("array".equals(type)) {
                    // Handle array types properly by looking at the item type
                    Schema itemsSchema = propSchema.getItems();
                    if (itemsSchema != null) {
                        String itemType = itemsSchema.getType();
                        if ("integer".equals(itemType)) {
                            if ("int64".equals(itemsSchema.getFormat())) {
                                dataType = "List<Long>";
                            } else {
                                dataType = "List<Integer>";
                            }
                        } else if ("number".equals(itemType)) {
                            if ("float".equals(itemsSchema.getFormat())) {
                                dataType = "List<Float>";
                            } else {
                                dataType = "List<Double>";
                            }
                        } else if ("boolean".equals(itemType)) {
                            dataType = "List<Boolean>";
                        } else if (itemsSchema.get$ref() != null) {
                            // Handle references to other schemas in array items
                            String refType = getRefType(itemsSchema.get$ref());
                            dataType = "List<" + refType + ">";
                        } else {
                            dataType = "List<String>";
                        }
                        imports.add("java.util.List");
                    } else {
                        dataType = "List<Object>";
                        imports.add("java.util.List");
                    }
                } else if (propSchema.get$ref() != null) {
                    // Handle references to other schemas
                    dataType = getRefType(propSchema.get$ref());
                } else if ("string".equals(type) && "date".equals(format)) {
                    // Handle date format
                    dataType = "LocalDate";
                    imports.add("java.time.LocalDate");
                } else if ("string".equals(type) && "date-time".equals(format)) {
                    // Handle date-time format
                    dataType = "LocalDateTime";
                    imports.add("java.time.LocalDateTime");
                } else {
                    dataType = "String";
                }
                
                var.put("datatypeWithEnum", dataType);
                vars.add(var);
            }
        }
        
        model.put("vars", vars);
        
        // Add imports
        List<Map<String, String>> importsList = new ArrayList<>();
        for (String importItem : imports) {
            Map<String, String> importMap = new HashMap<>();
            importMap.put("import", importItem);
            importsList.add(importMap);
        }
        model.put("imports", importsList);
        
        return model;
    }
    
    /**
     * Extracts the type name from a reference
     * 
     * @param ref the reference string (e.g., "#/components/schemas/SomeType")
     * @return the type name (e.g., "SomeType")
     */
    private static String getRefType(String ref) {
        if (ref != null && ref.startsWith("#/components/schemas/")) {
            return ref.substring("#/components/schemas/".length());
        }
        return "Object";
    }
    
    /**
     * Generates service classes from the YAML specification
     * 
     * @throws IOException if an error occurs
     */
    private static void generateServicesFromYaml() throws IOException {
        System.out.println("Generating service classes from YAML specification...");
        
        // Parse the OpenAPI specification from YAML
        OpenAPI openAPI = new OpenAPIV3Parser().read(YAML_FILE_PATH);
        
        // Create the service output directory if it doesn't exist
        File serviceOutputDir = new File(SERVICE_OUTPUT_DIR);
        if (!serviceOutputDir.exists()) {
            serviceOutputDir.mkdirs();
        }
        
        // Load the Mustache template for services
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache serviceMustache = mf.compile(SERVICE_TEMPLATE_FILE_PATH);
        
        // Get the paths from the OpenAPI specification
        Map<String, PathItem> paths = openAPI.getPaths();
        
        // Group operations by tag to create service classes
        Map<String, List<Map<String, Object>>> serviceOperations = new HashMap<>();
        
        // Process each path
        for (Map.Entry<String, PathItem> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            
            // Process GET operation
            if (pathItem.getGet() != null) {
                processOperation(pathItem.getGet(), path, "GET", serviceOperations, openAPI);
            }
            
            // Process POST operation
            if (pathItem.getPost() != null) {
                processOperation(pathItem.getPost(), path, "POST", serviceOperations, openAPI);
            }
            
            // Process PUT operation
            if (pathItem.getPut() != null) {
                processOperation(pathItem.getPut(), path, "PUT", serviceOperations, openAPI);
            }
            
            // Process DELETE operation
            if (pathItem.getDelete() != null) {
                processOperation(pathItem.getDelete(), path, "DELETE", serviceOperations, openAPI);
            }
        }
        
        // Generate a service class for each tag
        for (Map.Entry<String, List<Map<String, Object>>> entry : serviceOperations.entrySet()) {
            // Extract the first part of the tag name if it contains special syntax or spaces
            String tagName = entry.getKey();
            String firstPart = tagName;
            StringBuilder pascalCase = new StringBuilder();

            // Check if the tag name contains special syntax or spaces
            if (tagName.contains("-") || tagName.contains(" ")) {
                String[] parts = tagName.split("[-\\s]+");
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        pascalCase.append(
                                part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase()
                        );
                    }
                }
                 firstPart = pascalCase.toString();
//                // Extract the first part (before the first special character or space)
//                firstPart = tagName.split("[-\\s]")[0];
            }
            
            // Capitalize the first letter
            if (firstPart.length() > 0) {
                firstPart = firstPart.substring(0, 1).toUpperCase() + firstPart.substring(1);
            }
            
            String className = firstPart + "Service";
            List<Map<String, Object>> operations = entry.getValue();
            
            // Create a model for the Mustache template
            Map<String, Object> model = new HashMap<>();
            model.put("classname", firstPart);
            model.put("operations", operations);
            
            // Extract host and base path from the server URL
            String url = openAPI.getServers().get(0).getUrl();
            String host = url;
            String basePath = "";
            if (url.contains("/")) {
                int index = url.indexOf("/", url.indexOf("//") + 2);
                if (index != -1) {
                    host = url.substring(0, index);
                    basePath = url.substring(index);
                }
            }
            
            model.put("host", host);
            model.put("basePath", basePath);
            
            // Generate the Java code using the Mustache template
            StringWriter writer = new StringWriter();
            serviceMustache.execute(writer, model).flush();
            String javaCode = writer.toString();
            
            // Write the Java code to a file
            String filePath = SERVICE_OUTPUT_DIR + "/" + className + ".java";
            File file = new File(filePath);
            
            // Check if the file already exists
            if (file.exists()) {
                System.out.println("Updating existing service class: " + className);
            } else {
                System.out.println("Creating new service class: " + className);
            }
            
            FileUtils.writeStringToFile(file, javaCode, "UTF-8");
        }
    }
    
    
    /**
     * Generates the BasePathURLs class from the YAML specification
     * 
     * @throws IOException if an error occurs
     */
    private static void generateBasePathURLsFromYaml() throws IOException {
        System.out.println("Generating BasePathURLs constants from YAML specification...");
        
        // Parse the OpenAPI specification from YAML
        OpenAPI openAPI = new OpenAPIV3Parser().read(YAML_FILE_PATH);
        
        // Create the constants output directory if it doesn't exist
        File constantsOutputDir = new File(CONSTANTS_OUTPUT_DIR);
        if (!constantsOutputDir.exists()) {
            constantsOutputDir.mkdirs();
        }
        
        // Load the Mustache template for BasePathURLs
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache basePathUrlsMustache = mf.compile(BASE_PATH_URLS_TEMPLATE_FILE_PATH);
        
        // Get the servers from the OpenAPI specification
        List<io.swagger.v3.oas.models.servers.Server> servers = openAPI.getServers();
        
        // Create a list to hold server information for the template
        List<Map<String, String>> serversList = new ArrayList<>();
        
        // Process each server
        for (int i = 0; i < servers.size(); i++) {
            io.swagger.v3.oas.models.servers.Server server = servers.get(i);
            String url = server.getUrl();
            
            // Extract the base path part from the URL
            String basePath = "";
            if (url.contains("/")) {
                int index = url.indexOf("/", url.indexOf("//") + 2);
                if (index != -1) {
                    basePath = url.substring(index);
                }
            }
            
            // Create a map for the server
            Map<String, String> serverMap = new HashMap<>();
            
            // Generate a constant name for the server
            String constantName = "DEFAULT";
            if (i > 0) {
                constantName = "SERVER_" + i;
            }
            
            // Add the server information to the map
            serverMap.put("constantName", constantName);
            serverMap.put("basePath", basePath);
            serverMap.put("description", server.getDescription() != null ? server.getDescription() : "Default server");
            
            // Add the map to the list
            serversList.add(serverMap);
        }
        
        // Create a model for the Mustache template
        Map<String, Object> model = new HashMap<>();
        model.put("servers", serversList);
        
        // Generate the Java code using the Mustache template
        StringWriter writer = new StringWriter();
        basePathUrlsMustache.execute(writer, model).flush();
        String javaCode = writer.toString();
        
        // Write the Java code to a file
        String filePath = CONSTANTS_OUTPUT_DIR + "/BasePathURLs.java";
        File file = new File(filePath);
        
        // Check if the file already exists
        if (file.exists()) {
            System.out.println("Updating existing BasePathURLs class");
        } else {
            System.out.println("Creating new BasePathURLs class");
        }
        
        FileUtils.writeStringToFile(file, javaCode, "UTF-8");
    }
    
    /**
     * Generates the RelativeURLs class from the YAML specification
     * 
     * @throws IOException if an error occurs
     */
    private static void generateRelativeURLsFromYaml() throws IOException {
        System.out.println("Generating RelativeURLs constants from YAML specification...");
        
        // Parse the OpenAPI specification from YAML
        OpenAPI openAPI = new OpenAPIV3Parser().read(YAML_FILE_PATH);
        
        // Create the constants output directory if it doesn't exist
        File constantsOutputDir = new File(CONSTANTS_OUTPUT_DIR);
        if (!constantsOutputDir.exists()) {
            constantsOutputDir.mkdirs();
        }
        
        // Load the Mustache template for RelativeURLs
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache relativeUrlsMustache = mf.compile(RELATIVE_URLS_TEMPLATE_FILE_PATH);
        
        // Get the paths from the OpenAPI specification
        Map<String, PathItem> paths = openAPI.getPaths();
        
        // Create a list to hold path information for the template
        List<Map<String, String>> pathsList = new ArrayList<>();
        
        // Process each path
        for (Map.Entry<String, PathItem> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();
            
            // Create a map for the path
            Map<String, String> pathMap = new HashMap<>();
            
            // Generate a constant name for the path
            String constantName = generateConstantName(path);
            
            // Get a description for the path
            String description = getPathDescription(path, pathItem);
            
            // Add the path information to the map
            pathMap.put("constantName", constantName);
            pathMap.put("path", path);
            pathMap.put("description", description);
            
            // Add the map to the list
            pathsList.add(pathMap);
        }
        
        // Create a model for the Mustache template
        Map<String, Object> model = new HashMap<>();
        model.put("paths", pathsList);
        
        // Generate the Java code using the Mustache template
        StringWriter writer = new StringWriter();
        relativeUrlsMustache.execute(writer, model).flush();
        String javaCode = writer.toString();
        
        // Write the Java code to a file
        String filePath = CONSTANTS_OUTPUT_DIR + "/RelativeURLs.java";
        File file = new File(filePath);
        
        // Check if the file already exists
        if (file.exists()) {
            System.out.println("Updating existing RelativeURLs class");
        } else {
            System.out.println("Creating new RelativeURLs class");
        }
        
        FileUtils.writeStringToFile(file, javaCode, "UTF-8");
    }
    
    /**
     * Generates a constant name for a path
     * 
     * @param path the path
     * @return a constant name for the path
     */
    private static String generateConstantName(String path) {
        // Remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // Replace path parameters with descriptive names
        path = path.replaceAll("\\{([^}]+)\\}", "_BY_$1");
        
        // Replace slashes with underscores
        path = path.replace("/", "_");
        
        // Replace hyphens with underscores
        path = path.replace("-", "_");
        
        // Convert to uppercase
        path = path.toUpperCase();
        
        return path;
    }
    
    /**
     * Gets a description for a path
     * 
     * @param path the path
     * @param pathItem the path item
     * @return a description for the path
     */
    private static String getPathDescription(String path, PathItem pathItem) {
        // Try to get a description from the operations
        if (pathItem.getGet() != null && pathItem.getGet().getSummary() != null) {
            return pathItem.getGet().getSummary();
        } else if (pathItem.getPost() != null && pathItem.getPost().getSummary() != null) {
            return pathItem.getPost().getSummary();
        } else if (pathItem.getPut() != null && pathItem.getPut().getSummary() != null) {
            return pathItem.getPut().getSummary();
        } else if (pathItem.getDelete() != null && pathItem.getDelete().getSummary() != null) {
            return pathItem.getDelete().getSummary();
        }
        
        // If no description is found, use the path itself
        return "Path: " + path;
    }

    /**
     * Process an operation and add it to the appropriate service
     * 
     * @param operation the operation to process
     * @param path the path of the operation
     * @param httpMethod the HTTP method of the operation
     * @param serviceOperations map of service operations grouped by tag
     * @param openAPI the OpenAPI specification
     */
    private static void processOperation(Operation operation, String path, String httpMethod, 
                                        Map<String, List<Map<String, Object>>> serviceOperations,
                                        OpenAPI openAPI) {
        // Get the tag for the operation (use the first tag or default to "Api")
        String tag = operation.getTags() != null && !operation.getTags().isEmpty() 
                    ? operation.getTags().get(0) : "Api";
        
        // Create a map for the operation
        Map<String, Object> operationMap = new HashMap<>();
        operationMap.put("operationId", operation.getOperationId());
        operationMap.put("summary", operation.getSummary());
        operationMap.put("path", path);
        operationMap.put("httpMethod", httpMethod);
        
        // Add the constant name for the path
        operationMap.put("pathConstant", generateConstantName(path));
        
        // Extract path parameters from the URL path
        List<Map<String, String>> pathParams = new ArrayList<>();
        if (path.contains("{") && path.contains("}")) {
            String[] segments = path.split("/");
            for (String segment : segments) {
                if (segment.startsWith("{") && segment.endsWith("}")) {
                    String paramName = segment.substring(1, segment.length() - 1);
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("name",paramName);
                    paramMap.put("replaceName", "{" + paramName + "}");
                    pathParams.add(paramMap);
                }
            }
            operationMap.put("hasPathParams", !pathParams.isEmpty());
            operationMap.put("pathParams", pathParams);
        } else {
            operationMap.put("hasPathParams", false);
        }
        
        // Extract query parameters from the operation parameters
        List<Map<String, String>> queryParams = new ArrayList<>();
        if (operation.getParameters() != null) {
            for (io.swagger.v3.oas.models.parameters.Parameter parameter : operation.getParameters()) {
                if ("query".equals(parameter.getIn())) {
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("name", parameter.getName());
                    paramMap.put("description", parameter.getDescription());
                    paramMap.put("required", String.valueOf(parameter.getRequired() != null && parameter.getRequired()));
                    queryParams.add(paramMap);
                }
            }
            operationMap.put("hasQueryParams", !queryParams.isEmpty());
            operationMap.put("queryParams", queryParams);
        } else {
            operationMap.put("hasQueryParams", false);
        }
        
        // Check if the operation has a request body
        boolean hasRequestBody = operation.getRequestBody() != null;
        operationMap.put("hasRequestBody", hasRequestBody);
        
        if (hasRequestBody) {
            // Get the request body schema reference
            RequestBody requestBody = operation.getRequestBody();
            Content content = requestBody.getContent();
            if (content.get("application/json") != null) {
                MediaType mediaType = content.get("application/json");
                Schema schema = mediaType.getSchema();
                
                // Check if the schema is an array
                if ("array".equals(schema.getType())) {
                    // Get the item type from the array schema
                    Schema itemsSchema = schema.getItems();
                    if (itemsSchema != null && itemsSchema.get$ref() != null) {
                        String itemType = getRefType(itemsSchema.get$ref());
                        operationMap.put("requestBodyType", itemType);
                        operationMap.put("isArrayRequestBody", true);
                    }
                } else if (schema.get$ref() != null) {
                    // Handle regular object reference
                    String requestBodyType = getRefType(schema.get$ref());
                    operationMap.put("requestBodyType", requestBodyType);
                    operationMap.put("isArrayRequestBody", false);
                }
            }
        }
        
        // Add the operation to the appropriate service
        if (!serviceOperations.containsKey(tag)) {
            serviceOperations.put(tag, new ArrayList<>());
        }
        serviceOperations.get(tag).add(operationMap);
    }
}