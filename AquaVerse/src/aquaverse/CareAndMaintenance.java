package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// Care and Maintenance flowchart - staff submits reports, admin reviews them
public class CareAndMaintenance {

    // creation of care and maintenance
    static String FILE_NAME = "maintenance.txt";

    // Staff can submit reports. Admin can review them.
    public static void showCareAndMaintenance(Scanner sc, String role, String name) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("--------------CARE AND MAINTENANCE FACILITIES---------------");

            if (role.equals("admin")) {
                System.out.println("| [1] View Pending Maintenance Reports                     |");
                System.out.println("| [2] View Accepted Maintenance Reports                    |");
            } else {
                System.out.println("| [1] Submit Maintenance Report                            |");
            }

            System.out.println("| [0] Back                                                 |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose: ");

            String answer = sc.nextLine().trim();

            // pick 0, go back
            if (answer.equals("0")) {
                back = true;

            } else if (role.equals("admin") && answer.equals("1")) {
                reviewPending(sc);

            } else if (role.equals("admin") && answer.equals("2")) {
                showAccepted();

            } else if (!role.equals("admin") && answer.equals("1")) {
                submitReport(sc, name);

            } else {
                System.out.println("\n- Please enter the value of your choice.");
            }
        }
    }

    // submitting a maintenance report
    static void submitReport(Scanner sc, String staffName) {
        String[] levels = {"Low", "Medium", "High"};
        String[] statuses = {"Pending", "Ongoing", "Completed"};
        boolean submitted = false;

        while (!submitted) {
            System.out.println();
            System.out.println("-------------------NEW MAINTENANCE REPORT-------------------");

            String problem = askText(sc, "Problem description: ");
            String level = askChoice(sc, "Maintenance level", levels);
            String date = askDate(sc);
            String status = askChoice(sc, "Maintenance status", statuses);

            System.out.println("\nAssigned staff who reported: " + staffName);

            double cost = askCost(sc);
            String remarks = askText(sc, "Completion remarks (type N/A if none): ");

            // confirm report submission
            System.out.println("------------------------------------------------------------");
            System.out.print("Submit report? (yes/no): ");

            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("yes")) {

                // save the report to the notepad file
                saveReport(problem + ";" + level + ";" + date + ";" + status + ";"
                        + staffName + ";" + String.format("%.2f", cost) + ";" + remarks + ";pending");

                System.out.println("\n- Report submitted");
                submitted = true;

            } else {

                // ask if staff wants to enter the report again
                System.out.print("Enter the report again? (yes/no): ");

                if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    return;
                }
            }
        }
    }

    // admin reviews the submitted reports
    static void reviewPending(Scanner sc) {
        boolean back = false;

        while (!back) {

            // read all reports from the notepad file
            String[] lines = readReports();

            // pending[0], pending[1], ... hold the positions of the pending reports
            int[] pending = new int[lines.length];
            int pendingCount = 0;

            System.out.println();
            System.out.println("----------------PENDING MAINTENANCE REPORTS-----------------");

            for (int i = 0; i < lines.length; i++) {
                String[] p = lines[i].split(";");

                // only show reports that are still pending
                if (p.length >= 8 && p[7].equals("pending")) {
                    pending[pendingCount] = i;
                    pendingCount++;
                    printReport(p, pendingCount);
                }
            }

            // no pending reports found
            if (pendingCount == 0) {
                System.out.println("|             No pending maintenance reports.              |");
                System.out.println("------------------------------------------------------------");
                return;
            }

            System.out.println("------------------------------------------------------------");

            System.out.print("Enter report number to review (0 to go back): ");
            int number = toInt(sc.nextLine().trim());

            // pick 0, go back
            if (number == 0) {
                back = true;

            } else if (number < 1 || number > pendingCount) {
                System.out.println("\n- Invalid report number.");

            } else {

                // get the selected report
                int index = pending[number - 1];
                String[] p = lines[index].split(";");

                System.out.print("\nAccept report? (yes/no): ");

                String accept = sc.nextLine().trim();

                // accept the selected report
                if (accept.equalsIgnoreCase("yes")) {

                    // Same line as before, but the last part becomes "accepted"
                    String newLine = p[0];

                    for (int k = 1; k < 7; k++) {
                        newLine = newLine + ";" + p[k];
                    }

                    // update the report status
                    lines[index] = newLine + ";accepted";
                    saveAll(lines, -1);

                    System.out.println("\n- Report Accepted");
                    System.out.println("- Maintenance Report List Updated");

                } else {

                    // ask if admin wants to delete the report
                    System.out.print("Delete report? (yes/no): ");

                    String delete = sc.nextLine().trim();

                    if (delete.equalsIgnoreCase("yes")) {
                        saveAll(lines, index);   // saves everything except this report
                        System.out.println("\n- Report Deleted");
                    }
                }
            }
        }
    }

    // showing all accepted maintenance reports
    static void showAccepted() {

        // read all reports from the notepad file
        String[] lines = readReports();
        int count = 0;

        System.out.println();
        System.out.println("----------------ACCEPTED MAINTENANCE REPORTS----------------");

        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(";");

            // only show reports that are already accepted
            if (p.length >= 8 && p[7].equals("accepted")) {
                count++;
                printReport(p, count);
            }
        }

        // no accepted reports found
        if (count == 0) {
            System.out.println("|           No accepted maintenance reports yet.           |");
        }

        System.out.println("------------------------------------------------------------");
    }

    // show the details of one maintenance report
    static void printReport(String[] p, int number) {
        if (number > 1) {
            System.out.println("|----------------------------------------------------------|");
        }

        System.out.printf("| %-56.56s |%n", "[" + number + "] " + p[0]);
        System.out.printf("| %-56.56s |%n", "- Maintenance Level: " + p[1]);
        System.out.printf("| %-56.56s |%n", "- Date Reported: " + p[2]);
        System.out.printf("| %-56.56s |%n", "- Status: " + p[3]);
        System.out.printf("| %-56.56s |%n", "- Assigned Staff: " + p[4]);
        System.out.printf("| %-56.56s |%n", "- Repair Cost: PHP " + p[5]);
        System.out.printf("| %-56.56s |%n", "- Completion Remarks: " + p[6]);
    }

    // read all reports from the notepad file
    static String[] readReports() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return new String[0];
        }

        try {

            // count the lines in the notepad file
            int count = 0;
            Scanner counter = new Scanner(file);

            while (counter.hasNextLine()) {
                if (!counter.nextLine().trim().isEmpty()) {
                    count++;
                }
            }

            counter.close();

            // put all the reports inside an array
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

    // save a new report to the notepad file
    static void saveReport(String line) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(line + "\n");
            writer.close();

        } catch (IOException e) {
            System.out.println("Could not save to " + FILE_NAME);
        }
    }

    // update the reports inside the notepad file and the report at position "skip" will not be saved
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
    
    // convert the input into an integer
    static int toInt(String text) {
        try {
            return Integer.parseInt(text);

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // asking for a text
    static String askText(Scanner sc, String label) {
        String text = "";

        while (text.isEmpty()) {
            System.out.print(label);
            text = sc.nextLine().trim().replace(";", ",");

            if (text.isEmpty()) {
                System.out.println("\n- This field must be filled");
            }
        }

        return text;
    }

    // choosing from the given options
    static String askChoice(Scanner sc, String title, String[] options) {
        while (true) {
            System.out.println();
            System.out.println("----------------------SELECT AN OPTION----------------------");
            System.out.printf("| %-56.56s |%n", title);
            System.out.println("|----------------------------------------------------------|");

            for (int i = 0; i < options.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + options[i]);
            }

            System.out.println("------------------------------------------------------------");
            System.out.print("Choose: ");
            int pick = toInt(sc.nextLine().trim());

            if (pick >= 1 && pick <= options.length) {
                return options[pick - 1];
            }

            System.out.println("\n- Please enter the value of your choice.");
        }
    }

    // asking for the date (MM-DD-YYYY)
    static String askDate(Scanner sc) {
        while (true) {
            System.out.print("\nDate reported (MM-DD-YYYY): ");
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

            System.out.println("\n- Date must follow MM-DD-YYYY, for example 11-04-2026");
        }
    }

    // asking for the repair cost
    static double askCost(Scanner sc) {
        while (true) {
            System.out.print("Repair cost (PHP): ");

            try {
                double cost = Double.parseDouble(sc.nextLine().trim());

                if (cost >= 0) {
                    return cost;
                }

            } catch (NumberFormatException e) {
                // invalid amount, show the message below
            }

            System.out.println("\n- Please enter a valid amount.");
        }
    }
}
