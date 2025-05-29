# SE02_Yutnori
for Software Engineering class Team project (2025)

## 개발 환경
- OpenJDK 24
- Apache Maven
- 패키징: IntelliJ & Launch4j
- 테스트 프레임워크: JUnit 5

## 실행 환경
- [[이 링크]](https://github.com/SE2025TeamYutori/SE02_Yutnori/releases/tag/Release)에서 다운로드 후 실행
- Java 24 이상 필요
- 혹은 에디터에서 각 Main.java 실행

## 주요 패키지 설명

### model-module (`org.cau02.model`)
- 역할: 윷놀이 게임의 상태 및 규칙을 관리하는 로직
- 주요 클래스:
  <ul>
    <li>Piece: 윷놀이 게임말의 상태와 위치정보를 관리</li>
    <li>Board: 윷놀이 게임판의 추상 클래스, 이를 상속하는 클래스들이 다각형 게임판 지원</li>
    <li>GameManager: 윷놀이 게임의 흐름(턴의 관리, 윷 던지기 결과 처리, 게임말 이동의 검사, 말의 골인 등)을 제어
    </li>
  </ul>


### swing-module
- 역할: Swing 기반의 GUI 지원을 위한 view, controller 지원


### javafx-module
- 역할: JavaFX 기반의 GUI 지원을 위한 view, controller 지원
### swing/javafx-module 내부 패키지들
- ### 1️⃣ view (`org.cau02.controller`)
- 역할: 사용자 인터페이스 제공
- 주요 클래스:
  <ul>
    <li>(Swing/JavaFX)GameView: GUI 프레임워크 별 핵심 게임 UI를 구성하고 사용자와 게임 로직간의 인터페이스 역할 수행</li>
    <li>ui/BoardPanel: 윷놀이 게임판의 시각화</li>
    <li>ui/ControlPanel: 윷놀이 게임의 UI(윷 던지기나 게임말의 이동)를 위한 패널</li>
    <li>ui/MainGamePanel: 윷놀이 게임의 모든 GUI 컴포넌트의 중심 역할
    <br>->N각형 윷놀이판 설정에 따른 UI 변경, 게임 설정 입력/시작, BoardPanel/ControlPanel과 연동한 사용자 조작 처리 등</li>
  </ul>

- ### 2️⃣ controller (`org.cau02.view`)
- 역할: 게임 로직 제어와 view, model 간 연결 지원
- 주요 클래스: 
  <ul>
    <li>(Swing/JavaFX)GameController: controller의 핵심 기능 수행</li>
  </ul>
