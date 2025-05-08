package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;


//스윙 진입점
public class YutNoriSwingApp {
    private static void createAndShowGUI() {
        GameManager gameManager = new GameManager(4, 2, 4);
        
        MainGamePanel mainPanel = new MainGamePanel(gameManager);
        
        JFrame frame = new JFrame("YutNori Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 700));
        
        frame.getContentPane().add(mainPanel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {                 
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createAndShowGUI();
            }
        });
    }
}