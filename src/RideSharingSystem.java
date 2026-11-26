import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RideSharingSystem implements IRideSharingSystem {

    private IBST<Integer, IRider> ridersById;
    private IBST<String, IBST<Integer, IRider>> ridersByName;
    private IBST<String, IBST<Integer, IRider>> ridersByHomeCity;
    private int nextRideId;
    private IBST<Integer, IDriver> driversById;
    private IBST<String, IDriver> driversByPlate;
    private IBST<VehicleType, IBST<Integer, IDriver>> driversByVehicleType;
    private IBST<Integer, IRide> ridesById;
private IBST<String, IBST<Integer, IRide>> ridesByPickupLocation;
private IBST<IDateTime, IBST<Integer, IRide>> ridesByPickupTime;

    public RideSharingSystem() {
        ridersById = new BST<Integer, IRider>();

        ridersByName = new BST<String,
                IBST<Integer, IRider>>();

        ridersByHomeCity = new BST<String,
                IBST<Integer, IRider>>();
        nextRideId = 1;
        driversById = new BST<Integer, IDriver>();
        driversByPlate = new BST<String, IDriver>();
        driversByVehicleType = new BST<VehicleType, IBST<Integer, IDriver>>();
        ridesById = new BST<Integer, IRide>();
ridesByPickupLocation = new BST<String, IBST<Integer, IRide>>();
ridesByPickupTime = new BST<IDateTime, IBST<Integer, IRide>>();
    }

    @Override
public LinkedList<IRide> getAllRidesAlphabetically() {

    LinkedList<IRide> result = new LinkedList<IRide>();

    LinkedList<IBST<Integer, IRide>> locations =
            ridesByPickupLocation.inOrder();

    if (locations.empty()) {
        return result;
    }

    locations.findFirst();

    while (true) {

        LinkedList<IRide> locationRides =
                locations.retrieve().inOrder();

        if (!locationRides.empty()) {

            locationRides.findFirst();

            while (true) {

                result.insert(locationRides.retrieve());

                if (locationRides.last()) {
                    break;
                }

                locationRides.findNext();
            }
        }

        if (locations.last()) {
            break;
        }

        locations.findNext();
    }

    return result;
}

   @Override
public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {

    if (pickupLocation == null ||
            !ridesByPickupLocation.findKey(pickupLocation)) {
        return new LinkedList<IRide>();
    }

    return ridesByPickupLocation.retrieve().inOrder();
}

    @Override
public LinkedList<IRide> searchRidesByRiderName(String riderName) {

    IBST<Integer, IRide> sortedRides =
            new BST<Integer, IRide>();

    if (riderName == null) {
        return new LinkedList<IRide>();
    }

    LinkedList<IRider> matchingRiders =
            searchRidersByName(riderName);

    if (matchingRiders.empty()) {
        return new LinkedList<IRide>();
    }

    matchingRiders.findFirst();

    while (true) {

        IRider rider = matchingRiders.retrieve();

        LinkedList<IRide> riderRides =
                rider.getSchedule().inOrder();

        if (!riderRides.empty()) {

            riderRides.findFirst();

            while (true) {

                IRide ride = riderRides.retrieve();

                sortedRides.insert(ride.getRideId(), ride);

                if (riderRides.last()) {
                    break;
                }

                riderRides.findNext();
            }
        }

        if (matchingRiders.last()) {
            break;
        }

        matchingRiders.findNext();
    }

    return sortedRides.inOrder();
}

    @Override
public LinkedList<IRider> getSharedRideParticipants(int rideId) {

    if (!ridesById.findKey(rideId)) {
        return new LinkedList<IRider>();
    }

    IRide ride = ridesById.retrieve();

    if (!(ride instanceof ISharedRide)) {
        return new LinkedList<IRider>();
    }

    return ((ISharedRide) ride).getParticipants();
}

    

    @Override
public int schedulePrivateRide(String pickupLocation,
                               String dropoffLocation,
                               ITimeSlot timeSlot,
                               int riderId, int driverId) {

    IRider rider = searchRiderById(riderId);
    IDriver driver = searchDriverById(driverId);

    if (rider == null || driver == null
            || timeSlot == null
            || pickupLocation == null
            || dropoffLocation == null) {
        return -1;
    }

    if (rider.getSchedule().findKey(timeSlot)
            || driver.getSchedule().findKey(timeSlot)) {
        return -1;
    }

    PrivateRide newRide = new PrivateRide(
            nextRideId,
            pickupLocation,
            dropoffLocation,
            timeSlot,
            driver,
            rider
    );

    addRideToIndexes(newRide);

    rider.getSchedule().insert(timeSlot, newRide);
    driver.getSchedule().insert(timeSlot, newRide);

    return nextRideId++;
}

    @Override
public int scheduleSharedRide(String pickupLocation,
                              String dropoffLocation,
                              ITimeSlot timeSlot,
                              int[] riderIds, int driverId) {

    IDriver driver = searchDriverById(driverId);

    if (driver == null || riderIds == null
            || riderIds.length < 2
            || timeSlot == null
            || pickupLocation == null
            || dropoffLocation == null) {
        return -1;
    }

    if (driver.getSchedule().findKey(timeSlot)) {
        return -1;
    }

    SharedRide newRide = new SharedRide(
            nextRideId,
            pickupLocation,
            dropoffLocation,
            timeSlot,
            driver
    );

    for (int riderId : riderIds) {

        IRider rider = searchRiderById(riderId);

        if (rider == null ||
                rider.getSchedule().findKey(timeSlot)) {
            return -1;
        }

        if (!newRide.addParticipant(rider)) {
            return -1;
        }
    }

    addRideToIndexes(newRide);

    driver.getSchedule().insert(timeSlot, newRide);

    for (int riderId : riderIds) {
        IRider rider = searchRiderById(riderId);
        rider.getSchedule().insert(timeSlot, newRide);
    }

    return nextRideId++;
}
private void addRideToIndexes(IRide ride) {

    int id = ride.getRideId();
    String location = ride.getPickupLocation();
    IDateTime time = ride.getTimeSlot().getStart();

    ridesById.insert(id, ride);

    if (!ridesByPickupLocation.findKey(location)) {
        ridesByPickupLocation.insert(location,
                new BST<Integer, IRide>());
    }

    ridesByPickupLocation.findKey(location);
    ridesByPickupLocation.retrieve().insert(id, ride);

    if (!ridesByPickupTime.findKey(time)) {
        ridesByPickupTime.insert(time,
                new BST<Integer, IRide>());
    }

    ridesByPickupTime.findKey(time);
    ridesByPickupTime.retrieve().insert(id, ride);
}
private void removeRideFromIndexes(IRide ride) {

    int id = ride.getRideId();
    String location = ride.getPickupLocation();
    IDateTime time = ride.getTimeSlot().getStart();

    ridesById.removeKey(id);

    if (ridesByPickupLocation.findKey(location)) {

        IBST<Integer, IRide> inner =
                ridesByPickupLocation.retrieve();

        inner.removeKey(id);

        if (inner.empty()) {
            ridesByPickupLocation.removeKey(location);
        }
    }

    if (ridesByPickupTime.findKey(time)) {

        IBST<Integer, IRide> inner =
                ridesByPickupTime.retrieve();

        inner.removeKey(id);

        if (inner.empty()) {
            ridesByPickupTime.removeKey(time);
        }
    }
}
@Override
public boolean cancelRide(int rideId) {

    if (!ridesById.findKey(rideId)) {
        return false;
    }

    IRide ride = ridesById.retrieve();
    ITimeSlot timeSlot = ride.getTimeSlot();

    ride.getDriver().getSchedule().removeKey(timeSlot);

    if (ride instanceof IPrivateRide) {

        IRider rider = ((IPrivateRide) ride).getRider();

        rider.getSchedule().removeKey(timeSlot);

    } else if (ride instanceof ISharedRide) {

        LinkedList<IRider> participants =
                ((ISharedRide) ride).getParticipants();

        if (!participants.empty()) {

            participants.findFirst();

            while (true) {

                participants.retrieve()
                        .getSchedule().removeKey(timeSlot);

                if (participants.last()) {
                    break;
                }

                participants.findNext();
            }
        }
    }

    removeRideFromIndexes(ride);

    return true;
}
@Override
    public LinkedList<IRide> searchRidesByPickupTimeRange(
            IDateTime from, IDateTime to) {

        LinkedList<IRide> result = new LinkedList<IRide>();

        if (from == null || to == null || from.compareTo(to) > 0) {
            return result;
        }

        LinkedList<IBST<Integer, IRide>> times =
                ridesByPickupTime.rangeSearch(from, to);

        if (times.empty()) {
            return result;
        }

        times.findFirst();

        while (true) {

            LinkedList<IRide> timeRides =
                    times.retrieve().inOrder();

            if (!timeRides.empty()) {

                timeRides.findFirst();

                while (true) {

                    result.insert(timeRides.retrieve());

                    if (timeRides.last()) {
                        break;
                    }

                    timeRides.findNext();
                }
            }

            if (times.last()) {
                break;
            }

            times.findNext();
        }

        return result;
    }
    @Override
public int loadRidesFromCSV(String ridesFilePath) {

    int count = 0;

    try (BufferedReader reader =
            new BufferedReader(new FileReader(ridesFilePath))) {

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            try {
                String[] data = line.split(",", -1);

                if (data.length != 7) {
                    continue;
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

                ITimeSlot timeSlot =
                        new TimeSlot(pickupTime, dropoffTime);

                int rideId;

                if (type.equalsIgnoreCase("PRIVATE")) {

                    int riderId =
                            Integer.parseInt(data[6].trim());

                    rideId = schedulePrivateRide(
                            pickupLocation,
                            dropoffLocation,
                            timeSlot,
                            riderId,
                            driverId
                    );

                } else if (type.equalsIgnoreCase("SHARED")) {

                    String[] ids = data[6].trim().split(";", -1);

                    int[] riderIds = new int[ids.length];

                    for (int i = 0; i < ids.length; i++) {
                        riderIds[i] =
                                Integer.parseInt(ids[i].trim());
                    }

                    rideId = scheduleSharedRide(
                            pickupLocation,
                            dropoffLocation,
                            timeSlot,
                            riderIds,
                            driverId
                    );

                } else {
                    continue;
                }

                if (rideId != -1) {
                    count++;
                }

            } catch (IllegalArgumentException |
                     IndexOutOfBoundsException e) {
                continue;
            }
        }

        return count;

    } catch (java.io.FileNotFoundException e) {
        return -1;
    } catch (java.io.IOException e) {
        return count;
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
    public int loadRidersFromCSV(String ridersFilePath) {

        if (ridersFilePath == null) {
            return -1;
        }

        BufferedReader reader;

        try {
            reader = new BufferedReader(
                    new FileReader(ridersFilePath));
        } catch (IOException e) {
            return -1;
        }

        int addedCount = 0;

        try (BufferedReader file = reader) {

            String line;

            while ((line = file.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 5) {
                    continue;
                }

                try {

                    int riderId =
                            Integer.parseInt(
                                    data[0].trim());

                    String name =
                            data[1].trim();

                    String email =
                            data[2].trim();

                    String phoneNumber =
                            data[3].trim();

                    String homeCity =
                            data[4].trim();

                    Rider rider =
                            new Rider(
                                    riderId,
                                    name,
                                    email,
                                    phoneNumber,
                                    homeCity
                            );

                    if (addRider(rider)) {
                        addedCount++;
                    }

                } catch (IllegalArgumentException e) {
                    continue;
                }
            }

        } catch (IOException e) {
            // Return the number already loaded
        }

        return addedCount;
    }

    @Override
    public boolean addRider(IRider rider) {

        if (rider == null
                || rider.getName() == null
                || rider.getHomeCity() == null) {
            return false;
        }

        if (ridersById.findKey(rider.getId())) {
            return false;
        }

        ridersById.insert(
                rider.getId(),
                rider
        );

        addRiderToGroupedIndex(
                ridersByName,
                rider.getName(),
                rider
        );

        addRiderToGroupedIndex(
                ridersByHomeCity,
                rider.getHomeCity(),
                rider
        );

        return true;
    }

    private void addRiderToGroupedIndex(
            IBST<String, IBST<Integer, IRider>> index,
            String key,
            IRider rider) {

        if (index.findKey(key)) {

            IBST<Integer, IRider> inner =
                    index.retrieve();

            inner.insert(
                    rider.getId(),
                    rider
            );

        } else {

            IBST<Integer, IRider> inner =
                    new BST<Integer, IRider>();

            inner.insert(
                    rider.getId(),
                    rider
            );

            index.insert(
                    key,
                    inner
            );
        }
    }

    @Override
    public IRider searchRiderById(int riderId) {

        if (ridersById.findKey(riderId)) {
            return ridersById.retrieve();
        }

        return null;
    }

    @Override
    public LinkedList<IRider> searchRidersByName(String fullName) {

        LinkedList<IRider> result =
                new LinkedList<IRider>();

        if (fullName == null) {
            return result;
        }

        if (!ridersByName.findKey(fullName)) {
            return result;
        }

        IBST<Integer, IRider> inner =
                ridersByName.retrieve();

        return inner.inOrder();
    }

    @Override
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {

        LinkedList<IRider> result =
                new LinkedList<IRider>();

        if (homeCity == null) {
            return result;
        }

        if (!ridersByHomeCity.findKey(homeCity)) {
            return result;
        }

        IBST<Integer, IRider> inner =
                ridersByHomeCity.retrieve();

        return inner.inOrder();
    }

    @Override
    public LinkedList<IRider> getAllRiders() {

        return ridersById.inOrder();
    }

    private void removeRiderFromGroupedIndex(
            IBST<String, IBST<Integer, IRider>> index,
            String key,
            int riderId) {

        if (!index.findKey(key)) {
            return;
        }

        IBST<Integer, IRider> inner =
                index.retrieve();

        inner.removeKey(riderId);

        if (inner.empty()) {
            index.removeKey(key);
        }
    }

    @Override
    public boolean removeRider(int riderId) {

        IRider rider =
                searchRiderById(riderId);

        if (rider == null) {
            return false;
        }

        LinkedList<IRide> riderRides =
                rider.getSchedule().inOrder();

        if (!riderRides.empty()) {

            riderRides.findFirst();

            while (true) {

                IRide ride =
                        riderRides.retrieve();

                if (ride instanceof IPrivateRide) {

                    cancelRide(
                            ride.getRideId()
                    );

                } else if (ride instanceof ISharedRide) {

                    ISharedRide sharedRide =
                            (ISharedRide) ride;

                    sharedRide.removeParticipantById(
                            riderId
                    );

                    rider.getSchedule().removeKey(
                            ride.getTimeSlot()
                    );

                    if (sharedRide.isEmpty()) {

                        cancelRide(
                                ride.getRideId()
                        );
                    }
                }

                if (riderRides.last()) {
                    break;
                }

                riderRides.findNext();
            }
        }

        ridersById.removeKey(
                riderId
        );

        removeRiderFromGroupedIndex(
                ridersByName,
                rider.getName(),
                riderId
        );

        removeRiderFromGroupedIndex(
                ridersByHomeCity,
                rider.getHomeCity(),
                riderId
        );

        return true;
    }

    @Override
    public int nbKeyCompRiderById(int riderId) {
        return ridersById.nbKeyComp(riderId);
    }

    @Override
    public int indexSize(IndexName index) {

        switch (index) {

            case RIDERS_BY_ID:
                return ridersById.size();

            case RIDERS_BY_NAME:
                return ridersByName.size();

            case RIDERS_BY_HOME_CITY:
                return ridersByHomeCity.size();

            case DRIVERS_BY_ID:
                return driversById.size();

            case DRIVERS_BY_PLATE:
                return driversByPlate.size();

            case DRIVERS_BY_VEHICLE_TYPE:
                return driversByVehicleType.size();

            case RIDES_BY_ID:
                return ridesById.size();

            case RIDES_BY_PICKUP_LOCATION:
                return ridesByPickupLocation.size();

            case RIDES_BY_PICKUP_TIME:
                return ridesByPickupTime.size();

            default:
                return 0;
        }
    }

    @Override
    public int indexHeight(IndexName index) {

        switch (index) {

            case RIDERS_BY_ID:
                return ridersById.height();

            case RIDERS_BY_NAME:
                return ridersByName.height();

            case RIDERS_BY_HOME_CITY:
                return ridersByHomeCity.height();

            case DRIVERS_BY_ID:
                return driversById.height();

            case DRIVERS_BY_PLATE:
                return driversByPlate.height();

            case DRIVERS_BY_VEHICLE_TYPE:
                return driversByVehicleType.height();

            case RIDES_BY_ID:
                return ridesById.height();

            case RIDES_BY_PICKUP_LOCATION:
                return ridesByPickupLocation.height();

            case RIDES_BY_PICKUP_TIME:
                return ridesByPickupTime.height();

            default:
                return 0;
        }
    }
    @Override
public int loadDriversFromCSV(String driversFilePath) {
    if (driversFilePath == null) {
        return -1;
    }

    BufferedReader reader;

    try {
        reader = new BufferedReader(new FileReader(driversFilePath));
    } catch (IOException e) {
        return -1;
    }

    int addedCount = 0;

    try (BufferedReader file = reader) {
        String line;

        while ((line = file.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",", -1);

            if (data.length != 5) {
                continue;
            }

            try {
                int driverId = Integer.parseInt(data[0].trim());
                String name = data[1].trim();
                String phoneNumber = data[2].trim();
                String vehiclePlate = data[3].trim();

                VehicleType vehicleType =
                        VehicleType.valueOf(data[4].trim());

                Driver driver = new Driver(
                        driverId,
                        name,
                        phoneNumber,
                        vehiclePlate,
                        vehicleType
                );

                if (addDriver(driver)) {
                    addedCount++;
                }
            } catch (IllegalArgumentException e) {
                // Skip an invalid record and continue reading.
                continue;
            }
        }
    } catch (IOException e) {
        // Keep the records already added if reading is interrupted.
    }

    return addedCount;
}

 @Override
public boolean addDriver(IDriver driver) {
    if (driver == null) {
        return false;
    }

    // Check uniqueness before changing any index.
    if (driversById.findKey(driver.getId())) {
        return false;
    }

    if (driversByPlate.findKey(driver.getVehiclePlate())) {
        return false;
    }

    driversById.insert(driver.getId(), driver);
    driversByPlate.insert(driver.getVehiclePlate(), driver);

    VehicleType type = driver.getVehicleType();

    if (driversByVehicleType.findKey(type)) {
        IBST<Integer, IDriver> typeDrivers =
                driversByVehicleType.retrieve();

        typeDrivers.insert(driver.getId(), driver);
    } else {
        IBST<Integer, IDriver> typeDrivers =
                new BST<Integer, IDriver>();

        typeDrivers.insert(driver.getId(), driver);
        driversByVehicleType.insert(type, typeDrivers);
    }

    return true;
}

    @Override
public IDriver searchDriverById(int driverId) {
    if (driversById.findKey(driverId)) {
        return driversById.retrieve();
    }

    return null;
}

    @Override
public LinkedList<IDriver> searchDriversByVehicleType(
        VehicleType vehicleType) {

    if (vehicleType == null
            || !driversByVehicleType.findKey(vehicleType)) {
        return new LinkedList<IDriver>();
    }

    IBST<Integer, IDriver> typeDrivers =
            driversByVehicleType.retrieve();

    return typeDrivers.inOrder();
}

@Override
public LinkedList<IDriver> getAllDrivers() {
    return driversById.inOrder();
}
@Override
public LinkedList<IDriver> findAvailableDrivers(
        ITimeSlot timeSlot, VehicleType vehicleType) {

    LinkedList<IDriver> availableDrivers =
            new LinkedList<IDriver>();

    if (timeSlot == null || vehicleType == null) {
        return availableDrivers;
    }

    LinkedList<IDriver> matchingDrivers =
            searchDriversByVehicleType(vehicleType);

    if (matchingDrivers.empty()) {
        return availableDrivers;
    }

    matchingDrivers.findFirst();

    while (true) {
        IDriver driver = matchingDrivers.retrieve();

        // Add the driver only if no scheduled ride overlaps this slot.
        if (!driver.getSchedule().findKey(timeSlot)) {
            availableDrivers.insert(driver);
        }

        if (matchingDrivers.last()) {
            break;
        }

        matchingDrivers.findNext();
    }

    return availableDrivers;
}
@Override
public boolean removeDriver(int driverId) {
    IDriver driver = searchDriverById(driverId);

    if (driver == null) {
        return false;
    }

    // Get the driver's rides before cancellation changes the schedule.
    LinkedList<IRide> driverRides = driver.getSchedule().inOrder();

    if (!driverRides.empty()) {
        driverRides.findFirst();

        while (true) {
            IRide ride = driverRides.retrieve();

            cancelRide(ride.getRideId());

            if (driverRides.last()) {
                break;
            }

            driverRides.findNext();
        }
    }

    // Remove the driver from the two unique-key indexes.
    driversById.removeKey(driverId);
    driversByPlate.removeKey(driver.getVehiclePlate());

    // Remove the driver from the inner vehicle-type tree.
    VehicleType type = driver.getVehicleType();

    if (driversByVehicleType.findKey(type)) {
        IBST<Integer, IDriver> typeDrivers =
                driversByVehicleType.retrieve();

        typeDrivers.removeKey(driverId);

        // Remove the outer key if this type has no drivers left.
        if (typeDrivers.empty()) {
            driversByVehicleType.removeKey(type);
        }
    }

    return true;
}
}