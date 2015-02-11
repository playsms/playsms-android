package com.artivisi.android.playsms.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.fragment.InboxFragment;
import com.artivisi.android.playsms.ui.fragment.SentMessageFragment;
import com.google.gson.Gson;

public class DashboardActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        SentMessageFragment.OnFragmentInteractionListener,
        InboxFragment.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private AndroidMasterService service;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private SentMessageFragment sentMessageFragment = new SentMessageFragment();
    private InboxFragment inboxFragment = new InboxFragment();
    private User user;
    private String mCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        user = getUserCookie(LoginActivity.KEY_USER, User.class);

        service = new AndroidMasterServiceImpl(user);

        mTitle = user.getUsername();
        mCredit = "Checking Credit";
        new GetCredit().execute();
        ImageButton btnComposeMsg = (ImageButton) findViewById(R.id.btn_new_msg);
        btnComposeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent composeMsg = new Intent(DashboardActivity.this, ComposeMessageActivity.class);
                startActivity(composeMsg);
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .disallowAddToBackStack()
//                .commit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position){
//            case 0:
//                mTitle = "Compose";
//                fragmentTransaction.replace(R.id.container, newMessageFragment);
//                break;
            case 0:
                fragmentTransaction.replace(R.id.container, inboxFragment);
                break;
            case 1:
                fragmentTransaction.replace(R.id.container, sentMessageFragment);
                break;
            case 2:
                signout();
                break;
            default:
                fragmentTransaction.replace(R.id.container, inboxFragment);
                break;
        }
        fragmentTransaction.commit();
        fragmentTransaction.disallowAddToBackStack();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void signout(){
        SharedPreferences sharedpreferences = getSharedPreferences (LoginActivity.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent goToLogin = new Intent(this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setSubtitle(mCredit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
//            ((DashboardActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public class GetCredit extends AsyncTask<Void, Void, Credit>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportActionBar().setSubtitle("Checking Credit");
        }

        @Override
        protected Credit doInBackground(Void... params) {
            return service.getCredit();
        }

        @Override
         protected void onPostExecute(Credit credit) {
            super.onPostExecute(credit);
            mCredit = credit.getCredit();
            if(credit.getError().equals("0")){
                getSupportActionBar().setSubtitle(mCredit);
            }
        }
    }

    protected <T> T getUserCookie(String key, Class<T> a) {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);

        if (sharedPreferences == null) {
            return null;
        }

        String data = sharedPreferences.getString(key, null);

        if (data == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(data, a);
        }
    }

}
