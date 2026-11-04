package aquaverse;
import java.io.IOException;
import java.util.Scanner;
public class GiftShop {

    static void showGiftShop(Scanner input) throws IOException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==============================");
        System.out.println("  AQUAVERSE EXPEDITION CAFE   ");
        System.out.println("==============================");

        // Simple array to hold up to 5 items in the cart
        String[] cart = new String[5];
        int cartCount = 0;
        boolean shoppingActive = true;
        
        giftShopMenu:
        while (shoppingActive && cartCount < cart.length) {
            // 2. Gift Shop Categories
            System.out.println("--- Gift Shop ---");
            System.out.println("Main Categories:");
            System.out.println("[1] Stuff Toys");
            System.out.println("[2] Kids' Items");
            System.out.println("[3] Clothing");
            System.out.println("[0] Exit");
            System.out.print("Choice: ");
            String categoryChoice = scanner.nextLine().trim();

            if (categoryChoice.equals("0")) {
                return;
            }

            String selectedItem = "";
            switch (categoryChoice) {
                case "1":
                    System.out.println("\nStuff Toys Sub-categories:");
                    System.out.println("[1] Popular Sea Creatures");
                    System.out.println("[2] Reef & Deep-Sea Favorites");
                    System.out.println("[3] Eco-Friendly Plush");
                    System.out.println("[0] Exit");
                    System.out.print("Choice: ");
                    String stChoice = scanner.nextLine().trim();
                        if (stChoice.equals("1")) {
                            System.out.println("Stuff Toy: Popular Sea Creatures");
                            System.out.println("[1] Turtles - PHP180");
                            System.out.println("[2] Starfish - PHP150");
                            System.out.println("[3] Axolotl - PHP250");
                            System.out.println("[4] Whale - PHP300");
                            System.out.println("[0] Exit");
                            System.out.print("Choice: ");
                            stChoice = scanner.nextLine().trim();
                            if (stChoice.equals("1")) {
                                selectedItem = "Turtles";
                            } else if (stChoice.equals("2")) {
                                selectedItem = "Starfish";
                            } else if (stChoice.equals("3")) {
                                selectedItem = "Axolotl";
                            } else if (stChoice.equals("4")) {
                                selectedItem = "Whale";
                            } else if (stChoice.equals("0")) {
                                continue giftShopMenu;
                            } else {
                                System.out.println("Invalid choice");
                                break;
                            }
                        } else if (stChoice.equals("2")) {
                            System.out.println("Stuff Toy: Reef & Deep-Sea Favorites");
                            System.out.println("[1] Stingray - PHP220");
                            System.out.println("[2] Shark - PHP280");
                            System.out.println("[3] Jellyfish - PHP200");
                            System.out.println("[0] Exit");
                            System.out.print("Choice: ");
                            stChoice = scanner.nextLine().trim();
                            if (stChoice.equals("1")) {
                                selectedItem = "Stingray";
                            } else if (stChoice.equals("2")) {
                                selectedItem = "Shark";
                            } else if (stChoice.equals("3")) {
                                selectedItem = "Jellyfish";
                            } else if (stChoice.equals("0")) {
                                continue giftShopMenu;
                            } else {
                                System.out.println("Invalid choice");
                                break;
                            }
                        }
                        else if (stChoice.equals("3")) {
                            System.out.println("Stuff Toy: Eco-Friendly Plush");
                            System.out.println("[1] Eco-Shark - PHP120");
                            System.out.println("[2] Eco-Whale - PHP120");
                            System.out.println("[3] Eco-Jellyfish - PHP100");
                            System.out.println("[0] Exit");
                            System.out.print("Choice: ");
                            stChoice = scanner.nextLine().trim();
                            if (stChoice.equals("1")) {
                                selectedItem = "Eco-Shark";
                            } else if (stChoice.equals("2")) {
                                selectedItem = "Eco-Whale";
                            } else if (stChoice.equals("3")) {
                                selectedItem = "Eco-Jellyfish";
                            } else if (stChoice.equals("0")) {
                                continue giftShopMenu;
                            } else {
                                System.out.println("Invalid choice");
                                break;
                            }
                        }
                    break;
                case "2":
                    System.out.println("\nKids' Items Sub-categories:");
                    System.out.println("[1] Ocean-Theme Puzzles - PHP99");
                    System.out.println("[2] Bath Toys & Water Play - PHP99");
                    System.out.println("[3] Magnetic Fishing Sets - PHP99");
                    System.out.println("[0] Exit");
                    System.out.print("Choice: ");
                    String kiChoice = scanner.nextLine().trim();
                        if (kiChoice.equals("1")) selectedItem = "Kids' Item: Ocean-Theme Puzzles";
                        else if (kiChoice.equals("2")) selectedItem = "Kids' Item: Bath Toys & Water Play";
                        else if (kiChoice.equals("3")) selectedItem = "Kids' Item: Magnetic Fishing Sets";
                        else if (kiChoice.equals("0")) continue giftShopMenu;
                    break;
                case "3":
                    System.out.println("\nClothing Sub-categories:");
                    System.out.println("[1] Graphic & Novelty Tees - PHP299");
                    System.out.println("[2] Sleepwear & Loungewear - PHP299");
                    System.out.println("[3] Baby & Toddler Wear - PHP299");
                    System.out.println("[4] Outerwear & Accessories - PHP249");
                    System.out.println("[0] Exit");
                    System.out.print("Choice: ");
                    String clChoice = scanner.nextLine().trim();
                        if (clChoice.equals("1")) selectedItem = "Clothing: Graphic & Novelty Tees";
                        else if (clChoice.equals("2")) selectedItem = "Clothing: Sleepwear & Loungewear";
                        else if (clChoice.equals("3")) selectedItem = "Clothing: Baby & Toddler Wear";
                        else if (clChoice.equals("4")) selectedItem = "Clothing: Outerwear & Accessories";
                        else if (clChoice.equals("0")) continue giftShopMenu;
                    break;
                default:
                    System.out.println("Invalid category selection.");
                    continue;
            }

            if (!selectedItem.isEmpty()) {
                cart[cartCount] = selectedItem;
                cartCount++;
                System.out.println("Added to cart: " + selectedItem);
            }
            
            // 3. Does visitor buy?
            System.out.print("Continue shopping? (yes/no): ");
            String buyInput = scanner.nextLine().trim().toLowerCase();
            if (buyInput.equals("no")) {
                break;
            }
        }
 
