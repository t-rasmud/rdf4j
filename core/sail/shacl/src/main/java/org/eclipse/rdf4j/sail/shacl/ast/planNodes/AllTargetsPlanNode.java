/*******************************************************************************
 * Copyright (c) 2021 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.sail.shacl.ast.planNodes;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.shacl.ConnectionsGroup;
import org.eclipse.rdf4j.sail.shacl.ast.StatementMatcher;
import org.eclipse.rdf4j.sail.shacl.ast.constraintcomponents.ConstraintComponent;
import org.eclipse.rdf4j.sail.shacl.ast.targets.EffectiveTarget;

/**
 * Used to signal bulk validation. This plan node should only be used from EffectiveTarget#getAllTargets
 */
public class AllTargetsPlanNode implements PlanNode {

	private final Select select;
	private StackTraceElement[] stackTrace;
	private boolean printed;
	private ValidationExecutionLogger validationExecutionLogger;

	public AllTargetsPlanNode(ConnectionsGroup connectionsGroup,
			ArrayDeque<EffectiveTarget.EffectiveTargetObject> chain, List<StatementMatcher.Variable> vars,
			ConstraintComponent.Scope scope) {
		String query = chain.stream()
				.map(EffectiveTarget.EffectiveTargetObject::getQueryFragment)
				.reduce((a, b) -> a + "\n" + b)
				.orElse("");

		List<String> varNames = vars.stream().map(StatementMatcher.Variable::getName).collect(Collectors.toList());

		this.select = new Select(connectionsGroup.getBaseConnection(), query, null,
				new AllTargetsBindingSetMapper(varNames, scope, false));

	}

	@Override
	public CloseableIteration<? extends ValidationTuple, SailException> iterator() {

		return new LoggingCloseableIteration(this, validationExecutionLogger) {

			final CloseableIteration<? extends ValidationTuple, SailException> iterator = select.iterator();

			@Override
			public void close() throws SailException {
				iterator.close();
			}

			@Override
			protected ValidationTuple loggingNext() throws SailException {
				return iterator.next();
			}

			@Override
			protected boolean localHasNext() throws SailException {
				return iterator.hasNext();
			}
		};

	}

	@Override
	public int depth() {
		return select.depth() + 1;
	}

	@Override
	public void getPlanAsGraphvizDot(StringBuilder stringBuilder) {
		if (printed) {
			return;
		}
		printed = true;
		stringBuilder.append(getId() + " [label=\"" + StringEscapeUtils.escapeJava(this.toString()) + "\"];")
				.append("\n");
		stringBuilder.append(select.getId() + " -> " + getId()).append("\n");
		select.getPlanAsGraphvizDot(stringBuilder);
	}

	@Override
	public String getId() {
		return System.identityHashCode(this) + "";
	}

	@Override
	public String toString() {
		return "AllTargetsPlanNode";
	}

	@Override
	public void receiveLogger(ValidationExecutionLogger validationExecutionLogger) {
		this.validationExecutionLogger = validationExecutionLogger;
		select.receiveLogger(validationExecutionLogger);
	}

	@Override
	public boolean producesSorted() {
		return false;
	}

	@Override
	public boolean requiresSorted() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AllTargetsPlanNode that = (AllTargetsPlanNode) o;
		return select.equals(that.select);
	}

	@Override
	public int hashCode() {
		return Objects.hash(select);
	}

	static class AllTargetsBindingSetMapper implements Function<BindingSet, ValidationTuple> {
		List<String> varNames;
		ConstraintComponent.Scope scope;
		boolean hasValue;

		public AllTargetsBindingSetMapper(List<String> varNames, ConstraintComponent.Scope scope, boolean hasValue) {
			this.varNames = varNames;
			this.scope = scope;
			this.hasValue = hasValue;
		}

		@Override
		public ValidationTuple apply(BindingSet b) {
			return new ValidationTuple(b, varNames, scope, false);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			AllTargetsBindingSetMapper that = (AllTargetsBindingSetMapper) o;
			return hasValue == that.hasValue &&
					varNames.equals(that.varNames) &&
					scope == that.scope;
		}

		@Override
		public int hashCode() {
			return Objects.hash(varNames, scope, hasValue, AllTargetsBindingSetMapper.class);
		}
	}

}
