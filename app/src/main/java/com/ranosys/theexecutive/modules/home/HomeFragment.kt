package com.ranosys.theexecutive.modules.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeFragment : BaseFragment() {

    var homeModelView: HomeModelView? = null

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeModelView = ViewModelProviders.of(this).get(HomeModelView::class.java)
        mViewDataBinding?.mainfragmentviewmodel = homeModelView
        mViewDataBinding?.executePendingBindings()
        observeButtonClicks()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.title_home))
        setLeftIcon(R.drawable.ic_action_backward)
    }

    private fun observeButtonClicks() {
        homeModelView?.buttonClicked?.observe(this, Observer<HomeDataClass.HomeUserData>{ userData ->
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        recycler_items.layoutManager = LinearLayoutManager(activity)
//        recycler_items.adapter = RecyclerAdapter(object : RecyclerAdapter.OnItemClickListener {
//            override fun onItemClick(item: HomeDataClass.HomeUserData, position: Int) {
//                when(position){
//                    0->{
//                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()
//                    }
//                    1-> {
//                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()
//
//                    }
//                    2->{
//                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()
//
//                    }
//                    4->{
//                        val prefInstance = SavedPreferences.getInstance()
//                        prefInstance?.setIsLogin(false)
//                        prefInstance?.storeUserEmail("")
//                        var signoutIntent = Intent(activity, UserActivity::class.java)
//                        startActivity(signoutIntent)
//                        activity.finish()
//
//                    }
//                }
//            }
//        })
    }

}