        if (cartCount == 0) {
            System.out.println("No items selected. End of session.");
            return;
        }
 
        // 4. Show Shopping Cart & Accept/Cancel Purchase
        boolean checkoutComplete = false;
        while (!checkoutComplete) {
            System.out.println("\n--- Show Shopping Cart ---");
            for (int i = 0; i < cartCount; i++) {
                System.out.println((i + 1) + ". " + cart[i]);
            }
 
            System.out.print("Accept purchase? (yes/no): ");
            String acceptPurchase = scanner.nextLine().trim().toLowerCase();
 
            if (acceptPurchase.equals("no")) {
                System.out.print("Cancel transaction? (yes/no): ");
                String cancelTx = scanner.nextLine().trim().toLowerCase();
                if (cancelTx.equals("yes")) {
                    System.out.println("Transaction cancelled. Returning to Gift Shop...");
                    return;
                } else {
                    continue; // Loops back to show shopping cart
                }
            } else if (acceptPurchase.equals("yes")) {
                break;
            }
        }
        
        // 5. Payment Method
        System.out.println("\n--- Payment Method ---");
        System.out.println("[1] Cash");
        System.out.println("[2] Credit");
        System.out.println("[3] Online");
        System.out.print("Choose payment method (1-3): ");
        int paymentChoice = scanner.nextInt();
 
        String paymentMethod = "";
 
        if (paymentChoice == 1) {
            paymentMethod = "Cash";
        } else if (paymentChoice == 2) {
            paymentMethod = "Credit";
        } else if (paymentChoice == 3) {
            paymentMethod = "Online";
        } else {
            System.out.println("Invalid payment method.");
            return;
        }
 
        // 6. Show Total Price
        // SAMPLE PRICE: Each item costs PHP 100.00
        double itemPrice = 100.00;
        double totalPrice = cartCount * itemPrice;
 
        System.out.println("\nTotal Price: PHP " + totalPrice);
 
        // 7. Check Payment
        System.out.print("Enter payment amount: PHP ");
        double paymentAmount = scanner.nextDouble();
 
        while (paymentAmount < totalPrice) {
            System.out.println("\nInsufficient Funds");
 
            scanner.nextLine();
            System.out.print("Cancel transaction? (yes/no): ");
            String cancelPayment = scanner.nextLine().trim().toLowerCase();
 
            if (cancelPayment.equals("yes")) {
                System.out.println("Transaction cancelled. Returning to Gift Shop...");
                return;
            } else {
                System.out.println("Total Price: PHP " + totalPrice);
                System.out.print("Enter payment amount: PHP ");
                paymentAmount = scanner.nextDouble();
            }
        }
 
        // 8. Purchase Complete
        System.out.println("\nPurchase Complete. Thank you for shopping!");
        DataAnalytics.recordSale("Gift Shop", totalPrice);
 
        // 9. Generate Receipt
        System.out.println("\n--- RECEIPT ---");
        System.out.println("Items Purchased:");
        for (int i = 0; i < cartCount; i++) {
            System.out.println((i + 1) + ". " + cart[i]);
        }
 
        System.out.println("Number of Items: " + cartCount);
        System.out.println("Price per Item: PHP " + itemPrice);
        System.out.println("Total Price: PHP " + totalPrice);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Amount Paid: PHP " + paymentAmount);
        System.out.println("Change: PHP " + (paymentAmount - totalPrice));
        System.out.println("Purchase Complete!");
        System.out.println("---------------");
 
        // END
 
    }
}