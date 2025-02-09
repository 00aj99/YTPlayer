package com.kpstv.youtube.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kpstv.youtube.MainActivity;
import com.kpstv.youtube.R;
import com.kpstv.youtube.SettingsActivity;
import com.kpstv.youtube.adapters.SearchAdapter;
import com.kpstv.youtube.fragments.basedOnApi.PopularFragment;
import com.kpstv.youtube.models.SearchModel;
import com.kpstv.youtube.utils.HttpHandler;
import com.kpstv.youtube.utils.YTSearch;
import com.kpstv.youtube.utils.YTutils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.spyhunter99.supertooltips.ToolTip;
import com.spyhunter99.supertooltips.ToolTipManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SearchFragment extends Fragment {

    View v;
    RecyclerView recyclerView;
    static Fragment discoverFrag, SearchFrag;
    static RecyclerView.LayoutManager layoutManager;
    SearchAdapter adapter;
    boolean networkCreated;
    ArrayList<String> images;
    ArrayList<SearchModel> models;
    RelativeLayout progresslayout;
    CircularProgressBar progressBar;
    ArrayList<Drawable> drawables;
    FragmentActivity activity;
    TextView moreTrend;
    CardView discoverViral, searchCard;
    boolean istrendloaded, isdiscoverloaded;
    ImageView githubView, pulseView, myWebView;
    LinearLayout settingsLayout;
    NestedScrollView nestedScrollView;
    AsyncTask<Void, Float, Void> trendTask;
    AsyncTask<Void, Void, Void> discoverTask;
    boolean alertShown = false;
    LinearLayout SOW, SOF;
    ConstraintLayout tipLayout;
    LinearLayout searchButton;
    ProgressBar progressBar1;
    SharedPreferences preferences, settingpref;
    String region = "global";

    private static String SpotifyTrendsCSV, SpotifyViralCSV;
    CardView top100Card, viral100Card, mostViewedCard, mostPopularCard, weeklyPopularCard, top20songsCard;
    ToolTipManager toolTipManager;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!networkCreated) {
            v = inflater.inflate(R.layout.fragment_search, container, false);

            activity = getActivity();
            settingpref = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
            preferences = activity.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
            if (preferences != null) {
                region = preferences.getString("pref_select_region", "global");
            }

            Log.e("RegionSelected", region + "");

            toolTipManager = new ToolTipManager(activity);
            ToolTip toolTip = new ToolTip()
                    .withTextColor(activity.getResources().getColor(R.color.black))
                    .withText("You can also enter Spotify or YouTube url.")
                    .withColor(getResources().getColor(R.color.colorAccent)) //or whatever you want
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                    .withShadow();

            models = new ArrayList<>();
            drawables = new ArrayList<>();
            images = new ArrayList<>();

            tipLayout = v.findViewById(R.id.search_layout);
            top100Card = v.findViewById(R.id.top100songsCard);
            viral100Card = v.findViewById(R.id.top100musicCard);
            top20songsCard = v.findViewById(R.id.top20songsCard);
            mostViewedCard = v.findViewById(R.id.most_viewed_card);
            mostPopularCard = v.findViewById(R.id.most_popular_card);
            weeklyPopularCard = v.findViewById(R.id.week_popular_card);
            searchButton = v.findViewById(R.id.search_gotButton);
            nestedScrollView = v.findViewById(R.id.nestedScrollView);
            searchCard = v.findViewById(R.id.cardView_search);
            imageView1 = v.findViewById(R.id.dImage1);
            progressBar = v.findViewById(R.id.progressBar);
            progressBar1 = v.findViewById(R.id.progressBar1);
            moreTrend = v.findViewById(R.id.moreTrending);
            imageView2 = v.findViewById(R.id.dImage2);
            imageView3 = v.findViewById(R.id.dImage3);
            imageView4 = v.findViewById(R.id.dImage4);
            githubView = v.findViewById(R.id.githubImage);
            pulseView = v.findViewById(R.id.pulseWebImage);
            myWebView = v.findViewById(R.id.myWebImage);
            settingsLayout = v.findViewById(R.id.settingsLayout);
            SOW = v.findViewById(R.id.SOW_layout);
            SOF = v.findViewById(R.id.SOF_layout);

            recyclerView = v.findViewById(R.id.my_recycler_view);
            discoverViral = v.findViewById(R.id.discoverViral);
            progresslayout = v.findViewById(R.id.progressLayout);
            layoutManager = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, true);
            recyclerView.setLayoutManager(layoutManager);

            githubView.setOnClickListener(v1 -> {
                YTutils.StartURL("https://github.com/KaustubhPatange/YTPlayer", activity);
            });
            pulseView.setOnClickListener(v1 -> {
                YTutils.StartURL("https://kaustubhpatange.github.io/YTPlayer", activity);
            });
            myWebView.setOnClickListener(v1 -> {
                YTutils.StartURL("https://kaustubhpatange.github.io", activity);
            });
            SOW.setOnClickListener(v1 -> {
                String shareText = "If you are a music lover and wants to download Spotify, YouTube music for free try this app https://kaustubhpatange.github.io/YTPlayer";
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                try {
                    activity.startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show();
                    whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    activity.startActivity(whatsappIntent);
                }
            });
            SOF.setOnClickListener(v1 -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share this app");
                String shareMessage = "https://kaustubhpatange.github.io/YTPlayer";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Choose the messenger to share this AppNotify"));
            });

            searchCard.setOnClickListener(v -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }
               /* Intent intent = new Intent(activity,SearchActivity.class);
                intent.putExtra("data_csv",SpotifyViralCSV);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_enter,R.anim.left_exit);*/

                FrameLayout layout = activity.findViewById(R.id.fragment_container);
                if (layout != null) {
                    loadSearchViewFrag();
                }
            });

            moreTrend.setOnClickListener(v -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }
                FrameLayout layout = activity.findViewById(R.id.fragment_container);
                if (layout != null) {
                    discoverFrag = new DiscoverFragment();
                    Bundle args = new Bundle();
                    args.putString("data_csv", SpotifyTrendsCSV);
                    args.putString("title", "Discover Trends");
                    discoverFrag.setArguments(args);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out);
                    ft.replace(R.id.fragment_container, discoverFrag);
                    ft.commit();
                }
            });

            discoverViral.setOnClickListener(v -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }
                FrameLayout layout = activity.findViewById(R.id.fragment_container);
                if (layout != null) {
                    discoverFrag = new DiscoverFragment();
                    Bundle args = new Bundle();
                    args.putString("data_csv", SpotifyViralCSV);
                    args.putString("title", "Discover Viral");
                    discoverFrag.setArguments(args);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out);
                    ft.replace(R.id.fragment_container, discoverFrag);
                    ft.commit();
                }
                return;

            });

            top100Card.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "normalTag");
                ft.commit();
            });
            top20songsCard.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "sound20");
                ft.commit();
            });
            viral100Card.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "viral");
                ft.commit();
            });
            mostViewedCard.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "most_viewed");
                ft.commit();
            });
            mostPopularCard.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "most_popular");
                ft.commit();
            });
            weeklyPopularCard.setOnClickListener(view -> {
                if (!YTutils.isInternetAvailable()) {
                    Toast.makeText(activity, "No active connection is found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MainActivity.popularFrag.onDestroy();
                } catch (Exception e) {
                }
                MainActivity.popularFrag = new PopularFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                ft.replace(R.id.fragment_container, MainActivity.popularFrag, "weekly_popular");
                ft.commit();
            });

            settingsLayout.setOnClickListener(v ->
                    startActivity(new Intent(activity, SettingsActivity.class)));

            if (YTutils.isInternetAvailable())
                networkCreated = true;

            trendTask = new getTrending();
            trendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            discoverTask = new loadDiscoverImages();
            discoverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if (!settingpref.getBoolean("searchTip", false)) {
                toolTipManager.showToolTip(toolTip, searchCard);
                SharedPreferences.Editor editor = settingpref.edit();
                editor.putBoolean("searchTip", true);
                editor.apply();
            }
            changeYear();
        }
        return v;
    }

    private static final String TAG = "SearchFragment";

    void changeYear() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(Calendar.getInstance().getTime());
        LinearLayout regionLayout = v.findViewById(R.id.regionalLayout);
        LinearLayout item1 = regionLayout.findViewById(R.id.layout_item1);
        LinearLayout item2 = regionLayout.findViewById(R.id.layout_item2);
        LinearLayout item3 = regionLayout.findViewById(R.id.layout_item3);

        TextView txt1 = item1.findViewById(R.id.year);
        TextView txt2 = item2.findViewById(R.id.year);
        TextView txt3 = item3.findViewById(R.id.year);

        txt1.setText(year);
        txt2.setText(year);
        txt3.setText(year);
    }

    public void loadSearchViewFrag() {
        SearchFrag = new SFragment();
        Bundle args = new Bundle();
        args.putString("data_csv", SpotifyViralCSV);
        SearchFrag.setArguments(args);
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out);
        ft.replace(R.id.fragment_container, SearchFrag);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            loadSearchViewFrag();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String newregion = preferences.getString("pref_select_region", "global");
        Log.e("onResume", "region: " + region + ", newregion: " + newregion);
        if (!newregion.contains(region)) {

            nestedScrollView.scrollTo(0, 0);
            Toast.makeText(activity, "Reloading data from new region!", Toast.LENGTH_SHORT).show();

            models.clear();
            adapter.notifyDataSetChanged();
            drawables.clear();
            images.clear();
            progresslayout.setVisibility(View.VISIBLE);
            region = newregion;
            SpotifyTrendsCSV = null;
            SpotifyViralCSV = null;

            imageView1.setImageDrawable(null);
            imageView2.setImageDrawable(null);
            imageView3.setImageDrawable(null);
            imageView4.setImageDrawable(null);

            trendTask = new getTrending();
            trendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            discoverTask = new loadDiscoverImages();
            discoverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (drawables.size() > 3) {
            imageView1.setImageDrawable(drawables.get(0));
            imageView2.setImageDrawable(drawables.get(1));
            imageView3.setImageDrawable(drawables.get(2));
            imageView4.setImageDrawable(drawables.get(3));
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    class getTrending extends AsyncTask<Void, Float, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            progressBar1.setVisibility(View.VISIBLE);
            if (!YTutils.isInternetAvailable())
                trendTask.cancel(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter = new SearchAdapter(models, activity, false);
            recyclerView.setAdapter(adapter);
            recyclerView.getLayoutManager().scrollToPosition(models.size() - 1);
            progresslayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            istrendloaded = true;

            if (!settingpref.getBoolean("showSTip", false)) {
                tipLayout.setVisibility(View.VISIBLE);
                searchButton.setOnClickListener(view -> {
                    tipLayout.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = settingpref.edit();
                    editor.putBoolean("showSTip", true);
                    editor.apply();
                });
            }

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressBar1.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (SpotifyTrendsCSV == null) {
                HttpHandler handler = new HttpHandler();
                SpotifyTrendsCSV = handler.makeServiceCall(
                        "https://spotifycharts.com/regional/" + region + "/daily/latest/download");
            }

            String trendRead = YTutils.readContent(activity, "trend_" + region + ".csv");
            Log.e(TAG, "doInBackground: is File null");
            if (trendRead != null && !trendRead.isEmpty()) {
                String[] lines = trendRead.split("\n|\r");
                Log.e(TAG, "doInBackground: Not file null: " + lines[0] + ", lines Length: " + lines.length);
                if (lines[0].contains(YTutils.getTodayDate()) && lines.length == 11) {
                    Log.e(TAG, "doInBackground: Trend In here...");
                    for (int i = 1; i < 11; i++) {
                        String id = lines[i].split(",")[1];

                        publishProgress((float) (i - 1) * 10);

                        models.add(new SearchModel(
                                lines[i].split(",")[0],
                                YTutils.getImageUrlID(id),
                                YTutils.getYtUrl(id)

                        ));
                    }
                    return null;
                }
            }

            if (models.size() < 10) {
                models.clear();
                if (SpotifyTrendsCSV == null) return null;
                String[] csvlines = SpotifyTrendsCSV.split("\n|\r");
                for (int i = 2; i < 12; i++) {
                    try {
                        String line = csvlines[i];
                        String title = line.split(",")[1].replace("\"", "");
                        String author = line.split(",")[2].replace("\"", "");

                        String search_text = title.replace(" ", "+")
                                + "+by+" + author.replace(" ", "+");

                        Log.e("TrendingLines", line.split(",")[1].replace("\"", ""));

                        YTSearch ytSearch = new YTSearch(search_text);

                        final String videoId = ytSearch.getVideoIDs().get(0);
                        String imgurl = YTutils.getImageUrlID(videoId);

                        publishProgress((float) (i - 2) * 10);
                        models.add(0, new SearchModel(
                                title, imgurl, "https://www.youtube.com/watch?v=" + videoId
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // Save data to internal storage
            saveTrendToInternal();
            return null;
        }
    }

    void saveTrendToInternal() {
        String FILE_NAME = "trend_" + region + ".csv";
        StringBuilder builder = new StringBuilder();
        builder.append(YTutils.getTodayDate()).append("\n");

        for (SearchModel model : models) {
            builder.append(model.getTitle()).append(",").append(YTutils.getVideoID(model.getYturl())).append("\n");
        }

        YTutils.writeContent(activity, FILE_NAME, builder.toString());
    }

    void saveDiscoverToInternal() {
        String FILE_NAME = "discover_" + region + ".csv";
        StringBuilder builder = new StringBuilder();
        builder.append(YTutils.getTodayDate() + "\n");
        for (String image : images) {
            builder.append(image + "\n");
        }

        YTutils.writeContent(activity, FILE_NAME, builder.toString());
    }

    class loadDiscoverImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!YTutils.isInternetAvailable()) {
                discoverTask.cancel(true);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                loadImageGlide(images.get(0), imageView1);
                loadImageGlide(images.get(1), imageView2);
                loadImageGlide(images.get(2), imageView3);
                loadImageGlide(images.get(3), imageView4);
            } catch (Exception ignored) {
            }

            isdiscoverloaded = true;
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (SpotifyViralCSV == null) {
                HttpHandler handler = new HttpHandler();
                SpotifyViralCSV = handler.makeServiceCall(
                        "https://spotifycharts.com/viral/" + region + "/daily/latest/download"
                );
            }

            String discoverRead = YTutils.readContent(activity, "discover_" + region + ".csv");
            if (discoverRead != null && !discoverRead.isEmpty()) {
                String[] lines = discoverRead.split("\n|\r");
                if (lines[0].contains(YTutils.getTodayDate()) && lines.length == 5) {
                    for (int i = 1; i < 5; i++) {
                        images.add(lines[i]);
                    }
                    return null;
                }
            }

            if (images.size() < 4) {
                images.clear();
                int length = 5;
                if (SpotifyViralCSV != null) {
                    String[] csvlines = SpotifyViralCSV.split("\n|\r");
                    for (int i = 1; i < length; i++) {
                        String line = csvlines[i];
                        String title = line.split(",")[1].replace("\"", "");
                        String author = line.split(",")[2].replace("\"", "");

                        String search_text = title.replace(" ", "+")
                                + "+by+" + author.replace(" ", "+");

                        YTSearch ytSearch = new YTSearch(search_text);

                        if (ytSearch.getVideoIDs().size() > 0) {
                            final String videoId = ytSearch.getVideoIDs().get(0);
                            String imgurl = YTutils.getImageUrlID(videoId);
                            images.add(imgurl);
                        } else {
                            length++;
                        }
                    }
                }
            }
            saveDiscoverToInternal();
            return null;
        }
    }

    void loadImageGlide(String url, final ImageView imageView) {
        Glide.with(activity.getApplicationContext()).load(url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                drawables.add(resource);
                imageView.setImageDrawable(resource);
                return true;
            }
        }).into(imageView);
    }

}
