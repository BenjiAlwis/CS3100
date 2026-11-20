package part4_debugging.music;

/**
 * Abstract base class for all instruments.
 */
public abstract class Instrument {
    String name; // the name of the instrument as well as the brand and model
    Instruments type; // represents the type of instrument itself

    /**
     * public constructor to make a generic instrument. 
     * 
     * @param name the name of the instrument, representing the brand and model.
     * @param type the type of instrument, representing whatever object we are using, drums, 
     */
    public Instrument(String name, Instruments type) {
        this.name = name;
        this.type = type;
    }

    /**
     * abstract method to be implemented in the child class.
     * @return a string that states what was played.
     */
    public abstract String play();

    /**
     * abstract method to be implemented in the child class.
     * @return a string that states what was tuned.
     */
    public abstract String tune();

    /**
     * getter method to get the name of the given instrument.
     * @return the name of the instrument.
     */
    public String getName() {
        return name;
    }

    /**
     * method to get the type of the instrument.
     * @return the type of instrument, represented as the enumeration.
     */
    public Instruments getType() {
        return type;
    }
}
