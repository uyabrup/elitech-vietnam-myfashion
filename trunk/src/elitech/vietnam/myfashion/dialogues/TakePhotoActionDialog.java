/**
 * May 12, 2014 9:32:05 AM
 */
package elitech.vietnam.myfashion.dialogues;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import elitech.vietnam.myfashion.R;

/**
 * @author Cong
 *
 */
public class TakePhotoActionDialog extends AbstractDialogFragment implements View.OnClickListener {
	
	TakePhotoAction mClick;
	
	Button mBtnCamera, mBtnLibrary;
	
	int mMessage, mRequest;
	
	public static TakePhotoActionDialog newInstance(int request, Fragment target) {
		TakePhotoActionDialog dialog = new TakePhotoActionDialog();
		Bundle args = new Bundle();
		dialog.setArguments(args);
		dialog.setTargetFragment(target, request);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRequest = getTargetRequestCode();
		mClick = (TakePhotoAction) getTargetFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialog_takeimage, container, false);
		
		mBtnCamera = (Button) view.findViewById(R.id.dialog_takeimage_btnCamera);
		mBtnLibrary = (Button) view.findViewById(R.id.dialog_takeimage_btnLibrary);
		
		mBtnCamera.setOnClickListener(this);
		mBtnLibrary.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_takeimage_btnCamera:
			mClick.onCameraSelected();
			break;
		case R.id.dialog_takeimage_btnLibrary:
			mClick.onLibrarySelected();
			break;
		default:
			break;
		}
		dismiss();
	}
	
	public interface TakePhotoAction {
		void onCameraSelected();
		void onLibrarySelected();
	}
}
