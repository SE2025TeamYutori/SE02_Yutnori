import org.cau02.model.GenericBoardConfiguration;
import org.cau02.model.PathNode;
import org.cau02.model.interfaces.IBoardConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/** GenericBoardConfiguration 테스트 */
class BoardTest {

    @Test
    @DisplayName("표준 4각형 윷판 외곽 노드 개수 확인")
    void standardBoardNodeCount() {
        // 4각형, 변당 노드 5개 -> 외곽 4*5=20 + 시작/끝(0번) 1 = 21개 노드
        IBoardConfiguration config = new GenericBoardConfiguration(4, 5);
        List<PathNode> path = config.generatePathLayout();
        assertEquals(21, path.size());
    }

    @Test
    @DisplayName("삼각형 윷판 외곽 노드 개수 확인")
    void triangleBoardNodeCount() {
        // 3각형, 변당 노드 3개 -> 외곽 3*3=9 + 시작/끝(0번) 1 = 10개 노드
        IBoardConfiguration config = new GenericBoardConfiguration(3, 3);
        List<PathNode> path = config.generatePathLayout();
        assertEquals(10, path.size());
    }

    @Test
    @DisplayName("잘못된 설정 값으로 생성 시 예외 발생")
    void invalidConfigThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new GenericBoardConfiguration(2, 5)); // 최소 3변
        assertThrows(IllegalArgumentException.class, () -> new GenericBoardConfiguration(4, 0)); // 변당 최소 1노드
    }
}