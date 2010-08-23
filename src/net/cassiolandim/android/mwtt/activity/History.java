package net.cassiolandim.android.mwtt.activity;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.adapter.HistoryCursorAdapter;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

public class History extends ListActivity {

	private MyDbAdapter mDbHelper;
	private static final int MENU_ITEM_DELETE = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        registerForContextMenu(getListView());
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
	
	private void fillData() {
        Cursor c = mDbHelper.fetchAllTimeTracks();
        startManagingCursor(c);

        HistoryCursorAdapter notes = new HistoryCursorAdapter(this, R.layout.time_track_row, c);
        setListAdapter(notes);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_ITEM_DELETE, 0, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		switch(item.getItemId()){
		case MENU_ITEM_DELETE:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
			builder.setMessage("Are you sure you want to delete?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   mDbHelper.deleteTimeTrack(info.id);
			        	   fillData();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			final AlertDialog resetAlertDialog = builder.create();
			resetAlertDialog.show();
			
			return true;
		}
		
		return super.onContextItemSelected(item);
	}
}
