package com.gianvittorio;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


public class MockitoTests {
    @Mock
    List<String> lista;

    @Test
    public void primeiroTestMockito() {
//        // Cenario
//        when(lista.size()).thenReturn(20);
//
//        // Execucao
//        int sz = lista.size();

//        lista.size();
//        lista.add(any());

        // Verificacao
        InOrder inOrder = Mockito.inOrder(lista);

        inOrder.verify(lista).size();
        inOrder.verify(lista).add(any());


//        Assertions.assertThat(sz)
//                .isEqualTo(20);

        verify(lista).size();
    }
}
