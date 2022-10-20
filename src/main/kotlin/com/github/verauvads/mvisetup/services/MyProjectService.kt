package com.github.verauvads.mvisetup.services

import com.intellij.openapi.project.Project
import com.github.verauvads.mvisetup.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))

        System.getenv("CI")
            ?:System.getenv("CI")
    }
}
