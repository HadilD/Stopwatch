package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.util.*
import android.content.ContentResolver
import android.net.Uri
import android.media.AudioAttributes


const val CHANNEL_ID = "org.hyperskill"
const val NOTIFICATION_ID = 393939

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.startButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val settingsButton: Button = findViewById(R.id.settingsButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val time: TextView = findViewById(R.id.textView)
        val handler = Handler(Looper.getMainLooper())
        val rnd = Random()
        var upperTimeLimit = 60
        val sound: Uri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + applicationContext.getPackageName() + "/" + R.raw.star_trek_theme)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer's up!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.enableLights(true)
            channel.enableVibration(true)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(sound, attributes);

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Timer")
            .setContentText("Timer is up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(0xff0000ff.toInt(), 300, 1000) // blue color
            .setSound(sound)


        val updateTime: Runnable = object : Runnable {
            override fun run() {
                val timeElapsedString = time.text.toString()
                time.setText(addASecond(timeElapsedString))
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(rnd.nextInt()))
                if (timeElapsedString.substring(3).toInt() == upperTimeLimit) {
                    time.setTextColor(Color.RED)
                    val notificationManager = getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                }
                handler.postDelayed(this, 1000)

            }

        }
        var isRunning = false

        progressBar.visibility = View.INVISIBLE

        startButton.setOnClickListener {
            if (!isRunning) {
                handler.postDelayed(updateTime, 1000)
                isRunning = true
                progressBar.visibility = View.VISIBLE
                settingsButton.isEnabled = false
            }

        }

        resetButton.setOnClickListener {
            time.setText("00:00")
            handler.removeCallbacks(updateTime)
            isRunning = false
            progressBar.visibility = View.INVISIBLE
            settingsButton.isEnabled = true
            time.setTextColor(Color.BLACK)
        }

        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_settings, null, false)
            AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton(android.R.string.ok) {
                    _, _ -> val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText).text
                    upperTimeLimit = editText.toString().toInt()
                    Toast.makeText(this, editText, Toast.LENGTH_SHORT).show()
                }.setNegativeButton(android.R.string.cancel) { _, _ ->

                }
                .show()
        }

    }


    fun addASecond(time: String): String {
        val timeCharArray = time.toCharArray()
        return if (timeCharArray[3] == '5' && timeCharArray[4] == '9') {
            var newMinutes: Int = time.substring(0, 2).toInt()
            newMinutes++
            if ((""+newMinutes).length != 2) {
                "0$newMinutes:00"
            } else{
                "$newMinutes:00"
            }
        } else {
            var newSeconds: Int = time.substring(3).toInt()
            newSeconds++
            if ((""+newSeconds).length != 2) {
                "${time.substring(0, 2)}:0$newSeconds"
            } else {
                "${time.substring(0, 2)}:$newSeconds"
            }
        }
    }

}

