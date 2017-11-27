package com.loushuiyifan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 漏水亦凡
 * @date 2017/11/27
 */

public class ExecutorTest {

    public static void main(String[] args) throws Exception {
        Long start = System.currentTimeMillis();
        Executors.newCachedThreadPool();

        ExecutorService executorService = new ThreadPoolExecutor(5,
                30,
                100,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(500));
        CompletionService<String> completionService = new ExecutorCompletionService(executorService);

        List<TaskWithResult> list = new ArrayList<>();
        //创建10个任务并执行
        for (int i = 0; i < 500; i++) {
            list.add(new TaskWithResult(i));
        }

        for (TaskWithResult t : list) {
            completionService.submit(t);
        }

//        遍历任务的结果
        for (int i = 0; i < list.size(); i++) {
            try {
                System.out.println(completionService.take().get());     //打印各个线程（任务）执行的结果
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();


        Long end = System.currentTimeMillis();
        System.out.println((end - start));

    }

    static class TaskWithResult implements Callable<String> {
        private int id;

        public TaskWithResult(int id) {
            this.id = id;
        }

        /**
         * 任务的具体过程，一旦任务传给ExecutorService的submit方法，
         * 则该方法自动在一个线程上执行
         */
        public String call() throws Exception {
            if (id == 100) {
                throw new RuntimeException("100线程失败");
            }
            Thread.sleep(10);
            System.out.println("call()方法被自动调用！！！    " + Thread.currentThread().getName());
            //该返回结果将被Future的get方法得到
            return "call()方法被自动调用，任务返回的结果是：" + id + "    " + Thread.currentThread().getName();
        }

    }
}
