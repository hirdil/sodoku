package com.laszlohirdi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SodokuTable {
  private static final int SODOKU_SIZE = 9;

  private static final int SECTION_SIZE = (int)Math.sqrt(SODOKU_SIZE);

  private Set<Integer> ALL_ELEMENTS =
      Collections.unmodifiableSet(
          IntStream.range(1, SODOKU_SIZE + 1).boxed().collect(Collectors.toSet()));

  final int[][] _table = new int[SODOKU_SIZE][SODOKU_SIZE];

  public static SodokuTable generate() {
    int tryCount = 0;
    while (++tryCount > 0) {
      SodokuTable sodokuTable = null;
      try {
        sodokuTable = new SodokuTable();
        Random ran = new Random();
        for (int v = 1; v <= SODOKU_SIZE; v++) {
          for (int h = 1; h <= SODOKU_SIZE; h++) {
            final Set<Integer> missingElementsFromRow = sodokuTable.getMissingElementsFromRow(v);
            final Set<Integer> missingElementsFromColumn = sodokuTable.getMissingElementsFromColumn(h);
            final Set<Integer> missingElementsFromSection = sodokuTable.getMissingElementsFromSection(v, h);
            final Set<Integer> missingElements = sodokuTable
                .getMissingElements(missingElementsFromRow, missingElementsFromColumn,
                    missingElementsFromSection);
            final int generatedElement =
                missingElements.stream().skip(ran.nextInt(missingElements.size())).findFirst().get();
            sodokuTable._table[v - 1][h - 1] = generatedElement;
          }
        }
        System.out.println("Try count:" + tryCount);
        return sodokuTable;
      } catch (Exception e) {
        System.out.println(e);
        System.out.println(sodokuTable);
      }
    }
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder representation = new StringBuilder();
    for (int v = 1; v <= SODOKU_SIZE; v++) {
      for (int h = 1; h <= SODOKU_SIZE; h++) {
        representation.append(_table[v - 1][h - 1]);
        if (h % SECTION_SIZE == 0) {
          representation.append(' ');
        }
      }
      representation.append('\n');
      if (v % SECTION_SIZE == 0) {
        representation.append('\n');
      }
    }
    return representation.toString();
  }

  private Set<Integer> getMissingElementsFromRow(int row) {
    final Set<Integer> currentElements = getCurrentElements(row, 1, row, SODOKU_SIZE);
    return getMissingElements(currentElements);
  }

  private Set<Integer> getMissingElementsFromColumn(int column) {
    final Set<Integer> currentElements = getCurrentElements(1, column, SODOKU_SIZE, column);
    return getMissingElements(currentElements);
  }

  private Set<Integer> getMissingElementsFromSection(int row, int column) {
    final int vTop;
    int rowMod = row % SECTION_SIZE;
    if (rowMod == 0) {
      vTop = row - SECTION_SIZE + 1;
    } else {
      vTop = row - rowMod + 1;
    }

    final int hTop;
    int columnMod = column % SECTION_SIZE;
    if (columnMod == 0) {
      hTop = column - SECTION_SIZE + 1;
    } else {
      hTop = column - columnMod + 1;
    }

    final int vBottom = vTop + SECTION_SIZE - 1;
    final int hBottom = hTop + SECTION_SIZE - 1;
    final Set<Integer> currentElements = getCurrentElements(vTop, hTop, vBottom, hBottom);
    return getMissingElements(currentElements);
  }

  private Set<Integer> getMissingElements(Set<Integer> currentElements) {
    final Set<Integer> missingElements = new HashSet<>(ALL_ELEMENTS);
    missingElements.removeAll(currentElements);
    return missingElements;
  }

  private Set<Integer> getMissingElements(Set<Integer> missingElements1, Set<Integer> missingElements2,
                                          Set<Integer> missingElements3) {
    final Set<Integer> missingElements = new HashSet<>(missingElements1);
    missingElements.retainAll(missingElements2);
    missingElements.retainAll(missingElements3);
    return missingElements;
  }

  private Set<Integer> getCurrentElements(int vTop, int hTop, int vBottom, int hBottom) {
    final Set<Integer> currentElements = new HashSet<>();
    for (int v = vTop; v <= vBottom; v++) {
      for (int h = hTop; h <= hBottom; h++) {
        int element = _table[v - 1][h - 1];
        if (element != 0) {
          currentElements.add(element);
        }
      }
    }
    return currentElements;
  }

  public static void main(String[] args) {
    final SodokuTable sodokuTable = new SodokuTable();
    System.out.println(SodokuTable.generate());
  }
}
