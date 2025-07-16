package nz.ac.auckland.se281;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import nz.ac.auckland.se281.cli.MessageCli;
import nz.ac.auckland.se281.cli.Utils;
import nz.ac.auckland.se281.engine.Game;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;

/** You cannot modify this class! */
public abstract class SysCliTest {

  private static final String NEW_LINE = System.lineSeparator();
  private static final String DELIMITER_RUN = "---<END RUN>---";
  protected static List<Object[]> inputs = new ArrayList<>();
  private static String testName;
  private static int testCount = 0;
  private static int updateCount;

  private ByteArrayOutputStream captureOut;
  private ByteArrayOutputStream captureErr;
  private PrintStream origOut;
  private PrintStream origErr;
  private Class<?> mainClass;

  public static void incrementUpdateCount() {
    updateCount++;
  }

  public static String getTestName() {
    return testName;
  }

  public SysCliTest(Class<?> mainClass) {
    this.mainClass = mainClass;
  }

  public String getCaptureOut() {
    return captureOut.toString();
  }

  /** Timeout if test runs longer than 10 seconds */
  @Rule public org.junit.rules.Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

  /**
   * Configures the test output and input streams, by creating new temporary streams while storing
   * the default.
   */
  @Before
  public void setUp() {
    // Store defaults
    origOut = System.out;
    origErr = System.err;

    // Set console output and error streams
    captureOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(captureOut));
    captureErr = new ByteArrayOutputStream();
    System.setErr(new PrintStream(captureErr));

