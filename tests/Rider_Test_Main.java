import java.util.Scanner;

/** Development test console; keep in tests/, not the submitted application. */
public class Rider_Test_Main {
    private static RiderList seed() {
        RiderList list=new RiderList();
        IRider[] people={TestSupport.rider(3,"Same Name","Riyadh"), TestSupport.rider(1,"Same Name","Riyadh"), TestSupport.rider(2,"Other Name","Jeddah")};
        for (IRider p : people) list.add(p);
        return list;
    }
    public static void main(String[] args) {
        if (args.length>0 && args[0].equals("--auto")) {
            TestSupport.reset(); runTests();
            if (TestSupport.summary()>0) System.exit(1);
            return;
        }
        RiderList list=seed();
        try (Scanner in=new Scanner(System.in)) {
            while (true) {
                System.out.println("\nRider tests (starts with synthetic IDs 1, 2, 3)");
                System.out.println("1 List | 2 Find by Email | 3 Find by full name | 4 Find by ID");
                System.out.println("5 Find by HomeCity | 6 Add | 7 Remove by Email");
                System.out.println("8 Remove ALL by full name | 9 Remove by ID | 10 Remove ALL by HomeCity");
                System.out.println("11 Size | 12 Load CSV into fresh list | 90 Automatic tests | 91 Reset | 0 Exit");
                int choice=TestSupport.integer(in,"Choice: ");
                if (choice==0) return;
                try {
                    switch (choice) {
                    case 1: TestSupport.print(list.getAll()); break;
                    case 2: System.out.println(list.findByEmail(TestSupport.text(in,"Email: "))); break;
                    case 3: TestSupport.print(list.findByName(TestSupport.text(in,"Full name: "))); break;
                    case 4: System.out.println(list.findById(TestSupport.integer(in,"ID: "))); break;
                    case 5: TestSupport.print(list.findByHomeCity(TestSupport.text(in,"Home city: "))); break;
                    case 6: { int id=TestSupport.integer(in,"ID: ");
                        String name=TestSupport.text(in,"Full name: ");
                        String phone=TestSupport.text(in,"Phone: ");
                        String email=TestSupport.text(in,"Email: ");
                        String city=TestSupport.text(in,"City: ");
                        System.out.println("Added: "+list.add(new Rider(id,name,phone,email,city))); break; }
                    case 7: System.out.println("Removed: "+list.removeByEmail(TestSupport.text(in,"Email: "))); break;
                    case 8: System.out.println("Removed count: "+list.removeByName(TestSupport.text(in,"Full name: "))); break;
                    case 9: System.out.println("Removed: "+list.removeById(TestSupport.integer(in,"ID: "))); break;
                    case 10: System.out.println("Removed count: "+list.removeByHomeCity(TestSupport.text(in,"Home city: "))); break;
                    case 11: System.out.println("Size: "+list.size()); break;
                    case 12: {
                        RideSharingSystem loader=new RideSharingSystem();
                        String path=TestSupport.text(in,"CSV path (e.g. data/riders_100.csv): ");
                        if (!loader.loadRidersFromCSV(path)) { System.out.println("Load failed; manual list unchanged."); break; }
                        RiderList loaded=new RiderList();
                        LinkedList<IRider> people=loader.getAllRiders();
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
        TestSupport.test("Rider: constructor getters", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.eq(1,p.getId()); TestSupport.eq("Name",p.getName()); TestSupport.eq("0500000000",p.getPhoneNumber()); TestSupport.eq(0,TestSupport.count(p.getRideHistory())); TestSupport.check(p.toString()!=null&&!p.toString().isEmpty(),"formatted toString required"); });
        TestSupport.test("Rider: setters preserve identity", () -> { Rider p=TestSupport.rider(1,"Name","City"); p.setName("Changed"); p.setPhoneNumber("0599999999"); p.setEmail("changed@example.com"); p.setHomeCity("NewCity"); TestSupport.eq("changed@example.com",p.getEmail()); TestSupport.eq("NewCity",p.getHomeCity()); TestSupport.eq(1,p.getId()); TestSupport.eq("Changed",p.getName()); TestSupport.eq("0599999999",p.getPhoneNumber()); });
        TestSupport.test("Rider: compareTo negative zero positive", () -> { IRider a=TestSupport.at(seed().getAll(),0), b=TestSupport.at(seed().getAll(),2); TestSupport.check(a.compareTo(b)<0,"smaller ID"); TestSupport.check(b.compareTo(a)>0,"larger ID"); TestSupport.eq(0,a.compareTo(TestSupport.rider(1,"Name","City"))); });
        TestSupport.test("Rider: invalid phone null", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber(null)); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid phone ", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber("")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid phone 123", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber("123")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid phone 05000000000", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber("05000000000")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid phone 050000000x", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber("050000000x")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid phone 050 000000", () -> { Rider p=TestSupport.rider(1,"Name","City"); TestSupport.illegal(() -> p.setPhoneNumber("050 000000")); TestSupport.eq("0500000000",p.getPhoneNumber()); });
        TestSupport.test("Rider: invalid constructor phone", () -> { TestSupport.illegal(() -> new Rider(1,"N","bad","a@b","C")); });
        TestSupport.test("Rider: empty searches and removals", () -> { RiderList l=new RiderList(); TestSupport.eq(0,l.size()); TestSupport.eq(null,l.findById(7)); TestSupport.eq(null,l.findByEmail("missing")); TestSupport.eq(0,TestSupport.count(l.findByName("missing"))); TestSupport.eq(0,TestSupport.count(l.findByHomeCity("missing"))); TestSupport.check(!l.removeById(7),"missing ID false"); TestSupport.check(!l.removeByEmail("missing"),"missing unique false"); TestSupport.eq(0,l.removeByName("missing")); TestSupport.eq(0,l.removeByHomeCity("missing")); });
        TestSupport.test("Rider: sorted insertion and size", () -> { RiderList l=seed(); TestSupport.personIds(l.getAll(),1,2,3); TestSupport.eq(3,l.size()); });
        TestSupport.test("Rider: duplicate ID rejected without loss", () -> { RiderList l=seed(); TestSupport.check(!l.add(TestSupport.rider(1,"Name","City")),"duplicate ID must return false"); TestSupport.personIds(l.getAll(),1,2,3); });
        TestSupport.test("Rider: duplicate Email rejected", () -> { RiderList l=seed(); TestSupport.check(!l.add(new Rider(9,"N","0500000000","r1@example.com","City")),"duplicate unique field must return false"); TestSupport.eq(3,l.size()); });
        TestSupport.test("Rider: find by ID and Email", () -> { RiderList l=seed(); TestSupport.eq(2,l.findById(2).getId()); TestSupport.eq(1,l.findByEmail("r1@example.com").getId()); });
        TestSupport.test("Rider: all full-name matches; no substring", () -> { RiderList l=seed(); TestSupport.personSet(l.findByName("Same Name"),1,3); TestSupport.eq(0,TestSupport.count(l.findByName("Same"))); });
        TestSupport.test("Rider: all HomeCity matches", () -> { TestSupport.personSet(seed().findByHomeCity("Riyadh"),1,3); });
        TestSupport.test("Rider: remove ID 1", () -> { RiderList l=seed(); TestSupport.check(l.removeById(1),"existing ID removed"); TestSupport.personIds(l.getAll(),2,3); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(1),"repeat false"); });
        TestSupport.test("Rider: remove ID 2", () -> { RiderList l=seed(); TestSupport.check(l.removeById(2),"existing ID removed"); TestSupport.personIds(l.getAll(),1,3); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(2),"repeat false"); });
        TestSupport.test("Rider: remove ID 3", () -> { RiderList l=seed(); TestSupport.check(l.removeById(3),"existing ID removed"); TestSupport.personIds(l.getAll(),1,2); TestSupport.eq(2,l.size()); TestSupport.check(!l.removeById(3),"repeat false"); });
        TestSupport.test("Rider: remove by Email", () -> { RiderList l=seed(); TestSupport.check(l.removeByEmail("r2@example.com"),"existing unique removed"); TestSupport.personIds(l.getAll(),1,3); });
        TestSupport.test("Rider: remove full name only", () -> { RiderList l=seed(); TestSupport.eq(0,l.removeByName("Same")); TestSupport.eq(2,l.removeByName("Same Name")); TestSupport.personIds(l.getAll(),2); });
        TestSupport.test("Rider: singleton delete and reinsert", () -> { RiderList l=new RiderList(); l.add(TestSupport.rider(1,"Name","City")); TestSupport.check(l.removeById(1),"singleton removed"); TestSupport.eq(0,l.size()); TestSupport.check(l.getAll().empty(),"empty list"); TestSupport.check(l.add(TestSupport.rider(1,"Name","City")),"reinsert"); TestSupport.eq(1,l.size()); });
        for (int mask=0; mask<32; mask++) {
            final int pattern=mask;
            for (int mode=0;mode<2;mode++) {
                final boolean byName=mode==0;
                TestSupport.test("Rider: remove all "+(byName?"name":"HomeCity")+" pattern "+pattern, () -> {
                    RiderList list=new RiderList();
                    int expected=0;
                    for (int i=1;i<=5;i++) {
                        boolean match=(pattern & (1<<(i-1)))!=0;
                        if (match) expected++;
                        String name=match?"Match Name":"Other Name";
                        list.add(TestSupport.rider(i, name, match ? "Riyadh" : "Jeddah"));
                    }
                    int removed=byName?list.removeByName("Match Name"):list.removeByHomeCity("Riyadh");
                    TestSupport.eq(expected,removed);
                    TestSupport.eq(5-expected,list.size());
                    TestSupport.eq(5-expected,TestSupport.count(list.getAll()));
                    for (int i=1;i<=5;i++)
                        TestSupport.eq((pattern & (1<<(i-1)))==0,list.findById(i)!=null);
                    TestSupport.eq(0,byName?list.removeByName("Match Name"):list.removeByHomeCity("Riyadh"));
                });
            }
        }

    }
}
