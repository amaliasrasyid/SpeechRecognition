package com.wisnu.speechrecognition.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputEditText
import com.wisnu.speechrecognition.utils.UtilsCode.MODE_LIGHT
import com.wisnu.speechrecognition.utils.UtilsCode.MODE_NIGHT
import com.wisnu.speechrecognition.utils.UtilsCode.PATTERN_DATE_API
import com.wisnu.speechrecognition.utils.UtilsCode.PATTERN_DATE_VIEW_DAY
import com.wisnu.speechrecognition.utils.UtilsCode.PATTERN_DATE_VIEW_MONTH
import com.wisnu.speechrecognition.utils.UtilsCode.PATTERN_DATE_VIEW_YEAR
import com.wisnu.speechrecognition.utils.UtilsCode.UNDEFINIED_MODE
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToast.Companion.GRAVITY_BOTTOM
import www.sanju.motiontoast.MotionToast.Companion.LONG_DURATION
import java.io.File
import java.util.*

fun showMessage(
    activity: Activity,
    title: String,
    message: String = "Cek Koneksi Internet Dan Coba Lagi!" ,
    style: String,
    position: Int = GRAVITY_BOTTOM,
    duration: Long = LONG_DURATION
) {
    MotionToast.apply {
        when (checkSystemMode(activity)) {

            MODE_LIGHT -> {
                createColorToast(
                    activity,
                    title,
                    message,
                    style,
                    position,
                    duration,
                    ResourcesCompat.getFont(
                        activity,
                        www.sanju.motiontoast.R.font.helvetica_regular
                    )
                )
            }
            MODE_NIGHT -> {
                darkColorToast(
                    activity,
                    title,
                    message,
                    style,
                    position,
                    duration,
                    ResourcesCompat.getFont(
                        activity,
                        www.sanju.motiontoast.R.font.helvetica_regular
                    )
                )
            }
        }
    }

}

fun isLoading(
    state: Boolean,
    pb: ProgressBar? = null,
    pbLottie: LottieAnimationView? = null,
    isClickButton: Boolean = false,
    tv: TextView? = null,
) {
    if (isClickButton) {
        if (state) {
            pb?.visibility = VISIBLE
            pbLottie?.visibility = VISIBLE
            tv!!.visibility = GONE
        } else {
            pb?.visibility = GONE
            pbLottie?.visibility = GONE
            tv!!.visibility = VISIBLE
        }
    } else {
        if (state) {
            pb?.visibility = VISIBLE
            pbLottie?.visibility = VISIBLE
        } else {
            pb?.visibility = GONE
            pbLottie?.visibility = GONE
        }
    }
}

fun isLoadingPercentageProgress(state: Boolean, pb: ProgressBar, tv: TextView) {
    if (state) {
        pb.visibility = VISIBLE
        tv.visibility = GONE
    } else {
        pb.visibility = GONE
        tv.visibility = VISIBLE
    }
}

fun isLoadingImage(state: Boolean, pb: ProgressBar) {
    if (state) {
        pb.visibility = VISIBLE
    } else {
        pb.visibility = GONE
    }
}

fun listenerImages(pb: ProgressBar? = null, pbLottie: LottieAnimationView? = null) =
    object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            pb?.visibility = GONE
            pbLottie?.visibility = GONE
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            pb?.visibility = GONE
            pbLottie?.visibility = GONE
            return false
        }
    }

// loading in bottomsheet
fun loadingInBottomSheet(btnSave: Button, progressBarSheet: ProgressBar, isLoading: Boolean) {
    if (isLoading) {
        btnSave.visibility = View.INVISIBLE
        progressBarSheet.visibility = VISIBLE
    } else {
        btnSave.visibility = GONE
        progressBarSheet.visibility = GONE
    }
}

fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString.toRequestBody(MultipartBody.FORM)
}

fun reqFileImage(path: String?, name: String): MultipartBody.Part {
    val fileImage = File(path!!)
    val reqFileImage =
        fileImage.asRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        name, fileImage.name, reqFileImage
    )
}

fun reqFileImageEmpty(name: String): MultipartBody.Part {
    val reqFileImage = ""
        .toRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, "", reqFileImage)
}

fun setVisibilityBottom(activity: Activity, state: Boolean) {
//    activity.findViewById<BottomNavigationView>(R.id.nav_view).visibility =
    if (state) VISIBLE else GONE
}

fun checkSystemMode(context: Context): Int {
    return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> MODE_NIGHT
        Configuration.UI_MODE_NIGHT_NO -> MODE_LIGHT
        Configuration.UI_MODE_NIGHT_UNDEFINED -> UNDEFINIED_MODE
        else -> -2
    }
}

fun openKeyboard(fragmentActivity: FragmentActivity, editText: TextInputEditText) {
    val inputMethodManager: InputMethodManager =
        fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun closeKeyboard(fragmentActivity: FragmentActivity) {
    val view = fragmentActivity.currentFocus
    if (view != null) {
        val inputMethodManager: InputMethodManager =
            fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun normalizedNumber(number: String): String {
    return when {
        number.contains("+62") -> number.replace("+62", "")
        number.contains("0") -> {
//            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
//                number.replaceFirst("0", "")
//            } else {
//                number.replace("0", "")
//            }
            number.replaceFirst("0", "")
        }
        else -> number
    }
}

fun normalizedNumber2(number: String): String {
    return if (number.contains("+62")) {
        number.replace("+62", "0")
    } else {
        number
    }
}

fun calculateAge(birthDay: String): String {
    val formatDate = FormatDate()
    val year = formatDate.format(birthDay, PATTERN_DATE_API, PATTERN_DATE_VIEW_YEAR)
    val month = formatDate.format(birthDay, PATTERN_DATE_API, PATTERN_DATE_VIEW_MONTH)
    val day = formatDate.format(birthDay, PATTERN_DATE_API, PATTERN_DATE_VIEW_DAY)
    val calBirth = Calendar.getInstance()
    val calToday = Calendar.getInstance()
    calBirth.set(year.toInt(), month.toInt(), day.toInt())
    var age = calToday.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
    if (calToday.get(Calendar.DAY_OF_YEAR) < calBirth.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age.toString()
}

val String.capitalizeEachWords
    get() = this.split(" ").joinToString(" ") {
        it.replaceFirstChar { c ->
            if (c.isLowerCase()) {
                c.titlecase(Locale.getDefault())
            } else {
                c.toString()
            }
        }
    }