package com.example.studentcheck

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

class MyHostApduService : HostApduService() {

    override fun onDeactivated(reason: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}