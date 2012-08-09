package org.sigimera.app.sigimera;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.sigimera.controller.CrisesController;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class CrisesListActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crises_list);

		ListView list = (ListView) findViewById(R.id.crises_list);
		list.setOnItemClickListener(this.listClickListener);

		String auth_token = getIntent().getStringExtra("auth_token");

		CrisesController crisisControler = CrisesController.getInstance();
		JSONArray crises = crisisControler.getCrises(auth_token, 1);

		ArrayList<HashMap<String, String>> buttonList = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map = new HashMap<String, String>();

		for (int count = 0; count < crises.length(); count++) {
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

		SimpleAdapter adapterMainList = new SimpleAdapter(this, buttonList,
				R.layout.list_entry, new String[] { "icon", "top", "bottom" }, new int[] {
						R.id.icon, R.id.topText, R.id.bottomText });
		list.setAdapter(adapterMainList);
	}

	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			// TODO: show crises
			System.out.println("CLICKED ON " + position);
		}
	};

}
