package aquaverse;

import java.io.IOException;
import java.util.Scanner;

// Gift Shop flowchart - multiple items, each with a quantity of 1
public class GiftShop {

    // creation of gift shop
    static String FILE_NAME = "giftshop.txt";

    // formats any amount of money to exactly 2 decimal places (e.g. 150 -> 150.00)
    static String money(double amount) {
        return String.format("%.2f", amount);
    }

    static void showGiftShop(Scanner input) throws IOException {

        // read all items from the notepad file
        String[][] rows = DataFile.load(FILE_NAME, 4);

        if (rows.length == 0) {
            System.out.println("\n- Could not load the gift shop items from " + FILE_NAME);
            return;
        }

        String[] categories = DataFile.distinct(rows, 0);

        System.out.println();
        System.out.println("-------------------------GIFT SHOP--------------------------");
        System.out.println("|              AQUAVERSE EXPEDITION GIFT SHOP              |");
        System.out.println("------------------------------------------------------------");

        // The visitor's order (cart)
        String[] cart = new String[5];
        double[] cartPrices = new double[5];
        int cartCount = 0;
        boolean shoppingActive = true;

        while (shoppingActive && cartCount < cart.length) {

            // show the gift shop categories
            System.out.println();
            System.out.println("-------------------------GIFT SHOP--------------------------");
            System.out.println("| Main Categories:                                         |");

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + categories[i]);
            }

            System.out.println("| [0] Exit                                                 |");
            System.out.println("------------------------------------------------------------");

            int categoryChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

            // pick 0, leave gift shop
            if (categoryChoice == 0) {
                return;
            }

            // pick other than the choices, invalid
            if (categoryChoice < 1 || categoryChoice > categories.length) {
                System.out.println("\n- Invalid category selection.");
                continue;
            }

            String category = categories[categoryChoice - 1];
            String[][] items = DataFile.filter(rows, 0, category);

            // categories with groups (sub-categories) get another menu
            String[] groups = DataFile.distinct(items, 1);

            if (!groups[0].equals("-")) {
                int groupChoice;

                // an invalid choice asks again, only 0 goes back
                while (true) {
                    System.out.println();
                    System.out.println("-----------------------SUB-CATEGORIES-----------------------");
                    System.out.printf("| %-56.56s |%n", category.toUpperCase());
                    System.out.println("|----------------------------------------------------------|");

                    for (int i = 0; i < groups.length; i++) {
                        System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + groups[i]);
                    }

                    System.out.println("| [0] Exit                                                 |");
                    System.out.println("------------------------------------------------------------");

                    groupChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

                    if (groupChoice >= 0 && groupChoice <= groups.length) {
                        break;
                    }

                    System.out.println("\n- Invalid choice.");
                }

                // pick 0, go back to the Gift Shop categories
                if (groupChoice == 0) {
                    continue;   // back to the Gift Shop categories
                }

