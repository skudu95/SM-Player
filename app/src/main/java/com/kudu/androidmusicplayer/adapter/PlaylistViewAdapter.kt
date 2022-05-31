package com.kudu.androidmusicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.activities.PlayListActivity
import com.kudu.androidmusicplayer.activities.PlaylistDetails
import com.kudu.androidmusicplayer.databinding.PlaylistViewBinding
import com.kudu.androidmusicplayer.model.Playlist

class PlaylistViewAdapter(
    private var context: Context,
    private var playlistList: ArrayList<Playlist>
) :
    RecyclerView.Adapter<PlaylistViewAdapter.PlaylistHolder>() {

    class PlaylistHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {

        //initialising
        val image = binding.imgSongPV
        val name = binding.tvSongNamePV
        val root = binding.root
        val delete = binding.btnDelPV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        return PlaylistHolder(
            PlaylistViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    // while display
    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context, R.style.customDialogTheme)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete playlist?")
//                        .setBackground(R.color.navBarColor)
                .setPositiveButton("Yes") { dialog, _ -> // 1st _ dialog, 2nd _ result
                    PlayListActivity.musicPlaylist.ref.removeAt(position)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
        }
        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if (PlayListActivity.musicPlaylist.ref[position].playlist.size > 0) {
            //for image
            Glide.with(context)
                .load(PlayListActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.sm_logo_new).centerCrop())
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(PlayListActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }


}