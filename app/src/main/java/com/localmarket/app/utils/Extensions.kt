package com.localmarket.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.localmarket.app.R
import java.text.NumberFormat
import java.util.*

// This extension is defined at the end of the file

// Toast extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.showToast(message, duration)
}

// Snackbar extensions
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    Snackbar.make(this, message, duration)
        .setAction(actionText) { action() }
        .show()
}

// Image loading extension
fun ImageView.loadImage(url: String?, placeholder: Int = R.drawable.placeholder_image) {
    if (url.isNullOrEmpty()) {
        setImageResource(placeholder)
        return
    }
    
    Glide.with(context)
        .load(url)
        .placeholder(placeholder)
        .error(placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

// This function was moved to line 110

// WhatsApp intent
fun Context.openWhatsApp(phoneNumber: String, message: String = "") {
    try {
        val formattedNumber = if (phoneNumber.startsWith("+")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }
        
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        
        startActivity(intent)
    } catch (e: Exception) {
        showToast("WhatsApp is not installed on your device")
    }
}

// View visibility extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Format price to currency format
fun Double.formatPrice(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(this)
}

fun Int.formatPrice(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(this)
}

// Format distance to readable format
fun Double.distanceFormatted(): String {
    return if (this < 1.0) {
        String.format("%.0f m", this * 1000)
    } else {
        String.format("%.1f km", this)
    }
}

// Extension property for RecyclerView.ViewHolder to get binding adapter position
val RecyclerView.ViewHolder.bindingAdapterPosition: Int
    get() = adapterPosition

