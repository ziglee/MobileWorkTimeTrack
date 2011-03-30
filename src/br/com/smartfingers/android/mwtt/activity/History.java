package br.com.smartfingers.android.mwtt.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.adapter.HistoryCursorAdapter;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.dialog.HistoryRowDialog;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class History extends ListActivity {

	private MyDbAdapter mDbHelper;
	
	private HistoryRowDialog historyRowDialog;
	private AlertDialog deleteAllAlertDialog;
	
	private static final int MENU_ITEM_DETAILS = 0;
	private static final int MENU_ITEM_DELETE = 1;
	
	private static final int MENU_DELETE_ALL = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        registerForContextMenu(getListView());
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();
        fillData();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
		builder.setMessage("Todos os registros serão apagados. Deseja continuar?")
		       .setCancelable(false)
		       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   mDbHelper.deleteAll();
		        	   fillData();
		           }
		       })
		       .setNegativeButton("Não", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		deleteAllAlertDialog = builder.create();
		
        historyRowDialog = new HistoryRowDialog(this);
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

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DELETE_ALL, 0, "Apagar tudo").setIcon(R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, MENU_ITEM_DETAILS, 0, "Detalhes");
		menu.add(0, MENU_ITEM_DELETE, 1, "Apagar");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		switch(item.getItemId()){
		case MENU_ITEM_DETAILS:
			Cursor cursor = mDbHelper.fetchTimeTrack(info.id);
			final TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
			historyRowDialog.show(tt);
			cursor.close();
			break;
		case MENU_ITEM_DELETE:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
			builder.setMessage("Este registro será apagado. Deseja continuar?")
			       .setCancelable(false)
			       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   mDbHelper.deleteTimeTrack(info.id);
			        	   fillData();
			           }
			       })
			       .setNegativeButton("Não", new DialogInterface.OnClickListener() {
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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_ALL:
			deleteAllAlertDialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = mDbHelper.fetchTimeTrack(id);
		final TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
		historyRowDialog.show(tt);
		cursor.close();
	}
}
