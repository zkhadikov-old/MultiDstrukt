/*
 * MultiDstruktView.java
 */
package multidstrukt;

import helpers.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import structures.*;

/**
 * The application's main frame.
 * @author Philippe Hagedorn, Zurab Khadikov 
 */
public class MultiDstruktView extends FrameView {

    /**
     * Constructor
     * @param app Objekt von main Klasse
     */
    public MultiDstruktView(SingleFrameApplication app) {
        super(app);
        this.getFrame().setResizable(false);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    /**
     * Zeigt Informationen ueber den Cube an. 
     */
    public void showInformation() {
        if (MultiDstruktApp.getApplication().cubeReady) {
            // Gibt Anzahl von belegten Zellen aus
            this.sizeText.setText(new Integer(MultiDstruktApp.getApplication().getIDC().getSize()).toString());

            int fullSize = 1;

            int[] shape = MultiDstruktApp.getApplication().getIDC().getShape();
            int dimensionality = shape.length;
            for (int i = 0; i < dimensionality; i++) {
                fullSize *= shape[i];
            }
            // TODO: RealSize funktioniert beim RangeUpdate nicht immer richtig (wegen double Genauigkeit), nur bei einzelnen Zellen!
            double density = (MultiDstruktApp.getApplication().getIDC().getSize() / (double) fullSize) * 10000;
            density = (int) density;
            density /= 100.0;
            // Gibt die relative Belegung von Cube aus
            this.densityField.setText(new Double(density).toString() + " %");
            // Gibt die relative Belegung von Cube aus
            this.dimField.setText(new Integer(dimensionality).toString());
            sizeText.setEnabled(true);
            densityField.setEnabled(true);
            dimField.setEnabled(true);
            jLabel1.setEnabled(true);
            jLabel2.setEnabled(true);
            jLabel3.setEnabled(true);
            queryMenu.setEnabled(true);
            updateMenu.setEnabled(true);
            hierarchyMenu.setEnabled(true);
            queryButton.setEnabled(true);
            updateButton.setEnabled(true);
            setHierarhyButton.setEnabled(true);
            hierqueryButton.setEnabled(true);
        }
    }

    /**
     * Setzt eine Action fuer eine Abfrage zu einer einzelnen Zelle. 
     */
    @Action
    public void pointQuery() {
        this.update = false;
        this.showPoint();
        this.jLabel4.setText("Result");
    }

    /**
     * Setzt eine Action fuer ein Update zu einer einzelnen Zelle.
     */
    @Action
    public void pointUpdate() {
        this.update = true;
        this.showPoint();
        this.jLabel4.setText("new Value");
    }

    /**
     * Diese Methode Zeigt die Dialogtabelle, wo man Koordinaten von einer 
     * Zelle eingeben kann.  
     */
    public void showPoint() {
        ResultField.setText("0");
        queryPanel.setVisible(true);
        this.jComboBox2.setVisible(false);
        if (update) {
            queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Update"));
            setButton.setVisible(true);
            setButton.setEnabled(true);
            calculateButton.setVisible(false);
        } else {
            queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Query"));
            setButton.setVisible(false);
            calculateButton.setVisible(true);
        }
        queryPanel.setVisible(true);
        RangeBox.setSelected(false);
        this.descriptions = MultiDstruktApp.getApplication().getIDC().getDescription();
        int size = this.descriptions.length;
        Object[][] content = new Object[size][2];
        for (int i = 0; i < size; i++) {
            Object[] o = {this.descriptions[i], 0};
            content[i] = o;
        }

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                content,
                new String[]{
            "Dimension", "Coordinate"
        }) {

            Class[] types = new Class[]{
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean[]{
                false, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
    }

    /**
     * Setzt eine Action fuer eine Abfrage zu einem Bereich.
     */
    @Action
    public void rangeQuery() {
        this.update = false;
        showRange();
        this.jLabel4.setText("Result");
    }

    /**
     * Setzt eine Action fuer ein Update von einem Bereich.
     */
    @Action
    public void rangeUpdate() {
        this.update = true;
        showRange();
        this.jLabel4.setText("new Value");
    }

    /**
     * Diese Methode Zeigt die Dialogtabelle, wo man Koordinaten 
     * fuer ein Bereich eingeben kann. 
     */
    public void showRange() {
        ResultField.setText("0");
        queryPanel.setVisible(true);
        if (update) {
            queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Update"));
            setButton.setVisible(true);
            setButton.setEnabled(true);
            this.jComboBox2.setVisible(true);
            calculateButton.setVisible(false);
        } else {
            queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Query"));
            setButton.setVisible(false);
            this.jComboBox2.setVisible(false);
            calculateButton.setVisible(true);
        }
        RangeBox.setSelected(true);
        this.descriptions = MultiDstruktApp.getApplication().getIDC().getDescription();
        int size = this.descriptions.length;
        Object[][] content = new Object[size][3];
        for (int i = 0; i < size; i++) {
            Object[] o = {this.descriptions[i], 0, 0};
            content[i] = o;
        }

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                content,
                new String[]{
            "Dimension", "lower", "upper"
        }) {

            Class[] types = new Class[]{
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean[]{
                false, true, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setResizable(false);

    }

    /**
     * Setzt eine Action für die Berechnung einer Abfrage. 
     */
    @Action
    public void calculate() {
        if (this.RangeBox.isSelected()) {
            this.calculateRangeSum();
        } else {
            this.calculateQuery();
        }
    }

    /**
     * Setzt eine Action für die Aktualisierung
     */
    @Action
    public void update() {
        if (this.RangeBox.isSelected()) {
            this.updateRangeSum();
        } else {
            this.updateCell();
        }
    }

    /**
     * Setzt eine Action fuer die Unterscheidung, ob es eine 
     * Anfrage/Update zu einer Zelle oder zu einem Bereich durchgefuehrt werden muss.  
     */
    @Action
    public void rangeSelected() {
        if (this.update) {
            if (this.RangeBox.isSelected()) {
                this.rangeUpdate();
            } else {
                this.pointUpdate();
            }
            this.ResultField.setText("0");
        } else {
            if (this.RangeBox.isSelected()) {
                this.rangeQuery();
            } else {
                this.pointQuery();
            }
            this.ResultField.setText("0");
        }
    }

    /**
     * Setzt eine Action, die ein About… Informationsfenster anzeigt.
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
            aboutBox = new MultiDstruktAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MultiDstruktApp.getApplication().show(aboutBox);
    }

    /**
     * Setzt eine Action, die ein Dialogfenster fuer Hierarchieeingabe aufruft.
     */
    @Action
    public void setHierarchie() {

        JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
        hierarchie = new SetHierarchie(mainFrame, true, 0);
        hierarchie.setLocationRelativeTo(mainFrame);
        MultiDstruktApp.getApplication().show(hierarchie);
    }

    /**
     * Setzt eine Action, die „Open File“ Dialogfenster aufruft,
     * um ein Gespeichertes IDC in Hauptspeicher zu laden.   
     */
    @Action
    public void showOpenIDC() {
        //Create a file chooser
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("IDC Cube File", "idc"));
        int returnVal = fc.showOpenDialog(this.fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                IDC cube = (IDC) SerializeAndDeserializeCube.deserialize(file.getPath());
                MultiDstruktApp.getApplication().setIDC(cube);
                MultiDstruktApp.getApplication().fileLocation = file.getPath();
                MultiDstruktApp.getApplication().cubeReady = true;
                this.showInformation();
            } catch (Exception e) {
                MultiDstruktApp.getApplication().cubeReady = false;
                this.queryPanel.setVisible(false);
                this.queryMenu.setEnabled(false);
                this.updateMenu.setEnabled(false);
                this.hierarchyMenu.setEnabled(false);
                this.queryButton.setEnabled(false);
                this.updateButton.setEnabled(false);
                this.setHierarhyButton.setEnabled(false);
                this.hierqueryButton.setEnabled(false);
                this.jLabel1.setEnabled(false);
                this.jLabel2.setEnabled(false);
                this.jLabel3.setEnabled(false);
                this.sizeText.setEnabled(false);
                this.dimField.setEnabled(false);
                this.densityField.setEnabled(false);
                JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "Incorrect .idc-file!", "", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Setzt eine Action, die ruft „Open File“ Dialogfenster auf,
     * um ein CSV Datei zu laden.
     */
    @Action
    public void showLoadCSV() {
        //Create a file chooser    
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
        int returnVal = fc.showOpenDialog(this.fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            IDC cube = new IDC();
            FileParser.parse(file.getPath(), cube);
            MultiDstruktApp.getApplication().setIDC(cube);
            showNewIDC();
        }
    }

    /**
     * Diese Methode zeigt ein Dialogfenster fuer die Erstellung von neuem IDC Cube. 
     */
    public void showNewIDC() {
        JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
        newIDC = new NewIDC(mainFrame);
        newIDC.setLocationRelativeTo(mainFrame);
        MultiDstruktApp.getApplication().fileLocation = null;
        MultiDstruktApp.getApplication().show(newIDC);
    }

    /**
     * Setzt eine Action, die ruft „Save File“ Dialogfenster auf,
     * um ein IDC Object in eine Datei zu speichern.
     */
    @Action
    public void saveAs() {
        if (!MultiDstruktApp.getApplication().cubeReady)
            return;
        //Create a file chooser    
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("IDC Cube File", "idc"));
        int returnVal = fc.showSaveDialog(this.fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            IDC cube = MultiDstruktApp.getApplication().getIDC();
            String filename = file.getPath();
            if (!(filename.substring(filename.length() - 4).equals(".idc"))) {
                filename = filename + ".idc";
            }
            SerializeAndDeserializeCube.serialize(filename, cube);
            MultiDstruktApp.getApplication().fileLocation = filename;
        }
    }

    /**
     * Setzt eine Action um ein IDC Object in eine Datei zu speichern.
     */
    @Action
    public void save() {
        String filename = MultiDstruktApp.getApplication().fileLocation;
        IDC idc = MultiDstruktApp.getApplication().getIDC();
        if (MultiDstruktApp.getApplication().fileLocation == null) {
            saveAs();
        } else {
            SerializeAndDeserializeCube.serialize(filename, idc);
        }
    }
    
    /**
     * Speichert Hierarchien des Cubes in eine Datei.
     */
    @Action
    public void exportHierarchie() {
        if (!MultiDstruktApp.getApplication().cubeReady)
            return;
        IDC idc=MultiDstruktApp.getApplication().getIDC();
        int[] shape=idc.getShape();
        IDCDimension[] dimensions=idc.dimension;
        boolean empty=true;
        DimensionHierarchie[] dimHier=new DimensionHierarchie[shape.length];
        for (int i=0;i<dimensions.length;i++){
            if (dimensions[i].getHierarchie()!=null){
                empty=false;
                dimHier[i]=dimensions[i].getHierarchie();
            }
        }
        if (empty){
            JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(),"No Hierarchy defined! Nothing to save!","Empty Hierarchy!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        HierarchieData hData=new HierarchieData(dimHier,shape);
        //Create a file chooser    
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Hierarchy Data File", "hd"));
        int returnVal = fc.showSaveDialog(this.fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filename = file.getPath();
            if (!(filename.substring(filename.length() - 3).equals(".hd"))) {
                filename = filename + ".hd";
            }
            SerializeAndDeserializeHierarchie.serialize(filename, hData);
            MultiDstruktApp.getApplication().fileLocation = filename;
        }
    }
    /**
     * Laedt Hierarchie aus einer Datei aus und setzt Hierarchien des Cubes entsprechend,
     * falls Cube und Hierarchie kompatibel zueinander sind.
     */
    @Action
    public void importHierarchieData(){
    //Create a file chooser
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Hierarchy Data File", "hd"));
        int returnVal = fc.showOpenDialog(this.fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                HierarchieData hData = SerializeAndDeserializeHierarchie.deserialize(file.getPath());
                IDC idc=MultiDstruktApp.getApplication().getIDC();
                int[] shape=idc.getShape();
                // Pruefen, ob shape des geladenen IDCs mit shape der Hierarchie uebereinstimmt
                if (shape.length!=hData.shape.length){
                    JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(),"The Hierarchy doesn't fit to the loaded cube!","Wrong Shape!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i=0;i<shape.length;i++){
                    if (shape[i]!=hData.shape[i]){
                        JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(),"The Hierarchy doesn't fit to the loaded cube!","Wrong Shape!", JOptionPane.ERROR_MESSAGE);
                        return;                   
                    }
                }
                for (int i=0;i<shape.length;i++){
                    idc.dimension[i].setHierarchie(hData.hierarchies[i]);
                }                    
                MultiDstruktApp.getApplication().hChanged = true;
            } catch (Exception e) {}
        }
    }

    /**
     * Diese Methode prueft die eingegebenen Koordinaten und
     * rechnet dann Abfrage zu einer Zelle aus.   
     */
    private void calculateQuery() {
        int[] lower = new int[this.descriptions.length];
        int[] shape = MultiDstruktApp.getApplication().getIDC().getShape();
        for (int i = 0; i < lower.length; i++) {
            int value = -1;
            try {
                Integer tmp = ((Integer) this.jTable1.getValueAt(i, 1));
                value = tmp.intValue();
            } catch (Exception e) {
            }
            if (value < 0 || value >= shape[i]) {
                JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "You entered a wrong coordinate!", "False Coordinate!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lower[i] = value;
        }
        double result = MultiDstruktApp.getApplication().getIDC().getCellValue(lower);
        //Auf drei Nachkommastellen runden, damit Anzeige passt.
        result = Math.floor(result * 1000);
        result = result / 1000.0;
        this.ResultField.setText(new Double(result).toString());
    }

    /**
     * Diese Methode prueft die eingegebenen Koordinaten und
     * rechnet dann Abfrage zu einem Bereich aus.   
     */
    private void calculateRangeSum() {
        int[] lower = new int[this.descriptions.length];
        int[] upper = lower.clone();
        int[] shape = MultiDstruktApp.getApplication().getIDC().getShape();
        for (int i = 0; i < lower.length; i++) {
            int value = -1;
            int value1 = -1;
            try {
                Integer tmp = ((Integer) this.jTable1.getValueAt(i, 1));
                value = tmp.intValue();
                tmp = ((Integer) this.jTable1.getValueAt(i, 2));
                value1 = tmp.intValue();
            } catch (Exception e) {
            }
            if (value < 0 || value1 >= shape[i] || value > value1) {
                JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "You entered a wrong range!", "False Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lower[i] = value;
            upper[i] = value1;
        }
        double result = MultiDstruktApp.getApplication().getIDC().getRangeSum(lower, upper);
        //Auf drei Nachkommastellen runden, damit Anzeige passt.
        result = Math.floor(result * 1000);
        result = result / 1000.0;
        this.ResultField.setText(new Double(result).toString());
    }

    /**
     * Diese Methode prueft die eingegebenen Koordinaten und
     * fuehrt dann die Aktualisierung von einer Zelle durch.   
     */
    private void updateCell() {
        int[] lower = new int[this.descriptions.length];
        int[] shape = MultiDstruktApp.getApplication().getIDC().getShape();
        for (int i = 0; i < lower.length; i++) {
            int value = -1;
            try {
                Integer tmp = ((Integer) this.jTable1.getValueAt(i, 1));
                value = tmp.intValue();
            } catch (Exception e) {
            }
            if (value < 0 || value >= shape[i]) {
                JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "You entered a wrong coordinate!", "False Coordinate!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lower[i] = value;
        }
        double value = Double.parseDouble(this.ResultField.getText());
        MultiDstruktApp.getApplication().getIDC().setCellValue(lower, value);
        JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "Cell Value updated!");
        this.showInformation();
    }

    /**
     * Setzt eine Action, fuer den Aufruf von einem Dialogfenster, 
     * wo man Hierarchieabfrage durchfuehren kann. 
     */
    @Action
    public void HierarchieQuery() {
        if (MultiDstruktApp.getApplication().hChanged) {
            this.CreateHierarchyArrays();
        }
        JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
        JDialog test = new HierarchieQU(mainFrame, true);
        test.setLocationRelativeTo(mainFrame);
        MultiDstruktApp.getApplication().fileLocation = null;
        MultiDstruktApp.getApplication().show(test);
    }

    /**
     * Bereitet die für HierarchieQuery benoetigte Hierarchie Arrays fuer jede Dimension
     */
    private void CreateHierarchyArrays() {
        IDC idc = MultiDstruktApp.getApplication().getIDC();
        int dimensionality = idc.getShape().length;
        // Array das fuer jede Dimension ein Hierarchie Array enthaelt
        Object[] hierarchies = new Object[dimensionality];
        for (int i = 0; i < dimensionality; i++) {
            ArrayList<DimensionHierarchie> tmp = new ArrayList<DimensionHierarchie>();

            if (idc.dimension[i].getHierarchie() != null) {
                DimensionHierarchie current = idc.dimension[i].getHierarchie();
                DimensionHierarchie[] children = current.getChildren();
                if (children != null) {
                    for (int j = 0; j < children.length; j++) {
                        addHierarchie(children[j], tmp);
                    }
                }
            }
            hierarchies[i] = tmp.toArray();
        }
        // setzt die globalen Variablen
        MultiDstruktApp.getApplication().hChanged = false;
        MultiDstruktApp.getApplication().hierarchies = hierarchies;
    }

    private void addHierarchie(DimensionHierarchie current, ArrayList<DimensionHierarchie> tmp) {
        tmp.add(current);
        DimensionHierarchie[] children = current.getChildren();
        if (children == null) {
            return;
        }
        for (int i = 0; i < current.getChildren().length; i++) {
            addHierarchie(children[i], tmp);
        }
    }

    /**
     * Diese Methode prueft die eingegebenen Koordinaten und
     * fuehrt dann die Aktualisierung von einem Bereich durch.   
     */
    private void updateRangeSum() {
        int[] lower = new int[this.descriptions.length];
        int[] upper = lower.clone();
        int[] shape = MultiDstruktApp.getApplication().getIDC().getShape();
        for (int i = 0; i < lower.length; i++) {
            int value = -1;
            int value1 = -1;
            try {
                Integer tmp = ((Integer) this.jTable1.getValueAt(i, 1));
                value = tmp.intValue();
                tmp = ((Integer) this.jTable1.getValueAt(i, 2));
                value1 = tmp.intValue();
            } catch (Exception e) {
            }
            if (value < 0 || value1 >= shape[i] || value > value1) {
                JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "You entered a wrong range!", "False Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lower[i] = value;
            upper[i] = value1;
        }
        double value = Double.parseDouble(this.ResultField.getText());
        MultiDstruktApp.getApplication().getIDC().rangeUpdate(lower, upper, value, jComboBox2.getSelectedIndex());
        JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(), "Range Sum updated!");
        this.showInformation();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        queryMenu = new javax.swing.JMenu();
        cellQuery = new javax.swing.JMenuItem();
        rangeQuery = new javax.swing.JMenuItem();
        updateMenu = new javax.swing.JMenu();
        cellUpdate = new javax.swing.JMenuItem();
        rangeUpdate = new javax.swing.JMenuItem();
        hierarchyMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        mainToolbar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        queryButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        setHierarhyButton = new javax.swing.JButton();
        hierqueryButton = new javax.swing.JButton();
        infoButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        InfoPanel = new javax.swing.JPanel();
        dimField = new javax.swing.JTextField();
        sizeText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        densityField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        queryPanel = new javax.swing.JPanel();
        RangeBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        ResultField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        calculateButton = new javax.swing.JButton();
        setButton = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getResourceMap(MultiDstruktView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getActionMap(MultiDstruktView.class, this);
        newMenuItem.setAction(actionMap.get("showLoadCSV")); // NOI18N
        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setText(resourceMap.getString("newMenuItem.text")); // NOI18N
        newMenuItem.setToolTipText(resourceMap.getString("newMenuItem.toolTipText")); // NOI18N
        newMenuItem.setName("newMenuItem"); // NOI18N
        fileMenu.add(newMenuItem);

        openMenuItem.setAction(actionMap.get("showOpenIDC")); // NOI18N
        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText(resourceMap.getString("openMenuItem.text")); // NOI18N
        openMenuItem.setToolTipText(resourceMap.getString("openMenuItem.toolTipText")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        fileMenu.add(openMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setToolTipText(resourceMap.getString("saveMenuItem.toolTipText")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        jMenuItem1.setAction(actionMap.get("saveAs")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setToolTipText(resourceMap.getString("jMenuItem1.toolTipText")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        queryMenu.setText(resourceMap.getString("queryMenu.text")); // NOI18N
        queryMenu.setEnabled(false);
        queryMenu.setName("queryMenu"); // NOI18N

        cellQuery.setAction(actionMap.get("pointQuery")); // NOI18N
        cellQuery.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        cellQuery.setText(resourceMap.getString("cellQuery.text")); // NOI18N
        cellQuery.setToolTipText(resourceMap.getString("cellQuery.toolTipText")); // NOI18N
        cellQuery.setName("cellQuery"); // NOI18N
        queryMenu.add(cellQuery);

        rangeQuery.setAction(actionMap.get("rangeQuery")); // NOI18N
        rangeQuery.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        rangeQuery.setText(resourceMap.getString("rangeQuery.text")); // NOI18N
        rangeQuery.setToolTipText(resourceMap.getString("rangeQuery.toolTipText")); // NOI18N
        rangeQuery.setName("rangeQuery"); // NOI18N
        queryMenu.add(rangeQuery);

        menuBar.add(queryMenu);

        updateMenu.setText(resourceMap.getString("updateMenu.text")); // NOI18N
        updateMenu.setEnabled(false);
        updateMenu.setName("updateMenu"); // NOI18N

        cellUpdate.setAction(actionMap.get("pointUpdate")); // NOI18N
        cellUpdate.setText(resourceMap.getString("cellUpdate.text")); // NOI18N
        cellUpdate.setToolTipText(resourceMap.getString("cellUpdate.toolTipText")); // NOI18N
        cellUpdate.setName("cellUpdate"); // NOI18N
        updateMenu.add(cellUpdate);

        rangeUpdate.setAction(actionMap.get("rangeUpdate")); // NOI18N
        rangeUpdate.setText(resourceMap.getString("rangeUpdate.text")); // NOI18N
        rangeUpdate.setToolTipText(resourceMap.getString("rangeUpdate.toolTipText")); // NOI18N
        rangeUpdate.setName("rangeUpdate"); // NOI18N
        updateMenu.add(rangeUpdate);

        menuBar.add(updateMenu);

        hierarchyMenu.setText(resourceMap.getString("hierarchyMenu.text")); // NOI18N
        hierarchyMenu.setEnabled(false);
        hierarchyMenu.setName("hierarchyMenu"); // NOI18N

        jMenuItem2.setAction(actionMap.get("setHierarchie")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        hierarchyMenu.add(jMenuItem2);

        jMenuItem3.setAction(actionMap.get("HierarchieQuery")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        hierarchyMenu.add(jMenuItem3);

        jMenuItem4.setAction(actionMap.get("exportHierarchie")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        hierarchyMenu.add(jMenuItem4);

        jMenuItem5.setAction(actionMap.get("importHierarchieData")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        hierarchyMenu.add(jMenuItem5);

        menuBar.add(hierarchyMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 395, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        mainToolbar.setBorder(null);
        mainToolbar.setRollover(true);
        mainToolbar.setName("mainToolBar"); // NOI18N

        newButton.setAction(actionMap.get("showLoadCSV")); // NOI18N
        newButton.setIcon(resourceMap.getIcon("newButton.icon")); // NOI18N
        newButton.setToolTipText(resourceMap.getString("newButton.toolTipText")); // NOI18N
        newButton.setFocusable(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setMaximumSize(new java.awt.Dimension(43, 42));
        newButton.setMinimumSize(new java.awt.Dimension(42, 41));
        newButton.setName("newButton"); // NOI18N
        newButton.setPreferredSize(new java.awt.Dimension(42, 41));
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(newButton);

        openButton.setAction(actionMap.get("showOpenIDC")); // NOI18N
        openButton.setIcon(resourceMap.getIcon("openButton.icon")); // NOI18N
        openButton.setToolTipText(resourceMap.getString("openButton.toolTipText")); // NOI18N
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setMaximumSize(new java.awt.Dimension(59, 42));
        openButton.setMinimumSize(new java.awt.Dimension(58, 41));
        openButton.setName("openButton"); // NOI18N
        openButton.setPreferredSize(new java.awt.Dimension(58, 41));
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(openButton);

        saveButton.setAction(actionMap.get("save")); // NOI18N
        saveButton.setIcon(resourceMap.getIcon("saveButton.icon")); // NOI18N
        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setToolTipText(resourceMap.getString("saveButton.toolTipText")); // NOI18N
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setMaximumSize(new java.awt.Dimension(43, 42));
        saveButton.setMinimumSize(new java.awt.Dimension(42, 41));
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setPreferredSize(new java.awt.Dimension(42, 41));
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(saveButton);

        queryButton.setAction(actionMap.get("pointQuery")); // NOI18N
        queryButton.setIcon(resourceMap.getIcon("queryButton.icon")); // NOI18N
        queryButton.setText(resourceMap.getString("queryButton.text")); // NOI18N
        queryButton.setDisabledIcon(resourceMap.getIcon("queryButton.disabledIcon")); // NOI18N
        queryButton.setEnabled(false);
        queryButton.setFocusable(false);
        queryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        queryButton.setMaximumSize(new java.awt.Dimension(43, 42));
        queryButton.setMinimumSize(new java.awt.Dimension(42, 41));
        queryButton.setName("queryButton"); // NOI18N
        queryButton.setPreferredSize(new java.awt.Dimension(42, 41));
        queryButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(queryButton);

        updateButton.setAction(actionMap.get("pointUpdate")); // NOI18N
        updateButton.setIcon(resourceMap.getIcon("updateButton.icon")); // NOI18N
        updateButton.setActionCommand(resourceMap.getString("updateButton.actionCommand")); // NOI18N
        updateButton.setDisabledIcon(resourceMap.getIcon("updateButton.disabledIcon")); // NOI18N
        updateButton.setEnabled(false);
        updateButton.setFocusable(false);
        updateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateButton.setMaximumSize(new java.awt.Dimension(43, 42));
        updateButton.setMinimumSize(new java.awt.Dimension(42, 41));
        updateButton.setName("updateButton"); // NOI18N
        updateButton.setPreferredSize(new java.awt.Dimension(42, 41));
        updateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(updateButton);

        setHierarhyButton.setAction(actionMap.get("setHierarchie")); // NOI18N
        setHierarhyButton.setIcon(resourceMap.getIcon("setHierarhyButton.icon")); // NOI18N
        setHierarhyButton.setText(resourceMap.getString("setHierarhyButton.text")); // NOI18N
        setHierarhyButton.setDisabledIcon(resourceMap.getIcon("setHierarhyButton.disabledIcon")); // NOI18N
        setHierarhyButton.setEnabled(false);
        setHierarhyButton.setFocusable(false);
        setHierarhyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setHierarhyButton.setMaximumSize(new java.awt.Dimension(43, 42));
        setHierarhyButton.setMinimumSize(new java.awt.Dimension(42, 41));
        setHierarhyButton.setName("setHierarhyButton"); // NOI18N
        setHierarhyButton.setPreferredSize(new java.awt.Dimension(42, 41));
        setHierarhyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(setHierarhyButton);

        hierqueryButton.setAction(actionMap.get("HierarchieQuery")); // NOI18N
        hierqueryButton.setIcon(resourceMap.getIcon("hierqueryButton.icon")); // NOI18N
        hierqueryButton.setText(resourceMap.getString("hierqueryButton.text")); // NOI18N
        hierqueryButton.setDisabledIcon(resourceMap.getIcon("hierqueryButton.disabledIcon")); // NOI18N
        hierqueryButton.setEnabled(false);
        hierqueryButton.setFocusable(false);
        hierqueryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        hierqueryButton.setMaximumSize(new java.awt.Dimension(43, 42));
        hierqueryButton.setMinimumSize(new java.awt.Dimension(42, 41));
        hierqueryButton.setName("hierqueryButton"); // NOI18N
        hierqueryButton.setPreferredSize(new java.awt.Dimension(42, 41));
        hierqueryButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(hierqueryButton);

        infoButton.setAction(actionMap.get("showAboutBox")); // NOI18N
        infoButton.setIcon(resourceMap.getIcon("infoButton.icon")); // NOI18N
        infoButton.setText(resourceMap.getString("infoButton.text")); // NOI18N
        infoButton.setFocusable(false);
        infoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        infoButton.setMaximumSize(new java.awt.Dimension(43, 42));
        infoButton.setMinimumSize(new java.awt.Dimension(42, 41));
        infoButton.setName("infoButton"); // NOI18N
        infoButton.setPreferredSize(new java.awt.Dimension(42, 41));
        infoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(infoButton);

        mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("mainPanel.border.lineColor"))); // NOI18N
        mainPanel.setFocusable(false);
        mainPanel.setMaximumSize(new java.awt.Dimension(500, 450));
        mainPanel.setMinimumSize(new java.awt.Dimension(500, 450));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(500, 450));
        mainPanel.setRequestFocusEnabled(false);
        mainPanel.setVerifyInputWhenFocusTarget(false);

        InfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("InfoPanel.border.title"))); // NOI18N
        InfoPanel.setName("InfoPanel"); // NOI18N

        dimField.setEditable(false);
        dimField.setText(resourceMap.getString("dimField.text")); // NOI18N
        dimField.setEnabled(false);
        dimField.setMinimumSize(new java.awt.Dimension(4, 30));
        dimField.setName("dimField"); // NOI18N

        sizeText.setEditable(false);
        sizeText.setText(resourceMap.getString("sizeText.text")); // NOI18N
        sizeText.setEnabled(false);
        sizeText.setMinimumSize(new java.awt.Dimension(4, 30));
        sizeText.setName("sizeText"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setEnabled(false);
        jLabel1.setName("jLabel1"); // NOI18N

        densityField.setEditable(false);
        densityField.setText(resourceMap.getString("densityField.text")); // NOI18N
        densityField.setEnabled(false);
        densityField.setMinimumSize(new java.awt.Dimension(4, 30));
        densityField.setName("densityField"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setEnabled(false);
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout InfoPanelLayout = new javax.swing.GroupLayout(InfoPanel);
        InfoPanel.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(
            InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(25, 25, 25)
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dimField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(densityField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(268, Short.MAX_VALUE))
        );
        InfoPanelLayout.setVerticalGroup(
            InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(densityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dimField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("queryPanel.border.title"))); // NOI18N
        queryPanel.setMaximumSize(new java.awt.Dimension(0, 0));
        queryPanel.setName("queryPanel"); // NOI18N
        queryPanel.setRequestFocusEnabled(false);

        RangeBox.setAction(actionMap.get("rangeSelected")); // NOI18N
        RangeBox.setText(resourceMap.getString("RangeBox.text")); // NOI18N
        RangeBox.setToolTipText(resourceMap.getString("RangeBox.toolTipText")); // NOI18N
        RangeBox.setName("RangeBox"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dimension", "lower", "upper"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setResizable(false);

        ResultField.setText(resourceMap.getString("ResultField.text")); // NOI18N
        ResultField.setName("ResultField"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        calculateButton.setAction(actionMap.get("calculate")); // NOI18N
        calculateButton.setText(resourceMap.getString("calculateButton.text")); // NOI18N
        calculateButton.setToolTipText(resourceMap.getString("calculateButton.toolTipText")); // NOI18N
        calculateButton.setName("calculateButton"); // NOI18N

        setButton.setAction(actionMap.get("update")); // NOI18N
        setButton.setText(resourceMap.getString("setButton.text")); // NOI18N
        setButton.setToolTipText(resourceMap.getString("setButton.toolTipText")); // NOI18N
        setButton.setName("setButton"); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "uniformly", "relative" }));
        jComboBox2.setName("jComboBox2"); // NOI18N
        jComboBox2.setVisible(false);

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(RangeBox)
                    .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultField, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addComponent(setButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(RangeBox)
                        .addGap(17, 17, 17)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calculateButton)
                        .addGap(18, 18, 18)
                        .addComponent(setButton)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(InfoPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(queryPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {InfoPanel, queryPanel});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(InfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        queryPanel.setVisible(false);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(mainToolbar);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JCheckBox RangeBox;
    private javax.swing.JTextField ResultField;
    private javax.swing.JButton calculateButton;
    private javax.swing.JMenuItem cellQuery;
    private javax.swing.JMenuItem cellUpdate;
    private javax.swing.JTextField densityField;
    private javax.swing.JTextField dimField;
    private javax.swing.JMenu hierarchyMenu;
    private javax.swing.JButton hierqueryButton;
    private javax.swing.JButton infoButton;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newButton;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton queryButton;
    private javax.swing.JMenu queryMenu;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JMenuItem rangeQuery;
    private javax.swing.JMenuItem rangeUpdate;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton setButton;
    private javax.swing.JButton setHierarhyButton;
    private javax.swing.JTextField sizeText;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton updateButton;
    private javax.swing.JMenu updateMenu;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JFileChooser fc;
    private JDialog newIDC;
    private JDialog hierarchie;
    private String[] descriptions;
    private boolean update;
}
