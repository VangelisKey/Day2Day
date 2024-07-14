package com.example.day2day;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "GridPrefs";
    private static final String KEY_ITEMS = "items";
    private GridLayout gridLayout;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        items = getSavedItems();

        populateGridLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveItems();
    }

    private List<String> getSavedItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedItems = prefs.getString(KEY_ITEMS, null);
        if (savedItems != null) {
            return new ArrayList<>(Arrays.asList(savedItems.split(",")));
        } else {
            return Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6");
        }
    }

    private void saveItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder savedItems = new StringBuilder();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            TextView textView = view.findViewById(R.id.item_text);
            savedItems.append(textView.getText().toString());
            if (i < gridLayout.getChildCount() - 1) {
                savedItems.append(",");
            }
        }
        editor.putString(KEY_ITEMS, savedItems.toString());
        editor.apply();
    }

    private void populateGridLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gridLayout.removeAllViews();

        for (String item : items) {
            View view = inflater.inflate(R.layout.item_layout, null);
            TextView textView = view.findViewById(R.id.item_text);
            textView.setText(item);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FullScreenActivity.class);
                intent.putExtra("item_content", item);
                startActivity(intent);
            });

            view.setOnLongClickListener(v -> {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(null, shadowBuilder, v, 0);
                return true;
            });

            view.setOnDragListener(new DragListener());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.width = 0;
            params.height = 0;

            gridLayout.addView(view, params);
        }
    }

    private class DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            View draggedView = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(TAG, "Drag started");
                    draggedView.setVisibility(View.INVISIBLE);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "Drag entered");
                    v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(TAG, "Drag exited");
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "Drag dropped");
                    int draggedIndex = gridLayout.indexOfChild(draggedView);
                    int targetIndex = gridLayout.indexOfChild(v);

                    if (draggedIndex != targetIndex) {
                        // Swap the views
                        gridLayout.removeView(draggedView);
                        gridLayout.addView(draggedView, targetIndex);
                        draggedView.setVisibility(View.VISIBLE);

                        gridLayout.removeView(v);
                        gridLayout.addView(v, draggedIndex);
                        v.setVisibility(View.VISIBLE);
                    }

                    // Reset background color
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG, "Drag ended");
                    if (!event.getResult()) {
                        draggedView.setVisibility(View.VISIBLE);
                    }
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
                default:
                    break;
            }
            return true;
        }
    }
}