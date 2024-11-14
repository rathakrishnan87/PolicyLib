package com.cbre.privacyprompt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbre.privacyprompt.databinding.PrivacyPromptItemBinding

class PolicyAdapter(private val textColor: Int) : RecyclerView.Adapter<PolicyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PrivacyPromptItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val items = ArrayList<String>()

    override fun getItemCount(): Int = items.size

    fun updateDataSet(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PrivacyPromptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            numberTextView.text = (position + 1).toString()
            bodyTextView.text = item
            numberTextView.setTextColor(textColor)
            bodyTextView.setTextColor(textColor)
        }
    }
}
