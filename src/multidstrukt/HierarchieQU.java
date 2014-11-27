/*
 * HierarchieQU.java
 *
 * Created on 9. Januar 2008, 13:30
 */

package multidstrukt;
import helpers.Counters;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import structures.*;

/**
 * Dialog, um Abfragen ueber Hierarchien machen zu koennen.
 * @author  hagedorn, khadikov
 */
public class HierarchieQU extends javax.swing.JDialog {
    
    
    /** Creates new form HierarchieQU */
    public HierarchieQU(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // holt sich Array, in dem alle Hierarchien gespeichert sind
        this.hierarchies=MultiDstruktApp.getApplication().hierarchies;
        // Initialisierung aller Instanzvariablen
        this.dimNr=0;
        this.DimBox.setSelectedIndex(dimNr);
        this.idc=MultiDstruktApp.getApplication().getIDC();
        this.dim=this.idc.getShape().length;
        this.low=new int[dim];
        this.high=new int[dim];
        this.numberOfH=new int[dim];
        for (int i=0;i<dim;i++)
            try{
             numberOfH[i]=((Object[]) this.hierarchies[i]).length;
            }
            catch(Exception e){}
        // setzt ALL bei allen Dimensionen als Standardwert
        this.SelectedIndizes=numberOfH.clone();
        // Konfiguriert die Combo-Box zur Hierarchieauswahl
        updateHBox();
        
    }

    /**
     * Methode, die die Combo-Box zur Hierachieauswahl fuer die ausgewaehlte Dimension konfiguriert.
     */
    private void updateHBox() {
        // holt sich die verfuegbaren Hierarchien fuer die Dimension
        Object[] items=new Object[0];
        try{
            items=(Object[]) this.hierarchies[dimNr];
        }
        catch(Exception e){}
        // entfernt die Items der bisherigen Dimension
        HiBox.removeAllItems();
        // fuegt alle Hierarchien als Auswahlmoeglichkeiten fuer die Combo-Box hinzu
        for (int i=0;i<items.length;i++){
            String name=((DimensionHierarchie) items[i]).getName();
            HiBox.addItem(name);            
        }
        // fuegt ausserdem als zusaetzliche Auswahl "ALL" und "manual Range" hinzu
        HiBox.addItem("ALL");
        HiBox.addItem("manual Range");
        // setzt zuvor gespeicherten Wert (oder Standardwert) als Selected Index
        HiBox.setSelectedIndex(this.SelectedIndizes[dimNr]);
        // falls "manual Range" ausgewaehlt ist, kann man eine Range eingeben, ansonsten nicht
        if (this.SelectedIndizes[dimNr]==this.numberOfH[dimNr]+1){
            this.lowField.setText(new Integer(low[dimNr]).toString());
            this.highField.setText(new Integer(high[dimNr]).toString());
            this.lowField.setEnabled(true);
            this.highField.setEnabled(true);
            this.jLabel1.setEnabled(true);
            this.jLabel2.setEnabled(true);
        }
        else{
            this.lowField.setEnabled(false);
            this.highField.setEnabled(false);
            this.jLabel1.setEnabled(false);
            this.jLabel2.setEnabled(false);
        }
    }
    
    /**
     * Methode, die aufgerufen wird, wenn man mit der Combo-Box die Dimension wechselt.
     */
    private void dimChange(){
        // prueft, ob sich Dimension ueberhaupt geaendert hat.
        if (this.dimNr==this.DimBox.getSelectedIndex())
            return;
        // speichert ausgewaehlte Hierarchie und falls noetig Range, der zuvor ausgewaehlten Hierarchie ab
        update();
        // Speichert aktuelle Dimension ab
        this.dimNr=this.DimBox.getSelectedIndex();
        // setzt die Hierarchie und ggf. Range, der neu ausgewaehlten Dimension, auf zuvor gespeicherte Werte
        updateHBox();
    }
    
    /**
     * Methode, die Eingabe der Range je nach Auswahl der Hierarchie ein- und ausschaltet.
     */
    private void hierChange(){
        // Hierarchie wurde nicht veraendert oder Methode wurde nur aufgerufen,
        // weil die Items der Combo-Box entfernt wurden (also nach einem Dimensionswechsel).
        if (this.SelectedIndizes[dimNr]==this.HiBox.getSelectedIndex()||this.HiBox.getSelectedIndex()==-1)
            return;        
        // "manual Range" wurde ausgewaehlt
        if (this.HiBox.getSelectedIndex()==this.numberOfH[dimNr]+1){
            this.lowField.setEnabled(true);
            this.lowField.setText(new Integer(low[dimNr]).toString());
            this.highField.setText(new Integer(high[dimNr]).toString());
            this.highField.setEnabled(true);
            this.jLabel1.setEnabled(true);
            this.jLabel2.setEnabled(true);
        }
        // Hierarchie oder "ALL" wurde ausgewahelt.
        else{
            this.lowField.setEnabled(false);
            this.highField.setEnabled(false);
            this.jLabel1.setEnabled(false);
            this.jLabel2.setEnabled(false);
        }        
    }
    
