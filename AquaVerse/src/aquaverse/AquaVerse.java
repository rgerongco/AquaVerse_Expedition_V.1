package aquaverse;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AquaVerse {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Exhibitions.createFiles();
        } catch (IOException e) {
            System.out.println("Could not create visitors.txt");
        }

        // Each product here gets updated on the notepad
        InventoryManagement.syncStock();

        // This checks how a user is logged in whether they can be a user/staff/admin
        try {
            while (true) {
                LoginAuthentication.loggedRole = "";
                String name = LoginAuthentication.login(sc);
                
                // Exit chosen on the Login page
                if (name.isEmpty()) {
                    break;   
                }
                runDashboard(sc, name, LoginAuthentication.loggedRole);
                System.out.println("\nLogged out. Returning to the login page...\n");
            }
        } catch (NoSuchElementException e) {
            // The input stream ended, so there is nothing more to read
        }

        // The output once exiting the system
        System.out.println();
        System.out.println("-------------------------THANK YOU--------------------------");
        System.out.println("|            Thank you for visiting AquaVerse!             |");
        System.out.println("------------------------------------------------------------");
    }

    // Running the dashboard by roles
    static void runDashboard(Scanner sc, String name, String role) {
        boolean done = false;

        while (!done) {
            try {
                System.out.println();

                if (role.equals("user")) {
                    System.out.println("-------------------------DASHBOARD--------------------------");
                    System.out.printf("| %-56.56s |%n", "Visitor: " + name);
                    System.out.println("|----------------------------------------------------------|");
                    System.out.println("| [1] Information Desk                                     |");
                    System.out.println("| [2] Exhibitions                                          |");
                    System.out.println("| [3] Gift Shop                                            |");
                    System.out.println("| [4] Cafe                                                 |");
                    System.out.println("| [0] Exit                                                 |");
                    System.out.println("------------------------------------------------------------");

                } else if (role.equals("staff")) {
                    System.out.println("-------------------------DASHBOARD--------------------------");
                    System.out.printf("| %-56.56s |%n", "Staff: " + name);
                    System.out.println("|----------------------------------------------------------|");
                    System.out.println("| [1] Information Desk                                     |");
                    System.out.println("| [2] Exhibitions                                          |");
                    System.out.println("| [3] Gift Shop                                            |");
                    System.out.println("| [4] Cafe                                                 |");
                    System.out.println("| [5] Care and Maintenance Facilities                      |");
                    System.out.println("| [6] Inventory Management                                 |");
                    System.out.println("| [0] Exit                                                 |");
                    System.out.println("------------------------------------------------------------");

                } else if (role.equals("admin")) {
                    System.out.println("-------------------------DASHBOARD--------------------------");
                    System.out.printf("| %-56.56s |%n", "Admin: " + name);
                    System.out.println("|----------------------------------------------------------|");
                    System.out.println("| [1] Information Desk                                     |");
                    System.out.println("| [2] Exhibitions                                          |");
                    System.out.println("| [3] Gift Shop                                            |");
                    System.out.println("| [4] Cafe                                                 |");
                    System.out.println("| [5] Care and Maintenance Facilities                      |");
                    System.out.println("| [6] Data Analytics                                       |");
                    System.out.println("| [7] Account Management                                   |");
                    System.out.println("| [8] Inventory Management                                 |");
                    System.out.println("| [0] Exit                                                 |");
                    System.out.println("------------------------------------------------------------");

                // If it's not part of the role
                } else { 
                    System.out.println("------------------------------------------------------------");
                    System.out.println("|                   Invalid account role                   |");
                    System.out.println("------------------------------------------------------------");
                    return;
                }

                System.out.print("Choose: ");
                int choice = DataFile.toInt(sc.nextLine().trim());

                if (choice == 1) {
                    if (role.equals("user") && hasPaid(name)) { // if user have already paid
                        System.out.println("------------------------------------------------------------");
                        System.out.println("|                    Already have paid.                    |");
                        System.out.println("------------------------------------------------------------");
                    } else if (role.equals("user") && !hasPaid(name)) {//if user didn't pay
                        InformationDesk.buyTickets(sc, name);
                    } else { // if admin/staff
                        System.out.println("------------------------------------------------------------");
                        System.out.println("|               Information Desk Management                |");
                        System.out.println("|               Staff/Admin access granted.                |");
                        System.out.println("------------------------------------------------------------");
                    }

                } else if (choice == 2) {
                    if (role.equals("user") && !hasPaid(name)) {// if user didn't pay
                        System.out.println("------------------------------------------------------------");
                        System.out.println("|        Pay the entrance fee first for this access        |");
                        System.out.println("------------------------------------------------------------");
                    } else { // if user have already paid
                        Exhibitions.showExhibitions(sc);
                    }

                } else if (choice == 3) {
                    if (role.equals("user") && !hasPaid(name)) {// if user didn't pay
                        System.out.println("------------------------------------------------------------");
                        System.out.println("|         Pay the entrance fee first for this access       |");
                        System.out.println("------------------------------------------------------------");
                    } else { // if user have already paid
                        GiftShop.showGiftShop(sc);
                    }

                } else if (choice == 4) {
                    if (role.equals("user") && !hasPaid(name)) {// if user didn't pay
                        System.out.println("------------------------------------------------------------");
                        System.out.println("|         Pay the entrance fee first for this access       |");
                        System.out.println("------------------------------------------------------------");
                    } else { // if user have already paid
                        Cafe.showCafe(sc);
                    }

                } else if (choice == 5 && (role.equals("staff") || role.equals("admin"))) {
                    CareAndMaintenance.showCareAndMaintenance(sc, role, name);

                } else if (choice == 6 && role.equals("staff")) {
                    InventoryManagement.showInventoryManagement(sc, role);

                } else if (choice == 6 && role.equals("admin")) {
                    DataAnalytics.showDataAnalytics(sc);

                } else if (choice == 7 && role.equals("admin")) {
                    AccountManagement.showAccountManagement(sc);

                } else if (choice == 8 && role.equals("admin")) {
                    InventoryManagement.showInventoryManagement(sc, role);

                } else if (choice == 0) {
                    done = true;

                } else { // if picked choice isn't in the choices
                    System.out.println("------------------------------------------------------------");
                    System.out.println("|                      Invalid choice.                     |");
                    System.out.println("------------------------------------------------------------");
                }

            } catch (NoSuchElementException e) {
                throw e; // input ended, let main stop cleanly

            } catch (Exception e) {
                // any other errors show a message and go back to the dashboard
                System.out.println("------------------------------------------------------------");
                System.out.printf("| %-56.56s |%n", "Something went wrong (" + e.getMessage() + ").");
                System.out.println("|                  Back to the dashboard.                  |");
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    // reads visitors.txt and checks if the name has a ticket for today
    // (each line is "name,YYYY-MM-DD", a ticket booked for a later date does not count yet)
    public static boolean hasPaid(String name) {
        try {
            Scanner file = new Scanner(new File("visitors.txt"));
            boolean found = false;
            String today = java.time.LocalDate.now().toString();

            while (file.hasNextLine()) {
                String line = file.nextLine().trim();
                int comma = line.lastIndexOf(',');

                if (comma == -1) {
                    // old record without a date, the ticket stays valid
                    if (line.equalsIgnoreCase(name)) {
                        found = true;
                    }
                } else if (line.substring(0, comma).trim().equalsIgnoreCase(name)
                        && line.substring(comma + 1).trim().equals(today)) {
                    found = true;
                }
            }

            file.close();
            return found;

        } catch (IOException e) {
            return false;   // no visitors.txt yet means nobody has paid
        }
    }
}
