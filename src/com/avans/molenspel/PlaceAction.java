package com.avans.molenspel;

public class PlaceAction {
    private char point;
    private int weight;


    public PlaceAction(char point, int weight) {
        this.point = point;
        this.weight = weight;
    }

    /* Returns the weight (or impact) of the place action */
    public int getWeight() {
        return this.weight;
    }

    /* Returns the point where the piece will be places */
    public char getPoint() {
        return this.point;
    }
}
