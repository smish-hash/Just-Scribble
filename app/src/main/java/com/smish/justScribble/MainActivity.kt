package com.smish.justScribble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.smish.justScribble.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Toast.makeText(this, "Checking github", Toast.LENGTH_SHORT).show()
        setContentView(binding.root)

        val fragment: FragmentMain = FragmentMain.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, "fragment_main")
                .commit()
        }
    }
}