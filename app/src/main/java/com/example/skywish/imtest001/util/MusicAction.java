package com.example.skywish.imtest001.util;

import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by DYY-PC on 2015/7/5.
 */
public class MusicAction {
    public MusicAction() {
    }

    public MediaPlayer mediaPlayer;
    public boolean isComplete;

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void play(String fileUrl, boolean looping) {
        if (mediaPlayer != null) {
            stop();
        }
        mediaPlayer = new MediaPlayer();
        if (fileUrl != null) {
            try {
                mediaPlayer.setDataSource(fileUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (looping) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
        } else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isComplete = true;
                }
            });
        }
    }

    public boolean complete() {
        return isComplete;
    }
}
