
package edu.washington.cs.dt.impact.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.washington.cs.dt.impact.data.GeometricMeanData;
import edu.washington.cs.dt.impact.data.Project;
import edu.washington.cs.dt.impact.data.ProjectEnhancedResults;
import edu.washington.cs.dt.impact.util.Constants;
import edu.washington.cs.dt.impact.Main.FigureGenerator;

public class EnhancedResultsFigureGenerator extends FigureGenerator {

    /*
     * a private method to generate a line of LaTeX needed for figure 17
     *
     * @param projectName the name of the Project
     *
     * @param values the ordered values that need to be written
     *
     *
     * format: Crystal & 0.026 & 0.001 & 0.000 & 0.000 \\
     */
    private static String generate17(String projectName, double[] values, double orig_apfd_value,
            boolean[] nonZeroNumOfDTS) {
        String result = projectName;
        // i represents unenhanced, i + 1 represents enhanced
        for (int i = 0; i + 1 < values.length; i += 2) {
            double val;
            if (!nonZeroNumOfDTS[i]) {
                // enhanced - unenhanced
                val = values[i + 1] - values[i];
            } else {
                // enhanced - orig
                val = values[i + 1] - orig_apfd_value;
            }
            if (!allowNegatives && val < 0.0) {
                val = 0.0;
            }
            String output = apfdFormat.format(val);
            if (output.equals("-.00")) {
                output = ".00";
            }
            result += " & ";
            if (val >= 0.0 || output.equals(".00")) {
                result += "\\phantom{-}";// + output;
            }
            result += output;
        }
        result += " \\\\"; // "\\"
        return result;
    }

    /*
     * a private method to generate a line of LaTeX needed for figure 18
     *
     * @param projectName the name of the project
     *
     * @param percentages ordered percentages needed
     *
     * @param values the ordered values that need to be written
     *
     * @param orig_value the run time for running the entire original test suite unordered
     *
     * format: Crystal & 5.4\%\pa & 1.3\%\pa & 0.000 & 0.000 & 0.008 & 0.015 & 0.036 & 0.000 \\
     */
    private static String generate18(String projectName, double[] percentages, double[] values, double orig_time_value,
            String type, boolean[] nonZeroNumOfDTS, double orig_apfd_value) {
        String result = projectName + "       ";
        // i represents unenhanced, i + 1 represents enhanced
        for (int i = 0; i + 1 < percentages.length; i += 2) {
            // enhancedParaSpeedup,2,3 correspond to either S1,S2,S3 or S4,S5,S6
            // enhanced - unenhanced
            /*
             * double enhancedParaSpeedup = (percentages[i + 1] - percentages[i]) / orig_value * 100;
             * double unenhancedPara = (percentages[i + 3] - percentages[i + 2]) / orig_value * 100;
             * double diffBetweenEnhancedUnenhanced = (percentages[i + 5] - percentages[i + 4]) / orig_value * 100;
             * // the max. of these 3 values
             * double percent = Math.max(enhancedParaSpeedup, Math.max(unenhancedPara, diffBetweenEnhancedUnenhanced));
             */
            double percent;
            // i represents unenhanced, i + 1 represents enhanced
            if (!nonZeroNumOfDTS[i]) {
                // unenhanced - enhanced
                percent = (percentages[i] - percentages[i + 1]) / orig_time_value * 100;
            } else {
                // orig - enhanced
                percent = (orig_time_value - percentages[i + 1]) / orig_time_value * 100;
            }

            if (!allowNegatives && percent < 0.0) {
                percent = 0.0;
            }

            result += " & ";
            String output = percentFormat.format(percent);
            if (output.equals("-0")) {
                output = "0";
            }
            if (percent >= 0.0 || output.equals("0")) {
                result += "\\phantom{-}";
                if (output.length() == 1) // single digit number, #\%
                {
                    result += "\\phantom{1}";
                }
            } else { // negative
                if (output.length() == 2) {// single digit number, #\%
                    result += "\\phantom{1}";
                }
            }

            result += output + "\\%\\pa"; // "\%\pa"

        }
        // i represents unenhanced, i + 1 represents enhanced
        for (int i = 0; i + 1 < values.length; i += 2) {
            double val;

            if (!nonZeroNumOfDTS[i]) {
                // enhanced - unenhanced
                val = values[i + 1] - values[i];
            } else {
                // enhanced - orig
                val = values[i + 1] - orig_apfd_value;
            }
            if (!allowNegatives && val < 0.0) {
                val = 0.0;
            }
            String output = apfdFormat.format(val);
            if (output.equals("-.00")) {
                output = ".00";
            }
            result += " & ";
            if (val >= 0.0 || output.equals(".00")) {
                result += "\\phantom{-}";
            }
            result += output;
        }
        result += " \\\\"; // "\\"
        return result;
    }

