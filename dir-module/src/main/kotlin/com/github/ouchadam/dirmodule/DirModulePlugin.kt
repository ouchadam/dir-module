package com.github.ouchadam.dirmodule

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

class DirModulePlugin : Plugin<Any> {

    override fun apply(appliedTo: Any) {
        when (appliedTo) {
            is Settings -> settingsFlow(appliedTo)
            is Project -> projectFlow(appliedTo)
        }
    }

    private fun settingsFlow(settings: Settings) {
        println("applied to settings")
        val directoryNameCaptures = listOf("fixtures")
        settings.gradle.settingsEvaluated { evaluatedSettings ->
            val directoryModulizer = DirectoryModulizer(evaluatedSettings)
            directoryModulizer.includeDirectoriesAsModules(directoryNameCaptures)
        }
    }

    private fun projectFlow(project: Project) {
        println("applied to project")
        project.configureDirectoryModules()
    }

}
