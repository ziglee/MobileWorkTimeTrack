package br.com.smartfingers.android.mwtt.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import br.com.smartfingers.android.mwtt.BillingService;
import br.com.smartfingers.android.mwtt.BillingService.RequestPurchase;
import br.com.smartfingers.android.mwtt.BillingService.RestoreTransactions;
import br.com.smartfingers.android.mwtt.Consts;
import br.com.smartfingers.android.mwtt.Consts.PurchaseState;
import br.com.smartfingers.android.mwtt.Consts.ResponseCode;
import br.com.smartfingers.android.mwtt.PurchaseObserver;
import br.com.smartfingers.android.mwtt.R;
import br.com.smartfingers.android.mwtt.ResponseHandler;
import br.com.smartfingers.android.mwtt.db.MyDbAdapter;
import br.com.smartfingers.android.mwtt.dialog.TimePickerDialog;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class Home extends Activity {
	
	public static final String LOG_TAG = "MobileWorkTimeTrack";
	
	private TimePickerDialog timerPickerDialog;
	private TimePicker timePicker;
	private Button checkButton;
	private Button resetButton;
	private LinearLayout lunchLayout;
	private TextView horarioEntrada;
	private TextView horarioSaida;
	private TextView almoco;
	private TextView total;
	private XYPlot homeChart;
	
	private TimeTrack tt;
	private MyDbAdapter mDbHelper;
    private Handler mHandler;
    private BillingService mBillingService;
    private MyPurchaseObserver mPurchaseObserver;
	
	private static final int MENU_HISTORY = 1;
	private static final int MENU_EXIT = 3;
	private static final int MENU_DONATION = 4;
	private static final int MENU_HELP = 5;
	
	private static final int DIALOG_LUNCH_ID = 1;
    private static final int DIALOG_CANNOT_CONNECT_ID = 2;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 3;
    private static final int DIALOG_DONATION_THANKS_ID = 4;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        mDbHelper = new MyDbAdapter(this);
        mDbHelper.open();

        setupWidgets();
        loadPendingTimeTrack();
        updateDisplayTimeTrack();
        plotChart();
        
        mHandler = new Handler();
        mPurchaseObserver = new MyPurchaseObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);

        // Check if billing is supported.
        ResponseHandler.register(mPurchaseObserver);
        if (!mBillingService.checkBillingSupported()) {
            showDialog(DIALOG_CANNOT_CONNECT_ID);
        }
    }

    /**
     * Called when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mPurchaseObserver);
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

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mPurchaseObserver);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if(tt.hourIn != null && tt.hourOut == null){
    		savePendingTimeTrack(tt);
    	}
    	
    	mDbHelper.close();
        mBillingService.unbind();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_HISTORY, 0, R.string.history_menu).setIcon(R.drawable.ic_menu_view);
	    menu.add(0, MENU_HELP, 1, R.string.help_menu).setIcon(R.drawable.ic_menu_help);
	    menu.add(0, MENU_DONATION, 3, R.string.donate_menu).setIcon(R.drawable.ic_menu_allfriends);
	    menu.add(0, MENU_EXIT, 4, R.string.exit_menu).setIcon(R.drawable.ic_menu_close_clear_cancel);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_HISTORY:
        	startActivity(new Intent(this, History.class));
        	return true;
        case MENU_HELP:
        	startActivity(new Intent(this, AboutTabHost.class));
        	return true;
        case MENU_DONATION:
        	if (!mBillingService.requestPurchase("donation_001")) {
                showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
            }
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
        case DIALOG_LUNCH_ID:
            dialog = timerPickerDialog;
            break;
        case DIALOG_CANNOT_CONNECT_ID:
            return createDialog(R.string.cannot_connect_title,
                    R.string.cannot_connect_message);
        case DIALOG_BILLING_NOT_SUPPORTED_ID:
            return createDialog(R.string.billing_not_supported_title,
                    R.string.billing_not_supported_message);
        case DIALOG_DONATION_THANKS_ID:
            return createDialog(R.string.billing_not_supported_title,
                    R.string.billing_not_supported_message);
        default:
            dialog = null;
        }
        return dialog;
    }

    private Dialog createDialog(int titleId, int messageId) {
        String helpUrl = replaceLanguageAndRegion(getString(R.string.help_url));
        if (Consts.DEBUG) {
            Log.i(LOG_TAG, helpUrl);
        }
        final Uri helpUri = Uri.parse(helpUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
                    startActivity(intent);
                }
            });
        return builder.create();
    }

    /**
     * Replaces the language and/or country of the device into the given string.
     * The pattern "%lang%" will be replaced by the device's language code and
     * the pattern "%region%" will be replaced with the device's country code.
     *
     * @param str the string to replace the language/country within
     * @return a string containing the local language and region codes
     */
    private String replaceLanguageAndRegion(String str) {
        // Substitute language and or region if present in string
        if (str.contains("%lang%") || str.contains("%region%")) {
            Locale locale = Locale.getDefault();
            str = str.replace("%lang%", locale.getLanguage().toLowerCase());
            str = str.replace("%region%", locale.getCountry().toLowerCase());
        }
        return str;
    }

	private void setupWidgets() {
    	timePicker = (TimePicker)findViewById(R.id.main_time_picker);
        checkButton = (Button)findViewById(R.id.check_button);
        resetButton = (Button)findViewById(R.id.reset_button);
        horarioEntrada = (TextView)findViewById(R.id.horario_entrada);
        horarioSaida = (TextView)findViewById(R.id.horario_saida);
        almoco = (TextView)findViewById(R.id.almoco);
        total = (TextView)findViewById(R.id.total);
        lunchLayout = (LinearLayout)findViewById(R.id.img_lunch_dialog);
		homeChart = (XYPlot) findViewById(R.id.home_chart);
        timerPickerDialog = new TimePickerDialog(this);
		
		timerPickerDialog.setOkOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tt.hourLunch = timerPickerDialog.getCurrentHour();
				tt.minuteLunch = timerPickerDialog.getCurrentMinute();
				mDbHelper.updateTimeTrackLunch(tt);
				updateDisplayTimeTrack();
				timerPickerDialog.dismiss();
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
		builder.setMessage(R.string.reset_alert_dialog)
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   resetToCheckin();
		           }
		       })
		       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
        
        lunchLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_LUNCH_ID);
			}
		});
	}

	private void plotChart() {
		Long[] hours = new Long[7];
		Long[] days = new Long[7];
		
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		date.add(Calendar.DAY_OF_YEAR, 1);
		
		for(int x = 6; x >= 0; x--){
			date.add(Calendar.DAY_OF_YEAR, -1);
			TimeTrack tt = mDbHelper.fetchTimeTrack(date.getTime());
			days[x] = date.getTimeInMillis();
			hours[x] = tt != null ? tt.getTimeTotalInMilliseconds() : 0;
		}
		
        // create our series from our array of nums:
        XYSeries series = new SimpleXYSeries(
                Arrays.asList(days),
                Arrays.asList(hours),
                "Horas por Dia");
 
        homeChart.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        homeChart.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        homeChart.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        homeChart.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        homeChart.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
        //homeChart.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        homeChart.getBorderPaint().setStrokeWidth(2);
        homeChart.getBorderPaint().setAntiAlias(false);
        homeChart.getBorderPaint().setColor(Color.GRAY);
 
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        formatter.setFillPaint(lineFill);
        homeChart.getGraphWidget().setPaddingRight(2);
        homeChart.addSeries(series, formatter);
 
        // draw a domain tick for each day:
        homeChart.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 24*60*60*1000);
 
        // customize our domain/range labels
        homeChart.setDomainLabel("Dia");
        homeChart.setRangeLabel("Horas");
 
        homeChart.setRangeValueFormat(new Format() {
			private static final long serialVersionUID = 1L;

			@Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long timestamp = ((Number) obj).longValue();
                long totalMinutes = (timestamp / 60 / 1000);
                long totalHours = totalMinutes / 60;
        		totalMinutes = totalMinutes % 60;
                return new StringBuffer(TimeTrack.nf.format(totalHours) + ":" + TimeTrack.nf.format(totalMinutes));
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
 
        homeChart.setDomainValueFormat(new Format() {
			private static final long serialVersionUID = 1L;
			
			private SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
 
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long timestamp = ((Number) obj).longValue();
                Date date = new Date(timestamp);
                return new StringBuffer(dateFormat.format(date));
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
 
        homeChart.disableAllMarkup();
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
	    		total.setText(tt.getTimeTotalFormated());
	    		
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
    
    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class MyPurchaseObserver extends PurchaseObserver {
        public MyPurchaseObserver(Handler handler) {
            super(Home.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
            if (Consts.DEBUG) {
                Log.i(LOG_TAG, "supported: " + supported);
            }
            if (!supported) {
                showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
            }
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                int quantity, long purchaseTime, String developerPayload) {
            if (Consts.DEBUG) {
                Log.i(LOG_TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            }

            if (purchaseState == PurchaseState.PURCHASED) {
            	showDialog(DIALOG_DONATION_THANKS_ID);
            }
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            if (Consts.DEBUG) {
                Log.d(LOG_TAG, request.mProductId + ": " + responseCode);
            }
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.i(LOG_TAG, "purchase was successfully sent to server");
                }
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                if (Consts.DEBUG) {
                    Log.i(LOG_TAG, "user canceled purchase");
                }
            } else {
                if (Consts.DEBUG) {
                    Log.i(LOG_TAG, "purchase failed");
                }
            }
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.d(LOG_TAG, "completed RestoreTransactions request");
                }
            } else {
                if (Consts.DEBUG) {
                    Log.d(LOG_TAG, "RestoreTransactions error: " + responseCode);
                }
            }
        }
    }
}
