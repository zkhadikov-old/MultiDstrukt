/*
 * NewIDC.java
 *
 * Created on 9. Dezember 2007, 15:58
 */

package multidstrukt;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import structures.*;

/**
 * Ermoeglicht die Auswahl von Strategien
 * @author  Philippe Hagedorn, Zurab Khadikov
 */
public class NewIDC extends javax.swing.JDialog {
    
   
    public NewIDC(java.awt.Frame parent) {
        super(parent);
        initComponents();
        this.idc=MultiDstruktApp.getApplication().getIDC();
        this.description=idc.getDescription();
        this.dimensions=idc.getShape();
        int dimensionality=this.dimensions.length;
        if (MultiDstruktApp.getApplication().stratIndex==null){
            this.stratIndex=new int[dimensionality];
            this.blockSizes=new int[dimensionality];
            MultiDstruktApp.getApplication().blockSizes=this.blockSizes;
            MultiDstruktApp.getApplication().stratIndex=this.stratIndex;
        }
        else{
            this.blockSizes=MultiDstruktApp.getApplication().blockSizes;
            this.stratIndex=MultiDstruktApp.getApplication().stratIndex;
        }
        current=0;
        this.updateTextField();
    }
    @Action
    public void showAutoIDC() throws Throwable {
        if (autoIDC == null) {
            JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
            autoIDC = new AutoIDC(mainFrame);
            autoIDC.setLocationRelativeTo(mainFrame);
        }
         MultiDstruktApp.getApplication().setIDC(this.idc); 
         MultiDstruktApp.getApplication().show(autoIDC);
         this.dispose();
         this.finalize();
    }
    @Action
    public void showSetBlocks() throws Throwable {
        JFrame mainFrame = MultiDstruktApp.getApplication().getMainFrame();
        int count=0;
        try{
        count=Integer.parseInt(this.jTextField4.getText());
        }
        catch(Exception e){}            
        if (count<=0){
            JOptionPane.showMessageDialog(MultiDstruktApp.getApplication().getMainFrame(),"Type an integer greater than 0!","Incorrect number of blocks!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setBlocks = new SetBlocks(mainFrame,true,count,this.current);
        setBlocks.setLocationRelativeTo(mainFrame); 
        MultiDstruktApp.getApplication().show(setBlocks);        
    }
    
    @Action
    public void nextDim(){
        this.updateStrategies();
        if (current<this.description.length-1)
            current++;
        else
            current=0;
        this.updateTextField();
        
    }
    
    @Action
    public void prevDim(){
        this.updateStrategies();
        if (current>0)
            current--;
        else
            current=this.description.length-1;
        this.updateTextField();
    }
    
    protected void updateTextField(){
        this.dimNameFild.setText(description[current]);
        this.dimSizeFild.setText(new Integer(dimensions[current]).toString());
        this.jTextField3.setText(new Integer(blockSizes[current]).toString());
        this.jComboBox1.setSelectedIndex(stratIndex[current]);
    }
    
    private void updateStrategies(){
        this.stratIndex[current]=this.jComboBox1.getSelectedIndex();
        try{
            blockSizes[current]=Integer.parseInt(this.jTextField3.getText());
        }
        catch(Exception e){}
    }
    @Action
    public void precompute() throws Throwable{
        int dimensionality=this.description.length;
        for (int i=0;i<dimensionality;i++){
            IStrategie tmp;
            if (this.blockSizes[i]<=0)
                this.blockSizes[i]=(int) Math.sqrt(dimensions[i]);
            switch (this.stratIndex[i]){                
                case 2: try{tmp=new VariableLPS(dimensions[i], (int[]) MultiDstruktApp.getApplication().variableBlocks[i]);break;} catch(Exception e){}
                case 0: tmp=new LPS(dimensions[i],this.blockSizes[i]); break;
                case 3: try{tmp=new VariableSRPS(dimensions[i], (int[]) MultiDstruktApp.getApplication().variableBlocks[i]);break;} catch(Exception e){}
                case 1: tmp=new SRPS(dimensions[i],this.blockSizes[i]); break;
                case 4: tmp=new SDDC(dimensions[i]);break;                
                default: tmp=new SRPS(dimensions[i],this.blockSizes[i]); break;
            }
            idc.dimension[i].setStrategie(tmp);
        }
        this.idc.precompute();
        MultiDstruktApp.getApplication().setIDC(idc);
        MultiDstruktApp.getApplication().cubeReady=true;
        MultiDstruktApp.getApplication().getView().showInformation();
        this.dispose();
        this.finalize();
    }
    
    @Action 
    public void closeNewIDC() throws Throwable{
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

        PrecomputeButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        DimensionPanel = new javax.swing.JPanel();
        dimNameFild = new javax.swing.JTextField();
        dimName = new javax.swing.JLabel();
        dimSizeFild = new javax.swing.JTextField();
        dimSize = new javax.swing.JLabel();
        StrategyPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getResourceMap(NewIDC.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setLocationByPlatform(true);
        setName("Form"); // NOI18N
        setResizable(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(multidstrukt.MultiDstruktApp.class).getContext().getActionMap(NewIDC.class, this);
        PrecomputeButton.setAction(actionMap.get("precompute")); // NOI18N
        PrecomputeButton.setIcon(resourceMap.getIcon("PrecomputeButton.icon")); // NOI18N
        PrecomputeButton.setText(resourceMap.getString("PrecomputeButton.text")); // NOI18N
        PrecomputeButton.setToolTipText(resourceMap.getString("PrecomputeButton.toolTipText")); // NOI18N
        PrecomputeButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PrecomputeButton.setMaximumSize(new java.awt.Dimension(36, 36));
        PrecomputeButton.setMinimumSize(new java.awt.Dimension(36, 36));
        PrecomputeButton.setName("PrecomputeButton"); // NOI18N
        PrecomputeButton.setPreferredSize(new java.awt.Dimension(36, 36));

        CancelButton.setAction(actionMap.get("closeNewIDC")); // NOI18N
        CancelButton.setIcon(resourceMap.getIcon("CancelButton.icon")); // NOI18N
        CancelButton.setText(resourceMap.getString("CancelButton.text")); // NOI18N
        CancelButton.setToolTipText(resourceMap.getString("CancelButton.toolTipText")); // NOI18N
        CancelButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CancelButton.setMaximumSize(new java.awt.Dimension(36, 36));
        CancelButton.setMinimumSize(new java.awt.Dimension(36, 36));
        CancelButton.setName("CancelButton"); // NOI18N
        CancelButton.setPreferredSize(new java.awt.Dimension(36, 36));

        nextButton.setAction(actionMap.get("nextDim")); // NOI18N
        nextButton.setIcon(resourceMap.getIcon("nextButton.icon")); // NOI18N
        nextButton.setText(resourceMap.getString("nextButton.text")); // NOI18N
        nextButton.setToolTipText(resourceMap.getString("nextButton.toolTipText")); // NOI18N
        nextButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        nextButton.setMaximumSize(new java.awt.Dimension(36, 36));
        nextButton.setMinimumSize(new java.awt.Dimension(36, 36));
        nextButton.setName("nextButton"); // NOI18N
        nextButton.setPreferredSize(new java.awt.Dimension(31, 31));

        backButton.setAction(actionMap.get("prevDim")); // NOI18N
        backButton.setIcon(resourceMap.getIcon("backButton.icon")); // NOI18N
        backButton.setText(resourceMap.getString("backButton.text")); // NOI18N
        backButton.setToolTipText(resourceMap.getString("backButton.toolTipText")); // NOI18N
        backButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backButton.setMaximumSize(new java.awt.Dimension(36, 36));
        backButton.setMinimumSize(new java.awt.Dimension(36, 36));
        backButton.setName("backButton"); // NOI18N
        backButton.setPreferredSize(new java.awt.Dimension(36, 36));

        DimensionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("DimensionPanel.border.title"))); // NOI18N
        DimensionPanel.setName("DimensionPanel"); // NOI18N

        dimNameFild.setEditable(false);
        dimNameFild.setText(resourceMap.getString("dimNameFild.text")); // NOI18N
        dimNameFild.setName("dimNameFild"); // NOI18N

        dimName.setText(resourceMap.getString("dimName.text")); // NOI18N
        dimName.setName("dimName"); // NOI18N

        dimSizeFild.setEditable(false);
        dimSizeFild.setText(resourceMap.getString("dimSizeFild.text")); // NOI18N
        dimSizeFild.setName("dimSizeFild"); // NOI18N

        dimSize.setText(resourceMap.getString("dimSize.text")); // NOI18N
        dimSize.setName("dimSize"); // NOI18N

        javax.swing.GroupLayout DimensionPanelLayout = new javax.swing.GroupLayout(DimensionPanel);
        DimensionPanel.setLayout(DimensionPanelLayout);
        DimensionPanelLayout.setHorizontalGroup(
            DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DimensionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dimSize)
                    .addComponent(dimName))
                .addGap(93, 93, 93)
                .addGroup(DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dimNameFild, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dimSizeFild, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        DimensionPanelLayout.setVerticalGroup(
            DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DimensionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dimName)
                    .addComponent(dimNameFild, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(DimensionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dimSize)
                    .addComponent(dimSizeFild, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        StrategyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("StrategyPanel.border.title"))); // NOI18N
        StrategyPanel.setName("StrategyPanel"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setEnabled(false);
        jLabel5.setName("jLabel5"); // NOI18N

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setEnabled(false);
        jTextField4.setName("jTextField4"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LPS", "SRPS", "var. LPS", "var. SRPS", "SDDC" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jButton4.setAction(actionMap.get("showAutoIDC")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setToolTipText(resourceMap.getString("jButton4.toolTipText")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        jButton1.setAction(actionMap.get("showSetBlocks")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout StrategyPanelLayout = new javax.swing.GroupLayout(StrategyPanel);
        StrategyPanel.setLayout(StrategyPanelLayout);
        StrategyPanelLayout.setHorizontalGroup(
            StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StrategyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jButton1))
                .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StrategyPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                        .addGap(133, 133, 133))
                    .addGroup(StrategyPanelLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jButton4)
                        .addContainerGap())))
        );
        StrategyPanelLayout.setVerticalGroup(
            StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StrategyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StrategyPanelLayout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addComponent(jLabel3))
                .addGap(14, 14, 14)
                .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(StrategyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DimensionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StrategyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(PrecomputeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DimensionPanel, StrategyPanel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CancelButton, PrecomputeButton, backButton, nextButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(DimensionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StrategyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(PrecomputeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(nextButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(backButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CancelButton, PrecomputeButton, backButton, nextButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (this.jComboBox1.getSelectedIndex()==2 || this.jComboBox1.getSelectedIndex()==3){
            jLabel5.setEnabled(true);        
            jTextField4.setEnabled(true);
            jButton1.setEnabled(true);
            jTextField3.setEnabled(false);
        }
        else{
            jLabel5.setEnabled(false);        
            jTextField4.setEnabled(false);
            jButton1.setEnabled(false);
            if (this.jComboBox1.getSelectedIndex()!=4)
                jTextField3.setEnabled(true);
            else
                jTextField3.setEnabled(false);
        }
            
    }//GEN-LAST:event_jComboBox1ItemStateChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewIDC dialog = new NewIDC(new javax.swing.JFrame());
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
    private javax.swing.JButton CancelButton;
    private javax.swing.JPanel DimensionPanel;
    private javax.swing.JButton PrecomputeButton;
    private javax.swing.JPanel StrategyPanel;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel dimName;
    private javax.swing.JTextField dimNameFild;
    private javax.swing.JLabel dimSize;
    private javax.swing.JTextField dimSizeFild;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables
    private JDialog autoIDC;
    private JDialog setBlocks;
    private IDC idc;
    private String[] description;
    private int[] dimensions;
    private int current;
    private int[] stratIndex;
    private int[] blockSizes;
}
