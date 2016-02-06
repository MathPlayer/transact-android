package com.ssa.transact.transact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactBinder extends Binder {

    private Context context;

    void onCreate(Context context) {
        this.context = context;
    }

    void onDestroy() {

    }

    void doLogin(TransactLoginListener listener, String username, String password) {
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("user", username);
        credentials.put("pass", password);
        new LoginTask(listener, context.getString(R.string.url) + "login").execute(credentials);
    }

    void doGetOffers(TransactOffersListener listener, String category, int count) {
        HashMap<String, String> offersInfo = new HashMap<>();
        offersInfo.put("category", category);
        offersInfo.put("from", "0");
        offersInfo.put("numOffers", "" + count);
        new OffersTask(listener, context.getString(R.string.url) + "offers").execute(offersInfo);

    }

    public void doGetOffer(TransactOffersListener listener, String category, String id) {
        new OfferTask(listener,
                context.getString(R.string.url) + "offer?" +
                        "category=" + category + "&id=" + id).execute();
    }

    public void getMap(TransactBuyListener listener, String lat, String lng) {
        new MapTask(listener,
                "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng +
                        "&zoom=15&size=400x400&maptype=roadmap&" +
                        "markers=color:blue%7Clabel:S%7C" + lat + "," + lng).execute();
    }

    public void buy(TransactBuyListener listener, String category, String id, String date,
                    String username, String title, String deliveryMethod) {
        HashMap<String, String> buyMap = new HashMap<>();
        buyMap.put("category", category);
        buyMap.put("id", id);
        buyMap.put("date", date);
        buyMap.put("offeringUser", username);
        buyMap.put("offerName", title);
        buyMap.put("deliveryMethod", deliveryMethod);

        new BuyTask(listener, context.getString(R.string.url) + "buy").execute(buyMap);
    }
}

class LoginTask extends AsyncTask<HashMap<String, String>, Void, Boolean> {

    private static final String TAG = "Transact.LoginTask";

    private TransactLoginListener listener = null;
    private String urlString = null;

    LoginTask(TransactLoginListener listener, String urlString) {


        this.listener = listener;
        this.urlString = urlString;
    }

    @Override
    protected Boolean doInBackground(HashMap<String, String>... credentials) {
        Boolean ret = Boolean.FALSE;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> e : credentials[0].entrySet())
                json.put(e.getKey(), e.getValue());
            Log.d(TAG, "Sending: " + json.toString());

            // Send POST output.
            DataOutputStream printout = null;

            printout = new DataOutputStream(connection.getOutputStream());
            printout.write(json.toString().getBytes("UTF-8"));
            printout.flush();
            printout.close();

            Log.d(TAG, "login response code: " + connection.getResponseCode());

            //Get Response
            InputStream is = null;
            if (connection.getResponseCode() != 200) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                Log.d(TAG, "line: " + line);
                response.append(line);
                response.append('\n');
            }
            connection.disconnect();
            if (response.length() > 0) {
                response.deleteCharAt(response.length() - 1);
            }

            Log.d(TAG, "response: -" + response + "-");
            if (response.toString().equals("OK")) {

                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                if (cookiesHeader != null) {
                    Singleton.getInstance().saveSession(cookiesHeader, credentials[0].get("user"),
                            credentials[0].get("pass"));
                    ret = Boolean.TRUE;
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException error");
            e.printStackTrace();
        }


        return ret;
    }

    @Override
    protected void onPostExecute(Boolean response) {
        if (listener != null) {
            if (response != null) {
                listener.updateLogin(response);
            }
        }
    }
}

class OffersTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private static final String TAG = "Transact.OffersTask";

    private TransactOffersListener listener = null;
    private String urlString = null;

    OffersTask(TransactOffersListener listener, String urlString) {
        this.listener = listener;
        this.urlString = urlString;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... offersInfo) {
        Boolean ret = Boolean.FALSE;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> e : offersInfo[0].entrySet())
                json.put(e.getKey(), e.getValue());
            Log.d(TAG, "Sending: " + json.toString());

            // Send POST output.
            DataOutputStream printout = null;

            printout = new DataOutputStream(connection.getOutputStream());
            printout.write(json.toString().getBytes("UTF-8"));
            printout.flush();
            printout.close();

            //Get Response
            InputStream is = null;
            if (connection.getResponseCode() != 200) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line + "\n";
            }
            connection.disconnect();
            if (response.length() > 0) {
                response = response.substring(0, response.length() - 1);
            }

            Log.d(TAG, "response: =" + response + "=");
            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException error");
            e.printStackTrace();
        }


        return "";
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d(TAG, "onPostExecute");

        if (listener == null) {
            return;
        }
        if (response == null || response == "") {
            return;
        }

        try {
            List<String> content = new ArrayList<>();
            JSONObject r = new JSONObject(response);
            JSONArray offers = r.optJSONArray("offers");
            for (int i = 0; i < offers.length(); i++) {
                String offer = "";
                JSONObject o = offers.getJSONObject(i);
                if (o.optString("username").equals(Singleton.getInstance().getUsername())) {
                    continue;
                }
                offer += "id: " + o.optString("id") + "\n";
                offer += "name: " + o.optString("name") + "\n";
//                offer += "Description: " + o.optString("description") + "\n";
                offer += "price: " + o.optString("price") + "\n";
                content.add(offer);
            }

            listener.updateOffers(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

class OfferTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private static final String TAG = "Transact.OffersTask";

    private TransactOffersListener listener = null;
    private String urlString = null;

    OfferTask(TransactOffersListener listener, String urlString) {
        this.listener = listener;
        this.urlString = urlString;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... offersInfo) {
        Boolean ret = Boolean.FALSE;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);

            InputStream is = null;
            if (connection.getResponseCode() != 200) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line + "\n";
            }
            connection.disconnect();
            if (response.length() > 0) {
                response = response.substring(0, response.length() - 1);
            }

            Log.d(TAG, "response: =" + response + "=");
            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException error");
            e.printStackTrace();
        }


        return "";
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d(TAG, "onPostExecute");

        if (listener == null) {
            return;
        }
        if (response == null || response == "") {
            return;
        }

        try {
            HashMap<String, String> content = new HashMap<>();
            JSONObject r = new JSONObject(response);
            JSONObject offer = r.getJSONObject("offer");

            content.put("id", offer.getString("id"));
            content.put("username", offer.getString("username"));
            content.put("description", offer.getString("description"));
            content.put("price", offer.getString("price"));
            content.put("title", offer.getString("name"));
            content.put("date", offer.getJSONArray("dates").getString(0));
            content.put("lat", offer.getJSONObject("position").getString("lat"));
            content.put("lng", offer.getJSONObject("position").getString("lng"));

            listener.showOffer(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

class MapTask extends AsyncTask<HashMap<String, String>, Void, Bitmap> {

    private static final String TAG = "Transact.OffersTask";

    private TransactBuyListener listener = null;
    private String urlString = null;

    MapTask(TransactBuyListener listener, String urlString) {
        this.listener = listener;
        this.urlString = urlString;
    }

    @Override
    protected Bitmap doInBackground(HashMap<String, String>... offersInfo) {
        Boolean ret = Boolean.FALSE;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            final Bitmap bm;
            InputStream is = null;
            if (connection.getResponseCode() != 200) {
                is = connection.getErrorStream();
                return null;
            } else {
                is = connection.getInputStream();
            }

            bm = BitmapFactory.decodeStream(is);
            is.close();

            connection.disconnect();

            return bm;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException error");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bm) {
        Log.d(TAG, "onPostExecute");

        if (listener == null) {
            return;
        }
        if (bm == null) {
            return;
        }

        listener.updateMap(bm);

    }
}

class BuyTask extends AsyncTask<HashMap<String, String>, Void, String> {

    private static final String TAG = "Transact.BuyTask";

    private TransactBuyListener listener = null;
    private String urlString = null;

    BuyTask(TransactBuyListener listener, String urlString) {
        this.listener = listener;
        this.urlString = urlString;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... credentials) {
        Boolean ret = Boolean.FALSE;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", Singleton.getInstance().getSession());
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> e : credentials[0].entrySet())
                json.put(e.getKey(), e.getValue());
            Log.d(TAG, "Sending: " + json.toString());

            // Send POST output.
            DataOutputStream printout = null;

            printout = new DataOutputStream(connection.getOutputStream());
            printout.write(json.toString().getBytes("UTF-8"));
            printout.flush();
            printout.close();

            Log.d(TAG, "login response code: " + connection.getResponseCode());

            //Get Response
            InputStream is = null;
            if (connection.getResponseCode() != 200) {
                return "";
            } else {
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                Log.d(TAG, "line: " + line);
                response.append(line);
                response.append('\n');
            }
            connection.disconnect();
            if (response.length() > 0) {
                response.deleteCharAt(response.length() - 1);
            }

            Log.d(TAG, "response: -" + response + "-");
            return response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IOException error");
            e.printStackTrace();
        }


        return "";
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d(TAG, "onPostExecute: " + response);
        if (listener != null) {
            if (response != null && !response.equals("")) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = json.optString("message");
                if (message.isEmpty()) {
                    message = "Error! Try again.";
                }

                listener.buyActionFinished(message);
            }
        }
    }
}
