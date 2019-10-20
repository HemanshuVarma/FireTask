package com.varma.hemanshu.firetask.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.varma.hemanshu.firetask.databinding.ActivityMainBinding
import timber.log.Timber

class FireTaskViewModel(application: Application) : AndroidViewModel(application) {

    var showOverflow: Boolean = false

    //LiveData for showing Login when connected to Internet
    private val _showLogin = MutableLiveData<Boolean>()
    val showLogin: LiveData<Boolean> get() = _showLogin

    //LiveData for showing Offline status
    private val _showOffline = MutableLiveData<Boolean>()
    val showOffline: LiveData<Boolean> get() = _showOffline

    //LiveData for sign-in failed due to back pressed
    private val _backPressed = MutableLiveData<Boolean>()
    val backPressed: LiveData<Boolean> get() = _backPressed

    //LiveData for sign-in failed due to Client/Server error
    private val _errorWhileLogin = MutableLiveData<Boolean>()
    val errorWhileLogin: LiveData<Boolean> get() = _errorWhileLogin

    //Initializing with checking Internet
    init {
        checkNetwork(application)
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }

    /**
     * Checking Internet connectivity.
     * if connected to internet then show Firebase Auth UI Login,
     * else show No internet Animation.
     */
    private fun checkNetwork(application: Application) {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        if (isConnected) {
            _showLogin.value = true
            showOverflow = true
        } else {
            _showOffline.value = true
            showOverflow = false
        }
    }

    fun showLoginComplete() {
        _showLogin.value = false
    }

    fun showOfflineComplete() {
        _showOffline.value = false
    }

    fun backPressedComplete() {
        _backPressed.value = false
    }

    fun errorWhileLoginComplete() {
        _errorWhileLogin.value = false
    }

    fun signInProcess(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                //Sign in failed due to back pressed or client/server error
                if (response == null)
                    _backPressed.value = true
                else
                    _errorWhileLogin.value = true
            }
        }
    }

    fun chatLayoutVisibility(): Int {
        if (showOverflow) {
            return View.VISIBLE
        } else
            return View.GONE
    }

    fun noNetworkVisibility(): Int {
        if (showOverflow)
            return View.GONE
        else
            return View.VISIBLE
    }

    fun textWatcher(binding: ActivityMainBinding) {
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val messageString = binding.messageEditText.text.toString().trim()
                binding.sendButton.isEnabled = messageString.isNotEmpty()
            }
        })
    }

    //called to sign out a user from App
    fun signOutUser() {
        AuthUI.getInstance()
            .signOut(getApplication())
            .addOnCompleteListener {
                _showLogin.value = true
            }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared Called")
    }
}