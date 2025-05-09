import java.util.Stack;

public class CalcLogic {
    private StringBuilder mathExpression;
    private String fullExpressionHistory;

    public CalcLogic() {
        mathExpression = new StringBuilder();
        fullExpressionHistory = "";
    }

    public void clear() {
        mathExpression.setLength(0);
        fullExpressionHistory = "";
    }

    public void append(String value) {
        mathExpression.append(value);
    }

    public void deleteLast() {
        if (mathExpression.length() > 0) {
            mathExpression.deleteCharAt(mathExpression.length() - 1);
        }
    }

    public String getExpression() {
        return mathExpression.toString();
    }

    public String getFullExpressionHistory() {
        return fullExpressionHistory;
    }

    public void setFullExpressionHistory(String history) {
        this.fullExpressionHistory = history;
    }

    public String evaluate(String expression) {
        try {
            expression = expression.replace("×", "*").replace("÷", "/");

            expression = handleSpecialFunctions(expression);

            double result = evaluateExpression(expression);

            if (result == (long) result) {
                return String.valueOf((long) result);
            } else {
                return String.valueOf(result);
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    private String handleSpecialFunctions(String expression) {
        while (expression.contains("√")) {
            int startIndex = expression.indexOf("√");
            int endIndex = findMatchingParenthesis(expression, startIndex + 1);

            if (endIndex == -1)
                return "Error";

            String insideParens = expression.substring(startIndex + 2, endIndex);
            double insideValue = evaluateExpression(insideParens);
            double sqrtValue = Math.sqrt(insideValue);

            expression = expression.substring(0, startIndex) + sqrtValue +
                    expression.substring(endIndex + 1);
        }

        while (expression.contains("^")) {
            int opIndex = expression.indexOf("^");

            if (opIndex == 0 || opIndex == expression.length() - 1)
                return "Error";

            double base = 0;
            int baseStart = opIndex - 1;
            while (baseStart >= 0 && (Character.isDigit(expression.charAt(baseStart)) ||
                    expression.charAt(baseStart) == '.')) {
                baseStart--;
            }
            baseStart++;
            base = Double.parseDouble(expression.substring(baseStart, opIndex));

            int expEnd = opIndex + 1;
            double exponent = 0;
            if (expression.charAt(expEnd) == '(') {
                int closeParenIndex = findMatchingParenthesis(expression, expEnd);
                String expValue = expression.substring(expEnd + 1, closeParenIndex);
                exponent = evaluateExpression(expValue);
                expEnd = closeParenIndex + 1;
            } else {
                while (expEnd < expression.length() && (Character.isDigit(expression.charAt(expEnd)) ||
                        expression.charAt(expEnd) == '.')) {
                    expEnd++;
                }
                exponent = Double.parseDouble(expression.substring(opIndex + 1, expEnd));
            }

            double powerResult = Math.pow(base, exponent);
            expression = expression.substring(0, baseStart) + powerResult +
                    expression.substring(expEnd);
        }

        return expression;
    }

    private int findMatchingParenthesis(String expression, int startIndex) {
        if (startIndex >= expression.length() || expression.charAt(startIndex) != '(') {
            return -1;
        }

        int count = 1;
        for (int i = startIndex + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                count++;
            } else if (expression.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) ||
                        expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (c == '-' && (i == 0 || expression.charAt(i - 1) == '(' ||
                        expression.charAt(i - 1) == '+' || expression.charAt(i - 1) == '-' ||
                        expression.charAt(i - 1) == '*' || expression.charAt(i - 1) == '/')) {
                    if (i + 1 < expression.length()
                            && (Character.isDigit(expression.charAt(i + 1)) || expression.charAt(i + 1) == '.')) {
                        StringBuilder sb = new StringBuilder();
                        sb.append('-');
                        i++;
                        while (i < expression.length() && (Character.isDigit(expression.charAt(i)) ||
                                expression.charAt(i) == '.')) {
                            sb.append(expression.charAt(i++));
                        }
                        i--;
                        values.push(Double.parseDouble(sb.toString()));
                    } else if (i + 1 < expression.length() && expression.charAt(i + 1) == '(') {
                        operators.push('(');
                        values.push(-1.0);
                        operators.push('*');
                        i++;
                    }
                } else {
                    while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                        values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(c);
                }
            }
        }

        while (!operators.empty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }
}