package si2425_22;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaInicio extends JFrame {
    private JButton bAbrirFormulario;

    public VentanaInicio() {
        setTitle("Ventana de Inicio");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel lblMensaje = new JLabel("Bienvenido al sistema de envíos de artículos");
        add(lblMensaje);

        bAbrirFormulario = new JButton("Abrir formulario de envío");
        add(bAbrirFormulario);

        bAbrirFormulario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	NuevoEnvio ventanaFormulario = new NuevoEnvio();
                ventanaFormulario.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaInicio().setVisible(true);
        });
    }
}