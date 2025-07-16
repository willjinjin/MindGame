package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.Main.Difficulty;

public class ConcreteAi implements AiStrategyFactory {
  @Override
  public AiStrategy createAiStrategy(Difficulty difficulty) {
    // Factory design method to create AI strategy based on difficulty level
    switch (difficulty) {
      case EASY:
        return new EasyAi();
      case MEDIUM:
        return new MediumAi();
      case HARD:
        return new HardAi();
      default:
        throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
    }
  }
}
