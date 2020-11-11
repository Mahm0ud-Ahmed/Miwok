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

public class Family extends Fragment {
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

    public Family() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);


        //استدعاء خدمات النظام والصوت
        manager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);

        final ArrayList<Words> word = new ArrayList<>();
        word.add(new Words("father", "әpә", R.drawable.family_father, R.raw.family_father));
        word.add(new Words("mother", "әṭa", R.drawable.family_mother, R.raw.family_mother));
        word.add(new Words("son", "angsi", R.drawable.family_son, R.raw.family_son));
        word.add(new Words("daughter", "tune", R.drawable.family_daughter, R.raw.family_daughter));
        word.add(new Words("older brother", "taachi", R.drawable.family_older_brother, R.raw.family_older_brother));
        word.add(new Words("younger brother", "chalitti", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        word.add(new Words("older sister", "teṭe", R.drawable.family_older_sister, R.raw.family_older_sister));
        word.add(new Words("younger sister", "kolliti", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        word.add(new Words("grandmother ", "ama", R.drawable.family_grandmother, R.raw.family_grandmother));
        word.add(new Words("grandfather", "paapa", R.drawable.family_grandfather, R.raw.family_grandfather));


        MyAdapter adapter = new MyAdapter(getContext(), R.layout.list_view_templet, word, R.color.category_family);
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

