package com.localmarket.app.ui.vendor

import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.localmarket.app.R

class DialogAddProductBinding(view: View) {
    val textViewProductName: TextView = view.findViewById(R.id.textViewProductName)
    val textInputLayoutPrice: TextInputLayout = view.findViewById(R.id.textInputLayoutPrice)
    val editTextPrice: TextInputEditText = view.findViewById(R.id.editTextPrice)
    val textInputLayoutStock: TextInputLayout = view.findViewById(R.id.textInputLayoutStock)
    val editTextStock: TextInputEditText = view.findViewById(R.id.editTextStock)
    
    companion object {
        fun bind(view: View): DialogAddProductBinding {
            return DialogAddProductBinding(view)
        }
    }
}