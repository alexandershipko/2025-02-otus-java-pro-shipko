package framework;

import annotation.After;
import annotation.Before;
import annotation.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;


@SuppressWarnings({"java:S106", "java:S3011"})
public class TestRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private static final String NO_CAUSE = "No cause";


    private TestRunner() {

    }

    public static TestExecutionContext runTest(Class<?> clazz) {
        TestExecutionContext context = executeTests(clazz);
        logger.info("\nStatistics for {}:", clazz.getSimpleName());
        context.printStats();
        return context;
    }

    private static TestExecutionContext executeTests(Class<?> clazz) {
        TestExecutionContext context = new TestExecutionContext();

        try {
            Method before = findAnnotatedMethod(clazz, Before.class);
            Method after = findAnnotatedMethod(clazz, After.class);

            for (Method testMethod : clazz.getDeclaredMethods()) {
                if (!testMethod.isAnnotationPresent(Test.class)) {
                    continue;
                }

                context.incrementTotal();

                boolean skipTest = !runOptionalPhase(clazz, before, "@Before", testMethod);

                if (!skipTest) {
                    boolean testPassed = runPhase(clazz, testMethod, "Test", testMethod);
                    if (testPassed) {
                        context.incrementPassed();
                    } else {
                        context.incrementFailed();
                    }
                } else {
                    context.incrementFailed();
                }

                runOptionalPhase(clazz, after, "@After", testMethod);
            }

        } catch (Exception e) {
            logger.error("Error running tests for {}: {}", clazz.getSimpleName(), e.getMessage());
        }

        return context;
    }

    private static Method findAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
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
                        (e.getCause() != null ? e.getCause() : NO_CAUSE));
                return false;
            }
        }).orElseGet(() -> {
            logger.error("Failed to create instance for {} method of {}", phase, testMethod.getName());
            return false;
        });
    }

    private static boolean runOptionalPhase(Class<?> clazz, Method method, String phase, Method testMethod) {
        return method != null && runPhase(clazz, method, phase, testMethod);
    }

    private static Optional<Object> createInstance(Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            logger.error("Failed to create instance of class: {}", clazz.getSimpleName());
            return Optional.empty();
        }
    }

}