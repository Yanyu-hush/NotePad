package com.example.android.notepad;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SearchResultsActivity extends ListActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,
            NotePad.Notes.COLUMN_NAME_TITLE,
            NotePad.Notes.COLUMN_NAME_NOTE
    };
    private static final int LOADER_ID = 0; // 唯一ID标识符

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results); // 确保你有这个布局文件

        Intent intent = getIntent();
        String query = intent.getStringExtra(SearchManager.QUERY);

        // 初始化Loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // 当需要创建新的Loader时调用
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        Uri contentUri = NotePad.Notes.CONTENT_URI;
        String selection = "title LIKE ? OR note LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        return new CursorLoader(this, contentUri, PROJECTION, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        // 当Loader完成数据加载时调用
        ListView listView = (ListView) findViewById(R.id.list_View); // 强制转换为ListView
        SimpleCursorAdapter notesAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                data,
                new String[]{NotePad.Notes.COLUMN_NAME_TITLE},
                new int[]{android.R.id.text1},
                0
        );
        listView.setAdapter(notesAdapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // 当Loader被重置时调用
        ListView listView = (ListView) findViewById(R.id.list_View); // 强制转换为ListView
        listView.setAdapter(null);
    }
}