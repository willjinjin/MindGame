package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.model.Colour;

public class LeastUsed implements ColourPickStrategy {
  private int[] usage;

  public LeastUsed(int[] usage) {
    this.usage = usage;
  }

  @Override
  public Colour pickColour() {
    // Finds the least used colour by creating Colour array order, finding the minimum
    // usage and returning the corresponding colour
    // The order of colours is RED, GREEN, BLUE, YELLOW
    Colour[] order = {Colour.RED, Colour.GREEN, Colour.BLUE, Colour.YELLOW};
    int min = Integer.MAX_VALUE;
    Colour leastUsed = Colour.RED;
    for (int i = 0; i < order.length; i++) {
      if (usage[i] < min) {
        min = usage[i];
        leastUsed = order[i];
      }
    }
    return leastUsed;
  }
}
