package com.kudu.androidmusicplayer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.adapter.MusicAdapter
import com.kudu.androidmusicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(R.style.customTheme)
        setContentView(binding.root)

        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.musicListMA,  selectionActivity = true)
        binding.selectionRV.adapter = adapter

        binding.btnBackSA.setOnClickListener { finish() }
        //search view
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
//                Toast.makeText(this@MainActivity, newText.toString(), Toast.LENGTH_SHORT).show()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MainActivity.musicListMA)
                        if (song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }

        })
    }
}