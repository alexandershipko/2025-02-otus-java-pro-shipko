import framework.TestRunner;
import test.TestFour;
import test.TestOne;
import test.TestThree;
import test.TestTwo;

public class Application {

    public static void main(String[] args) {
        TestRunner.runTest(TestOne.class);
        TestRunner.runTest(TestTwo.class);
        TestRunner.runTest(TestThree.class);
        TestRunner.runTest(TestFour.class);

        TestRunner.printOverallStats();
    }

}