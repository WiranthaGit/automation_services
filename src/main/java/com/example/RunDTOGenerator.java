package com.example;

/**
 * Simple class to run the DTOGenerator
 */
public class RunDTOGenerator {
    public static void main(String[] args) {
        System.out.println("Running DTOGenerator...");
        try {
            DTOGenerator.main(args);
            System.out.println("DTOGenerator completed successfully!");
        } catch (Exception e) {
            System.err.println("Error running DTOGenerator: " + e.getMessage());
            e.printStackTrace();
        }
    }
}