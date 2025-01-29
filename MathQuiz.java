import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


//********************************************************************
//
//  Developer:     Grecia Bueno
//
//  Program #:     Seven
//
//  File Name:     MathQuiz.java
//
//  Course:        COSC 4301 Modern Programming
//
//  Due Date:      12/11/2024
//
//  Instructor:    Prof. Fred Kumi
//
//  Java Version:  <17.0.2.>
//
//  Description:   The MathQuiz class is an interactive program that lets
//                  the users pick different math difficulty levels to solve arithmetic
//                  expressions.  It generates math questions with random numbers
//                  and operators, collects the answers from the user, and provides feedback
//                  randomly on whether the answers are correct or incorrect.  At the 
//                  end, the program summarizes the user's performance for each level and 
//                  suggests seeking extra help if the accuracy in the basic level is below 80%
//
//********************************************************************
public class MathQuiz {
    private final SecureRandom random = new SecureRandom();
    private final String[] operators = {"*", "%", "+", "-"};
    private Scanner scanner;
    private List<String> correctResponses;
    private List<String> incorrectResponses;
    private int correctCount = 0;  
    private int difficulty = 1; 
    private boolean continueQuiz = true; 
    private int[] totalQuestionsAtEachLevel = new int[3]; 
    private int[] correctAnswersAtEachLevel = new int[3];
    private PrintWriter logger;
    BufferedWriter writer;


    // ***************************************************************
    //
    // Method: MathQuiz
    //
    // Description: This constructor receives three parameters, which are the 
    //              files to be used.  Two are to read from and one is to 
    //              write to.  The one that it writes to is used using PrintWriter
    //
    // Parameters: String correctFile, String incorrectFile, String outputFile
    //
    // Returns: N/A
    //
    // **************************************************************
    public MathQuiz(String correctFile, String incorrectFile, String outputFile) {
        scanner = new Scanner(System.in);
        correctResponses = loadFileContents(correctFile);
        incorrectResponses = loadFileContents(incorrectFile);
        initializeLogger(outputFile);

    }


    // ***************************************************************
    //
    // Method: initializeLogger
    //
    // Description: This method is in charge of configuring the logger to write log
    //              messages to a specified file, in parameter.  It uses the 
    //              PrintWriter to ensure that new log entries are added to the 
    //              file.  It uses flush to make sure the data is saved immediately. 
    //              It is surrounded with try and catch in case there is an error 
    //              accessing the file. 
    //
    // Parameters: String filename
    //
    // Returns: N/A
    //
    // **************************************************************
    private void initializeLogger(String filename) {
        try {
            // Ensure the file path is correct and the file is accessible
            logger = new PrintWriter(new FileWriter(filename, true)); // Append mode set to true
            logger.println("Logger initialized successfully.");
            logger.flush(); // Immediately flush to confirm file writing is operational
        } catch (IOException e) {
            System.err.println("Failed to initialize logging to file: " + e.getMessage());
            logger = null;  // Set logger to null to prevent NullPointerExceptions on attempted use
        }
    }
    

