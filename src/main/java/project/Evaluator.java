package project;

import java.math.BigDecimal;
import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;

/**
 * Coded by Fengdingwen(2016229064)
 * These codes are the main part of the program and they
 * are used to process every string input by the user.
 */
public class Evaluator {

    private String fileRootPath = "";

    // GLOBAL variable to store the 'last' computational result.
    private static double last;

    // Hashmap to store all variables
    private static Map<String, Double> variables = new HashMap<>();

    // Key words set, help to determine whether an identifier is a key word actually.
    private static Set<String> keyWords = new HashSet<>();

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
    }

    // Current precision, the default precision is 6
    private static int currentPrecision = 6;

    public Evaluator() {

    }

    // Determine whether a specific token is a keyword
    // such as "let", "reset" or so on.
    public static boolean isKeyWord(Token token) throws TokenException {
        return keyWords.contains(token.getIdentifier());
    }

    // The function to execute the computation
    private Result compute(Tokenizer t, Map<String, Double> variables, double theLast)
            throws TokenException, GeneralErrorException, LexicalErrorException, SyntaxErrorException {
        MathematicalEvaluator myCal = new MathematicalEvaluator();
        myCal.computeResult(t, variables, theLast);
        return myCal.getResult();
    }

    // Process the first token to decide what kind of input it is,
    // for example, if the first token is 'let' then it should be followed
    // by a '=', and if it is 'log' then it should be followed by a String
    public String startProcessByFirstKeyWord(Tokenizer t)
            throws TokenException, LexicalErrorException, GeneralErrorException, SyntaxErrorException, FileNotFoundException {

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
                            return formatWithPrecision(last);
                        } else {
                            throw new SyntaxErrorException("Redundant token: " + t.peekNextToken());
                        }
                    case "save":
                        return saveProcess(t);
                    case "load":
                        return loadProcess(t);
                    case "saved":
                        break;
                    case "log":
                        break;
                    case "logged":
                        break;
                    case "setprecision":
                        t.readNextToken();
                        return precisionManage(t);
                    default:
                        firstToken = t.readNextToken();
                        if (t.hasNextToken()) {
                            // Compute the expression(still have to make sure)
                            // and assign the result to 'last' and return it
                            Result tmpResult = compute(t, variables, last);
                            if (tmpResult.isExpression) {
                                last = tmpResult.result;
                            }

                            variables.put(firstToken.getIdentifier(), tmpResult.result);
                            return formatWithPrecision(tmpResult.result);
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
                last = compute(t, variables, last).result;
                return formatWithPrecision(last);
            }
        }


        return resultString;
    }

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

    // This function format a double number into string with the specific precision
    private String formatWithPrecision(double result) {
        BigDecimal bd = new BigDecimal(result);
        double withPrecision = bd.setScale(currentPrecision, BigDecimal.ROUND_HALF_DOWN).doubleValue();

        return Double.toString(withPrecision);
    }

    //endregion

    //region Let

    // This function processes following tokens after 'let'.
    // And this usually means that we are going to do some assignment-processing.
    public String letProcessor(Tokenizer t)
            throws TokenException, LexicalErrorException, SyntaxErrorException, GeneralErrorException {
        String result = "";

        if (t.hasNextToken()) {
            Token identifier = t.readNextToken();
            if (t.hasNextToken() && identifier.isIdentifier()) {
                Token equal = t.readNextToken();
                if (t.hasNextToken() && equal.isEqual()) {
                    if (!isKeyWord(identifier)) {
                        // Compute the expression(still have to make sure)
                        // and assign the result to 'last' and return it
                        Result tmpResult = compute(t, variables, last);
                        if (tmpResult.isExpression) {
                            last = tmpResult.result;
                        }

                        variables.put(identifier.getIdentifier(), tmpResult.result);
                        return formatWithPrecision(tmpResult.result);
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
                    Map.Entry<String, Double> tmpEntry = entries.next();
                    result += tmpEntry.getKey() + " = " + formatWithPrecision(tmpEntry.getValue()) + "\n";
                }
            } else {
                result = "no variable defined\n";
            }
        }

        return result;
    }

    //endregion

    //region Reset

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

    // This function is used to process the key word 'save', that means we
    // are going to save all variables in the map 'variables'.
    private String saveProcess(Tokenizer t) throws LexicalErrorException, TokenException, SyntaxErrorException, FileNotFoundException {
        String re = "";
        t.readNextToken();
        if (t.hasNextToken()) {
            Token tmp = t.peekNextToken();
            String name = tmp.getString();
            t.readNextToken();
            if (!t.hasNextToken()) {
                Set<String> keySet = variables.keySet();
                PrintStream output = new PrintStream(new File(this.fileRootPath + name + ".txt"));
                for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    double value = variables.get(key);
                    output.println(key + " = " + formatWithPrecision(value));
                    re = "varialbes saved in " + name;
                }
            } else {
                Token ch = t.peekNextToken();
                String v = ch.getIdentifier();
                PrintStream output = new PrintStream(new File(this.fileRootPath + name + ".txt"));
                if (variables.containsKey(v)) {
                    double value = variables.get(v);
                    output.println(v + " = " + formatWithPrecision(value));
                    re = "varialbes saved in " + name;
                } else {
                    throw new SyntaxErrorException("The name of variable is wrong.");
                }
            }
        } else {
            throw new FileNotFoundException("Filename is not entered.");
        }
        return re;
    }

    //endregion

    // region Load

    // This function is used to process the key word 'load', which means we
    // are going to read the context from file and store the context into the map.

    private String loadProcess(Tokenizer t) throws LexicalErrorException, TokenException, FileNotFoundException, SyntaxErrorException {
        String re = "";
        t.readNextToken();
        if (t.hasNextToken()) {
            Token tmp = t.peekNextToken();
            String name = tmp.getString();
            File file = new File(this.fileRootPath + name + ".txt");
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                Scanner Line = new Scanner(line);
                String varname = Line.next();
                String equal = Line.next();
                double num = Line.nextDouble();
                variables.put(varname, num);
            }
            re = name + "  load";
        } else {
            throw new SyntaxErrorException("File is not found.");
        }
        return re;
    }

    // endregion

    //region Saved
    private String processSaved() {
        String result = "";


        return result;
    }

    //endregion
}
