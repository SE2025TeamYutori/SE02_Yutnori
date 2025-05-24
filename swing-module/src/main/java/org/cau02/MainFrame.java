package org.cau02;

import javax.swing.*;

import org.cau02.ui.StartPanel;

public class MainFrame extends JFrame {
    public MainFrame() {
        setContentPane(new StartPanel());
        // 리소스 로드 생략 가능
    }
}
