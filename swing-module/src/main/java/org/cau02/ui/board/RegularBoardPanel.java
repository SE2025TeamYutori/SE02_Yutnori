package org.cau02.ui.board;

import javax.swing.*;
import org.cau02.model.GameManager;

public class RegularBoardPanel {
    private final AbstractBoardPanel boardPanel;

    public RegularBoardPanel(GameManager gm, int boardAngle) {
        switch (boardAngle) {
            case 4 -> boardPanel = new SquareBoardPanel(gm);
            case 5 -> boardPanel = new PentagonBoardPanel(gm);
            case 6 -> boardPanel = new HexagonBoardPanel(gm);
            default -> throw new IllegalArgumentException("지원하지 않는 boardAngle: " + boardAngle);
        }
    }

    public JPanel getPanel() {
        return boardPanel;
    }
    
    public SquareBoardPanel getSquareBoardPanel() {
        if (boardPanel instanceof SquareBoardPanel) {
            return (SquareBoardPanel) boardPanel;
        }
        return null;
    }
    
    public AbstractBoardPanel getBoardPanel() {
        return boardPanel;
    }
}
