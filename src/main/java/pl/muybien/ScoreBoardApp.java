package pl.muybien;

import pl.muybien.service.ScoreBoard;

import java.util.Scanner;

public class ScoreBoardApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScoreBoard scoreBoard = new ScoreBoard();

        System.out.println("\n" + "=*".repeat(20));
        System.out.println("    Welcome to the ScoreBoard CLI!");
        System.out.println("=*".repeat(20));


        while (true) {
            System.out.println("\n" + "*".repeat(55));
            System.out.println("Available commands:");
            System.out.println("start <homeTeam> <awayTeam>");
            System.out.println("update <homeTeam> <awayTeam> <homeScore> <awayScore>");
            System.out.println("finish <homeTeam> <awayTeam>");
            System.out.println("summary");
            System.out.println("exit");
            System.out.println("*".repeat(55)  + "\n");
            System.out.print("Enter command: ");

            String input = scanner.nextLine();
            String[] tokens = input.split("\\s+");

            if (tokens.length == 0) continue;

            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "start":
                        if (tokens.length != 3) {
                            System.out.println("Usage: start <homeTeam> <awayTeam>");
                            break;
                        }
                        scoreBoard.startMatch(tokens[1], tokens[2]);
                        System.out.println("Match started!");
                        break;
                    case "update":
                        if (tokens.length != 5) {
                            System.out.println("Usage: update <homeTeam> <awayTeam> <homeScore> <awayScore>");
                            break;
                        }
                        int homeScore = Integer.parseInt(tokens[3]);
                        int awayScore = Integer.parseInt(tokens[4]);
                        scoreBoard.updateScore(tokens[1], tokens[2], homeScore, awayScore);
                        System.out.println("Score updated!");
                        break;
                    case "finish":
                        if (tokens.length != 3) {
                            System.out.println("Usage: finish <homeTeam> <awayTeam>");
                            break;
                        }
                        scoreBoard.finishMatch(tokens[1], tokens[2]);
                        System.out.println("Match finished!");
                        break;
                    case "summary":
                        var summary = scoreBoard.getSummary();
                        for (var match : summary) {
                            System.out.printf("%s %d - %d %s\n",
                                    match.homeTeam(), match.homeScore(), match.awayScore(), match.awayTeam()
                            );
                        }
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}