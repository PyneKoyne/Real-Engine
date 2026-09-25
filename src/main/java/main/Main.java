// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the runner class, creating a window beforehand to put information

package main;


// Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener{

    // main function
    public static void main(String[] args){
        new Main().setVisible(true);

    }

    // Sets default width and height of the window
    public static int x = 1366;
    public static int y = 768;
    private Main(){
        //Properties
        super("Real Engine");
        setSize(x, y); //1024x768, 1600/900
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Press Q, E to decrease and increase the number of Cosine Functions applied respectively\n");
        JLabel label1 = new JLabel("Press Z, C to decrease and increase the number of Tangent Functions applied respectively\n");
        JLabel label2 = new JLabel("Press X to create a new plane where you are looking\n");
        JLabel label3 = new JLabel("Scroll the mouse wheel to zoom in and out, move the mouse to look around\n");
        JLabel label4 = new JLabel("Press W A S D to move around\n");
        JLabel label5 = new JLabel("Press Shift to unlock or lock the cursor");


        //Start Button
        JButton button = new JButton("Start");
        button.addActionListener(this);
        button.setBounds(x/2 - 110, y/2 - 15, 220, 30);

        //Panel
        JPanel pnlButton = new JPanel();
        pnlButton.setBounds(x/5, y/5, 3*x/5, 3*y/5);
        LayoutManager FlowLayout = new FlowLayout(java.awt.FlowLayout.CENTER, 550, 25);
        setLayout(FlowLayout);

        //adding button
        pnlButton.add(button);

        add(label);
        add(label1);
        add(label4);
        add(label2);
        add(label3);
        add(label5);
        add(pnlButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Closes the window if the start button is pressed
        String name = e.getActionCommand();

        if (name.equals("Start")){
            // Starts the Engine
            Engine.main();{
            }
            this.dispose();
        }

    }

}
