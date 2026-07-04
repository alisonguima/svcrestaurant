package com.techchallenge.restaurant.adapter.output.transaction;

import com.techchallenge.restaurant.application.port.output.TransactionPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class SpringTransactionAdapter implements TransactionPort {

  private final TransactionTemplate transactionTemplate;
  private final TransactionTemplate readOnlyTransactionTemplate;

  public SpringTransactionAdapter(PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
    this.readOnlyTransactionTemplate.setReadOnly(true);
  }

  @Override
  public <T> T execute(Supplier<T> action) {
    return transactionTemplate.execute(status -> action.get());
  }

  @Override
  public void executeVoid(Runnable action) {
    transactionTemplate.execute(status -> {
      action.run();
      return null;
    });
  }

  @Override
  public <T> T executeReadOnly(Supplier<T> action) {
    return readOnlyTransactionTemplate.execute(status -> action.get());
  }
}
