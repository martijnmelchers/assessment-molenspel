package com.avans.molenspel;

public class MoveAction {
    private Move move;
    private int weight;
    private boolean completesMill = false;

    public MoveAction(Move move, int weight) {
        this.move = move;
        this.weight = weight;
    }

    /* Returns the weight (or impact) of the move */
    public int getWeight() {
        return this.weight;
    }

    /* Returns the move as a array of characters */
    public char[] getMoveAsCharArray() {
        return new char[] { this.move.getOldPosition(), this.move.getNewPosition() };
    }

    /* Returns the move */
    public Move getMove() {
        return this.move;
    }

    /* Returns true if the move completes a mill */
    public boolean moveCompletesMill() {
        return this.completesMill;
    }

    /* Sets the flag that this move will complete a mill */
    public void setMoveCompletesMill() {
        this.completesMill = true;
    }
}
