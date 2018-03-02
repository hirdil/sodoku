package com.laszlohirdi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SodokuTable {

  private static final int SODOKU_SIZE = 9;

  private static final int SECTION_SIZE = (int)Math.sqrt(SODOKU_SIZE);

  private static final Set<Integer> ALL_ELEMENTS =
      Collections.unmodifiableSet(
          IntStream.range(1, SODOKU_SIZE + 1).boxed().collect(Collectors.toSet()));

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final int[][] table = new int[SODOKU_SIZE][SODOKU_SIZE];

  public static SodokuTable generate() {
    int tryCount = 0;
    while (true) {
      SodokuTable sodokuTable = null;
      try {
        sodokuTable = new SodokuTable();
        final Random ran = new Random();
        for (int v = 1; v <= SODOKU_SIZE; v++) {
          for (int h = 1; h <= SODOKU_SIZE; h++) {
            final Set<Integer> missingElementsFromRow = sodokuTable.getMissingElementsFromRow(v);
            final Set<Integer> missingElementsFromColumn = sodokuTable.getMissingElementsFromColumn(h);
            final Set<Integer> missingElementsFromSection = sodokuTable.getMissingElementsFromSection(v, h);
            final Set<Integer> missingElements = sodokuTable
                .getMissingElements(missingElementsFromRow, missingElementsFromColumn,
                    missingElementsFromSection);
            final Optional<Integer> optionalElement =
                missingElements.stream().skip(ran.nextInt(missingElements.size())).findFirst();
            final int generatedElement = optionalElement.get();
            sodokuTable.table[v - 1][h - 1] = generatedElement;
          }
        }
        System.out.println("Try count:" + tryCount);
        return sodokuTable;
      } catch (Exception e) {
        System.out.println(e);
        System.out.println(sodokuTable);
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder representation = new StringBuilder();
    for (int v = 1; v <= SODOKU_SIZE; v++) {
      for (int h = 1; h <= SODOKU_SIZE; h++) {
        representation.append(table[v - 1][h - 1]);
        if (h % SECTION_SIZE == 0) {
          representation.append(' ');
        }
      }
      representation.append(LINE_SEPARATOR);
      if (v % SECTION_SIZE == 0) {
        representation.append(LINE_SEPARATOR);
      }
    }
    return representation.toString();
  }

  private Set<Integer> getMissingElementsFromRow(final int row) {
    final Set<Integer> currentElements = getCurrentElements(row, 1, row, SODOKU_SIZE);
    return getMissingElements(currentElements);
  }

  private Set<Integer> getMissingElementsFromColumn(final int column) {
    final Set<Integer> currentElements = getCurrentElements(1, column, SODOKU_SIZE, column);
    return getMissingElements(currentElements);
  }

  private int getTopIndex(final int index) {
    final int topIndex;
    final int mod = index % SECTION_SIZE;
    if (mod == 0) {
      topIndex = index - SECTION_SIZE + 1;
    } else {
      topIndex = index - mod + 1;
    }
    return topIndex;
  }

  private Set<Integer> getMissingElementsFromSection(final int row, final int column) {
    final int vTop = getTopIndex(row);
    final int hTop = getTopIndex(column);
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

  private Set<Integer> getMissingElements(final Set<Integer> missingElements1, final Set<Integer> missingElements2,
                                          final Set<Integer> missingElements3) {
    final Set<Integer> missingElements = new HashSet<>(missingElements1);
    missingElements.retainAll(missingElements2);
    missingElements.retainAll(missingElements3);
    return missingElements;
  }

  private Set<Integer> getCurrentElements(final int vTop, final int hTop, final int vBottom, final int hBottom) {
    final Set<Integer> currentElements = new HashSet<>();
    for (int v = vTop; v <= vBottom; v++) {
      for (int h = hTop; h <= hBottom; h++) {
        final int element = table[v - 1][h - 1];
        if (element != 0) {
          currentElements.add(element);
        }
      }
    }
    return currentElements;
  }

  public static void main(final String[] args) {
    System.out.println(SodokuTable.generate());
  }
}
