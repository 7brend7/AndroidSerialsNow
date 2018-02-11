package com.brend.serialsnow.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.brend.serialsnow.databinding.RvItemBinding
import com.brend.serialsnow.models.SerialInfo
import com.squareup.picasso.Picasso

class SerialsRecycleViewAdapter(
        private var serials: ArrayList<SerialInfo>,
        private var listener: OnItemClickListener?) : RecyclerView.Adapter<SerialsRecycleViewAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(serials[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent?.context)
        val binding = RvItemBinding.inflate(inflate, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = serials.size

    class ViewHolder(private var binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serialInfo: SerialInfo, listener : OnItemClickListener?) {
            binding.serialInfo = serialInfo
            Picasso.with(binding.root.context).load(serialInfo.IMAGE).into(binding.image)

            listener?.let {
                binding.root.setOnClickListener { view -> run {
                    it.onItemClick(serialInfo)
                } }
            }

            binding.executePendingBindings()
        }
    }

    fun replaceData(data: ArrayList<SerialInfo>) {
        serials = data
    }

    interface OnItemClickListener {
        fun onItemClick(serial: SerialInfo)
    }
}