package org.cau02.view.ui;

import org.cau02.model.GameManager;
import org.cau02.model.Yut;
import org.cau02.model.GameState;
import org.cau02.model.YutNoriObserver;

import javax.swing.*;
import java.util.List;


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
                
                // 윷 결과 이미지 표시
                ControlPanel controlPanel = mainPanel.getControlPanel();
                if (controlPanel != null) {
                    // 마지막으로 던진 윷 결과 가져오기
                    Yut lastYut = getLastThrownYut();
                    if (lastYut != null) {
                        controlPanel.showYutImage(lastYut);
                    }
                }
            }
        });
    }

    private Yut getLastThrownYut() {
        if (gameManager.getState() != GameState.PLAYING) {
            return null;
        }

        List<Integer> yutResult = gameManager.getYutResult();
        // 가장 최근에 던진 윷을 찾기 (가장 높은 값을 가진 윷부터 확인)
        for (int i = yutResult.size() - 1; i >= 0; i--) {
            if (yutResult.get(i) > 0) {
                return Yut.values()[i];
            }
        }
        return null;
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
