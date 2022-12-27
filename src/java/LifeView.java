import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Graphics;

public class LifeView extends GridWorldView {

    private static final long serialVersionUID = 1L;

    LifeModel hmodel;

    public LifeView(LifeModel model, final LifeEnvironment env) {
        super(model, "Covid-19", 800);
        hmodel = model;
        setVisible(true);
        repaint();
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        c = Color.white;
        if (hmodel.isSusc(x,y)) {
            c = Color.green;
        } else if (hmodel.isInf(x,y)) {
        	c = Color.red;
        } else if (hmodel.isRmvd(x,y)) {
        	c = Color.darkGray;
        }
        g.setColor(c);
        g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
        //update(x, y);
        
    }


}
