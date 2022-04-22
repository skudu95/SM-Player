package com.kudu.androidmusicplayer.model

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.activities.PlayerActivity
import com.kudu.androidmusicplayer.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE // wont show up at start
        binding.btnPlayPauseNP.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic()
            else playMusic()
        }
        binding.btnNextNP.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
                .into(binding.imgSongNP)
            //title text
            binding.tvSongNameNP.text =
                PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        //on click Now Playing fragment
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE // shows up only when music is playing
            binding.tvSongNameNP.isSelected = true
            //for image
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_logo).centerCrop())
                .into(binding.imgSongNP)
            //title text
            binding.tvSongNameNP.text =
                PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            //play pause btn
            if (PlayerActivity.isPlaying) binding.btnPlayPauseNP.setIconResource(R.drawable.pause_icon)
            else binding.btnPlayPauseNP.setIconResource(R.drawable.play_icon)
        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.btnPlayPauseNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
//        PlayerActivity.binding.btnNextPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.binding.btnPlayPausePA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.btnPlayPauseNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
//        PlayerActivity.binding.btnNextPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.binding.btnPlayPausePA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }

}