    /*
     * a private method that generates Figure 19
     */
    private static String generate19(String projectName, double[] orig_values, double[] time_values,
            double orig_time_value, List<GeometricMeanData> fig19GeoData, boolean[] nonZeroNumOfDTS,
            double orig_apfd_value) {
        String result = projectName;
        double enhancedParaSpeedup = 0;
        double unenhancedPara = 0;
        double diffBetweenEnhancedUnenhanced = 0;
        // i represents unenhanced, i + 1 represents enhanced
        for (int i = 0; i + 2 <= orig_values.length; i += 2) {
            // enhanced
            enhancedParaSpeedup = (orig_values[i + 1] / orig_time_value);
            if (!nonZeroNumOfDTS[i]) {
                // unenhanced
                unenhancedPara = (orig_values[i] / orig_time_value);
            } else {
                // orig
                unenhancedPara = (orig_time_value / orig_time_value);
            }
            // enhanced - unenhanced or enhanced - orig if DTS != 0
            diffBetweenEnhancedUnenhanced = unenhancedPara - enhancedParaSpeedup;

            String output = timeFormat.format(diffBetweenEnhancedUnenhanced);
            if (output.equals("-0\\%")) {
                output = "0\\%";
            }

            result += " & ";
            if (diffBetweenEnhancedUnenhanced >= 0.0 || output.equals("0\\%")) {
                result += "\\phantom{-}";
                if (output.length() == 3) // single digit number, #\%
                {
                    result += "\\phantom{1}";
                }
            } else { // negative
                if (output.length() == 4) {// single digit number, #\%
                    result += "\\phantom{1}";
                }
            }

            result += output;
            // result += " & " + timeFormat.format(enhancedParaSpeedup) + " $\\rightarrow$ " +
            // timeFormat.format(unenhancedPara);
            fig19GeoData.add(new GeometricMeanData(getK(i), enhancedParaSpeedup, Constants.TD_SETTING.OMITTED_TD,
                    Constants.ORDER.ORIGINAL));
            fig19GeoData.add(new GeometricMeanData(getK(i), unenhancedPara, Constants.TD_SETTING.GIVEN_TD,
                    Constants.ORDER.ORIGINAL));
        }
        // i represents unenhanced, i + 1 represents enhanced
        for (int i = 0; i + 2 <= time_values.length; i += 2) {
            // enhanced
            enhancedParaSpeedup = (time_values[i + 1] / orig_time_value);
            if (!nonZeroNumOfDTS[i]) {
                // unenhanced
                unenhancedPara = (time_values[i] / orig_time_value);
            } else {
                // orig
                unenhancedPara = (orig_time_value / orig_time_value);
            }
            // enhanced - unenhanced or enhanced - orig if DTS != 0
            diffBetweenEnhancedUnenhanced = unenhancedPara - enhancedParaSpeedup;
            String output = timeFormat.format(diffBetweenEnhancedUnenhanced);
            if (output.equals("-0\\%")) {
                output = "0\\%";
            }

            result += " & ";
            if (diffBetweenEnhancedUnenhanced >= 0.0 || output.equals("0\\%")) {
                result += "\\phantom{-}";
                if (output.length() == 3) // single digit number, #\%
                {
                    result += "\\phantom{1}";
                }
            } else { // negative
                if (output.length() == 4) {// single digit number, #\%
                    result += "\\phantom{1}";
                }
            }

            result += output;
            // result += " & " + timeFormat.format(enhancedParaSpeedup) + " $\\rightarrow$ " +
            // timeFormat.format(unenhancedPara);
            fig19GeoData.add(new GeometricMeanData(getK(i), enhancedParaSpeedup, Constants.TD_SETTING.OMITTED_TD,
                    Constants.ORDER.TIME));
            fig19GeoData.add(new GeometricMeanData(getK(i), unenhancedPara, Constants.TD_SETTING.GIVEN_TD,
                    Constants.ORDER.TIME));
        }
        result += "\\\\";

        return result;
    }

