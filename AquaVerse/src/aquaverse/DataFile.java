package aquaverse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

// DataFile - reads and saves the notepad (.txt) files that hold the shop and exhibit information.
// Every line of a file is one record, and the parts of a record are separated by the | symbol.
//   giftshop.txt and cafe.txt:   Category|Group|Item|Price
//                                Drinks|Coffee|Americano|90
//                                (type - as the Group when the category has no groups)
//   exhibits.txt:                Category|Exhibit|Info line 1|Info line 2|...
// GiftShop, Cafe and Exhibitions all load their information from here, and so can
// Inventory Management: load a file, change rows[line][part], then save it again.
//   String[][] items = DataFile.load("giftshop.txt", 4);
//   items[0][3] = "200";                    // new price of the first item
//   DataFile.save("giftshop.txt", items);
public class DataFile {

    // load all records from the notepad file
    public static String[][] load(String fileName, int minParts) {
        String[] lines = readLines(fileName);
        String[][] rows = new String[lines.length][];
        int count = 0;

        // separate each record using the | symbol
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split("\\|");

            // check if the record has enough parts
            if (parts.length >= minParts) {

                // remove extra spaces from each part
                for (int j = 0; j < parts.length; j++) {
                    parts[j] = parts[j].trim();
                }

                // add the record to the array
                rows[count] = parts;
                count++;
            }
        }

        // create a new array containing only the records that were loaded
        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = rows[i];
        }
        return result;
    }

    // save all records to the notepad file
    public static void save(String fileName, String[][] rows) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            // combine the parts of each record using the | symbol
            for (int i = 0; i < rows.length; i++) {
                String line = rows[i][0];
                for (int j = 1; j < rows[i].length; j++) {
                    line = line + "|" + rows[i][j];
                }

                // write the record into the file
                writer.println(line);
            }

            // close the file after saving
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + fileName);
        }
    }

    // read all non-empty lines from the notepad file
    static String[] readLines(String fileName) {
        File file = new File(fileName);

        // check if the file exists
        if (!file.exists()) {
            return new String[0];
        }

        try {
            // first pass: count the non-empty lines
            int count = 0;
            Scanner counter = new Scanner(file, "UTF-8");
            while (counter.hasNextLine()) {
                String line = counter.nextLine().replace("\uFEFF", "").trim();
                if (!line.isEmpty()) {
                    count++;
                }
            }
            counter.close();

            // second pass: place all non-empty lines inside the array
            String[] lines = new String[count];
            int i = 0;
            Scanner reader = new Scanner(file, "UTF-8");
            while (reader.hasNextLine()) {
                String line = reader.nextLine().replace("\uFEFF", "");
                if (!line.trim().isEmpty()) {
                    lines[i] = line;
                    i++;
                }
            }
            reader.close();

            // return all the lines read from the file
            return lines;
        } catch (IOException e) {
            System.out.println("Could not read " + fileName);
        }
        return new String[0];
    }

    // get all unique values from a selected column
    public static String[] distinct(String[][] rows, int col) {
        String[] found = new String[rows.length];
        int count = 0;

        // check each record for a value that has not been added yet
        for (int i = 0; i < rows.length; i++) {
            boolean seen = false;
            for (int j = 0; j < count; j++) {
                if (found[j].equals(rows[i][col])) {
                    seen = true;
                }
            }

            // add the value if it is not yet in the array
            if (!seen) {
                found[count] = rows[i][col];
                count++;
            }
        }

        // create an array containing only the unique values
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = found[i];
        }
        return result;
    }

    // get all records that match the selected column and value
    public static String[][] filter(String[][] rows, int col, String value) {
        String[][] found = new String[rows.length][];
        int count = 0;

        // check each record for the selected value
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][col].equals(value)) {

                // add the matching record to the array
                found[count] = rows[i];
                count++;
            }
        }

        // create an array containing only the matching records
        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = found[i];
        }
        return result;
    }

    // ask the user for a text input
    public static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        String text = sc.nextLine().trim();

        // keep asking if the input is empty
        while (text.isEmpty()) {
            System.out.print(prompt);
            text = sc.nextLine().trim();
        }
        return text;
    }

    // ask a yes/no question, anything else is not accepted and the question is asked again
    public static boolean askYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();

            if (text.equalsIgnoreCase("yes")) {
                return true;
            }
            if (text.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("\n- Please enter yes or no.");
        }
    }

    // check if the text is a real date written as MM-DD-YYYY
    public static boolean isValidDate(String date) {
        if (date.length() != 10) {
            return false;
        }

        for (int i = 0; i < date.length(); i++) {
            char c = date.charAt(i);
            if (i == 2 || i == 5) {
                if (c != '-') {
                    return false;
                }
            } else if (c < '0' || c > '9') {
                return false;
            }
        }

        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));

        if (year < 1900) {
            return false;
        }

        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    // round a money value to centavos so the computations do not show tiny errors
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // convert text into a whole number
    public static int toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {

            // return -1 if the input is not a whole number
            return -1;
        }
    }

    // convert text into a decimal number
    public static double toDouble(String text) {
        String t = text.trim();

        // digits with an optional decimal point only (no NaN, Infinity, 5d, 1e5, etc.)
        if (!t.matches("\\d*\\.?\\d+")) {
            return -1;
        }

        try {
            double value = Double.parseDouble(t);

            if (Double.isInfinite(value) || Double.isNaN(value)) {
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {

            // return -1 if the input is not a valid number
            return -1;
        }
    }
}
