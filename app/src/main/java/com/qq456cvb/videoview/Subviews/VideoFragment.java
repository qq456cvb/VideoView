package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.R;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by qq456cvb on 8/17/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback, IVideoPlayer{

    private final static String TAG = "VideoFragment";
    private int isFullscreen = 0;
    private String              urlToStream;
    private View view;
    // Display Surface
    private LinearLayout vlcContainer;
    private SurfaceView mSurface;
    private SurfaceHolder       holder;

    // Overlay / Controls

    private FrameLayout vlcOverlay;
    private ImageView vlcButtonPlayPause;
    private ImageView vlcButtonToggleFullscreen;
    private ImageView vlcButtonSnapshot;
    private Handler handlerOverlay;
    private Runnable runnableOverlay;
    private Handler handlerSeekbar;
    private Runnable runnableSeekbar;
    private SeekBar vlcSeekbar;
    private SeekBar vlcVolume;
    private TextView vlcDuration;
    private TextView overlayTitle;

    // media player
    private LibVLC libvlc;
    private int mVideoWidth;
    private int mVideoHeight;
    public final static int VideoSizeChanged = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        urlToStream = getResources().getString(R.string.default_url);
        view = inflater.inflate(R.layout.video, container, false);

        // VLC
        vlcContainer = (LinearLayout) view.findViewById(R.id.vlc_container);
        mSurface = (SurfaceView) view.findViewById(R.id.vlc_surface);


        // OVERLAY / CONTROLS
        vlcOverlay = (FrameLayout) view.findViewById(R.id.vlc_overlay);
        vlcButtonPlayPause = (ImageView) view.findViewById(R.id.vlc_button_play_pause);
        vlcButtonToggleFullscreen = (ImageView) view.findViewById(R.id.vlc_button_fullscreen);
        vlcSeekbar = (SeekBar) view.findViewById(R.id.vlc_seekbar);
        vlcVolume = (SeekBar) view.findViewById(R.id.vlc_volume);
        vlcDuration = (TextView) view.findViewById(R.id.vlc_duration);
        vlcDuration.setVisibility(View.INVISIBLE);
        vlcSeekbar.setVisibility(View.INVISIBLE);
        vlcButtonSnapshot = (ImageView)view.findViewById(R.id.vlc_button_snapshot);

        overlayTitle = (TextView) view.findViewById(R.id.vlc_overlay_title);
        overlayTitle.setText(urlToStream);
        overlayTitle.setVisibility(View.INVISIBLE);

        // AUTOSTART
        playMovie();
        return view;
    }


    public void togglePlay(boolean play) {
        Log.d("Video", String.valueOf(play));
        if (!play && libvlc != null) {
//            libvlc.detachSurface();
            if (libvlc.isPlaying()) {
                libvlc.pause();
                vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_over_video));
            }
        } else if (play && libvlc != null) {
            if (!libvlc.isPlaying()) {
                libvlc.play();
                vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause_over_video));
            }
        }
    }

    public boolean isPlaying() {
        return libvlc.isPlaying();
    }

    private void showOverlay() {
        vlcOverlay.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        vlcOverlay.setVisibility(View.GONE);
    }

    private void setupControls() {
        // PLAY PAUSE
        vlcButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (libvlc.isPlaying()) {
                    libvlc.pause();
                    vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_over_video));
                } else {
                    libvlc.play();
                    vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause_over_video));
                }
            }
        });

        vlcButtonSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sdDir = null;
                long length = 0;
                String localPath = "";
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist)
                {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                    String dateNow;
                    SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");
                    dateNow = sdf.format(Calendar.getInstance().getTime());
                    localPath = sdDir.toString() + "/" + GlobalApp.currentChannel.getName() + dateNow + ".jpg";
                } else {
                    //TODO
                }
                libvlc.takeSnapShot(localPath, 800, 600);
                Toast.makeText(VideoFragment.this.getActivity(), "已保存至" + localPath, Toast.LENGTH_SHORT).show();
            }
        });

        vlcButtonToggleFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                libvlc.detachSurface();
