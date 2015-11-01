package com.markmao.pulltorefresh.slidetablayout;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.markmao.pulltorefresh.R;
import com.markmao.pulltorefresh.enterance.MainActivity_Slide;
import com.markmao.pulltorefresh.lib.listview.XListView;
import com.markmao.pulltorefresh.lib.net.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author fyales
 */
public class Fragment_Latest extends Fragment implements XListView.IXListViewListener{
    private static final String DATA = "data";

    private XListView mListView;
    private ArrayAdapter<String> mAdapter;
    private Handler mHandler;
    private int mIndex = 0;
    private int mRefreshIndex = 0;

    //JSON Node Names
    private static final String TAG_oneSet = "oneSet";
    private static final String TAG_userName = "userName";
    private static final String TAG_ideaName = "ideaName";
    private static final String TAG_comment = "comment";
    private static final String TAG_rank = "rank";
    private ArrayList<String> ideaNameList = new ArrayList<String>();
    private int pageNumber = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest,container,false);
        Log.e("here5", "Latest");

        new JSONParse().execute();

        mHandler = new Handler();
        mListView = (XListView) view.findViewById(R.id.list_view_latest);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstance(int type){
        Fragment fragment = new Fragment_Latest();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA,type);
        fragment.setArguments(bundle);
        return fragment;
    }


    /*
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);

            if (hasFocus) {
                mListView.autoRefresh();
            }
        }
    */
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ideaNameList.clear();
                pageNumber = 0;
                new JSONParse().execute();
                mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.vw_list_item, ideaNameList);
                mListView.setAdapter(mAdapter);
                onLoad();
            }
        }, 2500);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber = pageNumber+1;
                new JSONParse().execute();
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2500);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    /*****json here***/
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            String replyMessage = "";
            List<NameValuePair> postData = new ArrayList<NameValuePair>(2);

            postData.add(new BasicNameValuePair("pageNumber", pageNumber+""));
            HttpPost hmpr = new HttpPost(MainActivity_Slide.serverUrl+"get_LatestIdeaList", postData);
            replyMessage = hmpr.send();//for this time, the reply message is the total distance
            JSONObject json = null;
            try {
                Log.e("here3¥n", replyMessage);
                json = new JSONObject(replyMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            JSONArray android = null;
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_oneSet);
                for (int i = 0; i < android.length(); i++) {
                    JSONObject c = android.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String userName = c.getString(TAG_userName);
                    String ideaName = c.getString(TAG_ideaName);
                    String comment = c.getString(TAG_comment);
                    String rank = c.getString(TAG_rank);
                    ideaNameList.add(ideaName);

                    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.vw_list_item, ideaNameList);
                    mListView.setAdapter(mAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