    // ***************************************************************
    //
    // Method: loadFileContents
    //
    // Description: This method is used to read the contents from the 
    //              correct.txt and incorrect.txt and stores each line as a string
    //              in a List.  It uses Files.readAllLines to do the reading
    //
    // Parameters: String filename
    //
    // Returns: List<String>
    //
    // **************************************************************
    private List<String> loadFileContents(String filename) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + ". " + e.getMessage());
            return null;
        }
        return lines;
    }


    // ***************************************************************
    //
    // Method: runQuiz
    //
    // Description: The overall execution of the program is controlled by
    //              this method.  It starts by requiring the user to select 
    //              the first difficulty level before proceeding.  The user
    //              must answer questions until they meet the required number 
    //              of correct answers for the current level.  If they pass that
    //              level, they decide whether to stay at the same level, move to
    //              a higher level, or exit the program.  All the interactions are
    //              recorded in a log file.  The program ends when the user chooses
    //              to enter exit
    //
    // Parameters: N/A
    //
    // Returns: N/A
    //
    // **************************************************************
    public void runQuiz() {
        int choice1 = 0;
        System.out.println("Choose from the following options: ");
        logger.println("Choose from the following options: ");
        System.out.println("1. Basic - 2 Operands and 1 Operator");
        logger.println("1. Basic - 2 Operands and 1 Operator");
        System.out.println("2. Intermediate - 3 Operands and 2 Operators");
        logger.println("2. Intermediate - 3 Operands and 2 Operators");
        System.out.println("3. Advanced - 4 Operands and 3 Operators");
        logger.println("3. Advanced - 4 Operands and 3 Operators");
        System.out.println("All students MUST start at level 1. Enter your level: ");
        logger.println("All students MUST start at level 1. Enter your level: ");
        logger.flush();
    
        // Ensure the user starts at level 1
        while (choice1 != 1) {
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                logger.println(input);
                if (input == 1) {
                    choice1 = 1;
                } else {
                    System.out.print("Please enter the first level: ");
                }
            } else {
                scanner.next();  // Consume non-integer input
                System.out.println("Please enter a valid integer.");
            }
        }
    
        int requiredCorrectAnswers = 5; // Start with needing 5 correct answers
    
        while (continueQuiz) {
            correctCount = 0;  // Reset the counter for each new difficulty level challenge
            while (correctCount < requiredCorrectAnswers) {
                if (askQuestion(difficulty)) {
                    correctCount++;
                }
            }
    
            System.out.printf("You've solved %d questions at level %d correctly.\n", requiredCorrectAnswers, difficulty);
            System.out.println("Enter '1' to stay at the current level, '2' to go one level up, or 'exit' to leave the program:");
            String decision = scanner.next().toLowerCase();
    
            switch (decision) {
                case "exit":
                    System.out.println("Exiting the program. Well done!");
                    continueQuiz = false;
                    logger.println("User chose to exit the program.");
                    displayResultsAndExit();
                    break;
                case "2":
                    if (difficulty < 3) { // Only increment if not already at the highest level
                        difficulty++;
                        requiredCorrectAnswers = 5; 
                        logger.println("User chose to increase difficulty to level: " + difficulty);
                    } else {
                        System.out.println("You are already at the highest level. You cannot go any higher.");
                        
                        boolean awaitingExitCommand = true; // Flag to control the loop
                        while (awaitingExitCommand) {
                            System.out.println("Type 'exit' to end the program:");
                            String exitCommand = scanner.next().toLowerCase();
                            if ("exit".equals(exitCommand)) {
                                System.out.println("Exiting the program. Well done!");
                                continueQuiz = false; // This will exit the while loop and end the quiz
                                awaitingExitCommand = false;
                                logger.println("User chose to exit the program.");
                                displayResultsAndExit(); 
                            } else {
                                System.out.println("Please type 'exit' to end the program.");
                            }
                        }
                    }
                    break;
                
                case "1": // Stay at the current level
                    requiredCorrectAnswers = 1; 
                    logger.println("User chose to stay at the current difficulty level: " + difficulty);
                    break;
                default:
                    System.out.println("Invalid input, please choose '1', '2', or 'exit'. Continuing at current level.");
                    requiredCorrectAnswers = 1; // Continue prompting after every correct answer
                    break;
            }
            logger.flush();
        }
    }
    


    // ***************************************************************
    //
    // Method: askQuestion
    //
    // Description: This method is what generates the questions based on 
    //              the current difficulty level by generating random operands and 
    //              operators.  After generating the question, it displays the question
    //              to the user, calculates the correct answer, and evaluates the user's
    //              input.  If the answer is correct, it provides positive feedback selected
    //              randomly from a file and increments the correct answer count.  If it 
    //              is incorrect, it provides negative feedback chosen randomly from another
    //              file.  The same question will be asked over and over again until the 
    //              user gets it right
    //
    // Parameters: int difficulty
    //
    // Returns: boolean
    //
    // **************************************************************
    private boolean askQuestion(int difficulty) {
        int operandsNeeded = difficulty + 1;
        int[] operands = generateOperands(operandsNeeded);
        String[] chosenOperators = selectOperators(difficulty);

        String question = buildQuestion(operands, chosenOperators);
        int correctAnswer = calculateCorrectAnswer(operands, chosenOperators);
        boolean answeredCorrectly = false;

        while (!answeredCorrectly) {
            logger.println("Question asked: " + question); 
            System.out.println("Solve the following expression: " + question);
            System.out.print("Your answer: ");
            int userAnswer = getAnyIntInput();

            if (userAnswer == correctAnswer) {
                provideFeedback(true);
                answeredCorrectly = true;
                correctAnswersAtEachLevel[difficulty - 1]++;
                logger.println("User answered correctly: " + userAnswer);
                System.out.println("Answers correct: " + correctAnswersAtEachLevel[difficulty - 1]);
            } else {
                provideFeedback(false);
                logger.println("User answered incorrectly: " + userAnswer + " (Correct was: " + correctAnswer + ")");
                System.out.println("Answered incorrectly");
            }
            totalQuestionsAtEachLevel[difficulty - 1]++;
        }
        System.out.println("Total question so far: " + totalQuestionsAtEachLevel[difficulty - 1]);
        logger.flush();  // Ensure it writes to the file immediately
        return answeredCorrectly;
    }


    // ***************************************************************
    //
    // Method: displayResultsAndExit
    //
    // Description: This method calculates the percentage of correct answers
    //              based on the total questions asked and displays the 
    //              results on the console while also logging them to a file.  
    //              If the user's accuracy in th basic level is below 80% then 
    //              it prints a message saying that he/she should seek extra help
    //              from their professor
    //
    // Parameters: N/A
    //
    // Returns: N/A
    //
    // **************************************************************
    private void displayResultsAndExit() {
        for (int i = 0; i < totalQuestionsAtEachLevel.length; i++) {
            if (totalQuestionsAtEachLevel[i] > 0) { // To avoid division by zero
                double percentage = 100.0 * correctAnswersAtEachLevel[i] / totalQuestionsAtEachLevel[i];
                String resultMessage = String.format("Level %d: Correct Answers: %.2f%%", i + 1, percentage);
                System.out.println(resultMessage);  // Print to console
                logger.println(resultMessage);      // Log the same message to file
    
                if (i == 0 && percentage < 80.0) {
                    System.out.println("Please ask your teacher for extra help.");
                    logger.println("Please ask your teacher for extra help.");  // Log this advice to file
                }
            }
        }
        continueQuiz = false;
        System.out.println("Exiting the program. Well done!");
        logger.println("Exiting the program. Well done!");  // Log the exit message
        logger.flush();  // Ensure all data is written to the file before closing
        closeScanner();
    }
    
    


    // ***************************************************************
    //
    // Method: getAnyIntInput
    //
    // Description: This method calls the getIntInput method with any
    //               valid integers as a parameter
    //
    // Parameters: N/A
    //
    // Returns: int
    //
    // **************************************************************
    private int getAnyIntInput() {
        return getIntInput(Integer.MIN_VALUE, Integer.MAX_VALUE); 
    }


    // ***************************************************************
    //
    // Method: getIntInput
    //
    // Description: This method collects an integer input from the user. 
    //              It will check if it falls whithin the given range.  
    //              If it is not a valid integer then it will keep prompting
    //              the user until a valid integer is entered
    //
    // Parameters: int min, int max
    //
    // Returns: int
    //
    // **************************************************************
    private int getIntInput(int min, int max) {
        int input = 0;
        boolean isValid = false;
        while (!isValid) {
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();  // Read the input
                if (input >= min && input <= max) {
                    isValid = true;
                    logger.println("User input accepted: " + input);  // Log valid input
                } else {
                    logger.println("Invalid input (out of range): " + input);  // Log out of range input
                    System.out.printf("Please enter a number between %d and %d: ", min, max);
                }
            } else {
                logger.println("Invalid input (not an integer): " + scanner.next());  // Log non-integer input
                System.out.println("Please enter a valid integer.");
            }
            logger.flush();  // Ensure all logs are written to the file
        }
        return input;
    }
    
    

    // ***************************************************************
    //
    // Method: generateOperands
    //
    // Description: This method creates an array of random integers for use 
    //              when generating the math question.  It will take the number
    //              of operands as a parameter and generagets each operand as a random 
    //              number between 0 and 9.  This will be the numbers used for the 
    //              calculations
    //
    // Parameters: int numberOfOperands
    //
    // Returns: int[]
    //
    // **************************************************************
    private int[] generateOperands(int numberOfOperands) {
        int[] operands = new int[numberOfOperands];
        for (int i = 0; i < numberOfOperands; i++) {
            operands[i] = random.nextInt(10); // Generates a number from 0 to 9
        }
        return operands;
    }


    // ***************************************************************
    //
    // Method: selectOperators
    //
    // Description: This method creates an array of random math operators
    //              such as +, -, *, and %.  It picks a random operator for each
    //              position in the array and fills it up based on the number
    //              of operators needed.  It returns the array with the selected
    //              operators
    //
    // Parameters: int numberOfOperators
    //
    // Returns: String[]
    //
    // **************************************************************
    private String[] selectOperators(int numberOfOperators) {
        String[] operatorsArray = new String[numberOfOperators];
        for (int i = 0; i < numberOfOperators; i++) {
            int operatorIndex = random.nextInt(operators.length);
            operatorsArray[i] = operators[operatorIndex];
        }
        return operatorsArray;
    }


    // ***************************************************************
    //
    // Method: buildQuestion
    //
    // Description: This method brings the operands and operators together
    //              and uses StringBuilder to create the math question.  
    //              It loops through the operators,appending each operator and the 
    //              corresponding operand to 'question' StringBuilder
    //
    // Parameters: int[] operands, String[] operators
    //
    // Returns: String
    //
    // **************************************************************
    private String buildQuestion(int[] operands, String[] operators) {
        StringBuilder question = new StringBuilder();
        question.append(operands[0]);
        for (int i = 0; i < operators.length; i++) {
            question.append(" ").append(operators[i]).append(" ").append(operands[i + 1]);
        }
        return question.toString();
    }


    // ***************************************************************
    //
    // Method: calculateCorrectAnswer
    //
    // Description: Here the operands and operators are passed as arrays as parameters
    //              and its calculation is operated.  It uses a helper method 
    //              'applyOperator' to pass the result, the operand, and the operator. 
    //              It returns the result as an int.  Converts operands and operators 
    //              to lists for dynamic manipulation Handles high-precedence operators (* and %)
    //
    // Parameters: int[] operands, String[] operators
    //
    // Returns: int
    //
    // **************************************************************
    private int calculateCorrectAnswer(int[] operands, String[] operators) {
        List<Integer> operandList = new ArrayList<>();
        List<String> operatorList = new ArrayList<>();
        for (int operand : operands) {
            operandList.add(operand);
        }
        for (String operator : operators) {
            operatorList.add(operator);
        }
    
        int i = 0; 
        while (i < operatorList.size()) {
            String operator = operatorList.get(i);
            if (operator.equals("*") || operator.equals("%")) {
                int leftOperand = operandList.get(i);
                int rightOperand = operandList.get(i + 1);
                int result = applyOperator(leftOperand, rightOperand, operator);
    
                operandList.set(i, result);
                operandList.remove(i + 1); 
                operatorList.remove(i);  
            } else {
                i++; 
            }
        }

        int result = operandList.get(0);
        for (i = 0; i < operatorList.size(); i++) {
            result = applyOperator(result, operandList.get(i + 1), operatorList.get(i));
        }
    
        return result;
    }
    
    


    // ***************************************************************
    //
    // Method: provideFeedback
    //
    // Description: This method is what delivers the feedback to the user.  
    //              If they answered correct, it picks randomly from the correct.txt
    //              file and shows it to the screen as well as logging it into a file. 
    //              If the answer is incorrect, it reads a random response from incorrect.txt 
    //              and print it to the screen and also logs it
    //
    // Parameters: boolean isCorrect
    //
    // Returns: int
    //
    // **************************************************************
    private void provideFeedback(boolean isCorrect) {
        Random rand = new Random();
        if (isCorrect) {
            int index = rand.nextInt(correctResponses.size());
            String correctFeedback = correctResponses.get(index); // Get random correct feedback
            System.out.println(correctFeedback); // Print correct feedback to console
            logger.println("Feedback: " + correctFeedback); // Log correct feedback to file
        } else {
            int index = rand.nextInt(incorrectResponses.size());
            String incorrectFeedback = incorrectResponses.get(index); // Get random incorrect feedback
            System.out.println(incorrectFeedback); // Print incorrect feedback to console
            logger.println("Feedback: " + incorrectFeedback); // Log incorrect feedback to file
        }
        logger.flush(); // Ensure the feedback is immediately written to the log
    }
    


    // ***************************************************************
    //
    // Method: applyOperator
    //
    // Description: This method calculates the result using 2 numbers and a given math
    //              operator.  It uses a switch statement and the operator passed as 
    //              a parameter and checks whether the calculation will need to be 
    //              addition, subtraction, multiplication or modulus.  In case the operator
    //              is modulus and the second number is 0, it changes it to 1 to avoid 
    //              dividing by 0.  
    //
    // Parameters: int operand1, int operand2, String operator
    //
    // Returns: int
    //
    // **************************************************************
    private int applyOperator(int operand1, int operand2, String operator) {
        switch (operator) {
            case "*":
                return operand1 * operand2;
            case "%":
                if (operand2 == 0) {
                    System.out.println("Warning: Modulus by zero encountered. Adjusting divisor to 1.");
                    operand2 = 1; // Adjust to avoid exception
                }
                return operand1 % operand2;
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            default:
                return 0;
        }
    }


    // ***************************************************************
    //
    // Method: closeScanner
    //
    // Description: Here the scanner and logger is closed
    //
    // Parameters: N/A
    //
    // Returns: N/A
    //
    // **************************************************************
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }

        if (logger != null) {
            logger.close(); 
        }
    }
}
