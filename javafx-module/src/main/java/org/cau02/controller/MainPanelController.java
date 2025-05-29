package org.cau02.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.cau02.controller.boardController.BoardController;
import org.cau02.model.GameManager;
import org.cau02.model.RegularBoard;
import org.cau02.model.Yut;
import org.cau02.model.YutNoriObserver;
import org.cau02.view.PlayerPanel;
import org.cau02.view.WinPanel;
import org.cau02.view.YutSelectPanel;
import org.cau02.view.boardView.BoardPanel;
import org.cau02.view.boardView.RegularBoardPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainPanelController implements YutNoriObserver {
    private final GameManager gm;

    @FXML private StackPane rootStackPane;
    @FXML private Label yutCountLabel;
    @FXML private Button throwRandomYutButton;
    @FXML private Button throwSelectYutButton;

    @FXML private VBox playerBoxLeft;

    @FXML private VBox middleBox;
    @FXML private HBox yutBox;

    @FXML private VBox playerBoxRight;

    @FXML private StackPane yutResultPane;
    @FXML private ImageView yutResultView;
    @FXML private Label yutResultLabel;

    private Timeline yutResultTimer;

    private final List<StackPane> yutResultPanes = new ArrayList<>(6);
    private final List<PlayerPanel> playerPanels = new ArrayList<>(4);

    public MainPanelController(GameManager gm) {
        this.gm = gm;
        gm.registerObserver(this);
    }

    private void initializeYutResults() {
        for (int i = 0; i < 6; i++) {
            StackPane stackPane = new StackPane();
            stackPane.setId("yutResult");
            Circle circle = new Circle(0, 0, 25);
            Label yutLabel = new Label();
            Label countLabel = new Label();

            countLabel.setTranslateX(15);
            countLabel.setTranslateY(15);

            stackPane.getChildren().addAll(circle, yutLabel, countLabel);
            yutResultPanes.add(stackPane);
        }

        yutBox.getChildren().addAll(0, yutResultPanes);
    }

    @FXML
    private void initialize() {
        initializeYutResults();
        BoardPanel boardPanel = new RegularBoardPanel(gm, ((RegularBoard)(gm.getBoard())).getBoardAngle());
        middleBox.getChildren().addFirst(boardPanel.getRoot());
        BoardController boardController = boardPanel.getController();

        playerPanels.add(new PlayerPanel(gm, 0, boardController));
        playerPanels.getFirst().getRoot().getStyleClass().clear();
        playerPanels.getFirst().getRoot().getStyleClass().add("nowTurn");
        playerBoxLeft.getChildren().add(playerPanels.get(0).getRoot());

        playerPanels.add(new PlayerPanel(gm, 1, boardController));
        playerBoxRight.getChildren().add(playerPanels.get(1).getRoot());

        if (gm.getPlayerCount() >= 3) {
            playerPanels.add(new PlayerPanel(gm, 2, boardController));
            playerBoxLeft.getChildren().add(playerPanels.get(2).getRoot());
        }
        if (gm.getPlayerCount() >= 4) {
            playerPanels.add(new PlayerPanel(gm, 3, boardController));
            playerBoxRight.getChildren().add(playerPanels.get(3).getRoot());
        }
    }

    @FXML
    private void throwRandomYut() {
        Yut yut = gm.throwRandomYut();

        showYutImage(yut);
    }

    @FXML
    private void openYutSelectPanel() {
        rootStackPane.getChildren().add((new YutSelectPanel(gm, this)).getRoot());
    }

    void closeYutSelectPanel(VBox yutSelectPanel) {
        rootStackPane.getChildren().remove(yutSelectPanel);
    }

    void showYutImage(Yut yut) {
        yutResultPane.setVisible(true);
        Image yutImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/yut_images/" + yut.name().toLowerCase() + ".png")));
        yutResultView.setImage(yutImage);
        yutResultLabel.setText(yut.getKoreanName() + "!");

        if (yutResultTimer != null && yutResultTimer.getStatus() == Timeline.Status.RUNNING) {
            yutResultTimer.stop();
        }
        yutResultTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), // 1초 지연
                        event -> {
                            // 1초 후에 실행될 코드
                            yutResultPane.setVisible(false);
                            yutResultView.setImage(null);
                            yutResultLabel.setText("");
                        })
        );
        yutResultTimer.setCycleCount(1); // 단 한 번만 실행되도록 설정
        yutResultTimer.play();
    }

    @Override
    public void onGameEnded() {
        rootStackPane.getChildren().add((new WinPanel(gm.getWinner())).getRoot());
    }

    @Override
    public void onTurnChanged() {
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            if (gm.getCurrentPlayer() == i) {
                playerPanels.get(i).getRoot().getStyleClass().clear();
                playerPanels.get(i).getRoot().getStyleClass().add("nowTurn");
            } else {
                playerPanels.get(i).getRoot().getStyleClass().clear();
                playerPanels.get(i).getRoot().getStyleClass().add("notNowTurn");
            }
        }


        // 새 말 이동 버튼
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            if (gm.getCurrentPlayer() != i) {
                playerPanels.get(i).getController().disableMoveNewPieceButton();
            } else {
                if (gm.getReadyPiecesCount(i) > 0) {
                    playerPanels.get(i).getController().enableMoveNewPieceButton();
                } else {
                    playerPanels.get(i).getController().disableMoveNewPieceButton();
                }
            }
        }
    }

    @Override
    public void onYutStateChanged() {
        yutCountLabel.setText("남은 횟수: " + gm.getCurrentYutCount());

        // 윷 족보 보유상황
        int index = 0;
        List<Integer> yutResult = gm.getYutResult();
        for (int i = 0; i < Yut.values().length; i++) {
            if (yutResult.get(i) > 0) {
                ObservableList<Node> children = yutResultPanes.get(index).getChildren();
                children.getFirst().setId(Yut.values()[i].name().toLowerCase());
                ((Label)(children.get(1))).setText(Yut.values()[i].getKoreanName());
                if (yutResult.get(i) > 1) {
                    ((Label)(children.get(2))).setText("x" + yutResult.get(i).toString());
                } else {
                    ((Label)(children.get(2))).setText("");
                }
                index++;
            }
        }

        // 나머지칸
        for (int i = index; i < 6; i++) {
            ObservableList<Node> children = yutResultPanes.get(i).getChildren();
            children.getFirst().setId("");
            ((Label)(children.get(1))).setText("");
            ((Label)(children.get(2))).setText("");
        }
        
        // 윷 던지기 횟수로 버튼 활성화 / 비활성화
        if (gm.getCurrentYutCount() > 0) {
            throwRandomYutButton.setDisable(false);
            throwSelectYutButton.setDisable(false);
        } else {
            throwRandomYutButton.setDisable(true);
            throwSelectYutButton.setDisable(true);
        }
        
        // 이동 가능사항 체킹
        if (gm.getCurrentMoveCount() > 0 && gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 &&
                !Objects.equals(gm.getCurrentMoveCount(), gm.getYutResult().getFirst())
        ) {
            playerPanels.get(gm.getCurrentPlayer()).getController().enableMoveNewPieceButton();
        } else {
            playerPanels.get(gm.getCurrentPlayer()).getController().disableMoveNewPieceButton();
        }
    }

    @Override
    public void onPieceMoved() {
        // 새 말 던지기 버튼 처리
        if (gm.getCurrentMoveCount() > 0 && gm.getReadyPiecesCount(gm.getCurrentPlayer()) > 0 &&
                !Objects.equals(gm.getCurrentMoveCount(), gm.getYutResult().getFirst())) {
            playerPanels.get(gm.getCurrentPlayer()).getController().enableMoveNewPieceButton();
        } else {
            playerPanels.get(gm.getCurrentPlayer()).getController().disableMoveNewPieceButton();
        }

        // 대기 말 업데이트
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            playerPanels.get(i).getController().updateReadyPieces();
        }
    }
}
