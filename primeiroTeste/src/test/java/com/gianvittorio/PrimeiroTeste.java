package com.gianvittorio;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimeiroTeste {
    @Test
    public void testSomar2Numeros() {
        // Cenario
        int numero1 = 10, numero2 = 5;

        // Execucao
        int resultado = Calculadora.soma(numero1, numero2);

        // Verificacoes
        assertThat(resultado).isEqualTo(15);
    }

    @Test
    public void deveSubtrair() {
        // Cenario
        int num1 = 10, num2 = 5;

        // Execucao
        int res = Calculadora.subtracao(10, 5);

        // Verificacao
        assertThat(res).isEqualTo(5);
    }

    @Test
    public void deveMultiplicar() {
        // Cenario
        int num1 = 10, num2 = 5;

        // Execucao
        int res = Calculadora.multiplicacao(10, 5);

        // Verificacao
        assertThat(res).isEqualTo(50);
    }

    @Test
    public void deveDividir() {
        // Cenario
        int num1 = 10, num2 = 5;

        // Execucao
        int res = Calculadora.divisao(10, 5);

        // Verificacao
        assertThat(res).isEqualTo(2);
    }

//    @Test(expected = IllegalArgumentException.class)
    public void testNaoDeveSomarNegativos() {
        // Cenario
        int numero1 = -10, numero2 = 5;

        // Execucao
        int resultado = Calculadora.soma(-10, 5);

        // Verificacao
    }

    @Test
    public void deveEvitarOverflowEmSoma() {
        // Cenario
        int num1 = Integer.MAX_VALUE, num2 = 1;

        // Execucao
        int res = Calculadora.soma(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MAX_VALUE);

        // Cenario
        num1 = Integer.MAX_VALUE - 1;
        num2 = 2;

        // Execucao
        res = Calculadora.soma(num2, num1);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void deveEvitarUnderflowSubtracao() {
        // Cenario
        int num1 = Integer.MIN_VALUE, num2 = 1;
        // Execucacao
        int res = Calculadora.subtracao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MIN_VALUE);

        // Cenario
        num1 = -2;
        num2 = Integer.MAX_VALUE;
        // Execucacao
        res = Calculadora.subtracao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MIN_VALUE);

        // Cenario
        num1 = -3;
        num2 = Integer.MAX_VALUE;
        // Execucacao
        res = Calculadora.subtracao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void deveEvitarOverflowMultiplicacao() {
        // Cenario
        int num1 = Integer.MAX_VALUE, num2 = 2;

        // Execucacao
        int res = Calculadora.multiplicacao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MAX_VALUE);

        // Cenario
        num2 = Integer.MAX_VALUE;
        num1 = 2;

        // Execucacao
        res = Calculadora.multiplicacao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void deveEvitarUnderflowMultiplicacao() {
        // Cenario
        int num1 = Integer.MAX_VALUE, num2 = -2;

        // Execucacao
        int res = Calculadora.multiplicacao(num1, num2);

        // Verificacao
        assertThat(res).isEqualTo(Integer.MIN_VALUE);
    }

    //@Test(expected = IllegalArgumentException.class)
    public void deveEvitarIndeterminacao() {
        // Cenario
        int num1 = 0, num2 = 0;

        // Execucao
        int res = Calculadora.divisao(num1, num2);

        // Verificacao
    }

    public static class Calculadora {
        public static int soma(int a, int b) {
            if (a < 0 || b < 0) {
                throw new IllegalArgumentException("One or both args are negative!");
            }

            boolean isOverFlow = false;
            isOverFlow |= (a == Integer.MAX_VALUE && b > 0);
            isOverFlow |= (b == Integer.MAX_VALUE && a > 0);
            if (isOverFlow) {
                return Integer.MAX_VALUE;
            }

            if (Integer.MAX_VALUE - a < b) {
                return Integer.MAX_VALUE;
            }

            if (Integer.MAX_VALUE - b < a) {
                return Integer.MAX_VALUE;
            }

            return a + b;
        }

        public static int subtracao(int a, int b) {
            final int minValue = Integer.MIN_VALUE,
                    maxValue = Integer.MAX_VALUE;
            if (a == minValue && b > 0) {
                return minValue;
            }

            if (a < -1 && b == maxValue) {
                return minValue;
            }

            if (a < -1 && b > 0 && (minValue - a) > -b) {
                return minValue;
            }

            return a - b;
        }

        public static int multiplicacao(int a, int b) {
            if (a == 0 || b == 0) {
                return 0;
            }

            final int maxValue = Integer.MAX_VALUE,
                    minValue = Integer.MIN_VALUE;

            if (a == maxValue && Math.abs(b) > 0) {
                return (b < 0) ? (minValue) : (maxValue);
            }

            if (b == maxValue && Math.abs(a) > 0) {
                return (a < 0) ? (minValue) : maxValue;
            }

            if (a > maxValue / b) {
                return maxValue;
            }

            if (b > maxValue / a) {
                return maxValue;
            }

            return a * b;
        }

        public static int divisao(int a, int b) {
            int res;

            try {
                res = a / b;
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            return res;
        }
    }
}
