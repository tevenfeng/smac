package project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Coded by Fengdingwen(2016229064)
 * These codes are the main part of the program and they
 * are used to processor every string input by the usr.
 */
public class Processor {

    // GLOBAL variable to store the 'last' computational result
    private int last;

    // Hashmap to store all variables
    private static Map<String, Double> variables = new HashMap<>();

    // Key words set, help to determine whether an identifier is a key word actually.
    private static Set keyWords = new HashSet();

    // Init keyWords set
    static {
        keyWords.add("let");
        keyWords.add("reset");
        keyWords.add("last");
        keyWords.add("save");
        keyWords.add("exit");
        keyWords.add("load");
        keyWords.add("saved");
        keyWords.add("log");
        keyWords.add("end");
        keyWords.add("logged");
        keyWords.add("setprecision");
        keyWords.add("session");
    }

    // Current precison, if this value equals -1,
    // then we should use the default precision in java.
    private static int currentPrecision = -1;

    public Processor() {

    }

    // Determine whether a specific token is a keyword
    // such as "let", "reset" or so on.
    public static boolean isKeyWord(Token token) throws TokenException {
        return keyWords.contains(token.getIdentifier());
    }

    // Process the first token to decide what kind of input it is,
    // for example, if the first token is 'let' then it should be followed
    // by a '=', and if it is 'log' then it should be followed by a String
    public String startProcessByFirstKeyWord(Tokenizer t)
            throws TokenException, LexicalErrorException, GeneralErrorException, SyntaxErrorException {

        String resultString = "";

        if (t.hasNextToken()) {
            Token firstToken = t.readNextToken();
            if (firstToken.isIdentifier()) {
                switch (firstToken.getIdentifier()) {
                    case "let":
                        break;
                    case "reset":
                        if (t.hasNextToken()) {

                        }
                        break;
                    case "last":
                        break;
                    case "save":
                        break;
                    case "load":
                        break;
                    case "saved":
                        break;
                    case "log":
                        break;
                    case "end":
                        break;
                    case "logged":
                        break;
                    case "setprecision":
                        if (t.hasNextToken()) {
                            if (t.peekNextToken().isNumber()) {
                                Token tmp = t.readNextToken();
                                if (tmp.getNumber() == Math.floor(tmp.getNumber())) {
                                    this.setPrecision((int) tmp.getNumber());
                                    return "precision set to " + (int) tmp.getNumber();
                                } else {
                                    throw new LexicalErrorException("Precision should be an integer.");
                                }
                            } else {
                                throw new LexicalErrorException(t.peekNextToken().toString() + " can not be a precision.");
                            }
                        } else {
                            return "current precision is " + currentPrecision;
                        }
                    case "session":
                        break;
                    default:
                        throw new LexicalErrorException(firstToken.getIdentifier() + " is not a variable.");
                }
            }
        }


        return resultString;
    }


    // This function processes the reset of the tokens following a 'let' token.
    // And this means that we are going to do some assignment-processing.
    public void letProcessor() {

    }

    // This static function helps set the current precision.
    private void setPrecision(int precision) {
        currentPrecision = precision;
    }

    // This function helps get the current precision.
    public int getCurrentPrecision() {
        return currentPrecision;
    }
}
