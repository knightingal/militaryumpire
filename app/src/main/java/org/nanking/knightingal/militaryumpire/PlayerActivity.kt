package org.nanking.knightingal.militaryumpire

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import org.nanking.knightingal.militaryumpire.databinding.ActivityPlayerBinding

class PlayerActivity: AppCompatActivity() {

    private var activityResult = registerForActivityResult(object : ActivityResultContract<Void?, String?>() {

        override fun createIntent(context: Context, input: Void?) =
            Intent(context, ResultActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) : String? {
            if (resultCode != Activity.RESULT_OK) {
                return null
            }
            return intent?.getStringExtra("ocr")
        }
    }) { it ->
        Log.d("PlayerActivity", "ocr result $it")
    }

    private lateinit var viewBinding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.player1.setOnClickListener {
            activityResult.launch(null)
        }
    }

}


