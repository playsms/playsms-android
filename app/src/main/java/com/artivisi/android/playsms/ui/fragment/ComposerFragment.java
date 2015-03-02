package com.artivisi.android.playsms.ui.fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.DashboardActivity;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.google.gson.Gson;
import org.w3c.dom.Text;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.efuzone.android.smsclient.ui.fragment.ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int PICK_CONTACT_REQUEST = 1;
    public static final int    PICK_MULTI_REQUEST=2;
    public final static String TAG = "ComposerFragment";
    public final static String PICK_MULTI_CONTACTS_NAME = "rcpt";
    public final static String rcpt = "rcpt";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
        private View rootView;
    private EditText mMsgTo;
    EditText mMsg;
    ProgressBar  sendingMsg;
        Context context;
    private String to;
    private String msg;
    private AndroidMasterService service;


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

    public ComposerFragment() {
        // Required empty public constructor
    }

    public void onSelectSingleContactClick (View v) {
        Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    public void onSelectMultipleContactsClick(View view) {
//        Intent contactPickerIntent = new Intent(getActivity().getApplicationContext(), ContactManager.class);
//        startActivityForResult(contactPickerIntent, PICK_MULTI_REQUEST);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.setHasOptionsMenu(true);
        User u = getUserCookie(LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(u);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.fragment_composer, container, false);
mMsgTo = (EditText) rootView.findViewById(R.id.frag_msg_to);
        ImageView mass_picker = (ImageView) rootView.findViewById(R.id.frag_mass_picker);
        ImageView regular_picker = (ImageView) rootView.findViewById(R.id.frag_contact_picker);
         mMsg = (EditText) rootView.findViewById(R.id.frag_msg);
         sendingMsg = (ProgressBar) rootView.findViewById(R.id.frag_sending_msg);
       //sendingMsg.setVisibility(View.INVISIBLE);


        mass_picker.setClickable(true);
        mass_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open my contact picker
                onSelectMultipleContactsClick(view);

            }
        });
        regular_picker.setClickable(true);
        regular_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open system contact picker
                onSelectSingleContactClick(view);
            }
        });

        mMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                int charcount =mMsg.getText().length();
                int msgcount = 1;

                while (charcount>160) {
                    msgcount++;
                    charcount= charcount-160;
                }
                charcount= 160- charcount;
                // mCharCounter.setText( charcount+" / "+ msgcount  );
                ((DashboardActivity) getActivity()).set_subtitle(charcount + " / " + msgcount);
                // TODO Auto-generated method stub
            }
        });

            return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.v(TAG, "onActivityResult " + resultCode + " resultCode " + resultCode);
        if ( requestCode == PICK_CONTACT_REQUEST ) {

            if ( resultCode == Activity.RESULT_OK ) {
                Cursor cursor = null;
                Uri pickedPhoneNumber = intent.getData();

                cursor = getActivity().getContentResolver().query(pickedPhoneNumber, null, null, null, null);
                cursor.moveToFirst();

                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (cursor.moveToFirst()) {
                    String email = cursor.getString(phoneIndex);
                    Log.v("onActivityResult", "Got phone: " + email);
                    String str = email.replace(" ", "");
                    str.replace("-", "");
                    mMsgTo.setText(str );
                    return;
                } else {
                    Log.w("onActivityResult", "No results");
                }



                // handle the picked phone number in here.
            }
        }

        if (requestCode == PICK_MULTI_REQUEST) {
            if ( resultCode == Activity.RESULT_CANCELED ) {
                Toast.makeText(getActivity().getApplicationContext(), "canceled", Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_OK )
            {
                Toast.makeText(getActivity().getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
                String res ;
                res = intent.getStringExtra(PICK_MULTI_CONTACTS_NAME);
                res.replace(" ", "");
                res.replace("-", "");
                Log.d(TAG, " onActivityResult from multipick  "+res);
                mMsgTo.setText(res );
            }

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Log.d(TAG, "onCreateOptionsMenu");
        getActivity().getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_send) {
            Log.d(TAG, "action send ");

            if(isNetworkAvailable()){
                msg = mMsg.getText().toString();
                to = mMsgTo.getText().toString().trim();
                new SendMessage().execute();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            return true;



        }

        return super.onOptionsItemSelected(item);
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
        ((DashboardActivity) getActivity()).hideButtonCompose();
        String phone = ((DashboardActivity) getActivity()).gimme_destination();
        if (phone !=null)  mMsgTo.setText(phone);
        if (!isNetworkAvailable()) {

            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
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



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class SendMessage extends AsyncTask<Void, Void, MessageHelper> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendingMsg.setVisibility(View.VISIBLE);
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try {
                return service.sendMessage(to, msg);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            sendingMsg.setVisibility(View.INVISIBLE);
            if(messageHelper.getStatus() != null){
                if(messageHelper.getStatus().equals("ERR")){
                    Toast.makeText(getActivity().getApplicationContext(), messageHelper.getErrorString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Message has been delivered", Toast.LENGTH_SHORT).show();
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
