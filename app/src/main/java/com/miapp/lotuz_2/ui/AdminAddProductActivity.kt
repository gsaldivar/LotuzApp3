package com.miapp.lotuz_2.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.miapp.lotuz_2.databinding.ActivityAdminAddProductBinding
import com.miapp.lotuz_2.network.RetrofitClient
import com.miapp.lotuz_2.model.ImageObj
import com.miapp.lotuz_2.model.Product
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AdminAddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddProductBinding
    private var selectedImageUri: Uri? = null
    private var selectedImageUris: List<Uri> = emptyList()
    private val PERMISSION_REQUEST_CODE = 1001

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (!uris.isNullOrEmpty()) {
            selectedImageUris = uris
            selectedImageUri = uris.firstOrNull()
            if (selectedImageUri != null) binding.ivPreview.setImageURI(selectedImageUri)
            uris.forEach { logImageContext(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnSelectImage.setOnClickListener {
            if (ensureImagePermissions()) {
                pickImagesLauncher.launch("image/*")
            }
        }
        binding.btnUpload.setOnClickListener { uploadProduct() }
    }

    private fun uploadProduct() {
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString()
        val stock = binding.etStock.text.toString()

        if (name.isEmpty() || price.isEmpty() || selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Faltan datos o imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val mime = contentResolver.getType(selectedImageUris.first())
        val allowed = setOf("image/jpeg", "image/png")
        if (mime == null || !allowed.contains(mime)) {
            Toast.makeText(this, "Formato no soportado. Usa JPG o PNG", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnUpload.isEnabled = false
        binding.btnUpload.text = "Subiendo..."

        lifecycleScope.launch {
            try {
                val startMs = System.currentTimeMillis()
                val files = selectedImageUris.map { uriToFile(it) }
                val maxBytes = 5 * 1024 * 1024
                if (files.any { it.length() > maxBytes }) {
                    Toast.makeText(applicationContext, "Imagen supera 5MB", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestFile = files.first().asRequestBody(mime.toMediaTypeOrNull())
                val bodyImage = MultipartBody.Part.createFormData("image", files.first().name, requestFile)

                val namePart = name.toRequestBody(MultipartBody.FORM)
                val descPart = "Descripción Gamer".toRequestBody(MultipartBody.FORM)
                val pricePart = price.toRequestBody(MultipartBody.FORM)
                val stockPart = stock.toRequestBody(MultipartBody.FORM)

                val api = RetrofitClient.getInstance(this@AdminAddProductActivity)
                var response = api.createProduct(namePart, descPart, pricePart, stockPart, bodyImage)

                if (response.code() == 404) {
                    response = api.createProductAlt(namePart, descPart, pricePart, stockPart, bodyImage)
                }

                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Producto creado!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val code = response.code()
                    val err = response.errorBody()?.string()?.take(200) ?: ""
                    val url = response.raw().request.url.toString()
                    Log.e("UploadProduct", "code=${code} url=${url} err=${err} device=${Build.MODEL} sdk=${Build.VERSION.SDK_INT} mime=${mime} size=${files.first().length()}B duration=${System.currentTimeMillis()-startMs}ms")
                    val saved = saveLocalProductMulti(name, "Descripción Gamer", price, stock, files)
                    if (saved) {
                        Toast.makeText(applicationContext, "Sin conexión. Guardado local", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Error ${code} en ${url}: ${err}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UploadProduct", "stack", e)
                val files = if (selectedImageUris.isNotEmpty()) selectedImageUris.map { uriToFile(it) } else emptyList()
                val saved = if (files.isNotEmpty()) saveLocalProductMulti(name, "Descripción Gamer", price, stock, files) else false
                if (saved) {
                    Toast.makeText(applicationContext, "Sin conexión. Guardado local", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                binding.btnUpload.isEnabled = true
                binding.btnUpload.text = "GUARDAR PRODUCTO"
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }

    private fun saveLocalProduct(name: String, desc: String, price: String, stock: String, imageFile: File): Boolean {
        return try {
            val p = Product(
                id = (System.currentTimeMillis() / 1000).toInt(),
                name = name,
                description = desc,
                price = price.toIntOrNull() ?: 0,
                stock = stock.toIntOrNull() ?: 0,
                image = ImageObj(url = imageFile.toURI().toString())
            )
            val prefs = getSharedPreferences("LotuzLocal", MODE_PRIVATE)
            val gson = Gson()
            val existing = prefs.getString("local_products", null)
            val list = if (existing.isNullOrBlank()) mutableListOf<Product>() else gson.fromJson(existing, Array<Product>::class.java).toMutableList()
            list.add(p)
            prefs.edit().putString("local_products", gson.toJson(list)).apply()
            true
        } catch (ex: Exception) {
            Log.e("UploadProduct", "saveLocalProduct error", ex)
            false
        }
    }

    private fun saveLocalProductMulti(name: String, desc: String, price: String, stock: String, imageFiles: List<File>): Boolean {
        return try {
            val imgs = imageFiles.map { ImageObj(url = it.toURI().toString()) }
            val p = Product(
                id = (System.currentTimeMillis() / 1000).toInt(),
                name = name,
                description = desc,
                price = price.toIntOrNull() ?: 0,
                stock = stock.toIntOrNull() ?: 0,
                image = imgs.firstOrNull(),
                images = imgs
            )
            val prefs = getSharedPreferences("LotuzLocal", MODE_PRIVATE)
            val gson = Gson()
            val existing = prefs.getString("local_products", null)
            val list = if (existing.isNullOrBlank()) mutableListOf<Product>() else gson.fromJson(existing, Array<Product>::class.java).toMutableList()
            list.add(p)
            prefs.edit().putString("local_products", gson.toJson(list)).apply()
            true
        } catch (ex: Exception) {
            Log.e("UploadProduct", "saveLocalProductMulti error", ex)
            false
        }
    }

    private fun ensureImagePermissions(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE
        val granted = ContextCompat.checkSelfPermission(this, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val ok = grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!ok) {
                Toast.makeText(this, "Sin permisos para leer imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logImageContext(uri: Uri) {
        try {
            val mime = contentResolver.getType(uri)
            val fd = contentResolver.openFileDescriptor(uri, "r")
            val size = fd?.statSize ?: -1
            fd?.close()
            Log.i("UploadProduct", "picked mime=${mime} size=${size} device=${Build.MODEL} sdk=${Build.VERSION.SDK_INT}")
        } catch (e: IOException) {
            Log.e("UploadProduct", "logImageContext error", e)
        }
    }
}
