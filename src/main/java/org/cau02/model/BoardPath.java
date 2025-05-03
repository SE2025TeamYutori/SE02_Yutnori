package org.cau02.model;

import java.util.Iterator;
import java.util.List;

public class BoardPath implements Iterable<BoardSpace> {
    private final List<BoardSpace> path;

    public BoardSpace get(int index) {
        return path.get(index);
    }

    public int size() {
        return path.size();
    }

    public int indexOf(BoardSpace space) {
        return path.indexOf(space);
    }

    BoardPath(List<BoardSpace> path) {
        this.path = List.copyOf(path);
    }

    @Override
    public Iterator<BoardSpace> iterator() {
        return List.copyOf(path).iterator();
    }
}
