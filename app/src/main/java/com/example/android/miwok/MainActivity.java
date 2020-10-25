/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.miwok;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void activityNumber(View view) {
        startActivity(new Intent(getBaseContext(), Numbers.class));
    }

    public void activityPhrases(View view) {
        startActivity(new Intent(getBaseContext(), Phrases.class));
    }

    public void activityColors(View view) {
        startActivity(new Intent(getBaseContext(), Colors.class));
    }

    public void activityFamily(View view) {
        startActivity(new Intent(getBaseContext(), Family.class));
    }
}
