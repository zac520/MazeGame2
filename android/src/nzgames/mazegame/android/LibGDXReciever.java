package nzgames.mazegame.android;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import nzgames.mazegame.Handlers.IActivityRequestHandler;
import nzgames.mazegame.Screens.MenuScreen;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zac520 on 6/1/14.
 */
public class LibGDXReciever implements IActivityRequestHandler {
//a lot of this class is left over from an app that could give the score to a website. This may be implemented later. Leaving.

    public boolean adsLoaded = false;


    Context myContext;

    //need the contstructor to have application context so we can us system resources
    LibGDXReciever(Context context){
        myContext = context;
    }





    @Override
    public void sendScore(int score) {
//        //Message thisMessage = new Message();
//        //handler.handleMessage(thisMessage);
//        String deviceID = getDeviceId(myContext);
//        new UploadScore().execute("http://zacs-final-project.appspot.com/upload",deviceID, String.valueOf(score));

    }

    @Override
    public String getScore(MenuScreen menuScreen) {
//        //Message thisMessage = new Message();
//        //handler.handleMessage(thisMessage);
//        String deviceID = getDeviceId(myContext);
//
//        //make a new instance of the score downloader
//        DownloadMaxScore myMaxScore = new DownloadMaxScore();
//
//        //execute the score downloader
//        //we need somewhere to send the score to when it gets done, so we have passed in the menu screen
//        myMaxScore.menuScreen = menuScreen;
//        myMaxScore.execute("http://zacs-final-project.appspot.com/postScore", deviceID);
//
//        return myMaxScore.maxScore;
        return "";

    }

    //http://stackoverflow.com/questions/16078269/android-unique-serial-number/16929647#16929647
    public static String getDeviceId(Context context) {
        //apparently, a device will be unique with either the telephony ID or with a serial (if not telephone. ie tablet)
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }


}

//        http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
class UploadScore extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            //encode the data to send in the POST
            HttpPost httppost = new HttpPost(uri[0]);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("user", uri[1])); //get the score we sent in
            nameValuePairs.add(new BasicNameValuePair("score", uri[2]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //send the request, and get a response
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}


class DownloadMaxScore extends AsyncTask<String, String, String> {
    String maxScore="-1";
    MenuScreen menuScreen;
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            //        http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
            //encode the data to send in the POST
            HttpPost httppost = new HttpPost(uri[0]);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("user", uri[1])); //send user for now. maybe will use it in future


            //send the request, and get a response
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }




        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        //set the max score to the value returned
        //menuScreen.maxScore = result;
    }
}