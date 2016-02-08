package pt.ipb.sd;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PeerGUI extends Peer {

	private JPanel contentPane;

	private JFrame jFrame;

	JLabel dynAddress;
	JLabel lblDynguid;
	JLabel lblDynstate;
	JLabel lblDynclock;
	JButton btnRequest;

	/**
	 * Create the frame.
	 */
	public PeerGUI() {
		
		super();
		
		try {
			start();
		} catch (Exception e) {
			System.out.println("Could not start channel.\nException: " + e);
		}
	
		jFrame = new JFrame("Ricart-Agrawala (Peer)");
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setBounds(100, 100, 420, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		jFrame.setContentPane(contentPane);
		contentPane.setLayout(null);

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

		dynAddress = new JLabel(this.peerInfo.getAddress().toString());
		dynAddress.setFont(new Font("Garamond", Font.BOLD, 14));
		dynAddress.setBounds(135, 20, 239, 14);
		contentPane.add(dynAddress);

		lblDynguid = new JLabel(this.peerInfo.getGuid().toString());
		lblDynguid.setFont(new Font("Garamond", Font.BOLD, 14));
		lblDynguid.setBounds(135, 62, 239, 14);
		contentPane.add(lblDynguid);

		lblDynstate = new JLabel(this.getState());
		lblDynstate.setFont(new Font("Garamond", Font.BOLD, 14));
		lblDynstate.setBounds(135, 100, 239, 14);
		contentPane.add(lblDynstate);

		JLabel lblClock = new JLabel("CLOCK");
		lblClock.setFont(new Font("Garamond", Font.PLAIN, 16));
		lblClock.setBounds(23, 142, 102, 23);
		contentPane.add(lblClock);

		lblDynclock = new JLabel(String.valueOf(this.peerInfo.getLogicalClock()));
		lblDynclock.setFont(new Font("Garamond", Font.BOLD, 14));
		lblDynclock.setBounds(135, 147, 239, 14);
		contentPane.add(lblDynclock);

		btnRequest = new JButton("REQUEST");
		btnRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				request();
			}
		});
		btnRequest.setBounds(23, 193, 351, 51);
		contentPane.add(btnRequest);
		
	}	
	
	@Override
	public void refresh() {
		this.dynAddress.setText(this.peerInfo.getAddress().toString());
		this.lblDynclock.setText(String.valueOf(this.peerInfo.getLogicalClock()));
		this.lblDynguid.setText(this.peerInfo.getGuid().toString());
		this.lblDynstate.setText(this.getState().toString());
	
		if(state == State.inCriticalSection || state == State.waiting) {
			btnRequest.setEnabled(false);
		} else {
			btnRequest.setEnabled(true);
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PeerGUI peerGui = new PeerGUI();
				peerGui.jFrame.setVisible(true);
			}
		});
	}

}
