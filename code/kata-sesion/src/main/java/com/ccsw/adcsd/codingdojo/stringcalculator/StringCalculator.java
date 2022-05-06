package com.ccsw.adcsd.codingdojo.stringcalculator;

/**
 * Kata String Calculator
 */
@SuppressWarnings("javadoc")
public class StringCalculator {

  /**
   *
   */
  private static final String DEFAULT_DELIMITERS = ",|\n";

  public int add(String input) {

    int suma = 0;

    if (!input.isEmpty()) {

      String delimiters = obtenerDelimitadores(input);

      String[] numeros = obtenerNumeros(input, delimiters);

      suma = sumaNumeros(numeros);
    }

    return suma;

  }

  /**
   * @param input
   * @param delimiters
   * @return
   */
  private String[] obtenerNumeros(String input, String delimiters) {

    if (input.startsWith("//")) {
      int index = input.indexOf('\n');
      input = input.substring(index + 1);
    }

    return input.split(delimiters);
  }

  /**
   * @param input
   * @return
   */
  private String obtenerDelimitadores(String input) {

    String delimiters = DEFAULT_DELIMITERS;

    if (input.startsWith("//")) {
      int index = input.indexOf('\n');
      delimiters = input.substring(2, index);
    }
    return delimiters;
  }

  /**
   * @param suma
   * @param numeros
   * @return
   */
  private int sumaNumeros(String[] numeros) {

    int suma = 0;
    for (int i = 0; i < numeros.length; i++) {
      suma += Integer.parseInt(numeros[i]);
    }
    return suma;
  }

}
