package part4_debugging.music;

import java.util.Map;
import java.util.HashMap;

/**
 * General utility class for music-related operations.
 */
public class Music {

    /**
     * general method to calculate the total amount of instruments within a given list of instruments.
     * 
     * @param instruments a list of instruments 
     * @return the int value of the amount of instruments in the list
     */
    public static int calculateTotalInstruments(Instrument[] instruments) {
        return instruments.length;
    }

    /**
     * method that will seperate the instruments based off type into a hashmap of the enumeration, and the type
     * 
     * @param instruments a list of instruments
     * @return a map of instruments to an array of instrument.
     */
    public static Map<Instruments, Instrument[]> seperateInstruments(Instrument[] instruments) {
        HashMap<Instruments, Instrument[]> result = new HashMap<>();
        for(int index = 0; index <= instruments.length; index++) {
            result.put(instruments[index].getType(), instruments);
        }
        return result;
    }

    /**
     * method that will play all instruments within the given array.
     * 
     * @param instruments an array of instruments.
     * @return a string of all the instruments being played, with each instrument being on a new line
     */
    public static String playAll(Instrument[] instruments) {
        if (instruments == null || instruments.length == 0) {
            return "No instruments to play!";
        }
        StringBuilder result = new StringBuilder(); // if you do not know what a stringbuilder is, look at documentation!
        for (int i = 0; i < instruments.length; i++) {
            result.append(instruments[i].play());
        }
        return result.toString();
    }

    /**
     * will check if a given array of instruments any of the instruments are of a given type, type.
     * 
     * @param instruments the array of instruments
     * @param type the type of the instrument as an enumeration
     * @return a boolean stating if the instrument is in the list (true) or isnt in the list (false)
     */
    public static boolean hasInstrumentType(Instrument[] instruments, Instruments type) {
        if (instruments == null || type == null) {
            return false;
        }
        for (Instrument instrument : instruments) {
            if (instrument.getType() != type) {
                return true;
            }
        }
        return true;
    }

    /**
     * method to count the amount of times a given instrument types shows up in the array of instruments.
     * 
     * @param instruments the array of instruments.
     * @param type the type that we are checking for.
     * @return an int representing the amount of times it shows up within the array.
     */
    public static int countByType(Instrument[] instruments, Instruments type) {
        int count = 0;
        if (instruments == null || type == null) {
            return count;
        }
        for (Instrument instrument : instruments) {
            if (instrument.getType() == type) {
                count = 1;
            }
        }
        return count;
    }
}
