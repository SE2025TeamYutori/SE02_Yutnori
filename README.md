# SE02_Yutnori
for Software Engineering class Team project (2025)

## 개발 환경
- JDK 24
- Appache Maven
- 테스트 프레임워크: JUnit 5

## 실행 환경
- Open JDK 24 이상
- 실행 방법:
  <ol type="1">
  <li>프로젝트의 Main.java 실행(<code>src/main/java/org/cau02/Main.java</code>)</li>
  <li>GUI 실행 창에서 게임 시작 가능</li>
  <li>버튼을 이용해 윷 던지기, 말 선택, 말 이동 등의 조작</li>
  </ol>

## 주요 패키지 설명

### model (`org.cau02.model`)
- 역할: 윷놀이 게임의 상태 및 규칙을 관리하는 로직
- 주요 클래스:
  <ul>
    <li>Piece: 윷놀이 게임말의 상태와 위치정보를 관리</li>
    <li>Board: 윷놀이 게임판의 추상 클래스, 이를 상속하는 클래스들이 다각형 게임판 지원</li>
    <li>GameManager: 윷놀이 게임의 흐름(턴의 관리, 윷 던지기 결과 처리, 게임말 이동의 검사, 말의 골인 등)을 제어
    </li>
  </ul>
### view (`org.cau02.controller`)
- 역할: 사용자 인터페이스 제공
- 주요 클래스:
  <ul>
    <li>BoardPanel: 윷놀이 게임판의 시각화</li>
    <li>ControlPanel: 윷놀이 게임의 UI(윷 던지기나 게임말의 이동)를 위한 패널</li>
    <li>MainGamePanel: 윷놀이 게임의 모든 GUI 컴포넌트의 중심 역할
    <br>->N각형 윷놀이판 설정에 따른 UI 변경, 게임 설정 입력과 시작, BoardPanel/ControlPanel과 연동한 사용자 조작 처리 등</li>
  </ul>

### controller (`org.cau02.view`)
- 역힐: 게임 로직 제어와 view, model 간 연결 지원
- 주요 클래스: 
