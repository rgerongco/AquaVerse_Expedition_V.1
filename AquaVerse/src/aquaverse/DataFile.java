package aquaverse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

// DataFile - reads and saves the notepad (.txt) files that hold the shop and exhibit information.
//
// Every line of a file is one record, and the parts of a record are separated by the | symbol.
//
//   giftshop.txt and cafe.txt:   Category|Group|Item|Price
//                                Drinks|Coffee|Americano|90
//                                (type - as the Group when the category has no groups)
//
//   exhibits.txt:                Category|Exhibit|Info line 1|Info line 2|...
//
// GiftShop, Cafe and Exhibitions all load their information from here, and so can
// Inventory Management: load a file, change rows[line][part], then save it again.
//
//   String[][] items = DataFile.load("giftshop.txt", 4);
//   items[0][3] = "200";                    // new price of the first item
//   DataFile.save("giftshop.txt", items);
public class DataFile {

    // ---------- READING AND SAVING ----------

    // Loads a file into rows[line][part]. Blank lines and lines with fewer than
    // "minParts" parts are skipped. If the file can't be found, the result is empty.
    public static String[][] load(String fileName, int minParts) {
        String[] lines = readLines(fileName);
        String[][] rows = new String[lines.length][];
        int count = 0;

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split("\\|");
            if (parts.length >= minParts) {
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }
                rows[count] = parts;
                count++;
            }
        }

        // Cut the array down to the rows that were really kept
        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = rows[i];
        }
        return result;
    }

    // Saves rows[line][part] into the file. The old content of the file is replaced.
    public static void save(String fileName, String[][] rows) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            for (int i = 0; i < rows.length; i++) {
                String line = rows[i][0];
                for (int j = 1; j < rows[i].length; j++) {
                    line = line + "|" + rows[i][j];
                }
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + fileName);
        }
    }

    // Reads every non-blank line of a file into an array
    static String[] readLines(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return new String[0];
        }

        try {
            // First pass: count the lines
            int count = 0;
            Scanner counter = new Scanner(file, "UTF-8");
            while (counter.hasNextLine()) {
                String line = counter.nextLine().replace("﻿", "").trim();
                if (!line.isEmpty()) {
                    count++;
                }
            }
            counter.close();

            // Second pass: put the lines in the array
            String[] lines = new String[count];
            int i = 0;
            Scanner reader = new Scanner(file, "UTF-8");
            while (reader.hasNextLine()) {
                String line = reader.nextLine().replace("﻿", "");
                if (!line.trim().isEmpty()) {
                    lines[i] = line;
                    i++;
                }
            }
            reader.close();
            return lines;
        } catch (IOException e) {
            System.out.println("Could not read " + fileName);
        }
        return new String[0];
    }

    // ---------- SEARCHING THE ROWS ----------

    // Returns the different values found in column "col", in the order they first appear
    // (for example the list of categories)
    public static String[] distinct(String[][] rows, int col) {
        String[] found = new String[rows.length];
        int count = 0;

        for (int i = 0; i < rows.length; i++) {
            boolean seen = false;
            for (int j = 0; j < count; j++) {
                if (found[j].equals(rows[i][col])) {
                    seen = true;
                }
            }
            if (!seen) {
                found[count] = rows[i][col];
                count++;
            }
        }

        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = found[i];
        }
        return result;
    }

    // Returns only the rows where column "col" is equal to "value"
    // (for example all the items of one category).
    // The rows are the same ones that were loaded, so changing them changes the loaded data too.
    public static String[][] filter(String[][] rows, int col, String value) {
        String[][] found = new String[rows.length][];
        int count = 0;

        for (int i = 0; i < rows.length; i++) {
            if (rows[i][col].equals(value)) {
                found[count] = rows[i];
                count++;
            }
        }

        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = found[i];
        }
        return result;
    }

    // ---------- INPUT HELPERS ----------

    // Shows the prompt and returns what the user typed.
    // Empty lines are skipped, so an Enter left behind by an earlier sc.nextInt()
    // can't mess up the next question.
    public static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        String text = sc.nextLine().trim();
        while (text.isEmpty()) {
            text = sc.nextLine().trim();
        }
        return text;
    }

    // Turns text into a whole number, or -1 if it isn't one
    public static int toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Turns text into an amount, or -1 if it isn't one
    public static double toDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
