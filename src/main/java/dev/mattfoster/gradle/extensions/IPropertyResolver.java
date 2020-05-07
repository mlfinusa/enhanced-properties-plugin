package dev.mattfoster.gradle.extensions;

import java.util.Properties;

public interface IPropertyResolver {

    /**
     * Resolve the property with the given property name.
     *
     * @param propertyName the property name
     * @return the property if found, null otherwise
     */
    String resolve(String propertyName);

    /**
     * Get the loaded properties.
     *
     * @return the properties
     */
    Properties getProperties();
}
