package functest.dev.mattfoster.gradle.plugins.enhancedproperties

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import spock.lang.Unroll


class SingleProjectSpec extends BaseTestSpec {

    def setup(){
        applyPluginToBuild(rootBuildFile)
        addEnhancementsTaskToBuildFile(rootBuildFile)
    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses default EXTERNAL_PROPERTIES then gradle properties"() {
        given:
        println "Basis Setup"
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
        result.output.contains('enhancedproperties-base-test:check1:external')
        result.output.contains('enhancedproperties-base-test:check2:base')
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns

    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses only configured TEST_PROPERTIES"() {
        given:
        println "Basis Setup"
        configureExternalPropertiesOnly(rootBuildFile,'TEST_PROPERTIES')
        addBaseProperty("check1","alpha")
        addBaseProperty("check2","bravo")
        String propertiesFile = getPropertiesResourceFilePath('propFile_01.properties')
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PTEST_PROPERTIES=${propertiesFile}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2", "--stacktrace")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.output.contains('enhancedproperties-base-test:check1:external')
        !result.output.contains('enhancedproperties-base-test:check2:bravo')
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns

    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses only configured file"() {
        given:
        println "Basis Setup"
        String propertiesFilePath = getPropertiesResourceFilePath('propFile_01.properties').replace("\\","\\\\")
        configurePropertiesFileOnly(rootBuildFile, propertiesFilePath)
        addBaseProperty("check1","charlie")
        addBaseProperty("check2","delta")
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PEXTERNAL_PROPERTIES=${propertiesFilePath}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2", "--stacktrace")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.output.contains('enhancedproperties-base-test:check1:external')
        !result.output.contains('enhancedproperties-base-test:check2:delta')
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns
    }

    @Unroll
    def "Gradle Version: #gradleRun.runVersion: uses configured file then configured properties"() {
        given:
        println "Basis Setup"
        String propertiesFilePath = getPropertiesResourceFilePath('propFile_01.properties').replace("\\","\\\\")
        rootBuildFile << """
enhancedProperties {
    resolver file('${propertiesFilePath}')
    resolver 'TEST_PROPERTIES'
    resolver 'TEST_PROPERTY2'
}
"""
        configurePropertiesFileOnly(rootBuildFile, propertiesFilePath)
        propertiesFilePath = getPropertiesResourceFilePath('propFile_02.properties')
        String propertiesFile2Path = getPropertiesResourceFilePath('propFile_03.properties')
        addBaseProperty("check1","charlie")
        addBaseProperty("check2","delta")
        addBaseProperty("check3","echo")
        when:
        def result = GradleRunner.create()
                .withGradleDistribution(gradleRun.runUri)
                .withProjectDir(testRootProjectDir)
                .withArguments('enhancementOutputs', "-PTEST_PROPERTIES=${propertiesFilePath}", "-PTEST_PROPERTY2=${propertiesFile2Path}", "-PTESTPROPERTY=check1", "-PTESTPROPERTY2=check2", "-PTESTPROPERTY3=check3", "--stacktrace")
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.output.contains('enhancedproperties-base-test:check1:external')
        !result.output.contains('enhancedproperties-base-test:check2:delta')
        !result.output.contains('enhancedproperties-base-test:check3:echo')
        result.output.contains('enhancedproperties-base-test:check2:c2file2')
        result.output.contains('enhancedproperties-base-test:check3:exists3')
        result.task(":enhancementOutputs").outcome == SUCCESS

        where:
        gradleRun << gradleRuns
    }
}
