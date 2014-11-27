/*
 * MultiDstruktApp.java
 */
package multidstrukt;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import structures.IDC;

/**
 * The main class of the application.
 * @author Hagedorn, Khadikov
 */
public class MultiDstruktApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        view = new MultiDstruktView(this);
        show(view);

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MultiDstruktApp
     */
    public static MultiDstruktApp getApplication() {
        return Application.getInstance(MultiDstruktApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(MultiDstruktApp.class, args);
    }

    /**
     * Set Methode fuer IDC
     * @param cube
     */
    public void setIDC(IDC cube) {
        if (this.idc != cube) {
            this.idc = cube;
            hChanged = true;
        }
    }

    /**
     * Get Methode fuer IDC
     * @return aktueller Cube
     */
    public IDC getIDC() {
        return this.idc;
    }

    /**
     * Methode die das Hauptfenster liefert
     * @return Hauptfenster
     */
    public MultiDstruktView getView() {
        show(view);
        return this.view;
    }
    //Global benoetigte Variablen     
    private IDC idc;
    /**
     * Speichert fuer jede Dimension ausgewaehlte Strategie
     */
    protected int[] stratIndex = null;
    /**
     * Speicher fuer jede Dimenison die feste Bloeckgroesse
     */
    protected int[] blockSizes = null;
    /**
     * Speichert bei Dimensionen mit variabler Blockgroesse ein Array mit den Groessen der einzelnen Bloecke
     */
    protected Object[] variableBlocks;
    /**
     * Speicherort des aktuell geladenen IDCs
     */
    protected String fileLocation = null;
    /**
     * Gibt an, ob ein Cube geladen und schon vorberechnet wurde.
     */
    protected boolean cubeReady = false;
    private MultiDstruktView view;
    /**
     * Enthaelt fuer jede Dimension ein Array von allen Hierarchien
     */
    protected Object[] hierarchies;
    /**
     * Gibt an, ob hierarchies-Array noch aktuell ist.
     */
    protected boolean hChanged = false;
}
