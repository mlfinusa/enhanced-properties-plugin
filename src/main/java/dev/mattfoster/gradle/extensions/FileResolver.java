package dev.mattfoster.gradle.extensions;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class FileResolver implements IPropertyResolver {

    private Project project;
    private File file;
    private Properties properties;

    /**
     * Constructor.
     *
     * @param project the project
     * @param file the properties file
     */
    public FileResolver(Project project, File file) {
        this.project = project;
        this.file = file;
    }

    @Override
    public String resolve(String propertyName) {
        return getProperties().getProperty(propertyName);
    }

    @Override
    public Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        if (!file.exists()) {
            project.getLogger()
                    .info(MessageFormat.format("Properties file does not exist. Continuing gracefully. file = {0}", file));
        }
        else {
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            } catch (IOException e) {
                // This is a valid scenario so log it and continue gracefully
                project.getLogger()
                        .error(MessageFormat.format("Cannot load properties file. Continuing gracefully. file = {0}", file));
            }
        }
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        builder.append("(");
        builder.append("file=" + file);
        builder.append(")");
        return builder.toString();
    }
}
