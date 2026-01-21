package com.troot.assistentebuscapreco.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.troot.assistentebuscapreco.R
import com.troot.assistentebuscapreco.data.local.ProfilePrefs
import com.troot.assistentebuscapreco.databinding.ActivitySettingsBinding
import com.troot.assistentebuscapreco.utils.ThemeHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var profilePrefs: ProfilePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profilePrefs = ProfilePrefs(this)

        setupToolbar()
        loadCurrentValues()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadCurrentValues() {
        // Perfil
        binding.inputName.setText(profilePrefs.userName)
        binding.inputPreferences.setText(profilePrefs.preferencesCsv)

        // Tema
        when (ThemeHelper.getSavedTheme(this)) {
            ThemeHelper.THEME_LIGHT -> binding.radioLight.isChecked = true
            ThemeHelper.THEME_DARK -> binding.radioDark.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim().orEmpty()
            val prefsCsv = binding.inputPreferences.text?.toString()?.trim().orEmpty()

            if (name.isBlank()) {
                Toast.makeText(this, "Digite seu nome.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva perfil local
            profilePrefs.userName = name
            profilePrefs.preferencesCsv = prefsCsv

            // Salva tema
            val themeMode = when (binding.radioGroupTheme.checkedRadioButtonId) {
                R.id.radioLight -> ThemeHelper.THEME_LIGHT
                R.id.radioDark -> ThemeHelper.THEME_DARK
                else -> ThemeHelper.THEME_SYSTEM
            }
            ThemeHelper.setTheme(this, themeMode)

            Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()

            // Reabre o chat
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.btnReset.setOnClickListener {
            // Limpa os dados do perfil
            profilePrefs.clearAll()

            // Volta para o tour e limpa a pilha
            val intent = Intent(this, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
