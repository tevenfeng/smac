package project;

/**
 * Coded by Qixuebin(2016229075)
 * Class to help determine whether it is a
 * mathematicalexpression or just a variable or number
 */
public class Result {
    public double result;
    public boolean isExpression;

    public Result(double r, boolean is) {
        result = r;
        isExpression = is;
    }
}
