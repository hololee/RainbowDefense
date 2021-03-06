package com.test.rainbowDefense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.rainbowDefense.adapter.OnItemClickListener
import com.test.rainbowDefense.adapter.ReinforceAdapter
import com.test.rainbowDefense.database.UnitEntity
import com.test.rainbowDefense.database.UnitViewModel
import kotlinx.android.synthetic.main.activity_reinforce.*

class ReinforceActivity : AppCompatActivity() {

    private lateinit var unitViewModel: UnitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reinforce)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        val intent = Intent(this, LobyActivity::class.java)

        btn_reinforce_back.setOnClickListener { view ->
            super.finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val viewManager = LinearLayoutManager(this)
        val viewAdapter = ReinforceAdapter(this)

        viewAdapter.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                val unit: UnitEntity = unitViewModel.haveUnits.value!!.get(pos)
                unitViewModel.update(unit.apply { level++ })
            }
        })

        unitViewModel = ViewModelProvider(this)[UnitViewModel::class.java]
        unitViewModel.haveUnits.observe(this, Observer { units ->
            units?.let { viewAdapter.setUnits(it) }
            Log.d("디버깅", "옵저버 실행")
        })

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
    override fun finish(){
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
