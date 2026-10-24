package com.example.usbmirror

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val captureRequest = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = TextView(this).apply {
            textSize = 15f
            setPadding(16, 16, 16, 16)
            text = "USB Mirror Prototype\n\nThis build checks USB visibility and screen-capture permission. It does not stream video yet."
        }
        val usbButton = Button(this).apply {
            text = "Check USB capabilities"
            setOnClickListener { checkUsb() }
        }
        val captureButton = Button(this).apply {
            text = "Test screen-capture permission"
            setOnClickListener { requestCapture() }
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            addView(usbButton, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(captureButton, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(status, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        setContentView(ScrollView(this).apply { addView(layout) })
    }

    private fun checkUsb() {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val out = StringBuilder()
        out.appendLine("USB MIRROR DIAGNOSTICS")
        out.appendLine("Host supported: ${packageManager.hasSystemFeature("android.hardware.usb.host")}")
        out.appendLine("Accessory supported: ${packageManager.hasSystemFeature("android.hardware.usb.accessory")}")
        out.appendLine("Detected USB devices: ${manager.deviceList.size}")
        if (manager.deviceList.isEmpty()) {
            out.appendLine("No USB peripherals exposed to this app. This does not prove the cable is bad; Android phones often do not expose a generic USB interface to each other.")
        }
        for (device in manager.deviceList.values) {
            out.appendLine("\nDEVICE: ${device.deviceName}")
            out.appendLine("Vendor ID: ${device.vendorId}; Product ID: ${device.productId}")
            out.appendLine("Class: ${device.deviceClass}; interfaces: ${device.interfaceCount}")
            out.appendLine("App permission: ${manager.hasPermission(device)}")
            for (i in 0 until device.interfaceCount) {
                val intf = device.getInterface(i)
                out.appendLine(" Interface $i: class=${intf.interfaceClass}, subclass=${intf.interfaceSubclass}, protocol=${intf.interfaceProtocol}")
                for (j in 0 until intf.endpointCount) {
                    val ep = intf.getEndpoint(j)
                    val dir = if (ep.direction == android.hardware.usb.UsbConstants.USB_DIR_IN) "IN" else "OUT"
                    out.appendLine("  Endpoint $j: $dir type=${ep.type} maxPacket=${ep.maxPacketSize}")
                }
            }
        }
        out.appendLine("\nNext: run this on both phones with them connected directly.")
        status.text = out.toString()
    }

    private fun requestCapture() {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        @Suppress("DEPRECATION")
        startActivityForResult(manager.createScreenCaptureIntent(), captureRequest)
    }

    @Deprecated("Diagnostic prototype")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == captureRequest) {
            status.text = if (resultCode == RESULT_OK && data != null) {
                "Screen-capture permission granted. This prototype has not started a capture service or video stream yet."
            } else {
                "Screen-capture permission denied."
            }
        }
    }
}
