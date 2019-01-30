package ownCode;

import java.util.Scanner;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Intersection in) {
        super(name, in);
    }

    //move is wat de player aan int geeft
    public int determineMove(Board board) {
        String prompt = "> " + getName() + " (" + getPlayerColour().toString() + ")"
                + ", what is your choice? ";
        int choice = readInt(prompt);
        boolean valid = board.isIntersection(choice) && board.isEmptyIntersection(choice);//kijkt of het een valid move is
        while (!valid) {
            System.out.println("ERROR: field " + choice
                    + " is no valid choice.");
            choice = readInt(prompt);
            valid = board.isIntersection(choice) && board.isEmptyIntersection(choice);
        }
        return choice;
    }

    //Stuur prompt naar player, kijkt wat reactie is, maak int van string
    private int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        @SuppressWarnings("resource")
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine());) {
                if (scannerLine.hasNextInt()) {
                    intRead = true;
                    value = scannerLine.nextInt();
                }
            }
        } while (!intRead);
        return value;
    }

}
