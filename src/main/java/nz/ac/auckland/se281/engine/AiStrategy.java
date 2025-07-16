package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public interface AiStrategy {
  Colour chooseColour();

  Colour guessOpponentColour();
}
