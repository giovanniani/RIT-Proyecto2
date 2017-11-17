/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import apendix.Routes;
import java.awt.Desktop;
import java.awt.Dimension;
import static java.awt.SystemColor.control;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import query.SearchEngine;

/**
 *
 * @author Greivin
 */
public class SearchPanel extends javax.swing.JFrame {

    /**
     * Creates new form SearchPanel
     */
    public int S=190;
    public int M=280;
    public int L=420;
    public SearchEngine engine;
    public SearchPanel() throws IOException {
        initComponents();
        engine= new SearchEngine();
        normalSize();
    }
    public void normalSize(){
        showIndexPanel.setSelected(false);
        indexPanel.setVisible(false);
        resultadosPanel.setVisible(false);
        this.setSize(this.getSize().width, S);
        centerPanel();
    }
    public void indexSize(){
        showIndexPanel.setSelected(true);
        indexPanel.setVisible(true);
        resultadosPanel.setVisible(false);
        this.setSize(this.getSize().width, M);
        centerPanel();
    }
    public void resultsSize(){
        showIndexPanel.setSelected(false);
        indexPanel.setVisible(false);
        resultadosPanel.setVisible(true);
        this.setSize(this.getSize().width, L);
        centerPanel();
    }
    public void centerPanel(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2);
    }
    public void index(){
        if(txtRuta.getText().equals("")){
            showMessage("Ruta vacia", "No se ha indicado la ruta de la coleccion a indexar.\nPor favor, reintente.",0);
            return;
        }
        try {
            mensaje.setText("Indexando. Por favor espere");
            int cantidad=engine.index(txtRuta.getText().split(","));
            mensaje.setText("Se han indexado "+cantidad+" archivos nuevos.");
        } catch (IOException ex) {
            //showMessage("Error","Error io:\n"+ex.getMessage()+"\n"+ex.getStackTrace(),0);
            print(ex.getMessage());
        } catch (ParseException ex) {
            showMessage("Error","Error en el parseo:\n"+ex.getMessage(),0);
        }
    }
    public void search(String query){
        if(txtQuery.getText().equals("")){
            showMessage("Consulta vacia", "No se ha ingresado una consulta valida.\nPor favor, reintente.",0);
            return;
        }
        try {
            mensajeResultado.setText("");
            mensaje.setText("");
            ArrayList<ArrayList> docs=engine.search(query);
            mensajeResultado.setText("Se encontrado "+docs.size()+" archivos .");
            cargarResultados(tableResultados,docs);
            resultsSize();
        } catch (IOException ex) {
            normalSize();
            Logger.getLogger(SearchPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            normalSize();
            Logger.getLogger(SearchPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    } public void cargarResultados(JTable tabla,ArrayList<ArrayList> errores){
        String row[];
        int rowSize=0;
        if (!errores.isEmpty()) rowSize=errores.get(0).size();
        for (int i=(tabla.getRowCount())-1;i>-1 ;i--){
                DefaultTableModel fila = (DefaultTableModel) tabla.getModel();
                fila.removeRow(i);   
        }          
        for( int i = 0; i < errores.size(); i++ )
        {
            row = new String[rowSize];
            for (int j=0; j<rowSize;j++){
                
                row[j] = (String)(errores.get( i ).get(j));
            }
            DefaultTableModel fila = (DefaultTableModel) tabla.getModel();
            fila.addRow(row);
        }
        if(tabla.getRowCount()==0){normalSize();}
    
    }
    public void launchWebBrowser(String file)
    {
        System.out.println(file);
        File htmlFile = new File(file);
        try {
            Desktop.getDesktop().browse(htmlFile.toURI());
            System.out.println("Google Chrome launched!");
        } catch (IOException ex) {
            String msj="Los resultados no pudieron ser cargados por que ocurrio un error o el navegador no fue encontrado."
                    +"\n\nPor Favor busque los resultados manualmente en la ubicacion: \n"+file;
            System.out.println(msj);
            showMessage("Error al en la carga de la pagina",msj,0);
        }
    }
    public void getInfo(JTable table){
        DefaultTableModel fila = (DefaultTableModel) table.getModel();
        int row=table.getSelectedRow();
        String pagina= (String) fila.getValueAt(row, 1);
        String direccion= (String) fila.getValueAt(row, 2);
        String calificacion= (String) fila.getValueAt(row, 0);
        String s= "Pagina:\n\t"+pagina+"\nDireccion:\n\t"+direccion+"\n\nCalificacion: "+calificacion
                +"\n\nDesea abrir esta pagina en el navegador predeterminado?";
        if(this.showConfirmMessage("+Informacion",s )==0){
            //pagina=pagina.replaceAll("\"", "");
            launchWebBrowser(direccion);
        }
    }
    public ArrayList<String> getPaths(){
        JFileChooser chooser = new JFileChooser("C:/");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        //FileNameExtensionFilter filter = new FileNameExtensionFilter(
          //  "Carperas", "");
        //chooser.setFileFilter(filter);
        ArrayList<String> paths=new ArrayList();
        try{
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                for(File f:chooser.getSelectedFiles()){
                    paths.add(f.getCanonicalPath());
                }
                
            }
            return paths;
            
        }
        catch(Exception e){
            return new ArrayList();
        }
    }
    
    public void showMessage(String titulo,String descripcion,int icon){
        JOptionPane.showMessageDialog(this,descripcion,titulo, icon);
    }
    public int showConfirmMessage(String titulo,String descripcion){
        return JOptionPane.showConfirmDialog(this, descripcion, titulo, JOptionPane.OK_CANCEL_OPTION, 3);
    }
    public void print(String s){
        System.out.println(s);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        indexPanel = new javax.swing.JPanel();
        txtRuta = new javax.swing.JTextField();
        btnIndexar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        mensaje = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        buscar = new javax.swing.JButton();
        showIndexPanel = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        mensajeResultado = new javax.swing.JLabel();
        txtQuery = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        resultadosPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableResultados = new javax.swing.JTable();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        indexPanel.setBackground(new java.awt.Color(102, 102, 102));
        indexPanel.setMaximumSize(new java.awt.Dimension(347, 0));
        indexPanel.setName("Ruta de indexacion"); // NOI18N

        txtRuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRutaActionPerformed(evt);
            }
        });

        btnIndexar.setText("Indexar");
        btnIndexar.setContentAreaFilled(false);
        btnIndexar.setOpaque(true);
        btnIndexar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndexarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar Indice");
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setOpaque(true);
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        mensaje.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        mensaje.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Ruta");

        buscar.setText("buscar");
        buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout indexPanelLayout = new javax.swing.GroupLayout(indexPanel);
        indexPanel.setLayout(indexPanelLayout);
        indexPanelLayout.setHorizontalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(11, 11, 11)
                .addGroup(indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(indexPanelLayout.createSequentialGroup()
                        .addComponent(txtRuta, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(indexPanelLayout.createSequentialGroup()
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mensaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44)))
                .addGap(21, 21, 21)
                .addComponent(btnIndexar)
                .addGap(22, 22, 22))
        );
        indexPanelLayout.setVerticalGroup(
            indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, indexPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRuta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIndexar)
                    .addComponent(jLabel3)
                    .addComponent(buscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(indexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiar)
                    .addComponent(mensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        showIndexPanel.setBackground(new java.awt.Color(153, 153, 153));
        showIndexPanel.setText("Indexar");
        showIndexPanel.setContentAreaFilled(false);
        showIndexPanel.setOpaque(true);
        showIndexPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showIndexPanelActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(46, 77, 88));
        jPanel2.setForeground(new java.awt.Color(251, 251, 251));
        jPanel2.setMaximumSize(new java.awt.Dimension(347, 0));

        mensajeResultado.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        mensajeResultado.setForeground(new java.awt.Color(204, 204, 204));

        txtQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQueryActionPerformed(evt);
            }
        });

        btnBuscar.setText("Buscar");
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setOpaque(true);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Consulta");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar))
                    .addComponent(mensajeResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mensajeResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Resultados");

        tableResultados.setForeground(new java.awt.Color(0, 51, 102));
        tableResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Calificacion", "Pagina", "Ruta"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableResultados.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tableResultadosMouseMoved(evt);
            }
        });
        tableResultados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableResultadosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableResultadosMouseEntered(evt);
            }
        });
        jScrollPane4.setViewportView(tableResultados);
        if (tableResultados.getColumnModel().getColumnCount() > 0) {
            tableResultados.getColumnModel().getColumn(0).setMinWidth(120);
            tableResultados.getColumnModel().getColumn(0).setPreferredWidth(120);
            tableResultados.getColumnModel().getColumn(0).setMaxWidth(120);
            tableResultados.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        javax.swing.GroupLayout resultadosPanelLayout = new javax.swing.GroupLayout(resultadosPanel);
        resultadosPanel.setLayout(resultadosPanelLayout);
        resultadosPanelLayout.setHorizontalGroup(
            resultadosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultadosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultadosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultadosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );
        resultadosPanelLayout.setVerticalGroup(
            resultadosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultadosPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showIndexPanel)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(resultadosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(indexPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showIndexPanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(indexPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(resultadosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQueryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQueryActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        search(txtQuery.getText());
        
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRutaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRutaActionPerformed

    private void btnIndexarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndexarActionPerformed
        index();
    }//GEN-LAST:event_btnIndexarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        try {
            if(showConfirmMessage("Confirmar","Desea realmente limpiar el indice actual?")==0){
                engine.clean();
            }
        } catch (IOException ex) {
            showMessage("Error","Error al limpiar la coleccion:\n"+ex.getMessage(),0);
        }
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void showIndexPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showIndexPanelActionPerformed
        if (showIndexPanel.isSelected()){
            indexSize();
        }
        else{
            indexPanel.setVisible(false);
            normalSize();
            txtRuta.setText("");
        }
    }//GEN-LAST:event_showIndexPanelActionPerformed

    private void tableResultadosMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableResultadosMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_tableResultadosMouseMoved

    private void tableResultadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableResultadosMouseClicked
        if(evt.getClickCount()>1){
            getInfo(tableResultados);
        }
    }//GEN-LAST:event_tableResultadosMouseClicked

    private void tableResultadosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableResultadosMouseEntered

    }//GEN-LAST:event_tableResultadosMouseEntered

    private void buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarActionPerformed
        String paths=getPaths().toString();
        txtRuta.setText(paths.substring(1, paths.length()-1));
    }//GEN-LAST:event_buscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnIndexar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton buscar;
    private javax.swing.JPanel indexPanel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel mensaje;
    private javax.swing.JLabel mensajeResultado;
    private javax.swing.JPanel resultadosPanel;
    private javax.swing.JToggleButton showIndexPanel;
    private javax.swing.JTable tableResultados;
    private javax.swing.JTextField txtQuery;
    private javax.swing.JTextField txtRuta;
    // End of variables declaration//GEN-END:variables
}
