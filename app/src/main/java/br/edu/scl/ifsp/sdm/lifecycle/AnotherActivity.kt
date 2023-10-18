package br.edu.scl.ifsp.sdm.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.scl.ifsp.sdm.lifecycle.databinding.ActivityAnotherBinding

class AnotherActivity : AppCompatActivity() {
    private val activityAnotherBinding: ActivityAnotherBinding by lazy {
        ActivityAnotherBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityAnotherBinding.root)
    }
}