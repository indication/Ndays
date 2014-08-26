package com.github.indication.ndays;

import android.content.Context;

import java.math.BigDecimal;
import java.sql.Date;

public class DateTimeDistance {
	static class CalculateDistance {
		private int resource;
		private int resource_after;
		private int resource_before;
		private int base;
		private int filter;
		public CalculateDistance(int base, int resource, int filter){
			this.base = base;
			this.resource = resource;
			this.filter = filter;
			this.resource_after = R.string.label_after;
			this.resource_before = R.string.label_before;
		}
		public CalculateDistance(int base, int resource, int resource_before, int resource_after, int filter){
			this.base = base;
			this.resource = resource;
			this.filter = filter;
			this.resource_after = resource_after;
			this.resource_before = resource_before;
		}
		public boolean isEnabled(int filter){
			return (this.filter & filter) != filter;
		}
		public boolean isValid(long absdiff){
			if (base < absdiff)
				return true;
			return false;
		}
		public String getStringBefore(Context con, long absdiff){
			return con.getString(resource_before, getStringDiff(con, absdiff));
		}
		public String getStringAfter(Context con, long absdiff){
			return con.getString(resource_after, getStringDiff(con, absdiff));
		}
		protected String getStringDiff(Context con, long diff){
			return con.getString(resource,getDiff(diff).toString());
		}
		protected BigDecimal getDiff(long diff){
			BigDecimal bd = new BigDecimal(diff);
			bd = bd.divide(new BigDecimal(base), 0, BigDecimal.ROUND_HALF_DOWN);
			return bd;
		}
	}
	static final int INTERVAL_ALL           = 0xFFFFFFFF;
	static final int INTERVAL_SECOND        = 0x00000001;
	static final int INTERVAL_MINUTES       = 0x00000002;
	static final int INTERVAL_HOURS         = 0x00000004;
	static final int INTERVAL_DAYS          = 0x00000010;
	static final int INTERVAL_WEEK          = 0x00001000;
	static final int INTERVAL_LASTWEEK      = 0x00010000;
	static final int INTERVAL_MONTHS        = 0x00000200;
	static final int INTERVAL_YEARS         = 0x00000400;
	static final int INTERVAL_HALFYEARS     = 0x01000000;
	static CalculateDistance[] distances = {
			 new CalculateDistance(0, R.plurals.distance_seconds, INTERVAL_SECOND)
			,new CalculateDistance(60, R.plurals.distance_minutes, INTERVAL_MINUTES)
			,new CalculateDistance(60*60, R.plurals.distance_hours, INTERVAL_HOURS)
			,new CalculateDistance(60*60*24, R.plurals.distance_days, INTERVAL_DAYS)
			,new CalculateDistance(60*60*24*7, R.plurals.distance_weeks, R.string.label_last_week, R.string.label_next_week , INTERVAL_WEEK)
			,new CalculateDistance(60*60*24*7, R.plurals.distance_weeks, R.string.label_last_week, R.string.label_next_week , INTERVAL_LASTWEEK){
				@Override
				protected String getStringDiff(Context con, long diff) {
					String[] array = con.getResources().getStringArray(R.array.weeks);
					if(array == null) return "";
					int val = getDiff(diff).intValue() % array.length;
					return array[val];
				}
			}
			,new CalculateDistance(60*60*24*30, R.plurals.distance_months, INTERVAL_MONTHS)
			,new CalculateDistance(60*60*24*183, R.plurals.distance_half_years, INTERVAL_HALFYEARS)
			,new CalculateDistance(60*60*24*365, R.plurals.distance_years, INTERVAL_YEARS)
	};
	static String getDistance(Context con, Date current, Date target, int interval){
		long diff = target.getTime() - current.getTime();
		long absdiff = Math.abs(diff);
		CalculateDistance handler = null;
		for(CalculateDistance item : distances){
			if(!item.isEnabled(interval)) continue;
			if (!item.isValid(absdiff)) break;
			handler = item;
		}
		if(handler == null)
			return "";
		if(diff <= 0)
			return handler.getStringBefore(con, absdiff);
		else
			return handler.getStringAfter(con, absdiff);
	}
}
