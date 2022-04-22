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
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.activities.PlayerActivity
import com.kudu.androidmusicplayer.databinding.FavouriteViewBinding
import com.kudu.androidmusicplayer.model.Music

class FavouriteAdapter(private var context: Context, private var musicList: ArrayList<Music>) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteHolder>() {

    class FavouriteHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        //initialising
        val image = binding.imgSongFV
        val name = binding.tvSongNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteHolder {
        return FavouriteHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    // while display
    override fun onBindViewHolder(holder: FavouriteHolder, position: Int) {
        holder.name.text = musicList[position].title
        holder.name.isSelected = true // TODO: remove if not necessary
        //for image
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener{
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavouriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateFavourites(newList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

}