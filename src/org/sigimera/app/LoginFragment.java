package org.sigimera.app;

import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		 super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login, container, false);

		Button button = (Button) view.findViewById(R.id.login_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity != null) {
					SessionHandler session_handler = SessionHandler
							.getInstance(ApplicationController.getInstance()
									.getSharedPreferences(
											"session_handler_preferences"));
					EditText emailView = (EditText) activity
							.findViewById(R.id.email_input_field);
					EditText passwordView = (EditText) activity
							.findViewById(R.id.password_input_field);

					if (session_handler.login(emailView.getText().toString(),
							passwordView.getText().toString())) {
						String auth_token = null;
						try {
							auth_token = session_handler
									.getAuthenticationToken();
							Intent listIntent = new Intent(getActivity(),
									CrisesListFragment.class);
							listIntent.putExtra("auth_token", auth_token);
							startActivity(listIntent);
						} catch (AuthenticationErrorException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// JSONArray nearCrises =
						// CrisesController.getInstance().getNearCrises(auth_token,
						// 1,
						// LocationController.getInstance().getLastKnownLocation());
						// if ( nearCrises != null && nearCrises.length() > 0 ){
						// //TODO: show context menu with the near crises
						// Log.i(SigimeraConstants.LOG_TAG_SIGIMERA_APP,
						// nearCrises.toString());
						// new Notification(getApplicationContext(), "Found " +
						// nearCrises.length() +
						// "crises near you.\n TODO: show near crises",
						// Toast.LENGTH_LONG);
						// }
					} else {
						new Notification(ApplicationController.getInstance()
								.getApplicationContext(),
								"Email or password were incorrect!",
								Toast.LENGTH_SHORT);
					}
				}
			}
		});

		return view;
	}
}
