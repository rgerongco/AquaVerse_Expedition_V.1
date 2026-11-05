package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AccountManagement {

    // same file that LoginAuthentication uses
    static String FILE_NAME = "accounts.txt";

    // read all accounts from the notepad file
    static String[] readAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new String[0];
        }

        try {
            // count the number of accounts in the file
            int count = 0;
            Scanner counter = new Scanner(file);
            while (counter.hasNextLine()) {
                if (!counter.nextLine().trim().isEmpty()) {
                    count++;
                }
            }
            counter.close();

            // place all accounts inside the array
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

    // save all accounts to the notepad file except the selected one
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

    // save a new account to the notepad file
    static void saveNew(String line) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + FILE_NAME);
        }
    }

    // show the account management menu
    public static void showAccountManagement(Scanner sc) {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("---------------------ACCOUNT MANAGEMENT---------------------");
            System.out.println("| [1] View Accounts                                        |");
            System.out.println("| [2] Create Staff Account                                 |");
            System.out.println("| [3] Update Account                                       |");
            System.out.println("| [4] Delete Account                                       |");
            System.out.println("| [0] Back                                                 |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose: ");

            String answer = sc.nextLine().trim();

            // choose an account management action
            if (answer.equals("1")) {
                viewAccounts();
            } else if (answer.equals("2")) {
                createStaff(sc);
            } else if (answer.equals("3")) {
                updateAccount(sc);
            } else if (answer.equals("4")) {
                deleteAccount(sc);
            } else if (answer.equals("0")) {
                back = true;
            } else {
                System.out.println("Please enter the value of your choice.");
            }
        }
    }

    // show all registered accounts
    static void viewAccounts() {
        String[] lines = readAccounts();

        System.out.println();
        System.out.println("--------------------REGISTERED ACCOUNTS---------------------");
        System.out.println("| - admin (admin) - built-in account                       |");
        System.out.println("| - staff (staff) - built-in account                       |");

        // display the account details
        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(",");
            if (p.length < 4) {
                continue;
            }

            System.out.println("|----------------------------------------------------------|");
            System.out.printf("| %-56.56s |%n", "- " + p[0] + " (" + p[3] + ")");
            System.out.printf("| %-56.56s |%n", "    Phone: " + p[2]);

            // show additional details for staff accounts
            if (p.length >= 7) {
                System.out.printf("| %-56.56s |%n", "    Staff ID: " + p[4] + " | Age: " + p[5]);
                System.out.printf("| %-56.56s |%n", "    Email: " + p[6]);
            }
        }

        System.out.println("------------------------------------------------------------");
    }

    // create a new staff account
    static void createStaff(Scanner sc) {
        boolean created = false;

        while (!created) {
            System.out.println();
            System.out.println("--------------------CREATE STAFF ACCOUNT--------------------");
            System.out.print("Staff ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Age: ");
            String age = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();
            System.out.print("Phone number: ");
            String phone = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            System.out.println("------------------------------------------------------------");

            // check if the entered details are valid
            String error = checkDetails(name, age, password, phone, email, true);

            // check if the staff ID or account name already exists
            if (error.isEmpty() && (id.isEmpty() || id.contains(","))) {
                error = "Staff ID must be filled and cannot have commas";
            } else if (error.isEmpty() && idExists(readAccounts(), id)) {
                error = "Staff ID already exists";
            } else if (error.isEmpty() && LoginAuthentication.nameExists(name)) {
                error = "Account already exists. Please create another one.";
            }

            if (!error.isEmpty()) {
                System.out.println(error);

                // ask if the user wants to try again
                if (!askAgain(sc)) {
                    return;
                }
            } else {
                // confirm the account creation
                System.out.print("Create account? (yes/no): ");
                String confirm = sc.nextLine().trim();

                if (confirm.equalsIgnoreCase("yes")) {
                    saveNew(name + "," + password + "," + phone + ",staff," + id + "," + age + "," + email);
                    System.out.println("Account Created");
                    created = true;
                } else if (!askAgain(sc)) {
                    return;
                }
            }
        }
    }

    // update the details of an existing account
    static void updateAccount(Scanner sc) {
        System.out.println();
        System.out.println("-----------------------UPDATE ACCOUNT-----------------------");
        System.out.print("Name of the account to update: ");
        String name = sc.nextLine().trim();

        // built-in accounts cannot be updated
        if (name.equals("admin") || name.equals("staff")) {
            System.out.println("Built-in accounts cannot be updated here.");
            return;
        }

        String[] lines = readAccounts();
        int index = findAccount(lines, name);

        if (index == -1) {
            System.out.println("Account not found.");
            return;
        }

        String[] p = lines[index].split(",");
        boolean isStaff = p.length >= 7 && p[3].equals("staff");

        // ask for the new account details
        System.out.println("Leave a field blank to keep the current value.");

        System.out.print("New name [" + p[0] + "]: ");
        String newName = sc.nextLine().trim();

        System.out.print("New password [hidden]: ");
        String newPassword = sc.nextLine().trim();

        System.out.print("New phone number [" + p[2] + "]: ");
        String newPhone = sc.nextLine().trim();

        String newAge = "";
        String newEmail = "";

        // ask for the additional details if it is a staff account
        if (isStaff) {
            System.out.print("New age [" + p[5] + "]: ");
            newAge = sc.nextLine().trim();

            System.out.print("New email [" + p[6] + "]: ");
            newEmail = sc.nextLine().trim();
        }

        // keep the current value if the field is left blank
        if (newName.isEmpty()) newName = p[0];
        if (newPassword.isEmpty()) newPassword = p[1];
        if (newPhone.isEmpty()) newPhone = p[2];

        if (isStaff) {
            if (newAge.isEmpty()) newAge = p[5];
            if (newEmail.isEmpty()) newEmail = p[6];
        }

        // check if the updated details are valid
        String error = checkDetails(newName, newAge, newPassword, newPhone, newEmail, isStaff);

        // check if the new account name is already taken
        if (error.isEmpty() && !newName.equals(p[0]) && LoginAuthentication.nameExists(newName)) {
            error = "Account already exists. Please use another name.";
        }

        if (!error.isEmpty()) {
            System.out.println(error);
            return;
        }

        // confirm the account update
        System.out.println("------------------------------------------------------------");
        System.out.print("Update account? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            String newLine = newName + "," + newPassword + "," + newPhone + "," + p[3];

            // include the additional details for staff accounts
            if (isStaff) {
                newLine = newLine + "," + p[4] + "," + newAge + "," + newEmail;
            }

            lines[index] = newLine;
            saveAll(lines, -1);

            System.out.println("Account Updated");
        } else {
            System.out.println("Update cancelled.");
        }
    }

    // delete an existing account
    static void deleteAccount(Scanner sc) {
        System.out.println();
        System.out.println("-----------------------DELETE ACCOUNT-----------------------");
        System.out.print("Name of the account to delete: ");
        String name = sc.nextLine().trim();

        // built-in accounts cannot be deleted
        if (name.equals("admin") || name.equals("staff")) {
            System.out.println("Built-in accounts cannot be deleted.");
            return;
        }

        String[] lines = readAccounts();
        int index = findAccount(lines, name);

        if (index == -1) {
            System.out.println("Account not found.");
            return;
        }

        // confirm the account deletion
        System.out.println("------------------------------------------------------------");
        System.out.print("Delete " + name + "? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            saveAll(lines, index);   // saves everything except this account
            System.out.println("Account Deleted");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // find the account in the array
    static int findAccount(String[] lines, String name) {
        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(",");

            if (p[0].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    // check if the staff ID already exists
    static boolean idExists(String[] lines, String id) {
        for (int i = 0; i < lines.length; i++) {
            String[] p = lines[i].split(",");

            if (p.length >= 7 && p[4].equals(id)) {
                return true;
            }
        }
        return false;
    }

    // check if the input contains whole numbers only
    static boolean isWholeNumber(String text) {
        if (text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ask if the user wants to try again
    static boolean askAgain(Scanner sc) {
        System.out.print("Try again? (yes/no): ");
        return sc.nextLine().trim().equalsIgnoreCase("yes");
    }

    // check if all account details are valid
    static String checkDetails(String name, String age, String password,
                               String phone, String email, boolean isStaff) {

        // check if the required fields are filled
        if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            return "All fields must be filled";
        }

        if (isStaff && (age.isEmpty() || email.isEmpty())) {
            return "All fields must be filled";
        }

        // check if the details contain commas
        if (name.contains(",") || password.contains(",") || phone.contains(",")
                || age.contains(",") || email.contains(",")) {
            return "Commas are not allowed";
        }

        // check the password length
        if (password.length() < 8) {
            return "Passwords should be atleast 8 characters";
        }

        // check the phone number
        if (phone.length() != 11 || !isWholeNumber(phone)) {
            return "Phone number should be exactly 11 numbers";
        }

        // check the staff age and email
        if (isStaff) {
            if (!isWholeNumber(age) || age.length() > 3
                    || Integer.parseInt(age) < 18 || Integer.parseInt(age) > 100) {
                return "Age must be a number from 18 to 100";
            }

            if (!email.contains("@")) {
                return "Email must contain @";
            }
        }

        return "";
    }
}
