package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NDRectangleKeyIndex;

public interface EvaluationGoalFunction<N extends Number> {

	public N value(NDRectangleKeyIndex index);
}
