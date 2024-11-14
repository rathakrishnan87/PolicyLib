package com.cbre.privacyprompt

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cbre.privacyprompt.databinding.PrivacyPromptDialogBinding
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager


class PolicyPrompt(private val context: AppCompatActivity,
                   private val termsOfServiceUrl: String,
                   private val privacyPolicyUrl: String) {
    //Configure Color
    var backgroundColor = Color.parseColor("#ffffff")
    var titleTextColor = Color.parseColor("#222222")
    var subtitleTextColor = Color.parseColor("#757575")
    var linkTextColor = Color.parseColor("#000000")
    var termsOfServiceTextColor = Color.parseColor("#757575")

    // Accept Button
    var acceptButtonColor = Color.parseColor("#222222")
    var acceptTextColor = Color.parseColor("#ffffff")
    var acceptText = context.getString(R.string.cbre_accept)

    // Cancel
    var cancelText = context.getString(R.string.cbre_cancel)
    var cancelTextColor = Color.parseColor("#757575")

    // Strings
    var title = context.getString(R.string.cbre_terms_of_service)
    var termsOfServiceSubtitle = context.getString(R.string.cbre_terms_of_service_subtitle)

    var europeOnly = false

    private val lines = ArrayList<String>()

    // View binding for the activity
    private var _binding: PrivacyPromptDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val sharedPreferencesName = "CBREPolicies"
        private const val fieldPolicyAccepted = "CBREPolicyAccepted"
    }

    interface OnClickListener {
        fun onAccept(isFirstTime: Boolean)
        fun onCancel()
    }

    var onClickListener: OnClickListener? = null
    private var dialog: AlertDialog? = null

    private val sharedPref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    private val adapter: PolicyAdapter by lazy {
        PolicyAdapter(termsOfServiceTextColor)
    }

    private var policyAccepted: Boolean
        get() = sharedPref.getBoolean(fieldPolicyAccepted, false)
        set(value) = sharedPref.edit().putBoolean(fieldPolicyAccepted, value).apply()

    fun addPolicyLine(line: String) {
        lines.add(line)
    }

    private fun loadLayout(): View {
        _binding = PrivacyPromptDialogBinding.inflate(context.layoutInflater).apply {
            container.setBackgroundColor(backgroundColor)
            termsOfServiceTitle.apply {
                text = title
                setTextColor(titleTextColor)
            }
            termsOfServiceSubtitleTextView.apply {
                text = toHtml(termsOfServiceSubtitle)
                movementMethod = LinkMovementMethod.getInstance()
                setLinkTextColor(linkTextColor)
                setTextColor(subtitleTextColor)
            }
            acceptTextView.apply {
                text = acceptText
                setTextColor(acceptTextColor)
            }
            setBackgroundColor(acceptButton, acceptButtonColor)
            cancelTextView.apply {
                text = cancelText
                setTextColor(cancelTextColor)
            }
            acceptButton.setOnClickListener {
                dismiss()
                onClickListener?.onAccept(true)
                policyAccepted = true
            }
            cancelButton.setOnClickListener {
                dismiss()
                onClickListener?.onCancel()
                policyAccepted = false
            }
            policiesRecyclerView.layoutManager = LinearLayoutManager(context)
            policiesRecyclerView.adapter = adapter
            adapter.updateDataSet(lines)
        }
        return binding.root
    }

    private fun dismiss() {
        if (!context.isFinishing && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun show() {
        if (policyAccepted) {
            onClickListener?.onAccept(false)
            return
        }

        if (europeOnly && !ZoneHelper.isEu(context)) {
            onClickListener?.onAccept(false)
            return
        }

        dialog = AlertDialog.Builder(context)
            .setView(loadLayout())
            .setCancelable(false)
            .show()
    }

    fun forceReset() {
        policyAccepted = false
    }

    private fun toHtml(res: Int): Spanned {
        return toHtml(context.getString(res))
    }

    private fun toHtml(body: String): Spanned {
        val formattedBody = body
            .replace("{accept}", context.getString(R.string.cbre_accept))
            .replace("{privacy}", "<a href=\"$privacyPolicyUrl\">")
            .replace("{/privacy}", "</a>")
            .replace("{terms}", "<a href=\"$termsOfServiceUrl\">")
            .replace("{/terms}", "</a>")
            .replace("{", "<")
            .replace("}", ">")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(formattedBody, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(formattedBody)
        }
    }

    private fun setBackgroundColor(view: View?, resColor: Int) {
        view?.background?.let { background ->
            when (background) {
                is RippleDrawable -> background.setColorFilter(resColor, PorterDuff.Mode.MULTIPLY)
                is ColorDrawable -> background.color = resColor
                is GradientDrawable -> background.setColor(resColor)
            }
        }
    }
}