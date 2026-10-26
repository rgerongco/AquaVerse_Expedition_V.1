package aquaverse;

import java.io.*;
import java.util.Scanner;

public class InformationDesk {

    // Ticket purchase and payment.
    // Returns true if the payment was successful, false if the transaction was cancelled.
    public static boolean buyTickets(Scanner sc, String accountName) throws IOException {

        System.out.println("=== INFORMATION DESK ===");
        System.out.println("Account Name: " + accountName);

        //NUMBER OF TICKET/S TO PURCHASE
        System.out.print("Enter number/s of ticket to purchase: ");
        int ticketNumber = sc.nextInt();
        sc.nextLine();

        //DATE TO BOOK
        
        boolean date = false;
        String dateToBook = "";
        
        while (!date) {
            System.out.print("Enter date to book (e.g., MM-DD-YYYY): ");
            dateToBook = sc.nextLine();
                if (dateToBook.length() == 8) {
                    date = true;
                    break;
                } else {
                    System.out.println("Must exactly be 8 numbers.");
                }
        }

        //DECISION
        //ANY KIDS UNDER 10/SENIOR/PWD WILL HAVE DISCOUNT, ELSE REGULAR PRICE OF TICKET WILL BE SHOWN
        System.out.print("Are there any kids under 10 / Senior / PWD? (yes/no): ");
        String discountRequirement = sc.nextLine().toLowerCase();

        double regularTicketPrice = 150.00;
        double totalPriceOfTicket = ticketNumber * regularTicketPrice;

        boolean discountApplicable = discountRequirement.equals("yes");

        if (discountApplicable) {
            System.out.println("Apply 20% Discount");
            //GET THE 20% OF THE 150
            double discountedTicketPrice = regularTicketPrice * 0.20;
            //SUBTRACT FROM THE REGULAR PRICE
            totalPriceOfTicket = totalPriceOfTicket - discountedTicketPrice;
        }

        //TOTAL PRICE SHOWN
        System.out.println("\nTotal Price: PHP " + totalPriceOfTicket);

        //INPUT PAYMENT METHOD
        boolean pay = false;
        String paymentMethod = "";
        while (!pay) {
            System.out.println("\n<===PAYMENT METHOD===>");
            System.out.println("[1] Cash");
            System.out.println("[2] Credit");
            System.out.println("[3] Online");
            System.out.println("[0] Exit Payment");
            System.out.print("Choose payment method: ");
            paymentMethod = sc.nextLine();

            if (paymentMethod.equals("1") || paymentMethod.equals("") || paymentMethod.equals("3")) {
                pay = true;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        //INPUT PAYMENT
        System.out.print("Enter payment amount: PHP ");
        double paymentAmount = sc.nextDouble();

        //DECISION
        //IS PAYMENT ENOUGH?
        while (paymentAmount < totalPriceOfTicket) {

            //INSUFFICIENT FUNDS
            System.out.println("Insufficient funds");

            //CANCEL TRANSACTION?
            System.out.print("Cancel transaction? (yes/no): ");
            String cancelTransaction = sc.next().toLowerCase();

            if (cancelTransaction.equals("yes") || paymentMethod.equals("0")) {
                //BACK TO INFORMATION DESK
                System.out.println("Transaction Cancelled");
                System.out.println("Back to Information Desk");
                return false;
            } else {
                //BACK TO TOTAL PRICE SHOWN
                System.out.println("Total Price: PHP " + totalPriceOfTicket);
                System.out.print("Enter payment amount: PHP ");
                paymentAmount = sc.nextDouble();
            }
        }

        //PAYMENT SUCCESSFUL
        System.out.println("\nPayment Successful");

        //GENERATE RECEIPT
        System.out.println("\n<======RECEIPT======>");
        System.out.println("Account Name: " + accountName);
        System.out.println("Number of Tickets: " + ticketNumber);
        System.out.println("Date Booked: " + dateToBook);
        System.out.println("Total Price: PHP " + totalPriceOfTicket);
        System.out.println("Amount Paid: PHP " + paymentAmount);
        System.out.println("Change: PHP " + (paymentAmount - totalPriceOfTicket));
        System.out.println("<===================>");

        // Save the visitor's name in visitors.txt so the Dashboard knows they paid
        // true = add to the end of the file instead of overwriting it
        FileWriter out = new FileWriter("visitors.txt", true);
        out.write(accountName + "\n");
        out.close();

        return true;
    }
}
