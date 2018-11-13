import java.util.Arrays;
import java.util.Stack;

/**
 * Equation is a stack based equation solver that solves chained math equations.
 *
 * @author anirudh
 * @version 1.0
 */
public class Equation {

    private static final char OPENING_BRACKET = '(';
    private static final char CLOSING_BRACKET = ')';
    private static final String SPLIT_CHAR = " ";

    /**
     * This static function takes in a character and determine the precedence of the given operator.
     * The precedence is determined using the BEDMAS rule of math.<br/>
     * Higher the number, more priority is given to it. <br/>
     * <br/><br/>
     * See the following chart for precedence number returned.
     * 3: Exponential
     * 2: Divisions and Multiplications
     * 1: Subtraction and Addition
     * -1: Unsupported operations.
     *
     * @param character character to check precedence for.
     * @return integer specifying the precedence. see the chart in documentation for details.
     */
    private static int precedence(char character) {
        switch (character) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return -1;
        }
    }

    /**
     * This method converts the Infix expression (regular math) to Postfix express (computer math).
     *
     * @param expression expression to convert.
     * @return string representing the Postfix notation.
     */
    static String toPostfix(String expression) {

        final StringBuilder result = new StringBuilder(); // final output
        final CustomStack stack = new CustomStack();

        final Stack<Character> looked = new Stack();

        // for each character in the equation do the following.
        expression.chars().mapToObj(i -> (char)i).forEachOrdered(character -> {

            // if the given character is a letter or digit or a period '.'
            if(Character.isLetterOrDigit(character) || character == '.') {
                // if the last character we looked at was a number then this number is part of that decimal or integer
                if(
                        result.length() != 0 && (looked.isEmpty() || (Character.isLetterOrDigit(looked.peek()) || looked.peek() == '.'))
                ) {
                    // remove the space and make it part of the same number.
                    result.deleteCharAt(result.length() -1);
                }
                // append current character.
                result.append(character).append(SPLIT_CHAR);
            }

            // else if the character is OPENING_BRACKET then push it to stack.
            else if(character == OPENING_BRACKET) { stack.push(character); }
            // else if the character is CLOSING_BRACKET then
            else if (character == CLOSING_BRACKET) {
                // while the stack isn't empty or the OPENING_BRACKET is found
                while (!stack.isEmpty() && !stack.isLastCharacter(OPENING_BRACKET)) {
                    // push all the characters to result.
                    result.append(stack.pop()).append(SPLIT_CHAR);
                }
                // if stack is not empty and the last symbol present is not a closing bracket something happened.
                if (!stack.isEmpty() && !stack.isLastCharacter(OPENING_BRACKET)) {
                    throw new InvalidExpressionException(expression);
                } // otherwise drop the opening bracket
                else if (!stack.isEmpty()) { stack.pop(); }
            }
            else {
                // check if the stack is not empty and precedence of the character is less than or same as on stack
                while (!stack.isEmpty() && precedence(character) <= precedence( ((char)stack.peek()) )) {
                    // output the stack operator
                    result.append(stack.pop()).append(SPLIT_CHAR);
                }
                // add the character to stack
                stack.push(character);
            }

            looked.push(character); // since i am going through it let's add it to looked so we can make use of that later.
        });

        while (!stack.isEmpty()) {
            result.append(stack.pop()).append(SPLIT_CHAR);
        }

        return result.toString();
    }

    /**
     * Evaluates a postfix notation equation to just a single number.
     *
     * @param postfix a valid postfix notation which can be generated from {@link Equation#toPostfix(String)} method.
     * @return a number that represents the equation.
     */
    static double evaluate(String postfix) {
        final CustomStack stack = new CustomStack();

        Arrays.stream(postfix.split(SPLIT_CHAR)).map(chars -> {
            try {
                double d = Double.parseDouble(chars);
                return d;
            } catch (NumberFormatException nfe) {
                if (precedence(chars.charAt(0)) > 0) {
                    return chars.charAt(0);
                } else {
                    return chars;
                }
            }
        }).forEachOrdered(eq -> {
            // check if it is a number
            if(eq instanceof Double) {
                // if yes push to stack.
                stack.push(eq);
            } else if(eq instanceof Character){
                // character symbol found
                try {
                    double a = (double) stack.pop();
                    double b = (double) stack.pop();
                    stack.push(solve(((Character) eq).charValue(), b, a) );
                } catch (Exception ex) {
                    throw new InvalidExpressionException(postfix, ex);
                }
            }
        });
        return (double) stack.pop();
    }

    /**
     *
     * @param operator
     * @param operand1
     * @param operand2
     * @return
     */
    private static double solve(char operator, double operand1, double operand2) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                return operand1 / operand2;
            case '^':
                return Math.pow(operand1, operand2);
            default:
                return -1;
        }
    }

    /**
     * Exception class that is throws if the provided equation is invalid.
     */
    static class InvalidExpressionException extends RuntimeException {
        public InvalidExpressionException(String expression) {
            super();
        }
        public InvalidExpressionException(String expression, Exception ex) {
            super(String.format("Invalid expression provided: %s", expression), ex);
        }
    }

    /**
     * Custom stack class to add additional functionality to stack.
     */
    public static class CustomStack extends Stack<Object> {

        public boolean isLastCharacter(char character) {
            final Object o = this.peek();
            return (o instanceof Character && ((char) o) == character);
        }
    }


}
