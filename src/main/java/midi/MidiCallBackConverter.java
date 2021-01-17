package midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MidiCallBackConverter implements Receiver {
    private final List<Consumer<ShortMessage>> callbacks = new LinkedList<>();

    public void registerCallback(Consumer<ShortMessage> callback) {
        callbacks.add(callback);
    }

    @Override public void send(MidiMessage message, long timeStamp) {
        callbacks.forEach(shortMessageConsumer ->
                shortMessageConsumer.accept((ShortMessage) message)
        );
    }

    public void close() {}
}