package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.ProductDetailViewBinding
import com.ranosys.theexecutive.databinding.ProductImagesLayoutBinding
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.MediaGalleryEntryChild
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.StaticPagesUrlResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.bottom_size_layout.view.*
import kotlinx.android.synthetic.main.product_detail_view.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 11,Apr,2018
 */
class ProductViewFragment : BaseFragment() {

    lateinit var productItemViewModel : ProductItemViewModel
    var productItem : ProductListingDataClass.Item? = null
    var position : Int? = 0
    var productSku : String? = ""
    var colorAttrId : String? = ""
    var sizeAttrId : String? = ""
    var colorMap = HashMap<String, String>()
    var sizeMap = HashMap<String, String>()
    var childProductsMap = HashMap<String, List<MediaGalleryEntryChild>>()
    var colorOptionList : List<ProductOptionsResponse>? = null
    var sizeOptionList : List<ProductOptionsResponse>? = null
    var colorsViewList : MutableList<ColorsView>? = null
    var sizeViewList : List<ColorsView>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(inflater, R.layout.product_detail_view, container, false);
        productItemViewModel = ViewModelProviders.of(this).get(ProductItemViewModel::class.java)
        productItemViewModel.productItem = productItem
        listGroupBinding?.productItemVM = productItemViewModel

        observeEvents()
        getStaticPagesUrl()

