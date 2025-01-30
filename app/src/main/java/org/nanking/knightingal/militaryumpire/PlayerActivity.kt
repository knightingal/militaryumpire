package org.nanking.knightingal.militaryumpire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.nanking.knightingal.militaryumpire.databinding.ActivityPlayerBinding

class PlayerActivity: AppCompatActivity() {

    private var activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("PlayerActivity", "ocr result ${it.data!!.getStringExtra("ocr")}")
    }

    private lateinit var viewBinding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.player1.setOnClickListener {
            activityResult.launch(Intent(this, ResultActivity::class.java))
        }
    }

}


