package com.brend.serialsnow.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.brend.serialsnow.R
import com.brend.serialsnow.adapters.TabPagerAdapter
import com.brend.serialsnow.databinding.ActivityMainBinding
import com.brend.serialsnow.models.Category
import com.brend.serialsnow.viewmodels.MainViewModel
import com.brend.serialsnow.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val tabPagerAdapter = TabPagerAdapter(supportFragmentManager, arrayListOf())
        binding.pager.adapter = tabPagerAdapter
        binding.slidingTabs.setupWithViewPager(binding.pager)
        binding.pageHeader.tabIndicatorColor = ContextCompat.getColor(this, R.color.colorAccent)

        val viewModel = ViewModelProviders.of(this, MainViewModelFactory(application, this)).get(MainViewModel::class.java)
        viewModel.categories.observe(this, Observer<ArrayList<Category>> {
            it?.let {
                tabPagerAdapter.replaceData(it)
                tabPagerAdapter.notifyDataSetChanged()
            }
        })

        binding.viewModel = viewModel


        setSupportActionBar(findViewById(R.id.toolbar))

        binding.executePendingBindings()
    }
}
