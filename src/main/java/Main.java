import controller.ControlSurface;
import light.LightController;
import color.ColorHandler;
import util.Tuple;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws MidiUnavailableException, IOException {

        // get all predefined colors
        List<ColorHandler.Color> yellow = ColorHandler.YELLOWS;
        List<ColorHandler.Color> green = ColorHandler.GREENS;
        List<ColorHandler.Color> cyan = ColorHandler.CYANS;
        List<ColorHandler.Color> blue = ColorHandler.BLUES;
        List<ColorHandler.Color> violet = ColorHandler.VIOLETS;
        List<ColorHandler.Color> magenta = ColorHandler.MAGENTAS;
        List<ColorHandler.Color> gray = ColorHandler.GRAY;

        // and map them to the pads on the launchkey mini
        Map<Tuple<Integer, Integer>, ColorHandler.Color> padColorMap = new HashMap<>();
        padColorMap.put(Tuple.apply(0, 0), yellow.get(4));
        padColorMap.put(Tuple.apply(0, 1), green.get(4));
        padColorMap.put(Tuple.apply(0, 2), cyan.get(4));
        padColorMap.put(Tuple.apply(0, 3), blue.get(4));
        padColorMap.put(Tuple.apply(0, 4), violet.get(4));
        padColorMap.put(Tuple.apply(0, 5), violet.get(5));
        padColorMap.put(Tuple.apply(0, 6), magenta.get(4));
        padColorMap.put(Tuple.apply(0, 7), gray.get(1));
        padColorMap.put(Tuple.apply(1, 0), yellow.get(6));
        padColorMap.put(Tuple.apply(1, 1), green.get(6));
        padColorMap.put(Tuple.apply(1, 2), cyan.get(6));
        padColorMap.put(Tuple.apply(1, 3), cyan.get(7));
        padColorMap.put(Tuple.apply(1, 4), violet.get(6));
        padColorMap.put(Tuple.apply(1, 5), violet.get(7));
        padColorMap.put(Tuple.apply(1, 6), magenta.get(1));
        padColorMap.put(Tuple.apply(1, 7), magenta.get(2));

        // open the yeelight...
        LightController lightController = new LightController(100, "192.168.178.10", "192.168.178.11");
        // ... open the launchkey mini ...
        ControlSurface controlSurface = new ControlSurface();

        // ... and give control to our handler
        ControlHandler handler = new ControlHandler(controlSurface, lightController, padColorMap);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            lightController.close();
            controlSurface.close();
        }));

        new Thread(handler::start).start();
    }
}
