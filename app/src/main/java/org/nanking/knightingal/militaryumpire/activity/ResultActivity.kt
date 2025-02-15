package org.nanking.knightingal.militaryumpire.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.nanking.knightingal.militaryumpire.databinding.ActivityPlayerBinding

class ResultActivity: AppCompatActivity() {

    private lateinit var viewBinding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.player1.setOnClickListener {
            val intent = Intent()
            intent.putExtra("ocr", "ocr result")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}