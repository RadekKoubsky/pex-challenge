package org.rkoubsky;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ColorsFileWriter implements Runnable {
    public static final String OUTPUT_FILE = "prevalent_colors.txt";
    private BlockingQueue<PrevalentColors> imageQueue;
    private AtomicLong processedImages = new AtomicLong(0);

    public ColorsFileWriter(final BlockingQueue<PrevalentColors> imageQueue) {
        this.imageQueue = imageQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.writePrevalentColors();
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public AtomicLong getProcessedImages() {
        return this.processedImages;
    }

    private void writePrevalentColors() throws InterruptedException, IOException {
        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, true))){
            final PrevalentColors prevalentColors = this.imageQueue.take();
            final String csvFormat = this.toCSV(prevalentColors);
            writer.write(csvFormat);
            writer.newLine();
            this.processedImages.incrementAndGet();
        }
    }

    private String toCSV(final PrevalentColors prevalentColors) {
        return String.format("%s,%s", prevalentColors.imagUrl, String.join(",", prevalentColors.prevalentColors));
    }
}
