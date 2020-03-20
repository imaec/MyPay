package com.imaec.mypay.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.imaec.mypay.R
import kotlinx.android.synthetic.main.dialog_common.*

class CommonDialog(context: Context) : Dialog(context) {

    private var title = "안내"
    private var content = ""
    private var confirm = "확인"
    private var cancel = "취소"
    private var visible = true

    private var listenerConfirm: (Dialog) -> Unit =  { dismiss() }
    private var listenerCancel: (Dialog) -> Unit = { dismiss() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_common)


        val params = window?.attributes?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        window?.attributes = params

        text_title.text = title
        text_content.text = content
        text_confirm.text = confirm
        text_cancel.text = cancel
        if (!visible) text_cancel.visibility = View.GONE

        text_confirm.setOnClickListener {
            listenerConfirm(this@CommonDialog)
        }
        text_cancel.setOnClickListener {
            listenerCancel(this@CommonDialog)
        }
    }

    override fun show() {
        if (content.isEmpty()) return

        super.show()
    }

    fun setTitle(title: String) : CommonDialog {
        this.title = title
        return this
    }

    fun setContent(content: String) : CommonDialog {
        this.content = content
        return this
    }

    fun isOneButton(isOneButton: Boolean) : CommonDialog {
        this.visible = !isOneButton
        return this
    }

    fun setOnClickConfirm(str: String, listener: (Dialog) -> Unit) : CommonDialog {
        this.confirm = str
        listenerConfirm = listener
        return this
    }

    fun setOnClickCancel(str: String, listener: (Dialog) -> Unit) : CommonDialog {
        this.cancel = str
        listenerCancel = listener
        return this
    }
}