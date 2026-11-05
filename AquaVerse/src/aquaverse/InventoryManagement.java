package aquaverse;

import java.util.Scanner;

public class InventoryManagement {

    static String STOCK_FILE = "inventory.txt";

    static String[] SHOPS = {"Gift Shop", "Cafe"};
    static String[] SHOP_FILES = {"giftshop.txt", "cafe.txt"};

    // Stock given to a product that has no stock record yet
    static int DEFAULT_STOCK = 20;
    static int MAX_STOCK = 9999;
    
    // At or below this number the product is shown as LOW
    static int LOW_STOCK = 5;

    // check all products and their stock records
    public static void syncStock() {
        String[][] old = DataFile.load(STOCK_FILE, 3);
        String[][] products0 = DataFile.load(SHOP_FILES[0], 4);
        String[][] products1 = DataFile.load(SHOP_FILES[1], 4);

        String[][] fresh = new String[old.length + products0.length + products1.length][];
        int count = 0;

        // check the products of each shop
        for (int s = 0; s < SHOPS.length; s++) {
            String[][] products = (s == 0) ? products0 : products1;

            if (products.length == 0) {
                // keep the old stock records if the product file cannot be loaded
                for (int i = 0; i < old.length; i++) {
                    if (old[i][0].equals(SHOPS[s])) {
                        fresh[count] = old[i];
                        count++;
                    }
                }
            } else {
                for (int i = 0; i < products.length; i++) {
                    String item = products[i][2];
                    if (findRow(fresh, count, SHOPS[s], item) != -1) {
                        continue;   // same item name listed twice
                    }

                    // get the existing stock or give the product its default stock
                    int at = findRow(old, old.length, SHOPS[s], item);
                    String stock = String.valueOf(DEFAULT_STOCK);
                    if (at != -1 && DataFile.toInt(old[at][2]) >= 0) {
                        stock = old[at][2];
                    }
                    fresh[count] = new String[]{SHOPS[s], item, stock};
                    count++;
                }
            }
        }

        // save the updated stock records
        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = fresh[i];
        }
        DataFile.save(STOCK_FILE, result);
    }

    // get the stock of a selected product
    public static int getStock(String shop, String item) {
        String[][] rows = DataFile.load(STOCK_FILE, 3);
        int at = findRow(rows, rows.length, shop, item);
        if (at == -1) {
            syncStock();
            rows = DataFile.load(STOCK_FILE, 3);
            at = findRow(rows, rows.length, shop, item);
        }
        if (at == -1) {
            return 0;
        }
        int stock = DataFile.toInt(rows[at][2]);
        return stock < 0 ? 0 : stock;
    }

    // take the purchased quantity out of the stock
    public static void reduceStock(String shop, String item, int qty) {
        String[][] rows = DataFile.load(STOCK_FILE, 3);
        int at = findRow(rows, rows.length, shop, item);
        if (at == -1) {
            return;
        }
        int stock = DataFile.toInt(rows[at][2]) - qty;
        if (stock < 0) {
            stock = 0;
        }
        rows[at][2] = String.valueOf(stock);
        DataFile.save(STOCK_FILE, rows);
    }

    // show the inventory management menu
    public static void showInventoryManagement(Scanner sc, String role) {
        syncStock();
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("--------------------INVENTORY MANAGEMENT--------------------");
            System.out.println("| [1] Gift Shop                                            |");
            System.out.println("| [2] Cafe                                                 |");
            System.out.println("| [0] Back                                                 |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose: ");
            String answer = sc.nextLine().trim();

            // choose which shop to manage
            if (answer.equals("1")) {
                shopMenu(sc, role, 0);
            } else if (answer.equals("2")) {
                shopMenu(sc, role, 1);
            } else if (answer.equals("0")) {
                back = true;
            } else {
                System.out.println("\n- Please enter the value of your choice.");
            }
        }
    }

    // show the selected shop's inventory menu
    static void shopMenu(Scanner sc, String role, int shop) {
        boolean admin = role.equals("admin");
        boolean back = false;

        while (!back) {
            System.out.println();

            if (shop == 0) {
                System.out.println("--------------------GIFT SHOP INVENTORY---------------------");
            } else {
                System.out.println("-----------------------CAFE INVENTORY-----------------------");
            }

            System.out.println("| [1] View Stock                                           |");
            System.out.println("| [2] Update Stock                                         |");

            // admin can also add and delete products
            if (admin) {
                System.out.println("| [3] Add Product                                          |");
                System.out.println("| [4] Delete Product                                       |");
            }

            System.out.println("| [0] Back                                                 |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose: ");
            String answer = sc.nextLine().trim();

            // choose an inventory action
            if (answer.equals("1")) {
                viewStock(shop);
            } else if (answer.equals("2")) {
                updateStock(sc, shop);
            } else if (admin && answer.equals("3")) {
                addProduct(sc, shop);
            } else if (admin && answer.equals("4")) {
                deleteProduct(sc, shop);
            } else if (answer.equals("0")) {
                back = true;
            } else {
                System.out.println("\n- Please enter the value of your choice.");
            }
        }
    }

    // show all products with their prices and stock
    static String[][] printProducts(int shop) {
        syncStock();
        String[][] products = DataFile.load(SHOP_FILES[shop], 4);
        String[][] stockRows = DataFile.load(STOCK_FILE, 3);

        if (products.length == 0) {
            System.out.println("\n- No products found in " + SHOP_FILES[shop]);
            return products;
        }

        System.out.println();

        if (shop == 0) {
            System.out.println("----------------------GIFT SHOP STOCK-----------------------");
        } else {
            System.out.println("-------------------------CAFE STOCK-------------------------");
        }

        // display the product details and its current stock
        for (int i = 0; i < products.length; i++) {
            String place = products[i][0];
            if (!products[i][1].equals("-")) {
                place = place + " / " + products[i][1];
            }

            int at = findRow(stockRows, stockRows.length, SHOPS[shop], products[i][2]);
            int stock = (at == -1) ? 0 : Math.max(0, DataFile.toInt(stockRows[at][2]));

            // check if the product is low or out of stock
            String note = "";
            if (stock == 0) {
                note = "  ** OUT OF STOCK **";
            } else if (stock <= LOW_STOCK) {
                note = "  * LOW *";
            }

            if (i > 0) {
                System.out.println("|----------------------------------------------------------|");
            }

            System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + products[i][2] + " - PHP " + String.format("%.2f", DataFile.toDouble(products[i][3])));
            System.out.printf("| %-56.56s |%n", "    " + place);
            System.out.printf("| %-56.56s |%n", "    Stock: " + stock + note);
        }

        System.out.println("------------------------------------------------------------");
        return products;
    }

    // view the available stock
    static void viewStock(int shop) {
        printProducts(shop);
    }

    // update the stock of a selected product
    static void updateStock(Scanner sc, int shop) {
        boolean back = false;

        while (!back) {
            String[][] products = printProducts(shop);
            if (products.length == 0) {
                return;
            }

            System.out.print("Enter product number to update (0 to go back): ");
            int number = DataFile.toInt(sc.nextLine().trim());

            if (number == 0) {
                back = true;
            } else if (number < 1 || number > products.length) {
                System.out.println("\n- Invalid product number.");
            } else {
                String item = products[number - 1][2];
                System.out.println("\n- " + item + " - Current Stock: " + getStock(SHOPS[shop], item));

                // enter the new stock quantity
                System.out.print("New stock quantity (0-" + MAX_STOCK + ", or C to cancel): ");
                String text = sc.nextLine().trim();

                if (text.equalsIgnoreCase("c")) {
                    System.out.println("\n- Update cancelled.");
                } else {
                    int newStock = DataFile.toInt(text);
                    if (newStock < 0 || newStock > MAX_STOCK) {
                        System.out.println("\n- Stock must be a whole number from 0 to " + MAX_STOCK + ".");
                    } else {
                        // confirm the stock update
                        System.out.print("Update stock of " + item + " to " + newStock + "? (yes/no): ");
                        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                            setStock(SHOPS[shop], item, newStock);
                            System.out.println("\n- Stock Updated.");
                        } else {
                            System.out.println("\n- Update cancelled.");
                        }
                    }
                }
            }
        }
    }

    // save the new stock quantity
    static void setStock(String shop, String item, int stock) {
        String[][] rows = DataFile.load(STOCK_FILE, 3);
        int at = findRow(rows, rows.length, shop, item);
        if (at == -1) {
            String[][] bigger = new String[rows.length + 1][];
            for (int i = 0; i < rows.length; i++) {
                bigger[i] = rows[i];
            }
            bigger[rows.length] = new String[]{shop, item, String.valueOf(stock)};
            rows = bigger;
        } else {
            rows[at][2] = String.valueOf(stock);
        }
        DataFile.save(STOCK_FILE, rows);
    }

    // add a new product to the selected shop
    static void addProduct(Scanner sc, int shop) {
        String[][] rows = DataFile.load(SHOP_FILES[shop], 4);

        System.out.println();

        if (shop == 0) {
            System.out.println("------------------ADD PRODUCT TO GIFT SHOP------------------");
        } else {
            System.out.println("--------------------ADD PRODUCT TO CAFE---------------------");
        }

        // choose a category or create a new one
        String[] categories = DataFile.distinct(rows, 0);
        String category = "";
        String group = "-";
        boolean newCategory = false;

        if (categories.length > 0) {
            System.out.println("| Categories:                                              |");

            for (int i = 0; i < categories.length; i++) {
                System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + categories[i]);
            }

            System.out.printf("| %-56.56s |%n", "[" + (categories.length + 1) + "] New Category");
            System.out.println("| [0] Cancel                                               |");
            System.out.println("------------------------------------------------------------");
            System.out.print("Choose a category: ");
            int pick = DataFile.toInt(sc.nextLine().trim());

            if (pick == 0) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            if (pick < 1 || pick > categories.length + 1) {
                System.out.println("\n- Invalid choice.");
                return;
            }
            if (pick <= categories.length) {
                category = categories[pick - 1];
            } else {
                newCategory = true;
            }
        } else {
            newCategory = true;
        }

        // enter the new category and its sub-group
        if (newCategory) {
            category = askText(sc, "New category name (0 to cancel): ");
            if (category == null) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            System.out.print("Will this category have sub-groups? (yes/no): ");
            if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                group = askText(sc, "Sub-group name (0 to cancel): ");
                if (group == null) {
                    System.out.println("\n- Add cancelled.");
                    return;
                }
            }
        } else {
            // choose an existing sub-group or create a new one
            String[][] inCategory = DataFile.filter(rows, 0, category);
            String[] groups = DataFile.distinct(inCategory, 1);

            if (!groups[0].equals("-")) {
                System.out.println();
                System.out.println("-------------------------SUB-GROUPS-------------------------");
                System.out.printf("| %-56.56s |%n", category.toUpperCase());
                System.out.println("|----------------------------------------------------------|");

                for (int i = 0; i < groups.length; i++) {
                    System.out.printf("| %-56.56s |%n", "[" + (i + 1) + "] " + groups[i]);
                }

                System.out.printf("| %-56.56s |%n", "[" + (groups.length + 1) + "] New Sub-group");
                System.out.println("| [0] Cancel                                               |");
                System.out.println("------------------------------------------------------------");
                System.out.print("Choose a sub-group: ");
                int pick = DataFile.toInt(sc.nextLine().trim());

                if (pick == 0) {
                    System.out.println("\n- Add cancelled.");
                    return;
                }
                if (pick < 1 || pick > groups.length + 1) {
                    System.out.println("\n- Invalid choice.");
                    return;
                }
                if (pick <= groups.length) {
                    group = groups[pick - 1];
                } else {
                    group = askText(sc, "New sub-group name (0 to cancel): ");
                    if (group == null) {
                        System.out.println("\n- Add cancelled.");
                        return;
                    }
                }
            }
        }

        // enter the product name
        String name = "";
        while (name.isEmpty()) {
            name = askText(sc, "Product name (0 to cancel): ");
            if (name == null) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            if (nameExists(rows, name)) {
                System.out.println("\n- That product already exists in the " + SHOPS[shop] + ".");
                name = "";
            }
        }

        // enter the product price
        double price = -1;
        while (price <= 0) {
            System.out.print("Price in PHP (0 to cancel): ");
            String text = sc.nextLine().trim();
            if (text.equals("0")) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            price = DataFile.toDouble(text);
            if (price <= 0 || price > 100000) {
                System.out.println("\n- Please enter a valid price.");
                price = -1;
            }
        }

        String priceText = String.format("%.2f", price);

        // enter the starting stock
        int stock = -1;
        while (stock < 0) {
            System.out.print("Starting stock (0-" + MAX_STOCK + "): ");
            stock = DataFile.toInt(sc.nextLine().trim());
            if (stock < 0 || stock > MAX_STOCK) {
                System.out.println("\n- Stock must be a whole number from 0 to " + MAX_STOCK + ".");
                stock = -1;
            }
        }

        // show the product details and confirm adding it
        System.out.println();
        System.out.println("----------------------PRODUCT DETAILS-----------------------");
        System.out.printf("| %-56.56s |%n", "Category: " + category);

        if (!group.equals("-")) {
            System.out.printf("| %-56.56s |%n", "Sub-group: " + group);
        }

        System.out.printf("| %-56.56s |%n", "Product: " + name);
        System.out.printf("| %-56.56s |%n", "Price: PHP " + priceText);
        System.out.printf("| %-56.56s |%n", "Stock: " + stock);
        System.out.println("------------------------------------------------------------");
        System.out.print("Add product? (yes/no): ");

        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("\n- Add cancelled.");
            return;
        }

        // place the new product beside the same category and sub-group
        int lastSame = -1;
        int lastCategory = -1;

        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0].equals(category)) {
                lastCategory = i;
                if (rows[i][1].equals(group)) {
                    lastSame = i;
                }
            }
        }

        int insertAt = rows.length;
        if (lastSame != -1) {
            insertAt = lastSame + 1;
        } else if (lastCategory != -1) {
            insertAt = lastCategory + 1;
        }

        // save the new product to the product file
        String[][] updated = new String[rows.length + 1][];
        int k = 0;

        for (int i = 0; i <= rows.length; i++) {
            if (i == insertAt) {
                updated[i] = new String[]{category, group, name, priceText};
            } else {
                updated[i] = rows[k];
                k++;
            }
        }

        DataFile.save(SHOP_FILES[shop], updated);
        setStock(SHOPS[shop], name, stock);
        System.out.println("\n- Product Added Successfully.");
    }

    // delete a selected product from the shop
    static void deleteProduct(Scanner sc, int shop) {
        boolean back = false;

        while (!back) {
            String[][] products = printProducts(shop);
            if (products.length == 0) {
                return;
            }

            System.out.print("Enter product number to delete (0 to go back): ");
            int number = DataFile.toInt(sc.nextLine().trim());

            if (number == 0) {
                back = true;
            } else if (number < 1 || number > products.length) {
                System.out.println("\n- Invalid product number.");
            } else if (products.length == 1) {
                System.out.println("\n- The last product of the " + SHOPS[shop] + " cannot be deleted.");
            } else {
                String item = products[number - 1][2];

                // confirm the product deletion
                System.out.print("Delete " + item + "? (yes/no): ");

                if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    String[][] kept = new String[products.length - 1][];
                    int k = 0;

                    // keep all products except the selected one
                    for (int i = 0; i < products.length; i++) {
                        if (i != number - 1) {
                            kept[k] = products[i];
                            k++;
                        }
                    }

                    DataFile.save(SHOP_FILES[shop], kept);
                    syncStock();   // also removes the stock record of the deleted product
                    System.out.println("\n- Product Deleted Successfully.");
                } else {
                    System.out.println("\n- Deletion cancelled.");
                }
            }
        }
    }

    // find the stock record of a selected product
    static int findRow(String[][] rows, int count, String shop, String item) {
        for (int i = 0; i < count; i++) {
            if (rows[i][0].equals(shop) && rows[i][1].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    // check if the product name already exists
    static boolean nameExists(String[][] rows, String name) {
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][2].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // ask for a text input
    static String askText(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String text = sc.nextLine().trim();

            if (text.equals("0")) {
                return null;
            }
            if (text.isEmpty()) {
                System.out.println("\n- This field must be filled.");
            } else if (text.contains("|")) {
                System.out.println("\n- The | symbol is not allowed.");
            } else if (text.length() > 40) {
                System.out.println("\n- Please use 40 characters or fewer.");
            } else {
                return text;
            }
        }
    }
}
