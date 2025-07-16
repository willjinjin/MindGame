package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class MediumAi implements AiStrategy {
  private Colour aiGuess;
  private Colour userLastColour;
  private int currentRound;
  private ColourPickStrategy pickStrategy;

  public MediumAi() {
    userLastColour = null;
  }

  @Override
  public Colour chooseColour() {
    return Colour.getRandomColourForAi();
  }

  @Override
  public Colour guessOpponentColour() {
    // Uses random strategy for first round, afterwards uses avoid last strategy
    if (currentRound > 1) {
      aiGuess = new AvoidLast(userLastColour).pickColour();
    } else {
      aiGuess = new RandomStrategy().pickColour();
    }
    return aiGuess;
  }

  public void setPickStrategy(ColourPickStrategy pickStrategy) {
    this.pickStrategy = pickStrategy;
  }

  public void setCurrentRound(int currentRound) {
    this.currentRound = currentRound;
  }

  public void setUserLastColour(Colour userLastColour) {
    this.userLastColour = userLastColour;
  }
}
