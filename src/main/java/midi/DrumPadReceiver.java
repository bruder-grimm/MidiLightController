package midi;

import controller.PadMapping;
import util.Tuple;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static controller.ControlMapping.DRUMPAD_PRESS;
import static controller.ControlMapping.DRUMPAD_RELEASE;

public class DrumPadReceiver extends MidiCallBackConverter {
    private final Map<Integer, Tuple<Integer, Integer>> reversePadMap = PadMapping
            .getPadMapping()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public void registerDrumPadPressCallback(Consumer<Tuple<Integer, Integer>> callback) {
        registerCallback(message -> {
            if (message.getCommand() != DRUMPAD_PRESS) {
                return;
            }
            callback.accept(reversePadMap.get(message.getData1()));
        });
    }

    public void registerDrumPadReleaseCallback(Consumer<Tuple<Integer, Integer>> callback) {
        registerCallback(message -> {
            if (message.getCommand() != DRUMPAD_RELEASE) {
                return;
            }
            callback.accept(reversePadMap.get(message.getData1()));
        });
    }
}
