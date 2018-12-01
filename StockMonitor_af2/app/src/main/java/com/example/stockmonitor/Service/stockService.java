package com.example.stockmonitor.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.stockmonitor.Activities.myStock;
import com.example.stockmonitor.Bookmodel_for_service.ListModel;
import com.example.stockmonitor.Database.AppDatabase;
import com.example.stockmonitor.GSONclass.BookModel;
import com.example.stockmonitor.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;



public class stockService extends Service {

    boolean taskRunning = false;
    String stockName_;
    String url_create;
    String url_create_AddAndCeck;
    String url_update;
    private String addAndCheckData_price = "";
    boolean checkIfStockExits = false;
    boolean doubleCheck = true;
    double newestAverage;
    private AsyncTask waitTask;
    String weatherData;
    String weatherData_refresh;
    String weatherData_refresh_addAndCheck;
    String stockData_refresh;
    String update_data;
    JSONObject data = null;
    JSONObject data_refresh = null;
    JSONObject data_update = null;
    JSONObject data_refresh_addAndCheck;
    private WeakReference<Context> contextRef;
    List<ListModel> books;
    int onlyOnce;
    private boolean isStarted = false;
    boolean autoUpdate = false;
    public static final String BROADCAST_DATA_UPDATED = "some_key";

    private final IBinder binder = new stockUpdateServiceBinder();
    boolean started = false;
    private boolean runAsForegroundService = true;
    private static final int NOTIFY_ID = 142;

    AppDatabase appDatabase;

    static List<ListModel> stockList;

    //Stocklist functions

    public void setStockListByPosition_ListModel(int position, ListModel tempUser)
        {
        stockList.set(position, tempUser);
    }

    public void deleteStock(int position)
    {
        stockList.remove(position);
    }

    public ListModel getStockPriceByName(String stockName)
    {
        for(int i = 0; i < stockList.size(); ++i)
        {
            String testString = stockList.get(i).getName();
            if(testString.equals(stockName))
            {
                return stockList.get(i);
            }
        }
        return null;
    }

    public void setStockListFull(List<ListModel> liste)
    {
        int size = liste.size();

        for(int i = 0; i < size; ++i)
        {
            ListModel tempUser = liste.get(i);
            stockList.add(tempUser);
        }

    }

    public void setStockList(ListModel arg)
    {
        stockList.add(arg);
    }

    public List<ListModel> getStockList()
    {
        return stockList;
    }

    public ListModel getUser(int index)
    {
        return stockList.get(index);
    }

    //Check if startStockService is started

    public boolean getIsStarted()
    {
        return isStarted;
    }


    public double getNewestAverage()
    {
        return newestAverage;
    }

    public void clear()
    {
        stockList.clear();
    }

