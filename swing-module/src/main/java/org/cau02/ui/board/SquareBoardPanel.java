package org.cau02.ui.board;

import java.awt.*;
import java.util.List;
import org.cau02.model.*;

public class SquareBoardPanel extends AbstractBoardPanel {
    private static final double PADDING = 40.0;
    private static final double INNER_SIDE_LENGTH = SIDE_LENGTH - 2 * PADDING;
    private static final double START = PADDING;
    private static final double END = SIDE_LENGTH - PADDING;
    private static final double SPACE_LENGTH = INNER_SIDE_LENGTH / 5;
    private static final double DIAG_SPACE_LENGTH = INNER_SIDE_LENGTH / 6;

    private final List<Integer> crossIndexes = List.of(0, 5, 10, 15, 28);

    public SquareBoardPanel(GameManager gm) {
        super(gm);
        initializeBoard();
    }

    @Override
    protected void initializeBoardSpaces() {
        // 테두리
        for (int i = 0; i <= 5; i++) points.add(new Point((int) END, (int) (END - i * SPACE_LENGTH)));
        for (int i = 1; i <= 5; i++) points.add(new Point((int) (END - i * SPACE_LENGTH), (int) START));
        for (int i = 1; i <= 5; i++) points.add(new Point((int) START, (int) (START + i * SPACE_LENGTH)));
        for (int i = 1; i <= 4; i++) points.add(new Point((int) (START + i * SPACE_LENGTH), (int) END));

        // 대각선
        for (int i = 1; i <= 2; i++) points.add(new Point((int) (END - i * DIAG_SPACE_LENGTH), (int) (START + i * DIAG_SPACE_LENGTH)));
        for (int i = 1; i <= 2; i++) points.add(new Point((int) (START + i * DIAG_SPACE_LENGTH), (int) (START + i * DIAG_SPACE_LENGTH)));
        for (int i = 1; i <= 2; i++) points.add(new Point((int) (START + i * DIAG_SPACE_LENGTH), (int) (END - i * DIAG_SPACE_LENGTH)));
        for (int i = 1; i <= 2; i++) points.add(new Point((int) (END - i * DIAG_SPACE_LENGTH), (int) (END - i * DIAG_SPACE_LENGTH)));

        // 중앙
        points.add(new Point((int) ((START + END) / 2), (int) ((START + END) / 2)));
    }

    @Override
    protected void paintBoardShape(Graphics2D g2) {
        // 테두리 사각형
        g2.setColor(Color.BLACK);
        g2.drawRect((int) START, (int) START, (int) INNER_SIDE_LENGTH, (int) INNER_SIDE_LENGTH);

        // 대각선
        g2.drawLine((int) START, (int) START, (int) END, (int) END);
        g2.drawLine((int) START, (int) END, (int) END, (int) START);
    }

    @Override
    protected List<Integer> getCrossIndexes() {
        return crossIndexes;
    }
} 
