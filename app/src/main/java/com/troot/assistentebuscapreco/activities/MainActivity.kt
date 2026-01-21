package com.troot.assistentebuscapreco.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.troot.assistentebuscapreco.R
import com.troot.assistentebuscapreco.adapter.MessageAdapter
import com.troot.assistentebuscapreco.databinding.ActivityMainBinding
import com.troot.assistentebuscapreco.utils.ThemeHelper
import com.troot.assistentebuscapreco.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel: ChatViewModel by viewModels()
    private var isFabVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplica animação de entrada
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.initialize(this)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupFabMenu()
        updateThemeIcon()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.rvMessages.apply {
            adapter = messageAdapter
            val layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager.stackFromEnd = true
            this.layoutManager = layoutManager
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                messageAdapter.submitList(messages)
                binding.rvMessages.post {
                    if (messages.isNotEmpty()) {
                        binding.rvMessages.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val query = binding.etMessage.text.toString()
            if (query.isNotEmpty()) {
                viewModel.sendMessage(query)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun setupFabMenu() {
        // abre/fecha o menu
        binding.fabThemeTrigger.setOnClickListener {
            toggleFabVisibility()
        }

        // botão de tema
        binding.fabTheme.setOnClickListener {
            changeThemeWithAnimation()
            if (isFabVisible) toggleFabVisibility()
        }

        // botão de configurações
        binding.fabSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            if (isFabVisible) toggleFabVisibility()
        }
    }

    private fun changeThemeWithAnimation() {
        // Fade out da tela
        binding.root.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                // Troca o tema
                val isDark = ThemeHelper.isDarkMode(this)
                val newTheme = if (isDark) {
                    ThemeHelper.THEME_LIGHT
                } else {
                    ThemeHelper.THEME_DARK
                }
                ThemeHelper.setTheme(this, newTheme)
                // O tema é aplicado via AppCompatDelegate
            }
            .start()
    }

    private fun toggleFabVisibility() {
        if (isFabVisible) {
            ObjectAnimator.ofFloat(binding.fabTheme, "alpha", 1f, 0f).apply {
                duration = 200
                start()
            }
            ObjectAnimator.ofFloat(binding.fabSettings, "alpha", 1f, 0f).apply {
                duration = 200
                start()
            }

            binding.fabTheme.postDelayed({
                binding.fabTheme.visibility = View.GONE
                binding.fabSettings.visibility = View.GONE
            }, 200)

            isFabVisible = false
        } else {
            // aparece os dois
            binding.fabTheme.visibility = View.VISIBLE
            binding.fabSettings.visibility = View.VISIBLE

            ObjectAnimator.ofFloat(binding.fabTheme, "alpha", 0f, 1f).apply {
                duration = 200
                start()
            }
            ObjectAnimator.ofFloat(binding.fabSettings, "alpha", 0f, 1f).apply {
                duration = 200
                start()
            }

            isFabVisible = true

            // auto-fecha depois de 3s
            binding.fabTheme.postDelayed({
                if (isFabVisible) {
                    toggleFabVisibility()
                }
            }, 3000)
        }
    }

    private fun updateThemeIcon() {
        val iconRes = if (ThemeHelper.isDarkMode(this)) {
            android.R.drawable.ic_menu_day
        } else {
            android.R.drawable.ic_menu_month
        }
        binding.fabTheme.setImageResource(iconRes)

        // Fade in do conteúdo após recreate
        binding.root.alpha = 0f
        binding.root.animate()
            .alpha(1f)
            .setDuration(150)
            .start()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
