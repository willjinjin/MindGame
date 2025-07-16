package nz.ac.auckland.se281;

import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import static nz.ac.auckland.se281.Main.Command.NEW_GAME;
import static nz.ac.auckland.se281.Main.Command.PLAY;
import static nz.ac.auckland.se281.Main.Command.SHOW_STATS;
import static nz.ac.auckland.se281.cli.MessageCli.ASK_HUMAN_INPUT;
import static nz.ac.auckland.se281.cli.MessageCli.GAME_NOT_STARTED;
import static nz.ac.auckland.se281.cli.MessageCli.INVALID_HUMAN_INPUT;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_END_GAME;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_INFO_MOVE;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_OUTCOME_ROUND;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_PLAYER_POINTS;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_POWER_COLOUR;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_TIE_GAME;
import static nz.ac.auckland.se281.cli.MessageCli.PRINT_WINNER_GAME;
import static nz.ac.auckland.se281.cli.MessageCli.START_ROUND;
import static nz.ac.auckland.se281.cli.MessageCli.WELCOME_PLAYER;
import nz.ac.auckland.se281.cli.Utils;

@RunWith(Suite.class)
@SuiteClasses({
  MainTest.Task1.class,
  MainTest.Task2.class,
  MainTest.Task3.class,
  MainTest.Task4.class,
  MainTest.Task5.class
})
public class MainTest {

  private static final String AI_NAME = "HAL-9000";

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task1 extends SysCliTest {

    public Task1() {
      super(Main.class);
    }

    @Test
    public void T1_01_welcome_message() throws Exception {
      runCommands(NEW_GAME + " EASY 5", "Valerio");
      assertContains(WELCOME_PLAYER.getMessage("Valerio"));
    }

    @Test
    public void T1_02_play_start_round() throws Exception {
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "R B");
      assertContains(START_ROUND.getMessage("1", "5"));
      assertDoesNotContain(START_ROUND.getMessage("0", "5"));
      assertDoesNotContain(START_ROUND.getMessage("2", "5"));
      assertDoesNotContain(START_ROUND.getMessage("1", "0"));
    }

    @Test
    public void T1_03_play_second_round() throws Exception {
      runCommands(
          NEW_GAME + " EASY 6",
          "Valerio", //
          PLAY,
          "RED GREEN", //
          PLAY,
          "BLUE YELLOW");
      assertContains(START_ROUND.getMessage("1", "6"));
      assertContains(START_ROUND.getMessage("2", "6"));
      assertDoesNotContain(START_ROUND.getMessage("3", "6"));
    }

    @Test
    public void T1_04_play_second_round() throws Exception {
      runCommands(
          NEW_GAME + " EASY 2",
          "Valerio", //
          PLAY,
          "RED GREEN", //
          PLAY,
          "BLUE YELLOW");
      assertContains(START_ROUND.getMessage("1", "2"));
      assertContains(START_ROUND.getMessage("2", "2"));
      assertDoesNotContain(START_ROUND.getMessage("0", "2"));
    }

    @Test
    public void T1_05_asks_input_when_playing() throws Exception {
      runCommands(
          NEW_GAME + " EASY 3",
          "Valerio", //
          PLAY,
          "R B");
      assertContains(ASK_HUMAN_INPUT.getMessage());
    }

    @Test
    public void T1_06_asks_input_when_playing_invalid_input() throws Exception {
      runCommands(
          NEW_GAME + " EASY 3",
          "Valerio", //
          PLAY,
          "rrr rrrr", "G B");
      assertContains(ASK_HUMAN_INPUT.getMessage());
    }

    @Test
    public void T1_07_invalid_input_then_valid() throws Exception {
      runCommands(
          NEW_GAME + " EASY 3",
          "Valerio", //
          PLAY,
          "This is not a valid input",
          "G Y");
      assertContains(INVALID_HUMAN_INPUT.getMessage());
      assertContains(START_ROUND.getMessage("1", "3"));
      assertDoesNotContain(START_ROUND.getMessage("2", "5"));
      assertContains(ASK_HUMAN_INPUT.getMessage());
    }

    @Test
    public void T1_08_prints_player_move_info() throws Exception {
      runCommands(
          NEW_GAME + " EASY 1",
          "Valerio", //
          PLAY,
          "RED GREEN");
      assertContains(PRINT_INFO_MOVE.getMessage("Valerio", "RED", "GREEN"));
    }

