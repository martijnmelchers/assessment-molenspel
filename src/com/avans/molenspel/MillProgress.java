package com.avans.molenspel;

import java.util.ArrayList;

public class MillProgress {
    private ArrayList<Point> piecesOwnedByPlayer = new ArrayList<>();
    private ArrayList<Point> piecesOwnedByOpponent = new ArrayList<>();
    private ArrayList<Point> piecesOwnedByBoard = new ArrayList<>();
    private ArrayList<Point> boardPosition;

    /* Creates a new mill progress */
    public MillProgress(ArrayList<Point> position) {
        this.boardPosition = position;
    }

    /* Adds a point to the points owned by the player */
    public void addPieceOwnedByPlayer(Point piece) {
        this.piecesOwnedByPlayer.add(piece);
    }

    /* Adds a point to the points owned by the opponent */
    public void addPieceOwnedByOpponent(Point piece) {
        this.piecesOwnedByOpponent.add(piece);
    }

    /* Adds a point to the points owned by the board */
    public void addPieceOwnedByBoard(Point piece) {
        this.piecesOwnedByBoard.add(piece);
    }

    /* Returns all pieces that are owned by the player */
    public ArrayList<Point> getPiecesOwnedByPlayer() {
        return this.piecesOwnedByPlayer;
    }

    /* Returns all pieces that are owned by the opponent */
    public ArrayList<Point> getPiecesOwnedByOpponent() {
        return this.piecesOwnedByOpponent;
    }

    /* Returns all pieces that are owned by the board */
    public ArrayList<Point> getPiecesOwnedByBoard() {
        return this.piecesOwnedByBoard;
    }

    /* Returns the points that the mill is on */
    public ArrayList<Point> getBoardPosition() {
        return this.boardPosition;
    }

}
