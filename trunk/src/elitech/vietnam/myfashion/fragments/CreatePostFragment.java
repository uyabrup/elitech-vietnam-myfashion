/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.io.File;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.controllers.AppController.ResultListener;
import elitech.vietnam.myfashion.dialogues.LoadingDialog;
import elitech.vietnam.myfashion.dialogues.TakePhotoActionDialog;
import elitech.vietnam.myfashion.dialogues.TakePhotoActionDialog.TakePhotoAction;
import elitech.vietnam.myfashion.utilities.Utilities;
import elitech.vietnam.myfashion.widgets.ScaleImageView;
import elitech.vietnam.myfashion.wsclient.ImageUploader;

/**
 * @author Cong
 */
public class CreatePostFragment extends AbstractFragment implements View.OnClickListener,
	TakePhotoAction, ResultListener {
	
	private static final String POST_DIR = "images/post/";
	
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	ScaleImageView	mImage;
	EditText mEdtDes;
	Button mBtnTake;
	View mLayoutMain;
	
	int mMemberId;
	String mImagePath = null;
	
	public static CreatePostFragment newInstance(int memberId) {
		Bundle args = new Bundle();
		CreatePostFragment fragment = new CreatePostFragment();
		args.putInt(ARG_MEMBERID, memberId);
		fragment.setArguments(args);
		return fragment;
	}

	public CreatePostFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.getController().setResultListener(this);
		mActivity.getActionBar().setTitle(R.string.title_newstyle);
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		View view = inflater.inflate(R.layout.fragment_createpost, container, false);
		
		mLayoutMain = view.findViewById(R.id.createpost_scrMainView);
		mBtnTake = (Button) view.findViewById(R.id.createpost_btnTakePhoto);
		mEdtDes = (EditText) view.findViewById(R.id.createpost_edtContent);
		mImage = (ScaleImageView) view.findViewById(R.id.createpost_imgPhoto);
		
		mBtnTake.setOnClickListener(this);
		mImage.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		mActivity.getController().setResultListener(null);
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		for (int i=0; i<menu.size(); i++)
			menu.getItem(i).setVisible(false);
		
		inflater.inflate(R.menu.create_post, menu);
		
		final MenuItem save = menu.findItem(R.id.action_savePost);
		
		View view = null;
		view = MenuItemCompat.getActionView(save).findViewById(R.id.menuitem_action_savePost);
	    view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onOptionsItemSelected(save);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_savePost:
			savePost();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createpost_btnTakePhoto:
		case R.id.createpost_imgPhoto:
			TakePhotoActionDialog.newInstance(Const.REQUEST_DIALOG_TAKEPICPOST, this).show(getFragmentManager());
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onCameraSelected() {
		mImagePath = null;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(mActivity.getConfig().getImageTemp())));
		mActivity.getCurrentBase().startActivityForResult(intent, Const.REQUEST_POSTCAMERA);
	}

	@Override
	public void onLibrarySelected() {
		mImagePath = null;
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		mActivity.getCurrentBase().startActivityForResult(intent, Const.REQUEST_POSTLIBRARY);
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		if (mActivity.getController().mRequest != 0) {
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					onResult(mActivity.getController().mRequest, mActivity.getController().mResult, mActivity.getController().mData);
//				}
//			}, 200);
//		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);Log.w("onResult", System.identityHashCode(this) + "");
		onResult(requestCode, resultCode, data);
//		Log.w("onResult", "isAdded " + isAdded() + "; isDetached "  + isDetached() + "; isHidden " + isHidden() + "; isInLayout " + isInLayout() + "; isRemoving " + isRemoving() + "; isResumed " + isResumed() + "; isVisible " + isVisible());
	}
	
	@Override
	public void onResult(int requestCode, int resultCode, Intent data) {
		final int result = resultCode;
		final int mRequest = requestCode & 0xffff;
		final Intent mData = data;
		
		Log.w("onResult", mRequest + "/" + result);
		if (result == MainActivity.RESULT_CANCELED)
			return;

		new AsyncTask<Integer, Integer, String>() {
			@Override
			protected String doInBackground(Integer... params) {
				switch (mRequest) {
				case Const.REQUEST_POSTLIBRARY:
					Uri img = mData.getData();
					String path = "";
					if (img.toString().startsWith("content://com.android.gallery3d.provider"))
						img = Uri.parse(img.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
					path = mActivity.getRealPathFromURI(img);
					path = (path == null || path.length() == 0) ? img.toString() : path;
					if (URLUtil.isContentUrl(path) || URLUtil.isHttpsUrl(path) || URLUtil.isHttpUrl(path))
						return Utilities.downloadImage(mActivity, path, mActivity.getConfig().getImageTemp());
					else 
						return Utilities.copyFile(mActivity.getRealPathFromURI(img), mActivity.getConfig().getImageTemp());
				case Const.REQUEST_POSTCAMERA:
					return Uri.fromFile(new File(mActivity.getConfig().getImageTemp())).getPath();
				default:
					return null;
				}
			}
			@Override
			protected void onPostExecute(String result) {
				if (result == null || result.length() == 0) {
					Log.w("result == null", "result == null");
					Toast.makeText(mActivity, R.string.cannottakephoto, Toast.LENGTH_SHORT).show();
					return;
				}
				
				System.gc();
				int rotate = Utilities.getImageRotation(result);
				if (rotate != 0 && !Utilities.rotateImage(result, rotate)) {
					Log.w("rotate != 0", "rotate != 0");
					Toast.makeText(mActivity, R.string.cannottakephoto, Toast.LENGTH_SHORT).show();
					return;
				}

				if (!displayContentImage(result)) {
					Log.w("displayContentImage", "displayContentImage");
					Toast.makeText(mActivity, R.string.cannottakephoto, Toast.LENGTH_SHORT).show();
					return;
				}
				
				mBtnTake.setVisibility(View.GONE);
				mLayoutMain.setVisibility(View.VISIBLE);
				mImagePath = result;
				mEdtDes.requestFocus();
				Log.w("mImagePath", mImagePath);
			}
		}.execute();		
	}
	
	private boolean displayContentImage(String path) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int width = options.outWidth;
			int sampleSize = 1;
			while (width/sampleSize > mActivity.getConfig().getScreenWidth())
				sampleSize *= 2;
			options.inSampleSize = sampleSize;
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			Log.w("displayContentImage", bitmap.getWidth() + "x" + bitmap.getHeight());
			mImage.setImageBitmap(bitmap);
			return true;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void savePost() {
		if (mImagePath == null || mImagePath.length() < 1) {
			Toast.makeText(mActivity, R.string.youmusttakephotoupload, Toast.LENGTH_SHORT).show();
			return;
		}
		
		new AsyncTask<Integer, Integer, Boolean>() {
			LoadingDialog mDialog;
			String name = mActivity.getLoggedinUser().Id + "_" + System.currentTimeMillis() + ".jpg";
			String image = POST_DIR + name;
			@Override
			protected void onPreExecute() {
				mDialog = LoadingDialog.newInstance(getString(R.string.uploadphoto), getString(R.string.pleasewaituploadingphoto));
				mDialog.show(getFragmentManager());
			}
			@Override
			protected Boolean doInBackground(Integer... params) {
				return new ImageUploader().upload(mImagePath, name, null);
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					String title = mActivity.getLoggedinUser().Id + "_" + mActivity.getLoggedinUser().Name + " Style " + new Date().toString();
					mActivity.getServices().createStyle(mMemberId, title, image, mEdtDes.getText().toString().trim(), new Callback<Integer>() {
						@Override
						public void success(Integer arg0, Response arg1) {
							if (arg0 > 0) {
								Toast.makeText(mActivity, R.string.postcreated, Toast.LENGTH_SHORT).show();
								mDialog.dismiss();
								mActivity.onBackPressed();
							} else {
								Toast.makeText(mActivity, R.string.servererror, Toast.LENGTH_SHORT).show();
								mDialog.dismiss();
							}
						}
						@Override
						public void failure(RetrofitError arg0) {
							Log.w("RetrofitError", arg0.getMessage());
							mDialog.dismiss();
						}
					});
				}
				mDialog.dismiss();
			};
		}.execute();
	}
}