    @Test
    public void T1_09_input_is_case_insensitive() throws Exception {
      runCommands(
          NEW_GAME + " EASY 1",
          "Valerio", //
          PLAY,
          "g g");
      assertContains(PRINT_INFO_MOVE.getMessage("Valerio", "GREEN", "GREEN"));
    }

    @Test
    public void T1_10_invalid_input_then_valid_across_rounds() throws Exception {
      runCommands(
          NEW_GAME + " EASY 3",
          "Valerio", //
          PLAY,
          "INVALID INPUT",
          "G Y", //
          PLAY,
          "Y G");

      assertContainsAtRound(INVALID_HUMAN_INPUT.getMessage(), 1);
      assertDoesNotContainAtRound(INVALID_HUMAN_INPUT.getMessage(), 2);
    }

    @Test
    public void T1_11_input_with_three_colours_invalid() throws Exception {
      runCommands(
          NEW_GAME + " EASY 2",
          "Valerio", //
          PLAY,
          "RED GREEN BLUE",
          "RED GREEN");
      assertContainsAtRound(INVALID_HUMAN_INPUT.getMessage(), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage("Valerio", "RED", "GREEN"), 1);
    }

    @Test
    public void T1_12_input_with_one_colour_invalid() throws Exception {
      runCommands(
          NEW_GAME + " EASY 2",
          "Valerio", //
          PLAY,
          "RED",
          "GREEN RED");
      assertContainsAtRound(INVALID_HUMAN_INPUT.getMessage(), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage("Valerio", "GREEN", "RED"), 1);
    }

    @Test
    public void T1_13_input_with_invalid_colour_codes() throws Exception {
      runCommands(
          NEW_GAME + " EASY 1",
          "Valerio", //
          PLAY,
          "purple pink",
          "B G");
      assertContainsAtRound(INVALID_HUMAN_INPUT.getMessage(), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage("Valerio", "BLUE", "GREEN"), 1);
    }

    @Test
    public void T1_14_announce_power_colour_on_third_round() throws Exception {
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio",
          PLAY,
          "RED GREEN",
          PLAY,
          "BLUE YELLOW",
          PLAY,
          "YELLOW RED");

      assertDoesNotContainAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 1);
      assertDoesNotContainAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 2);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 3);
      assertContains(START_ROUND.getMessage("3", "5"));
    }

    @Test
    public void T1_15_announce_power_colour_on_third_round_different_seed() throws Exception {
      Utils.randomPowerNumber = new Random(19000);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio",
          PLAY,
          "RED GREEN",
          PLAY,
          "BLUE YELLOW",
          PLAY,
          "YELLOW RED");

      assertDoesNotContainAtRound(PRINT_POWER_COLOUR.getMessage("GREEN"), 1);
      assertDoesNotContainAtRound(PRINT_POWER_COLOUR.getMessage("GREEN"), 2);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("GREEN"), 3);
      assertContains(START_ROUND.getMessage("3", "5"));
    }

    @Test
    public void T1_16_announce_power_colour_on_sixth_round() throws Exception {
      Utils.randomPowerNumber = new Random(19876);
      runCommands(
          NEW_GAME + " EASY 10",
          "Valerio",
          PLAY,
          "RED GREEN",
          PLAY,
          "BLUE YELLOW",
          PLAY,
          "YELLOW RED",
          PLAY,
          "RED GREEN",
          PLAY,
          "BLUE YELLOW",
          PLAY,
          "YELLOW RED");
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("GREEN"), 3);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 6);
      assertContains(START_ROUND.getMessage("6", "10"));
    }

    @Test
    public void T1_17_prints_player_move_info() throws Exception {
      runCommands(NEW_GAME + " EASY 3", "Valerio", PLAY, "R GREEN");

      assertContains(PRINT_INFO_MOVE.getMessage("Valerio", "RED", "GREEN"));
      assertDoesNotContain(PRINT_INFO_MOVE.getMessage("Valerio", "R", "GREEN"));
    }

    @Test
    public void T1_18_prints_player_move_info_multiple_rounds() throws Exception {
      runCommands(NEW_GAME + " EASY 3", "Valerio", PLAY, "RED GREEN", PLAY, "YELLOW BLUE");

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage("Valerio", "RED", "GREEN"), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage("Valerio", "YELLOW", "BLUE"), 2);
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task2 extends SysCliTest {

    public Task2() {
      super(Main.class);
    }

    @Test
    public void T2_01_random_play() throws Exception {
      Utils.randomAi = new Random(1);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "R G");
      assertContains(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"));
      assertDoesNotContain(PRINT_INFO_MOVE.getMessage("Valerio", "BLUE", "RED"));
    }

    @Test
    public void T2_02_random_play_different_seed() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "R G");
      assertContains(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"));
    }

    @Test
    public void T2_03_random_play_different_rounds() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "R G",
          PLAY,
          "Y Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 2);
    }

    @Test
    public void T2_04_human_1_point_ai_0_point() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "R B");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1));
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0));
    }

    @Test
    public void T2_05_human_1_point_ai_1_point() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "G B");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1));
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1));
    }

    @Test
    public void T2_06_human_0_point_ai_1_point() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "G Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0));
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1));
    }

    @Test
    public void T2_07_human_1_1_point_ai_1_0_point_2_rounds() throws Exception {
      Utils.randomAi = new Random(2);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "G B",
          PLAY,
          "B Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 1);
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);
    }

    @Test
    public void T2_08_power_colour_guessed_by_human() throws Exception {
      Utils.randomAi = new Random(2);
      Utils.randomPowerNumber = new Random(465477);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "G B",
          PLAY,
          "B Y",
          PLAY,
          "B G");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);

      assertContains(PRINT_POWER_COLOUR.getMessage("GREEN"));
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "YELLOW"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 3), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);
    }

    @Test
    public void T2_09_power_colour_guessed_by_ai() throws Exception {
      Utils.randomAi = new Random(45);
      Utils.randomPowerNumber = new Random(4574875);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "B B",
          PLAY,
          "B R",
          PLAY,
          "R Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "YELLOW"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      assertContains(PRINT_POWER_COLOUR.getMessage("RED"));
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 3), 3);
    }

    @Test
    public void T2_10_power_colour_not_guessed() throws Exception {
      Utils.randomAi = new Random(1);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 5",
          "Valerio", //
          PLAY,
          "G R",
          PLAY,
          "G G",
          PLAY,
          "R Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "GREEN"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      assertContains(PRINT_POWER_COLOUR.getMessage("BLUE"));
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 3);
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task3 extends SysCliTest {

    public Task3() {
      super(Main.class);
    }

    @Test
    public void T3_01_first_round_is_random() throws Exception {
      Utils.randomAi = new Random(5555);
      runCommands(
          NEW_GAME + " MEDIUM 5",
          "Valerio", //
          PLAY,
          "R G");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 1);
    }

    @Test
    public void T3_02_second_round_change_strategy() throws Exception {
      Utils.randomAi = new Random(5555);
      runCommands(
          NEW_GAME + " MEDIUM 6",
          "Valerio", //
          PLAY,
          "R G",
          PLAY, // in the second round the game should switch strategy
          "Y Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 1);
      // this is random should not be this
      assertDoesNotContainAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 2);
      // this is the correct on as it avoids the Last Played Colour stategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "GREEN"), 2);
    }

    @Test
    public void T3_03_three_rounds() throws Exception {
      Utils.randomAi = new Random(56533333);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " MEDIUM 6",
          "Valerio", //
          PLAY,
          "R R",
          PLAY, // in the second round the game should switch strategy
          "G B",
          PLAY,
          "B B");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "BLUE"), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "GREEN"), 2);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 3);
    }

    @Test
    public void T3_04_four_rounds() throws Exception {
      Utils.randomAi = new Random(27890);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " MEDIUM 6",
          "Valerio", //
          PLAY,
          "R R",
          PLAY, // in the second round the game should switch strategy
          "G B",
          PLAY,
          "B B",
          PLAY,
          "Y B");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "BLUE"), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "YELLOW"), 2);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 3);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"), 4);
    }

    @Test
    public void T3_05_four_rounds_with_correct_outcome() throws Exception {
      Utils.randomAi = new Random(72309812);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " MEDIUM 6",
          "Valerio", //
          PLAY,
          "R R",
          PLAY, // in the second round the game should switch strategy
          "B B",
          PLAY,
          "R G",
          PLAY,
          "Y B");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "BLUE"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "GREEN"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "YELLOW"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "GREEN"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 4);
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task4 extends SysCliTest {

    public Task4() {
      super(Main.class);
    }

    @Test
    public void T4_01_the_First_two_rounds_should_be_random() throws Exception {
      Utils.randomAi = new Random(370001);
      runCommands(
          NEW_GAME + " HARD 6",
          "Valerio", //
          PLAY,
          "R G",
          PLAY,
          "Y Y");
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"), 1);

      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "BLUE"), 2);
    }

    @Test
    public void T4_02_three_rounds_change_strategy() throws Exception {
      Utils.randomAi = new Random(4098);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " HARD 6",
          "Valerio", //
          PLAY,
          "R Y",
          PLAY,
          "B B",
          PLAY, // in the third round the game should switch strategy to least used
          "G Y");
      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 3);
    }

    @Test
    public void T4_03_six_rounds_switch_strategy() throws Exception {
      Utils.randomAi = new Random(4098);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " HARD 6",
          "Valerio", //
          PLAY,
          "R Y",
          PLAY,
          "B B",
          PLAY, // in the third round the game should switch strategy to least
          "G Y", // AI wins so keeps the strategy
          PLAY,
          "Y Y", // AI wins so keeps the strategy
          PLAY,
          "B G", // AI lost so change strategy to AvoidLast
          PLAY,
          "R R");
      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 3);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 3);

      // Round 4
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "YELLOW"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 4);

      // Round 5
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 5);

      // Round 6
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"), 6);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 6);
      // 3 points because is a power colour
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 3), 6);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("RED"), 6);
    }

    @Test
    public void T4_04_six_rounds_switch_strategy() throws Exception {
      Utils.randomAi = new Random(849549584);
      Utils.randomPowerNumber = new Random(353537);
      runCommands(
          NEW_GAME + " HARD 6",
          "Valerio",
          PLAY,
          "B Y", // Round 1
          PLAY,
          "B B", // Round 2
          PLAY,
          "Y Y", // Round 3 - change to least used strategy
          PLAY,
          "G Y", // Round 4 - switch to Avoid last as AI did not get any points previoud round
          PLAY,
          "G G", // Round 5 - do not change stategy
          PLAY,
          "R R" // Round 6 - change again, bexause it lost
          );
      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("YELLOW"), 3);

      // Round 4
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "GREEN"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 4);

      // Round 5
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 5);

      // Round 6
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 6);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 6);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 6);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("GREEN"), 6);
    }

    @Test
    public void T4_05_eight_rounds_switch_strategy() throws Exception {
      Utils.randomAi = new Random(6644);
      Utils.randomPowerNumber = new Random(111);
      runCommands(
          NEW_GAME + " HARD 10",
          "Valerio",
          PLAY,
          "B B", // Round 1
          PLAY,
          "B B", // Round 2
          PLAY,
          "Y Y", // Round 3
          PLAY,
          "G G", // Round 4
          PLAY,
          "G R", // Round 5
          PLAY,
          "R B", // Round 6
          PLAY,
          "R B", // Round 7
          PLAY,
          "R B" // Round 8
          );
      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);

      // Round 3 - change from random to leastused strategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 3);

      // Round 4 - change strategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 4);

      // Round 5 - change strategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 5);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 5);

      // Round 6 - change strategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "YELLOW"), 6);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 6);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 6);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 6);

      // Round 7 - change strategy
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 7);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 7);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 7);

      // Round 8
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "YELLOW"), 8);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 8);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 8);
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task5 extends SysCliTest {

    public Task5() {
      super(Main.class);
    }

    @Test
    public void T5_01_game_not_started_show_stats() throws Exception {
      runCommands(SHOW_STATS);
      assertContains(GAME_NOT_STARTED.getMessage());
    }

    @Test
    public void T5_02_game_not_started_play() throws Exception {
      runCommands(PLAY);
      assertContains(GAME_NOT_STARTED.getMessage());
    }

    @Test
    public void T5_03_game_started_show_stats() throws Exception {
      runCommands(NEW_GAME + " HARD 10", "Valerio", SHOW_STATS);
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 0));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 0));
    }

    @Test
    public void T5_04_shows_stats_1_round() throws Exception {
      Utils.randomAi = new Random(3434343);
      runCommands(
          NEW_GAME + " EASY 2",
          "Valerio", //
          PLAY,
          "B Y",
          SHOW_STATS);
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 1));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 0));
    }

    @Test
    public void T5_05_shows_stats_3_rounds() throws Exception {
      Utils.randomAi = new Random(3434343);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 4",
          "Valerio",
          PLAY,
          "B Y", // Round 1
          PLAY,
          "B R", // Round 2
          PLAY,
          "Y G", // Round 3
          SHOW_STATS);

      // Check game started correctly
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);

      // Final scores
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 1));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 1));
    }

    @Test
    public void T5_06_end_game_3_rounds_tie() throws Exception {
      Utils.randomAi = new Random(3434343);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 3",
          "Valerio",
          PLAY,
          "B Y", // Round 1
          PLAY,
          "B R", // Round 2
          PLAY,
          "Y G" // Round 3
          );

      // Check game started correctly
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);

      // even if I don't run shows-stats the game terminates and shows the statistics
      // Final scores
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 1));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 1));
      // Game end and tie
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_TIE_GAME.getMessage());
    }

    @Test
    public void T5_07_game_should_not_end() throws Exception {
      Utils.randomAi = new Random(3434343);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 4",
          "Valerio",
          PLAY,
          "B Y", // Round 1
          PLAY,
          "B R", // Round 2
          PLAY,
          "Y G" // Round 3
          );

      // Check game started correctly
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "GREEN"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "BLUE"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);

      // game is not over and I did not run show-stats so no output should be given
      assertDoesNotContain(PRINT_PLAYER_POINTS.getMessage("Valerio", 1));
      assertDoesNotContain(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 1));
      assertDoesNotContain(PRINT_END_GAME.getMessage());
      assertDoesNotContain(PRINT_TIE_GAME.getMessage());
    }

    @Test
    public void T5_08_game_human_win() throws Exception {
      Utils.randomAi = new Random(94398473);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 4",
          "Valerio",
          PLAY,
          "B Y", // Round 1
          PLAY,
          "R R", // Round 2
          PLAY,
          "Y G", // Round 3
          PLAY,
          "B G" // Round 4
          );

      // Game starts
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "RED"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "YELLOW"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "GREEN"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);
      // Round 4
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 4);

      // Final scores and winner
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 1));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 0));
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_WINNER_GAME.getMessage("Valerio"));
    }

    @Test
    public void T5_09_game_ai_win() throws Exception {
      Utils.randomAi = new Random(4444);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 4",
          "Valerio",
          PLAY,
          "G Y", // Round 1
          PLAY,
          "G R", // Round 2
          PLAY,
          "Y R", // Round 3
          PLAY,
          "R G" // Round 4
          );

      // Game starts
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // Round 1
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "RED"), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 1);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 1);

      // Round 2
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "BLUE", "GREEN"), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 2);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1), 2);

      // Round 3
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "YELLOW", "RED"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 3);
      assertContainsAtRound(PRINT_POWER_COLOUR.getMessage("BLUE"), 3);

      // Round 4
      assertContainsAtRound(PRINT_INFO_MOVE.getMessage(AI_NAME, "RED", "GREEN"), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0), 4);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 0), 4);

      // Final scores and winner
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 0));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 1));
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_WINNER_GAME.getMessage(AI_NAME));
    }

    @Test
    public void T5_10_reset_state_new_game() throws Exception {
      Utils.randomAi = new Random(454545);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 2",
          "Valerio", //
          PLAY,
          "B Y",
          NEW_GAME + " EASY 3",
          "Valerio", //
          PLAY,
          "B G",
          PLAY,
          "R B",
          SHOW_STATS);
      // Ensure game state reset successfully
      assertDoesNotContain(GAME_NOT_STARTED.getMessage());

      // First game, round 1
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", 1));
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1));

      // Second game, round 2
      assertContains(PRINT_INFO_MOVE.getMessage(AI_NAME, "GREEN", "RED"));
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", 0));
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, 1));

      // Final stats for second game only
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", 0));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, 1));
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class YourTests extends SysCliTest {

    public YourTests() {
      super(Main.class);
    }

    @Test
    public void yourtest() throws Exception {}
  }
}
