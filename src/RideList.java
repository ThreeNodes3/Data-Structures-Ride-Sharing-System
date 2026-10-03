public class RideList implements IRideList {

    private LinkedList<IRide> rides;
    private int count;

    public RideList() {
        rides = new LinkedList<IRide>();
        count = 0;
    }

    @Override
    public boolean addRide(IRide ride) {

        if (ride == null) {
            return false;
        }

        // Ride IDs must be unique
        if (!rides.empty()) {
            rides.findFirst();

            while (true) {
                if (rides.retrieve().getRideId() == ride.getRideId()) {
                    return false;
                }

                if (rides.last()) {
                    break;
                }

                rides.findNext();
            }
        }

        // First ride
        if (rides.empty()) {
            rides.insert(ride);
            count++;
            return true;
        }

        // Find correct alphabetical position
        rides.findFirst();

        while (true) {

            if (ride.compareTo(rides.retrieve()) < 0) {

                IRide temp = rides.retrieve();

                rides.update(ride);
                rides.insert(temp);

                count++;
                return true;
            }

            if (rides.last()) {
                rides.insert(ride);
                count++;
                return true;
            }

            rides.findNext();
        }
    }

    @Override
    public boolean removeRideById(int rideId) {

        if (rides.empty()) {
            return false;
        }

        rides.findFirst();

        while (true) {

            if (rides.retrieve().getRideId() == rideId) {
                rides.remove();
                count--;
                return true;
            }

            if (rides.last()) {
                break;
            }

            rides.findNext();
        }

        return false;
    }

    @Override
    public LinkedList<IRide> getAllAlphabetically() {
        return rides;
    }

    @Override
    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {

        LinkedList<IRide> result = new LinkedList<IRide>();

        if (rides.empty()) {
            return result;
        }

        rides.findFirst();

        while (true) {

            IRide ride = rides.retrieve();

            if (ride.getPickupLocation().equals(pickupLocation)) {

                if (result.empty()) {
                    result.insert(ride);
                }
                else {
                    result.findFirst();

                    while (!result.last()) {
                        result.findNext();
                    }

                    result.insert(ride);
                }
            }

            if (rides.last()) {
                break;
            }

            rides.findNext();
        }

        return result;
    }

    @Override
    public LinkedList<IRide> findByRiderName(String riderFullName) {

        LinkedList<IRide> result = new LinkedList<IRide>();

        if (rides.empty()) {
            return result;
        }

        rides.findFirst();

        while (true) {

            IRide ride = rides.retrieve();
            boolean found = false;

            // Private ride
            if (ride instanceof IPrivateRide) {

                IPrivateRide privateRide = (IPrivateRide) ride;
                IRider rider = privateRide.getRider();

                if (rider != null &&
                    rider.getName().equals(riderFullName)) {

                    found = true;
                }
            }

            // Shared ride
            else if (ride instanceof ISharedRide) {

                ISharedRide sharedRide = (ISharedRide) ride;
                LinkedList<IRider> participants =
                    sharedRide.getParticipants();

                if (!participants.empty()) {

                    participants.findFirst();

                    while (true) {

                        if (participants.retrieve().getName()
                                .equals(riderFullName)) {

                            found = true;
                            break;
                        }

                        if (participants.last()) {
                            break;
                        }

                        participants.findNext();
                    }
                }
            }

            if (found) {

                if (result.empty()) {
                    result.insert(ride);
                }
                else {
                    result.findFirst();

                    while (!result.last()) {
                        result.findNext();
                    }

                    result.insert(ride);
                }
            }

            if (rides.last()) {
                break;
            }

            rides.findNext();
        }

        return result;
    }

    @Override
    public int size() {
        return count;
    }
}