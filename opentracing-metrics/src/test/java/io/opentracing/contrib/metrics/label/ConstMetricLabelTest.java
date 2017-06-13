package io.opentracing.contrib.metrics.label;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.label.ConstMetricLabel;

public class ConstMetricLabelTest {

    @Test
    public void testConstLabel() {
        MetricLabel label = new ConstMetricLabel("TestLabel", "TestValue");
        assertEquals("TestLabel", label.name());
        assertEquals("TestValue", label.value(null, null, Collections.<String,Object>emptyMap()));
     }

}
