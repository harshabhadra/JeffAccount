package com.example.jeffaccount.ui

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.jeffaccount.LogInViewModel
import com.example.jeffaccount.R
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.databinding.ActivityMainBinding
import com.example.jeffaccount.model.CompanyDetails
import com.infideap.drawerbehavior.Advance3DDrawerLayout
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout:Advance3DDrawerLayout
    private lateinit var appBarConfigaration: AppBarConfiguration
    private lateinit var mainBinding: ActivityMainBinding
    lateinit var companyDetails:CompanyDetails
    private lateinit var logInViewModel: LogInViewModel
    lateinit var loginCred:LogInCred

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.
            setContentView<ActivityMainBinding>(this,
                R.layout.activity_main
            )

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        //Getting intent
        val intent = getIntent()
        intent?.let {
            companyDetails = it.getParcelableExtra("company")!!
            loginCred = it.getParcelableExtra("login")!!
            Toast.makeText(this, "Company is ${companyDetails.comname}",Toast.LENGTH_SHORT).show()
        }

        val toolbar = mainBinding.mainToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        drawerLayout = mainBinding.drawerLayout
        val navHostFragment = nav_host_fragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation)
        val navController = this.findNavController(R.id.nav_host_fragment)
        val bundle = Bundle()
        bundle.putString("filepath","welcome")
        navHostFragment.navController.setGraph(graph,bundle)
        appBarConfigaration = AppBarConfiguration(navController.graph,drawerLayout)
        toolbar.setupWithNavController(navController, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this,
            navController, drawerLayout)

        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, bundle: Bundle? ->

            if (nd.id == nc.graph.startDestination){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                mainBinding.mainCoordinatorLayout.background = ContextCompat.getDrawable(this,R.drawable.home)
                setToolbarText(getString(R.string.app_name))
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

    fun setToolbarText(title:String){
        mainBinding.appHeadingTv.text = title
    }
}
