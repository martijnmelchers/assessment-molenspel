package com.avans.molenspel;

public class Morris {
    private Player[] players;
    private int playerTurn;

    private Board board;
    private int round = 0;
    private GameState state = GameState.PLACING_PIECES;
    private boolean ended = false;

    public Morris(Player[] players) {
        this.players = players;

        this.board = new Board();

        /* Generate a random starting player */
        int startingPlayer = Math.random() > 0.5 ? 1 : 0;
        int opponentPlayer = startingPlayer == 0 ? 1 : 0;

        this.playerTurn = startingPlayer;

        /* Assign the players a color */
        players[startingPlayer].setPlayerColor(PlayerColor.BLUE);
        players[opponentPlayer].setPlayerColor(PlayerColor.RED);

        /* Announce who has will start */
        System.out.println(String.format("Speler %s, %s is blauw en mag beginnen", players[startingPlayer].getPlayerNumber(), players[startingPlayer].getName()));
        System.out.println(String.format("Speler %s, %s is rood", players[opponentPlayer].getPlayerNumber(), players[opponentPlayer].getName()));

        /* Set the other player as opponent to the other player so the CPU can use this */
        players[startingPlayer].setOpponent(players[opponentPlayer]);
        players[opponentPlayer].setOpponent(players[startingPlayer]);
    }

    /* Start the game */
    public void play() {
        while (!this.ended) {
            this.announceGameRoundChange(this.players[playerTurn]);
            round++;

            if (players[0].getName().toLowerCase().equals("test") && round == 1)
                this.insertTestData();
            else if (round == 1)
                this.board.draw();


            Player player = this.players[playerTurn];
            if (player.isComputer()) player.setBoard(this.board);

            if (!player.placedAllPieces()) this.placePieceRequest(player);
            else this.movePieceRequest(player);

            if (!player.isComputer() || !player.getOpponent().isComputer()) this.board.draw();

            this.checkForWin();
        }
    }

    /* Announces when the round changes */
    private void announceGameRoundChange(Player player) {
        if(this.state == GameState.PLACING_PIECES) {
            if(this.players[0].placedAllPieces() && this.players[1].placedAllPieces()) {
                this.state = GameState.MOVING_PIECES;
                System.out.println("Iedereen heeft zijn pionnen geplaatst! Je moet nu je pionnen verplaatsen!");
            }
        }

        if(this.state == GameState.MOVING_PIECES) {
            if(this.board.getPointsByPlayer(player).size() == 3) {
                this.state = GameState.JUMPING_PIECES;
                System.out.println(player.getName() + " heeft nog maar 3 pionnen en mag nu overal zijn pionnen naartoe verplaatsen!");
            }
        }
    }

    /* Creates initial test data */
    private void insertTestData() {
        try {
            /* Place test pieces for player 1 */
            this.board.placePiece('A', players[0]);
            this.board.placePiece('B', players[0]);
            this.board.placePiece('F', players[0]);
            this.board.placePiece('H', players[0]);
            this.board.placePiece('K', players[0]);
            this.board.placePiece('P', players[0]);
            this.board.placePiece('Q', players[0]);
            this.board.placePiece('U', players[0]);
            this.board.placePiece('W', players[0]);

            /* Place test pieces for player 2 */
            this.board.placePiece('C', players[1]);
            this.board.placePiece('D', players[1]);
            this.board.placePiece('G', players[1]);
            this.board.placePiece('I', players[1]);
            this.board.placePiece('J', players[1]);
            this.board.placePiece('N', players[1]);
            this.board.placePiece('S', players[1]);
            this.board.placePiece('V', players[1]);
            this.board.placePiece('X', players[1]);

            this.board.draw();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    /* Ask the player or computer where they want to place a piece, also handles the errors which may occur */
    private void placePieceRequest(Player player) {
        try {
            char pos = player.askForPiecePlacement();

            if (this.board.placePiece(pos, player)) {
                System.out.println(String.format("%s heeft een pion geplaatst op positie %s en kreeg hierdoor een molen", player.getTextInPlayerColor(player.getName()), pos));

                if (!player.isComputer())
                    this.board.draw();

                this.slayPieceRequest(player);
            } else {
                System.out.println(String.format("%s heeft een pion geplaatst op positie %s", player.getTextInPlayerColor(player.getName()), pos));
            }

            this.changePlayer();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    /* Ask the player or computer which piece they want to move, also handles the errors which may occur */
    private void movePieceRequest(Player player) {
        try {
            char[] position = player.askForMove();
            /* Try to move the piece, and ask the player to slay a piece if it is a mill */
            if (this.board.movePiece(position[0], position[1], player)) {
                System.out.println(String.format("%s heeft pion op positie %s verplaatst naar %s en kreeg hierdoor een molen", player.getTextInPlayerColor(player.getName()), position[0], position[1]));

                this.slayPieceRequest(player);

                if (!player.isComputer())
                    this.board.draw();
            } else {
                System.out.println(String.format("%s heeft pion op positie %s verplaatst naar %s", player.getTextInPlayerColor(player.getName()), position[0], position[1]));
            }
            this.changePlayer();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }

    }

    /* Ask the player or computer which piece they want to slay, also handles the errors which may occur */
    private void slayPieceRequest(Player player) {
        try {
            /* Ask the player for the position they'd like to slay */
            char pos = player.askForSlay();
            Player opponent = player.getOpponent();

            /* Try to slay the piece */
            this.board.slayPiece(pos, player);

            /* If they they slayed the piece announce it */
            System.out.println(String.format("%s heeft een pion van %s op positie %s geslagen", player.getTextInPlayerColor(player.getName()), opponent.getTextInPlayerColor(opponent.getName()), pos));
        } catch (Exception e) {
            /* Handle the exception and ask the player again, we loop here because this won't be asked again in the game loop */
            ExceptionHandler.handleException(e);
            slayPieceRequest(player);
        }
    }

    /* Check if each player has won */
    private void checkForWin() {
        for (Player player : this.players) {
            int movesRemaining = 0;

            if (this.board.getPointsByPlayer(player).size() == 2 && player.placedAllPieces())
                this.endGame(player);

            for(Point piece : this.board.getPointsByPlayer(player))
                movesRemaining += this.board.getAvailableMoves(piece.getPosition(), player).size();

            if(movesRemaining == 0 && player.placedAllPieces())
                this.endGame(player.getOpponent());
        }
    }

    /* Ends the game */
    private void endGame(Player winner) {
        this.ended = true;
        System.out.println(String.format("Gefeliciteerd! Speler %s, %s heeft gewonnen!", winner.getPlayerNumber(), winner.getTextInPlayerColor(winner.getName())));
        System.out.println(String.format("Rondes gespeeld: %s", this.round));
        for(Player computer : this.players)
            if(computer.isComputer()) computer.gameEndedStatistics();
    }

    /* Changes the player which is playing */
    private void changePlayer() {
        this.playerTurn = playerTurn == 1 ? 0 : 1;
    }

    /* Returns whether or not the game has ended */
    public boolean gameEnded() {
        return this.ended;
    }
}
