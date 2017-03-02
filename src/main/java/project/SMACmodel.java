package project;

import java.util.Observable;

/**
 * This class is the model in the MVC architecture.
 * The model performs computations and notifies the view
 * with the result. The model should use your code to
 * evaluate SMAC commands and compute the result
 */
public class SMACmodel extends Observable {

    /**
     * evaluates the input and notify
     * the view with the result (a String)
     */
    public void eval(String input) {
        ////// CHANGE THIS PART //////
        String r = "> " + input + "\n\n";
        try {

            Tokenizer t = new Tokenizer(input);
            while (t.hasNextToken()) {
                r += t.peekNextToken() + " ";
                t.readNextToken();
            }

        } catch (Exception e) {
            r += "ERROR: " + e.getMessage();
        }
        //////////////////////////////
        // this notify the view and send the result
        setChanged();
        notifyObservers(r);
    }
}
