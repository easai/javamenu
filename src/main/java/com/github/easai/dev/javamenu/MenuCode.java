package com.github.easai.dev.javamenu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MenuCode extends JTextArea implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPopupMenu popup = new JPopupMenu();
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	MenuCode() {
		this.addMouseListener(this);

		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(new ActionAdaptor() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});	
		popup.add(copy);
	}

	public void copy() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			String buf = getSelectedText();
			clipboard.setContents(new StringSelection(buf), null);
		} catch (Exception e) {
			log.error("Error copying text:",e);
		}
	}
	
	

	@Override
	public void mouseClicked(MouseEvent e) {
		int x0 = e.getX();
		int y0 = e.getY();

		if (e.getButton() == MouseEvent.BUTTON3) {
			popup.show(this, x0, y0);
		} else {
			int end = 0;
			String text = this.getText();
			if (!text.isEmpty()) {
				end = text.length() - 1;
			}
			this.setSelectionStart(0);
			this.setSelectionEnd(end);
			;
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
