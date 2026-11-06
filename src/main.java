import java.util.*;

public class main {

    static int getInput(Scanner in) {
        while (true) {
            try {
                return in.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Enter a number: ");
                in.next();
            }
        }
    }

    static void displayCart(ArrayList<fooditem> cart, ArrayList<Integer> quantity) {

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        System.out.println("\n========== YOUR CART ==========");

        for (int i = 0; i < cart.size(); i++) {

            int amount = cart.get(i).itemprice * quantity.get(i);

            System.out.printf("%-15s x %-3d Rs.%d%n",
                    cart.get(i).itemname, quantity.get(i), amount);
        }

        System.out.println("================================");
    }

    static void addToCart(fooditem item, int qty,
                          ArrayList<fooditem> cart,
                          ArrayList<Integer> quantity) {

        for (int i = 0; i < cart.size(); i++) {

            if (cart.get(i).itemname.equalsIgnoreCase(item.itemname)) {

                quantity.set(i, quantity.get(i) + qty);

                System.out.println("Quantity updated successfully!");
                return;
            }
        }

        cart.add(item);
        quantity.add(qty);

        System.out.println("Food added to cart successfully!");
    }

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("           CAMPUS BITES");
        System.out.println("================================");

        System.out.print("Enter Student Name: ");
        String studentName= in.nextLine();

        System.out.print("Enter Register Number: ");
        String registerNumber = in.nextLine();

        System.out.println("\nWelcome to CAMPUS BITES!");

        System.out.println("\nSelect Your Shop:");
        System.out.println("1. REC_MART");
        System.out.println("2. REC_CAFE");
        System.out.println("3. REC_HUTCAFE");

        System.out.print("Enter choice: ");
        int n = getInput(in);

        String shopName;
        fooditem[] menu;

        if (n == 1) {

            shopName = "REC_MART";

            menu = new fooditem[] {
                new PreparedFood("Burger", 50),
                new PreparedFood("Pizza", 100),
                new PreparedFood("Sandwich", 30)
            };

        } else if (n == 2) {

            shopName = "REC_CAFE";

            menu = new fooditem[] {
                new PackagedFood("Biscuits", 10),
                new PackagedFood("Chips", 20),
                new PackagedFood("Chocolate", 25)
            };

        } else if (n == 3) {

            shopName = "REC_HUTCAFE";

            menu = new fooditem[] {
                new PreparedFood("Noodles", 40),
                new PreparedFood("Pasta", 60),
                new PreparedFood("Burger", 50),
                new PreparedFood("Pizza", 100)
            };

        } else {

            System.out.println("Invalid shop choice!");
            in.close();
            return;
        }

        ArrayList<fooditem> cart = new ArrayList<>();
        ArrayList<Integer> quantity = new ArrayList<>();

        boolean checkout = false;

