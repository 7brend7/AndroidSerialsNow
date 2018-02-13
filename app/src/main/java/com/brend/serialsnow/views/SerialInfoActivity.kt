package com.brend.serialsnow.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.brend.serialsnow.R
import com.brend.serialsnow.databinding.ActivitySerialInfoBinding
import com.brend.serialsnow.viewmodels.SerialInfoViewModel
import com.brend.serialsnow.viewmodels.SerialInfoViewModelFactory
import com.squareup.picasso.Picasso

class SerialInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivitySerialInfoBinding

    private var id: String? = null

    lateinit var toolbar: Toolbar

    private lateinit var toolbarTitle: TextView

    companion object {
        const val SPINNER_TYPE_SEASON: Int = 1
        const val SPINNER_TYPE_EPISODE: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_serial_info)

        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbar_title)

        id = intent.extras.getString("id")
        toolbarTitle.text = intent.extras.getString("title")
        toolbarTitle.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewModel = ViewModelProviders.of(this, SerialInfoViewModelFactory(application, this, id ?: "")).get(SerialInfoViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.serialInfoResponse.observe(this, Observer {
            it?.let {
                Picasso.with(binding.root.context).load(it.serialInfo?.IMAGE).into(binding.infoImage)

                binding.serialInfo = it.serialInfo

                val translations = ArrayList<String>()
                it.translation?.let {
                    for ((key, value) in it.iterator()) {
                        translations.add(value.TRANSLATOR ?: "")
                    }
                }

                binding.translationSpinner.adapter = object : ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, translations) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val view = super.getView(position, convertView, parent)
                        (view as TextView).setTextColor(Color.WHITE)

                        when (binding.root.tag) {
                            "sw600", "sw600-land" -> view.textSize = 18.0F
                        }

                        return view
                    }
                }
            }
        })

        viewModel.translation.observe(this, Observer<String> {
            buildTranslationLists(this, SPINNER_TYPE_SEASON)
            buildTranslationLists(this, SPINNER_TYPE_EPISODE)
        })

        viewModel.season.observe(this, Observer<String> {
            buildTranslationLists(this, SPINNER_TYPE_EPISODE)
        })

        viewModel.episode.observe(this, Observer<String> {
            buildTranslationLists(this, 0)
        })

        binding.translationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(view: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos >= 0) {

                    val selectedValue = parent?.getItemAtPosition(pos)

                    if (viewModel.translation.value != selectedValue) {
                        viewModel.seasonsKey.set(0)
                        viewModel.translation.value = selectedValue as String
                    }
                }
            }
        }

        binding.seasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos >= 0) {

                    val selectedValue = parent?.getItemAtPosition(pos)

                    if (selectedValue != viewModel.season.value) {
                        viewModel.episodesKey.set(0)
                        viewModel.season.value = selectedValue as String
                    }

                    viewModel.seasonsKey.set(pos)
                }
            }
        }

        binding.episodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos >= 0) {

                    val selectedValue = parent?.getItemAtPosition(pos)

                    if (selectedValue != viewModel.episode.value) {
                        viewModel.episodesKey.set(0)
                        viewModel.episode.value = selectedValue as String
                    }

                }
            }

        }

        binding.openInPlayerBtn.setOnClickListener {
            val url = "https://streamguard.cc/video/${viewModel.translationToken.get()}/index.m3u8?dreams_content_id=1897"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setDataAndType(Uri.parse(url), "video/mp4")
            startActivity(intent)
        }

        binding.playBtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("streamUri", "https://streamguard.cc/video/${binding.viewModel?.translationToken?.get()}/index.m3u8?dreams_content_id=1897")
            intent.putExtra("title", toolbarTitle.text as String?)
            startActivity(intent)
        }

        binding.executePendingBindings()
    }

    private fun buildTranslationLists(context: Context, spinnerType: Int) {
        binding.viewModel?.serialInfoResponse?.value?.translation?.let {
            for ((key, value) in it.iterator()) {
                if (value.TRANSLATOR == binding.viewModel?.translation?.value) {
                    value.TOKENS?.let {
                        val seasons = ArrayList<String>()
                        val seasonsHash = HashMap<String, String>()

                        val episodes = ArrayList<String>()
                        val episodesHash = HashMap<String, String>()

                        for ((key, value) in it.iterator()) {
                            val seasonKey = getString(R.string.season_key, key)
                            seasons.add(seasonKey)
                            seasonsHash[seasonKey] = key

                            if (binding.viewModel?.season?.value == seasonKey) {
                                for ((key, value) in value.iterator()) {
                                    val episodeKey = getString(R.string.episode_key, key)
                                    episodes.add(episodeKey)
                                    episodesHash[episodeKey] = key

                                    if (binding.viewModel?.episode?.value == episodeKey) {
                                        binding.viewModel?.translationToken?.set(value)
                                    }
                                }

                                break
                            }
                        }

                        if (spinnerType == SPINNER_TYPE_SEASON) {
                            buildSeasonSpinner(seasonsHash, context, seasons)
                        }

                        if (spinnerType == SPINNER_TYPE_EPISODE) {
                            buildEpisodeSpinner(episodesHash, context, episodes)
                        }
                    }

                    break
                }
            }
        }
    }

    private fun buildEpisodeSpinner(episodesHash: HashMap<String, String>, context: Context, episodes: ArrayList<String>) {
        binding.viewModel?.episodesHash = episodesHash
        binding.episodeSpinner.adapter = object : ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, episodes) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(Color.WHITE)
                view.text = episodesHash[view.text]

                when (binding.root.tag) {
                    "sw600", "sw600-land" -> view.textSize = 18.0F
                }

                return view
            }
        }
        binding.episodeSpinner.setSelection(binding.viewModel?.episodesKey?.get() ?: 0)
    }

    private fun buildSeasonSpinner(seasonsHash: HashMap<String, String>, context: Context, seasons: ArrayList<String>) {
        binding.viewModel?.seasonHash = seasonsHash
        binding.seasonSpinner.adapter = object : ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, seasons) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(Color.WHITE)
                view.text = seasonsHash[view.text]

                when (binding.root.tag) {
                    "sw600", "sw600-land" -> view.textSize = 18.0F
                }

                return view
            }
        }
        binding.seasonSpinner.setSelection(binding.viewModel?.seasonsKey?.get() ?: 0)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
