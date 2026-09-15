package com.novapvp.client

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
            setBackgroundColor(0xFF090B12.toInt())
        }

        fun title(text: String, size: Float): TextView =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(0xFFE8F8FF.toInt())
                setPadding(0, 12, 0, 12)
            }

        root.addView(title("NOVA PvP", 30f))
        root.addView(title("BEDROCK VISUAL CLIENT", 13f))
        root.addView(title("Оригинальный визуальный пак для Minecraft Bedrock", 16f))

        val install = Button(this).apply {
            text = "ОТКРЫТЬ РЕСУРСПАК"
            setOnClickListener { openPack() }
        }
        root.addView(install)

        val info = Button(this).apply {
            text = "О ВИЗУАЛЕ"
            setOnClickListener {
                title("NovaPvP\n\n• кастомный PvP HUD\n• неоновый прицел\n• визуальные частицы\n• тёмная тема", 15f)
                    .also { root.addView(it) }
            }
        }
        root.addView(info)

        setContentView(root)
    }

    private fun openPack() {
        val pack = File(cacheDir, "NovaPvP_Visual_Client_Bedrock.mcpack")
        if (!pack.exists()) {
            assets.open("NovaPvP_Visual_Client_Bedrock.mcpack").use { input ->
                pack.outputStream().use { output -> input.copyTo(output) }
            }
        }
        if (!pack.exists()) return
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            pack
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/octet-stream")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Открыть в Minecraft"))
    }
}
