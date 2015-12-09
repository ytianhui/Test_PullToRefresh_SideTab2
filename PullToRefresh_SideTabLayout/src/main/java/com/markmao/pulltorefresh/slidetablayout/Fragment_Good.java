package com.markmao.pulltorefresh.slidetablayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.markmao.pulltorefresh.R;
import com.markmao.pulltorefresh.enterance.MainActivity_Slide;
import com.markmao.pulltorefresh.lib.chat.ChatActivity;
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
public class Fragment_Good extends Fragment implements XListView.IXListViewListener{
    private static final String DATA = "data";

    private XListView mListView;
    private ArrayAdapter<String> mAdapter;
    private Handler mHandler;
    private int mIndex = 0;
    private int mRefreshIndex = 0;

    private int pageNumber = 1;

    //JSON Node Names
    private static final String TAG_oneSet = "oneSet";
    private static final String TAG_userName = "userName";
    private static final String TAG_ideaName = "ideaName";
    private static final String TAG_comment = "comment";
    private static final String TAG_rank = "rank";
    private static final String TAG_ideaID = "ideaID";

    private ArrayList<String> userNameList = new ArrayList<String>();
    private ArrayList<String> ideaNameList = new ArrayList<String>();
    private ArrayList<String> commentList = new ArrayList<String>();
    private ArrayList<String> rankList = new ArrayList<String>();
    private ArrayList<String> ideaIDList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_good,container,false);
        Log.e("here6", "1");
        new JSONParse().execute();
        Log.e("here6", "2");
        mHandler = new Handler();
        mListView = (XListView) view.findViewById(R.id.list_view_good);
        initialize();

        return view;
    }
    public void initialize(){
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());

        userNameList.clear();
        ideaNameList.clear();
        commentList.clear();
        rankList.clear();
        ideaIDList.clear();

        pageNumber = 1;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstance(int type){
        Fragment fragment = new Fragment_Good();
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
                pageNumber = 1;
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
                pageNumber = pageNumber + 1;
                ideaNameList.clear();
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
            Log.e("here6", "3");
            postData.add(new BasicNameValuePair("pageNumber", pageNumber+""));
            HttpPost hmpr = new HttpPost(MainActivity_Slide.serverUrl+"get_GoodIdeaRank", postData);
            replyMessage = hmpr.send();//for this time, the reply message is the total distance
            Log.e("here6", "4"+" "+MainActivity_Slide.serverUrl+"get_GoodIdeaRank"+" "+pageNumber);

            JSONObject json = null;
            if(replyMessage == null){
                Log.e("here3¥n", "hey");
                replyMessage = "{\"oneSet\":[\n" +
                        "]}\n";
            }
            try {
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
                    String ideaID = c.getString(TAG_ideaID);
                    userNameList.add(userName);
                    ideaNameList.add(ideaName);
                    commentList.add(comment);
                    rankList.add(rank);
                    ideaIDList.add(ideaID);
                }

                mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.vw_list_item, ideaNameList);
                mListView.setAdapter(mAdapter);
                setListClickView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setListClickView(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String item = (String) mListView.getItemAtPosition(position);
                Log.e("here7：", position+"");

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(item)
                        .setMessage("created by user: " + userNameList.get(position-1))
                        .setMessage("Comment:")
                        .setMessage(commentList.get(position-1))
                        .setPositiveButton("Good Idea!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("Disscuss Space", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), ChatActivity.class);
                                intent.putExtra("ideaID", ideaIDList.get(position - 1));
                                intent.putExtra("ideaName",ideaNameList.get(position-1));
                                intent.putExtra("userName", userNameList.get(position-1));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Bad Idea!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                dialog.show();
            }
        });
    }
    public void voteForIdea(int goodOrBad, String userName, String ideaName){
        List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
        String replyMessage = "";
        Log.e("here9", "3");

        postData.add(new BasicNameValuePair("vote", goodOrBad + ""));
        postData.add(new BasicNameValuePair("userName", userName+""));
        postData.add(new BasicNameValuePair("ideaName", ideaName+""));

        HttpPost hmpr = new HttpPost(MainActivity_Slide.serverUrl+"upload_vote", postData);
        replyMessage = hmpr.send();//for this time, the reply message is the total distance
        if(replyMessage.equals("success")){
            Toast.makeText(getActivity(), "Vote Success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Vote Failed", Toast.LENGTH_SHORT).show();
        }
    }
}