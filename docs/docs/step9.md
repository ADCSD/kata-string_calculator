# Desarrollo del requisito 9

## Requisito

!!! success "Asegúrate de que puedes manejar delimitadores de cualquier longitud mayor de un caracter."

## Desarrollo

Este requisito es muy sencillo, vamos a añadirlo en el último test que hicimos, ya que es un caso de prueba más pero dentro del mismo test.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void multiple_any_length_delimiter() {

      Assert.assertEquals(6, this.calculator.add("//[#][%]\n1#2%3"));
      Assert.assertEquals(6, this.calculator.add("//[;;][##]\n1;;2##3"));
    }
    ```

Si lo ejecutamos, el test funciona correctamente porque ya lo habíamos implementado para múltiples delimitadores con el tamaño variable. Así que ya tenemos toda la kata terminada. 


## Resultado final de la Kata

El código final del ejercicio nos ha quedado así:


=== "TestStringCalculator"
    ``` Java linenums="1" 
    public class TestStringCalculator {

      @Rule
      public final ExpectedException exception = ExpectedException.none();

      private StringCalculator calculator = new StringCalculator();

      @Test
      public void empty_string_return_zero() {

        Assert.assertEquals(0, this.calculator.add(""));
      }

      @Test
      public void one_number_return_this_number() {

        Assert.assertEquals(1, this.calculator.add("1"));
        Assert.assertEquals(2, this.calculator.add("2"));
        Assert.assertEquals(13, this.calculator.add("13"));
      }

      @Test
      public void two_comma_separated_number_return_sum() {

        Assert.assertEquals(3, this.calculator.add("1,2"));
        Assert.assertEquals(40, this.calculator.add("17,23"));
      }

      @Test
      public void multiple_comma_separated_number_return_sum() {

        Assert.assertEquals(6, this.calculator.add("1,2,3"));
        Assert.assertEquals(25, this.calculator.add("4,5,6,10"));
        Assert.assertEquals(51, this.calculator.add("10,10,10,10,10,1"));
      }

      @Test
      public void comma_or_return_line_separated_numbers_return_sum() {

        Assert.assertEquals(3, this.calculator.add("1\n2"));
        Assert.assertEquals(6, this.calculator.add("1\n2,3"));
      }

      @Test
      public void any_delimited_numbers_return_sum() {

        Assert.assertEquals(3, this.calculator.add("//;\n1;2"));
        Assert.assertEquals(6, this.calculator.add("//d\n1d2d3"));
      }

      @Test
      public void negative_number_throw_exception() {

        this.exception.expect(RuntimeException.class);
        this.exception.expectMessage("negativos no soportados: -1");

        this.calculator.add("-1");
      }

      @Test
      public void multiple_negative_number_throw_exception() {

        this.exception.expect(RuntimeException.class);
        this.exception.expectMessage("negativos no soportados: -1,-2");

        this.calculator.add("-1,-2");
      }

      @Test
      public void numbers_greater_than_thousand_not_sum() {

        Assert.assertEquals(3, this.calculator.add("1,2,1001"));
      }

      @Test
      public void any_length_delimiter() {

        Assert.assertEquals(6, this.calculator.add("//[;;;]\n1;;;2;;;3"));
      }

      @Test
      public void multiple_any_length_delimiter() {

        Assert.assertEquals(6, this.calculator.add("//[#][%]\n1#2%3"));
        Assert.assertEquals(6, this.calculator.add("//[;;][##]\n1;;2##3"));
      }

    }    
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
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
    ```
