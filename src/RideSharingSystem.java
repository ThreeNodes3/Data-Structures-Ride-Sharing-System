import java.io.BufferedReader;
import java.io.FileReader;

public class RideSharingSystem implements IRideSharingSystem {

    private IRiderList riders;
    private IDriverList drivers;
    private IRideList rides;
    private int nextRideId;

    public RideSharingSystem() {
        riders = new RiderList();
        drivers = new DriverList();
        rides = new RideList();
        nextRideId = 1;
    }

    @Override
    public LinkedList<IRide> getAllRidesAlphabetically() {
        return rides.getAllAlphabetically();
    }

    @Override
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rides.findByPickupLocation(pickupLocation);
    }

    @Override
    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rides.findByRiderName(riderName);
    }

    @Override
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {

        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRide> matchingRides =
                rides.findByPickupLocation(pickupLocation);

        if (matchingRides.empty()) {
            return result;
        }

        matchingRides.findFirst();

        while (true) {

            IRide ride = matchingRides.retrieve();

            if (ride instanceof ISharedRide) {
                return ((ISharedRide) ride).getParticipants();
            }

            if (matchingRides.last()) {
                break;
            }

            matchingRides.findNext();
        }

        return result;
    }

    private boolean isOverlap(IDateTime pickupTime,
                              IDateTime dropoffTime,
                              IRide ride) {

        return pickupTime.compareTo(ride.getDropoffTime()) < 0
                && ride.getPickupTime().compareTo(dropoffTime) < 0;
    }

    private boolean hasConflict(IDateTime pickupTime,
                                IDateTime dropoffTime,
                                int[] riderIds,
                                int driverId) {

        LinkedList<IRide> allRides = rides.getAllAlphabetically();

        if (allRides.empty()) {
            return false;
        }

        allRides.findFirst();

        while (true) {

            IRide ride = allRides.retrieve();

            if (isOverlap(pickupTime, dropoffTime, ride)) {

                if (ride.getDriver().getId() == driverId) {
                    return true;
                }

                for (int riderId : riderIds) {
                    if (ride.hasRider(riderId)) {
                        return true;
                    }
                }
            }

            if (allRides.last()) {
                break;
            }

            allRides.findNext();
        }

        return false;
    }

    @Override
    public boolean schedulePrivateRide(String pickupLocation,
                                       IDateTime pickupTime,
                                       IDateTime dropoffTime,
                                       String dropoffLocation,
                                       int riderId,
                                       int driverId) {

        IRider rider = searchRiderById(riderId);
        IDriver driver = searchDriverById(driverId);

        if (rider == null || driver == null
                || pickupTime == null || dropoffTime == null) {
            return false;
        }

        if (pickupTime.compareTo(dropoffTime) >= 0) {
            return false;
        }

        int[] riderIds = {riderId};

        if (hasConflict(pickupTime, dropoffTime, riderIds, driverId)) {
            return false;
        }

        PrivateRide newRide = new PrivateRide(
                nextRideId,
                pickupLocation,
                pickupTime,
                dropoffTime,
                dropoffLocation,
                driver,
                rider
        );

        if (rides.addRide(newRide)) {
            nextRideId++;
            return true;
        }

        return false;
    }

    @Override
    public boolean scheduleSharedRide(String pickupLocation,
                                      IDateTime pickupTime,
                                      IDateTime dropoffTime,
                                      String dropoffLocation,
                                      int[] riderIds,
                                      int driverId) {

        IDriver driver = searchDriverById(driverId);

        if (driver == null
                || riderIds == null
                || riderIds.length == 0
                || pickupTime == null
                || dropoffTime == null) {
            return false;
        }

        if (pickupTime.compareTo(dropoffTime) >= 0) {
            return false;
        }

        for (int i = 0; i < riderIds.length; i++) {

            if (searchRiderById(riderIds[i]) == null) {
                return false;
            }

            for (int j = 0; j < i; j++) {

                if (riderIds[i] == riderIds[j]) {
                    return false;
                }
            }
        }

        if (hasConflict(pickupTime, dropoffTime, riderIds, driverId)) {
            return false;
        }

        SharedRide newRide = new SharedRide(
                nextRideId,
                pickupLocation,
                pickupTime,
                dropoffTime,
                dropoffLocation,
                driver
        );

        for (int riderId : riderIds) {

            if (!newRide.addParticipant(searchRiderById(riderId))) {
                return false;
            }
        }

        if (rides.addRide(newRide)) {
            nextRideId++;
            return true;
        }

        return false;
    }

    @Override
    public boolean loadRidesFromCSV(String ridesFilePath) {

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(ridesFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                if (data.length != 7) {
                    return false;
                }

                String type = data[0].trim();
                String pickupLocation = data[1].trim();
                IDateTime pickupTime =
                        parseRideDateTime(data[2].trim());

                IDateTime dropoffTime =
                        parseRideDateTime(data[3].trim());

                String dropoffLocation = data[4].trim();
                int driverId =
                        Integer.parseInt(data[5].trim());

                boolean success;

                if (type.equalsIgnoreCase("PRIVATE")) {

                    int riderId =
                            Integer.parseInt(data[6].trim());

                    success = schedulePrivateRide(
                            pickupLocation,
                            pickupTime,
                            dropoffTime,
                            dropoffLocation,
                            riderId,
                            driverId
                    );

                } else if (type.equalsIgnoreCase("SHARED")) {

                    String[] ids =
                            data[6].trim().split(";");

                    int[] riderIds =
                            new int[ids.length];

                    for (int i = 0; i < ids.length; i++) {
                        riderIds[i] =
                                Integer.parseInt(ids[i].trim());
                    }

                    success = scheduleSharedRide(
                            pickupLocation,
                            pickupTime,
                            dropoffTime,
                            dropoffLocation,
                            riderIds,
                            driverId
                    );

                } else {
                    return false;
                }

                if (!success) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private IDateTime parseRideDateTime(String value) {

        String[] parts = value.split(" ");
        String[] date = parts[0].split("/");
        String[] time = parts[1].split(":");

        int month = Integer.parseInt(date[0]);
        int day = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        return new DateTime(year, month, day, hour, minute);
    }

    @Override
    public boolean loadRidersFromCSV(String ridersFilePath) {

        try {

            BufferedReader reader =
                    new BufferedReader(new FileReader(ridersFilePath));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                int riderId = Integer.parseInt(data[0]);
                String name = data[1];
                String email = data[2];
                String phoneNumber = data[3];
                String homeCity = data[4];

                Rider rider = new Rider(
                        riderId,
                        name,
                        phoneNumber,
                        email,
                        homeCity
                );

                if (!addRider(rider)) {
                    reader.close();
                    return false;
                }
            }

            reader.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addRider(IRider rider) {
        return riders.add(rider);
    }

    @Override
    public IRider searchRiderById(int riderId) {
        return riders.findById(riderId);
    }

    @Override
    public IRider searchRiderByEmail(String email) {
        return riders.findByEmail(email);
    }

    @Override
    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riders.findByName(fullName);
    }

    @Override
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riders.findByHomeCity(homeCity);
    }

    @Override
    public LinkedList<IRider> getAllRiders() {
        return riders.getAll();
    }

    @Override
    public boolean removeRider(int riderId) {

        IRider rider = riders.findById(riderId);

        if (rider == null) {
            return false;
        }

        LinkedList<IRide> allRides =
                rides.getAllAlphabetically();

        if (!allRides.empty()) {

            allRides.findFirst();

            while (true) {

                IRide ride = allRides.retrieve();

                if (ride.hasRider(riderId)) {

                    if (ride instanceof IPrivateRide) {

                        rides.removeRideById(
                                ride.getRideId()
                        );

                    } else if (ride instanceof ISharedRide) {

                        ISharedRide sharedRide =
                                (ISharedRide) ride;

                        sharedRide.removeParticipantById(
                                riderId
                        );

                        if (sharedRide.isEmpty()) {
                            rides.removeRideById(
                                    ride.getRideId()
                            );
                        }
                    }
                }

                if (allRides.last()) {
                    break;
                }

                allRides.findNext();
            }
        }

        return riders.removeById(riderId);
    }

    @Override
    public boolean loadDriversFromCSV(String driversFilePath) {

        if (driversFilePath == null) {
            return false;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(driversFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 5) {
                    return false;
                }

                for (int i = 0; i < data.length; i++) {

                    data[i] = data[i].trim();

                    if (data[i].isEmpty()) {
                        return false;
                    }
                }

                int driverId =
                        Integer.parseInt(data[0]);

                VehicleType vehicleType =
                        VehicleType.valueOf(data[4]);

                Driver driver = new Driver(
                        driverId,
                        data[1],
                        data[2],
                        data[3],
                        vehicleType
                );

                if (!addDriver(driver)) {
                    return false;
                }
            }

            return true;

        } catch (java.io.IOException |
                 IllegalArgumentException e) {

            return false;
        }
    }

    @Override
    public boolean addDriver(IDriver driver) {
        return drivers.add(driver);
    }

    @Override
    public IDriver searchDriverById(int driverId) {
        return drivers.findById(driverId);
    }

    @Override
    public IDriver searchDriverByVehiclePlate(
            String vehiclePlate) {

        return drivers.findByVehiclePlate(vehiclePlate);
    }

    @Override
    public LinkedList<IDriver> searchDriversByVehicleType(
            VehicleType vehicleType) {

        return drivers.findByVehicleType(vehicleType);
    }

    @Override
    public LinkedList<IDriver> getAllDrivers() {
        return drivers.getAll();
    }

    @Override
    public boolean removeDriver(int driverId) {

        if (drivers.findById(driverId) == null) {
            return false;
        }

        LinkedList<IRide> allRides =
                rides.getAllAlphabetically();

        LinkedList<Integer> rideIdsToRemove =
                new LinkedList<Integer>();

        if (!allRides.empty()) {

            allRides.findFirst();

            while (true) {

                IRide ride = allRides.retrieve();

                if (ride.getDriver().getId() == driverId) {
                    rideIdsToRemove.insert(
                            ride.getRideId()
                    );
                }

                if (allRides.last()) {
                    break;
                }

                allRides.findNext();
            }
        }

        if (!rideIdsToRemove.empty()) {

            rideIdsToRemove.findFirst();

            while (true) {

                rides.removeRideById(
                        rideIdsToRemove.retrieve()
                );

                if (rideIdsToRemove.last()) {
                    break;
                }

                rideIdsToRemove.findNext();
            }
        }

        return drivers.removeById(driverId);
    }
}