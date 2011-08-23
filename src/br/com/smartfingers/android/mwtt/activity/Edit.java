package br.com.smartfingers.android.mwtt.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.dialog.TimePickerDialog;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class Edit extends Activity {

	public static final String EXTRA_ID = "id";

	private DatePicker datePicker;
	private TextView inText;
	private TextView outText;
	private TextView lunchText;
	private TextView totalText;
	private TimePickerDialog timerPickerDialog;
	
	private OnClickListener inOnClickListener;
	private OnClickListener outOnClickListener;
	private OnClickListener lunchOnClickListener;
	
	private Integer id;

	private TimeTrack tt;
	private MyDbAdapter mDbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
        
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();

		Intent intent = getIntent();
		id = intent.getIntExtra(EXTRA_ID, -1);
		if (id.intValue() == -1){
			tt = new TimeTrack();
			tt.hourIn = 8;
			tt.minuteIn = 0;
			tt.hourOut = 18;
			tt.minuteOut = 0;
		} else {
			Cursor cursor = mDbHelper.fetchTimeTrack(id);
			tt = MyDbAdapter.populateTimeTrack(cursor);
		}
		
		setupWidgets();
		
		updateDisplayTimeTrack();
		
		final Calendar c = Calendar.getInstance();
		c.setTime(tt.date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
		datePicker.updateDate(year, month, day);
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDbHelper.close();
    }

	private void setupWidgets() {
		datePicker = (DatePicker) findViewById(R.id.date_picker);
		LinearLayout inLayout = (LinearLayout) findViewById(R.id.in_layout);
		LinearLayout outLayout = (LinearLayout) findViewById(R.id.out_layout);
		LinearLayout lunchLayout = (LinearLayout) findViewById(R.id.lunch_layout);
		Button saveButton = (Button) findViewById(R.id.save_button);
		inText = (TextView) findViewById(R.id.horario_entrada);
		outText = (TextView) findViewById(R.id.horario_saida);
		lunchText = (TextView) findViewById(R.id.almoco);
		totalText = (TextView) findViewById(R.id.total);
		
        timerPickerDialog = new TimePickerDialog(this);
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar calendar = new GregorianCalendar(
						datePicker.getYear(), 
						datePicker.getMonth(), 
						datePicker.getDayOfMonth());
				Date date = calendar.getTime();
				
				TimeTrack fetched = mDbHelper.fetchTimeTrack(date);
				if(fetched == null || (tt.id != null && fetched.id.equals(tt.id))) {
					tt.date = date;
					
					if (tt.id == null) {
			        	mDbHelper.createTimeTrack(tt);
					} else {
						mDbHelper.updateTimeTrack(tt);
					}
					
					finish();
					Toast.makeText(Edit.this, R.string.saved_success_toast, 10).show();	
				} else {
					Toast.makeText(Edit.this, R.string.saved_error_toast, 10).show();
				}
			}
		});
        
        inOnClickListener = new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			tt.hourIn = timerPickerDialog.getCurrentHour();
				tt.minuteIn = timerPickerDialog.getCurrentMinute();
				updateDisplayTimeTrack();
    			timerPickerDialog.dismiss();
    		}
    	};
        
        outOnClickListener = new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			tt.hourOut = timerPickerDialog.getCurrentHour();
				tt.minuteOut = timerPickerDialog.getCurrentMinute();
				updateDisplayTimeTrack();
    			timerPickerDialog.dismiss();
    		}
    	};
        
        lunchOnClickListener = new OnClickListener(){
    		@Override
    		public void onClick(View v) {
    			tt.hourLunch = timerPickerDialog.getCurrentHour();
				tt.minuteLunch = timerPickerDialog.getCurrentMinute();
				updateDisplayTimeTrack();
    			timerPickerDialog.dismiss();
    		}
    	};
        
        inLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
    			timerPickerDialog.setTitle(R.string.in_label);
				timerPickerDialog.setCurrentHour(tt.hourIn);
				timerPickerDialog.setCurrentMinute(tt.minuteIn);
				timerPickerDialog.setOkOnClickListener(inOnClickListener);
				timerPickerDialog.show();
			}
		});
        
        outLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
    			timerPickerDialog.setTitle(R.string.out_label);
				timerPickerDialog.setCurrentHour(tt.hourOut);
				timerPickerDialog.setCurrentMinute(tt.minuteOut);
				timerPickerDialog.setOkOnClickListener(outOnClickListener);
				timerPickerDialog.show();
			}
		});
        
        lunchLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
    			timerPickerDialog.setTitle(R.string.lunch_dialog_title);
				timerPickerDialog.setCurrentHour(tt.hourLunch);
				timerPickerDialog.setCurrentMinute(tt.minuteLunch);
				timerPickerDialog.setOkOnClickListener(lunchOnClickListener);
				timerPickerDialog.show();
			}
		});
	}
    
    private void updateDisplayTimeTrack(){
    	inText.setText(tt.getTimeIn());
    	lunchText.setText(tt.getTimeLunch());
		outText.setText(tt.getTimeOut());
		totalText.setText(tt.getTimeTotalFormated());
    }
}
