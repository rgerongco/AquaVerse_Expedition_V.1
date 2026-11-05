package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LoginAuthentication {

    // creating the accounts file
    static String FILE_NAME = "accounts.txt";

    // to check if user/staff/admin
    static String loggedRole = "";

    // checking if the name already existed
    static boolean nameExists(String name) {
        // pre-defined admin
        if (name.equals("admin")) {
            return true;
        }
        // pre-defined staff
        if (name.equals("staff")) {
            return true;
        }

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return false;
        }

        // reading the notepad if there's an existing username
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals(name)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("- Could not read " + FILE_NAME);
        }
        return false;
    }

    // checks if the username and password are inside of the file
    static String checkLogin(String name, String password) {
        // pre-defined admin and staff
        if (name.equals("admin") && password.equals("admin123")) {
            return "admin";
        }

        if (name.equals("staff") && password.equals("staff123")) {
            return "staff";
        }

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return "";
        }

        // reading the notepad to check if there's an existing name with its password
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(name) && parts[1].equals(password)) {
                    reader.close();
                    return parts[3];
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("- Could not read " + FILE_NAME);
        }
        return "";
    }

    // save new account
    static void saveAccount(String name, String password, String phone) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(name + "," + password + "," + phone + ",user\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("- Could not save to " + FILE_NAME);
        }
    }

    // run login
    public static String login(Scanner sc) {

        boolean loggedIn = false;
        String loggedName = "";

        while (!loggedIn) {

            System.out.println("---------------------------------");
            System.out.println("|       AQUAVERSE EXPEDITION    |");
            System.out.println("---------------------------------");
            System.out.println("|\t[1] Login\t\t|");
            System.out.println("|\t[2] Sign Up\t\t|");
            System.out.println("|\t[0] Exit\t\t|");
            System.out.println("---------------------------------");
            System.out.print("\tChoose: ");
            String answer = sc.nextLine();

            if (answer.equals("0")) {
                break;
            }

            else if (answer.equals("2")) {

                // sign up dashboard
                boolean created = false;
                while (!created) {
                    System.out.println("--------------------------------");
                    System.out.println("|            SIGN UP            |");
                    System.out.println("--------------------------------");
                    System.out.print("\tFirst Name: ");
                    String fname = sc.nextLine();
                    System.out.print("\tLast Name: ");
                    String lname = sc.nextLine();
                    System.out.print("\tPassword: ");
                    String password = sc.nextLine();
                    System.out.print("\tConfirm Password: ");
                    String confirm = sc.nextLine();
                    System.out.print("\tPhone Number: ");
                    String phone = sc.nextLine();
                    System.out.println("--------------------------------");
                    String name = fname + " " + lname;

                    if (name.isEmpty() || password.isEmpty() || confirm.isEmpty() || phone.isEmpty()) {
                        System.out.println("- All fields must be filled");
                    } else if (!password.equals(confirm)) {
                        System.out.println("- Passwords don't match");
                    } else if (password.length() < 8) {
                        System.out.println("- Passwords should be atleast 8 characters");
                    } else if (phone.length() < 11 || phone.length() > 11) {
                        System.out.println("- Phone number should be exactly 11 numbers");
                    } else if (nameExists(name)) {
                        System.out.println("- Account already exists. Please create another one.");
                    } else {
                        saveAccount(name, password, phone);
                        System.out.println("- Account Created Successfully");
                        created = true;
                    }
                }

            } else if (answer.equals("1")) {
                
                // login dashboard
                System.out.println("--------------------------------");
                System.out.println("|             LOGIN            |");
                System.out.println("--------------------------------");
                System.out.print("\tName: ");
                String name = sc.nextLine();
                System.out.print("\tPassword: ");
                String password = sc.nextLine();
                System.out.println("--------------------------------");

                // role-based login dashboard
                String role = checkLogin(name, password);
                if (!role.equals("")) {
                    System.out.println("- Login Successful");

                    loggedRole = role;

                    if (role.equals("admin")) {
                        System.out.println("---------------------------------------");
                        System.out.println("|   WELCOME TO THE ADMIN DASHBOARD    |");
                        System.out.println("---------------------------------------");
                    } else if (role.equals("staff")) {
                        System.out.println("---------------------------------------");
                        System.out.println("|   WELCOME TO THE STAFF DASHBOARD    |");
                        System.out.println("---------------------------------------");
                    } else {
                        System.out.println("---------------------------------------");
                        System.out.println("|   WELCOME TO AQUAVERSE EXPEDITION   |");
                        System.out.println("---------------------------------------");
                    }

                    loggedName = name;
                    loggedIn = true;

                } else {
                    System.out.println("- Wrong username or password");
                }
            } else {
                System.out.println("- Please enter the value of your choice.");
            }
        }

        return loggedName;

    }
}
