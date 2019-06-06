package eco.data.m3.demo.netperf.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView

class ConnActivity : AppCompatActivity() {

    val recyclerView:RecyclerView ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conn)
    }
}
