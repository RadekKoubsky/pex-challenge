package org.rkoubsky;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PexChallenge {
    public static final int TOP_K_COLORS = 3;
    private static final int N_PRODUCERS = Runtime.getRuntime().availableProcessors();
    public static final int MAX_TASKS = 1000;

    public static void main(final String[] args) throws IOException, InterruptedException {
        final String fileName = args[0];
        final ExecutorService exec = Executors.newFixedThreadPool(N_PRODUCERS);
        final BoundedExecutor boundedExec = new BoundedExecutor(exec, N_PRODUCERS + MAX_TASKS);
        final BlockingQueue<PrevalentColors> imageQueue = new LinkedBlockingQueue<>();

        final ImagesService imagesService = new ImagesService(boundedExec, imageQueue);
        final ColorsFileWriter writerTask = new ColorsFileWriter(imageQueue);
        final Thread writer = new Thread(writerTask);
        System.out.println("Starting to process list of images from a file " + fileName);
        final long start = System.nanoTime();
        writer.start();
        imagesService.process(fileName);
        waitForFinish(exec);
        final long elapsedTime = System.nanoTime() - start;
        System.out.printf("Processing %s images has finished, took %s s\n", writerTask.getProcessedImages().longValue(),
                          TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS));
        imagesService.stop();
        writer.interrupt();
    }

    private static void waitForFinish(final ExecutorService exec) throws InterruptedException {
        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) exec;
        while (threadPoolExecutor.getActiveCount() > 0){
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
