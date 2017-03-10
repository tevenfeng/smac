package project;

import java.util.Map;
import java.util.Stack;

/**
 * Coded by Qixuebin(2016229075) and Dingqixuan(2016229062)
 * These codes are used to do the evaluation of a given mathematical expression
 * which will be provided with form of a token queue.
 */
public class MathematicalEvaluator {

    private double result;
    private boolean isExpression;

    public MathematicalEvaluator() {
        result = 0;
        isExpression = false;
    }

    // Judge if the expression is legal.
    public void computeResult(Tokenizer tokenizer, Map<String, Double> variables, double theLast)
            throws TokenException, GeneralErrorException, LexicalErrorException, SyntaxErrorException {

        // The operate stack is used to store the operate.
        Stack<FunOp> theOperateStack = new Stack<FunOp>();

        // The number stack is used to store the number.
        Stack<Double> theValueStack = new Stack<Double>();

        int count = 0;

        while (tokenizer.hasNextToken()) {
            Token getTheNextToken = tokenizer.readNextToken();

            count++;
            if (count > 1) {
                this.isExpression = true;
            }

            if (getTheNextToken.isNumber()) {
                theValueStack.push(getTheNextToken.getNumber());
            } else if (getTheNextToken.isIdentifier()) {
                if (getTheNextToken.getIdentifier().equals("last")) {
                    theValueStack.push(theLast);
                } else if (variables.containsKey(getTheNextToken.getIdentifier())) {
                    theValueStack.push(variables.get(getTheNextToken.getIdentifier()));
                } else {
                    throw new SyntaxErrorException(getTheNextToken.getIdentifier() + " is not a variable");
                }
            } else if (getTheNextToken.isDelimiter()) {
                FunOp GetTheFunOp = new FunOp(getTheNextToken.getDelimiter());
                if (GetTheFunOp.getName().equals("(")) {
                    theOperateStack.push(GetTheFunOp);
                } else {
                    while (!theOperateStack.peek().getName().equals("(")) {
                        String theOperate = theOperateStack.pop().getName();
                        double firstValue = theValueStack.pop();
                        double secondValue = theValueStack.pop();
                        double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
                        theValueStack.push(theResult);
                    }

                    theOperateStack.pop();
                }
            } else if (getTheNextToken.isOperator()) {
                FunOp thisOperate = new FunOp(getTheNextToken.getOperator());
                while (!theOperateStack.empty() && (theOperateStack.peek().getPriority() >= thisOperate.getPriority())) {
                    String theOperate = theOperateStack.pop().getName();
                    double firstValue = theValueStack.pop();
                    double secondValue = theValueStack.pop();
                    double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
                    theValueStack.push(theResult);
                }

                theOperateStack.push(thisOperate);
            }

        }

        while (!theOperateStack.empty()) {
            FunOp tmp = theOperateStack.pop();
            String theOperate = tmp.getName();
            double firstValue = theValueStack.pop();
            double secondValue = theValueStack.pop();
            double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
            theValueStack.push(theResult);
        }

        this.result = theValueStack.pop();
    }

    private double ComputeTheResult(double firstValue, double secondValue, String theOperate)
            throws GeneralErrorException {
        double theResult = 0;
        switch (theOperate) {
            case "+":
                return firstValue + secondValue;
            case "-":
                return secondValue - firstValue;
            case "*":
                return firstValue * secondValue;
            case "/":
                if (secondValue == 0) {
                    String error = "division by zero";
                    throw new GeneralErrorException(error);
                } else {
                    return secondValue / firstValue;
                }
            case "^":
                for (int i = 1; i <= secondValue; i++) {
                    theResult *= firstValue;
                }
                return theResult;
            default:
                return 0;
        }
    }

    public Result getResult() {
        return new Result(this.result, this.isExpression);
    }
}
