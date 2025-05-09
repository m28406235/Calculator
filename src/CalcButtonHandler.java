public class CalcButtonHandler {
    private final CalcLogic calcLogic;
    private final Display display;
    private boolean lastOperationWasEquals = false;
    private String previousFullExpression = "";
    private String previousResult = "";
    private boolean continuingCalculation = false;

    public CalcButtonHandler(CalcLogic calcLogic, Display display) {
        this.calcLogic = calcLogic;
        this.display = display;
    }

    public void handleButtonClick(String label) {
        switch (label) {
            case "C":
                display.setText("");
                display.clearLastExpression();
                calcLogic.clear();
                lastOperationWasEquals = false;
                previousFullExpression = "";
                previousResult = "";
                continuingCalculation = false;
                break;

            case "DEL":
                String text = display.getText();
                int pos = display.getCursorPosition();
                if (pos > 0) {
                    display.setText(text.substring(0, pos - 1) + text.substring(pos));
                    display.setCursorPosition(pos - 1);
                    calcLogic.deleteLast();
                }
                lastOperationWasEquals = false;
                break;

            case "=":
                String expression = display.getText();
                if (!expression.isEmpty()) {
                    String fullHistoryExpression = expression;
                    String result = calcLogic.evaluate(expression);

                    if (continuingCalculation && !previousFullExpression.isEmpty()) {
                        if (previousFullExpression.contains("+") || previousFullExpression.contains("-") ||
                                previousFullExpression.contains("×") || previousFullExpression.contains("÷")) {
                            fullHistoryExpression = "(" + previousFullExpression + ")" +
                                    expression.substring(previousResult.length());
                        } else {
                            fullHistoryExpression = previousFullExpression +
                                    expression.substring(previousResult.length());
                        }
                    }

                    display.setLastExpression(fullHistoryExpression);
                    previousFullExpression = fullHistoryExpression;
                    previousResult = result;
                    display.setText(result);
                    display.setCursorPosition(result.length());
                    lastOperationWasEquals = true;
                    continuingCalculation = false;
                }
                break;

            case "←":
                display.moveCursorLeft();
                break;

            case "→":
                display.moveCursorRight();
                break;

            case "+":
            case "-":
            case "×":
            case "÷":
                if (lastOperationWasEquals) {
                    String currentDisplay = display.getText();
                    display.setText(currentDisplay + label);
                    display.setCursorPosition(display.getText().length());
                    lastOperationWasEquals = false;
                    continuingCalculation = true;
                } else {
                    display.insertTextAtCursor(label);
                }
                break;

            case "^":
                display.insertTextAtCursor("^(");
                lastOperationWasEquals = false;
                break;

            case "√":
                display.insertTextAtCursor("√(");
                lastOperationWasEquals = false;
                break;

            case ".":
                display.insertTextAtCursor(".");
                lastOperationWasEquals = false;
                break;

            case "(":
                display.insertTextAtCursor("(");
                lastOperationWasEquals = false;
                break;

            case ")":
                display.insertTextAtCursor(")");
                lastOperationWasEquals = false;
                break;

            default:
                if (label.matches("[0-9]")) {
                    if (lastOperationWasEquals) {
                        display.setText("");
                        display.clearLastExpression();
                        display.insertTextAtCursor(label);
                        lastOperationWasEquals = false;
                        previousFullExpression = "";
                        previousResult = "";
                        continuingCalculation = false;
                    } else {
                        display.insertTextAtCursor(label);
                    }
                }
                break;
        }
    }
}