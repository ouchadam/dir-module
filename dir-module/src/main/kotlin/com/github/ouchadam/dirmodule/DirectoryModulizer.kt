package com.github.ouchadam.dirmodule

import com.github.ouchadam.dirmodule.kotlinextensions.createIfMissing
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import java.io.File

private const val WORKING_DIR_NAME = "export-sourceset"

class DirectoryModulizer(
    private val settings: Settings
) {

    fun includeDirectoriesAsModules(directoryNameCaptures: List<String>) {
        val rootProject = settings.rootProject
        val projects = rootProject.children
            .toPotentialModules(directoryNameCaptures)
            .filter { (_, files) -> files?.isNotEmpty() ?: false }
            as List<Pair<ProjectDescriptor, List<File>>>

        val rootBuildDir = rootProject.projectDir.resolve("build").createIfMissing()
        val workingDir = rootBuildDir.resolve(WORKING_DIR_NAME).createIfMissing()

        projects.forEach {
            it.includeFilesAsModules(workingDir)
        }
    }

    private fun Pair<ProjectDescriptor, List<File>>.includeFilesAsModules(workingDir: File) {
        println("Evaluating... ${this.first.name}")
        println("files... ${this.second}")

        this.second.forEach { dir ->
            val fullProjectName = "${this.first.name}-${dir.name}"
            println("including $fullProjectName")
            settings.include(":${fullProjectName}")
            val dynProject = settings.project(":${fullProjectName}")
            dynProject.projectDir = workingDir.resolve(fullProjectName).createIfMissing()
        }
    }
}

private fun Set<ProjectDescriptor>.toPotentialModules(directoryNameCaptures: List<String>) = this.map {
    val src = File("${it.projectDir.absolutePath}/src")
    val extraDirs: Array<File>? = src.listFiles { _, name ->
        directoryNameCaptures.contains(name)
    }
    it to extraDirs?.toList()?.filter { it.isDirectory }
}