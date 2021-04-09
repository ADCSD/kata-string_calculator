# Desarrollo del requisito 2

## Requisito

!!! success "Permite al metodo “add" manejar cualquier cantidad de números"

## Desarrollo

Este requisito es simple, no se puede fraccionar en varios tests, así que vamos a implementarlo directamente tanto jUnit como su código en la aplicación para que se cumpla el test.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void multiple_comma_separated_number_return_sum() {

      Assert.assertEquals(6, this.calculator.add("1,2,3"));
      Assert.assertEquals(25, this.calculator.add("4,5,6,10"));
      Assert.assertEquals(51, this.calculator.add("10,10,10,10,10,1"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(DELIMITER)) {
        String numbers[] = input.split(DELIMITER);
        int a = 0;
        for (int i = 0; i < numbers.length; i++) {
          a += Integer.parseInt(numbers[i]);
        }
        return a;
      }

      return Integer.parseInt(input);

    }
    ```

Poco a poco el código va creciendo y podemos aplicar más refactors diferentes. 

=== "StringCalculator"
    ``` Java hl_lines="8-12" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(DELIMITER)) {
        String numbers[] = input.split(DELIMITER);   
        int a = 0;                                   /*(1)*/
        for (int i = 0; i < numbers.length; i++) {   /*(1)*/
          a += Integer.parseInt(numbers[i]);         /*(1)*/
        }                                            /*(1)*/
        return a;                                    /*(1)*/
      }

      return Integer.parseInt(input);

    }
    ```
=== "StringCalculator-Ref1"
    ``` Java hl_lines="17 19 21" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(DELIMITER)) {
        String numbers[] = input.split(DELIMITER); 
        return addNumbers(numbers);
      }

      return Integer.parseInt(input);

    }

    private int addNumbers(String numbers[]) {

      int a = 0;                                     /*(2)*/
      for (int i = 0; i < numbers.length; i++) {
        a += Integer.parseInt(numbers[i]);           /*(2)*/
      }
      return a;                                      /*(2)*/
    }
    ```
=== "StringCalculator-Ref2"
    ``` Java hl_lines="6-11 18 19" linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(DELIMITER)) {               /*(3)*/
        String numbers[] = input.split(DELIMITER);   /*(3)*/
        return addNumbers(numbers);                  /*(3)*/
      }                                              /*(3)*/

      return Integer.parseInt(input);                /*(3)*/

    }

    private int addNumbers(String numbers[]) {

      int total = 0;
      for (int i = 0; i < numbers.length; i++) {     /*(4)*/
        total += Integer.parseInt(numbers[i]);       /*(4)*/
      }
      return total;
    }
    ```
=== "StringCalculator-Ref3"
    ``` Java linenums="1"
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      String numbers[] = input.split(DELIMITER);
      return addNumbers(numbers);
    }

    private int addNumbers(String numbers[]) {

      int total = 0;
      for (String number : numbers) {
        total += Integer.parseInt(number);
      }
      return total;
    }
    ```

??? tip inline end "Refactoring"
    ***(1)*** El método empieza a ser muy largo y hace demasiadas cosas (comprobar si es vacío, comprobar si tiene delimitador, partir la cadena, recorrerla, sumarla). No podemos escribir toda una novela en un solo capítulo, debemos empezar a partir el método. Cuando un método (o clase) empieza a tener muchas líneas o empieza a coger demasiadas responsabilidades, debemos separarlo en varios. Lo más fácil y rápido es coger una de esas responsabilidades, por ejemplo la de sumar los números, y extraerla a un método privado que ejecute esas operaciones. Importante en este punto darle un nombre correcto al método que nos indique que es lo que vamos a hacer al invocarlo, `addNumbers` creo que es bastante descriptivo.

    ***(2)*** Como ya hemos comentado los nombres, de variables o métodos, cortos, ocasionan bastantes problemas y dificultades al leerlos, ya que generalemente debes memorizar lo que están almacenando, ya que semánticamente no es posible deducirlo de su nombre. En nuestro caso, la variable `a` nos sirve para ir acumulando la suma total, podríamos llamarlo así, `total` o `totalSum`.

    ***(3)*** En este bloque de código, sin nos paramos a pensar, tenemos código duplicado. A veces es dificil de ver este tipo de `smell`, pero si nos fijamos bien, vemos que aunque el código no sea igual que el otro, funcionalmente hacen lo mismo. En nuestro caso, si troceamos el `input` y resulta que no tenía ningún delimitador, el método `split` nos va a devolver un array con un único texto. Si sumamos ese único texto, obtenemos el mismo resultado que haciendo un parseInt de la línea 11. Es decir, que nos podemos ahorrar el `if` y la duplicidad.

    ***(4)*** Ya por último, y aunque no es un `smell` como tal. Siempre que podamos debemos simplificar la codificación del algoritmo. En este caso, ya que no vamos a utilizar el índice `i` para nada, se podría sustituir el `for`, por un `forEach` o incluso utilizar las funciones de `Streams`.





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

    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public class StringCalculator {

      private static final String DELIMITER = ",";

      private static final String EMPTY_INPUT = "";

      private static final int EMPTY_RESULT = 0;

      public int add(String input) {

        if (EMPTY_INPUT.equals(input))
          return EMPTY_RESULT;

        String numbers[] = input.split(DELIMITER);
        return addNumbers(numbers);
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

