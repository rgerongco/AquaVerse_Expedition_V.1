package aquaverse;

import java.io.IOException;
import java.util.Scanner;

public class GiftShop {

    // giftshop.txt -> Category|Group|Item|Price   (the Group is - when the category has no groups)
    static String FILE_NAME = "giftshop.txt";

    static void showGiftShop(Scanner input) throws IOException {

        // All the items come from the notepad file
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

        // Simple arrays to hold up to 5 items in the cart
        String[] cart = new String[5];
        double[] cartPrices = new double[5];
        int cartCount = 0;
        boolean shoppingActive = true;

        while (shoppingActive && cartCount < cart.length) {
            // 2. Gift Shop Categories
            System.out.println();
            System.out.println("-------------------------GIFT SHOP--------------------------");
            System.out.println("| Main Categories:                                         |");

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + categories[i]);
            }

            System.out.println("| [0] Exit                                                 |");
            System.out.println("------------------------------------------------------------");
            int categoryChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

            if (categoryChoice == 0) {
                return;
            }
            if (categoryChoice < 1 || categoryChoice > categories.length) {
                System.out.println("\n- Invalid category selection.");
                continue;
            }

            String category = categories[categoryChoice - 1];
            String[][] items = DataFile.filter(rows, 0, category);

            // Categories like Stuff Toys have groups (sub-categories) to choose from first
            String[] groups = DataFile.distinct(items, 1);
            if (!groups[0].equals("-")) {
                System.out.println();
                System.out.println("-----------------------SUB-CATEGORIES-----------------------");
                System.out.printf("| %-56.56s |%n", category.toUpperCase());
                System.out.println("|----------------------------------------------------------|");

                for (int i = 0; i < groups.length; i++) {
                    System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + groups[i]);
                }

                System.out.println("| [0] Exit                                                 |");
                System.out.println("------------------------------------------------------------");
                int groupChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

