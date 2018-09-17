package com.avans.molenspel;

import java.util.ArrayList;

public class Simulation implements Cloneable {
    private SimulationState state;
    private ArrayList<Move> moves = new ArrayList<>();

    public Simulation() {
        this.state = SimulationState.RUNNING;
    }

    /* Copy constructor */
    public Simulation(Simulation simulation) {
        this.state = simulation.getState();
        this.moves = simulation.getMoves();
    }

    /* Adds a move if the simulation is still running */
    public void addMove(Move move) {
        if (state == SimulationState.RUNNING)
            this.moves.add(move);
    }

    /* Returns all the moves in the simulation */
    public ArrayList<Move> getMoves() {
        return this.moves;
    }

    /* Returns the amount of steps the simulation needs to get to its destination */
    public int totalSteps() {
        return this.moves.size();
    }

    /* Sets the state of the simulation */
    public void setState(SimulationState state) {
        this.state = state;
    }

    /* Returns the state of the simulation */
    public SimulationState getState() {
        return this.state;
    }

    /* Returns true if a specific move has been made */
    public boolean moveHasBeenMade(Move move) {
        boolean matchFound = false;
        for (Move m : moves) {
            if (m.getNewPosition() == move.getNewPosition() || m.getOldPosition() == move.getNewPosition())
                matchFound = true;
        }

        return matchFound;
    }

    /* Returns true if there has been a last move */
    public boolean hasLastMove() {
        return this.moves.size() > 0;
    }

    /* Returns the last move in the simulation */
    public Move lastMove() {
        if (this.moves.size() > 0)
            return this.moves.get(this.moves.size() - 1);

        return null;
    }
}
