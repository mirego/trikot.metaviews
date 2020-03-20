package com.trikot.viewModels.sample

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mirego.trikot.viewModels.LifecycleOwnerWrapper
import com.trikot.viewModels.sample.databinding.ActivityMainBinding
import com.trikot.viewModels.sample.databinding.MetaListItemAdapter
import com.trikot.viewModels.sample.navigation.Destination
import com.trikot.viewModels.sample.navigation.NavigationDelegate
import com.trikot.viewModels.sample.viewmodels.AndroidAppViewModel

class MainActivity : AppCompatActivity(), NavigationDelegate {
    lateinit var binding: ActivityMainBinding
    private lateinit var appViewModel: AndroidAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProviders.of(this).get(AndroidAppViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.datasets.adapter = MetaListItemAdapter()
        binding.viewModel = appViewModel.getVm(this)
        binding.lifecycleOwner = this
        binding.lifecycleOwnerWrapper = LifecycleOwnerWrapper(this)
    }

    override fun navigateTo(destination: Destination) {
        startListActivity(destination)
    }

    override fun showAlert(text: String) {
        AlertDialog.Builder(this)
            .setTitle(text)
            .setNegativeButton(android.R.string.yes, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}

