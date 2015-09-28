package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends ActionBarActivity {

    EditText ed1;
    EditText ed2;
    Button loginButton;
    TextView txt;
    LinkedHashMap<String, StringBuilder> URLMap = new LinkedHashMap<>();
    String registeredUsername, username, password, encodedUsername;

    String salt = "oZ7QE6LcLJp6fiWzdqZc";
    String errorFormat = "";
    Boolean error = false;
    String selectedOption;
    String InternalIp = "";
    String serverDevice = "";
    String identityToken;
    int responseCode = 0;
    Context context;
    Boolean isStopped = false;
    String type = "";
    boolean indexAccess = false;
    String pk_device = "";
    String[] PK_Devices, Internal_Ips;
    CharSequence[] items;
    Database database;
    Methods methods;

    //String URLLogin = "https://vera-us-oem-autha.mios.com/autha/auth/username/";
    //String getTheDevices = "https://vera-us-oem-authd.mios.com/locator/locator/";
    //String requestToGateway = "https://vera-us-oem-device11.mios.com/device/device/device/";
    String CommandURL = "http://";
    //String usingRelayServer = "device/device/device/";
    //String sessionKeyServer = "relay/relay/relay/device/";
    //String mmsSession = "/info/session/token";
    //String verifyURL = "/authd/auth/provision";

    LinkedHashMap<String, String> postData = new LinkedHashMap<>();
    LoginCheck loginCheck = new LoginCheck();

    int say = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBroadcastReceiver();
        registerUIReceiver();
        methods = new Methods(getApplicationContext());
        database = new Database(getApplicationContext());

        registeredUsername = "burak";


        methods.createSPreferences(getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("Server_Autha", getString(R.string.server_autha),
                getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("Server_Autha_Alt", getString(R.string.server_autha_alt),
                getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("Server_Authd", getString(R.string.server_authd),
                getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("Server_Authd_Alt", getString(R.string.server_authd_alt),
                getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("AppKey", "",
                getString(R.string.predefined_variables), getApplicationContext());
        methods.updateSPreferences("PK_Oem", "",
                getString(R.string.predefined_variables), getApplicationContext());

        //PK_Device and HWKey will be checked here whether they have been stored or not.
        if ((methods.getSPreferences(getString(R.string.preferences_initial), "PK_Device", getApplicationContext()) == null ||
                methods.getSPreferences(getString(R.string.preferences_initial), "PK_Device", getApplicationContext()) == "") &&
                (methods.getSPreferences(getString(R.string.preferences_initial), "HWKey", getApplicationContext()) == null ||
                        methods.getSPreferences(getString(R.string.preferences_initial), "PK_Device", getApplicationContext()) == "")) {

            // call request to get PK_Device and HWKey
            // And store them on sharedPreferences

            String response = methods.handleURLConnections(getString(R.string.server_authd)
                    + getString(R.string.verifyURL), null, 3, "PUT", getApplicationContext(), 0);

            String PK_Device = "";
            String HWKey = "";

            try {
                JSONObject jsonArray = new JSONObject(response);
                PK_Device = (String) jsonArray.get(getString(R.string.pk_device));
                HWKey = (String) jsonArray.get(getString(R.string.hw_key));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            methods.updateSPreferences(getString(R.string.pk_device), PK_Device, getString(R.string.preferences_initial), getApplicationContext());
            methods.updateSPreferences(getString(R.string.hw_key), HWKey, getString(R.string.preferences_initial), getApplicationContext());
        }
        context = getApplicationContext();

        URLMap.put("1", new StringBuilder(getString(R.string.URLLogin)));
        URLMap.put("2", new StringBuilder(getString(R.string.getTheDevices)));
        URLMap.put("3", new StringBuilder(getString(R.string.requestToGateway)));
        URLMap.put("4", new StringBuilder(CommandURL));
        URLMap.put("5", new StringBuilder(getString(R.string.usingRelayServer)));
        URLMap.put("6", new StringBuilder(getString(R.string.sessionKeyServer)));
        URLMap.put("7", new StringBuilder(getString(R.string.mmsSession)));

        loginButton = (Button) findViewById(R.id.LoginButton);
        ed1 = (EditText) findViewById(R.id.UsernameEditText);
        ed2 = (EditText) findViewById(R.id.PasswordEditText);
        txt = (TextView) findViewById(R.id.forgot_text);

        //ed1.setText(methods.getSPreferences(getString(R.string.preferences_initial), getString(R.string.username), getApplicationContext()));

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // this method call is very controversial
                //cachedMemoryVariables("x", "y", "z", "t", "u", "i", "m");

                password = ed2.getText().toString();
                username = ed1.getText().toString();

                if (username.matches("") || username == null) {
                    errorFormat += "Username is empty";
                    error = true;
                }

                if (password.matches("") || password == null) {
                    errorFormat += "Password is empty";
                    error = true;
                }

                /*if (!database.loginCheck(password, username)) {
                    errorFormat += "Login is incorrect";
                    error = true;
                }*/

                if (error) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(errorFormat);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(RESULT_OK, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ed2.setText("");
                            errorFormat = "";
                            error = false;
                        }
                    });
                    alertDialog.show();
                } else {
                    new LoginCheck().execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.sign_in);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        CharSequence signInOption = item.getTitle();
        String signInString = getResources().getString(R.string.sign_in);

        if (item.hasSubMenu()) {
            if (signInOption == signInString) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            } else {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, getString(R.string.an_error), Toast.LENGTH_SHORT);
                toast.show();
            }
            selectedOption = item.getTitle().toString();
        }
        return true;
    }


    public void ForgotPass(View view) {
        Intent intent = new Intent(MainActivity.this, Forgot.class);
        startActivity(intent);
    }

    class LoginCheck extends AsyncTask<Void, Void, Void> {
        String itemValue;
        ListView listView;
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        public void showLoadingDialog() {
            if (progress == null) {
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle(getString(R.string.loading));
                progress.setMessage(getString(R.string.please_wait));
            }
            progress.show();
        }

        public void dismissLoadingDialog() {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //dismissLoadingDialog();
            //new Polling(getApplicationContext()).execute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //String response = "";

            try {
                // SHA-1 Hashed Password to go to vera
                String hashedPassword = hashThePassword(password);

                // encoded username will be posted to mios vera
                encodedUsername = URLEncoder.encode(username, "utf-8");

                URLMap.put("1", URLMap.get("1").append(encodedUsername));
                URLMap.put("1", URLMap.get("1").append("?"));
                System.out.println(URLMap.get("1"));

                postData.clear();
                postData.put(getString(R.string.sha), hashedPassword);
                postData.put(getString(R.string.pk_oem), "1");
                System.out.println("THIS IS THE POST DATA " + postData);
                String encodedString = handleURLConnections(URLMap.get("1"), postData, "1", getString(R.string.post));

                String pk_account = processTheData(1, encodedString);
                System.out.println("This is the pk_account used for getting details about devices \n" + pk_account);

                postData.clear();

                postData.put(getString(R.string.pk_account), pk_account);
                String devices = handleURLConnections(URLMap.get("2"), postData, "1", getString(R.string.get));

                System.out.println("DEVICES JSON ARRAY" + devices);
                String PK_Device = processTheData(2, devices);
                System.out.println("PK_Device " + pk_device);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String hashThePassword(String password) throws NoSuchAlgorithmException {

            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(("burak" + "smartfire3" + salt).getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                stringBuilder.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        }

        public String handleURLConnections(StringBuilder inputUrl, LinkedHashMap linkedHashMap, String headers, String type) {

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();

            switch (headers) {
                case "1":
                    try {
                        stringBuilder.append(inputUrl);
                        if (!linkedHashMap.isEmpty()) {
                            stringBuilder.append("?");
                        }
                        stringBuilder.append(postDataString(linkedHashMap).toString());
                        URL url = new URL(stringBuilder.toString());
                        //System.out.println("URL Link is Here \n" + inputUrl.toString());

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(type);
                        conn.setDoOutput(false);
                        conn.setDoInput(true);
                        conn.addRequestProperty("Accept", "application/json");
                        conn.connect();


                        //System.out.println("Post Data String rolls in here \n" + postDataString(linkedHashMap));
                        int responseCode = conn.getResponseCode();
                        System.out.println("RESPONSE CODE IS: " + responseCode);
                        setResponseCode(responseCode);

                        System.out.println("reader a geldi");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        System.out.println("reader i gecti");

                        while ((line = reader.readLine()) != null) {
                            stringBuilder2.append(line);
                            //System.out.println("BASTI MI BURAYI" + stringBuilder2.toString());
                        }
                        reader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "2":
                    try {

                        // this will be used when header is necessary.
                        URL url = new URL(inputUrl.toString());

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        //conn.addRequestProperty("MMSAuth", identityToken.getString("MMSAuth"));
                        //conn.addRequestProperty("MMSAuthSig", identityToken.getString("MMSAuthSig"));

                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                        writer.write((postDataString(linkedHashMap)).toString());
                        writer.flush();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            System.out.println("reader icinde");
                            //printResponse(text);
                            //getEncodedStringAndDecode(text);
                        }
                        writer.close();
                        reader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                case "3":
                    try {

                        URL url = new URL("www.google.com");

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.connect();

                        System.out.println("Post Data String rolls in here \n" + postDataString(linkedHashMap));

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            System.out.println(stringBuilder.toString());
                        }
                        reader.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }


            return stringBuilder2.toString();
        }

        public String processTheData(int type, String text) {

            String result = "";
            StringBuilder stringBuilder;

            switch (type) {
                case 1:
                    try {
                        stringBuilder = new StringBuilder(text);
                        System.out.println("THIS IS THE TEXT " + stringBuilder);
                        JSONObject jsonObject = new JSONObject(String.valueOf(stringBuilder));
                        System.out.println("THIS IS THE ENCODED STRING " + jsonObject.getString("Identity"));
                        String identity = jsonObject.getString("Identity");
                        System.out.println("Identity: \n" + identity);
                        byte[] decodedBytes = Base64.decode(identity, Base64.DEFAULT);
                        String valueDecoded = new String(decodedBytes, Charset.forName("UTF-8"));
                        System.out.println("Decoded value is " + valueDecoded);
                        JSONObject jsonObject1 = new JSONObject(valueDecoded);
                        String pk_account = jsonObject1.getString("PK_Account");
                        System.out.println("PK_ACCOUNT \n" + pk_account);
                        result = pk_account;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        selectGateway(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        JSONObject jsonArray = new JSONObject(text);
                        return (String) jsonArray.get(getString(R.string.server_relay));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        JSONObject jsonArray = new JSONObject(text);
                        return (String) jsonArray.get(getString(R.string.server_relay));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }

        private StringBuilder postDataString(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result;
        }

        private void selectGateway(String json) throws JSONException {

            listView = (ListView) findViewById(R.id.gatewaysListView);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("Devices");
            PK_Devices = new String[jsonArray.length()];
            Internal_Ips = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                pk_device = String.valueOf(jsonObject1.get(getString(R.string.pk_device)));
                Internal_Ips[i] = String.valueOf(jsonObject1.get(getString(R.string.internal_ip)));
                PK_Devices[i] = pk_device;
            }
            items = new CharSequence[PK_Devices.length];
            for (int i = 0; i < PK_Devices.length; i++) {
                items[i] = String.valueOf(PK_Devices[i]);
                System.out.println("Number " + i + " is: " + items[i]);
            }

            Intent myIntent = new Intent("change_of_ui");
            context.sendBroadcast(myIntent);
        }
    }

    private void changeUI() {
        System.out.println("CHANGE OF UI METHOD WORKS");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.pick_gateway)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pk_device = String.valueOf(items[which]);
                        setInternalIp(Internal_Ips[which]);
                        new LoginCheck2().execute();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        this.isStopped = true;
        super.onDestroy();

    }

    public void setInternalIp(String InternalIp) {
        this.InternalIp = InternalIp;
    }

    public void setServerDevice(String serverDevice) {
        this.serverDevice = serverDevice;
    }

    public void setIdentityToken(String identityToken) {
        this.identityToken = identityToken;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /*public void setRunChoice(int runChoice) {
        this.runChoice = runChoice;
    }

    public int getRunChoice() {
        return this.runChoice;
    }*/

    public void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, myIntentFilter);
    }

    public void registerUIReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("change_of_ui");
        registerReceiver(myBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            say++;
            String action = intent.getAction();
            //System.out.println("Action of the broadcast message is->>" + action);

            switch (action) {
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    if (isConnected) {
                        System.out.println("MAIN ACTIVITY" + activeNetwork.getTypeName());
                        Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_LONG).show();
                        connectionType(activeNetwork.getTypeName());
                    } else {
                        System.out.println("MAIN ACTIVITY" + "Not Connected");
                        Toast.makeText(context, "No Network", Toast.LENGTH_LONG).show();
                        connectionType("");
                    }
                    break;
                case "change_of_ui":
                    System.out.println("CHANGE OF UI BROADCAST MESSAGE");
                    changeUI();
                    break;
            }
        }
    };

    private void connectionType(String type) {
        // gets the connection type and returns it.
        // connection
        this.type = type;
        LoginCheck loginCheck = new LoginCheck();
        //loginCheck.remoteOrLocalConnection();
    }

    private String getConnectionType() {
        return this.type;
    }

    public void cachedMemoryVariables(String pk_device, String encodedUsername, String hwkey, String last_ip,
                                      String last_wifi, String last_engine_connect, String last_pk_device) {

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.preferences_initial), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getString(R.string.username), encodedUsername);
        editor.putString(getString(R.string.pk_device), pk_device);
        editor.putString(getString(R.string.hw_key), hwkey);
        editor.putString(getString(R.string.last_ip), last_ip);
        editor.putString(getString(R.string.last_wifi), last_wifi);
        editor.putString(getString(R.string.last_engine_connect), last_engine_connect);
        editor.putString(getString(R.string.last_pk_device), last_pk_device);
        editor.apply();
    }

    class LoginCheck2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_initial), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.last_pk_device), pk_device);
            editor.apply();

            postData.clear();

            String server_relay_mmsSession = "";
            System.out.println("LOCAL CONNECTION IP " + InternalIp);

            if (!InternalIp.equals("") || !InternalIp.equals(null)) {
                StringBuilder sb = new StringBuilder("http://");
                sb.append(InternalIp + "/");
                sb.append(getString(R.string.port) + "/");
                sb.append(getString(R.string.data_req));
                postData.clear();
                System.out.println("LOCAL CONNECTION URL " + sb.toString());
                String response = loginCheck.handleURLConnections(sb, postData, "1", "GET");
                if (responseCode == 200) {
                    try {
                        System.out.println("SIKERIM BELANI YAVSAK");
                        setLocalConnectionData(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(context, Index.class);
                    intent.putExtra("response", response);
                    startActivity(intent);
                } else {
                    URLMap.put("4", URLMap.get("4").append(String.valueOf(pk_device)).append("/port_3480"));
                }
            } else {
                //Connect Locally
                URLMap.put("4", URLMap.get("4").append(InternalIp).append("/port_3480"));
            }
            loginCheck.dismissLoadingDialog();
            return null;

        }

    }

    public void setLocalConnectionData(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String full, version, model, zwave_heal, temperature, skin, serial_number, fwd1, fwd2, mode,
                ir, irtx, loadtime, dataversion, state, comment;
        full = jsonObject.getString("full");
        version = jsonObject.getString("version");
        model = jsonObject.getString("model");
        zwave_heal = jsonObject.getString("zwave_heal");
        temperature = jsonObject.getString("temperature");
        skin = jsonObject.getString("skin");
        serial_number = jsonObject.getString("serial_number");
        fwd1 = jsonObject.getString("fwd1");
        fwd2 = jsonObject.getString("fwd2");
        mode = jsonObject.getString("mode");
        ir = jsonObject.getString("ir");
        irtx = jsonObject.getString("irtx");
        loadtime = jsonObject.getString("loadtime");
        dataversion = jsonObject.getString("dataversion");
        state = jsonObject.getString("state");
        comment = jsonObject.getString("comment");

        String sections = jsonObject.getString("sections");
        String rooms = jsonObject.getString("rooms");
        String scenes = jsonObject.getString("scenes");
        String devices = jsonObject.getString("devices");
        String categories = jsonObject.getString("categories");

        JSONArray sectionsJsonArray = new JSONArray(sections);
        JSONArray roomsJsonArray = new JSONArray(rooms);
        JSONArray scenesJsonArray = new JSONArray(scenes);
        JSONArray devicesJsonArray = new JSONArray(devices);
        //System.out.println("BEN SENIN TA GOTUNU SIKIYIM " + devicesJsonArray.length() + " " + devicesJsonArray);
        //System.out.println("JSON OBJECT NAME: " + (devicesJsonArray.getJSONObject(0)).getString("name"));
        JSONArray categoriesJsonArray = new JSONArray(categories);

        for (int a = 0; a < sectionsJsonArray.length(); a++) {
            JSONObject JsonObject = sectionsJsonArray.getJSONObject(a);
            database.insertSectionsData(JsonObject.getString("name"), JsonObject.getString("id"));
            //System.out.println("OHA AMK: " + JsonObject.getString("name"));
        }

        for (int b = 0; b < roomsJsonArray.length(); b++) {
            JSONObject JsonObject = roomsJsonArray.getJSONObject(b);
            database.insertRoomsData(JsonObject.getString("name"), JsonObject.getString("id"),
                    JsonObject.getInt("section"));
            //System.out.println("OHA AMK: " + JsonObject.getString("name"));
            //System.out.println("OHA AMK: " + JsonObject.getString("id"));
            //System.out.println("OHA AMK: " + JsonObject.getString("section"));
        }

        for (int i = 0; i < scenesJsonArray.length(); i++) {
            JSONObject JsonObject = scenesJsonArray.getJSONObject(i);
            database.insertScenesData(JsonObject.getString("active"), JsonObject.getString("name"),
                    JsonObject.getString("id"), JsonObject.getString("room"));
            //System.out.println("OHA AMK: " + JsonObject.getString("name"));
        }


        for (int j = 0; j < devicesJsonArray.length(); j++) {

            JSONObject JsonObject = devicesJsonArray.getJSONObject(j);
            System.out.println("DEVICES JSON ARRAY LENGTH: " + devicesJsonArray.length());
            methods.createSPreferences("Devices", context);
            methods.updateSPreferences("numberOfDevices", String.valueOf(devicesJsonArray.length()), "Devices", context);

            //System.out.println("OHA AMK: " + (devicesJsonArray.getJSONObject(j).getString("category")));

            if ((devicesJsonArray.getJSONObject(j).getString("category")).equals("3")) {

                database.insertDevices("", JsonObject.getString("name"),
                        (devicesJsonArray.getJSONObject(j).getString("category")));

                database.insertSwitchData(JsonObject.getString("name"), JsonObject.getString("altid"),
                        JsonObject.getString("id"), (devicesJsonArray.getJSONObject(j).getString("category")),
                        JsonObject.getString("subcategory"), JsonObject.getString("room"), JsonObject.getString("parent"),
                        JsonObject.getString("status"), JsonObject.getString("kwh"), JsonObject.getString("watts"),
                        JsonObject.getString("state"), JsonObject.getString("comment"));
            } else if ((devicesJsonArray.getJSONObject(j).getString("category")).equals("6")) {
                database.insertCameraData(JsonObject.getString("name"), JsonObject.getString("altid"),
                        JsonObject.getString("id"), JsonObject.getString("category"), JsonObject.getString("subcategory"),
                        JsonObject.getString("room"), JsonObject.getString("parent"), JsonObject.getString("ip"),
                        JsonObject.getString("url"), JsonObject.getString("streaming"), JsonObject.getString("commands"),
                        JsonObject.getString("videourls"), JsonObject.getString("state"), JsonObject.getString("comment"));
            }
        }

        for (int k = 0; k < categoriesJsonArray.length(); k++) {
            JSONObject JsonObject = categoriesJsonArray.getJSONObject(k);
            database.insertCategoriesData(JsonObject.getString("name"), JsonObject.getString("id"));
            //System.out.println("OHA AMK: " + JsonObject.getString("name"));
        }
    }

}