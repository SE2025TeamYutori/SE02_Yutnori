package org.cau02.view.ui;

import org.cau02.model.GameManager;
import org.cau02.model.YutNoriObserver;

import javax.swing.*;


//윷놀이 스윙 옵저버
public class YutNoriSwingObserver implements YutNoriObserver {
    private final GameManager gameManager;
    private final JPanel uiPanel;

    public YutNoriSwingObserver(GameManager gameManager, JPanel uiPanel) {
        this.gameManager = gameManager;
        this.uiPanel = uiPanel;
    }

    @Override
    public void onGameEnded() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                uiPanel,
                "Game Over! Player " + gameManager.getWinner() + " wins!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    @Override
    public void onTurnChanged() {
        SwingUtilities.invokeLater(() -> {
            if (uiPanel instanceof MainGamePanel) {
                MainGamePanel mainPanel = (MainGamePanel) uiPanel;
                mainPanel.updateTurnInfo();
                mainPanel.updateControlPanel();
            }
        });
    }

    @Override
    public void onYutStateChanged() {
        SwingUtilities.invokeLater(() -> {
            if (uiPanel instanceof MainGamePanel) {
                MainGamePanel mainPanel = (MainGamePanel) uiPanel;
                mainPanel.updateYutInfo();
                mainPanel.updateControlPanel();
            }
        });
    }

    @Override
    public void onPieceMoved() {
        SwingUtilities.invokeLater(() -> {
            if (uiPanel instanceof MainGamePanel) {
                MainGamePanel mainPanel = (MainGamePanel) uiPanel;
                mainPanel.updateBoardPanel();
                mainPanel.updatePlayerInfo();
            }
        });
    }
}