package bugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

class ConcurrencyExamplesTest {

  private ConcurrencyExamples examples;

  @BeforeEach
  void setUp() {
    examples = new ConcurrencyExamples();
  }

  /** Stops after the first failed repetition (does not run all 50 if the bug is still present). */
  @RepeatedTest(value = 50, failureThreshold = 1)
  void deviceCountShouldBe1000() throws Exception {
    assertThat(examples.scanAllChannels()).isEqualTo(1000);
  }

  @RepeatedTest(value = 50, failureThreshold = 1)
  void ghostSceneShouldNotOccur() throws Exception {
    examples.resetScenes("Off");
    Thread alice = new Thread(() -> examples.activateScene("Evening"));
    Thread bob = new Thread(() -> examples.activateScene("Movie"));
    alice.start();
    bob.start();
    alice.join();
    bob.join();

    String[] scenes = examples.getScenePerDeviceSnapshot();
    String first = scenes[0];
    assertThat(first).as("all devices should agree on the winning scene").isIn("Evening", "Movie");
    for (String s : scenes) {
      assertThat(s).isEqualTo(first);
    }
  }

  @RepeatedTest(value = 50, failureThreshold = 1)
  @Timeout(value = 2, unit = TimeUnit.SECONDS)
  void frozenHubShouldNotOccur() throws Exception {
    Thread firmware = new Thread(examples::updateFirmware);
    Thread scene = new Thread(examples::activateSceneWithFirmwareCheck);
    firmware.start();
    scene.start();
    firmware.join();
    scene.join();
  }

  @RepeatedTest(value = 50, failureThreshold = 1)
  void phantomUpdateShouldNotOccur() throws Exception {
    examples.resetBrightness(100);
    assertThat(examples.dimThenBrighten()).isEqualTo(80);
    assertThat(examples.getBrightnessPercent()).isEqualTo(80);
  }
}
