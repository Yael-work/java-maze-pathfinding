module org.example.mvvmmazegui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.media;
    requires MavenPartB;
    requires java.desktop;

    // Open packages to javafx.fxml
    opens org.example.mvvmmazegui to javafx.fxml;
    opens org.example.mvvmmazegui.View to javafx.fxml;

    // Export packages
    exports org.example.mvvmmazegui;
    exports org.example.mvvmmazegui.Model;
    exports org.example.mvvmmazegui.View;
    exports org.example.mvvmmazegui.ViewModel;
}
