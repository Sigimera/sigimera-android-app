package org.sigimera.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.sigimera.app.android.util.MD5Util;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

	private View view;
	private ImageView avatar;
	Drawable drawable;
	private ProgressDialog progessDialog = null;

	private final Handler guiHandler = new Handler();
	private final Runnable updateGUI = new Runnable() {
		@Override
		public void run() {
			updateProfile();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.profile_fragment, container, false);
		avatar = (ImageView) view.findViewById(R.id.avatar);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(Html.fromHtml("<p><b>29</b><br/>API Calls</p>"));

		progessDialog = ProgressDialog.show(getActivity(),
				"Preparing crises information!",
				"Please be patient until the information are ready...");
		Thread worker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					InputStream is = (InputStream) getAvatarURL(
							"corneliu.stanciu@sigimera.org").getContent();
					drawable = Drawable.createFromStream(is, "src name");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				guiHandler.post(updateGUI);
			}
		};
		worker.start();

		return view;
	}
	
	private void updateProfile() {
		avatar.setImageDrawable(drawable);
		progessDialog.dismiss();
	}
	
	private URL getAvatarURL(String email) {
		try {
			String emailHash = MD5Util.md5Hex(email.toLowerCase().trim());
			URL url = new URL("http://www.gravatar.com/avatar/" + emailHash);
			return url;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