   /**
    * Aktualisiert gespeicherte Hierarchie und falls noetig auch Range.
    */
    private void update(){
        this.SelectedIndizes[this.dimNr]=this.HiBox.getSelectedIndex();
        this.low[dimNr]=-1;
        this.high[dimNr]=-1;
        if (this.SelectedIndizes[this.dimNr]==this.numberOfH[dimNr]+1){
            try{
                this.low[dimNr]=Integer.parseInt(this.lowField.getText());
                this.high[dimNr]=Integer.parseInt(this.highField.getText());
            }
            catch(Exception e){}
        }
    }
    
    /**
     * Berechnet im Fall, dass bei mindestens einer Dimension eine Hierarchie aus
     * mehr als eine Range besteht, alle weiteren Rangesummen (die erste Summe wird
     * schon beim pruefen, ob eine Hierarchie mehr als eine Range hat, berechnet.)
     * @param numberOfRanges Array, bei dem fuer jede Dimension die Anzahl an Ranges aus denen die Hierarchie besteht, gespeichert ist.
     * @return Summe aller weiteren Rangesummen, abgesehen von der aus dem einfachsten Fall.
     */
    private double calculateAllRangeSums(int[] numberOfRanges) {
        // Liste, in der alle Dimensionsnummern gespeichert sind, bei denen die
        // ausgewaehlte Hierarchie aus mehr als einer Range besteht.
        ArrayList<Integer> concerned=new ArrayList<Integer>();
        double result=0;
        // Liste wird gefuellt.
        for (int i=0;i<dim;i++)
            if (numberOfRanges[i]>1)
                concerned.add(i);
        // Zaehler zum Durchlaufen aller Faelle wird erzeugt.
        int[] counter=new int[dim];
        int count=1;
        // Liste der LoHiListen von Hierarchien mit mehr als einer Range.
        LoHiList[] Lists=new LoHiList[concerned.size()];
        // ermittelt, wie viele Rangesummen berechnet werden muessen und fuellt Array von LoHiListen.
        for (int i=0; i<concerned.size();i++){
            count*=numberOfRanges[concerned.get(i)];
            // holt sich die ausgewaehlten Hierarchien
            Object[] tmp=((Object[])this.hierarchies[i]);
            DimensionHierarchie tmp2=(DimensionHierarchie) tmp[SelectedIndizes[concerned.get(i)]];
            // Speichert die zur Hierarchie gehoerende LoHiListe
            Lists[i]=tmp2.getLoHi();
        }
        // da eine Summe schon berechnet wurde, wird ermittelt, welche Summe noch fehlt.
        count--;
        for (int i=0;i<count;i++){
            counter=Counters.counternext(counter, numberOfRanges, 0);
            // ermittelt die Ranges, in den Dimensionen, wo es mehr als nur eine gibt.
            for (int j=0; j<concerned.size();j++){
                int pos=concerned.get(j);
                LoHiNode tmp=Lists[j].head;
                // waehlt die richtige Stelle in der LoHiListe aus
                for (int k=0;k<counter[pos];k++){
                    tmp=tmp.next();
                }
                // setzt die Range
                low[pos]=tmp.low;
                high[pos]=tmp.high;
            }
            result+=this.idc.getRangeSum(low, high);    
        }
        return result;
    }
    
    
    /**
     * fuehrt Berechnung der Query aus
     */
    @Action
    public void setCalculateButton() {        
        // speichert Hierarchie, der aktuell ausgewaehlten Hierarchie ab.
        update();
        double result=0;
        // Variable, die angibt, ob Ergebnis aus mehr als einer RangeSumme besteht.
        boolean moreThanOne=false;
        // Groesse der Dimensionen
        int[] shape=this.idc.getShape();
        // Array, das die Anzahl der Ranges fuer jede Dimension enthaelt.
        int[] numberOfRanges=new int[dim];
        LoHiNode[] Range=new LoHiNode[dim];
        // holt fuer jede Dimension, die erste betroffene Range und ermittelt,
        // ob insgesamt mehr als eine RangeSumme berechnet werden muss.
        for (int i=0;i<dim;i++){
            // "ALL" wurde ausgewaehlt
            if (this.SelectedIndizes[i]==numberOfH[i]){
                Range[i]=new LoHiNode(0, shape[i]-1);
                low[i]=0;
                high[i]=shape[i]-1;
                numberOfRanges[i]=1;
                continue;
            }
            // "manual Range" wurde ausgewaehlt.
            if (this.SelectedIndizes[i]==numberOfH[i]+1){
                // Prueft, ob die eingebene Range korrekt war, wenn nicht erscheint MessageBox.
                if (low[i]>high[i]||high[i]>=shape[i]||low[i]<0){
                    String tmpName=this.idc.getDescription()[i];
                    JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(),"The range you entered in dimension "+ tmpName+ " is not correct!","False Range!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                numberOfRanges[i]=1;
                continue;
            }
            // eine Hierarchie wurde ausgewaehlt, nur hier kann es passieren, dass es mehrere Ranges gibt.
            Object[] tmp=(Object[]) hierarchies[i];
            // Hierarchie wird ermittelt und deren LoHiListe geholt.
            DimensionHierarchie h=(DimensionHierarchie) tmp[SelectedIndizes[i]];
            LoHiList lohi=h.getLoHi();
            // Falls Hierarchie aus mehreren Ranges besteht, wird Boolesche Variable gesetzt.
            if (lohi.size()>1){
                moreThanOne=true;
            }
            numberOfRanges[i]=lohi.size();
            Range[i]=lohi.head;
            low[i]=lohi.head.low;
            high[i]=lohi.head.high;
        }
        
        //erste RangeSum wird berechnet.
        result+=this.idc.getRangeSum(low, high);
        // falls Ergebnis aus mehr als einer Summe besteht, werden restliche Summen noch dazugerechnet.
        if (moreThanOne)
            result+=calculateAllRangeSums(numberOfRanges);

        //Auf drei Nachkommastellen runden, damit Anzeige passt.
        result = Math.floor(result * 1000);
        result = result / 1000.0;
        //Ergebnis wird angezeigt.
        ResultField.setText(new Double(result).toString());

    }
    
    @Action
    public void close() throws Throwable{
        this.dispose();
        this.finalize();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HiBox = new javax.swing.JComboBox();
        lowField = new javax.swing.JTextField();
        highField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        CloseButton = new javax.swing.JButton();
        DimBox = new javax.swing.JComboBox();
        ResultField = new javax.swing.JTextField();
        CalcButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        resultupdate = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getResourceMap(HierarchieQU.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setLocationByPlatform(true);
        setName("Form"); // NOI18N
        setResizable(false);

        HiBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        HiBox.setName("HiBox"); // NOI18N
        HiBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                HiBoxItemStateChanged(evt);
            }
        });

