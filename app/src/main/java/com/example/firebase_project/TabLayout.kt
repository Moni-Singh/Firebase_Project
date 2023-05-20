package com.example.firebase_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.firebase_project.Adapter.PagerAdapters
import com.google.android.material.tabs.TabLayout
class TabLayout : AppCompatActivity() {
    private lateinit var pToolbar: Toolbar
    private lateinit var pTabs:TabLayout
    private lateinit var ptitle:TextView
    private lateinit var pViewPager:ViewPager
    private lateinit var pagerAdapters: PagerAdapters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout)
        //setting here tolbar
        pToolbar = findViewById(R.id.pToolBar)
        ptitle = findViewById(R.id.titleText)
        pTabs = findViewById(R.id.tabs)
        //settig here toolbar
        pToolbar.setTitle("")
        ptitle.setText("Its me moni ")
        setSupportActionBar(pToolbar)
        pViewPager = findViewById(R.id.myPagerView)
        pagerAdapters = PagerAdapters(supportFragmentManager)
       //setting fragment list
        pagerAdapters.addFragment(HomeFragment(),"")
        pagerAdapters.addFragment(ProfileFragment(),"")
        pagerAdapters.addFragment(SettingFragment(),"")
        ///setting view page adapter
        pViewPager.adapter = pagerAdapters
       //setting the tab
        pTabs.setupWithViewPager(pViewPager)
       //adding icon here
        pTabs.getTabAt(0)!!.setIcon(R.drawable.home)
        pTabs.getTabAt(1)!!.setIcon(R.drawable.setting)
        pTabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_person_24)
    }


}