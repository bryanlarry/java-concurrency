package org.example;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class OrderProcessor {

    private final ExecutorService executor;

    public OrderProcessor(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public List<Future<String>> processOrdersWithCallable(List<Order> orders) {
        List<Callable<String>> tasks = orders.stream()
                .map(order -> (Callable<String>) () -> processOrder(order))
                .collect(Collectors.toList());

        try {
            return executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    public List<CompletableFuture<String>> processOrdersWithCompletableFuture(List<Order> orders) {
        return orders.stream()
                .map(order -> CompletableFuture.supplyAsync(() -> processOrder(order), executor))
                .collect(Collectors.toList());
    }

    public List<String> processOrdersWithParallelStream(List<Order> orders) {
        return orders.parallelStream()
                .map(this::processOrder)
                .collect(Collectors.toList());
    }

    private String processOrder(Order order) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Pedido " + order.getId() + " processado.";
    }

    public void shutdown() {
        executor.shutdown();
    }
}

