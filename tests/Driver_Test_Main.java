import java.util.Scanner;

/** Development test console; keep in tests/, not the submitted application. */
public class Driver_Test_Main {
    private static DriverList seed() {
        DriverList list=new DriverList();
        IDriver[] people={TestSupport.driver(3,"Same Name",VehicleType.SUV), TestSupport.driver(1,"Same Name",VehicleType.SUV), TestSupport.driver(2,"Other Name",VehicleType.VAN)};
        for (IDriver p : people) list.add(p);
        return list;
    }
    public static void main(String[] args) {
        if (args.length>0 && args[0].equals("--auto")) {
            TestSupport.reset(); runTests();
            if (TestSupport.summary()>0) System.exit(1);
            return;
        }
        DriverList list=seed();
        try (Scanner in=new Scanner(System.in)) {
            while (true) {
                System.out.println("\nDriver tests (starts with synthetic IDs 1, 2, 3)");
                System.out.println("1 List | 2 Find by VehiclePlate | 3 Find by full name | 4 Find by ID");
                System.out.println("5 Find by VehicleType | 6 Add | 7 Remove by VehiclePlate");
                System.out.println("8 Remove ALL by full name | 9 Remove by ID | 10 Remove ALL by VehicleType");
                System.out.println("11 Size | 12 Load CSV into fresh list | 90 Automatic tests | 91 Reset | 0 Exit");
                int choice=TestSupport.integer(in,"Choice: ");
                if (choice==0) return;
                try {
                    switch (choice) {
                    case 1: TestSupport.print(list.getAll()); break;
                    case 2: System.out.println(list.findByVehiclePlate(TestSupport.text(in,"VehiclePlate: "))); break;
                    case 3: TestSupport.print(list.findByName(TestSupport.text(in,"Full name: "))); break;
                    case 4: System.out.println(list.findById(TestSupport.integer(in,"ID: "))); break;
                    case 5: TestSupport.print(list.findByVehicleType(TestSupport.vehicle(in))); break;
                    case 6: { int id=TestSupport.integer(in,"ID: ");
                        String name=TestSupport.text(in,"Full name: ");
                        String phone=TestSupport.text(in,"Phone: ");
                        String plate=TestSupport.text(in,"Plate: ");
                        VehicleType type=TestSupport.vehicle(in);
                        System.out.println("Added: "+list.add(new Driver(id,name,phone,plate,type))); break; }
                    case 7: System.out.println("Removed: "+list.removeByVehiclePlate(TestSupport.text(in,"VehiclePlate: "))); break;
                    case 8: System.out.println("Removed count: "+list.removeByName(TestSupport.text(in,"Full name: "))); break;
                    case 9: System.out.println("Removed: "+list.removeById(TestSupport.integer(in,"ID: "))); break;
                    case 10: System.out.println("Removed count: "+list.removeByVehicleType(TestSupport.vehicle(in))); break;
                    case 11: System.out.println("Size: "+list.size()); break;
                    case 12: {
                        RideSharingSystem loader=new RideSharingSystem();
                        String path=TestSupport.text(in,"CSV path (e.g. data/drivers_30.csv): ");
                        if (!loader.loadDriversFromCSV(path)) { System.out.println("Load failed; manual list unchanged."); break; }
                        DriverList loaded=new DriverList();
                        LinkedList<IDriver> people=loader.getAllDrivers();
                        int size=TestSupport.count(people);
                        for (int i=0;i<size;i++) loaded.add(TestSupport.at(people,i));
                        list=loaded; System.out.println("Loaded: "+list.size()); break;
                    }
                    case 90: TestSupport.reset(); runTests(); TestSupport.summary(); break;
                    case 91: list=seed(); System.out.println("Reset."); break;
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
        TestSupport.test("Driver: constructor getters", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.eq(1,p.getId()); TestSupport.eq("Name",p.getName()); TestSupport.eq("0500000000",p.getPhoneNumber()); TestSupport.eq(0,TestSupport.count(p.getRideHistory())); TestSupport.check(p.toString()!=null&&!p.toString().isEmpty(),"formatted toString required"); });
        TestSupport.test("Driver: setters preserve identity", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); p.setName("Changed"); p.setPhoneNumber("0599999999"); p.setVehiclePlate("XYZ9999"); p.setVehicleType(VehicleType.LUXURY_SEDAN); TestSupport.eq("XYZ9999",p.getVehiclePlate()); TestSupport.eq(VehicleType.LUXURY_SEDAN,p.getVehicleType()); TestSupport.eq(1,p.getId()); TestSupport.eq("Changed",p.getName()); TestSupport.eq("0599999999",p.getPhoneNumber()); });
        TestSupport.test("Driver: compareTo negative zero positive", () -> { IDriver a=TestSupport.at(seed().getAll(),0), b=TestSupport.at(seed().getAll(),2); TestSupport.check(a.compareTo(b)<0,"smaller ID"); TestSupport.check(b.compareTo(a)>0,"larger ID"); TestSupport.eq(0,a.compareTo(TestSupport.driver(1,"Name",VehicleType.SEDAN))); });
        TestSupport.test("Driver: invalid phone null", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber(null)); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid phone ", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber("")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid phone 123", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber("123")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid phone 05000000000", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber("05000000000")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid phone 050000000x", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber("050000000x")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid phone 050 000000", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setPhoneNumber("050 000000")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Driver: invalid constructor phone", () -> { TestSupport.illegal(() -> new Driver(1,"N","bad","ABC1234",VehicleType.SEDAN)); });
        TestSupport.test("Driver: invalid plate null", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate(null)); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate ", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate AB1234", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("AB1234")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate ABCD1234", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("ABCD1234")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate abc1234", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("abc1234")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate AB11234", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("AB11234")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate ABC12X4", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("ABC12X4")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid plate ABC12345", () -> { Driver p=TestSupport.driver(1,"Name",VehicleType.SEDAN); TestSupport.illegal(() -> p.setVehiclePlate("ABC12345")); TestSupport.eq("ABC0001",p.getVehiclePlate()); });
        TestSupport.test("Driver: invalid constructor plate", () -> { TestSupport.illegal(() -> new Driver(1,"N","0500000000","bad",VehicleType.SEDAN)); });
        TestSupport.test("Driver: all enum values", () -> { for (VehicleType type : VehicleType.values()) TestSupport.eq(type,TestSupport.driver(1,"N",type).getVehicleType()); TestSupport.eq(4,VehicleType.values().length); });
        TestSupport.test("Driver: empty searches and removals", () -> { DriverList l=new DriverList(); TestSupport.eq(0,l.size()); TestSupport.eq(null,l.findById(7)); TestSupport.eq(null,l.findByVehiclePlate("missing")); TestSupport.eq(0,TestSupport.count(l.findByName("missing"))); TestSupport.eq(0,TestSupport.count(l.findByVehicleType(VehicleType.SEDAN))); TestSupport.check(!l.removeById(7),"missing ID false"); TestSupport.check(!l.removeByVehiclePlate("missing"),"missing unique false"); TestSupport.eq(0,l.removeByName("missing")); TestSupport.eq(0,l.removeByVehicleType(VehicleType.SEDAN)); });
        TestSupport.test("Driver: sorted insertion and size", () -> { DriverList l=seed(); TestSupport.personIds(l.getAll(),1,2,3); TestSupport.eq(3,l.size()); });
        TestSupport.test("Driver: duplicate ID rejected without loss", () -> { DriverList l=seed(); TestSupport.check(!l.add(TestSupport.driver(1,"Name",VehicleType.SEDAN)),"duplicate ID must return false"); TestSupport.personIds(l.getAll(),1,2,3); });
        TestSupport.test("Driver: duplicate VehiclePlate rejected", () -> { DriverList l=seed(); TestSupport.check(!l.add(new Driver(9,"N","0500000000","ABC0001",VehicleType.VAN)),"duplicate unique field must return false"); TestSupport.eq(3,l.size()); });
        TestSupport.test("Driver: find by ID and VehiclePlate", () -> { DriverList l=seed(); TestSupport.eq(2,l.findById(2).getId()); TestSupport.eq(1,l.findByVehiclePlate("ABC0001").getId()); });
        TestSupport.test("Driver: all full-name matches; no substring", () -> { DriverList l=seed(); TestSupport.personSet(l.findByName("Same Name"),1,3); TestSupport.eq(0,TestSupport.count(l.findByName("Same"))); });
        TestSupport.test("Driver: all VehicleType matches", () -> { TestSupport.personSet(seed().findByVehicleType(VehicleType.SUV),1,3); });
        TestSupport.test("Driver: remove ID 1", () -> { DriverList l=seed(); TestSupport.check(l.removeById(1),"existing ID removed"); TestSupport.personIds(l.getAll(),2,3); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(1),"repeat false"); });
        TestSupport.test("Driver: remove ID 2", () -> { DriverList l=seed(); TestSupport.check(l.removeById(2),"existing ID removed"); TestSupport.personIds(l.getAll(),1,3); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(2),"repeat false"); });
        TestSupport.test("Driver: remove ID 3", () -> { DriverList l=seed(); TestSupport.check(l.removeById(3),"existing ID removed"); TestSupport.personIds(l.getAll(),1,2); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(3),"repeat false"); });
        TestSupport.test("Driver: remove by VehiclePlate", () -> { DriverList l=seed(); TestSupport.check(l.removeByVehiclePlate("ABC0002"),"existing unique removed"); TestSupport.personIds(l.getAll(),1,3); });
        TestSupport.test("Driver: remove full name only", () -> { DriverList l=seed(); TestSupport.eq(0,l.removeByName("Same")); TestSupport.eq(2,l.removeByName("Same Name")); TestSupport.personIds(l.getAll(),2); });
        TestSupport.test("Driver: singleton delete and reinsert", () -> { DriverList l=new DriverList(); l.add(TestSupport.driver(1,"Name",VehicleType.SEDAN)); TestSupport.check(l.removeById(1),"singleton removed"); TestSupport.eq(0,l.size()); TestSupport.check(l.getAll().empty(),"empty list"); TestSupport.check(l.add(TestSupport.driver(1,"Name",VehicleType.SEDAN)),"reinsert"); TestSupport.eq(1,l.size()); });
        for (int mask=0; mask<32; mask++) {
            final int pattern=mask;
            for (int mode=0;mode<2;mode++) {
                final boolean byName=mode==0;
                TestSupport.test("Driver: remove all "+(byName?"name":"VehicleType")+" pattern "+pattern, () -> {
                    DriverList list=new DriverList();
                    int expected=0;
                    for (int i=1;i<=5;i++) {
                        boolean match=(pattern & (1<<(i-1)))!=0;
                        if (match) expected++;
                        String name=match?"Match Name":"Other Name";
                        list.add(TestSupport.driver(i, name, match ? VehicleType.SUV : VehicleType.VAN));
                    }
                    int removed=byName?list.removeByName("Match Name"):list.removeByVehicleType(VehicleType.SUV);
                    TestSupport.eq(expected,removed);
                    TestSupport.eq(5-expected,list.size());
                    TestSupport.eq(5-expected,TestSupport.count(list.getAll()));
                    for (int i=1;i<=5;i++)
                        TestSupport.eq((pattern & (1<<(i-1)))==0,list.findById(i)!=null);
                    TestSupport.eq(0,byName?list.removeByName("Match Name"):list.removeByVehicleType(VehicleType.SUV));
                });
            }
        }

    }
}
