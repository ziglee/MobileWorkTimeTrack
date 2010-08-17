package net.cassiolandim.android.mwtt.adapter;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import net.cassiolandim.android.mwtt.entity.TimeTrack;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class HistoryCursorAdapter extends ResourceCursorAdapter {

	public HistoryCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        final TextView entryDate = (TextView) view.findViewById(R.id.row_date);
        TextView details = (TextView) view.findViewById(R.id.row_details);
        
		TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
        
        entryDate.setText(cursor.getString(1));
        details.setText("In: " +tt.getTimeIn() + " - Out: " + tt.getTimeOut() + " => " + tt.getTimeTotal());
        
        TextView sample = new TextView(context);
        sample.setText(tt.toString());
        
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(sample);
        popupWindow.setHeight(50);
        popupWindow.setWidth(200);
        
        view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.showAsDropDown(entryDate);
			}
		});
	}

}
