package com.novapvp.client

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this).apply {
            text = "NovaPvP"
            textSize = 32f
            setPadding(32, 64, 32, 32)
        }

        setContentView(title)
    }
}
