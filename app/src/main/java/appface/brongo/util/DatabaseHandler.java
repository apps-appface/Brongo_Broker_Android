package appface.brongo.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.model.ApiModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 12/6/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

        // All Static variables
        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "MyDatabase";

        // Database table name
        private static final String TABLE_LIST = "MatchingList";

        // Table Columns names
        private static final String DEAL_ID = "dealid";
        private static final String ADDRESS_ITEM = "address";
    private static final String BHK_ITEM = "bhk";
    private static final String BUDGET_ITEM = "budget";
    private static final String PROPERTY_STATUS = "propertystatus";
    private static final String NAME = "name";
    private static final String INVENTORY_TYPE = "inventory_type";
    private static final String IMAGE = "image";
    private static final String COMMISSION = "commission";
    private static final String AREA = "area";
    private static final String POSTING_TYPE = "postingtype";
    private static final String PROPERTY_TYPE = "propertytype";
    private static final String SUBPROPERTY = "subproperty";
    private static final String MOBILE_ITEM = "mobile";

    public DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_LIST + "(" + DEAL_ID
                    + " TEXT PRIMARY KEY," + ADDRESS_ITEM + " TEXT," + BHK_ITEM + " TEXT DEFAULT \"\","+BUDGET_ITEM + " TEXT DEFAULT \"\","+PROPERTY_STATUS + " TEXT DEFAULT \"\","+SUBPROPERTY + " TEXT DEFAULT \"\","+ NAME + " TEXT DEFAULT \"\","+
                    INVENTORY_TYPE + " TEXT DEFAULT \"\","+IMAGE + " TEXT DEFAULT \"\","+COMMISSION + " TEXT DEFAULT \"\","+AREA + " TEXT DEFAULT \"\","+POSTING_TYPE + " TEXT DEFAULT \"\","+PROPERTY_TYPE + " TEXT DEFAULT \"\","+MOBILE_ITEM + " TEXT DEFAULT \"\""+")";

            db.execSQL(CREATE_LIST_TABLE);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);

            // Create tables again
            onCreate(db);
        }
    public void addListItem(ArrayList<ApiModel.InventoryPersoanlList> listItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);

        // Create tables again
        onCreate(db);
        ContentValues values = new ContentValues();
        for (int i = 0; i < listItem.size(); i++) {

            Log.e("vlaue inserting==", "" + listItem.get(i));
            values.put(DEAL_ID,listItem.get(i).getPropertyId());
            values.put(ADDRESS_ITEM, listItem.get(i).getMicroMarketName());
            values.put(BHK_ITEM, listItem.get(i).getBedRoomType());
            values.put(BUDGET_ITEM, listItem.get(i).getBudget());
            values.put(PROPERTY_STATUS, listItem.get(i).getPropertyStatus());
            values.put(SUBPROPERTY, listItem.get(i).getSubPropertyType());
            values.put(NAME, listItem.get(i).getClientName());
            values.put(INVENTORY_TYPE, "personal");
            values.put(IMAGE, listItem.get(i).getPropertyImage1());
            values.put(POSTING_TYPE, listItem.get(i).getPostingType());
            values.put(PROPERTY_TYPE, listItem.get(i).getPropertyType());
            values.put(MOBILE_ITEM, listItem.get(i).getClientMobileNo());
            db.insert(TABLE_LIST, null, values);
        }

        db.close(); // Closing database connection
    }

    public Cursor getListItem() {
        String selectQuery = "SELECT  * FROM " + TABLE_LIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

}
