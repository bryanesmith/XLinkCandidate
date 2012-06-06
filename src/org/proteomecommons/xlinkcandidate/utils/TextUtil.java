/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Reading/writing text, convenient formatting and streams.</p>
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class TextUtil {

    /**
     * <p>BigInteger representation of the number of bytes in a kilobyte.</p>
     */
    public static BigInteger KB = BigInteger.valueOf(Long.valueOf("1024")),
            /**
             * <p>BigInteger representation of the number of bytes in a megabyte.</p>
             */
            MB = BigInteger.valueOf(Long.valueOf("1048576")),
            /**
             * <p>BigInteger representation of the number of bytes in a gigabyte.</p>
             */
            GB = BigInteger.valueOf(Long.valueOf("1073741824")),
            /**
             * <p>BigInteger representation of the number of bytes in a terrabyte.</p>
             */
            TB = BigInteger.valueOf(Long.valueOf("1099511627776")),
            /**
             * <p>BigInteger representation of the number of bytes in a petabyte.</p>
             */
            PB = BigInteger.valueOf(Long.valueOf("1125899906842624")),
            /**
             * <p>BigInteger representation of the number of bytes in a exabyte.</p>
             */
            EB = new BigInteger("1152921504606846976");
    private static NumberFormat nf = NumberFormat.getInstance();

    static {
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(1);
    }
    private static String NEWLINE = null;
    // Token for representing newline on disk. The records are separated by newlines.
    private static final String NEWLINE_TOKEN = "<<NL>>";
    private static boolean lazyloaded = false;

    /**
     * <p>Internal method to lazily load resources.</p>
     */
    public static void lazyload() {
        if (lazyloaded) {
            return;
        }
        lazyloaded = true;

        try {
            NEWLINE = System.getProperty("line.separator");
        } catch (Exception nope) {
        }

        // If System.getProperty("line.separator") was fruitless, guess
        if (NEWLINE == null) {
            NEWLINE = "\n";
        }
    }

    /**
     * <p>Creates a comma-separated string from a list of strings.</p>
     * @param items Strings
     * @return A single string with the strings in the list separated by commas
     */
    public static String getCommaSeparatedString(List<String> items) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < items.size(); i++) {
            buffer.append(items.get(i));

            if (i < items.size() - 1) {
                buffer.append(", ");
            }
        }

        return buffer.toString();
    }

    /**
     * <p>Get platform-independent newline character.</p>
     * @return
     */
    public static String getNewLine() {
        lazyload();
        return NEWLINE;
    }

    /**
     * <p>Replace newlines with special token. To get original string, use detokenizeNewlines(String str).</p>
     * @param string
     * @return
     */
    public static String tokenizeNewlines(String string) {
        lazyload();
        if (string == null) {
            return null;
        }

        // \r\n first since others first could result in double newline tokens
        return string.replaceAll("\r\n", NEWLINE_TOKEN).replaceAll("\r", NEWLINE_TOKEN).replaceAll("\n", NEWLINE_TOKEN);
    }

    /**
     * <p>Replaces a string tokenized using tokenizeNewline(String str) with the original string.</p>
     * @param string
     * @return
     */
    public static String detokenizeNewlines(String string) {
        lazyload();
        if (string == null) {
            return null;
        }

        return string.replaceAll(NEWLINE_TOKEN, NEWLINE);
    }

    /**
     * <p>Creates a human-readable string representing the time ellapsed.</p>
     * @param milliseconds The time to represent in milliseconds
     * @return A string representing the time.
     */
    public static String getPrettyEllapsedTimeString(double milliseconds) {
        lazyload();
        return getPrettyEllapsedTimeString((long) milliseconds);
    }

    /**
     * <p>Creates a human-readable string representing the time ellapsed.</p>
     * @param milliseconds The time to represent in milliseconds
     * @return A string representing the time.
     */
    public static String getPrettyEllapsedTimeString(long milliseconds) {
        lazyload();
        StringBuffer prettyTime = new StringBuffer();

        final long MILLISECONDS = 1, SECONDS = MILLISECONDS * 1000, MINUTES = SECONDS * 60, HOURS = MINUTES * 60, DAYS = HOURS * 24;

        long days = milliseconds / DAYS;
        milliseconds %= DAYS;

        long hours = milliseconds / HOURS;
        milliseconds %= HOURS;

        long minutes = milliseconds / MINUTES;
        milliseconds %= MINUTES;

        long seconds = milliseconds / SECONDS;
        milliseconds %= SECONDS;

        if (days > 0) {
//            if (prettyTime.toString().length() != 0) prettyTime.append(", ");
            prettyTime.append(days + " day");
            if (days > 1) {
                prettyTime.append("s");
            }
        }
        if (hours > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(", ");
            }
            prettyTime.append(hours + " hour");
            if (hours > 1) {
                prettyTime.append("s");
            }
        }
        if (minutes > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(", ");
            }
            prettyTime.append(minutes + " minute");
            if (minutes > 1) {
                prettyTime.append("s");
            }
        }
        if (seconds > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(", ");
            }
            prettyTime.append(seconds + " second");
            if (seconds > 1) {
                prettyTime.append("s");
            }
        }

        // Always show minutes if nothing else
        if (prettyTime.toString().length() == 0 || milliseconds != 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(", ");
            }
            prettyTime.append(milliseconds + " millisecond");
            if (milliseconds > 1) {
                prettyTime.append("s");
            }
        }

        return prettyTime.toString();
    }

    public static String getShortPrettyEllapsedTimeString(long milliseconds) {
        lazyload();
        StringBuffer prettyTime = new StringBuffer();

        final long MILLISECONDS = 1, SECONDS = MILLISECONDS * 1000, MINUTES = SECONDS * 60, HOURS = MINUTES * 60, DAYS = HOURS * 24;

        long days = milliseconds / DAYS;
        milliseconds %= DAYS;

        long hours = milliseconds / HOURS;
        milliseconds %= HOURS;

        long minutes = milliseconds / MINUTES;
        milliseconds %= MINUTES;

        long seconds = milliseconds / SECONDS;
        milliseconds %= SECONDS;

        if (days > 0) {
            prettyTime.append(days + " d");
        }
        if (hours > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(" ");
            }
            prettyTime.append(hours + " h");
        }
        if (minutes > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(" ");
            }
            prettyTime.append(minutes + " m");
        }
        if (seconds > 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(" ");
            }
            prettyTime.append(seconds + " s");
        }

        // Always show minutes if nothing else
        if (prettyTime.toString().length() == 0 || milliseconds != 0) {
            if (prettyTime.toString().length() != 0) {
                prettyTime.append(" ");
            }
            prettyTime.append(milliseconds + " ms");
        }

        return prettyTime.toString();
    }

    /**
     * <p>Get a PrintStream that does nothing. A bit bucket.</p>
     * @return
     */
    public static NullPrintStream getNullPrintStream() {
        return new NullPrintStream();
    }

    /**
     * <p>Get a OutputStream that does nothing. A bit bucket.</p>
     * @return
     */
    public static NullOutputStream getNullOutputStream() {
        return new NullOutputStream();
    }

    /**
     * <p>Get a PrintStream that does nothing. A bit bucket.</p>
     */
    public static class NullPrintStream extends PrintStream {

        public NullPrintStream() {
            super(new NullOutputStream());
        }

        @Override()
        public void print(boolean b) { /* nope */ }

        @Override()
        public void print(char c) { /* nope */ }

        @Override()
        public void print(char[] s) { /* nope */ }

        @Override()
        public void print(double d) { /* nope */ }

        @Override()
        public void print(float f) { /* nope */ }

        @Override()
        public void print(int i) { /* nope */ }

        @Override()
        public void print(long l) { /* nope */ }

        @Override()
        public void print(Object obj) { /* nope */ }

        @Override()
        public void print(String s) { /* nope */ }

        @Override()
        public void println() { /* nope */ }

        @Override()
        public void println(boolean x) { /* nope */ }

        @Override()
        public void println(char x) { /* nope */ }

        @Override()
        public void println(char[] x) { /* nope */ }

        @Override()
        public void println(double x) { /* nope */ }

        @Override()
        public void println(float x) { /* nope */ }

        @Override()
        public void println(int x) { /* nope */ }

        @Override()
        public void println(long x) { /* nope */ }

        @Override()
        public void println(Object x) { /* nope */ }

        @Override()
        public void println(String x) { /* nope */ }

        @Override()
        protected void setError() { /* nope */ }

        @Override()
        public void write(byte[] buf, int off, int len) { /* nope */ }

        @Override()
        public void write(int b) { /* nope */ }
    }

    /**
     * <p>Get a OutputStream that does nothing. A bit bucket.</p>
     */
    public static class NullOutputStream extends OutputStream {

        public NullOutputStream() {
            super();
        }

        @Override()
        public void close() { /* Nope */ }

        @Override()
        public void flush() { /* Nope */ }

        @Override()
        public void write(byte[] b) { /* Nope */ }

        @Override()
        public void write(byte[] b, int off, int len) { /* Nope */ }

        @Override()
        public void write(int b) { /* Nope */ }
    }

    /**
     * <p>Returns Month dd yyyy, hh:mm AM/PM</p>
     * @param timestamp
     * @return
     */
    public static String getFormattedDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(timestamp));

        return getMonth(c) + " " + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR) + ", " + getHourAndMinutes(c);
    }

    /**
     * <p>Returns MONTH-DAY-YEAR. Doesn't contain spaces, suitable for files names, etc.</p>
     * @param timestamp
     * @return
     */
    public static String getFormattedDateSimple(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(timestamp));

        return getMonth(c) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.YEAR);
    }

    /**
     * <p>Returns human-friendly memory size.</p>
     * @param numBytes Number of bytes
     * @return Human-friendly string reporting in kB, MB, GB, TB, PB, or EB
     */
    public static String getFormattedBytes(long numBytes) {
        // set size postfix appropriately
        BigInteger bi = BigInteger.valueOf(numBytes);
        if (bi.compareTo(KB) < 0) {
            return nf.format(numBytes) + " bytes";
        } else if (bi.compareTo(MB) < 0) {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(KB.longValue())) + " KB";
        } else if (bi.compareTo(GB) < 0) {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(MB.longValue())) + " MB";
        } else if (bi.compareTo(TB) < 0) {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(GB.longValue())) + " GB";
        } else if (bi.compareTo(PB) < 0) {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(TB.longValue())) + " TB";
        } else if (bi.compareTo(EB) < 0) {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(PB.longValue())) + " PB";
        } else {
            return nf.format(Double.valueOf(numBytes) / Double.valueOf(EB.toString())) + " EB";
        }
    }

    /**
     * <p>Parses human-friendly size into number of bytes. See formatBytes(...) for complementary function.</p>
     * @param sizeString String with number of bytes followed by space and units.
     * @return Number of bytes
     */
    public static long parseBytes(String sizeString) {

        double bytes = 0;
        String numberString, unitsString;

        Pattern pattern = Pattern.compile("(.*)\\s*(\\w\\w\\w?\\w?\\w?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sizeString);

        if (matcher.find()) {
            numberString = matcher.group(1);
            unitsString = matcher.group(2).trim();

            bytes = Double.parseDouble(numberString);

            if (unitsString.equalsIgnoreCase("kb")) {
                bytes *= KB.longValue();
            } else if (unitsString.equalsIgnoreCase("mb")) {
                bytes *= MB.longValue();
            } else if (unitsString.equalsIgnoreCase("gb")) {
                bytes *= GB.longValue();
            } else if (unitsString.equalsIgnoreCase("tb")) {
                bytes *= TB.longValue();
            } else if (unitsString.equalsIgnoreCase("pb")) {
                bytes *= PB.longValue();
            } else if (unitsString.equalsIgnoreCase("bytes")); // Nothing, just bytes
            else {
                bytes = -1;
            }
        }

        return new Double(bytes).longValue();
    }

    /**
     * <p>Returns Weekday hh:mm AM/PM. E.g., Monday 1:30 PM</p>
     * @param timestamp
     * @return
     */
    public static String getWeekdayAndHour(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(timestamp));

        return getDayOfWeek(c) + " " + getHourAndMinutes(c);
    }

    /**
     * <p>Helper method to get the day of the week, e.g., "Mon", "Tues", etc.</p>
     * @param c
     * @return
     */
    private static String getDayOfWeek(Calendar c) {
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thur";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            case Calendar.SUNDAY:
                return "Sun";
        }
        return "Unknown";
    }

    /**
     * <p>Helper method to get the month, e.g., "January", "February", etc.</p>
     * @param c
     * @return
     */
    private static String getMonth(Calendar c) {
        switch (c.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
        }
        return "Unknown";
    }

    /**
     * <p>Get zero padded time, e.g., 07:05:54 PM</p>
     * @param c
     * @return
     */
    private static String getHourAndMinutes(Calendar c) {

        StringBuffer buffer = new StringBuffer();

        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);

        // Hours
        if (hour < 10) {
            buffer.append("0" + hour);
        } else {
            buffer.append(hour);
        }

        buffer.append(":");

        // Minutes
        if (min < 10) {
            buffer.append("0" + min);
        } else {
            buffer.append(min);
        }

        buffer.append(":");

        // Seconds
        if (sec < 10) {
            buffer.append("0" + sec);
        } else {
            buffer.append(sec);
        }

        buffer.append(".");

        // Milliseconds
        if (ms < 10) {
            buffer.append("00" + ms);
        } else if (ms < 100) {
            buffer.append("0" + ms);
        } else {
            buffer.append(ms);
        }

        switch (c.get(Calendar.AM_PM)) {
            case Calendar.AM:
                buffer.append(" AM");
                break;
            case Calendar.PM:
                buffer.append(" PM");
                break;
        }

        return buffer.toString();
    }

    /**
     * <p>Prints out breadth-first directory structure for humans. Nice to see files quickly.</p>
     * @param root
     */
    public static void printRecursiveDirectoryStructure(File root) {
        File[] file = new File[1];
        file[0] = root;
        recursivePrintRecursiveDirectoryStructure(file, 0);
    }

    /**
     * <p>Prints out breadth-first directory structure for humans. Nice to see files quickly.</p>
     * @param files
     * @param indent Number of spaces to indent files/directories for each subdirectory
     */
    private static void recursivePrintRecursiveDirectoryStructure(File[] files, int indent) {

        // Build up indentation
        StringBuffer indentation = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            indentation.append(" ");
        }

        // Recursively print
        for (File f : files) {
            if (f.isDirectory()) {
                System.out.println(indentation.toString() + f.getName() + "/");
                recursivePrintRecursiveDirectoryStructure(f.listFiles(), indent + 2);
            } else {
                System.out.println(indentation.toString() + f.getName() + ": " + TextUtil.getFormattedBytes(f.length()));
            }
        }
    }

    /**
     * <p>Returns the Levenstein distance between two strings, which is the number of operations necessary to match the strings. This is used to find similar strings.</p>
     * <p>Specify the relevant weight for insert, deleting or substituting. If you want equal weight, you do not need to specify.</p>
     * @param str1
     * @param str2
     * @param insertCost
     * @param deleteCost
     * @param substitutionCost
     * @return the Levenstein distance between two strings, which is the number of operations necessary to match the strings.
     */
    public static long getLevenshteinDistance(String str1, String str2, int insertCost, int deleteCost, int substitutionCost) {

        if (str1 == null && str2 == null) {
            throw new RuntimeException("You passed two null Strings, cannot calculate Levenshtein distance.");
        }

        if (str1 == null) {
            throw new RuntimeException("The first string you passed is null. Cannot calculate Levenshtein distance.");
        }

        if (str2 == null) {
            throw new RuntimeException("The second string you passed is null. Cannot calculate Levenshtein distance.");
        }

        long[][] distanceMatrix = new long[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i < str1.length(); i++) {
            distanceMatrix[i][0] = i;
        }

        for (int j = 0; j < str2.length(); j++) {
            distanceMatrix[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {

                // Get the characters from the string
                char str1Char = str1.charAt(i - 1);
                char str2Char = str2.charAt(j - 1);

                // Assume a substitution unless the chars are same
                long cost = substitutionCost;

                // If characters are equal, doesn't cost as much.
                if (str1Char == str2Char) {
                    cost = 0;
                }

                // Calculate deletion score
                long del = distanceMatrix[i - 1][j] + deleteCost;

                // Calculate insertion score
                long ins = distanceMatrix[i][j - 1] + insertCost;

                // Calculate substitution score
                long sub = distanceMatrix[i - 1][j - 1] + cost;

                // Find minimum
                List<Long> costs = new ArrayList<Long>(3);
                costs.add(del);
                costs.add(ins);
                costs.add(sub);

                Collections.sort(costs);

                // Set value to least cost.
                distanceMatrix[i][j] = costs.get(0);
            }
        }


        return distanceMatrix[str1.length()][str2.length()];
    }

    /**
     * <p>Returns the Levenstein distance between two strings, which is the number of operations necessary to match the strings. This is used to find similar strings.</p>
     * <p>If don't specify the cost of inserting, deleting or substituting, equal weight for each.</p>
     * @param str1
     * @param str2
     * @return The Levenstein distance between two strings, which is the number of operations necessary to match the strings.
     */
    public static long getLevenshteinDistance(String str1, String str2) {
        return getLevenshteinDistance(str1, str2, 1, 1, 1);
    }

    /**
     * <p>Returns the total number of characters not shared by two strings. Uses maximum string length and subtracts the number of identical characters at same position in both strings.</p>
     * <p>Very fast.</p>
     * @param str1
     * @param str2
     * @return The total number of characters not shared by two strings.
     */
    public static int getCharacterDifferenceBetweenStrings(String str1, String str2) {
        int sameCharCount = 0;
        for (int i = 0; i < str1.length() && i < str2.length(); i++) {

            char c1 = str1.charAt(i);
            char c2 = str2.charAt(i);

            if (c1 == c2) {
                sameCharCount++;
            }
        }

        int total = str1.length() > str2.length() ? str1.length() : str2.length();
        return total - sameCharCount;
    }

    /**
     * <p>Internal method to get the order of a base-64 character. Used for making an array of characters.</p>
     * @param c
     * @return
     */
    private static int getIndexForBase64Character(char c) {
        switch (c) {
            case 'A':
                return 0;
            case 'B':
                return 1;
            case 'C':
                return 2;
            case 'D':
                return 3;
            case 'E':
                return 4;
            case 'F':
                return 5;
            case 'G':
                return 6;
            case 'H':
                return 7;
            case 'I':
                return 8;
            case 'J':
                return 9;
            case 'K':
                return 10;
            case 'L':
                return 11;
            case 'M':
                return 12;
            case 'N':
                return 13;
            case 'O':
                return 14;
            case 'P':
                return 15;
            case 'Q':
                return 16;
            case 'R':
                return 17;
            case 'S':
                return 18;
            case 'T':
                return 19;
            case 'U':
                return 20;
            case 'V':
                return 21;
            case 'W':
                return 22;
            case 'X':
                return 23;
            case 'Y':
                return 24;
            case 'Z':
                return 25;
            case 'a':
                return 26;
            case 'b':
                return 27;
            case 'c':
                return 28;
            case 'd':
                return 29;
            case 'e':
                return 30;
            case 'f':
                return 31;
            case 'g':
                return 32;
            case 'h':
                return 33;
            case 'i':
                return 34;
            case 'j':
                return 35;
            case 'k':
                return 36;
            case 'l':
                return 37;
            case 'm':
                return 38;
            case 'n':
                return 39;
            case 'o':
                return 40;
            case 'p':
                return 41;
            case 'q':
                return 42;
            case 'r':
                return 43;
            case 's':
                return 44;
            case 't':
                return 45;
            case 'u':
                return 46;
            case 'v':
                return 47;
            case 'w':
                return 48;
            case 'x':
                return 49;
            case 'y':
                return 50;
            case 'z':
                return 51;
            case '0':
                return 52;
            case '1':
                return 53;
            case '2':
                return 54;
            case '3':
                return 55;
            case '4':
                return 56;
            case '5':
                return 57;
            case '6':
                return 58;
            case '7':
                return 59;
            case '8':
                return 60;
            case '9':
                return 61;
            case '+':
                return 62;
            case '/':
                return 63;
            case '=':
                return 64;

            default:
                throw new RuntimeException("Character " + c + " is not Base64");
        }

    }

    /**
     * <p>Calculates the difference between the contents of two strings.</p>
     * <p>The difference is the sum of all the differences between number of instances of particular Base64 character.</p>
     * @param str1 One of the strings to compare
     * @param str2 One of the strings to compare
     * @return The difference between all character counts (64 possible characters) for both strings.
     */
    public static int getCharacterCountDifferenceBetweenBase64Strings(String str1, String str2) {

        // Initialize two arrays. These contain count of each of 64 possible
        // characters, initialized to zero.
        // Why is array initialized to 65?
        char[] str1Chars = new char[65];
        for (int i = 0; i < str1Chars.length; i++) {
            str1Chars[i] = 0;
        }

        char[] str2Chars = new char[65];
        for (int i = 0; i < str2Chars.length; i++) {
            str2Chars[i] = 0;
        }

        for (char c : str1.toCharArray()) {
            // Increase count for particular Base64 character
            // E.g., if 'b' is at 2, found another, increment to 3
            str1Chars[getIndexForBase64Character(c)]++;
        }

        for (char c : str2.toCharArray()) {
            str2Chars[getIndexForBase64Character(c)]++;
        }

        int difference = 0;

        for (int i = 0; i < str1Chars.length; i++) {
            difference += Math.abs(str1Chars[i] - str2Chars[i]);
        }

        return difference;
    }

    /**
     * <p>Internal method to get the order of a base-16 character. Used for making an array of characters.</p>
     * @param c
     * @return
     */
    private static int getIndexForBase16Character(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
                return 10;
            case 'B':
                return 11;
            case 'C':
                return 12;
            case 'D':
                return 13;
            case 'E':
                return 14;
            case 'F':
                return 15;
            case 'a':
                return 10;
            case 'b':
                return 11;
            case 'c':
                return 12;
            case 'd':
                return 13;
            case 'e':
                return 14;
            case 'f':
                return 15;

            default:
                throw new RuntimeException("Character " + c + " is not Base64");
        }
    }

    /**
     * <p>Determine difference (count) between two base-16 strings. Higher numbers are more difference; 0 means no difference.</p>
     * @param str1
     * @param str2
     * @return The sum of all the differences between number of instances of particular Base16 character.
     */
    public static int getCharacterCountDifferenceBetweenBase16Strings(String str1, String str2) {

        char[] str1Chars = new char[16];
        for (int i = 0; i < str1Chars.length; i++) {
            str1Chars[i] = 0;
        }

        char[] str2Chars = new char[16];
        for (int i = 0; i < str2Chars.length; i++) {
            str2Chars[i] = 0;
        }

        for (char c : str1.toCharArray()) {
            str1Chars[getIndexForBase16Character(c)]++;
        }

        for (char c : str2.toCharArray()) {
            str2Chars[getIndexForBase16Character(c)]++;
        }

        int difference = 0;

        for (int i = 0; i < str1Chars.length; i++) {
            difference += Math.abs(str1Chars[i] - str2Chars[i]);
        }

        return difference;
    }

    /**
     * <p>Escape functionality for regular expression.</p>
     * @param aRegexFragment
     * @return
     */
    public static String forRegex(String aRegexFragment) {
        final StringBuilder result = new StringBuilder();

        final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            /*
             * All literals need to have backslashes doubled.
             */
            if (character == '.') {
                result.append("\\.");
            } else if (character == '\\') {
                result.append("\\\\");
            } else if (character == '?') {
                result.append("\\?");
            } else if (character == '*') {
                result.append("\\*");
            } else if (character == '+') {
                result.append("\\+");
            } else if (character == '&') {
                result.append("\\&");
            } else if (character == ':') {
                result.append("\\:");
            } else if (character == '{') {
                result.append("\\{");
            } else if (character == '}') {
                result.append("\\}");
            } else if (character == '[') {
                result.append("\\[");
            } else if (character == ']') {
                result.append("\\]");
            } else if (character == '(') {
                result.append("\\(");
            } else if (character == ')') {
                result.append("\\)");
            } else if (character == '^') {
                result.append("\\^");
            } else if (character == '$') {
                result.append("\\$");
            } else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
}

