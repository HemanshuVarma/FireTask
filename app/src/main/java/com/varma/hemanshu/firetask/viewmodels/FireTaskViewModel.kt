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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varma.hemanshu.firetask.FireTask
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

    //LiveData for data from Firebase
    private val _database = MutableLiveData<List<FireTask>>()
    val database: LiveData<List<FireTask>> get() = _database

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
            database()
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

    private val items: MutableList<FireTask> = mutableListOf()
    fun database() {
        if (_database.value == null) {
            FirebaseDatabase.getInstance().reference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        Timber.e("Loading messages cancelled ${databaseError.toException()}")
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            items.clear()
                            dataSnapshot.children.mapNotNullTo(items) {
                                it.getValue<FireTask>(FireTask::class.java)
                            }
                            _database.value = items
                        }
                    }
                })
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared Called")
    }
}