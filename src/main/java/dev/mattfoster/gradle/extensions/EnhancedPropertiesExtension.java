package dev.mattfoster.gradle.extensions;


import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnhancedPropertiesExtension {

    public static final String NAME = "enhancedProperties";
    private Project project;
    private ListProperty<IPropertyResolver> resolvers;

    /**
     * Constructor.
     *
     * @param project the project
     */
    public EnhancedPropertiesExtension(Project project) {
        this.project = project;

        // Default the list of resolvers to an empty list
        resolvers = project.getObjects().listProperty(IPropertyResolver.class);
        resolvers.set(new ArrayList<>());
    }

    /**
     * Allows the user to add a {@link File} based resolver.
     *
     * @param file the file
     */
    public void resolver(File file) {
        appendResolver(new FileResolver(project, file));
    }
    public void resolver(String propertyName) {
        if (project.getExtensions().getExtraProperties().has(propertyName)) {
            Object theFileLocationObject = project.getExtensions().getExtraProperties().get(propertyName);
            if (theFileLocationObject instanceof String ) {
                File file = new File((String) theFileLocationObject);
                appendResolver(new FileResolver(project, file));
            }
        }
    }
    public void propertyResolver(String theProperty) {
        if (project.getExtensions().getExtraProperties().has(theProperty)) {
            Object theFileLocationObject = project.getExtensions().getExtraProperties().get(theProperty);
            if (theFileLocationObject instanceof String ) {
                File file = new File((String) theFileLocationObject);
                appendResolver(new FileResolver(project, file));
            }
        }
    }

    /**
     * Get all the resolvers.
     *
     * @return the list of resolvers
     */
    public ListProperty<IPropertyResolver> getResolvers() {
        return resolvers;
    }

    /**
     * Appends the given resolver to the list of resolvers.
     *
     * @param resolver the resolver to append
     */
    private void appendResolver(IPropertyResolver resolver) {
        List<IPropertyResolver> existingResolvers = resolvers.get();
        List<IPropertyResolver> newResolvers = new ArrayList<>(existingResolvers);
        newResolvers.add(resolver);
        resolvers.set(newResolvers);
    }
}
