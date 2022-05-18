package com.example.nutrity.ui.user_profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nutrity.MainActivity
import com.example.nutrity.R
import com.example.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.example.nutrity.databinding.ActivityUserProfileConfigBinding
import com.example.nutrity.uielements.Loading
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class UserProfileConfig : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileConfigBinding
    private var uriImage: Uri? = null
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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityUserProfileConfigBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)

        with(binding) {

            imageViewSelected = imageUser

            imageUser.setOnClickListener {
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

            if (prefs.getUri()!="")
            {
                imageUser.setImageURI(prefs.getUri().toUri())
                userNameLayout.hint = prefs.getUsername()
                firstNameLayout.hint = prefs.getFirstName()
                lastNameLayout.hint = prefs.getLastName()
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
        lastname: String
    ) {
        if(!validateForm(username, firstname, lastname)) return

        GlobalScope.launch(Dispatchers.Main){

            loading.startDialog()
            val state = intent.extras!!.getBoolean("state")
            val email = intent.extras!!.getString("email")

            withContext(Dispatchers.IO){
                val request = Volley.newRequestQueue(applicationContext)

                var url: String = if(uriImage == null) {
                    "https://ivanurenda.000webhostapp.com/ConfigUser.php?email=${email}&firstName=${firstname}" +
                            "&lastName=${lastname}" + "&username=${username}&uriImage=${prefs.getUri()}"
                } else{
                    "https://ivanurenda.000webhostapp.com/ConfigUser.php?email=${email}&firstName=${firstname}" +
                            "&lastName=${lastname}" + "&username=${username}&uriImage=${uriImage.toString()}"
                }

                url=url.replace(" ", "%20")
                var stringRequest = StringRequest(Request.Method.POST, url, { response ->

                }, { error ->

                    Toast.makeText(applicationContext, ""+error.toString(), Toast.LENGTH_SHORT).show()
                })
                request.add(stringRequest)

                delay(1000)

                url = "https://ivanurenda.000webhostapp.com/UserData.php?email=${email}"
                url=url.replace(" ", "%20")
                stringRequest = StringRequest(Request.Method.GET, url, { response ->

                    val jsonArray = JSONArray(response)
                    val jsonObject = JSONObject(jsonArray.getString(0))
                    setValuesPrefs(jsonObject, state, username, firstname, lastname)
                }, { error ->

                })
                request.add(stringRequest)
            }

        }

    }

    private fun setValuesPrefs(
        jsonObject: JSONObject,
        state: Boolean,
        username: String,
        firstname: String,
        lastname: String
    ){
        prefs.saveCalories(jsonObject.get("calories").toString().toInt())
        prefs.saveProgress(jsonObject.get("progress").toString().toInt())
        prefs.saveProteins(jsonObject.get("proteins").toString().toInt())
        prefs.saveCarbs(jsonObject.get("carbs").toString().toInt())
        prefs.saveFats(jsonObject.get("fats").toString().toInt())
        prefs.saveWeight(jsonObject.get("weight").toString().toInt())
        prefs.saveHeight(jsonObject.get("height").toString().toInt())
        prefs.saveAge(jsonObject.get("age").toString().toInt())
        prefs.saveObjective(jsonObject.get("objective").toString())
        prefs.saveUsername(username)
        prefs.saveLogged(state)
        prefs.saveFirstName(firstname)
        prefs.saveLastName(lastname)
        if (uriImage!=null){
            if (prefs.getUri()==""){
                prefs.saveUri(uriImage.toString())
            }

        }
        loading.isDismiss()
        redirectToHome()
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