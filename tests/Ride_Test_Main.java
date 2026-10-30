import java.util.Scanner;

/** Manual system console plus isolated automatic ride/time/CSV tests. */
public class Ride_Test_Main {
    public static void main(String[] args) {
        if (args.length>0 && args[0].equals("--auto")) {
            TestSupport.reset(); runTests();
            if (TestSupport.summary()>0) System.exit(1);
            return;
        }
        RideSharingSystem system=TestSupport.system();
        try (Scanner in=new Scanner(System.in)) {
            while (true) {
                System.out.println("\nRide tests: synthetic riders 1..4, drivers 1..3; initially no rides.");
                System.out.println("1 Remove rider | 2 Remove driver | 3 List rides | 4 Find by pickup");
                System.out.println("5 Find by full rider name | 6 Shared participants | 7 Add ride");
                System.out.println("8 List riders | 9 List drivers | 10 Rider history | 11 Driver history");
                System.out.println("12 Load actual CSVs into fresh system | 90 Automatic tests | 91 Reset | 0 Exit");
                int choice=TestSupport.integer(in,"Choice: ");
                if (choice==0) return;
                try {
                    switch(choice) {
                    case 1: System.out.println("Removed: "+system.removeRider(TestSupport.integer(in,"Rider ID: "))); break;
                    case 2: System.out.println("Removed: "+system.removeDriver(TestSupport.integer(in,"Driver ID: "))); break;
                    case 3: TestSupport.print(system.getAllRidesAlphabetically()); break;
                    case 4: TestSupport.print(system.searchRidesByPickupLocation(TestSupport.text(in,"Pickup: "))); break;
                    case 5: TestSupport.print(system.searchRidesByRiderName(TestSupport.text(in,"Full name: "))); break;
                    case 6: TestSupport.print(system.getSharedRideParticipants(TestSupport.text(in,"Pickup: "))); break;
                    case 7: {
                        String type=TestSupport.text(in,"P private / S shared: ");
                        if (!type.equalsIgnoreCase("P")&&!type.equalsIgnoreCase("S"))
                            throw new IllegalArgumentException("Use P or S.");
                        String pickup=TestSupport.text(in,"Pickup: ");
                        IDateTime start=TestSupport.readTime(in,"Pickup time");
                        IDateTime end=TestSupport.readTime(in,"Dropoff time");
                        String dropoff=TestSupport.text(in,"Dropoff: ");
                        int driver=TestSupport.integer(in,"Driver ID: ");
                        boolean result;
                        if (type.equalsIgnoreCase("P")) {
                            int rider=TestSupport.integer(in,"Rider ID: ");
                            result=system.schedulePrivateRide(pickup,start,end,dropoff,rider,driver);
                        } else {
                            String[] values=TestSupport.text(in,"Rider IDs separated by ; (e.g. 1;2): ").split(";",-1);
                            int[] ids=new int[values.length];
                            for(int i=0;i<ids.length;i++) ids[i]=Integer.parseInt(values[i].trim());
                            // Pass one/duplicate IDs through intentionally: this console tests the backend.
                            result=system.scheduleSharedRide(pickup,start,end,dropoff,ids,driver);
                        }
                        System.out.println("Scheduled: "+result); break;
                    }
                    case 8: TestSupport.print(system.getAllRiders()); break;
                    case 9: TestSupport.print(system.getAllDrivers()); break;
                    case 10: {
                        IRider r=system.searchRiderById(TestSupport.integer(in,"Rider ID: "));
                        if (r==null) System.out.println("No rider."); else TestSupport.print(r.getRideHistory()); break;
                    }
                    case 11: {
                        IDriver d=system.searchDriverById(TestSupport.integer(in,"Driver ID: "));
                        if (d==null) System.out.println("No driver."); else TestSupport.print(d.getRideHistory()); break;
                    }
                    case 12: {
                        RideSharingSystem loaded=new RideSharingSystem();
                        boolean a=loaded.loadRidersFromCSV("data/riders_100.csv");
                        boolean b=loaded.loadDriversFromCSV("data/drivers_30.csv");
                        boolean c=loaded.loadRidesFromCSV("data/rides_40.csv");
                        System.out.println("Riders="+a+" Drivers="+b+" Rides="+c);
                        if(a&&b&&c) system=loaded;
                        else System.out.println("Manual system unchanged. Check working directory/files.");
                        break;
                    }
                    case 90: TestSupport.reset(); runTests(); TestSupport.summary(); break;
                    case 91: system=TestSupport.system(); System.out.println("Reset."); break;
                    default: System.out.println("Unknown option.");
                    }
                } catch (RuntimeException e) {
                    if(e instanceof java.util.NoSuchElementException) throw e;
                    System.out.println("Operation failed: "+e.getClass().getSimpleName()+": "+e.getMessage());
                }
            }
        } catch (java.util.NoSuchElementException e) { System.out.println("Input ended."); }
    }
    public static void runTests() {
        dateTests();
        modelTests();
        systemTests();
        csvTests();
    }
    private static PrivateRide ride(int id, String place) {
        return new PrivateRide(id,place,TestSupport.time(600),TestSupport.time(660),
                "End",TestSupport.driver(1,"Driver",VehicleType.SEDAN),
                TestSupport.rider(1,"Same Name","City"));
    }
    private static void dateTests() {
        TestSupport.test("DateTime: getters and format",()->{
            DateTime d=new DateTime(2026,2,3,4,5);
            TestSupport.eq(2026,d.getYear()); TestSupport.eq(2,d.getMonth());
            TestSupport.eq(3,d.getDay()); TestSupport.eq(4,d.getHour()); TestSupport.eq(5,d.getMinute());
            TestSupport.eq("02/03/2026 04:05",d.format());
        });
        int[][] values={{2026,1,1,0,0},{2026,1,1,0,1},{2026,1,1,1,0},
                {2026,1,2,0,0},{2026,2,1,0,0},{2027,1,1,0,0}};
        for(int i=0;i<values.length;i++) for(int j=0;j<values.length;j++) {
            final int left=i,right=j;
            TestSupport.test("DateTime: chronological "+i+" vs "+j,()->{
                int[] a=values[left],b=values[right];
                DateTime x=new DateTime(a[0],a[1],a[2],a[3],a[4]);
                DateTime y=new DateTime(b[0],b[1],b[2],b[3],b[4]);
                TestSupport.eq(Integer.signum(left-right),Integer.signum(x.compareTo(y)));
            });
        }
        int[][] bad={{2026,0,1,0,0},{2026,13,1,0,0},{2026,1,0,0,0},
                {2026,1,32,0,0},{2026,1,1,-1,0},{2026,1,1,24,0},
                {2026,1,1,0,-1},{2026,1,1,0,60}};
        for(int i=0;i<bad.length;i++) {
            final int[] v=bad[i];
            TestSupport.test("DateTime: reject out-of-range component "+i,()->
                TestSupport.illegal(()->new DateTime(v[0],v[1],v[2],v[3],v[4])));
        }
        TestSupport.test("DateTime: leap day valid",()->TestSupport.eq(29,new DateTime(2028,2,29,23,59).getDay()));
    }
    private static void modelTests() {
        TestSupport.test("Model: Person and Ride are abstract",()->{
            TestSupport.check(java.lang.reflect.Modifier.isAbstract(Person.class.getModifiers()),"Person must be abstract");
            TestSupport.check(java.lang.reflect.Modifier.isAbstract(Ride.class.getModifiers()),"Ride must be abstract");
        });
        TestSupport.test("PrivateRide: getters hasRider setters",()->{
            PrivateRide r=ride(10,"Alpha");
            TestSupport.eq(10,r.getRideId()); TestSupport.eq("Alpha",r.getPickupLocation());
            TestSupport.eq(0,r.getPickupTime().compareTo(TestSupport.time(600)));
            TestSupport.eq(0,r.getDropoffTime().compareTo(TestSupport.time(660)));
            TestSupport.eq("End",r.getDropoffLocation()); TestSupport.eq(1,r.getDriver().getId());
            TestSupport.eq(1,r.getRider().getId()); TestSupport.check(r.hasRider(1)&&!r.hasRider(2),"exact rider ID");
            r.setRider(TestSupport.rider(2,"Other","City"));
            r.setDriver(TestSupport.driver(2,"Other",VehicleType.VAN));
            r.setPickupLocation("Beta"); r.setDropoffLocation("New End");
            TestSupport.eq("Beta",r.getPickupLocation()); TestSupport.eq("New End",r.getDropoffLocation());
            TestSupport.eq(2,r.getDriver().getId()); TestSupport.check(r.hasRider(2)&&!r.hasRider(1),"replacement rider");
            TestSupport.eq(10,r.getRideId());
            TestSupport.eq(0,r.getPickupTime().compareTo(TestSupport.time(600)));
        });
        TestSupport.test("Ride: alphabetic comparison",()->{
            TestSupport.check(ride(1,"Alpha").compareTo(ride(2,"Zulu"))<0,"alphabetic pickup order");
            TestSupport.check(ride(2,"Zulu").compareTo(ride(1,"Alpha"))>0,"reverse pickup order");
            TestSupport.eq(0,ride(1,"Alpha").compareTo(ride(2,"Alpha")));
        });
        TestSupport.test("SharedRide: add find remove all positions",()->{
            SharedRide r=new SharedRide(20,"Start",TestSupport.time(600),TestSupport.time(660),
                    "End",TestSupport.driver(1,"D",VehicleType.VAN));
            TestSupport.check(r.isEmpty(),"initially empty");
            for(int i=1;i<=3;i++) TestSupport.check(r.addParticipant(TestSupport.rider(i,"N","C")),"participant added");
            TestSupport.personSet(r.getParticipants(),1,2,3);
            TestSupport.check(r.hasRider(2)&&!r.hasRider(9),"membership");
            TestSupport.check(r.removeParticipantById(2),"middle removed");
            TestSupport.personSet(r.getParticipants(),1,3);
            TestSupport.check(r.removeParticipantById(1),"head removed");
            TestSupport.check(!r.isEmpty(),"one retained");
            TestSupport.check(r.removeParticipantById(3),"tail removed");
            TestSupport.check(r.isEmpty(),"none remaining");
            TestSupport.check(!r.removeParticipantById(3),"repeat removal false");
        });
        TestSupport.test("RideList: sorted insertion and unique IDs",()->{
            RideList l=new RideList();
            TestSupport.eq(0,l.size()); TestSupport.eq(0,TestSupport.count(l.getAllAlphabetically()));
            TestSupport.eq(0,TestSupport.count(l.findByPickupLocation("none")));
            TestSupport.eq(0,TestSupport.count(l.findByRiderName("none")));
            TestSupport.check(!l.removeRideById(99),"missing false");
            l.addRide(ride(3,"Zulu")); l.addRide(ride(1,"Alpha")); l.addRide(ride(2,"Middle"));
            TestSupport.eq(3,l.size());
            TestSupport.eq("Alpha",TestSupport.at(l.getAllAlphabetically(),0).getPickupLocation());
            TestSupport.eq("Middle",TestSupport.at(l.getAllAlphabetically(),1).getPickupLocation());
            TestSupport.eq("Zulu",TestSupport.at(l.getAllAlphabetically(),2).getPickupLocation());
            TestSupport.check(!l.addRide(ride(2,"Other")),"duplicate ride ID");
            TestSupport.eq(3,l.size());
        });
        for(int id=1;id<=3;id++) {
            final int target=id;
            TestSupport.test("RideList: remove position "+id,()->{
                RideList l=new RideList(); l.addRide(ride(1,"Alpha")); l.addRide(ride(2,"Middle")); l.addRide(ride(3,"Zulu"));
                TestSupport.check(l.removeRideById(target),"existing removal true");
                TestSupport.eq(2,l.size()); TestSupport.eq(2,TestSupport.count(l.getAllAlphabetically()));
                for(int i=0;i<2;i++) TestSupport.check(TestSupport.at(l.getAllAlphabetically(),i).getRideId()!=target,"removed ID absent");
                TestSupport.check(!l.removeRideById(target),"repeat false");
            });
        }
        TestSupport.test("RideList: all matching pickups/full names",()->{
            RideList l=new RideList(); l.addRide(ride(1,"Same")); l.addRide(ride(2,"Same")); l.addRide(ride(3,"Else"));
            TestSupport.rideIds(l.findByPickupLocation("Same"),1,2);
            TestSupport.rideIds(l.findByRiderName("Same Name"),1,2,3);
            TestSupport.eq(0,TestSupport.count(l.findByRiderName("Same")));
        });
        TestSupport.test("RideList: singleton removal/reinsert",()->{
            RideList l=new RideList(); l.addRide(ride(1,"A")); l.removeRideById(1);
            TestSupport.eq(0,l.size()); TestSupport.check(l.getAllAlphabetically().empty(),"empty");
            TestSupport.check(l.addRide(ride(1,"A")),"reinsert");
        });
    }
    private static void systemTests() {
        TestSupport.test("System: empty searches and removal",()->{
            RideSharingSystem s=new RideSharingSystem();
            TestSupport.eq(0,TestSupport.count(s.getAllRiders())); TestSupport.eq(0,TestSupport.count(s.getAllDrivers()));
            TestSupport.eq(0,TestSupport.count(s.getAllRidesAlphabetically()));
            TestSupport.check(!s.removeRider(9)&&!s.removeDriver(9),"missing removals false");
            TestSupport.eq(0,TestSupport.count(s.getSharedRideParticipants("none")));
        });
        TestSupport.test("System: person search wrappers",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.eq(1,s.searchRiderById(1).getId());
            TestSupport.eq(1,s.searchRiderByEmail("r1@example.com").getId());
            TestSupport.personSet(s.searchRidersByName("Alpha Rider"),2,3);
            TestSupport.personSet(s.searchRidersByHomeCity("Riyadh"),1,3);
            TestSupport.eq(2,s.searchDriverById(2).getId());
            TestSupport.eq(2,s.searchDriverByVehiclePlate("ABC0002").getId());
            TestSupport.personSet(s.searchDriversByVehicleType(VehicleType.SUV),2);
        });
        TestSupport.test("System: reject duplicate rider ID/email and driver ID/plate",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.check(!s.addRider(TestSupport.rider(1,"Other","C")),"duplicate rider ID");
            TestSupport.check(!s.addRider(new Rider(9,"N","0500000000","r1@example.com","C")),"duplicate email");
            TestSupport.check(!s.addDriver(TestSupport.driver(1,"Other",VehicleType.VAN)),"duplicate driver ID");
            TestSupport.check(!s.addDriver(new Driver(9,"N","0500000000","ABC0001",VehicleType.VAN)),"duplicate plate");
            TestSupport.eq(4,TestSupport.count(s.getAllRiders())); TestSupport.eq(3,TestSupport.count(s.getAllDrivers()));
        });
        TestSupport.test("System: alphabetical riders (section 3)",()->TestSupport.alphabetic(TestSupport.system().getAllRiders()));
        TestSupport.test("System: alphabetical drivers (section 3)",()->TestSupport.alphabetic(TestSupport.system().getAllDrivers()));
        TestSupport.test("System: private and shared creation and unique ride IDs",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.check(TestSupport.privateRide(s,1,1,600,660),"private");
            TestSupport.check(TestSupport.sharedRide(s,new int[]{2,3},2,600,660),"shared");
            LinkedList<IRide> all=s.getAllRidesAlphabetically(); TestSupport.eq(2,TestSupport.count(all));
            TestSupport.check(TestSupport.at(all,0).getRideId()!=TestSupport.at(all,1).getRideId(),"unique ride IDs");
            TestSupport.check(TestSupport.at(all,0) instanceof IPrivateRide,"private type");
            TestSupport.check(TestSupport.at(all,1) instanceof ISharedRide,"shared type");
            TestSupport.personSet(s.getSharedRideParticipants("Shared"),2,3);
            TestSupport.eq(0,TestSupport.count(s.getSharedRideParticipants("Pickup")));
        });
        int[][] missing={{99,1},{1,99}};
        for(int[] v:missing) TestSupport.test("System: missing private rider/driver "+v[0]+"/"+v[1],()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.check(!TestSupport.privateRide(s,v[0],v[1],600,660),"must reject missing person");
            TestSupport.eq(0,TestSupport.count(s.getAllRidesAlphabetically()));
        });
        TestSupport.test("System: missing shared rider",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.check(!TestSupport.sharedRide(s,new int[]{1,99},1,600,660),"all riders must exist");
            TestSupport.eq(0,TestSupport.count(s.getAllRidesAlphabetically()));
        });
        TestSupport.test("System: missing shared driver",()->TestSupport.check(
                !TestSupport.sharedRide(TestSupport.system(),new int[]{1,2},99,600,660),"driver must exist"));
        TestSupport.test("System: one rider cannot CREATE shared ride (section 4)",()->TestSupport.check(
                !TestSupport.sharedRide(TestSupport.system(),new int[]{1},1,600,660),"expected false for one-rider shared creation"));
        TestSupport.test("System: zero riders rejected",()->TestSupport.check(
                !TestSupport.sharedRide(TestSupport.system(),new int[]{},1,600,660),"expected false for empty participants"));
        // Cover both containment directions, partial overlaps, equality and touching endpoints.
        int[][] intervals={{540,600},{660,720},{600,660},{610,650},{590,670},{590,610},{650,670}};
        for(int[] v:intervals) for(int who=0;who<3;who++) {
            final int collision=who;
            TestSupport.test("System: interval "+v[0]+"-"+v[1]+" collision mode "+who,()->{
                RideSharingSystem s=TestSupport.system();
                TestSupport.check(TestSupport.privateRide(s,1,1,600,660),"base ride");
                boolean overlaps=v[0]<660&&600<v[1];
                boolean actual=collision==0?TestSupport.privateRide(s,1,2,v[0],v[1])
                    :collision==1?TestSupport.privateRide(s,2,1,v[0],v[1])
                    :TestSupport.sharedRide(s,new int[]{2,1},2,v[0],v[1]);
                TestSupport.eq(!overlaps,actual);
                TestSupport.eq(overlaps?1:2,TestSupport.count(s.getAllRidesAlphabetically()));
            });
        }
        TestSupport.test("System: private detects an existing shared participant",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.sharedRide(s,new int[]{1,2},1,600,660);
            TestSupport.check(!TestSupport.privateRide(s,2,2,610,620),"shared rider conflict");
        });
        TestSupport.test("System: independent people may overlap",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            TestSupport.check(TestSupport.privateRide(s,2,2,600,660),"independent schedule");
        });
        TestSupport.test("System: full-name ride search returns each matching ride once",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.privateRide(s,2,1,600,660);
            TestSupport.sharedRide(s,new int[]{2,3},1,660,720);
            TestSupport.eq(2,TestSupport.count(s.searchRidesByRiderName("Alpha Rider")));
            TestSupport.eq(0,TestSupport.count(s.searchRidesByRiderName("Alpha")));
        });
        TestSupport.test("System: repeated pickup locations all returned in alphabetical list",()->{
            RideSharingSystem s=TestSupport.system();
            s.schedulePrivateRide("Zulu",TestSupport.time(600),TestSupport.time(660),"E",1,1);
            s.schedulePrivateRide("Alpha",TestSupport.time(660),TestSupport.time(720),"E",1,1);
            s.schedulePrivateRide("Alpha",TestSupport.time(720),TestSupport.time(780),"E",1,1);
            LinkedList<IRide> all=s.getAllRidesAlphabetically(); TestSupport.eq(3,TestSupport.count(all));
            TestSupport.eq("Alpha",TestSupport.at(all,0).getPickupLocation());
            TestSupport.eq("Alpha",TestSupport.at(all,1).getPickupLocation());
            TestSupport.eq("Zulu",TestSupport.at(all,2).getPickupLocation());
            TestSupport.eq(2,TestSupport.count(s.searchRidesByPickupLocation("Alpha")));
        });
        TestSupport.test("System: private rider removal cascades; unrelated ride survives",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.privateRide(s,1,1,600,660); TestSupport.privateRide(s,2,2,600,660);
            TestSupport.check(s.removeRider(1),"remove rider");
            TestSupport.eq(1,TestSupport.count(s.getAllRidesAlphabetically()));
            TestSupport.check(TestSupport.at(s.getAllRidesAlphabetically(),0).hasRider(2),"unrelated rider retained");
        });
        TestSupport.test("System: shared rider removal keeps one then deletes zero",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.sharedRide(s,new int[]{1,2},1,600,660);
            s.removeRider(1);
            TestSupport.eq(1,TestSupport.count(s.getAllRidesAlphabetically()));
            TestSupport.personSet(s.getSharedRideParticipants("Shared"),2);
            s.removeRider(2); TestSupport.eq(0,TestSupport.count(s.getAllRidesAlphabetically()));
        });
        TestSupport.test("System: driver removes private AND shared, preserves other driver",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.privateRide(s,1,1,600,660);
            TestSupport.sharedRide(s,new int[]{2,3},1,660,720);
            TestSupport.privateRide(s,4,2,600,660);
            TestSupport.check(s.removeDriver(1),"driver removed");
            TestSupport.eq(1,TestSupport.count(s.getAllRidesAlphabetically()));
            TestSupport.eq(2,TestSupport.at(s.getAllRidesAlphabetically(),0).getDriver().getId());
        });
        TestSupport.test("History: private ride recorded for rider and driver",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            int id=TestSupport.at(s.getAllRidesAlphabetically(),0).getRideId();
            TestSupport.rideIds(s.searchRiderById(1).getRideHistory(),id);
            TestSupport.rideIds(s.searchDriverById(1).getRideHistory(),id);
        });
        TestSupport.test("History: shared ride recorded for ALL participants and driver",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.sharedRide(s,new int[]{1,2},1,600,660);
            int id=TestSupport.at(s.getAllRidesAlphabetically(),0).getRideId();
            TestSupport.rideIds(s.searchRiderById(1).getRideHistory(),id);
            TestSupport.rideIds(s.searchRiderById(2).getRideHistory(),id);
            TestSupport.rideIds(s.searchDriverById(1).getRideHistory(),id);
        });
        TestSupport.test("History: driver cascade removes stale entries",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.sharedRide(s,new int[]{1,2},1,600,660);
            IRider r=s.searchRiderById(1); IDriver d=s.searchDriverById(1);
            TestSupport.eq(1,TestSupport.count(r.getRideHistory()));
            TestSupport.eq(1,TestSupport.count(d.getRideHistory()));
            s.removeDriver(1); TestSupport.eq(0,TestSupport.count(r.getRideHistory()));
            TestSupport.eq(0,TestSupport.count(d.getRideHistory()));
        });
        TestSupport.test("History: shared survivor history retained then cleared",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.sharedRide(s,new int[]{1,2},1,600,660);
            s.removeRider(1);
            TestSupport.eq(1,TestSupport.count(s.searchRiderById(2).getRideHistory()));
            s.removeRider(2); TestSupport.eq(0,TestSupport.count(s.searchDriverById(1).getRideHistory()));
        });
        TestSupport.test("History: private rider deletion clears driver and rider histories",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            IRider r=s.searchRiderById(1); IDriver d=s.searchDriverById(1);
            TestSupport.eq(1,TestSupport.count(r.getRideHistory()));
            TestSupport.eq(1,TestSupport.count(d.getRideHistory()));
            s.removeRider(1);
            TestSupport.eq(0,TestSupport.count(r.getRideHistory()));
            TestSupport.eq(0,TestSupport.count(d.getRideHistory()));
        });
        TestSupport.test("System: rejected conflict leaves all histories unchanged",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            TestSupport.eq(1,TestSupport.count(s.searchRiderById(1).getRideHistory()));
            TestSupport.check(!TestSupport.sharedRide(s,new int[]{1,2},2,610,620),"conflict rejected");
            TestSupport.eq(1,TestSupport.count(s.getAllRidesAlphabetically()));
            TestSupport.eq(1,TestSupport.count(s.searchRiderById(1).getRideHistory()));
            TestSupport.eq(0,TestSupport.count(s.searchRiderById(2).getRideHistory()));
            TestSupport.eq(0,TestSupport.count(s.searchDriverById(2).getRideHistory()));
        });
        TestSupport.test("System: removing rider frees driver interval",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            s.removeRider(1);
            TestSupport.check(TestSupport.privateRide(s,2,1,600,660),"deleted private ride must not block driver");
        });
        TestSupport.test("System: removing driver frees rider interval",()->{
            RideSharingSystem s=TestSupport.system(); TestSupport.privateRide(s,1,1,600,660);
            s.removeDriver(1);
            TestSupport.check(TestSupport.privateRide(s,1,2,600,660),"deleted ride must not block rider");
        });
        TestSupport.test("System: chronological conflict across midnight",()->{
            RideSharingSystem s=TestSupport.system();
            TestSupport.check(s.schedulePrivateRide("A",new DateTime(2026,12,31,23,30),
                    new DateTime(2027,1,1,0,30),"B",1,1),"cross-year ride");
            TestSupport.check(!s.schedulePrivateRide("A",new DateTime(2027,1,1,0,0),
                    new DateTime(2027,1,1,1,0),"B",1,2),"cross-year overlap");
            TestSupport.check(s.schedulePrivateRide("A",new DateTime(2027,1,1,0,30),
                    new DateTime(2027,1,1,1,0),"B",1,1),"cross-year touching allowed");
        });
    }
    private static void csvTests() {
        TestSupport.test("CSV: supplied files load 100 riders, 30 drivers, 40 rides",()->{
            RideSharingSystem s=new RideSharingSystem();
            TestSupport.check(s.loadRidersFromCSV("data/riders_100.csv"),"rider CSV load; run from repository root");
            TestSupport.check(s.loadDriversFromCSV("data/drivers_30.csv"),"driver CSV load");
            TestSupport.check(s.loadRidesFromCSV("data/rides_40.csv"),"ride CSV load");
            TestSupport.eq(100,TestSupport.count(s.getAllRiders()));
            TestSupport.eq(30,TestSupport.count(s.getAllDrivers()));
            TestSupport.eq(40,TestSupport.count(s.getAllRidesAlphabetically()));
        });
        TestSupport.test("CSV: missing file returns false",()->{
            RideSharingSystem s=new RideSharingSystem();
            String missing="tests/fixtures/DOES_NOT_EXIST_8bfb550b.csv";
            TestSupport.check(!s.loadRidersFromCSV(missing),"missing rider file");
            TestSupport.check(!s.loadDriversFromCSV(missing),"missing driver file");
            TestSupport.check(!s.loadRidesFromCSV(missing),"missing ride file");
        });
        TestSupport.test("CSV: exact field mapping",()->{
            RideSharingSystem s=new RideSharingSystem();
            TestSupport.check(s.loadRidersFromCSV("tests/fixtures/riders.csv"),"fixture load");
            TestSupport.check(s.loadDriversFromCSV("tests/fixtures/drivers.csv"),"fixture load");
            TestSupport.eq("First Rider",s.searchRiderById(1).getName());
            TestSupport.eq("one@example.com",s.searchRiderById(1).getEmail());
            TestSupport.eq("0500000001",s.searchRiderById(1).getPhoneNumber());
            TestSupport.eq("Riyadh",s.searchRiderById(1).getHomeCity());
            TestSupport.eq("XYZ1234",s.searchDriverById(1).getVehiclePlate());
            TestSupport.eq(VehicleType.SUV,s.searchDriverById(1).getVehicleType());
            TestSupport.check(s.loadRidesFromCSV("tests/fixtures/rides.csv"),"ride fixture load");
            TestSupport.eq(2,TestSupport.count(s.getAllRidesAlphabetically()));
            IRide r=TestSupport.at(s.searchRidesByPickupLocation("PrivateStart"),0);
            TestSupport.eq(1,r.getDriver().getId()); TestSupport.check(r.hasRider(1),"private rider");
            TestSupport.eq("01/02/2027 10:00",r.getPickupTime().format());
            TestSupport.eq("01/02/2027 11:00",r.getDropoffTime().format());
            TestSupport.eq("PrivateEnd",r.getDropoffLocation());
            TestSupport.personSet(s.getSharedRideParticipants("SharedStart"),1,2);
        });
        String[] badRiders={"rider_bad_phone","rider_duplicate_id","rider_duplicate_email"};
        for(String file:badRiders) TestSupport.test("CSV: rejects "+file,()->TestSupport.check(
            !new RideSharingSystem().loadRidersFromCSV("tests/fixtures/"+file+".csv"),"must return false"));
        String[] badDrivers={"driver_bad_phone","driver_bad_plate","driver_duplicate_id","driver_duplicate_plate","driver_bad_type"};
        for(String file:badDrivers) TestSupport.test("CSV: rejects "+file,()->TestSupport.check(
            !new RideSharingSystem().loadDriversFromCSV("tests/fixtures/"+file+".csv"),"must return false"));
        String[] badRides={"ride_missing_rider","ride_missing_driver","ride_missing_shared_rider",
                "ride_driver_conflict","ride_rider_conflict","ride_shared_conflict"};
        for(String file:badRides) TestSupport.test("CSV: rejects "+file,()->TestSupport.check(
            !TestSupport.system().loadRidesFromCSV("tests/fixtures/"+file+".csv"),"must reject nonexistent/conflicting participant"));
        TestSupport.test("CSV: rides cannot load before people",()->TestSupport.check(
                !new RideSharingSystem().loadRidesFromCSV("tests/fixtures/rides.csv"),"people must already exist"));
    }
}
