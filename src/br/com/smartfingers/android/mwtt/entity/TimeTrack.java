package br.com.smartfingers.android.mwtt.entity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeTrack implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static final NumberFormat nf;
	public static final String FILENAME = "timeTrack.obj";
	
	static{
		nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		nf.setMaximumFractionDigits(0);
	}
	
	public Integer id;
	public Date date;
	public Integer hourIn;
	public Integer minuteIn;
	public Integer hourOut;
	public Integer minuteOut;
	public Integer hourLunch = 1;
	public Integer minuteLunch = 0;
	
	public TimeTrack() {
		date = new Date();
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append(sdf.format(date));
		
		if(hourIn != null && minuteIn != null) toString.append(" [" + nf.format(hourIn) + ":" + nf.format(minuteIn));
		if(hourOut != null && minuteOut != null) toString.append(" - " + nf.format(hourOut) + ":" + nf.format(minuteOut));
		toString.append("]");
		
		return toString.toString();
	}
	
	public String getTimeIn(){
		return nf.format(hourIn) + ":" + nf.format(minuteIn);
	}
	
	public String getTimeOut(){
		return nf.format(hourOut) + ":" + nf.format(minuteOut);
	}
	
	public String getTimeLunch(){
		return nf.format(hourLunch) + ":" + nf.format(minuteLunch);
	}
	
	public String getTimeTotal(){
		int totalMinutesIn = (hourIn * 60) + minuteIn;
		int totalMinutesOut = (hourOut * 60) + minuteOut;
		int totalMinutesLunch = 0;
		
		if(hourLunch != null){
			totalMinutesLunch = (hourLunch * 60) + minuteLunch;
		}
		
		int totalMinutes = totalMinutesOut - (totalMinutesIn + totalMinutesLunch);
		int totalHours = totalMinutes / 60;
		totalMinutes = totalMinutes % 60;
		
		return nf.format(totalHours) + ":" + nf.format(totalMinutes);
	}
	
	public boolean isBeforeToday(){
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		return date.before(today.getTime());
	}
}
