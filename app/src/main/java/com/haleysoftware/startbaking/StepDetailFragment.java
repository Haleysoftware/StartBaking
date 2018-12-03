package com.haleysoftware.startbaking;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.haleysoftware.startbaking.utils.StepItem;


import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haleysoft on 11/16/18.
 */
public class StepDetailFragment extends Fragment implements Player.EventListener {

    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    @BindView(R.id.b_next)
    Button nextButton;
    @BindView(R.id.b_previous)
    Button previousButton;

    @BindView(R.id.playerView)
    SimpleExoPlayerView exoPlayerView;

    @BindView(R.id.scrolling_text)
    ScrollView scrollText;


    @BindView(R.id.tv_direction)
    TextView direction;

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    private boolean nextActive;
    private boolean previousActive;
    private StepItem stepItem;
    private String ingredients;
    private OnButtonClickListener buttonCall;

    private static MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer exoPlayer;

    public interface OnButtonClickListener {
        void onButtonPress(int buttonId);
    }

    // Mandatory empty constructor
    public StepDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            buttonCall = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);


        if (isTablet) {
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
        } else {
            nextButton.setEnabled(nextActive);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonCall.onButtonPress(R.id.b_next);
                }
            });
            previousButton.setEnabled(previousActive);
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonCall.onButtonPress(R.id.b_previous);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeMediaSession();

        if (!stepItem.getImage().isEmpty()) {
            initializePlayer(stepItem.getImage());
        }else if (!stepItem.getVideo().isEmpty()) {
            initializePlayer(stepItem.getVideo());
        } else {
            exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_video));
            initializePlayer("");
        }

        if (previousActive) {
            direction.setText(stepItem.getDescription());
        } else {
            direction.setText(ingredients);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mediaSession.setActive(false);
    }

    public void fullscreenMode(boolean isLandscape) {
        if (isLandscape) {
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
            scrollText.setVisibility(View.GONE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
            scrollText.setVisibility(View.VISIBLE);
        }
    }

    public void setStepItem(StepItem stepItem) {
        this.stepItem = stepItem;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setButtonState(int id, int size) {
        if (id == 0) {
            this.nextActive = true;
            this.previousActive = false;
        } else if (id == size-1) {
            this.nextActive = false;
            this.previousActive = true;
        } else {
            this.nextActive = true;
            this.previousActive = true;
        }
    }

    //************************* Exo Player *************************

    private void initializeMediaSession() {
        if (getContext() != null) {

            // Create a MediaSessionCompat.
            mediaSession = new MediaSessionCompat(getContext(), TAG);

            // Enable callbacks from MediaButtons and TransportControls.
            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            // Do not let MediaButtons restart the player when the app is not visible.
            mediaSession.setMediaButtonReceiver(null);

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
            mStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);

            mediaSession.setPlaybackState(mStateBuilder.build());


            // MySessionCallback has methods that handle callbacks from a media controller.
            mediaSession.setCallback(new MySessionCallback());

            // Start the Media Session since the activity is active.
            mediaSession.setActive(true);
        }
    }

    private void initializePlayer(String url) {
        if (getContext() != null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            exoPlayer.setPlayWhenReady(true);

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayerView.hideController();

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                     "StartBaking");

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            Uri uri = Uri.parse(url);

//            MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory).createMediaSource(uri);

            exoPlayer.prepare(mediaSource);

        }
    }


    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.removeListener(this);
            exoPlayer.release();
            exoPlayer = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
        }
        mediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }
}
