package org.computer.aman.metrics.util.var_scope;

/**
 * 変数のスコープ情報をカプセル化したクラス
 * 
 * @author Hirohisa Aman <aman@ehime-u.ac.jp>
 */
public class Scope 
{
	public static final int METHOD_ARGUMENT = 0;
	public static final int LOCAL_VARIABLE = 1;
	public static final int FIELD = 2;

	/**
	 * 与えられた変数のスコープオブジェクトを生成する．
	 * 
	 * @param aVariableName 変数名
	 * @param aVariableType 変数の型名
	 */
	public Scope(final String aVariableName, final String aVariableType)
	{
		variableName = aVariableName;
		variableType = aVariableType;
	}

	public boolean equals(Object aScope)
	{
		Scope sc = (Scope)aScope;
		if ( !variableName.equals(sc.getName()) 
				|| begin != sc.getBegin() || end != sc.getEnd() 
				|| kind != sc.getKind() 
				|| !variableType.equals(sc.getType())
				){
			return false;
		}
		return true;
	}
	
	/**
	 * スコープの開始位置（文字配列内の添字）を返す．
	 * 
	 * @return スコープの開始位置（文字配列内の添字）
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * スコープの終了位置（文字配列内の添字）を返す．
	 * 
	 * @return スコープの終了位置（文字配列内の添字）
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * 変数の種類（メソッドの引数，ローカル変数，フィールド）を返す．
	 * 
	 * @return　変数の種類を表す整数（各種類に対応する定数はこのクラスのクラス変数として用意されている）
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * 管理している変数の名前を返す．
	 * 
	 * @return 変数名
	 */
	public String getName() {
		return variableName;
	}

	/**
	 * 管理している変数の型名を返す．
	 * @return 変数の型名
	 */
	public String getType() {
		return variableType;
	}

	/**
	 * スコープの開始位置（文字配列内の添字） を設定する．
	 * 
	 * @param aBegin スコープの開始位置（文字配列内の添字）
	 */
	public void setBegin(final int aBegin) {
		begin = aBegin;
	}

	/**
	 * スコープの終了位置（文字配列内の添字）を設定する．
	 * 
	 * @param anEnd スコープの終了位置（文字配列内の添字）
	 */
	public void setEnd(final int anEnd) {
		end = anEnd;
	}

	/**
	 * 変数の種類（メソッドの引数，ローカル変数，フィールド）を設定する．
	 * 各種類に対応する定数はこのクラスのクラス変数として用意されている．
	 * 
	 * @param aKind 変数の種類を表す整数
	 */
	public void setKind(final int aKind) {
		kind = aKind;
	}

	private int begin;
	private int end;
	private int kind;
	private String variableName;
	private String variableType;
}
