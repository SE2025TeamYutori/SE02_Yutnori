package org.cau02.model;

/**
 * 윷 족보를 나타냅니다.
 * ordinal 순서
 * {@link Yut#BACKDO} {@link Yut#DO} {@link Yut#GE} {@link Yut#GEOL} {@link Yut#YUT} {@link Yut#MO}
 */
public enum Yut {
    BACKDO(-1, "빽도"),
    DO(1, "도"),
    GE(2, "개"),
    GEOL(3, "걸"),
    YUT(4, "윷"),
    MO(5, "모");

    final private int value;
    /**
     * 족보가 몇 칸 움직이는지를 반환합니다.
     * @return 족보가 몇 칸 움직이는지
     */
    public int getValue() {
        return value;
    }

    final private String koreanName;
    /**
     * 해당 족보의 한글 이름을 반환합니다.
     * @return 족보의 한글 이름
     */
    public String getKoreanName() {
        return koreanName;
    }

    Yut(int value, String koreanName){
        this.value = value;
        this.koreanName = koreanName;
    }
}
