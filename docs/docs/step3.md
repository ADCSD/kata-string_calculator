# Desarrollo del requisito 3

## Requisito

!!! success "Permite al método "add" manejar saltos de línea entre números en lugar de usar comas."
    + La siguiente entrada es correcta: "1\n2,3" (el resultado será 6)
    + La siguiente entrada NO es correcta: "1,\n" (no hace falta que la pruebes, es simplemente para clarificar)

## Desarrollo

Este requisito, es muy sencillo de implementar si conocemos un poco la funcionalidad de `split`.

=== "TestStringCalculator"
    ``` Java linenums="1"
    @Test
    public void comma_or_return_line_separated_numbers_return_sum() {

        Assert.assertEquals(3, this.calculator.add("1\n2"));
        Assert.assertEquals(6, this.calculator.add("1\n2,3"));
    }
    ```
=== "StringCalculator"
    ``` Java linenums="1"
    private static final String DELIMITER = ",|\n";
    ```

!!! todo "Regex"
    Recuerda que en Java existen muchos métodos que operan sobre cadenas de texto y que aceptan expresiones regulares. Este tipo de expresiones son realmente potentes y muy útiles para simplificar operaciones. Revisa la página de [Baeldung - Expresiones Regulares](https://www.baeldung.com/regular-expressions-java), para profundizar más en este tema.

Con este cambio, no hace falta ningun `Refactoring`. Así que dejaremos el código tal cual lo tenemos.


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

      private static final String DELIMITER = ",|\n";

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

