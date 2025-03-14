/*******************************************************************************
 * Copyright (c) 2016 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.examples.model;

import static org.eclipse.rdf4j.model.util.Values.bnode;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * RDF Tutorial example 07: Writing an RDF model in Turtle syntax
 *
 * In this example, we show how you can use the Rio Parser/writer toolkit to write your model in Turtle syntax.
 *
 * @author Jeen Broekstra
 * @see
 */
public class Example07WriteTurtle {

	public static void main(String[] args) {

		BNode address = bnode();

		// Identically to example 05, we create a model with some data
		ModelBuilder builder = new ModelBuilder();
		builder
				.setNamespace("ex", "http://example.org/")
				.subject("ex:Picasso")
				.add(RDF.TYPE, "ex:Artist")
				.add(FOAF.FIRST_NAME, "Pablo")
				.add("ex:homeAddress", address) // link the blank node
				.subject(address) // switch the subject
				.add("ex:street", "31 Art Gallery")
				.add("ex:city", "Madrid")
				.add("ex:country", "Spain");

		Model model = builder.build();

		// Instead of simply printing the statements to the screen, we use a Rio writer to
		// write the model in Turtle syntax:
		Rio.write(model, System.out, RDFFormat.TURTLE);

	}
}
