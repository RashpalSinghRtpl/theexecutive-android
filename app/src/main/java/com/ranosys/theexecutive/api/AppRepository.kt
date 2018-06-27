package com.ranosys.theexecutive.api

import com.google.gson.JsonObject
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.DelamiBrandsApplication
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.api.interfaces.ApiService
import com.ranosys.theexecutive.modules.bankTransfer.BankTransferRequest
import com.ranosys.theexecutive.modules.bankTransfer.Recipients
import com.ranosys.theexecutive.modules.bankTransfer.TransferMethodsDataClass
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.changePassword.ChangePasswordDataClass
import com.ranosys.theexecutive.modules.checkout.CheckoutDataClass
import com.ranosys.theexecutive.modules.forgotPassword.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.notification.dataclasses.DeviceRegisterRequest
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationListResponse
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailResponse
import com.ranosys.theexecutive.modules.order.orderList.OrderListResponse
import com.ranosys.theexecutive.modules.order.orderReturn.OrderReturnRequest
import com.ranosys.theexecutive.modules.productDetail.dataClassess.*
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagQtyUpdateRequest
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse
import com.ranosys.theexecutive.modules.shoppingBag.TotalResponse
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.modules.wishlist.MoveToBagRequest
import com.ranosys.theexecutive.modules.wishlist.WishlistResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


/**
 * @Details Repository class for api calling
 * @Author Ranosys Technologies
 * @Date 23,Feb,2018
 */
object AppRepository {

    private fun parseError(response: Response<Any>?, callBack: ApiCallback<Any>) {
        try {
            val jobError = JSONObject(response?.errorBody()?.string())
            val errorBody = jobError.getString(Constants.MESSAGE)
            callBack.onError(errorBody)

        } catch (e: JSONException) {
            callBack.onException(Throwable(DelamiBrandsApplication.samleApplication?.getString(R.string.something_went_wrong_error)))
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        } catch (e: IOException) {
            callBack.onException(Throwable(DelamiBrandsApplication.samleApplication?.getString(R.string.something_went_wrong_error)))
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }
    }

