package aquaverse;

import java.util.Scanner;

// Cafe flowchart - basic version (multiple items, each with a quantity)
public class Cafe {

    // creation of cafe
    static String FILE_NAME = "cafe.txt";

    // The visitor's order (cart)
    static String[] cartNames = new String[100];
    static double[] cartPrices = new double[100];
    static int[] cartQty = new int[100];
    static int cartCount = 0;

    // checking out
    static boolean checkout(Scanner sc) {

        // computation of the total of all items
        double total = 0;
        for (int i = 0; i < cartCount; i++) {
            total += cartPrices[i] * cartQty[i];
        }

        while (true) {

            // Show order
            System.out.println();
            System.out.println("-------------------------YOUR ORDER-------------------------");

            for (int i = 0; i < cartCount; i++) {
                System.out.printf("| %-56.56s |%n",
                        cartQty[i] + " x " + cartNames[i] + " - "
                        + String.format("%.2f", cartPrices[i] * cartQty[i]));
            }

            System.out.println("|----------------------------------------------------------|");
            System.out.printf("| %-56.56s |%n", "TOTAL: " + String.format("%.2f", total));
            System.out.println("------------------------------------------------------------");

            String accept = DataFile.ask(sc, "Accept purchase? (yes/no): ");

            if (!accept.equalsIgnoreCase("yes")) {
                return false;
            }

            // payment method
            System.out.println();
            System.out.println("-----------------------PAYMENT METHOD-----------------------");
            System.out.println("| [1] Cash                                                 |");
            System.out.println("| [2] Credit                                               |");
            System.out.println("| [3] Online                                               |");
            System.out.println("------------------------------------------------------------");
            String method = DataFile.ask(sc, "Choose payment method (1-3): ");
            String paymentMethod = "";

            if (method.equals("1")) {
                paymentMethod = "Cash";
            } else if (method.equals("2")) {
                paymentMethod = "Credit";
            } else if (method.equals("3")) {
                paymentMethod = "Online";
            } else {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|                 Invalid payment method.                  |");
                System.out.println("------------------------------------------------------------");
                continue;
            }

            double amount = DataFile.toDouble(DataFile.ask(sc, "Enter payment amount: "));

            // insufficient funds
            if (amount >= total) {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|        Purchase Complete. Thank you for shopping         |");
                System.out.println("------------------------------------------------------------");
                DataAnalytics.recordSale("Cafe", total);

                // take the ordered items out of the stock
                for (int i = 0; i < cartCount; i++) {
                    InventoryManagement.reduceStock("Cafe", cartNames[i], cartQty[i]);
                }

                // generate receipt
                System.out.println();
                System.out.println("--------------------------RECEIPT---------------------------");

                for (int i = 0; i < cartCount; i++) {
                    System.out.printf("| %-56.56s |%n",
                            cartQty[i] + " x " + cartNames[i]
                            + " @ " + String.format("%.2f", cartPrices[i])
                            + " = " + String.format("%.2f", cartPrices[i] * cartQty[i]));
                }

                System.out.println("|----------------------------------------------------------|");
                System.out.printf("| %-56.56s |%n", "Total:  " + String.format("%.2f", total));
                System.out.printf("| %-56.56s |%n", "Paid:   " + String.format("%.2f", amount) + " via " + paymentMethod);
                System.out.printf("| %-56.56s |%n", "Change: " + String.format("%.2f", amount - total));
                System.out.println("------------------------------------------------------------");

                return true;

            } else {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|                    Insufficient Funds                    |");
                System.out.println("------------------------------------------------------------");

                // cancel transaction?
                String cancel = DataFile.ask(sc, "Cancel transaction? (yes/no): ");

                if (cancel.equalsIgnoreCase("yes")) {
                    return false; // YES -> back to Cafe
                }
            }
        }
    }

    public static void showCafe(Scanner sc) {
        // read all items come from the notepad file
        String[][] rows = DataFile.load(FILE_NAME, 4);

        if (rows.length == 0) {
            System.out.println("\n- Could not load the cafe menu from " + FILE_NAME);
            return;
        }

        String[] categories = DataFile.distinct(rows, 0);
        cartCount = 0;

        boolean done = false;

        while (!done) {

            System.out.println();
            System.out.println("----------------------------CAFE----------------------------");

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + categories[i]);
            }

