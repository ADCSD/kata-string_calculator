# Desarrollo del requisito 5

## Requisito

!!! success "Llamar al método “add" con números negativos deberá lanzar una excepción con el texto "negativos no soportados" y el número negativo que ha sido pasado. Si hay múltiples números negativos, muestra todos ellos en el mensaje de la excepción."

## Desarrollo

Cuando queremos testear que un método nos devuelve una excepción, deberíamos crearnos una `@Rule exception` con el valor o mensaje que queremos capturar en el test, en lugar de utilizar los `Asserts`, como veníamos haciendo hasta ahora.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Rule
    public final ExpectedException exception = ExpectedException.none();

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
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String delimiter = extractDelimiter(input);
      String[] numbers = extractNumbers(input, delimiter);

      List<Integer> negativeNumbers = new ArrayList<>();
      for (String number : numbers) {
        int parsedNumber = Integer.parseInt(number);
        if (parsedNumber < 0)
          negativeNumbers.add(parsedNumber);
      }

      if (negativeNumbers.size() > 0) {

        StringBuilder sb = new StringBuilder();
        for (Integer number : negativeNumbers) {
          sb.append(number);
          sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);

        throw new RuntimeException("negativos no soportados: " + sb.toString());
      }

      return addNumbers(numbers);
    }
    ```

He decidido (como una posible solución entre muchas), hacer una comprobación previa a la suma que compare los números y si encuentra algún negativo devuelva una excepción. Con este código vuelven a aparecer varios `smells` que podemos `Refactorizar` de forma sencilla. 

=== "StringCalculator"
    ``` Java hl_lines="9-27" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String delimiter = extractDelimiter(input);
      String[] numbers = extractNumbers(input, delimiter);

      List<Integer> negativeNumbers = new ArrayList<>();                         /*(1)*/
      for (String number : numbers) {                                            /*(1)*/
        int parsedNumber = Integer.parseInt(number);                             /*(1)*/
        if (parsedNumber < 0)                                                    /*(1)*/
          negativeNumbers.add(parsedNumber);                                     /*(1)*/
      }                                                                          /*(1)*/

      if (negativeNumbers.size() > 0) {                                          /*(1)*/

        StringBuilder sb = new StringBuilder();                                  /*(1)*/
        for (Integer number : negativeNumbers) {                                 /*(1)*/
          sb.append(number);                                                     /*(1)*/
          sb.append(",");                                                        /*(1)*/
        }                                                                        /*(1)*/

        sb.deleteCharAt(sb.length() - 1);                                        /*(1)*/

        throw new RuntimeException("negativos no soportados: " + sb.toString()); /*(1)*/
      }                                                                          /*(1)*/

      return addNumbers(numbers);
    }
    ```
=== "StringCalculator-Ref1"
    ``` Java hl_lines="16 18 19 23 26 34" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String delimiter = extractDelimiter(input);
      String[] numbers = extractNumbers(input, delimiter);

      checkNegativeNumbers(numbers);

      return addNumbers(numbers);
    }

    private void checkNegativeNumbers(String[] numbers) {

      List<Integer> negativeNumbers = new ArrayList<>(); /*(2)*/
      for (String number : numbers) {
        int parsedNumber = Integer.parseInt(number);     /*(2)*/
        if (parsedNumber < 0)                            /*(2)*/
          negativeNumbers.add(parsedNumber);
      }

      if (negativeNumbers.size() > 0) {                  /*(3)*/

        StringBuilder sb = new StringBuilder();
        for (Integer number : negativeNumbers) {         /*(2)*/
          sb.append(number);
          sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);

        throw new RuntimeException("negativos no soportados: " + sb.toString());
      }                                                  /*(3)*/
    }
    ```
=== "StringCalculator-Ref2"
    ``` Java hl_lines="3-7 12-18" linenums="1"
    private void checkNegativeNumbers(String[] numbers) {

      List<String> negativeNumbers = new ArrayList<>();  /*(4)*/
      for (String number : numbers) {                    /*(4)*/
        if (isNegative(number))                          /*(4)*/
          negativeNumbers.add(number);                   /*(4)*/
      }                                                  /*(4)*/

      if (negativeNumbers.isEmpty())
        return;

      StringBuilder sb = new StringBuilder();            /*(5)*/
      for (String number : negativeNumbers) {            /*(5)*/
        sb.append(number);                               /*(5)*/
        sb.append(",");                                  /*(5)*/
      }                                                  /*(5)*/

      sb.deleteCharAt(sb.length() - 1);                  /*(5)*/

      throw new RuntimeException("negativos no soportados: " + sb.toString());
    }

    private boolean isNegative(String number) {

      return Integer.parseInt(number) < 0;
    }
    ```
=== "StringCalculator-Ref3"
    ``` Java linenums="1"
    private void checkNegativeNumbers(String[] numbers) {

      List<String> negativeNumbers = getNegativeNumbers(numbers);

      if (negativeNumbers.isEmpty())
        return;

      throw new RuntimeException("negativos no soportados: " + convertListToString(negativeNumbers));
    }

    private List<String> getNegativeNumbers(String[] numbers) {

      

      List<String> negativeNumbers = new ArrayList<>();
      for (String number : numbers) {
        if (Integer.parseInt(number) < 0)
          negativeNumbers.add(number);
      }
      return negativeNumbers;
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
    ```

??? tip inline end "Refactoring"
    ***(1)*** `Extract Method`, vemos que el método `add` es demasiado largo y hace muchas cosas, así que mejor extraemos todo lo que acabamos de implementar a un método nuevo que se llame `checkNegativeNumbers`.

    ***(2)*** Vemos que la comparación es sencilla, pero aun podríamos ayudar más a su comprensión. Además, podemos aprovechar para minimizarlo todo si en vez de un Array de Integers lo cambiamos a un Array de Strings. Vamos a crear un método que nos diga si un número es negativo o no `isNegative`, eso facilitará la lectura.

    ***(3)*** Es un bloque `if` complejo que si le damos la vuelta a la comparación, se hace mucho más sencillo de entender y de memorizar.

    ***(4)*** `Extract Method`, igual que en el primer punto, se puede extraer la funcionalidad para darle mejor semántica al método. Haremos un método llamado `getNegativeNumbers`.

    ***(5)*** `Extract Method`, igual que el punto anterior, extraeremos un método llamado `convertListToString`.


## Resultado final

En este punto deberíamos tener el siguiente código:


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
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public class StringCalculator {

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

        String delimiter = DEFAULT_DELIMITER;
        if (input.startsWith(START_DELIMITER_TOKEN)) {
          int endIndex = input.indexOf(END_DELIMITER_TOKEN);
          delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);
        }

        return delimiter;
      }

      private int addNumbers(String numbers[]) {

        int total = 0;
        for (String number : numbers) {
          total += Integer.parseInt(number);
        }

        return total;
      }

    }
    ```
