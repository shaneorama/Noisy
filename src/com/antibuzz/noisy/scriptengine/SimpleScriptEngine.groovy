package com.antibuzz.noisy.scriptengine;

import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * The Class ScriptEngine.
 */
public class SimpleScriptEngine {
	
	/** The script engine. */
	private GroovyScriptEngine scriptEngine;
	

	public SimpleScriptEngine(String scriptRootPath, String scriptLibDir) throws MalformedURLException {
		URL scriptRoot = new File(scriptRootPath).toURI().toURL();

		CompilerConfiguration compilerConfig = new CompilerConfiguration();
        compilerConfig.setScriptBaseClass("sse.SimpleBaseScript");
		
		GroovyClassLoader scriptEngineCL = new GroovyClassLoader(SimpleScriptEngine.class.getClassLoader(), compilerConfig);
        new File(scriptLibDir).eachFile {f->
            URL entry = f.toURI().toURL()
            scriptEngineCL.addURL(entry)
        }

		scriptEngine = new GroovyScriptEngine([scriptRoot] as URL[], scriptEngineCL);
	}

	public Object runScript(String script, Object scriptDelegate) throws Exception {
        Binding binding = new Binding()
        if(scriptDelegate != null) {
            binding.setVariable('__delegate', scriptDelegate)
        }
		Object retVal = scriptEngine.run(script, binding);
		return retVal;		
	}
	
}
