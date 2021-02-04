/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Platform,
  TouchableOpacity,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';
import {
  RNMoPubInterstitial,
  MoPubBanner,
  RNMoPubRewardedVideo,
  RNNativeAdView,
} from 'expo-mopub';

const INTERSTITIAL_UNIT_ID =
  Platform.OS === 'ios'
    ? '4f117153f5c24fa6a3a92b818a5eb630'
    : '24534e1901884e398f1253216226017e';
const BANNERL_UNIT_ID =
  Platform.OS === 'ios'
    ? '0ac59b0996d947309c33f59d6676399f'
    : 'b195f8dd8ded45fe847ad89ed1d016da';
const REWARDED_UNIT_ID =
  Platform.OS === 'ios'
    ? '8f000bd5e00246de9c789eed39ff6096'
    : '920b6145fb1546cf8b5cf2ac34638bb7';
const NATIVE_UNIT_ID =
  Platform.OS === 'ios'
    ? '76a3fefaced247959582d2d2df6f4757'
    : '11a17b188668469fb0412708c3d16813';

const App: () => React$Node = () => {
  useEffect(() => {
    RNMoPubInterstitial.initializeInterstitialAd(INTERSTITIAL_UNIT_ID);
    //RNMoPubRewardedVideo.initializeSdkForRewardedVideoAd(REWARDED_UNIT_ID);
    RNMoPubInterstitial.addEventListener('onLoaded', () => {
      console.log('Interstitial Loaded');
      RNMoPubInterstitial.show();
    });
    RNMoPubInterstitial.addEventListener('onFailed', (message) =>
      console.log('Interstitial failed: ' + JSON.stringify(message)),
    );
  }, []);

  const onLoaded = () => {
    console.log('MoPub banner loaded');
  };

  const onFailed = (message) => {
    console.log('MoPub banner failed with message: ' + message);
  };

  const onClicked = () => {
    console.log('MoPub banner was clicked');
  };

  return (
    <>
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <MoPubBanner
            adUnitId={BANNERL_UNIT_ID}
            autoRefresh={true}
            onLoaded={onLoaded}
            onFailed={onFailed}
            onClicked={onClicked}
          />

          <TouchableOpacity
            style={styles.bloc}
            onPress={() => console.log(RNMoPubInterstitial.show())}>
            <Text>show ad</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.bloc}
            onPress={() => RNMoPubInterstitial.loadAd()}>
            <Text>load ad</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.bloc}
            onPress={() =>
              RNMoPubRewardedVideo.loadRewardedVideoAdWithAdUnitID(
                REWARDED_UNIT_ID,
              )
            }>
            <Text>load video</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.bloc}
            onPress={() =>
              RNMoPubRewardedVideo.presentRewardedVideoAdForAdUnitID(
                REWARDED_UNIT_ID,
                '$',
                12,
                (data) => {
                  console.log(data);
                },
              )
            }>
            <Text>show video</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.bloc}
            onPress={() =>
              RNMoPubRewardedVideo.availableRewardsForAdUnitID(
                REWARDED_UNIT_ID,
                (data) => {
                  console.log(data);
                },
              )
            }>
            <Text>has ad for key</Text>
          </TouchableOpacity>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
  bloc: {
    width: 100,
    height: 30,
    backgroundColor: 'red',
    marginTop: 10,
  },
});

export default App;
