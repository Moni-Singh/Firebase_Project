package com.example.firebase_project


import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.newproject.Adapter.ImagePagerAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class Dashboard : AppCompatActivity() {


    private lateinit var viewPager: ViewPager
    private lateinit var adapter: ImagePagerAdapter
    private val images = listOf(R.drawable.food, R.drawable.nonvg, R.drawable.img)
    private var currentPage = 0
    private var timer: Timer? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var card4: CardView
    private lateinit var card5: CardView
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    private lateinit var uid: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        drawerLayout = findViewById(R.id.drawer)
        navView = findViewById(R.id.navmenu)
        toolbar = findViewById(R.id.toolbar)
        // Rest of your code...
        setSupportActionBar(toolbar)


        auth = FirebaseAuth.getInstance()
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        card4 = findViewById(R.id.card4)
        card4.setOnClickListener{
            val i = Intent(this@Dashboard,TabLayout::class.java)
            startActivity(i)
        }

        card5 = findViewById(R.id.card5)
        card5.setOnClickListener{

            val i = Intent(this@Dashboard,itemkids::class.java)
            startActivity(i)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.home_menu -> {
                    val i = Intent(this,itemkids::class.java)
                    startActivity(i)
                    true

                }
                R.id.about_menu -> {

                    val i = Intent(this@Dashboard,Profile::class.java)
                    startActivity(i)
                    true
                }
                R.id.share -> {
                    val i = Intent (this@Dashboard,itemkids::class.java)
                    startActivity(i)
                    true
                }
                R.id.changepassword -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    changepassword()
                    true
                }
                R.id.logout_menu -> {


                    drawerLayout.closeDrawer(GravityCompat.START)
                    logout()

                    true
                }
                R.id.home_menu -> {

                    true
                }
                R.id.exit_menu -> {

                    drawerLayout.closeDrawer(GravityCompat.START)
                    exit()


                    true
                }
                // Add more menu items as needed
                else -> false
            }
        }


        viewPager = findViewById<ViewPager>(R.id.viewPager)
        adapter = ImagePagerAdapter(images, this)
        viewPager.adapter = adapter

        val handler = Handler()
        val update = Runnable {
            if (currentPage == images.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 2000) // Change image every 2 seconds
    }

    private fun changepassword() {
        val resetMail = EditText(this)
        val passwordChangeDialog = AlertDialog.Builder(this)
        passwordChangeDialog.setTitle("Change Password")
        passwordChangeDialog.setMessage("Enter your email to receive a change password link.")
        passwordChangeDialog.setView(resetMail)
        passwordChangeDialog.setPositiveButton("Change") { _, _ ->
            val mail = resetMail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@Dashboard,
                        "Change Password Link Sent To Your Email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@Dashboard,
                        "Error! Change Password Link Not Sent To Your Email: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        passwordChangeDialog.setNegativeButton("Cancel", null)
        passwordChangeDialog.show()
    }

    private fun logout() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, _ ->
                FirebaseAuth.getInstance().signOut()
                val sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("islogin", false)
                editor.apply()
                Toast.makeText(applicationContext, "Logout Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@Dashboard, LoginPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()

}


    private fun exit() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit?")
        builder.setMessage("Are you sure want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") {_,_  ->
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
                Toast.makeText(applicationContext, "Exit", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, LoginPage::class.java))
                finish()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }



    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}