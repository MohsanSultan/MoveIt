package com.moveitdriver.utils;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.moveitdriver.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class BackgroundService extends Service {

    private static String TAG = "SOCKET";
    private Handler handler;
    private int delay;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constants.socket_base_url);
        } catch (URISyntaxException e) {
            Constants.showAlert(this, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "Service Start...");
        connectSocket();

        if (Constants.mCurLat == 0.0 && Constants.mCurLong == 0.0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Socket functions...
                    SendInfo();
                    mSocket.on("Rider_Req", onAcceptReject);
                }
            }, 5000);
        } else {
            // Socket functions...
            SendInfo();
            mSocket.on("Rider_Req", onAcceptReject);
        }

        // Socket Listener For Start/End Trip Notification...
        mSocket.on("Notification", onNotifications);
        mSocket.on("Rider_Cancel", onRideCancel);

        handler = new Handler();
        delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                Log.e(TAG, "Service Is Active...");
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // ========================================================================================== //

    public void connectSocket() {
        // SOCKET CODE...
        mSocket.connect();
        Log.e(TAG, "Socket Connected...");
    }

    // ============================= >> SOCKET LISTENERS << ===================================== //

    public Emitter.Listener onAcceptReject = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (!Constants.foregrounded()) {
                // Run when app is on background...
                Constants.showNotification(getApplicationContext(), "Test1", "Rider Request...");
            } else {
                // Run when app is open...
                Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
                intent.putExtra("path", "onAcceptReject");
                intent.putExtra("data", args[0].toString());
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(intent);
            }
        }
    };

    public Emitter.Listener onNotifications = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject obj = (JSONObject) args[0];

            if (!Constants.foregrounded()) {
                // Run when app is on background...
                try {
                    Constants.showNotification(getApplicationContext(), "" + obj.getString("Type"), "" + obj.getString("Msg"));
                } catch (JSONException e) {
                    return;
                }
            } else {
                // Run when app is open...
                try {
                    Constants.showNotification(getApplicationContext(), "" + obj.getString("Type"), "" + obj.getString("Msg"));
                    Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
                    intent.putExtra("path", "onNotifications");
                    intent.putExtra("data", args[0].toString());
                    LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(intent);
                } catch (JSONException e) {
                    return;
                }
            }
        }
    };

    public Emitter.Listener onRideCancel = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (!Constants.foregrounded()) {
                // Run when app is on background...
                Constants.showNotification(getApplicationContext(), "Ride Cancel", "Rider Cancel The Ride...");
            } else {
                // Run when app is open...
                Constants.showNotification(getApplicationContext(), "Ride Cancel", "Rider Cancel The Ride...");
                Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
                intent.putExtra("path", "onRideCancel");
                intent.putExtra("data", args[0].toString());
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(intent);
            }
        }
    };

    // =========================== >> SOCKET EMIT FUNCTIONS << ================================== //

    private void SendInfo() {
        JSONObject objectData = new JSONObject();
        JSONObject object = new JSONObject();

        try {
            object.put("userid", SharedPrefManager.getInstance(this).getDriverId());
            object.put("firstname", SharedPrefManager.getInstance(this).getDriverFirstName());
            object.put("lastname", SharedPrefManager.getInstance(this).getDriverLastName());
            object.put("email", SharedPrefManager.getInstance(this).getDriverEmail());
            object.put("status", 0);
            object.put("role", "Driver");
            object.put("contact", SharedPrefManager.getInstance(this).getDriverContact());
            object.put("latitude", "31.5313234");
            object.put("longitude", "74.3526096");

            Log.e("Salman", Constants.mCurLat + " / " + Constants.mCurLong);

            objectData.put("data", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("info", objectData);
    }

    public void UpdateLatLong(JSONObject object) {
        mSocket.emit("Driver_UpLatLong", object);
    }

    public void AcceptRejectAttemptSend(String method, JSONObject object) {
        if (method == "Driver_Accept") {
            mSocket.emit("Driver_Accept", object, new Ack() {
                @Override
                public void call(Object... args) {
                    Constants.bookingId = args[0].toString();
                }
            });
        } else if (method == "Driver_Reject") {
            mSocket.emit("Driver_Reject", object);
        } else if (method == "Driver_NoAccept") {
            mSocket.emit("Driver_NoAccept", object);
        }
    }

    public void StartTrip(JSONObject object) {
        mSocket.emit("Driver_StartTrip", object);
    }

    public void EndTrip(JSONObject object) {
        mSocket.emit("Driver_EndTrip", object);
    }

    public void CancelBooking(JSONObject object) {
        mSocket.emit("Driver_Cancel", object, new Ack() {
            @Override
            public void call(Object... args) {
                if (!Constants.foregrounded()) {
                    // Run when app is on background...
                } else {
                    // Run when app is open...
                    Intent intent = new Intent(MainActivity.RECEIVER_INTENT);
                    intent.putExtra("path", "onBookingCancel");
                    intent.putExtra("data", args[0].toString());
                    LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(intent);
                }
            }
        });
    }
}