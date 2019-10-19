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


private const val RC_SIGN_IN = 1
var networkConnected: Boolean = false

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FireTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Getting ViewModel ref.
        viewModel = ViewModelProviders.of(this).get(FireTaskViewModel::class.java)
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

        // Create and launch sign-in intent
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
                Toast.makeText(
                    applicationContext,
                    "Sign In Success ${user?.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    Toast.makeText(applicationContext, "Sign-in Cancelled", Toast.LENGTH_SHORT)
                        .show()
                    //Exit App
                    finishAffinity()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error : ${response.error?.errorCode}",
                        Toast.LENGTH_SHORT
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

    // Called to signOut a user account
    private fun signOutUser() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { checkNetwork() }
    }
}
