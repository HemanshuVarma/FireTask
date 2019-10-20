package com.varma.hemanshu.firetask

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("message")
fun TextView.setMessage(item: FireTask?) {
    item?.let { text = item.message }
}

@BindingAdapter("userName")
fun TextView.setUserName(item: FireTask?) {
    item?.let { text = item.username }
}