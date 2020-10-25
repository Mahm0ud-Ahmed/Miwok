package com.example.android.miwok;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Phrases extends AppCompatActivity {
    ListView lv_temple;
    //متغير لتشغيل ملف الصوت المراد
    MediaPlayer player;
    //كائن لطلب التركيز الصوتي من نظام الأندرويد
    AudioFocusRequest audioFocusRequest;
    //كائن لاستدعاء خدمات ادارة الصوت داخل الجهاز
    AudioManager manager;

    //كائن لتنظيم عمل التطبيق الخاص بنا في حال فقده للتركيز الصوتي او الحصول عليه بعد فقده
    AudioManager.OnAudioFocusChangeListener changeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            //شرط في حال الحصول على التركيز الصوتي بعد فقده
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                player.start();
            }
            //شرط في حال خسارة التركيز الصوتي لـ(فتره قصيره - فقدانه مع امكانية خفض صوته)
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                player.pause();
                player.seekTo(0);
            }
            //شرط في حال خسارة التركيز الصوتي نهائيا
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                player.stop();
                releaseResource();
            }
        }
    };

    //كائن يتم استدعائه من قبل النظام بعد انتهاء الصوت من العمل
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //يتم تحرير الموارد التي كانت تعمل لتفريغ الذاكرة
            releaseResource();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        //استدعاء خدمات النظام والصوت
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final ArrayList<Words> word = new ArrayList<>();
        word.add(new Words("Where are you going?", "minto wuksus", R.raw.phrase_where_are_you_going));
        word.add(new Words("What is your name?", "tinnә oyaase'nә", R.raw.phrase_what_is_your_name));
        word.add(new Words("My name is...", "oyaaset...", R.raw.phrase_my_name_is));
        word.add(new Words("How are you feeling?", "michәksәs?", R.raw.phrase_how_are_you_feeling));
        word.add(new Words("I’m feeling good.", "kuchi achit", R.raw.phrase_im_feeling_good));
        word.add(new Words("Are you coming?", "әәnәs'aa?", R.raw.phrase_are_you_coming));
        word.add(new Words("Yes, I’m coming.", "hәә’ әәnәm", R.raw.phrase_yes_im_coming));
        word.add(new Words("I’m coming.", "әәnәm", R.raw.phrase_im_coming));
        word.add(new Words("Let’s go.", "yoowutis", R.raw.phrase_lets_go));
        word.add(new Words("Come here.", "әnni'nem", R.raw.phrase_come_here));


        MyAdapter adapter = new MyAdapter(this, R.layout.list_view_templet, word, R.color.category_phrases);
        lv_temple = (ListView) findViewById(R.id.lv_templet);
        lv_temple.setAdapter(adapter);

        //كائن يختص بالضغط على أي item داخل ال List View
        lv_temple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //استدعاء ال item الذي تم الضغط عليه وحفظه في متغير من نوع Words
                Words words = word.get(position);
                // تحرير الذاكره من الموارد القديمه لعدم تداخل الأصوات لو تم الضغط على اكثر من item في وقت واحد
                releaseResource();

                //انشاء كائن لتخزين خصائص الميديا التي ستعمل داخل تطبيقك ويحدد هنا خاصيتين (نظام الصوت الذي سيعمل - ونوعه)
                AudioAttributes audioAttributes =
                        new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

                //كائن لطلب التركيز الصوتي من نظام الأندرويد ويتم تمرير الكائن السابق له وتمرير كائن تغير حالات التركيز الصوتي
                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(audioAttributes).setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener(changeListener).build();

                // عمل طلب لأخذ التركيز الصوتي من خلال كائن Audio Manager وتخزين الناتج
                int result = manager.requestAudioFocus(audioFocusRequest);

                //شرط في حال الموافقه من قبل النظام على أخذ التركيز الصوتي
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    player = MediaPlayer.create(getBaseContext(), words.getResRecord());
                    player.start();
                    player.setOnCompletionListener(completionListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseResource();
    }


    @TargetApi(Build.VERSION_CODES.O)
    public void releaseResource() {
        if (player != null) {
            player.release();
            player = null;
            //داله لافلات التركيز الصوتي بعد الانتهاء من الصوت
            manager.abandonAudioFocusRequest(audioFocusRequest);
        }
    }

}