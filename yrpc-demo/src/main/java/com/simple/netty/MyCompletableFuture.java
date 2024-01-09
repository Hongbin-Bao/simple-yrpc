package com.simple.netty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Hongbin BAO
 * @Date 2024/1/9 01:22
 */
public class MyCompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        /**
         * 可以获取子线程中的返回 过程中的结果 并可以在主线程中阻塞等待其完成
         */
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        new Thread( () ->{


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int i = 8;
            completableFuture.complete(i);

        }).start();

        //  get 方法是一个阻塞的方法
        Integer integer = completableFuture.get(1, TimeUnit.SECONDS);
        System.out.println(integer);
        //  如何在子线程中获取到这个8

    }
}
