package com.example.stockmonitor.Activities;
import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stockmonitor.Adapter.CustomAdapter;
import com.example.stockmonitor.Bookmodel_for_service.ListModel;
import com.example.stockmonitor.Database.AppDatabase;
import com.example.stockmonitor.R;
import com.example.stockmonitor.Service.stockService;
//import com.example.stockmonitor.stockService;

import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.List;



public class myStock extends AppCompatActivity {


    String nameOfStock;
    String priceOfStock;
    String amountOfStock;
    int checkBoxChecked = 5;
    int lastPosition;
    JSONObject data = null;
    boolean bound = false;
    final int DETAILSREQUEST = 114;
    final int RESULT_DELETE = 115;
    final int DATAREFRESHED = 118;
    String addAndCheckData_price = "";
    int only_once = 0;
    String dialogName;
    String dialogPrimaryExchange;
    String dialogAmount;
    int i = 0;
    double firstPrice;
    String price;
    double price_double;
    double change_in_price;
    String changeInPrice_string;

    //Binding to service

    private stockService stockService;
    private ServiceConnection serviceConnection;


    private void setupConnectionToStockService() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been established
                //ref: http://developer.android.com/reference/android/app/Service.html
                stockService = ((stockService.stockUpdateServiceBinder) service).getService();

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            context_db= new WeakReference<>(getApplicationContext());
            AppDatabase refresh_db  = AppDatabase.getDatabase(context_db.get());

            if(message.equals("StartStockServiceMessage"))
            {
                if(bound)
                {

                    adapter.clear();

                    for(int i = 0; i < stockService.getStockList().size(); ++i) {
                        ListModel tempUser = stockService.getStockList().get(i);
                        adapter.add(tempUser);
                        adapter.notifyDataSetChanged();
                        refresh_db.daoAccess().update(tempUser);
                    }

                }
            }

            else if(message.equals("Refresh"))
            {
                if(bound)
                {

                    adapter.clear();

                for(int i = 0; i < stockService.getStockList().size(); ++i) {
                    ListModel tempUser = stockService.getStockList().get(i);
                        adapter.add(tempUser);
                        adapter.notifyDataSetChanged();
                        refresh_db.daoAccess().update(tempUser);
                    }

                }

                only_once++;
            }

            else if(message.equals("Didnt_work"))
            {
                Toast.makeText(getApplicationContext(), "Couldn't find stock", Toast.LENGTH_LONG).show();
            }

            else if(message.equals("Add"))
            {
                ListModel tempUser = stockService.getStockList().get(stockService.getStockList().size()-1);
                int hej = 0;
                adapter.add(tempUser);
                adapter.notifyDataSetChanged();
                refresh_db.daoAccess().insert(tempUser);
            }

        }


    };

    AppDatabase appDatabase;
    AppDatabase mydb;

    CustomAdapter adapter;
    ListView listView;

    private static WeakReference<Context> contextRef;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stock);

        Intent backgroundServiceIntent = new Intent(myStock.this, stockService.class);
        startService(backgroundServiceIntent);

        // Attach the adapter to a ListView

        listView = (ListView) findViewById(R.id.stock_listView);
        adapter = new CustomAdapter(this);
        listView.setAdapter(adapter);

        AlertDialog.Builder alertDialogName = new AlertDialog.Builder(myStock.this);

        // set title
        alertDialogName.setTitle("Enter Name");


        final EditText E_input_name = new EditText (this);
        final EditText E_input_primary_exchange = new EditText (this);
        final EditText E_input_amount = new EditText (this);

        E_input_amount.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertDialogName.setView(E_input_name);


        // set dialog message
        alertDialogName
                //.setMessage("NEJ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialogName = E_input_name.getText().toString();
                        stockService.addAndCheck(dialogName);


                    }

                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

        final AlertDialog alertDialog_name = alertDialogName.create();


        //Buttons
        final Button refresh = findViewById(R.id.drefresh_btn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bound) {
                    stockService.refresh();
                }
            }

        });


        //Listview

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(myStock.this, Details.class);

                intent.putExtra("position", position);
                startActivityForResult(intent, DETAILSREQUEST);

            }
        });

        Button addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                alertDialog_name.show();
            }
        });

    }

    private static WeakReference<Context> context_db;


    //OnActivityResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DETAILSREQUEST)
            if (resultCode == RESULT_DELETE) {

                int position_ = data.getIntExtra("position", 0);

                adapter.remove(adapter.getItem(position_));

            } else if (resultCode == DATAREFRESHED) {
                ListModel tempUser = stockService.getStockList().get(data.getIntExtra("position", 0));

                adapter.clear();

                for (int i = 0; i < stockService.getStockList().size(); i++) {
                    adapter.add(stockService.getUser(i));
                }

            } else if (resultCode == RESULT_OK) {

                lastPosition = data.getIntExtra("position", 0);

                if (bound && stockService != null) {

                    ListModel tempUser = stockService.getUser(lastPosition);
                    double pricePaid_d = tempUser.getBought_for();

                    String currentPrice = tempUser.getPrice();
                    double currentPrice_d = 0;

                    try {
                        currentPrice_d = Double.parseDouble(currentPrice);
                    } catch (NumberFormatException nfe) {
                    }

                    double changeInPrice;
                    String changeInPrice_s = "";

                    if (pricePaid_d > currentPrice_d) {
                        changeInPrice = pricePaid_d - currentPrice_d;
                        try {
                            changeInPrice_s = "- " + Double.toString(changeInPrice);
                        } catch (NumberFormatException nfe) {
                        }

                    } else if (pricePaid_d < currentPrice_d) {
                        changeInPrice = currentPrice_d - pricePaid_d;
                        try {
                            changeInPrice_s = Double.toString(changeInPrice);
                        } catch (NumberFormatException nfe) {
                        }
                    }

                    tempUser.setC_price(changeInPrice_s);
                    stockService.setStockListByPosition_ListModel(lastPosition, tempUser);

                    int count = adapter.getCount();
                    adapter.clear();

                    List<ListModel> list = stockService.getStockList();

                    for (int i = 0; i < count; i++) {
                        adapter.add(list.get(i));
                    }

                    context_db = new WeakReference<>(getApplicationContext());
                    AppDatabase edit_db = AppDatabase.getDatabase(context_db.get());

                    edit_db.daoAccess().updateAll(tempUser);

                }
            } else if (resultCode == RESULT_CANCELED) {

            }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //stopService(bound);

        if(bound) {
            unbindService(serviceConnection);
            bound = false;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {

        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(stockService.BROADCAST_DATA_UPDATED);
        setupConnectionToStockService();

        Intent intent = new Intent(myStock.this, stockService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Refresh"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


}
