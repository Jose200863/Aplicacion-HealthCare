package com.example.aplicacion_healthcare

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// Maneja el permiso de cámara y la captura de la foto.
// Se crea como propiedad de la Activity
class CamaraHelper(
    private val activity: ComponentActivity,
    private val alRecibirFoto: (Bitmap?) -> Unit
) {

    private val permisoCamara =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) {
                abrirCamara()
            } else {
                Toast.makeText(activity, R.string.foto_permiso_denegado, Toast.LENGTH_SHORT).show()
            }
        }

    private val tomarFoto =
        activity.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { foto ->
            alRecibirFoto(foto)
        }

    fun solicitarFoto() {
        val permiso = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            permisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        try {
            tomarFoto.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.foto_sin_camara, Toast.LENGTH_SHORT).show()
        }
    }
}