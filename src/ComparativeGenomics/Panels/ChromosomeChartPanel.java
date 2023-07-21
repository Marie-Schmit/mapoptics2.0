package ComparativeGenomics.Panels;

import ComparativeGenomics.FileHandling.DataHandling.Chromosome;
import ComparativeGenomics.FileHandling.DataHandling.Genome;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 *
 * @author franpeters 
 * Plot the number of each different type of SV event per
 * chromosome within the reference genome using XChart. The chart style can be
 * changed by the user.
 */
public class ChromosomeChartPanel extends JPanel {

    boolean drawChart = false;
    HashMap<String, Double> siteDensities;
    ChartTheme style = ChartTheme.GGPlot2;
    Genome genome;
    // Create Chart
    CategoryChart chart = new CategoryChartBuilder()
            .width(this.getWidth()).height(this.getHeight())
            .title("Structural Variants Detected By Chromosome").xAxisTitle("Chromosome").yAxisTitle("SV Count")
            .theme(this.style).build();

    public ChromosomeChartPanel() {

    }

    public void plotGenome(Genome genome) {
        drawChart = true;
        this.genome = genome;
        ArrayList<Number> numIndels = new ArrayList();
        ArrayList<Number> numTrans = new ArrayList();
        ArrayList<Number> numDups = new ArrayList();
        ArrayList<Number> numInvs = new ArrayList();
        ArrayList<String> chrNames = new ArrayList();
        for (Chromosome chr : genome.getChromosomes().values()) {
            numIndels.add(chr.getNumIndels());
            numTrans.add(chr.getNumTranslocations());
            numDups.add(chr.getNumDuplications());
            numInvs.add(chr.getNumInversions());
            chrNames.add(chr.getName());
        }
        try {
            chart.addSeries("Indels", chrNames, numIndels);
        } catch (java.lang.IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "A problem occured while plotting indels. The plot will not be complete. \n"
                    + ex,
                    "Indels display problem!", JOptionPane.ERROR_MESSAGE);
        }
        try {
            chart.addSeries("Translocations", chrNames, numTrans);
        } catch (java.lang.IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "A problem occured while plotting translocations. The plot will not be complete",
                    "Translocations display problem!", JOptionPane.ERROR_MESSAGE);
        }
        try {
            chart.addSeries("Duplications", chrNames, numDups);
        } catch (java.lang.IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "A problem occured while plotting duplications. The plot will not be complete",
                    "Duplications display problem!", JOptionPane.ERROR_MESSAGE);
        }
        try {
            chart.addSeries("Inversions", chrNames, numInvs);
        } catch (java.lang.IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "A problem occured while plotting inversions. The plot will not be complete",
                    "Inversions display problem!", JOptionPane.ERROR_MESSAGE);
        }
        chart.getStyler().setXAxisLabelRotation(45);
        chart.getStyler().setChartTitleVisible(true);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (drawChart) {
            Graphics2D g2d = (Graphics2D) g;
            chart.paint(g2d, this.getWidth(), this.getHeight());
        }
    }

    public void setStyle(ChartTheme style) {
        this.style = style;
        repaint();
    }
}
