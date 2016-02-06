package com.ssa.transact.transact;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TransactOffersListener {

    private static final String TAG = "Transact.Main";
    private ArrayAdapter<String> offersAdapter = null;
    private List<String> offersList = null;
    private String category = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().bindService(new Intent(this, TransactService.class),
                Singleton.getInstance().getServiceConnection(),
                BIND_AUTO_CREATE);

        final Spinner categorySpinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.categories,
                R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        category = adapter.getItem(0).toString();

        offersList = new ArrayList<>();
        offersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, offersList);

        final ListView offers = (ListView) findViewById(R.id.listView);
        offers.setAdapter(offersAdapter);
        offers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) offersAdapter.getItem(position);
                if (position == offersAdapter.getCount() - 1) {
                    Singleton.getInstance().getBinder().doGetOffers(MainActivity.this,
                            category, position + 4);
                } else {
                    String[] sp = s.split("\n");
                    if (sp != null && sp.length > 0) {
                        Singleton.getInstance().getBinder().doGetOffer(MainActivity.this,
                                category, sp[0].substring(4));
                    }
                }
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = adapter.getItem(position).toString();
                Singleton.getInstance().getBinder().doGetOffers(MainActivity.this,
                        category, 4);
                final TextView loadingCategory = (TextView) findViewById(R.id.loadingCategory);
                loadingCategory.setText("Loading ...");
                offersAdapter.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getApplicationContext().unbindService(Singleton.getInstance().getServiceConnection());
    }

    @Override
    public void updateOffers(List<String> offers) {
        final TextView load = (TextView) findViewById(R.id.loadingCategory);
        load.setText("");

        offersList.clear();
        offersList.addAll(offers);
        for (String o : offers) {
            Log.d(TAG, "new offer: ---" + o + "---");
        }
        offersAdapter.clear();
        offersAdapter.addAll(offers);
        offersAdapter.add("Load more ...");
    }

    @Override
    public void showOffer(HashMap<String, String> offer) {
        Intent i = new Intent(this, BuyActivity.class);
        offer.put("category", category);
        i.putExtra("offer", offer);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ssa.transact.transact/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.ssa.transact.transact/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
