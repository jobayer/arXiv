package com.jobayr.arxiv.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jobayr.arxiv.R
import com.jobayr.arxiv.extensions.launch
import com.jobayr.arxiv.extensions.showExitDialog
import com.jobayr.arxiv.fragments.FavoriteFragment
import com.jobayr.arxiv.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var homeFragment: Fragment
    private lateinit var favoriteFragment: Fragment
    private lateinit var activeFragment: Fragment
    private var activeFragmentIndex = 0
    private var mainMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        mainMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSearch) launch(SearchActivity::class.java)
        return super.onOptionsItemSelected(item)
    }

    // Implement what to do on back press
    override fun onBackPressed() {
        if (activeFragmentIndex != 0) {
            fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit()
            activeFragment = homeFragment
            activeFragmentIndex = 0
            bottomNavigationView.selectedItemId = R.id.miHome
            bottomNavigationView.menu.findItem(R.id.miHome).isChecked = true
            mainMenu?.let {
                it.findItem(R.id.miSearch).isVisible = true
            }
        } else showExitDialog()
    }

    // Starting point of the activity
    private fun init() {
        initFragments()
        initOnClick()
    }

    // Initialize everything related to fragments
    private fun initFragments() {
        fragmentManager = supportFragmentManager
        homeFragment = HomeFragment()
        favoriteFragment = FavoriteFragment()
        fragmentManager.beginTransaction().add(R.id.fragmentsContainer, favoriteFragment)
            .hide(favoriteFragment).commit()
        fragmentManager.beginTransaction().add(R.id.fragmentsContainer, homeFragment)
            .commit()
        activeFragment = homeFragment
    }

    // Handle all the onClick and onLongClick events of all the components of this activity
    private fun initOnClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            return@setOnNavigationItemSelectedListener when(item.itemId) {
                R.id.miHome -> {
                    if (activeFragmentIndex != 0) {
                        fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                        activeFragment = homeFragment
                        activeFragmentIndex = 0
                        mainMenu?.let {
                            it.findItem(R.id.miSearch).isVisible = true
                        }
                        true
                    } else false
                }
                R.id.miFavorite -> {
                    if (activeFragmentIndex != 1) {
                        fragmentManager.beginTransaction().hide(activeFragment).show(favoriteFragment).commit()
                        activeFragment = favoriteFragment
                        activeFragmentIndex = 1
                        mainMenu?.let {
                            it.findItem(R.id.miSearch).isVisible = false
                        }
                        true
                    } else false
                }
                R.id.miSettings -> {
                    launch(SettingsActivity::class.java)
                    false
                }
                else -> false
            }
        }
    }

}