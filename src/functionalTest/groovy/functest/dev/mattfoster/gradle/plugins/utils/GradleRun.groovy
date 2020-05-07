package functest.dev.mattfoster.gradle.plugins.utils

class GradleRun {
    String runVersion
    String runComment
    URI runUri
    GradleRun(String version, String comment) {
        runVersion = version
        runComment = comment
        runUri = URI.create("https://services.gradle.org/distributions/gradle-${version}-bin.zip")
    }
}
