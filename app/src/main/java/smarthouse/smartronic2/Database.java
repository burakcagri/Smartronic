package smarthouse.smartronic2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by burak on 03/09/15.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Veritabani";

    private String SCENES = "scenes";
    private String CATEGORIES = "categories";
    private String SWITCH = "switch";
    private String ROOMS = "rooms";
    private String CAMERA = "camera";
    private String SECTIONS = "sections";

    private String ID = "id";
    private String STATUS = "status";
    private String SECTION = "section";
    private String ROOM = "room";
    private String ACTIVE = "active";
    private String SECTION_ID = "section_id";
    private String SWITCH_ID = "switch_id";
    private String SCENE_ID = "scene_id";
    private String CAMERA_ID = "camera_id";
    private String ALTID = "altid";
    private String VIDEO_URLS = "videourls";
    private String CATEGORY = "category";
    private String SUBCATEGORY = "subcategory";
    private String PARENT = "parent";
    private String IP = "ip";
    private String STREAMING = "streaming";
    private String URL = "url";
    private String KWH = "kwh";
    private String WATTS = "watts";
    private String STATE = "state";
    private String COMMENT = "comment";
    private String ROOM_ID = "room_id";
    private String NAME = "name";
    private String DEVICES = "devices";
    private String DEVICE_ID = "device_id";
    private String XML_ID = "xml_id";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ROOM = "CREATE TABLE " + ROOMS + " (" + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME
                + " TEXT," + ID + " TEXT," + SECTION + " TEXT)";
        db.execSQL(CREATE_ROOM);

        String CREATE_CAMERA = "CREATE TABLE " + " " + CAMERA + " (" + CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ALTID + " TEXT," + ID + " TEXT," + VIDEO_URLS + " TEXT," + CATEGORY + " TEXT," + SUBCATEGORY +
                " TEXT," + ROOM + " TEXT," + PARENT + " TEXT," + IP + " TEXT," + STREAMING + " TEXT," + URL + " TEXT" +
                ")";
        db.execSQL(CREATE_CAMERA);

        String CREATE_CATEGORIES = "CREATE TABLE " + " " + CATEGORIES + " (" + CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT)";
        db.execSQL(CREATE_CATEGORIES);

        String CREATE_SWITCH = "CREATE TABLE " + SWITCH + "(" + SWITCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ALTID + " TEXT," + ID + " TEXT," + CATEGORY + " TEXT," + SUBCATEGORY + " TEXT," + ROOM +
                " TEXT," + PARENT + " TEXT," + STATUS + " TEXT," + KWH + " TEXT," + WATTS + " TEXT," + STATE + " TEXT," + COMMENT + " TEXT" +
                ")";
        db.execSQL(CREATE_SWITCH);

        String CREATE_SECTIONS = "CREATE TABLE " + SECTIONS + " (" + SECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT)";
        db.execSQL(CREATE_SECTIONS);

        String CREATE_SCENES = "CREATE TABLE " + " " + SCENES + " (" + SCENE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME +
                " TEXT," + ID + " TEXT," + ROOM + " TEXT," + ACTIVE + " TEXT)";
        db.execSQL(CREATE_SCENES);

        String CREATE_DEVICES = "CREATE TABLE " + " " + DEVICES + " (" + DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + XML_ID +
                " TEXT," + NAME + " TEXT," + CATEGORY + " TEXT)";
        db.execSQL(CREATE_DEVICES);

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
        String countQuery = "SELECT * FROM " + table;
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

    public void insertSectionsData(String name, String id) {
    }

    public void insertRoomsData(String name, String id, int section) {
    }

    public void insertScenesData(String active, String name, String id, String room) {
    }

    public void insertCategoriesData(String name, String id) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!checkAlreadyHas(id, CATEGORIES)) {
            values.put(NAME, name);
            values.put(ID, id);
            database.insert(CATEGORIES, null, values);
        }
        database.close();
    }

    public void insertSwitchData(String name, String altid, String id, String category, String subcategory, String room,
                                 String parent, String status, String kwh, String watts, String state, String comment) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!checkAlreadyHas(id, SWITCH)) {
            values.put(NAME, name);
            values.put(ALTID, altid);
            values.put(ID, id);
            values.put(CATEGORY, category);
            values.put(SUBCATEGORY, subcategory);
            values.put(ROOM, room);
            values.put(PARENT, parent);
            values.put(STATUS, status);
            values.put(KWH, kwh);
            values.put(WATTS, watts);
            values.put(STATE, state);
            values.put(COMMENT, comment);
            database.insert(SWITCH, null, values);
        }
        database.close();

    }

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    private boolean checkAlreadyHas(String id, String table) {
        boolean response = false;
        String countQuery = "SELECT * FROM " + table + " WHERE id=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        if (rowCount > 0) {
            response = true;
        }
        cursor.close();
        return response;
    }

    private boolean checkAlreadyHasName(String name, String table) {
        boolean response = false;
        String countQuery = "SELECT * FROM " + table + " WHERE name=" + name;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        if (rowCount > 0) {
            response = true;
        }
        cursor.close();
        return response;
    }

    public void insertCameraData(String name, String altid, String id, String category, String subcategory, String room, String parent, String ip, String url, String streaming, String commands, String videourls, String state, String comment) {
    }

    public int categoryIds() {
        String query = "SELECT id from categories";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int response = 0;
        int i = 0;
        while (cursor.moveToNext()) {
            String entry = cursor.getString(i);
            System.out.println("CATEGORY IS:" + entry);
            response = Integer.parseInt(entry);
        }
        db.close();
        cursor.close();
        return response;
    }

    public void insertDevices(String s, String name, String category) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!checkAlreadyHasName(name, DEVICES)) {
            values.put(XML_ID, s);
            values.put(NAME, name);
            values.put(CATEGORY, category);
            database.insert(DEVICES, null, values);
        }
        database.close();
    }

    public String categoryNames() {

        String query = "SELECT name from categories";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String response = "";
        int i = 0;
        while (cursor.moveToNext()) {
            response = cursor.getString(i);
            System.out.println("CATEGORY IS:" + response);
            i++;
        }
        db.close();
        cursor.close();
        return response;

    }
}
