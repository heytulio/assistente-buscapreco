package com.troot.assistentebuscapreco.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.troot.assistentebuscapreco.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia animações
        startAnimations()

        // Navega para MainActivity após 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000)
    }

    private fun startAnimations() {
        // Animação do ícone (escala + fade)
        val iconScaleX = ObjectAnimator.ofFloat(binding.tvLogoIcon, View.SCALE_X, 0.3f, 1f)
        val iconScaleY = ObjectAnimator.ofFloat(binding.tvLogoIcon, View.SCALE_Y, 0.3f, 1f)
        val iconAlpha = ObjectAnimator.ofFloat(binding.tvLogoIcon, View.ALPHA, 0f, 1f)

        val iconAnimator = AnimatorSet().apply {
            playTogether(iconScaleX, iconScaleY, iconAlpha)
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Animação do nome do app (slide + fade)
        val nameTranslation = ObjectAnimator.ofFloat(binding.tvAppName, View.TRANSLATION_Y, 50f, 0f)
        val nameAlpha = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 0f, 1f)

        val nameAnimator = AnimatorSet().apply {
            playTogether(nameTranslation, nameAlpha)
            duration = 500
            startDelay = 300
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Animação da tagline
        val taglineAlpha = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 0f, 1f)
        taglineAlpha.apply {
            duration = 500
            startDelay = 600
        }

        // Animação do progress bar
        val progressAlpha = ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 0f, 1f)
        progressAlpha.apply {
            duration = 400
            startDelay = 900
        }

        // Animação da versão
        val versionAlpha = ObjectAnimator.ofFloat(binding.tvVersion, View.ALPHA, 0f, 1f)
        versionAlpha.apply {
            duration = 400
            startDelay = 1000
        }

        // Executa todas as animações
        AnimatorSet().apply {
            playSequentially(iconAnimator, nameAnimator, taglineAlpha, progressAlpha, versionAlpha)
            start()
        }
    }
}
