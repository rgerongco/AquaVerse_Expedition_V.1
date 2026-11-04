package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CareAndMaintenance {

    // Every report is one line in maintenance.txt, separated by semicolons:
    // problem;level;date;status;staff;cost;remarks;review
    // "review" is "pending" until the admin accepts it, then "accepted"
    static String FILE_NAME = "maintenance.txt";

    // Staff can submit reports. Admin can review them.
    public static void showCareAndMaintenance(Scanner sc, String role, String name) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== CARE AND MAINTENANCE FACILITIES ===");
            if (role.equals("admin")) {
                System.out.println("[1] View Pending Maintenance Reports");
                System.out.println("[2] View Accepted Maintenance Reports");
            } else {
                System.out.println("[1] Submit Maintenance Report");
            }
            System.out.println("[0] Back");
            System.out.print("Choose: ");
            String answer = sc.nextLine().trim();

            if (answer.equals("0")) {
                back = true;
            } else if (role.equals("admin") && answer.equals("1")) {
                reviewPending(sc);
            } else if (role.equals("admin") && answer.equals("2")) {
                showAccepted();
            } else if (!role.equals("admin") && answer.equals("1")) {
                submitReport(sc, name);
            } else {
                System.out.println("Please enter the value of your choice.");
            }
        }
    }

    // ---------- STAFF: SUBMIT A REPORT ----------
    static void submitReport(Scanner sc, String staffName) {
        String[] levels = {"Low", "Medium", "High"};
        String[] statuses = {"Pending", "Ongoing", "Completed"};
        boolean submitted = false;

        while (!submitted) {
            System.out.println("\n=== NEW MAINTENANCE REPORT ===");
            String problem = askText(sc, "Problem description: ");
            String level = askChoice(sc, "Maintenance level:", levels);
            String date = askDate(sc);
            String status = askChoice(sc, "Maintenance status:", statuses);
            System.out.println("Assigned staff who reported: " + staffName);
            double cost = askCost(sc);
            String remarks = askText(sc, "Completion remarks (type N/A if none): ");

            System.out.print("Submit report? (yes/no): ");
            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("yes")) {
                saveReport(problem + ";" + level + ";" + date + ";" + status + ";"
                        + staffName + ";" + String.format("%.2f", cost) + ";" + remarks + ";pending");
                System.out.println("Report submitted");
                submitted = true;
            } else {
                System.out.print("Enter the report again? (yes/no): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    return;
                }
            }
        }
    }

    // ---------- ADMIN: REVIEW PENDING REPORTS ----------
    static void reviewPending(Scanner sc) {
        boolean back = false;

        while (!back) {
            String[] lines = readReports();

            // pending[0], pending[1], ... hold the positions of the pending reports
            int[] pending = new int[lines.length];
            int pendingCount = 0;

            System.out.println("\n=== PENDING MAINTENANCE REPORTS ===");
            for (int i = 0; i < lines.length; i++) {
                String[] p = lines[i].split(";");
                if (p.length >= 8 && p[7].equals("pending")) {
                    pending[pendingCount] = i;
                    pendingCount++;
                    printReport(p, pendingCount);
                }
            }

            if (pendingCount == 0) {
                System.out.println("No pending maintenance reports.");
                return;
            }

            System.out.print("Enter report number to review (0 to go back): ");
            int number = toInt(sc.nextLine().trim());

            if (number == 0) {
                back = true;
            } else if (number < 1 || number > pendingCount) {
                System.out.println("Invalid report number.");
            } else {
                int index = pending[number - 1];
                String[] p = lines[index].split(";");

                System.out.print("Accept report? (yes/no): ");
                String accept = sc.nextLine().trim();

                if (accept.equalsIgnoreCase("yes")) {
                    // Same line as before, but the last part becomes "accepted"
                    String newLine = p[0];
                    for (int k = 1; k < 7; k++) {
                        newLine = newLine + ";" + p[k];
                    }
                    lines[index] = newLine + ";accepted";
                    saveAll(lines, -1);
                    System.out.println("Report Accepted");
                    System.out.println("Maintenance Report List Updated");
                } else {
                    System.out.print("Delete report? (yes/no): ");
                    String delete = sc.nextLine().trim();
                    if (delete.equalsIgnoreCase("yes")) {
                        saveAll(lines, index);   // saves everything except this report
                        System.out.println("Report Deleted");
                    }
                    // NO -> goes back to the pending reports
                }
            }
        }
    }

    // ---------- ADMIN: VIEW ACCEPTED REPORTS ----------
    static void showAccepted() {
        String[] lines = readReports();
        int count = 0;

        System.out.println("\n=== ACCEPTED MAINTENANCE REPORTS ===");
        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(";");
            if (p.length >= 8 && p[7].equals("accepted")) {
                count++;
                printReport(p, count);
            }
        }
        if (count == 0) {
            System.out.println("No accepted maintenance reports yet.");
        }
    }

    static void printReport(String[] p, int number) {
        System.out.println("[" + number + "] " + p[0]);
        System.out.println("    Maintenance Level: " + p[1]);
        System.out.println("    Date Reported: " + p[2]);
        System.out.println("    Status: " + p[3]);
        System.out.println("    Assigned Staff: " + p[4]);
        System.out.println("    Repair Cost: PHP " + p[5]);
        System.out.println("    Completion Remarks: " + p[6]);
    }

    // ---------- FILE HELPERS ----------

    // Reads every report line of maintenance.txt into an array
    static String[] readReports() {
        File file = new File(FILE_NAME);
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
            System.out.println("Could not read " + FILE_NAME);
        }
        return new String[0];
    }

    static void saveReport(String line) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + FILE_NAME);
        }
    }

    // Writes the lines back to maintenance.txt.
    // The line at position "skip" is left out (use -1 to keep every line).
    static void saveAll(String[] lines, int skip) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            for (int i = 0; i < lines.length; i++) {
                if (i != skip) {
                    writer.write(lines[i] + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + FILE_NAME);
        }
    }

    // ---------- INPUT HELPERS ----------
    static int toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Keeps asking until something is typed (a semicolon is replaced with a comma)
    static String askText(Scanner sc, String label) {
        String text = "";
        while (text.isEmpty()) {
            System.out.print(label);
            text = sc.nextLine().trim().replace(";", ",");
            if (text.isEmpty()) {
                System.out.println("This field must be filled");
            }
        }
        return text;
    }

    static String askChoice(Scanner sc, String title, String[] options) {
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("[" + (i + 1) + "] " + options[i]);
            }
            System.out.print("Choose: ");
            int pick = toInt(sc.nextLine().trim());
            if (pick >= 1 && pick <= options.length) {
                return options[pick - 1];
            }
            System.out.println("Please enter the value of your choice.");
        }
    }

    // Date must look like MM-DD-YYYY
    static String askDate(Scanner sc) {
        while (true) {
            System.out.print("Date reported (MM-DD-YYYY): ");
            String date = sc.nextLine().trim();

            boolean valid = date.length() == 10;
            for (int i = 0; valid && i < date.length(); i++) {
                if (i == 2 || i == 5) {
                    if (date.charAt(i) != '-') {
                        valid = false;
                    }
                } else if (!Character.isDigit(date.charAt(i))) {
                    valid = false;
                }
            }

            if (valid) {
                return date;
            }
            System.out.println("Date must follow MM-DD-YYYY, for example 11-04-2026");
        }
    }

    static double askCost(Scanner sc) {
        while (true) {
            System.out.print("Repair cost (PHP): ");
            try {
                double cost = Double.parseDouble(sc.nextLine().trim());
                if (cost >= 0) {
                    return cost;
                }
            } catch (NumberFormatException e) {
                // falls through to the message below
            }
            System.out.println("Please enter a valid amount.");
        }
    }
}
