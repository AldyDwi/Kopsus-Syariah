package ardiansyah.dwinur.aldy.koperasi

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    lateinit var b : ActivitySplashBinding
    private val SPLASH_DISPLAY_LENGTH = 2000 // 2 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(b.root)

        Handler().postDelayed(Runnable {
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}