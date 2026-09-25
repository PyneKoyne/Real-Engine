package main;

public class Rendering {

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
