package com.anti.rootadbcontroller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * A service that tracks the device's location. It fetches the last known GPS location
 * and saves it to a file. This service is designed to run, fetch the location, and then
 * stop itself immediately.
 */
public class LocationTrackerService extends Service {
    private static final String TAG = "LocationTrackerService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fetchLocation();
        stopSelf(); // Stop the service after fetching the location
        return START_NOT_STICKY;
    }

    private void fetchLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "Location Manager not found");
            return;
        }

        try {
            // Get last known location from GPS_PROVIDER
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                String locationString = "Lat: " + lastKnownLocation.getLatitude() + ", Lon: " + lastKnownLocation.getLongitude();
                Log.d(TAG, "Location: " + locationString);
                saveLocationToFile(locationString);
            } else {
                Log.d(TAG, "Last known location is not available.");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted", e);
        }
    }

    /**
     * Saves the location data to a text file in the "Downloads" directory.
     * Each entry is timestamped.
     * @param locationData The location data string to be saved.
     */
    private void saveLocationToFile(String locationData) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File outputFile = new File(downloadsDir, "extracted_location.txt");
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile, true); // append
            fos.write((new java.util.Date().toString() + ": " + locationData + "\n").getBytes());
            fos.close();
            Log.d(TAG, "Location saved to " + outputFile.getAbsolutePath());
        } catch (java.io.IOException e) {
            Log.e(TAG, "Failed to save location to file", e);
        }
    }
}
