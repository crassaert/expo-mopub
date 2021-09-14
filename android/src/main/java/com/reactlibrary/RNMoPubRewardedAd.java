package com.reactlibrary;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mopub.common.MediationSettings;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedAdListener;
import com.mopub.mobileads.MoPubRewardedAds;
import com.mopub.mobileads.MoPubRewardedAdManager.RequestParameters;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import javax.annotation.Nullable;

/**
 * Created by crassaert on 07/09/2021.
 */

public class RNMoPubRewardedAd extends ReactContextBaseJavaModule implements LifecycleEventListener, MoPubRewardedAdListener {

    ReactApplicationContext mReactContext;

    public RNMoPubRewardedAd(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    public static final String ON_REWARDED_AD_LOAD_SUCCESS = "onRewardedAdLoadSuccess";
    public static final String ON_REWARDED_AD_LOAD_FAILURE = "onRewardedAdLoadFailure";
    public static final String ON_REWARDED_AD_STARTED = "onRewardedAdStarted";
    public static final String ON_REWARDED_AD_PLAYBACK_ERROR = "onRewardedAdPlaybackError";
    public static final String ON_REWARDED_AD_CLOSED = "onRewardedAdClosed";
    public static final String ON_REWARDED_AD_COMPLETED = "onRewardedAdCompleted";
    public static final String ON_REWARDED_AD_CLICKED = "onRewardedAdClicked";
    public static final String ON_REWARDED_AD_SHOW_ERROR = "onRewardedAdShowError";


    @Override
    public String getName() {
        return "RNMoPubRewardedAd";
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        Log.d("Mopub", "Send Event " + eventName);
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }


    @ReactMethod
    public void initializeSdkForRewardedAd(String adUnitId) {

        AdLibSDK.initializeAdSDK(null, adUnitId, mReactContext.getCurrentActivity());

    }

    @ReactMethod
    public void loadRewardedAdWithAdUnitID(final String adUnitId, final String customerId) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        final RequestParameters mParams;

        mParams = new RequestParameters(null, null, null, customerId);

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MoPubRewardedAds.loadRewardedAd(adUnitId, mParams, (MediationSettings) null);
            }
        };
        mainHandler.post(myRunnable);

        MoPubRewardedAds.setRewardedAdListener(this);
    }


    private void sendCallBackMessage(Callback callback, boolean success, String message) {
        try {
            JSONObject dictionary = new JSONObject();
            dictionary.put("success", success);
            dictionary.put("message", message);
            callback.invoke(dictionary.toString());

        } catch (Exception ex) {
            callback.invoke("Internet error!" + ex);
        }
    }

    @ReactMethod
    public void presentRewardedAdForAdUnitID(String unitId, String currencyType, Double amount, Callback callback) {

        Set<MoPubReward> rewards = MoPubRewardedAds.getAvailableRewards(unitId);

        if (rewards.isEmpty()) {
            sendCallBackMessage(callback, false, "reward not found for this UnitId!");
        } else {
            MoPubReward selectedReward = null;
            for (MoPubReward reward : rewards) {
                if ((reward.getAmount() == amount.intValue() && reward.getLabel().equals(currencyType))) {
                    selectedReward = reward;
                }
            }
            if (selectedReward != null) {
                MoPubRewardedAds.showRewardedAd(unitId);
                sendCallBackMessage(callback, true, "video showing!");

            } else {
                sendCallBackMessage(callback, false, "reward not found! for these ingredients!");
            }
        }


    }

    @ReactMethod
    public void hasAdAvailableForAdUnitID(String adUnitId, Callback callback) {

        callback.invoke(MoPubRewardedAds.hasRewardedAd(adUnitId));

    }

    @ReactMethod
    public void availableRewardsForAdUnitID(String unitId, Callback callback) {
        Set<MoPubReward> rewards = MoPubRewardedAds.getAvailableRewards(unitId);

        HashMap<String, Integer> hm = new HashMap<>();
        for (MoPubReward reward : rewards) {
            hm.put(reward.getLabel(), reward.getAmount());
        }
        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            map.putInt(entry.getKey(), entry.getValue());
        }
        callback.invoke(map);

    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }


    @Override
    public void onRewardedAdLoadSuccess(String adUnitId) {
        sendEvent(ON_REWARDED_AD_LOAD_SUCCESS, null);
    }

    @Override
    public void onRewardedAdLoadFailure(String adUnitId, MoPubErrorCode errorCode) {

        HashMap<String, String> hm = new HashMap<>();
        hm.put("error", errorCode.toString());

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }

        sendEvent(ON_REWARDED_AD_LOAD_FAILURE, map);
    }

    @Override
    public void onRewardedAdStarted(String adUnitId) {
        sendEvent(ON_REWARDED_AD_STARTED, null);
    }

    @Override
    public void onRewardedAdCompleted(Set<String> adUnitIds, MoPubReward reward) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("amount", String.valueOf(reward.getAmount()));
        hm.put("currencyType", String.valueOf(reward.getLabel()));

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }
        sendEvent(ON_REWARDED_AD_COMPLETED, map);
    }

    @Override
    public void onRewardedAdClicked(@NonNull String adUnitId) {
        sendEvent(ON_REWARDED_AD_CLICKED, null);
    }

    @Override
    public void onRewardedAdClosed(@NonNull String s) {
        sendEvent(ON_REWARDED_AD_CLOSED, null);
    }

    @Override
    public void onRewardedAdShowError(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("error", moPubErrorCode.toString());

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }

        sendEvent(ON_REWARDED_AD_SHOW_ERROR, map);
    }
}
