package com.appNutrity.nutrity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.appNutrity.nutrity.dataPersistence.loggedIn.Companion.prefs
import com.appNutrity.nutrity.databinding.ActivityMainBinding

enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var username: TextView
    lateinit var caloriesGoal: TextView
    private lateinit var ivUser: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_imc, R.id.nav_calorias, R.id.nav_recetas, R.id.nav_configuracion
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        username = navView.getHeaderView(0).findViewById(R.id.username)
        ivUser = navView.getHeaderView(0).findViewById(R.id.userImage)

        username.text = prefs.getUsername()
        setCaloriesGoal()

        val uri = prefs.getUri()
        if(uri != ""){
            ivUser.setImageURI(uri.toUri())
        }

    }

    @SuppressLint("SetTextI18n")
    fun setCaloriesGoal(){
        val navView: NavigationView = binding.navView
        caloriesGoal = navView.getHeaderView(0).findViewById(R.id.caloriesGoal)
        when(prefs.getObjective()){
            "Lose weight" -> {
                caloriesGoal.text = "< "+prefs.getCalories().toString()+" kcal"
            }

            "Gain weight" -> {
                caloriesGoal.text = "> "+prefs.getCalories().toString()+" kcal"
            }

            "Maintain weight" -> {
                caloriesGoal.text = "= "+prefs.getCalories().toString()+" kcal"
            }
        }
    }

    fun setNewData(){
        username.text = prefs.getUsername()
        setCaloriesGoal()

        val uri = prefs.getUri()
        if(uri != ""){
            ivUser.setImageURI(uri.toUri())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun Notification(){

        val channelID = "chat"
        val chanelName = "chat"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, chanelName, importancia)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelID).also { noti ->
                noti.setContentTitle("Nutrity")
                noti.setContentText("You need to consume 600 calories to complete your day!!")
                noti.setSmallIcon(R.mipmap.ic_launcher)
            }.build()

            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(1, notification)
        }
    }


}