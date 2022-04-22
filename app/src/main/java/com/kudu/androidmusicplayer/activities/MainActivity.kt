package com.kudu.androidmusicplayer.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.adapter.MusicAdapter
import com.kudu.androidmusicplayer.databinding.ActivityMainBinding
import com.kudu.androidmusicplayer.model.Music
import com.kudu.androidmusicplayer.model.MusicPlaylist
import com.kudu.androidmusicplayer.model.exitApplication
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        //static objects, to be accessed from anywhere
        lateinit var musicListMA: ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var search: Boolean = false

        //theme
        var themeIndex: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestRuntimePermission()

        //theme changes
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)

        setTheme(R.style.Theme_AndroidMusicPlayer)

        // initializing binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requestRuntimePermission()) {
            initialiseLayout()
            //retrieving favourites data using shared preferences
            FavouriteActivity.favSongs = ArrayList()
            val editor = getSharedPreferences( "FAVOURITES", MODE_PRIVATE) //data cannot be accessed by other apps
            val jSonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
            if (jSonString != null) {
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jSonString, typeToken)
                FavouriteActivity.favSongs.addAll(data)
            }
            //retrieving music playlist data using shared preferences
            PlayListActivity.musicPlaylist = MusicPlaylist()
            val jSonStringPlaylist = editor.getString("MusicPlaylist", null)
            if (jSonStringPlaylist != null) {
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jSonStringPlaylist, MusicPlaylist::class.java)
                PlayListActivity.musicPlaylist = dataPlaylist
            }
        }
        // shuffle button
        binding.btnShuffle.setOnClickListener {
            //  Toast.makeText(this, "Shuffle Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        //fav button
        binding.btnFavourites.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

        // playlist button
        binding.btnPlayList.setOnClickListener {
            val intent = Intent(this, PlayListActivity::class.java)
            startActivity(intent)
        }
        //navBar
        binding.navBarView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> startActivity(Intent(this,FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this,SettingsActivity::class.java))
                R.id.navAbout -> startActivity(Intent(this,AboutActivity::class.java))
                R.id.navExit -> {
                    // dialog for exit
                    val builder = MaterialAlertDialogBuilder(this, R.style.customDialogTheme)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close the app?")
//                        .setBackground(R.color.navBarColor)
                        .setPositiveButton("Yes") { _, _ -> // 1st _ dialog, 2nd _ result
                            exitApplication()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    /*  // button color
                        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)*/

                }
            }
            true
        }
    }

    //requesting permission
    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                20
            )
            return false
        }
        return true
    }

    // defines what to do with or without permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 20) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initialiseLayout()

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 20
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("SetTextI18n")
    private fun initialiseLayout() {
        //search
        search = false
        //music adapter set up to recycler view
        binding.musicRV.setHasFixedSize(true) //uses less RAM for application

        //initialising music list
        musicListMA = getAllAudio()

        binding.musicRV.setItemViewCacheSize(5)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, musicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.tvAllSongs.text = "All Songs : " + musicAdapter.itemCount
    }

    //access audio files from storage
    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        //storing favourites data using shared preferences
        val editor = getSharedPreferences(
            "FAVOURITES",
            MODE_PRIVATE
        ).edit() //data cannot be accessed by other apps
        val jSonString = GsonBuilder().create().toJson(FavouriteActivity.favSongs)
        editor.putString("FavouriteSongs", jSonString)
        val jSonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jSonStringPlaylist)
        editor.apply()
    }

    // searchView
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
//                Toast.makeText(this@MainActivity, newText.toString(), Toast.LENGTH_SHORT).show()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in musicListMA)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    musicAdapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }


}