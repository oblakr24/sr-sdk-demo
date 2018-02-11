package demo.utils

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import java.text.DateFormat
import java.util.*

/**
 * Created by rokoblak on 1/10/18.
 */

fun DateFormat.safeFormat(date: Date?) = date?.let { this.format(it) } ?: "/"

fun View.fadeIn(duration: Long = 1000L) {
    animateVisibility(View.VISIBLE, duration)
}

fun View.fadeOut(duration: Long = 1000L) {
    animateVisibility(View.GONE, duration)
}

fun View.animateVisibility(endVisibility: Int, animDuration: Long) {
    val isHiddenInitially = visibility == View.GONE
    visibility = View.VISIBLE
    animation = AlphaAnimation(if (isHiddenInitially) 0f else 1f, if (endVisibility == View.GONE) 0f else 1f).apply {
        duration = animDuration
        interpolator = if (isHiddenInitially) DecelerateInterpolator() else AccelerateInterpolator()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                visibility = endVisibility
            }
        })
    }
}

fun ViewGroup.inflateChild(resourceId: Int, attach: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resourceId, this, attach)
}

/**
 * Fades in a view from a previous one
 */
fun View.crossFadeFrom(previous: View, fadeDuration: Long = 800L) {
    visibility = View.VISIBLE
    previous.visibility = View.VISIBLE

    val newView = this
    if (this == previous) return

    if (fadeDuration == 0L) {
        previous.visibility = View.GONE
    } else {

        val fadeOutDurationFactor = 0.7f

        animation = AlphaAnimation(0f, 1f).apply {
            interpolator = DecelerateInterpolator()
            duration = fadeDuration
        }

        previous.animation = AlphaAnimation(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            duration = (fadeDuration * fadeOutDurationFactor).toLong()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {}
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    newView.visibility = View.VISIBLE
                    previous.visibility = View.GONE
                }
            })
        }
    }
}

fun Activity.hideKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}