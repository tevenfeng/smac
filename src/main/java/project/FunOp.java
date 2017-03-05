package project;

/**
 * Coded by Fengdingwen(2016229064)
 * We assume that the priorities of this
 * operators are like the followings:
 * 0: + -
 * 1: * /
 * 2: ^
 */
public class FunOp {
    // Name of the operator.
    // For example, '+', '-' and so on.
    public String name;

    // The number of operands, for example, 1 or 2
    // For example, '+' needs two numbers to calculate.
    public int arity;

    // The priority of this operator. We assume the default priority is -1.
    public int priority = -1;
}
