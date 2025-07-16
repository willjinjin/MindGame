package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class HardAi implements AiStrategy {
  private Colour aiGuess;
  private Colour userLastColour;
  private int currentRound;
  private boolean leastUsedWins = true;
  private ColourPickStrategy pickStrategy;
  private int[] usage;
  private String lastStrategyUsed = null;

  public HardAi() {
    userLastColour = null;
  }

  @Override
  public Colour chooseColour() {
    return Colour.getRandomColourForAi();
  }

  @Override
  public Colour guessOpponentColour() {
    // Uses random strategy for the first two rounds
    if (currentRound < 3) {
      pickStrategy = new RandomStrategy();
      aiGuess = pickStrategy.pickColour();
      lastStrategyUsed = "Random";
    } else {
      // Starts with least used strategy, if it wins, it continues with it
      // Otherwise, it switches to avoid last strategy
      // If avoid last strategy wins, it continues with it
      // Otherwise, it switches back to least used strategy
      if (leastUsedWins) {
        pickStrategy = new LeastUsed(usage);
        aiGuess = pickStrategy.pickColour();
        lastStrategyUsed = "LeastUsed";
      } else {
        pickStrategy = new AvoidLast(userLastColour);
        aiGuess = pickStrategy.pickColour();
        lastStrategyUsed = "AvoidLast";
      }
    }
    return aiGuess;
  }

  public String getLastStrategyUsed() {
    return lastStrategyUsed;
  }

  public void setWinner(boolean leastUsedWins) {
    this.leastUsedWins = leastUsedWins;
  }

  public void setPickStrategy(ColourPickStrategy pickStrategy) {
    this.pickStrategy = pickStrategy;
  }

  public void setColourUsage(int[] usage) {
    this.usage = usage;
  }

  public void setCurrentRound(int currentRound) {
    this.currentRound = currentRound;
  }

  public void setUserLastColour(Colour userLastColour) {
    this.userLastColour = userLastColour;
  }
}
