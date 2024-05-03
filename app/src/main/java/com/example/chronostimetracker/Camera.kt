package com.example.chronostimetracker
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import android.util.Base64
import java.io.FileNotFoundException


class Camera(private val activity: Activity) {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var imageView: ImageView

    fun openCamera(imageView: ImageView) {
        this.imageView = imageView
        if (checkCameraPermission()) {
            dispatchTakePictureIntent()
        } else {
            requestCameraPermission()
        }
    }

    fun showImagePickerOptions(imageView: ImageView) {
        this.imageView = imageView
        val items = arrayOf("Take Photo", "Choose from Library")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Photo")
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    if (checkCameraPermission()) {
                        dispatchTakePictureIntent()
                    } else {
                        requestCameraPermission()
                    }
                }
                items[item] == "Choose from Library" -> openImagePicker()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, REQUEST_IMAGE_PICK) // Use REQUEST_IMAGE_PICK here
    }



    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                // Permission denied
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    try {
                        val inputStream = activity.contentResolver.openInputStream(selectedImageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageView.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}
