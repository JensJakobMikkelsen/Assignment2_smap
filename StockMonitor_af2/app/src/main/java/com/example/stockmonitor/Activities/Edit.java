package com.example.stockmonitor.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockmonitor.Bookmodel_for_service.ListModel;
import com.example.stockmonitor.R;
import com.example.stockmonitor.Service.stockService;
//import com.example.stockmonitor.stockService;

public class Edit extends AppCompatActivity {


    int position;

    private stockService stockService;
    private ServiceConnection serviceConnection;
    private boolean bound = false;
    ListModel currentStock;
    TextView nameOfStock_, priceOfStock_, amountOfStock_;


    // Made with inspiration from ServiceDemo from class
    private void setupConnectionToStockService() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been established
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = ((stockService.stockUpdateServiceBinder) service).getService();

                currentStock = stockService.getUser(position);
                updateUI();
                // Update data as soon as we are connected.
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Intents
        setupConnectionToStockService();

        Intent intent = new Intent(Edit.this, stockService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;

        Intent get_intent = getIntent();

        position = get_intent.getIntExtra("position", 0);
        // Buttons

        Button Cancel = findViewById(R.id.edit_Cancel_btn);
        Button Save = findViewById(R.id.edit_Save_btn);

        //TextViews

        nameOfStock_ = findViewById(R.id.edit_Name);
        priceOfStock_ = findViewById(R.id.edit_Price);
        amountOfStock_ = findViewById(R.id.edit_Stocks);

        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name", name);
            price = savedInstanceState.getString("price", price);
            stockAmount = savedInstanceState.getString("stockAmount", stockAmount);
        }

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailsActivity();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyStockActivity();

            }
        });


    }

    public void startMyStockActivity()
    {
        Intent intent = new Intent(this, myStock.class);

        //intent.setAction(Intent.ACTION_SEND);

        updateData();

        double buy_price = 0;

        buy_price = Double.parseDouble(priceOfStock_.getText().toString());

        currentStock.setAmount(amountOfStock_.getText().toString());
        currentStock.setBought_for(buy_price);
        currentStock.setName(nameOfStock_.getText().toString());

        stockService.setStockListByPosition_ListModel(position, currentStock);

        intent.putExtra("position", position);

        setResult(this.RESULT_OK, intent);
        finish();
    }

    public void updateData() {
        name = nameOfStock_.getText().toString();
        price = priceOfStock_.getText().toString();
        stockAmount = amountOfStock_.getText().toString();
    }

    public void startDetailsActivity()
    {
        Intent intent = new Intent(this, Details.class);

        setResult(this.RESULT_CANCELED, intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        //stopService(bound);
        super.onDestroy();

        if (bound) {
            // Detach our existing connection.
            unbindService(serviceConnection);
            bound = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        updateData();

        savedInstanceState.putString("name", name);
        savedInstanceState.putString("price", price);
        savedInstanceState.putString("stockAmount", stockAmount);
    }

    String name, price, stockAmount;

    public void updateUI(){
        // If symbol is null savedInstanceState haven't happened.
        if(name == null){
            name = currentStock.getName();
            price = Double.toString(currentStock.getBought_for());
            stockAmount = currentStock.getAmount();
        }
        /*
        String stockName = currentStock.getName();
        if (stockName == "Unknown"){
            stockName = currentStock.getName();
        }
        */
        nameOfStock_.setText(name);
        priceOfStock_.setText(price);
        amountOfStock_.setText(stockAmount);
    }


}
