package com.example.caloriecounter.view.EIM;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriecounter.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Conectarea la un URL (la alegere) si afisarea, in aplicatie,
// a unei informatii disponibile online preluate dintr-un RSS feed
// sau link personalizat (1 pct). Deschiderea unui browser pentru a
// vizualiza adresa este optionala (0.5 pct).

/**
 * This class fetches RSS feed from a given URL
 * and displays the headlines in a ListView.
 *
 * @author cc458
 */
public class RssFeederActivityEIM extends AppCompatActivity {

    private List<String> healines;
    private List<String> links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feeder_eim);
        new MyAsyncTask().execute();
    }

    private class MyAsyncTask extends AsyncTask<Object, Void, ArrayAdapter<String>> {

        // background task to fetch the RSS feed data
        protected ArrayAdapter<String> doInBackground(Object[] params) {
            healines = new ArrayList<>();
            links = new ArrayList<>();
            try {
                URL url = new URL("https://www.independent.co.uk/rss");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem = false;

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                healines.add(xpp.nextText());
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xpp.nextText());
                            }
                        }
                    }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException | IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayAdapter<String> adapter){
            adapter = new ArrayAdapter<>(RssFeederActivityEIM.this, android.R.layout.simple_list_item_1, healines);
            setListAdapter(adapter);
        }

        private void setListAdapter(ArrayAdapter<String> adapter) {
            ListView list = findViewById(R.id.lvRssFeed);
            list.setOnItemClickListener((parent, view, position, id) -> {
                Uri uri = Uri.parse((links.get(position)));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
            list.setAdapter(adapter);
        }
    }

    public InputStream getInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        }catch (IOException e){
            return null;
        }
    }
}