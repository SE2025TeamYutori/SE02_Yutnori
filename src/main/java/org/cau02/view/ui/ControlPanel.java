package org.cau02.view.ui;

import org.cau02.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * 게임 컨트롤 (윷 던지기, 말 이동 등)을 위한 패널
 */
public class ControlPanel extends JPanel {
    private final GameManager gameManager;
    private final JLabel turnLabel = new JLabel("Current Player: ");
    private final JLabel yutCountLabel = new JLabel("Remaining throws: ");
    private final JLabel yutResultLabel = new JLabel("Yut throws available: ");
    
    private final JButton randomYutButton = new JButton("Throw Random Yut");
    
    private final JComboBox<Yut> selectYutComboBox = new JComboBox<>(Yut.values());
    private final JButton selectYutButton = new JButton("Throw Selected Yut");
    
    private final JPanel activePiecesPanel = new JPanel();
    private final JButton moveNewPieceButton = new JButton("Move New Piece");
    
    private final JTextArea messageArea = new JTextArea(5, 30);
    

    public ControlPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 게임 정보를 표시하는 상단 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1));
        infoPanel.add(turnLabel);
        infoPanel.add(yutCountLabel);
        infoPanel.add(yutResultLabel);
        
        // 컨트롤을 포함하는 중앙 패널
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(5, 1, 5, 5));
        
        // 윷 던지기 컨트롤
        JPanel yutPanel = new JPanel();
        yutPanel.setLayout(new GridLayout(2, 1, 5, 5));
        
        yutPanel.add(randomYutButton);
        
        JPanel selectYutPanel = new JPanel();
        selectYutPanel.setLayout(new BorderLayout());
        selectYutPanel.add(selectYutComboBox, BorderLayout.CENTER);
        selectYutPanel.add(selectYutButton, BorderLayout.EAST);
        yutPanel.add(selectYutPanel);
        
        controlsPanel.add(yutPanel);
        
        // 말 이동 컨트롤
        controlsPanel.add(new JLabel("Active Pieces:"));
        
        activePiecesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane piecesScrollPane = new JScrollPane(activePiecesPanel);
        piecesScrollPane.setPreferredSize(new Dimension(200, 80));
        controlsPanel.add(piecesScrollPane);
        
        JPanel newPiecePanel = new JPanel();
        newPiecePanel.setLayout(new BorderLayout());
        
        JLabel readyPiecesLabel = new JLabel("Ready: 0");
        newPiecePanel.add(readyPiecesLabel, BorderLayout.WEST);
        newPiecePanel.add(moveNewPieceButton, BorderLayout.EAST);
        
        controlsPanel.add(newPiecePanel);
        
        // 메시지 영역
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setPreferredSize(new Dimension(200, 100));
        
        //메인 패널에 추가
        add(infoPanel, BorderLayout.NORTH);
        add(controlsPanel, BorderLayout.CENTER);
        add(messageScrollPane, BorderLayout.SOUTH);
        
        randomYutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Yut result = gameManager.throwRandomYut();
                    addMessage("Threw a random yut: " + result.name());
                } catch (IllegalStateException ex) {
                    addMessage("Error: " + ex.getMessage());
                }
            }
        });
        
        selectYutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Yut selected = (Yut) selectYutComboBox.getSelectedItem();
                    Yut result = gameManager.throwSelectedYut(selected);
                    addMessage("Threw a selected yut: " + result.name());
                } catch (IllegalStateException ex) {
                    addMessage("Error: " + ex.getMessage());
                }
            }
        });
        
        moveNewPieceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0) {
                        showNewPieceMoveDialog();
                    } else {
                        addMessage("No ready pieces available");
                    }
                } catch (IllegalStateException ex) {
                    addMessage("Error: " + ex.getMessage());
                }
            }
        });
        
        initializeUI();
    }
    
    //선택된 말 설정
    public void setSelectedPiece(Piece piece) {
        if (piece != null) {
            showPieceMoveDialog(piece);
        }
    }
    
    //UI 초기화
    public void initializeUI() {
        turnLabel.setText("Game not started");
        yutCountLabel.setText("");
        yutResultLabel.setText("");
        randomYutButton.setEnabled(false);
        selectYutButton.setEnabled(false);
        moveNewPieceButton.setEnabled(false);
    }
    
    //현재 게임 상태 반영
    @Override
    public void updateUI() {
        super.updateUI();
        
        if (gameManager == null) return;
        
        if (gameManager.getState() != GameState.PLAYING) {
            turnLabel.setText("Game not started");
            yutCountLabel.setText("");
            yutResultLabel.setText("");
            randomYutButton.setEnabled(false);
            selectYutButton.setEnabled(false);
            moveNewPieceButton.setEnabled(false);
            updateActivePieces();
            return;
        }
        
        // 턴 정보 업데이트
        turnLabel.setText("Current Player: " + gameManager.getCurrentPlayer());
        
        // 윷 정보 업데이트
        Integer yutCount = gameManager.getCurrentYutCount();
        yutCountLabel.setText("Remaining throws: " + yutCount);
        
        // 윷 결과 업데이트
        StringBuilder sb = new StringBuilder("Yut moves available: ");
        List<Integer> yutResults = gameManager.getYutResult();
        for (int i = 0; i < yutResults.size(); i++) {
            if (yutResults.get(i) > 0) {
                sb.append(Yut.values()[i].name()).append("(").append(yutResults.get(i)).append(") ");
            }
        }
        yutResultLabel.setText(sb.toString());
        
        // 버튼 상태 업데이트
        randomYutButton.setEnabled(yutCount != null && yutCount > 0);
        selectYutButton.setEnabled(yutCount != null && yutCount > 0);
        
        // 새 말 버튼 업데이트
        boolean hasReadyPieces = gameManager.getReadyPiecesCount(gameManager.getCurrentPlayer()) > 0;
        boolean canMove = gameManager.getCurrentMoveCount() != null && gameManager.getCurrentMoveCount() > 0;
        moveNewPieceButton.setEnabled(hasReadyPieces && canMove);
        
        // 활성 말 업데이트
        updateActivePieces();
    }
    
    //활성 말 표시 업데이트
    private void updateActivePieces() {
        activePiecesPanel.removeAll();
        
        if (gameManager == null || gameManager.getState() != GameState.PLAYING) {
            activePiecesPanel.revalidate();
            activePiecesPanel.repaint();
            return;
        }
        
        try {
            Set<Piece> activePieces = gameManager.getActivePieces(gameManager.getCurrentPlayer());
            for (final Piece piece : activePieces) {
                final int pieceIndex = gameManager.getBoard().getSpaces().indexOf(piece.getLocation());
                
                JButton pieceButton = new JButton("Piece at " + pieceIndex);
                pieceButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectedPiece(piece);
                    }
                });
                
                activePiecesPanel.add(pieceButton);
            }
        } catch (Exception e) {
        }
        
        activePiecesPanel.revalidate();
        activePiecesPanel.repaint();
    }
    

    //말 이동 대화 상자 표시
    private void showPieceMoveDialog(final Piece piece) {
        try {
            List<BoardSpace> possibleLocations = gameManager.getPossibleLocations(piece);
            showMoveDialog(possibleLocations, yut -> {
                try {
                    gameManager.movePiece(piece, yut);
                    addMessage("Moved piece using " + yut.name());
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    addMessage("Error: " + ex.getMessage());
                }
            });
        } catch (IllegalStateException | IllegalArgumentException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    //새 말 이동 대화 상자 표시
    private void showNewPieceMoveDialog() {
        try {
            List<BoardSpace> possibleLocations = gameManager.getPossibleLocationsOfNewPiece();
            showMoveDialog(possibleLocations, yut -> {
                try {
                    gameManager.moveNewPiece(yut);
                    addMessage("Moved new piece using " + yut.name());
                } catch (IllegalStateException ex) {
                    addMessage("Error: " + ex.getMessage());
                }
            });
        } catch (IllegalStateException ex) {
            addMessage("Error: " + ex.getMessage());
        }
    }
    
    /**
     * 이동할 윷 선택 대화 상자 표시
     * 
     * @param possibleLocations 각 윷 타입에 대한 가능한 위치 목록
     * @param onYutSelected 윷이 선택될 때의 콜백
     */
    private void showMoveDialog(List<BoardSpace> possibleLocations, Consumer<Yut> onYutSelected) {
        if (possibleLocations == null) {
            addMessage("No possible moves");
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Select move");
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        
        JPanel yutPanel = new JPanel();
        yutPanel.setLayout(new GridLayout(0, 1));
        
        List<Integer> yutResults = gameManager.getYutResult();
        boolean anyMoves = false;
        
        for (int i = 0; i < possibleLocations.size(); i++) {
            final Yut yut = Yut.values()[i];
            BoardSpace destination = possibleLocations.get(i);
            
            if (destination != null && yutResults.get(i) > 0) {
                anyMoves = true;
                int destIndex = gameManager.getBoard().getSpaces().indexOf(destination);
                
                JButton yutButton = new JButton(yut.name() + " -> " + destIndex);
                yutButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onYutSelected.accept(yut);
                        dialog.dispose();
                    }
                });
                
                yutPanel.add(yutButton);
            }
        }
        
        if (!anyMoves) {
            addMessage("No possible moves");
            return;
        }
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(new JLabel("Select a yut to move with:"), BorderLayout.NORTH);
        dialog.add(new JScrollPane(yutPanel), BorderLayout.CENTER);
        dialog.add(cancelButton, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    //메시지 영역에 메시지 추가
    public void addMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }
    
    //콜백을 위한 Consumer 인터페이스
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }
}