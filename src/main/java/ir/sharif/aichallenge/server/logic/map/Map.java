package ir.sharif.aichallenge.server.logic.map;

import ir.sharif.aichallenge.server.logic.entities.TargetType;
import ir.sharif.aichallenge.server.logic.entities.Unit;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Map {
    HashMap<Integer, Path> paths;

    UnitsInCellsCollection unitsInCell;
    ArrayList<Unit> kings;

    public Map(int width, int height) {
        unitsInCell = new UnitsInCellsCollection(width, height);
        paths = new HashMap<>();
    }

    public int getHeight() {
        return unitsInCell.getHeight();
    }

    public int getWidth() {
        return unitsInCell.getWidth();
    }

    public void addPath(Path path) {
        this.paths.put(path.getId(), path);
    }

    public void putUnit(Unit unit, int pathId) {
        final Path path = this.paths.get(pathId);
        unit.setPosition(new PathCell(path, 0));
        unitsInCell.add(unit);
    }

    public void moveUnit(Unit unit, PathCell target) {
        unitsInCell.remove(unit);
        unit.setPosition(target);
        unitsInCell.add(unit);
    }

    public void removeUnit(Unit unit) {
        unitsInCell.remove(unit);
    }

    public Stream<Unit> getUnits(int row, int col) {
        return unitsInCell.getUnits(row, col);
    }

    public Stream<Unit> getUnitsInRange(int centerRow, int centerCol, int range) {
        //Implement using getUnitsInArea or getUnitsInRangeManhattan
        throw new UnsupportedOperationException();
    }

    public Stream<Unit> getUnitsInArea(int startRow, int startCol, int endRow, int endCol) {
        throw new UnsupportedOperationException();

    }

    public Stream<Unit> getUnitsInManhattanRange(Cell cell, int range) {
        return getUnitsInManhattanRange(cell.row, cell.col, range);
    }

    public Stream<Unit> getUnitsInManhattanRange(int centerRow, int centerCol, int range) {
        return IntStream.rangeClosed(0, range).boxed()
                .flatMap(i -> IntStream.rangeClosed(-i, i).boxed()
                        .flatMap(j -> getUnits(centerRow + j, centerCol + i - Math.abs(j))));
    }

    public Optional<Unit> getNearestTargetUnit(int centerRow, int centerCol, int range, TargetType targetType) {
        Stream<Unit> units = getUnitsInRange(centerRow, centerCol, range);
        if (targetType != TargetType.BOTH)
            units = units.filter(unit -> unit.getMoveType().value == targetType.value);
        return units.findFirst();
    }
}
