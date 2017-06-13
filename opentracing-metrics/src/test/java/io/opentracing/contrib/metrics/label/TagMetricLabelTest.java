package io.opentracing.contrib.metrics.label;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.opentracing.contrib.metrics.MetricLabel;
import io.opentracing.contrib.metrics.label.TagMetricLabel;

public class TagMetricLabelTest {

    private static final String TEST_LABEL_DEFAULT = "testLabelDefault";
    private static final String TEST_LABEL = "testLabel";

    @Test
    public void testLabelDefault() {
        MetricLabel label = new TagMetricLabel(TEST_LABEL, TEST_LABEL_DEFAULT);
        assertEquals(TEST_LABEL, label.name());
        assertEquals(TEST_LABEL_DEFAULT, label.value(null, null, Collections.<String,Object>emptyMap()));
    }

    @Test
    public void testLabelFromTagWithValue() {
        MetricLabel label = new TagMetricLabel(TEST_LABEL, TEST_LABEL_DEFAULT);
        Map<String,Object> tags = new HashMap<String,Object>();
        tags.put(TEST_LABEL, "TagValue");
        assertEquals("TagValue", label.value(null, null, tags));
    }

    @Test
    public void testLabelFromTagWithNull() {
        MetricLabel label = new TagMetricLabel(TEST_LABEL, TEST_LABEL_DEFAULT);
        Map<String,Object> tags = new HashMap<String,Object>();
        tags.put(TEST_LABEL, null);
        assertEquals(TEST_LABEL_DEFAULT, label.value(null, null, tags));
    }

}
