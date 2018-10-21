package DrawShapesProgram;

//from http://www.java2s.com/Code/Java/Swing-JFC/DemonstrationofFiledialogboxes.htm

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSaver extends JFrame {
    private JTextField filename = new JTextField(), dir = new JTextField();

    private JButton open = new JButton("Open"), save = new JButton("Save");

    public FileSaver() {
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
            int rVal = c.showSaveDialog(DrawShapesProgram.FileSaver.this);

            /*if (rVal == JFileChooser.APPROVE_OPTION) {
                filename.setText(c.getCurrentDirectory().toString());
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                filename.setText("You pressed cancel");
                dir.setText("");
            } */
        }
    }
}