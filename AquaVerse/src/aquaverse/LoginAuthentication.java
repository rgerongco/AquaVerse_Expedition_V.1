package aquaverse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LoginAuthentication {
    
    //Creation of the Accounts File
    static String FILE_NAME = "accounts.txt";
    
    //To know if USER? STAFF? ADMIN?
    static String loggedRole = "";

    //Checking if username already exists
    static boolean nameExists(String name) {
        
        //ADMIN and STAFF account already pre-defined for 
        //role-based but will change once account management is here
        if (name.equals("admin")) {
            return true;
        }
        
        if (name.equals("staff")) {
            return true;
        }
        
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return false;
        }
        
        //Reading the notepad if there's an existing username
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
            System.out.println("Could not read " + FILE_NAME);
        }
        return false;
    }

    //Checks if the username and password are inside of the file
    static String checkLogin(String name, String password) {
        //Pre-defined ADMIN and STAFF
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
        
        //Reading the notepad to check if there's an existing name with its password
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals(name) && parts[1].equals(password)) {
                    reader.close();
                    return parts[3];
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME);
        }
        return "";
    }

    // Saving a new account
    static void saveAccount(String name, String password, String phone) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(name + "," + password + "," + phone + ",user\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save to " + FILE_NAME);
        }
    }

    //Runs the login
    public static String login(Scanner sc) {

        boolean loggedIn = false;
        String loggedName = "";

        while (!loggedIn) {

            System.out.println("=== AquaVerse Expedition ===");
            System.out.println("[1] Login");
            System.out.println("[2] Sign Up");
            System.out.println("[0] Exit");
            System.out.println("============================");
            String answer = sc.nextLine();

            if (answer.equals("0")) {
                break;
            }
            
            else if (answer.equals("2")) {

                // ---------- SIGN UP ----------
                boolean created = false;
                while (!created) {
                    System.out.println("=== Sign Up ===");
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Password: ");
                    String password = sc.nextLine();
                    System.out.print("Confirm password: ");
                    String confirm = sc.nextLine();
                    System.out.print("Phone number: ");
                    String phone = sc.nextLine();
                    System.out.println("===============");

                    if (name.isEmpty() || password.isEmpty() || confirm.isEmpty() || phone.isEmpty()) {
                        System.out.println("All fields must be filled");
                    } else if (!password.equals(confirm)) {
                        System.out.println("Passwords don't match");
                    } else if (password.length() < 8) {
                        System.out.println("Passwords should be atleast 8 characters");
                    } else if (phone.length() < 11 || phone.length() > 11) {
                        System.out.println("Phone number should be exactly 11 numbers");
                    } else if (nameExists(name)) {
                        System.out.println("Account already exists. Please create another one.");
                    } else {
                        saveAccount(name, password, phone);
                        System.out.println("Account Created Successfully");
                        created = true;
                    }
                }

            } else if (answer.equals("1")) {

                System.out.println("=== Login ===");
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                System.out.println("=============");

                String role = checkLogin(name, password);
                if (!role.equals("")) {
                    System.out.println("Login Successful");
                    
                    loggedRole = role;
                    
                    if (role.equals("admin")) {
                        System.out.println("=== Welcome to the Admin Dashboard ===");
                    } else if (role.equals("staff")) {
                        System.out.println("=== Welcome to the Staff Dashboard ===");
                    } else {
                        System.out.println("=== Welcome to the AquaVerse Expedition ===");
                    }

                    loggedName = name;
                    loggedIn = true;
                    
                } else {
                    System.out.println("Wrong username or password");
                }
            } else {
                System.out.println("Please enter the value of your choice.");
            }
        }
        
        return loggedName;
        
    }
}
