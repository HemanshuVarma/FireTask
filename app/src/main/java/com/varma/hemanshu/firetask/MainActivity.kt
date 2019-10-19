package com.varma.hemanshu.firetask

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.varma.hemanshu.firetask.databinding.ActivityMainBinding
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModel
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FireTaskViewModel

    private var networkConnected: Boolean = false

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

        checkNetwork()
    }

    /**
     * Checking Internet connectivity.
     * if connected to internet then show Firebase Auth UI Login,
     * else show No internet Animation.
     */
    private fun checkNetwork() {

        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        //Check for Internet if block for Android version Marshmallow otherwise else block for below version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    networkConnected = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        networkConnected = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        networkConnected = true
                    }
                }
            }
        }
        if (networkConnected) {
            showLoginUI()
        } else {
            binding.noNetworkAnim.visibility = View.VISIBLE
            invalidateOptionsMenu()
        }
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
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Sign In Success ${user?.displayName}", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Sign in failed due to back button pressed. Exit the App
                if (response == null) {
                    Toast.makeText(this, "Sign-in Cancelled", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
                // Some error from client/server
                else {
                    Toast.makeText(
                        this, "Error : ${response.error?.errorCode}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
        menuItem?.isVisible = networkConnected
        return super.onPrepareOptionsMenu(menu)
    }

    //Handling click event for Menu Item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                signOutUser()
            }
        }
        return true
    }

    //called to sign out a user from App
    private fun signOutUser() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                checkNetwork()
            }
    }
}
