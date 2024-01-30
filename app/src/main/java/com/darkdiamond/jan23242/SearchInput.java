package com.darkdiamond.jan23242;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchInput extends AppCompatActivity {

    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_input);
        searchEditText = findViewById(R.id.inputSearch);
    }

    public void searchWeb(View view) {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No web search app available", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
