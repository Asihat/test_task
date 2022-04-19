package com.example.testtask

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testtask.Util.Companion.BASE_URL
import com.example.testtask.Util.Companion.readCallLog
import com.example.testtask.WebSocketManager.TAG


class MainActivity : AppCompatActivity(), MessageListener {

    private var callerId = "0"
    private val permission: String = Manifest.permission.READ_CALL_LOG
    private val permissionToReadStatePhone: String = Manifest.permission.READ_PHONE_NUMBERS
    private val requestCode: Int = 2
    private lateinit var callerIdTextView: TextView
    private lateinit var inNumberTextView: TextView
    private lateinit var inDateTimeTextView: TextView
    private lateinit var pickUpDateTimeTextView: TextView
    private lateinit var outDateTimeTextView: TextView
    private val serverUrl =
        BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeFields()
        initSocket()
        permisionsForContacts(permission)
        permisionsForContacts(permissionToReadStatePhone)

        if (WebSocketManager.sendMessage(" Client send ")) {
            addText("TEXT \n ")
        }
    }

    private fun permisionsForContacts(permission: String) {
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun initSocket() {
        WebSocketManager.init(serverUrl, this)
        Log.d(TAG, "STARTED SOCKET")
        WebSocketManager.connect()
    }

    private fun initializeFields() {
        callerIdTextView = findViewById(R.id.callerIdTextView)
        inNumberTextView = findViewById(R.id.numberTextView)
        inDateTimeTextView = findViewById(R.id.inDatetimeTextView)
        pickUpDateTimeTextView = findViewById(R.id.pickupDateTimeTextView)
        outDateTimeTextView = findViewById(R.id.outDatetimeTextView)
    }

    fun setData(
        caller_id: String,
        date: String,
        name: String,
        inDateTime: String,
        outDateTime: String
    ) {
        runOnUiThread {
            callerIdTextView.text = getString(R.string.concat_twoString, callerIdTextView.text.toString(), caller_id)
            inNumberTextView.text = getString(R.string.concat_twoString, inNumberTextView.text.toString(), name)
            inDateTimeTextView.text = getString(R.string.concat_twoString, inDateTimeTextView.text.toString(), date)
            pickUpDateTimeTextView.text = getString(R.string.concat_twoString, pickUpDateTimeTextView.text.toString(), inDateTime)
            outDateTimeTextView.text = getString(R.string.concat_twoString, outDateTimeTextView.text.toString(), outDateTime)
        }
    }

    override fun onConnectSuccess() {
        addText(" Connected successfully \n ")
    }

    override fun onConnectFailed() {
        addText(" Connection failed \n ")
    }

    override fun onClose() {
        addText(" Closed successfully \n ")
    }

    override fun onMessage(text: String?) {
        callerId = "1000"
        readCallLog(this, callerId)
        WebSocketManager.close()
    }

    @SuppressLint("SetTextI18n")
    private fun addText(text: String?) {
        Log.d(TAG, text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketManager.close()
    }
}