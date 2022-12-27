import jason.asSyntax.Term;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LifeModel extends GridWorldModel {
    
    public List<String> health = new ArrayList<String>();
    public List<String> mask = new ArrayList<String>();
    public List<String> vaccine = new ArrayList<String>();
    public HashMap<String, Double> protection = new HashMap<String, Double>();

    Random random = new Random();

    public LifeModel(int size, int n, int infected, int protegidos, int vacinados) {
        super(size, size, n);
        protection.put("nenhuma", 0.00);
        
        protection.put("pff2", 0.98);
        protection.put("cirurgica", 0.89);
        protection.put("algodao", 0.40);
        
        protection.put("astrazeneca", 0.7042);
        protection.put("sinovac", 0.5038);
        protection.put("pfizer", 0.95);
        protection.put("janssen", 0.94);        
        
        try {
        	for (int i=0; i<n; i++) {
				setAgPos(i, getFreePos());
				mask.add("nenhuma");
				vaccine.add("nenhuma");
				if (i < infected) {
					health.add("I");
				}
				else {
					health.add("S");
				}
			}
        	int id = 0;
        	for (int i=0; i<protegidos; i++) {
        		do {
        			id = random.nextInt(n);
        		} while (!mask.get(id).equals("nenhuma"));
        		if (i <= Math.floor(0.9*protegidos)) {
        			mask.set(id, "algodao");
        		} else if (i > Math.floor(0.9*protegidos) && i <= Math.floor(0.98*protegidos)) {
        			mask.set(id, "cirurgica");
        		} else {
        			mask.set(id, "pff2");
        		}
        	}
        	for (int i=0; i<vacinados; i++) {
        		do {
        			id = random.nextInt(n);
        		} while (!vaccine.get(id).equals("nenhuma"));
        		if (i <= Math.floor(0.1237*vacinados)) {
        			vaccine.set(id, "janssen");
        		} else if (i > Math.floor(0.1237*vacinados) && i <= Math.floor(0.3367*vacinados)) {
        			vaccine.set(id, "sinovac");
        		} else if (i > Math.floor(0.3367*vacinados) && i <= Math.floor(0.6017*vacinados)) {
        			vaccine.set(id, "astrazeneca");
        		} else {
        			vaccine.set(id, "pfizer");
        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void inf(int ag) {
        health.set(ag, "I");
    }
    
    void susc(int ag) {
        health.set(ag, "S");
    }
    
    void rmvd(int ag) {
    	health.set(ag, "R");
    }
    
    boolean isSusc(int x, int y) {
    	int ag = getAgAtPos(x, y);
    	return health.get(ag).equals("S");
    }
    
    boolean isInf(int x, int y) {
    	int ag = getAgAtPos(x, y);
    	return health.get(ag).equals("I");
    }
    
    boolean isRmvd(int x, int y) {
    	int ag = getAgAtPos(x, y);
    	return health.get(ag).equals("R");
    }
    
    void mover(int ag) {    	
    	int d = 0;
		int x = getAgPos(ag).x;
		int y = getAgPos(ag).y;
		
		
		
		do {
			d = random.nextInt(5);
			if (d == 1) {
				x = (x + 1) % getWidth();
			} else if (d == 2) {
				x = (x - 1) % getWidth();
			} else if (d == 3) {
				y = (y + 1) % getHeight();
			} else if (d == 4) {
				y = (y - 1) % getHeight();
			}
			if (x < 0) x = x + getWidth();
			if (y < 0) y = y + getHeight();

		} while (!isFree(x, y) && d != 0);
		//Location newP = getFreePos();
		setAgPos(ag, x, y);
    }
    
    void transmitir(int ag) {
		Location r = getAgPos(ag);		
		for (int i = 0; i < getNbOfAgs(); i++) {
			if (r.distance(getAgPos(i)) <= 3) {
				if (i != ag && !isRmvd(getAgPos(i).x, getAgPos(i).y)){
					double maskTX = 1 - (protection.get(mask.get(ag)));
					double maskRX = 1 - (protection.get(mask.get(i)));
					double vacinaTX = 1 - (protection.get(vaccine.get(ag)));
					double vacinaRX = 1 - (protection.get(vaccine.get(i)));
					double protectRate = maskTX*maskRX*vacinaTX*vacinaRX;
					//System.out.println(protectRate);
					
					if (100*random.nextDouble() <= protectRate*63.2) {
						inf(i);
					}
				}
			}
		}
    }
    
    void remover(int ag) {
    	rmvd(ag);
    	/*if (random.nextInt(100) <= 70) {
    		rmvd(ag);
    	}*/
    }
    
    void colocar_mascara(int ag, List<Term> actArgs) {
    	mask.set(ag, actArgs.get(0).toString());
    }
    
    void vacinar(int ag, List<Term> actArgs) {
    	vaccine.set(ag, actArgs.get(0).toString());
    }
}
