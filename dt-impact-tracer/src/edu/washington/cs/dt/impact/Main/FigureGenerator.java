package edu.washington.cs.dt.impact.Main;
import java.io.BufferedWriter;
import edu.washington.cs.dt.impact.data.Project;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;


public class FigureGenerator {
    // for Figures 17, 18, and 19
    protected static final String ORDER_TIME = "Execution time for executing the following testing order:";
    protected static final String ORDER_TIME_PARA = "New order time:";
    protected static final String APFD_VALUE = "APFD value:";
    protected static final DecimalFormat apfdFormat = new DecimalFormat(".00");
    protected static final DecimalFormat timeFormat = new DecimalFormat("#\\%");
    protected static final DecimalFormat percentFormat = new DecimalFormat("0");
    protected static boolean allowNegatives = false;
    // For Figures 7,8,9
    protected static final String FIXED_DTS = "Number of DTs fixed:";
    protected static final String NOT_FIXED_DTS = "Number of DTs not fixed:";
    // Project Names
    protected static final String CRYSTAL_NAME = "Crystal";
    protected static final String JFREECHART_NAME = "JFreechart";
    protected static final String JODATIME_NAME = "Joda-Time";
    protected static final String SYNOTPIC_NAME = "Synoptic";
    protected static final String XMLSECURITY_NAME = "XML Security";

    /*
     * A public method to search a file for a keyword and return the value that follows
     * that keyword
     *
     * @return the data value without any leading or trailing whitespaces, null if keyword not found
     */
    public static String parseFile(File file, String keyword) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String currLine = scanner.nextLine();
                if (currLine.contains(keyword)) {
                    // gets numeric value of data
                    String data = currLine.substring(keyword.length(), currLine.length());
                    scanner.close(); // close Scanner before returning
                    // trim away any whitespaces leading or after the data value
                    return data.trim();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        } finally {
            scanner.close();
        }

        return null; // none of the lines contained the keyword

    }
    /*
     * a public method that gets the line with all the flags in the file
     * that line starts with "-technique"
     */

    public static String getFlagsLine(File file) {
        Scanner scanner = null;
        String keyword = "-technique";
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String currLine = scanner.nextLine();
                if (currLine.contains(keyword)) {
                    scanner.close(); // close Scanner before returning
                    return currLine;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        } finally {
            scanner.close();
        }

        return null; // none of the lines contained the keyword -technique
    }

    /*
     * A public method that returns the name of the argument specified by the flag
     *
     * @return the name of the argument corresponding to flag
     */
    public static String getArgName(List<String> argsList, String flag) {
        String flagName = null;
        int index = argsList.indexOf(flag);
        if (index != -1) {
            int nameIndex = index + 1;
            if (nameIndex >= argsList.size()) {
                System.err.println(flag + " argument is specified but flagName is not." + " Please use the format: "
                        + flag + " flagName");
                System.exit(0);
            }
            flagName = argsList.get(nameIndex);
        } else {
            System.err
                    .println("No" + flag + " argument is specified." + " Please use the format: " + flag + " flagName");
            System.exit(0);
        }
        return flagName;
    }

    public static int getK(int i) {
        if (i == 0) {
            return 2;
        } else if (i == 2) {
            return 4;
        } else if (i == 4) {
            return 8;
        } else if (i == 6) {
            return 16;
        }
        return 1;
    }

    /*
     * a public method that searches a List<Project> objects for the project that matches projName
     *
     * @return -1 if no match found, otherwise index of the project with projName
     */

    public static int indexOfByName(List<Project> projList, String projName) {
        int index = 0;
        for (Project temp : projList) {
            if (temp.getName().equals(projName)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static void sortList(List<Project> projList, List<Project> sortedList, String keyword) {
        for (Project temp : projList) {
            if (temp.getName().equals(keyword)) {
                sortedList.add(temp);
                return;
            }
        }
    }

    /*
     * a public method that writes to the output file specified using the given latex string
     *
     */

    public static void writeToLatexFile(String latex, String outputFileName) {
        try {
            File outputFile = new File(outputFileName);
            FileWriter writer = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(latex);
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

}

