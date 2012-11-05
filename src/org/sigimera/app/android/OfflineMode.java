package org.sigimera.app.android;

import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.util.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OfflineMode extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		ApplicationController appController = ApplicationController.getInstance();
		if ( appController.getActionbar() != null ) {
			if ( Common.hasInternet() )				
				appController.getActionbar().setIcon(context.getResources().getDrawable(R.drawable.sigimera_logo));
			else
				appController.getActionbar().setIcon(context.getResources().getDrawable(R.drawable.sigimera_logo_offline));
		}			
	}
}
