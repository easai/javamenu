package com.github.easai.dev.javamenu;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuTransferHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;
    int srcIndex=0;
    static public Logger log = LoggerFactory.getLogger(MenuTransferHandler.class);

    public MenuTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                              ";class=\"" +
                javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                              "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch(ClassNotFoundException e) {
            log.error("ClassNotFound: " + e);
        }
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDrop()) {
            return false;
        }
        
        support.setShowDropLocation(true);
        if(!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }        
 
        return true;
    }

    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree)c;
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null) {
            // Make up a node array of copies for transfer and
            // another for/of the nodes that will be removed in
            // exportDone after a successful drop.
            List<DefaultMutableTreeNode> copies =
                new ArrayList<DefaultMutableTreeNode>();
            List<DefaultMutableTreeNode> toRemove =
                new ArrayList<DefaultMutableTreeNode>();
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)paths[0].getLastPathComponent();
            DefaultMutableTreeNode copy = copy(node);
            copies.add(copy);
            toRemove.add(node);
            for(int i = 1; i < paths.length; i++) {
                DefaultMutableTreeNode next =
                    (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                // Do not allow higher level nodes to be added to list.
                if(next.getLevel() < node.getLevel()) {
                    break;
                } else if(next.getLevel() > node.getLevel()) {  // child node
                    copy.add(copy(next));
                    // node already contains child
                } else {                                        // sibling
                    copies.add(copy(next));
                    toRemove.add(next);
                }
            }
            DefaultMutableTreeNode[] nodes =
                copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove =
                toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			if(0<nodesToRemove.length){
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode)nodesToRemove[0].getParent();
        		srcIndex=parent.getIndex(nodesToRemove[0]);
			}
            return new NodesTransferable(nodes);
        }
        return null;
    }

    /** Defensive copy used in createTransferable. */
    private DefaultMutableTreeNode copy(TreeNode node) {
        return new DefaultMutableTreeNode(node);
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        if((action & MOVE) == MOVE) {
            JTree tree = (JTree)source;
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for(int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        if(!canImport(support)) {
            return false;
        }
        // Extract transfer data.
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavor);
        } catch(UnsupportedFlavorException ufe) {
            log.error("UnsupportedFlavor: ", ufe);
        } catch(java.io.IOException ioe) {
            log.error("I/O error: ", ioe);
        }
        // Get drop location info.
        JTree.DropLocation dl =
                (JTree.DropLocation)support.getDropLocation();        
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)dest.getLastPathComponent();
        JTree tree = (JTree)support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                        
        // Add data to model.
        for(int i = 0; i < nodes.length; i++) {
        	
        	int parentLevel=parent.getLevel();
        	int level=0;        	        	        	
        	
        	DefaultMutableTreeNode upRoot=(DefaultMutableTreeNode)parent.getParent();
        	DefaultMutableTreeNode orgRoot=null;
        	
			int destIndex=upRoot.getIndex(parent); // dest       			
			
			if(0<nodesToRemove.length){
        		level=nodesToRemove[0].getLevel();
        		orgRoot=(DefaultMutableTreeNode)nodesToRemove[0].getParent();        
        	}        	
        	
        	if(parentLevel==level && level == 3 && upRoot!=null && upRoot.equals(orgRoot)){
        		// reorder        		
        		destIndex=(srcIndex<destIndex)?destIndex+1:destIndex;
        		destIndex=(destIndex<0)?0:destIndex;
        			
        		model.insertNodeInto(nodes[i], upRoot, destIndex);
        			
        	}else{
        		return false;
        	}
        	
        }
        return true;
    }

    public String toString() {
        return getClass().getName();
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }

        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
