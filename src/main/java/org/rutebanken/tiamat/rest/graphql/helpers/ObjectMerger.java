package org.rutebanken.tiamat.rest.graphql.helpers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
  * "Borrowed" from https://stackoverflow.com/questions/41602260/how-to-merge-two-java-objects-into-one
 */
public class ObjectMerger extends BeanUtils {


    private static void setAccessible(Method method) {
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
    }

    /**
     * Copy the not null property values of the given source bean into the given target bean.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * @param source the source bean
     * @param target the target bean
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    public static void copyPropertiesNotNull(Object source, Object target, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPropertyDescriptor : targetPds) {
            Method targetWriteMethod = targetPropertyDescriptor.getWriteMethod();
            if (targetWriteMethod != null
                    && (ignoreList == null || !ignoreList.contains(targetPropertyDescriptor.getName()))) {
                PropertyDescriptor sourcePropertyDescriptor =
                        getPropertyDescriptor(source.getClass(), targetPropertyDescriptor.getName());
                if (sourcePropertyDescriptor != null) {
                    Method sourceReadMethod = sourcePropertyDescriptor.getReadMethod();
                    if (sourceReadMethod != null &&
                            ClassUtils.isAssignable(
                                    targetWriteMethod.getParameterTypes()[0], sourceReadMethod.getReturnType())) {
                        try {
                            Method targetReadMethod = targetPropertyDescriptor.getReadMethod();
                            setAccessible(sourceReadMethod);
                            setAccessible(targetWriteMethod);
                            Object sourceValue = sourceReadMethod.invoke(source);

                            if (sourceValue != null && targetReadMethod != null) {
                                setAccessible(targetReadMethod);
                                Object targetValue = targetReadMethod.invoke(target);
                                if (targetValue == null | (targetValue instanceof Enum)) {
                                    targetWriteMethod.invoke(target, sourceValue);
                                } else if(targetValue instanceof Collection<?>) {
                                    ((Collection) targetValue).addAll((Collection) sourceValue);
                                } else if (targetValue instanceof Map<?,?>) {
                                    ((Map) targetValue).putAll((Map) sourceValue);
                                } else {
                                    copyPropertiesNotNull(sourceValue, targetValue, ignoreProperties);
                                }
                            }
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPropertyDescriptor.getName() +
                                    "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}