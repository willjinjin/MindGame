package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.Main.Difficulty;
import nz.ac.auckland.se281.cli.MessageCli;
import nz.ac.auckland.se281.cli.Utils;
import nz.ac.auckland.se281.model.Colour;

public class Game {
  private static final String AI_NAME = "HAL-9000";
  private int numRounds;
  private int currentRound;
  private int userScore;
  private int aiScore;
  private String namePlayer;
  private AiStrategyFactory aiStrategyFactory;
  private AiStrategy aiStrategy;
  private Colour playerColour;
  private Colour playerGuess;
  private Colour aiColour;
  private Colour aiGuess;
  private Colour powerColour;
  private int red = 0;
  private int green = 0;
  private int blue = 0;
  private int yellow = 0;

  public Game() {
    aiStrategyFactory = new ConcreteAi();
  }

  // Creates a new game with the specified difficulty, number of rounds, and player name.
  // If the player name is not provided, it will be prompted from the user.
  public void newGame(Difficulty difficulty, int numRounds, String[] options) {
    // Resetting scores and rounds
    aiScore = 0;
    userScore = 0;
    this.numRounds = numRounds;
    this.currentRound = 1;
    this.namePlayer = (options.length == 1) ? options[0] : Utils.getInput();
    aiStrategy = aiStrategyFactory.createAiStrategy(difficulty);
    // Prints welcome message
    MessageCli.WELCOME_PLAYER.printMessage(namePlayer);
  }

  // Starts the game and plays a round.
  public void play() {
    if (aiStrategy == null) {
      MessageCli.GAME_NOT_STARTED.printMessage();
      return;
    }

    startRound();
    handleAiMove();
    handlePlayerMove();
    determineRoundWinner();
  }

  // Starts a new round of the game.
  // It resets the power colour and prints the round information.
  // If the round is a multiple of 3, it generates a new power colour.
  private void startRound() {
    powerColour = null;
    MessageCli.START_ROUND.printMessage(currentRound, numRounds);
    if (currentRound % 3 == 0) {
      powerColour = Colour.getRandomColourForPowerColour();
      MessageCli.PRINT_POWER_COLOUR.printMessage(powerColour);
    }

    // If Ai Strategy is medium, set the current round and user last colour
    // If Ai Strategy is hard, set the current round, user last colour and colour usage
    if (aiStrategy instanceof MediumAi) {
      ((MediumAi) aiStrategy).setCurrentRound(currentRound);
      ((MediumAi) aiStrategy).setUserLastColour(playerColour);
    }

    if (aiStrategy instanceof HardAi) {
      ((HardAi) aiStrategy).setCurrentRound(currentRound);
      ((HardAi) aiStrategy).setUserLastColour(playerColour);
      ((HardAi) aiStrategy).setColourUsage(new int[] {red, green, blue, yellow});
    }

    currentRound++;
  }

  private void handlePlayerMove() {
    // Ask the player for input until a valid input is given
    // The input should be two colours separated by a space
    // The first colour is the player's colour and the second colour is the guess
    while (true) {
      MessageCli.ASK_HUMAN_INPUT.printMessage();
      String input = Utils.getInput();
      String[] colours = parseInput(input);

      // If the input is valid, set the player's colour and guess
      // and break the loop
      if (colours != null && isValidColours(colours)) {
        MessageCli.PRINT_INFO_MOVE.printMessage(
            namePlayer, Colour.fromInput(colours[0]), Colour.fromInput(colours[1]));
        playerColour = Colour.fromInput(colours[0]);
        playerGuess = Colour.fromInput(colours[1]);
        break;
      }

      MessageCli.INVALID_HUMAN_INPUT.printMessage();
    }
  }

  private void handleAiMove() {
    // Sets the AI's colour and guess
    this.aiColour = aiStrategy.chooseColour();
    this.aiGuess = aiStrategy.guessOpponentColour();
    MessageCli.PRINT_INFO_MOVE.printMessage(AI_NAME, aiColour, aiGuess);
  }

  private void determineRoundWinner() {
    // Checks if the player and AI guessed the correct colours
    boolean playerCorrect = playerGuess.equals(aiColour);
    boolean aiCorrect = aiGuess.equals(playerColour);
    int win = 1;
    int lose = 0;

    // If player is correct, increase their score by 1
    // If player also guessed the power colour, increase their score by 3
    if (playerCorrect) {
      if (playerGuess.equals(powerColour)) {
        userScore += 3;
        win = 3;
      } else {
        userScore++;
      }
    }

    // If AI is correct, increase their score by 1
    // If AI also guessed the power colour, increase their score by 3
    if (aiCorrect) {
      if (aiGuess.equals(powerColour)) {
        aiScore += 3;
        win = 3;
      } else {
        aiScore++;
      }
    }

    // If the AI is hard and the current round is greater than 3,
    // check if least used strategy is better for next round
    if (aiStrategy instanceof HardAi && currentRound > 3) {
      String strategyUsed = ((HardAi) aiStrategy).getLastStrategyUsed();
      boolean leastUsedWins =
          (strategyUsed.equals("LeastUsed") && aiCorrect)
              || (strategyUsed.equals("AvoidLast") && !aiCorrect);
      ((HardAi) aiStrategy).setWinner(leastUsedWins);
    }

    // Print the outcome of the round
    if (playerCorrect && aiCorrect) {
      printRoundOutcome(win, win);
    } else if (playerCorrect) {
      printRoundOutcome(win, lose);
    } else if (aiCorrect) {
      printRoundOutcome(lose, win);
    } else {
      printRoundOutcome(lose, lose);
    }

    // Increment the colour usage for the player
    switch (playerColour) {
      case RED:
        red++;
        return;
      case GREEN:
        green++;
        return;
      case BLUE:
        blue++;
        return;
      case YELLOW:
        yellow++;
    }
  }

  private void printRoundOutcome(int playerOutcome, int aiOutcome) {
    // Print the outcome of the round
    MessageCli.PRINT_OUTCOME_ROUND.printMessage(namePlayer, playerOutcome);
    MessageCli.PRINT_OUTCOME_ROUND.printMessage(AI_NAME, aiOutcome);

    // Check if the game is over
    // If the game is over, print the stats and the winner
    if (isGameOver()) {
      showStats();
      MessageCli.PRINT_END_GAME.printMessage();
      if (userScore > aiScore) {
        MessageCli.PRINT_WINNER_GAME.printMessage(namePlayer);
      } else if (aiScore > userScore) {
        MessageCli.PRINT_WINNER_GAME.printMessage(AI_NAME);
      } else {
        MessageCli.PRINT_TIE_GAME.printMessage();
      }
    }
  }

  // Checks if the game is over
  // The game is over if the current round is greater than the number of rounds
  private boolean isGameOver() {
    return currentRound > numRounds;
  }

  private String[] parseInput(String input) {
    if (input == null || input.trim().isEmpty()) {
      return null;
    }
    String[] inputs = input.split(" ");
    return (inputs.length == 2) ? inputs : null;
  }

  private boolean isValidColours(String[] colours) {
    return Colour.fromInput(colours[0]) != null && Colour.fromInput(colours[1]) != null;
  }

  public void showStats() {
    // Print the stats of the game
    // If the game is not started, print a special message
    if (aiStrategy == null) {
      MessageCli.GAME_NOT_STARTED.printMessage();
      return;
    }

    MessageCli.PRINT_PLAYER_POINTS.printMessage(namePlayer, userScore);
    MessageCli.PRINT_PLAYER_POINTS.printMessage(AI_NAME, aiScore);
  }
}
