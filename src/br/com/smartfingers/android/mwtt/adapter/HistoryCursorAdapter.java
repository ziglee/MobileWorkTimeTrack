package br.com.smartfingers.android.mwtt.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class HistoryCursorAdapter extends ResourceCursorAdapter {

	public HistoryCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
	}

	@Override
	public void bindView(final View timeTrackRow, Context context, Cursor cursor) {
        final TextView entryDate = (TextView) timeTrackRow.findViewById(R.id.row_date);
        TextView total = (TextView) timeTrackRow.findViewById(R.id.row_total);
        
		final TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
        
        entryDate.setText(cursor.getString(1));
        total.setText(tt.getTimeTotalFormated());
	}
}
