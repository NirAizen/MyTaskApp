package com.coding.meet.tasks.utils
import androidx.appcompat.app.AppCompatActivity
import com.coding.meet.tasks.R
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.coding.meet.tasks.MainActivity
import android.content.Intent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}
