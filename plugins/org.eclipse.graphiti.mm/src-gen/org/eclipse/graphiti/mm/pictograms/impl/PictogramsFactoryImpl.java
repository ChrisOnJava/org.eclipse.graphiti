/**
 * <copyright>
 * 
 * Copyright (c) 2005, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    Benjamin Schmeling - mwenz - Bug 367483 - Support composite connections
 * 
 * </copyright>
 */
package org.eclipse.graphiti.mm.pictograms.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.graphiti.mm.pictograms.*;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.CompositeConnection;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.CurvedConnection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.ManhattanConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PictogramsFactoryImpl extends EFactoryImpl implements PictogramsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PictogramsFactory init() {
		try {
			PictogramsFactory thePictogramsFactory = (PictogramsFactory)EPackage.Registry.INSTANCE.getEFactory(PictogramsPackage.eNS_URI);
			if (thePictogramsFactory != null) {
				return thePictogramsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new PictogramsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PictogramsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case PictogramsPackage.SHAPE: return createShape();
			case PictogramsPackage.CONTAINER_SHAPE: return createContainerShape();
			case PictogramsPackage.DIAGRAM: return createDiagram();
			case PictogramsPackage.CONNECTION: return createConnection();
			case PictogramsPackage.FIX_POINT_ANCHOR: return createFixPointAnchor();
			case PictogramsPackage.BOX_RELATIVE_ANCHOR: return createBoxRelativeAnchor();
			case PictogramsPackage.CHOPBOX_ANCHOR: return createChopboxAnchor();
			case PictogramsPackage.CONNECTION_DECORATOR: return createConnectionDecorator();
			case PictogramsPackage.FREE_FORM_CONNECTION: return createFreeFormConnection();
			case PictogramsPackage.MANHATTAN_CONNECTION: return createManhattanConnection();
			case PictogramsPackage.PICTOGRAM_LINK: return createPictogramLink();
			case PictogramsPackage.CURVED_CONNECTION: return createCurvedConnection();
			case PictogramsPackage.COMPOSITE_CONNECTION: return createCompositeConnection();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Shape createShape() {
		ShapeImpl shape = new ShapeImpl();
		return shape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ContainerShape createContainerShape() {
		ContainerShapeImpl containerShape = new ContainerShapeImpl();
		return containerShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Diagram createDiagram() {
		DiagramImpl diagram = new DiagramImpl();
		return diagram;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Connection createConnection() {
		ConnectionImpl connection = new ConnectionImpl();
		return connection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FixPointAnchor createFixPointAnchor() {
		FixPointAnchorImpl fixPointAnchor = new FixPointAnchorImpl();
		return fixPointAnchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BoxRelativeAnchor createBoxRelativeAnchor() {
		BoxRelativeAnchorImpl boxRelativeAnchor = new BoxRelativeAnchorImpl();
		return boxRelativeAnchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ChopboxAnchor createChopboxAnchor() {
		ChopboxAnchorImpl chopboxAnchor = new ChopboxAnchorImpl();
		return chopboxAnchor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConnectionDecorator createConnectionDecorator() {
		ConnectionDecoratorImpl connectionDecorator = new ConnectionDecoratorImpl();
		return connectionDecorator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FreeFormConnection createFreeFormConnection() {
		FreeFormConnectionImpl freeFormConnection = new FreeFormConnectionImpl();
		return freeFormConnection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ManhattanConnection createManhattanConnection() {
		ManhattanConnectionImpl manhattanConnection = new ManhattanConnectionImpl();
		return manhattanConnection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PictogramLink createPictogramLink() {
		PictogramLinkImpl pictogramLink = new PictogramLinkImpl();
		return pictogramLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CurvedConnection createCurvedConnection() {
		CurvedConnectionImpl curvedConnection = new CurvedConnectionImpl();
		return curvedConnection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CompositeConnection createCompositeConnection() {
		CompositeConnectionImpl compositeConnection = new CompositeConnectionImpl();
		return compositeConnection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PictogramsPackage getPictogramsPackage() {
		return (PictogramsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static PictogramsPackage getPackage() {
		return PictogramsPackage.eINSTANCE;
	}

} //PictogramsFactoryImpl
