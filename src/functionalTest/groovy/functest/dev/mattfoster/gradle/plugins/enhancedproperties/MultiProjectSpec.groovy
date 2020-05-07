package functest.dev.mattfoster.gradle.plugins.enhancedproperties

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class MultiProjectSpec extends BaseTestSpec {

    public File p1Dir
    public File p2Dir
    public File p3Dir
    public File p1BuildFile
    public File p2BuildFile
    public File p3BuildFile

    private static final String projA = "enhancetestProjectA"
    private static final String projB = "enhancetestProjectB"
    private static final String projC = "enhancetestProjectC"


    def buildTwoProjectSetup(String description) {
        println description;
        p1Dir = createSubproject(projA)
        p1BuildFile = getSubprojectBuildFile(projA)
        p2Dir = createSubproject(projB)
        p2BuildFile = getSubprojectBuildFile(projB)
    }

    def buildThreeProjectSetup(String description) {
        println description;
        p1Dir = createSubproject(projA)
        p1BuildFile = getSubprojectBuildFile(projA)
        p2Dir = createSubproject(projB)
        p2BuildFile = getSubprojectBuildFile(projB)
        p3Dir = createSubproject(projC)
        p3BuildFile = getSubprojectBuildFile(projC)
    }

 /*
 * Runs before each feature method
 * @return
 */
    def setup(){

    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses default EXTERNAL_PROPERTIES then gradle properties with plugin applied at root project"() {
        given:
        buildTwoProjectSetup("Multi-Project Setup with default-enhancedProperties")
        applyPluginToBuild(rootBuildFile)
        addEnhancementsTaskToBuildFile(rootBuildFile,false)
        addEnhancementsTaskToBuildFile(p1BuildFile,false)
        addEnhancementsTaskToBuildFile(p2BuildFile,false)
        addBaseProperty("check1","base")
        addBaseProperty("check2","base")
        String propertiesFile = getPropertiesResourceFilePath('propFile_01.properties')
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PEXTERNAL_PROPERTIES=${propertiesFile}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2", "--stacktrace")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.output.contains('enhancedproperties-base-test:check1:external')
        result.output.contains('enhancedproperties-base-test:check2:base')
        result.output.contains("${projA}:check1:external")
        result.output.contains("${projA}:check2:base")
        result.output.contains("${projB}:check1:external")
        result.output.contains("${projB}:check2:base")
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns
    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses default EXTERNAL_PROPERTIES then gradle properties with plugin applied to subprojects"() {
        given:
        buildTwoProjectSetup("Multi-Project Setup with default-enhancedProperties")
        applyPluginToBuild(rootBuildFile,true)
        addEnhancementsTaskToBuildFile(rootBuildFile,true)
        addEnhancementsTaskToBuildFile(p1BuildFile,false)
        addEnhancementsTaskToBuildFile(p2BuildFile,false)
        addBaseProperty("check1","base")
        addBaseProperty("check2","base")
        String propertiesFile = getPropertiesResourceFilePath('propFile_01.properties')
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PEXTERNAL_PROPERTIES=${propertiesFile}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2", "--stacktrace")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        !result.output.contains('enhancedproperties-base-test:check1:external')
        !result.output.contains('enhancedproperties-base-test:check2:base')
        result.output.contains("${projA}:check1:external")
        result.output.contains("${projA}:check2:base")
        result.output.contains("${projB}:check1:external")
        result.output.contains("${projB}:check2:base")
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns

    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses configured file with plugin applied to subprojects"() {
        given:
        buildTwoProjectSetup("Multi-Project Setup with default-enhancedProperties")
        applyPluginToBuild(rootBuildFile,true)
        addEnhancementsTaskToBuildFile(rootBuildFile,true)
        addEnhancementsTaskToBuildFile(p1BuildFile,false)
        addEnhancementsTaskToBuildFile(p2BuildFile,false)
        addBaseProperty("check1","base")
        addBaseProperty("check2","base")
        String propertiesFile = getPropertiesResourceFilePath('propFile_01.properties')
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PEXTERNAL_PROPERTIES=${propertiesFile}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        !result.output.contains('enhancedproperties-base-test:check1:external')
        !result.output.contains('enhancedproperties-base-test:check2:base')
        result.output.contains("${projA}:check1:external")
        result.output.contains("${projA}:check2:base")
        result.output.contains("${projB}:check1:external")
        result.output.contains("${projB}:check2:base")
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns

    }
}
