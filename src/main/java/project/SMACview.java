package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is the view in the MVC architecture.
 * This class defines the GUI of the application.
 * It observes the model and get notified when a
 * new result is available.
 * It sends events to the controller when a graphical
 * event is fired (like a button click or a return).
 * You should not change this class.
 */
public class SMACview implements Observer {

    private JFrame frame;
    private JTextField input;
    private JTextPane output;
    private JButton eval, clear, redo, change, exit;
    private JScrollPane scrollPane;

    private boolean addOnTop;
    private String lastInput;

    /**
     * Create and show the GUI
     */
    public SMACview() {
        addOnTop = true;
        lastInput = "";
        createGUI();
    }

    // create the GUI
    private void createGUI() {
        frame = new JFrame("SMAC");
        creatAndPlaceComponents();
        setListener();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // create and place the widgets
    private void creatAndPlaceComponents() {
        input = new JTextField();
        input.setPreferredSize(new Dimension(340, 30));
        output = new JTextPane();
        output.setEditable(false);

        Font font = new Font("Courrier New", Font.BOLD, 16);
        input.setFont(font);
        output.setFont(font);
        input.setMargin(new Insets(0, 5, 0, 5));
        output.setMargin(new Insets(5, 5, 5, 5));
        output.setForeground(Color.BLUE);

        eval = new JButton("Eval");
        clear = new JButton("Clear");
        eval.setActionCommand("eval");
        clear.setActionCommand("clear");
        redo = new JButton("Redo");
        redo.setActionCommand("redo");
        change = new JButton("Change");
        change.setActionCommand("change");
        exit = new JButton("Exit");
        exit.setActionCommand("exit");

        JPanel jpTop = new JPanel(new FlowLayout());
        jpTop.add(input);
        jpTop.add(eval);
        jpTop.add(redo);

        JPanel jpBottom = new JPanel(new GridLayout(1, 3));
        jpBottom.add(clear);
        jpBottom.add(change);
        jpBottom.add(exit);

        scrollPane = new JScrollPane(output);
        scrollPane.setFont(font);
        scrollPane.setPreferredSize(new Dimension(320, 600));

        JPanel jpMain = new JPanel();
        jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.PAGE_AXIS));
        jpMain.add(jpTop);
        jpMain.add(scrollPane);
        jpMain.add(jpBottom);

        frame.setContentPane(jpMain);
    }

    // set up all listener
    private void setListener() {
        clear.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        output.setText("");
                    }
                }
        );

        input.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        lastInput = input.getText();
                        input.setText("");
                        input.requestFocus();
                    }
                }
        );

        redo.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        input.setText(lastInput);
                    }
                }
        );

        change.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        output.setText("");
                        addOnTop = !addOnTop;
                    }
                }
        );
    }

    /**
     * returns the string input by the user
     */
    public String getInput() {
        return input.getText();
    }

    /**
     * links a listener to the view
     */
    public void setListener(ActionListener listener) {
        eval.addActionListener(listener);
        input.addActionListener(listener);
        exit.addActionListener(listener);
    }

    /**
     * updates the view when notified by the model
     */
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String r = (String) arg;
            if (addOnTop) {
                output.setText(r + "\n\n" + output.getText());
                output.grabFocus();
                output.setCaretPosition(0);
            } else
                output.setText(output.getText() + "\n\n" + r);
        }
    }

}
