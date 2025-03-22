package framework;


@SuppressWarnings({"java:S106", "java:S2696"})
public class TestExecutionContext {

    private int totalTests = 0;

    private int passedTests = 0;

    private int failedTests = 0;


    private static int totalTestsAllClasses = 0;

    private static int passedTestsAllClasses = 0;

    private static int failedTestsAllClasses = 0;


    public void incrementTotal() {
        totalTests++;
        totalTestsAllClasses++;
    }

    public void incrementPassed() {
        passedTests++;
        passedTestsAllClasses++;
    }

    public void incrementFailed() {
        failedTests++;
        failedTestsAllClasses++;
    }

    public void printStats(String className) {
        System.out.println("\nStatistics for: " + className);
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
    }

    public static void printOverallStats() {
        System.out.println("\nOverall Statistics for all Tests:");
        System.out.println("Total tests: " + totalTestsAllClasses);
        System.out.println("Passed tests: " + passedTestsAllClasses);
        System.out.println("Failed tests: " + failedTestsAllClasses);
    }

}