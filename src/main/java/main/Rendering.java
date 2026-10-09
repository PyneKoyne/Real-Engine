package main;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class Rendering implements Runnable {
    Process ffmpeg;
    BufferedOutputStream os;
    ByteBuffer buffer;
    private final LinkedBlockingQueue<int[]> frames = new LinkedBlockingQueue<int[]>();


    public Rendering (int width, int height, String outputPath, int fps) {
        ProcessBuilder cmd = new ProcessBuilder(
                "ffmpeg",
                "-loglevel", "error",
                "-y",
                "-f", "rawvideo",
                "-vcodec", "rawvideo",
                "-pix_fmt", "argb",
                "-s", width + "x" + height,
                "-i", "-",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-r", Integer.toString(fps),
                outputPath
        );
        cmd.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            this.ffmpeg = cmd.start();
            this.os = new BufferedOutputStream(ffmpeg.getOutputStream());
            this.buffer = ByteBuffer.allocate(width * height * 4);
        } catch (IOException  e) {
            e.printStackTrace();
        }

    }

    public void renderFrame(int[] pixelData) {
        try {
            frames.put(pixelData);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(this.os != null){
            try {
                int[] pixelData = frames.take();
                this.buffer.clear();
                this.buffer.asIntBuffer().put(pixelData);
                this.os.write(this.buffer.array());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        try {
            os.close();
            ffmpeg.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // a method to blend two rgb colours together
    public static int blendColor(int color1, int color2, double ratio) {
        int a1 = (color1 >> 24 & 0xff);
        int r1 = ((color1 & 0xff0000) >> 16);
        int g1 = ((color1 & 0xff00) >> 8);
        int b1 = (color1 & 0xff);

        int a2 = (color2 >> 24 & 0xff);
        int r2 = ((color2 & 0xff0000) >> 16);
        int g2 = ((color2 & 0xff00) >> 8);
        int b2 = (color2 & 0xff);

        int a = (int) ((a1 * (1 - ratio)) + (a2 * ratio));
        int r = (int) ((r1 * (1 - ratio)) + (r2 * ratio));
        int g = (int) ((g1 * (1 - ratio)) + (g2 * ratio));
        int b = (int) ((b1 * (1 - ratio)) + (b2 * ratio));
        return (a << 24 | r << 16 | g << 8 | b);
    }
}