    // Prepare to record test performance
    testName = "test_" + testCount;
    updateCount = 0;
  }

  /**
   * Restores the test output and input streams back to the defaults and prints the relevant System
   * out and err output messages where necessary.
   */
  @After
  public void tearDown() {
    // Restore defaults
    System.setOut(origOut);
    System.setErr(origErr);

    // Display capture messages
    String captureOutMessage = captureOut.toString();
    String captureErrMessage = captureErr.toString();

    if (captureOutMessage.length() > 1) {
      System.out.println(NEW_LINE + "the System.out.print was :" + NEW_LINE + captureOutMessage);
    }

    if (captureErrMessage.length() > 1) {
      System.out.println(NEW_LINE + "the System.err.print was :" + NEW_LINE + captureErrMessage);
    }

    // If the test affects zero branches then remove from consideration
    if (updateCount == 0) {
      inputs.remove(testCount);
    } else {
      testCount++;
    }

    updateCount = 0;
  }

  /**
   * Takes an unspecified number of strings as an array of commands to construct a string list of
   * commands for the cli which are then executed by the main code.
   *
   * @param commands the strings of commands to be run
   * @throws NoSuchElementException
   * @throws SecurityException
   * @throws InstantiattionException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   */
  public void runCommands(Object... commands)
      throws NoSuchElementException,
          IllegalArgumentException,
          IllegalAccessException,
          InstantiationException,
          SecurityException,
          NoSuchMethodException,
          InvocationTargetException {
    inputs.add(commands);

    StringBuilder sb = new StringBuilder();
    for (Object cmdString : commands) {
      sb.append(cmdString.toString());
      sb.append(NEW_LINE);
    }

    sb.append("exit");
    Utils.scanner = new Scanner(sb.toString());
    Object main = mainClass.getConstructor(Game.class).newInstance(new Game());

    mainClass.getMethod("start").invoke(main);

    // Display end of run message after completion of commands.
    System.out.println(NEW_LINE + DELIMITER_RUN);
  }

  /**
   * Asserts that the input string occurs within the console stream and fails the test if the
   * condition is not met.
   *
   * @param s the string to check for.
   */
  public void assertContains(String s) {
    assertContains(s, captureOut.toString());
  }

  private void assertContains(String s, String output) {
    if (!output.contains(s)) {
      Assert.fail(
          "The test is expecting the following output in the console, but it was not there: "
              + NEW_LINE
              + "\t\""
              + s
              + "\"");
    }
  }

  /**
   * Asserts that the input string occurs within the console stream at the given round and fails the
   * test if the condition is not met.
   *
   * @param s the string to check for.
   */
  public void assertContainsAtRound(String s, int round) {

    try {
      assertContains(s, captureOut.toString().split("Starting round")[round]);

    } catch (Exception e) {
      Assert.fail(
          "Something is wrong in your code, your should print something like this after each round"
              + MessageCli.START_ROUND.toString());
    }
  }

  /**
   * Asserts that the input string does not occur within the console and fails the test if the
   * condition is not met. This method is case sensitive.
   *
   * @param s the string to check for.
   */
  public void assertDoesNotContain(String s) {
    assertDoesNotContain(s, captureOut.toString());
  }

  private void assertDoesNotContain(String s, String output) {

    if (output.toString().contains(s)) {
      Assert.fail(
          "The test is expecting that the following output WAS NOT in the console, but was:"
              + NEW_LINE
              + "\t\""
              + s
              + "\"");
    }
  }

  /**
   * Asserts that the input string DOES NOT occur within the console stream at the given round and
   * fails the test if the condition is not met.
   *
   * @param s the string to check for.
   */
  public void assertDoesNotContainAtRound(String s, int round) {

    try {
      assertDoesNotContain(s, captureOut.toString().split("Starting round")[round]);

    } catch (Exception e) {
      Assert.fail(
          "Something is wrong in your code, your should print something like this after each round"
              + MessageCli.START_ROUND.toString());
    }
  }

  /**
   * Checks that the output from running is correct. The run value should be greater than 0 and less
   * than or equal to the number of instruction sets in one test. The console output will be split
   * by delimiterRun message and should return an array with zero or more elements. (Check if one or
   * more).
   *
   * @param s Unsure of function.
   * @param run integer index of the intended instruction set output.
   */
  private void checkRun(int run) { // Can we remove the String parameter?
    String captureOutMessage = captureOut.toString();
    if (run < 0
        || !captureOutMessage.contains(DELIMITER_RUN)
        || run > captureOutMessage.split(DELIMITER_RUN).length - 2) {
      throw new RuntimeException("Something is wrong with the test case!");
    }
  }

  public String getOutput() {
    return captureOut.toString();
  }

  /**
   * Asserts that the input string occurs within a substring of the captureOut and fails the test if
   * the condition is not met. The substring is obtained by splitting the captureOut message at
   * every occurrence of the runDelimiter message, and indexing by the run value.
   *
   * <p>For example, the second set of instructions in a test will have an index 1, and the
   * corresponding output will be indexed at 1 as well. Therefore, a test for this instruction set
   * would use a run value of 1 to check the string occurs in the relevant output.
   *
   * @param s the String to check for
   * @param run index of the instruction set and console output of interest
   */
  public void assertContains(String s, int run) {
    checkRun(run);
    if (!captureOut.toString().split(DELIMITER_RUN)[run].contains(s)) {
      Assert.fail(
          "The test is expecting the following output in the console but was not there "
              + NEW_LINE
              + s);
    }
  }

  /**
   * Asserts that the input string does not occur within a substring of the captureOut and fails the
   * test if the condition is not met. The substring is obtained by splitting the captureOut message
   * at every occurrence of the runDelimiter message, and indexing by the run value.
   *
   * <p>For example, the second set of instructions in a test will have an index 1, and the
   * corresponding output will be indexed at 1 as well. Therefore, a test for this instruction set
   * would use a run value of 1 to check the string does not occur in the relevant output.
   *
   * @param s the String to check for
   * @param run index of the instruction set and console output of interest
   */
  public void assertDoesNotContain(String s, int run) {
    checkRun(run);
    if (captureOut.toString().split(DELIMITER_RUN)[run].contains(s)) {
      Assert.fail(
          "The test is expecting that the following output WAS NOT in the console but was there "
              + NEW_LINE
              + s);
    }
  }
}
