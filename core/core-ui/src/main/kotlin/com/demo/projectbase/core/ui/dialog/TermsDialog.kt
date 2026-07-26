package com.demo.projectbase.core.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.demo.projectbase.core.ui.R

class TermsDialog(
    context: Context,
    private val listener: Listener
) {
    interface Listener {
        fun onAccept()
    }

    private val dialog: AlertDialog

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_terms, null)
        val btnAccept = view.findViewById<Button>(R.id.btnAccept)

        btnAccept.setOnClickListener {
            listener.onAccept()
            dialog.dismiss()
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(true)
        dialog = builder.create()
    }

    fun show() {
        dialog.show()
    }
}
