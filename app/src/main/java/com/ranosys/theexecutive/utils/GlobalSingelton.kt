package com.ranosys.theexecutive.utils

import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class GlobalSingelton private constructor(){

    var storeList: List<StoreResponse>? = null
    var configuration: ConfigurationResponse? = null
    var cartCount: MutableLiveData<Int> = MutableLiveData()

    companion object {
        var instance: GlobalSingelton? = null
            get() {
                field = field ?: GlobalSingelton()
                return field
            }
    }


}