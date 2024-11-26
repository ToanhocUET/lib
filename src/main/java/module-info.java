module com.demo.zlib {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens com.demo.zlib.Controllers.Admin.ManageBorrowings to javafx.fxml;
    opens com.demo.zlib.Controllers to javafx.fxml;
    opens com.demo.zlib.Controllers.Admin to javafx.fxml;
    opens com.demo.zlib.Controllers.Admin.ManageBooks to javafx.fxml;
    opens com.demo.zlib.Controllers.Admin.ManageMembers to javafx.fxml;
    opens com.demo.zlib.Source to javafx.base;

    exports com.demo.zlib;
    exports com.demo.zlib.Controllers;
    exports com.demo.zlib.Controllers.Admin;
    exports com.demo.zlib.Models;
    exports com.demo.zlib.Views;
}