        while (!checkout) {

            System.out.println("\n================================");
            System.out.println("           " + shopName);
            System.out.println("================================");

            System.out.println("1. Display Food Menu");
            System.out.println("2. Search Food");
            System.out.println("3. View Cart");
            System.out.println("4. Update Quantity");
            System.out.println("5. Remove Food");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");

            System.out.print("\nEnter your choice: ");

            int choice = getInput(in);

            switch (choice) {

                case 1:

                    System.out.println("\n========== FOOD MENU ==========");

                    for (int i = 0; i < menu.length; i++) {
                        System.out.print((i + 1) + ". ");
                        menu[i].display();
                    }

                    System.out.print("Select food number: ");
                    int foodChoice = getInput(in);

                    if (foodChoice >= 1 && foodChoice <= menu.length) {

                        System.out.print("Enter quantity: ");
                        int qty = getInput(in);

                        if (qty > 0 && qty <= 20) {
                            addToCart(menu[foodChoice - 1], qty, cart, quantity);
                        } else {
                            System.out.println("Quantity must be between 1 and 20.");
                        }

                    } else {
                        System.out.println("Invalid food choice!");
                    }

                    break;

                case 2:

                    in.nextLine();

                    System.out.print("Enter food name to search: ");
                    String search = in.nextLine();

                    boolean found = false;

                    for (int i = 0; i < menu.length; i++) {

                        if (menu[i].itemname.toLowerCase().contains(search.toLowerCase())) {

                            System.out.print((i + 1) + ". ");
                            menu[i].display();
                            found = true;
                        }
                    }

                    if (found) {

                        System.out.print("Enter food number to add (0 to cancel): ");
                        int selected = getInput(in);

                        if (selected >= 1 && selected <= menu.length &&
                            menu[selected - 1].itemname.toLowerCase().contains(search.toLowerCase())) {

                            System.out.print("Enter quantity: ");
                            int qty = getInput(in);

                            if (qty > 0 && qty <= 20) {
                                addToCart(menu[selected - 1], qty, cart, quantity);
                            } else {
                                System.out.println("Invalid quantity!");
                            }

                        } else if (selected != 0) {
                            System.out.println("Invalid selection!");
                        }

                    } else {
                        System.out.println("Food item not found!");
                    }

                    break;

                case 3:

                    displayCart(cart, quantity);
                    break;

                case 4:

                    if (cart.isEmpty()) {
                        System.out.println("Your cart is empty!");
                        break;
                    }

                    displayCart(cart, quantity);

                    System.out.print("Enter item number to update: ");
                    int update = getInput(in);

                    if (update >= 1 && update <= cart.size()) {

                        System.out.print("Enter new quantity: ");
                        int newQty = getInput(in);

                        if (newQty > 0 && newQty <= 20) {

                            quantity.set(update - 1, newQty);
                            System.out.println("Quantity updated!");

                        } else {
                            System.out.println("Invalid quantity!");
                        }

                    } else {
                        System.out.println("Invalid item number!");
                    }

                    break;

                case 5:

                    if (cart.isEmpty()) {
                        System.out.println("Your cart is empty!");
                        break;
                    }

                    displayCart(cart, quantity);

                    System.out.print("Enter item number to remove: ");
                    int remove = getInput(in);

                    if (remove >= 1 && remove <= cart.size()) {

                        cart.remove(remove - 1);
                        quantity.remove(remove - 1);

                        System.out.println("Item removed successfully!");

                    } else {
                        System.out.println("Invalid item number!");
                    }

                    break;

                case 6:

                    if (cart.isEmpty()) {
                        System.out.println("Your cart is empty!");
                        break;
                    }

                    checkout = true;
                    break;

                case 7:

                    System.out.println("Thank you for visiting CAMPUS BITES!");
                    in.close();
                    return;

                default:

                    System.out.println("Invalid choice!");
            }
        }

        // FINAL BILL

        long total = 0;

        System.out.println("\n================================");
        System.out.println("           CAMPUS BITES");
        System.out.println("================================");

        System.out.println("Student Name: " + studentName);
        System.out.println("Register Number: " + registerNumber);
        System.out.println("Shop: " + shopName);

        System.out.println("\nYour Cart:\n");

        for (int i = 0; i < cart.size(); i++) {

            fooditem item = cart.get(i);

            long amount = (long) item.itemprice * quantity.get(i);

            total += amount;

            System.out.printf("%-15s x %-3d Rs.%d%n",
                    item.itemname, quantity.get(i), amount);
        }

        System.out.println("--------------------------------");
        System.out.println("Total Amount: Rs." + total);
        System.out.println("--------------------------------");

        reference b = new reference();
        b.generateReferenceCode();
        OrderDatabase.saveOrder(
    studentName,
    registerNumber,
    shopName,
    b.referenceCode,
    (int) total,
    cart,
    quantity
);

        System.out.println("Reference Code: ");
        b.displayReferenceCode();

        System.out.println("\nOrder Status: PENDING");

        System.out.println("\n      BOOKING SUCCESSFUL!");
        System.out.println("================================");

        in.close();
    }
}