package project;

import java.util.*;

/**
 * Coded by Fengdingwen(2016229064)
 * These codes are the main part of the program and they
 * are used to process every string input by the user.
 */
public class Evaluator {

    // GLOBAL variable to store the 'last' computational result.
    private static double last;

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

    public Evaluator() {

    }

    // Determine whether a specific token is a keyword
    // such as "let", "reset" or so on.
    public static boolean isKeyWord(Token token) throws TokenException {
        return keyWords.contains(token.getIdentifier());
    }

    private double compute(Tokenizer t, Map<String, Double> variables, double theLast) {
        MathematicalEvaluator myCal = new MathematicalEvaluator();
        myCal.computeResult(t, variables, theLast);
        return myCal.getResult();
    }

    // Process the first token to decide what kind of input it is,
    // for example, if the first token is 'let' then it should be followed
    // by a '=', and if it is 'log' then it should be followed by a String
    public String startProcessByFirstKeyWord(Tokenizer t)
            throws TokenException, LexicalErrorException, GeneralErrorException, SyntaxErrorException {

        String resultString = "";

        if (t.hasNextToken()) {
            Token firstToken = t.peekNextToken();
            if (firstToken.isIdentifier()) {
                switch (firstToken.getIdentifier()) {
                    case "let":
                        t.readNextToken();
                        return letProcessor(t);
                    case "reset":
                        t.readNextToken();
                        return resetProcess(t);
                    case "last":
                        t.readNextToken();
                        if (!t.hasNextToken()) {
                            return Double.toString(this.last);
                        } else {
                            throw new SyntaxErrorException("Redundant token: " + t.peekNextToken());
                        }
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
                        t.readNextToken();
                        return precisionManage(t);
                    default:
                        firstToken = t.readNextToken();
                        if (t.hasNextToken()) {
                            // Compute the expression and assign the result to 'last' and return it
                            this.last = compute(t, variables, this.last);
                            variables.put(firstToken.getIdentifier(), this.last);
                            return Double.toString(this.last);
                        } else {
                            //return firstToken.getIdentifier();
                            if (variables.containsKey(firstToken.getIdentifier())) {
                                // Output the value of this variable.
                                return variables.get(firstToken.getIdentifier()).toString();
                            } else {
                                throw new GeneralErrorException(firstToken.getIdentifier() + " is not a variable");
                            }
                        }
                        //break;
                }
            } else {
                // Compute the expression and assign the result to 'last' and return it
                this.last = compute(t, variables, this.last);
                return Double.toString(this.last);
            }
        }


        return resultString;
    }

    //region Codes to process 'let'

    // This function processes following tokens after 'let'.
    // And this usually means that we are going to do some assignment-processing.
    public String letProcessor(Tokenizer t)
            throws TokenException, LexicalErrorException, SyntaxErrorException {
        String result = "";

        if (t.hasNextToken()) {
            Token identifier = t.readNextToken();
            if (t.hasNextToken() && identifier.isIdentifier()) {
                Token equal = t.readNextToken();
                if (t.hasNextToken() && equal.isEqual()) {
                    if (!isKeyWord(identifier)) {
                        // Compute the expression and assign it to 'last' and then return it.
                        this.last = compute(t, variables, this.last);
                        variables.put(identifier.getIdentifier(), this.last);
                        return Double.toString(this.last);

                    } else {
                        throw new SyntaxErrorException(identifier.getIdentifier() + " is not a variable");
                    }
                } else {
                    throw new SyntaxErrorException("Malformed assignment");
                }
            } else {
                throw new SyntaxErrorException("Malformed assignment");
            }
        } else {
            if (!variables.isEmpty()) {
                Iterator<Map.Entry<String, Double>> entries = variables.entrySet().iterator();
                while (entries.hasNext()) {
                    result += entries.next().getKey() + " = " + entries.next().getValue() + "\n";
                }
            } else {
                result = "no variable defined\n";
            }
        }

        return result;
    }

    //endregion

    //region Precision Management

    // This function manages the precision according to the input by the usr.
    private String precisionManage(Tokenizer t)
            throws LexicalErrorException, TokenException, SyntaxErrorException {
        if (t.hasNextToken()) {
            if (t.peekNextToken().isNumber()) {
                Token tmp = t.readNextToken();
                if (tmp.getNumber() > 0 && tmp.getNumber() == Math.floor(tmp.getNumber())) {
                    this.setPrecision((int) tmp.getNumber());
                    return "precision set to " + (int) tmp.getNumber();
                } else {
                    throw new LexicalErrorException("Precision should be an unsigned integer");
                }
            } else {
                throw new SyntaxErrorException(t.peekNextToken().toString() + " can not be a precision");
            }
        } else {
            return "current precision is " + currentPrecision;
        }
    }

    // This function helps set the current precision.
    private void setPrecision(int precision) {
        currentPrecision = precision;
    }

    // This function helps get the current precision.
    public int getCurrentPrecision() {
        return currentPrecision;
    }

    //endregion

    //region Codes to process 'Reset'

    // This function is used to process the keyword 'reset'
    private String resetProcess(Tokenizer t) throws TokenException, SyntaxErrorException, LexicalErrorException {
        String result = "";
        if (t.hasNextToken()) {
            // Followed by some specific variables(maybe).
            while (t.hasNextToken()) {
                if (t.peekNextToken().isIdentifier()) {
                    if (variables.containsKey(t.peekNextToken().getIdentifier())
                            && !keyWords.contains(t.peekNextToken().getIdentifier())) {
                        variables.remove(t.peekNextToken().getIdentifier());
                        result += t.readNextToken().getIdentifier() + " has been reset\n";
                    } else {
                        throw new SyntaxErrorException(t.peekNextToken().getIdentifier() + " is not a variable");
                    }
                } else {
                    throw new SyntaxErrorException(t.peekNextToken().toString() + " is not a variable");
                }
            }
        } else {
            // Followed by nothing, so we have to delete all variables.
            Iterator<Map.Entry<String, Double>> entries = variables.entrySet().iterator();
            while (entries.hasNext()) {
                result += entries.next().getKey() + " has been reset\n";
            }
            variables.clear();
        }

        return result;
    }

    //endregion

    //region Save

//    private String saveProcess(Tokenizer t){
//
//    }

    //endregion
}
