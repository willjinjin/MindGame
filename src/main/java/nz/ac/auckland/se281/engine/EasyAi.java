package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class EasyAi implements AiStrategy {
  private Colour aiGuess;
  private ColourPickStrategy pickStrategy;


  @Override
  public Colour chooseColour() {
    return Colour.getRandomColourForAi();
  }

  @Override
  public Colour guessOpponentColour() {
    pickStrategy = new RandomStrategy();
    aiGuess = pickStrategy.pickColour();
    return aiGuess;
  }
}
