package at.langhofer.yellowdesks;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static android.R.attr.value;
import static at.langhofer.yellowdesks.R.id.map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Data data = Data.getInstance();

        // hook up data ready
        TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println("data ready");

                List<Host> hosts = Data.getInstance().getData();
                for (Host host : hosts) {
                    LatLng latlng = new LatLng( host.getLat(), host.getLng() );
                    System.out.println("new latlng: " + latlng.toString());
                    Marker myMarker = mMap.addMarker( new MarkerOptions().position( latlng ).title( host.getHost() ) );

                    // attaching host to marker to retreive in OnMarkerClickListener
                    myMarker.setTag(host);


                    GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent myIntent = new Intent( MapActivity.this, DetailActivity.class );

                            // retreive host from marker
                            Host host = (Host) marker.getTag();
                            System.out.println("opening detail activity with key: " + host.getId());
                            myIntent.putExtra( "key", host.getId() );

                            MapActivity.this.startActivity( myIntent );
                            return false;
                        }
                    };
                    mMap.setOnMarkerClickListener(onMarkerClickListener);
                }
            }
        };

        // start download
        System.out.println("starting download...");
        data.downloadHosts(taskDelegate);

        mMap.getUiSettings().setZoomControlsEnabled( true );
        mMap.getUiSettings().setAllGesturesEnabled( true );
        mMap.getUiSettings().setZoomGesturesEnabled( true );

        LatLng salzburgdowntown = new LatLng( 47.806021, 13.050602000000026 );

        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(salzburgdowntown);
        circleOptions.radius(1000);

        circleOptions.fillColor(Color.argb(50, 50,50,0));
        circleOptions.strokeColor(Color.YELLOW);
        circleOptions.strokeWidth(2);

        mMap.addCircle(circleOptions);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( salzburgdowntown, 12 );
        mMap.moveCamera( update );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( map );
        mapFragment.getMapAsync( this );


        final Button btnLogin = (Button) findViewById( R.id.btnList );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent( MapActivity.this, ResultsActivity.class );
                myIntent.putExtra( "key", value ); //Optional parameters
                MapActivity.this.startActivity( myIntent );
            }
        } );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder( this ).addApi( AppIndex.API ).build();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName( "Map Page" ) // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl( Uri.parse( "http://[ENTER-YOUR-URL-HERE]" ) )
                .build();
        return new Action.Builder( Action.TYPE_VIEW )
                .setObject( object )
                .setActionStatus( Action.STATUS_TYPE_COMPLETED )
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start( client, getIndexApiAction() );
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end( client, getIndexApiAction() );
        client.disconnect();
    }
}
