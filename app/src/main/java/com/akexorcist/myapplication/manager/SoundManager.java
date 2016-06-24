package com.akexorcist.myapplication.manager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Akexorcist on 6/7/16 AD.
 */
public class SoundManager {
    public static boolean play(AssetFileDescriptor assetFileDescriptor) {
        if (assetFileDescriptor != null) {
            try {
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static AssetFileDescriptor getAssetFileDescriptor(Context context, String filePath) {
        try {
            return context.getAssets().openFd(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
