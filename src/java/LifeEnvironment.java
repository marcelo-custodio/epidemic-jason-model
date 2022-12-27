import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.grid.Location;
import java.util.logging.Logger;
import java.io.*;

public class LifeEnvironment extends jason.environment.TimeSteppedEnvironment {

    private Logger logger = Logger.getLogger("teii_sir."+LifeEnvironment.class.getName());
    private LifeModel model;
    private File FileLogging = new File("run.csv");
    private FileWriter fw = null;
    private Boolean finalizou = false;
    private Boolean fechado = false;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(new String[] { "5000" } );
        //setSleep(300);
        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);
        model = new LifeModel(
        			Integer.parseInt(args[0]),
        			Integer.parseInt(args[1]),
        			Integer.parseInt(args[2]),
        			Integer.parseInt(args[3]),
        			Integer.parseInt(args[4])
        		);
        model.setView(new LifeView(model, this));
        if (FileLogging.exists()) FileLogging.delete();
        try {
			FileLogging.createNewFile();
			fw = new FileWriter("run.csv");
			for (int i=0; i<Integer.parseInt(args[1]); i++) {
				if (i == 0) fw.write("Ag"+i);
				else fw.write(", Ag"+i);
			}
			fw.write("\n");
			fw.write(model.health.toString().replaceAll("\\[|\\]|\\s", "")+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        updateAgsPercept();
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        String actId = action.getFunctor();
        if (actId.equals("skip"))
            return true;

        int ag = getAgIdBasedOnName(agName);
        if (actId.equals("mover"))
            model.mover(ag);
        else if (actId.equals("transmitir"))
            model.transmitir(ag);
        else if (actId.equals("remover"))
        	model.remover(ag);
        else if (actId.equals("colocar_mascara"))
        	model.colocar_mascara(ag, action.getTerms());
        else if (actId.equals("vacinar"))
        	model.vacinar(ag, action.getTerms());

        return true;
    }

    @Override
    protected void stepStarted(int step) {
        //logger.info("start step "+step);
    }

    private long sum = 0;

    @Override
    protected void stepFinished(int step, long time, boolean timeout) {
        long mean = (step > 0 ? sum / step : 0);
        logger.info("step "+step+" finished in "+time+" ms. mean = "+mean);
        sum += time;
    	if ((!model.health.contains("I")) && !finalizou) {
        	System.out.println("Finalizou");
        	try {
        		if (!fechado) {
        			fw.write(model.health.toString().replaceAll("\\[|\\]|\\s", "")+"\n");
    				fw.close();
    				fechado = true;
        		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	finalizou = true;
        } else {
        	try {
        		if (!fechado) {
        			fw.write(model.health.toString().replaceAll("\\[|\\]|\\s", "")+"\n");
        		} else System.out.println("Finalizou");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    int getAgIdBasedOnName(String agName) {
    	
        return (Integer.parseInt(agName.substring(4))) - 1;
    }

    @Override
    protected void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            updateAgPercept(i);
        }
    }

    void updateAgPercept(int ag) {
        String name = "cell" + (ag + 1);
        updateAgPercept(name, ag);
    }

    void updateAgPercept(String agName, int ag) {
        clearPercepts(agName);
        Location l = model.getAgPos(ag);
        
        if (model.isSusc(l.x, l.y)) {
        	//logger.info(agName + " suscetivel");
        	addPercept(agName, Literal.parseLiteral("suscetivel"));
        }
        if (model.isInf(l.x, l.y)) {
        	//logger.info(agName + " infectado");
        	addPercept(agName, Literal.parseLiteral("infectado"));
        }
        if (model.isRmvd(l.x, l.y)) {
        	//logger.info(agName + " removido");
        	addPercept(agName, Literal.parseLiteral("removido"));
        }
        addPercept(agName, ASSyntax.createLiteral("step", ASSyntax.createNumber(getStep())));
    }
}
