package ownCode;

import java.util.Scanner;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Intersection in) {
        super(name, in);
    }

    public int determineMove(Board board) {
        String prompt = "> " + getName() + " (" + getPlayerColour().toString() + ")"
                + ", what is your choice? ";
        int choice = readInt(prompt);
        boolean valid = board.isIntersection(choice) && board.isEmptyIntersection(choice);
        while (!valid) {
            System.out.println("ERROR: field " + choice
                    + " is no valid choice.");
            choice = readInt(prompt);
            valid = board.isIntersection(choice) && board.isEmptyIntersection(choice);
        }
        return choice;
    }

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
