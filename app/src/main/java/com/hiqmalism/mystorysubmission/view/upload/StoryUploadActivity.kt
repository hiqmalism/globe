package com.hiqmalism.mystorysubmission.view.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hiqmalism.mystorysubmission.R
import com.hiqmalism.mystorysubmission.data.resource.ResultState
import com.hiqmalism.mystorysubmission.databinding.ActivityStoryUploadBinding
import com.hiqmalism.mystorysubmission.view.ViewModelFactory
import com.hiqmalism.mystorysubmission.view.main.MainActivity
import com.hiqmalism.mystorysubmission.view.utils.getImageUri
import com.hiqmalism.mystorysubmission.view.utils.reduceFileImage
import com.hiqmalism.mystorysubmission.view.utils.uriToFile
import com.yalantis.ucrop.UCrop
import java.io.File

class StoryUploadActivity : AppCompatActivity() {

    private val storyUploadViewModel by viewModels<StoryUploadViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryUploadBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(null)
        supportActionBar?.title = ""
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .start(this)
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edDescription.text.toString()
            storyUploadViewModel.uploadStory(imageFile, description).observe(this) { response ->
                if (response != null) {
                    when (response) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            response.data.message?.let { showToast(it) }
                            showLoading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        is ResultState.Error -> {
                            showToast(response.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let { startCrop(it) }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(uri)
        } else {
            Snackbar.make(binding.root, "No media selected", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let { showToast(it.message ?: "Crop error") }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
