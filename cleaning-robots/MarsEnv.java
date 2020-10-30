import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

public class MarsEnv extends Environment {

    public static final int GSize = 7; // grid size
    public static final int ENEMY = 16; // garbage code in grid model
    public static final int GARB = 32; // garbage code in grid model

    public static final Term    ns = Literal.parseLiteral("next(slot)");
    
    public static final Term    ke = Literal.parseLiteral("kill(enemy)");
    
    public static final Literal e1 = Literal.parseLiteral("enemyhere(enemy)");
    
 

    static Logger logger = Logger.getLogger(MarsEnv.class.getName());

    private MarsModel model;
    private MarsView  view;

    @Override
    public void init(String[] args) {
        model = new MarsModel();
        view  = new MarsView(model);
        model.setView(view);
        updatePercepts();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        try {
            if (action.equals(ns)) {
                model.nextSlot();
            } else if (action.getFunctor().equals("move_towards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                
            } else if (action.equals(ke)) {
            	//sleep 3 seconds to finish killing
            	 try {
                     Thread.sleep(3000);
                     model.killEnemy();
                 } catch (Exception e) {
                     logger.info("Failed to execute action deliver!"+e);
                 }
               
            /*} else if (action.equals(dg)) {
                model.dropGarb();
            } else if (action.equals(bg)) {
                model.burnGarb();*/
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }

    /** creates the agents perception based on the MarsModel */
    void updatePercepts() {
        clearPercepts();

        Location r1Loc = model.getAgPos(0);
     

        Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
       

        addPercept(pos1);
    

        
        if (model.hasObject(ENEMY, r1Loc)) {
            addPercept(e1);
        }
     
    }
    
    

    class MarsModel extends GridWorldModel {
    	
  
       //generate random int between min and max all inclusive
  private int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

        Random random = new Random(System.currentTimeMillis());

        private MarsModel() {
            super(GSize, GSize, 2);

            // initial location of agents
            try {
                setAgPos(0, 0, 0);

               
            } catch (Exception e) {
                e.printStackTrace();
            }

        
            
            add(ENEMY,3,3);
            add(ENEMY, GSize-1, GSize-1);
            add(ENEMY,  0, GSize-2);
        }

        void nextSlot() throws Exception {
            Location r1 = getAgPos(0);
            //
           int dir=getRandomNumberInRange(0,3);
            
            System.out.println("direction ="+dir);
            
            switch(dir) {
            //move up
            case 0:
            	if(r1.y>0)
            		r1.y--;
            	
            	break;
            //move down	
            case 1:
            	if(r1.y<getHeight())
            		r1.y++;
            	
            	break;
            	
           //move left		
            case 2:
            	if(r1.x>0)
            		r1.x--;
            	
            	
            	break;
           //move right	
            case 3:
            	if(r1.x<getWidth())
            		{r1.x++;
            		System.out.println("direction ="+dir);}
            	
            	break;
            	
            	
            	
			default:
				
				break;
            }
           /* r1.x++;
            if (r1.x == getWidth()) {
                r1.x = 0;
                r1.y++;
            }
            // finished searching the whole grid
            if (r1.y == getHeight()) {
                r1.y=0;
            }*/
            setAgPos(0, r1);
         
        }

        /*void moveTowards(int x, int y) throws Exception {
            Location r1 = getAgPos(0);
            if (r1.x < x)
                r1.x++;
            else if (r1.x > x)
                r1.x--;
            if (r1.y < y)
                r1.y++;
            else if (r1.y > y)
                r1.y--;
            setAgPos(0, r1);
           
        }*/


        
        
        void killEnemy() {
        	if (model.hasObject(ENEMY, getAgPos(0))) {
                remove(ENEMY, getAgPos(0));
            }
        	
        	
        }
    }

    class MarsView extends GridWorldView {

        public MarsView(MarsModel model) {
            super(model, "Mars World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }

        /** draw application objects */
       @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
            case MarsEnv.ENEMY:
                drawEnemy(g, x, y);
                break;
            }
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "Enemy";
            c = Color.red;
            if (id == 0) {
            	label="Player";
                c = Color.green;
                
            }
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
            }
            super.drawString(g, x, y, defaultFont, label);
            repaint();
        }

        public void drawEnemy(Graphics g, int x, int y) {
            super.drawAgent(g, x, y,Color.red,3);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "enemy");
        }

    }
}
