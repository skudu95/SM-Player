package com.kudu.androidmusicplayer.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.adapter.PlaylistViewAdapter
import com.kudu.androidmusicplayer.databinding.ActivityPlayListBinding
import com.kudu.androidmusicplayer.databinding.AddPlaylistDialogBinding
import com.kudu.androidmusicplayer.model.MusicPlaylist
import com.kudu.androidmusicplayer.model.Playlist
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayListBinding
    private lateinit var playlistAdapter: PlaylistViewAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.customTheme)
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_play_list)
        setContentView(binding.root)

        binding.playListRV.setItemViewCacheSize(5)
        binding.playListRV.layoutManager = GridLayoutManager(this@PlayListActivity, 3)
        playlistAdapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playListRV.adapter = playlistAdapter

        binding.btnBackPL.setOnClickListener { finish() }

        //add playlist
        binding.btnAddPL.setOnClickListener {
            customAlertDialog()
        }

    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlayListActivity)
            .inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this, R.style.customDialogTheme)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add") { dialog, _ -> // 1st _ dialog, 2nd _ result
                val playlistName = binder.playlistNameDialog.text
                val createdBy = binder.createdByDialog.text
                if (playlistName != null && createdBy != null)
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                dialog.dismiss()
            }
            .show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref){
            if (name == i.name){
                playlistExists = true
                break
            }
        }
        if (playlistExists) {
            Toast.makeText(this, "Playlist Exists!!", Toast.LENGTH_SHORT).show()
        }else{
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            playlistAdapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }
}