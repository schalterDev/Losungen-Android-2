package schalter.de.losungen2.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import io.reactivex.Single

object AsyncUtils {
    data class Optional<T>(val value: T?)

    fun <T> liveDataToSingleOptional(liveData: LiveData<T>): Single<Optional<T>> {
        return Single.create { single ->
            val observer: androidx.lifecycle.Observer<T?> = object : androidx.lifecycle.Observer<T?> {
                override fun onChanged(value: T?) {
                    single.onSuccess(Optional(value))
                    liveData.removeObserver(this)
                }
            }

            liveData.observeForever(observer)
        }
    }

    fun runOnMainThread(block: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            block()
        }
    }
}