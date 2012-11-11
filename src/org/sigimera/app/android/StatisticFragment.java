package org.sigimera.app.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class StatisticFragment extends Fragment implements OnClickListener{

    private View view;

    private Location userLocation;

    private Crisis latestCrisis = null;
    private Crisis nearCrisis = null;
    private String auth_token = null;
    
    private ProgressDialog progessDialog = null;

    private final Handler guiHandler = new Handler();
    private final Runnable updateGUI = new Runnable() {
        @Override
        public void run() { updateStatistics(); }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statistic, container, false);
        progessDialog = ProgressDialog.show(getActivity(), "Preparing crises information!", 
        		"Please be patient until the information are ready...");
        Thread worker = new Thread() {
            @Override
            public void run() {
                try {
                    userLocation = LocationController.getInstance().getLastKnownLocation();

                    auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
                    latestCrisis = CrisesController.getInstance().getLatestCrisis(auth_token);
                    nearCrisis = CrisesController.getInstance().getNearCrisis(auth_token, userLocation);

                    guiHandler.post(updateGUI);
                } catch (AuthenticationErrorException e) {
                    // SHOULD NEVER OCCUR: Check before calling this window.
                    Log.e(Constants.LOG_TAG_SIGIMERA_APP, "Error on authentification" + e.getLocalizedMessage());
                }
            }
        };
        worker.start();

        return view;
    }

    private void updateStatistics() {
        if ( auth_token != null ) {
            Button nearCrisisButton = (Button) view.findViewById(R.id.button0);
            double nearDistance = DistanceController.getNearCrisisDistance(auth_token, nearCrisis, userLocation);
            if ( nearDistance != -1.0 )
                nearCrisisButton.setText(Html.fromHtml(nearDistance + " km" + "<br/><small><i>" + "Near crisis" + "</i></small>"));
            else
                nearCrisisButton.setText(Html.fromHtml("unknown<br/><small><i>" + "No near crisis" + "</i></small>"));
            nearCrisisButton.setOnClickListener(this);

            Cursor c = CrisesController.getInstance().getTodayCrises(auth_token);

            Button todayCrisesButton = (Button) view.findViewById(R.id.button1);
            String crisesToday = null;
            if ( c.getCount() == 0 )
                crisesToday = "No";
            else
                crisesToday = c.getCount() + "";
            todayCrisesButton.setText(Html.fromHtml(crisesToday + " Crises<br/><small><i>" + "Today" + "</i></small>"));

        }

        if ( latestCrisis != null ) {
            Button latestCrisisButton = (Button) view.findViewById(R.id.button2);
            latestCrisisButton.setText(Html.fromHtml(Common.getTimeAgoInWordsSplitted(Common.getMiliseconds(latestCrisis.getDate())) + "<br/><small><i>" + "Latest crisis" + "</i></small>"));
        }

        Button totalCrises = (Button) view.findViewById(R.id.button3);

        CrisesStats stats = CrisesController.getInstance().getCrisesStats(auth_token);
        if ( stats != null ) {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat outputFormatter = new SimpleDateFormat("d. MMMM yyyy");
            Date date = new Date();
            try {
                date = inputFormatter.parse(stats.getFirstCrisisAt());
            } catch ( Exception e) {
                e.printStackTrace();
            }
            totalCrises.setText(Html.fromHtml(stats.getTotalCrises() + " crises since<br/><small><i>" + outputFormatter.format(date) + "</i></small>"));
        }

        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        Fragment frag = new StatShortCrisis();
        Bundle bundle = new Bundle();
        bundle.putSerializable("crisis", nearCrisis);
        frag.setArguments(bundle);
        fragTransaction.add(R.id.main_frag_container, frag);
        fragTransaction.commit();
        closingProgressDialog();
    }
    
    public void closingProgressDialog() {
    	if ( progessDialog != null ) {
    		progessDialog.dismiss();
    		progessDialog = null;
    	}
    }

    @Override
    public void onClick(View v) {}
}
