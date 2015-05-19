package org.computer.aman.metrics.util.var_scope;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;

public class TreeNode 
{
	/**
	 * 何もない状態の木構造での根ノードを返す．
	 * （解析のスタート時点で使用する）
	 * @return 空の木の根ノード
	 */
	public static TreeNode createRootNode()
	{
		return new TreeNode(null, "0", null);
	}
	
	/**
	 * 一つの AST ノードをカプセル化し，親子関係を走査できるようにした木構造のノードを作る
	 * 
	 * @param aNode 対象となる AST ノード
	 * @param anAddress ノードのアドレス：木の根を 0 として，子孫の向きに進む場合は "-"，下の兄弟（右）の向きに進む場合は数字を +1 したもの
	 * @param aParent 親ノード
	 */
	public TreeNode( ASTNode aNode, final String anAddress, final TreeNode aParent )
	{
		data = aNode;
		children = new ArrayList<TreeNode>();
		address = anAddress;
		parent = aParent;
	}
	
	/**
	 * 子ノードを追加登録する．
	 * 
	 * @param aNode　子ノード
	 */
	public void addChild( TreeNode aNode )
	{
		children.add(aNode);
	}
	
	/**
	 * このノードのアドレスを返す．
	 * 
	 * @return ノードのアドレス
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * 子ノードのリストを返す．
	 * 
	 * @return 子ノードのリスト
	 */
	public ArrayList<TreeNode> getChildren()
	{
		return children;
	}

	/**
	 * 管理している AST ノードへの参照を返す．
	 * 
	 * @return AST ノード
	 */
	public ASTNode getData()
	{
		return data;
	}
	
	/**
	 * 子ノードの個数を返す．
	 * 
	 * @return 子ノードの個数
	 */
	public int getChildrenCount()
	{
		return children.size();
	}
		
	/**
	 * 親ノードへの参照を返す．
	 * 
	 * @return 親ノード
	 */
	public TreeNode getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return "[" + address + "] " + data.getClass() + "\n" + data;
	}
	
	private String address;
	private ArrayList<TreeNode> children;
	private ASTNode data;
	private TreeNode parent;
}
