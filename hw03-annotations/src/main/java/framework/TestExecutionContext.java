package framework;


@SuppressWarnings("java:S106")
public class TestExecutionContext {

    private int totalTests = 0;

    private int passedTests = 0;

    private int failedTests = 0;


    public void incrementTotal() {
        totalTests++;
    }

    public void incrementPassed() {
        passedTests++;
    }

    public void incrementFailed() {
        failedTests++;
    }

    public TestExecutionContext add(TestExecutionContext other) {
        this.totalTests += other.totalTests;
        this.passedTests += other.passedTests;
        this.failedTests += other.failedTests;
        return this;
    }

    public void printStats() {
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
    }

}