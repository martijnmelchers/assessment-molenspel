package com.avans.molenspel;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private HashMap<Character, Point> points = new HashMap<>();

    public Board() {
        this.initiateBoard();
    }

    /* Create all the points on the board */
    private void initiateBoard() {
        for (char character = 'A'; character <= 'X'; character++) {
            points.put(character, new Point(character));
        }
    }

    /* Draw the board */
    public void draw() {
        for (String boardRow : BoardGeometry.LAYOUT) {
            for (char c : boardRow.toCharArray()) {
                if (Character.isLetter(c))
                    System.out.print(getVisualPoint(c));
                else
                    System.out.print(c);

            }

            System.out.print("        ");
            System.out.println(boardRow);
        }
    }

    /* Visualize the point */
    private String getVisualPoint(Character character) {
        return points.get(character).visualize();
    }

    /* Returns true if the passed character does not exist on the board */
    private boolean pointDoesNotExist(char pos) {
        return !points.containsKey(pos);
    }

    /* Returns true if the point is already owned */
    private boolean cannotPlacePiece(char pos) {
        return this.points.get(pos).isOwned();
    }

    /* Returns true if the point is owned by a specific player */
    private boolean ownedByPlayer(char pos, Player player) {
        return this.points.get(pos).isOwnedByPlayer(player);
    }

    /* Returns all points that are owned by a player */
    public ArrayList<Point> getPointsByPlayer(Player player) {
        ArrayList<Point> points = new ArrayList<>();

        for (Point point : this.points.values()) {
            if (point.isOwnedByPlayer(player))
                points.add(point);
        }

        return points;
    }

    /* Returns true if the move passed is valid */
    public boolean validMove(char oldPos, char newPos) {
        return BoardGeometry.areConnected(oldPos, newPos);
    }

    /* Places a piece on a board, returns true if a mill is made */
    public boolean placePiece(char pos, Player player) throws Exception {
        if (this.pointDoesNotExist(pos)) throw new Exception("Deze positie bestaat niet!");
        if (this.cannotPlacePiece(pos)) throw new Exception("Je kunt hier geen pion plaatsen");
        if (player.placedAllPieces()) throw new Exception("Je kunt geen pionnen meer plaatsen!");
        this.points.get(pos).claim(player);
        player.piecePlaced();
        return isPartOfMill(pos, player);
    }

    /* Moves a piece on the board, returns true when a mill is made */
    public boolean movePiece(char oldPos, char newPos, Player player) throws Exception {
        if (this.pointDoesNotExist(oldPos) || this.pointDoesNotExist(newPos))
            throw new Exception("Deze positie bestaat niet!");
        if (!this.ownedByPlayer(oldPos, player)) throw new Exception("Deze pion is niet van jou!");
        if (this.cannotPlacePiece(newPos)) throw new Exception("Je kunt hier geen pion plaatsen");
        if (getPointsByPlayer(player).size() == 3 && player.placedAllPieces()) {
            this.points.get(oldPos).abandon(player);
            this.points.get(newPos).claim(player);
        } else if (!this.validMove(oldPos, newPos)) {
            throw new Exception("Je kunt hier niet naartoe verplaatsen!");
        } else {
            this.points.get(oldPos).abandon(player);
            this.points.get(newPos).claim(player);
        }

        return isPartOfMill(newPos, player);
    }

    /* Slay a specific piece throws an exception when the piece cannot be slain */
    public void slayPiece(char pos, Player player) throws Exception {
        if (this.ownedByPlayer(pos, player)) throw new Exception("Je kunt je eigen pionnen niet slaan!");
        if (!this.ownedByPlayer(pos, player.getOpponent())) throw new Exception("Er staat hier geen pion!");
        boolean canSlayMills = true;
        for (Point point : getPointsByPlayer(player.getOpponent())) {
            if (!isPartOfMill(point.getPosition(), player)) canSlayMills = false;
        }

        if (canSlayMills)
            this.points.get(pos).slay(player);
        else if (!isPartOfMill(pos, player.getOpponent()))
            this.points.get(pos).slay(player);
        else
            throw new Exception("Je kunt een molen niet slaan er zijn nog niet-molen pionnen!");
    }

    /* Returns whether or not the piece is part of a complete mill */
    private boolean isPartOfMill(char pos, Player player) {
        for (String s : BoardGeometry.MILLS) {
            if (s.indexOf(pos) >= 0) {
                int matches = 0;
                for (char c : s.toCharArray()) {
                    if (this.ownedByPlayer(c, player))
                        matches++;
                }

                /* Return true when there are 3 matches (aka a mill) */
                if (matches == 3)
                    return true;
            }
        }
        return false;
    }

    /* Returns a MillProgress where the computer can find how many pieces it needs before it can make a mill */
    public ArrayList<MillProgress> piecesUntilMill(char pos, Player player) {
        ArrayList<MillProgress> matches = new ArrayList<>();

        for (String s : BoardGeometry.MILLS) {
            if (s.indexOf(pos) >= 0) {
                MillProgress progress = new MillProgress(this.getPointsFromCharArray(s.toCharArray()));
                for (Point point : progress.getBoardPosition()) {
                    if (this.ownedByPlayer(point.getPosition(), player))
                        progress.addPieceOwnedByPlayer(point);
                    else if (this.ownedByPlayer(point.getPosition(), player.getOpponent()))
                        progress.addPieceOwnedByOpponent(point);
                    else
                        progress.addPieceOwnedByBoard(point);
                }
                matches.add(progress);
            }
        }

        return matches;
    }

    /* Get all points that are not owned by a player */
    public ArrayList<Point> getAvailablePoints() {
        ArrayList<Point> points = new ArrayList<>();
        for (Point point : this.points.values())
            if (!point.isOwned()) points.add(point);
        return points;
    }

    /* Get a point from a character */
    public Point getPointFromChar(char pos) {
        return this.points.get(pos);
    }

    /* Get a point from a char array */
    private ArrayList<Point> getPointsFromCharArray(char[] positions) {
        ArrayList<Point> points = new ArrayList<>();
        for (char c : positions) points.add(this.points.get(c));
        return points;
    }

    /* Get all available moves for a certain point */
    public ArrayList<Move> getAvailableMoves(char pos, Player player) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        /* Return every point on the board if the player only has 3 pieces remaining */
        if (this.getPointsByPlayer(player).size() == 3) {
            for (Point point : this.getAvailablePoints()) {
                possibleMoves.add(new Move(pos, point.getPosition()));
            }
        } else {
            for (String s : BoardGeometry.CONNECTIONS) {
                if (s.indexOf(pos) >= 0) {
                    for (char c : s.toCharArray()) {
                        boolean contains = false;
                        /* Loop through all existing moves and continue if the move has already been added */
                        for (Move moves : possibleMoves)
                            if (moves.getNewPosition() == c) contains = true;

                        if (contains) continue;

                        if (!this.points.get(c).isOwned()) possibleMoves.add(new Move(pos, c));
                    }
                }
            }
        }
        return possibleMoves;
    }

    /* Get all available moves for a point, but with added simulation moves. These moves make it possible for a simulation to simulate fake moves */
    public ArrayList<Move> getAvailableMoves(char pos, Player player, ArrayList<Move> simulatedMoves) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        if (this.getPointsByPlayer(player).size() == 3) {
            for (Point point : this.getAvailablePoints()) {
                possibleMoves.add(new Move(pos, point.getPosition()));
            }
        } else {
            for (String s : BoardGeometry.CONNECTIONS) {
                if (s.indexOf(pos) >= 0) {
                    for (char c : s.toCharArray()) {
                        /* Skip iteration if the move is to the position where the piece is already at */
                        if (c == pos) continue;

                        boolean contains = false;
                        /* Loop through all existing moves and continue if the move has already been added */
                        for (Move moves : possibleMoves)
                            if (moves.getNewPosition() == c) contains = true;

                        if (contains) continue;

                        /* If there are simulated moves iterate through them to add the move the old position of the simulated move */
                        if (simulatedMoves != null) {
                            for (Move simulatedMove : simulatedMoves) {
                                if (simulatedMove.getOldPosition() == c)
                                    possibleMoves.add(new Move(pos, c));
                            }
                        }
                        if (!this.points.get(c).isOwned()) possibleMoves.add(new Move(pos, c));
                    }
                }
            }
        }

        return possibleMoves;
    }
}
