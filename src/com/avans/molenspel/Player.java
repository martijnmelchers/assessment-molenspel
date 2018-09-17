package com.avans.molenspel;

import java.util.Scanner;

public class Player {
    private PlayerColor playerColor;
    /* Stores the name of the player */
    private String name;
    /* Stores how many pieces the player has placed and their player number */
    private int piecesPlayed = 0, playerNumber;
    /* Stores the opponent of the player */
    protected Player opponent;

    /* Called when a new player is created */
    public Player(String name, int playerNumber) {
        /* Set the player name */
        this.name = name;
        /* The constructor gives an index and we increment it by one */
        this.playerNumber = playerNumber + 1;
    }

    /* Returns true when the player has placed all it's pieces */
    public boolean placedAllPieces() {
        return this.piecesPlayed == 9;
    }

    /* Increases the amount of pieces the player has placed */
    public void piecePlaced() {
        this.piecesPlayed++;
    }

    /* Ask the player where they'd like to place a piece, throws an exception when an invalid position is given */
    public char askForPiecePlacement() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print(String.format("%s, (%s) kies een positie waar je een pion wil neerzetten: ", this.name, getPlayerColorText()));
        String input = scanner.nextLine().toUpperCase();
        if (input.length() != 1)
            throw new Exception("Ongeldige positie ingevuld!");
        return input.charAt(0);
    }

    /* Ask the player which move they'd like to make, throws an exception when an invalid position is given */
    public char[] askForMove() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print(String.format("%s, (%s) kies een positie waar de een pion naartoe wilt verplaatsen (bijv. AB): ", this.name, getPlayerColorText()));
        String input = scanner.nextLine().toUpperCase();
        if (input.length() != 2)
            throw new Exception("Ongeldige positie ingevuld!");
        return new char[]{input.charAt(0), input.charAt(1)};
    }

    /* Ask the player which piece they want to slay */
    public char askForSlay() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print(String.format("%s, (%s) selecteer een pion van de tegenstander die je wilt uitschakelen: ", this.name, getPlayerColorText()));
        String input = scanner.nextLine().toUpperCase();
        if (input.length() != 1)
            throw new Exception("Ongeldige positie ingevuld!");
        return input.charAt(0);
    }

    /* Returns false because player isnt a computer, is overwritten in computer class */
    public boolean isComputer() {
        return false;
    }

    /* Returns the name of the player */
    public String getName() {
        return this.name;
    }

    /* Sets the color of the player */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /* Returns the color of the player */
    public PlayerColor getPlayerColor() {
        return this.playerColor;
    }

    /* Generates text in the color of the player */
    public String getTextInPlayerColor(String text) {
        return (this.getPlayerColor() == PlayerColor.BLUE ? "(Blauw) ": "(Rood) ") + text;

    }

    /* Returns the color of the player (as text) in the color of the player */
    private String getPlayerColorText() {
        return this.getPlayerColor() == PlayerColor.BLUE ? "Blauw" : "Rood";
    }

    /* The player does not require this function but the computer does, but to call it on a computer it has to be implemented in a player */
    public void setBoard(Board board) {
        throw new UnsupportedOperationException("Een speler heeft geen bord nodig!");
    }

    /* Sets the opponent of the player */
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    /* Returns the opponent of the player */
    public Player getOpponent() {
        return this.opponent;
    }

    /* Returns the player number */
    public int getPlayerNumber() {
        return this.playerNumber;
    }

    /* Returns the player statistics, is used in the computer */
    public void gameEndedStatistics() {
        throw new UnsupportedOperationException("Er zijn geen statistieken voor een speler!");
    }
}
