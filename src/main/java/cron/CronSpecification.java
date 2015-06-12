package cron;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a cron scheduling specification. Each column can contain single items, acceptable abbreviations,
 * ranges delimited by hyphen (-), comma delimited lists, or any combination of the previous tokens.
 *
 * <pre>
 * * * * * *
 * │ │ │ │ │
 * │ │ │ │ │
 * │ │ │ │ └───── day of week (0 - 6) (0 to 6 are Sunday to Saturday)
 * │ │ │ └────────── month (1 - 12)
 * │ │ └─────────────── day of month (1 - 31)
 * │ └──────────────────── hour (0 - 23)
 * └───────────────────────── min (0 - 59)
 * </pre>
 *
 * Examples: <br><br>
 * <b>0 0 1 * 1</b> -> Midnight, the first of every month, only if it's a monday<br>
 * <b>* * * Jan-Mar,5 1</b> -> Every minute of every monday in January, February, March, and May
 * <br><br>
 * Abbreviations (case-insensitive): <br><br>
 * <ul>
 * <li><b>Months:</b> jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec</li>
 * <li><b>Days:</b> sun,mon,tue,wed,thu,fri,sat</li>
 * </ul>
 */
public class CronSpecification {
    //these are used as limits, if empty, assume all should match
    private Set<Integer> minutes;
    private Set<Integer> hours;
    private Set<Integer> daysOfMonth;
    private Set<Integer> monthsOfYear;
    private Set<Integer> daysOfWeek;

    /**
     * Constuct a cron spec based on the given columnular specification.
     */
    public CronSpecification(String spec) {
        this();
        setSpecification(spec);
    }

    /**
     * Construct an empty cron spec, assume all times are targeted. Same as * * * * *.
     */
    public CronSpecification() {
        minutes = new HashSet<>(0);
        hours = new HashSet<>(0);
        daysOfMonth = new HashSet<>(0);
        monthsOfYear = new HashSet<>(0);
        daysOfWeek = new HashSet<>(0);
    }

    /**
     * Test whether a given date is targeted by this cron specification. i.e. should this cron specification
     * execute its goal for the given date and time?
     */
    public boolean isTargeted(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (minutes.size() == 0 || minutes.contains(cal.get(Calendar.MINUTE))) &&
                (hours.size() == 0 || hours.contains(cal.get(Calendar.HOUR_OF_DAY))) &&
                (daysOfMonth.size() == 0 || daysOfMonth.contains(cal.get(Calendar.DAY_OF_MONTH))) &&
                (monthsOfYear.size() == 0 || monthsOfYear.contains(cal.get(Calendar.MONTH) + 1)) &&
                (daysOfWeek.size() == 0 || daysOfWeek.contains(cal.get(Calendar.DAY_OF_WEEK) - 1));
    }

    /**
     * Shortcut to test a given timestamp based on epoch-time ms.

     */
    public boolean isTargeted(long ms) {
        return isTargeted(new Date(ms));
    }

    /**
     * Update the columnular specification this cron spec represents.
     */
    public void setSpecification(String spec) {
        String[] specs = spec.split("\\s+");
        if (specs.length != 5) {
            throw new IllegalArgumentException("Spec must contain 5 space-delimited columns");
        }
        setMinutes(specs[0]);
        setHours(specs[1]);
        setDaysOfMonth(specs[2]);
        setMonthsOfYear(specs[3]);
        setDaysOfWeek(specs[4]);
    }

    /**
     * Update the minutes column
     */
    public void setMinutes(String m) {
        handleNumericTokenSpec(minutes, m);
    }

    /**
     * Update the hours column
     */
    public void setHours(String h) {
        handleNumericTokenSpec(hours, h);
    }

    /**
     * Update the day of month column
     */
    public void setDaysOfMonth(String d) {
        handleNumericTokenSpec(daysOfMonth, d);
    }

    /**
     * Update the month column
     */
    public void setMonthsOfYear(String m) {
        String convertedM = m.replaceAll("[Jj][Aa][Nn]", "1")
                .replaceAll("[Ff][Ee][Bb]", "2")
                .replaceAll("[Mm][Aa][Rr]", "3")
                .replaceAll("[Aa][Pp][Rr]", "4")
                .replaceAll("[Mm][Aa][Yy]", "5")
                .replaceAll("[Jj][Uu][Nn]", "6")
                .replaceAll("[Jj][Uu][Ll]", "7")
                .replaceAll("[Aa][Uu][Gg]", "8")
                .replaceAll("[Ss][Ee][Pp]", "9")
                .replaceAll("[Oo][Cc][Tt]", "10")
                .replaceAll("[Nn][Oo][Vv]", "11")
                .replaceAll("[Dd][Ee][Cc]", "12");
        handleNumericTokenSpec(monthsOfYear, convertedM);
    }

    /**
     * Update the day of week column
     */
    public void setDaysOfWeek(String d) {
        String convertedD = d.replaceAll("[Ss][Uu][Nn]", "0")
                .replaceAll("[Mm][Oo][Nn]", "1")
                .replaceAll("[Tt][Uu][Ee]", "2")
                .replaceAll("[Ww][Ee][Dd]", "3")
                .replaceAll("[Tt][Hh][Uu]", "4")
                .replaceAll("[Ff][Rr][Ii]", "5")
                .replaceAll("[Ss][Aa][Tt]", "6");
        handleNumericTokenSpec(daysOfWeek, convertedD);
    }

    private void handleNumericTokenSpec(Set<Integer> specSet, String spec) {
        if (!spec.equals("*")) {
            Range r;
            for (String token : splitSpecToTokens(spec)) {
                if (isRangeToken(token)) {
                    r = getTokenRange(token);
                    int lower = Integer.parseInt(r.getLowerBound());
                    int upper = Integer.parseInt(r.getUpperBound());
                    if (lower > upper) {
                        throw new IllegalArgumentException("upper bounds cannot be less than lower bounds");
                    }
                    for (int i=Integer.parseInt(r.getLowerBound()); i<=Integer.parseInt(r.getUpperBound()); i++) {
                        specSet.add(i);
                    }
                } else {
                    specSet.add(Integer.parseInt(token));
                }
            }
        }
    }

    private String[] splitSpecToTokens(String spec) {
        return spec.split(",");
    }

    private boolean isRangeToken(String token) {
        return token.matches(".+-.+");
    }

    private Range getTokenRange(String token) {
        String[] parts = token.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Range must contain a lower and upper bounds, separated by hyphen");
        }
        return new Range(parts[0], parts[1]);
    }

    /**
     * Simple class to represent the parts of a cron range. The lower and upper will need to be converted
     * to their integer representation, this is only mean for lexical purposes.
     */
    private class Range {
        private String lowerBound;
        private String upperBound;

        public Range(String lowerBound, String upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public String getLowerBound() {
            return lowerBound;
        }
        public String getUpperBound() {
            return upperBound;
        }


    }
}