    /*
     * a private method to generate the LaTeX format of the data of each Project in projList
     *
     * @param projList a list of Projects that each contain data
     */

    private static String generateLatexString(List<Project> projList, List<Project> otherProjList, String type) {
        String latexString = "";
        List<Project> sortedList = new ArrayList<Project>();
        sortList(projList, sortedList, CRYSTAL_NAME);
        sortList(projList, sortedList, JFREECHART_NAME);
        sortList(projList, sortedList, JODATIME_NAME);
        sortList(projList, sortedList, SYNOTPIC_NAME);
        sortList(projList, sortedList, XMLSECURITY_NAME);

        int index = 0;
        List<GeometricMeanData> fig19GeoData = new ArrayList<>();
        for (Project projTemp : sortedList) {
            ProjectEnhancedResults temp = (ProjectEnhancedResults) projTemp;
            // get the correct orig_value...one of the Lists will not have the correct value (will be 0)
            if (temp.isFig19()) {
                double orig_time_value = (temp.getOrigTimeValue() == 0)
                        ? ((ProjectEnhancedResults) otherProjList.get(index)).getOrigTimeValue()
                        : temp.getOrigTimeValue();
                double orig_apfd_value = (temp.getOrigAPFDValue() == 0)
                        ? ((ProjectEnhancedResults) otherProjList.get(index)).getOrigAPFDValue()
                        : temp.getOrigAPFDValue();
                latexString += generate19(temp.getName(), temp.get_fig19_orig(), temp.get_fig19_time(), orig_time_value,
                        fig19GeoData, temp.get_fig19_nonZeroNumOfDTS(), orig_apfd_value);
                latexString += "\r\n";
            } else if (temp.isFig18()) {
                double orig_time_value = (temp.getOrigTimeValue() == 0)
                        ? ((ProjectEnhancedResults) otherProjList.get(index)).getOrigTimeValue()
                        : temp.getOrigTimeValue();
                double orig_apfd_value = (temp.getOrigAPFDValue() == 0)
                        ? ((ProjectEnhancedResults) otherProjList.get(index)).getOrigAPFDValue()
                        : temp.getOrigAPFDValue();
                latexString += generate18(temp.getName(), temp.get_fig18_percents(), temp.get_fig18_values(),
                        orig_time_value, type, temp.get_fig18_nonZeroNumOfDTS(), orig_apfd_value);
                latexString += "\r\n";
            } else if (temp.isFig17()) {
                double orig_apfd_value = (temp.getOrigAPFDValue() == 0)
                        ? ((ProjectEnhancedResults) otherProjList.get(index)).getOrigAPFDValue()
                        : temp.getOrigAPFDValue();
                latexString += generate17(temp.getName(), temp.get_fig17_values(), orig_apfd_value,
                        temp.get_fig17_nonZeroNumOfDTS());
                latexString += "\r\n";
            }
            index++;
        }

        if (!fig19GeoData.isEmpty()) {
            int columns = 16;
            double[] geometricMeans = new double[columns];
            for (int i = 0; i < columns; i++) {
                double value = 1.0;
                int counter = 0;
                for (GeometricMeanData data : fig19GeoData) {
                    if (i % 2 == 0 && !data.getTdSetting().equals(Constants.TD_SETTING.OMITTED_TD)) {
                        continue;
                    }
                    if (i % 2 == 1 && !data.getTdSetting().equals(Constants.TD_SETTING.GIVEN_TD)) {
                        continue;
                    }
                    if (i >= 0 && i < 8 && !data.getOrder().equals(Constants.ORDER.ORIGINAL)) {
                        continue;
                    }
                    if (i >= 8 && i < 16 && !data.getOrder().equals(Constants.ORDER.TIME)) {
                        continue;
                    }

                    if (i == 0 && data.getK() == 2) {
                        value *= data.getValue();
                    } else if (i == 1 && data.getK() == 2) {
                        value *= data.getValue();
                    } else if (i == 2 && data.getK() == 4) {
                        value *= data.getValue();
                    } else if (i == 3 && data.getK() == 4) {
                        value *= data.getValue();
                    } else if (i == 4 && data.getK() == 8) {
                        value *= data.getValue();
                    } else if (i == 5 && data.getK() == 8) {
                        value *= data.getValue();
                    } else if (i == 6 && data.getK() == 16) {
                        value *= data.getValue();
                    } else if (i == 7 && data.getK() == 16) {
                        value *= data.getValue();
                    } else if (i == 8 && data.getK() == 2) {
                        value *= data.getValue();
                    } else if (i == 9 && data.getK() == 2) {
                        value *= data.getValue();
                    } else if (i == 10 && data.getK() == 4) {
                        value *= data.getValue();
                    } else if (i == 11 && data.getK() == 4) {
                        value *= data.getValue();
                    } else if (i == 12 && data.getK() == 8) {
                        value *= data.getValue();
                    } else if (i == 13 && data.getK() == 8) {
                        value *= data.getValue();
                    } else if (i == 14 && data.getK() == 16) {
                        value *= data.getValue();
                    } else if (i == 15 && data.getK() == 16) {
                        value *= data.getValue();
                    } else {
                        continue;
                    }
                    counter += 1;
                }
                geometricMeans[i] = Math.pow(value, (1.0 / counter));
            }
            StringBuilder sb = new StringBuilder();
            sb.append("\\hline\r\nGeometric mean");
            for (int i = 0; i + 2 <= geometricMeans.length; i += 2) {
                double diffOfGeometricMeans = geometricMeans[i + 1] - geometricMeans[i];
                String diffStringFormat = timeFormat.format(diffOfGeometricMeans);
                if (diffStringFormat.equals("-0\\%")) {
                    diffStringFormat = "0\\%";
                }
                if (diffOfGeometricMeans >= 0.0 || diffStringFormat.equals("0\\%")) {
                    diffStringFormat = "\\phantom{-}" + diffStringFormat;
                }
                if (diffOfGeometricMeans * 100 < 10.0 && diffOfGeometricMeans * 100 > -10.0) {
                    diffStringFormat = "\\phantom{1}" + diffStringFormat;
                }
                sb.append(" & " + diffStringFormat);
            }
            sb.append(" \\\\");
            latexString += sb.toString();
        }

        // take off the "\r\n" from the last line
        return latexString;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));

        allowNegatives = argsList.contains("-allowNegatives");

        String directoryName = getArgName(argsList, "-directory");
        // name of directory where files should be outputted
        String outputDirectoryName = getArgName(argsList, "-outputDirectory");

        /*
         * 17: prioritization
         * 18: selection
         * 19: parallelizaiton
         */

        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        // create a list of project Objects that each have a diff project name
        int size = 5;
        List<Project> proj_orig_arrayList = new ArrayList<Project>(size);
        List<Project> proj_auto_arrayList = new ArrayList<Project>(size);

        for (File file : fList) {
            if (file.isFile()) {

                // String containing all the flags
                String flagsInFile = getFlagsLine(file);
                if (flagsInFile == null) {
                    continue;
                }
                // get rid of square brackets
                flagsInFile = flagsInFile.substring(1, flagsInFile.length() - 1);
                String[] flags = flagsInFile.split(",");
                List<String> flagsList = Arrays.asList(flags);
                // get rid of whitespaces
                for (int i = 0; i < flagsList.size(); i++) {
                    flagsList.set(i, flagsList.get(i).trim());
                }
                int index = flagsList.indexOf("-technique");
                // index + 1 because want arg after the index of flag
                String techniqueName = flagsList.get(index + 1);

                index = flagsList.indexOf("-coverage");
                String coverageName = flagsList.get(index + 1);

                index = flagsList.indexOf("-order");
                String orderName = flagsList.get(index + 1);

                index = flagsList.indexOf("-project");
                String projectName = flagsList.get(index + 1);
                if (projectName.equals("crystal")) {
                    projectName = CRYSTAL_NAME;
                } else if (projectName.equals("jfreechart")) {
                    projectName = JFREECHART_NAME;
                } else if (projectName.equals("jodatime")) {
                    projectName = JODATIME_NAME;
                } else if (projectName.equals("synoptic")) {
                    projectName = SYNOTPIC_NAME;
                } else if (projectName.equals("xml_security")) {
                    projectName = XMLSECURITY_NAME;
                } else {
                    System.err.println("Project argument is specified but the project name"
                            + " value provided is invalid. Please use either crystal, jfreechart, jodatime, synoptic or xml_security.");
                    System.exit(0);
                }

                index = flagsList.indexOf("-testType");
                String testType = flagsList.get(index + 1);

                index = flagsList.indexOf("-dependentTestFile");
                String resolveDependences = null;
                // if index = -1, flag not present
                if (index != -1) {
                    resolveDependences = flagsList.get(index);
                }

                // see if List needs to orig or auto generated one
                List<Project> currProjList = null;
                if (testType.equals("auto")) {
                    currProjList = proj_auto_arrayList;
                } else if (testType.equals("orig")) {
                    currProjList = proj_orig_arrayList;
                }

                // index of this project in the arrayList, might be -1 if not found
                int indexOfProj = indexOfByName(currProjList, projectName);

                // Project Object that corresponds to the current project name in this file
                Project currProj2 = null;

                if (indexOfProj != -1) {
                    currProj2 = currProjList.get(indexOfProj);
                } else {// projectName not seen before

                    currProj2 = new ProjectEnhancedResults(projectName);
                    currProjList.add(currProj2);
                }
                ProjectEnhancedResults currProj = (ProjectEnhancedResults) currProj2;

                int numFixed = Integer.parseInt(parseFile(file, FIXED_DTS));
                int numNotFixed = Integer.parseInt(parseFile(file, NOT_FIXED_DTS));
                int numTotal = numFixed + numNotFixed;

                /*
                 * The method setNumTotalDependentTests sets the number of DTS for a specific file corresponding to a
                 * project such as Crystal
                 *
                 * setNumTotalDependentTests(int figNum, int index, int numTotal)
                 *
                 * @param figNum represents the figure number in the paper (17, 18, or 19)
                 *
                 * @param index indicates which column in the figure it corresponds to (i.e. Figure 17 column 0
                 * represents T3)
                 *
                 * @param numTotal is the total number of DTS for that file
                 *
                 */

                // parallelization technique, figure 19
                if (techniqueName.equals("parallelization")) {
                    String order_time_string = parseFile(file, ORDER_TIME_PARA);
                    double order_time = Double.parseDouble(order_time_string);
                    // order will be time or original
                    // k = 2, 4, 8, 16 is the number of machines
                    index = flagsList.indexOf("-numOfMachines");
                    String numMachines_string = flagsList.get(index + 1);
                    int numMachines = Integer.parseInt(numMachines_string);

                    currProj.useFig19();
                    // this array will correspond to P1 or P2
                    double[] curr_fig19_array = null;
                    if (orderName.equals("original")) {
                        curr_fig19_array = currProj.get_fig19_orig();
                    } else { // orderName == time
                        curr_fig19_array = currProj.get_fig19_time();
                    }

                    if (resolveDependences == null) { // non-enhanced
                        if (numMachines == 2) {
                            curr_fig19_array[0] = order_time;
                            currProj.setNumTotalDependentTests(19, 0, numTotal);
                        } else if (numMachines == 4) {
                            curr_fig19_array[2] = order_time;
                            currProj.setNumTotalDependentTests(19, 2, numTotal);
                        } else if (numMachines == 8) {
                            curr_fig19_array[4] = order_time;
                            currProj.setNumTotalDependentTests(19, 4, numTotal);
                        } else if (numMachines == 16) {
                            curr_fig19_array[6] = order_time;
                            currProj.setNumTotalDependentTests(19, 6, numTotal);
                        }
                    } else // enhanced
                    {
                        if (numMachines == 2) {
                            curr_fig19_array[1] = order_time;
                        } else if (numMachines == 4) {
                            curr_fig19_array[3] = order_time;
                        } else if (numMachines == 8) {
                            curr_fig19_array[5] = order_time;
                        } else if (numMachines == 16) {
                            curr_fig19_array[7] = order_time;
                        }
                    }

                } // selection technique, figure 18
                else if (techniqueName.equals("selection")) {

                    String apfd_value_string = parseFile(file, APFD_VALUE);
                    double apfd_value = Double.parseDouble(apfd_value_string);
                    String order_time_string = parseFile(file, ORDER_TIME);
                    double order_time = Double.parseDouble(order_time_string);
                    currProj.useFig18();
                    double[] fig18_values_array = currProj.get_fig18_values();
                    double[] fig18_percents_array = currProj.get_fig18_percents();
                    // original values, index should be i=0, i+=2
                    if (resolveDependences == null) { // orig, unenhanced
                        if (coverageName.equals("statement")) {
                            if (orderName.equals("original")) {
                                // S1
                                fig18_values_array[0] = apfd_value;
                                fig18_percents_array[0] = order_time;
                                currProj.setNumTotalDependentTests(18, 0, numTotal);
                            } else if (orderName.equals("absolute")) {
                                // S2
                                fig18_values_array[2] = apfd_value;
                                fig18_percents_array[2] = order_time;
                                currProj.setNumTotalDependentTests(18, 2, numTotal);
                            } else {
                                // S3
                                fig18_values_array[4] = apfd_value;
                                fig18_percents_array[4] = order_time;
                                currProj.setNumTotalDependentTests(18, 4, numTotal);
                            }

                        } else if (coverageName.equals("function")) {
                            if (orderName.equals("original")) {
                                // S4
                                fig18_values_array[6] = apfd_value;
                                fig18_percents_array[6] = order_time;
                                currProj.setNumTotalDependentTests(18, 6, numTotal);

                            } else if (orderName.equals("absolute")) {
                                // S5
                                fig18_values_array[8] = apfd_value;
                                fig18_percents_array[8] = order_time;
                                currProj.setNumTotalDependentTests(18, 8, numTotal);
                            } else {
                                // S6
                                fig18_values_array[10] = apfd_value;
                                fig18_percents_array[10] = order_time;
                                currProj.setNumTotalDependentTests(18, 10, numTotal);
                            }
                        }

                    } else { // auto, enhanced, index should be i = 1, i+=2
                        if (coverageName.equals("statement")) {
                            if (orderName.equals("original")) {
                                // S1
                                fig18_values_array[1] = apfd_value;
                                fig18_percents_array[1] = order_time;
                            } else if (orderName.equals("absolute")) {
                                // S2
                                fig18_values_array[3] = apfd_value;
                                fig18_percents_array[3] = order_time;
                            } else {
                                // S3
                                fig18_values_array[5] = apfd_value;
                                fig18_percents_array[5] = order_time;
                            }

                        } else if (coverageName.equals("function")) {
                            if (orderName.equals("original")) {
                                // S4
                                fig18_values_array[7] = apfd_value;
                                fig18_percents_array[7] = order_time;

                            } else if (orderName.equals("absolute")) {
                                // S5
                                fig18_values_array[9] = apfd_value;
                                fig18_percents_array[9] = order_time;
                            } else {
                                // S6
                                fig18_values_array[11] = apfd_value;
                                fig18_percents_array[11] = order_time;
                            }
                        }
                    }

                } // prioritization techinque, figure 17
                else if (techniqueName.equals("prioritization")) {
                    // if orderName is original, run time for entire test suite
                    if (orderName.equals("original")) {
                        String order_time_string = parseFile(file, ORDER_TIME);
                        double order_time = Double.parseDouble(order_time_string);
                        currProj.setOrigTimeValue(order_time);
                        String apfd_value_string = parseFile(file, APFD_VALUE);
                        double apfd_value = Double.parseDouble(apfd_value_string);
                        currProj.setOrigAPFDValue(apfd_value);
                        continue;
                    }
                    String apfd_value_string = parseFile(file, APFD_VALUE);
                    double apfd_value = Double.parseDouble(apfd_value_string);
                    currProj.useFig17();
                    double[] fig17_array = currProj.get_fig17_values();
                    // original values, index should be i=0, i+=2
                    if (resolveDependences == null) { // orig, unenhanced
                        if (coverageName.equals("statement")) {
                            if (orderName.equals("absolute")) {
                                // T3
                                fig17_array[0] = apfd_value;
                                currProj.setNumTotalDependentTests(17, 0, numTotal);
                            } else { // relative
                                // T4
                                fig17_array[2] = apfd_value;
                                currProj.setNumTotalDependentTests(17, 2, numTotal);
                            }

                        } else if (coverageName.equals("function")) {
                            if (orderName.equals("absolute")) {
                                // T5
                                fig17_array[4] = apfd_value;
                                currProj.setNumTotalDependentTests(17, 4, numTotal);

                            } else { // relative
                                // T7
                                fig17_array[6] = apfd_value;
                                currProj.setNumTotalDependentTests(17, 6, numTotal);
                            }
                        }

                    } else { // auto, enhanced, index should be i = 1, i+=2
                        if (coverageName.equals("statement")) {
                            if (orderName.equals("absolute")) {
                                // T3
                                fig17_array[1] = apfd_value;
                            } else {
                                // T4
                                fig17_array[3] = apfd_value;
                            }

                        } else if (coverageName.equals("function")) {
                            if (orderName.equals("absolute")) {
                                // T5
                                fig17_array[5] = apfd_value;

                            } else {
                                // T7
                                fig17_array[7] = apfd_value;
                            }
                        }

                    }

                } else {// garbage value...return error
                    // TODO throw an exception and exit
                }

            }
        }

        // generate LaTeX for the human-written and automatic test suites
        String origLatexString = generateLatexString(proj_orig_arrayList, proj_auto_arrayList, "orig");
        String autoLatexString = generateLatexString(proj_auto_arrayList, proj_orig_arrayList, "auto");

        String origOutputFilename = outputDirectoryName + System.getProperty("file.separator");
        String autoOutputFilename = outputDirectoryName + System.getProperty("file.separator");

        // fig 19
        if ((!proj_orig_arrayList.isEmpty() && ((ProjectEnhancedResults) proj_orig_arrayList.get(0)).isFig19())
                || (!proj_auto_arrayList.isEmpty()
                        && ((ProjectEnhancedResults) proj_auto_arrayList.get(0)).isFig19())) {
            origOutputFilename += "enhanced-para-orig-results.tex";
            autoOutputFilename += "enhanced-para-auto-results.tex";
        } else if ((!proj_orig_arrayList.isEmpty() && ((ProjectEnhancedResults) proj_orig_arrayList.get(0)).isFig18())
                || (!proj_auto_arrayList.isEmpty()
                        && ((ProjectEnhancedResults) proj_auto_arrayList.get(0)).isFig18())) {
            origOutputFilename += "enhanced-sele-orig-results.tex";
            autoOutputFilename += "enhanced-sele-auto-results.tex";
        } else { // fig 17
            origOutputFilename += "enhanced-prior-orig-results.tex";
            autoOutputFilename += "enhanced-prior-auto-results.tex";

        }

        writeToLatexFile(origLatexString, origOutputFilename);
        writeToLatexFile(autoLatexString, autoOutputFilename);

    }
}
