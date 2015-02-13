package com.artivisi.android.playsms.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.artivisi.android.playsms.ui.adapter.SentMessageAdapter;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SentMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SentMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SentMessageFragment extends Fragment {
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
    private SentMessageAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SentMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SentMessageFragment newInstance(String param1, String param2) {
        SentMessageFragment fragment = new SentMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SentMessageFragment() {
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
        adapter = new SentMessageAdapter(getActivity());
        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(u);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_sent_message, container, false);

        mEmptySentMsg = (TextView) rootView.findViewById(R.id.sent_msg_empty);
        lvSentMessage = (ListView) rootView.findViewById(R.id.list_sent_msg);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list_sent_msg);

        mEmptySentMsg.setVisibility(View.GONE);
        lvSentMessage.setVisibility(View.VISIBLE);
        lvSentMessage.setAdapter(adapter);

        if(playSmsDb.getAllSent().size() <= 0){
            mEmptySentMsg.setVisibility(View.VISIBLE);
            lvSentMessage.setVisibility(View.GONE);

        }

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetSentMessage().execute();
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

    private class GetSentMessage extends AsyncTask<Void, Void, MessageHelper>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            return service.pollSentMessage(playSmsDb.getLastSent());
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            swipeRefreshLayout.setRefreshing(false);
            if(messageHelper.getStatus() != null){
                if(messageHelper.getStatus().equals("ERR")){
                    if(messageHelper.getError().equals("400")){
//                        mEmptySentMsg.setVisibility(View.VISIBLE);
//                        lvSentMessage.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No New Sent Message", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                lvSentMessage.setVisibility(View.VISIBLE);
                mEmptySentMsg.setVisibility(View.GONE);
                for (int i = 0; i < messageHelper.getData().size(); i++){
                    playSmsDb.insertSent(messageHelper.getData().get(i));
                }
                adapter.updateList();
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
}
