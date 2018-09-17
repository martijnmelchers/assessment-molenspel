package com.avans.molenspel;

public class SlayAction {

    private char point;
    private int weight;


    SlayAction(char point, int weight) {
        this.point = point;
        this.weight = weight;
    }

    /* Returns the weight (or impact) of the slay action */
    public int getWeight() {
        return this.weight;
    }

    /* Returns the point it wants to slay */
    char getPoint() {
        return this.point;
    }


}
