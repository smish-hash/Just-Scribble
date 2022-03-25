package com.smish.justScribble

import android.content.Context
import android.graphics.Paint
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import com.google.android.material.tabs.TabLayout
import com.smish.justScribble.PaintView.Companion.canvas
import com.smish.justScribble.PaintView.Companion.currentBrushColor
import com.smish.justScribble.PaintView.Companion.currentShape
import com.smish.justScribble.databinding.FragmentMainBinding


class FragmentMain : Fragment() {

    companion object {
        fun newInstance() = FragmentMain()
        var brush = Paint()
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var vibratorManager: VibratorManager
    private lateinit var vibration: Vibrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        brush.color = resources.getColor(R.color.black, context?.theme)
        currentColor(brush.color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibration = vibratorManager.defaultVibrator
        } else {
            vibration = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun setListeners() {

        binding.palette.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> currentShape = "LINE"
                    1 -> currentShape = "ARROW"
                    2 -> currentShape = "RECTANGLE"
                    3 -> currentShape = "CIRCLE"
                    4 -> setColorPalette()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    4 -> {
                        binding.colorPalette.visibility = View.GONE
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    4 -> {
                        binding.colorPalette.startAnimation(AnimationUtils.loadAnimation(context, R.anim.color_palette_open_anim))
                        binding.colorPalette.visibility = View.VISIBLE
                    }
                }
            }
        })

        binding.colorRed.setOnClickListener {
            setAnimation(0, it)
        }

        binding.colorGreen.setOnClickListener {
            setAnimation(1, it)
        }

        binding.colorBlue.setOnClickListener {
            setAnimation(2, it)
        }

        binding.colorBlack.setOnClickListener {
            setAnimation(3, it)
        }

        binding.clearAll.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibration.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                vibration.vibrate(VibrationEffect.createOneShot(50, 1))
            }

            it.animate()
                .rotationBy(180f)
                .setDuration(100)
                .setInterpolator(LinearInterpolator())
                .withEndAction {
                    canvas.drawColor(resources.getColor(R.color.white, requireContext().theme))
                    binding.colorPalette.visibility = View.GONE
                }
                .start()
        }
    }

    private fun setAnimation(color: Int, view: View) {
        view.animate()
            .setInterpolator(LinearInterpolator())
            .setDuration(100)
            .scaleXBy(-0.7f)
            .scaleYBy(-0.7f)
            .withEndAction {
                view.animate()
                    .setInterpolator(BounceInterpolator())
                    .setDuration(100)
                    .scaleX(1f)
                    .scaleY(1f)
                    .withEndAction {
                        when (color) {
                            0 -> {
                                brush.color = requireContext().getColor(R.color.red)
                                currentColor(brush.color)
                            }

                            1 -> {
                                brush.color = requireContext().getColor(R.color.green)
                                currentColor(brush.color)
                            }

                            2 -> {
                                brush.color = requireContext().getColor(R.color.blue)
                                currentColor(brush.color)
                            }

                            3 -> {
                                brush.color = requireContext().getColor(R.color.black)
                                currentColor(brush.color)
                            }
                        }

                        binding.colorPalette.visibility = View.GONE
                    }
                    .start()
            }
            .start()
    }

    private fun currentColor(color: Int) {
        currentBrushColor = color
    }

    private fun setColorPalette() {
        if (binding.colorPalette.visibility == View.VISIBLE) {
            binding.colorPalette.visibility = View.GONE
        } else
            binding.colorPalette.startAnimation(AnimationUtils.loadAnimation(context, R.anim.color_palette_open_anim))
        binding.colorPalette.visibility = View.VISIBLE
    }
}