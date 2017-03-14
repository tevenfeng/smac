package project;

import java.util.Map;
import java.util.Stack;

/**
 * Coded by Qixuebin(2016229075) and Dingqixuan(2016229062)
 * These codes are used to do the evaluation of a given mathematical expression
 * which will be provided with form of a token queue.
 */
public class MathematicalEvaluator {

    // To store the final result
    private double result;

    // To help determine whether it is just a number
    // or an expression that needs to be calculated.
    private boolean isExpression;

    public MathematicalEvaluator() {
        result = 0;
        isExpression = false;
    }

    // Judge if the expression is legal and calculate to get its value.
    public void computeResult(Tokenizer tokenizer, Map<String, Double> variables, double theLast)
            throws TokenException, GeneralErrorException, LexicalErrorException, SyntaxErrorException {

        // The operate stack is used to store the operate.
        Stack<FunOp> theOperateStack = new Stack<FunOp>();

        // The number stack is used to store the number.
        Stack<Double> theValueStack = new Stack<Double>();

        int count = 0;

        // The flag is used to count how many number has been pushed into the value stack


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

                if (GetTheFunOp.getName().equals(")") && theValueStack.isEmpty()) {
                    throw new SyntaxErrorException("malformed expression");
                }
                if (!theOperateStack.isEmpty() && theValueStack.isEmpty()) {
                    throw new SyntaxErrorException("malformed expression");
                }

                if (GetTheFunOp.getName().equals("(")) {
                    theOperateStack.push(GetTheFunOp);
                } else {
                    while (!theOperateStack.peek().getName().equals("(")) {
                        String theOperate = theOperateStack.pop().getName();
                        if (theValueStack.isEmpty()) {
                            throw new SyntaxErrorException("malformed expression");
                        }
                        double firstValue = theValueStack.pop();
                        if (theValueStack.isEmpty()) {
                            throw new SyntaxErrorException("malformed expression");
                        }
                        double secondValue = theValueStack.pop();
                        double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
                        theValueStack.push(theResult);
                    }

                    theOperateStack.pop();
                }
            } else if (getTheNextToken.isOperator()) {
                // check if the expression is malformed, such as + 2 + 3,is a malformed expression,
                // 3 + 7 * / 2 is a malformed expression too.
                if (theValueStack.isEmpty()) {
                    throw new SyntaxErrorException("malformed expression");
                }

                FunOp thisOperate = new FunOp(getTheNextToken.getOperator());
                while (!theOperateStack.empty() && (theOperateStack.peek().getPriority() >= thisOperate.getPriority())) {
                    String theOperate = theOperateStack.pop().getName();
                    if (theValueStack.isEmpty()) {
                        throw new SyntaxErrorException("malformed expression");
                    }
                    double firstValue = theValueStack.pop();
                    if (theValueStack.isEmpty()) {
                        throw new SyntaxErrorException("malformed expression");
                    }
                    double secondValue = theValueStack.pop();
                    double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
                    theValueStack.push(theResult);
                }

                theOperateStack.push(thisOperate);
            } else {
                throw new SyntaxErrorException("malformed expression");
            }
        }

        while (!theOperateStack.empty()) {
            FunOp tmp = theOperateStack.pop();
            String theOperate = tmp.getName();
            if (theValueStack.isEmpty()) {
                throw new SyntaxErrorException("malformed expression");
            }
            double firstValue = theValueStack.pop();
            if (theValueStack.isEmpty()) {
                throw new SyntaxErrorException("malformed expression");
            }
            double secondValue = theValueStack.pop();
            double theResult = ComputeTheResult(firstValue, secondValue, theOperate);
            theValueStack.push(theResult);
        }

        if (theOperateStack.empty() && theValueStack.size() == 1) {
            this.result = theValueStack.pop();
        } else {
            throw new SyntaxErrorException("malformed expression");
        }

    }

    // Compute + - * / or ^
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
                if (firstValue == 0) {
                    throw new GeneralErrorException("division by zero");
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

    // Get the final result
    public Result getResult() {
        return new Result(this.result, this.isExpression);
    }
}
