package aquaverse;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AquaVerse {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        Exhibitions.createFiles();

        String name = LoginAuthentication.login(sc);
        String role = LoginAuthentication.loggedRole;

        boolean done = false;

        while (done == false) {
            if (role.equals("user")) {
                System.out.println("=== DASHBOARD ===");
                System.out.println("Visitor: " + name);
                System.out.println("[1] Information Desk");
                System.out.println("[2] Exhibitions");
                System.out.println("[3] Gift Shop");
                System.out.println("[4] Cafe");
                System.out.println("[0] Exit");
            } else if (role.equals("staff")) {
                System.out.println("=== DASHBOARD ===>");
                System.out.println("Staff: " + name);
                System.out.println("[1] Information Desk");
                System.out.println("[2] Exhibitions");
                System.out.println("[3] Gift Shop");
                System.out.println("[4] Cafe");
                System.out.println("[5] Care and Maintenance Facilities");
                System.out.println("[0] Exit");
            } else if (role.equals("admin")) {
                System.out.println("<=== DASHBOARD ===>");
                System.out.println("Admin: " + name);
                System.out.println("[1] Information Desk");
                System.out.println("[2] Exhibitions");
                System.out.println("[3] Gift Shop");
                System.out.println("[4] Cafe");
                System.out.println("[5] Care and Maintenance Facilities");
                System.out.println("[6] Data Analytics");
                System.out.println("[7] Account Management");
                System.out.println("[0] Exit");
            } else {
                System.out.println("Invalid account role");
                break;
            }
            System.out.print("Choose: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                if (role.equals("user")) {
                    InformationDesk.buyTickets(sc, name);
                } else {
                    System.out.println("Information Desk Management");
                    System.out.println("Staff/Admin access granted.");
                }
            } else if (choice == 2) {
                if (role.equals("user")) {
                    if (hasPaid(name)) {
                        Exhibitions.showExhibitions(sc);
                        done = true;
                    } else {
                        System.out.println("\"Pay the entrance fee first for this access\"");
                    }
                } else {
                    System.out.println("Information Desk Management");
                    System.out.println("Staff/Admin access granted.");
                    Exhibitions.showExhibitions(sc);
                    done = true;
                }
            } else if (choice == 3) {
                if (role.equals("user") && !hasPaid(name)) {
                    System.out.println("\"Pay the entrance fee first for this access\"");
                } else {
                    GiftShop.showGiftShop(sc);
                }
            } else if (choice == 4) {
                if (role.equals("user") && !hasPaid(name)) {
                    System.out.println("\"Pay the entrance fee first for this access\"");
                } else {
                    Cafe.showCafe(sc);
                }
            } else if (choice == 5 && (role.equals("staff") || role.equals("admin"))) {
                CareAndMaintenance.showCareAndMaintenance(sc, role, name);
            } else if (choice == 6 && role.equals("admin")) {
                DataAnalytics.showDataAnalytics(sc);
            } else if (choice == 7 && role.equals("admin")) {
                AccountManagement.showAccountManagement(sc);
            } else if (choice == 0) {
                done = true;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        System.out.println("Thank you!");
        
    }

    // Reads visitors.txt and checks if the name is in it
    public static boolean hasPaid(String name) throws IOException {
        Scanner file = new Scanner(new File("visitors.txt"));
        boolean found = false;

        while (file.hasNextLine()) {
            String line = file.nextLine();
            if (line.equalsIgnoreCase(name)) {
                found = true;
            }
        }

        return found;
        
    }
}
