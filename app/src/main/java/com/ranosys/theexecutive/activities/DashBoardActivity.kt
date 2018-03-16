package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.utils.FragmentUtils

/**
 * Created by Mohammad Sunny on 19/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        FragmentUtils.addFragment(this, HomeFragment.newInstance(), HomeFragment::class.java.name)

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                val backStackCount = supportFragmentManager.getBackStackEntryCount()
                if(backStackCount > 0){
                    val fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)
                    if(null != fragment){
                        if(fragment is HomeFragment)
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.app_title),0, false, 0, false )
                        if(fragment is LoginFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.login), R.drawable.back, true, 0, false)
                        }
                    }
                }
            }

        })

    }
}
