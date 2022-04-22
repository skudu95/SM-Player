package com.kudu.androidmusicplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.adapter.FavouriteAdapter
import com.kudu.androidmusicplayer.databinding.ActivityFavouriteBinding
import com.kudu.androidmusicplayer.model.Music
import com.kudu.androidmusicplayer.model.checkPlaylist

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favAdapter: FavouriteAdapter

    companion object{
        var favSongs: ArrayList<Music> = ArrayList()
        var favChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.customTheme)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favSongs = checkPlaylist(favSongs)

        binding.btnBackFA.setOnClickListener { finish() }

        binding.favSongsRV.setHasFixedSize(true)
        binding.favSongsRV.setItemViewCacheSize(5)
        binding.favSongsRV.layoutManager = GridLayoutManager(this, 4)
        favAdapter = FavouriteAdapter(this, favSongs )
        binding.favSongsRV.adapter = favAdapter

        favChanged = false

        //hide button
        if (favSongs.size < 1) binding.btnShuffleFA.visibility = View.INVISIBLE
        binding.btnShuffleFA.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if(favChanged){
            favAdapter.updateFavourites(favSongs)
            favChanged = true
        }
    }
}