//                libvlc.attachSurface(((MainActivity)VideoFragment.this.getActivity()).mVideoFullscreen.getHolder().getSurface(), VideoFragment.this);
                Message msg = Message.obtain(MainActivity.handler);
                msg.what = MainActivity.TOGGLE_FULLSCREEN;
                isFullscreen = isFullscreen ^ 1;
                msg.arg1 = isFullscreen;
                msg.sendToTarget();
            }
        });
        // SEEKBAR
        handlerSeekbar = new Handler();
        runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                if (libvlc != null) {
                    long curTime = libvlc.getTime();
                    long totalTime = (long) (curTime / libvlc.getPosition());
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);
                    int endMinutes = (int) (totalTime / (60 * 1000));
                    int endSeconds = (int) ((totalTime / 1000) % 60);
                    String duration = String.format("%02d:%02d / %02d:%02d", minutes, seconds, endMinutes, endSeconds);
                    vlcSeekbar.setProgress((int) (libvlc.getPosition() * 100));
                    vlcDuration.setText(duration);
                }
                handlerSeekbar.postDelayed(runnableSeekbar, 1000);
            }
        };

        final long timeToDisappear = 3000;
        runnableSeekbar.run();
        vlcSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("NEW POS", "pos is : " + i);
                if (i != 0 && b)
                    libvlc.setPosition(((float) i / 100.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handlerOverlay.removeCallbacks(runnableOverlay);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });

        vlcVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("NEW Volume", "pos is : " + progress);
                libvlc.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handlerOverlay.removeCallbacks(runnableOverlay);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });

        // OVERLAY
        handlerOverlay = new Handler();
        runnableOverlay = new Runnable() {
            @Override
            public void run() {
                vlcOverlay.setVisibility(View.GONE);
            }
        };

        handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
        vlcContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!vlcOverlay.isShown()) {
                    vlcOverlay.setVisibility(View.VISIBLE);
                    handlerOverlay.removeCallbacks(runnableOverlay);
                    handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
                } else {
                    vlcOverlay.setVisibility(View.GONE);
                }
            }
        });
    }

    public void playMovie() {
        if (libvlc != null && libvlc.isPlaying())
            return ;
        vlcContainer.setVisibility(View.VISIBLE);
        holder = mSurface.getHolder();
        holder.addCallback(this);
        createPlayer(urlToStream);
    }


    private void toggleFullscreen(boolean fullscreen)
    {
        WindowManager.LayoutParams attrs = VideoFragment.this.getActivity().getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            vlcContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        VideoFragment.this.getActivity().getWindow().setAttributes(attrs);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

//    @Override
    public void resume() {
//        super.onResume();
        if (!libvlc.isPlaying()) {
            libvlc.play();
            vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause_over_video));
        }
    }

//    @Override
    public void pause() {
//        super.onPause();
        if (libvlc.isPlaying()) {
            libvlc.pause();
            vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_over_video));
        }
        //releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /**
     * **********
     * Surface
     * ***********
     */

    public void surfaceCreated(SurfaceHolder holder) {
        if (libvlc != null)
            libvlc.attachSurface(holder.getSurface(), this);
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                               int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    }

    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        // get screen size
        int w = view.getWidth();
        int h = view.getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        if (holder != null)
            holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width,
                               int visible_height, int sar_num, int sar_den) {
        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
        msg.sendToTarget();
    }

    /**
     * **********
     * Player
     * ***********
     */

    private void createPlayer(String media) {
        releasePlayer();
        setupControls();
        try {
            if (media.length() > 0) {
//                Toast toast = Toast.makeText(VideoFragment.this.getActivity(), media, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
//                        0);
//                toast.show();
            }
            // Create a new media player
            libvlc = LibVLC.getInstance();
//            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_AUTOMATIC);
//            libvlc.eventVideoPlayerActivityCreated(true);
            libvlc.setSubtitlesEncoding("");
            libvlc.setAout(LibVLC.AOUT_OPENSLES);
            libvlc.setChroma("RV32");
            LibVLC.restart(view.getContext());
//            libvlc.setNetworkCaching(20000);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
            libvlc.setVolume(50);
            libvlc.playIndex(0);
        } catch (Exception e) {
            Toast.makeText(VideoFragment.this.getActivity(), "Could not create Vlc Player", Toast.LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        if (handlerSeekbar != null && runnableSeekbar != null)
            handlerSeekbar.removeCallbacks(runnableSeekbar);
        EventHandler.getInstance().removeHandler(mHandler);
        if (libvlc == null)
            return;
        libvlc.stop();
        libvlc.clearBuffer();
        libvlc.getMediaList().clear();
        libvlc.detachSurface();
        holder = null;
        libvlc.closeAout();
        libvlc.destroy();

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    public void changeSrc(String src) {
        if (src.contains("upload") || src.contains("VIDEO")) {
            vlcDuration.setVisibility(View.VISIBLE);
            vlcSeekbar.setVisibility(View.VISIBLE);
        } else {
            vlcDuration.setVisibility(View.INVISIBLE);
            vlcSeekbar.setVisibility(View.INVISIBLE);
        }
        if (libvlc.isPlaying()) {
            libvlc.pause();
        }
        libvlc.stop();
        libvlc.clearBuffer();
//        LibVLC.restart(view.getContext());
        libvlc.getMediaList().clear();
        libvlc.getMediaList().add(new Media(libvlc, LibVLC.PathToURI(src)), false);
        libvlc.playIndex(0);
//        libvlc.pause();
        overlayTitle.setText(src);
    }

    /**
     * **********
     * Events
     * ***********
     */

    private static class MyHandler extends Handler {
        private WeakReference<VideoFragment> mOwner;

        public MyHandler(VideoFragment owner) {
            mOwner = new WeakReference<VideoFragment>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoFragment player = mOwner.get();

            // Player events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
                case EventHandler.MediaPlayerEndReached:
                    player.releasePlayer();
                    break;
                case EventHandler.MediaPlayerPlaying:
                case EventHandler.MediaPlayerPaused:
                case EventHandler.MediaPlayerStopped:
                case EventHandler.MediaPlayerPositionChanged:
//                    Log.d(TAG, "Position changed!");
                    break;
//                case EventHandler.MediaPlayerBuffering:
//                    Log.d(TAG, "MediaPlayerBuffering");
//                    break;
                default:
                    break;
            }
        }
    }

    public Handler mHandler = new MyHandler(this);

}
