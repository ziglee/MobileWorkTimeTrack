package br.com.smartfingers.android.mwtt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import br.com.smartfingers.android.mwtt.R;

public class TimePickerDialog extends Dialog {

	private final Button ok;
	private final TimePicker timePicker;
	
	public TimePickerDialog(Context context) {
		super(context);
		
		setContentView(R.layout.lunch_dialog);
    	setTitle(R.string.lunch_dialog_title);
    	
    	timePicker = (TimePicker) findViewById(R.id.lunch_picker);
    	ok = (Button) findViewById(R.id.lunch_ok);
    	Button cancel = (Button) findViewById(R.id.lunch_cancel);
    	
    	timePicker.setIs24HourView(true);
    	timePicker.setCurrentHour(1);
    	timePicker.setCurrentMinute(0);
    	
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
		return timePicker.getCurrentHour();
	}
	
	public Integer getCurrentMinute(){
		return timePicker.getCurrentMinute();
	}
	
	public void setCurrentHour(Integer hour){
		timePicker.setCurrentHour(hour);
	}
	
	public void setCurrentMinute(Integer minute){
		timePicker.setCurrentMinute(minute);
	}
}
