package net.cassiolandim.android.mwtt.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.cassiolandim.android.mwtt.R;
import net.cassiolandim.android.mwtt.db.MyDbAdapter;
import net.cassiolandim.android.mwtt.entity.TimeTrack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Home extends Activity {
	
	public static final String LOG_TAG = "MobileWorkTimeTrack";
	
	private Dialog sobreDialog;
	private TimePicker timePicker;
	private Button checkButton;
	private Button resetButton;
	private TextView horarioEntrada;
	private TextView horarioSaida;
	private TextView almoco;
	private TextView total;
	private TimeTrack tt;
	private MyDbAdapter mDbHelper;
	
	private static final int MENU_HISTORY = 1;
	private static final int MENU_ABOUT = 2;
	private static final int MENU_EXIT = 3;
	
	private static final int DIALOG_SOBRE_ID = 0;
	
	private final Intent emailIntent;
	
	public Home() {
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("message/rfc822");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"clandim@ciandt.com"});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MWTT");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();
        
        bindComponents();
        loadPreferences();
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
	    menu.add(0, MENU_EXIT, 2, "Sair").setIcon(R.drawable.ic_menu_close_clear_cancel);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_HISTORY:
        	Toast.makeText(Home.this, "Exibir histórico", Toast.LENGTH_SHORT).show();
        	return true;
        case MENU_ABOUT:
        	showDialog(DIALOG_SOBRE_ID);
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
        case DIALOG_SOBRE_ID:
            dialog = sobreDialog;
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
		sobreDialog = createSobreDialog();

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
		builder.setMessage("Are you sure you want to reset?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   resetToCheckin();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
	}
    
    private void loadPreferences() {
    	
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
    
    private Dialog createSobreDialog(){
    	Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.sobre_dialog);
    	dialog.setTitle("Sobre");

    	ImageView image = (ImageView) dialog.findViewById(R.id.sobre_image);
    	image.setImageResource(R.drawable.creep_003);
    	
    	TextView text = (TextView) dialog.findViewById(R.id.sobre_text);
    	text.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
    	text.setLinksClickable(false);
    	text.setText("Desenvolvido por Cássio Landim Ribeiro.\nclandim@ciandt.com.\nTodos os direitos reservados.");
    	text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
    	return dialog;
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
	    		checkButton.setText("Checkin");
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

/*Cursor timeTracks = mDbHelper.fetchAllTimeTracks();
timeTracks.moveToFirst();
while (timeTracks.isAfterLast() == false) {
	TimeTrack tt = MyDbAdapter.populateTimeTrack(timeTracks);
	timeTrackList.append("\n" + tt.toString());
	    timeTracks.moveToNext();
}
timeTracks.close();*/