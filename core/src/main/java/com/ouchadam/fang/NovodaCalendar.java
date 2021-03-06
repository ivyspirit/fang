package com.ouchadam.fang;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class NovodaCalendar implements Serializable, Cloneable {

    private final Calendar time;

    public NovodaCalendar(String time) {
        this(Calendar.getInstance());
        parseTime(time);
    }

    public NovodaCalendar(long time) {
        this(Calendar.getInstance());
        parseTime(time);
    }

    private synchronized void parseTime(String date) {
        try {
            setTime(getDateFormat().parse(date));
        } catch (ParseException ignored) {
            System.out.println("Couldn't parse : " + date);
            ignored.printStackTrace();
        }
    }

    protected abstract DateFormat getDateFormat();

    private void parseTime(long date) {
        setTime(new Date(date));
    }

    private void setTime(Date date) {
        time.setTime(date);
    }

    private NovodaCalendar(Calendar time) {
        this.time = time;
    }

    public void adjustDate(NovodaCalendar reference) {
        time.set(Calendar.YEAR, reference.getField(Calendar.YEAR));
        time.set(Calendar.MONTH, reference.getField(Calendar.MONTH));
        time.set(Calendar.DAY_OF_MONTH, reference.getField(Calendar.DAY_OF_MONTH));
        time.set(Calendar.DAY_OF_WEEK, reference.getField(Calendar.DAY_OF_WEEK));
        time.set(Calendar.DAY_OF_WEEK_IN_MONTH, reference.getField(Calendar.DAY_OF_WEEK_IN_MONTH));
        time.set(Calendar.DAY_OF_YEAR, reference.getField(Calendar.DAY_OF_YEAR));
    }

    private String getField(int field, int style) {
        return time.getDisplayName(field, style, Locale.ENGLISH);
    }

    private int getField(int field) {
        return time.get(field);
    }

    public long getTimeInMillis() {
        return time.getTimeInMillis();
    }

    public String get(DateText dateText) {
        return dateText.get(this);
    }

    public int get(DateInteger dateInteger) {
        return dateInteger.get(this);
    }

    public Date asDate() {
        return time.getTime();
    }

    @Override
    public String toString() {
        return get(DateInteger.HOUR_24) + " : " + get(DateInteger.MINUTE);
    }

    public enum DateText {
        LONG_DAY {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.DAY_OF_WEEK, Calendar.LONG);
            }
        },
        SHORT_DAY {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.DAY_OF_WEEK, Calendar.SHORT);
            }
        },
        LONG_MONTH {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.MONTH, Calendar.LONG);
            }
        },
        SHORT_MONTH {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.MONTH, Calendar.SHORT);
            }
        },
        DAY_SUFFIX {
            @Override
            String get(NovodaCalendar calendar) {
                return DAY_SUFFIXES[calendar.get(DateInteger.DAY_IN_MONTH)];
            }
        },
        AM_PM_LOWER_CASE {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.AM_PM) == Calendar.AM ? AM : PM;
            }
        },
        AM_PM_UPPER_CASE {
            @Override
            String get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.AM_PM) == Calendar.AM ? AM.toUpperCase() : PM.toUpperCase();
            }
        };

        private static final String PM = "pm";
        private static final String AM = "am";

        private static final String[] DAY_SUFFIXES = {
                "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                "th", "st"
        };

        abstract String get(NovodaCalendar calendar);

    }

    public enum DateInteger {
        MINUTE {
            @Override
            int get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.MINUTE);
            }
        },
        HOUR_24 {
            @Override
            int get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.HOUR);
            }
        },
        DAY_IN_MONTH {
            @Override
            int get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.DAY_OF_MONTH);
            }
        },
        MONTH {
            @Override
            int get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.MONTH);
            }
        },
        YEAR {
            @Override
            int get(NovodaCalendar calendar) {
                return calendar.getField(Calendar.YEAR);
            }
        };

        abstract int get(NovodaCalendar calendar);

    }

}