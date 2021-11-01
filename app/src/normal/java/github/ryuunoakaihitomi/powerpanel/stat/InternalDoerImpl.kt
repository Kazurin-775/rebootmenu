package github.ryuunoakaihitomi.powerpanel.stat

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import github.ryuunoakaihitomi.powerpanel.BuildConfig
import timber.log.Timber

/**
 * 目前使用的是`Firebase`和`Microsoft App Center`
 */
object InternalDoerImpl : InternalDoer {

    override fun initialize(app: Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (BuildConfig.DEBUG) AppCenter.setLogLevel(Log.VERBOSE)
            AppCenter.start(app, BuildConfig.AK_APP_CENTER, Analytics::class.java)
        }
    }

    override fun setCustomKey(k: String, v: Any) {
        Firebase.crashlytics.apply {
            when (v) {
                is String -> setCustomKey(k, v)
                is Int -> setCustomKey(k, v)
                is Long -> setCustomKey(k, v)
                is Float -> setCustomKey(k, v)
                is Double -> setCustomKey(k, v)
                is Boolean -> setCustomKey(k, v)
                is Array<*> -> setCustomKey(k, v.contentToString())
                else -> Timber.w("Undefined type: $k, $v")
            }
        }
    }

    override fun logEvent(tag: String, bundle: Bundle) {
        Timber.i(bundle.toString())
        Firebase.analytics.logEvent(tag, bundle)
        Firebase.crashlytics.setCustomKey(tag, bundle.toString())
    }

    override fun log(level: String, tag: String, msg: String) {
        val logLine = listOf(level, tag, msg).toString()
        Firebase.crashlytics.log(logLine)
    }
}