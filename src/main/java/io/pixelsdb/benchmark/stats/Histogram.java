package io.pixelsdb.benchmark.stats;
/**
 * @time 2023-03-04
 * @version 1.0.0
 * @file Histogram.java
 * @description calc histogram during workload running
 **/

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;


public class Histogram
{
    public SynchronizedDescriptiveStatistics[] apstat = new SynchronizedDescriptiveStatistics[13];
    public SynchronizedDescriptiveStatistics[] xpstat_iq = new SynchronizedDescriptiveStatistics[6];
    public SynchronizedDescriptiveStatistics[] xpstat_at = new SynchronizedDescriptiveStatistics[6];
    public SynchronizedDescriptiveStatistics[] tpstat = new SynchronizedDescriptiveStatistics[19]; // the last item is freshness update
    public SynchronizedDescriptiveStatistics freshness;

    public Histogram()
    {
        for (int i = 0; i < 13; i++)
        {
            apstat[i] = new SynchronizedDescriptiveStatistics();
        }
        for (int i = 0; i < 19; i++)
        {
            tpstat[i] = new SynchronizedDescriptiveStatistics();
        }
        for (int i = 0; i < 6; i++)
        {
            xpstat_iq[i] = new SynchronizedDescriptiveStatistics();
        }
        for (int i = 0; i < 6; i++)
        {
            xpstat_at[i] = new SynchronizedDescriptiveStatistics();
        }
        this.freshness = new SynchronizedDescriptiveStatistics();
    }

    public SynchronizedDescriptiveStatistics getAPItem(int index)
    {
        return apstat[index];
    }

    public SynchronizedDescriptiveStatistics getTPItem(int index)
    {
        return tpstat[index];
    }

    public SynchronizedDescriptiveStatistics getXPATItem(int index)
    {
        return xpstat_at[index];
    }

    public SynchronizedDescriptiveStatistics getXPIQItem(int index)
    {
        return xpstat_iq[index];
    }

    public SynchronizedDescriptiveStatistics getFreshness()
    {
        return freshness;
    }
}
