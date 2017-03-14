package project;

import java.lang.String;
import java.io.*;
import java.util.Observable;

/**
 * This class is the model in the MVC architecture.
 * The model performs computations and notifies the view
 * with the result. The model should use your code to
 * evaluate SMAC commands and compute the result
 */
public class SMACmodel extends Observable {

    private static boolean isLogging = false;
    private static boolean justLog = false;
    private static String logName = "";
    private static PrintStream logFile = null;

    /**
     * evaluates the input and notify
     * the view with the result (a String)
     */
    public void eval(String input) {
        ////// CHANGE THIS PART //////
        String r = "";

        try {
            Tokenizer t = new Tokenizer(input);

            if (isLogging) {
                r = ">> " + input + "\n";
            } else {
                r = "> " + input + "\n";
            }

            if (t.peekNextToken().isIdentifier() && t.peekNextToken().getIdentifier().equals("log")) {
                t.readNextToken();
                if (t.hasNextToken()) {
                    if (t.peekNextToken().isString()) {
                        if (isLogging) {
                            throw new GeneralErrorException("Already in session: " + logName);
                        } else {
                            isLogging = true;
                            justLog = true;
                            logName = t.peekNextToken().getString();
                            r += "logging session to " + t.peekNextToken().getString() + "\n";
                        }
                    } else {
                        if (t.peekNextToken().isIdentifier() && t.peekNextToken().getIdentifier().equals("end")) {
                            isLogging = false;
                            r += "session was logged to " + logName + "\n";
                        } else {
                            throw new SyntaxErrorException("Session name required.");
                        }
                    }
                } else {
                    if (isLogging) {
                        r += logName + "\n";
                    } else {
                        throw new SyntaxErrorException("Session name required.");
                    }
                }
            } else {
                Evaluator myEvaluator = new Evaluator();

                r += myEvaluator.startProcessByFirstKeyWord(t) + "\n";
            }

            if (isLogging) {
                if (!justLog) {
                    // output to session file
                    if (logFile == null) {
                        logFile = new PrintStream(new File(logName + ".log"));
                    }
                    logFile.print(r);
                } else {
                    justLog = false;
                }
            }
        } catch (Exception e) {
            r += e.toString();
        }

        //////////////////////////////
        // this notify the view and send the result
        setChanged();
        notifyObservers(r);
    }
}
