package org.cau02.model;

public enum Yut {
    BACKDO(-1),
    DO(1),
    GE(2),
    GEOL(3),
    YUT(4),
    MO(5);

    final private int value;
    public int getValue() {
        return value;
    }

    Yut(int value){
        this.value = value;
    }
}
