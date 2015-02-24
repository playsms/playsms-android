package com.artivisi.android.playsms.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Contact;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.ContactHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.ComposeMessageActivity;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.artivisi.android.playsms.ui.adapter.ContactsAdapter;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.artivisi.android.playsms.ui.fragment.ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.artivisi.android.playsms.ui.fragment.ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView lvSentMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private TextView mEmptySentMsg;
    private AndroidMasterService service;
    private PlaySmsDb playSmsDb;
    private ContactsAdapter adapter;
    private Button buttonSend, buttonSelectAll, buttonClear;
    Context context;
    public final static String TAG = "ContactsFragment";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SentMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        playSmsDb = new PlaySmsDb(getActivity());
        adapter = new ContactsAdapter(getActivity(), init_data());
        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(u);

    }

    private List<Contact> init_data(){
        List<Contact> list = new ArrayList<Contact>();
        //list.add(new Contact("1", "+79024764617", "foo1@id.com", "Vyacheslav"));
        //list.add(new Contact("2", "+19024764617", "foo2@id.com", "Some fake user"));
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        //mEmptySentMsg = (TextView) rootView.findViewById(R.id.contacts_empty);
        //mEmptySentMsg.setVisibility(View.INVISIBLE);
        lvSentMessage = (ListView) rootView.findViewById(R.id.list_contacts);
        buttonSend = (Button) rootView.findViewById(R.id.Sendbutton);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendClick(view);
            }
        });
        buttonSelectAll = (Button) rootView.findViewById(R.id.buttonSelectall);
        buttonSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectallClick(view);
            }
        });
        buttonClear = (Button) rootView.findViewById(R.id.buttonClearSelection);
        buttonClear.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   onClearallClick(view);
                                               }
                                           });
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list_contacts);

        //mEmptySentMsg.setVisibility(View.GONE);
        lvSentMessage.setVisibility(View.VISIBLE);
        lvSentMessage.setAdapter(adapter);
        lvSentMessage.setClickable(true);
        lvSentMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick "+i);
// i number
                boolean selected = adapter.getItem(i).isSelected();
                selected = !selected;
                adapter.getItem(i).setSelected(selected);
               // view.setSelected(true);
                CheckBox checked = (CheckBox) view.findViewById(R.id.selectedcheckbox);
                //checked.setVisibility(View.VISIBLE);
                checked.setChecked(selected);
            }
        }        );
        ///lvSentMessage.setOnClickListener(new ItemClickListener() );

//        if(playSmsDb.getAllSent().size() <= 0){
//            mEmptySentMsg.setVisibility(View.VISIBLE);
//            lvSentMessage.setVisibility(View.GONE);
//
//        }

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()){
                    new GetSentMessage().execute();
                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        if (isNetworkAvailable()){
            new GetSentMessage().execute();
        } else {

            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    public void onSendClick (View view) {
        Intent intent = new Intent(context, ComposeMessageActivity.class);
        String numbers = "";

        for (int i = 0; i < adapter.getCount(); i++) {
            Log.d(TAG, "check "+i);
            if (adapter.getItem(i).isSelected()) {
                Log.d(TAG, "selected");
                if (!numbers.isEmpty()) numbers=numbers+",";
                numbers=numbers+adapter.getItem(i).getP_num();
            }
        }
        intent.putExtra("rcpt",numbers);
        Log.d(TAG, "numbers "+numbers);
        startActivity(intent);

    }

    //onSelectallClick

    public void onSelectallClick (View view) {

        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).setSelected(true);
            }
        adapter.notifyDataSetChanged();
    }

    public void onClearallClick (View view) {

        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).setSelected(false);
        }
        adapter.notifyDataSetChanged();
    }


    public void refreshList(){
        //mEmptySentMsg.setVisibility(View.GONE);
        lvSentMessage.setVisibility(View.VISIBLE);
        adapter.updateList();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        context = activity.getApplicationContext();







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

    private class GetSentMessage extends AsyncTask<Void, Void, ContactHelper>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ContactHelper doInBackground(Void... params) {
            return service.getContacts();
        }

        @Override
        protected void onPostExecute(ContactHelper contactHelper) {
            super.onPostExecute(contactHelper);
            Log.d(TAG, "updated "+contactHelper.getStatus());
                    swipeRefreshLayout.setRefreshing(false);
            if(contactHelper.getStatus() != null) {
                if (contactHelper.getStatus().equals("ERR")) {
                    if (contactHelper.getError().equals("400")) {
//                        mEmptySentMsg.setVisibility(View.VISIBLE);
//                        lvSentMessage.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No contacts returned", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "new list ");
                    List<Contact> list = new ArrayList<Contact>();
                    for (int i = 0; i < contactHelper.getData().size(); i++) {

                        list.add(contactHelper.getData().get(i));
                        Log.d(TAG, "update " + i + " " + contactHelper.getData().get(i).getP_desc());
                        //playSmsDb.insertSent(contactHelper.getData().get(i));
                    }
                    Log.d(TAG, "new adapter " + list.size());
                    adapter.setdata( list);
                   // adapter.updateList();
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
