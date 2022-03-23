package com.capgemini.adcsd.codingdojo.stringcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kata String Calculator
 */
@SuppressWarnings("javadoc")
public class StringCalculator {

  private static final String EXTRACT_DELIMITER_REGEX = "\\[(.*?)\\]";

  private static final String END_LONG_DELIMITER_TOKEN = "]";

  private static final String START_LONG_DELIMITER_TOKEN = "[";

  private static final int MAXIMUM_NUMBER_ALLOWED = 1000;

  private static final String END_DELIMITER_TOKEN = "\n";

  private static final String START_DELIMITER_TOKEN = "//";

  private static final String DEFAULT_DELIMITER = ",|\n";

  private static final String EMPTY_INPUT = "";

  private static final int EMPTY_RESULT = 0;

  public int add(String input) {

    if (EMPTY_INPUT.equals(input))
      return EMPTY_RESULT;

    String delimiter = extractDelimiter(input);
    String[] numbers = extractNumbers(input, delimiter);

    checkNegativeNumbers(numbers);

    return addNumbers(numbers);
  }

  private void checkNegativeNumbers(String[] numbers) {

    List<String> negativeNumbers = getNegativeNumbers(numbers);

    if (negativeNumbers.isEmpty())
      return;

    throw new RuntimeException("negativos no soportados: " + convertListToString(negativeNumbers));
  }

  private List<String> getNegativeNumbers(String[] numbers) {

    List<String> negativeNumbers = new ArrayList<>();
    for (String number : numbers) {
      if (isNegative(number))
        negativeNumbers.add(number);
    }
    return negativeNumbers;
  }

  private boolean isNegative(String number) {

    return Integer.parseInt(number) < 0;
  }

  private String convertListToString(List<String> negativeNumbers) {

    StringBuilder sb = new StringBuilder();
    for (String number : negativeNumbers) {
      sb.append(number);
      sb.append(",");
    }

    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  private String[] extractNumbers(String input, String delimiter) {

    if (input.startsWith(START_DELIMITER_TOKEN)) {
      int endIndex = input.indexOf(END_DELIMITER_TOKEN);
      input = input.substring(endIndex + END_DELIMITER_TOKEN.length());
    }

    return input.split(delimiter);
  }

  private String extractDelimiter(String input) {

    if (isDefaultDelimiter(input))
      return DEFAULT_DELIMITER;

    int endIndex = input.indexOf(END_DELIMITER_TOKEN);
    String delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

    if (isLongDelimiter(delimiter)) {

      delimiter = extractMultipleDelimiterFromRegex(delimiter);
    }

    return delimiter;
  }

  private String extractMultipleDelimiterFromRegex(String delimiter) {

    Pattern regexPattern = Pattern.compile(EXTRACT_DELIMITER_REGEX);
    Matcher regexMatcher = regexPattern.matcher(delimiter);

    StringBuilder regexDelimiter = new StringBuilder();
    while (regexMatcher.find()) {
      String singleDelimiter = regexMatcher.group(1);
      regexDelimiter.append(singleDelimiter);
      regexDelimiter.append("|");
    }

    regexDelimiter.deleteCharAt(regexDelimiter.length() - 1);
    delimiter = regexDelimiter.toString();

    return delimiter;
  }

  private boolean isDefaultDelimiter(String input) {

    return input.startsWith(START_DELIMITER_TOKEN) == false;
  }

  private boolean isLongDelimiter(String delimiter) {

    return delimiter.startsWith(START_LONG_DELIMITER_TOKEN) && delimiter.endsWith(END_LONG_DELIMITER_TOKEN);
  }

  private int addNumbers(String numbers[]) {

    int total = 0;
    for (String number : numbers) {
      int parsedNumber = Integer.parseInt(number);

      if (parsedNumber <= MAXIMUM_NUMBER_ALLOWED)
        total += Integer.parseInt(number);
    }

    return total;
  }

}
