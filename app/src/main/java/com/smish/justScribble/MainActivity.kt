package com.smish.justScribble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smish.justScribble.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment: FragmentMain = FragmentMain.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, "fragment_main")
                .commit()
        }
    }
}