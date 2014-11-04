package nzgames.mazegame.android;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import nzgames.mazegame.MainGame;


public class AndroidLauncher extends AndroidApplication {
    /** The view to show the ad. */
    private AdView adView;


    private boolean showAds = true;
    /* Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-3051992755771126/3110326895";


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //set the screen to stay on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        // Create the libgdx View
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


        //initialize the MainGame view
        final MainGame mainGame = new MainGame();
        if(showAds) {
            //mainGame.showAds = true;
        }
        View gameView = initializeForView(mainGame, config);

        // Add the libgdx view
        layout.addView(gameView);

        if(showAds) {
            // Add the AdMob view
            RelativeLayout.LayoutParams adParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            // Create an ad.
            adView = new AdView(this);
            //adView.setAdSize(AdSize.BANNER);//this one skips part of the screen. Why? Smart banner seems to fill it with gray.
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(AD_UNIT_ID);

            //this is how libgdx will know the ad is fully loaded
            adView.setAdListener(new AdListener() {
                boolean firstAd = true;
                @Override
                public void onAdOpened() {
                    // Save app state before going to the ad overlay.
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    //wait until the ads are loaded before setting libgdx to allow space
                    if(firstAd) {
                        adView.setBackgroundColor(Color.BLACK);//the ad takes forever to view sometimes, at least make the spot visible.
                        mainGame.showAds = true;
                        mainGame.setScreenDimensionsForAds();
                        mainGame.needCameraResize = true;
                    }
                    firstAd = false; //so that we only change the camera one time


                }
            });

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            adView.loadAd(adRequest);

            // Add the admob view
            layout.addView(adView);
        }

        // Hook it all up
        setContentView(layout);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
