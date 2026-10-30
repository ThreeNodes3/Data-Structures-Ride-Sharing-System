/** Runs all automated cases without interactive input. Exit 1 means test failures. */
public class All_Tests_Main {
    public static void main(String[] args) {
        TestSupport.reset();
        Rider_Test_Main.runTests();
        Driver_Test_Main.runTests();
        Ride_Test_Main.runTests();
        if (TestSupport.summary()>0) System.exit(1);
    }
}
