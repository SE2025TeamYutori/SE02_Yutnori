package org.cau02.model;

public class HexagonBoard extends RegularBoard {
    HexagonBoard() {
        super(6);
    }

    @Override
    BoardPath computeNextPath(BoardPath path, BoardSpace space) throws IllegalArgumentException {
        if (!spaces.contains(space) || !paths.contains(path)) {
            throw new IllegalArgumentException();
        }

        return switch (paths.indexOf(path)) {
            case 0 -> switch (spaces.indexOf(space)) {
                case 5 -> paths.get(1);
                case 10 -> paths.get(3);
                case 15 -> paths.get(5);
                case 20 -> paths.get(7);
                default -> path;
            };
            case 1 -> switch (spaces.indexOf(space)) {
                case 42 -> paths.get(2);
                case 4 -> paths.get(0);
                default -> path;
            };
            case 2 -> switch (spaces.indexOf(space)) {
                case 31 -> paths.get(1);
                default -> path;
            };
            case 3 -> switch (spaces.indexOf(space)) {
                case 42 -> paths.get(4);
                case 9 -> paths.get(0);
                default -> path;
            };
            case 4 -> switch (spaces.indexOf(space)) {
                case 33 -> paths.get(3);
                default -> path;
            };
            case 5 -> switch (spaces.indexOf(space)) {
                case 42 -> paths.get(6);
                case 14 -> paths.get(0);
                default -> path;
            };
            case 6 -> switch (spaces.indexOf(space)) {
                case 35 -> paths.get(5);
                default -> path;
            };
            case 7 -> switch (spaces.indexOf(space)) {
                case 19 -> paths.get(0);
                default -> path;
            };
            default -> throw new IllegalArgumentException();
        };
    }
}
