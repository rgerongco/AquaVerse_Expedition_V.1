package aquaverse;

import java.util.Scanner;

// Inventory Management for the Gift Shop and the Cafe.
//
// The products stay in giftshop.txt and cafe.txt (Category|Group|Item|Price),
// the stock of every product is kept in inventory.txt (Shop|Item|Stock):
//
//   Gift Shop|Dolphin Plush|20
//   Cafe|Americano|15
//
// Staff  : view the stock and update it
// Admin  : the same, plus add a new product and delete a product
public class InventoryManagement {

    static String STOCK_FILE = "inventory.txt";

    static String[] SHOPS = {"Gift Shop", "Cafe"};
    static String[] SHOP_FILES = {"giftshop.txt", "cafe.txt"};

    // Stock given to a product that has no stock record yet
    static int DEFAULT_STOCK = 20;
    static int MAX_STOCK = 9999;
    // At or below this number the product is shown as LOW
    static int LOW_STOCK = 5;

    // ---------- USED BY THE GIFT SHOP AND THE CAFE ----------

    // Makes sure every product has a stock record and removes the records of
    // products that no longer exist. Call it once when the program starts.
    public static void syncStock() {
        String[][] old = DataFile.load(STOCK_FILE, 3);
        String[][] products0 = DataFile.load(SHOP_FILES[0], 4);
        String[][] products1 = DataFile.load(SHOP_FILES[1], 4);

        String[][] fresh = new String[old.length + products0.length + products1.length][];
        int count = 0;

        for (int s = 0; s < SHOPS.length; s++) {
            String[][] products = (s == 0) ? products0 : products1;

            if (products.length == 0) {
                // The product file could not be loaded: keep the old records of this shop
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

        String[][] result = new String[count][];
        for (int i = 0; i < count; i++) {
            result[i] = fresh[i];
        }
        DataFile.save(STOCK_FILE, result);
    }

    // How many pieces of the item are in stock
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

    // Takes "qty" pieces out of the stock after a purchase
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

    // ---------- MENUS ----------

    // role is "staff" or "admin"
    public static void showInventoryManagement(Scanner sc, String role) {
        syncStock();
        boolean back = false;

        while (!back) {
            System.out.println("\n-------------------INVENTORY MANAGEMENT-------------------");
            System.out.println("|\t[1] Gift Shop\t\t\t\t\t|");
            System.out.println("|\t[2] Cafe\t\t\t\t\t|");
            System.out.println("|\t[0] Back\t\t\t\t\t|");
            System.out.println("-----------------------------------------------------------");
            System.out.print("\tChoose: ");
            String answer = sc.nextLine().trim();

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

    static void shopMenu(Scanner sc, String role, int shop) {
        boolean admin = role.equals("admin");
        boolean back = false;

        while (!back) {
            System.out.println("\n-------------------" + SHOPS[shop].toUpperCase() + " INVENTORY-------------------");
            System.out.println("|\t[1] View Stock");
            System.out.println("|\t[2] Update Stock");

            if (admin) {
                System.out.println("|\t[3] Add Product");
                System.out.println("|\t[4] Delete Product");
            }

            System.out.println("|\t[0] Back");
            System.out.println("-----------------------------------------------------------");
            System.out.print("\tChoose: ");
            String answer = sc.nextLine().trim();

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

    // ---------- VIEW ----------

    // Prints the numbered product list with the stock. Returns the products shown.
    static String[][] printProducts(int shop) {
        syncStock();
        String[][] products = DataFile.load(SHOP_FILES[shop], 4);
        String[][] stockRows = DataFile.load(STOCK_FILE, 3);

        if (products.length == 0) {
            System.out.println("\n- No products found in " + SHOP_FILES[shop]);
            return products;
        }

        System.out.println("\n----------------------" + SHOPS[shop].toUpperCase() + " STOCK----------------------");

        for (int i = 0; i < products.length; i++) {
            String place = products[i][0];
            if (!products[i][1].equals("-")) {
                place = place + " / " + products[i][1];
            }

            int at = findRow(stockRows, stockRows.length, SHOPS[shop], products[i][2]);
            int stock = (at == -1) ? 0 : Math.max(0, DataFile.toInt(stockRows[at][2]));

            String note = "";
            if (stock == 0) {
                note = "  ** OUT OF STOCK **";
            } else if (stock <= LOW_STOCK) {
                note = "  * LOW *";
            }

            System.out.println("[" + (i + 1) + "] " + products[i][2] + " (" + place + ") - PHP "
                    + products[i][3] + " | Stock: " + stock + note);
        }

        System.out.println("-----------------------------------------------------------");
        return products;
    }

    static void viewStock(int shop) {
        printProducts(shop);
    }

    // ---------- UPDATE ----------

    static void updateStock(Scanner sc, int shop) {
        boolean back = false;

        while (!back) {
            String[][] products = printProducts(shop);
            if (products.length == 0) {
                return;
            }

            System.out.print("\tEnter product number to update (0 to go back): ");
            int number = DataFile.toInt(sc.nextLine().trim());

            if (number == 0) {
                back = true;
            } else if (number < 1 || number > products.length) {
                System.out.println("\n- Invalid product number.");
            } else {
                String item = products[number - 1][2];
                System.out.println("\n- " + item + " - Current Stock: " + getStock(SHOPS[shop], item));

                System.out.print("\tNew stock quantity (0-" + MAX_STOCK + ", or C to cancel): ");
                String text = sc.nextLine().trim();

                if (text.equalsIgnoreCase("c")) {
                    System.out.println("\n- Update cancelled.");
                } else {
                    int newStock = DataFile.toInt(text);
                    if (newStock < 0 || newStock > MAX_STOCK) {
                        System.out.println("\n- Stock must be a whole number from 0 to " + MAX_STOCK + ".");
                    } else {
                        System.out.print("\tUpdate stock of " + item + " to " + newStock + "? (yes/no): ");
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

    // ---------- ADD (admin) ----------

    static void addProduct(Scanner sc, int shop) {
        String[][] rows = DataFile.load(SHOP_FILES[shop], 4);

        System.out.println("\n-------------------ADD PRODUCT TO " + SHOPS[shop].toUpperCase() + "-------------------");

        // ----- category -----
        String[] categories = DataFile.distinct(rows, 0);
        String category = "";
        String group = "-";
        boolean newCategory = false;

        if (categories.length > 0) {
            for (int i = 0; i < categories.length; i++) {
                System.out.println("[" + (i + 1) + "] " + categories[i]);
            }
            System.out.println("[" + (categories.length + 1) + "] New Category");
            System.out.println("[0] Cancel");
            System.out.println("-----------------------------------------------------------");
            System.out.print("\tChoose a category: ");
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

        if (newCategory) {
            category = askText(sc, "\tNew category name (0 to cancel): ");
            if (category == null) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            System.out.print("\tWill this category have sub-groups? (yes/no): ");
            if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                group = askText(sc, "\tSub-group name (0 to cancel): ");
                if (group == null) {
                    System.out.println("\n- Add cancelled.");
                    return;
                }
            }
        } else {
            // An existing category: use one of its groups, or none if it has none
            String[][] inCategory = DataFile.filter(rows, 0, category);
            String[] groups = DataFile.distinct(inCategory, 1);

            if (!groups[0].equals("-")) {
                for (int i = 0; i < groups.length; i++) {
                    System.out.println("[" + (i + 1) + "] " + groups[i]);
                }
                System.out.println("[" + (groups.length + 1) + "] New Sub-group");
                System.out.println("[0] Cancel");
                System.out.println("-----------------------------------------------------------");
                System.out.print("\tChoose a sub-group: ");
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
                    group = askText(sc, "\tNew sub-group name (0 to cancel): ");
                    if (group == null) {
                        System.out.println("\n- Add cancelled.");
                        return;
                    }
                }
            }
        }

        // ----- product name -----
        String name = "";
        while (name.isEmpty()) {
            name = askText(sc, "\tProduct name (0 to cancel): ");
            if (name == null) {
                System.out.println("\n- Add cancelled.");
                return;
            }
            if (nameExists(rows, name)) {
                System.out.println("\n- That product already exists in the " + SHOPS[shop] + ".");
                name = "";
            }
        }

        // ----- price -----
        double price = -1;
        while (price <= 0) {
            System.out.print("\tPrice in PHP (0 to cancel): ");
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

        String priceText = (price == Math.floor(price))
                ? String.valueOf((long) price) : String.format("%.2f", price);

        // ----- starting stock -----
        int stock = -1;
        while (stock < 0) {
            System.out.print("\tStarting stock (0-" + MAX_STOCK + "): ");
            stock = DataFile.toInt(sc.nextLine().trim());
            if (stock < 0 || stock > MAX_STOCK) {
                System.out.println("\n- Stock must be a whole number from 0 to " + MAX_STOCK + ".");
                stock = -1;
            }
        }

        // ----- confirm -----
        System.out.println("\n----------------------PRODUCT DETAILS----------------------");
        System.out.println("- Category: " + category);

        if (!group.equals("-")) {
            System.out.println("- Sub-group: " + group);
        }

        System.out.println("- Product: " + name);
        System.out.println("- Price: PHP " + priceText);
        System.out.println("- Stock: " + stock);
        System.out.println("------------------------------------------------------------");
        System.out.print("\tAdd product? (yes/no): ");

        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("\n- Add cancelled.");
            return;
        }

        // Put the new product next to the products of the same category/group
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

    // ---------- DELETE (admin) ----------

    static void deleteProduct(Scanner sc, int shop) {
        boolean back = false;

        while (!back) {
            String[][] products = printProducts(shop);
            if (products.length == 0) {
                return;
            }

            System.out.print("\tEnter product number to delete (0 to go back): ");
            int number = DataFile.toInt(sc.nextLine().trim());

            if (number == 0) {
                back = true;
            } else if (number < 1 || number > products.length) {
                System.out.println("\n- Invalid product number.");
            } else if (products.length == 1) {
                System.out.println("\n- The last product of the " + SHOPS[shop] + " cannot be deleted.");
            } else {
                String item = products[number - 1][2];
                System.out.print("\tDelete " + item + "? (yes/no): ");

                if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                    String[][] kept = new String[products.length - 1][];
                    int k = 0;

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

    // ---------- HELPERS ----------

    // Position of the stock row of (shop, item) in the first "count" rows, or -1
    static int findRow(String[][] rows, int count, String shop, String item) {
        for (int i = 0; i < count; i++) {
            if (rows[i][0].equals(shop) && rows[i][1].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    // Is there already a product with this name? (the name is compared in lower case)
    static boolean nameExists(String[][] rows, String name) {
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][2].equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // Asks for a text. Returns null when the visitor types 0.
    // The | symbol is not allowed because it separates the parts in the files.
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