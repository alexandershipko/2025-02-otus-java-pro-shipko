package log.proxy;

import log.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;


public class LogProxyFactory {

    private static final Logger logger = LoggerFactory.getLogger(LogProxyFactory.class);


    private LogProxyFactory() {
    }

    public static <T> T createProxy(Class<T> interfaceClass, T implementation) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("interfaceClass must be an interface!");
        }

        InvocationHandler handler = new LogInvocationHandler(implementation);
        Object proxy = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                handler
        );

        return interfaceClass.cast(proxy);
    }

    static class LogInvocationHandler implements InvocationHandler {

        private final Object target;


        public LogInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (realMethod.isAnnotationPresent(Log.class)
                    && logger.isInfoEnabled()) {

                String paramsString = "";
                if (args != null && args.length > 0) {
                    paramsString = Arrays.stream(args)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                    logger.info("executed method: {}, params: {}", method.getName(), paramsString);
                } else {
                    logger.info("executed method: {}", method.getName());
                }
            }

            return method.invoke(target, args);
        }
    }

}