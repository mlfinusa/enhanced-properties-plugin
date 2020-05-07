package dev.mattfoster.gradle.extensions;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

public class PropertyNameResolver implements IPropertyResolver {
    private String propertyName;
    private Project project;
    private Properties properties;
    /**
     * Constructor.
     *
     * @param project the project
     * @param propertyName the property name to evaluate for File name resolution.
     */
    public PropertyNameResolver(Project project, String propertyName) {
        this.project = project;
        this.propertyName = propertyName;

    }

    @Override
    public String resolve(String propertyName) {
        return getProperties().getProperty(propertyName);
    }

    @Override
    public Properties getProperties() {

        if (properties == null) {
            properties = new Properties();
            if (project.getExtensions().getExtraProperties().has(propertyName)) {
                try {

                    File file = new File((String) Objects.requireNonNull(project.getExtensions().getExtraProperties().get(propertyName)));
                    InputStream inputStream = new FileInputStream(file);
                    properties.load(inputStream);
                } catch (IOException e) {
                    // This is a valid scenario so log it and continue gracefully
                    project.getLogger()
                            .info(MessageFormat.format("Cannot load properties file. Continuing gracefully. propertyName = {0}; propertyValue = {1}", propertyName, project.getExtensions().getExtraProperties().get(propertyName)));
                }
            }
        }
        return properties;
    }
}
