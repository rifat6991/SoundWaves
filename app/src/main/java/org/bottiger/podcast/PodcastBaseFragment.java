package org.bottiger.podcast;

import org.bottiger.podcast.listeners.PlayerStatusObservable;
import org.bottiger.podcast.playlist.Playlist;
import org.bottiger.podcast.playlist.ReorderCursor;
import org.bottiger.podcast.provider.FeedItem;
import org.bottiger.podcast.provider.Subscription;
import org.bottiger.podcast.service.PlayerService;
import org.bottiger.podcast.service.PodcastDownloadManager;
import org.bottiger.podcast.utils.PodcastLog;
import org.bottiger.podcast.utils.StrUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public abstract class PodcastBaseFragment extends Fragment {

    protected RecyclerView currentView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected SharedPreferences sharedPreferences;

	public static PlayerService mPlayerServiceBinder = null;
	protected static ComponentName mService = null;

	protected CursorAdapter mCursorAdapter;

	protected final PodcastLog log = PodcastLog.getLog(getClass());

    private Playlist mPlaylist;

	protected ReorderCursor mCursor = null;

	private static TextView mCurrentTime = null;
	private static SeekBar mProgressBar = null;
	private static TextView mDuration = null;

	public TextView getCurrentTime() {
		return mCurrentTime;
	}

	public static void setCurrentTime(TextView mCurrentTime) {
		PodcastBaseFragment.mCurrentTime = mCurrentTime;
	}

	public SeekBar getProgress() {
		return mProgressBar;
	}

	public void setProgressBar(SeekBar mProgress) {
		PodcastBaseFragment.mProgressBar = mProgress;
	}

	public void setDuration(TextView mDuration) {
		PodcastBaseFragment.mDuration = mDuration;
	}

	public ServiceConnection playerServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mPlayerServiceBinder = ((PlayerService.PlayerBinder) service)
					.getService();
			PlayerStatusObservable.setActivity(getActivity());
			// log.debug("onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mPlayerServiceBinder = null;
			// log.debug("onServiceDisconnected");
		}
	};

    public RecyclerView getListView() {
        return currentView;
    }

	// Container Activity must implement this interface
	public interface OnItemSelectedListener {
		public void onItemSelected(long id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mService = getActivity().startService(
				new Intent(getActivity(), PlayerService.class));

		Intent bindIntent = new Intent(getActivity(), PlayerService.class);
		getActivity().bindService(bindIntent, playerServiceConnection,
				Context.BIND_AUTO_CREATE);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

        mPlaylist = new Playlist(getActivity(), 30, true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// unbindService(playerServiceConnection);
			getActivity().unbindService(playerServiceConnection);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();
	}


	public ReorderCursor getCursor() {
		return this.mCursor;
	}

	public void refreshView() {
		FeedItem.clearCache();
	}

    public Playlist getPlaylist() {
        return mPlaylist;
    }

	abstract String getWhere();

	abstract String getOrder();

	protected static String orderByFirst(String condition) {
		String priorityOrder = "case when " + condition + " then 1 else 2 end";
		return priorityOrder;
	}
}
