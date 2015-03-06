package com.artivisi.android.playsms.ui.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.ContactHelper;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.artivisi.android.playsms.ui.adapter.ContactAdapter;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lvContact;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private TextView mEmptyContact;
    private AndroidMasterService service;
    private PlaySmsDb playSmsDb;
    public ContactAdapter adapter;
    private MenuItem menuSelectAll;
    private MenuItem menuClearAll;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);

        playSmsDb = new PlaySmsDb(getActivity());

        adapter = new ContactAdapter(getActivity());

        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(u);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mEmptyContact = (TextView) rootView.findViewById(R.id.contact_empty);
        lvContact = (ListView) rootView.findViewById(R.id.list_contact);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list_contact);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light);

        mEmptyContact.setVisibility(View.GONE);
        lvContact.setVisibility(View.VISIBLE);
        lvContact.setAdapter(adapter);
        lvContact.setClickable(true);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean selected = adapter.getItem(position).isSelected();
                selected = !selected;
                adapter.getItem(position).setSelected(selected);
                CheckBox checked = (CheckBox) view.findViewById(R.id.checkbox_contact);
                checked.setChecked(selected);

                int counter = 0;
                for (int i = 0; i < adapter.getCount(); i++){
                    counter = adapter.getItem(i).isSelected() ? counter + 1 : counter + 0;
                }
                if(counter == 0){
                    menuSelectAll.setVisible(true);
                    menuClearAll.setVisible(false);
                } else if (counter == adapter.getCount()){
                    menuSelectAll.setVisible(false);
                    menuClearAll.setVisible(true);
                } else {
                    menuSelectAll.setVisible(true);
                    menuClearAll.setVisible(true);
                }
            }
        });


        if (playSmsDb.getAllContact().size() <= 0){
            mEmptyContact.setVisibility(View.VISIBLE);
            lvContact.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isNetworkAvailable()){
                    new GetContact().execute();
                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contact, menu);
        menuSelectAll = menu.findItem(R.id.action_select_all);
        menuClearAll = menu.findItem(R.id.action_clear_all);
        menuSelectAll.setVisible(true);
        menuClearAll.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_select_all){
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.getItem(i).setSelected(true);
            }
            menuSelectAll.setVisible(false);
            menuClearAll.setVisible(true);
            adapter.notifyDataSetChanged();
        }

        if(id == R.id.action_clear_all){
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.getItem(i).setSelected(false);
            }
            menuSelectAll.setVisible(true);
            menuClearAll.setVisible(false);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class GetContact extends AsyncTask<Void, Void, ContactHelper> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ContactHelper doInBackground(Void... params) {
            try{
                return service.getContact();
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ContactHelper contactHelper) {
            super.onPostExecute(contactHelper);
            swipeRefreshLayout.setRefreshing(false);
            if(contactHelper == null){
                Toast.makeText(getActivity(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if (contactHelper.getStatus() != null) {
                    if (contactHelper.getError().equals("0")) {
                        if(contactHelper.getData() == null){
                            playSmsDb.truncateContact();
                            lvContact.setVisibility(View.GONE);
                            mEmptyContact.setVisibility(View.VISIBLE);
                        } else if(playSmsDb.getAllContact().size() == contactHelper.getData().size()){
                            Toast.makeText(getActivity(), "No New Contact", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < contactHelper.getData().size(); i++) {
                                playSmsDb.insertContact(contactHelper.getData().get(i));
                            }
                            refreshList();
                        }
                    } else {
                        Log.i("ERROR : ", "NO CONTACT");
                    }
                }
            }
        }

    }

    public void refreshList(){
        mEmptyContact.setVisibility(View.GONE);
        lvContact.setVisibility(View.VISIBLE);
        adapter.updateList();
    }

    protected <T> T getUserCookie(String key, Class<T> a) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
