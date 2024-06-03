package com.example.storyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.maps.MapsFragment
import com.example.storyapp.view.profile.ProfileFragment
import com.example.storyapp.view.story.StoryFragment

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentId: Int = R.id.beranda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beranda -> {
                    currentFragmentId = R.id.beranda
                    replaceFragment(StoryFragment())
                    true
                }
                R.id.maps -> {
                    currentFragmentId = R.id.maps
                    replaceFragment(MapsFragment())
                    true
                }
                R.id.profile -> {
                    currentFragmentId = R.id.profile
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt("currentFragmentId", R.id.beranda)
        }

        when (currentFragmentId) {
            R.id.beranda -> replaceFragment(StoryFragment())
            R.id.profile -> replaceFragment(ProfileFragment())
            R.id.maps -> replaceFragment(MapsFragment())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentFragmentId", currentFragmentId)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}