package eme.codegen

class WrapperGenerator {

	def String generate(String className, String factoryName, String packagePath) '''
		package wrappers
		
		import org.eclipse.xtend.lib.annotations.Delegate
		import «packagePath».«className»
		import «packagePath».«factoryName»
		
		/**
		 * Wrapper class for the class «className»
		 */
		class «className»Wrapper implements «className» {
			@Delegate
			private var «className» ecoreImplementation;
		
			new() {
				ecoreImplementation = «factoryName».eINSTANCE.create«className»();
			}
		}
	'''
}
