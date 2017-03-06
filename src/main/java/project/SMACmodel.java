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

        // Evaluate the input here(done by Qixuebin and Dingqixuan).

        try {
            Tokenizer t = new Tokenizer(input);

            Processor myProcessor = new Processor();

            r += myProcessor.startProcessByFirstKeyWord(t);

//            while (t.hasNextToken()) {
//                r += t.peekNextToken() + " ";
//                t.readNextToken();
//
//            }

        } catch (Exception e) {
            r += e.toString();
        }

        //////////////////////////////
        // this notify the view and send the result
        setChanged();
        notifyObservers(r);
    }
}
