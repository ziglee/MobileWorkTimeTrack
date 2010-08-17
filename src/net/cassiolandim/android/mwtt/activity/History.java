package net.cassiolandim.android.mwtt.activity;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.adapter.HistoryCursorAdapter;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

public class History extends ListActivity {

	private MyDbAdapter mDbHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
	
	private void fillData() {
        Cursor c = mDbHelper.fetchAllTimeTracks();
        startManagingCursor(c);

        HistoryCursorAdapter notes = new HistoryCursorAdapter(this, R.layout.time_track_row, c);
        setListAdapter(notes);
    }
}
