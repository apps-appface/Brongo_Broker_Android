package appface.brongo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import appface.brongo.R;

/**
 * Created by Rohit Kumar on 1/22/2018.
 */

public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer = new MediaPlayer();
        try {
            playMedia();
        }catch (Exception e){

        }
        return START_NOT_STICKY;
    }
    private void playMedia(){
        String audioUri = "android.resource://" + getPackageName() + "/" + R.raw.ios7_radiate;
        try {
            //  mediaPlayer.setDataSource(context, defaultRingtoneUri);
            //  mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            // mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setMode(AudioManager.MODE_NORMAL);
           /* audio.setStreamVolume(AudioManager.STREAM_RING,
                    90, 0);*/
            mediaPlayer.setDataSource(MusicService.this, Uri.parse(audioUri));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy()
    {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        // Unregistered or disconnect what you need to
        // For example: mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}
