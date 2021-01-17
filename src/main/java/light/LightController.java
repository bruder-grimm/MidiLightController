package light;

import color.ColorHandler;
import color.ColorHandler.Color;
import com.mollin.yapi.YeelightDevice;
import com.mollin.yapi.YeelightMusicServer;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;
import com.mollin.yapi.flow.YeelightFlow;
import com.mollin.yapi.flow.transition.YeelightColorTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.mollin.yapi.enumeration.YeelightEffect.SMOOTH;
import static com.mollin.yapi.enumeration.YeelightFlowAction.RECOVER;
import static com.mollin.yapi.flow.YeelightFlow.INFINITE_COUNT;

public class LightController {
    private final YeelightMusicServer server;
    private int brightness;
    private boolean isStrobing = false;
    private int strobeSpeed = 100;
    private Color currentColor = ColorHandler.DRUMPAD_PRESS_COLOR;

    public LightController(int initialBrightness, String firstIp, String... subsequentIps) throws IOException {
        server = new YeelightMusicServer();
        brightness = initialBrightness;

        List<String> ips = new ArrayList<>();
        ips.add(firstIp);
        ips.addAll(Arrays.asList(subsequentIps));

        ips.forEach(ip -> { try {
            YeelightDevice device = new YeelightDevice(ip);
            server.register(device);
        } catch (YeelightSocketException e) {
            System.out.println("Couldn't communicate with yeelight, reason: " + e.getMessage());
        }});

        try {
            server.setPower(true);
            server.setEffect(SMOOTH);
            server.setDuration(200);
        } catch (YeelightSocketException | YeelightResultErrorException e) {
            System.out.println("Couldn't communicate with yeelight, reason: " + e.getMessage());
        }
    }

    public void setLightColor(Color color) {
        try {
            currentColor = color;
            if (isStrobing) {
                startStrobe(strobeSpeed, currentColor);
            } else {
                server.setRGB(
                        currentColor.getR(),
                        currentColor.getG(),
                        currentColor.getB()
                );
            }
        } catch (YeelightResultErrorException | YeelightSocketException e) {
            System.out.println("Couldn't communicate with yeelight, reason: " + e.getMessage());
        }
    }

    public void setBrightness(int value) {
        System.out.println(value);
        try {
            brightness = value;
            if (isStrobing) {
                startStrobe(strobeSpeed, currentColor);
            } else {
                server.setBrightness(value);
            }
        } catch (YeelightSocketException | YeelightResultErrorException e) {
            System.out.println("Couldn't communicate with yeelight, reason: " + e.getMessage());
        }
    }

    public void setStrobeSpeed(int strobeSpeed) {
        this.strobeSpeed = strobeSpeed;
    }

    public boolean isStrobing() {
        return isStrobing;
    }

    public void setStrobing(boolean strobe) {
        isStrobing = strobe;
    }

    public void startStrobe(int bpm, Color color) {
        int interval = 60000 / bpm; // there are 60k ms in a minute

        Function<Integer, YeelightColorTransition> strobeFlow = (strobeBrightness) ->
                new YeelightColorTransition(color.getR(), color.getG(), color.getB(), interval / 2, strobeBrightness);

        YeelightFlow flow = new YeelightFlow(INFINITE_COUNT, RECOVER,
                strobeFlow.apply(0),
                strobeFlow.apply(brightness)
        );

        try {
            server.startFlow(flow);
        } catch (YeelightResultErrorException | YeelightSocketException e) {
            e.printStackTrace();
        }
    }

    @Override protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close()  {
        try {
            server.setPower(false);
            server.close();
            System.out.println("Yeelight closed");
        } catch (YeelightResultErrorException | YeelightSocketException | IOException e) {
            System.out.println("Couldn't close connection with yeelight, reason: " + e.getMessage());
        }
    }
}
