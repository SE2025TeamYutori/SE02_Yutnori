package org.cau02.view;

import org.cau02.model.GameManager;
import org.cau02.model.YutNoriObserver;
import org.cau02.view.ui.MainGamePanel;
import org.cau02.view.ui.YutNoriSwingObserver;

import javax.swing.*;
import java.awt.*;


public class SwingGameView {
    private JFrame frame;
    private MainGamePanel mainPanel;
    private GameManager gameManager;
    private YutNoriSwingObserver observer;


    public SwingGameView(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    //Swing UI 초기화 및 표시
    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            createAndShowGUI();
        });
    }

    //메인 GUI 컴포넌트들을 생성하고 구성
    private void createAndShowGUI() {
        // 게임 매니저를 사용하여 메인 패널 생성
        mainPanel = new MainGamePanel(gameManager);
        
        // 게임 이벤트를 처리할 옵저버 등록
        observer = new YutNoriSwingObserver(gameManager, mainPanel);
        gameManager.registerObserver(observer);
        
        // 메인 프레임 생성 및 구성
        frame = new JFrame("YutNori Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 700));
        
        // 메인 패널을 프레임에 추가
        frame.getContentPane().add(mainPanel);
        
        // 프레임 최종화 및 표시
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //메인 게임 패널 반환
    public MainGamePanel getMainPanel() {
        return mainPanel;
    }

    //이 뷰에 등록된 옵저버 반환
    public YutNoriObserver getObserver() {
        return observer;
    }

    //애플리케이션의 메인 프레임 반환
    public JFrame getFrame() {
        return frame;
    }
    
    //프레임 해제 및 뷰 닫기
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }
}