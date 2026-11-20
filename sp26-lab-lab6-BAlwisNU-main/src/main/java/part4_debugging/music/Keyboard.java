package part4_debugging.music;

public class Keyboard extends Instrument {
    private int numberOfKeys;

    public Keyboard(String name, int numberOfKeys) {
        super(name, Instruments.KEYBOARD);
        this.numberOfKeys = numberOfKeys;
    }

    @Override
    public String play() {
        return name + " plays a melody!";
    }

    @Override
    public String tune() {
        return name + " is an electronic keyboard - auto-tuned!";
    }
}
