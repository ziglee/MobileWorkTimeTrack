package net.cassiolandim.android.mwtt.dialog;

import net.cassiolandim.android.mwtt.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class LunchDialog extends Dialog {

	private final Button ok;
	private final TimePicker lunchPicker;
	
	public LunchDialog(Context context) {
		super(context);
		
		setContentView(R.layout.lunch_dialog);
    	setTitle("Tempo de Almoço:");
    	
    	lunchPicker = (TimePicker) findViewById(R.id.lunch_picker);
    	ok = (Button) findViewById(R.id.lunch_ok);
    	Button cancel = (Button) findViewById(R.id.lunch_cancel);
    	
    	lunchPicker.setIs24HourView(true);
    	lunchPicker.setCurrentHour(1);
    	lunchPicker.setCurrentMinute(0);
    	
    	cancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setOkOnClickListener(Button.OnClickListener listener){
		ok.setOnClickListener(listener);
	}
	
	public Integer getCurrentHour(){
		return lunchPicker.getCurrentHour();
	}
	
	public Integer getCurrentMinute(){
		return lunchPicker.getCurrentMinute();
	}
}
