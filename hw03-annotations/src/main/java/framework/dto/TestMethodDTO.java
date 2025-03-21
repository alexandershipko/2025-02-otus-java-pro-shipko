package framework.dto;

import java.lang.reflect.Method;
import java.util.List;


public class TestMethodDTO {

    private final Method beforeMethod;

    private final List<Method> testMethods;

    private final Method afterMethod;


    public TestMethodDTO(Method beforeMethod, List<Method> testMethods, Method afterMethod) {
        this.beforeMethod = beforeMethod;
        this.testMethods = testMethods;
        this.afterMethod = afterMethod;
    }

    public Method getBeforeMethod() {
        return beforeMethod;
    }

    public List<Method> getTestMethods() {
        return testMethods;
    }

    public Method getAfterMethod() {
        return afterMethod;
    }

}