/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.MainActivity.ResultListener;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.dialogues.TakePhotoActionDialog.TakePhotoAction;
import elitech.vietnam.myfashion.widgets.ScaleImageView;

/**
 * @author Cong
 */
public class CreatePostFragment extends AbstractFragment implements View.OnClickListener, TakePhotoAction, ResultListener {
	
	public static final String ARG_IMAGE = "ARG_IMAGE";
	public static final String ARG_CONTENT = "ARG_CONTENT";
	public static final String ARG_TYPE = "ARG_TYPE";
	public static final String ARG_MEMBERID = "ARG_MEMBERID";
	
	ScaleImageView	mImage;
	EditText mEdtDes;

	public enum Type {
		NEW,
		EDIT
	}
	
	int mMemberId;
	String mImagePath, mContent;
	Type mType;
	
	public static CreatePostFragment newInstance(int memberId, String image, String content, Type type) {
		Bundle args = new Bundle();
		CreatePostFragment fragment = new CreatePostFragment();
		args.putInt(ARG_MEMBERID, memberId);
		args.putString(ARG_CONTENT, content);
		args.putString(ARG_IMAGE, image);
		args.putSerializable(ARG_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	public CreatePostFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_createpost, container, false);
		mMemberId = getArguments().getInt(ARG_MEMBERID);
		mType = (Type) getArguments().getSerializable(ARG_TYPE);
		mImagePath = getArguments().getString(ARG_IMAGE, "");
		mContent = getArguments().getString(ARG_CONTENT, "");
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onCameraSelected() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(mActivity.getConfig().getImageTemp())));
		startActivityForResult(intent, Const.REQUEST_EDITCAMERA);
	}

	@Override
	public void onLibrarySelected() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, Const.REQUEST_EDITLIBRARY);
	}
	
	@Override
	public void onDestroyView() {
		mActivity.getController().setResultListener(null);
		super.onDestroyView();
	}

	@Override
	public boolean onResult(int request, int result, Intent data) {
		if (result == MainActivity.RESULT_CANCELED)
			return false;
		
		request &= 0xffff;
		String image = null;
		switch (request) {
		default:
			break;
		}
		return true;
	}
}
