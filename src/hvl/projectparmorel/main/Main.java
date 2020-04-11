package hvl.projectparmorel.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import hvl.projectparmorel.ecore.EcoreErrorExtractor;
import hvl.projectparmorel.ecore.EcoreQModelFixer;
import hvl.projectparmorel.exceptions.NoErrorsInModelException;
import hvl.projectparmorel.general.ErrorExtractor;
import hvl.projectparmorel.general.ModelFixer;
import hvl.projectparmorel.utils.ParmorelUtils;
import hvl.projectparmorel.general.Error;

public class Main {

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchMethodException, SecurityException {

		ModelFixer ql = new EcoreQModelFixer(ParmorelUtils.generateUserSettings(0));
		long startTimeT = System.currentTimeMillis();
		long endTimeT = 0;
		String root = "././mutants/";
		String root2 = "././fixed/";
		File folder = new File(root);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
//			if(listOfFiles[i].getName().equals("b10.ecore")) {
			// invert mutant order
			// for (int i = listOfFiles.length-1; i >=0; i--) {

			File dest = new File(root2 + listOfFiles[i].getName());
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			System.out.println("----------------------------------------------------------------------");
			System.out.println("----------------------------------------------------------------------");
			System.out.println("STARTING WITH MODEL - " + i + ": " + listOfFiles[i].getName());

			// Copy original file
			copyFile(listOfFiles[i], dest);

			URI uri = URI.createFileURI(dest.getAbsolutePath());
			Resource myMetaModel = getModel(uri);

			Resource auxModel = copy(myMetaModel, uri);

			EPackage epa = (EPackage) auxModel.getContents().get(0);
			System.out.println("Num. Classes: " + epa.getEClassifiers().size());

			int enums = 0;
			int refs = 0;
			int attribs = 0;
			int ops = 0;
			int datatypes = 0;

			for (int j = 0; j < epa.getEClassifiers().size(); j++) {
				if (epa.getEClassifiers().get(j).getClass() == EEnumImpl.class) {
					enums++;
				} else {
					if (epa.getEClassifiers().get(j).getClass() == EDataTypeImpl.class) {
						datatypes++;
					} else {
						refs = refs + ((EClassImpl) epa.getEClassifiers().get(j)).getEAllReferences().size();
						attribs = attribs + ((EClassImpl) epa.getEClassifiers().get(j)).getEAllAttributes().size();
						ops = ops + ((EClassImpl) epa.getEClassifiers().get(j)).getEAllOperations().size();
					}
				}
			}

			System.out.println("Num. References: " + refs);
			System.out.println("Num. Attributes: " + attribs);
			System.out.println("Num. Operations: " + ops);
			System.out.println("Num. Enums: " + enums);
			System.out.println("Num. DataTypes: " + datatypes);
			System.out.println("----------------------------------------------------------------------");
			System.out.println("----------------------------------------------------------------------");

			Set<Integer> unsupportedErrors = new HashSet<>();
			unsupportedErrors.add(4);
			unsupportedErrors.add(6);
			ErrorExtractor errorExtractor = new EcoreErrorExtractor(unsupportedErrors);
			List<Error> errors = errorExtractor.extractErrorsFrom(auxModel);

			System.out.println("INITIAL ERRORS:");
			System.out.println(errors.toString());
			System.out.println("Size= " + errors.size());
			System.out.println();

//			System.out.println("PREFERENCES: " + ql.getPreferences().toString());
			try {
				ql.fixModel(dest);
			} catch (NoErrorsInModelException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			endTime = System.currentTimeMillis();
			long timeneeded = (endTime - startTime);
			System.out.println("TOTAL TIME: " + timeneeded);
//			}
		}

		System.out.println("COMPLETELY FINISHED!!!!!!");
		endTimeT = System.currentTimeMillis();
		long timeneededT = (endTimeT - startTimeT);
		System.out.println("TOTAL EXECUTION TIME: " + timeneededT);
//		ql.saveKnowledge();
		System.exit(0);
	}
	
	private static void copyFile(File from, File to) throws IOException {
		Files.copy(from.toPath(), to.toPath());
	}
	
	private static Resource getModel(URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		return resourceSet.getResource(uri, true);
	}
	
	private static Resource copy(Resource model, URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		Resource modelCopy = resourceSet.createResource(uri);
		EList<EObject> contents = model.getContents();
		modelCopy.getContents().addAll(EcoreUtil.copyAll(contents));
		return modelCopy;
	}
}
