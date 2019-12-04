package org.rkoubsky;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedExecutor {
    private final ExecutorService exec;
    private final Semaphore semaphore;

    public BoundedExecutor(final int nProducers, final int tasks) {
        this.exec = Executors.newFixedThreadPool(nProducers);
        /**
         * set the bound on the semaphore to be equal to
         * the pool size plus the number of queued tasks you want to allow
         */
        this.semaphore = new Semaphore(nProducers + tasks);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        this.semaphore.acquire();
        try {
            this.exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } finally {
                        BoundedExecutor.this.semaphore.release();
                    }
                }
            });
        } catch (final RejectedExecutionException e) {
            this.semaphore.release();
        }
    }

    public int activeThreads(){
        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) this.exec;
        return threadPoolExecutor.getActiveCount();
    }

    public void stop() throws InterruptedException {
        this.exec.shutdown();
        this.exec.awaitTermination(10, TimeUnit.SECONDS);
    }
}
