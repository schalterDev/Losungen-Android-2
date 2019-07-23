package de.schalter.losungen.components.async

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class RxAsyncTask<Params, Progress, Result> {

    private var handler: Handler? = null

    @SuppressLint("CheckResult")
    fun execute(data: Params) {
        Observable.fromCallable { doInBackground(data) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onPreExecute() }
                .subscribe({ onPostExecute(it) }, { onError(it) })
    }

    protected fun publishProgress(progress: Progress) {
        this.runOnUiThread { onProgress(progress) }
    }

    abstract fun doInBackground(params: Params): Result

    protected open fun onPreExecute() {}

    protected open fun onPostExecute(result: Result) {}

    protected open fun onProgress(progress: Progress) {}

    protected open fun onError(error: Throwable) {}

    protected fun runOnUiThread(run: () -> Unit) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }

        handler?.post { run() }
    }
}