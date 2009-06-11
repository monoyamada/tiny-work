package study.asm;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import study.io.FileHelper;
import study.lang.ArrayHelper;

public class AsmTestClasses {
	public static final String CLASS_FILE_SUFFIX = ".class";
	private static final MethodNode[] EMPTY_METHOD_NODE_ARRAY = {};
	private static FileFilter classFileFilter;

	public static FileFilter getClassFileFilter() {
		if (AsmTestClasses.classFileFilter == null) {
			AsmTestClasses.classFileFilter = AsmTestClasses.newClassFileFilter();
		}
		return AsmTestClasses.classFileFilter;
	}
	protected static FileFilter newClassFileFilter() {
		return new FileFilter() {
			public boolean accept(File pathname) {
				final String name = pathname.getName();
				if (pathname.isDirectory()) {
					return !name.equals(".") && !name.equals("..");
				} else if (pathname.isFile()) {
					return name.endsWith(AsmTestClasses.CLASS_FILE_SUFFIX);
				}
				return false;
			}
		};
	}
	public static File[] listClasses(File directory) {
		final Collection<File> buffer = new ArrayList<File>();
		AsmTestClasses.listClasses(buffer, directory);
		return (File[]) buffer.toArray(ArrayHelper.EMPTY_FILE_ARRAY);
	}
	public static void listClasses(Collection<File> output, File directory) {
		assert directory != null;
		assert output != null;
		assert directory.isDirectory();
		final File[] files = directory.listFiles(AsmTestClasses
				.getClassFileFilter());
		for (int i = 0, n = files != null ? files.length : 0; i < n; ++i) {
			final File file = files[i];
			if (file.isDirectory()) {
				// keepit
				continue;
			} else if (file.isFile()) {
				output.add(file);
			}
			files[i] = null;
		}
		for (int i = 0, n = files != null ? files.length : 0; i < n; ++i) {
			final File dir = files[i];
			if (dir != null) {
				AsmTestClasses.listClasses(output, dir);
			}
		}
	}
	public static ClassNode getClassNode(File file) throws IOException {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			final ClassReader classReader = new ClassReader(input);
			final ClassNode classNode = new ClassNode();
			classReader.accept(classNode, ClassReader.SKIP_DEBUG);
			return classNode;
		} finally {
			FileHelper.close(input);
		}
	}
	@SuppressWarnings("unchecked")
	public static MethodNode[] getMethodNodes(ClassNode classNode) {
		final List methods = classNode.methods;
		if (methods == null) {
			return AsmTestClasses.EMPTY_METHOD_NODE_ARRAY;
		}
		return (MethodNode[]) methods
				.toArray(AsmTestClasses.EMPTY_METHOD_NODE_ARRAY);
	}
	public static boolean and(int x0, int x1) {
		return (x0 & x1) != 0;
	}
	public static void writeMethods(File outputFile, File classDirectory,
			int classAccess, int methodAccess) throws IOException,
			ClassNotFoundException {
		final String eol = "\n";
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(outputFile);
			writer.write("/**");
			writer.write(eol);
			final File[] files = AsmTestClasses.listClasses(classDirectory);
			for (File file : files) {
				final ClassNode asmClass = AsmTestClasses.getClassNode(file);
				if (AsmTestClasses.and(asmClass.access, classAccess)) {
					final MethodNode[] asmMethods = AsmTestClasses
							.getMethodNodes(asmClass);
					for (MethodNode asmMethod : asmMethods) {
						if (AsmTestClasses.and(asmMethod.access, methodAccess)) {
							writer.write(" * ");
							AsmTestClasses.writeMethods(writer, asmClass, asmMethod);
							writer.write(eol);
						}
					}
				}
			}
			final String name = outputFile.getName().replaceAll(".java$", "");
			writer.write(" */");
			writer.write(eol);
			writer.write("interface ");
			writer.write(name);
			writer.write(" {");
			writer.write(eol);
			writer.write("}");
			writer.write(eol);
		} finally {
			FileHelper.close(writer);
		}
	}
	protected static void writeMethods(Writer writer, ClassNode asmClass,
			MethodNode asmMethod) throws IOException {
		final Type[] params = Type.getArgumentTypes(asmMethod.desc);
		final StringBuilder buffer = new StringBuilder();
		{
			final String name = AsmTestClasses.asmToJavaClassName(asmClass);
			buffer.append(name);
		}
		buffer.append('#');
		{
			final String name = AsmTestClasses.asmToJavaMethodName(asmClass,
					asmMethod);
			buffer.append(name);
		}
		buffer.append('(');
		for (int ii = 0, nn = params.length; ii < nn; ++ii) {
			String name = AsmTestClasses.asmToJavaClassName(params[ii]);
			if (0 < ii) {
				buffer.append(", ");
			}
			buffer.append(name);
		}
		buffer.append(')');
		writer.write("{@link " + buffer + "}");
		writer.flush();
	}
	protected static String asmToJavaMethodName(ClassNode asmClass,
			MethodNode asmMethod) {
		String name = asmMethod.name;
		if (name.equals("<init>")) {
			name = asmClass.name;
			final int index = name.lastIndexOf('/');
			if (0 <= index) {
				name = name.substring(index + 1);
			}
		}
		return name;
	}
	protected static String asmToJavaClassName(ClassNode asmClass) {
		final String java_lang_ = "java.lang.";
		String name = asmClass.name;
		if (name == null || name.length() < 1) {
			return name;
		}
		name = name.replace('/', '.');
		if (name.startsWith(java_lang_)) {
			return name.substring(java_lang_.length());
		}
		return name;
	}
	protected static String asmToJavaClassName(Type asmType) {
		final String java_lang_ = "java.lang.";
		String name = asmType.getClassName();
		if (name == null || name.length() < 1) {
			return name;
		}
		if (name.startsWith(java_lang_)) {
			return name.substring(java_lang_.length());
		}
		return name;
	}
}
