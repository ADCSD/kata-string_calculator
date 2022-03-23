package com.capgemini.adcsd.codingdojo.stringcalculator;

import org.junit.Assert;
import org.junit.Test;

/**
 * jUnit Test Kata String Calculator
 */
@SuppressWarnings("javadoc")
public class TestStringCalculator {

  StringCalculator calculator = new StringCalculator();

  @Test
  public void entradaCadenaVaciaSalidaCero() {

    Assert.assertEquals(0, this.calculator.add(""));
  }

  @Test
  public void entradaUnNumeroSalidaMismoNumero() {

    Assert.assertEquals(1, this.calculator.add("1"));
    Assert.assertEquals(14, this.calculator.add("14"));
  }

  @Test
  public void entradaNumeroSeparadoComasSalidaSuma() {

    Assert.assertEquals(3, this.calculator.add("1,2"));
    Assert.assertEquals(9, this.calculator.add("4,5"));
  }

  @Test
  public void entradaMultiplesNumerosSeparadoComasSalidaSuma() {

    Assert.assertEquals(15, this.calculator.add("4,5,6"));
  }

  @Test
  public void entradaMultiplesNumerosSeparadoComasOSaltoLineaSalidaSuma() {

    Assert.assertEquals(6, this.calculator.add("1\n2,3"));
  }

  @Test
  public void entradaMultiplesNumerosDelimitadorCustomSalidaSuma() {

    Assert.assertEquals(6, this.calculator.add("//;\n1;2;3"));
  }

}
