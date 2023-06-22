package net.jqwik.micronaut.internal.hook.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.annotation.Nonnull;

import net.jqwik.api.JqwikException;
import net.jqwik.api.NonNullApi;
import net.jqwik.api.lifecycle.LifecycleContext;
import net.jqwik.api.lifecycle.ParameterResolutionContext;
import net.jqwik.api.lifecycle.ResolveParameterHook;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import net.jqwik.micronaut.internal.extension.JqwikMicronautExtension;

public class ParameterResolver implements ResolveParameterHook {
    @Override
    @NonNullApi
    @Nonnull
    public Optional<ParameterSupplier> resolve(final ParameterResolutionContext parameterContext,
                                               final LifecycleContext lifecycleContext) {
        return Optional.of(new MicronautSupplier(parameterContext));
    }

    private static class MicronautSupplier implements ParameterSupplier {
        private final ParameterResolutionContext parameterContext;

        MicronautSupplier(final ParameterResolutionContext parameterContext) {
            this.parameterContext = parameterContext;
        }

        @Override
        @NonNullApi
        @Nonnull
        public Object get(final Optional<TryLifecycleContext> optionalTry) {
            final ApplicationContext applicationContext = JqwikMicronautExtension.STORE.get().getApplicationContext();
            final Argument<?> argument = getArgument(parameterContext, applicationContext);
            if (argument == null) {
                return applicationContext.getBean(parameterContext.parameter().getType());
            }
            final Parameter parameter = parameterContext.parameter();
            if (parameter.isAnnotationPresent(Value.class)) {
                return propertyFromValueAnnotation(applicationContext, parameter);
            }
            if (argument.isAnnotationPresent(Property.class)) {
                return propertyFromPropertyAnnotation(applicationContext, argument, parameter);
            }
            return applicationContext.getBean(argument.getType(), resolveQualifier(argument));
        }

        private Argument<?> getArgument(final ParameterResolutionContext parameterContext,
                                        final ApplicationContext applicationContext) {
            final Executable declaringExecutable = parameterContext.parameter().getDeclaringExecutable();
            final int index = parameterContext.index();
            if (declaringExecutable instanceof Constructor) {
                final Class<?> declaringClass = declaringExecutable.getDeclaringClass();
                return applicationContext.findBeanDefinition(declaringClass)
                        .map(e -> e.getConstructor().getArguments())
                        .filter(e -> index < e.length)
                        .map(e -> e[index])
                        .orElse(null);
            }
            try {
                final ExecutableMethod<?, Object> executableMethod = applicationContext.getExecutableMethod(
                        declaringExecutable.getDeclaringClass(),
                        declaringExecutable.getName(),
                        declaringExecutable.getParameterTypes()
                );
                final Argument<?>[] arguments = executableMethod.getArguments();
                if (index < arguments.length) {
                    return arguments[index];
                }
            } catch (final NoSuchMethodException e) {
                return null;
            }
            return null;
        }

        private Object propertyFromValueAnnotation(final ApplicationContext applicationContext, final Parameter parameter) {
            return applicationContext.getEnvironment()
                    .getProperty(getValueFromValueAnnotation(parameter), parameter.getType())
                    .orElseThrow(() -> new JqwikException("Unresolvable property specified to @Value: " + parameter.getName()));
        }

        private String getValueFromValueAnnotation(final Parameter parameter) {
            return parameter.getAnnotation(Value.class).value().replaceAll("[${}]", StringUtils.EMPTY_STRING);
        }

        private Object propertyFromPropertyAnnotation(final ApplicationContext applicationContext,
                                                      final Argument<?> argument, final Parameter parameter) {
            final String propertyName = parameter.getAnnotation(Property.class).name();
            if (propertyName.isEmpty()) {
                return applicationContext.getBean(parameter.getType(), resolveQualifier(argument));
            }
            return applicationContext.getEnvironment()
                    .getProperty(propertyName, parameter.getType())
                    .orElseThrow(() -> new JqwikException("Unresolvable property specified to @Property: " + parameter.getName()));
        }

        private <T> Qualifier<T> resolveQualifier(final Argument<?> argument) {
            final AnnotationMetadata annotationMetadata = Objects.requireNonNull(argument, "Argument cannot be null")
                    .getAnnotationMetadata();
            boolean hasMetadata = annotationMetadata != AnnotationMetadata.EMPTY_METADATA;

            final List<String> qualifierTypes = hasMetadata ?
                    annotationMetadata.getAnnotationNamesByStereotype(AnnotationUtil.QUALIFIER) :
                    Collections.emptyList();
            if (CollectionUtils.isEmpty(qualifierTypes)) {
                return null;
            }

            if (qualifierTypes.size() == 1) {
                return Qualifiers.byAnnotation(annotationMetadata, qualifierTypes.get(0));
            }

            @SuppressWarnings("unchecked") final Qualifier<T>[] qualifiers = qualifierTypes
                    .stream()
                    .map(type -> Qualifiers.<T>byAnnotation(annotationMetadata, type))
                    .toArray(Qualifier[]::new);
            return Qualifiers.byQualifiers(qualifiers);
        }
    }
}
