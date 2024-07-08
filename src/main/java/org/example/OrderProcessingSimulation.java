package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OrderProcessingSimulation {

    public static void main(String[] args) {
        List<Order> orders = createOrders(10);
        OrderProcessor processor = new OrderProcessor(4);

        List<Future<String>> resultsCallable = processor.processOrdersWithCallable(orders);
        for (Future<String> result : resultsCallable) {
            try {
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<CompletableFuture<String>> resultsCompletableFuture = processor.processOrdersWithCompletableFuture(orders);
        resultsCompletableFuture.forEach(cf -> cf.thenAccept(System.out::println).join());
        
        List<String> resultsParallelStream = processor.processOrdersWithParallelStream(orders);
        resultsParallelStream.forEach(System.out::println);

        processor.shutdown();
    }

    private static List<Order> createOrders(int numberOfOrders) {
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= numberOfOrders; i++) {
            orders.add(new Order(i, "Descrição do Pedido " + i, i * 10.0));
        }
        return orders;
    }
}
