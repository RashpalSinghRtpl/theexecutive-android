package com.ranosys.theexecutive.modules.category

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCategoryBinding
import com.ranosys.theexecutive.databinding.HomeViewPagerBinding
import com.ranosys.theexecutive.modules.category.adapters.CustomViewPageAdapter
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.home_view_pager.view.*

/**
 * @Details Class showing categories on Home screen
 * @Author Ranosys Technologies
 * @Date 21,Feb,2018
 */
class CategoryFragment : BaseFragment() {

    private var categoryModelView: CategoryModelView? = null
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var viewPager : ViewPager
    private lateinit var pagerAdapter:CustomViewPageAdapter
    private lateinit var promotionBinding : HomeViewPagerBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentCategoryBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        categoryModelView = ViewModelProviders.of(this).get(CategoryModelView::class.java)
        mViewDataBinding?.categoryViewModel = categoryModelView
        mViewDataBinding?.executePendingBindings()

        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val inflater = LayoutInflater.from(context)
        promotionBinding = DataBindingUtil.inflate(inflater, R.layout.home_view_pager, null, false)
        promotionBinding.categoryModel = categoryModelView
        promotionBinding.tvPromotionText.text = GlobalSingelton.instance?.configuration?.home_promotion_message

        // update height of promotion view pager according to ration 1.5
        Utils.setViewHeightWrtDeviceWidth(activity as Context, promotionBinding.viewpager, Constants.IMAGE_RATIO)
        viewPager = promotionBinding.root?.viewpager!!

