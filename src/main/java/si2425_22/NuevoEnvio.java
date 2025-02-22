package si2425_22;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NuevoEnvio extends JFrame {
    private JTextField txtTitulo, txtPalabrasClave, txtArticulo, txtNombre, txtCorreo, txtOrganizacion, txtGrupoInvst, txtNumArticulo;
    private JTextArea txtResumen;
    private JTable tablaAutores;
    private DefaultTableModel modeloTabla;
    private JButton btnAnadirAutor, btnEnviar;
    private Set<String> articulos = new HashSet<>();

    public NuevoEnvio() {
        setTitle("Nuevo envío de artículo");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Campos del formulario
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setBounds(20, 20, 100, 25);
        add(lblTitulo);
        txtTitulo = new JTextField();
        txtTitulo.setBounds(120, 20, 250, 25);
        add(txtTitulo);

        JLabel lblPalabrasClave = new JLabel("Palabras clave:");
        lblPalabrasClave.setBounds(20, 50, 100, 25);
        add(lblPalabrasClave);
        txtPalabrasClave = new JTextField();
        txtPalabrasClave.setBounds(120, 50, 250, 25);
        add(txtPalabrasClave);

        JLabel lblResumen = new JLabel("Resumen:");
        lblResumen.setBounds(20, 80, 100, 25);
        add(lblResumen);
        txtResumen = new JTextArea();
        txtResumen.setBounds(120, 80, 250, 80);
        add(txtResumen);

        JLabel lblArticulo = new JLabel("Artículo:");
        lblArticulo.setBounds(20, 170, 100, 25);
        add(lblArticulo);
        txtArticulo = new JTextField();
        txtArticulo.setBounds(120, 170, 250, 25);
        add(txtArticulo);

        // Tabla de autores
        JLabel lblListaAutores = new JLabel("Lista de autores:");
        lblListaAutores.setBounds(20, 200, 150, 25);
        add(lblListaAutores);

        String[] columnas = {"Nombre", "Correo", "Organización", "Grupo Invst"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaAutores = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaAutores);
        scrollPane.setBounds(20, 230, 350, 120);
        add(scrollPane);

        btnAnadirAutor = new JButton("Añadir");
        btnAnadirAutor.setBounds(390, 160, 100, 25);
        add(btnAnadirAutor);

        // Campos para el autor
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(400, 20, 100, 25);
        add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(500, 20, 250, 25);
        add(txtNombre);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(400, 50, 100, 25);
        add(lblCorreo);
        txtCorreo = new JTextField();
        txtCorreo.setBounds(500, 50, 250, 25);
        add(txtCorreo);

        JLabel lblOrganizacion = new JLabel("Organización:");
        lblOrganizacion.setBounds(400, 80, 100, 25);
        add(lblOrganizacion);
        txtOrganizacion = new JTextField();
        txtOrganizacion.setBounds(500, 80, 250, 25);
        add(txtOrganizacion);

        JLabel lblGrupoInvst = new JLabel("Grupo Invst:");
        lblGrupoInvst.setBounds(400, 110, 100, 25);
        add(lblGrupoInvst);
        txtGrupoInvst = new JTextField();
        txtGrupoInvst.setBounds(500, 110, 250, 25);
        add(txtGrupoInvst);

        btnEnviar = new JButton("Enviar");
        btnEnviar.setBounds(550, 360, 100, 25);
        add(btnEnviar);

        JLabel lblNumArticulo = new JLabel("Nº de artículo:");
        lblNumArticulo.setBounds(400, 360, 100, 25);
        add(lblNumArticulo);
        txtNumArticulo = new JTextField();
        txtNumArticulo.setBounds(500, 360, 40, 25);
        txtNumArticulo.setEditable(false);
        add(txtNumArticulo);

        // Acción para añadir autores
        btnAnadirAutor.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String organizacion = txtOrganizacion.getText();
            String grupoInvst = txtGrupoInvst.getText();

            if (nombre.isEmpty() || correo.isEmpty() || organizacion.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos del autor deben estar llenos.");
                return;
            }

            if (autorExisteEnDB(correo)) {
                JOptionPane.showMessageDialog(null, "El correo ya está registrado en la base de datos.");
                return;
            }

            String[] autorData = {nombre, correo, organizacion, grupoInvst};
            modeloTabla.addRow(autorData);
            insertarAutorEnDB(nombre, correo, organizacion, grupoInvst);

            txtNombre.setText("");
            txtCorreo.setText("");
            txtOrganizacion.setText("");
            txtGrupoInvst.setText("");
        });
    }

    // Método para verificar si el autor ya existe en la base de datos
    private boolean autorExisteEnDB(String correo) {
        String sql = "SELECT COUNT(*) FROM autores WHERE correo = ?";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, correo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el autor: " + e.getMessage());
        }
        return false;
    }

    // Método para insertar el autor en la base de datos
    private void insertarAutorEnDB(String nombre, String correo, String organizacion, String grupoInvst) {
        String sql = "INSERT INTO autores (nombre, correo, organizacion, grupo_invst) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, correo);
            pst.setString(3, organizacion);
            pst.setString(4, grupoInvst);
            pst.executeUpdate();
            System.out.println("Autor insertado en la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al insertar el autor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NuevoEnvio().setVisible(true));
    }
}
