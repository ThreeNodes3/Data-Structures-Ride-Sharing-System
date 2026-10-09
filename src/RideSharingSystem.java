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
    LinkedList<IRide> matchingRides = rides.findByPickupLocation(pickupLocation);

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
@Override
public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime,
        IDateTime dropoffTime, String dropoffLocation, int riderId, int driverId) {

    IRider rider = searchRiderById(riderId);
    IDriver driver = searchDriverById(driverId);

    if (rider == null || driver == null || pickupTime == null || dropoffTime == null) {
        return false;
    }

    if (pickupTime.compareTo(dropoffTime) >= 0) {
        return false;
    }

    LinkedList<IRide> allRides = rides.getAllAlphabetically();

    if (!allRides.empty()) {
        allRides.findFirst();

        while (true) {
            IRide ride = allRides.retrieve();

            boolean overlap = pickupTime.compareTo(ride.getDropoffTime()) < 0
                    && ride.getPickupTime().compareTo(dropoffTime) < 0;

            if (overlap && (ride.hasRider(riderId)
                    || ride.getDriver().getId() == driverId)) {
                return false;
            }

            if (allRides.last()) {
                break;
            }

            allRides.findNext();
        }
    }

    PrivateRide newRide = new PrivateRide(nextRideId, pickupLocation,
            pickupTime, dropoffTime, dropoffLocation, driver, rider);

    if (rides.addRide(newRide)) {
        nextRideId++;
        return true;
    }

    return false;
}
@Override
public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime,
        IDateTime dropoffTime, String dropoffLocation, int[] riderIds, int driverId) {

    IDriver driver = searchDriverById(driverId);

    if (driver == null || riderIds == null || riderIds.length == 0
            || pickupTime == null || dropoffTime == null) {
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

    LinkedList<IRide> allRides = rides.getAllAlphabetically();

    if (!allRides.empty()) {
        allRides.findFirst();

        while (true) {
            IRide ride = allRides.retrieve();

            boolean overlap = pickupTime.compareTo(ride.getDropoffTime()) < 0
                    && ride.getPickupTime().compareTo(dropoffTime) < 0;

            if (overlap) {
                if (ride.getDriver().getId() == driverId) {
                    return false;
                }

                for (int riderId : riderIds) {
                    if (ride.hasRider(riderId)) {
                        return false;
                    }
                }
            }

            if (allRides.last()) {
                break;
            }

            allRides.findNext();
        }
    }

    SharedRide newRide = new SharedRide(nextRideId, pickupLocation,
            pickupTime, dropoffTime, dropoffLocation, driver);

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
    try (java.io.BufferedReader reader =
            new java.io.BufferedReader(new java.io.FileReader(ridesFilePath))) {

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
            IDateTime pickupTime = parseRideDateTime(data[2].trim());
            IDateTime dropoffTime = parseRideDateTime(data[3].trim());
            String dropoffLocation = data[4].trim();
            int driverId = Integer.parseInt(data[5].trim());

            boolean success;

            if (type.equalsIgnoreCase("PRIVATE")) {
                int riderId = Integer.parseInt(data[6].trim());

                success = schedulePrivateRide(pickupLocation, pickupTime,
                        dropoffTime, dropoffLocation, riderId, driverId);

            } else if (type.equalsIgnoreCase("SHARED")) {
                String[] ids = data[6].trim().split(";");
                int[] riderIds = new int[ids.length];

                for (int i = 0; i < ids.length; i++) {
                    riderIds[i] = Integer.parseInt(ids[i].trim());
                }

                success = scheduleSharedRide(pickupLocation, pickupTime,
                        dropoffTime, dropoffLocation, riderIds, driverId);

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
}
