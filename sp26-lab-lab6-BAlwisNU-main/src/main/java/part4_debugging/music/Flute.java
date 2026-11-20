package part4_debugging.music;

public class Flute extends Instrument {
    private String material;

    public Flute(String name, String material) {
        super(name, Instruments.FLUTE);
        this.material = material;
    }

    @Override
    public String play() {
        return name + " plays a beautiful note!";
    }

    @Override
    public String tune() {
        return "Adjusting the " + material + " " + name;
    }
}
