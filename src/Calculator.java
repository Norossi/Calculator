import java.util.*;

/**
 * Created by VIP or bb on 24.10.2018.
 * ^[\d+(\.\d+)?\(]+[\d+(\.\d+)?\+\-\*\/\(\)]+[\d+(\.\d+)?\)]+$ regexp
 */
public class Calculator {

    // list of operations that can be done
    private static String operator = "+-*/^";

    // delimiters for splitting initial string into tokens
    private static String delimiters = "()" + operator;


    // the only public method which returns calculated value of the infixEx (input string)
    // or null, if infixEx cannot be evaluated (incorrect input)
    public static String calculate(String infixEx) {
        if (isValid(infixEx)) return evaluateRPN(convertToRPN(infixEx));
        else return null;
    }

    // boolean method which returns true if infixEX (input string) is correct
    // and can be evaluated

    // method splits infixEx string into tokens and checks if current token matches
    // next one (for example, 2 + + isn't correct and result will be null)
    // method does not check parentheses
    private static boolean isValid(String infixEx) {
        if (infixEx == null || infixEx.equals("")) return false;
        boolean flag = false;
        int index;
        String currentToken;
        ArrayList<String> infixList = new ArrayList<>();
        StringTokenizer input = new StringTokenizer(infixEx, delimiters, true);
        while (input.hasMoreTokens()) {
            infixList.add(input.nextToken());
        }
        ListIterator<String> iterator = infixList.listIterator();

        index = iterator.nextIndex();
        currentToken = iterator.next();

        if (infixList.get(infixList.size() - 1).equals("(") || isOperator(infixList.get(infixList.size() - 1)) ||
            infixList.get(0).equals(")") || isOperator(infixList.get(0))) flag = true;

        while (iterator.hasNext() && !flag) {

            if (isNumber(currentToken)) {
                if (index == 0) if (!isOperator(infixList.get(index + 1))) flag = true;
                else if (!isOperator(infixList.get(index + 1)) && !infixList.get(index + 1).equals(")")) flag = true;
            }

            else if (isOperator(currentToken)) {
                if (index == 0) flag = true;
                else if (!isNumber(infixList.get(index + 1)) && !infixList.get(index + 1).equals("(")) flag = true;
            }

            else if (currentToken.equals(")")) {
                if (index == 0) flag = true;
                else if (!isOperator(infixList.get(index + 1)) && !infixList.get(index + 1).equals(")")) flag = true;
            }

            else if (currentToken.equals("(")) {
                if (!isNumber(infixList.get(index + 1)) && !infixList.get(index + 1).equals("(")) flag = true;
            }

            else flag = true;
            index = iterator.nextIndex();
            currentToken = iterator.next();
        }
        return !flag;
    }


    // method which converts infixEx (input string) into postfix expression (reverse polish notation, RPN)
    // via the shunting-yard algorithm and returns result as Linked List

    // method also checks if parentheses match each other
    private static LinkedList<String> convertToRPN(String infixEx) {
        if (infixEx == null || infixEx.equals("")) return null;
        StringTokenizer input = new StringTokenizer(infixEx, delimiters, true);
        LinkedList<String> rpnEx = new LinkedList<>();
        LinkedList<String> operatorsStack = new LinkedList<>();
        String currentToken;
        boolean parenthesesFlag = false;
        while (input.hasMoreTokens() && !parenthesesFlag) {
            currentToken = input.nextToken();
            if (isNumber(currentToken)) rpnEx.add(currentToken);
            else if (isDelimiter(currentToken)) {
                if (isOperator(currentToken)) {
                    if (operatorsStack.isEmpty()) operatorsStack.add(currentToken);
                    else {
                        if (getPriority(currentToken) > getPriority(operatorsStack.peekLast()))
                            operatorsStack.add(currentToken);
                        else {
                            while (!operatorsStack.isEmpty() &&
                                    getPriority(operatorsStack.peekLast()) >= getPriority(currentToken)) {
                                rpnEx.add(operatorsStack.pollLast());
                            }
                            operatorsStack.add(currentToken);
                        }
                    }
                }
                if (currentToken.equals("(")) operatorsStack.add(currentToken);
                if (currentToken.equals(")")) {
                    while (!operatorsStack.isEmpty() && !operatorsStack.peekLast().equals("(")) {
                        rpnEx.add(operatorsStack.pollLast());
                    }
                    if (operatorsStack.isEmpty()) parenthesesFlag = true;
                    else operatorsStack.removeLast();
                }
            }
            else parenthesesFlag = true;
        }
        if (parenthesesFlag || operatorsStack.contains("(")) return null;
        else {
            while (!operatorsStack.isEmpty()) {
                rpnEx.add(operatorsStack.pollLast());
            }
        }
        return rpnEx;
    }


    // method which evaluates value of postfix expression (rpnEx, given as Linked List)
    // and returns result as string, or returns null if there's dividing by zero
    private static String evaluateRPN(LinkedList<String> rpnEx) {
        if (rpnEx == null) return null;
        String s;
        boolean divideByZeroFlag = false;
        ListIterator<String> iter = rpnEx.listIterator();
        String currentToken;
        double currentResult = 0;
        double[] operands = new double[2];
        while (iter.hasNext() && !divideByZeroFlag) {
            currentToken = iter.next();
            if (isNumber(currentToken)) continue;
            if (isOperator(currentToken)) {
                iter.remove();
                for (int i = 1; i >= 0 && iter.hasPrevious(); i--) {
                    s = iter.previous();
                    operands[i] = Double.parseDouble(s);
                    iter.remove();
                }
                switch (currentToken) {
                    case "+": currentResult = operands[0] + operands[1]; break;
                    case "-": currentResult = operands[0] - operands[1]; break;
                    case "*": currentResult = operands[0] * operands[1]; break;
                    case "/":
                        if (operands[1] == 0) divideByZeroFlag = true;
                        else currentResult = operands[0] / operands[1]; break;
                    case "^": currentResult = Math.pow(operands[0], operands[1]); break;
                }
                iter.add(Double.toString(currentResult));
                iter = rpnEx.listIterator();
            }
        }
        if (divideByZeroFlag) return null;
        currentResult = Double.parseDouble(rpnEx.getFirst());
        return Double.toString(currentResult);
    }

    private static int getPriority(String operator) {
        if (operator.equals("(")) return 1;
        if (operator.equals("+") || operator.equals("-")) return 2;
        if (operator.equals("*") || operator.equals("/")) return 3;
        if (operator.equals("^")) return 4;
        return 5;
    }

    private static boolean isOperator(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < operator.length(); i++) {
            if (token.charAt(0) == operator.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isNumber(String token) {
        return token.matches("^\\d+(\\.\\d+)?$");
    }

    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }


}
