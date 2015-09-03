package smarthouse.smartronic2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;


public class Index extends ActionBarActivity {

    String selectedOption;
    String[] Logout = {"Yes", "No"};
    Button roomsButton, securityButton, button4, button3, button2, button;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private Context context;
    String response = "";

    public final static String EXTRA_MESSAGE = "extra message!";

    @Override
    protected void onCreate(Bundle onRestoreInstanceState) {
        super.onCreate(onRestoreInstanceState);
        setContentView(R.layout.activity_index);
        context = this.getApplicationContext();
        mDrawerList = (ListView) findViewById(R.id.navList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawerItems();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        String fontPath = "fonts/Walkway_Oblique_Bold.ttf";

        //Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), Index.class);
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), Room.class);
                    startActivity(intent);
                }
                if (position == 2) {
                    Intent intent = new Intent(getApplicationContext(), Room.class);
                    startActivity(intent);
                }
                if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), Security.class);
                    startActivity(intent);
                }
                if (position == 4) {
                    Intent intent = new Intent(getApplicationContext(), Room.class);
                    startActivity(intent);
                }
                if (position == 5) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
        menu.add(R.string.home);
        menu.add(R.string.logout);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        /*Typeface font3 = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        Button b3 = (Button) findViewById(R.id.rooms_icon_button);
        b3.setTypeface(font3);

        Typeface font4 = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        Button b4 = (Button) findViewById(R.id.security_icon_button);
        b4.setTypeface(font4);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        CharSequence option = item.getTitle();
        String homeString = getResources().getString(R.string.home);
        String logoutString = getResources().getString(R.string.logout);

        if (item.hasSubMenu() == false) {
            if (option == logoutString) {
                this.displaySelectedItemMessage("Logout", this.Logout);
            } else if (option == homeString) {
                Intent intent = new Intent(getApplicationContext(), Index.class);
                startActivity(intent);
            } else if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            } else {
                Toast toast = Toast.makeText(context, "an error occurred!", Toast.LENGTH_SHORT);
                toast.show();
            }
            selectedOption = item.getTitle().toString();
        }
        return true;
    }


    private void displaySelectedItemMessage(String title, String[] item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(item, (android.content.DialogInterface.OnClickListener) this);
        builder.show();
    }

    // is called when user clicks on Security
    public void goSecurity(View view) {

        //System.out.println("in security method");
        Intent intent = new Intent(Index.this, Security.class);
        String message = "going to Security";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // is called when user clicks on Room
    public void goRooms(View view) {
        Intent intent = new Intent(Index.this, Room.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    private void addDrawerItems() {
        String[] osArray = {"Devices", "Rooms", "Scenes", "Security", "Settings", "Exit"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
    }
}