    fun getStores(callBack: ApiCallback<ArrayList<StoreResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val callPost = retrofit?.create<ApiService.StoresService>(ApiService.StoresService::class.java)?.getStores(ApiConstants.BEARER + adminToken)

        callPost?.enqueue(object : Callback<ArrayList<StoreResponse>> {
            override fun onResponse(call: Call<ArrayList<StoreResponse>>?, response: Response<ArrayList<StoreResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<ArrayList<StoreResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getConfiguration(callBack: ApiCallback<ConfigurationResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode = Constants.ALL
        val callGet = retrofit?.create<ApiService.ConfigurationService>(ApiService.ConfigurationService::class.java)?.getConfiguration(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<ConfigurationResponse> {
            override fun onResponse(call: Call<ConfigurationResponse>?, response: Response<ConfigurationResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ConfigurationResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun login(loginRequest: LoginDataClass.LoginRequest?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE


        val data = HashMap<String, String>()
        data.put("device_id", SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY).toString())
        data.put("registration_id", SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID).toString())
        data.put("device_type", Constants.OS_TYPE)

/*
        val username= RequestBody.create(okhttp3.MediaType.parse("text/plain"), loginRequest?.username)
        val password= RequestBody.create(okhttp3.MediaType.parse("text/plain"), loginRequest?.password)
        val device_id= RequestBody.create(okhttp3.MediaType.parse("text/plain"), loginRequest?.device_id)
        val device_type= RequestBody.create(okhttp3.MediaType.parse("text/plain"), loginRequest?.device_type)
        val registration_id= RequestBody.create(okhttp3.MediaType.parse("text/plain"), loginRequest?.registration_id)
*/

        val callPost = retrofit?.create<ApiService.LoginService>(ApiService.LoginService::class.java)?.getLoginData(ApiConstants.BEARER + adminToken, storeCode, loginRequest, data)


        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_401) {
                        val errorBody = DelamiBrandsApplication.samleApplication?.applicationContext?.getString(R.string.error_invalid_login_credential) ?: Constants.UNKNOWN_ERROR
                        callBack.onError(errorBody)
                    } else {
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getPromotions(callBack: ApiCallback<List<PromotionsResponseDataClass>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.PromotionService>(ApiService.PromotionService::class.java)?.getPromotions(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<List<PromotionsResponseDataClass>> {
            override fun onResponse(call: Call<List<PromotionsResponseDataClass>>?, response: Response<List<PromotionsResponseDataClass>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<PromotionsResponseDataClass>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCategories(callBack: ApiCallback<CategoryResponseDataClass>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CategoryService>(ApiService.CategoryService::class.java)?.getCategories(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<CategoryResponseDataClass> {
            override fun onResponse(call: Call<CategoryResponseDataClass>?, response: Response<CategoryResponseDataClass>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<CategoryResponseDataClass>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun isEmailAvailable(request: LoginDataClass.IsEmailAvailableRequest?, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.IsEmailAvailableService>(ApiService.IsEmailAvailableService::class.java)?.isEmailAvailableApi(ApiConstants.BEARER + adminToken, storeCode = storeCode, request = request)

        callPost?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun socialLogin(request: LoginDataClass.SocialLoginRequest?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val data = HashMap<String, String>()
        data.put("device_id", SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY).toString())
        data.put("registration_id", SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID).toString())
        data.put("device_type", Constants.OS_TYPE)

        val callPost = retrofit?.create<ApiService.SocialLoginService>(ApiService.SocialLoginService::class.java)?.socialLogin(ApiConstants.BEARER + adminToken, storeCode, request, data)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun forgotPassword(request: ForgotPasswordDataClass.ForgotPasswordRequest, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.ForgotPasswordService>(ApiService.ForgotPasswordService::class.java)?.forgotPasswordApi(ApiConstants.BEARER + adminToken, storeCode, request)

        callPut?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = DelamiBrandsApplication.samleApplication?.applicationContext?.getString(R.string.error_no_user_exist) ?: Constants.UNKNOWN_ERROR
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }

                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }


    fun changePassword(request: ChangePasswordDataClass, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.ChangePasswordService>(ApiService.ChangePasswordService::class.java)?.changePasswordApi(ApiConstants.BEARER + userToken, storeCode, request)

        callPut?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = DelamiBrandsApplication.samleApplication?.applicationContext?.getString(R.string.error_no_user_exist) ?: Constants.UNKNOWN_ERROR
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }

                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun getCountryList(callBack: ApiCallback<List<RegisterDataClass.Country>>) {
        val retrofit = ApiClient.retrofit
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CountryListService>(ApiService.CountryListService::class.java)?.countryList(storeCode)

        callGet?.enqueue(object : Callback<List<RegisterDataClass.Country>> {
            override fun onResponse(call: Call<List<RegisterDataClass.Country>>?, response: Response<List<RegisterDataClass.Country>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<RegisterDataClass.Country>>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun getCityList(stateCode: String, callBack: ApiCallback<List<RegisterDataClass.City>>) {
        val retrofit = ApiClient.retrofit
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CityListService>(ApiService.CityListService::class.java)?.cityList(storeCode, stateCode)

        callGet?.enqueue(object : Callback<List<RegisterDataClass.City>> {
            override fun onResponse(call: Call<List<RegisterDataClass.City>>?, response: Response<List<RegisterDataClass.City>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<RegisterDataClass.City>>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun registrationApi(registrationRequest: RegisterDataClass.RegisterRequest, callBack: ApiCallback<RegisterDataClass.RegistrationResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.RegistrationService>(ApiService.RegistrationService::class.java)?.registration(ApiConstants.BEARER + adminToken, storeCode, registrationRequest)

        callPost?.enqueue(object : Callback<RegisterDataClass.RegistrationResponse> {
            override fun onResponse(call: Call<RegisterDataClass.RegistrationResponse>?, response: Response<RegisterDataClass.RegistrationResponse>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_400) {
                        val errorBody = DelamiBrandsApplication.samleApplication?.applicationContext?.getString(R.string.error_user_already_exist) ?: Constants.UNKNOWN_ERROR
                        callBack.onError(errorBody)

                    } else {
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<RegisterDataClass.RegistrationResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun subscribeNewsletterApi(request: MyAccountDataClass.NewsletterSubscriptionRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.NewsLetterSubscription>(ApiService.NewsLetterSubscription::class.java)?.newsLetterSuscribe(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = DelamiBrandsApplication.samleApplication?.applicationContext?.getString(R.string.error_no_user_exist) ?: Constants.UNKNOWN_ERROR
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun sortOptionApi(type: String, callBack: ApiCallback<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.SortOptionService>(ApiService.SortOptionService::class.java)?.getSortOptions(ApiConstants.BEARER + adminToken, storeCode, type)

        callGet?.enqueue(object : Callback<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>> {
            override fun onResponse(call: Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>?, response: Response<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun filterOptionApi(categoryId: Int, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getFilterOptions(ApiConstants.BEARER + adminToken, storeCode, categoryId = categoryId)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun searchFilterOptionApi(query: String, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getSearchFilters(ApiConstants.BEARER + adminToken, storeCode, searchQuery = query)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductList(requestMap: Map<String, String>, fromSearch: Boolean, callBack: ApiCallback<ProductListingDataClass.ProductListingResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val listFrom = if (fromSearch) "catalogsearch/list" else "productslist"
        val callGet = retrofit?.create<ApiService.ProductListingService>(ApiService.ProductListingService::class.java)?.getProductList(ApiConstants.BEARER + adminToken, storeCode, listFrom, requestMap)

        callGet?.enqueue(object : Callback<ProductListingDataClass.ProductListingResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.ProductListingResponse>?, response: Response<ProductListingDataClass.ProductListingResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.ProductListingResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductDetail(productSku: String?, callBack: ApiCallback<ProductListingDataClass.Item>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductDetail(ApiConstants.BEARER + adminToken, storeCode, productSku)

        callGet?.enqueue(object : Callback<ProductListingDataClass.Item> {
            override fun onResponse(call: Call<ProductListingDataClass.Item>?, response: Response<ProductListingDataClass.Item>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<ProductListingDataClass.Item>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductChildren(productSku: String?, callBack: ApiCallback<List<ChildProductsResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductChildren(ApiConstants.BEARER + adminToken, storeCode, productSku)

        callGet?.enqueue(object : Callback<List<ChildProductsResponse>> {
            override fun onResponse(call: Call<List<ChildProductsResponse>>?, response: Response<List<ChildProductsResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<ChildProductsResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductOptions(attributeId: String?, callBack: ApiCallback<List<ProductOptionsResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductOptions(ApiConstants.BEARER + adminToken, storeCode, attributeId)

        callGet?.enqueue(object : Callback<List<ProductOptionsResponse>> {
            override fun onResponse(call: Call<List<ProductOptionsResponse>>?, response: Response<List<ProductOptionsResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<ProductOptionsResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getStaticPagesUrl(callBack: ApiCallback<StaticPagesUrlResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getStaticPagesUrl(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<StaticPagesUrlResponse> {
            override fun onResponse(call: Call<StaticPagesUrlResponse>?, response: Response<StaticPagesUrlResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<StaticPagesUrlResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToWishList(requestMap: JsonObject, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.addToWishList(ApiConstants.BEARER + userToken, storeCode, requestMap)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun createGuestCart(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.createGuestCart(ApiConstants.BEARER + adminToken, storeCode)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun createUserCart(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.createUserCart(ApiConstants.BEARER + userToken, storeCode)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun cartCountUser(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.cartCountUser(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun cartCountGuest(cartId: String, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.cartCountGuest(ApiConstants.BEARER + adminToken, storeCode, cartId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToCartGuest(cartId: String, request: AddToCartRequest, callBack: ApiCallback<AddToCartResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.addTOCartGuest(ApiConstants.BEARER + adminToken, storeCode, cartId, request)

        callPost?.enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>?, response: Response<AddToCartResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AddToCartResponse
                    >, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToCartUser(request: AddToCartRequest, callBack: ApiCallback<AddToCartResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.addTOCartUser(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>?, response: Response<AddToCartResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCartOfUser(callBack: ApiCallback<List<ShoppingBagResponse>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCartOfUser(ApiConstants.BEARER + userToken, storeCode)

        callPost?.enqueue(object : Callback<List<ShoppingBagResponse>> {
            override fun onResponse(call: Call<List<ShoppingBagResponse>>?, response: Response<List<ShoppingBagResponse>>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_400 || response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = Constants.CART_DE_ACTIVE
                        callBack.onError(errorBody)
                    }else{
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<ShoppingBagResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCartOfGuest(cartId: String, callBack: ApiCallback<List<ShoppingBagResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCartOfGuest((ApiConstants.BEARER + adminToken), storeCode, cartId)

        callPost?.enqueue(object : Callback<List<ShoppingBagResponse>> {
            override fun onResponse(call: Call<List<ShoppingBagResponse>>?, response: Response<List<ShoppingBagResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<ShoppingBagResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun searchFilterApi(searchQuery: String, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getSearchFilters(ApiConstants.BEARER + adminToken, storeCode, searchQuery = searchQuery)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getUserInfo(callBack: ApiCallback<MyAccountDataClass.UserInfoResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.MyAccountService>(ApiService.MyAccountService::class.java)?.getUserInfo(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<MyAccountDataClass.UserInfoResponse> {
            override fun onResponse(call: Call<MyAccountDataClass.UserInfoResponse>?, response: Response<MyAccountDataClass.UserInfoResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<MyAccountDataClass.UserInfoResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun updateUserInfo(request: MyAccountDataClass.UpdateInfoRequest, callBack: ApiCallback<MyAccountDataClass.UserInfoResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.MyAccountService>(ApiService.MyAccountService::class.java)?.updateUserInfo(ApiConstants.BEARER + userToken, storeCode, request)

        callPut?.enqueue(object : Callback<MyAccountDataClass.UserInfoResponse> {
            override fun onResponse(call: Call<MyAccountDataClass.UserInfoResponse>?, response: Response<MyAccountDataClass.UserInfoResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<MyAccountDataClass.UserInfoResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getWishlist(callBack: ApiCallback<WishlistResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.getWishlist(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<WishlistResponse> {
            override fun onResponse(call: Call<WishlistResponse>?, response: Response<WishlistResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteWishlistItem(itemId: Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.deleteWishlistItem(ApiConstants.BEARER + userToken, storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun deleteFromShoppingBagItemGuestUser(cartId: String?, itemId: Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteItemFromShoppingBagGuestUser(ApiConstants.BEARER + userToken, storeCode, cartId, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun updateFromShoppingBagItemGuestUser(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest, callBack: ApiCallback<ShoppingBagQtyUpdateRequest>) {
        val retrofit = ApiClient.retrofit
        val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.updateItemFromShoppingBagGuestUser(ApiConstants.BEARER + adminToken, storeCode, guestCartId, shoppingBagQtyUpdateRequest.cartItem.item_id.toInt(), shoppingBagQtyUpdateRequest)

        callGet?.enqueue(object : Callback<ShoppingBagQtyUpdateRequest> {
            override fun onResponse(call: Call<ShoppingBagQtyUpdateRequest>?, response: Response<ShoppingBagQtyUpdateRequest>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ShoppingBagQtyUpdateRequest>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun updateFromShoppingBagItemUser(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest, callBack: ApiCallback<ShoppingBagQtyUpdateRequest>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.updateItemFromShoppingBagUser(ApiConstants.BEARER + userToken, storeCode, shoppingBagQtyUpdateRequest.cartItem.item_id.toInt()
                , shoppingBagQtyUpdateRequest)

        callGet?.enqueue(object : Callback<ShoppingBagQtyUpdateRequest> {
            override fun onResponse(call: Call<ShoppingBagQtyUpdateRequest>?, response: Response<ShoppingBagQtyUpdateRequest>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ShoppingBagQtyUpdateRequest>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteFromShoppingBagItemUser(itemId: Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteItemFromShoppingBagUser(ApiConstants.BEARER + userToken, storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun moveItemFromCart(itemId: Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.moveItemFromCart(ApiConstants.BEARER + userToken, storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun applyCouponCodeForUser(couponCode: String?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.applyCouponCodeForUser(ApiConstants.BEARER + userToken, storeCode, couponCode)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun applyCouponCodeForGuestUser(couponCode: String?, cartId: String, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.applyCouponCodeForGuestUser(ApiConstants.BEARER + userToken, storeCode, cartId, couponCode)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCouponCodeForUser(callBack: ApiCallback<Any>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCouponCodeForUser(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCouponCodeForGuestUser(cartId: String, callBack: ApiCallback<Any>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCouponCodeForGuestUser(ApiConstants.BEARER + userToken, storeCode, cartId)

        callGet?.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getTotalForUser(callBack: ApiCallback<TotalResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getTotalForUser(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>?, response: Response<TotalResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<TotalResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getTotalForGuestUser(cartId: String, callBack: ApiCallback<TotalResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getTotalForGuestUser(ApiConstants.BEARER + userToken, storeCode, cartId)

        callGet?.enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>?, response: Response<TotalResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<TotalResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteCouponCodeForUser(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteCouponCodeForUser(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteCouponCodeForGuestUser(cartId: String, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteCouponCodeForGuestUser(ApiConstants.BEARER + userToken, storeCode, cartId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun addToBagWishlistItem(request: MoveToBagRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.addToBagWishlistItem(ApiConstants.BEARER + userToken, storeCode, request.id, request)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun cartMergeApi(guestCartId: String, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.mergeCart(ApiConstants.BEARER + userToken,  storeCode, guestCartId = guestCartId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getOrdersList(callBack: ApiCallback<List<OrderListResponse>>){
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.MyOrdersService>(ApiService.MyOrdersService::class.java)?.getMyOrderList(ApiConstants.BEARER + userToken, storeCode)
        callGet?.enqueue(object : Callback<List<OrderListResponse>> {

            override fun onResponse(call: Call<List<OrderListResponse>>?, response: Response<List<OrderListResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<OrderListResponse>>?, t: Throwable?) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun getOrdersDetail(orderId : String, callBack: ApiCallback<OrderDetailResponse>){
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val params = HashMap<String, String>()
        params.put("fields", "items,grand_total,subtotal_incl_tax,shipping_incl_tax,billing_address,payment,extension_attributes[virtual_account_number,returnto_address,formatted_shipping_address,payment_method]")
        val callGet = retrofit?.create<ApiService.MyOrdersService>(ApiService.MyOrdersService::class.java)?.getOrderDetail(ApiConstants.BEARER + userToken, storeCode, orderId, params)
        callGet?.enqueue(object : Callback<OrderDetailResponse> {

            override fun onResponse(call: Call<OrderDetailResponse>?, response: Response<OrderDetailResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>?, t: Throwable?) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun returnProduct(request: OrderReturnRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.MyOrdersService>(ApiService.MyOrdersService::class.java)?.returnProduct(ApiConstants.BEARER + userToken, storeCode, request)
        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getShippingMethods(request: CheckoutDataClass.GetShippingMethodsRequest, callBack: ApiCallback<List<CheckoutDataClass.GetShippingMethodsResponse>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.getShippingMethods(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<List<CheckoutDataClass.GetShippingMethodsResponse>> {
            override fun onResponse(call: Call<List<CheckoutDataClass.GetShippingMethodsResponse>>?, response: Response<List<CheckoutDataClass.GetShippingMethodsResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<CheckoutDataClass.GetShippingMethodsResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getPaymentMethods(request: CheckoutDataClass.GetPaymentMethodsRequest, callBack: ApiCallback<CheckoutDataClass.PaymentMethodResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.getPaymentMethods(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<CheckoutDataClass.PaymentMethodResponse> {
            override fun onResponse(call: Call<CheckoutDataClass.PaymentMethodResponse>?, response: Response<CheckoutDataClass.PaymentMethodResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<CheckoutDataClass.PaymentMethodResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getOrderStatus(orderId: String, callBack: ApiCallback<CheckoutDataClass.OrderStatusResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.getOrderStatus(ApiConstants.BEARER + userToken, storeCode, orderId)

        callGet?.enqueue(object : Callback<CheckoutDataClass.OrderStatusResponse> {
            override fun onResponse(call: Call<CheckoutDataClass.OrderStatusResponse>?, response: Response<CheckoutDataClass.OrderStatusResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<CheckoutDataClass.OrderStatusResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun placeOrder(request: CheckoutDataClass.PlaceOrderRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.placeOrder(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getTotalAmounts(callBack: ApiCallback<CheckoutDataClass.Totals>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.getTotalAmounts(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<CheckoutDataClass.Totals> {
            override fun onResponse(call: Call<CheckoutDataClass.Totals>?, response: Response<CheckoutDataClass.Totals>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<CheckoutDataClass.Totals>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getUserInfoNSelectedShipping(callBack: ApiCallback<CheckoutDataClass.UserInfoNselectedShippingResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) ?: Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CheckoutService>(ApiService.CheckoutService::class.java)?.getUserInfoNSelectedShipping(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<CheckoutDataClass.UserInfoNselectedShippingResponse> {
            override fun onResponse(call: Call<CheckoutDataClass.UserInfoNselectedShippingResponse>?, response: Response<CheckoutDataClass.UserInfoNselectedShippingResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<CheckoutDataClass.UserInfoNselectedShippingResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getNotificationList(callBack: ApiCallback<List<NotificationListResponse>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

       var request = DeviceRegisterRequest("", "", SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))

        val callGet = retrofit?.create<ApiService.NotificationService>(ApiService.NotificationService::class.java)?.getNotificationList(ApiConstants.BEARER + userToken,  storeCode, request)

        callGet?.enqueue(object : Callback<List<NotificationListResponse>> {
            override fun onResponse(call: Call<List<NotificationListResponse>>?, response: Response<List<NotificationListResponse>>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<NotificationListResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun changeNotificationStatus(request: NotificationChangeStatusRequest, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.NotificationService>(ApiService.NotificationService::class.java)?.changeNotificationStatus(ApiConstants.BEARER + userToken,  storeCode, request)

        callGet?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun getBankTransferMethod(callBack: ApiCallback<List<TransferMethodsDataClass>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.BankTransfer>(ApiService.BankTransfer::class.java)?.getBankTransferMethod(ApiConstants.BEARER + userToken,  storeCode)

        callGet?.enqueue(object : Callback<List<TransferMethodsDataClass>> {
            override fun onResponse(call: Call<List<TransferMethodsDataClass>>?, response: Response<List<TransferMethodsDataClass>>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<TransferMethodsDataClass>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun getRecipient(callBack: ApiCallback<List<Recipients>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.BankTransfer>(ApiService.BankTransfer::class.java)?.getRecipient(ApiConstants.BEARER + userToken,  storeCode)

        callGet?.enqueue(object : Callback<List<Recipients>> {
            override fun onResponse(call: Call<List<Recipients>>?, response: Response<List<Recipients>>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<Recipients>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun submitBankTransfer(file : File?, request: BankTransferRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("attachment", file?.name, reqFile)

        val name= RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.name)
        val email_submitter = RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.email_submitter)
        val orderid =  RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.orderid)
        val bank_name = RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.bank_name)
        val holder_account=  RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.holder_account)
        val amount =  RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.amount)
        val recipient =  RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.recipient)
        val method =RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.method)
        val date =  RequestBody.create(okhttp3.MediaType.parse("text/plain"), request.date)

       val callGet = retrofit?.create<ApiService.BankTransfer>(ApiService.BankTransfer::class.java)?.submitBankTransfer(ApiConstants.BEARER + userToken,   storeCode, part, name, email_submitter, orderid, bank_name, holder_account,amount,recipient,method,date)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }



    fun registerDevice(request: DeviceRegisterRequest, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.NotificationService>(ApiService.NotificationService::class.java)?.registerDevice(ApiConstants.BEARER + userToken,  storeCode, request)

        callGet?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }




    fun logoutNotification( callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        var request = NotificationChangeStatusRequest("",  SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY), SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY))
        val callGet = retrofit?.create<ApiService.NotificationService>(ApiService.NotificationService::class.java)?.logoutNotification(ApiConstants.BEARER + userToken,  storeCode, request)

        callGet?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }



}