                if (groupChoice < 1 || groupChoice > groups.length) {
                    if (groupChoice != 0) {
                        System.out.println("\n- Invalid choice.");
                    }
                    continue;   // back to the Gift Shop categories
                }
                System.out.println("\n- " + category + ": " + groups[groupChoice - 1]);
                items = DataFile.filter(items, 1, groups[groupChoice - 1]);
            } else {
                System.out.println("\n- " + category + ":");
            }

            // The items and their prices
            System.out.println();
            System.out.println("----------------------AVAILABLE ITEMS-----------------------");

            for (int i = 0; i < items.length; i++) {
                String stockNote = "";
                if (InventoryManagement.getStock("Gift Shop", items[i][2]) <= 0) {
                    stockNote = "  (OUT OF STOCK)";
                }
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + items[i][2] + " - PHP " + items[i][3] + stockNote);
            }

            System.out.println("| [0] Exit                                                 |");
            System.out.println("------------------------------------------------------------");
            int itemChoice = DataFile.toInt(DataFile.ask(input, "Choose: "));

            if (itemChoice == 0) {
                continue;   // back to the Gift Shop categories
            }
            if (itemChoice < 1 || itemChoice > items.length) {
                System.out.println("\n- Invalid choice.");
                continue;
            }

            String selectedItem = items[itemChoice - 1][2];
            double price = DataFile.toDouble(items[itemChoice - 1][3]);
            if (price < 0) {
                System.out.println("\n- This item has no valid price in " + FILE_NAME);
                continue;
            }

            // Check the stock (pieces already in the cart count too)
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

            cart[cartCount] = selectedItem;
            cartPrices[cartCount] = price;
            cartCount++;
            System.out.println("\n- Added to cart: " + selectedItem);

            // 3. Does visitor buy?
            String buyInput = DataFile.ask(input, "\nContinue shopping? (yes/no): ").toLowerCase();
            if (buyInput.equals("no")) {
                break;
            }
        }

        if (cartCount == 0) {
            System.out.println("\n- No items selected. End of session.");
            return;
        }

        // The total is the sum of the real prices of the items in the cart
        double totalPrice = 0;
        for (int i = 0; i < cartCount; i++) {
            totalPrice = totalPrice + cartPrices[i];
        }

        // 4. Show Shopping Cart & Accept/Cancel Purchase
        boolean checkoutComplete = false;
        while (!checkoutComplete) {
            System.out.println();
            System.out.println("-----------------------SHOPPING CART------------------------");

            for (int i = 0; i < cartCount; i++) {
                System.out.printf("| %-56.56s |%n", (i + 1) + ". " + cart[i] + " - PHP " + cartPrices[i]);
            }

            System.out.println("|----------------------------------------------------------|");
            System.out.printf("| %-56.56s |%n", "Total: PHP " + totalPrice);
            System.out.println("------------------------------------------------------------");

            String acceptPurchase = DataFile.ask(input, "Accept purchase? (yes/no): ").toLowerCase();

            if (acceptPurchase.equals("no")) {
                String cancelTx = DataFile.ask(input, "Cancel transaction? (yes/no): ").toLowerCase();
                if (cancelTx.equals("yes")) {
                    System.out.println("\n- Transaction cancelled. Returning to Gift Shop...");
                    return;
                }
                // otherwise it loops back to show the shopping cart
            } else if (acceptPurchase.equals("yes")) {
                checkoutComplete = true;
            }
        }

        // 5. Payment Method
        System.out.println();
        System.out.println("-----------------------PAYMENT METHOD-----------------------");
        System.out.println("| [1] Cash                                                 |");
        System.out.println("| [2] Credit                                               |");
        System.out.println("| [3] Online                                               |");
        System.out.println("------------------------------------------------------------");
        int paymentChoice = DataFile.toInt(DataFile.ask(input, "Choose payment method (1-3): "));

        String paymentMethod = "";

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
            return;
        }

        // 6. Show Total Price
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + totalPrice);
        System.out.println("------------------------------------------------------------");

        // 7. Check Payment
        double paymentAmount = DataFile.toDouble(DataFile.ask(input, "Enter payment amount: PHP "));

        while (paymentAmount < totalPrice) {
            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("|                    Insufficient Funds                    |");
            System.out.println("------------------------------------------------------------");

            String cancelPayment = DataFile.ask(input, "Cancel transaction? (yes/no): ").toLowerCase();

            if (cancelPayment.equals("yes")) {
                System.out.println();
                System.out.println("------------------------------------------------------------");
                System.out.println("|     Transaction cancelled. Returning to Gift Shop...     |");
                System.out.println("------------------------------------------------------------");
                return;
            } else {
                System.out.println("\nTotal Price: PHP " + totalPrice);
                paymentAmount = DataFile.toDouble(DataFile.ask(input, "Enter payment amount: PHP "));
            }
        }

        // 8. Purchase Complete
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("|        Purchase Complete. Thank you for shopping!        |");
        System.out.println("------------------------------------------------------------");

        DataAnalytics.recordSale("Gift Shop", totalPrice);

        // Take the bought items out of the stock
        for (int i = 0; i < cartCount; i++) {
            InventoryManagement.reduceStock("Gift Shop", cart[i], 1);
        }

        // 9. Generate Receipt
        System.out.println();
        System.out.println("--------------------------RECEIPT---------------------------");
        System.out.println("| Items Purchased:                                         |");

        for (int i = 0; i < cartCount; i++) {
            System.out.printf("| %-56.56s |%n", (i + 1) + ". " + cart[i] + " - PHP " + cartPrices[i]);
        }

        System.out.println("|----------------------------------------------------------|");
        System.out.printf("| %-56.56s |%n", "Number of Items: " + cartCount);
        System.out.printf("| %-56.56s |%n", "Total Price: PHP " + totalPrice);
        System.out.printf("| %-56.56s |%n", "Payment Method: " + paymentMethod);
        System.out.printf("| %-56.56s |%n", "Amount Paid: PHP " + paymentAmount);
        System.out.printf("| %-56.56s |%n", "Change: PHP " + (paymentAmount - totalPrice));
        System.out.println("|----------------------------------------------------------|");
        System.out.println("|                    PURCHASE COMPLETE!                    |");
        System.out.println("------------------------------------------------------------");
    }
}
