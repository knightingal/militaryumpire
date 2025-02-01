package org.nanking.knightingal.militaryumpire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.nanking.knightingal.militaryumpire.databinding.ActivityPlayerBinding

class PlayerActivity: AppCompatActivity() {

    private var player1Chequer: Chequer? = null
    private var player2Chequer: Chequer? = null

    private var activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("PlayerActivity", "ocr result ${it.data!!.getStringExtra("ocr")}")
        Log.d("PlayerActivity", "ocr result ${it.data!!.getStringExtra("player")}")
        val player = it.data!!.getStringExtra("player")
        val ocr = it.data!!.getStringExtra("ocr")
        if (ocr != null) {
            if (player != null && player == "player1") {
                player1Chequer = Chequer.valueOf(ocr)
            } else if (player != null && player == "player2") {
                player2Chequer = Chequer.valueOf(ocr)
            }
        }

        if (player1Chequer != null && player2Chequer != null) {
            if (player1Chequer!!.weight == player2Chequer!!.weight) {
                Log.i("PlayerActivity", "all die")
            } else if (player1Chequer == Chequer.工兵 && player2Chequer == Chequer.地雷) {
                Log.i("PlayerActivity", "player2Chequer ${player2Chequer!!.name} die")
            } else if (player2Chequer == Chequer.工兵 && player1Chequer == Chequer.地雷) {
                Log.i("PlayerActivity", "player1Chequer ${player1Chequer!!.name} die")
            } else if (player1Chequer == Chequer.地雷
                    || player2Chequer == Chequer.地雷
                    || player2Chequer == Chequer.炸弹
                    || player2Chequer == Chequer.炸弹) {
                Log.i("PlayerActivity", "all die")
            } else if (player1Chequer!!.weight < player2Chequer!!.weight) {
                Log.i("PlayerActivity", "player2Chequer ${player2Chequer!!.name} die")
            } else {
                Log.i("PlayerActivity", "player1Chequer ${player1Chequer!!.name} die")
            }
        }
    }

    private lateinit var viewBinding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.player1.setOnClickListener {
            activityResult.launch(Intent(this, MainActivity::class.java).apply {
                putExtra("player", "player1")
            })
        }
        viewBinding.player2.setOnClickListener {
            activityResult.launch(Intent(this, MainActivity::class.java).apply {
                putExtra("player", "player2")
            })
        }
    }

}


