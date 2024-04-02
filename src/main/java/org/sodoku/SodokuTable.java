package org.sodoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;

public class SodokuTable {

  private static final int SODOKU_SIZE = 9;

  private static final int SECTION_SIZE = (int)Math.sqrt(SODOKU_SIZE);

  private static final Set<Integer> ALL_ELEMENTS =
      IntStream.rangeClosed(1, SODOKU_SIZE).boxed().collect(Collectors.toSet());

  private final SimpleMatrix table;

  public SodokuTable() {
    table = new SimpleMatrix(SODOKU_SIZE, SODOKU_SIZE);
  }

  public SodokuTable(SimpleMatrix rotatedTable) {
    this.table = rotatedTable;
  }

  public static SodokuTable generate() {
    Random random = new Random();
    while (true) {
      try {
        SodokuTable sodokuTable = new SodokuTable();
        for (int row = 0; row < SODOKU_SIZE; row++) {
          for (int col = 0; col < SODOKU_SIZE; col++) {
            Set<Integer> rowElements = sodokuTable.getRowElements(row);
            Set<Integer> colElements = sodokuTable.getColumnElements(col);
            Set<Integer> sectionElements = sodokuTable.getSectionElements(row, col);
            Set<Integer> missingElements = sodokuTable.getMissingElements(rowElements, colElements, sectionElements);
            int randomElement = getRandomElement(missingElements, random);
            sodokuTable.table.set(row, col, randomElement);
          }
        }
        return sodokuTable;
      } catch (Exception e) {
        System.out.println("retrying...");
      }
    }
  }

  private static int getRandomElement(Set<Integer> set, Random random) {
    return new ArrayList<>(set).get(random.nextInt(set.size()));
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
    return IntStream.range(0, SODOKU_SIZE).mapToObj(col -> (int)table.get(row, col)).collect(Collectors.toSet());
  }

  private Set<Integer> getColumnElements(int col) {
    return IntStream.range(0, SODOKU_SIZE).mapToObj(row -> (int)table.get(row, col)).collect(Collectors.toSet());
  }

  private Set<Integer> getSectionElements(int row, int col) {
    int startRow = (row / SECTION_SIZE) * SECTION_SIZE;
    int startCol = (col / SECTION_SIZE) * SECTION_SIZE;
    Set<Integer> sectionElements = new HashSet<>();
    for (int i = startRow; i < startRow + SECTION_SIZE; i++) {
      for (int j = startCol; j < startCol + SECTION_SIZE; j++) {
        sectionElements.add((int)table.get(i, j));
      }
    }
    return sectionElements;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < SODOKU_SIZE; i++) {
      for (int j = 0; j < SODOKU_SIZE; j++) {
        sb.append((int)table.get(i, j)).append(' ');
        if ((j + 1) % SECTION_SIZE == 0) {
          sb.append(' ');
        }
      }
      sb.append('\n');
      if ((i + 1) % SECTION_SIZE == 0) {
        sb.append('\n');
      }
    }
    sb.append("-------------------\n");
    return sb.toString();
  }

  private SodokuTable rotateLeft() {
    SimpleMatrix rotatedTable = table.transpose();
    for (int row = 0; row < SODOKU_SIZE; row++) {
      for (int col = 0; col < SODOKU_SIZE / 2; col++) {
        double temp = rotatedTable.get(row, col);
        rotatedTable.set(row, col, rotatedTable.get(row, SODOKU_SIZE - col - 1));
        rotatedTable.set(row, SODOKU_SIZE - col - 1, temp);
      }
    }
    return new SodokuTable(rotatedTable);
  }

  private SodokuTable mirrorHorizontal() {
    SimpleMatrix mirroredTable = new SimpleMatrix(SODOKU_SIZE, SODOKU_SIZE);
    for (int row = 0; row < SODOKU_SIZE; row++) {
      for (int col = 0; col < SODOKU_SIZE; col++) {
        mirroredTable.set(row, SODOKU_SIZE - col - 1, table.get(row, col));
      }
    }
    return new SodokuTable(mirroredTable);
  }

  private SodokuTable mirrorVertical() {
    SimpleMatrix mirroredTable = new SimpleMatrix(SODOKU_SIZE, SODOKU_SIZE);
    for (int row = 0; row < SODOKU_SIZE; row++) {
      for (int col = 0; col < SODOKU_SIZE; col++) {
        mirroredTable.set(SODOKU_SIZE - row - 1, col, table.get(row, col));
      }
    }
    return new SodokuTable(mirroredTable);
  }

  public static void main(String[] args) {
    SodokuTable generatedMatrix = SodokuTable.generate();
    System.out.println(generatedMatrix);
    System.out.println(generatedMatrix.mirrorHorizontal());
    System.out.println(generatedMatrix.mirrorVertical());
    System.out.println(generatedMatrix.mirrorVertical().mirrorHorizontal());
    System.out.println(generatedMatrix = generatedMatrix.rotateLeft());
    System.out.println(generatedMatrix = generatedMatrix.rotateLeft());
    System.out.println(generatedMatrix = generatedMatrix.rotateLeft());
  }
}
