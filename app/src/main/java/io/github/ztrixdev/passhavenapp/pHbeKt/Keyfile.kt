package io.github.ztrixdev.passhavenapp.pHbeKt

import android.app.Activity
import android.content.Context

import android.widget.Toast
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class Keyfile {
    fun writeTheKey(encryptedKey: String, activity: Activity) {
        val fileOutputStream: FileOutputStream = activity.openFileOutput("keyfile", Context.MODE_PRIVATE)
        val outputWriter = OutputStreamWriter(fileOutputStream)
        outputWriter.write(encryptedKey)
        outputWriter.close()
    }

}
