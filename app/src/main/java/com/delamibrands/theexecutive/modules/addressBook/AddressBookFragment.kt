package com.delamibrands.theexecutive.modules.addressBook

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.activities.DashBoardActivity
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.modules.addAddress.AddAddressFragment
import com.delamibrands.theexecutive.modules.editAddress.EditAddressFragment
import com.delamibrands.theexecutive.modules.myAccount.MyAccountDataClass
import com.delamibrands.theexecutive.utils.DialogOkCallback
import com.delamibrands.theexecutive.utils.FragmentUtils
import com.delamibrands.theexecutive.utils.GlobalSingelton
import com.delamibrands.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_address_book.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

/**
 * @Details screen showing address list
 * @Author Ranosys Technologies
 * @Date 01-May-2018
 */
class AddressBookFragment: BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mViewModel: AddressBookViewModel
    private var addressList: MutableList<MyAccountDataClass.Address>? = null
    private lateinit var addressBookAdapter: AddressBookAdapter
    private var isFromCheckout: Boolean  = false
    private var liveAddress: MutableLiveData<MyAccountDataClass.Address>?  = null
    private var deleteAddressId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(AddressBookViewModel::class.java)
        observeAddressList()
        observeRemoveAddressApiResponse()
        observeSetDefaultAddressApiResponse()

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_address_book, container, false)
        addressList = GlobalSingelton.instance?.userInfo?.addresses?.toMutableList()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity as Context)
        address_list.layoutManager = linearLayoutManager

        //get stored
        addressBookAdapter = AddressBookAdapter(addressList, action = { id: Int, pos: Int ->
            handleAddressEvents(id, pos)
        })
        address_list.adapter = addressBookAdapter

        (activity as DashBoardActivity).toolbarBinding.root.toolbar_right_icon_image.setOnClickListener {
            addAddress()
        }

    }

    override fun onResume() {
        super.onResume()
        addressBookAdapter.addressList = GlobalSingelton.instance?.userInfo?.addresses
        setToolBarParams(getString(R.string.address_book), 0, "", R.drawable.back, true, R.drawable.add , true)
    }

    fun setToolbarAndCallAddressApi(){
        setToolBarParams(getString(R.string.address_book), 0, "", R.drawable.back, true, R.drawable.add , true)
        getAddressList()
    }

    private fun getAddressList(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            mViewModel.getAddressList()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observeRemoveAddressApiResponse() {
        mViewModel.removeAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){

                Toast.makeText(activity as Context, getString(R.string.address_remove_success_msg), Toast.LENGTH_SHORT).show()
                addressList = apiResponse?.apiResponse?.addresses
                addressBookAdapter.addressList = addressList
                addressBookAdapter.notifyDataSetChanged()

                //to update selected address with default address at checkout screen when selected address deleted
                if(deleteAddressId == liveAddress?.value?.id){
                    liveAddress?.value = Utils.getDefaultAddress()
                    deleteAddressId = ""
                }

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })
    }

    private fun observeSetDefaultAddressApiResponse() {
        mViewModel.setDefaultAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){
                addressList = apiResponse?.apiResponse?.addresses
                addressBookAdapter.addressList = addressList
                addressBookAdapter.notifyDataSetChanged()

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })
    }

    private fun observeAddressList() {
        mViewModel.addressList.observe(this, Observer { apiResponse ->

            if(apiResponse?.error.isNullOrEmpty()){
                addressBookAdapter.let {
                    addressList = apiResponse?.apiResponse
                    addressBookAdapter.addressList = addressList
                    addressBookAdapter.notifyDataSetChanged()

                }

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }

        })
    }


    private fun handleAddressEvents(id: Int, addressPosition: Int){
        when(id){
            R.id.tv_remove_address -> {
                removeAddress(addressPosition)
            }

            R.id.tv_edit_address -> {
                editAddress(addressPosition)
            }

            R.id.chk_default -> {
                changeDefaultAddress(addressPosition)
            }

            else -> {
                if(isFromCheckout){
                    addressSelection(addressPosition)
                }
            }

        }
    }

    private fun addressSelection(addressPosition: Int) {
        //perform only if from checkout
        Toast.makeText(activity, "Address selected", Toast.LENGTH_SHORT).show()
        liveAddress?.value = addressList?.get(addressPosition)
        activity?.onBackPressed()

    }

    private fun changeDefaultAddress(addressPosition: Int) {

        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.setDefaultAddress(addressList?.get(addressPosition))
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }

    }

    private fun editAddress(addressPosition: Int) {
        val editAddressFragment = EditAddressFragment.getInstance(addressList?.get(addressPosition), liveAddress)
        FragmentUtils.addFragment(context, editAddressFragment,null, EditAddressFragment::class.java.name, true )
    }

    private fun addAddress() {
        FragmentUtils.addFragment(context, AddAddressFragment(),null, AddAddressFragment::class.java.name, true )
    }

    private fun removeAddress(addressPosition: Int) {
        if(addressList?.get(addressPosition)?.default_billing == true && addressList?.get(addressPosition)?.default_shipping == true){
            Utils.showDialog(activity, getString(R.string.dafault_address_delete_warning), getString(R.string.ok), "", null)
        }else{
            Utils.showDialog(activity, getString(R.string.delete_address_confirmation), getString(R.string.ok), getString(R.string.cancel), object: DialogOkCallback{
                override fun setDone(done: Boolean) {
                    callRemoveAddressApi(addressPosition)
                }

            })
        }
    }

    private fun callRemoveAddressApi(addressPosition: Int) {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            deleteAddressId = addressList?.get(addressPosition)?.id
            mViewModel.removeAddress(addressList?.get(addressPosition))
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    companion object {
        fun getInstance(isFromCheckout : Boolean  = false, liveAddress: MutableLiveData<MyAccountDataClass.Address>? = null): AddressBookFragment{
            return AddressBookFragment().apply {
                this.isFromCheckout = isFromCheckout
                this.liveAddress = liveAddress
            }
        }
    }

}