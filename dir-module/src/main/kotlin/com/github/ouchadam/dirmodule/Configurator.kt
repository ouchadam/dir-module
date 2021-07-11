package com.github.ouchadam.dirmodule

import com.android.build.gradle.BaseExtension
import com.github.ouchadam.dirmodule.ProjectType.*
import com.github.ouchadam.dirmodule.kotlinextensions.applyPluginIfMissing
import com.github.ouchadam.dirmodule.kotlinextensions.callViaDelegate
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

fun Project.configureDirectoryModules() {
    val extension = this.extensions.create("dirModule", DirModuleExtension::class.java)

    this.afterEvaluate {
        extension.modules.forEach { (key, value) ->
            println("finding: ${":${project.name}-$key"}")
            val module = this.project(":${project.name}-$key")

            module.beforeEvaluate { evaluatedProject ->
                println("applying.... ${evaluatedProject.name}")
                val (projectType, closure) = value
                module.applyRequiredPlugins(projectType)
                closure.callViaDelegate(module)
                module.applyDefaults(projectType, containerProject = this, moduleName = key)
            }
        }
    }
}

private fun Project.applyRequiredPlugins(projectType: ProjectType) {
    when (projectType) {
        ANDROID -> {
            this
                .applyPluginIfMissing("com.android.library")
                .applyPluginIfMissing("kotlin-android")
        }
        KOTLIN -> {
            this.applyPluginIfMissing("kotlin")
        }
        NOT_SPECIFIED -> {
            // do nothing
        }
    }
}

private fun Project.applyDefaults(projectType: ProjectType, containerProject: Project, moduleName: String) {
    when (projectType) {
        ANDROID -> {
            val androidExtension = this.extensions.getByName("android") as BaseExtension
            if (androidExtension.compileSdkVersion == null) {
                androidExtension.setCompileSdkVersion(30)
            }

            val moduleDir = "${project.projectDir.absolutePath}/src/$moduleName"

            val mainSourceSet = androidExtension.sourceSets.getByName("main")
            mainSourceSet.java.setSrcDirs(
                listOf("$moduleDir/java")
            )

            when {
                mainSourceSet.manifest.srcFile.exists() -> {
                    // do nothing
                }
                this.file("$moduleDir/AndroidManifest.xml").exists() -> {
                    mainSourceSet.manifest.srcFile("$moduleDir/AndroidManifest.xml")
                }
                else -> {
                    val manifest = this.file("${this.projectDir.absolutePath}/AndroidManifest.xml")
                    manifest.createNewFile()
                    manifest.writeText("<manifest package=\"com.generated.manifest\"></manifest>")
                    mainSourceSet.manifest.srcFile(manifest)
                }
            }
        }
        KOTLIN -> {
            setKotlinSrcDir(this, containerProject, moduleName)
        }
        NOT_SPECIFIED -> {
            // do nothing
        }
    }
}

private fun setKotlinSrcDir(module: Project, project: Project, key: String) {
    val sourceSets = module.properties["sourceSets"] as SourceSetContainer
    sourceSets.getByName("main").java.setSrcDirs(
        listOf("${project.projectDir.absolutePath}/src/$key/java")
    )
}
