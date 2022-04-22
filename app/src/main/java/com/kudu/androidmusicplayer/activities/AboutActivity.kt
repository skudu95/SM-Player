package com.kudu.androidmusicplayer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AndroidMusicPlayer)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"

        binding.tvAbout.text = aboutText()

    }

    private fun aboutText(): String{
        return "Developed By: Sharjeel Karim" +
                "\n\nUI Designed By: Sonia Tabassum" +
                "\n\nIf you want to provide feedback, please send an email to: kudu.kudu.tolu@gmail.com"
    }
}