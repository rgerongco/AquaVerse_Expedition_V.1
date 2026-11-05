package aquaverse;

import java.io.*;
import java.util.Scanner;

public class InformationDesk {

    // reads a whole number
    static int askInt(Scanner sc, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            int value = DataFile.toInt(sc.nextLine().trim());
            if (value >= min) {
                return value;
            }
            System.out.println("\n- Please enter a valid number.");
        }
    }

    // reads an amount
    static double askAmount(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            double value = DataFile.toDouble(sc.nextLine().trim());
            if (value >= 0) {
                return value;
            }
            System.out.println("\n- Please enter a valid amount.");
        }
    }

    // ticket purchase and payment
    public static boolean buyTickets(Scanner sc, String accountName) {

        System.out.println();
        System.out.println("----------------------INFORMATION DESK----------------------");
        System.out.printf("| %-56.56s |%n", "Account Name: " + accountName);
        System.out.println("------------------------------------------------------------");

        // number of tickets to purchase
        int ticketNumber = askInt(sc, "Enter number/s of ticket to purchase: ", 1);

        // booking date
        boolean date = false;
        String dateToBook = "";

        while (!date) {
            System.out.print("Enter date to book (e.g., MM-DD-YYYY): ");
            dateToBook = sc.nextLine().trim();
            boolean valid = dateToBook.length() == 10;

            for (int i = 0; valid && i < dateToBook.length(); i++) {
                if (i == 2 || i == 5) {
                    if (dateToBook.charAt(i) != '-') {
                        valid = false;
                    }
                } else if (!Character.isDigit(dateToBook.charAt(i))) {
                    valid = false;
                }
            }

            if (valid) {
                date = true;
            } else {
                System.out.println("\n- Date must follow MM-DD-YYYY, for example 11-04-2026");
            }
        }

        // checking for discount
        System.out.print("Are there any kids under 10 / Senior / PWD? (yes/no): ");
        String discountRequirement = sc.nextLine().trim().toLowerCase();

        double regularTicketPrice = 150.00;
        boolean discountApplicable = discountRequirement.equals("yes");

        // how many tickets are available for the discount
        int discountedTickets = 0;

        if (discountApplicable) {
            boolean countValid = false;

            while (!countValid) {
                discountedTickets = askInt(sc, "How many of the " + ticketNumber
                        + " ticket/s are for kids under 10 / Senior / PWD? ", 1);

                if (discountedTickets > ticketNumber) {
                    System.out.println("\n- It cannot be more than the number of tickets (" + ticketNumber + ").");
                } else {
                    countValid = true;
                }
            }
        }

        // discounted tickets + regular
        int regularTickets = ticketNumber - discountedTickets;
        double regularSubtotal = regularTickets * regularTicketPrice;
        double discountedSubtotal = discountedTickets * regularTicketPrice;

        double discountAmount = discountedSubtotal * 0.20;

        // total price of the tickets
        double totalPriceOfTicket = regularSubtotal + discountedSubtotal - discountAmount;

        if (discountApplicable) {
            System.out.println("\n- Apply 20% Discount to " + discountedTickets + " ticket/s");
        }

        // total price
        System.out.println();
        System.out.println("------------------------TOTAL PRICE-------------------------");

        if (discountApplicable) {
            System.out.printf("| %-56.56s |%n", "Regular Ticket/s: " + regularTickets + " x PHP "
                    + String.format("%.2f", regularTicketPrice) + " = PHP " + String.format("%.2f", regularSubtotal));

            System.out.printf("| %-56.56s |%n", "Discounted Ticket/s: " + discountedTickets + " x PHP "
                    + String.format("%.2f", regularTicketPrice) + " = PHP " + String.format("%.2f", discountedSubtotal));

            System.out.printf("| %-56.56s |%n", "20% Discount: - PHP " + String.format("%.2f", discountAmount));

            System.out.println("|----------------------------------------------------------|");
        }

        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + String.format("%.2f", totalPriceOfTicket));
        System.out.println("------------------------------------------------------------");

        // payment method
        boolean pay = false;
        String paymentMethod = "";

        while (!pay) {
            System.out.println();
            System.out.println("-----------------------PAYMENT METHOD-----------------------");
            System.out.println("| [1] Cash                                                 |");
            System.out.println("| [2] Credit                                               |");
            System.out.println("| [3] Online                                               |");
            System.out.println("| [0] Exit Payment                                         |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose payment method: ");
            paymentMethod = sc.nextLine().trim();

            if (paymentMethod.equals("0")) {
                System.out.println("\n- Transaction Cancelled");
                System.out.println("- Back to Information Desk");
                return false;
            } else if (paymentMethod.equals("1") || paymentMethod.equals("2") || paymentMethod.equals("3")) {
                pay = true;
            } else {
                System.out.println("\n- Invalid choice.");
            }
        }

        double paymentAmount = askAmount(sc, "Enter payment amount: PHP ");
        
        // payment decision
        while (paymentAmount < totalPriceOfTicket) {

            // insufficient funds
            System.out.println("\n- Insufficient funds");

            // cancel transaction
            System.out.print("Cancel transaction? (yes/no): ");
            String cancelTransaction = sc.nextLine().trim().toLowerCase();

            if (cancelTransaction.equals("yes")) {
                // back to information desk
                System.out.println("\n- Transaction Cancelled");
                System.out.println("- Back to Information Desk");
                return false;
            } else {
                // back to total price
                System.out.println("\nTotal Price: PHP " + String.format("%.2f", totalPriceOfTicket));
                paymentAmount = askAmount(sc, "Enter payment amount: PHP ");
            }
        }

        // success of payment
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("|                    PAYMENT SUCCESSFUL                    |");
        System.out.println("------------------------------------------------------------");

        // generation of receipt
        System.out.println();
        System.out.println("--------------------------RECEIPT---------------------------");
        System.out.printf("| %-56.56s |%n", "Account Name: " + accountName);
        System.out.printf("| %-56.56s |%n", "Number of Tickets: " + ticketNumber);

        if (discountApplicable) {
            System.out.printf("| %-56.56s |%n", "Regular Ticket/s: " + regularTickets);
            System.out.printf("| %-56.56s |%n", "Discounted Ticket/s: " + discountedTickets);
            System.out.printf("| %-56.56s |%n", "20% Discount: PHP " + String.format("%.2f", discountAmount));
        }

        System.out.printf("| %-56.56s |%n", "Date Booked: " + dateToBook);
        System.out.println("|----------------------------------------------------------|");
        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + String.format("%.2f", totalPriceOfTicket));
        System.out.printf("| %-56.56s |%n", "Amount Paid: PHP " + String.format("%.2f", paymentAmount));
        System.out.printf("| %-56.56s |%n", "Change: PHP " + String.format("%.2f", paymentAmount - totalPriceOfTicket));
        System.out.println("|----------------------------------------------------------|");
        System.out.println("|                  TRANSACTION COMPLETE!                   |");
        System.out.println("------------------------------------------------------------");

        // save the visitor's name in visitors.txt
        try {
            FileWriter out = new FileWriter("visitors.txt", true);
            out.write(accountName + "\n");
            out.close();
        } catch (IOException e) {
            System.out.println("\n- Could not save your payment to visitors.txt");
        }

        return true;
    }
}
