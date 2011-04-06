package br.com.smartfingers.android.mwtt.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.dialog.TimePickerDialog;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class Edit extends Activity {

	public static final String EXTRA_ID = "id";

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
		Cursor cursor = mDbHelper.fetchTimeTrack(id);
		tt = MyDbAdapter.populateTimeTrack(cursor);
		
		setupWidgets();
		
		updateDisplayTimeTrack();
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDbHelper.close();
    }

	private void setupWidgets() {
		Button saveButton = (Button) findViewById(R.id.save_button);
		LinearLayout inLayout = (LinearLayout) findViewById(R.id.in_layout);
		LinearLayout outLayout = (LinearLayout) findViewById(R.id.out_layout);
		LinearLayout lunchLayout = (LinearLayout) findViewById(R.id.lunch_layout);
		inText = (TextView) findViewById(R.id.horario_entrada);
		outText = (TextView) findViewById(R.id.horario_saida);
		lunchText = (TextView) findViewById(R.id.almoco);
		totalText = (TextView) findViewById(R.id.total);
		
        timerPickerDialog = new TimePickerDialog(this);
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDbHelper.updateTimeTrack(tt);
				finish();
				Toast.makeText(Edit.this, R.string.saved_success_toast, 10).show();
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
		totalText.setText(tt.getTimeTotal());
    }
}
