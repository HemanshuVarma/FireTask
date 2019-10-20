package com.varma.hemanshu.firetask

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.varma.hemanshu.firetask.databinding.ActivityMainBinding
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModel
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModelFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FireTaskViewModel
    private lateinit var adapterFireTask: FireTaskAdapter

    companion object {
        private const val RC_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        //Getting application(context) which is then passed to factoryViewModel
        //factoryViewModel will initialize viewModel with context
        //This is to prevent Memory leaks
        val application = requireNotNull(this).application
        val viewModelFactory = FireTaskViewModelFactory(application)
        //Getting ViewModel ref.
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FireTaskViewModel::class.java)
        //Linking ViewModel with created Ref.
        binding.fireTaskViewModel = viewModel

        initRecyclerView()
        addDataSet()

        viewModel.showLogin.observe(this, Observer {
            if (it == true) {
                showLoginUI()
                viewModel.showLoginComplete()
            }
        })
        viewModel.showOffline.observe(this, Observer {
            if (it == true) {
                invalidateOptionsMenu()
                viewModel.showOfflineComplete()
            }
        })
        viewModel.backPressed.observe(this, Observer {
            if (it == true) {
                exitApp()
                viewModel.backPressedComplete()
            }
        })
        viewModel.errorWhileLogin.observe(this, Observer {
            if (it == true) {
                showErrorDetails()
                viewModel.errorWhileLoginComplete()
            }
        })

        viewModel.textWatcher(binding)
    }

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        adapterFireTask.submitList(data)
    }

    private fun initRecyclerView() {
        binding.messagesList.apply {
            adapterFireTask = FireTaskAdapter()
            adapter = adapterFireTask
        }
    }


    private fun showErrorDetails() {
        Toast.makeText(this, getString(R.string.error_firebase), Toast.LENGTH_SHORT).show()

    }

    private fun exitApp() {
        Toast.makeText(this, getString(R.string.sing_in_cancelled), Toast.LENGTH_SHORT).show()
        finishAffinity()
    }

    //Method invoked only when connected to internet
    private fun showLoginUI() {
        // Authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_round)
                .build(),
            RC_SIGN_IN
        )
    }

    /**
     * This returns the Result of Sign In Auth from Firebase
     * @param requestCode unique code sent to server for Auth
     * @param resultCode is the Result of query which can be either SUCCESS/FAILURE
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.signInProcess(requestCode, resultCode, data)
    }

    //Menu Inflater
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    /**
     * Called after invalidateOptionsMenu() call
     * Inflating menu only if there's active internet.
     * Else hiding the menu to signOut.
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.findItem(R.id.signOut)
        menuItem?.isVisible = viewModel.showOverflow
        return super.onPrepareOptionsMenu(menu)
    }

    //Handling click event for Menu Item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                viewModel.signOutUser()
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }
}

class DataSource {

    companion object {
        fun createDataSet(): ArrayList<FireTask> {
            val list = ArrayList<FireTask>()
            list.add(
                FireTask(
                    "Hemanshu Varma",
                    "This is a sample text message retrieved from firebase realtime databse"
                )
            )
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(
                FireTask(
                    "Hemanshu",
                    "This is a sample text message retrieved from firebase realtime databse"
                )
            )
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Varma", "Hello sample message from firebase as a sample"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(
                FireTask(
                    "Hemanshu Varma",
                    "This is a sample text message retrieved from firebase realtime databse"
                )
            )
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Varma", "Hello sample message from firebase as a sample"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(
                FireTask(
                    "Hemanshu Varma",
                    "This is a sample text message retrieved from firebase realtime databse"
                )
            )
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(
                FireTask(
                    "Hemanshu Varma",
                    "This is a sample text message retrieved from firebase realtime databse"
                )
            )
            list.add(FireTask("Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu Varma", "Hello Firebase message"))
            list.add(FireTask("Hemanshu", "Hello Firebase message"))
            list.add(FireTask("Varma", "Hello Firebase message"))
            return list
        }
    }
}
