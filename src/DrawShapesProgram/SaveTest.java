package DrawShapesProgram;

//from http://www.java2s.com/Code/Java/Swing-JFC/DemonstrationofFiledialogboxes.htm

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveTest extends JFrame {
    private JTextField filename = new JTextField(), dir = new JTextField();

    //BL private JButton open = new JButton("Open")
    private JButton save = new JButton("Save");

    public SaveTest() {
        JPanel p = new JPanel();
        //open.addActionListener(new OpenL());
        //p.add(open);
        save.addActionListener(new SaveL());
        p.add(save);
        Container cp = getContentPane();
        cp.add(p, BorderLayout.SOUTH);
        dir.setEditable(false);
        filename.setEditable(false);
        p = new JPanel();
        p.setLayout(new GridLayout(2,1));
        p.add(filename);
        p.add(dir);
        cp.add(p, BorderLayout.NORTH);
    }

    class SaveL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser();
            //Demonstrate "Save" dialog:
            int rVal = c.showSaveDialog(DrawShapesProgram.SaveTest.this);
            System.out.println("rVal: " + rVal);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                filename.setText(c.getCurrentDirectory().toString());
                System.out.println("filename: " +c.getCurrentDirectory().toString() );
                System.out.println("filename: " +c.getSelectedFile().getName() );

            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                filename.setText("You pressed cancel");
                dir.setText("");
            }
        }
    }

    public static void main(String[] args) {
        run(new SaveTest(), 250, 110);

    }

    public static void run(JFrame frame, int width, int height) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

}