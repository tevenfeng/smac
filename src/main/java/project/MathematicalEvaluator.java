package project;

import java.util.Stack;

/**
 * Coded by Qixuebin(2016229075) and Dingqixuan(2016229062)
 * These codes are used to do the evaluation of a given mathematical expression
 * which will be provided with form of a token queue.
 */
public class MathematicalEvaluator {
    private Tokenizer tokenizer;

    private double result;

    public MathematicalEvaluator(Tokenizer t) {
        this.tokenizer = t;
    }

    // Judge if the expression is legal.
    public void JudegTheExpression()
            throws TokenException, GeneralErrorException {

        // The operate stack is used to store the operate.
        Stack<FunOp> theOperateStack = new Stack<FunOp>();

        // The number stack is used to store the number.
        Stack<Double> theValueStack = new Stack<Double>();

        while (this.tokenizer.hasNextToken()) {
            Token getTheNextToken = this.tokenizer.peekNextToken();

            if (getTheNextToken.isNumber()) {
                theValueStack.push(getTheNextToken.getNumber());
            } else if (getTheNextToken.isIdentifier()) {
                //    theValueStack.push(getTheNextToken.getString());
            } else if (getTheNextToken.isDelimiter()) {
                FunOp GetTheFunOp = new FunOp(getTheNextToken.getDelimiter());
                if (GetTheFunOp.getName() == "(") {
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
                FunOp formerOperate = theOperateStack.peek();
                while (!theOperateStack.empty() && (formerOperate.getPriority() >= thisOperate.getPriority())) {
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
            theOperateStack.pop();
            String theOperate = theOperateStack.pop().getName();
            double firstValue = theValueStack.pop();
            double secondValue = theValueStack.pop();
            double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
            theValueStack.push(theResult);
        }

        this.result = theValueStack.pop();
    }

    public double ComputeTheResult(double firstValue, double secondValue, String theOperate)
            throws GeneralErrorException {
        double theResult = 0;
        switch (theOperate) {
            case "+":
                return firstValue + secondValue;
            case "-":
                return firstValue + secondValue;
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
}
