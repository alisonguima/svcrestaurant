package com.techchallenge.restaurant.application.port.output;

import java.util.function.Supplier;

public interface TransactionPort {

  <T> T execute(Supplier<T> action);

  void executeVoid(Runnable action);

  <T> T executeReadOnly(Supplier<T> action);
}
