package com.brend.serialsnow.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brend.serialsnow.R
import com.brend.serialsnow.adapters.SerialsRecycleViewAdapter
import com.brend.serialsnow.databinding.FragmentTabBinding
import com.brend.serialsnow.models.Category
import com.brend.serialsnow.models.SerialInfo
import com.brend.serialsnow.utils.Utility
import com.brend.serialsnow.viewmodels.MainViewModel
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import java.util.*

class TabFragment : Fragment() {

    lateinit var binding: FragmentTabBinding
    private var position: Int = 0

    companion object Factory {
        private const val ARG_POSITION: String = "ARG_POSITION"

        fun newInstance(position: Int): TabFragment {
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            val fragment = TabFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments.getInt(ARG_POSITION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab, container, false)

        val viewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        val serialsRecycleViewAdapter = SerialsRecycleViewAdapter(arrayListOf(), object : SerialsRecycleViewAdapter.OnItemClickListener {
            override fun onItemClick(serial: SerialInfo) {
                val intent = Intent(context, SerialInfoActivity::class.java)
                intent.putExtra("id", serial.ID)
                intent.putExtra("title", serial.TITLE_RU)
                startActivity(intent)
            }

        })
        val scaleInAnimationAdapter = ScaleInAnimationAdapter(serialsRecycleViewAdapter)
        scaleInAnimationAdapter.setDuration(500)
        binding.itemsRv.adapter = scaleInAnimationAdapter
        binding.itemsRv.layoutManager = GridLayoutManager(context, Utility.calculateNoOfColumns(context), StaggeredGridLayoutManager.VERTICAL, false)

        viewModel.categories.observe(this, Observer<ArrayList<Category>> {
            it?.let {
                serialsRecycleViewAdapter.replaceData(it[position].serials)
                serialsRecycleViewAdapter.notifyDataSetChanged()
                binding.itemsRv.layoutManager.scrollToPosition(0)
            }
        })

        binding.swiperefresh.setOnRefreshListener {
            Utility.connectionCheck(context, {
                viewModel.loadSerials()
            })
            binding.swiperefresh.isRefreshing = false
        }

        return binding.root
    }
}