package org.nanking.knightingal.militaryumpire

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
            var winner = ""
            if (player1Chequer!!.weight == player2Chequer!!.weight) {
                Log.i("PlayerActivity", "all die")
            } else if (player1Chequer == Chequer.工兵 && player2Chequer == Chequer.地雷) {
                Log.i("PlayerActivity", "player2Chequer ${player2Chequer!!.name} die")
                winner = "player1"
            } else if (player2Chequer == Chequer.工兵 && player1Chequer == Chequer.地雷) {
                Log.i("PlayerActivity", "player1Chequer ${player1Chequer!!.name} die")
                winner = "player2"
            } else if (player1Chequer == Chequer.地雷
                    || player2Chequer == Chequer.地雷
                    || player1Chequer == Chequer.炸弹
                    || player2Chequer == Chequer.炸弹) {
                Log.i("PlayerActivity", "all die")
            } else if (player1Chequer!!.weight < player2Chequer!!.weight) {
                Log.i("PlayerActivity", "player2Chequer ${player2Chequer!!.name} die")
                winner = "player1"
            } else {
                Log.i("PlayerActivity", "player1Chequer ${player1Chequer!!.name} die")
                winner = "player2"
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setOnDismissListener {
                player1Chequer = null
                player2Chequer = null
                viewBinding.player1Check.visibility = View.GONE
                viewBinding.player2Check.visibility = View.GONE
                viewBinding.player1.visibility = View.VISIBLE
                viewBinding.player2.visibility = View.VISIBLE
            }
            if (winner != "") {
                builder.setMessage("Winner is ${winner}")
                    .setTitle("Player win").create().show()
            } else {
                builder.setMessage("All die")
                    .setTitle("All die").create().show()
            }
        } else if (player1Chequer != null) {
            viewBinding.player1Check.visibility = View.VISIBLE
            viewBinding.player1.visibility = View.GONE
        } else if (player2Chequer != null) {
            viewBinding.player2Check.visibility = View.VISIBLE
            viewBinding.player2.visibility = View.GONE
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


