package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class DataAnalytics {

    // sales.txt  -> date,category,amount     (category is Gift Shop or Cafe)
    // visits.txt -> date,category            (category is one of the exhibit categories)
    static String SALES_FILE = "sales.txt";
    static String VISITS_FILE = "visits.txt";

    static String[] VISIT_CATEGORIES = {
        "Fish Exhibits",
        "Marine Animal Exhibits",
        "Coral Reef Exhibits",
        "Special Animal Habitats",
        "Interactive and Educational Areas"
    };

    static String[] PERIODS = {"Daily", "Weekly", "Monthly", "Every Six Months", "Yearly"};

    // ---------- RECORDING (called by GiftShop, Cafe and Exhibitions) ----------
    public static void recordSale(String category, double amount) {
        addLine(SALES_FILE, LocalDate.now() + "," + category + "," + amount);
    }

    public static void recordVisit(String category) {
        addLine(VISITS_FILE, LocalDate.now() + "," + category);
    }

    static void addLine(String fileName, String line) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + fileName);
        }
    }

    // Reads every line of a file into an array
    static String[] readLines(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return new String[0];
        }

        try {
            // First pass: count the lines
            int count = 0;
            Scanner counter = new Scanner(file);
            while (counter.hasNextLine()) {
                if (!counter.nextLine().trim().isEmpty()) {
                    count++;
                }
            }
            counter.close();

            // Second pass: put the lines in the array
            String[] lines = new String[count];
            int i = 0;
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
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

    // ---------- MENU (admin only) ----------
    public static void showDataAnalytics(Scanner sc) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== DATA ANALYTICS ===");
            System.out.println("[1] Total Sales");
            System.out.println("[2] Total Visits");
            System.out.println("[0] Back");
            System.out.print("Choose: ");
            String answer = sc.nextLine().trim();

            if (answer.equals("0")) {
                back = true;
            } else if (answer.equals("1") || answer.equals("2")) {
                int period = choosePeriod(sc);
                if (period != 0) {
                    LocalDate start = getStartDate(period);
                    if (answer.equals("1")) {
                        showSales(period, start);
                    } else {
                        showVisits(period, start);
                    }
                }
            } else {
                System.out.println("Please enter the value of your choice.");
            }
        }
    }

    // Returns 1-5 for the chosen time period, or 0 to go back
    static int choosePeriod(Scanner sc) {
        System.out.println("\nSelect a time period:");
        for (int i = 0; i < PERIODS.length; i++) {
            System.out.println("[" + (i + 1) + "] " + PERIODS[i]);
        }
        System.out.println("[0] Back");
        System.out.print("Choose: ");
        String answer = sc.nextLine().trim();

        if (answer.equals("1")) return 1;
        if (answer.equals("2")) return 2;
        if (answer.equals("3")) return 3;
        if (answer.equals("4")) return 4;
        if (answer.equals("5")) return 5;
        if (!answer.equals("0")) {
            System.out.println("Please enter the value of your choice.");
        }
        return 0;
    }

    // The first day included in the chosen time period (the last day is today)
    static LocalDate getStartDate(int period) {
        LocalDate today = LocalDate.now();
        if (period == 1) {
            return today;
        } else if (period == 2) {
            return today.minusDays(6);
        } else if (period == 3) {
            return today.minusMonths(1).plusDays(1);
        } else if (period == 4) {
            return today.minusMonths(6).plusDays(1);
        } else {
            return today.minusYears(1).plusDays(1);
        }
    }

    static boolean inPeriod(LocalDate date, LocalDate start) {
        return !date.isBefore(start) && !date.isAfter(LocalDate.now());
    }

    // ---------- TOTAL SALES ----------
    static void showSales(int period, LocalDate start) {
        String[] lines = readLines(SALES_FILE);
        double giftShop = 0;
        double cafe = 0;
        int count = 0;

        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(",");
            if (p.length < 3) {
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(p[0]);
                double amount = Double.parseDouble(p[2]);
                if (inPeriod(date, start)) {
                    count++;
                    if (p[1].equals("Gift Shop")) {
                        giftShop += amount;
                    } else if (p[1].equals("Cafe")) {
                        cafe += amount;
                    }
                }
            } catch (Exception e) {
                // skip a damaged line
            }
        }

        System.out.println("\n=== TOTAL SALES (" + PERIODS[period - 1] + ") ===");
        System.out.println("From " + start + " to " + LocalDate.now());
        if (count == 0) {
            System.out.println("No matching records found.");
        } else {
            System.out.println("Gift Shop: PHP " + String.format("%.2f", giftShop));
            System.out.println("Cafe:      PHP " + String.format("%.2f", cafe));
            System.out.println("Total:     PHP " + String.format("%.2f", giftShop + cafe));
        }
    }

    // ---------- TOTAL VISITS ----------
    static void showVisits(int period, LocalDate start) {
        String[] lines = readLines(VISITS_FILE);
        int[] counts = new int[VISIT_CATEGORIES.length];
        int total = 0;

        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(",");
            if (p.length < 2) {
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(p[0]);
                if (inPeriod(date, start)) {
                    for (int j = 0; j < VISIT_CATEGORIES.length; j++) {
                        if (p[1].equals(VISIT_CATEGORIES[j])) {
                            counts[j]++;
                            total++;
                        }
                    }
                }
            } catch (Exception e) {
                // skip a damaged line
            }
        }

        System.out.println("\n=== TOTAL VISITS (" + PERIODS[period - 1] + ") ===");
        System.out.println("From " + start + " to " + LocalDate.now());
        if (total == 0) {
            System.out.println("No matching records found.");
        } else {
            for (int j = 0; j < VISIT_CATEGORIES.length; j++) {
                System.out.println(VISIT_CATEGORIES[j] + ": " + counts[j]);
            }
            System.out.println("Total Visits: " + total);
        }
    }
}
