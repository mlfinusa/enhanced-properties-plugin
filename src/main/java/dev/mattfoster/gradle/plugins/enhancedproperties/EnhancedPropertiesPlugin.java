package dev.mattfoster.gradle.plugins.enhancedproperties;
import dev.mattfoster.gradle.extensions.EnhancedPropertiesExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;



public class EnhancedPropertiesPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        // Create the plugin extension
        EnhancedPropertiesExtension extension = target.getExtensions().create(EnhancedPropertiesExtension.NAME,
                EnhancedPropertiesExtension.class, target);

        // Create the properties container
        target.getExtensions().create(EnhancedPropertiesContainer.NAME, EnhancedPropertiesContainer.class, target,
                extension);
    }
}
