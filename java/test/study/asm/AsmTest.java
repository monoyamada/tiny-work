package study.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import study.io.FileHelper;
import study.lang.Debug;

class MethodListWriter {
	private static final MethodNode[] EMPTY_METHOD_NODE_ARRAY = {};

	public static boolean and(int x0, int x1) {
		return (x0 & x1) != 0;
	}
	public void writeMethods(File outputFile, File classDirectory)
			throws IOException {
		final String eol = "\n";
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(outputFile);
			writer.write("/**");
			writer.write(eol);
			final File[] files = AsmTestClasses.listClasses(classDirectory);
			for (File file : files) {
				final ClassNode asmClass = this.getClassNode(file);
				if (this.acceptClass(asmClass)) {
					final MethodNode[] asmMethods = this.getMethodNodes(asmClass);
					for (MethodNode asmMethod : asmMethods) {
						if (this.acceptMethod(asmClass, asmMethod)) {
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
	protected boolean acceptClass(ClassNode asmClass) {
		return MethodListWriter.and(asmClass.access, Opcodes.ACC_PUBLIC);
	}
	protected boolean acceptMethod(ClassNode asmClass, MethodNode asmMethod) {
		return MethodListWriter.and(asmMethod.access, Opcodes.ACC_PUBLIC);
	}
	@SuppressWarnings("unchecked")
	protected MethodNode[] getMethodNodes(ClassNode asmClass) {
		final MethodNode[] array = MethodListWriter.EMPTY_METHOD_NODE_ARRAY;
		final List<MethodNode> methods = asmClass.methods;
		if (methods == null) {
			return array;
		}
		return methods.toArray(array);
	}
	protected ClassNode getClassNode(File file) throws IOException {
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
}

public class AsmTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
	public void testClassReader() throws IOException, ClassNotFoundException {
		final File outputDir = new File("data/tmp");
		{
			final String classPrefix = "org/soars/";
			final String classPath = "../org.soars.log-0.2/bin";
			final File classDir = new File(classPath);
			final File outputFile = new File(outputDir, "LogMethods.java");
			Assert.assertTrue(classDir.getAbsolutePath(), classDir.isDirectory());
			FileHelper.ensureDirectory(outputDir);
			final MethodListWriter writer = new MethodListWriter() {
				protected boolean acceptClass(ClassNode asmClass) {
					return MethodListWriter.and(asmClass.access, Opcodes.ACC_PUBLIC)
							&& asmClass.name.startsWith(classPrefix)
							&& !asmClass.name.endsWith("Test");
				}
			};
			writer.writeMethods(outputFile, classDir);
			Debug.log().info("wrote=" + outputFile.getAbsolutePath());
		}
	}
}
