package com.ssa.transact.transact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class BuyActivity extends AppCompatActivity implements TransactBuyListener {

    private static final String TAG = "Transact.Buy";
    private String deliveryMethod = null;
    private String textTitle;
    private String textDescription;
    private String textPrice;
    private String textUsername;
    private String textDate;
    private String textId;
    private String textCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Intent i = getIntent();
        HashMap<String, String> offer = (HashMap<String, String>)i.getSerializableExtra("offer");

        final TextView title = (TextView) findViewById(R.id.buyTitle);
        final TextView description = (TextView) findViewById(R.id.buyDescription);
        final TextView price = (TextView) findViewById(R.id.buyPrice);
        final TextView owner = (TextView) findViewById(R.id.buyOwner);
        final TextView date = (TextView) findViewById(R.id.buyDate);
        final Spinner spin = (Spinner) findViewById(R.id.buySpinner);

        textDate = offer.get("date");
        textTitle = offer.get("title");
        textDescription = offer.get("description");
        textPrice = offer.get("price");
        textId = offer.get("id");
        textUsername = offer.get("username");
        textCategory = offer.get("category");

        title.setText(offer.get("title"));
        description.setText(offer.get("description"));
        price.setText(offer.get("price") + " RON");
        owner.setText(offer.get("username"));
        date.setText(offer.get("date"));
        date.setEnabled(false);

        final Spinner deliverySpinner = (Spinner) findViewById(R.id.buySpinner);
        final ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.delivery,
                R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deliverySpinner.setAdapter(adapter);
        deliveryMethod = adapter.getItem(0).toString();

        deliverySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deliveryMethod = adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Singleton.getInstance().getBinder().getMap(this, offer.get("lat"), offer.get("lng"));
    }

    @Override
    public void updateMap(Bitmap bm) {
        final ImageView map = (ImageView) findViewById(R.id.buyMap);
        map.setImageBitmap(bm);
    }

    @Override
    public void buyActionFinished(String response) {
        Log.d(TAG, "buyActionFinished with message: " + response);
        final Button b = (Button) findViewById(R.id.buyButton);
        b.setText("Buy");
        b.setEnabled(true);
        Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        if (response.contains("Purchase successful")) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    public void buyProduct(View v) {
        final Button b = (Button) findViewById(R.id.buyButton);
        b.setText("Buying ...");
        b.setEnabled(false);
        Singleton.getInstance().getBinder().buy(this,
                textCategory, textId, textDate, textUsername, textTitle, deliveryMethod);
    }
}
