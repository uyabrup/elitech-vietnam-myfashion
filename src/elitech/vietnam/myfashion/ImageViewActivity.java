package elitech.vietnam.myfashion;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import elitech.vietnam.myfashion.config.Const;

public class ImageViewActivity extends Activity {
	private WebView touchImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		touchImageView = (WebView) findViewById(R.id.webViewZoom);
		
		touchImageView.getSettings().setUseWideViewPort(true);
		touchImageView.getSettings().setLoadWithOverviewMode(true);
		touchImageView.getSettings().setBuiltInZoomControls(true);
		String linkImage = getIntent().getStringExtra("Link");
		String data = getIntent().getStringExtra("Data");
		if (linkImage != null && linkImage.length() > 0)
			touchImageView.loadUrl((Const.SERVER_IMAGE_URL + linkImage).replace("app/", ""));
		else if (data != null && data.length() > 0)
			touchImageView.loadDataWithBaseURL(Const.SERVER_IMAGE_URL, data, "text/html", "utf-8", null);
	}
}
