package example.demo;



/*
Mohamed Mustafa
5/1/2024
Comp 167 section 3
description: The code creates a scientific calculator interface using JavaFX. It has input and output fields for
numbers and results. Users interact with buttons for math operations like addition and functions.
Clicking a button adds its value to the input or performs an action like clearing. The "=" button calculates expressions
using parsing, handling math operations and functions. Results appear in the output field, with whole numbers shown
clearly. The calculator remembers the last result, letting users continue calculations seamlessly.
 */




import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

// Package declaration
public class Calculating extends Application {

    // Fields declaration
    private TextField inputField;
    private TextField outputField;
    private double previousResult = 0;

    @Override
    public void start(Stage primaryStage) {
        // Setting up the primary stage
        primaryStage.setTitle("Calculator");

        // Creating a BorderPane for layout
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #cad8e4;");

        // Creating input field
        inputField = new TextField();
        inputField.setEditable(false);
        inputField.setPrefColumnCount(20);
        borderPane.setTop(inputField);

        // Creating output field
        outputField = new TextField();
        outputField.setEditable(false);
        outputField.setPrefColumnCount(20);
        borderPane.setCenter(outputField);

        // Creating GridPane for buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setPadding(new Insets(20, 5, 20, 5));
        buttonGrid.setHgap(12);
        buttonGrid.setVgap(12);

        // Array of button labels
        String[] buttons = {
                "7", "8", "9", "/", "C",
                "4", "5", "6", "*", "^2",
                "1", "2", "3", "-", "^3",
                "0", ".", "+", "=","sqrt", "sin",
                "cos", "tan", "log", "ln", "asin",
                "acos", "atan", "abs", "mod", "sinh",
                "cosh", "tanh", "1/", "EXIT"
        };

        // Adding buttons to the GridPane
        int row = 1;
        int col = 0;
        for (String buttonLabel : buttons) {
            Button button = new Button(buttonLabel);
            if (buttonLabel.equals("C") || buttonLabel.equals("=") || buttonLabel.equals("EXIT")) {
                button.setPrefSize(95, 55);
                button.setStyle("-fx-background-color: #716e6e; -fx-text-fill: white;");
            } else {
                button.setPrefSize(95, 55);
                button.setStyle("-fx-background-color: #292725; -fx-text-fill: white;");
            }
            button.setOnAction(e -> handleButtonClick(button.getText()));
            buttonGrid.add(button, col, row);
            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        // Adding buttonGrid to the bottom of the BorderPane
        borderPane.setBottom(buttonGrid);

        // Creating the scene
        Scene scene = new Scene(borderPane, 350, 570);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to handle button clicks
    private void handleButtonClick(String value) {
        switch (value) {
            case "=":
                evaluateExpression();
                break;
            case "C":
                inputField.clear();
                outputField.clear();
                break;
            case "EXIT":
                System.exit(0);
                break;
            default:
                // Append the button value to the input field
                inputField.appendText(value);
                break;
        }
    }

    // Method to evaluate expression
    private void evaluateExpression() {
        String expression = inputField.getText();
        try {
            double result;
            // Check if the input expression is just a result from a previous calculation
            if (expression.equals(Double.toString(previousResult))) {
                result = previousResult;
            } else {
                // Evaluate the expression normally
                result = evaluate(expression);
                previousResult = result; // Store the result for future use
            }
            // Update the output field with the result
            if (result == (long) result) {
                outputField.setText(String.format("%d", (long) result));
            } else {
                outputField.setText(Double.toString(result));
            }
            // Clear the input field for the next expression
            inputField.clear();
            // Set inputField text to the output field text to continue from the previous result
            inputField.setText(outputField.getText());
        } catch (Exception e) {
            outputField.setText("Error");
        }
    }


    private double evaluate(String expression) {
        // handles basic arithmetic operations
        return new Object() {
            int pos = -1, chr;

            void nextChar() {
                chr = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }


            boolean removeChar(int charToRemove) {
                // Method to remove specified character from the expression
                while (chr == ' ') nextChar(); // Skip whitespace characters
                if (chr == charToRemove) { // If the current character matches the one to remove
                    nextChar(); // Move to the next character
                    return true; // Return true indicating removal
                }
                return false; // Return false indicating no removal
            }

            double parse() {
                // Method to parse the expression
                nextChar(); // Move to the next character
                double x = parseExpression(); // Parse the expression
                if (pos < expression.length())
                    throw new RuntimeException("Unexpected: " + (char) chr); // If there are unexpected characters after parsing
                return x; // Return the parsed value
            }

            double parseExpression() {
                // Method to parse an expression
                double x = parseTerm(); // Parse the term
                for (; ; ) { // Infinite loop for parsing expressions
                    if (removeChar('+')) x += parseTerm(); // If encountering addition, parse the term and add
                    else if (removeChar('-'))
                        x -= parseTerm(); // If encountering subtraction, parse the term and subtract
                    else return x; // Return the parsed value if no more operators found
                }
            }

            double parseTerm() {
                // Method to parse a term
                double x = parseFactor(); // Parse the factor
                for (; ; ) { // Infinite loop for parsing terms
                    if (removeChar('*'))
                        x *= parseFactor(); // If encountering multiplication, parse the factor and multiply
                    else if (removeChar('/'))
                        x /= parseFactor(); // If encountering division, parse the factor and divide
                    else return x; // Return the parsed value if no more operators found
                }
            }

            double parseFactor() {
                // Method to parse a factor
                if (removeChar('+')) return parseFactor(); // If encountering unary plus, parse the factor recursively
                if (removeChar('-'))
                    return -parseFactor(); // If encountering unary minus, parse the factor recursively and negate
                double x;
                int startPos = this.pos; // Store the starting position
                if (removeChar('(')) { // If encountering parentheses
                    x = parseExpression(); // Parse the enclosed expression
                    removeChar(')'); // Remove the closing parenthesis
                } else if ((chr >= '0' && chr <= '9') || chr == '.') { // If encountering numbers
                    while ((chr >= '0' && chr <= '9') || chr == '.') nextChar(); // Parse the number
                    x = Double.parseDouble(expression.substring(startPos, this.pos)); // Convert the parsed substring to a double
                } else if (chr >= 'a' && chr <= 'z') { // If encountering functions
                    while (chr >= 'a' && chr <= 'z') nextChar(); // Parse the function name
                    String func = expression.substring(startPos, this.pos); // Get the function name
                    x = parseFactor(); // Parse the argument of the function
                    switch (func) { // Various mathematical functions
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        case "log":
                            x = Math.log10(x);
                            break;
                        case "ln":
                            x = Math.log(x);
                            break;
                        case "asin":
                            x = Math.asin(x);
                            break;
                        case "acos":
                            x = Math.acos(x);
                            break;
                        case "atan":
                            x = Math.atan(x);
                            break;
                        case "abs":
                            x = Math.abs(x);
                            break;
                        case "mod":
                            x = x % parseFactor();
                            break;
                        case "sinh":
                            x = Math.sinh(x);
                            break;
                        case "cosh":
                            x = Math.cosh(x);
                            break;
                        case "tanh":
                            x = Math.tanh(x);
                            break;
                        case "^2":
                            x = Math.pow(x, 2);
                            break;
                        case "^3":
                            x = Math.pow(x, 3);
                            break;
                        case "1/":
                            x = 1 / x;
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) chr);
                }


                if (removeChar('^')) x = Math.pow(x, parseFactor()); // exponentiation


                return x;
            }
        }.parse();
    }




    public static void main(String[] args) {
        launch(args);// Start the JavaFX application
    }
}//ends Public class Calculator