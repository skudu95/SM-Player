package com.kudu.androidmusicplayer.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.databinding.ActivityPlayerBinding
import com.kudu.androidmusicplayer.model.*
import com.kudu.androidmusicplayer.util.MusicService

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0

        //        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.customTheme)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_player)
        setContentView(binding.root)

        initialiseLayout()

        //back button
        binding.btnBackPA.setOnClickListener {
            finish()
        }
        //play-pause button
        binding.btnPlayPausePA.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        //previous button
        binding.btnPreviousPA.setOnClickListener {
            prevNextSong(increment = false)
        }
        //next button
        binding.btnNextPA.setOnClickListener {
            prevNextSong(increment = true)
        }
        //test shuffle button
        binding.btnShufflePA.setOnClickListener {
            musicListPA = ArrayList()
            musicListPA.addAll(MainActivity.musicListMA)
            musicListPA.shuffle()
            setLayout()
            createMediaPlayer()
        }
        //seekbar
        binding.seekbarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        //repeat
        binding.btnRepeatPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.btnRepeatPA.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.navHeaderTextColor
                    )
                )
            } else {
                repeat = false
                binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this, R.color.white))
            }
        }
        //equaliser
        binding.btnEqualiserPA.setOnClickListener {
            try {
                val eQIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eQIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eQIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eQIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eQIntent, 20)
            } catch (e: Exception) {
                Toast.makeText(this, "Equaliser not supported!", Toast.LENGTH_SHORT).show()
            }
        }
        //timer
        binding.btnTimerPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
            } else {
                val builder = MaterialAlertDialogBuilder(this, R.style.customDialogTheme)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
//                        .setBackground(R.color.navBarColor)
                    .setPositiveButton("Yes") { _, _ -> // 1st _ dialog, 2nd _ result
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.btnTimerPA.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.white
                            )
                        )
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }
        //share button
        // TODO: change this to link sharing of the app
        binding.btnSharePA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*" //shares only audio file of any type
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File"))
        }
        //fav button
        binding.btnAddFavouritePA.setOnClickListener {
            fIndex = favouriteChecker(musicListPA[songPosition].id)
            if (isFavourite) {
                isFavourite = false
                binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_empty_icon)
                binding.btnAddFavouritePA.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )// line added since the color doesn't change
                FavouriteActivity.favSongs.removeAt(fIndex)
            } else {
                isFavourite = true
//                binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_red_icon)
                binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_red_icon)
                binding.btnAddFavouritePA.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.red
                    )
                )// line added since the color doesn't change
                FavouriteActivity.favSongs.add(musicListPA[songPosition])
            }
        }
    }

    private fun initialiseLayout() {
        songPosition = intent.getIntExtra("index", 0)
        //from which class intent is coming
        when (intent.getStringExtra("class")) {
            "FavouriteAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favSongs)
                setLayout()
            }
            "NowPlaying" -> {
                setLayout()
                binding.tvStartSeekbar.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvEndSeekbar.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.btnPlayPausePA.setIconResource(R.drawable.pause_icon)
                else binding.btnPlayPausePA.setIconResource(R.drawable.play_icon)
            }
            "MusicAdapterSearch" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
            }
            "MainActivity" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                musicListPA.shuffle()
                setLayout()
            }
            "FavouriteShuffle" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favSongs)
                musicListPA.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "PlaylistDetailsShuffle" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout() {
        // if in fav or not
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        //for image
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.sm_logo_new).centerCrop())
            .into(binding.imgSongPA)
        //text
        binding.tvSongNamePA.text = musicListPA[songPosition].title
        // binding.tvSongNamePA.text.isSelected = true //not working
        //repeat
        if (repeat) {
            binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
        //timer
        if (min15 || min30 || min60) {
            binding.btnTimerPA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.navHeaderTextColor
                )
            )
        }
        //fav
        if (isFavourite) {
//            binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_red_icon) // not working
            binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_red_icon)
            binding.btnAddFavouritePA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )// line added since the color doesn't change
        } else {
            binding.btnAddFavouritePA.setImageResource(R.drawable.favourite_empty_icon)
            binding.btnAddFavouritePA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            ) // not needed since it works fine
        }
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPlayPausePA.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            //seekbar
            binding.tvStartSeekbar.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvEndSeekbar.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress = 0
            binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        binding.btnPlayPausePA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnPlayPausePA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        //handing call
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    // song completion
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 20 || resultCode == RESULT_OK)
            return
    }

    //bottom sheet dialog
    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min15)?.setOnClickListener {
            Toast.makeText(
                baseContext,
                "Music will stop playing after 15 minutes",
                Toast.LENGTH_SHORT
            ).show()
            //color set
            binding.btnTimerPA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.navHeaderTextColor
                )
            )
            min15 = true
            Thread {
                Thread.sleep(15 * 60000) // (15 * 60000).toLong()
                if (min15) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener {
            Toast.makeText(
                baseContext,
                "Music will stop playing after 30 minutes",
                Toast.LENGTH_SHORT
            ).show()
            //color set
            binding.btnTimerPA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.navHeaderTextColor
                )
            )
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener {
            Toast.makeText(
                baseContext,
                "Music will stop playing after 60 minutes",
                Toast.LENGTH_SHORT
            ).show()
            //color set
            binding.btnTimerPA.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.navHeaderTextColor
                )
            )
            min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min60) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }

    //service start
    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

}