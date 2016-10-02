package com.example.johnteng.foodfinder;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ibm.watson.developer_cloud.personality_insights.v2.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;



public class FoodFinder extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private EditText edLoc, edHandle;
    private Button btnFindMe;
    private double lat, lon;
    private String username, password, url;
    private final String TWITTER_CONSUMER_KEY = "GBBRn1BcbWy6WHboV4t24N7In";
    private final String TWITTER_CONSUMER_SECRET = "78AS0Dnj46pui9bxU4g2IXVI4vfsOcJ92fF0eoNtxCrD3UaL4g";
    private final String TWITTER_ACCESS_TOKEN = "335657764-ja1g5iImHeEirRq6CO9BEZlRijbT3UBFb5Q8brBa";
    private final String TWITTER_ACCESS_TOKEN_SECRET = "su3uqGtU3hyFHDrGtPBeRxWQkrfry8NVW4IlbVWBZ1KGk";
    private int CASE;
    public static boolean authenticate = true;
    AlertDialog dialogBuilder;
    public final jsonParser jp = new jsonParser();
    JSONObject j = null;
    readAPI r;
    ComputationalMatrix cm = new ComputationalMatrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        url = "https://gateway.watsonplatform.net/personality-insights/api";
        username = "c710423e-c611-434f-89d3-aa4e0ce1f503";
        password = "sDQqS0Kjdp74";

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        edLoc = (EditText) findViewById(R.id.edLoc);
        edHandle = (EditText) findViewById(R.id.edHandle);
        btnFindMe = (Button) findViewById(R.id.btnFindMe);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvURL = (TextView) findViewById(R.id.tvURL);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        ImageView imgBusiness = (ImageView) findViewById(R.id.imgBusiness);

        //new DownloadImageTask(imgBusiness).execute("https://s3-media1.fl.yelpcdn.com/bphoto/HENovrpv3Uh0M6UONHT2XA/ms.jpg");

        r = new readAPI(new jsonParser());

        new Thread(new Runnable() {
            @Override
            public void run() {
                r.run();
            }
        }).start();

        btnFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Finding Waldo...", Snackbar.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    lat = lastLocation.getLatitude();
                    lon = lastLocation.getLongitude();
                    edLoc.setText("(" + lat + ", " + lon + ")");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edHandle.getText().length() != 0) {
                    Snackbar.make(view, "Contacting Watson...", Snackbar.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        public void run() {
                            getTweets(edHandle.getText().toString());
                        }
                    }).start();
                } else {
                    makeToast(1);
                }
                if (CASE == 2) {
                    makeToast(2);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(FoodFinder.class.getSimpleName(), "Can't connect to Google Play Services!");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_howto) {
            howToDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPersonalityInsights(String text) {
        PersonalityInsights service = new PersonalityInsights();
        service.setUsernameAndPassword(username, password);

        Profile profile = service.getProfile(text).execute();

        try {
            j = new JSONObject(profile.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jp.parseWatson(j);//Send the watson json object to be parsed, this is a synchronized call
        setFoodQuery();
        r.run();
        //System.out.println(profile.toString());
    }

    public void setFoodQuery () {
        foodQuery.latitude = lat;
        foodQuery.longitude = lon;
        foodQuery.radius = cm.calculateStandardForRadius(cm.radiusMap);
        foodQuery.limit = 1;
        foodQuery.openNow = true;
        foodQuery.term = "restaurants";
        foodQuery.sortBy = cm.choseSortingMethod();
        foodQuery.price = cm.calculateStandardForPrice(cm.priceMap);
    }

    public void getTweets(String handle) {
        String text = "";
        Paging paging = new Paging(1, 100);
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            List<Status> statuses;
            String user;
            user = handle;
            statuses = twitter.getUserTimeline(user, paging);
            Log.i("Status Count", statuses.size() + " Feeds");
            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                text += (status.getText());
                //Log.i("Tweet Count " + (i + 1), status.getText() + "\n\n");
            }
            if (text.length() != 0) {
                Log.d("Log", "Outputting the JSON data");
                getPersonalityInsights(text);
            } else {
                CASE = 2;
            }
        } catch (TwitterException te) {
            te.printStackTrace();
        }
    }

    public void makeToast(int CASE) {
        if (CASE == 1) {
            Toast.makeText(getApplicationContext(), "Please enter a Twitter handle", Toast.LENGTH_LONG).show();
        } else if (CASE == 2) {
            Toast.makeText(getApplicationContext(), "I couldn't find any tweets!", Toast.LENGTH_LONG).show();
        }
    }

    private void howToDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View calibrateView = layoutInflater.inflate(R.layout.how_it_works_layout, null);

        dialogBuilder = new AlertDialog.Builder(this).create();
        dialogBuilder.setView(calibrateView);

        Button btnClose = (Button) calibrateView.findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.show();
    }
}
/*
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
*/
