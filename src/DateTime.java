public class DateTime implements IDateTime {

    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

    public DateTime(int year, int month, int day,
                    int hour, int minute) {

        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException(
                "Year must be between 1 and 9999."
            );
        }

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                "Month must be between 1 and 12."
            );
        }

        int maxDays = 31;

        if (month == 4 || month == 6
                || month == 9 || month == 11) {
            maxDays = 30;
        } else if (month == 2) {
            boolean leapYear = (year % 400 == 0)
                    || (year % 4 == 0 && year % 100 != 0);

            if (leapYear) {
                maxDays = 29;
            } else {
                maxDays = 28;
            }
        }

        if (day < 1 || day > maxDays) {
            throw new IllegalArgumentException(
                "Invalid day for the given month and year."
            );
        }

        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException(
                "Hour must be between 0 and 23."
            );
        }

        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(
                "Minute must be between 0 and 59."
            );
        }

        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

   
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

  
    public int getDay() {
        return day;
    }

    
    public int getHour() {
        return hour;
    }

    
    public int getMinute() {
        return minute;
    }

  
    public String format() {
        String yearText = "" + year;
        String monthText = "" + month;
        String dayText = "" + day;
        String hourText = "" + hour;
        String minuteText = "" + minute;

        // Pad the year to four digits.
        if (year < 10) {
            yearText = "000" + year;
        } else if (year < 100) {
            yearText = "00" + year;
        } else if (year < 1000) {
            yearText = "0" + year;
        }

       
        if (month < 10) {
            monthText = "0" + month;
        }

        if (day < 10) {
            dayText = "0" + day;
        }

        if (hour < 10) {
            hourText = "0" + hour;
        }

        if (minute < 10) {
            minuteText = "0" + minute;
        }

        return monthText + "/" + dayText + "/" + yearText
                + " " + hourText + ":" + minuteText;
    }

  
    public int compareTo(IDateTime other) {
        if (year < other.getYear()) {
            return -1;
        } else if (year > other.getYear()) {
            return 1;
        }

        if (month < other.getMonth()) {
            return -1;
        } else if (month > other.getMonth()) {
            return 1;
        }

        if (day < other.getDay()) {
            return -1;
        } else if (day > other.getDay()) {
            return 1;
        }

        if (hour < other.getHour()) {
            return -1;
        } else if (hour > other.getHour()) {
            return 1;
        }

        if (minute < other.getMinute()) {
            return -1;
        } else if (minute > other.getMinute()) {
            return 1;
        }

        return 0;
    }

    
    public String toString() {
        return format();
    }
}
