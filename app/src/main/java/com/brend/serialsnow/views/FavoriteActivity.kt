package com.brend.serialsnow.views

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.widget.TextView
import com.brend.serialsnow.R
import com.brend.serialsnow.adapters.SerialsRecycleViewEditAdapter
import com.brend.serialsnow.databinding.ActivityFavoriteBinding
import com.brend.serialsnow.models.Favorites
import com.brend.serialsnow.models.SerialInfo
import com.brend.serialsnow.utils.Utility
import com.brend.serialsnow.viewmodels.FavoriteViewModel
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class FavoriteActivity : AppCompatActivity() {

    lateinit var binding: ActivityFavoriteBinding

    private var serialsRecycleViewAdapter: SerialsRecycleViewEditAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorite)

        val viewModel = ViewModelProviders.of(this).get(FavoriteViewModel::class.java)
        binding.viewModel = viewModel

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.favorite_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.loadFavorites(this)
        serialsRecycleViewAdapter = SerialsRecycleViewEditAdapter(viewModel.favorites, false, object : SerialsRecycleViewEditAdapter.OnItemClickListener {
            override fun onItemClick(serial: SerialInfo) {

                if (!viewModel.editMode.get()) {
                    val intent = Intent(this@FavoriteActivity, SerialInfoActivity::class.java)
                    intent.putExtra("id", serial.ID)
                    intent.putExtra("title", serial.TITLE_RU)
                    startActivity(intent)
                }
            }

            override fun onCheckBoxChanged(serial: SerialInfo, isChecked: Boolean) {
                if (viewModel.editMode.get()) {
                    if(isChecked) {
                        viewModel.selectedFavorites.add(serial.ID as String)
                    }
                    else {
                        viewModel.selectedFavorites.remove(serial.ID as String)
                    }
                }
            }

        })
        val scaleInAnimationAdapter = ScaleInAnimationAdapter(serialsRecycleViewAdapter)
        scaleInAnimationAdapter.setDuration(500)
        binding.itemsRv.adapter = scaleInAnimationAdapter
        binding.itemsRv.layoutManager = GridLayoutManager(this, Utility.calculateNoOfColumns(this), StaggeredGridLayoutManager.VERTICAL, false)

        binding.editModeBtn.setOnClickListener {
            viewModel.selectedFavorites.clear()
            updateEditMode(true)
        }

        binding.removeBtn.setOnClickListener {
            AlertDialog.Builder(it.context, R.style.MyDialogTheme)
                    .setTitle(getString(R.string.dialog_remove_title))
                    .setMessage(getString(R.string.favorite_remove_message))
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton(getString(R.string.dialog_yes), { dialogInterface, i ->
                        Favorites(this).remove(viewModel.selectedFavorites)
                        viewModel.selectedFavorites.clear()
                        viewModel.loadFavorites(this)
                        serialsRecycleViewAdapter?.replaceData(viewModel.favorites)
                        updateEditMode(false)
                    })
                    .setNegativeButton(getString(R.string.dialog_no), null)
                    .show()

        }

        binding.executePendingBindings()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK ->

                if (binding.viewModel?.editMode?.get() == true) {
                    updateEditMode(false)
                    return true
                }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateEditMode(data: Boolean) {
        binding.viewModel?.editMode?.set(data)
        serialsRecycleViewAdapter?.setEditMode(data)
        serialsRecycleViewAdapter?.notifyDataSetChanged()
    }
}
