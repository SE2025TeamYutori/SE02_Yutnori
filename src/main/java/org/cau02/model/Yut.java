package org.cau02.model;

/**
 * 윷 족보를 나타냅니다.
 * ordinal 순서
 * {@link Yut#BACKDO} {@link Yut#DO} {@link Yut#GE} {@link Yut#GEOL} {@link Yut#YUT} {@link Yut#MO}
 */
public enum Yut {
    BACKDO(-1),
    DO(1),
    GE(2),
    GEOL(3),
    YUT(4),
    MO(5);

    final private int value;
    /**
     * 족보가 몇 칸 움직이는지를 반환합니다.
     * @return 족보가 몇 칸 움직이는지
     */
    public int getValue() {
        return value;
    }

    Yut(int value){
        this.value = value;
    }
}
