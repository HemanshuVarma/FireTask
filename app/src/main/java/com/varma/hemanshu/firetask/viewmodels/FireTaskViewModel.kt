package com.varma.hemanshu.firetask.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    //Initializing with checking Internet
    init {
        checkNetwork(application)
    }

    /**
     * Checking Internet connectivity.
     * if connected to internet then show Firebase Auth UI Login,
     * else show No internet Animation.
     */
    fun checkNetwork(application: Application) {
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

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared Called")
    }
}