package com.ranosys.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var partialProductList: MutableLiveData<ArrayList<ProductListingDataClass.DummyResponse>> = MutableLiveData()
    var isLoading: Boolean = false
    var totalProductCount: Int = 0

    var filterOptionList: MutableLiveData<MutableList<ProductListingDataClass.Filter>>? = MutableLiveData<MutableList<ProductListingDataClass.Filter>>()
    var selectedFilterMap = hashMapOf<String, String>()

    fun getSortOptions() {
        AppRepository.sortOptionApi(object : ApiCallback<ProductListingDataClass.SortOptionResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("Sort option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Sort option api", errorMsg?: "error")
            }

            override fun onSuccess(sortOptions: ProductListingDataClass.SortOptionResponse?) {
                //TODO - save sort options
            }

        })
    }

    fun getFilterOptions() {
        AppRepository.filterOptionApi(74, object : ApiCallback<ProductListingDataClass.FilterOptionsResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("Filter option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Filter option api", errorMsg?: "error")
            }

            override fun onSuccess(filterOptions: ProductListingDataClass.FilterOptionsResponse?) {

                filterOptions?.run {
                    for (filter in filterOptions.filters){
                        selectedFilterMap.put(filter.name, "")
                    }
                }

                val priceFilter = filterOptions?.filters?.filter { option -> option.name == "Price" }
                val filters = filterOptions?.filters?.filterNot { option -> option.name == "Price" }

                filterOptionList?.value = filters as MutableList<ProductListingDataClass.Filter>


            }
        })
    }

    fun getProductListing(sku: String) {
        isLoading = true
        //dummy data
        val response: ArrayList<ProductListingDataClass.DummyResponse> = ArrayList()

        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse(normalPrice = "1,266.900", specialPrice = "193.600"))
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse(normalPrice = "100,266.900", specialPrice = "13.60"))
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse(normalPrice = "1,266.900", specialPrice = "193.600"))
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse(normalPrice = "1,200", specialPrice = "21"))

        partialProductList.value = response

        // when data recieved
        isLoading = false
    }


}