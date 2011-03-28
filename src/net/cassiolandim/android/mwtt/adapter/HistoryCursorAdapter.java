package net.cassiolandim.android.mwtt.adapter;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import net.cassiolandim.android.mwtt.entity.TimeTrack;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class HistoryCursorAdapter extends ResourceCursorAdapter {

	public HistoryCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
	}

	@Override
	public void bindView(final View timeTrackRow, Context context, Cursor cursor) {
        final TextView entryDate = (TextView) timeTrackRow.findViewById(R.id.row_date);
//        TextView checkin = (TextView) timeTrackRow.findViewById(R.id.row_checkin);
//        TextView checkout = (TextView) timeTrackRow.findViewById(R.id.row_checkout);
        TextView total = (TextView) timeTrackRow.findViewById(R.id.row_total);
        
		final TimeTrack tt = MyDbAdapter.populateTimeTrack(cursor);
        
        entryDate.setText(cursor.getString(1));
//        checkin.setText(tt.getTimeIn());
//        checkout.setText(tt.getTimeOut());
        total.setText(tt.getTimeTotal());
        
        /*
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
		});*/
	}
}
