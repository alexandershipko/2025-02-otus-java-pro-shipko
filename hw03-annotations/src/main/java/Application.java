import framework.TestExecutionContext;
import framework.TestRunner;
import test.TestFour;
import test.TestOne;
import test.TestThree;
import test.TestTwo;


@SuppressWarnings("java:S106")
public class Application {

    public static void main(String[] args) {
        TestExecutionContext overallContext = new TestExecutionContext();

        overallContext.add(TestRunner.runTest(TestOne.class));
        overallContext.add(TestRunner.runTest(TestTwo.class));
        overallContext.add(TestRunner.runTest(TestThree.class));
        overallContext.add(TestRunner.runTest(TestFour.class));

        System.out.println("\nOverall statistics:");
        overallContext.printStats();
    }

}