package com.troot.assistentebuscapreco.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.troot.assistentebuscapreco.data.local.ProfilePrefs
import com.troot.assistentebuscapreco.databinding.ActivityOnboardingBinding
import com.troot.assistentebuscapreco.utils.ThemeHelper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var profilePrefs: ProfilePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica o tema salvo antes de inflar a tela
        ThemeHelper.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profilePrefs = ProfilePrefs(this)

        setupThemeDropdown()
        setupButtons()
    }

    private fun setupThemeDropdown() {
        val themes = listOf("Sistema", "Claro", "Escuro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, themes)
        binding.themeDropdown.setAdapter(adapter)

        // Seleciona opção com o que já estiver salvo
        val savedTheme = ThemeHelper.getSavedTheme(this)
        val text = when (savedTheme) {
            ThemeHelper.THEME_LIGHT -> "Claro"
            ThemeHelper.THEME_DARK -> "Escuro"
            else -> "Sistema"
        }
        binding.themeDropdown.setText(text, false)
    }

    private fun setupButtons() {
        binding.btnContinue.setOnClickListener {
            val name = binding.inputName.text?.toString()?.trim().orEmpty()
            val prefsText = binding.inputPrefs.text?.toString()?.trim().orEmpty()

            if (name.isBlank()) {
                Toast.makeText(this, "Digite seu nome para continuar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Salva perfil local
            profilePrefs.userName = name
            profilePrefs.preferencesCsv = prefsText
            profilePrefs.hasCompletedOnboarding = true

            // Salva tema escolhido
            val selectedTheme = binding.themeDropdown.text?.toString()?.trim().orEmpty()
            val themeMode = when (selectedTheme) {
                "Claro" -> ThemeHelper.THEME_LIGHT
                "Escuro" -> ThemeHelper.THEME_DARK
                else -> ThemeHelper.THEME_SYSTEM
            }
            ThemeHelper.setTheme(this, themeMode)

            // Vai pro chat
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnSkip.setOnClickListener {
            // Marca onboarding como concluído, para não voltar
            profilePrefs.hasCompletedOnboarding = true

            // Não altera, deixa como está
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
