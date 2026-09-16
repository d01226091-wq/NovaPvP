package com.novapvp.client

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showMainMenu()
    }

    private fun showMainMenu() {

        setContentView(R.layout.activity_main)

        val serversButton =
            findViewById<Button>(R.id.serversButton)

        val worldsButton =
            findViewById<Button>(R.id.worldsButton)

        val settingsButton =
            findViewById<Button>(R.id.settingsButton)

        serversButton.setOnClickListener {
            showServerScreen()
        }

        worldsButton.setOnClickListener {
            Toast.makeText(
                this,
                "Раздел миров пока в разработке",
                Toast.LENGTH_SHORT
            ).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(
                this,
                "Настройки NovaPvP",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showServerScreen() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(30, 20, 30, 20)
        root.setBackgroundColor(Color.rgb(32, 32, 32))

        val title = TextView(this)

        title.text = "СЕРВЕРЫ"
        title.textSize = 32f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        root.addView(title)

        val ip = EditText(this)

        ip.hint = "IP сервера"
        ip.setTextColor(Color.WHITE)
        ip.setHintTextColor(Color.LTGRAY)
        ip.singleLine = true

        val ipParams = LinearLayout.LayoutParams(
            400,
            60
        )

        ipParams.topMargin = 25

        root.addView(ip, ipParams)

        val port = EditText(this)

        port.hint = "Порт"
        port.setText("19132")
        port.setTextColor(Color.WHITE)
        port.setHintTextColor(Color.LTGRAY)
        port.inputType = 2
        port.singleLine = true

        val portParams = LinearLayout.LayoutParams(
            400,
            60
        )

        portParams.topMargin = 10

        root.addView(port, portParams)

        val connect = Button(this)

        connect.text = "ПОДКЛЮЧИТЬСЯ"

        val connectParams = LinearLayout.LayoutParams(
            400,
            60
        )

        connectParams.topMargin = 15

        root.addView(connect, connectParams)

        val status = TextView(this)

        status.text = "Введите IP и порт"
        status.textSize = 16f
        status.setTextColor(Color.WHITE)
        status.gravity = Gravity.CENTER

        val statusParams = LinearLayout.LayoutParams(
            400,
            80
        )

        root.addView(status, statusParams)

        val back = Button(this)

        back.text = "НАЗАД"

        root.addView(back)

        setContentView(root)

        back.setOnClickListener {
            showMainMenu()
        }

        connect.setOnClickListener {

            val host = ip.text.toString().trim()

            val portNumber =
                port.text.toString().toIntOrNull()

            if (host.isEmpty()) {

                status.text = "Введите IP сервера"
                return@setOnClickListener
            }

            if (
                portNumber == null ||
                portNumber < 1 ||
                portNumber > 65535
            ) {

                status.text = "Неверный порт"
                return@setOnClickListener
            }

            status.text = "Проверяем сервер..."

            connect.isEnabled = false

            Thread {

                val result =
                    checkServer(host, portNumber)

                runOnUiThread {

                    status.text = result
                    connect.isEnabled = true
                }

            }.start()
        }
    }

    private fun checkServer(
        host: String,
        port: Int
    ): String {

        return try {

            val address =
                InetAddress.getByName(host)

            DatagramSocket().use { socket ->

                socket.soTimeout = 3000

                val data = ByteArray(25)

                data[0] = 0x01

                val packet =
                    DatagramPacket(
                        data,
                        data.size,
                        address,
                        port
                    )

                socket.send(packet)

                val responseData =
                    ByteArray(2048)

                val response =
                    DatagramPacket(
                        responseData,
                        responseData.size
                    )

                socket.receive(response)

                "Сервер отвечает!"

            }

        } catch (e: Exception) {

            "Сервер не ответил"
        }
    }
}
