package org.bottiger.podcast;

import org.bottiger.podcast.R;
import org.bottiger.podcast.R.id;
import org.bottiger.podcast.R.layout;
import org.bottiger.podcast.adapters.CompactListCursorAdapter;
import org.bottiger.podcast.adapters.ItemCursorAdapter;
import org.bottiger.podcast.playlist.PlaylistCursorLoader;
import org.bottiger.podcast.playlist.SubscriptionCursorLoader;
import org.bottiger.podcast.provider.ItemColumns;
import org.bottiger.podcast.provider.Subscription;
import org.bottiger.podcast.service.PodcastService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedFragment extends AbstractEpisodeFragment {

	public static final String subscriptionIDKey = "subscription_id";
	private static final long defaultSubscriptionID = 1;

	private long subId = 14;
	private Subscription mSubscription = null;

	private ViewGroup header;

	private void setSubscription(Subscription subscription) {
		mSubscription = subscription;
	}

	public static CursorAdapter listItemCursorAdapter(Activity activity,
			PodcastBaseFragment fragment, Cursor cursor) {
		CompactListCursorAdapter.FieldHandler[] fields = {
				CompactListCursorAdapter.defaultTextFieldHandler,
				new CompactListCursorAdapter.TextFieldHandler(),
				new ItemCursorAdapter.TextFieldHandler(),
				new CompactListCursorAdapter.IconFieldHandler(mIconMap), };
		return new CompactListCursorAdapter(activity,
				R.layout.episode_list_compact, cursor, new String[] {
						ItemColumns.TITLE, ItemColumns.SUB_TITLE,
						ItemColumns.DURATION, ItemColumns.IMAGE_URL },
				new int[] { R.id.title, R.id.podcast, R.id.duration,
						R.id.list_image }, fields);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		header = (ViewGroup) inflater.inflate(R.layout.podcast_header, null);
		Subscription subscription = mSubscription;

		if (subscription != null)
			setHeader(header, subscription);

		// fragmentView = inflater.inflate(R.layout.subscription_list,
		// container, false);
		fragmentView = inflater.inflate(R.layout.recent_new, container, false);
		Intent intent = getActivity().getIntent();
		intent.setData(ItemColumns.URI);

		getPref();
		return fragmentView;
	}

	private void setHeader(ViewGroup header, Subscription subscription) {

		TextView title = (TextView) header.findViewById(R.id.title);
		ImageView icon = (ImageView) header.findViewById(R.id.podcast_cover);

		title.setText(subscription.title);
		ImageLoader imageLoader = ((CompactListCursorAdapter) getAdapter(mCursor))
				.getImageLoader(getActivity());

		if (subscription.imageURL != null && !subscription.imageURL.equals("")) {
			imageLoader.displayImage(subscription.imageURL, icon);
		}
	}

	// Read here:
	// http://developer.android.com/reference/android/app/Fragment.html#Layout
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (header != null)
			this.getListView().addHeaderView(header);

		mAdapter = this.getAdapter(mCursor);
		new SubscriptionCursorLoader(this, mAdapter, mSubscription);
		setListAdapter(mAdapter);

		/*
		 * mAdapter = FeedFragment.listItemCursorAdapter(this.getActivity(),
		 * this, mCursor); String where = getWhere(); String order = getOrder();
		 * startInit(1, ItemColumns.URI, ItemColumns.ALL_COLUMNS, where, order);
		 * enablePullToRefresh(mSubscription);
		 */
	}

	public CursorAdapter createAdapter(Activity activity, Cursor cursor) {
		return listItemCursorAdapter(activity, this, cursor);
	}

	public static FeedFragment newInstance(Subscription subscription) {
		FeedFragment fragment = new FeedFragment();
		fragment.mSubscription = subscription;

		return fragment;
	}

	public void refreshCursor() {
		startInit(1, ItemColumns.URI, ItemColumns.ALL_COLUMNS, getWhere(),
				getOrder());
	}

	@Override
	public String getWhere() {
		String where = ItemColumns.SUBS_ID + "=" + mSubscription.getId();
		return where;
	}

	/*
	public void showEpisodes(String condition) {
		mCursor = createCursor(condition, getOrder());

		mAdapter = FeedFragment.listItemCursorAdapter(this.getActivity(), this,
				mCursor);

		setListAdapter(mAdapter);
	}
	*/

	@Override
	public int getItemLayout() {
		return R.layout.episode_list_compact;
	}

	@Override
	View getPullView() {
		return getListView();
	}
}
