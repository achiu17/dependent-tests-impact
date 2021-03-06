package edu.washington.cs.dt.impact.data;

public class ProjectEnhancedResults extends Project {
    private String name;
    private double orig_time_value;
    private double orig_apfd_value;

    /*
     * fig**[i] corresponds to orig value
     * fig**[i+1] corresponds to new value
     * where i=0, i+=2
     * so when doing calculations, do data = fig**[i] - fig**[i+1]
     */
    // corresponds to T3, T4, T5, and T7 respectively
    private double[] fig17_values;
    // corresponds to S1, S2, S3, S4, S5, and S6 respectively
    private double[] fig18_values;
    /*
     * corresponds to S1, S2, S3, S4, S5 and S6
     * fig18_percents[i] corresponds to nonenhanced
     * fig18_percents[i + 1] corresponds to enhanced
     */
    private double[] fig18_percents;

    /*
     * fig19[i] is the original test suite data value
     * fig19[i+1] is the speedup for the dependence-aware algorithm
     * where i =0, i+=2
     */
    // corresponds to P1(Original Order)
    private double[] fig19_orig;
    // corresponds to P2(Time-Minimized)
    private double[] fig19_time;

    private boolean[] fig17_nonzero;
    private boolean[] fig18_nonzero;
    private boolean[] fig19_nonzero;

    private boolean uses_fig17;
    private boolean uses_fig18;
    private boolean uses_fig19;

    public ProjectEnhancedResults(String projName) {
        name = projName;
        orig_time_value = 0;
        orig_apfd_value = 0;
        fig17_values = new double[4 * 2];
        fig18_values = new double[6 * 2];
        fig18_percents = new double[6 * 2];
        fig19_orig = new double[2 * 4];
        fig19_time = new double[2 * 4];
        fig17_nonzero = new boolean[4 * 2];
        fig18_nonzero = new boolean[6 * 2];
        fig19_nonzero = new boolean[4 * 2];
        uses_fig17 = false;
        uses_fig18 = false;
        uses_fig19 = false;
    }

    public boolean isFig17() {
        return uses_fig17;
    }

    public boolean isFig18() {
        return uses_fig18;
    }

    public boolean isFig19() {
        return uses_fig19;
    }

    public void useFig17() {
        uses_fig17 = true;
    }

    public void useFig18() {
        uses_fig18 = true;
    }

    public void useFig19() {
        uses_fig19 = true;
    }

    @Override
    public String getName() {
        return name;
    }

    public double[] get_fig17_values() {
        return fig17_values;
    }

    public double[] get_fig18_values() {
        return fig18_values;
    }

    public double[] get_fig18_percents() {
        return fig18_percents;
    }

    public double[] get_fig19_orig() {
        return fig19_orig;
    }

    public double[] get_fig19_time() {
        return fig19_time;
    }

    public void setOrigTimeValue(double val) {
        orig_time_value = val;
    }

    public double getOrigTimeValue() {
        return orig_time_value;
    }

    public void setOrigAPFDValue(double val) {
        orig_apfd_value = val;
    }

    public double getOrigAPFDValue() {
        return orig_apfd_value;
    }
    /*
     * @param figNum represents the figure number in the paper (17, 18, or 19)
     *
     * @param index indicates which column in the figure it corresponds to (i.e. Figure 17 column 0 represents T3)
     *
     * @param numTotal is the total number of DTS for that file
     */

    public void setNumTotalDependentTests(int figNum, int index, int numTotal) {
        if (figNum == 17) {
            fig17_nonzero[index] = numTotal != 0;
        } else if (figNum == 18) {
            fig18_nonzero[index] = numTotal != 0;
        } else { // 19
            fig19_nonzero[index] = numTotal != 0;
        }
    }

    public boolean[] get_fig17_nonZeroNumOfDTS() {
        return fig17_nonzero;
    }

    public boolean[] get_fig18_nonZeroNumOfDTS() {
        return fig18_nonzero;
    }

    public boolean[] get_fig19_nonZeroNumOfDTS() {
        return fig19_nonzero;
    }
}

