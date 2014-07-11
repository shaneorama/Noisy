package com.antibuzz.noisy.compiler

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * Created by shane_000 on 7/9/2014.
 */
class CompilerConfigurator {

    public CompilerConfiguration getConfiguration(){
        CompilerConfiguration config = new CompilerConfiguration()
        config.addCompilationCustomizers(
            createImportCustomizer()
        )
        return config
    }

    private ImportCustomizer createImportCustomizer(){
        def icz = new ImportCustomizer()
        // "normal" import
        icz.addImports('java.util.concurrent.atomic.AtomicInteger', 'java.util.concurrent.ConcurrentHashMap')
        // "aliases" import
        icz.addImport('CHM', 'java.util.concurrent.ConcurrentHashMap')
        // "static" import
        icz.addStaticImport('java.lang.Math', 'PI') // import static java.lang.Math.Pi
        // "aliased static" import
        icz.addStaticImport('pi', 'java.lang.Math', 'PI') // import static java.lang.Math.PI as pi
        // "star" import
        icz.addStarImports 'java.util.concurrent' // import java.util.concurrent.*
        // "static star" import
        icz.addStaticStars 'java.lang.Math' // import static java.lang.Math.*
        return icz
    }


}