    public class stockUpdateServiceBinder extends Binder {
        //return ref to service (or at least an interface) that activity can call public methods on
        public stockService getService() {
            return stockService.this;
        }
    }
    @Override
    //very important! return your IBinder (your custom Binder)
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //in this case we only start the background running loop once
        if(!started && intent!=null) {
            started = true;

            if(runAsForegroundService) {

                //Intent notificationIntent = new Intent(this, MainActivity.class);
                //PendingIntent pendingIntent =
                //        PendingIntent.getActivity(this, 0, notificationIntent, 0);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // Do not do this at home :)
                    NotificationChannel mChannel = new NotificationChannel("myChannel", "Visible myChannel", NotificationManager.IMPORTANCE_LOW);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                Notification notification =
                        new NotificationCompat.Builder(this, "myChannel")
                                .setContentTitle("title")
                                .setContentText("text")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                //        .setContentIntent(pendingIntent)
                                .setTicker("Ticker_Text")
                                .setChannelId("myChannel")
                                .build();

                //calling Android to
                startForeground(NOTIFY_ID, notification);
            }

            //do background thing
            //doBackgroundThing(wait);
        } else {
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    //This instance receives a message from the database

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");

            if(message == "databasePopulated")
            {
                if(onlyOnce == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //If program is running for the first time

                            if(onlyOnce == 0) {

                                books = mydb.daoAccess().getAllListModels();

                                stockList.add(books.get(0));
                                stockList.add(books.get(1));
                                stockList.add(books.get(2));
                                stockList.add(books.get(3));
                                stockList.add(books.get(4));
                                stockList.add(books.get(5));
                                stockList.add(books.get(6));
                                stockList.add(books.get(7));
                                stockList.add(books.get(8));
                                stockList.add(books.get(9));

                                //refresh();

                                //startStockService();
                            }

                            onlyOnce = 1;
                            sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("onlyOnce", onlyOnce);
                            editor.apply();


                        }
                    }).start();
                }
            }
        }
    };

    AppDatabase mydb;
    SharedPreferences sharedPreferences;

    private static final long UPDATE_RATE_MS = 15*1000; // 2 minutes update rate
    private Handler autoRefreshHandler = new Handler();
    private Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {

            if (autoUpdate) {
                autoRefreshHandler.postDelayed(autoRefreshRunnable, UPDATE_RATE_MS);
            }
            refresh();
        }
    };

    @Override
    public void onCreate() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));

        contextRef = new WeakReference<>(getApplicationContext());
        stockList = new ArrayList<>();

        //Database callback happens only if getDatabase is called
        mydb = appDatabase.getDatabase(contextRef.get());
        mydb.daoAccess().getAllListModels();

        retrieve();

        //If program has already run at least once

        if(onlyOnce == 1)
        {
            books = mydb.daoAccess().getAllListModels();

            for(int i = 0; i < books.size(); ++i) {

                stockList.add(books.get(i));
            }
            refresh();


        }

        autoUpdate = true;
        autoRefreshHandler.postDelayed(autoRefreshRunnable, UPDATE_RATE_MS);

    }


    public static final String myPreferences = "MyPrefs";

    //To check if program has run once

    public void retrieve()
    {
        sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        onlyOnce = sharedPreferences.getInt("onlyOnce", 0);
    }


    //Checks if stock can be found, but doesnt update stockList
    @SuppressLint("StaticFieldLeak")
    public void addAndCheck(String name_) {

        final String URL_name = name_;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                        url_create_AddAndCeck = "https://api.iextrading.com/1.0/stock/" + URL_name + "/quote";

                        try {

                            URL url = new URL(url_create_AddAndCeck);

                            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                            BufferedReader reader_ =
                                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

                            StringBuffer json = new StringBuffer(2048);
                            String tmp = "";

                            while ((tmp = reader_.readLine()) != null) {
                                json.append(tmp).append("\n");
                            }

                            //json.deleteCharAt(0);
                            //json.deleteCharAt(json.length());

                            reader_.close();

                            data_refresh_addAndCheck = new JSONObject(json.toString());

                            int hej = 0;

                        } catch (Exception e) {

                            System.out.println("Exception " + e.getMessage());
                            checkIfStockExits = false;
                            sendDidntWorkMessage();

                            return null;
                        }


                        if (data_refresh_addAndCheck != null) {

                            Log.d("my weather received", data_refresh_addAndCheck.toString());
                            Log.e("my weather received", data_refresh_addAndCheck.toString());
                            Log.wtf("my weather received", data_refresh_addAndCheck.toString());

                            weatherData_refresh_addAndCheck = data_refresh_addAndCheck.toString();
                            String average_ = "0";

                            Gson gson_weather = new Gson();

                            BookModel weather_main = gson_weather.fromJson(weatherData_refresh_addAndCheck, BookModel.class);

                            String tempString = "";
                            try
                            {
                                tempString = Double.toString(weather_main.getLatestPrice());
                            }
                            catch(NumberFormatException nfe)
                            {
                            }

                            ListModel tempModel = new ListModel();
                            tempModel.setName(URL_name);
                            tempModel.setPrice(tempString);
                            tempModel.setSector(weather_main.getSector());
                            tempModel.setCompanyName(weather_main.getCompanyName());

                            //ListModel tempUser = stockList.get(stockList.size() - 1);
                            String price = tempModel.getPrice();
                            double price_d = Double.parseDouble(price);
                            double original_price = tempModel.getBought_for();
                            double change_in_price = 0;
                            String change_in_price_s = "";

                            if(original_price > price_d)
                            {
                                change_in_price = original_price - price_d;
                                try
                                {
                                    change_in_price_s = "- " + Double.toString(change_in_price);
                                }
                                catch(NumberFormatException nfe)
                                {
                                }

                            }
                            else if(original_price < price_d)
                            {
                                change_in_price = price_d - original_price;
                                try
                                {
                                    change_in_price_s = Double.toString(change_in_price);
                                }
                                catch(NumberFormatException nfe)
                                {
                                }
                            }
                            tempModel.setC_price(change_in_price_s);
                            stockList.add(tempModel);
                            checkIfStockExits = true;
                            sendCheckAndAddMessage();

                        }

                        return null;
            }

            @Override
            protected void onPostExecute(Void Void) {

            }
        }.execute();


    }

    private void sendCheckAndAddMessage()
    {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("Refresh");
        intent.putExtra("message", "Add");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendDidntWorkMessage()
    {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("Refresh");
        intent.putExtra("message", "Didnt_work");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    //Updates stockList once

    @SuppressLint("StaticFieldLeak")
    public void refresh() {

        if(taskRunning) {
           //stopService();
            taskRunning = false;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {



                    for(int i = 0; i < stockList.size(); ++i)
                    {

                        url_create = "https://api.iextrading.com/1.0/stock/" + stockList.get(i).getName() + "/quote";

                        try {

                            URL url = new URL(url_create);

                            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                            BufferedReader reader_ =
                                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

                            StringBuffer json = new StringBuffer(2048);
                            String tmp = "";

                            while ((tmp = reader_.readLine()) != null) {
                                json.append(tmp).append("\n");
                            }

                            //json.deleteCharAt(0);
                            //json.deleteCharAt(json.length());

                            reader_.close();

                            data_refresh = new JSONObject(json.toString());


                        } catch (Exception e) {

                            System.out.println("Exception " + e.getMessage());
                            return null;
                        }


                        if (data_refresh != null) {

                            Log.d("Data received", data_refresh.toString());
                            Log.e("Data received", data_refresh.toString());
                            Log.wtf("Data received", data_refresh.toString());

                            weatherData_refresh = data_refresh.toString();
                            String average_ = "0";

                            Gson gson_weather = new Gson();

                            BookModel weather_main = gson_weather.fromJson(weatherData_refresh, BookModel.class);

                            String tempString = "";
                            try
                            {
                                tempString = Double.toString(weather_main.getLatestPrice());
                            }
                            catch(NumberFormatException nfe)
                            {

                            }

                            stockList.get(i).setPrice(tempString);
                            stockList.get(i).setSector(weather_main.getSector());
                            stockList.get(i).setCompanyName(weather_main.getCompanyName());

                            ListModel tempUser = stockList.get(i);
                            String price = tempUser.getPrice();
                            double price_d = Double.parseDouble(price);
                            double original_price = tempUser.getBought_for();
                            double change_in_price = 0;
                            String change_in_price_s = "";


                            if(original_price > price_d)
                            {
                                change_in_price = original_price - price_d;
                                try
                                {
                                    change_in_price_s = "- " + Double.toString(change_in_price);
                                }
                                catch(NumberFormatException nfe)
                                {
                                }

                            }
                            else if(original_price < price_d)
                            {
                                change_in_price = price_d - original_price;
                                try
                                {
                                    change_in_price_s = Double.toString(change_in_price);
                                }
                                catch(NumberFormatException nfe)
                                {
                                }
                            }
                            stockList.get(i).setC_price(change_in_price_s);


                        }

                        //sendRefreshMsg();
                    }
                    sendRefreshMsg();

                //https://stackoverflow.com/questions/13902115/how-to-create-a-notification-with-notificationcompat-builder

                notifycation_intent = new Intent(getApplicationContext(), myStock.class);
                contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifycation_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                final NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

                b.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.alligator_background)
                        .setTicker("Hearty365")
                        .setContentTitle("AU547056 stocks")
                        .setContentText("Stocklist updated")
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent)
                        .setContentInfo("Info");

                final NotificationManager notificationManager = (NotificationManager) getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);


                return null;

            }

            @Override
            protected void onPostExecute(Void Void) {
                // TODO all data is fetched

                if(!taskRunning) {
                    //startStockService();
                }

            }
        }.execute();

    }

    public void stopService()
    {
        if(waitTask != null)
        {
            waitTask.cancel(true);
            waitTask = null;
        }
    }

    Handler handler = new Handler();
    int delay = 1000; //milliseconds
    Intent notifycation_intent;
    PendingIntent contentIntent;

    private void sendRefreshMsg()
    {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("Refresh");
        intent.putExtra("message", "Refresh");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        started = false;
        autoUpdate = false;
        Toast.makeText(this, "Service destroyed!", Toast.LENGTH_LONG).show();

    }


}

