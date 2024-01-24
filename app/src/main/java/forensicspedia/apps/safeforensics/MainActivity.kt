package forensicspedia.apps.safeforensics

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
            val context = LocalContext.current
            ShowConnectedUsbDevices(context = context)
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                // Update the toast message based on whether the device is rooted
                toastMessage = if (isDeviceRooted()) {
                    "Phone is Rooted"
                } else {
                    "Phone is Not Rooted"
                }
            }) {
                Text("Check Root & Disable USB Write if Rooted")
            }

            // Observe toastMessage for changes and show toast when it changes
            toastMessage?.let {
                LaunchedEffect(it) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    // Reset the message to null after showing the toast to avoid repeated toasts
                    // if the composition happens without a change in the button's onClick result.
                    toastMessage = null
                }
            }
        }
    }
}


private fun isDeviceRooted(): Boolean {
    val paths = arrayOf(
        "/system/bin/",
        "/system/xbin/",
        "/sbin/",
        "/system/sbin/",
        "/vendor/bin/",
        "/su/bin/"
    )

    paths.forEach { path ->
        try {
            val file = File(path + "su")
            if (file.exists()) return true // 'su' binary found, device is likely rooted
        } catch (e: Exception) {
            // Handle potential security exceptions or file access issues
            e.printStackTrace()
        }
    }

    return false
}


private fun disableUsbWrite() {
    try {
        val command = "mount -o remount,ro /path/to/usb" // Replace with actual mount point
        Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        // Handle successful execution
    } catch (e: Exception) {
        // Handle exceptions
    }
}

@Composable
fun ShowConnectedUsbDevices(context: Context) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val deviceList = usbManager.deviceList
    val connectedDevices = deviceList.values.joinToString(separator = "\n") { device ->
        "Device: ${device.deviceName}, Vendor ID: ${device.vendorId}, Product ID: ${device.productId}"
    }

    // You can display the connectedDevices string in your UI
    Text(text = "Connected USB Devices:\n$connectedDevices")
}