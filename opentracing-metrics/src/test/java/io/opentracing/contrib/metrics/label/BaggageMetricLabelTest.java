package io.opentracing.contrib.metrics.label;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import static org.mockito.Mockito.*;

import io.opentracing.Span;
import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.label.BaggageMetricLabel;

public class BaggageMetricLabelTest {

    private static final String TEST_LABEL_DEFAULT = "testLabelDefault";
    private static final String TEST_LABEL = "testLabel";

    @Test
    public void testLabelDefault() {
        MetricLabel label = new BaggageMetricLabel(TEST_LABEL, TEST_LABEL_DEFAULT);
        Span span = mock(Span.class);
        when(span.getBaggageItem(anyString())).thenReturn(null);

        assertEquals(TEST_LABEL, label.name());
        assertEquals(TEST_LABEL_DEFAULT, label.value(span, null, Collections.<String,Object>emptyMap()));
        verify(span, times(1)).getBaggageItem(TEST_LABEL);
    }

    @Test
    public void testLabelFromBaggage() {
        MetricLabel label = new BaggageMetricLabel(TEST_LABEL, TEST_LABEL_DEFAULT);
        Span span = mock(Span.class);
        when(span.getBaggageItem(anyString())).thenReturn("BaggageValue");
        
        assertEquals("BaggageValue", label.value(span, null, Collections.<String,Object>emptyMap()));
        verify(span, times(1)).getBaggageItem(TEST_LABEL);
    }

}
