package com.example.livelocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val channelID="01"
    private val notificationID= 10

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder=Geocoder(this,Locale.getDefault())

        val btnLocation = findViewById<Button>(R.id.button_Location)

        notificationChannel()

        btnLocation.setOnClickListener{
            findLocation()
            Handler().postDelayed({
                btnLocation.performClick()

            }, 5000)

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun findLocation() {
        notificationChannel()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != (PackageManager.PERMISSION_GRANTED) &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        val location = fusedLocationProviderClient.lastLocation

        location.addOnSuccessListener {
            val submitField = findViewById<TextView>(R.id.address_text_view)
            val address=geocoder.getFromLocation(it.latitude,it.longitude,1)
            val changeAddress: String=submitField.text.toString()

            if(address!=null) {
                submitField.text= address[0].getAddressLine(0)
                submitField.setTextSize(1,16.5f)
                if(submitField.text.toString()!=changeAddress){
                    Toast.makeText(this,address[0].getAddressLine(0),Toast.LENGTH_LONG).show()
                    displayNotify(changeAddress)
            }
            }
        }
    }

    private fun notificationChannel() {
        val name = "Live Location"
        val descriptionText= "Address value"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID,name,importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun displayNotify(text: String){
        val builder=NotificationCompat.Builder(this,channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Live Location")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setShowWhen(true)

        with(NotificationManagerCompat.from(this)){
            notify(notificationID,builder.build())
        }
    }
    }
