public class Main {
    import java.util.Scanner;

    public class Main {

        public static void main(String[] args) {

            Scanner input = new Scanner(System.in);

            RideSharingSystem system = new RideSharingSystem();

            // Load CSV files
            boolean ridersLoaded =
                    system.loadRidersFromCSV("data/riders_100.csv");

            boolean driversLoaded =
                    system.loadDriversFromCSV("data/drivers_30.csv");

            boolean ridesLoaded =
                    system.loadRidesFromCSV("data/rides_40.csv");

            if (!ridersLoaded || !driversLoaded || !ridesLoaded) {
                System.out.println("Error loading CSV files.");
            }

            int choice;

            do {

                System.out.println("\n===== Ride Sharing System =====");

                System.out.println("1. List all riders");
                System.out.println("2. Search for riders by home city");
                System.out.println("3. Search for riders by name");
                System.out.println("4. Add a rider");

                System.out.println("5. List all drivers");
                System.out.println("6. Search for drivers by vehicle type");
                System.out.println("7. Add a driver");

                System.out.println("8. List all rides alphabetically by pickup location");
                System.out.println("9. Search for rides by pickup location");
                System.out.println("10. Search for rides by rider name");
                System.out.println("11. List participants of a shared ride by pickup location");
                System.out.println("12. Add a ride");

                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");

                choice = input.nextInt();
                input.nextLine();

                switch (choice) {

                    // Rider
                    case 1:
                        // TODO
                        break;

                    case 2:
                        // TODO
                        break;

                    case 3:
                        // TODO
                        break;

                    case 4:
                        // TODO
                        break;

                    // Driver
                    case 5:
                        // TODO
                        break;

                    case 6:
                        // TODO
                        break;

                    case 7:
                        // TODO
                        break;

                    // Ride
                    case 8:
                        // TODO
                        break;

                    case 9:
                        // TODO
                        break;

                    case 10:
                        // TODO
                        break;

                    case 11:
                        // TODO
                        break;

                    case 12:
                        // TODO
                        break;

                    case 0:
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } while (choice != 0);

            input.close();
        }
    }
}
