package log.proxy;

import log.annotation.Log;
import log.exception.MethodNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

        //cache
        private final Map<Method, Method> methodCache = new ConcurrentHashMap<>();


        public LogInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method realMethod = methodCache.computeIfAbsent(method, m -> {
                try {
                    return target.getClass().getMethod(m.getName(), m.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    throw new MethodNotFoundException("Failed to find the method", e);
                }
            });

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