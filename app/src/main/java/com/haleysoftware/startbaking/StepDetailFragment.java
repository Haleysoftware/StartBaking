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
 * This fragment displays the details of one step of the recipe.
 * If a video or image URL is present, it displays it.
 * <p>
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

    /**
     * The fragment has been attached to an activity.
     *
     * @param context The context of the attached activity.
     */
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

    /**
     * Creates and sets up the views of the fragment.
     *
     * @param inflater           The layout inflater for the fragment.
     * @param container          The view container for the fragment.
     * @param savedInstanceState The saved instance from the system.
     * @return The view that was inflated to display.
     */
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

    /**
     * The attached activity has been created.
     * Need to start the media session and player.
     * Collect the
     *
     * @param savedInstanceState The saved instance from the system.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeMediaSession();

        if (!stepItem.getImage().isEmpty()) {
            initializePlayer(stepItem.getImage());
        } else if (!stepItem.getVideo().isEmpty()) {
            initializePlayer(stepItem.getVideo());
        } else {
            exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_video));
            initializePlayer("");
        }

        // If the previous button is active, display the description else the ingredients.
        if (previousActive) {
            direction.setText(stepItem.getDescription());
        } else {
            direction.setText(ingredients);
        }
    }

    /**
     * The fragment is being destroyed.
     * The player and session need to be turned off.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mediaSession.setActive(false);
    }

    /**
     * Hides or shows views if the device is in landscape or not.
     * Only used on phones.
     *
     * @param isLandscape True if phone is in landscape.
     */
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

    /**
     * Sets the step item to be displayed.
     *
     * @param stepItem The current step item.
     */
    public void setStepItem(StepItem stepItem) {
        this.stepItem = stepItem;
    }

    /**
     * Sets the ingredient string that is displayed on the first step.
     *
     * @param ingredients The recipe ingredients in string format.
     */
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Controls weather the next and previous buttons should be active or not.
     * Prevents going below 0 and over the size of the list.
     *
     * @param id   The ID of the current recipe step.
     * @param size The size of the recipe step list.
     */
    public void setButtonState(int id, int size) {
        if (id == 0) {
            this.nextActive = true;
            this.previousActive = false;
        } else if (id == size - 1) {
            this.nextActive = false;
            this.previousActive = true;
        } else {
            this.nextActive = true;
            this.previousActive = true;
        }
    }

    //************************* Exo Player *************************

    /**
     * Creates the media session.
     */
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

    /**
     * Sets the player up and loads the content.
     *
     * @param url The URL for the video.
     */
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

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory).createMediaSource(uri);

            exoPlayer.prepare(mediaSource);

        }
    }

    /**
     * Releases the player when the fragment is going away.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.removeListener(this);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    // Not used
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    // Not used
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    // Not used
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Triggers when the exo player changes state and handles the state change.
     *
     * @param playWhenReady If video should start playing when loaded.
     * @param playbackState The current state of the player.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
        }
        mediaSession.setPlaybackState(mStateBuilder.build());
    }

    // Not used
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    // Not used
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    // Not used
    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    // Not used
    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    // Not used
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    // Not used
    @Override
    public void onSeekProcessed() {

    }

    /**
     * Handles the media player button calls.
     */
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
