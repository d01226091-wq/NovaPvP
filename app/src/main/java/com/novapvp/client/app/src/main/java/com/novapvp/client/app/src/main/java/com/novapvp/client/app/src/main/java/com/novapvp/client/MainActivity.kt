package com.novapvp.client

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this)
        title.text = "NovaPvP"
        title.textSize = 32f
        title.setPadding(32, 64, 32, 32)

        setContentView(title)
    }
}
