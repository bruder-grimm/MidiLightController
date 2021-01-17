package controller;

public enum LightMode {
    STATIC(9), FLASHING(10), PULSING(11);

    public final int channel;
    LightMode(int channel) {
        this.channel = channel;
    }
}