package test.dev.mattfoster.gradle.plugins

import dev.mattfoster.gradle.exceptions.PropertyMissingException
import dev.mattfoster.gradle.extensions.EnhancedPropertiesExtension
import dev.mattfoster.gradle.plugins.enhancedproperties.EnhancedPropertiesContainer
import org.gradle.api.logging.Logger
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import org.gradle.api.logging.Logging


class EnhancedPropertiesPluginTest extends Specification {

    Logger logger = Logging.getLogger(this.class)
    private static String pluginName ="dev.mattfoster.enhanced-properties";

    def "plugin has enhancedProperties container"() {

        given: "a project build with default configuration"
        def project = ProjectBuilder.builder().build()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.getProperties().each { key,value ->
            logger.trace("\tPreApply: Key: %s -> value: %s\n",key,value)
        }
        project.plugins.apply(pluginName)
        project.getProperties().each { key,value ->
            logger.trace("\tPostApply: Key: %s -> value: %s\n",key,value)
        }

        then: "Validate the extension and configuration container are available"
        project.getExtensions().findByName(EnhancedPropertiesExtension.NAME) != null
        project.getExtensions().findByName(EnhancedPropertiesContainer.NAME) != null

    }

    def "handles missing Property with expected exception"() {

        given: "a project build instantiated with no EXTERNAL_PROPERTIES value with default configuration"
        def project = ProjectBuilder.builder().build()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.plugins.apply(pluginName)
        def value = project.enhancedProps.get('pipelineTestValue')

        then: "Validate the pipelinePropertiesApplied value it set true and file values are available"
        thrown PropertyMissingException

    }

    def "handles default configuration with EXTERNAL_PROPERTIES property file"() {

        given: "a project build instantiated with a valid EXTERNAL_PROPERTIES value"
        def project = ProjectBuilder.builder().build()
        File propertiesFile = new File("./src/test/resources/propertiesFiles/test.properties")
        project.ext["EXTERNAL_PROPERTIES"] = propertiesFile.getCanonicalPath()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.plugins.apply(pluginName)
        def value = project.enhancedProps.get('pipelineTestValue')

        then: "Validate the pipelineTestValue value is available and reads as 'true'"
        value=='true'
    }

    def "handles default configuration when no content in EXTERNAL_PROPERTIES  referenced properties"() {

        given: "a project build instantiated with a valid EXTERNAL_PROPERTIES value"
        def project = ProjectBuilder.builder().build()
        File propertiesFile = new File("./src/test/resources/propertiesFiles/empty.properties")
        project.ext["EXTERNAL_PROPERTIES"] = propertiesFile.getCanonicalPath()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"

        project.plugins.apply(pluginName)
        def value = project.enhancedProps.get('pipelineTestValue')

        then: "pipelineTestValue value is not available and PropertyMissingException is thrown"
        thrown PropertyMissingException

    }

    def "handles default configuration when EXTERNAL_PROPERTIES  references non-existent file"() {

        given: "a project build instantiated with a non-existent EXTERNAL_PROPERTIES value"
        def project = ProjectBuilder.builder().build()
        File propertiesFile = new File("./src/test/resources/propertiesFiles/non-existing.properties")
        project.ext["EXTERNAL_PROPERTIES"] = propertiesFile.getCanonicalPath()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"

        project.plugins.apply(pluginName)
        def value = project.enhancedProps.get('pipelineTestValue')

        then: "pipelineTestValue value is not available and PropertyMissingException is thrown"
        thrown PropertyMissingException

    }

    def "handles multiple property providers resolves by configuration precedence"() {

        given: "a project build instantiated with a configured enhancedProps"
        def project = ProjectBuilder.builder().build()
        File propertiesFile = new File("./src/test/resources/propertiesFiles/test.properties")
        File propertiesFile2 = new File("./src/test/resources/propertiesFiles/quaternary.properties")
        File propertiesFile3 = new File("./src/test/resources/propertiesFiles/tertiaryitems.properties")
        project.ext["INTERNAL3_PROPERTIES"] = propertiesFile3.getCanonicalPath()
        project.ext["INTERNAL2_PROPERTIES"] = propertiesFile2.getCanonicalPath()
        project.ext["INTERNAL_PROPERTIES"] = propertiesFile.getCanonicalPath()


        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.plugins.apply(pluginName)
        project.enhancedProps.extension.resolver("INTERNAL_PROPERTIES")
        project.enhancedProps.extension.resolver("INTERNAL2_PROPERTIES")
        project.enhancedProps.extension.resolver("INTERNAL3_PROPERTIES")
        def value = project.enhancedProps.get('pipelineTestItem')
        def value2 = project.enhancedProps.get('onlyFoundInTestProperties');
        def value3 = project.enhancedProps.get('foundOnlyInTertiaryFile');

        then: "pipelineTestItem is available match inputs"
        value == 'Is Applied from test.properties'
        value2 == 'this unique value'
        value3 == 'f3UQ'

    }

    def "resolves configured file properties and default EXTERNAL_PROPERTIES is ignored"() {

        given: "a project build instantiated with a configured enhancedProps file"

        File gPropFile = new File("./src/test/resources/propertiesFiles/test.properties")
        File propertiesFile = new File("./src/test/resources/propertiesFiles/tertiaryitems.properties")
        def project = ProjectBuilder.builder().build()
        project.ext["EXTERNAL_PROPERTIES"] = gPropFile.getCanonicalPath()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.plugins.apply(pluginName)
        project.enhancedProps.extension.resolver(propertiesFile)
        def value = project.enhancedProps.get('pipelineTestItem')
        def value2 = project.enhancedProps.get('foundOnlyInTertiaryFile');
        def value3 = project.enhancedProps.get('onlyFoundInTestProperties');

        then: "pipelineTestItem is available match inputs"
        value == 'Is Applied from tertiaryItems.properties'
        value2 == 'f3UQ'
        thrown PropertyMissingException
    }

    def "resolves configured file properties and configured EXTERNAL_PROPERTIES is evaluated as primary"() {

        given: "a project build instantiated with a configured enhancedProps file"

        File gPropFile = new File("./src/test/resources/propertiesFiles/test.properties")
        File propertiesFile = new File("./src/test/resources/propertiesFiles/tertiaryitems.properties")
        def project = ProjectBuilder.builder().build()
        project.ext["EXTERNAL_PROPERTIES"] = gPropFile.getCanonicalPath()

        when: "Apply dev.mattfoster.gradle.enhanced-properties plugin"
        project.plugins.apply(pluginName)
        project.enhancedProps.extension.resolver("EXTERNAL_PROPERTIES")
        project.enhancedProps.extension.resolver(propertiesFile)
        def value = project.enhancedProps.get('pipelineTestItem')
        def value2 = project.enhancedProps.get('foundOnlyInTertiaryFile');
        def value3 = project.enhancedProps.get('onlyFoundInTestProperties');

        then: "pipelineTestItem is available match inputs"
        value == 'Is Applied from test.properties'
        value2 == 'f3UQ'
        value3 == 'this unique value'
    }
}
