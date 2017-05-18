package com.bsu.klimansky.graphics;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anton Klimansky on 10.05.2017.
 */
public class TestAnalysis extends AbstractAnalysis {

    @Override
    public void init() throws Exception {
        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
    }

    public void draw(Polygon p) {
        List<Polygon> lp;
        lp = new ArrayList<>();
        lp.add(p);
        AbstractDrawable dr = new Shape(lp);
        draw(dr);
    }

    public void draw(AbstractDrawable dr) {
        List<AbstractDrawable> ldr = new ArrayList<>();
        ldr.add(dr);
        chart.getScene().getGraph().add(ldr);
        chart.updateProjectionsAndRender();
    }


}
