package smarthouse.smartronic2;

/**
 * Created by burak on 19/03/15.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Smartronic";

    String SCENES = "scenes";
    String CATEGORIES = "categories";
    String SWITCH = "switch";
    String ROOMS = "rooms";
    String CAMERA = "camera";
    String SECTIONS = "sections";

    String ID = "id";
    String STATUS = "status";
    String SECTION = "section";
    String ROOM = "room";
    String ACTIVE = "active";
    String SECTION_ID = "section_id";
    String SWITCH_ID = "switch_id";
    String SCENE_ID = "scene_id";
    String CAMERA_ID = "camera_id";
    String ALTID = "altid";
    String VIDEO_URLS = "videourls";
    String CATEGORY = "category";
    String SUBCATEGORY = "subcategory";
    String PARENT = "parent";
    String IP = "ip";
    String STREAMING = "streaming";
    String URL = "url";
    String KWH = "kwh";
    String WATTS = "watts";
    String STATE = "state";
    String COMMENT = "comment";
    String ROOM_ID = "room_id";
    String NAME = "name";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ROOM = "CREATE TABLE" + " " + ROOMS + "(" + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME
                + " TEXT," + ID + " TEXT," + SECTION + " TEXT)";
        db.execSQL(CREATE_ROOM);

        String CREATE_CAMERA = "CREATE TABLE" + " " + CAMERA + "(" + CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ALTID + " TEXT," + ID + " TEXT," + VIDEO_URLS + " TEXT," + CATEGORY + " TEXT," + SUBCATEGORY +
                " TEXT," + ROOM + " TEXT," + PARENT + " TEXT," + IP + " TEXT," + STREAMING + " TEXT," + URL + " TEXT," +
                ")";
        db.execSQL(CREATE_CAMERA);

        String CREATE_CATEGORIES = "CREATE TABLE" + " " + CATEGORIES + "(" + CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT)";
        db.execSQL(CREATE_CATEGORIES);

        String CREATE_SWITCH = "CREATE TABLE" + " " + SWITCH + "(" + SWITCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ALTID + " TEXT," + ID + " TEXT," + CATEGORY + " TEXT," + SUBCATEGORY + " TEXT," + ROOM +
                " TEXT," + PARENT + " TEXT," + STATUS + " TEXT," + KWH + " TEXT," + WATTS + " TEXT," + STATE + " TEXT," + COMMENT + " TEXT" +
                ")";
        db.execSQL(CREATE_SWITCH);

        String CREATE_SECTIONS = "CREATE TABLE" + " " + SECTIONS + "(" + SECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT)";
        db.execSQL(CREATE_SECTIONS);

        String CREATE_SCENES = "CREATE TABLE" + " " + SCENES + "(" + SCENE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT," + ROOM + " TEXT," + ACTIVE + " TEXT)";
        db.execSQL(CREATE_SCENES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteRoom(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ROOM, "room_xml_id" + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public String getRoomText(String id) {
        String query = "SELECT" + " " + NAME + " FROM " + ROOM + " where room_xml_id=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String response = cursor.toString();
        db.close();
        cursor.close();
        return response;
    }

    public void updateRoomText(String id, String value) {
        String input = getRoomText(id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, value);

        db.update(ROOM, values, input, new String[]{String.valueOf(value)});
    }

    public void setLayoutId(String generatedButtonId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("room_xml_id", generatedButtonId);

        db.update(ROOM, values, name, new String[]{String.valueOf(generatedButtonId)});
    }

    public void addRoom(String id, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("room_xml_id", id);
        values.put(NAME, value);

        db.insert(ROOM, null, values);
        db.close();
    }

    public int getRowCount(String table) {
        String countQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    public String getIdWithXMLId(int id) {
        String query = "SELECT" + " room_xml_id FROM " + ROOM + " where " + ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String response = cursor.toString();
        db.close();
        cursor.close();
        return response;
    }
}
