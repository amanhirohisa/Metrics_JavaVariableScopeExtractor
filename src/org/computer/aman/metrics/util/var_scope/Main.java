package org.computer.aman.metrics.util.var_scope;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Main 
{
	public static final String VERSION = "1.2";
	public static final String COPYRIGHT = "(C) 2015 Hirohisa AMAN <aman@computer.org>";
	
	public static void main(String[] args) throws IOException 
	{
		System.err.println("This is JavaVariableScopeExtractor ver." + VERSION + ".");
		System.err.println(COPYRIGHT);
		System.err.println();
		
		boolean debugMode = false;
		String fileName = null;
		for ( int i = 0; i < args.length; i++ ){
			if ( args[i].startsWith("-") ){
				if ( args[i].equals("-d") ){
					debugMode = true;
				}
				else{
					printError("Invalid option: " + args[i]);
				}
			}
			else if ( fileName == null ){
				fileName = args[i];
			}
			else{
				printError("Specify ONE source file!");
			}
		}
		if ( fileName == null ){
			printError("No Java source file is specified!" + "\n" + "Specify the path of Java source file to be analyzed.");
		}
		
		if ( debugMode ){
			System.err.println("[debug mode]");
		}
		
		System.err.print("Reading " + fileName + " ...");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		StringBuffer source = new StringBuffer();
		while ( (line = reader.readLine()) != null ){
			source.append(line);
			source.append("\n");
		}
		reader.close();
		System.err.println(" done.");
		System.err.println();
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toString().toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		TreeNode root = TreeNode.createRootNode();
		VariableDeclarationVisitor visitor = new VariableDeclarationVisitor(root, debugMode);
		unit.accept(visitor);

		if ( debugMode ){
			System.out.println("===========================================================");
			System.out.println(" Method arguments, fields and local variables");
			System.out.println("===========================================================");
		}
		
		ArrayList<Scope> scopeList = new ArrayList<Scope>();
		ArrayList<TreeNode> singleVariableDeclarationNodes = visitor.getListOfSingleVariableDeclarationNodes();
		for (Iterator<TreeNode> iterator = singleVariableDeclarationNodes.iterator(); iterator.hasNext();) {
			TreeNode node = iterator.next();
			scopeList.add(getVariableScopeForSingleVariableDeclarationNode(node));
		}
		ArrayList<TreeNode> variableDeclarationFragmentNodes = visitor.getListOfVariableDeclarationFragmentNodes();
		for (Iterator<TreeNode> iterator = variableDeclarationFragmentNodes.iterator(); iterator.hasNext();) {
			TreeNode node = iterator.next();
			scopeList.add(getVariableScopeForDeclarationFragmentNode(node));
		}

		Collections.sort(scopeList, new ScopeComparator());
		for (Iterator<Scope> iterator = scopeList.iterator(); iterator.hasNext();) {
			System.out.print(fileName + ",");
			Scope scope = iterator.next();
			switch ( scope.getType() ){
			case Scope.FIELD : System.out.print("F"); break;
			case Scope.LOCAL_VARIABLE : System.out.print("L"); break;
			case Scope.METHOD_ARGUMENT : System.out.print("M"); break;
			}
			System.out.println("," + scope.getVariable() + "," + unit.getLineNumber(scope.getBegin()) + "," + unit.getLineNumber(scope.getEnd()));			
		}
	}

	/**
	 * 与えられた SingleVariableDeclaration ノードがメソッドの引数宣言かどうかを判定する．
	 * 
	 * @param aNode 判定対象の SingleVariableDeclaration ノード
	 * @return メソッドの引数宣言ならば true，さもなくば false
	 */
	private static boolean isMethodArgument(TreeNode aNode) 
	{
		return aNode.getParent().getData() instanceof org.eclipse.jdt.core.dom.MethodDeclaration;
	}
	
	/**
	 * 与えられた VariableDeclarationFragment ノードがフィールド宣言かどうかを判定する．
	 * @param aNode 判定対象の VariableDeclarationFragment ノード
	 * @return フィールド宣言ならば true，さもなくば false
	 */
	private static boolean isField(TreeNode aNode)
	{
		return aNode.getParent().getData() instanceof org.eclipse.jdt.core.dom.FieldDeclaration;
	}

	/**
	 * SingleVariableDeclaration に該当するノードを解析して，そこで宣言されている変数の名前とスコープを返す．
	 * 
	 * @param aNode SingleVariableDeclaration に該当するノード
	 * @return 変数のスコープ情報を格納したオブジェクト  
	 */
	private static Scope getVariableScopeForSingleVariableDeclarationNode( TreeNode aNode )
	{
		Scope scope = null;
		for (Iterator<TreeNode> iterator = aNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode treeNode = iterator.next();
			if ( treeNode.getData() instanceof org.eclipse.jdt.core.dom.SimpleName ) {
				scope = new Scope(treeNode.getData().toString());			
				break;
			}
		}
		
		scope.setType(isMethodArgument(aNode) ? Scope.METHOD_ARGUMENT : Scope.LOCAL_VARIABLE);

		ArrayList<TreeNode> brothers = aNode.getParent().getChildren();
		ASTNode node = brothers.get(brothers.size()-1).getData();
		scope.setBegin(node.getStartPosition());
		scope.setEnd(node.getStartPosition() + node.getLength());
		return scope;
	}

	/**
	 * VariableScopeForDeclarationFragment に該当するノードを解析して，そこで宣言されている変数の名前とスコープを返す．
	 * 
	 * @param aNode VariableScopeForDeclarationFragment に該当するノード
	 * @return 変数のスコープ情報を格納したオブジェクト  
	 */
	private static Scope getVariableScopeForDeclarationFragmentNode( TreeNode aNode )
	{
		Scope scope = null;
		for (Iterator<TreeNode> iterator = aNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode treeNode = iterator.next();
			if ( treeNode.getData() instanceof org.eclipse.jdt.core.dom.SimpleName ) {
				scope = new Scope(treeNode.getData().toString());			
				break;
			}
		}

		if ( isField(aNode) ){
			scope.setType(Scope.FIELD);
			TreeNode node = aNode.getParent();
			while ( !(node.getData() instanceof org.eclipse.jdt.core.dom.TypeDeclaration) ){
				node = node.getParent();
			}
			
			int offset = 1;
			ArrayList<TreeNode> brothers = node.getChildren();
			for ( int i = 0; brothers.get(i).getData() instanceof org.eclipse.jdt.core.dom.Javadoc; i++ ){
				offset += brothers.get(i).getData().getLength();
			}
			scope.setBegin(node.getData().getStartPosition() + offset);
			scope.setEnd(node.getData().getStartPosition() + node.getData().getLength());
		}
		else{
			scope.setType(Scope.LOCAL_VARIABLE );
			TreeNode node = aNode.getParent();
			while ( !(node.getData() instanceof org.eclipse.jdt.core.dom.Block) &&
					!(node.getData() instanceof org.eclipse.jdt.core.dom.ForStatement)){
				node = node.getParent();
			}				
			ArrayList<TreeNode> children = node.getChildren();
			ASTNode lastNode = children.get(children.size()-1).getData();
			scope.setBegin(aNode.getData().getStartPosition() + aNode.getData().getLength() + 1);
			scope.setEnd(lastNode.getStartPosition() + lastNode.getLength());
		}
		
		return scope;
	}

	/**
	 * エラーメッセージを出力して，アプリケーションを終了させる．
	 * 
	 * @param aMessage エラーメッセージ
	 */
	private static void printError(final String aMessage)
	{
		System.err.println("*** Error ***");
		System.err.println(aMessage);
		System.exit(1);
	}
}
