module byransha {

	requires it.unimi.dsi.fastutil; // primivite collections
	requires org.bouncycastle.pkix;  
	requires org.bouncycastle.provider;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;

	requires io.github.classgraph; // for classpath scanning 

	requires java.desktop;  

	requires com.formdev.flatlaf;
	requires com.formdev.flatlaf.intellijthemes;

	requires de.mkammerer.argon2.nolibs; // encryption
	requires atlantafx.base;  
	requires java.management;
	requires jdk.management;
	requires net.sourceforge.plantuml;
	requires org.apache.commons.io;
	requires org.apache.commons.collections4;
	requires java.net.http;
	requires java.naming;

    requires langchain4j.ollama;
    requires org.jfree.jfreechart;
    requires org.jfree.jfreesvg;
    requires langchain4j.core;


	// Allow other modules to use your code
	exports byransha;
}