package com.github.verauvads.mvisetup.services

import com.github.verauvads.mvisetup.MyBundle

class MyApplicationService {

    init {
        println(MyBundle.message("applicationService"))

        System.getenv("CI")
            ?: System.getenv("CI")
    }
}
