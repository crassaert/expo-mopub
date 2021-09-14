//
//  AdLibSDK.m
//  DoubleConversion
//
//  Created by Usama Azam on 29/03/2019.
//

#import "AdLibSDK.h"
#import <MPGoogleGlobalMediationSettings.h>
#import <UnityAdsInstanceMediationSettings.h>

@implementation AdLibSDK

+ (void)initializeAdSDK:(NSString *)unitID {
    MPGoogleGlobalMediationSettings *mpGoogleMediationSettings = [[MPGoogleGlobalMediationSettings alloc] init];
    UnityAdsInstanceMediationSettings *unityMediationSettings = [[UnityAdsInstanceMediationSettings alloc] init];

    MPMoPubConfiguration *sdkConfig = [[MPMoPubConfiguration alloc] initWithAdUnitIdForAppInitialization: unitID];
    sdkConfig.loggingLevel = MPBLogLevelDebug;
    sdkConfig.globalMediationSettings = [[NSArray alloc] initWithObjects: @[mpGoogleMediationSettings, unityMediationSettings], nil];
    [[MoPub sharedInstance] initializeSdkWithConfiguration:sdkConfig completion:^{
        NSLog(@"SDK initialization complete");
    }];
}

@end
