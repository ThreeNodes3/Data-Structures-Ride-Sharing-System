import java.util.Scanner;

/** Development-only helpers. Uses the project's custom LinkedList, no JUnit. */
public final class TestSupport {
    private TestSupport() {}
    public interface Case { void run() throws Exception; }
    private static int passed, failed;

    public static void test(String label, Case body) {
        try {
            body.run();
            passed++;
            System.out.println("[PASS] " + label);
        } catch (AssertionError | Exception e) {
            failed++;
            System.out.println("[FAIL] " + label + " -> "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    public static void reset() { passed = failed = 0; }
    public static int summary() {
        System.out.println("\nTOTAL=" + (passed + failed)
                + " PASS=" + passed + " FAIL=" + failed);
        return failed;
    }
    public static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void eq(Object expected, Object actual) {
        check(expected == null ? actual == null : expected.equals(actual),
                "expected <" + expected + "> but was <" + actual + ">");
    }
    public static void illegal(Case body) throws Exception {
        try { body.run(); }
        catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("expected IllegalArgumentException, but no exception");
    }
    public static <T> int count(LinkedList<T> list) {
        check(list != null, "expected a non-null LinkedList");
        if (list.empty()) return 0;
        int count = 0;
        list.findFirst();
        while (true) {
            check(++count <= 10000, "list traversal exceeded 10000 nodes");
            if (list.last()) return count;
            list.findNext();
        }
    }
    public static <T> T at(LinkedList<T> list, int position) {
        check(position >= 0 && position < count(list), "missing list index " + position);
        list.findFirst();
        for (int i=0; i<position; i++) list.findNext();
        return list.retrieve();
    }
    public static void personIds(LinkedList<? extends IPerson> list, int... ids) {
        eq(ids.length, count(list));
        for (int i=0; i<ids.length; i++) eq(ids[i], at(list,i).getId());
    }
    public static void personSet(LinkedList<? extends IPerson> list, int... ids) {
        eq(ids.length, count(list));
        for (int id : ids) {
            int matches=0;
            for (int i=0; i<ids.length; i++) if (at(list,i).getId()==id) matches++;
            eq(1,matches);
        }
    }
    public static void alphabetic(LinkedList<? extends IPerson> list) {
        for (int i=1; i<count(list); i++)
            check(at(list,i-1).getName().compareToIgnoreCase(at(list,i).getName())<=0,
                    "expected alphabetical names; got " + at(list,i-1).getName()
                    + " before " + at(list,i).getName());
    }
    public static void rideIds(LinkedList<IRide> list, int... ids) {
        eq(ids.length,count(list));
        for (int id : ids) {
            int matches=0;
            for (int i=0; i<ids.length; i++) if (at(list,i).getRideId()==id) matches++;
            eq(1,matches);
        }
    }
    public static void print(LinkedList<?> list) {
        if (list == null || list.empty()) { System.out.println("No results."); return; }
        list.findFirst();
        while (true) {
            Object item=list.retrieve();
            // Include identity even if the project's toString omits it.
            if (item instanceof IPerson) {
                IPerson p=(IPerson)item;
                System.out.print("[ID=" + p.getId() + ", name=" + p.getName() + "] ");
            }
            System.out.println(item);
            if (list.last()) break;
            list.findNext();
        }
    }
    public static String text(Scanner in, String prompt) {
        System.out.print(prompt); return in.nextLine().trim();
    }
    public static int integer(Scanner in, String prompt) {
        while (true) {
            try { return Integer.parseInt(text(in,prompt)); }
            catch (NumberFormatException e) { System.out.println("Enter a valid whole number."); }
        }
    }
    public static VehicleType vehicle(Scanner in) {
        String s=text(in,"Type (SEDAN/LUXURY_SEDAN/SUV/VAN): ");
        for (VehicleType t : VehicleType.values()) if (t.name().equalsIgnoreCase(s)) return t;
        throw new IllegalArgumentException("Unknown vehicle type.");
    }
    public static IDateTime readTime(Scanner in, String label) {
        String s=text(in,label+" (MM/DD/YYYY HH:MM): ");
        if (!s.matches("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}"))
            throw new IllegalArgumentException("Use MM/DD/YYYY HH:MM.");
        return new DateTime(Integer.parseInt(s.substring(6,10)),
                Integer.parseInt(s.substring(0,2)),Integer.parseInt(s.substring(3,5)),
                Integer.parseInt(s.substring(11,13)),Integer.parseInt(s.substring(14,16)));
    }
    public static Rider rider(int id, String name, String city) {
        return new Rider(id,name,"0500000000","r"+id+"@example.com",city);
    }
    public static Driver driver(int id, String name, VehicleType type) {
        String number=Integer.toString(id);
        while (number.length()<4) number="0"+number;
        return new Driver(id,name,"0500000000","ABC"+number,type);
    }
    public static RideSharingSystem system() {
        RideSharingSystem s=new RideSharingSystem();
        s.addRider(rider(1,"Zulu Rider","Riyadh"));
        s.addRider(rider(2,"Alpha Rider","Jeddah"));
        s.addRider(rider(3,"Alpha Rider","Riyadh"));
        s.addRider(rider(4,"Other Rider","Dammam"));
        s.addDriver(driver(1,"Zulu Driver",VehicleType.SEDAN));
        s.addDriver(driver(2,"Alpha Driver",VehicleType.SUV));
        s.addDriver(driver(3,"Other Driver",VehicleType.VAN));
        return s;
    }
    public static DateTime time(int minute) {
        return new DateTime(2027,1,1,minute/60,minute%60);
    }
    public static boolean privateRide(RideSharingSystem s,int rider,int driver,int start,int end) {
        return s.schedulePrivateRide("Pickup",time(start),time(end),"End",rider,driver);
    }
    public static boolean sharedRide(RideSharingSystem s,int[] ids,int driver,int start,int end) {
        return s.scheduleSharedRide("Shared",time(start),time(end),"End",ids,driver);
    }
}
