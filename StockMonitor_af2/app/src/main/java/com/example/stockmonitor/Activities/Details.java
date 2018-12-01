package com.example.stockmonitor.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stockmonitor.Bookmodel_for_service.ListModel;
import com.example.stockmonitor.Database.AppDatabase;
import com.example.stockmonitor.R;
import com.example.stockmonitor.Service.stockService;
//import com.example.stockmonitor.stockService;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class Details extends AppCompatActivity implements Serializable {

    String nameOfStock;
    double priceOfStock_d;
    String timeStamp;
    String amountOfStock;

    int checkBoxChecked;
    final int RESULT_DELETE = 115;
    int position;
    String sector;
    ListModel currentStock;

    TextView stockNameTV, stockPriceTV, stockAmountTV,
            stockSectorTV, timeStamp_, bought_for;
    private static WeakReference<Context> context_db;

    final int DETAILSREQUEST = 114;
    final int EDITREQUEST = 117;
    final int DATAREFRESHED = 118;
    boolean dataRefreshed = false;

    private stockService stockService;
    private ServiceConnection serviceConnection;
    private boolean bound = false;

    //Broadcastreceiver

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");


            if (message == "Refresh") {

            }

        }


    };

    private void setupConnectionToStockService() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been established
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = ((stockService.stockUpdateServiceBinder) service).getService();
                // Update data as soon as we are connected.
                updateUI();
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = null;
            }
        };
    }

    public void updateUI(){
        currentStock = stockService.getStockList().get(position);

        stockNameTV.setText(currentStock.getCompanyName());
        stockPriceTV.setText(currentStock.getPrice());
        stockAmountTV.setText(currentStock.getAmount());
        timeStamp_.setText(currentStock.getTimeStamp());
        stockSectorTV.setText(currentStock.getSector());
        bought_for.setText(Double.toString(currentStock.getBought_for()));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Intents

        Intent intent_get = getIntent();

        position = intent_get.getIntExtra("position", 0);
        checkBoxChecked = intent_get.getIntExtra("checkBoxChecked", 0);


        stockNameTV = findViewById(R.id.txt_box_Name);
        stockPriceTV = findViewById(R.id.txt_box_Price);
        stockAmountTV = findViewById(R.id.txt_box_AmountOfStocks);
        stockSectorTV = findViewById(R.id.txt_box_sector);
        timeStamp_ = findViewById(R.id.timeStamp);
        bought_for = findViewById(R.id.bought_for);

        setupConnectionToStockService();

        Intent intent = new Intent(Details.this, stockService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;

        if(checkBoxChecked == 5)
        {

        }

        Button delete = findViewById(R.id.Delete_btn);

        // Buttons

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context_db = new WeakReference<>(getApplicationContext());
                AppDatabase edit_db = AppDatabase.getDatabase(context_db.get());
                edit_db.daoAccess().delete(stockService.getUser(position));
                stockService.deleteStock(position);

                Intent intent = new Intent(getApplicationContext(), myStock.class);
                intent.putExtra("position", position);
                setResult(RESULT_DELETE, intent);

                finish();

            }
        });

        Button back = findViewById(R.id.Back_btn);
        Button edit = findViewById(R.id.Edit_btn);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), myStock.class);

                if(!dataRefreshed) {

                    setResult(RESULT_CANCELED, intent);
                }

                else if (dataRefreshed)
                {
                    Intent intent_myStock = new Intent(getApplicationContext(), myStock.class);
                    intent_myStock.putExtra("position", position);
                    setResult(DATAREFRESHED, intent_myStock);
                }

                finish();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity();
            }
        });

    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        unbindService(serviceConnection);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==EDITREQUEST){
            if(resultCode==RESULT_OK){
                setResult(RESULT_OK, data);
                finish();
            }

            else if(resultCode == RESULT_CANCELED)
            {
                //Do nothing
            }
        }
    }

    public void startEditActivity()
    {
        Intent intent = new Intent(this, Edit.class);
        intent.putExtra("position", position);

        startActivityForResult(intent, EDITREQUEST);
    }


}
