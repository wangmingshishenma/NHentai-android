package moe.feng.nhentai.ui.fragment.main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import moe.feng.nhentai.R;
import moe.feng.nhentai.dao.FavoriteCategoriesManager;
import moe.feng.nhentai.ui.CategoryActivity;
import moe.feng.nhentai.ui.adapter.FavoriteCategoriesRecyclerAdapter;
import moe.feng.nhentai.ui.common.AbsRecyclerViewAdapter;
import moe.feng.nhentai.ui.common.LazyFragment;
import moe.feng.nhentai.util.AsyncTask;

public class FavoriteCategoryFragment extends LazyFragment {

	private RecyclerView mRecyclerView;
	private FavoriteCategoriesRecyclerAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private FavoriteCategoriesManager mFCM;

	public static final String TAG = FavoriteCategoryFragment.class.getSimpleName();

	@Override
	public int getLayoutResId() {
		return R.layout.fragment_home;
	}

	@Override
	public void finishCreateView(Bundle state) {
		mFCM = FavoriteCategoriesManager.getInstance(getApplicationContext());

		mSwipeRefreshLayout = $(R.id.swipe_refresh_layout);
		mRecyclerView = $(R.id.recycler_view);
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		mRecyclerView.setHasFixedSize(false);

		mAdapter = new FavoriteCategoriesRecyclerAdapter(mRecyclerView, mFCM);
		setRecyclerViewAdapter(mAdapter);

		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.deep_purple_500, R.color.pink_500, R.color.orange_500, R.color.brown_500,
				R.color.indigo_500, R.color.blue_500, R.color.teal_500, R.color.green_500
		);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (!mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.setRefreshing(true);
				}
				new FavoritesRefreshTask().execute();
			}
		});
	}

	private void setRecyclerViewAdapter(FavoriteCategoriesRecyclerAdapter adapter) {
		mRecyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder viewHolder) {
				CategoryActivity.launch((AppCompatActivity) getActivity(), mFCM.get(position));
			}
		});
	}

	private class FavoritesRefreshTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mFCM.reload();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mSwipeRefreshLayout.setRefreshing(false);
			mAdapter = new FavoriteCategoriesRecyclerAdapter(mRecyclerView, mFCM);
			setRecyclerViewAdapter(mAdapter);
		}

	}

}
