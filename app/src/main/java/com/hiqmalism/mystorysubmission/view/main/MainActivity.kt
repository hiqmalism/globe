package com.hiqmalism.mystorysubmission.view.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiqmalism.mystorysubmission.R
import com.hiqmalism.mystorysubmission.databinding.ActivityMainBinding
import com.hiqmalism.mystorysubmission.view.ViewModelFactory
import com.hiqmalism.mystorysubmission.view.adapter.LoadingStateAdapter
import com.hiqmalism.mystorysubmission.view.adapter.StoryAdapter
import com.hiqmalism.mystorysubmission.view.maps.MapsActivity
import com.hiqmalism.mystorysubmission.view.upload.StoryUploadActivity
import com.hiqmalism.mystorysubmission.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                mainViewModel.getStories()
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        setupView()
        setupAction()
        getData()

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        mainViewModel.successMessage.observe(this) { message ->
            message?.let {
                showSuccessDialog(it)
            }
        }
        mainViewModel.errorMessage.observe(this) { message ->
            message?.let {
                showErrorDialog(it)
            }
        }
    }

    private fun setupView() {
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.governor_bay)
            )
        )
    }

    private fun setupAction() {
        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(this, StoryUploadActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                finish()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                true
            }
            R.id.action_maps -> {
                val mapsIntent = Intent(this, MapsActivity::class.java)
                startActivity(mapsIntent)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getData() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.listStory.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onResume() {
        val adapter = StoryAdapter()
        adapter.refresh()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSuccessDialog(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showErrorDialog(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.view?.setBackgroundColor(ContextCompat.getColor(this, R.color.on_error))
        toast.show()
    }

}