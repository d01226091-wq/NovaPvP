package com.novapvp.client

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import kotlinx.coroutines.*

class MainActivity : Activity() {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainMenu()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun button(text: String): Button {
        return Button(this).apply {
            this.text = text
            textSize = 17f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(55, 55, 60))
            isAllCaps = false
        }
    }

    private fun showMainMenu() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(40, 30, 40, 30)
        root.setBackgroundColor(Color.rgb(20, 20, 23))

        val title = TextView(this)
        title.text = "NovaPvP"
        title.textSize = 36f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(-1, 100)
        )

        val servers = button("🌐 Серверы")
        val worlds = button("🗺 Миры")
        val settings = button("⚙ Настройки")

        root.addView(
            servers,
            LinearLayout.LayoutParams(400, 65)
        )

        root.addView(space(15))

        root.addView(
            worlds,
            LinearLayout.LayoutParams(400, 65)
        )

        root.addView(space(15))

        root.addView(
            settings,
            LinearLayout.LayoutParams(400, 65)
        )

        servers.setOnClickListener {
            showServers()
        }

        worlds.setOnClickListener {
            showMessage("Миры пока находятся в разработке")
        }

        settings.setOnClickListener {
            showMessage("Настройки NovaPvP")
        }

        setContentView(root)
    }

    private fun showServers() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(40, 30, 40, 30)
        root.setBackgroundColor(Color.rgb(20, 20, 23))

        val title = TextView(this)
        title.text = "Сервер"
        title.textSize = 32f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(400, 80)
        )

        val ip = EditText(this)
        ip.hint = "IP сервера"
        ip.setTextColor(Color.WHITE)
        ip.setHintTextColor(Color.GRAY)
        ip.singleLine = true

        root.addView(
            ip,
            LinearLayout.LayoutParams(400, 60)
        )

        root.addView(space(10))

        val port = EditText(this)
        port.setText("19132")
        port.hint = "Порт"
        port.setTextColor(Color.WHITE)
        port.setHintTextColor(Color.GRAY)
        port.inputType = 2
        port.singleLine = true

        root.addView(
            port,
            LinearLayout.LayoutParams(400, 60)
        )

        root.addView(space(15))

        val connect =
            button("🔌 Подключиться")

        val back =
            button("← Назад")

        root.addView(
            connect,
            LinearLayout.LayoutParams(400, 65)
        )

        root.addView(space(10))

        root.addView(
            back,
            LinearLayout.LayoutParams(400, 65)
        )

        val status = TextView(this)
        status.text = "Введите IP сервера"
        status.textSize = 15f
        status.setTextColor(Color.LTGRAY)
        status.gravity = Gravity.CENTER

        root.addView(
            status,
            LinearLayout.LayoutParams(400, 100)
        )

        connect.setOnClickListener {

            val host =
                ip.text.toString().trim()

            val portNumber =
                port.text.toString().toIntOrNull()

            if (host.isEmpty()) {
                status.text =
                    "Введите IP сервера"
                return@setOnClickListener
            }

            if (
                portNumber == null ||
                portNumber !in 1..65535
            ) {
                status.text =
                    "Неверный порт"
                return@setOnClickListener
            }

            connect.isEnabled = false

            status.text =
                "Подключение..."

            scope.launch {

                val result =
                    ServerConnection.connect(
                        host,
                        portNumber
                    )

                connect.isEnabled = true

                status.text =
                    result.message

                if (result.success) {
                    delay(500)
                    showGame()
                }
            }
        }

        back.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun showGame() {

        val root = FrameLayout(this)

        val game =
            GameView(this)

        root.addView(
            game,
            FrameLayout.LayoutParams(-1, -1)
        )

        val menu =
            button("☰")

        val menuParams =
            FrameLayout.LayoutParams(
                70,
                65
            )

        menuParams.gravity =
            Gravity.TOP or Gravity.START

        menuParams.setMargins(
            20,
            20,
            0,
            0
        )

        root.addView(
            menu,
            menuParams
        )

        menu.setOnClickListener {
            showMainMenu()
        }

        setContentView(root)
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun space(size: Int): Space {
        return Space(this).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    1,
                    size
                )
        }
    }
}
