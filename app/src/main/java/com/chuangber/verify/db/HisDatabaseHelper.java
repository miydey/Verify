package com.chuangber.verify.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chuangber.verify.bean.HistoryInfo;
import com.chuangber.verify.bean.IDCardInfoHd;
import com.huashi.otg.sdk.IDCardInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jinyh on 2017/4/20.
 */

public class HisDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = HisDatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 3;
    private static HisDatabaseHelper instance;
    private static final String DATABASE_NAME= "history.db";
    private static final String TABLE_NAME = "face";
    private Context mContext;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase hisData;
    private static final String CREATE_TABLE = "create table face ( id INTEGER primary key autoincrement," +
            "name TEXT," +
            "date INTEGER," +
            "sex TEXT,"+
            "cardNumber TEXT,"+
            "type TEXT," +
            "picCamera BLOB," +
            "picID BLOB," +
            "sim FLOAT )";



    public HisDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public synchronized static HisDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HisDatabaseHelper(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        final int FIRST_DATABASE_VERSION = 1;
        onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);
        //hisData = getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) { // 跨版本升级
            switch (i) {
                case 1:
                    upgradeToVersion2(db);
                    break;
                case 2:
                    upgradeToVersion3(db);
                    break;
                default:
                    break;
            }
        }
    }

    public void insertHsData(boolean upload,IDCardInfo IDinfoHs, float score, byte[] picCamera,byte[] picIDCard) {
        ContentValues values = new ContentValues();
        hisData = openDatabase();
        if (upload){ //是否已经上传
            values.put("upload",1);
        }else {
            values.put("upload",0);
        }
            values.put("sex", IDinfoHs.getSex());
            values.put("cardNumber", IDinfoHs.getIDCard());
            values.put("sim",score);
            values.put("picCamera",picCamera);
            values.put("picID",picIDCard);
            values.put("name", IDinfoHs.getPeopleName());
            values.put("address",IDinfoHs.getAddr());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR,5);
            values.put("date",calendar.getTimeInMillis());
            hisData.insert("face","name",values);
            values.clear();

    }

    public void insertHdData(boolean upload,IDCardInfoHd idCardInfoHd, float score, byte[] picCamera, byte[] picIDCard) {
        ContentValues values = new ContentValues();
        openDatabase();
        if (upload){ //是否已经上传
            values.put("upload",1);
        }else {
            values.put("upload",0);
        }
            values.put("sex", idCardInfoHd.getSex());
            values.put("cardNumber", idCardInfoHd.getIDCard());
            values.put("sim",score);
            values.put("picCamera",picCamera);
            values.put("picID",picIDCard);
            values.put("name", idCardInfoHd.getPeopleName());
            values.put("address", idCardInfoHd.getAddr());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR,5);
            values.put("date",calendar.getTimeInMillis());
        hisData.insert("face","name",values);
        values.clear();
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        String addResult = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN result TEXT";// 添加比对结果
        db.execSQL(addResult);
       // Log.d(TAG, "upgradeToVersion2 " );
    }
    private void upgradeToVersion3(SQLiteDatabase db){
        String addUpload = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN upload INTEGER";// 添加上传结果
        db.execSQL(addUpload);
        String addAddress = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN address TEXT";
        db.execSQL(addAddress);
        //Log.d(TAG, "upgradeToVersion3 " );
    }
    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
           hisData = getWritableDatabase();
        }
        return hisData;
    }
    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            hisData.close();

        }
    }

    /**
     * 获取本地的未上传的信息
     * @return history
     */
    public ArrayList<HistoryInfo> getHisList (){
        ArrayList histroy = new ArrayList<HistoryInfo>();
        String SQL_PAGE = "SELECT * FROM face where upload = 0";
        //hisData = getWritableDatabase();
        SQLiteDatabase database = openDatabase();
        Cursor cursor = database.rawQuery(SQL_PAGE,null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        for (int i = 0;i < count;i ++){
            HistoryInfo history = new HistoryInfo();
            IDCardInfo idCardInfo = new IDCardInfo();
            idCardInfo.setPeopleName(cursor.getString(1));
            idCardInfo.setSex(cursor.getString(3));
            idCardInfo.setIDCard(cursor.getString(4));
            history.setIdCardInfo(idCardInfo);
            history.setId(cursor.getInt(0));
            history.setDate(cursor.getLong(2));
            history.setPicture_camera(cursor.getBlob(6));
            history.setPicture_id(cursor.getBlob(7));
            history.setSim(cursor.getFloat(8));
            history.setAddress(cursor.getString(11));
            histroy.add(history);
            cursor.moveToNext();
        }
        return histroy;
    }

    /**
     * 将已经上传的记录状态更新为已上传
     */
    public void setUpload(int id){
        ContentValues values = new ContentValues();
        values.put("upload", 1);//key为字段名，value为值
        hisData.update("face", values, "id=?", new String[]{String.valueOf(id)});
    }
}
