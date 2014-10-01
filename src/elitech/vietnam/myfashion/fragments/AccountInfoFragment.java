/**
 * Sep 25, 2014 4:04:21 PM
 */
package elitech.vietnam.myfashion.fragments;

import java.io.File;
import java.text.SimpleDateFormat;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import elitech.vietnam.myfashion.MainActivity;
import elitech.vietnam.myfashion.MainActivity.ResultListener;
import elitech.vietnam.myfashion.R;
import elitech.vietnam.myfashion.config.Const;
import elitech.vietnam.myfashion.cropimage.CropImage;
import elitech.vietnam.myfashion.dialogues.ChangePasswordDialog;
import elitech.vietnam.myfashion.dialogues.TakePhotoActionDialog;
import elitech.vietnam.myfashion.dialogues.TakePhotoActionDialog.TakePhotoAction;
import elitech.vietnam.myfashion.dialogues.UpdateBasicInfoDialog;
import elitech.vietnam.myfashion.dialogues.UpdateBasicInfoDialog.BasicInfoDialogCallback;
import elitech.vietnam.myfashion.dialogues.UpdateShippingDialog;
import elitech.vietnam.myfashion.dialogues.UpdateShippingDialog.ShippingDialogCallback;
import elitech.vietnam.myfashion.dialogues.UpdateStatusDialog;
import elitech.vietnam.myfashion.dialogues.UpdateStatusDialog.StatusDialogCallback;
import elitech.vietnam.myfashion.entities.Member;
import elitech.vietnam.myfashion.utilities.Utilities;
import elitech.vietnam.myfashion.widgets.CircularImageView;
import elitech.vietnam.myfashion.wsclient.ImageUploader;

/**
 * @author Cong
 */
