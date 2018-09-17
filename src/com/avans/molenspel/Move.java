package com.avans.molenspel;

public class Move {
    private char oldPosition, newPosition;

    public Move(char oldPosition, char newPosition) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    /* Returns the old position of the move */
    public char getOldPosition() {
        return this.oldPosition;
    }

    /* Returns the new position of the move */
    public char getNewPosition() {
        return this.newPosition;
    }
}
