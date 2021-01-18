package controller;

import color.ColorHandler;
import color.ColorHandler.Color;
import org.brudergrimm.jmonad.option.Option;
import util.Tuple;

import javax.sound.midi.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ControlSurface {
    private final Map<Tuple<Integer, Integer>, Integer> pads = PadMapping.getPadMapping();
    private final List<MidiDevice> openDevices = new LinkedList<>();

    private Receiver drumPadOut;
    private Transmitter drumPadIn;
    private Transmitter potentiometerIn;

    public ControlSurface() throws MidiUnavailableException {
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (info.getDescription().contains("Launchkey Mini MK3 DAW Port")) {
                MidiDevice daw = MidiSystem.getMidiDevice(info);
                openDevice(daw);
                // this is the ugliest shit. Since there's no pattern matching, this is how we have to determine
                // whether we're working with midi in our out
                try {
                    drumPadOut = daw.getReceiver();
                } catch (MidiUnavailableException ignored) {
                    drumPadIn = daw.getTransmitter();
                }
            // if potentiometerIn isn't null anymore we've already found the midi in. I hate everything about this
            } else if (info.getDescription().contains("Launchkey Mini MK3 MIDI Port") && potentiometerIn == null) {
                MidiDevice midi = MidiSystem.getMidiDevice(info);
                try {
                    openDevice(midi);
                    potentiometerIn = midi.getTransmitter();
                } catch (MidiUnavailableException e) {
                    midi.close();
                    potentiometerIn = null;
                    System.out.println(e.getMessage() + ", trying next port");
                }
            }
        }

        enableInControl();
    }

    private void openDevice(MidiDevice device) throws MidiUnavailableException {
        if (device.isOpen()) {
            System.out.println("Given device is open in another program already, closing it!");
        }
        device.open();
        openDevices.add(device);
    }

    private void enableInControl() {
        try {
            drumPadOut.send(new ShortMessage(ShortMessage.NOTE_ON, 15, 12, 127), -1);
        } catch (InvalidMidiDataException e) {
            System.out.println("Couldn't assume control, reason: " + e.getMessage());
        } catch (NullPointerException npe) {
            System.out.println("Couldn't establish connection with launchkey, program will fail now");
        }
    }

    public void setDrumPadReceiver(Receiver receiver) {
        setReceiver(drumPadIn, receiver);
    }

    public void setPotentiometerReceiver(Receiver receiver) {
        setReceiver(potentiometerIn, receiver);
    }

    private void setReceiver(Transmitter transmitter, Receiver receiver) {
        Option.apply(transmitter.getReceiver()).ifSome(Receiver::close);
        transmitter.setReceiver(receiver);
    }

    public void sendColorData(LightMode mode, int row, int column, Color color) {
        int pad = pads.get(Tuple.apply(row, column));
        try {
            drumPadOut.send(new ShortMessage(ShortMessage.NOTE_ON, mode.channel, pad, color.getPadColor()), -1);
        } catch (InvalidMidiDataException e) {
            System.out.println("Couldn't send MIDI Message, reason: " + e.getMessage());
        }
    }

    public void reset() {
        enableInControl();

        BiConsumer<Integer, Integer> resetPadAt = (row, col) -> {
            int pad = pads.get(Tuple.apply(row, col));
            try {
                ShortMessage padReset = new ShortMessage(ShortMessage.NOTE_ON, LightMode.STATIC.channel, pad, 0);
                drumPadOut.send(padReset, -1);
            } catch (InvalidMidiDataException e) {
                System.out.println("Couldn't reset pads, reason: " + e.getMessage());
            }
        };

        for(int row = 0; row < 2; row++) {
            for (int col = 0; col < 8; col++) {
                resetPadAt.accept(row, col);
            }
        }
    }

    public void close() {
        reset();
        openDevices.forEach(MidiDevice::close);
        System.out.println("Launchkey closed");
    }

    @Override protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}