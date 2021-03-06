package com.example.androidapp.Data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.androidapp.Data.ClientData.Client;
import com.example.androidapp.Data.ClientData.ClientDao;
import com.example.androidapp.Data.DayRevenueData.DayRevenue;
import com.example.androidapp.Data.DayRevenueData.DayRevenueDao;
import com.example.androidapp.Data.HistoryOrder.HistoryOrder;
import com.example.androidapp.Data.HistoryOrder.HistoryOrderDao;
import com.example.androidapp.Data.MonthRevenueData.MonthRevenue;
import com.example.androidapp.Data.MonthRevenueData.MonthRevenueDao;
import com.example.androidapp.Data.OrderData.OrderTodayData.DataConverter;
import com.example.androidapp.Data.OrderData.OrderTodayData.Order;
import com.example.androidapp.Data.OrderData.OrderUnpaidData.UnpaidOrder;
import com.example.androidapp.Data.OrderData.OrderUnpaidData.UnpaidOrderDao;
import com.example.androidapp.Data.OrderData.OrderUpcomingData.UpcomingOrder;
import com.example.androidapp.Data.OrderData.OrderTodayData.OrderDao;
import com.example.androidapp.Data.OrderData.OrderUpcomingData.UpcomingOrderDao;
import com.example.androidapp.Data.ProductData.ProductDao;
import com.example.androidapp.Data.ProductDetailData.ProductDetail;
import com.example.androidapp.Data.ProductDetailData.ProductDetailDao;
import com.example.androidapp.Data.ProductType.ProductType;
import com.example.androidapp.Data.ProductType.ProductTypeDao;
import com.example.androidapp.Data.ProductData.Product;
import com.example.androidapp.Data.ProductTypeRevenue.ProductTypeRevenue;
import com.example.androidapp.Data.ProductTypeRevenue.ProductTypeRevenueDao;
import com.example.androidapp.HelperClass.DateConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

//App database
//Add more entities (tables) to database by listing them inside {}

@Database(entities = {Client.class, Order.class, UpcomingOrder.class, ProductType.class,
        Product.class, ProductDetail.class, MonthRevenue.class, DayRevenue.class,
        ProductTypeRevenue.class, HistoryOrder.class, UnpaidOrder.class}, version = 6)

@TypeConverters({DataConverter.class})
public abstract class AppDatabase extends RoomDatabase{
    private static final String DATABASE_NAME = "database.db";
    private static AppDatabase instance;

    //Entities' DAO
    public abstract ClientDao clientDao();
    public abstract OrderDao orderDao();
    public abstract ProductDao productDao();
    public abstract UpcomingOrderDao upcomingOrderDao();
    public abstract ProductTypeDao productTypeDao();
    public abstract ProductDetailDao productDetailDao();
    public abstract MonthRevenueDao monthRevenueDao();
    public abstract DayRevenueDao dayRevenueDao();
    public abstract ProductTypeRevenueDao productTypeRevenueDao();
    public abstract HistoryOrderDao historyOrderDao();
    public abstract UnpaidOrderDao unpaidOrderDao();



    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private ClientDao clientDao;
//        private DishDao dishDao;
        private OrderDao orderDao;
//        private UnpaidOrderDao unpaidOrderDao;
        private UpcomingOrderDao upcomingOrderDao;
        private ProductTypeDao productTypeDao;
        private ProductDao productDao;
        private ProductDetailDao productDetailDao;
//        private HistoryOrderDao historyOrderDao;
        private MonthRevenueDao monthRevenueDao;
        private DayRevenueDao dayRevenueDao;
        private ProductTypeRevenueDao productTypeRevenueDao;
        private HistoryOrderDao historyOrderDao;
        private UnpaidOrderDao unpaidOrderDao;
        private PopulateDbAsyncTask(AppDatabase db) {
            clientDao = db.clientDao();
//            dishDao = db.dishDao();
            orderDao = db.orderDao();
            unpaidOrderDao = db.unpaidOrderDao();
            upcomingOrderDao = db.upcomingOrderDao();
//            historyOrderDao = db.historyOrderDao();
            productTypeDao = db.productTypeDao();
            productDao = db.productDao();
            productDetailDao = db.productDetailDao();
            monthRevenueDao = db.monthRevenueDao();
            dayRevenueDao = db.dayRevenueDao();
            productTypeRevenueDao = db.productTypeRevenueDao();
            historyOrderDao = db.historyOrderDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

//    private static AppDatabase buildDatabase(final Context context){
//        return Room.databaseBuilder(context, AppDatabase.class, "database.db")
//                .addCallback(new Callback() {
//                    @Override
//                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                        super.onCreate(db);
//                        Executors.newSingleThreadExecutor().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                Date nowDate = Calendar.getInstance().getTime();
//                                getInstance(context).monthRevenueDao().insertMonthRevenue(new MonthRevenue(nowDate, 0, 0));
//                            }
//                        });
//                    }
//                }).build();
//    }
}
