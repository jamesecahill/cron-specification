package com.bleept.cron;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.bleept.cron.CronSpecification;

public class CronSpecificationTest {
    private static final Long FRIDAY_JUNE_12_2015_15_19_GMT_LONG = 1434122346000L;
    private static final Long SUNDAY_JUNE_14_2015_15_19_GMT_LONG = 1434295146000L;
    private static final Date FRIDAY_JUNE_12_2015_15_19_GMT = new Date(1434122346000L);
    private static final Date SUNDAY_JUNE_14_2015_15_19_GMT = new Date(1434295146000L);

    @Test
    public void testCronSpecAll() {
        CronSpecification spec = new CronSpecification();
        Assert.assertTrue(spec.isTargeted(new Date()));
        spec = new CronSpecification("* * * * *");
        Assert.assertTrue(spec.isTargeted(new Date()));
    }

    @Test
    public void testLongDayRange() {
        CronSpecification spec = new CronSpecification("* * * * Mon-FRI");
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
    }

    @Test
    public void testMixedDayRange() {
        CronSpecification spec = new CronSpecification("* * * * Mon-6");
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
        Assert.assertFalse(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
    }

    @Test
    public void testMixedCronStmt() {
        CronSpecification spec = new CronSpecification("* 10-15 8,9,10-20 jun *");
        Assert.assertTrue(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
    }

    @Test
    public void testMinuteRange() {
        CronSpecification spec = new CronSpecification("10-20,8 * * * *");
        Assert.assertTrue(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
    }

    @Test
    public void testMixedRangeList() {
        CronSpecification spec = new CronSpecification("* * * 1-3,may,jun-AUG 1,2,thu-sat");
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
        Assert.assertFalse(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
    }

    @Test
    public void testMixedRangeListLongs() {
        CronSpecification spec = new CronSpecification("* * * 1-3,may,jun-AUG 1,2,thu-sat");
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT_LONG));
        Assert.assertFalse(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT_LONG));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpperLowerIllegalArg(){
        new CronSpecification("2-1 * * * *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpperLowerIllegalArgLongWay(){
        new CronSpecification("* * * * fri-mon");
    }

    @Test
    public void testChangingSpec() {
        CronSpecification spec = new CronSpecification("* 10-15 8,9,10-20 jun *");
        Assert.assertTrue(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
        spec.setSpecification("* 10-15 8,9,10-20 jun fri");
        Assert.assertFalse(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
        spec.setDaysOfWeek("*");
        Assert.assertTrue(spec.isTargeted(SUNDAY_JUNE_14_2015_15_19_GMT));
        Assert.assertTrue(spec.isTargeted(FRIDAY_JUNE_12_2015_15_19_GMT));
    }
}
