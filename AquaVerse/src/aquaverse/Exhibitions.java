package aquaverse;

import java.io.*;
import java.util.Scanner;

public class Exhibitions {

    // exhibits.txt -> Category|Exhibit|Info line 1|Info line 2|...
    static String FILE_NAME = "exhibits.txt";

    // Creates visitors.txt if it doesn't exist yet
    public static void createFiles() throws IOException {

        File visitors = new File("visitors.txt");
        if (!visitors.exists()) {
            visitors.createNewFile();
        }
    }

    // Reads exhibits.txt and lets the visitor view each category
    public static void showExhibitions(Scanner input) throws IOException {
        String[][] rows = DataFile.load(FILE_NAME, 3);
        if (rows.length == 0) {
            System.out.println("\n- Could not load the exhibits from " + FILE_NAME);
            return;
        }
        String[] categories = DataFile.distinct(rows, 0);

        boolean done = false;
        while (!done) {
            System.out.println();
            System.out.println("------------------------EXHIBITIONS-------------------------");

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + categories[i]);
            }

            System.out.println("| [0] Finish Visit                                         |");
            System.out.println("------------------------------------------------------------");
            int pick = DataFile.toInt(DataFile.ask(input, "Choose a category: "));

            if (pick == 0) {
                done = true;
            } else if (pick < 1 || pick > categories.length) {
                System.out.println("------------------------------------------------------------");
                System.out.println("|                     Invalid choice.                      |");
                System.out.println("------------------------------------------------------------");
            } else {
                showCategory(input, rows, categories[pick - 1]);
            }
        }
    }

    // Lists the exhibits of one category, then shows the one the visitor picks
    static void showCategory(Scanner input, String[][] rows, String category) {
        // Save the visit so Data Analytics can count it
        DataAnalytics.recordVisit(category);

        String[][] exhibits = DataFile.filter(rows, 0, category);

        System.out.println();
        System.out.println("--------------------------EXHIBITS--------------------------");
        System.out.printf("| %-56.56s |%n", category.toUpperCase());
        System.out.println("|----------------------------------------------------------|");

        for (int i = 0; i < exhibits.length; i++) {
            System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + exhibits[i][1]);
        }

        System.out.println("| [0] Exit Exhibit                                         |");
        System.out.println("------------------------------------------------------------");
        int choice = DataFile.toInt(DataFile.ask(input, "Choice: "));

        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > exhibits.length) {
            System.out.println("\n- Invalid choice.");
            return;
        }

        // Show the exhibit until the visitor exits it
        // (part 1 is the name, every part after that is one line of information)
        String[] exhibit = exhibits[choice - 1];
        boolean viewing = true;

        while (viewing) {
            System.out.println();
            System.out.println("--------------------EXHIBIT INFORMATION---------------------");
            System.out.printf("| %-56.56s |%n", exhibit[1].toUpperCase());
            System.out.println("|----------------------------------------------------------|");

            for (int i = 2; i < exhibit.length; i++) {
                printWrapped("- " + exhibit[i]);
            }

            System.out.println("|----------------------------------------------------------|");
            System.out.println("| [0] Exit                                                 |");
            System.out.println("------------------------------------------------------------");

            if (DataFile.toInt(DataFile.ask(input, "Choice: ")) == 0) {
                viewing = false;
            }
        }
    }

    // Prints a long text inside the box, breaking it into rows that fit
    static void printWrapped(String text) {
        while (text.length() > 56) {
            int cut = text.lastIndexOf(' ', 56);

            if (cut < 5) {
                cut = 56;
            }

            System.out.printf("| %-56.56s |%n", text.substring(0, cut));
            text = "  " + text.substring(cut).trim();
        }

        System.out.printf("| %-56.56s |%n", text);
    }
}
