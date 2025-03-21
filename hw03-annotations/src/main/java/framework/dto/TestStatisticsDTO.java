package framework.dto;


public class TestStatisticsDTO {

    private final int totalTests;

    private final int passedTests;

    private final int failedTests;


    public TestStatisticsDTO(int totalTests, int passedTests, int failedTests) {
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

}