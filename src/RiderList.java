public class RiderList implements IRiderList{

    private LinkedList<IRider>riders;
    private int size;

    public RiderList(){

        riders=new LinkedList<IRider>();
        size=0;
    }
/// /////////////////////////////////////////////////////////////
    @Override
    public boolean add(IRider rider) {

        if (rider == null) {
            return false;
        }

        if (riders.empty()) {
            riders.insert(rider);
            size++;
            return true;
        }

        if (findById(rider.getId()) != null) {
            return false;
        }

        if (findByEmail(rider.getEmail()) != null) {
            return false;
        }

        LinkedList<IRider> newList = new LinkedList<IRider>();

        riders.findFirst();

        boolean inserted = false;

        while (true) {

            IRider currentRider = riders.retrieve();

            if (!inserted && rider.getId() < currentRider.getId()) {
                newList.insert(rider);
                inserted = true;
            }

            newList.insert(currentRider);

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        if (!inserted) {
            newList.insert(rider);
        }

        riders = newList;
        size++;

        return true;
    }

/// //////////////////////////////////////////////////////

    @Override
    public IRider findById(int riderId) {

        if (riders.empty()) {
            return null;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getId() == riderId) {
                return rider;
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return null;
    }////////////////////////////////////////////////////

    @Override
    public IRider findByEmail(String email) {

        if (riders.empty()) {
            return null;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getEmail().equals(email)) {
                return rider;
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return null;
    }///////////////////////////////////////////////////


    @Override
    public LinkedList<IRider> findByName(String fullName) {

        LinkedList<IRider> result = new LinkedList<IRider>();

        if (riders.empty()) {
            return result;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getName().equals(fullName)) {
                result.insert(rider);
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return result;
    }////////////////////////////////////////////

    @Override
    public LinkedList<IRider> findByHomeCity(String homeCity) {

        LinkedList<IRider> result = new LinkedList<IRider>();

        if (riders.empty()) {
            return result;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getHomeCity().equals(homeCity)) {
                result.insert(rider);
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return result;
    }///////////////////////////////////////////////////

    @Override
    public LinkedList<IRider> getAll() {

        LinkedList<IRider> result = new LinkedList<IRider>();

        if (riders.empty()) {
            return result;
        }

        riders.findFirst();

        while (true) {

            result.insert(riders.retrieve());

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return result;
    }/////////////////////////////////////////////

    @Override
    public boolean removeById(int riderId) {

        if (riders.empty()) {
            return false;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getId() == riderId) {
                riders.remove();
                size--;
                return true;
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return false;
    }//////////////////////////////////////////////

    @Override
    public boolean removeByEmail(String email) {

        if (riders.empty()) {
            return false;
        }

        riders.findFirst();

        while (true) {

            IRider rider = riders.retrieve();

            if (rider.getEmail().equals(email)) {
                riders.remove();
                size--;
                return true;
            }

            if (riders.last()) {
                break;
            }

            riders.findNext();
        }

        return false;
    }//////////////////////////////////////////////////////////

    @Override
    public int removeByHomeCity(String homeCity) {

        int removed = 0;

        if (riders.empty()) {
            return removed;
        }

        riders.findFirst();

        while (!riders.empty()) {

            IRider rider = riders.retrieve();

            if (rider.getHomeCity().equals(homeCity)) {

                boolean wasLast = riders.last();

                riders.remove();
                size--;
                removed++;

                if (riders.empty() || wasLast) {
                    break;
                }

            } else {

                if (riders.last()) {
                    break;
                }

                riders.findNext();
            }
        }

        return removed;
    }///////////////////////////////////////////////////////////


    @Override
    public int removeByName(String fullName) {

        int removed = 0;

        if (riders.empty()) {
            return removed;
        }

        riders.findFirst();

        while (!riders.empty()) {

            IRider rider = riders.retrieve();

            if (rider.getName().equals(fullName)) {

                boolean wasLast = riders.last();

                riders.remove();
                size--;
                removed++;

                if (riders.empty() || wasLast) {
                    break;
                }

            } else {

                if (riders.last()) {
                    break;
                }

                riders.findNext();
            }
        }

        return removed;
    }///////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }


}


