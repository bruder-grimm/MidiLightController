package midi;

import java.util.function.BiConsumer;

import static controller.ControlMapping.*;

public class PotentiometerReceiver extends MidiCallBackConverter {
    public void registerPotentiometerChangeCallback(BiConsumer<Integer, Float> callback) {
        registerCallback(message -> {
            int selectedPot = message.getData1();
            if (message.getCommand() == DEVICE_OK || selectedPot < TEMPO_POT || selectedPot > UNLABELED_3_POT) {
                // operating outside of the poti range
                return;
            }

            // normalize Data to be between 0 and 1 and round to one decimal point
            float value = (float) Math.round((message.getData2() / 127f) * 5) / 5;
            callback.accept(selectedPot, value);
        });
    }
}
