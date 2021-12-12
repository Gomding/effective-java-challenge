package item22_20211212;

public class SamsungTvRemoteControl implements TvRemoteControl {

    private boolean turnOnPower;
    private int channel = 0;
    private int volume = 0;

    @Override
    public void turnOn() {
        this.turnOnPower = true;
    }

    @Override
    public void turnOff() {
        this.turnOnPower = false;
    }

    @Override
    public void upChannel() {
        this.channel++;
    }

    @Override
    public void downChannel() {
        this.channel--;
    }

    @Override
    public void upVolume() {
        this.volume++;
    }

    @Override
    public void downVolume() {
        this.volume--;
    }
}
