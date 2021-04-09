# Desarrollo del requisito 4

## Requisito

!!! success "Soporta diferentes delimitadores"
    + Para cambiar un delimitador, el comienzo del string debe contener una línea separada que sea como esta: "//[delimitador]\n[números...]". Por ejemplo: "//;\n1;2" debe dar como resultado 3 donde el delimitador por defecto es ";".
    + La primera línea es opcional. Todos los escenarios existentes hasta ahora, deben estar soportados.

## Desarrollo

Este requisito es más complejo que el anterior. Tenemos que comprobar si la cadena de entrada tiene el texto `//` y en función de si viene o no, utilizar el delimitador custom o el delimitador por defecto.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void any_delimited_numbers_return_sum() {

        Assert.assertEquals(3, this.calculator.add("//;\n1;2"));
        Assert.assertEquals(6, this.calculator.add("//d\n1d2d3"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public int add(String input) {

        if (EMPTY_INPUT.equals(input))
          return EMPTY_RESULT;

        String numbers[];
        if (input.startsWith("//")) {
          int endIndex = input.indexOf("\n");
          String customDelimiter = input.substring(2, endIndex);
          input = input.substring(endIndex + 1);
          numbers = input.split(customDelimiter);
        } else {
          numbers = input.split(DELIMITER);
        }
        
        return addNumbers(numbers);
    }
    ```

De nuevo el código es un poco confuso, así que vamos a intentar mejorarlo un poco aplicando algo de `Refactoring`.

=== "StringCalculator"
    ``` Java hl_lines="9 11 13" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String numbers[];
      if (input.startsWith("//")) {
        int endIndex = input.indexOf("\n");
        String customDelimiter = input.substring(2, endIndex);   /*(1)*/
        input = input.substring(endIndex + 1);
        numbers = input.split(customDelimiter);                  /*(2)*/
      } else {
        numbers = input.split(DELIMITER);                        /*(1) (2)*/
      }
        
      return addNumbers(numbers);
    }
    ```
=== "StringCalculator-Ref1"
    ``` Java hl_lines="11 12 13 14" linenums="1"
    private static final String DEFAULT_DELIMITER = ",|\n";

    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String numbers[];
      String delimiter = DEFAULT_DELIMITER;

      if (input.startsWith("//")) {                              /*(3)*/
        int endIndex = input.indexOf("\n");                      /*(3)*/
        delimiter = input.substring(2, endIndex);                /*(4)*/
        input = input.substring(endIndex + 1);                   /*(4)*/
      }

      numbers = input.split(delimiter);

      return addNumbers(numbers);
    }

    ```
=== "StringCalculator-Ref2"
    ``` Java hl_lines="10-19" linenums="1"
    private static final String END_DELIMITER_TOKEN = "\n";

    private static final String START_DELIMITER_TOKEN = "//";

    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String numbers[];                                                        
      String delimiter = DEFAULT_DELIMITER;                                    

      if (input.startsWith(START_DELIMITER_TOKEN)) {                           
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);                     
        delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex); 
        input = input.substring(endIndex + END_DELIMITER_TOKEN.length());      
      }                                                                        

      numbers = input.split(delimiter);                                        

      return addNumbers(numbers);
    }
    ```

??? tip inline end "Refactoring"
    ***(1)*** A veces, a medida que vamos añadiendo código le vamos cambiando el sentido o el contexto a las variables o a los métodos. En este caso, la constante `DELIMITER` ya no tiene mucho sentido que se llame así, en realidad debería llamarse `DEFAULT_DELIMITER`.

    ***(2)*** Volvemos a tener código duplicado, que podría solucionarse muy fácilmente eliminando toda la clausula `else` y sacando fuera del `if` el método de `split`.

    ***(3)*** Si nos fijamos bien, los textos, son `smells` muy fáciles de detectar, al igual que los números. Se trata del típico `Magic Number`. ¿Que significa `//`? ¿Por qué es `//` y no es `%%`?. Si le damos nombre a esos valores, quedará más claro.

    ***(4)*** Lo mismo pasa con los números, volvemos a tener `Magic Number`. ¿Por qué le sumamos 2 y no 3 o 4?. Cambiandolo por algo semántico queda mucho más claro.


Llegamos a este punto, parece que ya no podemos mejorar mucho más el código pero, a menudo, para intentar mejorar el código hay que meter pequeños `smells`. En el ejemplo que estamos haciendo, podemos introducir un poco de duplicado de código, para poder aplicar otro tipo de `Refactor` que nos ayude a aumentar la legibilidad. Todo el bloque señalado en el `Refactor-2` se podría desdoblar en algo similar a esto:

=== "StringCalculator-Ref3"
    ``` Java hl_lines="6-10 12-16"  linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String delimiter = DEFAULT_DELIMITER;                                     /*(5)*/
      if (input.startsWith(START_DELIMITER_TOKEN)) {                            /*(5)*/
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);                      /*(5)*/
        delimiter = input.substring(START_DELIMITER_TOKEN.length(), endIndex);  /*(5)*/
      }                                                                         /*(5)*/

      if (input.startsWith(START_DELIMITER_TOKEN)) {                            /*(6)*/
        int endIndex = input.indexOf(END_DELIMITER_TOKEN);                      /*(6)*/
        input = input.substring(endIndex + END_DELIMITER_TOKEN.length());       /*(6)*/
      }                                                                         /*(6)*/
      String numbers[] = input.split(delimiter);                                /*(6)*/

      return addNumbers(numbers);
    }
    ```
=== "StringCalculator-Ref4"
    ``` Java linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String delimiter = extractDelimiter(input);
      String[] numbers = extractNumbers(input, delimiter);

      return addNumbers(numbers);
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
    ```

??? tip inline end "Refactoring"
    ***(5)*** Ahora que ya lo tenemos desdoblado, vemos claramente que el método hace demasiadas cosas. Podríamos extraer el bloque de código a un método que nos indicara claramente lo que hace, por ejemplo `extractDelimiter`.

    ***(6)*** El mismo caso que el anterior, le podríamos dar el nombre `extractNumbers`.


Después del Refactor-4 volvemos a tener el código mucho más legible, de hecho se ve claramente lo que el método hace: extrae el delimitador, extrae los números con ese delimitador y realiza la suma de esos números. Muy fácil de leer. Si queremos ver el detalle de cada método, pues ya nos iríamos al método concreto y lo leeríamos, que aunque en esencia es lo mismo que teníamos antes todo junto, en este caso, al leerlo, tenemos el contexto acotado a ese método y el cerebro no trabaja tanto intentando recordar.


## Resultado final

Al finalizar obtenemos el siguiente código:


=== "TestStringCalculator"
    ``` Java linenums="1" 
    public class TestStringCalculator {

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

          return addNumbers(numbers);
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

