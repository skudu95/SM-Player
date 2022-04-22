package com.kudu.androidmusicplayer.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.adapter.MusicAdapter
import com.kudu.androidmusicplayer.databinding.ActivityPlaylistDetailsBinding
import com.kudu.androidmusicplayer.model.checkPlaylist

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.customTheme)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int

        PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist = checkPlaylist(playlist = PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist)

        //recycler view
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //adapter
        adapter = MusicAdapter(this, PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter

        //back button
        binding.btnBackPD.setOnClickListener { finish() }
        //shuffle button
        binding.btnShufflePD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.btnAddPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.btnRemovePD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this, R.style.customDialogTheme)
            builder.setTitle("Remove All Songs")
                .setMessage("Do you want to remove all songs from playlist?")
//                        .setBackground(R.color.navBarColor)
                .setPositiveButton("Yes") { dialog, _ -> // 1st _ dialog, 2nd _ result
                    PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.tvPlaylistNamePD.text = PlayListActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.playlistInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On: \n${PlayListActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "- ${PlayListActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if (adapter.itemCount > 0){
            //for image
            Glide.with(this)
                .load(PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
                .into(binding.imgPlaylistPD)
            binding.btnShufflePD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //storing music playlist data using shared preferences
        val editor = getSharedPreferences("PLAYLIST", MODE_PRIVATE).edit() //data cannot be accessed by other apps
        val jSonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jSonStringPlaylist)
        editor.apply()
    }

}