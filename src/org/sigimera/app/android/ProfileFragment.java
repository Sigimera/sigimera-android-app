package org.sigimera.app.android;

import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {

	private View view;
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

//		progessDialog = ProgressDialog.show(getActivity(),
//				"Preparing crises information!",
//				"Please be patient until the information are ready...");
//		Thread worker = new Thread() {
//			@Override
//			public void run() {
//					Looper.prepare();				
//			}
//		};
//		worker.start();

		return view;
	}
	
	
	private void updateProfile() {
		// TODO Auto-generated method stub
		
	}
}
