package org.odk.collect.audiorecorder.recording.internal

import android.app.Activity
import android.os.Bundle

internal class ReturnToAppActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
