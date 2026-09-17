package com.novapvp.client

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("novapvp", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainMenu()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun background(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.rgb(24, 24, 27))
            cornerRadius = dp(12).toFloat()
        }
    }

    private fun button(text: String): Button {
        return Button(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(Color.WHITE)
            background = background()
            isAllCaps = false
            minimumHeight = dp(52)
        }
    }

    private fun title(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(20))
        }
    }

    private fun baseLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(32), dp(24), dp(32), dp(24))
            setBackgroundColor(Color.rgb(15, 15, 18))
        }
    }

    private fun showMainMenu() {
        val root = baseLayout()

        root.addView(
            title("NovaPvP")
        )

        val subtitle = TextView(this).apply {
            text = "Bedrock PvP Client"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                -1,
                dp(40)
            )
        )

        val servers = button("🌐  Серверы")
        val worlds = button("🗺  Миры")
        val settings = button("⚙  Настройки")

        root.addView(servers, widthParams())
        root.addView(spacer(10))
        root.addView(worlds, widthParams())
        root.addView(spacer(10))
        root.addView(settings, widthParams())

        servers.setOnClickListener {
            showServers()
        }

        worlds.setOnClickListener {
            showWorlds()
        }

        settings.setOnClickListener {
            showSettings()
        }

        setContentView(root)
    }

    private fun showServers() {
        val root = baseLayout()

        root.addView(title("Серверы"))

        val ip = EditText(this).apply {
            hint = "IP сервера"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            singleLine = true
            setPadding(dp(16), 0, dp(16), 0)
            background = background()
        }

        val port = EditText(this).apply {
            hint = "Порт"
            setText("19132")
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            singleLine = true
            setPadding(dp(16), 0, dp(16), 0)
            background = background()
        }

        root.addView(ip, widthParams(dp(55)))
        root.addView(spacer(10))
        root.addView(port, widthParams(dp(55)))
        root.addView(spacer(15))

        val connect = button("🔌  Подключиться")
        val save = button("💾  Сохранить сервер")
        val back = button("←  Назад")

        root.addView(connect, widthParams())
        root.addView(spacer(8))
        root.addView(save, widthParams())
        root.addView(spacer(8))
        root.addView(back, widthParams())

        val status = TextView(this).apply {
            text = "Введите адрес сервера"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, dp(15), 0, 0)
        }

        root.addView(status, widthParams())

        connect.setOnClickListener {
            val host = ip.text.toString().trim()
            val portNumber = port.text.toString().toIntOrNull()

            if (host.isEmpty()) {
                status.text = "Введите IP сервера"
                return@setOnClickListener
            }

            if (portNumber == null || portNumber !in 1..65535) {
                status.text = "Неверный порт"
                return@setOnClickListener
            }

            status.text = "Подключение..."
            connect.isEnabled = false

            Thread {
                val result = ServerConnection.ping(host, portNumber)

                runOnUiThread {
                    connect.isEnabled = true

                    if (result.success) {
                        status.text = "✓ ${result.message}"
                        showGame()
                    } else {
                        status.text = "✕ ${result.message}"
                    }
                }
            }.start()
        }

        save.setOnClickListener {
            val host = ip.text.toString().trim()
            val portNumber = port.text.toString().toIntOrNull()

            if (host.isEmpty() || portNumber == null) {
                status.text = "Сначала укажи IP и порт"
                return@setOnClickListener
            }

            prefs.edit()
                .putString("server_ip", host)
                .putInt("server_port", portNumber)
                .apply()

            status.text = "✓ Сервер сохранён"
        }

        // Загружаем последний сервер
        prefs.getString("server_ip", null)?.let {
            ip.setText(it)
            port.setText(
                prefs.getInt("server_port", 19132).toString()
            )
        }

        back.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun showWorlds() {
        val root = baseLayout()

        root.addView(title("Миры"))

        val info = TextView(this).apply {
            text = "Локальные миры\n\nЭтот раздел готовится для загрузки\nи запуска миров."
            textSize = 17f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(
            info,
            LinearLayout.LayoutParams(
                -1,
                dp(150)
            )
        )

        val back = button("←  Назад")
        root.addView(back, widthParams())

        back.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun showSettings() {
        val root = baseLayout()

        root.addView(title("Настройки"))

        val version = TextView(this).apply {
            text = "NovaPvP\nВерсия 1.0"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(
            version,
            LinearLayout.LayoutParams(-1, dp(100))
        )

        val landscape = CheckBox(this).apply {
            text = "Альбомная ориентация"
            textSize = 16f
            setTextColor(Color.WHITE)
            isChecked = true
        }

        root.addView(landscape)

        val back = button("←  Назад")
        root.addView(back, widthParams())

        back.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun showGame() {
        val gameView = GameView(this)

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(35, 35, 35))
        root.addView(
            gameView,
            FrameLayout.LayoutParams(-1, -1)
        )

        val menu = button("☰")
        val params = FrameLayout.LayoutParams(
            dp(60),
            dp(55)
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.setMargins(dp(15), dp(15), 0, 0)

        root.addView(menu, params)

        menu.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun widthParams(height: Int = 55): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            dp(360),
            dp(height)
        )
    }

    private fun spacer(height: Int): View {
        return Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        }
    }
}
