package org.rkoubsky;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class ImagesService {
    private BoundedExecutor boundedExec;
    private BlockingQueue<PrevalentColors> imageQueue;

    public ImagesService(final BoundedExecutor boundedExec, final BlockingQueue<PrevalentColors> imageQueue) {
        this.boundedExec = boundedExec;
        this.imageQueue = imageQueue;
    }

    public void process(final String imagesFile) throws IOException, InterruptedException {
        this.processImages(imagesFile);
    }

    public void stop() throws InterruptedException {
        this.boundedExec.stop();
    }

    private void processImages(final String imagesFile) throws IOException {
        try (final Stream<String> stream = Files.lines(Paths.get(imagesFile))) {
            stream.forEach(image -> {
                try {
                    this.boundedExec.submitTask(new ImageScan(this.imageQueue, image));
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}