        pagerAdapter = CustomViewPageAdapter(view.context, categoryModelView?.promotionResponse?.get())
        promotionBinding.viewpager.adapter = pagerAdapter
        pagerAdapter.setItemClickListener(listener = object: CustomViewPageAdapter.OnItemClickListener{
            override fun onItemClick(item: PromotionsResponseDataClass?) {
                when(item?.type){
                    Constants.TYPE_CATEGORY -> {
                        val bundle = Bundle()
                        bundle.putInt(Constants.CATEGORY_ID, item.value.toInt())
                        bundle.putString(Constants.CATEGORY_NAME, item.title)
                        FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                    }

                    Constants.TYPE_PRODUCT -> {
                        val fragment = ProductDetailFragment.getInstance(null, item.value, item.title, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }

                    Constants.TYPE_CMS_PAGE -> {
                        if(item.value.isNotBlank()){
                            prepareWebPageDialog(activity as Context, item.value ,item.title)
                        }

                    }
                }
            }
        })
        elv_parent_category.addHeaderView(promotionBinding.root)

        elv_parent_category.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(groupPosition: Int) {
                if(groupPosition != previousGroup){
                    elv_parent_category.collapseGroup(previousGroup)
                }
                previousGroup = groupPosition
            }

        })


        elv_parent_category.setOnGroupClickListener { _, _, p2, _ ->
            if(categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.children_data?.size!! == 0){
                elv_parent_category.smoothScrollToPosition(0)
                val bundle = Bundle()
                bundle.putInt(Constants.CATEGORY_ID, categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.id!!)
                bundle.putString(Constants.CATEGORY_NAME, categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.name!!)
                FragmentUtils.addFragment(context!!, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
            }
            false
        }

        et_search_home.setOnClickListener { loadSearchFragment() }
        btn_search_action.setOnClickListener { loadSearchFragment() }

        /*et_search_home.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrBlank().not()){
                    et_search_home.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0)
                }
            }

        })

        et_search_home.setOnTouchListener(View.OnTouchListener { _, event ->
            val drawableRight = 2
            if(event.action == MotionEvent.ACTION_UP) {
                if(event.rawX >= et_search_home.right - et_search_home.compoundDrawables[drawableRight].bounds.width()) {
                    if(Utils.compareDrawable(activity as Context, et_search_home.compoundDrawables[drawableRight], (activity as Context).getDrawable(R.drawable.cancel))){
                        return@OnTouchListener true
                    }else if(Utils.compareDrawable(activity as Context, et_search_home.compoundDrawables[drawableRight], (activity as Context).getDrawable(R.drawable.search))){
                        if(et_search_home.text.isNotBlank()){
                            val query = et_search_home.text.toString()
                            et_search_home.setText("")
                            Utils.hideSoftKeypad(activity as Context)
                            elv_parent_category.smoothScrollToPosition(0)
                            val bundle = Bundle()
                            bundle.putString(Constants.SEARCH_FROM_HOME_QUERY, query)
                            FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                        }else{
                            Toast.makeText(activity as Context, getString(R.string.enter_search_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            false
        })

        et_search_home.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(v.text.toString().isEmpty().not()){
                    Utils.hideSoftKeypad(activity as Context)
                    elv_parent_category.smoothScrollToPosition(0)
                    val bundle = Bundle()
                    bundle.putString(Constants.SEARCH_FROM_HOME_QUERY, v.text.toString())
                    FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)

                }else{
                    Toast.makeText(activity as Context, getString(R.string.enter_search_error), Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })*/

        promotionBinding.tvPromotionText.setOnClickListener {
            if(!TextUtils.isEmpty(GlobalSingelton.instance?.configuration?.home_promotion_message_url))
            prepareWebPageDialog(activity,GlobalSingelton.instance?.configuration?.home_promotion_message_url, "" )
        }

        observePromotionsApiResponse()
        observeCategoryApiResponse()
        if (Utils.isConnectionAvailable(activity as Context)) {
            getPromotions()
            getCategories()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun loadSearchFragment() {
        FragmentUtils.addFragment(activity, SearchFragment.getInstance(searchAction = {searchStr ->
            val bundle = Bundle()
            bundle.putString(Constants.SEARCH_FROM_HOME_QUERY, searchStr)
            FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
        }), null, SearchFragment::class.java.simpleName, true)
    }

    override fun onDestroy() {
        super.onDestroy()
       handler.removeCallbacksAndMessages(null)
    }

    private fun getPromotions() {
        categoryModelView?.getPromotions()
    }

    private fun getCategories() {
        showLoading()
        categoryModelView?.getCategories()
    }


    private fun observePromotionsApiResponse() {
        categoryModelView?.mutualPromotionResponse?.observe(this, Observer<ApiResponse<List<PromotionsResponseDataClass>>> { apiResponse ->
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is List<*>) {
                categoryModelView?.promotionResponse?.set(response as List<PromotionsResponseDataClass>?)
                pagerAdapter.promotionList = categoryModelView?.promotionResponse?.get()
                pagerAdapter.notifyDataSetChanged()
                startScrollViewPager(viewPager, response.size)
            } else {
                Toast.makeText(activity, getString(R.string.common_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun observeCategoryApiResponse() {
        categoryModelView?.mutualHomeResponse?.observe(this, Observer<ApiResponse<CategoryResponseDataClass>> { apiResponse ->
            hideLoading()
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is CategoryResponseDataClass) {

                response.children_data = response.children_data.filter { it.is_active == true } as ArrayList<ChildrenData>

                //add view all category and remove inactive sub categories
                for(cat in response.children_data){
                    val viewAll = ChildrenData(id = cat.id, name = getString(R.string.view_all), is_active = true)
                    cat.children_data?.add(0, viewAll)
                    cat.children_data = cat.children_data?.filter{ it.is_active == true} as ArrayList<ChildrenData>
                }

                categoryModelView?.categoryResponse?.set(response)

            } else {
                Toast.makeText(activity, getString(R.string.common_error), Toast.LENGTH_LONG).show()
            }
        })
    }



    private fun startScrollViewPager(viewPager : ViewPager, count : Int){
        val runnable = object : Runnable {
            override fun run() {
                if (viewPager.currentItem < count - 1) {
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.setCurrentItem(0,false)
                }
                handler.postDelayed(this, Constants.SPLASH_TIMEOUT)
            }
        }
        handler.postDelayed(runnable, Constants.SPLASH_TIMEOUT)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            pagerAdapter.refresh(0)
        }else{
            pagerAdapter.refresh(1)
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SavedPreferences.getInstance()?.setBooleanValue(Constants.ORIENTATION, true)
        } else {
            SavedPreferences.getInstance()?.setBooleanValue( Constants.ORIENTATION, false)
        }
    }
}
