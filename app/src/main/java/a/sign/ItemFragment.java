package a.sign;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.sign.baseadapter.DigCertBaseAdapter;
import a.sign.model.DigCert;
import a.sign.model.Login;
import a.sign.service.StaticVars;
import a.sign.service.UtilsService;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener{

    private static final String LOG_TAG = "ItemFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private String mParam2;

    private OnItemFragmentInteractionListener mListener;

    private Activity activity;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    /*private ListAdapter mAdapter;*/
    private BaseAdapter mAdapter;

    private Login login;
    private View view;
    private boolean isLoggedin; //PUBLIC LIST CODE
    private List<DigCert> digCertList = null;
    private TextView msg;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String loginStringJson = null;

        if (getArguments() != null) {
            loginStringJson = getArguments().getString("loginStringJson");
        }

        login = new Gson().fromJson(loginStringJson, Login.class);

        boolean sf = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_item, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);

        msg = (TextView) view.findViewById(R.id.msg_textView);

        try {
            initBaseAdapter(mSectionNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the adapter
//        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    // BEGIN_INCLUDE (setup_views)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the adapter
        mListView.setAdapter(mAdapter);

    }
    // END_INCLUDE (setup_views)

    public void initBaseAdapter(int number) throws IOException {
        switch (number) {
            case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES:
//                    activity.deleteFile(StaticVars.DIGCERTLIST);

                digCertList = (List<DigCert>) UtilsService.getDigCertList(activity);

                if (digCertList == null){
                    digCertList = new ArrayList<>();
                }
                mAdapter = new DigCertBaseAdapter(activity, digCertList);

                emptyMsg(StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES);

                break;
        }
    }

    public void emptyMsg(int mSectionNumber){
        switch (mSectionNumber) {
            case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES:
                if (mAdapter.getCount() == 0) {
                    msg.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    msg.setText(activity.getResources().getText(R.string.tv_empty_list_digital_certificate));
                } else if (mAdapter.getCount() > 0){
                    msg.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    msg.setText(activity.getResources().getText(R.string.tv_select_digital_certificate));
                }else {
                    msg.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = activity;

            mListener = (OnItemFragmentInteractionListener) activity;

            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((MainActivity) activity).onSectionAttached(mSectionNumber);

            switch (mSectionNumber) {
            }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (null != mListener) {

            switch (mSectionNumber) {
                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES:
                    digitalCertificateOnItemClick(position);
                    break;
            }
        }
    }

    public void digitalCertificateOnItemClick(int position){
        DigCert digCert = digCertList.get(position);
        String digCertJson = new Gson().toJson(digCert);
        Intent intent = new Intent(activity, DigSigActivity.class);
        Bundle args = new Bundle();
        args.putString("digCertJson", digCertJson);
        intent.putExtras(args);
        activity.startActivityForResult(intent, StaticVars.DIG_CERT_ACTIVITY_RESULT);
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
    public interface OnItemFragmentInteractionListener {

    }

}
