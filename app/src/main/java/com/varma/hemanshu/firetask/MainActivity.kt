package com.varma.hemanshu.firetask

import android.app.Activity
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
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.varma.hemanshu.firetask.databinding.ActivityMainBinding
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModel
import com.varma.hemanshu.firetask.viewmodels.FireTaskViewModelFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FireTaskViewModel
    private lateinit var adapter: FireTaskAdapter

    //Firebase instances
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference

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

        //Initializing Firebase Components
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference

        //Adapter ref.
        adapter = FireTaskAdapter()

        //Setting Adapter ref. to RecyclerView
        binding.messagesList.adapter = adapter

        //passing the data into adapter
        viewModel.database.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

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

        viewModel.textWatcher(binding)
        binding.sendButton.setOnClickListener { sendData() }
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
                .setIsSmartLockEnabled(false)
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
                val user = mFirebaseAuth.currentUser
                Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
            } else {
                //Sign in failed due to back pressed or client/server error
                if (response == null) {
                    Toast.makeText(this, getString(R.string.sing_in_cancelled), Toast.LENGTH_SHORT)
                        .show()
                    Timber.e("Sign-In cancelled by User")
                    finishAffinity()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.error_firebase),
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.e("Error : ${response.error?.errorCode}")
                }
            }
        }
    }

    private fun sendData() {
        val userName = mFirebaseAuth.currentUser?.displayName.toString()
        val messageToStore = binding.messageEditText.text.toString().trim()
        val uidFirebase = mDatabaseReference.push().key.toString()
        Toast.makeText(
            this, "UserName: $userName \n Message: $messageToStore \n UID: $uidFirebase",
            Toast.LENGTH_SHORT
        ).show()
        val firebaseData = FireTask(userName, messageToStore, uidFirebase)
        binding.messageEditText.text.clear()
        mDatabaseReference.child(uidFirebase).setValue(firebaseData)
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
                viewModel.checkNetwork(application)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }
}