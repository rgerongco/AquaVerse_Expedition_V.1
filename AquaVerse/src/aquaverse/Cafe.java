package aquaverse;
import java.util.Scanner;

// Cafe flowchart - basic version (multiple items, each with a quantity)
public class Cafe {
    
        static Scanner sc = new Scanner(System.in);

        static String[] categories = {"Food", "Drinks", "Dessert/Snack"};

        static String[][] items = {
            // Food
            {"Adobo", "Silog Meals", "Fried Fish", "Carbonara", "Pinoy Style Spaghetti", "Lasagna"},
            // Drinks (Coffee, Tea, Soft Drinks, Water)
            {"Americano", "Espresso", "Cappuccino", "Macchiato",
             "Matcha", "Green Tea", "Sweet Tea", "Yellow Tea",
             "Coke", "Sprite", "Royal",
             "Water"},
            // Dessert/Snack
            {"Halo Halo", "Ube Cake", "Red Velvet Cake", "Cheese Cake", "Chocolate Cake", "Leche Flan"}
        };

        static double[][] prices = {
            {120, 110, 130, 150, 120, 180},
            {90, 80, 110, 120, 130, 70, 60, 80, 45, 45, 45, 25},
            {110, 120, 130, 140, 120, 80}
        };

        // The visitor's order (cart)
        static String[] cartNames = new String[100];
        static double[] cartPrices = new double[100];
        static int[] cartQty = new int[100];
        static int cartCount = 0;

        // Show order -> accept? -> payment -> enough? -> receipt
        // Returns true if purchase completed, false if visitor goes back to the Cafe
        static boolean checkout() {
            // Compute the total of all items
            double total = 0;
            for (int i = 0; i < cartCount; i++) {
                total += cartPrices[i] * cartQty[i];
            }

            while (true) {
                // Show order
                System.out.println("\n===== YOUR ORDER =====");
                for (int i = 0; i < cartCount; i++) {
                    System.out.println(cartQty[i] + " x " + cartNames[i] + " - "
                            + (cartPrices[i] * cartQty[i]));
                }
                System.out.println("TOTAL: " + total);

                // Does the visitor accept the purchase?
                System.out.print("Accept purchase? (yes/no): ");
                if (!sc.nextLine().equalsIgnoreCase("yes")) {
                    return false; // NO -> back to Cafe
                }

                // Payment method
                System.out.print("Payment method (Cash, Credit or Online): ");
                String method = sc.nextLine();
                System.out.print("Enter payment amount: ");
                double amount = Double.parseDouble(sc.nextLine());

                // Is payment enough?
                if (amount >= total) {
                    System.out.println("\nPurchase Complete. Thank you for shopping");
                    // Generate receipt
                    System.out.println("\n------- RECEIPT -------");
                    for (int i = 0; i < cartCount; i++) {
                        System.out.println(cartQty[i] + " x " + cartNames[i] + " @ "
                                + cartPrices[i] + " = " + (cartPrices[i] * cartQty[i]));
                    }
                    System.out.println("Total:  " + total);
                    System.out.println("Paid:   " + amount + " via " + method);
                    System.out.println("Change: " + (amount - total));
                    System.out.println("-----------------------");
                    return true;
                } else {
                    System.out.println("Insufficient Funds");
                    // Cancel transaction?
                    System.out.print("Cancel transaction? (yes/no): ");
                    if (sc.nextLine().equalsIgnoreCase("yes")) {
                        return false; // YES -> back to Cafe
                    }
                    // NO -> loops back to Show order
                }
            }
        }

        public static void showCafe(Scanner sc) {
            // ---------- START ----------

            // Dashboard + entrance fee check
            while (true) {
                System.out.println("=== DASHBOARD ===");
                System.out.print("Are you a visitor? (yes/no): ");
                String isVisitor = sc.nextLine();

                if (isVisitor.equalsIgnoreCase("yes")) {
                    System.out.print("Have you paid the entrance fee? (yes/no): ");
                    String paid = sc.nextLine();
                    if (!paid.equalsIgnoreCase("yes")) {
                        System.out.println("Pay the entrance fee first for this access");
                        continue; // back to Dashboard
                    }
                }
                break;
            }

            // Cafe
            boolean done = false;
            while (!done) {
                System.out.println("\n=== CAFE ===");
                for (int i = 0; i < categories.length; i++) {
                    System.out.println((i + 1) + ". " + categories[i]);
                }
                System.out.print("Choose a category (1-3): ");
                int choice = Integer.parseInt(sc.nextLine()) - 1;

                if (choice < 0 || choice >= categories.length) {
                    System.out.println("Invalid choice");
                    continue;
                }

                // Show the items with prices
                System.out.println("\n-- " + categories[choice] + " --");
                for (int i = 0; i < items[choice].length; i++) {
                    // Drinks have sub-groups
                    if (choice == 1) {
                        if (i == 0) System.out.println("[Coffee]");
                        if (i == 4) System.out.println("[Tea]");
                        if (i == 8) System.out.println("[Soft Drinks]");
                        if (i == 11) System.out.println("[Water]");
                    }
                    System.out.println((i + 1) + ". " + items[choice][i] + " - " + prices[choice][i]);
                }

                // Does the visitor order?
                System.out.print("Enter item number to order (0 for none): ");
                int itemNum = Integer.parseInt(sc.nextLine()) - 1;

                if (itemNum < 0 || itemNum >= items[choice].length) {
                    if (cartCount == 0) {
                        continue; // NO order yet -> back to Cafe
                    }
                    // Already has items in the order -> go to checkout
                    done = checkout();
                    if (!done) {
                        cartCount = 0; // order cancelled, clear it
                    }
                    continue;
                }

                // How many amount for this item?
                int qty = 0;
                while (qty < 1 || qty > 10) {
                    System.out.print("How many amount do you want for this item? (1-10): ");
                    qty = Integer.parseInt(sc.nextLine());
                    if (qty < 1 || qty > 10) {
                        System.out.println("Please enter a number from 1 to 10");
                    }
                }

                // Add to the order
                cartNames[cartCount] = items[choice][itemNum];
                cartPrices[cartCount] = prices[choice][itemNum];
                cartQty[cartCount] = qty;
                cartCount++;
                System.out.println("Added: " + qty + " x " + items[choice][itemNum]);

                // Order more items?
                System.out.print("Order more items? (yes/no): ");
                if (sc.nextLine().equalsIgnoreCase("yes")) {
                    continue; // back to Cafe
                }

                done = checkout();
                if (!done) {
                    cartCount = 0; // order cancelled, clear it
                }
            }

            // ---------- END ----------
            System.out.println("Goodbye!");
        
    }
}
