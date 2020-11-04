package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listViewMusic;
    Context context;
    MediaPlayer player;
    int[] songList;
    int songIndex;
    int songAmount;
    ImageButton ibtnPlay, ibtnReset, ibtnNext;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //設定歌曲資源陣列
        songList = new int[]{R.raw.birds, R.raw.finding_synergy, R.raw.full_moon_empty_house};
        //設定歌曲數
        songAmount = 3;

        setListView();

        ibtnReset = findViewById(R.id.ibtn_reset);
        ibtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.seekTo(0);
                player.start();
                //設定暫停鍵為暫停圖示
                ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying = true;
            }
        });

        ibtnPlay = findViewById(R.id.ibtn_play);
        ibtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myTest", "onClick");
                if(!isPlaying){
                    //設定暫停鍵為播放圖示
                    ibtnPlay.setImageResource(android.R.drawable.ic_media_play);
                    player.pause();
                }else{
                    //設定暫停鍵為暫停圖示
                    ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    player.start();
                }
                isPlaying = !isPlaying;
            }
        });

        ibtnNext = findViewById(R.id.ibtn_next);
        ibtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null){
                    player.reset();
                }
                songIndex = (songIndex + 1) % songAmount;
                player = MediaPlayer.create(context, songList[songIndex]);
                player.start();
                //設定暫停鍵為暫停圖示
                ibtnPlay.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying = true;
            }
        });

    }

    private void setListView() {
        listViewMusic = findViewById(R.id.listview_music);

        ArrayList info  = new ArrayList();
        info = getInfo();

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, info);

        listViewMusic.setAdapter(adapter);
        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(player != null){
                    player.reset();
                }
                switch (i){
                    case 0:
                        songIndex = 0;
                        break;
                    case 1:
                        songIndex = 1;
                        break;
                    case 2:
                        songIndex = 2;
                        break;
                }
                player = MediaPlayer.create(context, songList[songIndex]);
                player.start();
            }
        });
    }

    private ArrayList getInfo() {
        ArrayList data = new ArrayList();
        data.add("birds");
        data.add("Finding Synergy");
        data.add("Full Moon Empty House");
        return data;
    }
}