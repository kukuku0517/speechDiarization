package com.example.user.mediacodecpractice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class PlayerActivity extends Activity implements OnAudioStreamInterface, OnSeekBarChangeListener, OnClickListener
{

    private Button mPlayButton = null;
    private Button mStopButton = null;

    private TextView mTextCurrentTime = null;
    private TextView mTextDuration = null;

    private SeekBar mSeekProgress = null;

    private ProgressDialog mProgressDialog = null;

    AudioStreamPlayer mAudioPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayButton = (Button) this.findViewById(R.id.button_play);
        mPlayButton.setOnClickListener(this);
        mStopButton = (Button) this.findViewById(R.id.button_stop);
        mStopButton.setOnClickListener(this);

        mTextCurrentTime = (TextView) findViewById(R.id.text_pos);
        mTextDuration = (TextView) findViewById(R.id.text_duration);

        mSeekProgress = (SeekBar) findViewById(R.id.seek_progress);
        mSeekProgress.setOnSeekBarChangeListener(this);
        mSeekProgress.setMax(0);
        mSeekProgress.setProgress(0);

        Data data = new Data(this,2);
        ArrayList<double[][]> d = new ArrayList<>();
        d.add(data.readTrainingFile());
//        new KmeansCluster(15,2,d,3).iterRun();

        updatePlayer(AudioStreamPlayer.State.Stopped);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        stop();
    }

    private void updatePlayer(AudioStreamPlayer.State state)
    {
        switch (state)
        {
            case Stopped:
            {
                if (mProgressDialog != null)
                {
                    mProgressDialog.cancel();
                    mProgressDialog.dismiss();

                    mProgressDialog = null;
                }
                mPlayButton.setSelected(false);
                mPlayButton.setText("Play");

                mTextCurrentTime.setText("00:00");
                mTextDuration.setText("00:00");

                mSeekProgress.setMax(0);
                mSeekProgress.setProgress(0);

                break;
            }
            case Prepare:
            case Buffering:
            {
                if (mProgressDialog == null)
                {
                    mProgressDialog = new ProgressDialog(this);
                }
                mProgressDialog.show();

                mPlayButton.setSelected(false);
                mPlayButton.setText("Play");

                mTextCurrentTime.setText("00:00");
                mTextDuration.setText("00:00");
                break;
            }
            case Pause:
            {
                break;
            }
            case Playing:
            {
                if (mProgressDialog != null)
                {
                    mProgressDialog.cancel();
                    mProgressDialog.dismiss();

                    mProgressDialog = null;
                }
                mPlayButton.setSelected(true);
                mPlayButton.setText("Pause");
                break;
            }
        }
    }

    private void pause()
    {
        if (this.mAudioPlayer != null)
        {
            this.mAudioPlayer.pause();
        }
    }

    public static String getFilename(String title) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "Music");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = (title == null | title.equals("")) ? String.valueOf(System.currentTimeMillis()) : title;
        return (file.getAbsolutePath() + "/" + fileName +
                ".wav");
    }


    private void play()
    {
        releaseAudioPlayer();

        mAudioPlayer = new AudioStreamPlayer();
        mAudioPlayer.setOnAudioStreamInterface(this);


        mAudioPlayer.setUrlString(getFilename("희진정환1"));

//        mAudioPlayer.setUrlString(getFilename("태원정환"));
        try
        {
            mAudioPlayer.play();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void releaseAudioPlayer()
    {
        if (mAudioPlayer != null)
        {
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer = null;

        }
    }

    private void stop()
    {
        if (this.mAudioPlayer != null)
        {
            this.mAudioPlayer.stop();
        }
    }

    @Override
    public void onAudioPlayerStart(AudioStreamPlayer player)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                updatePlayer(AudioStreamPlayer.State.Playing);
            }
        });
    }

    @Override
    public void onAudioPlayerStop(AudioStreamPlayer player)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                updatePlayer(AudioStreamPlayer.State.Stopped);
            }
        });

    }

    @Override
    public void onAudioPlayerError(AudioStreamPlayer player)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                updatePlayer(AudioStreamPlayer.State.Stopped);
            }
        });

    }

    @Override
    public void onAudioPlayerBuffering(AudioStreamPlayer player)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                updatePlayer(AudioStreamPlayer.State.Buffering);
            }
        });

    }

    @Override
    public void onAudioPlayerDuration(final int totalSec)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (totalSec > 0)
                {
                    int min = totalSec / 60;
                    int sec = totalSec % 60;

                    mTextDuration.setText(String.format("%02d:%02d", min, sec));

                    mSeekProgress.setMax(totalSec);
                }
            }

        });
    }

    @Override
    public void onAudioPlayerCurrentTime(final int sec)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!isSeekBarTouch)
                {
                    int m = sec / 60;
                    int s = sec % 60;

                    mTextCurrentTime.setText(String.format("%02d:%02d", m, s));

                    mSeekProgress.setProgress(sec);
                }
            }
        });
    }

    @Override
    public void onAudioPlayerPause(AudioStreamPlayer player)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mPlayButton.setText("Play");
            }
        });
    }

    private boolean isSeekBarTouch = false;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        this.isSeekBarTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        this.isSeekBarTouch = false;

        int progress = seekBar.getProgress();

        this.mAudioPlayer.seekTo(progress);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_play:
            {
                if (mPlayButton.isSelected())
                {
                    if (mAudioPlayer != null && mAudioPlayer.getState() == AudioStreamPlayer.State.Pause)
                    {
                        mAudioPlayer.pauseToPlay();
                    }
                    else
                    {
                        pause();
                    }
                }
                else
                {
                    play();
                }
                break;
            }
            case R.id.button_stop:
            {
                stop();
                break;
            }
        }
    }

}