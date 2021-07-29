/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.model;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nonempty.qual.EnsuresNonEmpty;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.checker.nonempty.qual.PolyNonEmpty;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Timeout;

interface Value extends Serializable {
}

interface Statement extends Serializable {
	Value getObject();
}

interface Resource extends Value {
}

interface URI extends Resource {
}

interface IRI extends URI, Resource {
}

interface Namespace extends Serializable, Comparable<Namespace> {
	boolean isPresent();

	Namespace get();

	Namespace orElse(Object obj);
}

interface Model extends Set<Statement>, Serializable {
	@EnsuresNonEmpty(value = "this")
	boolean add(Resource subj, IRI pred, Value obj, Resource... contexts);

	boolean contains(Resource subj, IRI pred, Value obj, Resource... contexts);

	Model filter(Resource subj, IRI pred, Value obj, Resource... contexts);

	boolean remove(Resource subj, IRI pred, Value obj, Resource... contexts);

	@PolyNonEmpty
	Set<Resource> subjects(@PolyNonEmpty Model this);

	@PolyNonEmpty
	Set<IRI> predicates(@PolyNonEmpty Model this);

	@PolyNonEmpty
	Set<Value> objects(@PolyNonEmpty Model this);

	@PolyNonEmpty
	Set<Resource> contexts(@PolyNonEmpty Model this);

	Set<Namespace> getNamespaces();

	Optional<Namespace> removeNamespace(String prefix);

	Namespace getNamespace(String prefix);

	Namespace setNamespace(Namespace name);

	Namespace setNamespace(String name1, String name2);

	Iterable<Statement> getStatements(Resource subject, IRI predicate, Value object, Resource... contexts);
}

/**
 * Abstract test suite for implementations of the {@link Model} interface
 *
 * @author Peter Ansell
 */
@TestInstance(Lifecycle.PER_CLASS)
@Timeout(value = 1000, unit = MILLISECONDS)
public abstract class ModelTest {

	protected Literal literal1;

	protected Literal literal2;

	protected Literal literal3;

	protected IRI uri1;

	protected IRI uri2;

	protected IRI uri3;

	protected BNode bnode1;

	protected BNode bnode2;

	protected BNode bnode3;

	protected final ValueFactory vf = SimpleValueFactory.getInstance();

	protected abstract Model getNewModel();

	/**
	 * Helper method that asserts that the returned model is empty before returning.
	 *
	 * @return An empty instance of the {@link Model} implementation being tested.
	 */
	protected Model getNewEmptyModel() {
		Model model = getNewModel();
		assertTrue(model.isEmpty());
		return model;
	}

