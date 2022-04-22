package com.kudu.androidmusicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.activities.MainActivity
import com.kudu.androidmusicplayer.activities.PlayListActivity
import com.kudu.androidmusicplayer.activities.PlayerActivity
import com.kudu.androidmusicplayer.activities.PlaylistDetails
import com.kudu.androidmusicplayer.databinding.MusicViewBinding
import com.kudu.androidmusicplayer.model.Music
import com.kudu.androidmusicplayer.model.formatDuration

class MusicAdapter(
    private var context: Context,
    private var musicList: ArrayList<Music>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) : RecyclerView.Adapter<MusicAdapter.MusicHolder>() {

    class MusicHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {

        //initialising
        val title = binding.songNameMV
        val album = binding.albumNameMV
        val image = binding.imgSongMV
        val duration = binding.songDurationMV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    // while display
    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.title.text = musicList[position].title
        //holder.title.isSelected = true // TODO: remove this if not needed
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        //for image
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
            .into(holder.image)
        //playlist details
        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)

                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position])) {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.navHeaderTextColor))
                    } else {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                    }
                }
            }
            else -> {
                //upon click on list of audio
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent("MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else -> sendIntent("MusicAdapter", pos = position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    private fun addSong(song: Music): Boolean {
        PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(
                    index
                )
                return false
            }
        }
        PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

}