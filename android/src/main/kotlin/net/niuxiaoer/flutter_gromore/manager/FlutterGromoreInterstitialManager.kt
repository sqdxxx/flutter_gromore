package net.niuxiaoer.flutter_gromore.manager

import android.app.Activity
import android.util.Log
import com.bytedance.msdk.api.AdError
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull
import io.flutter.plugin.common.MethodChannel

class FlutterGromoreInterstitialManager(private val params: Map<String, Any?>,
                                        private val activity: Activity,
                                        private val result: MethodChannel.Result) :
        GMInterstitialFullAdLoadCallback {

    private var mInterstitialAd: GMInterstitialFullAd? = null

    init {
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adUnitId = params["adUnitId"] as? String
        val width = params["width"] as? Double
        val height = params["height"] as? Double

        require(adUnitId != null && adUnitId.isNotEmpty() && width != null && height != null)

        mInterstitialAd = GMInterstitialFullAd(activity, adUnitId)

        val adSlotInterstitialFull = GMAdSlotInterstitialFull.Builder()
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setImageAdSize(width.toInt(), height.toInt())
                .build()

        mInterstitialAd?.loadAd(adSlotInterstitialFull, this)
    }

    override fun onInterstitialFullLoadFail(error: AdError) {
        Log.d("InterstitialManager", "onInterstitialLoadFail - ${error.code} - ${error.message}")
        mInterstitialAd?.destroy()
        mInterstitialAd = null
        result.error(error.code.toString(), error.message, error.toString())
    }

    override fun onInterstitialFullAdLoad() {
        mInterstitialAd?.let {
            FlutterGromoreInterstitialCache.addCacheInterstitialAd(mInterstitialAd.hashCode(), it)
            result.success(mInterstitialAd.hashCode().toString())
        }
    }

    override fun onInterstitialFullCached() {
    }
}