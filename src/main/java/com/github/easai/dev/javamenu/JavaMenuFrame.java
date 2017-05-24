package com.github.easai.dev.javamenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.easai.dev.javamenu.JavaMenuMenu.MENUITEM;
import com.github.easai.utils.TreeEditor.TreeEditor;

public class JavaMenuFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TreeEditor treeEditor = new TreeEditor("");

	JTextArea menuCode = new JTextArea();
	JTextArea listenerCode = new JTextArea();

	JavaMenuMenu menu = new JavaMenuMenu();

	Logger log = LoggerFactory.getLogger(this.getClass());

	public JavaMenuFrame() {
		treeEditor.init();

		menu.setMenu(this, this, Locale.US);

		JSplitPane horizPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizPane.setLeftComponent(treeEditor);

		horizPane.setDividerLocation(500);
		JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightPane.setTopComponent(new JScrollPane(menuCode));
		rightPane.setBottomComponent(new JScrollPane(listenerCode));
		rightPane.setDividerLocation(500);

		horizPane.setRightComponent(rightPane);

		getContentPane().add(horizPane);

		setTitle("JavaMenuFrame");
		setSize(1300, 900);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void about() {
		JOptionPane.showMessageDialog(this, "JavaMenu\n Coypright 2017 easai\n All rights reserved.");
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		MENUITEM n = menu.comp.get(source);
		if (n != null) {
			switch (n) {
			case nFileOpen:
				treeEditor.read();
				break;
			case nFileSave:
				treeEditor.save();
				break;
			case nFileSaveAs:
				treeEditor.fileName = "";
				treeEditor.save();
				break;
			case nFileQuit:
				quit();
				break;
			case nToolsConvert:
				generateMenu();
				break;
			case nHelpAbout:
				about();
				break;
			}
		}
	}

	public String readFile(String fn) {
		String buffer = "";
		BufferedReader reader = null;
		try {			
			InputStream is = this.getClass().getResourceAsStream("/"+fn);			
			reader = new BufferedReader(new InputStreamReader(is));
			char buf[] = new char[255];
			int len;
			while ((len = reader.read(buf, 0, 255)) != -1) {
				buffer += new String(buf, 0, len);
			}
			reader.close();
		} catch (Exception e) {
			log.error("File read error.", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				log.error("File read error.", e);
			}
		}
		return buffer;
	}

	public void writeFile(String fileName, String text) {
	}

	public String getMenu() {
		String res = "static String menus[] = { ";
		TreeNode menuNode = treeEditor.top.getFirstChild();
		if (menuNode != null) {

			DefaultMutableTreeNode buffer;
			if (!menuNode.isLeaf()) {
				int i;

				for (i = 0; i < menuNode.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) menuNode.getChildAt(i);
					if (0 < i) {
						res += ",";
					}
					res += "\"" + ((String) buffer.getUserObject()) + "\"";
				}
			}
		}
		res += " };";
		return res;
	}

	public String getMenuItemList() {
		String res = "String menuitems[][] = { ";
		TreeNode menuNode = treeEditor.top.getFirstChild();
		if (menuNode != null) {

			DefaultMutableTreeNode buffer;
			if (!menuNode.isLeaf()) {
				int i;

				for (i = 0; i < menuNode.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) menuNode.getChildAt(i);
					if (0 < i) {
						res += ",";
					}
					res += "{";
					for (int j = 0; j < buffer.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) buffer.getChildAt(j);
						if (0 < j) {
							res += ",";
						}
						res += "\"" + node.getUserObject() + "\"";
					}
					res += "}";
				}
			}
		}
		res += " };";
		return res;
	}

	public String getEnum() {
		String res = "enum MENUITEM{";
		TreeNode menuNode = treeEditor.top.getFirstChild();
		if (menuNode != null) {
			DefaultMutableTreeNode buffer;
			if (!menuNode.isLeaf()) {
				int i;
				for (i = 0; i < menuNode.getChildCount(); i++) {
					if (0 < i) {
						res += ", ";
					}
					buffer = (DefaultMutableTreeNode) menuNode.getChildAt(i);
					String menuStr = (String) buffer.getUserObject();
					for (int j = 0; j < buffer.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) buffer.getChildAt(j);
						if (0 < j) {
							res += ", ";
						}
						res += "n" + menuStr + node.getUserObject() + "";
					}

				}
			}
		}
		res += " };";
		return res;
	}

	public String getMiNum() {
		String res = "	MENUITEM mi_num[][] = {";
		TreeNode menuNode = treeEditor.top.getFirstChild();
		if (menuNode != null) {

			DefaultMutableTreeNode buffer;
			if (!menuNode.isLeaf()) {
				int i;

				for (i = 0; i < menuNode.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) menuNode.getChildAt(i);
					if (0 < i) {
						res += ",";
					}
					res += "{";
					String menuStr = (String) buffer.getUserObject();
					for (int j = 0; j < buffer.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) buffer.getChildAt(j);
						if (0 < j) {
							res += ", ";
						}
						res += "MENUITEM.n" + menuStr + node.getUserObject() + "";
					}
					res += "}";
				}
			}
		}
		res += " };";
		return res;
	}

	public String getCases() {
		String res = "public void actionPerformed(ActionEvent e) {\n";
		res += "Object source = e.getSource();\n";
		res += "MENUITEM n = menu.comp.get(source);\n";
		res += "if (n != null) {\n";
		res += "switch (n) {\n";
		TreeNode menuNode = treeEditor.top.getFirstChild();
		if (menuNode != null) {

			DefaultMutableTreeNode buffer;
			if (!menuNode.isLeaf()) {
				int i;

				for (i = 0; i < menuNode.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) menuNode.getChildAt(i);

					String menuStr = (String) buffer.getUserObject();
					for (int j = 0; j < buffer.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) buffer.getChildAt(j);
						res += "case n" + menuStr + node.getUserObject() + ":\n";
						res += "break;\n";
					}
				}
			}
		}
		res += " }\n}\n}\n";
		return res;
	}

	public void generateMenu() {

		// read the tree
		String header = readFile("header.template");

		String menuClass = (String) ((DefaultMutableTreeNode) treeEditor.top.getFirstChild()).getUserObject();
		header = header.replace("#MENUCLASS", menuClass);

		// generate constants
		String menuStr = getMenu();
		String menuItemStr = getMenuItemList();
		String enumStr = getEnum();
		String miNum = getMiNum();
		String menuConstStr = menuStr + "\n" + menuItemStr + "\n" + enumStr + "\n" + miNum;

		/*
		 * Generates menu constants.
		 * 
		 * Ex. static String menus[] = { "Files", "Edit", "View", "Help" };
		 * String menuitems[][] = { { "Open", "Save", "Exit" }, { "Add", "Copy",
		 * "Delete" }, { "CollapseAll", "ExpandAll", "Collapse",
		 * "CollapseNodeatSameLevel", "Refresh", "FontSize" }, { "AboutJavaMenu"
		 * } };
		 * 
		 * enum MENUITEM{ nFilesOpen, nFilesSave, nFilesExit, nEditAdd,
		 * nEditCopy, nEditDelete, nViewCollapseAll, nViewExpandAll,
		 * nViewCollapse, nViewCollapseNodeatSameLevel, nViewRefresh,
		 * nViewFontSize, nHelpAboutJavaMenu };
		 * 
		 * MENUITEM mi_num[][] = { { MENUITEM.nFilesOpen, MENUITEM.nFilesSave,
		 * MENUITEM.nFilesExit }, { MENUITEM.nEditAdd, MENUITEM.nEditCopy,
		 * MENUITEM.nEditDelete }, { MENUITEM.nViewCollapseAll,
		 * MENUITEM.nViewExpandAll, MENUITEM.nViewCollapse,
		 * MENUITEM.nViewCollapseNodeatSameLevel, MENUITEM.nViewRefresh,
		 * MENUITEM.nViewFontSize }, { MENUITEM.nHelpAboutJavaMenu } };
		 */

		String footer = readFile("footer.template");

		menuCode.setText(header + menuConstStr + footer);

		String desc = readFile("desc.template");
		desc = desc.replace("#MENUCLASS", menuClass);

		listenerCode.setText(desc + getCases());
	}

	public void quit() {
		treeEditor.quit();
	}
}