public class AccountInfoFragment extends AbstractFragment implements View.OnClickListener, StatusDialogCallback,
		BasicInfoDialogCallback, ShippingDialogCallback, TakePhotoAction, ResultListener {
	
	private static final String		MEM_DIR			= "images/account/";
	
	CircularImageView	mImgAvatar;
	TextView			mTxtName, mTxtEmail, mTxtJoinDate, mTxtStatus, mTxtNickName, mTxtGender, mTxtAddress,
			mTxtDistrict, mTxtCity, mTxtPhone;
	RelativeLayout		mBtnPassword, mBtnNickName, mBtnGender, mBtnDistrict, mBtnCity, mBtnPhone;

	Member				mMember;
	int					mGenders[]	= new int[] { R.string.gender_female, R.string.gender_male, R.string.gender_others };

	public static AccountInfoFragment newInstance() {
		Bundle args = new Bundle();
		AccountInfoFragment fragment = new AccountInfoFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public AccountInfoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMember = mActivity.getLoggedinUser();
		mActivity.getController().setResultListener(this);
		View view = inflater.inflate(R.layout.fragment_account_info, container, false);

		mImgAvatar = (CircularImageView) view.findViewById(R.id.accountinfo_imgAvatar);
		mTxtName = (TextView) view.findViewById(R.id.accountinfo_txtName);
		mTxtEmail = (TextView) view.findViewById(R.id.accountinfo_txtEmail);
		mTxtJoinDate = (TextView) view.findViewById(R.id.accountinfo_txtJoinDate);
		mTxtStatus = (TextView) view.findViewById(R.id.accountinfo_txtStatus);
		mTxtNickName = (TextView) view.findViewById(R.id.accountinfo_txtNickName);
		mTxtGender = (TextView) view.findViewById(R.id.accountinfo_txtSex);
		mTxtAddress = (TextView) view.findViewById(R.id.accountinfo_txtAddress);
		mTxtDistrict = (TextView) view.findViewById(R.id.accountinfo_txtDistrict);
		mTxtCity = (TextView) view.findViewById(R.id.accountinfo_txtCity);
		mTxtPhone = (TextView) view.findViewById(R.id.accountinfo_txtPhone);
		mBtnPassword = (RelativeLayout) view.findViewById(R.id.accountinfo_btnChangePassword);
		mBtnNickName = (RelativeLayout) view.findViewById(R.id.accountinfo_btnNickName);
		mBtnGender = (RelativeLayout) view.findViewById(R.id.accountinfo_btnGender);
		mBtnDistrict = (RelativeLayout) view.findViewById(R.id.accountinfo_btnDistrict);
		mBtnCity = (RelativeLayout) view.findViewById(R.id.accountinfo_btnCity);
		mBtnPhone = (RelativeLayout) view.findViewById(R.id.accountinfo_btnPhone);

		mImgAvatar.setOnClickListener(this);
		mBtnPassword.setOnClickListener(this);
		mTxtStatus.setOnClickListener(this);
		mBtnNickName.setOnClickListener(this);
		mBtnGender.setOnClickListener(this);
		mTxtAddress.setOnClickListener(this);
		mBtnDistrict.setOnClickListener(this);
		mBtnCity.setOnClickListener(this);
		mBtnPhone.setOnClickListener(this);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		UrlImageViewHelper.setUrlDrawable(mImgAvatar, Const.SERVER_IMAGE_URL + mMember.Image, R.drawable.no_avatar);
		mTxtName.setText(mMember.Name);
		mTxtEmail.setText(mMember.Email);
		mTxtJoinDate.setText(new SimpleDateFormat("dd / MM / yyyy").format(mMember.JoinedDate()));
		mTxtStatus.setText(mMember.Profile);
		mTxtNickName.setText(mMember.NickName);
		mTxtGender.setText(mGenders[mMember.Sex]);
		mTxtAddress.setText(mMember.Address);
		mTxtDistrict.setText(mActivity.getDatabase().getDistrictById(mMember.District).Name);
		mTxtCity.setText(mActivity.getDatabase().getCityById(mMember.City).Name);
		mTxtPhone.setText(mMember.Phone);

		if (mMember.Profile.length() == 0) {
			mTxtStatus.setText(R.string.what_s_inyourmind);
			mTxtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
		}
		if (mTxtNickName.getText().toString().trim().length() == 0)
			mTxtNickName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
		if (mTxtGender.getText().toString().trim().length() == 0)
			mTxtGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
		if (mTxtDistrict.getText().toString().trim().length() == 0)
			mTxtDistrict.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
		if (mTxtCity.getText().toString().trim().length() == 0)
			mTxtCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
		if (mTxtPhone.getText().toString().trim().length() == 0)
			mTxtPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountinfo_txtStatus:
			UpdateStatusDialog.newInstance(mMember.Id, mMember.Profile, Const.REQUEST_DIALOG_UPDATESTATUS, this).show(
					getFragmentManager());
			break;
		case R.id.accountinfo_btnNickName:
		case R.id.accountinfo_btnGender:
			UpdateBasicInfoDialog.newInstance(mMember.Id, mMember.NickName, mMember.Sex,
					Const.REQUEST_DIALOG_UPDATEBASICINFO, this).show(getFragmentManager());
			break;
		case R.id.accountinfo_txtAddress:
		case R.id.accountinfo_btnDistrict:
		case R.id.accountinfo_btnCity:
		case R.id.accountinfo_btnPhone:
			UpdateShippingDialog.newInstance(mMember.Id, mMember.Address, mMember.District, mMember.City,
					mMember.Phone, Const.REQUEST_DIALOG_UPDATESHIPPING, this).show(getFragmentManager());
			break;
		case R.id.accountinfo_btnChangePassword:
			ChangePasswordDialog.newInstance(mMember.Id, mMember.Password, Const.REQUEST_DIALOG_UPDATESHIPPING, this)
					.show(getFragmentManager());
			break;
		case R.id.accountinfo_imgAvatar:
			TakePhotoActionDialog.newInstance(Const.REQUEST_DIALOG_TAKEPICAVATAR, this).show(getFragmentManager());
			break;
		default:
			break;
		}
	}

	@Override
	public void updateStatus(String status) {
		mMember.Profile = status;
		mTxtStatus.setText(mMember.Profile.length() == 0 ? getString(R.string.what_s_inyourmind) : status);
		mTxtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				mMember.Profile.length() == 0 ? R.drawable.chevron_right : 0, 0);
	}

	@Override
	public void updateInfo(String nickName, int gender) {
		mMember.NickName = nickName;
		mMember.Sex = gender;
		mTxtNickName.setText(nickName);
		mTxtGender.setText(mGenders[gender]);
		mTxtNickName.setCompoundDrawablesWithIntrinsicBounds(0, 0, nickName.length() == 0 ? R.drawable.chevron_right
				: 0, 0);
		mTxtGender.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				mTxtGender.getText().toString().length() == 0 ? R.drawable.chevron_right : 0, 0);
	}

	@Override
	public void updateShipping(String address, int city, int district, String phone) {
		mMember.Address = address;
		mMember.City = city;
		mMember.District = district;
		mMember.Phone = phone;
		mTxtCity.setText(mActivity.getDatabase().getCityById(city).Name);
		mTxtDistrict.setText(mActivity.getDatabase().getDistrictById(district).Name);
		mTxtAddress.setText(address);
		mTxtPhone.setText(phone);
		mTxtAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, address.length() == 0 ? R.drawable.chevron_right : 0,
				0);
		mTxtPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				mTxtGender.getText().toString().length() == 0 ? R.drawable.chevron_right : 0, 0);
		mTxtCity.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				mTxtCity.getText().toString().length() == 0 ? R.drawable.chevron_right : 0, 0);
		mTxtDistrict.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				mTxtDistrict.getText().toString().length() == 0 ? R.drawable.chevron_right : 0, 0);
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
		case Const.REQUEST_EDITLIBRARY:
			Uri img = data.getData();
			String path = "";
			if (img.toString().startsWith("content://com.android.gallery3d.provider"))
				img = Uri.parse(img.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
			path = mActivity.getRealPathFromURI(img);
			path = (path == null || path.length() == 0) ? img.toString() : path;
			if (URLUtil.isContentUrl(path) || URLUtil.isHttpsUrl(path) || URLUtil.isHttpUrl(path))
				image = Utilities.downloadImage(mActivity, path, mActivity.getConfig().getImageTemp());
			else 
				image = Utilities.copyFile(mActivity.getRealPathFromURI(img), mActivity.getConfig().getImageTemp());
			startCrop(image);
			break;
		case Const.REQUEST_EDITCAMERA:
			image = Uri.fromFile(new File(mActivity.getConfig().getImageTemp())).getPath();
			startCrop(image);
			break;
		case Const.REQUEST_EDITCROP:
			saveAvatar(data.getStringExtra(CropImage.IMAGE_PATH));
			break;
		default:
			break;
		}
		return true;
	}
	
	private void startCrop(String image) {
		Intent intent = new Intent(mActivity, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, image);
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra(CropImage.CIRCLE_CROP, "true");
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		startActivityForResult(intent, Const.REQUEST_EDITCROP);
	}
	
	private void saveAvatar(String path) {
		final String imagePath = path;
		final String name = mMember.Id + "_" + System.currentTimeMillis() + ".jpg";
		final String image = MEM_DIR + name;
		
		new AsyncTask<Integer, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(Integer... params) {
				return new ImageUploader().upload(imagePath, name, "account");
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					mActivity.getServices().updateAvatar(mMember.Id, image, new Callback<Integer>() {
						@Override
						public void success(Integer arg0, Response arg1) {
							if (arg0 > 0) {
								mMember.Image = image;
								Toast.makeText(mActivity, R.string.profilesaved, Toast.LENGTH_SHORT).show();
								UrlImageViewHelper.setUrlDrawable(mImgAvatar, Const.SERVER_IMAGE_URL + image, R.drawable.no_avatar);
							} else {
								Toast.makeText(mActivity, R.string.updateavatarfailed, Toast.LENGTH_SHORT).show();
							}
						}
						@Override
						public void failure(RetrofitError arg0) {
							Log.w("RetrofitError", arg0.getMessage());
						}
					});
				} else
					Toast.makeText(mActivity, R.string.updateavatarfailed, Toast.LENGTH_SHORT).show();
			};
		}.execute();
	}
}
