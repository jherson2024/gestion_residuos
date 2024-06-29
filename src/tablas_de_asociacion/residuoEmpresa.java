package tablas_de_asociacion;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class residuoEmpresa extends JFrame {
	private JPanel panel;
	private List<JLabel> listLabels=new ArrayList<>();
    private List<JTextField> listComponents= new ArrayList<>();
    private String nameEntidad="residuoEmpresa";          //##
    private static String[] strLabels= {"Código","Cod. Empresa Productora","Código Residuo","Estado de Registro"};
    private String[] listAtr= {"ResEmpCod","ResEmpEmpCod","ResEmpResCod","ResEmpEstReg"};   //##
    private int[]typeNum= {0,1,2}; 
    private int[]typeFlo= {};
    private int[]typeBoolean= {};
    private int[]typeDate= {};
    private int[]foreyKey= {1,2};
    private String[][]tablasForaneas= {{"disposicionesFinales","DisFinCod","DisFinNom"},{"residuo","ResCod","ResNom"}};
    private List<DefaultListModel> listsModels=new ArrayList<>();
    private List<List>listCodes=new ArrayList<>();
    private List<List>listForeign=new ArrayList<>();
    private List<JButton>showListsButtons=new ArrayList<>();
    private List<String>datos=new ArrayList<>();
    private JCheckBox estadoCheckBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean CarFlaAct = false;
    private List<Integer> codigos=new ArrayList<>();
    private List<String> nombres=new ArrayList<>();
    private List<JButton> botones=new ArrayList<>();
    private String letraEstado="";
    private Boolean grillaSelect=true;
    private int numeroCampos=listAtr.length;
    private int x=600,y=370;
    private final String DB_URL = "jdbc:mysql://localhost:3306/gestion_residuos?useSSL=false";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1234";
    private Connection conn;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(residuoEmpresa::new);
    }

    public residuoEmpresa() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        setTitle("Mantenimiento tablas referenciales");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        add(panel);
        placeComponents(panel);
        setSize(x+40*(numeroCampos-4), y+30*(listComponents.size()-2));
        setLocationRelativeTo(null);
        setVisible(true);
        listComponents.get(0).requestFocusInWindow();  
        protegerInputFields();
        loadDataFromDatabase();
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);
//        panel.setBackground(Color.decode("#c4b9ba"));
        Color colorButton=Color.decode("#f2a080");
        JLabel label = new JLabel("<html><b>" + sepYConvertirAMayusculas(nameEntidad) + "</b></html>");
        panel.add(label);
        label.setSize(label.getPreferredSize());
        int x0 = (x - label.getWidth()) / 2-15; 
        label.setBounds(x0+20*(numeroCampos-4), 0, label.getWidth(), label.getHeight());
        
        for(int i=0;i<numeroCampos-1;i++) {
        	JLabel label_=new JLabel(strLabels[i]+":");
        	JTextField textField=new JTextField(20);
        	listLabels.add(label_);
        	listComponents.add(textField);
        	panel.add(label_);  
            panel.add(textField);
        }
        for (int i = 0; i < listComponents.size()-1; i++) {
        	Boolean saltar=false;
        	for(int indice:foreyKey) {
        		if(indice==i) {
        			saltar=true;
        		}
        	}
        	if(saltar==false) {
             JTextField textField = listComponents.get(i);
             final int currentIndex = i;
             textField.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     listComponents.get(currentIndex + 1).requestFocusInWindow();
                 }
             });
        	}
        }
        for (int i = 0; i < typeBoolean.length; i++) {
            JTextField textField = listComponents.get(typeBoolean[i]);
            textField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    char keyChar = e.getKeyChar();
                    if (keyChar == 't' || keyChar == 'T') {
                    	textField.setText("1");
                    } else if (keyChar == 'f' || keyChar == 'F') {
                    	textField.setText("0");
                    }
                }
            });
        }
        listComponents.get(0).addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		char c = e.getKeyChar();
                if (c == 'a' || c == 'A') {
                    int nextAvailableCode = getNextAvailableCode();
                    JTextField componente=(JTextField)e.getSource();
                    componente.setText(String.valueOf(nextAvailableCode));
                    e.consume();
                }
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); 
                }
        	}
        });
        int a=10,b=50,c=150,d=25,a2=170;
        for(int i=0;i<foreyKey.length;i++) {
        	JButton showListButton=new JButton("v");
        	showListsButtons.add(showListButton);
        	showListButton.setBounds(a2+c,b+30*(foreyKey[i]-1),18,18); 
            showListButton.setBackground(Color.white);
            showListButton.setMargin(new Insets(0, 0, 0, 0));
            panel.add(showListButton); 
            JPopupMenu methodsPopupMenu = new JPopupMenu();
            DefaultListModel listModel=new DefaultListModel();
            listsModels.add(listModel);
            JList<String>  list = new JList<>(listsModels.get(i));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setVisibleRowCount(20); 
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(200, 100)); 
            methodsPopupMenu.add(scrollPane);
            int ig=i;
            List<Integer> codes = new ArrayList<>();
            List<String> cf = new ArrayList<>();
            listCodes.add(codes);
            listForeign.add(cf);
            fetchMethodsFromDatabase(ig);
            showListButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listModel.clear();
                    codes.clear();
                    cf.clear();
                    fetchMethodsFromDatabase(ig);
                    methodsPopupMenu.show(showListButton, 0, showListButton.getHeight());
                }
            });
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                	int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex != -1) {
                    	String selected = (String) listForeign.get(ig).get(selectedIndex);
//                        String selected = String.valueOf(listCodes.get(ig).get(selectedIndex));
                        listComponents.get(foreyKey[ig]).setText(selected);
                        methodsPopupMenu.setVisible(false);
                    }
                }
            });
            listComponents.get(foreyKey[i]).addKeyListener(new KeyAdapter() {
            	private DefaultListModel<String> modeloLista=listsModels.get(ig);
            	private int indiceCoincidente=-1;
            	public void keyReleased(KeyEvent e) {
            		JTextField textField = (JTextField) e.getSource();
            		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {	
            			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            				String textoIngresado = textField.getText(); 
                            String palabraCoincidente = null;
                            int indiceCoincidente = -1;
                            for (int i = 0; i < modeloLista.getSize(); i++) {
                                String palabra = modeloLista.getElementAt(i);
                                if (palabra.toLowerCase().startsWith(textoIngresado.toLowerCase())) {
                                    if (palabraCoincidente == null) {
                                        palabraCoincidente = palabra;
                                        indiceCoincidente = i; 
                                    } else {
                                        return;
                                    }
                                }
                            }
                            if (indiceCoincidente != -1) {
                                textField.setText((String) listCodes.get(ig).get(indiceCoincidente));
                            }
                            if(foreyKey[ig]+1<7) {
                            	listComponents.get(foreyKey[ig]+1).requestFocusInWindow();	
                            }
            			}
                        return;
            		}
                    String textoIngresado = textField.getText(); 
                    String palabraCoincidente = null;
                    indiceCoincidente = -1;
                    for (int i = 0; i < modeloLista.getSize(); i++) {
                        String palabra = modeloLista.getElementAt(i);
                        if (palabra.toLowerCase().startsWith(textoIngresado.toLowerCase())) {
                            if (palabraCoincidente == null) {
                                palabraCoincidente = palabra;
                                indiceCoincidente = i; 
                            } else {
                                return;
                            }
                        }
                    }
                    if (palabraCoincidente != null) {
                        textField.setText(palabraCoincidente);
                    }
                }
            });
        }
        int size=listComponents.size()-2;
        JLabel estadoLabel = new JLabel("Estado Registro:");
        estadoLabel.setBounds(10, 110+30*size-30, 120, 25);
        panel.add(estadoLabel);
        estadoCheckBox = new JCheckBox() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                if (isSelected()) { 
                    g.setColor(Color.BLACK); 
                    g.drawString(letraEstado, 2, getHeight() / 2 + 5);
                }
            }
        };
        estadoCheckBox.setEnabled(false);	
        estadoCheckBox.setSelected(true);
        estadoCheckBox.setBounds(140, 110+30*size-30, 50, 25);
        panel.add(estadoCheckBox);
        String[] strings=new String[numeroCampos];
        for(int i=0;i<numeroCampos;i++) {
        	strings[i]=strLabels[i];
        }
        tableModel = new DefaultTableModel(strings, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setBounds(10, 140+30*size-30, 560+40*(numeroCampos-4), 150);
        panel.add(scrollPane1);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	int row = table.getSelectedRow();
                if (e.getClickCount() == 2&&grillaSelect==true) {
                    int column = table.getSelectedColumn();
                    Object value = table.getValueAt(row, column);
                    String message = "<html><body style='width: 200px;'>" + (value != null ? value.toString() : "Celda vacía") + "</body></html>";
                    JOptionPane.showMessageDialog(panel, message, "Contenido de la celda", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        List<Integer>array=new ArrayList<Integer>();
        for(int n:typeNum) array.add(n);
        for(int n:typeFlo) array.add(n);
        for(int n:typeDate) array.add(n);
        for(int n:typeBoolean) array.add(n);
        for(int i=0;i<array.size();i++) {
        	table.getColumnModel().getColumn(array.get(i)).setCellRenderer(centerRenderer);
        	table.getColumnModel().getColumn(array.get(i)).setPreferredWidth(10);
        }
        table.getColumnModel().getColumn(numeroCampos-1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(numeroCampos-1).setPreferredWidth(20);
        int margen=60;
        JButton adicionarButton = new JButton("Adicionar");
        size=size-1;
        adicionarButton.setBounds(10+margen+20*(numeroCampos-4), 300+30*size, 100, 25);
        panel.add(adicionarButton);
        adicionarButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	clearInputFields();
            	listComponents.get(0).requestFocusInWindow();
            	habilitarInputFields();
                cambiarLetra("A");
                CarFlaAct = true;
                seleccionar(false);
                congelar();
            }
        });
        botones.add(adicionarButton);
        JButton modificarButton = new JButton("Modificar");
        modificarButton.setBounds(120+margen+20*(numeroCampos-4), 300+30*size, 100, 25);
        panel.add(modificarButton);
        modificarButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                	if(!tableModel.getValueAt(selectedRow, numeroCampos-1).toString().equals("*")) {
                		rellenarInputFields();	
                        protegerCodigo();
                        CarFlaAct = true;
                        grillaSelect=true;
                        congelar();
                	}else {
                		JOptionPane.showMessageDialog(panel, "El registro ya esta eliminado.", "Error", JOptionPane.ERROR_MESSAGE);	
                	}
                }else {
                	JOptionPane.showMessageDialog(panel, "Primero seleccione un registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        botones.add(modificarButton);
        JButton eliminarButton = new JButton("Eliminar");
        eliminarButton.setBounds(230+margen+20*(numeroCampos-4), 300+30*size, 100, 25);
        panel.add(eliminarButton);
        eliminarButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                	if(!tableModel.getValueAt(selectedRow, numeroCampos-1).toString().equals("*")) {
                		rellenarInputFields();
                        protegerInputFields();
                        letraEstado="*";
                        CarFlaAct = true;
                        grillaSelect=true;
                        congelar();
                	}else {
                		JOptionPane.showMessageDialog(panel, "El registro ya esta eliminado.", "Error", JOptionPane.ERROR_MESSAGE);	
                	}
                }
                else {
                	JOptionPane.showMessageDialog(panel, "Primero seleccione un registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        botones.add(eliminarButton);
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setBounds(340+margen+20*(numeroCampos-4), 300+30*size, 100, 25);
        panel.add(cancelarButton);
        cancelarButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cancelar();
			}
        });
        JButton inactivarButton = new JButton("Inactivar");
        inactivarButton.setBounds(10+margen+20*(numeroCampos-4), 330+30*size, 100, 25);
        panel.add(inactivarButton);
        inactivarButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                	String estado="";
                	estado=tableModel.getValueAt(selectedRow, numeroCampos-1).toString();
                	switch (estado){
                	case "I":
                		JOptionPane.showMessageDialog(panel, "El registro ya estó inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
                		break;
                	case "A":
                		rellenarInputFields();
                        cambiarLetra("I");
                        protegerInputFields();
                        CarFlaAct = true;
                        grillaSelect=true;
                        congelar();
                        break;
                	case "*":
                		JOptionPane.showMessageDialog(panel, "No se puede modificar registros eliminados.", "Error", JOptionPane.ERROR_MESSAGE);
                		break;
                	default:break;
                	}
                }else {
                	JOptionPane.showMessageDialog(panel, "Primero seleccione un registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        	}
        });
        botones.add(inactivarButton);
        JButton reactivarButton = new JButton("Reactivar");
        reactivarButton.setBounds(120+margen+20*(numeroCampos-4), 330+30*size, 100, 25);
        panel.add(reactivarButton);
        reactivarButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                	String estado="";
                	estado=tableModel.getValueAt(selectedRow, numeroCampos-1).toString();
                	switch (estado){
                	case "I":
                		rellenarInputFields();
                        cambiarLetra("A");
                        protegerInputFields();
                        CarFlaAct = true;
                        grillaSelect=true;
                        congelar();
                		break;
                	case "A":
                		JOptionPane.showMessageDialog(panel, "El registro ya estó activo.", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                	case "*":
                		JOptionPane.showMessageDialog(panel, "No se puede modificar registros eliminados.", "Error", JOptionPane.ERROR_MESSAGE);
                		break;
                	default:break;
                	}
                }else {
                	JOptionPane.showMessageDialog(panel, "Primero seleccione un registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        botones.add(reactivarButton);
        JButton actualizarButton = new JButton("Actualizar");
        actualizarButton.setBounds(230+margen+20*(numeroCampos-4), 330+30*size, 100, 25);
        panel.add(actualizarButton);
        actualizarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (CarFlaAct) {
	                if (grillaSelect) {
	                	actualizar();
	                } else {
	                	insertar();
	                }
	                if(!CarFlaAct) {
	                	clearInputFields();
	                    protegerInputFields();
	                    grillaSelect=true;
	                    descongelar();
	                    seleccionar(true); 	
	                }
	            }else {
	            	JOptionPane.showMessageDialog(panel, "No a hecho ningun cambio.", "Error", JOptionPane.ERROR_MESSAGE);
	            }
			}
        });
        JButton salirButton = new JButton("Salir");
        salirButton.setBounds(340+margen+20*(numeroCampos-4), 330+30*size, 100, 25);
        panel.add(salirButton);
        salirButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	try {
    				conn.close();
    			} catch (SQLException e1) {
    				e1.printStackTrace();
    			}
            	dispose();
            }
        });
        salirButton.setBackground(colorButton);
        reactivarButton.setBackground(colorButton);
        inactivarButton.setBackground(colorButton);
        cancelarButton.setBackground(colorButton);
        eliminarButton.setBackground(colorButton);
        modificarButton.setBackground(colorButton);
        adicionarButton.setBackground(colorButton);
        actualizarButton.setBackground(colorButton);
        position();
    }
    private void position() {
    	int a=10,b=20,c=150,d=25,a2=170;
    	int size=listComponents.size();
    	for(int i=0;i<size;i++) {
    		JTextField componente=listComponents.get(i);
    		JLabel label=listLabels.get(i);
    		componente.setBounds(a2,b+30*i,c,d);
    		label.setBounds(a,b+30*i,c,d);
    	}
    	listComponents.get(0).setBounds(120, 20, 160, 25);
    }

    public String sepYConvertirAMayusculas(String texto) {
        StringBuilder palabrasEnMayusculas = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (Character.isUpperCase(caracter) && i > 0) {
                palabrasEnMayusculas.append(' ');
            }
            palabrasEnMayusculas.append(Character.toUpperCase(caracter));
        }
        return palabrasEnMayusculas.toString();
    }
    
    private int getNextAvailableCode() {
    	Set<Integer> numberSet = new HashSet<>(codigos);
        int i = 0;
        while (true) {
            if (!numberSet.contains(i)) {
                return i;
            }
            i++;
        }
    }	
    private void protegerInputFields() {
    	listComponents.get(0).setEnabled(false);
    	for(JButton showListButton:showListsButtons) {
    		showListButton.setEnabled(false);
    	}
    	for(JTextField j:listComponents) {
    		j.setEnabled(false);
    	}
    };
    private void protegerCodigo() {
    	for(JButton showListButton:showListsButtons) {
    		showListButton.setEnabled(true);
    	}
    	for(JTextField j:listComponents) {
    		j.setEnabled(true);
    	}
    	listComponents.get(0).setEnabled(false);
    };
    private void habilitarInputFields() {
    	listComponents.get(0).setEnabled(true);
    	for(JButton showListButton:showListsButtons) {
    		showListButton.setEnabled(true);
    	}
    	for(JTextField j:listComponents) {
    		j.setEnabled(true);
    	}
    };
    private void clearInputFields() {
        for(JTextField j:listComponents) {
    		j.setText("");
    	}
        cambiarLetra("");
    }
    private void rellenarInputFields() {
    	int selectedRow = table.getSelectedRow();
        for(int i=0;i<listComponents.size();i++) {
        	Object dato_=tableModel.getValueAt(selectedRow, i);
        	if(dato_!=null) {
        		listComponents.get(i).setText(dato_.toString());
        	}
        }
        cambiarLetra(tableModel.getValueAt(selectedRow, numeroCampos-1).toString());
    }
    private void cambiarLetra(String letra) {
    	letraEstado=letra;
    	estadoCheckBox.repaint();
    }
    private void congelar() {
    	for(JButton boton:botones) {
    		boton.setEnabled(false);
    	}
    	table.setFocusable(false);
        table.setEnabled(false);
    }
    private void descongelar() {
    	for(JButton boton:botones) {
    		boton.setEnabled(true);
    	}
    	table.setFocusable(true);
        table.setEnabled(true);
    }
    private void seleccionar(Boolean boo) {
    	grillaSelect=false;
        table.setRowSelectionAllowed(boo);
        table.setRowSelectionAllowed(boo);
    }
    private void loadDataFromDatabase() {
    	int[]foreyKey= {3,6};
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM "+nameEntidad)) {
            while (rs.next()) {
            	Object[] object=new Object[numeroCampos];
            	for(int i=0;i<numeroCampos;i++) {
            		object[i]=rs.getString(i+1);
            	}
                tableModel.addRow(object);   //## 
                codigos.add(rs.getInt(1));
                nombres.add(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void insertar() {
    	Boolean insercion=true;
    	if(listComponents.get(0).getText().length()!=0) {
        if(!camposValidos()) {
			insercion=false;
		}
        if(insercion==true) {
    	if(!codigos.contains(Integer.parseInt(listComponents.get(0).getText()))){
    			try {
    				PreparedStatement pstmt = null;
            		String sql="INSERT INTO "+nameEntidad+" (";
            		for(int i=0;i<listAtr.length;i++) {
            			if(i==listAtr.length-1) {
            				sql+=listAtr[i];
            				break;
            			}
            			sql+=listAtr[i]+",";
            		}
            		sql+=")VALUES ("; 
            		for(int i=0;i<listAtr.length;i++) {
            			if(i==listAtr.length-1) {
            				sql+="?)";
                            break;
            			}
            			sql+="?,";
            		}
    		     	pstmt = conn.prepareStatement(sql);      // ##
                    for(int i=0;i<listComponents.size();i++) {
                    	String dato=listComponents.get(i).getText();
                    	boolean en=false;
                    	for(int num:typeNum) {
                    		if(num==i) {
                    			pstmt.setInt(i+1, Integer.parseInt(dato)); 
                    			en=true;
                    			break;
                        	}
                    	}
                    	if(en==false) {
                    		pstmt.setString(i+1, dato);
                    	}
                    }
                    pstmt.setString(numeroCampos, "A");
                    pstmt.executeUpdate();
                    Object[]object=new Object[numeroCampos];
                    for(int i=0;i<listComponents.size();i++) {
                    	object[i]=listComponents.get(i).getText();
                    	if(i==listComponents.size()-1) {
                    		object[i+1]=letraEstado;
                    	}
                    }
                    tableModel.addRow(object);
                	codigos.add(Integer.parseInt(listComponents.get(0).getText()));
                	CarFlaAct=false;
                	// Enfocar la vista en la celda seleccionada
                	int rowCount = table.getRowCount();
                	table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
                	Rectangle rect = table.getCellRect(rowCount - 1, 0, true);
                	table.scrollRectToVisible(rect);
        		} catch (SQLException e1) {
        			e1.printStackTrace();
        		}
    	
    	}else {
    		JOptionPane.showMessageDialog(panel, "Ya hay un registro con el mismo código.", "Error", JOptionPane.ERROR_MESSAGE);
    		listComponents.get(0).requestFocusInWindow();
    	}
        }
    	}
    }
    private void actualizar() {
    	boolean actualizar=false;
    	if(letraEstado.equals("*")) {
    		int confirm = JOptionPane.showConfirmDialog(panel,"¿Desea eliminar el registro?","Confirmación de Eliminación",JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION||!letraEstado.equals("*")) {
        	  actualizar=true;
            }else {
            	cancelar();
            }
        }else {
        	actualizar=true;
        }
    	if(!camposValidos()) {
			actualizar=false;
		}
    	int selectedRow = table.getSelectedRow();
    	if(actualizar) {
    		try {
    			PreparedStatement pstmt = null;
        		String sql = "UPDATE " + nameEntidad + " SET ";      // ##
    			for(int i=0;i<listAtr.length-1;i++) {
    				if(i==listAtr.length-2) {
    					sql+=listAtr[i+1]+"=?";
    					break;
    				}
    				sql+=listAtr[i+1]+"=?,";
    			}
        	    sql+="WHERE " + listAtr[0] + " = ?";
        	    pstmt = conn.prepareStatement(sql);
        	    for(int i=1;i<numeroCampos-1;i++) {
                	boolean en=false;
                	String dato=listComponents.get(i).getText();
                	for(int num:typeNum) {
                		if(num==i) {
                			pstmt.setInt(i, Integer.parseInt(dato)); 
                			en=true;
                			break;
                    	}
                	}
                	if(en==false) {
                		pstmt.setString(i, dato);
                	}
                }
            	pstmt.setString(numeroCampos-1, letraEstado);
        	    pstmt.setInt(numeroCampos, Integer.parseInt(listComponents.get(0).getText()));
        	    pstmt.executeUpdate();
                for(int i=0;i<numeroCampos-2;i++) {
                	tableModel.setValueAt(listComponents.get(i+1).getText(), selectedRow, i+1);
                }
                tableModel.setValueAt(letraEstado, selectedRow, numeroCampos-1);
                
                CarFlaAct=false;
    		} catch (SQLException e1) {
    			e1.printStackTrace();
    		}
    	}
    }
    private void cancelar() {
    	clearInputFields();
        protegerInputFields();
        CarFlaAct = false;
        descongelar();
        seleccionar(true);
        grillaSelect=true;
        panel.requestFocusInWindow();
    }
    private int obtenerCodigo(int j) {
    	String[]tablaForanea=tablasForaneas[j];
    	String entidad=tablaForanea[0];
    	String atr1=tablaForanea[1];
    	String atr2=tablaForanea[2];
    	String valor=listComponents.get(foreyKey[j]).getText();
    	try {
            String query = "SELECT "+atr1+" FROM "+entidad+" WHERE "+atr2+" = "+"'"+valor+"'";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
            	if (rs.next()) {
                    return rs.getInt(atr1); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return -1;
    }
    private String obtenerValor(int j, String valor) {
    	if(valor==null) {
    		return "";
    	}
    	String[]tablaForanea=tablasForaneas[j];
    	String entidad=tablaForanea[0];
    	String cod=tablaForanea[1];
    	String des=tablaForanea[2];
    	try {
            String query = "SELECT "+des+" FROM "+entidad+" WHERE "+cod+" = "+"'"+valor+"'";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
            	if (rs.next()) {
                    return rs.getString(des); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return "-a";
    }
    private void fetchMethodsFromDatabase(int ig) {
        try {
            String query = "SELECT "+tablasForaneas[ig][1]+", "+tablasForaneas[ig][2]+" FROM "+tablasForaneas[ig][0]+" ORDER BY "+tablasForaneas[ig][2]+" ASC";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String methodCode = rs.getString(1);
                    String methodName = rs.getString(2);
                    listCodes.get(ig).add(methodCode);
                    listForeign.get(ig).add(methodCode);
                    listsModels.get(ig).addElement(methodName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean inputsVacios() {
    	String mensaje="Falta rellenar estos campos: \n";
    	List<String>lista=new ArrayList<String>();
    	Boolean respuesta=false;
    	for(int i=0;i<listComponents.size();i++) {
    		JTextField componente=listComponents.get(i);
    		if(componente.getText().equals("")) {
    			lista.add(listLabels.get(i).getText());
    			mensaje+=strLabels[i]+"\n";
    			respuesta=true;
    			componente.requestFocusInWindow();
    		}
    	}
    	if(respuesta==true) {
    		JOptionPane.showMessageDialog(panel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    	}
        return respuesta;
    }
    public boolean esEntero(String str) {
        try {
            Double.parseDouble(str);
            int numero = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean esNumero(String cadena) {
        if (cadena == null || cadena.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean esUnoCero(String cadena) {
    	if (cadena == null) {
            return false;
        }
        return cadena.equals("1") || cadena.equals("0");
    }
    public static boolean esFecha(String cadena) {
    	String FORMATO_FECHA = "yyyy-MM-dd";
        if (cadena == null || cadena.isEmpty()) {
            return false;
        }
        SimpleDateFormat formatoFecha = new SimpleDateFormat(FORMATO_FECHA);
        formatoFecha.setLenient(false); 
        try {
            Date fecha = formatoFecha.parse(cadena);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private boolean camposValidos() {
    	String mensaje="";
    	Boolean respuesta=true;
    	//type ""
    	if(inputsVacios()) {
         	respuesta=false;
        }
    	//type num
    	if(respuesta==true) {
    		mensaje+="Ingrese un número entero en: \n";
        	for(int num:typeNum) {
        		if(!esEntero(listComponents.get(num).getText())) {
        			mensaje+=strLabels[num]+"\n";
        			respuesta=false;
        		}
        	}
    	}
    	if(respuesta==true) {
    		mensaje="Ingrese un número válido en: \n";
        	for(int num:typeFlo) {
        		if(!esNumero(listComponents.get(num).getText())) {
        			mensaje+=strLabels[num]+"\n";
        			respuesta=false;
        		}
        	}
    	}
    	if(respuesta==true) {
    		mensaje="Ingrese 1 o 0 en: \n";
        	for(int num:typeBoolean) {
        		if(!esUnoCero(listComponents.get(num).getText())) {
        			mensaje+=strLabels[num]+"\n";
        			respuesta=false;
        		}
        	}
    	}
    	if(respuesta==true) {
    		mensaje="Ingrese una fecha válida en: \n";
        	for(int num:typeDate) {
        		if(!esFecha(listComponents.get(num).getText())) {
        			mensaje+=strLabels[num]+"\n";
        			respuesta=false;
        		}
        	}
    	}
    	if(respuesta==false) {
    		JOptionPane.showMessageDialog(panel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    		return respuesta;
    	}else{
    	 mensaje="Dato no válido en:\n ";
    	 for(int j=0;j<foreyKey.length;j++) {
    		 JTextField componente=listComponents.get(foreyKey[j]);
    		 if(obtenerValor(j,componente.getText()).equals("-a")) {
    			 mensaje+=strLabels[foreyKey[j]]+"\n";
    			 respuesta=false;
    			 componente.requestFocusInWindow();
    		 }
     	 }
     	 if(respuesta==false) {
     		 JOptionPane.showMessageDialog(panel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    	 }
    	 return respuesta;
    	}
    }
}
