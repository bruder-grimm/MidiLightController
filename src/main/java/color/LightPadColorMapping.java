package color;

import color.ColorHandler.Color;
import util.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class LightPadColorMapping {
    private static final Map<String, List<Color>> lightPadColorMappingByColor = new HashMap<>();
    private static final List<Color> lightPadColorMapping = new LinkedList<>();

    static {
        List<Color> yellow = new LinkedList<>();
        List<Color> green = new LinkedList<>();
        List<Color> cyan = new LinkedList<>();
        List<Color> blue = new LinkedList<>();
        List<Color> violet = new LinkedList<>();
        List<Color> magenta = new LinkedList<>();
        List<Color> gray = new LinkedList<>();

        /* please for the love of god do not worry about these values, they're color-metered straight from
         * the mk3 documentation. There is absolutely NO logic to the colors, that's why there was no way
         * to programatically map them. Therefore, this 'table' had to be created in order to map launchkey
         * color to rgb colors. The good thing is that you'll NEVER have to change them */
        yellow.add(new Color(255, 238, 170, 13));
        yellow.add(new Color(255, 254, 123, 14));
        yellow.add(new Color(221, 220, 116, 15));
        yellow.add(new Color(228, 253, 171, 16));
        yellow.add(new Color(207, 252, 121, 17));
        yellow.add(new Color(140, 177, 106, 18));
        yellow.add(new Color(207, 253, 186, 19));

        green.add(new Color(207, 253, 186, 20));
        green.add(new Color(143, 251, 119, 21));
        green.add(new Color(131, 217, 112, 22));
        green.add(new Color(117, 176, 106, 23));
        green.add(new Color(207, 253, 199, 24));
        green.add(new Color(143, 251, 152, 25));
        green.add(new Color(131, 217, 129, 26));
        green.add(new Color(117, 176, 114, 27));

        cyan.add(new Color(207, 253, 208, 28));
        cyan.add(new Color(143, 251, 207, 29));
        cyan.add(new Color(131, 218, 166, 30));
        cyan.add(new Color(117, 176, 133, 31));
        cyan.add(new Color(207, 253, 243, 32));
        cyan.add(new Color(143, 252, 233, 33));
        cyan.add(new Color(131, 218, 195, 34));
        cyan.add(new Color(117, 177, 152, 35));

        blue.add(new Color(204, 242, 253, 36));
        blue.add(new Color(137, 235, 252, 37));
        blue.add(new Color(123, 197, 218, 38));
        blue.add(new Color(112, 160, 177, 39));
        blue.add(new Color(199, 221, 252, 40));
        blue.add(new Color(123, 197, 250, 41));
        blue.add(new Color(112, 160, 216, 42));
        blue.add(new Color(103, 129, 175, 43));

        violet.add(new Color(157, 143, 248, 44));
        violet.add(new Color(96, 101, 246, 45));
        violet.add(new Color(97, 100, 214, 46));
        violet.add(new Color(97, 99, 174, 47));
        violet.add(new Color(193, 174, 241, 48));
        violet.add(new Color(147, 101, 238, 49));
        violet.add(new Color(121, 98, 209, 50));
        violet.add(new Color(112, 98, 171, 51));

        magenta.add(new Color(244, 183, 251, 52));
        magenta.add(new Color(237, 111, 248, 53));
        magenta.add(new Color(206, 107, 215, 54));
        magenta.add(new Color(168, 103, 175, 55));
        magenta.add(new Color(240, 180, 209, 56));
        magenta.add(new Color(233, 107, 187, 57));
        magenta.add(new Color(202, 103, 156, 58));
        magenta.add(new Color(168, 102, 138, 59));

        gray.add(new Color(86, 86, 86, 0));
        gray.add(new Color(170, 170, 170, 1));
        gray.add(new Color(216, 216, 216, 2));
        gray.add(new Color(255, 255, 255, 3));

        lightPadColorMappingByColor.put("Yellow", yellow);
        lightPadColorMappingByColor.put("Green", green);
        lightPadColorMappingByColor.put("Cyan", cyan);
        lightPadColorMappingByColor.put("Blue", blue);
        lightPadColorMappingByColor.put("Violet", violet);
        lightPadColorMappingByColor.put("Magenta", magenta);
        lightPadColorMappingByColor.put("Gray", gray);
    }

    static {
        lightPadColorMapping.addAll(
                lightPadColorMappingByColor.values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    public static int getClosestPadColor(int r, int g, int b) {
        Color sought = Color.withoutPadColor(r, g, b);
        return lightPadColorMapping.stream()
                .map(color -> Tuple.apply(color, getDistance(sought, color)))
                .min(Comparator.comparingDouble(colorAndDistance -> colorAndDistance._2))
                .map(possibleColorAndDistance -> possibleColorAndDistance._1.getPadColor())
                .orElse(106); // this should never happen
    }

    private static double getDistance(Color left, Color right) {
        // weighted distance function for colors a la
        // https://web.archive.org/web/20100316195057/http://www.dfanning.com/ip_tips/color2gray.html
        return Math.sqrt(
                Math.pow((right.getR()-left.getR()) * 0.3, 2d) +
                        Math.pow((right.getG()-left.getG()) * 0.59, 2d) +
                        Math.pow((right.getB()-left.getB()) * 0.11, 2d)
        );
    }

    public static Map<String, List<Color>> getMappingByColor() {
        return lightPadColorMappingByColor;
    }
}