        return listGroupBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(productItem?.type_id.equals("configurable")){
            setData()
            if(productItem?.sku.equals("5-BLWBBX417L014")) {
                getProductChildren(productItem?.sku)
            }
        }

    }

    fun setData(){

        setDescription()
        setProductImages(productItem?.media_gallery_entries)
        if(productItem?.sku.equals("5-BLWBBX417L014")) {
            setColorImagesList()
        }
        setWearWithProductsData()
    }

    fun setDescription(){
        try {
            val productDescription = productItem?.custom_attributes?.filter { s ->
                s.attribute_code == "short_description"
            }?.single()
            tv_description.setText(Html.fromHtml(productDescription?.value.toString()))
        }catch (e : NoSuchElementException){
            AppLog.printStackTrace(e)
        }

    }

    fun setWearWithProductsData(){
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        list_wear_with_products.layoutManager = linearLayoutManager

        if(productItem?.product_links?.size!! > 0) {
            val wearWithAdapter = WearWithProductsAdapter(activity as Context, productItem?.product_links)
            list_wear_with_products.adapter = wearWithAdapter
            wearWithAdapter.setItemClickListener(object : WearWithProductsAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductListingDataClass.ProductLinks?) {

                }
            })
        }else {
            rl_wear_with_layout.visibility = View.GONE
        }

    }

    fun setProductImages(mediaGalleryList : List<ProductListingDataClass.MediaGalleryEntry>?){
        Utils.setImageViewHeight(activity as Context, img_one, 27)
        Utils.setImageViewHeight(activity as Context, img_two, 27)
        val listSize = mediaGalleryList?.size
        for(i in 2..listSize!!.minus(1)){
            val productImagesBinding : ProductImagesLayoutBinding? = DataBindingUtil.inflate(activity?.layoutInflater, R.layout.product_images_layout, null, false)
            productImagesBinding?.mediaGalleryEntry = productItem?.media_gallery_entries?.get(i)
            Utils.setImageViewHeight(activity as Context, productImagesBinding?.imgProductImage, 27)
            ll_color_choice.addView(productImagesBinding?.root)
        }
    }

    fun setColorImagesList(){
        val length = productItem?.extension_attributes?.configurable_product_options?.size!!
        for(i in 0..length-1){
            val option = productItem?.extension_attributes?.configurable_product_options?.get(i)
            when(option?.label){
                "Color" -> {
                    option.values.forEachIndexed {index, value ->
                        colorMap.put(index.toString(), value = value.value_index.toString())
                    }
                    AppLog.e("ColorList : " +colorMap.toString())
                    colorAttrId = productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                    getProductOptions(colorAttrId, "color")
                }
                "Size" -> {
                    option.values.forEachIndexed {index, value ->
                        sizeMap.put(index.toString(), value = value.value_index.toString())
                    }
                    AppLog.e("Sizelist : " + sizeMap.toString())
                    sizeAttrId = productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                    getProductOptions(sizeAttrId, "size")
                }
            }
        }
    }

    fun getProductChildren(productSku : String?){
        productItemViewModel.getProductChildren(productSku)
    }

    fun getProductOptions(attributeId : String?, label : String?){
        productItemViewModel.getProductOptions(attributeId, label)
    }

    fun getStaticPagesUrl(){
        productItemViewModel.getStaticPagesUrl()
    }

    fun observeEvents(){
        productItemViewModel.clickedAddBtnId?.observe(this, Observer<Int> { id ->
            when (id){
                R.id.btn_add_to_bag -> {
                    openBottomSizeSheet ()
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_composition_and_care -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.composition_and_care)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_size_guideline -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.size_guideline)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_shipping -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.shipping)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_return -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.returns)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_share -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_buying_guidelinie -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.buying_guideline)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_chat -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_wishlist -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
            }

        })

        productItemViewModel.productChildrenResponse?.observe(this, object : Observer<ApiResponse<List<ChildProductsResponse>>> {
            override fun onChanged(apiResponse: ApiResponse<List<ChildProductsResponse>>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is List<*>) {
                    val list = response as List<ChildProductsResponse>

                    list.forEach {
                        val value =it.custom_attributes.filter { s ->
                            s.attribute_code == "color"
                        }.single().value.toString()
                        if(!childProductsMap.containsKey(value))
                            childProductsMap.put(value, it.media_gallery_entries)
                    }

                    setColorViewList()

                    AppLog.e("ChildProductsMap : " + childProductsMap.toString())


                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })

        productItemViewModel.productOptionResponse?.observe(this, object : Observer<ApiResponse<List<ProductOptionsResponse>>> {
            override fun onChanged(apiResponse: ApiResponse<List<ProductOptionsResponse>>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is List<*>) {
                    val list = response as List<ProductOptionsResponse>
                    list.get(0).label
                    when(list.get(0).label){
                        "color" -> {
                            AppLog.e("color index : " + (response.get(0) as ProductOptionsResponse).label!!)
                            colorOptionList = list.filter {
                                it.value in colorMap.values
                            }
                            AppLog.e("New color list : " + colorOptionList.toString())
                        }
                        "size" -> {
                            AppLog.e("size index : " + (response.get(0) as ProductOptionsResponse).label!!)
                            sizeOptionList = list.filter {
                                it.value in sizeMap.values
                            }
                            AppLog.e("New size list : " + sizeOptionList.toString())
                        }
                    }

                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })

        productItemViewModel.staticPagesUrlResponse?.observe( this, object : Observer<ApiResponse<StaticPagesUrlResponse>> {
            override fun onChanged(apiResponse: ApiResponse<StaticPagesUrlResponse>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if(response is StaticPagesUrlResponse){
                    productItemViewModel.staticPages = response
                }
                else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun setColorViewList(){
        colorOptionList?.forEach {
           colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap.get(it.value)))
        }
        AppLog.e("colorsViewList : " + colorsViewList.toString())

    }

    fun openBottomSizeSheet()
    {
        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        mBottomSheetDialog.window.setGravity(Gravity.BOTTOM)
        view.tv_price.setText(Constants.IDR + productItem?.price.toString())
        mBottomSheetDialog.show()

        mBottomSheetDialog.btn_done.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(mBottomSheetDialog.isShowing){
                    mBottomSheetDialog.dismiss()
                }
            }
        })


    }

    companion object {

        fun getInstance(productItem : ProductListingDataClass.Item?, productSku : String?, position : Int?) =
                ProductViewFragment().apply {
                    this.productItem = productItem
                    this.productSku = productSku
                    this.position = position
                }

    }

    data class ColorsView(var label: String?, var attr_id:String?, var value : String?,
                          var list : List<MediaGalleryEntryChild>?)


}