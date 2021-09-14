require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
    s.name         = "expo-mopub"
    s.version      = package["version"]
    s.summary      = package["description"]
    s.description  = <<-DESC
    expo-mopub
    DESC
    s.homepage     = "https://github.com/jeremyvinec/expo-mopub"
    s.license      = "MIT"
    # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
    s.author       = { "author" => "author@domain.cn" }
    s.platform     = :ios, "7.0"
    s.source       = { :git => "https://github.com/jeremyvinec/expo-mopub", :tag => "#{s.version}" }

    s.source_files = "ios/**/*.{h,m}"
    s.requires_arc = true

    s.dependency "React"

    s.subspec "MoPub" do |ss|

        ss.dependency 'mopub-ios-sdk', '~> 5.17.0'

        s.static_framework = true
    end

    s.subspec "AdMob" do |ss|

        ss.dependency 'MoPub-AdMob-Adapters', '~> 8.6.0'

    end

    s.subspec "FacebookAudienceNetwork" do |ss|

        ss.dependency 'MoPub-FacebookAudienceNetwork-Adapters', '~> 6.2.1'

    end

    s.subspec "UnityAds" do |ss|

        ss.dependency 'MoPub-UnityAds-Adapters', '~> 3.7.5.1'

    end

    #s.dependency "others"
end

