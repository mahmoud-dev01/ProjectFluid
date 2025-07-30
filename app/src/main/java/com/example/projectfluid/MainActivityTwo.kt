package com.example.projectfluid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectfluid.databinding.ActivityMainTwoBinding

class MainActivityTwo : AppCompatActivity() {

    private lateinit var binding: ActivityMainTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClippingDemo.setOnClickListener {
            startActivity(Intent(this, ClippingDecorationActivity::class.java))
        }

        binding.btnMaskingDemo.setOnClickListener {
            startActivity(Intent(this, MaskingLayoutActivity::class.java))
        }
    }

}