package org.protege.osgi.graph;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainPanel extends JPanel {
    private static final int CLASS = 0;
    private static final int PACKAGE = 1;
    private JComboBox classOrPackageBox;
    private JTextField classOrPackageText;

    public MainPanel() {
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
    }
    
    private JComponent createHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        String[] choices = { "Class", "Package" };
        classOrPackageBox = new JComboBox(choices);
        add(classOrPackageBox);
        JButton draw = new JButton();
        draw.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               System.out.println("Draw it!");
            } 
        });
        panel.add(draw);
        JButton clear = new JButton();
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clear and draw everything.");
            }
        });
        return panel;
    }
}
