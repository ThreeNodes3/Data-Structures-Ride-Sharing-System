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
                    case 1: {
                        LinkedList<IRider> allRiders = system.getAllRiders();

                        if (allRiders.empty()) {
                            System.out.println("No riders found.");
                        } else {
                            allRiders.findFirst();

                            while (true) {
                                System.out.println(allRiders.retrieve());

                                if (allRiders.last()) {
                                    break;
                                }

                                allRiders.findNext();
                            }
                        }

                        break;
                    }
                    case 2: {
                        System.out.print("Enter home city: ");
                        String homeCity = input.nextLine();

                        LinkedList<IRider> matchingRiders =
                                system.searchRidersByHomeCity(homeCity);

                        if (matchingRiders.empty()) {
                            System.out.println("No riders found.");
                        } else {
                            matchingRiders.findFirst();

                            while (true) {
                                System.out.println(matchingRiders.retrieve());

                                if (matchingRiders.last()) {
                                    break;
                                }

                                matchingRiders.findNext();
                            }
                        }

                        break;
                    }
                    case 3: {
                        System.out.print("Enter rider name: ");
                        String riderName = input.nextLine();

                        LinkedList<IRider> matchingRiders =
                                system.searchRidersByName(riderName);

                        if (matchingRiders.empty()) {
                            System.out.println("No riders found.");
                        } else {
                            matchingRiders.findFirst();

                            while (true) {
                                System.out.println(matchingRiders.retrieve());

                                if (matchingRiders.last()) {
                                    break;
                                }

                                matchingRiders.findNext();
                            }
                        }

                        break;
                    }
                    case 4: {
                        try {
                            System.out.print("Enter rider ID: ");
                            int riderId = Integer.parseInt(input.nextLine().trim());

                            System.out.print("Enter full name: ");
                            String riderName = input.nextLine().trim();

                            System.out.print("Enter phone number (10 digits): ");
                            String phoneNumber = input.nextLine().trim();

                            System.out.print("Enter email: ");
                            String email = input.nextLine().trim();

                            System.out.print("Enter home city: ");
                            String homeCity = input.nextLine().trim();

                            Rider newRider = new Rider(
                                    riderId,
                                    riderName,
                                    phoneNumber,
                                    email,
                                    homeCity
                            );

                            boolean added = system.addRider(newRider);

                            if (added) {
                                System.out.println("Rider added successfully.");
                            } else {
                                System.out.println(
                                        "Failed to add rider: duplicate ID, duplicate email, or invalid data.");
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid rider ID. Enter a whole number.");

                        } catch (IllegalArgumentException e) {
                            System.out.println("Failed to add rider: " + e.getMessage());
                        }

                        break;
                    }
                  // Driver
case 5: {
    LinkedList<IDriver> allDrivers = system.getAllDrivers();
    printDrivers(allDrivers);
    break;
}

case 6: {
    System.out.print(
            "Enter vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ");

    try {
        VehicleType vehicleType = VehicleType.valueOf(
                input.nextLine().trim().toUpperCase());

        LinkedList<IDriver> matchingDrivers =
                system.searchDriversByVehicleType(vehicleType);

        printDrivers(matchingDrivers);

    } catch (IllegalArgumentException e) {
        System.out.println(
                "Invalid vehicle type. Use SEDAN, LUXURY_SEDAN, SUV, or VAN.");
    }

    break;
}

case 7: {
    try {
        System.out.print("Enter driver ID: ");
        int newDriverId = Integer.parseInt(input.nextLine().trim());

        System.out.print("Enter full name: ");
        String driverName = input.nextLine().trim();

        System.out.print("Enter phone number (10 digits): ");
        String phoneNumber = input.nextLine().trim();

        System.out.print("Enter vehicle plate (example: ABC1234): ");
        String vehiclePlate = input.nextLine().trim();

        System.out.print(
                "Enter vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ");

        VehicleType vehicleType = VehicleType.valueOf(
                input.nextLine().trim().toUpperCase());

        Driver newDriver = new Driver(
                newDriverId, driverName, phoneNumber,
                vehiclePlate, vehicleType);

        boolean added = system.addDriver(newDriver);

        if (added) {
            System.out.println("Driver added successfully.");
        } else {
            System.out.println(
                    "Failed to add driver: duplicate ID, duplicate plate, or invalid data.");
        }

    } catch (NumberFormatException e) {
        System.out.println("Invalid driver ID. Enter a whole number.");

    } catch (IllegalArgumentException e) {
        System.out.println("Failed to add driver: " + e.getMessage());
    }

    break;
}

                    // Ride
                    case 8:
    LinkedList<IRide> allRides = system.getAllRidesAlphabetically();

    if (allRides.empty()) {
        System.out.println("No rides found.");
    } else {
        allRides.findFirst();

        while (true) {
            System.out.println(allRides.retrieve());

            if (allRides.last()) {
                break;
            }

            allRides.findNext();
        }
    }
    break;

                    case 9:
    System.out.print("Enter pickup location: ");
    String pickupLocation = input.nextLine();

    LinkedList<IRide> matchingRides =
            system.searchRidesByPickupLocation(pickupLocation);

    if (matchingRides.empty()) {
        System.out.println("No rides found.");
    } else {
        matchingRides.findFirst();

        while (true) {
            System.out.println(matchingRides.retrieve());

            if (matchingRides.last()) {
                break;
            }

            matchingRides.findNext();
        }
    }
    break;

                    case 10:
    System.out.print("Enter rider name: ");
    String riderName = input.nextLine();

    LinkedList<IRide> riderRides =
            system.searchRidesByRiderName(riderName);

    if (riderRides.empty()) {
        System.out.println("No rides found.");
    } else {
        riderRides.findFirst();

        while (true) {
            System.out.println(riderRides.retrieve());

            if (riderRides.last()) {
                break;
            }

            riderRides.findNext();
        }
    }
    break;

                    case 11:
    System.out.print("Enter pickup location: ");
    String sharedLocation = input.nextLine();

    LinkedList<IRider> participants =
            system.getSharedRideParticipants(sharedLocation);

    if (participants == null || participants.empty()) {
        System.out.println("No participants found.");
    } else {
        participants.findFirst();

        while (true) {
            System.out.println(participants.retrieve());

            if (participants.last()) {
                break;
            }

            participants.findNext();
        }
    }
    break;

                    case 12:
    System.out.println("Choose ride type:");
    System.out.println("1. Private Ride");
    System.out.println("2. Shared Ride");
    System.out.print("Enter ride type: ");

    int rideType = input.nextInt();
    input.nextLine();

    if (rideType == 1 || rideType == 2) {

        System.out.print("Enter pickup location: ");
        String pickup = input.nextLine();

        System.out.print("Enter dropoff location: ");
        String dropoff = input.nextLine();

        System.out.print("Enter pickup year: ");
        int pickupYear = input.nextInt();

        System.out.print("Enter pickup month: ");
        int pickupMonth = input.nextInt();

        System.out.print("Enter pickup day: ");
        int pickupDay = input.nextInt();

        System.out.print("Enter pickup hour: ");
        int pickupHour = input.nextInt();

        System.out.print("Enter pickup minute: ");
        int pickupMinute = input.nextInt();

        System.out.print("Enter dropoff year: ");
        int dropoffYear = input.nextInt();

        System.out.print("Enter dropoff month: ");
        int dropoffMonth = input.nextInt();

        System.out.print("Enter dropoff day: ");
        int dropoffDay = input.nextInt();

        System.out.print("Enter dropoff hour: ");
        int dropoffHour = input.nextInt();

        System.out.print("Enter dropoff minute: ");
        int dropoffMinute = input.nextInt();

        IDateTime pickupTime = new DateTime(
                pickupYear, pickupMonth, pickupDay,
                pickupHour, pickupMinute);

        IDateTime dropoffTime = new DateTime(
                dropoffYear, dropoffMonth, dropoffDay,
                dropoffHour, dropoffMinute);

        System.out.print("Enter driver ID: ");
        int driverId = input.nextInt();

        boolean success = false;

        if (rideType == 1) {

            System.out.print("Enter rider ID: ");
            int riderId = input.nextInt();

            success = system.schedulePrivateRide(
                    pickup, pickupTime, dropoffTime,
                    dropoff, riderId, driverId);

        } else {

            System.out.print("Enter number of riders: ");
            int count = input.nextInt();

            if (count > 0) {
                int[] riderIds = new int[count];

                for (int i = 0; i < count; i++) {
                    System.out.print("Enter rider ID " + (i + 1) + ": ");
                    riderIds[i] = input.nextInt();
                }

                success = system.scheduleSharedRide(
                        pickup, pickupTime, dropoffTime,
                        dropoff, riderIds, driverId);
            }
        }

        if (success) {
            System.out.println("Ride scheduled successfully.");
        } else {
            System.out.println("Failed to schedule ride.");
        }

    } else {
        System.out.println("Invalid ride type.");
    }
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
        private static void printDrivers(LinkedList<IDriver> driverResults) {
    if (driverResults.empty()) {
        System.out.println("No drivers found.");
        return;
    }

    driverResults.findFirst();

    while (true) {
        System.out.println(driverResults.retrieve().toString());

        if (driverResults.last()) {
            break;
        }

        driverResults.findNext();
    }
}
    }
