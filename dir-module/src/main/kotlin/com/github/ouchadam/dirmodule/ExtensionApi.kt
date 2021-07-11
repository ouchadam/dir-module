package com.github.ouchadam.dirmodule

import groovy.lang.Closure
import org.gradle.api.Project

open class DirModuleExtension {

    internal val modules = mutableMapOf<String, Pair<ProjectType, Closure<Project>>>()

    fun configure(name: String, closure: Closure<Project>) {
        modules[name] = ProjectType.NOT_SPECIFIED to closure
    }

    fun android(name: String, closure: Closure<Project>) {
        modules[name] = ProjectType.ANDROID to closure
    }

}

enum class ProjectType {
    ANDROID,
    KOTLIN,
    NOT_SPECIFIED
}
