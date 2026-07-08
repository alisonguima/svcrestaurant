package com.techchallenge.restaurant.adapter.output.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringTransactionAdapterTest {

  @Mock
  private PlatformTransactionManager transactionManager;

  @Mock
  private TransactionStatus transactionStatus;

  private SpringTransactionAdapter adapter;

  @BeforeEach
  void setUp() {
    when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
    adapter = new SpringTransactionAdapter(transactionManager);
  }

  @Test
  void shouldExecuteActionAndReturnResult() {
    String result = adapter.execute(() -> "value");

    assertEquals("value", result);
    verify(transactionManager).commit(transactionStatus);
  }

  @Test
  void shouldExecuteVoidAction() {
    AtomicBoolean executed = new AtomicBoolean(false);

    adapter.executeVoid(() -> executed.set(true));

    assertTrue(executed.get());
    verify(transactionManager).commit(transactionStatus);
  }

  @Test
  void shouldExecuteReadOnlyActionAndReturnResult() {
    String result = adapter.executeReadOnly(() -> "read-only-value");

    assertEquals("read-only-value", result);
    verify(transactionManager).commit(transactionStatus);
  }

  @Test
  void shouldUseWriteTransactionDefinitionForExecute() {
    ArgumentCaptor<TransactionDefinition> captor = ArgumentCaptor.forClass(TransactionDefinition.class);

    adapter.execute(() -> null);

    verify(transactionManager).getTransaction(captor.capture());
    assertFalse(captor.getValue().isReadOnly());
  }

  @Test
  void shouldUseReadOnlyTransactionDefinitionForExecuteReadOnly() {
    ArgumentCaptor<TransactionDefinition> captor = ArgumentCaptor.forClass(TransactionDefinition.class);

    adapter.executeReadOnly(() -> null);

    verify(transactionManager).getTransaction(captor.capture());
    assertTrue(captor.getValue().isReadOnly());
  }
}
