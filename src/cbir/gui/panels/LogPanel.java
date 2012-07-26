package cbir.gui.panels;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class LogPanel extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3673627726759758144L;
	private JTextArea logText;
	
	public LogPanel(String title) {
		super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
		logText = new JTextArea();
		logText.setLineWrap(true);
		logText.setRows(8);
		logText.setEditable(false);
		setViewportView(logText);
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}
	
	public void addLine(String line) {
		logText.append(line + "\n");
		logText.setCaretPosition(logText.getText().length());
		repaint();
	}
	
	public void clear() {
		logText.setText("");
//		repaint();
	}
}
