package com.example.stockmonitor.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.stockmonitor.Bookmodel_for_service.ListModel;

import java.lang.ref.WeakReference;

@Database(entities = {ListModel.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {


    //Most come from https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#0

    static final Migration MIGRATION_1_2 = new Migration(1, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public abstract DaoAccess daoAccess();

    private static volatile AppDatabase INSTANCE;

    private static WeakReference<Context> contextRef;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    contextRef = new WeakReference<>(context);

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "stockDB")
                            //.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new PopulateDbAsync(INSTANCE, contextRef.get()).execute();
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                }
                            })
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    new PopulateDbAsync(INSTANCE, contextRef.get()).execute();
                }
            };


    //Pre-populate database

    public static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final DaoAccess mDao;

        PopulateDbAsync(AppDatabase db, Context context) {
            mDao = db.daoAccess();
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.deleteAll();

            final ListModel FB = new ListModel("FB", "0", "0", "Internet", "0");
            final ListModel NFLX = new ListModel("NFLX", "0", "0", "Wikipedia", "0");
            final ListModel AMZN = new ListModel("AMZN", "0", "0", "Stuff", "0");
            final ListModel ADSK = new ListModel("ADSK", "0", "0", "movies and shit", "0");
            final ListModel MSFT = new ListModel("MSFT", "0", "0", "movies and shit", "0");
            final ListModel MDLZ = new ListModel("MDLZ", "0", "0", "movies and shit", "0");
            final ListModel TSLA = new ListModel("TSLA", "0", "0", "movies and shit", "0");
            final ListModel CTXS = new ListModel("CTXS", "0", "0", "movies and shit", "0");
            final ListModel HSIC = new ListModel("HSIC", "0", "0", "movies and shit", "0");
            final ListModel QRTEA = new ListModel("QRTEA", "0", "0", "movies and shit", "0");

            mDao.insert(FB);
            mDao.insert(NFLX);
            mDao.insert(AMZN);
            mDao.insert(ADSK);
            mDao.insert(MSFT);
            mDao.insert(MDLZ);
            mDao.insert(TSLA);
            mDao.insert(CTXS);
            mDao.insert(HSIC);
            mDao.insert(QRTEA);

            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", "databasePopulated");
            LocalBroadcastManager.getInstance(contextRef.get()).sendBroadcast(intent);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", "databasePopulated");
            LocalBroadcastManager.getInstance(contextRef.get()).sendBroadcast(intent);

        }

    }

}

