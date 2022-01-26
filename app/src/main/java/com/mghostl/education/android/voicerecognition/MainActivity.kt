package com.mghostl.education.android.voicerecognition
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name = "Lev"
        val surname = "Zilberman"
        val age = 26
        val height = 192.0

        val summary = "name: $name surname: $surname age: $age height: $height"
        val output: TextView = findViewById(R.id.output)
        output.text = summary

        Log.d(TAG, summary)

    }
}