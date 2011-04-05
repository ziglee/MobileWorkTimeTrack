package br.com.smartfingers.android.mwtt.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.dialog.AboutDialog;
import br.com.smartfingers.android.mwtt.dialog.LunchDialog;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class Home extends Activity {
	
	public static final String LOG_TAG = "MobileWorkTimeTrack";
	
	private LunchDialog lunchDialog;
	private AboutDialog sobreDialog;
	private TimePicker timePicker;
	private Button checkButton;
	private Button resetButton;
	private LinearLayout imgLunch;
	private TextView horarioEntrada;
	private TextView horarioSaida;
	private TextView almoco;
	private TextView total;
	private TimeTrack tt;
	private MyDbAdapter mDbHelper;
	
	private static final int MENU_HISTORY = 1;
	private static final int MENU_ABOUT = 2;
	private static final int MENU_EXIT = 3;
	private static final int MENU_DONATION = 4;
	
	private static final int DIALOG_LUNCH_ID = 1;
	private static final int DIALOG_ABOUT_ID = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();
        
        bindComponents();
        loadPendingTimeTrack();
        updateDisplayTimeTrack();
    }

	@Override
    protected void onResume() {
    	super.onResume();
    	
    	Calendar now = new GregorianCalendar();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if(tt.hourIn != null && tt.hourOut == null){
    		savePendingTimeTrack(tt);
    	}
    	
    	mDbHelper.close();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_HISTORY, 0, "Histórico").setIcon(R.drawable.ic_menu_view);
	    menu.add(0, MENU_ABOUT, 1, "Sobre").setIcon(R.drawable.ic_menu_info_details);
	    menu.add(0, MENU_DONATION, 2, "Doar").setIcon(R.drawable.ic_menu_allfriends);
	    menu.add(0, MENU_EXIT, 3, "Sair").setIcon(R.drawable.ic_menu_close_clear_cancel);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_HISTORY:
        	startActivity(new Intent(this, History.class));
        	return true;
        case MENU_ABOUT:
        	showDialog(DIALOG_ABOUT_ID);
        	return true;
        case MENU_DONATION:
        	return true;
        case MENU_EXIT:
        	finish();
	    	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
        switch(id) {
        case DIALOG_ABOUT_ID:
            dialog = sobreDialog;
            break;
        case DIALOG_LUNCH_ID:
            dialog = lunchDialog;
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

	private void bindComponents() {
    	timePicker = (TimePicker)findViewById(R.id.main_time_picker);
        checkButton = (Button)findViewById(R.id.check_button);
        resetButton = (Button)findViewById(R.id.reset_button);
        horarioEntrada = (TextView)findViewById(R.id.horario_entrada);
        horarioSaida = (TextView)findViewById(R.id.horario_saida);
        almoco = (TextView)findViewById(R.id.almoco);
        total = (TextView)findViewById(R.id.total);
        imgLunch = (LinearLayout)findViewById(R.id.img_lunch_dialog);
        lunchDialog = new LunchDialog(this);
		sobreDialog = new AboutDialog(this);
		
		lunchDialog.setOkOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tt.hourLunch = lunchDialog.getCurrentHour();
				tt.minuteLunch = lunchDialog.getCurrentMinute();
				mDbHelper.updateTimeTrack(tt);
				updateDisplayTimeTrack();
				lunchDialog.dismiss();
			}
		});

        timePicker.setIs24HourView(true);
        checkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        if(tt.hourIn != null){
		        	tt.date = new Date();
		        	tt.hourOut = timePicker.getCurrentHour();
		        	tt.minuteOut = timePicker.getCurrentMinute();
		        	
		        	deleteFile(TimeTrack.FILENAME);		        	
		        	mDbHelper.createTimeTrack(tt);
		        }else{
		        	tt.hourIn = timePicker.getCurrentHour();
		        	tt.minuteIn = timePicker.getCurrentMinute();
		        }
		        
		        updateDisplayTimeTrack();
			}
		});
        
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
		builder.setMessage("O registro de hoje será apagado. Deseja continuar?")
		       .setCancelable(false)
		       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   resetToCheckin();
		           }
		       })
		       .setNegativeButton("Não", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		final AlertDialog resetAlertDialog = builder.create();
		
        resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetAlertDialog.show();
			}
		});
        
        imgLunch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_LUNCH_ID);
			}
		});
	}
	
	private void loadPendingTimeTrack(){
		tt = loadTimeTrackFromFile();
    	if(tt == null) tt = mDbHelper.fetchTodayTimeTrack();
    	if(tt == null) tt = new TimeTrack();
    		
		if(tt.isBeforeToday()){
			resetToCheckin();
		}
	}

	private TimeTrack loadTimeTrackFromFile() {
		TimeTrack tt = null;
		try {
    		FileInputStream fis = openFileInput(TimeTrack.FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			tt = (TimeTrack)ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return tt;
	}
	
	private TimeTrack savePendingTimeTrack(TimeTrack tt){
		try {
    		FileOutputStream fos = openFileOutput(TimeTrack.FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(tt);
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tt;
	}
	
	private void resetToCheckin(){
		mDbHelper.deleteTodayTimeTrack();
		deleteFile(TimeTrack.FILENAME);
    	tt = new TimeTrack();
    	
    	updateDisplayTimeTrack();
	}
    
    private void updateDisplayTimeTrack(){
    	if(tt.hourIn != null){
	    	horarioEntrada.setText(tt.getTimeIn());
	    	almoco.setText(tt.getTimeLunch());
	    	
	    	if(tt.hourOut != null){
	    		horarioSaida.setText(tt.getTimeOut());
	    		total.setText(tt.getTimeTotal());
	    		
	        	checkButton.setText("Checkout");
	        	checkButton.setEnabled(false);
	    	}else{
	    		checkButton.setText("Checkout");
	        	checkButton.setEnabled(true);
	    	}
    	}else{
    		horarioEntrada.setText("");
    		horarioSaida.setText("");
    		almoco.setText("");
    		total.setText("");
    		
    		checkButton.setText("Checkin");
        	checkButton.setEnabled(true);
    	}
    }
}
