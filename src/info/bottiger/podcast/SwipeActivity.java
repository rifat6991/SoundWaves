package info.bottiger.podcast;

import info.bottiger.podcast.RecentItemFragment.OnEpisodeSelectedListener;
import info.bottiger.podcast.provider.ItemColumns;
import info.bottiger.podcast.utils.GoogleReader;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SwipeActivity extends FragmentActivity {

	private static final int MENU_SETTINGS = Menu.FIRST + 1;
	private static final int MENU_REFRESH = Menu.FIRST + 2;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter; // FIXME not static

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		Account[] a = AccountManager.get(getApplicationContext())
				.getAccountsByType("com.google");
		GoogleReader agr = new GoogleReader();
		agr.refreshAuthToken(SwipeActivity.this, a[0]);
		agr.getSubscriptionsFromReader();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_swipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			// mServiceBinder.start_update();
			return true;
		case MENU_REFRESH:
			//mServiceBinder.start_update();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class RecentItemFragment extends ListFragment {
		OnEpisodeSelectedListener mListener;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				mListener = (OnEpisodeSelectedListener) activity;
				int i = 5;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement OnArticleSelectedListener");
			}
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment;
			if (i == 1) {
				fragment = new SubscriptionsFragment();
			} else if (i == 0) {
				fragment = new RecentItemFragment();
			} else {
				fragment = new DummySectionFragment();
			}
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section3).toUpperCase();
			case 1:
				return getString(R.string.title_section1).toUpperCase();
			case 2:
				return getString(R.string.title_section2).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			Bundle args = getArguments();
			textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
			return textView;
		}
	}
}
