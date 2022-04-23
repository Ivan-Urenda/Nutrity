package com.example.nutrity.ui.user_profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.databinding.ActivityUserProfileConfigBinding
import com.example.nutrity.uielements.Loading
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class UserProfileConfig : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileConfigBinding
    private lateinit var uriImage: Uri
    private lateinit var fireDb: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var imageViewSelected: ImageView

    // Variable that tracks if a the user profile image was replaced
    private var photoHasChanged = false

    // Loading view
    private val loading = Loading(this)

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            uriImage = result.data?.data!!
            imageViewSelected.setImageURI(uriImage)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        pickImage.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileConfigBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)

        // Access a Cloud Firestore instance from your Activity
        fireDb = Firebase.firestore

        // Access to the Cloud Storage instance from the Activity
        storage = Firebase.storage

        with(binding) {

            imageViewSelected = imageView3

            imageView3.setOnClickListener {
                requestPermissions()
                photoHasChanged = true
            }

            changePictureFabBtn.setOnClickListener {
                requestPermissions()
                photoHasChanged = true
            }

            doneBtn.setOnClickListener {
                val username = userNameTextInput.text.toString().trim()
                val firstname = firstNameTextInput.text.toString().trim()
                val lastname = lastNameTextInput.text.toString().trim()

                uploadUserConfig(
                    username,
                    firstname,
                    lastname
                )
            }

            userNameTextInput.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    cleanErrors(userNameTextInput, userNameLayout)
                }
            }

            firstNameTextInput.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    cleanErrors(firstNameTextInput, firstNameLayout)
                }
            }

            lastNameTextInput.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    cleanErrors(lastNameTextInput, lastNameLayout)
                }
            }
        }
    }

    private fun redirectToHome(){
        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        this.finish()
    }

    private fun uploadUserConfig(
        username: String,
        firstname: String,
        lastname: String,
    ) {
        if(!validateForm(username, firstname, lastname)) return

        // create a new user with a username, firstname and lastname
        val user = mutableMapOf<String, Any>(
            "username" to username,
            "firstname" to firstname,
            "lastname" to lastname,
            "userProfileEdited" to true
        )

        val userAuth = Firebase.auth.currentUser

        loading.startDialog()

        val docRef = fireDb.collection("users").document("${userAuth?.email}")

        docRef.update(user)
            .addOnCompleteListener {
                // If user has set a different image,
                // then upload image to firebase
                if(photoHasChanged) uploadImage()

                Log.d(TAG, "DocumentSnapshot added with ID: $taskId")
                loading.isDismiss()
                redirectToHome()
            }
            .addOnFailureListener { e ->
                loading.isDismiss()
                Log.w(TAG, "Error adding document", e)
                Snackbar.make(
                    binding.root,
                    "Something went wrong. Try again.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadImage() {

        val user = Firebase.auth.currentUser

        user?.let {
            val email = it.email

            val storageRef = storage.reference

            // Create a child reference
            // imagesRef now points to "images/userEmail"
            val imagesRef: StorageReference = storageRef.child("images/${email}/profile_pic.png")
            try {
                imagesRef.putFile(uriImage)
                    .addOnCompleteListener { taskSnapshot ->
                        Log.d(TAGImg, "Image uploaded successfully. ${taskSnapshot.result}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAGImg, "Something went wrong uploading the image.", e)
                        throw e
                    }
            } catch (exception: Exception) {
                throw exception
            }
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            photoHasChanged = false
            Toast.makeText(
                this,
                "Access to storage permissions need to be enabled.",
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickImageFromGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun validateForm(
        username: String,
        firstname: String,
        lastname: String
    ): Boolean {
        var valid = true

        if(TextUtils.isEmpty(username)) {
            binding.userNameTextInput.error = getString(R.string.signForm_helperText)
            binding.userNameLayout.helperText = getString(R.string.signForm_helperText)
            binding.userNameLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.userNameTextInput.error = null
            binding.userNameLayout.helperText = getString(R.string.blank)
            binding.userNameLayout.isHelperTextEnabled = false
        }

        if(TextUtils.isEmpty(firstname)) {
            binding.firstNameTextInput.error = getString(R.string.signForm_helperText)
            binding.firstNameLayout.helperText = getString(R.string.signForm_helperText)
            binding.firstNameLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.firstNameTextInput.error = null
            binding.firstNameLayout.helperText = getString(R.string.blank)
            binding.firstNameLayout.isHelperTextEnabled = false
        }

        if(TextUtils.isEmpty(lastname)) {
            binding.lastNameTextInput.error = getString(R.string.signForm_helperText)
            binding.lastNameLayout.helperText = getString(R.string.signForm_helperText)
            binding.lastNameLayout.isHelperTextEnabled = true
            valid = false
        } else {
            binding.lastNameTextInput.error = null
            binding.lastNameLayout.helperText = getString(R.string.blank)
            binding.lastNameLayout.isHelperTextEnabled = false
        }

        return valid
    }

    /*
    * Clean the helper texts in the form fields
    * */
    private fun cleanErrors(inputText: TextInputEditText, inputLayout: TextInputLayout) {
        inputText.error = null
        inputLayout.isHelperTextEnabled = false
        inputLayout.helperText = getString(R.string.blank)
    }

    companion object {
        private const val TAG = "DocSnapshot"
        private const val TAGImg = "ImgSnapshot"
    }
}