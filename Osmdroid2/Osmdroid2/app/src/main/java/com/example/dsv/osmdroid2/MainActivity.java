package com.example.dsv.osmdroid2;

/**
 * Mini-Demo zum Einsatz von osmdroid
 *
 * Quelle: osmdroid Tutorial:
 *     https://github.com/osmdroid/osmdroid/wiki/How%20to%20use%20the%20osmdroid%20library
 *
 * ToDo:
 *     - aktuelle Libraries einbinden
 *     - set user agent, s. comment
 *
 * History:
 *     01.10.2016   ai  fuer AndroidStudio aktualisierte Version
 *
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView         mMapView;
    private MapController mMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //important! set your user agent to prevent getting banned from the osm servers
        // org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(13);
        GeoPoint gPt = new GeoPoint(54.788685, 9.431248);
        mMapController.setCenter(gPt);
    }
}
