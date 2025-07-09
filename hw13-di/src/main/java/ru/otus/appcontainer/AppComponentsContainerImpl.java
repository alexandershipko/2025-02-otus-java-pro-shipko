package ru.otus.appcontainer;

import java.lang.reflect.Method;
import java.util.*;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.exception.IoCException;


@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... configClasses) {
        for (Class<?> configClass : configClasses) {
            checkConfigClass(configClass);
        }

        List<Class<?>> sortedConfigs = Arrays.stream(configClasses)
                .sorted(Comparator.comparingInt(c -> Objects.requireNonNull(c.getAnnotation(AppComponentsContainerConfig.class)).order()))
                .toList();

        for (Class<?> configClass : sortedConfigs) {
            processConfig(configClass);
        }
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);

        List<Class<?>> sortedConfigs = configClasses.stream()
                .sorted(Comparator.comparingInt(c -> Objects.requireNonNull(c.getAnnotation(AppComponentsContainerConfig.class)).order()))
                .toList();

        for (Class<?> configClass : sortedConfigs) {
            checkConfigClass(configClass);
            processConfig(configClass);
        }
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();

            List<Method> methods = Arrays.stream(configClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(m -> Objects.requireNonNull(m.getAnnotation(AppComponent.class)).order()))
                    .toList();

            for (Method method : methods) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String componentName = Objects.requireNonNull(annotation).name();

                if (appComponentsByName.containsKey(componentName)) {
                    throw new IoCException("Duplicate component name: " + componentName);
                }

                Class<?>[] paramTypes = method.getParameterTypes();
                Object[] params = new Object[paramTypes.length];

                for (int i = 0; i < paramTypes.length; i++) {
                    params[i] = getAppComponent(paramTypes[i]);
                    if (params[i] == null) {
                        throw new IoCException("Dependency not found: " + paramTypes[i].getName());
                    }
                }

                method.setAccessible(true);
                Object component = method.invoke(configInstance, params);

                appComponents.add(component);
                appComponentsByName.put(componentName, component);
            }
        } catch (Exception e) {
            throw new IoCException("Failed to process config class " + configClass.getName(), e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<C> candidates = new ArrayList<>();
        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                candidates.add((C) component);
            }
        }
        if (candidates.isEmpty()) {
            throw new IoCException("Component not found by class: " + componentClass.getName());
        }
        if (candidates.size() > 1) {
            throw new IoCException("Multiple components found by class: " + componentClass.getName());
        }
        return candidates.getFirst();
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new IoCException("Component not found by name: " + componentName);
        }
        return (C) component;
    }

}
