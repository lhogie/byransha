module byransha {

//	requires org.apache.commons.collections4;

	// This is the one that contains JsonNode
	requires com.fasterxml.jackson.databind;
	requires it.unimi.dsi.fastutil;
	 requires org.bouncycastle.pkix;
	    requires org.bouncycastle.provider;
	// Usually you need these too for Jackson to function
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.annotation;
	requires io.github.classgraph;
	requires javafx.controls;
	requires javafx.fxml; // Only if you use FXML files
	requires java.desktop;
	requires com.formdev.flatlaf;
	requires de.mkammerer.argon2.nolibs;
	requires atlantafx.base;
	requires java.management;
	requires jdk.management;
	requires net.sourceforge.plantuml;
	// Apache Commons IO (FileUtils, IOUtils, etc.)
	requires org.apache.commons.io;
	// Apache Commons Collections 4
	requires org.apache.commons.collections4;

	// The themes pack module (where FlatDraculaIJTheme lives)
	requires com.formdev.flatlaf.intellijthemes;

    requires org.jfree.jfreechart;
    // requires org.jfree.jcommon; /
    requires org.jfree.jfreesvg;
	// 2. Allow JavaFX to access your classes (important for start() method)
	opens byransha to javafx.graphics, javafx.fxml;

	// 3. Allow other modules to use your code
	exports byransha;
}