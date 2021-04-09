# Desarrollo del requisito 7

## Requisito

!!! success "Los delimitadores pueden ser de cualquier longitud con el siguiente formato: '//[delimiter]\n'."
    + Por ejemplo: "//[;;;]\n1;;;2;;;3" debe dar como resultado 6.

## Desarrollo

De nuevo sabemos donde tenemos que modificar. Tenemos un método concreto que extrae el delimitador, así que debemos modificar el código de ese método para ajustarlo al nuevo requisito.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void any_length_delimiter() {

      Assert.assertEquals(6, this.calculator.add("//[;;;]\n1;;;2;;;3"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    private String extractDelimiter(String input) {

      String delimiter = DEFAULT_DELIMITER;
      if (input.startsWith(START_DELIMITER_TOKEN)) {
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);
        delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

        if (delimiter.startsWith("[") && delimiter.endsWith("]")) {
          delimiter = delimiter.substring(1, delimiter.length() - 1);
        }
      }

      return delimiter;
    }
    ```

No me siento del todo cómodo con este código, veo algunas cadenas de texto que podría extraer (`MagicNumber`), veo una condición doble con un `&&` que a veces resulta dificil de leer y veo un `if` dentro de otro `if`. Como me ha costado poco tiempo implementar el cambio, me gustaría darle una vuelta a ver si lo puedo simplificar. 

=== "StringCalculator"
    ``` Java hl_lines="8-9" linenums="1"
    private String extractDelimiter(String input) {

      String delimiter = DEFAULT_DELIMITER;
      if (input.startsWith(START_DELIMITER_TOKEN)) {
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);
        delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

        if (delimiter.startsWith("[") && delimiter.endsWith("]")) {   /*(1)(2)*/
          delimiter = delimiter.substring(1, delimiter.length() - 1); /*(3)*/
        }
      }

      return delimiter;
    }
    ```
=== "StringCalculator-Ref1"
    ``` Java hl_lines="7-8 16" linenums="1"
    private static final String END_LONG_DELIMITER_TOKEN = "]";

    private static final String START_LONG_DELIMITER_TOKEN = "[";

    private String extractDelimiter(String input) {

      String delimiter = DEFAULT_DELIMITER;           /*(4)*/
      if (input.startsWith(START_DELIMITER_TOKEN)) {  /*(4)*/
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);
        delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

        if (isLongDelimiter(delimiter)) {
          delimiter = delimiter.substring(START_LONG_DELIMITER_TOKEN.length(),
              (delimiter.length() - END_LONG_DELIMITER_TOKEN.length()));
        }
      }                                               /*(4)*/

      return delimiter;
    }

    private boolean isLongDelimiter(String delimiter) {

      return delimiter.startsWith(START_LONG_DELIMITER_TOKEN) && delimiter.endsWith(END_LONG_DELIMITER_TOKEN);
    }
    ```
=== "StringCalculator-Ref2"
    ``` Java hl_lines="3" linenums="1"
    private String extractDelimiter(String input) {

      if (input.startsWith(START_DELIMITER_TOKEN) == false)  /*(5)*/
        return DEFAULT_DELIMITER;

      int endIndex = input.indexOf(END_DELIMITER_TOKEN);
      String delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

      if (isLongDelimiter(delimiter)) {
        delimiter = delimiter.substring(START_LONG_DELIMITER_TOKEN.length(),
            (delimiter.length() - END_LONG_DELIMITER_TOKEN.length()));
      }

      return delimiter;
    }
    ```
=== "StringCalculator-Ref3"
    ``` Java linenums="1"
    private String extractDelimiter(String input) {

      if (isDefaultDelimiter(input))
        return DEFAULT_DELIMITER;

      int endIndex = input.indexOf(END_DELIMITER_TOKEN);
      String delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);

      if (isLongDelimiter(delimiter)) {
        delimiter = delimiter.substring(START_LONG_DELIMITER_TOKEN.length(),
            (delimiter.length() - END_LONG_DELIMITER_TOKEN.length()));
      }

      return delimiter;
    }

    private boolean isDefaultDelimiter(String input) {

      return input.startsWith(START_DELIMITER_TOKEN) == false;
    }
    ```

??? tip inline end "Refactoring"
    ***(1)*** `Extract Constant`, vamos a refactorizar los `MagicNumber` y vamos a crear constantes que nos indiquen que significa `[` y `]`. Por ejemplo les llamaremos `START_LONG_DELIMITER_TOKEN` y `END_LONG_DELIMITER_TOKEN`.

    ***(2)*** `Extract Method`, además, vamos a reducir la complejidad de las condiciones dobles extrayendo la comparación a un método con un nombre más fácil de entender `isLongDelimiter`.

    ***(3)*** También utilizaremos el refactor que hemos hecho en el punto 1, para darle más legibilidad a los números `1` que tenemos en el código y no sabemos muy bien de donde vienen.

    ***(4)*** Como tenemos dos `if` anidados, cuando llegamos al segundo tenemos que ir acumulando condiciones en el cerebro. El ser humano es perezoso por naturaleza y todo lo que requiera un gasto energético va a intentar eludirlo, así que facilitémosle la faena. En este caso en concreto podemos invertir el significado del `if`, ya que según parece si tiene el token de delimitador "custom" hace muchas cosas, y si no lo tiene no hace nada. Pues démosle la vuelta al `if` y nos evitamos el anidado.

    ***(5)*** `Extract Method` vamos a reducir la complejidad de la condición. Podríamos utilizar el operador "bang" ´(!)´ para negar la condición. La verdad es que hay mucho debate en torno a si es legible o no ese operador, yo prefiero utilizarlo lo menos posible ya que muchas veces dificulta bastante la lectura, sobre todo cuando se trata de comparaciones más complejas. Prefiero extraer a un método la comparación y darle un valor semántico al nombre del método, por ejemplo `isDefaultDelimiter`.


Ahora el código queda más claro, ya no existe esa animación de `if` y las condiciones son más fáciles de leer.

## Resultado final

Deberíamos tener el siguiente código:


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

    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public class StringCalculator {

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
          delimiter = delimiter.substring(START_LONG_DELIMITER_TOKEN.length(),
              (delimiter.length() - END_LONG_DELIMITER_TOKEN.length()));
        }

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

