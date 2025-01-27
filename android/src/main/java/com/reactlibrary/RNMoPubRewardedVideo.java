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
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubRewardedVideoManager.RequestParameters;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import javax.annotation.Nullable;

/**
 * Created by usamaazam on 30/03/2019.
 */

public class RNMoPubRewardedVideo extends ReactContextBaseJavaModule implements LifecycleEventListener, MoPubRewardedVideoListener {

    ReactApplicationContext mReactContext;

    public RNMoPubRewardedVideo(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    public static final String ON_REWARDED_VIDEO_LOAD_SUCCESS = "onRewardedVideoLoadSuccess";
    public static final String ON_REWARDED_VIDEO_LOAD_FAILURE = "onRewardedVideoLoadFailure";
    public static final String ON_REWARDED_VIDEO_STARTED = "onRewardedVideoStarted";
    public static final String ON_REWARDED_VIDEO_PLAYBACK_ERROR = "onRewardedVideoPlaybackError";
    public static final String ON_REWARDED_VIDEO_CLOSED = "onRewardedVideoClosed";
    public static final String ON_REWARDED_VIDEO_COMPLETED = "onRewardedVideoCompleted";
    public static final String ON_REWARDED_VIDEO_CLICKED = "onRewardedVideoClicked";


    @Override
    public String getName() {
        return "RNMoPubRewardedVideo";
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        Log.d("Mopub", "Send Event " + eventName);
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }


    @ReactMethod
    public void initializeSdkForRewardedVideoAd(String adUnitId) {

        AdLibSDK.initializeAdSDK(null, adUnitId, mReactContext.getCurrentActivity());

    }

    @ReactMethod
    public void loadRewardedVideoAdWithAdUnitID(final String adUnitId, final String customerId) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        final RequestParameters mParams;

        mParams = new RequestParameters(null, null, null, customerId);

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.loadRewardedVideo(adUnitId, mParams, (MediationSettings) null);
            }
        };
        mainHandler.post(myRunnable);

        MoPubRewardedVideos.setRewardedVideoListener(this);
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
    public void presentRewardedVideoAdForAdUnitID(final String unitId, String currencyType, Double amount, final Callback callback) {

        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.showRewardedVideo(unitId);
                sendCallBackMessage(callback, true, "video showing!");
            }
        };

        Set<MoPubReward> rewards = MoPubRewardedVideos.getAvailableRewards(unitId);

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
                mainHandler.post(myRunnable);

            } else {
                sendCallBackMessage(callback, false, "reward not found! for these ingredients!");
            }
        }
    }

    @ReactMethod
    public void hasAdAvailableForAdUnitID(String adUnitId, Callback callback) {

        callback.invoke(MoPubRewardedVideos.hasRewardedVideo(adUnitId));

    }

    @ReactMethod
    public void availableRewardsForAdUnitID(String unitId, Callback callback) {
        Set<MoPubReward> rewards = MoPubRewardedVideos.getAvailableRewards(unitId);

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
    public void onRewardedVideoLoadSuccess(String adUnitId) {
        sendEvent(ON_REWARDED_VIDEO_LOAD_SUCCESS, null);
    }

    @Override
    public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {

        HashMap<String, String> hm = new HashMap<>();
        hm.put("error", errorCode.toString());

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }

        sendEvent(ON_REWARDED_VIDEO_LOAD_FAILURE, map);
    }

    @Override
    public void onRewardedVideoStarted(String adUnitId) {
        sendEvent(ON_REWARDED_VIDEO_STARTED, null);
    }

    @Override
    public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("error", errorCode.toString());

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }


        sendEvent(ON_REWARDED_VIDEO_PLAYBACK_ERROR, null);
    }

    @Override
    public void onRewardedVideoClosed(String adUnitId) {
        sendEvent(ON_REWARDED_VIDEO_CLOSED, null);

    }

    @Override
    public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("amount", String.valueOf(reward.getAmount()));
        hm.put("currencyType", String.valueOf(reward.getLabel()));

        WritableMap map = new WritableNativeMap();
        for (Map.Entry<String, String> entry : hm.entrySet()) {
            map.putString(entry.getKey(), entry.getValue());
        }
        sendEvent(ON_REWARDED_VIDEO_COMPLETED, map);
    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {
        sendEvent(ON_REWARDED_VIDEO_CLICKED, null);
    }
}
