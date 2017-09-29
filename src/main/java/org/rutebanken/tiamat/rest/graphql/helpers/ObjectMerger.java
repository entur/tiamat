/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
     *
     * Values are copied if target-property is null or empty
     *
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
                                if (targetValue == null |
                                        ("").equals(targetValue)) {
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