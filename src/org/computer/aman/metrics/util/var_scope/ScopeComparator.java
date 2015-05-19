package org.computer.aman.metrics.util.var_scope;

import java.util.Comparator;

public class ScopeComparator 
implements Comparator<Scope> 
{
	public int compare( Scope aScope1, Scope aScope2 )
	{
		return aScope1.getBegin() - aScope2.getBegin();
	}
}
