package com.kudu.androidmusicplayer.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kudu.androidmusicplayer.R
import com.kudu.androidmusicplayer.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AndroidMusicPlayer)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"

        //temporary work with send button
        binding.btnSendFA.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Work Under Process..!", Toast.LENGTH_LONG).show()
        }

       /* binding.btnSendFA.setOnClickListener {
            val feedbackMsg =
                binding.messageFA.text.toString() + "\n" + binding.emailFA.text.toString()
            val subject = binding.topicFA.text.toString()
//            val emailUser = binding.emailFA.text.toString()
//            val userName = "kudu.kudu.tolu@gmail.com"
            val userName = binding.emailFA.text.toString()
//            val pass = "Sweetloveaniandsk"
            val pass = ""
            val myEmail = "kudu.kudu.tolu@gmail.com"
            val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (feedbackMsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
               Thread{
                   try {
                       val properties = Properties()
                       properties["mail.smtp.auth"] = "true"
                       properties["mail.smtp.starttls.enable"] = "true"
                       properties["mail.smtp.host"] = "smtp.gmail.com"
                       properties["mail.smtp.port"] = "587"
                       val session = Session.getInstance(properties, object : Authenticator() {
                           override fun getPasswordAuthentication(): PasswordAuthentication {
                               return PasswordAuthentication(userName, pass)
                           }
                       })
                       val mail = MimeMessage(session)
                       mail.subject = subject
                       mail.setText(feedbackMsg)
//                       mail.setFrom(InternetAddress(userName))
                       mail.setFrom(InternetAddress(userName))
                       mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(myEmail))
                       Transport.send(mail)
                   } catch (e: Exception) {
                       Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                   }
               }.start()
                Toast.makeText(this, "Thanks for your Feedback..!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Something Went Wrong!\nPlease try again..", Toast.LENGTH_LONG)
                    .show()
            }
        }*/
    }
}