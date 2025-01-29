import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


//********************************************************************
//
//  Developer:     Grecia Bueno
//
//  Program #:     Seven
//
//  File Name:     Program7.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      12/11/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Java Version:  <17.0.2.>
//
//  Description:   This is the starting point for the whole program.  This 
//                 class checks that the correct.txt and incorrect.txt files are
//                  present and contain data before the program starts.  The 
//                  fileExistsAndNotEmpty method checks if those files exist and 
//                  are not empty, handling any errors during the process.  If that 
//                  method returns true, then MathQuiz object is created
//
//********************************************************************
public class Program7 {

    // ***************************************************************
    //
    // Method: main
    //
    // Description: The main method of the program
    //
    // Parameters: String array
    //
    // Returns: N/A
    //
    // **************************************************************
    public static void main(String[] args) {
        Program7 program = new Program7();
        program.startQuiz();
    }

    // ***************************************************************
    //
    // Method: startQuiz
    //
    // Description: This method calls the fileExistsAndNotEmpty method to verify
    //              that the files correct.txt and incorrect.txt exist.  If the 
    //              helper method returns false then the program won't run, if both
    //              return true then the program will create a MathQuiz method
    //
    // Parameters: N/A
    //
    // Returns: N/A
    //
    // **************************************************************
    private void startQuiz() {
        // Check for the existence and content of both correct.txt and incorrect.txt
        if (!fileExistsAndNotEmpty("correct.txt")) {
            System.out.println("Error: 'correct.txt' does not exist or is empty. Please check the file and try again.");
            return;
        }
        if (!fileExistsAndNotEmpty("incorrect.txt")) {
            System.out.println("Error: 'incorrect.txt' does not exist or is empty. Please check the file and try again.");
            return;
        }

        // If both files are fine, proceed with the quiz
        MathQuiz quiz = new MathQuiz("correct.txt", "incorrect.txt", "Program7-Output.txt");
        try {
            quiz.runQuiz();
        } finally {
            quiz.closeScanner();
        }
    }


    // ***************************************************************
    //
    // Method: fileExistsAndNotEmpty
    //
    // Description: This file checks if the file passed through the parameter
    //              exists, if it does then it makes sure that it's not empty and
    //              that the lines can be read.  It is surrounded in a try and catch
    //              block to catch any errors that might arise
    //
    // Parameters: String fileName
    //
    // Returns: boolean
    //
    // **************************************************************
    private boolean fileExistsAndNotEmpty(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        // Check if the file is not empty
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            if (br.readLine() == null) {
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while checking file content: " + e.getMessage());
            return false;
        }
        return true;
    }
}
