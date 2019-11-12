package com.udayasreesoftwaresolution.mybusinessanalysis.appclasspackage

import android.app.Application

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ApplicationLifecycleCallback())
    }
}