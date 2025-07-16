package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.Main.Difficulty;

public interface AiStrategyFactory {
  AiStrategy createAiStrategy(Difficulty difficulty);
}
