package com.avans.molenspel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Computer extends Player {
    private Board board;
    /* Previous move is initiated so we don't have to null check it */
    private Move previousMove = new Move('.', '.');
    /* Save the simulation execution count for after game statistics */
    private int simulationsExecuted = 0, highestSimulationsExecuted = 0, lowestSimulationsExecuted = 0, totalSimulationsExecuted = 0;

    Computer(int playerIndex) {
        super(null, playerIndex);
    }

    /* Set the board object so the computer can use it to ask questions to */
    @Override
    public void setBoard(Board board) {
        this.board = board;
    }

    /* In this function the computer calculates all the possible moves and returns the move valuable one (or selects one from the best) */
    @Override
    public char askForPiecePlacement() {
        /* Initiate a new array with placement actions */
        ArrayList<PlaceAction> guesses = new ArrayList<>();
        /* Get all available points on the board */
        ArrayList<Point> availablePoints = this.board.getAvailablePoints();
        /* Get all points that are currently owned by the player */
        ArrayList<Point> points = this.board.getPointsByPlayer(this);
        /* Get all points that are currently owned by the opponent */
        ArrayList<Point> opponentPoints = this.board.getPointsByPlayer(this.opponent);

        /* Loop through each point owned by the player and try to make a mill */
        for (Point point : points) {
            for (MillProgress progress : this.board.piecesUntilMill(point.getPosition(), this)) {
                int piecesOwnedByPlayer = progress.getPiecesOwnedByPlayer().size();
                int piecesOwnedByBoard = progress.getPiecesOwnedByBoard().size();

                if (piecesOwnedByPlayer == 2 && piecesOwnedByBoard == 1)
                    guesses.add(new PlaceAction(this.randomItem(progress.getPiecesOwnedByBoard()).getPosition(), 4));


                if (piecesOwnedByPlayer == 1 && piecesOwnedByBoard > 1)
                    guesses.add(new PlaceAction(this.randomItem(progress.getPiecesOwnedByBoard()).getPosition(), 2));


                if (piecesOwnedByPlayer == 1 && piecesOwnedByBoard > 0)
                    guesses.add(new PlaceAction(this.randomItem(progress.getPiecesOwnedByBoard()).getPosition(), 1));

            }
        }

        /* Try to block the opponent from making mills */
        for (Point point : opponentPoints) {
            for (MillProgress progress : this.board.piecesUntilMill(point.getPosition(), this.opponent)) {
                int piecesOwnedByPlayer = progress.getPiecesOwnedByPlayer().size();
                int piecesOwnedByBoard = progress.getPiecesOwnedByBoard().size();

                if (piecesOwnedByPlayer == 2 && piecesOwnedByBoard == 1)
                    guesses.add(new PlaceAction(this.randomItem(progress.getPiecesOwnedByBoard()).getPosition(), 3));

                if (piecesOwnedByPlayer == 1 && piecesOwnedByBoard > 0)
                    guesses.add(new PlaceAction(this.randomItem(progress.getPiecesOwnedByBoard()).getPosition(), 1));
            }
        }

        guesses.add(new PlaceAction(this.randomItem(availablePoints).getPosition(), 1));

        guesses.sort(Comparator.comparingInt(PlaceAction::getWeight));
        Collections.reverse(guesses);

        return guesses.get(0).getPoint();
    }

    @Override
    public char[] askForMove() {
        this.simulationsExecuted = 0;
        ArrayList<MoveAction> guesses = new ArrayList<>();
        ArrayList<Point> points = this.board.getPointsByPlayer(this);
        ArrayList<Point> opponentPoints = this.board.getPointsByPlayer(super.opponent);

        if (!this.opponent.isComputer())
            System.out.println(String.format("%s bedenkt de beste zet...", this.getTextInPlayerColor(this.getName())));

        /* Loop through each point owned by player */
        for (Point point : points) {
            /* Get available move for the point and add them with the lowest weight to guesses */
            for (Move move : this.board.getAvailableMoves(point.getPosition(), this))
                guesses.add(new MoveAction(move, 1));

            /* Foreach mill the point is part of */
            for (MillProgress progress : this.board.piecesUntilMill(point.getPosition(), this)) {
                int piecesOwnedByPlayer = progress.getPiecesOwnedByPlayer().size();
                int piecesOwnedByBoard = progress.getPiecesOwnedByBoard().size();

                /* If the piece is already a mill move it so it can because a mill again next move */
                if (piecesOwnedByPlayer == 3) {
                    ArrayList<Move> moves = this.board.getAvailableMoves(point.getPosition(), this);
                    if (moves.size() > 0)
                        guesses.add(new MoveAction(moves.get(0), 10));

                }

                /* If there are two pieces owned by the player */
                if (piecesOwnedByPlayer == 2 && piecesOwnedByBoard == 1) {
                    /* Find the fastest route to the mill for each point in the board */
                    for (Point piece : points) {
                        for (FastestRoute route : this.getFastestRouteToMill(piece.getPosition(), this, progress)) {
                            switch (route.totalSteps()) {
                                /* If there are no steps in the route do nothing */
                                case 0:
                                    break;
                                case 1:
                                    if (this.firstStepCompletesMill(route, progress)) {
                                        MoveAction action = new MoveAction(route.getFirstMove(), 11);
                                        action.setMoveCompletesMill();
                                        guesses.add(action);
                                    } else {
                                        MoveAction action = new MoveAction(route.getFirstMove(), 10);
                                        guesses.add(action);
                                    }
                                    break;
                                case 2:
                                    guesses.add(new MoveAction(route.getFirstMove(), 9));
                                    break;
                                case 3:
                                    guesses.add(new MoveAction(route.getFirstMove(), 8));
                                    break;
                                case 4:
                                    guesses.add(new MoveAction(route.getFirstMove(), 7));
                                    break;
                                default:
                                    guesses.add(new MoveAction(route.getFirstMove(), 3));
                                    break;
                            }
                        }
                    }
                }

                /* If there is one piece owned by the player */
                if (piecesOwnedByPlayer == 1 && piecesOwnedByBoard > 1) {
                    /* Find the fastest route to the mill for each point in the board */
                    for (Point piece : points) {
                        for (FastestRoute route : this.getFastestRouteToMill(piece.getPosition(), this, progress)) {
                            switch (route.totalSteps()) {
                                /* If there are no steps in the route do nothing */
                                case 0:
                                    break;
                                case 1:
                                    guesses.add(new MoveAction(route.getFirstMove(), 8));
                                case 2:
                                    guesses.add(new MoveAction(route.getFirstMove(), 7));
                                case 3:
                                    guesses.add(new MoveAction(route.getFirstMove(), 6));
                                case 4:
                                    guesses.add(new MoveAction(route.getFirstMove(), 5));
                                default:
                                    guesses.add(new MoveAction(route.getFirstMove(), 2));
                            }
                        }
                    }
                }

            }
        }

        for (Point point : opponentPoints) {
            for (MillProgress progress : this.board.piecesUntilMill(point.getPosition(), this)) {
                int piecesOwnedByPlayer = progress.getPiecesOwnedByPlayer().size();
                int piecesOwnedByBoard = progress.getPiecesOwnedByBoard().size();

                if (piecesOwnedByPlayer == 2 && piecesOwnedByBoard == 1) {
                    for (@SuppressWarnings("unused") Point ignored : progress.getPiecesOwnedByBoard()) {
                        for (Point piece : points) {
                            for (FastestRoute route : this.getFastestRouteToMill(piece.getPosition(), this, progress)) {
                                switch (route.totalSteps()) {
                                    /* If there are no steps in the route do nothing */
                                    case 0:
                                        break;
                                    case 1:
                                        guesses.add(new MoveAction(route.getFirstMove(), 9));
                                    case 2:
                                        guesses.add(new MoveAction(route.getFirstMove(), 9));
                                    case 3:
                                        guesses.add(new MoveAction(route.getFirstMove(), 4));
                                    case 4:
                                        guesses.add(new MoveAction(route.getFirstMove(), 3));
                                    default:
                                        guesses.add(new MoveAction(route.getFirstMove(), 2));

                                }
                            }
                        }
                    }
                }
            }
        }

        int highestWeight = 0;
        ArrayList<MoveAction> bestGuesses = new ArrayList<>();

        /* Loop through each guess and find the best */
        for (MoveAction action : guesses) {
            /* But only if the move is not a duplicate it can count */
            if (previousMove.getOldPosition() != action.getMove().getNewPosition() && previousMove.getNewPosition() != action.getMove().getOldPosition() || action.moveCompletesMill()) {
                if (action.getWeight() > highestWeight) {
                    highestWeight = action.getWeight();
                    bestGuesses.clear();
                    bestGuesses.add(action);
                } else if (action.getWeight() == highestWeight) {
                    bestGuesses.add(action);
                }
            }
        }

        /* Select a random move and store the move it will do so it cannot go back next move (except */
        MoveAction chosenMove = this.randomItem(bestGuesses);
        this.previousMove = chosenMove.getMove();

        /* Increase the total simulations */
        if (this.lowestSimulationsExecuted > this.simulationsExecuted || this.lowestSimulationsExecuted == 0)
            this.lowestSimulationsExecuted = this.simulationsExecuted;

        if (this.simulationsExecuted > this.highestSimulationsExecuted)
            this.highestSimulationsExecuted = this.simulationsExecuted;

        this.totalSimulationsExecuted += this.simulationsExecuted;

        if (!this.opponent.isComputer())
            System.out.println(String.format("%s heeft in totaal %s simulaties uitgevoerd, daarvan zijn er %s van gelukt en nu nog %s van over", this.getTextInPlayerColor(this.getName()), this.simulationsExecuted, guesses.size(), bestGuesses.size()));

        return chosenMove.getMoveAsCharArray();
    }

    /* Ask the computer for a piece that it wants to slay */
    @Override
    public char askForSlay() {
        ArrayList<SlayAction> bestGuesses = new ArrayList<>();
        ArrayList<Point> points = this.board.getPointsByPlayer(this.opponent);

        /* Loop through each point that is owned by the player */
        for (Point point : points) {
            for (MillProgress progress : this.board.piecesUntilMill(point.getPosition(), this.opponent)) {
                int piecesOwnedByPlayer = progress.getPiecesOwnedByPlayer().size();
                int piecesOwnedByBoard = progress.getPiecesOwnedByBoard().size();
                int piecesOwnedByOpponent = progress.getPiecesOwnedByOpponent().size();

                if (piecesOwnedByPlayer == 2 && piecesOwnedByBoard == 1)
                    bestGuesses.add(new SlayAction(this.randomItem(progress.getPiecesOwnedByPlayer()).getPosition(), 5));

                if (piecesOwnedByPlayer == 2 && piecesOwnedByOpponent == 1)
                    bestGuesses.add(new SlayAction(this.randomItem(progress.getPiecesOwnedByPlayer()).getPosition(), 4));

                if (piecesOwnedByPlayer == 1 && piecesOwnedByBoard == 2)
                    bestGuesses.add(new SlayAction(this.randomItem(progress.getPiecesOwnedByPlayer()).getPosition(), 3));

                if (piecesOwnedByOpponent != 3) {
                    for (Point p : progress.getPiecesOwnedByPlayer()) {
                        bestGuesses.add(new SlayAction(p.getPosition(), 1));
                    }
                }
            }

        }

        bestGuesses.sort(Comparator.comparingInt(SlayAction::getWeight));
        return this.randomItem(bestGuesses).getPoint();
    }

    /* Returns that the player is a computer */
    @Override
    public boolean isComputer() {
        return true;
    }

    /* Return the name and number of the computer */
    @Override
    public String getName() {
        return "Computer " + this.getPlayerNumber();
    }

    /* Print end game statistics */
    @Override
    public void gameEndedStatistics() {
        System.out.println(String.format("%s heeft in totaal %s simulaties uitgevoerd. De meeste simulaties in een ronde waren: %s en de minste: %s", this.getTextInPlayerColor(this.getName()), this.totalSimulationsExecuted, this.highestSimulationsExecuted, this.lowestSimulationsExecuted));
    }

    /* Find the fastest route from point A to point B */
    private FastestRoute getFastestRouteToPoint(char start, char end, Player player, ArrayList<Move> piecesMovedBySimulation) {
        Simulation simulation = this.startSimulation(start, end, player, piecesMovedBySimulation);
        if (simulation == null) return null;
        return new FastestRoute(simulation.getMoves());
    }

    /* Find the fastest route to a mill from a certain point */
    private ArrayList<FastestRoute> getFastestRouteToMill(char start, Player player, MillProgress millToComplete) {
        ArrayList<FastestRoute> routes = new ArrayList<>();

        /* For each point in the mill if its owned by the player */
        for (Point point : millToComplete.getBoardPosition()) {
            if (point.isOwnedByPlayer(player)) {
                /* Get all the moves for the point that we already own so we can simulate the process if we move this first */
                ArrayList<Move> possibleMovesForPoint = this.board.getAvailableMoves(point.getPosition(), player);
                /* Loop through each possible move for that point */
                for (Move move : possibleMovesForPoint) {
                    ArrayList<Move> simulatedMoves = new ArrayList<>();

                    /* If it's a move within the mill simulate it else it it's useless */
                    if (millToComplete.getBoardPosition().contains(this.board.getPointFromChar(move.getNewPosition()))) {
                        simulatedMoves.add(move);
                        /* Simulate all possible routes */
                        FastestRoute route = this.getFastestRouteToPoint(start, move.getOldPosition(), player, simulatedMoves);
                        /* If no route was found continue */
                        if (route == null) continue;
                        /* Add the step to the route because it needs to be moved first */
                        route.addStep(move);
                        /* Only add the route if it has more than one step, because one step is the internal move only */
                        if (route.totalSteps() > 1)
                            routes.add(route);
                    }
                }
            }
        }

        /* Then run a simulation to each piece owned by the board */
        for (Point point : millToComplete.getPiecesOwnedByBoard()) {
            FastestRoute route = this.getFastestRouteToPoint(start, point.getPosition(), player, null);
            if (route == null) continue;
            /* Only add it if it has more than one step, and there are two pieces owned by the player so it will filter out internal moves */
            if (route.totalSteps() == 1 && millToComplete.getPiecesOwnedByPlayer().size() != 2)
                continue;
            routes.add(route);
        }

        return routes;
    }

    /* This is the wrapper for runSimulation, this returns the best possible simulation */
    private Simulation startSimulation(char start, char goal, Player player, ArrayList<Move> piecesMovedBySimulation) {
        ArrayList<Simulation> simulations = this.runSimulation(start, goal, player, piecesMovedBySimulation, new Simulation(), new ArrayList<>());
        ArrayList<Simulation> bestSimulations = new ArrayList<>();

        /* Find the best simulations */
        for (Simulation simulation : simulations) {
            if (simulation.getState() == SimulationState.COMPLETED)
                bestSimulations.add(simulation);
        }

        if (bestSimulations.size() == 0) return null;

        bestSimulations.sort(Comparator.comparingInt(Simulation::totalSteps));
        /* If there are no simulations return null so the parent function can handle this */
        return bestSimulations.get(0);
    }

    /* Run a simulation, this runs until it has run all possible simulations */
    private ArrayList<Simulation> runSimulation(char point, char goal, Player player, ArrayList<Move> piecesMovedBySimulation, Simulation parentSimulation, ArrayList<Simulation> simulations) {
        /* Increase the amount of simulations executed */
        this.simulationsExecuted++;
        /* Copy the previous simulation using the copy constructor */
        Simulation simulation = new Simulation(parentSimulation);
        /* Get available moves for the current point */
        ArrayList<Move> moves = this.board.getAvailableMoves(point, player, piecesMovedBySimulation);

        /* If there are no moves possible fail the simulation */
        if (moves.size() == 0) {
            simulation.setState(SimulationState.FAILED);
            simulations.add(simulation);
            return simulations;
        } else {
            /* Loop through each possible move */
            for (Move move : moves) {
                /* If the simulation has already been made the move or if the move is invalid */
                if (simulation.moveHasBeenMade(move) || (simulation.hasLastMove() && move.getOldPosition() != simulation.lastMove().getNewPosition())) {
                    simulation.setState(SimulationState.FAILED);
                    simulations.add(simulation);
                } else {
                    simulation.addMove(move);

                    /* If the goal has been reached */
                    if (move.getNewPosition() == goal) {
                        simulation.setState(SimulationState.COMPLETED);
                        simulations.add(simulation);
                        return simulations;
                    } else {
                        /* Start a new simulation and then add them to the simulation array */
                        ArrayList<Simulation> simulationResult = runSimulation(move.getNewPosition(), goal, player, piecesMovedBySimulation, simulation, simulations);
                        simulations.addAll(simulationResult);
                    }
                }
            }
        }

        return simulations;
    }

    /* Returns true if the first step of a route completes a mill */
    private boolean firstStepCompletesMill(FastestRoute route, MillProgress progress) {
        for (Point point : progress.getPiecesOwnedByPlayer()) {
            for (Point boardPoint : progress.getPiecesOwnedByBoard()) {
                /* Return false if the move is an internal move for example: A to B when you want to create a mill on ABC) */
                if (point.getPosition() == route.getFirstMove().getOldPosition() && route.getFirstMove().getNewPosition() == boardPoint.getPosition())
                    return false;
            }
        }

        /* If the first move of the route finishes at the last position the mill needs return true, else return false */
        if (progress.getPiecesOwnedByPlayer().size() == 2)
            return progress.getPiecesOwnedByBoard().get(0).getPosition() == route.getFirstMove().getNewPosition();

        return false;
    }

    /* Select a random item from an ArrayList, this uses a generic, so it takes over the type of the passed ArrayList */
    private <T> T randomItem(ArrayList<T> entries) {
        return entries.get(new Random().nextInt(entries.size()));
    }
}
