package io.github.oliviercailloux.y2018.xmgui.contract1;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/**
 * An object of this class MCProblem contains a set of alternatives, criteria
 * and an evaluation table in order to store a complete multi-criteria problem
 * for decision making.
 */
public class MCProblem {

	private Set<Alternative> alternatives = new HashSet<>();
	private Set<Criterion> criteria = new HashSet<>();
	private Table<Alternative, Criterion, Double> tableEval = HashBasedTable.create();

	/**
	 * Add a value for an alternative-criterion pair
	 * 
	 * @param alt, c and val must not be null
	 */
	public void putValue(Alternative alt, Criterion c, double val) {
		this.alternatives.add(Objects.requireNonNull(alt));
		this.criteria.add(Objects.requireNonNull(c));
		tableEval.put(alt, c, val);
	}

	/**
	 * Add an alternative to the Set
	 * 
	 * @param alt must not be null
	 */
	public void addAlt(Alternative alt) {
		this.alternatives.add(Objects.requireNonNull(alt));
	}

	/**
	 * Add a criterion to the Set
	 * 
	 * @param c must not be null
	 */
	public void addAlt(Criterion c) {
		this.criteria.add(Objects.requireNonNull(c));
	}

	/**
	 * Accessor for an alternative-criterion pair value in the tableEval
	 * 
	 * @param alt and c must not be null and must be present in the tableEval
	 */
	public double getValue(Alternative alt, Criterion c) {
		if (!tableEval.contains(Objects.requireNonNull(alt), Objects.requireNonNull(c))) {
			throw new IllegalArgumentException("the alt-c pair does not exist in the tableEval");
		}
		return tableEval.get(Objects.requireNonNull(alt), Objects.requireNonNull(c));
	}

	/**
	 * Accessor for the whole tableEval
	 * 
	 * @return an immutable copy of the tableEval
	 */
	public ImmutableTable<Alternative, Criterion, Double> getTableEval() {
		return ImmutableTable.copyOf(tableEval);
	}

}
