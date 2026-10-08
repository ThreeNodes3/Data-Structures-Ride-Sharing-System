public class DriverList implements IDriverList {

    private LinkedList<IDriver> drivers;
    private int count;

    public DriverList() {
        drivers = new LinkedList<IDriver>();
        count = 0;
    }

  
    public boolean add(IDriver driver) {
        if (driver == null || driver.getVehiclePlate() == null) {
            return false;
        }

        
        if (findById(driver.getId()) != null || findByVehiclePlate(driver.getVehiclePlate()) != null) {
            return false;
        }

        if (drivers.empty()) {
            drivers.insert(driver);
        } else {
            drivers.findFirst();

            
            while (!drivers.last()
                    && drivers.retrieve().compareTo(driver) < 0) {
                drivers.findNext();
            }

            if (drivers.retrieve().compareTo(driver) > 0) {
               
                IDriver oldDriver = drivers.retrieve();
                drivers.update(driver);
                drivers.insert(oldDriver);
            } else {
             
                drivers.insert(driver);
            }
        }

        count++;
        return true;
    }

    public IDriver findById(int driverId) {
        if (drivers.empty()) {
            return null;
        }

        drivers.findFirst();

        for (int i = 0; i < count; i++) {
            IDriver driver = drivers.retrieve();

            if (driver.getId() == driverId) {
                return driver;
            }

            if (i < count - 1) {
                drivers.findNext();
            }
        }

        return null;
    }

    public LinkedList<IDriver> findByName(String fullName) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();

        if (drivers.empty() || fullName == null) {
            return result;
        }

        drivers.findFirst();

        for (int i = 0; i < count; i++) {
            IDriver driver = drivers.retrieve();

            if (fullName.equals(driver.getName())) {
                result.insert(driver);
            }

            if (i < count - 1) {
                drivers.findNext();
            }
        }

        return result;
    }

    public IDriver findByVehiclePlate(String vehiclePlate) {
        if (drivers.empty() || vehiclePlate == null) {
            return null;
        }

        drivers.findFirst();

        for (int i = 0; i < count; i++) {
            IDriver driver = drivers.retrieve();

            if (vehiclePlate.equals(driver.getVehiclePlate())) {
                return driver;
            }

            if (i < count - 1) {
                drivers.findNext();
            }
        }

        return null;
    }

    public LinkedList<IDriver> findByVehicleType(VehicleType vehicleType) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();

        if (drivers.empty() || vehicleType == null) {
            return result;
        }

        drivers.findFirst();

        for (int i = 0; i < count; i++) {
            IDriver driver = drivers.retrieve();

            if (driver.getVehicleType() == vehicleType) {
                result.insert(driver);
            }

            if (i < count - 1) {
                drivers.findNext();
            }
        }

        return result;
    }

  
    public LinkedList<IDriver> getAll() {
        LinkedList<IDriver> result = new LinkedList<IDriver>();

        if (drivers.empty()) {
            return result;
        }

        drivers.findFirst();

        for (int i = 0; i < count; i++) {
            result.insert(drivers.retrieve());

            if (i < count - 1) {
                drivers.findNext();
            }
        }

        return result;
    }


    public boolean removeById(int driverId) {
        if (findById(driverId) == null) {
            return false;
        }

     
        drivers.remove();
        count--;

        return true;
    }

  
    public boolean removeByVehiclePlate(String vehiclePlate) {
        if (findByVehiclePlate(vehiclePlate) == null) {
            return false;
        }

      
        drivers.remove();
        count--;

        return true;
    }

  
    public int removeByName(String fullName) {
        if (drivers.empty() || fullName == null) {
            return 0;
        }

        int originalSize = count;
        int removed = 0;

        drivers.findFirst();

        for (int i = 0; i < originalSize; i++) {
            if (fullName.equals(drivers.retrieve().getName())) {
                drivers.remove();
                count--;
                removed++;
            } else if (!drivers.last()) {
                drivers.findNext();
            }
        }

        return removed;
    }

    public int removeByVehicleType(VehicleType vehicleType) {
        if (drivers.empty() || vehicleType == null) {
            return 0;
        }

        int originalSize = count;
        int removed = 0;

        drivers.findFirst();

        for (int i = 0; i < originalSize; i++) {
            if (drivers.retrieve().getVehicleType() == vehicleType) {
                drivers.remove();
                count--;
                removed++;
            } else if (!drivers.last()) {
                drivers.findNext();
            }
        }

        return removed;
    }

   
    public int size() {
        return count;
    }
}