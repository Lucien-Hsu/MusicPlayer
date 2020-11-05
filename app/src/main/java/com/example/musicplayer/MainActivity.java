package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listViewMusic;
    Context context;
    MediaPlayer player;
    int[] songList;
    int songIndex;
    int songAmount;
    int progress;
    ImageButton ibtnPlay, ibtnReset, ibtnNext;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //設定歌曲資源陣列
        songList = new int[]{
                R.raw.birds,
                R.raw.finding_synergy,
                R.raw.full_moon_empty_house,
                R.raw.higher_octane,
                R.raw.hopeful_freedom,
                R.raw.instant_crush,
                R.raw.morning_joe,
                R.raw.shine_your_little_light,
                R.raw.south_street_strut,
                R.raw.stars_align,
                R.raw.street_rhapsody,
                R.raw.sunset_strut,
                R.raw.true_art_real_affection_part4,
                R.raw.wind_riders};

        //設定歌曲數
        songAmount = 14;
        //初始化歌曲索引
        songIndex = -1;

        setListView();

        setResetButton();
        setPlayButton();
        setNextButton();
    }

    private static final String PREF = "PREF";
    private static final String PREF_PROGRESS = "PREF_PROGRESS";
    private static final String PREF_SONG_INDEX = "PREF_SONG_INDEX";
    private static final String PREF_ISPLAYING = "PREF_ISPLAYING";

    @Override
    protected void onPause() {
        super.onPause();
        //創建一個SharedPreferences，引數一為要使用的xml檔名，引數二為權限
        SharedPreferences settings = getSharedPreferences(PREF, 0);

        progress = player.getCurrentPosition();
        //設定為編輯模式，並放入資料鍵值，最後commit()才會寫入
        settings.edit()
                .putInt(PREF_PROGRESS, progress)
                .putInt(PREF_SONG_INDEX, songIndex)
                .putBoolean(PREF_ISPLAYING, isPlaying)
                .apply();
        Log.d("myTest", "onPause 儲存狀態 isPlaying:" + isPlaying);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //創建一個SharedPreferences，引數一為要使用的xml檔名，引數二為權限
        SharedPreferences settings = getSharedPreferences(PREF, 0);
        //取出資料值
        progress = settings.getInt(PREF_PROGRESS, 0);
        songIndex = settings.getInt(PREF_SONG_INDEX, 0);
        isPlaying = settings.getBoolean(PREF_ISPLAYING, false);
        //恢復儲存進度
        player = MediaPlayer.create(context, songList[songIndex]);
        player.seekTo(progress);
        //若之前有進度且為暫停則讓按鈕顯示為播放圖示
        if((progress != 0) && !isPlaying){
            //設定暫停鍵為播放圖示
            ibtnPlay.setImageResource(android.R.drawable.ic_media_play);
        }
        //若之前有播放則繼續播放
        if (isPlaying) {
            player.start();
        }
        Log.d("myTest", "onResume 取出狀態 isPlaying:" + isPlaying);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    private void setNextButton() {
        ibtnNext = findViewById(R.id.ibtn_next);
        ibtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {
                    player.reset();
                }
                songIndex = (songIndex + 1) % songAmount;
                player = MediaPlayer.create(context, songList[songIndex]);
                //設定暫停鍵為暫停圖示
                ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                player.start();
                Log.d("myTest", "下一首 isPlaying:" + isPlaying);
            }
        });
    }

    private void setPlayButton() {
        ibtnPlay = findViewById(R.id.ibtn_play);
        ibtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPlaying) {
                    //設定暫停鍵為播放圖示
                    ibtnPlay.setImageResource(android.R.drawable.ic_media_play);
                    player.pause();
                    Log.d("myTest", "按下暫停");
                } else {
                    //設定暫停鍵為暫停圖示
                    ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    player.start();
                    Log.d("myTest", "按下播放");
                }
                isPlaying = !isPlaying;
                Log.d("myTest", "isPlaying:" + isPlaying);
            }
        });
    }

    private void setResetButton() {
        ibtnReset = findViewById(R.id.ibtn_reset);
        ibtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.seekTo(0);
                //設定暫停鍵為暫停圖示
                ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                player.start();
                Log.d("myTest", "重頭撥放 isPlaying:" + isPlaying);
            }
        });
    }

    private void setListView() {
        listViewMusic = findViewById(R.id.listview_music);

        ArrayList info = new ArrayList();
        info = getInfo();

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, info);

        listViewMusic.setAdapter(adapter);
        //ListView的監聽器
        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //若點選同一首歌則做，若不同歌則播放
                if(songIndex == i){
                    //若在播放中則暫停，若在暫停中則播放
                    if(isPlaying){
                        //設定暫停鍵為播放圖示
                        ibtnPlay.setImageResource(android.R.drawable.ic_media_play);
                        player.pause();
                    }else{
                        //設定暫停鍵為暫停圖示
                        ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        player.start();
                    }
                    isPlaying = !isPlaying;
                    Log.d("myTest", "isPlaying:" + isPlaying);
                }else{
                    //若有音樂在播放則停止並回到開頭
                    if (player != null) {
                        player.reset();
                    }

                    isPlaying = true;
                    Log.d("myTest", "isPlaying:" + isPlaying);

                    //存取當前歌曲索引
                    songIndex = i;

                    player = MediaPlayer.create(context, songList[songIndex]);
                    //設定暫停鍵為暫停圖示
                    ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    player.start();
                }

            }
        });
    }

    private ArrayList getInfo() {
        ArrayList data = new ArrayList();
        data.add("birds");
        data.add("Finding Synergy");
        data.add("Full Moon Empty House");
        data.add("Higher Octane");
        data.add("Hopeful Freedom");
        data.add("Instant Crush");
        data.add("Morning Joe");
        data.add("Shine Your Little Light");
        data.add("South Street Strut");
        data.add("Stars Align");
        data.add("Street Rhapsody");
        data.add("Sunset Strut");
        data.add("True Art Real Affection Part4");
        data.add("Wind Riders");

        return data;
    }

}