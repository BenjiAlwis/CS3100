package part4_debugging.music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Music utility methods.
 * Run these tests to verify your bug fixes work correctly.
 * 
 * To run these tests with Gradle:
 *   ./gradlew test --tests "part4_debugging.music.MusicTest"
 * 
 * Or run all Part 4 tests:
 *   ./gradlew test --tests "part4_debugging.*"
 */
class MusicTest {
    private Instrument[] instruments;
    private Instrument[] singleGuitar;
    private Instrument[] emptyArray;

    @BeforeEach
    void setUp() {
        instruments = new Instrument[] {
            new Guitar("Fender Stratocaster", 6),
            new Drums("Pearl Export", 5),
            new Keyboard("Yamaha P-125", 88),
            new Flute("Yamaha YFL-222", "silver"),
            new Guitar("Gibson Les Paul", 6)
        };
        
        singleGuitar = new Instrument[] {
            new Guitar("Test Guitar", 6)
        };
        
        emptyArray = new Instrument[] {};
    }

    // ==================== calculateTotalInstruments Tests ====================

    @Test
    @DisplayName("Calculate total: Should return correct count")
    void testCalculateTotalInstruments_ShouldReturnCorrectCount() {
        assertEquals(5, Music.calculateTotalInstruments(instruments));
        assertEquals(1, Music.calculateTotalInstruments(singleGuitar));
        assertEquals(0, Music.calculateTotalInstruments(emptyArray));
    }

    // ==================== seperateInstruments Tests ====================

    @Test
    @DisplayName("Separate instruments: Should not throw ArrayIndexOutOfBoundsException")
    void testSeperateInstruments_ShouldNotThrowException() {
        assertDoesNotThrow(() -> Music.seperateInstruments(instruments),
            "Should not throw ArrayIndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Separate instruments: Should group instruments by type correctly")
    void testSeperateInstruments_ShouldGroupByType() {
        Map<Instruments, Instrument[]> separated = Music.seperateInstruments(instruments);
        
        // Should have 4 different types
        assertEquals(4, separated.size(), "Should have 4 instrument types");
        
        // Guitar group should have 2 guitars
        assertTrue(separated.containsKey(Instruments.GUITAR), "Should have GUITAR key");
        assertEquals(2, separated.get(Instruments.GUITAR).length, 
            "Should have 2 guitars in GUITAR group");
        
        // Drums group should have 1 drum
        assertTrue(separated.containsKey(Instruments.DRUMS), "Should have DRUMS key");
        assertEquals(1, separated.get(Instruments.DRUMS).length, 
            "Should have 1 drum in DRUMS group");
    }

    // ==================== playAll Tests ====================

    @Test
    @DisplayName("Play all: Should have each instrument on new line")
    void testPlayAll_ShouldHaveNewLines() {
        String result = Music.playAll(instruments);
        
        // Should contain newlines between instruments
        String[] lines = result.split("\n");
        assertEquals(5, lines.length, 
            "Each instrument should be on its own line (5 instruments = 5 lines)");
    }

    @Test
    @DisplayName("Play all: Empty array should return appropriate message")
    void testPlayAll_EmptyArray_ShouldReturnMessage() {
        String result = Music.playAll(emptyArray);
        assertEquals("No instruments to play!", result);
    }

    @Test
    @DisplayName("Play all: Null array should return appropriate message")
    void testPlayAll_NullArray_ShouldReturnMessage() {
        String result = Music.playAll(null);
        assertEquals("No instruments to play!", result);
    }

    // ==================== hasInstrumentType Tests ====================

    @Test
    @DisplayName("Has instrument type: Should return true when type exists")
    void testHasInstrumentType_TypeExists_ShouldReturnTrue() {
        assertTrue(Music.hasInstrumentType(instruments, Instruments.GUITAR),
            "Should find GUITAR in instruments");
        assertTrue(Music.hasInstrumentType(instruments, Instruments.DRUMS),
            "Should find DRUMS in instruments");
        assertTrue(Music.hasInstrumentType(instruments, Instruments.KEYBOARD),
            "Should find KEYBOARD in instruments");
        assertTrue(Music.hasInstrumentType(instruments, Instruments.FLUTE),
            "Should find FLUTE in instruments");
    }

    @Test
    @DisplayName("Has instrument type: Should return false when type does not exist")
    void testHasInstrumentType_TypeNotExists_ShouldReturnFalse() {
        // singleGuitar only has a guitar
        assertFalse(Music.hasInstrumentType(singleGuitar, Instruments.DRUMS),
            "Should NOT find DRUMS in guitar-only array");
        assertFalse(Music.hasInstrumentType(singleGuitar, Instruments.KEYBOARD),
            "Should NOT find KEYBOARD in guitar-only array");
        assertFalse(Music.hasInstrumentType(singleGuitar, Instruments.FLUTE),
            "Should NOT find FLUTE in guitar-only array");
    }

    @Test
    @DisplayName("Has instrument type: Empty array should return false")
    void testHasInstrumentType_EmptyArray_ShouldReturnFalse() {
        assertFalse(Music.hasInstrumentType(emptyArray, Instruments.GUITAR),
            "Empty array should return false");
    }

    // ==================== countByType Tests ====================

    @Test
    @DisplayName("Count by type: Should return correct count for guitars")
    void testCountByType_Guitars_ShouldReturnTwo() {
        assertEquals(2, Music.countByType(instruments, Instruments.GUITAR),
            "Should count 2 guitars");
    }

    @Test
    @DisplayName("Count by type: Should return correct count for single items")
    void testCountByType_SingleItems_ShouldReturnOne() {
        assertEquals(1, Music.countByType(instruments, Instruments.DRUMS),
            "Should count 1 drum set");
        assertEquals(1, Music.countByType(instruments, Instruments.KEYBOARD),
            "Should count 1 keyboard");
        assertEquals(1, Music.countByType(instruments, Instruments.FLUTE),
            "Should count 1 flute");
    }

    @Test
    @DisplayName("Count by type: Should return zero when type not present")
    void testCountByType_TypeNotPresent_ShouldReturnZero() {
        assertEquals(0, Music.countByType(singleGuitar, Instruments.DRUMS),
            "Should return 0 when type not present");
    }

    @Test
    @DisplayName("Count by type: Empty array should return zero")
    void testCountByType_EmptyArray_ShouldReturnZero() {
        assertEquals(0, Music.countByType(emptyArray, Instruments.GUITAR),
            "Empty array should return 0");
    }

    @Test
    @DisplayName("Count by type: Null inputs should return zero")
    void testCountByType_NullInputs_ShouldReturnZero() {
        assertEquals(0, Music.countByType(null, Instruments.GUITAR),
            "Null array should return 0");
        assertEquals(0, Music.countByType(instruments, null),
            "Null type should return 0");
    }
}
