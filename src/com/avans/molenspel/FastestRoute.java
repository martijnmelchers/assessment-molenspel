package com.avans.molenspel;

import java.util.ArrayList;

public class FastestRoute {
    private ArrayList<Move> moves;

    /* Initiates a route with moves */
    public FastestRoute(ArrayList<Move> moves) {
        this.moves = moves;
    }

    /* Add a step to the route */
    public void addStep(Move move) {
        moves.add(0, move);
    }

    /* Returns the first move */
    public Move getFirstMove() {
        return this.moves.get(0);
    }

    /* Returns the total steps to reach goal */
    public int totalSteps() {
        return moves.size();
    }
}
