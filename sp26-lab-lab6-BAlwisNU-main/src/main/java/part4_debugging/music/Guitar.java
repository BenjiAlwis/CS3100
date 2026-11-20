package part4_debugging.music;

public class Guitar extends Instrument {
    private int numberOfStrings;

    public Guitar(String name, int numberOfStrings) {
        super(name, Instruments.GUITAR);
        this.numberOfStrings = numberOfStrings;
    }

    @Override
    public String play() {
        return name + " strums a chord!";
    }

    @Override
    public String tune() {
        return "Tuning " + numberOfStrings + " strings on " + name;
    }
}
