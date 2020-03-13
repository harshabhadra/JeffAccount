package com.example.jeffaccount.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.databinding.ActivityMainBinding
import com.infideap.drawerbehavior.Advance3DDrawerLayout
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout:Advance3DDrawerLayout
    private lateinit var appBarConfigaration: AppBarConfiguration
    var loginCred:LogInCred? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainBinding = DataBindingUtil.
            setContentView<ActivityMainBinding>(this,
                R.layout.activity_main
            )

        val toolbar = mainBinding.mainToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        drawerLayout = mainBinding.drawerLayout

        val navController = this.findNavController(R.id.nav_host_fragment)
        appBarConfigaration = AppBarConfiguration(navController.graph,drawerLayout)
        toolbar.setupWithNavController(navController, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this,
            navController, drawerLayout)

        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, bundle: Bundle? ->

            if (nd.id == nc.graph.startDestination){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                mainBinding.mainCoordinatorLayout.background = ContextCompat.getDrawable(this,R.drawable.home)
            }else{
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                mainBinding.mainCoordinatorLayout.background = ContextCompat.getDrawable(this,R.drawable.loginpage)
            }
        }
        NavigationUI.setupWithNavController(mainBinding.navigationView,navController)

        drawerLayout.setViewRotation(Gravity.START, 10F)
        drawerLayout.setViewElevation(Gravity.START, 15F)

        //Getting intent
        loginCred = intent.getParcelableExtra("login")
        Timber.e("username: ${loginCred?.userName}, password: ${loginCred?.password}")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navcontroller = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navcontroller,appBarConfigaration)
    }
}
