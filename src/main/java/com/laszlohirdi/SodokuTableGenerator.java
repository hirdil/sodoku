package com.laszlohirdi;

public class SodokuTableGenerator {

  public static void main(String[] args) {
    SodokuTable sodokuTable = SodokuTable.generate();
    System.out.print(sodokuTable);
  }
}
