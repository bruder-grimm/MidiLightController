import color.ColorHandler.Color;
import controller.ControlSurface;
import controller.LightMode;
import light.LightController;
import midi.DrumPadReceiver;
import midi.PotentiometerReceiver;
import util.Tuple;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static color.ColorHandler.DRUMPAD_PRESS_COLOR;
import static controller.ControlMapping.*;
import static controller.LightMode.*;

public class ControlHandler {
    private final ControlSurface controlSurface;
    private final LightController lightController;
    private final Map<Tuple<Integer, Integer>, Color> padColorMap;

    // set the initial pad to be toggled
    AtomicReference<Tuple<Integer, Integer>> selectedPad;
    Color selectedLightColor;

    public ControlHandler(ControlSurface controlSurface, LightController lightController, Map<Tuple<Integer, Integer>, Color> padColorMap) {
        this.controlSurface = controlSurface;
        this.lightController = lightController;
        this.padColorMap = padColorMap;

        Tuple<Integer, Integer> selectedPad = Tuple.apply(0, 0);
        this.selectedPad = new AtomicReference<>(selectedPad);
        this.selectedLightColor = padColorMap.get(selectedPad);
    }

    public void start() {
        System.out.print("Initializing pads... ");
        initializePads();
        System.out.println("Done!");

        DrumPadReceiver drumPadReceiver = new DrumPadReceiver();
        PotentiometerReceiver potentiometerReceiver = new PotentiometerReceiver();

        drumPadReceiver.registerDrumPadPressCallback(this::handleDrumpadPress);
        drumPadReceiver.registerDrumPadReleaseCallback(this::handleDrumpadRelease);

        potentiometerReceiver.registerPotentiometerChangeCallback(this::handlePotentiometerInput);

        System.out.println("Listening for changes...");
        controlSurface.setDrumPadReceiver(drumPadReceiver);
        controlSurface.setPotentiometerReceiver(potentiometerReceiver);
    }

    private void initializePads() {
        padColorMap.forEach((pad, padColor) -> updatePhysicalPad(pad, padColor, STATIC));
        updateActivePadAndLighting();
    }

    private void updateActivePadAndLighting() {
        new Thread(() -> {
            padColorMap.put(selectedPad.get(), selectedLightColor);
            updatePhysicalPad(selectedPad.get(), selectedLightColor, PULSING);
            updatePhysicalLight();
        }).start();
    }

    private void updatePhysicalLight() {
        lightController.setLightColor(selectedLightColor);
    }

    private void updatePhysicalPad(Tuple<Integer, Integer> pad, Color color, LightMode mode) {
        controlSurface.sendColorData(mode, pad._1, pad._2, color);
    }

    private void handleDrumpadPress(Tuple<Integer, Integer> pressedPad) {
        updatePhysicalPad(pressedPad, DRUMPAD_PRESS_COLOR, STATIC); // indicate press
        if (pressedPad._1 == 1) { // we're in bottom row
            lightController.setLightColor(padColorMap.get(pressedPad)); // hold the color as long as the pad is pressed
        }
    }

    private void handleDrumpadRelease(Tuple<Integer, Integer> releasedPad) {
        updatePhysicalPad(releasedPad, padColorMap.get(releasedPad), STATIC); // indicate release
        if (releasedPad._1 != 1) { // we're not in bottom row, so we want to update the current color...
            updatePhysicalPad(selectedPad.get(), padColorMap.get(selectedPad.get()), STATIC); // ... stop flashing the old drumpad ...
            selectedLightColor = padColorMap.get(releasedPad);
            updatePhysicalPad(releasedPad, padColorMap.get(releasedPad), PULSING);
            selectedPad.set(releasedPad); // ... and set the selected Pad
        }
        updatePhysicalLight(); // revert back to original color if not in bottom row or change to new selected color
    }

    private void handlePotentiometerInput(int potentiometer, float value) {
        switch (potentiometer) {
            case TEMPO_POT: // sets the speed at which to strobe (if strobing)
                int strobeSpeed = (int) (value * 500);
                lightController.setStrobeSpeed(strobeSpeed);
                lightController.setStrobing(strobeSpeed >= 20);
                updateActivePadAndLighting();
                break;
            case SWING_POT: // changes brightness
                lightController.setBrightness((int) (value * 100));
                break;
            case GATE_POT: // change R
                Color nextColor = Color.withClosestPadColor((int) (value * 255), selectedLightColor.getG(), selectedLightColor.getB());
                System.out.println(selectedLightColor);
                System.out.println(nextColor);
                selectedLightColor = nextColor;
                updateActivePadAndLighting();
                break;
            case MUTATE_POT: // change G
                nextColor = Color.withClosestPadColor(selectedLightColor.getR(), (int) (value * 255), selectedLightColor.getB());
                System.out.println(selectedLightColor);
                System.out.println(nextColor);
                selectedLightColor = nextColor;
                updateActivePadAndLighting();
                break;
            case DEVIATE_POT: // change B
                nextColor = Color.withClosestPadColor(selectedLightColor.getR(), selectedLightColor.getG(), (int) (value * 255));
                System.out.println(selectedLightColor);
                System.out.println(nextColor);
                selectedLightColor = nextColor;
                updateActivePadAndLighting();
                break;
        }
    };
}