        lowField.setText(resourceMap.getString("lowField.text")); // NOI18N
        lowField.setName("lowField"); // NOI18N

        highField.setText(resourceMap.getString("highField.text")); // NOI18N
        highField.setName("highField"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getActionMap(HierarchieQU.class, this);
        CloseButton.setAction(actionMap.get("close")); // NOI18N
        CloseButton.setText(resourceMap.getString("CloseButton.text")); // NOI18N
        CloseButton.setToolTipText(resourceMap.getString("CloseButton.toolTipText")); // NOI18N
        CloseButton.setName("CloseButton"); // NOI18N

        String[] dimNames=MultiDstruktApp.getApplication().getIDC().getDescription();
        DimBox.setModel(new javax.swing.DefaultComboBoxModel(dimNames));
        DimBox.setName("DimBox"); // NOI18N
        DimBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DimBoxItemStateChanged(evt);
            }
        });

        ResultField.setText(resourceMap.getString("ResultField.text")); // NOI18N
        ResultField.setName("ResultField"); // NOI18N
        ResultField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResultFieldActionPerformed(evt);
            }
        });

        CalcButton.setAction(actionMap.get("setCalculateButton")); // NOI18N
        CalcButton.setText(resourceMap.getString("CalcButton.text")); // NOI18N
        CalcButton.setToolTipText(resourceMap.getString("CalcButton.toolTipText")); // NOI18N
        CalcButton.setName("CalcButton"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        resultupdate.setText(resourceMap.getString("resultupdate.text")); // NOI18N
        resultupdate.setName("resultupdate"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultupdate)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(23, 23, 23))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lowField, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(highField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addComponent(DimBox, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ResultField, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                        .addGap(51, 51, 51)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(HiBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CalcButton)
                        .addGap(18, 18, 18)
                        .addComponent(CloseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(21, 21, 21)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {highField, lowField});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CalcButton, CloseButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lowField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(highField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HiBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DimBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(resultupdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ResultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CalcButton)
                    .addComponent(CloseButton))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DimBox, HiBox});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ResultFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResultFieldActionPerformed

}//GEN-LAST:event_ResultFieldActionPerformed

    private void DimBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DimBoxItemStateChanged
        this.dimChange();
    }//GEN-LAST:event_DimBoxItemStateChanged

    private void HiBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_HiBoxItemStateChanged
        this.hierChange();
    }//GEN-LAST:event_HiBoxItemStateChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HierarchieQU dialog = new HierarchieQU(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CalcButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JComboBox DimBox;
    private javax.swing.JComboBox HiBox;
    private javax.swing.JTextField ResultField;
    private javax.swing.JTextField highField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField lowField;
    private javax.swing.JLabel resultupdate;
    // End of variables declaration//GEN-END:variables
    // Array, das fuer jede Dimension ein Array mit allen definierten Hierarchien enthaelt.
    private Object[] hierarchies;
    // Array, das fuer jede Dimension den Index der ausgewaehlten Hierarchie enthaelt. 
    private int[] SelectedIndizes;
    // untere Grenze der als naechstes zu berechnenden RangeSum
    private int[] low;
    // obere Grenze der als naechstes zu berechnenden RangeSum
    private int[] high;
    // enthaelt fuer jede Dimension, die Anzahl der definierten Hierarchien
    private int[] numberOfH;
    // aktuell ausgewaehlte Dimension
    private int dimNr;
    private IDC idc;
    // Anzahl der Dimensionen
    private int dim;

    
}
