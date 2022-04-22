package com.kudu.androidmusicplayer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.activities.PlayerActivity
import com.kudu.androidmusicplayer.model.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {
                prevNextSong(increment = false, context = context!!)
            }
            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
            ApplicationClass.NEXT -> {
                prevNextSong(increment = true, context = context!!)
            }
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.btnPlayPausePA.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.btnPlayPauseNP.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.btnPlayPausePA.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.btnPlayPauseNP.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        //for image
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
            .into(PlayerActivity.binding.imgSongPA)
        //text
        PlayerActivity.binding.tvSongNamePA.text =
            PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        //for Now Playing
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
            .into(NowPlaying.binding.imgSongNP)
        //title text
        NowPlaying.binding.tvSongNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if (PlayerActivity.isFavourite) {
            PlayerActivity.binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_red_icon)
            PlayerActivity.binding.btnAddFavouritePA.setColorFilter(ContextCompat.getColor(context, R.color.red))
        }else{
            PlayerActivity.binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_empty_icon)
            PlayerActivity.binding.btnAddFavouritePA.setColorFilter(ContextCompat.getColor(context, R.color.white))
        }
    }

}