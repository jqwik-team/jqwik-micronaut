package net.jqwik.micronaut.extension;

import io.micronaut.inject.BeanDefinition;
import io.micronaut.test.annotation.MockBean;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

class MockInjector {
    private MockInjector() {

    }

    /**
     * Injects mock declared at field or method level via @MockBean annotation.
     * Unexpected behavior can happen if both approaches are used for same field. Example:
     * {@literal
     *
     * @param specDefinition corresponding to test class.
     * @return consumer with logic to inject mocks.
     * @MockBean MyObject myObject = mock(MyObject.class)
     * // more code...
     * @MockBean(MyObject.class) MyObject myObject() { return mock(MyObject.class);}
     * }
     */
    public static Consumer<Object> inject(final BeanDefinition<?> specDefinition) {
        return specInstance -> {
            final Class<?> specInstanceClass = specInstance.getClass();
            final var mockBeanAnnotatedMethods = getMockBeanAnnotated(specInstanceClass.getDeclaredMethods());
            final var mockBeanAnnotatedFields = getMockBeanAnnotated(specInstanceClass.getDeclaredFields());
            for (final var injectedField : specDefinition.getInjectedFields()) {
                Stream.concat(mockBeanAnnotatedMethods.stream(), mockBeanAnnotatedFields.stream())
                        .filter(e -> isSameType(e, injectedField.getType()))
                        .map(e -> getMock(e, specInstance))
                        .forEach(e -> injectMock(specInstance, injectedField.getField(), e));
            }
        };
    }

    @SafeVarargs
    private static <T extends AccessibleObject> List<T> getMockBeanAnnotated(final T... accessibleObject) {
        return Arrays.stream(accessibleObject)
                .filter(e -> e.isAnnotationPresent(MockBean.class))
                .toList();
    }

    private static boolean isSameType(final AccessibleObject accessibleObject, final Class<?> targetType) {
        if (accessibleObject instanceof Method m) {
            return m.getReturnType().equals(targetType);
        }
        if (accessibleObject instanceof Field f) {
            return f.getType().equals(targetType);
        }
        return false;
    }

    private static Map.Entry<AccessibleObject, Callable<?>> getMock(final AccessibleObject accessibleObject,
                                                                    final Object specInstance) {
        if (accessibleObject instanceof Field f) {
            return Map.entry(accessibleObject, () -> f.get(specInstance));
        }
        if (accessibleObject instanceof Method m) {
            return Map.entry(accessibleObject, () -> m.invoke(specInstance));
        }
        throw new RuntimeException();
    }

    private static void injectMock(final Object specInstance, final Field fieldToInject,
                                   final Map.Entry<AccessibleObject, Callable<?>> mockProvider) {
        fieldToInject.setAccessible(true);
        mockProvider.getKey().setAccessible(true);
        try {
            fieldToInject.set(specInstance, mockProvider.getValue().call());
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
