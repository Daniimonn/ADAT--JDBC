import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;

public class Ejercicio2 extends JFrame {

	private JPanel contentPane;
	private JButton btnInsertar, btnModificar, btnEliminar, btnLectura;
	private String consulta = "select * from productos";
	private DefaultTableModel modeloTabla;

	private String bd = "ejerciciojdbc";
	private String login = "root";
	private String pwd = "";
	private String url = "jdbc:mysql://localhost/" + bd;
	private Connection conexion;
	private JTable table;
	private JTextField txtProducto;
	private JTextField txtPrecio;

	public static void main(String[] args) {

		Ejercicio2 frame = new Ejercicio2();
		frame.setVisible(true);

	}

	/**
	 * Create the frame.
	 */
	public Ejercicio2() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				conexion();
				obtenerTabla();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnInsertar = new JButton("Insertar");
		btnInsertar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				insertar();
			}
		});
		btnInsertar.setBounds(841, 344, 89, 23);
		contentPane.add(btnInsertar);

		btnLectura = new JButton("Lectura");
		btnLectura.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				table.setModel(modeloTabla);
				obtenerTabla();
			}
		});
		btnLectura.setBounds(395, 495, 89, 23);
		contentPane.add(btnLectura);

		btnModificar = new JButton("Modificar");
		btnModificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				modificar();
			}
		});
		btnModificar.setBounds(985, 344, 89, 23);
		contentPane.add(btnModificar);

		btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eliminar();
			}
		});
		btnEliminar.setBounds(1111, 344, 89, 23);
		contentPane.add(btnEliminar);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(144, 165, 599, 292);
		contentPane.add(scrollPane);

		table = new JTable();
		modeloTabla = new DefaultTableModel(
				new Object[][] { { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null },
						{ null, null, null }, { null, null, null }, },
				new String[] { "New column", "New column", "New column" });
		scrollPane.setViewportView(table);

		txtProducto = new JTextField();
		txtProducto.setBounds(961, 253, 184, 20);
		contentPane.add(txtProducto);
		txtProducto.setColumns(10);

		txtPrecio = new JTextField();
		txtPrecio.setBounds(961, 298, 184, 20);
		contentPane.add(txtPrecio);
		txtPrecio.setColumns(10);

		JLabel lblDescripcionProducto = new JLabel("Producto:");
		lblDescripcionProducto.setBounds(881, 256, 83, 14);
		contentPane.add(lblDescripcionProducto);

		JLabel lblProducto = new JLabel("Precio:");
		lblProducto.setBounds(881, 301, 49, 14);
		contentPane.add(lblProducto);

		JLabel lblProductos = new JLabel("PRODUCTOS");
		lblProductos.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblProductos.setBounds(529, 30, 433, 65);
		contentPane.add(lblProductos);

	}

	public String getTxtProducto() {
		return txtProducto.getText();
	}

	public String getTxtPrecio() {
		return txtPrecio.getText();
	}

	public void conexion() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conexion = DriverManager.getConnection(url, login, pwd);
			if (conexion != null) {
				System.out.println("Conexión a la bd " + url + "...ok!");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Driver JDBC no econtrado");
			cnfe.printStackTrace();
		} catch (SQLException sqle) {
			System.out.println("Error al conectarse a la BD");
			sqle.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error General");
			e.printStackTrace();
		}
	}

	public void insertar() {
		try {
			String producto = getTxtProducto();
			String precio = getTxtPrecio();
			Double preciodb = Double.parseDouble(precio);
			String sql = "INSERT INTO productos (Producto, Precio) values (?,?);";
			PreparedStatement ps = conexion.prepareStatement(sql);
			ps.setString(1, producto);
			ps.setDouble(2, preciodb);
			ps.executeUpdate();
		} catch (SQLException s) {
			s.printStackTrace();
		}
	}

	public void modificar() {
		try {
			String producto = getTxtProducto();
			String precio = getTxtPrecio();
			Double preciodb = Double.parseDouble(precio);
			Integer p = Integer.parseInt(table.getValueAt(table.getSelectedRow(),0).toString());
			String sql = "UPDATE productos set Producto=?, Precio=? where ID=?;";
			PreparedStatement ps = conexion.prepareStatement(sql);
			ps.setInt(3, p);
			ps.setDouble(2, preciodb);
			ps.setString(1, producto);
			ps.executeUpdate();
		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println(s.getMessage());
		}
	}

	public void eliminar() {
		try {
			Integer p = Integer.parseInt(table.getValueAt(table.getSelectedRow(),0).toString());
			String sql = "DELETE FROM productos where ID=?";
			PreparedStatement ps = conexion.prepareStatement(sql);
			ps.setInt(1, p);
			ps.executeUpdate();
		} catch (SQLException s) {
			s.printStackTrace();
		}
	}

	public void obtenerTabla() {

		int numeroColumnas = Columnas(consulta);
		int numerosFilas = Filas(consulta);
		String[] header = new String[numeroColumnas];

		Object[][] content = new Object[numerosFilas][numeroColumnas];
		PreparedStatement pstmt;
		try {
			pstmt = conexion.prepareStatement(consulta);
			ResultSet rset = pstmt.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rset.getMetaData();
			for (int i = 0; i < numeroColumnas; i++) {

				header[i] = rsmd.getColumnName(i + 1);
			}
			int row = 0;
			while (rset.next()) {
				for (int col = 1; col <= numeroColumnas; col++) {
					content[row][col - 1] = rset.getString(col);
				}
				row++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		modeloTabla = new DefaultTableModel(content, header);

		setModeloTabla(modeloTabla);

	}

	public DefaultTableModel getModeloTabla() {
		return modeloTabla;
	}

	public void setModeloTabla(DefaultTableModel modeloTabla) {
		this.modeloTabla = modeloTabla;
	}

	private int Columnas(String sql) {
		int num = 0;
		try {
			PreparedStatement pstmt = conexion.prepareStatement(sql);
			ResultSet rset = pstmt.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rset.getMetaData();
			num = rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	private int Filas(String sql) {
		int numFilas = 0;
		try {
			PreparedStatement pstmt = conexion.prepareStatement(sql);
			ResultSet rset = pstmt.executeQuery();
			while (rset.next())
				numFilas++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numFilas;
	}
}