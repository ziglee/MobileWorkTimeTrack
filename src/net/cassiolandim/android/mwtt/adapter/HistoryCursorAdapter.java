package net.cassiolandim.android.mwtt.adapter;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import net.cassiolandim.android.mwtt.entity.TimeTrack;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class HistoryCursorAdapter extends ResourceCursorAdapter {

	public HistoryCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
	}

	@Override
	public void bindView(final View timeTrackRow, Context context, Cursor cursor) {
        final TextView entryDate = (TextView) timeTrackRow.findViewById(R.id.row_date);
        TextView details = (TextView) timeTrackRow.findViewById(R.id.row_details);
        
		final TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
        
        entryDate.setText(cursor.getString(1));
        details.setText("In: " + tt.getTimeIn() + " - Out: " + tt.getTimeOut() + " => " + tt.getTimeTotal());
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View quickActionsLayout = inflater.inflate(R.layout.history_quick_actions, null);

        Resources res = timeTrackRow.getResources();
        Drawable shape = res.getDrawable(R.drawable.gradient_box);

        final PopupWindow popupWindow = new PopupWindow(quickActionsLayout);
        popupWindow.setHeight(40);
        popupWindow.setWidth(70);
		popupWindow.setBackgroundDrawable(shape);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(false);
        
        timeTrackRow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.showAsDropDown(timeTrackRow);
			}
		});
        
        ImageView del = (ImageView)quickActionsLayout.findViewById(R.id.hist_qa_del);
        del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
	}
}
