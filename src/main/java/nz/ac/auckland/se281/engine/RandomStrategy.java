package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class RandomStrategy implements ColourPickStrategy {
  @Override
  public Colour pickColour() {
    return Colour.getRandomColourForAi();
  }
}
