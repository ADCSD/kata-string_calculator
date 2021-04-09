# Desarrollo del requisito 1

## Requisito

!!! success "Crea una String Calculator con el método: int add(String input)"
    + El parámetro del método puede contener 0, 1 o 2 números y devolverá su suma (para un string vacío devolverá 0). Por ejempo: "" o "1" o "1,2”
    + Comieza por un test simple para un string vacío y luego para 1 y 2 números.
    + Recuerda resolver el problema de la manera más simple posible para que te fuerce a escribir las pruebas que aún no se te habían ocurrido.
    + Recuerda refactorizar después de conseguir pasar cada test.

## Desarrollo

### Empezamos la codificación

Como siempre, en TDD, empezamos creando el test unitario que cubra el primer requisito. Vamos a empezar con la primera parte del requisito y vamos a testear la cadena vacía. Para ello añadimos un test:

=== "TestStringCalculator"
``` Java linenums="1"
  @Test
  public void empty_string_return_zero() {

    StringCalculator calculator = new StringCalculator();
    Assert.assertEquals(0, calculator.add(""));
  }
```

!!! todo "Recuerda"
    Recuerda que los nombres de los tests deben ser descriptivos, para que no sea necesario leer el código del test para saber lo que realiza. Con leer el nombre del método debería ser suficiente para saber lo que hace.

Obviamente si ejecutamos el test, obtendremos un test fail en rojo ya que nuestro código no cumple con lo esperado por el test. El siguiente paso en TDD es realizar la implementación **mínima** que haga funcionar los tests.

=== "StringCalculator"
``` Java linenums="1"

  public int add(String input) {

    return 0;
  }

```

La implementación mínima que se me ocurre ahora mismo y que pasaría todos los tests actuales es esta. Ahora si ejecutamos, obtenemos una ejecución correcta en verde. El siguiente paso paso en TDD sería realizar el `Refactoring`. Aunque se podría aplicar alguna acción, vamos a dejarlo pasar hasta tener algo más de código, donde ya empecemos a detectar ciertos `smells`.


### Segundo test unitario