            System.out.println("| [0] Exit                                                 |");
            System.out.println("------------------------------------------------------------");

            int choice = DataFile.toInt(DataFile.ask(sc, "Choose: "));

            // pick 0, leave cafe
            if (choice == 0) {
                System.out.println("\n- Leaving the Cafe...");
                return;
            }

            // pick other than the choices, invalid
            if (choice < 1 || choice > categories.length) {
                System.out.println("\n- Invalid choice.");
                continue;
            }

            String category = categories[choice - 1];
            String[][] items = DataFile.filter(rows, 0, category);

            // show the items with prices
            System.out.println();
            System.out.println("----------------------------MENU----------------------------");
            System.out.printf("| %-56.56s |%n", category.toUpperCase());
            System.out.println("|----------------------------------------------------------|");

            String lastGroup = "";

            for (int i = 0; i < items.length; i++) {

                // items with a group (like Coffee or Tea) get the group name shown above them
                if (!items[i][1].equals("-") && !items[i][1].equals(lastGroup)) {
                    System.out.printf("| %-56.56s |%n", "[" + items[i][1] + "]");
                    lastGroup = items[i][1];
                }

                String stockNote = "";

                if (InventoryManagement.getStock("Cafe", items[i][2]) <= 0) {
                    stockNote = "  (OUT OF STOCK)";
                }

                System.out.printf("| %-56.56s |%n",
                        "- [" + (i + 1) + "] " + items[i][2]
                        + " - " + String.format("%.2f", DataFile.toDouble(items[i][3])) + stockNote);
            }
            System.out.println("| - [0] Exit                                               |");
            System.out.println("------------------------------------------------------------");

            // Does the visitor order?
            int itemNum = DataFile.toInt(DataFile.ask(sc,
                    "Choose: ")) - 1;

            if (itemNum < 0 || itemNum >= items.length) {

                if (cartCount == 0) {
                    continue;
                }

                // already has items in the order -> go to checkout
                done = checkout(sc);

                if (!done) {
                    cartCount = 0; // order cancelled, clear it
                }

                continue;
            }

            double price = DataFile.toDouble(items[itemNum][3]);

            if (price < 0) {
                System.out.println("\n- This item has no valid price in " + FILE_NAME);
                continue;
            }

            // check the stock (what is already in the order counts too)
            int inOrder = 0;

            for (int i = 0; i < cartCount; i++) {
                if (cartNames[i].equals(items[itemNum][2])) {
                    inOrder += cartQty[i];
                }
            }

            int available = InventoryManagement.getStock("Cafe", items[itemNum][2]) - inOrder;

            if (available <= 0) {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.printf("| %-56.56s |%n", "Sorry, " + items[itemNum][2] + " is out of stock.");
                System.out.println("------------------------------------------------------------");
                continue;
            }

            // checking the amount wanted
            int qty = 0;

            while (qty < 1 || qty > 10 || qty > available) {

                qty = DataFile.toInt(DataFile.ask(sc,
                        "How many amount do you want for this item? (1-10): "));

                if (qty < 1 || qty > 10) {
                    System.out.println("\n- Please enter a number from 1 to 10");

                } else if (qty > available) {
                    System.out.println("\n- Only " + available + " left in stock");
                }
            }

            // add to the order
            cartNames[cartCount] = items[itemNum][2];
            cartPrices[cartCount] = price;
            cartQty[cartCount] = qty;
            cartCount++;

            System.out.println("\n- Added: " + qty + " x " + items[itemNum][2]);

            // ask if order more items
            String more = DataFile.ask(sc, "Order more items? (yes/no): ");

            if (more.equalsIgnoreCase("yes")) {
                continue; // back to Cafe
            }

            done = checkout(sc);

            if (!done) {
                cartCount = 0; // order cancelled, clear it
            }
        }

        // ending
        System.out.println("\n- Leaving the Cafe...");
    }
}
