package com.artivisi.android.playsms.ui.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Message;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.ComposeMessageActivity;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.artivisi.android.playsms.ui.adapter.InboxAdapter;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InboxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String username;
    private String token;

    private OnFragmentInteractionListener mListener;

    private ListView lvInbox;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private TextView mEmptyInbox;
    private AndroidMasterService service;
    private PlaySmsDb playSmsDb;
    private InboxAdapter adapter;
    protected Object mActionMode;
    private Message selectedMessage;
    private LinearLayout selectedList;
    private NotificationManager notificationManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InboxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InboxFragment newInstance(String param1, String param2) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InboxFragment() {
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

        adapter = new InboxAdapter(getActivity());

        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(u);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mEmptyInbox = (TextView) rootView.findViewById(R.id.inbox_empty);
        lvInbox = (ListView) rootView.findViewById(R.id.list_inbox);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list_inbox);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light);

        lvInbox.setAdapter(adapter);

        refreshList();

        lvInbox.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }
                selectedMessage = adapter.getItem(position);
                mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionMoCallback);
                view.setSelected(true);
                selectedList = (LinearLayout) view.findViewById(R.id.layout_list_inbox);

                if (Build.VERSION.SDK_INT >= 16){
                    selectedList.setBackground(getActivity().getResources().getDrawable(R.color.white_milk));
                }
                else{
                    selectedList.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.white_milk));
                }
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isNetworkAvailable()){
                    new GetInbox().execute();
                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        if (notificationManager == null)
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

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
        MenuItem menuItem = menu.add("INBOX");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setEnabled(false);
    }

    private ActionMode.Callback mActionMoCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_message, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            int id = menuItem.getItemId();

            if (Build.VERSION.SDK_INT >= 16){
                selectedList.setBackground(getActivity().getResources().getDrawable(R.color.grey_light));
            }
            else{
                selectedList.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.grey_light));
            }

            if(id == R.id.action_delete){
                playSmsDb.deleteInboxLocally(selectedMessage.getId());
                refreshList();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_LONG).show();
                actionMode.finish();
                return  true;
            } else if (id == R.id.action_copy){
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", selectedMessage.getMsg());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
                actionMode.finish();
                return  true;
            } else if (id == R.id.action_forward){
                Intent forwardMsg = new Intent(getActivity(), ComposeMessageActivity.class);
                forwardMsg.putExtra("msg", selectedMessage.getMsg());
                startActivity(forwardMsg);
                actionMode.finish();
                return  true;
            } else if (id == R.id.action_reply){
                Intent forwardMsg = new Intent(getActivity(), ComposeMessageActivity.class);
                forwardMsg.putExtra("to", selectedMessage.getSrc());
                forwardMsg.putExtra("msg", selectedMessage.getMsg());
                startActivity(forwardMsg);
                actionMode.finish();
                return  true;
            } else {
                return  false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            if (Build.VERSION.SDK_INT >= 16){
                selectedList.setBackground(getActivity().getResources().getDrawable(R.color.grey_light));
            }
            else{
                selectedList.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.grey_light));
            }
        }
    };

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

    private class GetInbox extends AsyncTask<Void, Void, MessageHelper>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try{
                return service.pollInbox(playSmsDb.getLastInbox());
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            if (notificationManager == null)
            notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            swipeRefreshLayout.setRefreshing(false);
            if(messageHelper == null){
                Toast.makeText(getActivity(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if (messageHelper.getStatus() != null) {
                    if (messageHelper.getStatus().equals("ERR")) {
                        if (messageHelper.getError().equals("501")) {
                            Toast.makeText(getActivity(), "No New Inbox", Toast.LENGTH_SHORT).show();
                            playSmsDb.readInbox();
                            adapter.updateList();
                        }
                    }
                } else {
                    if (messageHelper.getData().size() == 1) {
                        Toast.makeText(getActivity(), messageHelper.getData().size() + " New Message", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), messageHelper.getData().size() + " New Messages", Toast.LENGTH_SHORT).show();
                    }
                    lvInbox.setVisibility(View.VISIBLE);
                    mEmptyInbox.setVisibility(View.GONE);
                    for (int i = 0; i < messageHelper.getData().size(); i++) {
                        playSmsDb.insertInbox(messageHelper.getData().get(i));
                    }
                    refreshList();
                }
            }
        }

    }

    public void refreshList(){
        if (playSmsDb.getAllInbox().size() <= 0){
            mEmptyInbox.setVisibility(View.VISIBLE);
            lvInbox.setVisibility(View.GONE);
        } else {
            adapter.updateList();
            mEmptyInbox.setVisibility(View.GONE);
            lvInbox.setVisibility(View.VISIBLE);
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
