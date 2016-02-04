package pt.ipb.sd;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	public static Peer peer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				peer = new Peer();
				try {
					peer.start();
				} catch (Exception e) {
					System.out.println("ERROR: Could not start channel!\nException: " + e);
				}
				
				try {
					MainFrame mainFrame = new MainFrame();
					mainFrame.setVisible(true);
				} catch (Exception e) {
					System.out.println("ERROR: Could not create frame.\nException: " + e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Ricart-Agrawala (Peer)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnRequest = new JButton("REQUEST");
		btnRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				peer.request();				
			}
		});
		btnRequest.setBounds(105, 199, 142, 51);
		contentPane.add(btnRequest);
		
		JLabel lblState = new JLabel("STATE");
		lblState.setFont(new Font("Garamond", Font.PLAIN, 16));
		lblState.setBounds(23, 95, 102, 23);
		contentPane.add(lblState);
		
		JLabel lblGuid = new JLabel("GUID");
		lblGuid.setFont(new Font("Garamond", Font.PLAIN, 16));
		lblGuid.setBounds(23, 53, 102, 31);
		contentPane.add(lblGuid);
		
		JLabel lblAddress = new JLabel("ADDRESS");
		lblAddress.setFont(new Font("Garamond", Font.PLAIN, 16));
		lblAddress.setBounds(23, 11, 102, 31);
		contentPane.add(lblAddress);
		
		JLabel dynAddress = new JLabel(peer.getAddress());
		dynAddress.setFont(new Font("Garamond", Font.ITALIC, 14));
		dynAddress.setBounds(135, 20, 239, 14);
		contentPane.add(dynAddress);
		
		JLabel lblDynguid = new JLabel(peer.getGuid());
		lblDynguid.setFont(new Font("Garamond", Font.ITALIC, 14));
		lblDynguid.setBounds(135, 62, 239, 14);
		contentPane.add(lblDynguid);
		
		JLabel lblDynstate = new JLabel(peer.getState());
		lblDynstate.setFont(new Font("Garamond", Font.ITALIC, 14));
		lblDynstate.setBounds(135, 100, 239, 14);
		contentPane.add(lblDynstate);
		
		JLabel lblClock = new JLabel("CLOCK");
		lblClock.setFont(new Font("Garamond", Font.PLAIN, 16));
		lblClock.setBounds(23, 142, 102, 23);
		contentPane.add(lblClock);
		
		JLabel lblDynclock = new JLabel(String.valueOf(peer.getLogicalClock()));
		lblDynclock.setFont(new Font("Garamond", Font.ITALIC, 14));
		lblDynclock.setBounds(135, 147, 239, 14);
		contentPane.add(lblDynclock);
	}
}
