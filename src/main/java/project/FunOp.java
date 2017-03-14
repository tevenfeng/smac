package project;

/**
 * Coded by Fengdingwen(2016229064)
 * We assume that the priorities of this
 * operators are like the followings:
 * 0: ( )
 * 1: + -
 * 2: * /
 * 3: ^
 */
public class FunOp {
    // Name of the operator.
    // For example, '+', '-' and so on.
    private String name;

    // The number of operands, for example, 1 or 2
    // For example, '+' needs two numbers to calculate.
    private int arity;

    // The priority of this operator. We assume the default priority is -1.
    private int priority = -1;

    // Get the name of the operate.
    public String getName() {
        return this.name;
    }

    // Get the numbers that the operate need,such as "+" need two numbers to finish the compute.
    public int getArity() {
        return this.arity;
    }

    // Get the priority of the operate.
    public int getPriority() {
        return this.priority;
    }

    // Initialize the Funop method,and set the priority of the operate.
    public FunOp(String operate) {
        this.name = operate;

        switch (operate) {
            case "(":
                this.arity = 0;
                this.priority = 0;
                break;
            case "+":
            case "-":
                this.arity = 2;
                this.priority = 1;
                break;
            case "*":
            case "/":
                this.arity = 2;
                this.priority = 2;
                break;
            case "^":
                this.arity = 2;
                this.priority = 3;
                break;
            case ")":
                this.arity = 0;
                this.priority = 0;
                break;
            default:
                break;
        }
    }
}
