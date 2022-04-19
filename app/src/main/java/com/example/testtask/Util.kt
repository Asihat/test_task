package com.example.testtask

import android.Manifest
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.provider.CallLog
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*


class Util {
    companion object {
        const val BASE_URL =
            "wss://demo.piesocket.com/v3/channel_1?api_key=oCdCMcMPQpbvNjUIzqtvF1d2X2okWpDQj4AwARJuAgtjhzKxVEjQU6IdCjwm&notify_self"

        fun readCallLog(activity: MainActivity, caller_id: String) {
            val numberCol = CallLog.Calls.NUMBER
            val durationCol = CallLog.Calls.DURATION
            val typeCol = CallLog.Calls.TYPE // 1 - Incoming, 2 - Outgoing, 3 - Missed
            val dateTimeCol = CallLog.Calls.DATE
            val lastModifiedCol = CallLog.Calls.LAST_MODIFIED

            val projection = arrayOf(numberCol, durationCol, typeCol, dateTimeCol, lastModifiedCol)

            val cursor = activity.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection, null, null, CallLog.Calls.DATE + " DESC"
            )

            val numberColIdx = cursor!!.getColumnIndex(numberCol)
            val durationColIdx = cursor.getColumnIndex(durationCol)
            //val typeColIdx = cursor.getColumnIndex(typeCol)
            val dateTimeColx = cursor.getColumnIndex(dateTimeCol)
            val lastModifiedColx = cursor.getColumnIndex(lastModifiedCol)

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberColIdx)
                val primaryNumber = getPrimaryNumber(activity)
                if (caller_id == number) {
                    val duration = cursor.getString(durationColIdx)
                    // val type = cursor.getString(typeColIdx)
                    val inDateTime = cursor.getString(dateTimeColx)
                    val lastModifiedDate = cursor.getString(lastModifiedColx)
                    activity.setData(
                        number,
                        convertLongToTime(inDateTime.toLong()),
                        primaryNumber,
                        getInDateTime(lastModifiedDate, duration),
                        convertLongToTime(lastModifiedDate.toLong())
                    )
                    return
                }
            }
            cursor.close()
        }

        private fun getPrimaryNumber(activity: MainActivity): String {
            val telephonyManager = activity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return "87001234578"
            }
            return telephonyManager.line1Number.toString()
        }

        private fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }

        private fun getInDateTime(time: String, duration: String): String {
            return convertLongToTime(time.toLong().minus(duration.toLong() * 1000))
        }
    }
}