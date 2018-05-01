package com.ranosys.theexecutive.modules.myAccount

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.modules.register.RegisterDataClass

/**
 * Created by nikhil on 22/3/18.
 */
class MyAccountDataClass {

    data class MyAccountOption(val title: String, val icon: Int)

    data class NewsletterSubscriptionRequest(val email: String)


    data class UserInfoResponse(
            val id: Int,
            val group_id: Int,
            val default_billing: String,
            val default_shipping: String,
            val created_at: String,
            val updated_at: String,
            val created_in: String,
            val dob: String,
            val email: String,
            val firstname: String,
            val lastname: String,
            val prefix: String,
            val gender: Int,
            val store_id: Int,
            val website_id: Int,
            val addresses: List<Address>,
            val disable_auto_group_change: Int
    )

    data class Address(
            var id: String = "",
            var customer_id: String = "",
            val region: RegisterDataClass.Region,
            val region_id: String,
            val country_id: String,
            val street: List<String>,
            var telephone: String,
            val postcode: String,
            val city: String,
            val prefix: String = "",
            val firstname: String,
            val lastname: String,
            val default_shipping: Boolean,
            val default_billing: Boolean
    )

    data class MaskedUserInfo(
            var _firstName: String?,
            var _lastName: String?,
            var _email: String?,
            var _mobile: String?,
            var _countryCode: String? = "",
            var _streedAdd1: String?,
            var _streedAdd2: String?,
            var _country: String?,
            var _state: String?,
            var _city: String?,
            var _postalCode: String?,
            val _id: String?
    ): BaseObservable(){

        var firstName : String?
            @Bindable get() = _firstName
            set(value) {
                _firstName = value
                notifyPropertyChanged(BR.firstName)
            }


        var lastName : String?
            @Bindable get() = _lastName
            set(value) {
                _lastName = value
                notifyPropertyChanged(BR.lastName)
            }

        var email : String?
            @Bindable get() = _email
            set(value) {
                _email = value
                notifyPropertyChanged(BR.email)
            }

        var mobile : String?
            @Bindable get() = _mobile
            set(value) {
                _mobile = value
                notifyPropertyChanged(BR.mobile)
            }

        var countryCode : String?
            @Bindable get() = _countryCode
            set(value) {
                _countryCode = value
                notifyPropertyChanged(BR.countryCode)
            }

        var streedAdd1 : String?
            @Bindable get() = _streedAdd1
            set(value) {
                _streedAdd1 = value
                notifyPropertyChanged(BR.streedAdd1)
            }

        var streedAdd2 : String?
            @Bindable get() = _streedAdd2
            set(value) {
                _streedAdd2 = value
                notifyPropertyChanged(BR.streedAdd2)
            }

        var country : String?
            @Bindable get() = _country
            set(value) {
                _country = value
                notifyPropertyChanged(BR.country)
            }

        var state : String?
            @Bindable get() = _state
            set(value) {
                _state = value
                notifyPropertyChanged(BR.state)
            }

        var city : String?
            @Bindable get() = _city
            set(value) {
                _city = value
                notifyPropertyChanged(BR.city)
            }

        var postalCode : String?
            @Bindable get() = _postalCode
            set(value) {
                _postalCode = value
                notifyPropertyChanged(BR.postalCode)
            }

        val id : String?
            @Bindable get() = _id

    }


    data class UpdateInfoRequest(
            var customer: UserInfoResponse
    )

}