	protected Model getNewModelObjectSingleLiteral() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		assertEquals(1, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri2);
		assertEquals(1, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1);
		assertEquals(1, model.size());
		return model;
	}

	protected @NonEmpty Model getNewModelObjectDoubleLiteral() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);
		assertEquals(2, model.size());
		return model;
	}

	protected @NonEmpty Model getNewModelObjectDoubleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		assertEquals(2, model.size());
		return model;
	}

	protected @NonEmpty Model getNewModelObjectDoubleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, bnode2);
		assertEquals(2, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleLiteralSingleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, uri2);
		assertEquals(2, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleLiteralSingleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, bnode1);
		assertEquals(2, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleURISingleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri1);
		model.add(uri1, RDFS.LABEL, bnode1);
		assertEquals(2, model.size());
		return model;
	}

	protected Model getNewModelObjectTripleLiteral() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);
		model.add(uri1, RDFS.LABEL, literal3);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectTripleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri1);
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectTripleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, bnode2);
		model.add(uri1, RDFS.LABEL, bnode3);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleLiteralSingleURISingleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, bnode1);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleLiteralDoubleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleLiteralDoubleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, bnode2);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleURIDoubleBNode() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri1);
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, bnode2);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleURIDoubleLiteral() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri1);
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleBNodeDoubleURI() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		assertEquals(3, model.size());
		return model;
	}

	protected Model getNewModelObjectSingleBNodeDoubleLiteral() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1);
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);
		assertEquals(3, model.size());
		return model;
	}

	protected @NonEmpty Model getNewModelTwoContexts() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, bnode1, uri1);
		model.add(uri1, RDFS.LABEL, literal1, uri1);
		model.add(uri1, RDFS.LABEL, literal2, uri2);
		assertEquals(3, model.size());
		return model;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		uri1 = vf.createIRI("urn:test:uri:1");
		uri2 = vf.createIRI("urn:test:uri:2");
		uri3 = vf.createIRI("urn:test:uri:3");
		bnode1 = vf.createBNode();
		bnode2 = vf.createBNode("bnode2");
		bnode3 = vf.createBNode("bnode3");
		literal1 = vf.createLiteral("test literal 1");
		literal2 = vf.createLiteral("test literal 2");
		literal3 = vf.createLiteral("test literal 3");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	@SuppressWarnings("iteration:method.invocation") // next called after asserting hasNext returns true : assertion in
														// test
	public void testGetStatements_SingleLiteral() {
		Model model = getNewModelObjectSingleLiteral();

		Iterator<Statement> selection = model.getStatements(null, null, literal1).iterator();

		assertThat(selection.hasNext()).isTrue();
		assertThat(selection.next().getObject()).isEqualTo(literal1);
		assertThat(selection.hasNext()).isFalse();
	}

	@Test
	public void testGetStatements_IteratorModification() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);

		Iterator<Statement> selection = model.getStatements(uri1, null, null).iterator();
		while (selection.hasNext()) {
			Statement st = selection.next();
			if (st.getObject().equals(uri2)) {
				selection.remove();
			}
		}
		assertThat(model.contains(uri1, RDFS.LABEL, uri2)).isFalse();
		assertThat(model.contains(uri1, RDFS.LABEL, uri3)).isTrue();
	}

	@Test
	public void testGetStatements_ConcurrentModificationOfModel() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, uri2);
		model.add(uri1, RDFS.LABEL, uri3);
		model.add(uri1, RDFS.LABEL, literal1);
		model.add(uri1, RDFS.LABEL, literal2);

		Iterator<Statement> selection = model.getStatements(uri1, null, null).iterator();
		try {
			while (selection.hasNext()) {
				Statement st = selection.next();
				if (st.getObject().equals(uri2)) {
					model.remove(uri1, RDFS.LABEL, uri3);
				}
			}
			fail("should have resulted in ConcurrentModificationException");
		} catch (ConcurrentModificationException e) {
			// do nothing, expected
		}
	}

	@Test
	public void testGetStatements_AddToEmptyModel() {
		Model model = getNewEmptyModel();
		Iterator<Statement> selection = model.getStatements(null, null, null).iterator();
		assertThat(selection.hasNext()).isFalse();

		model.add(uri2, RDFS.LABEL, literal1);
		assertThat(model.contains(uri2, RDFS.LABEL, literal1)).isTrue();
		assertThat(selection.hasNext()).isFalse();
		Iterator<Statement> newSelection = model.getStatements(null, null, null).iterator();
		assertThat(newSelection.hasNext()).isTrue();
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#filter(Resource, IRI, Value, Resource...)}.
	 */
	@Test
	public void testFilterSingleLiteral() {
		Model model = getNewModelObjectSingleLiteral();
		Model filter1 = model.filter(null, null, literal1);
		assertFalse(filter1.isEmpty());
		Model filter2 = model.filter(null, null, literal1, (Resource) null);
		assertFalse(filter2.isEmpty());
	}

	@Test
	public void testFilter_AddToEmptyFilteredModel() {
		Model model = getNewEmptyModel();
		Model filter = model.filter(null, null, null);
		assertTrue(filter.isEmpty());

		filter.add(uri2, RDFS.LABEL, literal1);
		assertTrue(model.contains(uri2, RDFS.LABEL, literal1));
		assertTrue(filter.contains(uri2, RDFS.LABEL, literal1));
	}

	@Test
	public void testFilter_RemoveFromFilter() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1, uri1);
		Model filter = model.filter(uri1, RDFS.LABEL, literal1, uri1);
		assertTrue(filter.contains(uri1, RDFS.LABEL, literal1));

		filter.remove(uri1, RDFS.LABEL, literal1, uri1);
		assertFalse(model.contains(uri1, RDFS.LABEL, literal1));
		assertFalse(filter.contains(uri1, RDFS.LABEL, literal1));
	}

	@Test
	public void testFilter_AddToNonEmptyFilteredModel() {
		Model model = getNewModelObjectSingleLiteral();
		Model filter = model.filter(null, null, literal1);
		assertFalse(filter.isEmpty());

		filter.add(uri2, RDFS.LABEL, literal1);
		assertTrue(model.contains(uri2, RDFS.LABEL, literal1));
	}

	@Test
	public void testFilter_AddToEmptyOriginalModel() {
		Model model = getNewEmptyModel();
		Model filter = model.filter(null, null, null);
		assertTrue(filter.isEmpty());

		model.add(uri2, RDFS.LABEL, literal1);
		assertTrue(model.contains(uri2, RDFS.LABEL, literal1));
		assertTrue(filter.contains(uri2, RDFS.LABEL, literal1));
	}

	@Test
	public void testFilter_RemoveFromOriginal() {
		Model model = getNewEmptyModel();
		model.add(uri1, RDFS.LABEL, literal1, uri1);
		Model filter = model.filter(uri1, RDFS.LABEL, literal1, uri1);
		assertTrue(filter.contains(uri1, RDFS.LABEL, literal1));

		model.remove(uri1, RDFS.LABEL, literal1, uri1);
		assertFalse(model.contains(uri1, RDFS.LABEL, literal1));
		assertFalse(filter.contains(uri1, RDFS.LABEL, literal1));
	}

	@Test
	public void testFilter_AddToOriginalModel() {
		Model model = getNewModelObjectSingleLiteral();
		Model filter = model.filter(null, null, literal1);
		assertFalse(filter.isEmpty());
		assertTrue(filter.contains(uri1, RDFS.LABEL, literal1));
		assertFalse(filter.contains(uri2, RDFS.LABEL, literal1));

		model.add(uri2, RDFS.LABEL, literal1);
		assertTrue(filter.contains(uri2, RDFS.LABEL, literal1));
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#contains(Resource, IRI, Value, Resource...)} .
	 */
	@Test
	public void testContainsSingleLiteral() {
		Model model = getNewModelObjectSingleLiteral();
		assertTrue(model.contains(null, null, literal1));
		assertTrue(model.contains(null, null, literal1, (Resource) null));
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#subjects()}.
	 */
	@Test
	public void testSubjects() {
		Model m = getNewModelObjectDoubleLiteral();

		final int modelSizeBefore = m.size();

		Set<Resource> subjects = m.subjects();
		assertNotNull(subjects);

		final int setSizeBefore = subjects.size();

		Value predicate = subjects.iterator().next();
		subjects.remove(predicate);

		assertEquals(setSizeBefore - 1, subjects.size());
		assertTrue(m.size() < modelSizeBefore);
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#predicates()}.
	 */
	@Test
	public void testPredicates() {
		Model m = getNewModelObjectDoubleLiteral();

		final int modelSizeBefore = m.size();

		Set<IRI> predicates = m.predicates();
		assertNotNull(predicates);

		final int setSizeBefore = predicates.size();

		Value predicate = predicates.iterator().next();
		predicates.remove(predicate);

		assertEquals(setSizeBefore - 1, predicates.size());
		assertTrue(m.size() < modelSizeBefore);
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#objects()}.
	 */
	@Test
	public void testObjects() {
		Model m = getNewModelObjectDoubleLiteral();

		final int modelSizeBefore = m.size();

		Set<Value> objects = m.objects();
		assertNotNull(objects);

		final int setSizeBefore = objects.size();

		Value object = objects.iterator().next();
		objects.remove(object);

		assertEquals(setSizeBefore - 1, objects.size());
		assertTrue(m.size() < modelSizeBefore);
	}

	/**
	 * Test method for {@link org.eclipse.rdf4j.model.Model#contexts()}.
	 */
	@Test
	public void testContexts() {
		Model m = getNewModelTwoContexts();

		final int modelSizeBefore = m.size();

		Set<Resource> contexts = m.contexts();
		assertNotNull(contexts);

		final int setSizeBefore = contexts.size();

		Value predicate = contexts.iterator().next();
		contexts.remove(predicate);

		assertEquals(setSizeBefore - 1, contexts.size());
		assertTrue(m.size() < modelSizeBefore);
	}

	@Test
	public void testEqualsVsIsomorphic() {

		final Model x = getNewEmptyModel();
		x.add(vf.createBNode("node1ejfmfm4dx1"), RDF.VALUE, vf.createBNode("node1ejfmfm4dx2"));

		final Model y = getNewEmptyModel();
		y.add(vf.createBNode("node1ejfmfm4dx3"), RDF.VALUE, vf.createBNode("node1ejfmfm4dx4"));

		assertThat(Models.isomorphic(x, y));
		assertThat(x.equals(y)).isFalse();
	}

	@Test
	public void testEqualsHashcode() {
		final Model x = getNewEmptyModel();
		x.add(vf.createBNode("node1ejfmfm4dx1"), RDF.VALUE, vf.createBNode("node1ejfmfm4dx2"));

		final Model y = getNewEmptyModel();
		y.add(vf.createBNode("node1ejfmfm4dx1"), RDF.VALUE, vf.createBNode("node1ejfmfm4dx2"));

		assertThat(x.equals(y)).isTrue();
		assertThat(x.hashCode()).isEqualTo(y.hashCode());
	}

}
