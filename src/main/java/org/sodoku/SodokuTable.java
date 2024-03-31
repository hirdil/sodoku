package org.sodoku;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SodokuTable {

  private static final int SODOKU_SIZE = 9;

  private static final int SECTION_SIZE = (int)Math.sqrt(SODOKU_SIZE);

  private static final Set<Integer> ALL_ELEMENTS =
      IntStream.rangeClosed(1, SODOKU_SIZE).boxed().collect(Collectors.toSet());

  private final int[][] table = new int[SODOKU_SIZE][SODOKU_SIZE];

  public static SodokuTable generate() {
    int tryCount = 0;
    Random random = new Random();
    while (true) {
      ++tryCount;
      ;
      try {
        SodokuTable sodokuTable = new SodokuTable();

        for (int row = 0; row < SODOKU_SIZE; row++) {
          for (int col = 0; col < SODOKU_SIZE; col++) {
            Set<Integer> rowElements = sodokuTable.getRowElements(row);
            Set<Integer> colElements = sodokuTable.getColumnElements(col);
            Set<Integer> sectionElements = sodokuTable.getSectionElements(row, col);
            Set<Integer> missingElements = sodokuTable.getMissingElements(rowElements, colElements, sectionElements);
            int randomElement = getRandomElement(missingElements, random);
            sodokuTable.table[row][col] = randomElement;
          }
        }

        System.out.println("Try count:" + tryCount);
        return sodokuTable;
      } catch (Exception e) {
        System.out.println("retrying...");
      }
    }
  }

  private static int getRandomElement(Set<Integer> set, Random random) {
    List<Integer> list = new ArrayList<>(set);
    return list.get(random.nextInt(list.size()));
  }

  private Set<Integer> getMissingElements(Set<Integer> rowElements, Set<Integer> colElements,
                                          Set<Integer> sectionElements) {
    Set<Integer> missingElements = new HashSet<>(ALL_ELEMENTS);
    missingElements.removeAll(rowElements);
    missingElements.removeAll(colElements);
    missingElements.removeAll(sectionElements);
    return missingElements;
  }

  private Set<Integer> getRowElements(int row) {
    return Arrays.stream(table[row]).boxed().collect(Collectors.toSet());
  }

  private Set<Integer> getColumnElements(int col) {
    return IntStream.range(0, SODOKU_SIZE).map(i -> table[i][col]).boxed().collect(Collectors.toSet());
  }

  private Set<Integer> getSectionElements(int row, int col) {
    int startRow = (row / SECTION_SIZE) * SECTION_SIZE;
    int startCol = (col / SECTION_SIZE) * SECTION_SIZE;

    Set<Integer> sectionElements = new HashSet<>();
    for (int i = startRow; i < startRow + SECTION_SIZE; i++) {
      for (int j = startCol; j < startCol + SECTION_SIZE; j++) {
        sectionElements.add(table[i][j]);
      }
    }

    return sectionElements;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < SODOKU_SIZE; i++) {
      for (int j = 0; j < SODOKU_SIZE; j++) {
        sb.append(table[i][j]).append(' ');
        if ((j + 1) % SECTION_SIZE == 0) {
          sb.append(' ');
        }
      }
      sb.append('\n');
      if ((i + 1) % SECTION_SIZE == 0) {
        sb.append('\n');
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    System.out.println(SodokuTable.generate());
  }
}
