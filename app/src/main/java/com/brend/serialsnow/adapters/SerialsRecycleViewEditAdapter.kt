package com.brend.serialsnow.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.brend.serialsnow.databinding.RvItemBinding
import com.brend.serialsnow.models.SerialInfo
import com.squareup.picasso.Picasso

class SerialsRecycleViewEditAdapter(
        private var serials: ArrayList<SerialInfo>,
        private var editMode: Boolean = false,
        private var listener: OnItemClickListener?
) : RecyclerView.Adapter<SerialsRecycleViewEditAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(serials[position], editMode, listener)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SerialsRecycleViewEditAdapter.ViewHolder {
        val inflate = LayoutInflater.from(parent?.context)
        val binding = RvItemBinding.inflate(inflate, parent, false)

        return SerialsRecycleViewEditAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int = serials.size

    class ViewHolder(private var binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serialInfo: SerialInfo, editMode: Boolean, listener: OnItemClickListener?) {
            binding.serialInfo = serialInfo
            binding.editMode = editMode
            Picasso.with(binding.root.context).load(serialInfo.IMAGE).into(binding.image)
            binding.editChk.isChecked = false

            listener?.let {
                binding.root.setOnClickListener { view ->
                    run {
                        it.onItemClick(serialInfo)
                        binding.editChk.isChecked = !binding.editChk.isChecked
                    }
                }

                binding.editChk.setOnCheckedChangeListener { buttonView, isChecked -> it.onCheckBoxChanged(serialInfo, isChecked) }
            }

            binding.executePendingBindings()
        }
    }

    fun setEditMode(data: Boolean) {
        editMode = data
    }

    fun replaceData(data: ArrayList<SerialInfo>) {
        serials = data
    }

    interface OnItemClickListener {
        fun onItemClick(serial: SerialInfo)
        fun onCheckBoxChanged(serial: SerialInfo, isChecked: Boolean)
    }
}