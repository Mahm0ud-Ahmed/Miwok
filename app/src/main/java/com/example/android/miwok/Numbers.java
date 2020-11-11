package com.example.android.miwok;

import android.annotation.TargetApi;
import android.content.Context;
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

public class Numbers extends Fragment {

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
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
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

    public Numbers() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);
        //استدعاء خدمات النظام والصوت
        manager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Words> word = new ArrayList<>();
        word.add(new Words("one", "lutti", R.drawable.number_one, R.raw.number_one));
        word.add(new Words("two", "otiiko", R.drawable.number_two, R.raw.number_two));
        word.add(new Words("three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        word.add(new Words("four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        word.add(new Words("five", "massokka", R.drawable.number_five, R.raw.number_five));
        word.add(new Words("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        word.add(new Words("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        word.add(new Words("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        word.add(new Words("nine", "wo’e", R.drawable.number_nine, R.raw.number_nine));
        word.add(new Words("ten", "na’aacha", R.drawable.number_ten, R.raw.number_ten));

        MyAdapter adapter = new MyAdapter(getActivity(), R.layout.list_view_templet, word, R.color.category_numbers);
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