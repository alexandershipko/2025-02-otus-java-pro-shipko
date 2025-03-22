import framework.TestExecutionContext;
import framework.TestRunner;
import test.TestFour;
import test.TestOne;
import test.TestThree;
import test.TestTwo;


@SuppressWarnings("java:S106")
public class Application {

    public static void main(String[] args) {
        TestRunner.runTest(TestOne.class);
        TestRunner.runTest(TestTwo.class);
        TestRunner.runTest(TestThree.class);
        TestRunner.runTest(TestFour.class);

        TestExecutionContext.printOverallStats();
    }

}