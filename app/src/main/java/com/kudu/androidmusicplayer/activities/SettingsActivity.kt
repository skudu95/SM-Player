package com.kudu.androidmusicplayer.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kudu.androidmusicplayer.BuildConfig
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.databinding.ActivitySettingsBinding
import com.kudu.androidmusicplayer.model.exitApplication

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AndroidMusicPlayer)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        when(MainActivity.themeIndex){
            0 -> binding.navBarTheme.setBackgroundColor(Color.YELLOW)
            1 -> binding.nowPlayingTheme.setBackgroundColor(Color.YELLOW)
            2 -> binding.navHeaderTheme.setBackgroundColor(Color.YELLOW)
            3 -> binding.navTextTheme.setBackgroundColor(Color.YELLOW)
            4 -> binding.redTheme.setBackgroundColor(Color.YELLOW)
        }

        binding.navBarTheme.setOnClickListener {
            //saveTheme(0)
            Toast.makeText(this, "Work Under Process..!!", Toast.LENGTH_LONG).show()
        }
        binding.nowPlayingTheme.setOnClickListener{
            //saveTheme(1)
            Toast.makeText(this, "Work Under Process..!!", Toast.LENGTH_LONG).show()
        }
        binding.navHeaderTheme.setOnClickListener {
           // saveTheme(2)
            Toast.makeText(this, "Work Under Process..!!", Toast.LENGTH_LONG).show()
        }
        binding.navTextTheme.setOnClickListener {
           // saveTheme(3)
            Toast.makeText(this, "Work Under Process..!!", Toast.LENGTH_LONG).show()
        }
        binding.redTheme.setOnClickListener {
            //saveTheme(4)
            Toast.makeText(this, "Work Under Process..!!", Toast.LENGTH_LONG).show()
        }
        binding.versionName.text = setVersionDetails()
    }

    private fun saveTheme(index: Int) {
        if (MainActivity.themeIndex != index) {
            val editor = getSharedPreferences("THEME", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            // dialog for exit
            val builder = MaterialAlertDialogBuilder(this, R.style.customDialogTheme)
            builder.setTitle("Apply Theme")
                .setMessage("Do you want to apply theme?")
                .setPositiveButton("Yes") { _, _ -> // 1st _ dialog, 2nd _ result
                    exitApplication()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
        }
    }


    //version
    private fun setVersionDetails(): String{
//        return "Version Name: 1"
        return "Version Name: ${BuildConfig.VERSION_NAME}"
    }
}