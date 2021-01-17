package color;

import java.util.List;
import java.util.Map;

import static java.awt.Color.RGBtoHSB;

public class ColorHandler {
    public static final Color DRUMPAD_PRESS_COLOR = new Color(118, 245, 86, 122);

    public static final Map<String, List<Color>> COLORMAPPING = LightPadColorMapping.getMappingByColor();

    public static final List<Color> GREENS = COLORMAPPING.get("Green");
    public static final List<Color> YELLOWS = COLORMAPPING.get("Yellow");
    public static final List<Color> CYANS = COLORMAPPING.get("Cyan");
    public static final List<Color> BLUES = COLORMAPPING.get("Blue");
    public static final List<Color> VIOLETS = COLORMAPPING.get("Violet");
    public static final List<Color> MAGENTAS = COLORMAPPING.get("Magenta");
    public static final List<Color> GRAY = COLORMAPPING.get("Gray");

    private static class LightColor {
        public final int r, g, b;
        public final float h,s,v;

        public LightColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;

            float[] hsb = new float[3];
            RGBtoHSB(r, g, b, hsb);
            this.h = hsb[0];
            this.s = hsb[1];
            this.v = hsb[2];
        }
    }

    public static class Color {
        private final LightColor lightColor;
        private final int padColor;

        public static Color withClosestPadColor(int r, int g, int b) {
            return new Color(r, g, b, LightPadColorMapping.getClosestPadColor(r, g, b));
        }

        static Color withoutPadColor(int r, int g, int b) {
            return new Color(r, g, b, 106);
        }

        public Color(int r, int g, int b, int padColor) {
            lightColor = new LightColor(r, g, b);
            this.padColor = padColor;
        }

        public int getR() {
            return lightColor.r;
        }
        public int getG() {
            return lightColor.g;
        }
        public int getB() {
            return lightColor.b;
        }

        @Override public String toString() {
            return String.format(
                    "R: %d G: %d B: %d \t - \t H: %.2fd S: %.2fd B: %.2fd",
                    lightColor.r, lightColor.g, lightColor.b,
                    lightColor.s, lightColor.s, lightColor.v
            );
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LightColor other = ((Color) o).lightColor;

            return lightColor.r == other.r && lightColor.g == other.g && lightColor.b == other.b;
        }

        @Override public int hashCode() {
            return lightColor.hashCode();
        }

        public int getPadColor() {
            return padColor;
        }
    }
}
