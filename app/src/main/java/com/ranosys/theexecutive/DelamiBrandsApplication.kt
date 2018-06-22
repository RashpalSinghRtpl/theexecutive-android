package com.ranosys.theexecutive

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import io.fabric.sdk.android.Fabric

/**
 * @Details Application class
 * @Author Ranosys Technologies
 * @Date 02,March,2018
 */
class DelamiBrandsApplication : Application(){

    companion object {
        var samleApplication: DelamiBrandsApplication? = null
        var deviceHeight : Int = 0
        var deviceWidth : Int = 0
    }

    override fun onCreate() {
        super.onCreate()
        samleApplication = this
        SavedPreferences.init(this)
        Fabric.with(this, Crashlytics())
        deviceHeight = resources.displayMetrics.heightPixels
        deviceWidth = resources.displayMetrics.widthPixels

        if(SavedPreferences.getInstance()?.getIntValue(Constants.HEIGHT) == 0){
            SavedPreferences.getInstance()?.saveIntValue( deviceHeight, Constants.HEIGHT)
            SavedPreferences.getInstance()?.saveIntValue( deviceWidth, Constants.WIDTH)
        }
    }
}