import {NativeModules, NativeEventEmitter} from 'react-native';

const {RNMoPubRewardedAd} = NativeModules;


const emitter = new NativeEventEmitter(RNMoPubRewardedAd);

module.exports = {
    initializeSdkForRewardedAd: (adUnitId: string) => RNMoPubRewardedAd.initializeSdkForRewardedAd(adUnitId),
    loadRewardedAdWithAdUnitID: (adUnitId: string, customerId: string) => RNMoPubRewardedAd.loadRewardedAdWithAdUnitID(adUnitId, customerId),
    presentRewardedAdForAdUnitID: (adUnitId: string, currencyType: string, amount: number, promise: ()=>{}) => RNMoPubRewardedAd.presentRewardedAdForAdUnitID(adUnitId, currencyType, amount, promise),
    availableRewardsForAdUnitID: (adUnitId: string, promise: ()=>{}) => RNMoPubRewardedAd.availableRewardsForAdUnitID(adUnitId, promise),
    hasAdAvailableForAdUnitID: (adUnitId: string, promise: ()=>{}) => RNMoPubRewardedAd.hasAdAvailableForAdUnitID(adUnitId, promise),
    addEventListener: (eventType: string, listener: Function) => emitter.addListener(eventType, listener),
    removeAllListeners: (eventType: string) => emitter.removeAllListeners(eventType)
};
