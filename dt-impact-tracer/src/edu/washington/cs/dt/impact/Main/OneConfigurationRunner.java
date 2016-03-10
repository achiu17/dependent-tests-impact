/**
 * Copyright 2015 University of Washington. All Rights Reserved.
 * @author Wing Lam
 *
 *         Main class that relies on program arguments to generate a regression testing
 *         execution order. The following options are supported:
 *         -technique - prioritization, selection, parallelization
 *         -coverage - statement, function
 *         -order - absolute, relative, random, original, time (if technique is parallelization)
 *         -resolveDependences - when specified the output test order will not be affected
 *         by dependent tests
 *         -numMachines - integer value (only valid when technique is parallelization
 *         -selectionOldVers - path to directory to older version of program's selectionOutput
 *         (only valid when technique is selection)
 *         -selectionNewVers - path to directory to newer version of program's selectionOutput
 *         (only valid when technique is selection)
 *         -origOrder - path to file containing the original order the tests are executed in
 *         -testInputDir - path to directory to sootTestOutput
 *         -dependentTestFile - path to file containing existing known dependent tests
 *         -filesToDelete - path to file containing list of files to delete to clean the environment
 *         each time the tests are executed
 *         -outputFile - path to file to output the regression test order, dependent test list and
 *         execution time, if unspecified the output will be sent to System.out
 *         -help - display this message
 */

package edu.washington.cs.dt.impact.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.washington.cs.dt.RESULT;
import edu.washington.cs.dt.TestExecResults;
import edu.washington.cs.dt.impact.data.TestFunctionStatement;
import edu.washington.cs.dt.impact.data.WrapperTestList;
import edu.washington.cs.dt.impact.technique.Parallelization;
import edu.washington.cs.dt.impact.technique.Prioritization;
import edu.washington.cs.dt.impact.technique.Selection;
import edu.washington.cs.dt.impact.technique.Test;
import edu.washington.cs.dt.impact.tools.CrossReferencer;
import edu.washington.cs.dt.impact.tools.DependentTestFinder;
import edu.washington.cs.dt.impact.tools.FileTools;
import edu.washington.cs.dt.impact.util.Constants;
import edu.washington.cs.dt.impact.util.Constants.COVERAGE;
import edu.washington.cs.dt.impact.util.Constants.DT_SETTING;
import edu.washington.cs.dt.impact.util.Constants.MACHINES;
import edu.washington.cs.dt.impact.util.Constants.ORDER;
import edu.washington.cs.dt.impact.util.Constants.PROJECT;
import edu.washington.cs.dt.impact.util.Constants.TD_SETTING;
import edu.washington.cs.dt.impact.util.Constants.TECHNIQUE;
import edu.washington.cs.dt.impact.util.Constants.TEST_TYPE;
import edu.washington.cs.dt.main.ImpactMain;

public class OneConfigurationRunner {

