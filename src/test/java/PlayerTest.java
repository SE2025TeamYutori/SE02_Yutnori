import org.cau02.model.old.Color;
import org.cau02.model.old.Player;
import org.cau02.view.GameView;
import org.cau02.model.old.Move;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Player 클래스 및 하위 클래스 testing **/
class PlayerTest {

    @Test
    @DisplayName("Player 생성 시 지정된 말 개수 확인")
    void humanPlayer_PieceCount() {
        Player player = new Player("테스터", Color.RED, 3);
        assertEquals(3, player.getPieces().size());
    }

    @Test
    @DisplayName("잘못된 말 개수로 Player 생성 시 예외 발생")
    void createPlayerWithInvalidPieceCount() {
        assertThrows(IllegalArgumentException.class, () -> new Player("Test", Color.GREEN, 1));
        assertThrows(IllegalArgumentException.class, () -> new Player("Test", Color.YELLOW, 6));
    }

    @Test
    @DisplayName("플레이어 초기 점수는 0점")
    void initialScoreIsZero() {
        Player player = new Player("테스터", Color.RED, 4);
        assertEquals(0, player.getScore());
    }

    @Test
    @DisplayName("HumanPlayer chooseMove 호출 시 View 메서드 호출 검증")
    void humanPlayer_chooseMove_CallsView() {
        Player player = new Player("플레이어 1", Color.RED, 4);
        GameView mockView = mock(GameView.class); // Mockito 사용
        List<Move> moves = Collections.emptyList(); // 테스트용 빈 리스트

        player.chooseMove(moves, mockView);

        // verify: mockView의 promptMoveChoice가 정확히 1번 호출되었는지 확인
        verify(mockView, times(1)).promptMoveChoice(moves, player);
    }
}