package functest.dev.mattfoster.gradle.plugins.enhancedproperties

import functest.dev.mattfoster.gradle.plugins.utils.GradleRun
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BaseTestSpec extends Specification {
    public File testRootProjectDir
    public File settingsFile
    public File rootBuildFile
    public File gradlePropertiesFile
    @Shared public gradleRuns =[
                                new GradleRun('4.5', 'Earliest possible gradle version for plugin dependencies'),
                                new GradleRun('4.8', 'Deployment Tool installed Release'),
                                new GradleRun('4.9', 'Reused release for IRT projects'),
                                new GradleRun('4.10.3', 'Last 4.x release'),
                                new GradleRun('5.0', 'First 5.x release'),
                                new GradleRun('5.6.4', 'Last 5.x release'),
                                new GradleRun('6.0', 'First 6.x release'),
                                new GradleRun('6.3', 'previous 6.x release'),
                                new GradleRun('6.4', 'Most recent release')
                                ]

    public static final File propertyResourcesBase = new File("./src/functionalTest/resources/propertiesFiles/")
    public static final String repoLocation = new File("./build/repos/testing").toURI().toURL()
    public static final String enhancementsTask = """
        task enhancementOutputs {
            doLast {
                println project.name+": Hello world! from Gradle Version \${gradle.gradleVersion}"
                def checkPropertyName = project.ext.TESTPROPERTY
                println "checkPropertyName:\${checkPropertyName}"
                if (enhancedProps.exists(checkPropertyName)) {
                    def checkPropertyValue = enhancedProps.get(checkPropertyName)
                    println "\${project.name}:\${checkPropertyName}:\${checkPropertyValue}"
                }
                checkPropertyName = project.ext.TESTPROPERTY2
                println "checkPropertyName2:\${checkPropertyName}"
                if (enhancedProps.exists(checkPropertyName)) {
                    def checkPropertyValue = enhancedProps.get(checkPropertyName)
                    println "\${project.name}:\${checkPropertyName}:\${checkPropertyValue}"
                }
                checkPropertyName = project.ext.TESTPROPERTY3
                println "checkPropertyName3:\${checkPropertyName}"
                if (enhancedProps.exists(checkPropertyName)) {
                    def checkPropertyValue = enhancedProps.get(checkPropertyName)
                    println "\${project.name}:\${checkPropertyName}:\${checkPropertyValue}"
                }
                checkPropertyName = project.ext.TESTPROPERTY4
                println "checkPropertyName4:\${checkPropertyName}"
                if (enhancedProps.exists(checkPropertyName)) {
                    def checkPropertyValue = enhancedProps.get(checkPropertyName)
                    println "\${project.name}:\${checkPropertyName}:\${checkPropertyValue}"
                }
                checkPropertyName = project.ext.TESTPROPERTY5
                println "checkPropertyName5:\${checkPropertyName}"
                if (enhancedProps.exists(checkPropertyName)) {
                    def checkPropertyValue = enhancedProps.get(checkPropertyName)
                    println "\${project.name}:\${checkPropertyName}:\${checkPropertyValue}"
                }
                println project.name+': Goodbye world!'
            }
        }
"""
    public static final String emptyEnhancementsTask = """
        task enhancementOutputs {
            doLast {
                println project.name+': Hello world!'
                println 'Intentionally Empty'
                println project.name+': Goodbye world!'
            }
        }
"""



    def getPropertiesResourceFilePath(fileName) {
        return (new File(propertyResourcesBase, fileName)).canonicalPath
    }

    def createSubproject(projName) {
        def result = new File(testRootProjectDir, projName)
        result.mkdir();
        def pBuild = new File(result, 'build.gradle')
        pBuild.createNewFile();
        settingsFile << """include '${projName}'
"""
        return result;
    }

    def getSubprojectBuildFile(projName) {
        def result = new File(new File(testRootProjectDir, projName), 'build.gradle')
        return result;
    }

    def addEnhancementsTaskToBuildFile(File buildFile, Boolean empty = false) {
        buildFile << """${empty ? emptyEnhancementsTask : enhancementsTask}
"""
    }

    def applyPluginToSubProjectsInRootBuild(String dummy) {
        /*rootBuildFile << """
subprojects{
    plugins{
        id '${Utility.pluginID}'

    }
}
"""*/
    }

    def applyPluginToBuild(File buildFile, Boolean subProjectsOnly = false) {
        def apply
        if (subProjectsOnly) {
            apply = """
buildscript{
    repositories {
        maven {
            url = '${repoLocation}'
        }
    }
    dependencies {
        classpath (group: 'dev.mattfoster.gradle', name: 'enhanced-properties-plugin', version:'test-SNAPSHOT')
    }
}
subprojects {
        apply plugin : '${Utility.pluginID}'
}
"""
        } else {
            apply = """
plugins{
    id '${Utility.pluginID}'
}
"""
        }
        buildFile << apply
    }

    def addBaseProperty(String name, String value) {
        gradlePropertiesFile << """${name}=${value}
"""
    }

    def configureExternalPropertiesOnly(buildFile, propertyName) {
        buildFile << """
enhancedProperties {
    resolver ('${propertyName}')
}
"""
    }

    def configurePropertiesFileOnly(buildFile, path) {
        buildFile << """
enhancedProperties {
    resolver file('${path}')
}
"""
    }

    /*
     * Runs before each feature method
     * @return
     */

    def setup() {
        testRootProjectDir = File.createTempDir()
        println(testRootProjectDir.getCanonicalPath())
        settingsFile = new File(testRootProjectDir, 'settings.gradle')
        settingsFile << """rootProject.name = 'enhancedproperties-base-test'
"""
        rootBuildFile = new File(testRootProjectDir, 'build.gradle')
        gradlePropertiesFile = new File(testRootProjectDir, 'gradle.properties')
        gradlePropertiesFile << """BaseTestSpec=done
TESTPROPERTY=BaseTestSpec
TESTPROPERTY2=BaseTestSpec
TESTPROPERTY3=BaseTestSpec
TESTPROPERTY4=BaseTestSpec
TESTPROPERTY5=BaseTestSpec
"""
    }

}