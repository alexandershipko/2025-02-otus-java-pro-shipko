package framework;

import annotation.After;
import annotation.Before;
import annotation.Test;
import framework.dto.TestMethodDTO;
import framework.dto.TestStatisticsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SuppressWarnings({"java:S112", "java:S106", "java:S3011"})
public class TestRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private static final String NO_CAUSE = "No cause";

    private static int totalPassed = 0;
    private static int totalFailed = 0;
    private static int totalTests = 0;

    private TestRunner() {

    }

    public static void runTest(Class<?> clazz) {
        try {
            TestMethodDTO methodsDTO = findAnnotatedMethods(clazz);
            TestStatisticsDTO stats = executeTests(clazz, methodsDTO);

            totalTests += stats.getTotalTests();
            totalPassed += stats.getPassedTests();
            totalFailed += stats.getFailedTests();

            printStats(clazz.getSimpleName(), stats);
        } catch (Exception e) {
            throw new RuntimeException("Error running tests: " + e.getMessage(), e);
        }
    }

    private static TestMethodDTO findAnnotatedMethods(Class<?> clazz) {
        Method before = null;
        Method after = null;
        List<Method> testMethods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                if (before != null) {
                    throw new IllegalStateException("There can be only one @Before method");
                }
                before = method;
            } else if (method.isAnnotationPresent(After.class)) {
                if (after != null) {
                    throw new IllegalStateException("There can be only one @After method");
                }
                after = method;
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }
        return new TestMethodDTO(before, testMethods, after);
    }

    private static TestStatisticsDTO executeTests(Class<?> clazz, TestMethodDTO methodsDTO) {
        int passed = 0;
        int failed = 0;

        for (Method testMethod : methodsDTO.getTestMethods()) {
            boolean skipTest = false;

            // BEFORE
            if (methodsDTO.getBeforeMethod() != null) {
                if (!runPhase(clazz, methodsDTO.getBeforeMethod(), "@Before", testMethod)) {
                    failed++;
                    skipTest = true;
                }
            }

            // TEST
            if (!skipTest) {
                if (runPhase(clazz, testMethod, "Test", testMethod)) {
                    passed++;
                } else {
                    failed++;
                }
            }

            // AFTER
            if (methodsDTO.getAfterMethod() != null) {
                runPhase(clazz, methodsDTO.getAfterMethod(), "@After", testMethod);
            }
        }
        return new TestStatisticsDTO(passed + failed, passed, failed);
    }

    private static boolean runPhase(Class<?> clazz, Method method, String phase, Method testMethod) {
        return createInstance(clazz).map(instance -> {
            try {
                method.setAccessible(true);
                method.invoke(instance);
                if ("Test".equals(phase)) {
                    logger.info("Test passed: {}", testMethod.getName());
                }
                return true;
            } catch (Exception e) {
                logger.error("Error in {} method {}: {}", phase, testMethod.getName(),
                        e.getCause() != null ? e.getCause() : NO_CAUSE);
                return false;
            }
        }).orElseGet(() -> {
            logger.error("Failed to create instance for {} method of {}", phase, testMethod.getName());
            return false;
        });
    }

    private static Optional<Object> createInstance(Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            logger.error("Error creating instance of {}: {}", clazz.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    private static void printStats(String className, TestStatisticsDTO stats) {
        System.out.println("\nClass name: " + className);
        System.out.println("Total tests: " + stats.getTotalTests());
        System.out.println("Passed tests: " + stats.getPassedTests());
        System.out.println("Failed tests: " + stats.getFailedTests());
    }

    public static void printOverallStats() {
        System.out.println("\nOverall statistics:");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + totalPassed);
        System.out.println("Failed tests: " + totalFailed);
    }

}