package com.example.android.miwok;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static android.content.Context.AUDIO_SERVICE;

public class Colors extends Fragment {
    ListView lv_temple;
    //متغير لتشغيل ملف الصوت المراد
    MediaPlayer player;
    //كائن لطلب التركيز الصوتي من نظام الأندرويد
    AudioFocusRequest audioFocusRequest;
    //كائن لاستدعاء خدمات ادارة الصوت داخل الجهاز
    AudioManager manager;

    //كائن يتم استدعائه من قبل النظام بعد انتهاء الصوت من العمل
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //يتم تحرير الموارد التي كانت تعمل لتفريغ الذاكرة
            releaseResource();
        }
    };
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

    public Colors() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        //استدعاء خدمات النظام والصوت
        manager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);

        // Create a list of words
        final ArrayList<Words> word = new ArrayList<Words>();
        word.add(new Words("red", "weṭeṭṭi", R.drawable.color_red, R.raw.color_red));
        word.add(new Words("mustard yellow", "chiwiiṭә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));
        word.add(new Words("dusty yellow", "ṭopiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        word.add(new Words("green", "chokokki", R.drawable.color_green, R.raw.color_green));
        word.add(new Words("brown", "ṭakaakki", R.drawable.color_brown, R.raw.color_brown));
        word.add(new Words("gray", "ṭopoppi", R.drawable.color_gray, R.raw.color_gray));
        word.add(new Words("black", "kululli", R.drawable.color_black, R.raw.color_black));
        word.add(new Words("white", "kelelli", R.drawable.color_white, R.raw.color_white));


        MyAdapter adapter = new MyAdapter(getActivity(), R.layout.list_view_templet, word, R.color.category_colors);
        lv_temple = rootView.findViewById(R.id.lv_templet);
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
                    player = MediaPlayer.create(getActivity(), words.getResRecord());
                    player.start();
                    player.setOnCompletionListener(completionListener);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
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
