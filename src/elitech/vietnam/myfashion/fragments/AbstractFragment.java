/**
 * May 2, 2014 11:20:55 AM
 */
package elitech.vietnam.myfashion.fragments;

import elitech.vietnam.myfashion.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * @author Cong
 *
 */
public class AbstractFragment extends Fragment {
	
	protected View mLoading;
	protected View mNodata;
	protected View mMain;
	
	Animation mShowAnim;
	
	protected MainActivity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.w(getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		mShowAnim = AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.w(getClass().getSimpleName(), "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		Log.w(getClass().getSimpleName(), "onAttach");
		super.onAttach(activity);
	}
	
	@Override
	public void onDestroy() {
		Log.w(getClass().getSimpleName(), "onDestroy");
		mShowAnim = null;
		super.onDestroy();
	}
	
	@Override
	public void onDestroyView() {
		Log.w(getClass().getSimpleName(), "onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDetach() {
		Log.w(getClass().getSimpleName(), "onDetach");
		super.onDetach();
	}
	
	@Override
	public void onPause() {
		Log.w(getClass().getSimpleName(), "onPause");
		super.onPause();
	}
	
	@Override
	public void onResume() {
		Log.w(getClass().getSimpleName(), "onResume");
		super.onResume();
	}
	
	@Override
	public void onStart() {
		Log.w(getClass().getSimpleName(), "onStart");
		super.onStart();
	}
	
	@Override
	public void onStop() {
		Log.w(getClass().getSimpleName(), "onStop");
		super.onStop();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.w(getClass().getSimpleName(), "onViewCreated");
//		super.onViewCreated(view, savedInstanceState);
//		mMain = getView().findViewById(R.id.mainLayout);
//		mLoading = getView().findViewById(R.id.prgLoading);
//		mNodata = getView().findViewById(R.id.txtNodata);
//		if (mMain == null || mLoading == null || mNodata == null)
//			throw new IllegalArgumentException("Your layout doesn't support loading and nodata");
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.w(getClass().getSimpleName(), "onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(getClass().getSimpleName(), "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void showLoading() {
		mMain.setVisibility(View.GONE);
		mLoading.setVisibility(View.VISIBLE);
		mNodata.setVisibility(View.GONE);
	}
	
	public void showText(String text) {
		mMain.setVisibility(View.GONE);
		mNodata.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);
		((TextView) mNodata).setText(text);
	}
	
	public void showMain() {
		mMain.startAnimation(mShowAnim);
		mMain.setVisibility(View.VISIBLE);
		mNodata.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
	}
}
