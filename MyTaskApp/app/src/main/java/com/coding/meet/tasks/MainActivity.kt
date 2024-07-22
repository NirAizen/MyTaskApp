package com.coding.meet.tasks

import android.app.Dialog
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.coding.meet.tasks.databinding.ActivityMainBinding
import com.coding.meet.tasks.ui.TaskListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var loadingDialog: Dialog
    private lateinit var navController: NavController
    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showLoadingScreen() {
        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.loading_screen)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun hideLoadingScreen() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }

    }
}
