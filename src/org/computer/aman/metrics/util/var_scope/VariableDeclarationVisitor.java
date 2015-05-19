package org.computer.aman.metrics.util.var_scope;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class VariableDeclarationVisitor 
extends ASTVisitor 
{
	public VariableDeclarationVisitor( TreeNode aNode )
	{
		this(aNode, false);
	}

	public VariableDeclarationVisitor( TreeNode aNode, final boolean aDebugMode )
	{
		root = aNode;
		debugMode = aDebugMode;
	}

	public void preVisit( ASTNode node )
	{
		TreeNode nodeInTree = null;
		String address = null;
		TreeNode parentInTree = hash.get( node.getParent() );
		if ( parentInTree != null ){
			address = parentInTree.getAddress() + "-" + (parentInTree.getChildrenCount() + 1);
			nodeInTree = new TreeNode(node, address, parentInTree);
			parentInTree.addChild(nodeInTree);
		}
		else{
			address = "0";
			nodeInTree = new TreeNode(node, address, null);
			root.addChild(nodeInTree);
		}
		hash.put(node, nodeInTree);

		if ( debugMode ){
			System.out.println("-------------------------------------------------------");
			System.out.println(nodeInTree);
		}
	}
	
	
	@Override
	public boolean visit(SingleVariableDeclaration aNode) 
	{
		listOfSingleVariableDeclarationNodes.add(hash.get(aNode));
		return super.visit(aNode);
	}


	@Override
	public boolean visit(VariableDeclarationFragment aNode) 
	{
		listOfVariableDeclarationFragmentNodes.add(hash.get(aNode));
		return super.visit(aNode);
	}

	public ArrayList<TreeNode> getListOfSingleVariableDeclarationNodes() 
	{
		return listOfSingleVariableDeclarationNodes;
	}


	public ArrayList<TreeNode> getListOfVariableDeclarationFragmentNodes() 
	{
		return listOfVariableDeclarationFragmentNodes;
	}


	private boolean debugMode;

	private HashMap<ASTNode, TreeNode> hash = new HashMap<ASTNode, TreeNode>();
	
	private TreeNode root;
	
	private ArrayList<TreeNode> listOfSingleVariableDeclarationNodes = new ArrayList<TreeNode>();

	private ArrayList<TreeNode> listOfVariableDeclarationFragmentNodes = new ArrayList<TreeNode>();	
}
