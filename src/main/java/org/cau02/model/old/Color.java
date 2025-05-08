package org.cau02.model.old;

/** 플레이어 또는 말을 구분하기 위한 색깔 -> 굳이 쓸 필요없긴 합니다..*/
public enum Color {
    RED("빨강"), BLUE("파랑"), GREEN("초록"), YELLOW("노랑");

    private final String name;
    Color(String name) { this.name = name; }
    public String getName() { return name; }
}