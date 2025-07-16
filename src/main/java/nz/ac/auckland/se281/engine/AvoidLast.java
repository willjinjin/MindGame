package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class AvoidLast implements ColourPickStrategy {
  private Colour userLastColour;

  public AvoidLast(Colour userLastColour) {
    this.userLastColour = userLastColour;
  }

  @Override
  public Colour pickColour() {
    return Colour.getRandomColourExcluding(userLastColour);
  }
}
