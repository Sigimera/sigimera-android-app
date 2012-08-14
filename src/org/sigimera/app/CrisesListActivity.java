package org.sigimera.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.controller.Common;
import org.sigimera.app.controller.CrisesController;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CrisesListActivity extends Activity {
	private JSONArray crises;
	private ListView list;
	private CrisesController crisisControler;

	private final Handler guiHandler = new Handler();
	private final Runnable updateCrises = new Runnable() {
		@Override
		public void run() {
			 showCrises();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crises_list);

		list = (ListView) findViewById(R.id.crises_list);
		list.setOnItemClickListener(this.listClickListener);
		registerForContextMenu(list);

		final ProgressDialog progressDialog = ProgressDialog.show(CrisesListActivity.this, null, "Searching for latest crises...", false);
        
        Thread seeker = new Thread() {
        	@Override
        	public void run() {
        		Looper.prepare();
        		try {
        			String auth_token = getIntent().getStringExtra("auth_token");
        			crisisControler = CrisesController.getInstance();
        			crises = crisisControler.getCrises(auth_token, 1);
        		} finally {
        			guiHandler.post(updateCrises);
        			progressDialog.dismiss();
        		}
        	}
        };
        seeker.start();			
	}

	private void showCrises() {
		ArrayList<HashMap<String, String>> buttonList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();		
		
		try {
			for ( int count = 0; count < crises.length(); count++ ) {
				try {
					JSONObject crisis = (JSONObject) crises.get(count);
	
					map = new HashMap<String, String>();
					map.put("top", crisisControler.getShortTitle(crisis));
	
					String crisis_type = crisis.getJSONArray("dc_subject")
							.getString(0);
					if (crisis_type.contains("flood"))
						map.put("icon", R.drawable.flood + "");
					else if (crisis_type.contains("earthquake"))
						map.put("icon", R.drawable.earthquake + "");
					else if (crisis_type.contains("cyclone"))
						map.put("icon", R.drawable.cyclone + "");
					else if (crisis_type.contains("volcano"))
						map.put("icon", R.drawable.volcano + "");
	
					map.put("bottom", crisis.getString("dc_date"));
	
					buttonList.add(map);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			Intent loginIntent = new Intent(CrisesListActivity.this, LoginActivity.class);
			this.startActivity(loginIntent);				
		}					

		SimpleAdapter adapterMainList = new SimpleAdapter(this, buttonList,
				R.layout.list_entry, new String[] { "icon", "top", "bottom" }, new int[] {
						R.id.icon, R.id.topText, R.id.bottomText });
		list.setAdapter(adapterMainList);
	}

	private void showClickedCrisis(int position) {
		try {
			JSONObject crisis = (JSONObject) this.crises.get(position);
			Intent crisisIntent = new Intent(CrisesListActivity.this, CrisisActivity.class);
			crisisIntent.putExtra("crisis", crisis.toString());
			this.startActivity(crisisIntent);
		} catch (JSONException e) {
			new Notification(getApplicationContext(), "Failed to read the clicked crisis!", Toast.LENGTH_SHORT);
		}		
	}
			
	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			showClickedCrisis(position);					
		}
	};
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle("Options");
	    menu.setHeaderIcon(R.drawable.sigimera_logo);	    
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.list_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    // Handle item selection
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.open:
	        	showClickedCrisis(info.position);
	            return true;
	        case R.id.share:
				try {
					this.startActivity(Common.shareCrisis(((JSONObject) crises.get(info.position)).getString("_id")));
				} catch (JSONException e) {
					new Notification(getApplicationContext(), "Failed to get the crisis", Toast.LENGTH_SHORT);
				}
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

}
