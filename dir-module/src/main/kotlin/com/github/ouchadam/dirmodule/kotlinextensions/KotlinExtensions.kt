package com.github.ouchadam.dirmodule.kotlinextensions

import groovy.lang.Closure
import org.gradle.api.Project
import java.io.File

fun File.createIfMissing(): File {
    if (!this.exists()) {
        this.mkdir()
    }
    return this
}

fun <T> Closure<T>.callViaDelegate(delegate: T) {
    this.resolveStrategy = Closure.DELEGATE_ONLY
    this.delegate = delegate
    this.call()
}

fun Project.applyPluginIfMissing(name: String): Project {
    val plugins = this.plugins
    if (!plugins.hasPlugin(name)) {
        plugins.apply(name)
    }
    return this
}