    public static void main(String[] args) {
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));

        if (argsList.contains("-help")) {
            System.out.println("Main class that relies on program arguments to generate a regression testing "
                    + "execution order. The following options are supported:\n"
                    + "-technique - prioritization, selection, parallelization\n" + "-coverage - statement, function\n"
                    + "-order - absolute, relative, random, original, time (if technique is parallelization)\n"
                    + "-resolveDependences - when specified the output will not be affected " + "by dependent tests\n"
                    + "-numMachines - integer value (only valid when technique is parallelization\n"
                    + "-selectionOldVers - path to directory to older version of program's selectionOutput "
                    + "(only valid when technique is selection)\n"
                    + "-selectionNewVers - path to directory to newer version of program's selectionOutput "
                    + "(only valid when technique is selection)\n"
                    + "-origOrder - path to file containing the original order the tests are executed in\n"
                    + "-testInputDir - path to directory to sootTestOutput\n"
                    + "-dependentTestFile - path to file containing existing known dependent tests\n"
                    + "-filesToDelete - path to file containing list of files to delete to clean the environment "
                    + "each time the tests are executed\n"
                    + "-outputFile - path to file to output the regression test order, dependent test list and "
                    + "execution time, if unspecified the output will be sent to System.out\n"
                    + "-help - display this message\n");
            System.exit(0);
        }

        // get the technique
        TECHNIQUE techniqueName = null;
        int techniqueIndex = argsList.indexOf("-technique");
        if (techniqueIndex != -1) {
            // get index of technique name
            int techniqueNameIndex = techniqueIndex + 1;
            if (techniqueNameIndex >= argsList.size()) {
                System.err.println("Technique argument is specified but technique name is not."
                        + " Please use the format: -technique aTechniqueName");
                System.exit(0);
            }

            String techniqueStr = argsList.get(techniqueNameIndex).toLowerCase().trim();
            if (techniqueStr.equals("prioritization")) {
                techniqueName = TECHNIQUE.PRIORITIZATION;
            } else if (techniqueStr.equals("selection")) {
                techniqueName = TECHNIQUE.SELECTION;
            } else if (techniqueStr.equals("parallelization")) {
                techniqueName = TECHNIQUE.PARALLELIZATION;
            } else {
                System.err.println("Technique name is invalid. Try \"prioritization\","
                        + " \"selection\" or \"parallelization\".");
                System.exit(0);
            }
        } else {
            System.err.println(
                    "No technique argument is specified." + " Please use the format: -technique aTechniqueName");
            System.exit(0);
        }

        // get the coverage
        COVERAGE coverage = null;
        int coverageIndex = argsList.indexOf("-coverage");
        if (coverageIndex != -1) {
            int coverageNameIndex = coverageIndex + 1;
            if (coverageNameIndex >= argsList.size()) {
                System.err.println("Coverage argument is specified but valid coverage was not."
                        + " Please use the format: -coverage aCoverageName");
                System.exit(0);
            }
            String coverageStr = argsList.get(coverageNameIndex).trim().toLowerCase();
            if (coverageStr.equals("statement")) {
                coverage = COVERAGE.STATEMENT;
            } else if (coverageStr.equals("function")) {
                coverage = COVERAGE.FUNCTION;
            } else {
                System.err.println("Coverage is invalid. Try \"statement\", \"branch\" or \"function\".");
                System.exit(0);
            }
        } else if (techniqueName != TECHNIQUE.PARALLELIZATION) {
            System.err
                    .println("No coverage argument is specified." + " Please use the format: -coverage aCoverageName");
            System.exit(0);
        }

        // get the order
        File timeOrder = null;
        ORDER order = null;
        int orderIndex = argsList.indexOf("-order");
        if (orderIndex != -1) {
            int orderNameIndex = orderIndex + 1;
            if (orderNameIndex >= argsList.size()) {
                System.err.println("Order argument is specified but valid order was not."
                        + " Please use the format: -order aOrderName");
                System.exit(0);
            }
            String orderStr = argsList.get(orderNameIndex).trim().toLowerCase();
            if (orderStr.equals("absolute")) {
                order = ORDER.ABSOLUTE;
            } else if (orderStr.equals("relative")) {
                order = ORDER.RELATIVE;
            } else if (orderStr.equals("random")) {
                order = ORDER.RANDOM;
            } else if (orderStr.equals("original")) {
                order = ORDER.ORIGINAL;
            } else if (orderStr.equals("time") && techniqueName == TECHNIQUE.PARALLELIZATION) {
                int timeOrderIndex = argsList.indexOf("-timeOrder");
                if (timeOrderIndex != -1) {
                    // get file for the time each test took
                    int timeOrderNameIndex = timeOrderIndex + 1;
                    if (timeOrderNameIndex >= argsList.size()) {
                        System.err.println("Time order argument is specified but a directory path"
                                + " is not. Please use the format: -timeOrder aFilePath");
                        System.exit(0);
                    }
                    timeOrder = new File(argsList.get(timeOrderNameIndex));
                    if (!timeOrder.isFile()) {
                        System.err.println("Time order argument is specified but the file path"
                                + " is invalid. Please check the file path.");
                        System.exit(0);
                    }
                    order = ORDER.TIME;
                } else {
                    System.err.println("Time is specified as the order but no -timeOrder is "
                            + " supplied. Please add -timeOrder aFilePath argument.");
                    System.exit(0);
                }
            } else {
                System.err.println("Order is invalid. Please input a valid order.\n"
                        + "Try \"absolute\", \"relative\", \"random\", or \"original\".\n"
                        + "If your technique is parallelization then \"time\" is also valid.");
                System.exit(0);
            }
        } else {
            System.err.println("No order argument is specified." + " Please use the format: -order aOrderName");
            System.exit(0);
        }

        // get directory for the input of test files, the default is sootTestOutput
        int testInputDirIndex = argsList.indexOf("-testInputDir");
        File testInputDir = new File(Constants.DEFAULT_TEST_DIR);
        if (testInputDirIndex != -1) {
            int testInputDirNameIndex = testInputDirIndex + 1;
            if (testInputDirNameIndex >= argsList.size()) {
                System.err.println("Test input directory argument is specified but a directory name is not."
                        + " Please use the format: -testInputDir aDirName");
                System.exit(0);
            }
            testInputDir = new File(argsList.get(testInputDirNameIndex));
        } else {
            System.err.println("No test input directory argument is specified."
                    + " Please use the format: -testInputDir aDirectoryPath");
            System.exit(0);
        }

        // if specified, the output is saved to the file name instead of printed to console
        String outputFileName = null;
        int outputFile = argsList.indexOf("-outputFile");
        if (outputFile != -1) {
            // get index of output file
            int outputFileNameIndex = outputFile + 1;
            if (outputFileNameIndex >= argsList.size()) {
                System.err.println("Output file argument is specified but a file name is not."
                        + " Please use the format: -outputFile aFileName");
                System.exit(0);
            }
            outputFileName = argsList.get(outputFileNameIndex);
        }

        // get file for the original order in which the tests should be ordered
        File origOrder = null;
        int origOrderIndex = argsList.indexOf("-origOrder");
        if (origOrderIndex != -1) {
            int origOrderNameIndex = origOrderIndex + 1;
            if (origOrderNameIndex >= argsList.size()) {
                System.err.println("Original order argument is specified but a directory"
                        + " path is not. Please use the format: -origOrder aFilePath");
                System.exit(0);
            }
            origOrder = new File(argsList.get(origOrderNameIndex));
            if (!origOrder.isFile()) {
                System.err.println("Original order argument is specified but the file"
                        + " path is invalid. Please check the file path. Path is: " + argsList.get(origOrderNameIndex));
                System.exit(0);
            }
        } else {
            System.err.println(
                    "No original order argument is specified." + " Please use the format: -origOrder aFileName");
            System.exit(0);
        }

        File selectionOutput1 = null;
        File selectionOutput2 = null;
        if (techniqueName == TECHNIQUE.SELECTION) {
            // get directory of old version's selection output
            int oldVersCFGIndex = argsList.indexOf("-oldVersCFG");
            if (oldVersCFGIndex != -1) {
                int oldVersCFGNameIndex = oldVersCFGIndex + 1;
                if (oldVersCFGNameIndex >= argsList.size()) {
                    System.err.println("Old version CFG argument is specified but a directory"
                            + " path is not. Please use the format: -oldVersCFG aDirPath");
                    System.exit(0);
                }
                selectionOutput1 = new File(argsList.get(oldVersCFGNameIndex));
                if (!selectionOutput1.isDirectory()) {
                    System.err.println("Old version CFG argument is specified but the directory"
                            + " path is invalid. Please check the directory path. The input path was: "
                            + selectionOutput1);
                    System.exit(0);
                }
            } else {
                System.err.println(
                        "No old version CFG argument is specified." + " Please use the format: -oldVersCFG aDirPath");
                System.exit(0);
            }

            // get directory of new version's selection output
            int newVersCFGIndex = argsList.indexOf("-newVersCFG");
            if (newVersCFGIndex != -1) {
                int newVersCFGNameIndex = newVersCFGIndex + 1;
                if (newVersCFGNameIndex >= argsList.size()) {
                    System.err.println("New version CFG argument is specified but a directory"
                            + " path is not. Please use the format: -newVersCFG aDirPath");
                    System.exit(0);
                }
                selectionOutput2 = new File(argsList.get(newVersCFGNameIndex));
                if (!selectionOutput2.isDirectory()) {
                    System.err.println("New version CFG argument is specified but the directory"
                            + " path is invalid. Please check the directory path. The input path was: "
                            + selectionOutput2);
                    System.exit(0);
                }
            } else {
                System.err.println(
                        "No new version CFG argument is specified." + " Please use the format: -newVersCFG aDirPath");
                System.exit(0);
            }
        }

        Constants.MACHINES numOfMachines = MACHINES.ONE;
        if (techniqueName == TECHNIQUE.PARALLELIZATION) {
            int numOfMachinesIndex = argsList.indexOf("-numOfMachines");
            if (numOfMachinesIndex != -1) {
                int numOfMachinesIntIndex = numOfMachinesIndex + 1;
                if (numOfMachinesIntIndex >= argsList.size()) {
                    System.err.println("Number of machines argument is specified but a integer"
                            + " is not. Please use the format: -numOfMachines aInteger");
                    System.exit(0);
                }
                int machines = Integer.parseInt(argsList.get(numOfMachinesIntIndex));
                if (machines == 2) {
                    numOfMachines = MACHINES.TWO;
                } else if (machines == 4) {
                    numOfMachines = MACHINES.FOUR;
                } else if (machines == 8) {
                    numOfMachines = MACHINES.EIGHT;
                } else if (machines == 16) {
                    numOfMachines = MACHINES.SIXTEEN;
                } else {
                    System.err.println("Number of machines argument is specified but the integer"
                            + " value provided is invalid. Please input either 2, 4, 8 or 16.");
                    System.exit(0);
                }
            }
        }

        int timesToRun = 1;
        int timesToRunIndex = argsList.indexOf("-timesToRun");
        if (timesToRunIndex != -1) {
            int timesToRunIntIndex = timesToRunIndex + 1;
            if (timesToRunIntIndex >= argsList.size()) {
                System.err.println("Times to run argument is specified but a integer"
                        + " is not. Please use the format: -timesToRun aInteger");
                System.exit(0);
            }
            timesToRun = Integer.parseInt(argsList.get(timesToRunIntIndex));
            if (timesToRun < 1) {
                System.err.println("Times to run argument is specified but the integer"
                        + " value provided is invalid. Please check the integer value.");
                System.exit(0);
            }
        }

        Constants.PROJECT project = PROJECT.CRYSTAL;
        int projectIndex = argsList.indexOf("-project");
        if (projectIndex != -1) {
            int projectStrIndex = projectIndex + 1;
            if (projectStrIndex >= argsList.size()) {
                System.err.println("Project argument is specified but a project name"
                        + " is not. Please use the format: -project aProjectName");
                System.exit(0);
            }
            String projectName = argsList.get(projectStrIndex);
            if (projectName.equals("crystal")) {
                project = PROJECT.CRYSTAL;
            } else if (projectName.equals("jfreechart")) {
                project = PROJECT.JFREECHART;
            } else if (projectName.equals("jodatime")) {
                project = PROJECT.JODATIME;
            } else if (projectName.equals("synoptic")) {
                project = PROJECT.SYNOPTIC;
            } else if (projectName.equals("xml_security")) {
                project = PROJECT.XML_SECURITY;
            } else {
                System.err.println("Project argument is specified but the project name"
                        + " value provided is invalid. Please use either crystal, jfreechart, jodatime, synoptic or xml_security.");
                System.exit(0);
            }
        } else {
            System.err.println("No project argument is specified." + " Please use the format: -project aProjectName");
            System.exit(0);
        }

        Constants.TEST_TYPE testType = TEST_TYPE.ORIG;
        int testTypeIndex = argsList.indexOf("-testType");
        if (testTypeIndex != -1) {
            int testTypeStrIndex = testTypeIndex + 1;
            if (testTypeStrIndex >= argsList.size()) {
                System.err.println("Test type argument is specified but a test type name"
                        + " is not. Please use the format: -testType aTestTypeName");
                System.exit(0);
            }
            String testTypeName = argsList.get(testTypeStrIndex);
            if (testTypeName.equals("orig")) {
                testType = TEST_TYPE.ORIG;
            } else if (testTypeName.equals("auto")) {
                testType = TEST_TYPE.AUTO;
            } else {
                System.err.println("Test type argument is specified but the test type name"
                        + " value provided is invalid. Please use either orig or auto.");
                System.exit(0);
            }
        } else {
            System.err
                    .println("No test type argument is specified." + " Please use the format: -testType aTestTypeName");
            System.exit(0);
        }

        // if specified, the test list generated will consider the dependencies in this file
        List<String> allDTList;
        File dependentTestFile = null;
        int dependentFile = argsList.indexOf("-dependentTestFile");
        if (dependentFile != -1) {
            // get index of output file
            int dependentFileNameIndex = dependentFile + 1;
            if (dependentFileNameIndex >= argsList.size()) {
                System.err.println("Dependent test file argument is specified but a file name is not."
                        + " Please use the format: -dependentTestFile aFileName");
                System.exit(0);
            }
            dependentTestFile = new File(argsList.get(dependentFileNameIndex));
            allDTList = FileTools.parseFileToList(dependentTestFile);
        } else {
            allDTList = null;
        }

        boolean resolveDependences = argsList.contains("-resolveDependences");

        // file containing list of files that should be deleted before the test suite is executed
        // again
        String filesToDeleteStr = null;
        int filesToDeleteIndex = argsList.indexOf("-filesToDelete");
        if (filesToDeleteIndex != -1) {
            int filesToDeleteFileIndex = filesToDeleteIndex + 1;
            if (filesToDeleteFileIndex >= argsList.size()) {
                System.err.println("Files to delete argument is specified but a file name is not."
                        + " Please use the format: -filesToDelete aFileName");
                System.exit(0);
            }
            filesToDeleteStr = argsList.get(filesToDeleteFileIndex);
        }

        boolean getCoverage = argsList.contains("-getCoverage");

        List<String> filesToDelete = FileTools.parseFileToList(new File(filesToDeleteStr));
        List<String> origOrderTestList = FileTools.parseFileToList(origOrder);
        Map<String, RESULT> nameToOrigResults = getCurrentOrderTestListResults(origOrderTestList, filesToDelete);

        // double totalTimeOrigOrder = 1.0;
        // if (techniqueName == TECHNIQUE.PARALLELIZATION) {
        // WrapperTestList origOrderTestListData = new WrapperTestList();
        // setTestListMedianTime(timesToRun, filesToDelete, origOrderTestList, origOrderTestListData);
        // totalTimeOrigOrder = origOrderTestListData.getNewOrderTime();
        // }

        if (outputFileName == null) {
            outputFileName = Constants.getOutputFileName(coverage, techniqueName, order, project, testType,
                    numOfMachines, resolveDependences ? DT_SETTING.FIXED_DT : DT_SETTING.CONTAINS_DT,
                    allDTList == null ? TD_SETTING.OMITTED_TD : TD_SETTING.GIVEN_TD);
        }

        // capture start time
        double start = System.nanoTime();

        // TestListGenerator
        Test testObj = null;
        if (techniqueName == TECHNIQUE.PRIORITIZATION) {
            testObj = new Prioritization(order, outputFileName, testInputDir, coverage, dependentTestFile, false,
                    origOrder);
        } else if (techniqueName == TECHNIQUE.SELECTION) {
            testObj = new Selection(order, outputFileName, testInputDir, coverage, selectionOutput1, selectionOutput2,
                    origOrder, dependentTestFile, getCoverage);
        } else if (techniqueName == TECHNIQUE.PARALLELIZATION) {
            testObj = new Parallelization(order, outputFileName, testInputDir, coverage, dependentTestFile,
                    numOfMachines.getValue(), origOrder, timeOrder);
        } else {
            System.err.println("The regression testing technique selected is invalid. Please restart the"
                    + " program and try again.");
            System.exit(0);
        }
        double TLGTime = System.nanoTime() - start;

        List<WrapperTestList> listTestList = new ArrayList<>();
        for (int i = 0; i < numOfMachines.getValue(); i++) {
            start = System.nanoTime();

            WrapperTestList testList = new WrapperTestList();
            List<String> currentOrderTestList = getCurrentTestList(testObj, i);
            // ImpactMain
            Map<String, RESULT> nameToTestResults = getCurrentOrderTestListResults(currentOrderTestList, filesToDelete);
            // CrossReferencer
            Set<String> changedTests = CrossReferencer.compareResults(nameToOrigResults, nameToTestResults, false);

            Set<String> fixedDT = new HashSet<>();
            if (resolveDependences) {
                int counter = 0;
                while (!changedTests.isEmpty()) {
                    System.out.println("Nullifying DTs iteration number: " + counter);
                    counter += 1;
                    String testName = changedTests.iterator().next();
                    fixedDT.add(testName);
                    // DependentTestFinder
                    DependentTestFinder.runDTF(testName, nameToOrigResults.get(testName), currentOrderTestList,
                            origOrderTestList, filesToDelete, allDTList);
                    allDTList = DependentTestFinder.getAllDTs();
                    // TestListGenerator
                    testObj.resetDTList(allDTList);
                    currentOrderTestList = getCurrentTestList(testObj, i);
                    // ImpactMain
                    nameToTestResults = getCurrentOrderTestListResults(currentOrderTestList, filesToDelete);
                    // Cross Referencer
                    changedTests = CrossReferencer.compareResults(nameToOrigResults, nameToTestResults, false);
                }
            }

            // capture end time
            double runTotal = System.nanoTime() - start;
            testList.setNullifyDTTime(runTotal);
            testList.setNumNotFixedDT(changedTests.size());
            testList.setNumFixedDT(fixedDT.size());
            testList.setTestList(currentOrderTestList);
            Map<Double, List<Double>> totalTimeToCumulTime =
                    setTestListMedianTime(timesToRun, filesToDelete, currentOrderTestList, testList);

            if (getCoverage) {
                // Get coverage each test achieved
                List<String> coverageEachTest = getCurrentCoverage(testObj, i);
                testList.setCoverage(coverageEachTest);
                List<Double> cumulCoverage = getCumulList(coverageEachTest);
                testList.setAPFD(getAPFD(totalTimeToCumulTime.get(testList.getNewOrderTime()), cumulCoverage));
            }
            listTestList.add(testList);
        }

        FileTools.clearEnv(filesToDelete);
        double totalTime = TLGTime;
        double maxTime = Double.MIN_VALUE;
        double testListTime;
        int numTests = 0;
        List<String> outputArr = new ArrayList<>();
        outputArr.add("The following arguments were used to generate this output.\n");
        outputArr.add(argsList + "\n\n");
        for (WrapperTestList testList : listTestList) {
            testListTime = testList.getNewOrderTime();
            totalTime += testListTime;
            numTests += testList.getTestList().size();
            maxTime = Math.max(maxTime, testListTime);
            outputArr.add("Execution time of TLG and its time to find/nullify any DTs for 1 machine"
                    + " (does not include the time to run the tests in the new order): "
                    + nanosecondToSecond(testList.getNullifyDTTime()) + "\n");
            outputArr.add("Number of tests selected out of total in original order: " + testList.getTestList().size()
                    + " / " + origOrderTestList.size() + "\n");
            outputArr.add("Number of DTs not fixed: " + testList.getNumNotFixedDT() + "\n");
            outputArr.add("Number of DTs fixed: " + testList.getNumFixedDT() + "\n");
            if (getCoverage) {
                outputArr.add("APFD value: " + testList.getAPFD() + "\n");
            }
            outputArr.add("Execution time for executing the following testing order: "
                    + nanosecondToSecond(testListTime) + "\n");
            outputArr.add("Test order list:\n");
            outputArr.add(testList.getTestList() + "\n");
            outputArr.add("\nTime each test takes to run in the new order:\n");
            outputArr.add(testList.getTimeEachTest() + "\n");
            if (allDTList != null) {
                outputArr.add("\nDependent test list:\n");
                outputArr.add(allDTList.toString() + "\n");
            }
            if (getCoverage) {
                outputArr.add("\nCoverage test list:\n");
                outputArr.add(testList.getCoverage() + "\n");
            }
            outputArr.add("--------------------------\n");
        }
        outputArr.add("Total time (of all machines and iterations plus initial TestListGenerator): "
                + nanosecondToSecond(totalTime));
        if (techniqueName == TECHNIQUE.PARALLELIZATION) {
            outputArr.add("\nNew order time: " + nanosecondToSecond(maxTime));
            outputArr.add("\nTotal number of tests executed in all machines out of total in original order: " + numTests
                    + " / " + origOrderTestList.size());
        }

        FileWriter output = null;
        BufferedWriter writer = null;
        try {
            output = new FileWriter(outputFileName);
            writer = new BufferedWriter(output);
            for (String line : outputArr) {
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private static String nanosecondToSecond(double nanoseconds) {
        double sec = nanoseconds / 1E9;
        return String.format("%.3f", sec);
    }

    private static Map<Double, List<Double>> setTestListMedianTime(int timesToRun, List<String> filesToDelete,
            List<String> currentOrderTestList, WrapperTestList testList) {
        // Get time each test took
        Map<Double, List<Double>> totalTimeToCumulTime = new TreeMap<>();
        Map<Double, List<String>> totalTimeToTimeEachTest = new TreeMap<>();
        for (int j = 0; j < timesToRun; j++) {
            System.out.println("Getting median in iteration: " + j);
            FileTools.clearEnv(filesToDelete);
            List<String> timeEachTest =
                    ImpactMain.getResults(currentOrderTestList, true).getExecutionRecords().get(0).getValues();
            List<Double> cumulTime = getCumulList(timeEachTest);
            double totalTimeNewOrder = getSum(cumulTime);
            totalTimeToCumulTime.put(totalTimeNewOrder, cumulTime);
            totalTimeToTimeEachTest.put(totalTimeNewOrder, timeEachTest);
        }
        Double[] keys = totalTimeToCumulTime.keySet().toArray(new Double[totalTimeToCumulTime.keySet().size()]);
        double median = keys[keys.length / 2];
        testList.setTimeEachTest(totalTimeToTimeEachTest.get(median).toString());
        testList.setNewOrderTime(median);
        return totalTimeToCumulTime;
    }

    private static double getAPFD(List<Double> cumulTime, List<Double> cumulCoverage) {
        if (cumulTime.size() < 2 || cumulCoverage.size() < 1) {
            throw new IllegalArgumentException("cumulTime or cumulCoverage is too small to get APFD.\ncumulTime is: "
                    + cumulTime + "\ncumulCoverage is: " + cumulCoverage);
        }

        List<Double> testAPFD = new ArrayList<>();
        testAPFD.add((cumulTime.get(1) - cumulTime.get(0)) * cumulCoverage.get(0));
        for (int i = 2; i < cumulTime.size(); i++) {
            testAPFD.add((cumulTime.get(i) - cumulTime.get(i - 1)) * cumulCoverage.get(i - 1));
        }
        return getSum(testAPFD) / (cumulTime.get(cumulTime.size() - 1) * cumulCoverage.get(cumulCoverage.size() - 1));
    }

    private static double getSum(List<Double> list) {
        double sum = 0.0;
        for (Double val : list) {
            sum += val;
        }
        return sum;
    }

    private static List<Double> getCumulList(List<String> list) {
        if (list == null || list.size() < 1) {
            throw new IllegalArgumentException("getCumulList recieved argument: " + list);
        }
        List<Double> cumulList = new ArrayList<>();
        cumulList.add(Double.valueOf(list.get(0)));
        for (int i = 1; i < list.size(); i++) {
            cumulList.add(Double.valueOf(list.get(i)) + cumulList.get(i - 1));
        }
        return cumulList;
    }

    private static Map<String, RESULT> getCurrentOrderTestListResults(List<String> currentOrderTestList,
            List<String> filesToDelete) {
        // ImpactMain
        FileTools.clearEnv(filesToDelete);
        TestExecResults results = ImpactMain.getResults(currentOrderTestList);
        return results.getExecutionRecords().get(0).getNameToResultsMap();
    }

    private static List<String> getCurrentTestList(Test testObj, int numOfMachines) {
        // TestListGenerator
        List<String> currentOrderTestList = new LinkedList<String>();
        for (TestFunctionStatement tfs : testObj.getResults(numOfMachines)) {
            currentOrderTestList.add(tfs.getName());
        }
        return currentOrderTestList;
    }

    private static List<String> getCurrentCoverage(Test testObj, int numOfMachines) {
        List<String> currentOrderTestList = new LinkedList<String>();
        for (TestFunctionStatement tfs : testObj.getCoverage(numOfMachines)) {
            currentOrderTestList.add(tfs.getName());
        }
        return currentOrderTestList;
    }
}