                System.out.println("\n- " + category + ": " + groups[groupChoice - 1]);
                items = DataFile.filter(items, 1, groups[groupChoice - 1]);

            } else {
                System.out.println("\n- " + category + ":");
            }

            int itemChoice;

            // an invalid choice asks again, only 0 goes back
            while (true) {

                // show the items with prices
                System.out.println();
                System.out.println("----------------------AVAILABLE ITEMS-----------------------");

                for (int i = 0; i < items.length; i++) {
                    String stockNote = "";

                    if (InventoryManagement.getStock("Gift Shop", items[i][2]) <= 0) {
                        stockNote = "  (OUT OF STOCK)";
                    }

                    // show the price with 2 decimals (falls back to the raw text if it is not a valid number)
                    double itemPrice = DataFile.toDouble(items[i][3]);
                    String priceText = itemPrice < 0 ? items[i][3] : money(itemPrice);

                    System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + items[i][2] + " - PHP " + priceText + stockNote);
                }

                System.out.println("| [0] Exit                                                 |");
                System.out.println("------------------------------------------------------------");

                itemChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

                if (itemChoice >= 0 && itemChoice <= items.length) {
                    break;
                }

                System.out.println("\n- Invalid choice.");
            }

            // pick 0, go back to the Gift Shop categories
            if (itemChoice == 0) {
                continue;   // back to the Gift Shop categories
            }

            String selectedItem = items[itemChoice - 1][2];
            double price = DataFile.toDouble(items[itemChoice - 1][3]);

            if (price < 0) {
                System.out.println("\n- This item has no valid price in " + FILE_NAME);
                continue;
            }

            // check the stock (items already in the cart count too)
            int inCart = 0;

            for (int i = 0; i < cartCount; i++) {
                if (cart[i].equals(selectedItem)) {
                    inCart++;
                }
            }

            if (InventoryManagement.getStock("Gift Shop", selectedItem) - inCart <= 0) {
                System.out.println();
                System.out.println("------------------------------------------------------------");

                if (inCart == 0) {
                    System.out.printf("| %-56.56s |%n", "Sorry, " + selectedItem + " is out of stock.");
                } else {
                    System.out.printf("| %-56.56s |%n", "Sorry, no more " + selectedItem + " in stock.");
                }

                System.out.println("------------------------------------------------------------");
                continue;
            }

            // add the selected item to the cart
            cart[cartCount] = selectedItem;
            cartPrices[cartCount] = price;
            cartCount++;

            System.out.println("\n- Added to cart: " + selectedItem);

            // ask if visitor wants to buy more items
            if (!DataFile.askYesNo(input, "\nContinue shopping? (yes/no): ")) {
                break;
            }
        }

        // no items selected, end session
        if (cartCount == 0) {
            System.out.println("\n- No items selected. End of session.");
            return;
        }

        // computation of the total of all items in the cart
        double totalPrice = 0;

        for (int i = 0; i < cartCount; i++) {
            totalPrice = totalPrice + cartPrices[i];
        }
        totalPrice = DataFile.round2(totalPrice);

        // show shopping cart and accept/cancel purchase
        boolean checkoutComplete = false;

        while (!checkoutComplete) {
            System.out.println();
            System.out.println("-----------------------SHOPPING CART------------------------");

            for (int i = 0; i < cartCount; i++) {
                System.out.printf("| %-56.56s |%n", (i + 1) + ". " + cart[i] + " - PHP " + money(cartPrices[i]));
            }

            System.out.println("|----------------------------------------------------------|");
            System.out.printf("| %-56.56s |%n", "Total: PHP " + money(totalPrice));
            System.out.println("------------------------------------------------------------");

            // only yes or no is accepted, other inputs ask again
            if (!DataFile.askYesNo(input, "Accept purchase? (yes/no): ")) {

                // cancel transaction?
                if (DataFile.askYesNo(input, "Cancel transaction? (yes/no): ")) {
                    System.out.println("\n- Transaction cancelled. Returning to Gift Shop...");
                    return;
                }

                // otherwise it loops back to show the shopping cart

            } else {
                checkoutComplete = true;
            }
        }

        // payment method (an invalid choice asks again)
        String paymentMethod = "";

        while (paymentMethod.isEmpty()) {
            System.out.println();
            System.out.println("-----------------------PAYMENT METHOD-----------------------");
            System.out.println("| [1] Cash                                                 |");
            System.out.println("| [2] Credit                                               |");
            System.out.println("| [3] Online                                               |");
            System.out.println("------------------------------------------------------------");

            int paymentChoice = DataFile.toInt(DataFile.ask(input, "Choose payment method (1-3): "));

            if (paymentChoice == 1) {
                paymentMethod = "Cash";
            } else if (paymentChoice == 2) {
                paymentMethod = "Credit";
            } else if (paymentChoice == 3) {
                paymentMethod = "Online";
            } else {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|                 Invalid payment method.                  |");
                System.out.println("------------------------------------------------------------");
            }
        }

        // show total price
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + money(totalPrice));
        System.out.println("------------------------------------------------------------");

        // enter payment amount
        double paymentAmount = DataFile.toDouble(DataFile.ask(input, "Enter payment amount: PHP "));

        // insufficient funds
        while (paymentAmount < totalPrice) {
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("|                    Insufficient Funds                    |");
            System.out.println("------------------------------------------------------------");

            // cancel transaction?
            if (DataFile.askYesNo(input, "Cancel transaction? (yes/no): ")) {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|     Transaction cancelled. Returning to Gift Shop...     |");
                System.out.println("------------------------------------------------------------");
                return;

            } else {
                System.out.println("\nTotal Price: PHP " + money(totalPrice));
                paymentAmount = DataFile.toDouble(DataFile.ask(input, "Enter payment amount: PHP "));
            }
        }

        // purchase complete
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("|        Purchase Complete. Thank you for shopping!        |");
        System.out.println("------------------------------------------------------------");

        DataAnalytics.recordSale("Gift Shop", totalPrice);

        // take the bought items out of the stock
        for (int i = 0; i < cartCount; i++) {
            InventoryManagement.reduceStock("Gift Shop", cart[i], 1);
        }

        // generate receipt
        System.out.println();
        System.out.println("--------------------------RECEIPT---------------------------");
        System.out.println("| Items Purchased:                                         |");

        for (int i = 0; i < cartCount; i++) {
            System.out.printf("| %-56.56s |%n", (i + 1) + ". " + cart[i] + " - PHP " + money(cartPrices[i]));
        }

        System.out.println("|----------------------------------------------------------|");
        System.out.printf("| %-56.56s |%n", "Number of Items: " + cartCount);
        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + money(totalPrice));
        System.out.printf("| %-56.56s |%n", "Payment Method: " + paymentMethod);
        System.out.printf("| %-56.56s |%n", "Amount Paid: PHP " + money(paymentAmount));
        System.out.printf("| %-56.56s |%n", "Change: PHP " + money(DataFile.round2(paymentAmount - totalPrice)));
        System.out.println("|----------------------------------------------------------|");
        System.out.println("|                    PURCHASE COMPLETE!                    |");
        System.out.println("------------------------------------------------------------");
    }
}
