package part4_debugging.music;

public class Drums extends Instrument {
    private int numberOfDrums;

    public Drums(String name, int numberOfDrums) {
        super(name, Instruments.DRUMS);
        this.numberOfDrums = numberOfDrums;
    }

    @Override
    public String play() {
        return name + " beats a rhythm!";
    }

    @Override
    public String tune() {
        return "Tightening " + numberOfDrums + " drum heads on " + name;
    }
}