Vamos con el segundo test. En esta ocasión vamos a probar que si la entrada es un número debe devolver ese números. De nuevo hacemos el test para probar, fallará, e implementaremos el código mínimo para que cumpla con todos los tests y dejarlo en un estado correcto en verde.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void one_number_return_this_number() {

      StringCalculator calculator = new StringCalculator();
      Assert.assertEquals(1, calculator.add("1"));
      Assert.assertEquals(2, calculator.add("2"));
      Assert.assertEquals(13, calculator.add("13"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public int add(String input) {

      if (input.equals(""))
        return 0;

      else
        return Integer.parseInt(input);

    }
    ```

Ahora que tenemos la aplicación estable, en verde, y tenemos un poco más de código podemos empezar a realizar el `Refactoring`. 

=== "StringCalculator"
    ``` Java hl_lines="3 6" linenums="1"
    public int add(String input) {

      if (input.equals("")) /*(1)*/
        return 0;

      else /*(2)*/
        return Integer.parseInt(input);

    }
    ```
=== "StringCalculator-Refactor-1"
    ``` Java hl_lines="3 4" linenums="1"
    public int add(String input) {

      if ("".equals(input)) /*(3)*/
        return 0; /*(3)*/

      return Integer.parseInt(input);

    }
    ```
=== "StringCalculator-Refactor-2"
    ``` Java linenums="1"
    private static final String EMPTY_INPUT = "";

    private static final int EMPTY_RESULT = 0;

    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
            return EMPTY_RESULT;

      return Integer.parseInt(input);

    }
    ```

??? tip inline end "Refactoring"
    ***(1)*** Tal y como está escrito, si la entrada `input` fuera null, en esa línea nos saltaría un `NullPointerException`. Hay varias formas en las que podríamos corregirlo, o bien utilizando librerías de utilidades sobre String como las de ApacheUtils que ya hacen comprobaciones, o bien añadiendo una comprobación previa `input != null`, o bien simplemente dándole la vuelta al equals. Sabemos que la cadena vacía **nunca** va a ser nula, así que si le damos la vuelta no puede saltar un `NullPointerException`.

    ***(2)*** La clausula `else` la podemos eliminar ya que no aporta nada al código. Cuando programemos, debemos tener en cuenta que todos los condicionales `if`, `if else` y `else`, obligan al cerebro a tener que recordar cual era la condición anterior e ir acumulándolas. En este caso el else nos obliga a pensar que antes había un `if` que decía si la cadena era vacía, así que en este caso se ejecutará cuando la cadena **no** sea vacía. Esto se puede arreglar fácil metiendo operaciones de `return` y simplificando el nivel de los `if` y `else`.

    ***(3)*** Tanto la cadena vacía como el número 0 son `smells` de tipo `Magic Number`. En este caso está bastante claro que se trata de una entrada vacía y el resultado cero, pero está bien acostumbrarnos a tener todas esas variables o resultados por defecto en constantes. De nuevo la nomenclatura es importante, el nombre de la constante debe describir para que vamos a utilizar ese valor. No sirve con poner `ZERO` o `EMPTY_STRING` porque eso ya lo sabemos al ver el valor. Debemos ponerle un nombre acorde con la funcionalidad de ese valor.

Cuando realizamos `Refactoring` del código **no debemos olvidar** que los jUnits también forman parte del código, así que también son susceptibles de hacerles `Refactoring`. Así que vamos a ello:

=== "TestStringCalculator"
    ``` Java hl_lines="4 11" linenums="1"
    @Test
    public void empty_string_return_zero() {

      StringCalculator calculator = new StringCalculator(); /*(1)*/
      Assert.assertEquals(0, calculator.add(""));
    }

    @Test
    public void one_number_return_this_number() {

      StringCalculator calculator = new StringCalculator(); /*(1)*/
      Assert.assertEquals(1, calculator.add("1"));
      Assert.assertEquals(2, calculator.add("2"));
      Assert.assertEquals(13, calculator.add("13"));
    }
    ```
=== "TestStringCalculator-Refactor-1"
    ``` Java linenums="1"
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
    ```

??? tip inline end "Refactoring"
    ***(1)*** Existe código duplicado en los test y tiene pinta de que se va a duplicar más veces, así que es una buena práctica extraer el código duplicado a una variable o método externo.



### Tercer test unitario

Vamos con el tercer y último test de este requisito. En esta ocasión vamos a probar que si la entrada son dos números separados por coma, debe devolver la suma de esos números. De nuevo hacemos el test para probar, fallará, e implementaremos el código mínimo para que cumpla con todos los tests y dejarlo en un estado correcto en verde.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void two_comma_separated_number_return_sum() {

      Assert.assertEquals(3, this.calculator.add("1,2"));
      Assert.assertEquals(40, this.calculator.add("17,23"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1" 
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(",")) {
        String aux[] = input.split(",");
        return Integer.parseInt(aux[0]) + Integer.parseInt(aux[1]);
      }

      return Integer.parseInt(input);

    }
    ```

De nuevo, como tenemos estables el código y los tests funcionando, es hora de hacer `Refactoring`. 


=== "StringCalculator"
    ``` Java hl_lines="6 7 8" linenums="1" 
    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(",")) { /*(1)*/
        String aux[] = input.split(","); /*(1)*/ /*(2)*/
        return Integer.parseInt(aux[0]) + Integer.parseInt(aux[1]); /*(2)*/
      }

      return Integer.parseInt(input);

    }
    ```
=== "StringCalculator-Refactor-1"
    ``` Java linenums="1" 
    private static final int EMPTY_RESULT = 0;

    public int add(String input) {

      if (EMPTY_INPUT.equals(input))
        return EMPTY_RESULT;

      if (input.contains(DELIMITER)) {
        String numbers[] = input.split(DELIMITER);
        return Integer.parseInt(numbers[0]) + Integer.parseInt(numbers[1]);
      }

      return Integer.parseInt(input);

    }
    ```

??? tip inline end "Refactoring"
    ***(1)*** De nuevo tenemos un `Magic Number` que podemos extraer a una constante. En este caso la hemos llamado DELIMITER porque encaja bien con la semántica que se le quiere dar a la coma.   

    ***(2)*** Aquí el nombre de la variable no es nada descriptivo. Tendemos a ahorrarnos caracteres al escribir nombres de variables y métodos, cuando no nos cobran por escribir más, y no penalizamos el rendimiento. Un nombre de variable más correcto podría ser `numbers`, ya que es lo que vamos a almacenar en este vector.



## Resultado final

Llegados a este punto y con estos tests ya tendríamos cubiertos este primer requisito. Al finalizar obtenemos el siguiente código:


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

        if (input.contains(DELIMITER)) {
          String numbers[] = input.split(DELIMITER);
          return Integer.parseInt(numbers[0]) + Integer.parseInt(numbers[1]);
        }

        return Integer.parseInt(input);

      }

    }
    ```

