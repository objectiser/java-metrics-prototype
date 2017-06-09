package io.opentracing.contrib.metrics.label;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.label.OperationMetricLabel;

public class OperationMetricLabelTest {

    @Test
    public void testOperationLabel() {
        MetricLabel label = new OperationMetricLabel();
        assertEquals("TestOperation", label.value(null, "TestOperation", Collections.<String,Object>emptyMap()));
     }

}
