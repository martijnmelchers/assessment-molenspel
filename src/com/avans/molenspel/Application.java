package com.avans.molenspel;

import java.util.Scanner;

public class Application {
    private Player[] players = new Player[2];
    private int gamesPlayed = 0;
    private boolean gameActive = false;
    private Morris morris;
    private Scanner scanner = new Scanner(System.in);

    /* Start the game */
    public void start() {
        this.gameActive = true;
        this.printIntroduction();

        /* Ask the user for a name */
        this.promptUserForName(0);
        this.promptUserForName(1);


        /*
         * Make some space
         */
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Alle spelers hebben een naam, de loting bepaald wie er mag beginnen!");

        /*
         * When all user data has been collected we initiate a new board and start the game loop
         */
        this.morris = new Morris(players);
        this.morris.play();

        /* Start watch for game ending and increase the amount of games played */
        this.gamesPlayed++;
        this.loop();
    }

    /* Asks the player of a name */
    private void promptUserForName(int playerIndex) {
        System.out.print(String.format("Speler %s, voer je naam in: ", playerIndex + 1));
        String response = this.scanner.nextLine();

        /* Throw an error if the name is empty */
        if (response.length() == 0) {
            ExceptionHandler.handleException(new Exception("Er is geen naam ingevoerd!"));
            this.promptUserForName(playerIndex);
            return;
        }

        /* Check whether or not to make the player an computer */
        switch (response.toLowerCase()) {
            case "c":
                System.out.println(String.format("Speler %s, is een computer", playerIndex + 1));
                players[playerIndex] = new Computer(playerIndex);
                break;
            case "stop":
                System.out.println(String.format("Bedankt voor het spelen! Je hebt %s games gespeeld!", this.gamesPlayed));
                System.exit(0);
            default:
                System.out.println(String.format("Speler %s, is een speler. Hallo %s", playerIndex + 1, response));
                players[playerIndex] = new Player(response, playerIndex);
                break;
        }
    }

    private void printIntroduction() {
        System.out.println("Welkom bij het molenspel!");
        System.out.println("Het spel is redelijk simpel er zijn drie fasen in het spel");
        System.out.println("Het plaatsen van pionnen");
        System.out.println("   Tijdens deze fase van het spel mogen beide spelers om de beurt één pion neerzetten");
        System.out.println("Het verplaatsen van pionnen");
        System.out.println("   Tijdens deze fase mogen spelers hun pionnen verplaatsen aan een van de aangrenzende vrije plaatsen.");
        System.out.println("Eindfase");
        System.out.println("   Zodra een van de spelers minder dan 3 pionnen heeft mag die speler zijn pionnen laten springen.");
        System.out.println("   Dit houd in dat hij zijn pionnen naar elk vakje op het bord mag verplaatsen.");
        System.out.println("   Zodra een speler nog maar twee pionnen over heeft is het spel over en heeft de andere speler gewonnen.");
        System.out.println("\n\nElkaar slaan");
        System.out.println("   Tijdens elke beurt mag je een pion verplaatsen, als je drie pionnen naast elkaar hebt (in een rechte lijn) heb je een molen.");
        System.out.println("   Als je een molen hebt mag je in die beurt één pion van de tegenstander slaan die NIET in een molen opstelling staan.");
        System.out.println("   Zodra er geen pionnen meer zijn die NIET in een molen opstelling staan mag je die pionnen ook slaan.");
        System.out.println("   Je hebt dus gewonnen als de tegenstander nog maar twee pionnen heeft");
        System.out.println();
        System.out.println("Je moet nu eerst de namen van de spelers invullen. Vul 'c' in als de speler een computer moet zijn en vul 'stop' in als je wilt stoppen!");
    }

    private void loop() {
        while (this.gameActive) {
            if (this.morris.gameEnded()) {
                this.morris = null;
                this.players = new Player[2];
                System.gc();
                this.gameActive = false;
            }
            if (!this.gameActive) this.start();
        }
    }
}
