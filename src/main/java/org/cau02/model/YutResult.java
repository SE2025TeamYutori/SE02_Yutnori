package org.cau02.model;

/** 윷 던지기 결과 */
public enum YutResult {
    BACK_DO(-1, false, "백도"), // 뒤로 1칸
    DO(1, false, "도"),
    GAE(2, false, "개"),
    GEOL(3, false, "걸"),
    YUT(4, true, "윷"),      // 한번 더 던짐
    MO(5, true, "모");       // 한번 더 던짐

    private final int spaces;   // 이동 칸 수
    private final boolean bonus;  // 추가 던지기 여부
    private final String name;    // 결과 이름

    YutResult(int spaces, boolean bonus, String name) {
        this.spaces = spaces;
        this.bonus = bonus;
        this.name = name;
    }

    public int getSpaces() { return spaces; }
    public boolean isBonus() { return bonus; }
    public String getName() { return name; }
}