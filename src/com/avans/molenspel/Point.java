package com.avans.molenspel;

public class Point {
    private Player owner;
    private Character position;

    public Point(Character  position) {
        this.position = position;
    }

    /* Returns true if the point is owned */
    public boolean isOwned() {
        return owner != null;
    }

    /* Returns true if the point is owned by a specific player */
    public boolean isOwnedByPlayer(Player player) {
        return this.isOwned() && this.owner.equals(player);
    }

    /* Returns the position of the point on the board */
    public char getPosition() {
        return this.position;
    }

    /* Claims the point as a player */
    public void claim(Player player) throws Exception {
        if(this.isOwned()) throw new Exception("Je kunt deze positie niet claimen!");
        this.owner = player;
    }

    /* Removes the claim of the point */
    public void abandon(Player player) throws Exception {
        if(!this.isOwnedByPlayer(player)) throw new Exception("Deze positie is niet van jou!");
        this.owner = null;
    }

    /* Slays the point as the other player */
    public void slay(Player player) throws Exception {
        if(this.isOwnedByPlayer(player)) throw new Exception("Je kunt je eigen tegel niet slaan!");
        this.owner = null;
    }

    /* Visualizes the point */
    public String visualize() {
        if (!this.isOwned())
            return ".";

        return (this.owner.getPlayerColor() == PlayerColor.BLUE ?  "B":  "R");
    }
}
