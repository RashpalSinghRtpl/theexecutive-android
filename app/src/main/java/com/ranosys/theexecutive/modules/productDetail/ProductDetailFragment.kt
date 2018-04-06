package com.ranosys.theexecutive.modules.productDetail

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductDetailBinding
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.fragment_product_detail.*

/**
 * @Class The class shows the details of the product.
 * @author Ranosys Technologies
 * @Date 02-Apr-2018
 */
class ProductDetailFragment : BaseFragment() {

    lateinit var productDetailViewModel : ProductDetailViewModel
    var bottomSheetBehavior = BottomSheetBehavior<View>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentProductDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
        mViewDataBinding?.productDetailVM = productDetailViewModel
        mViewDataBinding?.executePendingBindings()


        return mViewDataBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeViewPager = ProductViewPagerAdapter(this)
        product_viewpager.adapter = homeViewPager
        //product_viewpager.offscreenPageLimit = 2
    //    homeViewPager.observeAddToBagEvent()


    }



    fun openBottomSheet ()
    {
        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.getWindow()!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        mBottomSheetDialog.getWindow()!!.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()

        mBottomSheetDialog.btn_done.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(mBottomSheetDialog.isShowing){
                    mBottomSheetDialog.dismiss()
                }
            }
        })
    }

    fun toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }
}