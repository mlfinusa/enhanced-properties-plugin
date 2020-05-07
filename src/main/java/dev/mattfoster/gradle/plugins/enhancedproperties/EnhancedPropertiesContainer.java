package dev.mattfoster.gradle.plugins.enhancedproperties;

import dev.mattfoster.gradle.exceptions.PropertyInvalidException;
import dev.mattfoster.gradle.extensions.FileResolver;
import dev.mattfoster.gradle.exceptions.PropertyMissingException;
import dev.mattfoster.gradle.extensions.EnhancedPropertiesExtension;
import dev.mattfoster.gradle.extensions.IPropertyResolver;
import dev.mattfoster.gradle.extensions.PropertyNameResolver;
import org.gradle.api.Project;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class EnhancedPropertiesContainer {

    final public static String EXTERNAL_PROPERTIES = "EXTERNAL_PROPERTIES";
    final public static String NAME = "enhancedProps";
    private Project project;
    EnhancedPropertiesExtension extension;
    private List<IPropertyResolver> defaultResolvers;

    /**
     * Constructor.
     *
     * @param project the project
     * @param extension the extension
     */
    public EnhancedPropertiesContainer(Project project, EnhancedPropertiesExtension extension) {
        this.project = project;
        this.extension = extension;
    }

    /**
     * Get the property value for the given property name. Throws a runtime exception if the property
     * isn't found.
     *
     * @param propertyName the property name
     * @return the property value
     */
    public String get(String propertyName) {
        // Find the property
        String propertyValue = findPropertyValue(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }

        // Missing property!
        throw new PropertyMissingException(
                MessageFormat.format("Enhanced property not found. propertyName = {0}", propertyName));
    }

    /**
     * Get the property value for the given property name. Returns the given default value if the
     * property isn't found.
     *
     * @param propertyName the property name
     * @param defaultValue the default value
     * @return the property value
     */
    public String get(String propertyName, String defaultValue) {
        // Find the property
        String propertyValue = findPropertyValue(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }

        // Missing property!
        return defaultValue;
    }

    /**
     * Determine if a property value exists for the given property name.
     *
     * @param propertyName the property name
     * @return true if the property exists, false otherwise
     */
    public Boolean exists(String propertyName) {
        // Find the property
        String propertyValue = findPropertyValue(propertyName);
        if (propertyValue != null) {
            return Boolean.TRUE;
        }

        // Missing property!
        return Boolean.FALSE;
    }

    /**
     * Find the property value for the given property name. First looks in this project's resolvers.
     * Moves to the parent project's resolvers if not found. Continues up through the root project.
     *
     * @param propertyName the property name
     * @return the property value if found, null otherwise
     */
    private String findPropertyValue(String propertyName) {
        // Validate the property name
        propertyName = validatePropertyName(propertyName);

        // Attempt to find the property using this project's resolvers, the parent's resolvers, that
        // parent's resolvers, etc
        Project iterProject = project;
        while (iterProject != null) {
            EnhancedPropertiesContainer iterContainer = (EnhancedPropertiesContainer) iterProject
                    .getExtensions()
                    .findByName(NAME);
            if (iterContainer != null) {
                String property = null;
                for (IPropertyResolver resolver : iterContainer.getResolvers()) {
                    property = resolver.resolve(propertyName);
                    if (property != null) {
                        return property;
                    }
                }
            }

            // Move on to the parent project (should return null when we've already reached the root project)
            iterProject = project.getParent();
        }

        // Cannot find the property
        return null;
    }

    /**
     * Validate a property name. Throws a runtime exception if the name is invalid.
     *
     * @param propertyName the property name
     * @return the property name
     */
    private String validatePropertyName(String propertyName) {
        propertyName = propertyName != null ? propertyName.trim() : null;
        if (propertyName == null || propertyName.isEmpty()) {
            throw new PropertyInvalidException(
                    MessageFormat.format("Enhanced property name is invalid. propertyName = {0}", propertyName));
        }
        return propertyName;
    }

    /**
     * Get all the property resolvers for this project. Does not include parent projects. Uses defaults
     * if the user doesn't specify any custom resolvers.
     *
     * @return the list of property resolvers
     */
    public List<IPropertyResolver> getResolvers() {
        List<IPropertyResolver> resolvers = extension.getResolvers().get();
        if (!resolvers.isEmpty()) {
            return resolvers;
        } else {
            return getDefaultResolvers();
        }
    }

    /**
     * Get all the default property resolvers. The following files are defined as defaults:
     *
     * <ol>
     * <li>{@code Property:EXTERNAL_PROPERTIES=&lt;some valid path&gt;</li>
     * <li>{@code [PROJECT ROOT]/gradle.properties}</li>
     * </ol>
     *
     * @return the list of property resolvers
     */
    public List<IPropertyResolver> getDefaultResolvers() {
        if (defaultResolvers == null) {
            defaultResolvers = new ArrayList<>();

            // 1) Build the EXTERNAL_PROPERTIES property name resolver
            String propertyNameResolverString = MessageFormat.format("External Properties Resolver propertyName= {0}",
                    EXTERNAL_PROPERTIES);
            IPropertyResolver propertyNameResolver = new PropertyNameResolver(project, EXTERNAL_PROPERTIES);
            defaultResolvers.add(propertyNameResolver);


            // 2) Build the project directory file resolver
            String projectDirectoryResolverString = MessageFormat.format("{0}/gradle.properties",
                    project.getRootProject().getProjectDir().getAbsolutePath());
            IPropertyResolver projectDirectoryResolver = new FileResolver(project,
                    new File(projectDirectoryResolverString));
            defaultResolvers.add(projectDirectoryResolver);
        }
        return defaultResolvers;
    }
}
