package org.rkoubsky;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static org.rkoubsky.PexChallenge.TOP_K_COLORS;

public class ImageScan implements Runnable {
    private final BlockingQueue<PrevalentColors> imageQueue;
    private final String imageName;

    public ImageScan(final BlockingQueue<PrevalentColors> imageQueue, final String imageName) {
        this.imageQueue = imageQueue;
        this.imageName = imageName;
    }

    @Override
    public void run() {
        try {
            this.scan();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void scan() throws InterruptedException {
        final BufferedImage image = this.readImage();
        final Map<Integer, Integer> occurrences = new HashMap();

        this.countOccurrences(image, occurrences);

        final List<Integer> mostPrevalentColors = this.mostKPrevalentColors(occurrences, TOP_K_COLORS);
        this.imageQueue.put(this.create(mostPrevalentColors));
    }

    private PrevalentColors create(final List<Integer> mostPrevalentColors) {
        return new PrevalentColors(this.imageName, this.prevalentColorsHex(mostPrevalentColors));
    }

    private void countOccurrences(final BufferedImage image, final Map<Integer, Integer> occurrences) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int rgb = image.getRGB(x, y);
                occurrences.put(rgb, occurrences.getOrDefault(rgb, 0) + 1);
            }
        }
    }

    private BufferedImage readImage() {
        final BufferedImage image;
        try {
            image = ImageIO.read(new URL(this.imageName));
        } catch (final IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read an image from URL: %s", this.imageName));
        }
        return image;
    }

    private List<Integer> mostKPrevalentColors(final Map<Integer, Integer> map, final int k) {
        // create a min heap
        final PriorityQueue<Map.Entry<Integer, Integer>> queue = new PriorityQueue<>(Comparator.comparing(e -> e.getValue()));

        //maintain a heap of size k.
        for (final Map.Entry<Integer, Integer> entry : map.entrySet()) {
            queue.offer(entry);
            if (queue.size() > k) {
                queue.poll();
            }
        }

        //get all elements from the heap
        final List<Integer> result = new ArrayList<>();
        while (queue.size() > 0) {
            result.add(queue.poll().getKey());
        }

        //reverse the order
        Collections.reverse(result);
        return result;
    }

    private List<String> prevalentColorsHex(final List<Integer> mostPrevalentColors) {
        final List<String> colorsHex = mostPrevalentColors.stream()
                                                          .map(color -> {
                                                              int[] rgb = getRGBArr(color);
                                                              return String.format("#%02X%02X%02X", rgb[0],
                                                                                   rgb[1],
                                                                                   rgb[2]);
                                                          }).collect(Collectors.toList());
        return Collections.unmodifiableList(colorsHex);
    }

    public static int[] getRGBArr(final int pixel) {
        final int alpha = (pixel >> 24) & 0xff;
        final int red = (pixel >> 16) & 0xff;
        final int green = (pixel >> 8) & 0xff;
        final int blue = (pixel) & 0xff;
        return new int[]{red, green, blue};

    }
}