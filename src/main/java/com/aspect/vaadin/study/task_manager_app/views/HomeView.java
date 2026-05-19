package com.aspect.vaadin.study.task_manager_app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@CssImport("./styles/output.css")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Set some basic properties for the layout itself
        setSizeFull(); // Make the layout take up the full available space
        addClassNames("my-bg");
        setAlignItems(Alignment.CENTER); // Center content horizontally

        setJustifyContentMode(JustifyContentMode.CENTER); // Center content vertically
        setSpacing(true); // Add space between components
        setPadding(true); // Add padding around the layout

        // Create some components to add to the layout
        H1 title = new H1("Welcome to Vertical Layout!");
        Paragraph description = new Paragraph("This is an example of components arranged vertically.");

        Button clickMeButton = new Button("Click Me!");
        Button anotherButton = new Button("Another Button");
        VerticalLayout buttonVerticalLayout = new VerticalLayout();
        buttonVerticalLayout.add(clickMeButton, anotherButton);
        buttonVerticalLayout.getStyle()
                .set("background-color", "#e0ffe0")  // Light green
                .set("border", "1px solid #ccc")
                .set("padding", "16px");
        // Override alignment for specific components
        buttonVerticalLayout.setAlignSelf(FlexComponent.Alignment.CENTER, clickMeButton);
        buttonVerticalLayout.setAlignSelf(FlexComponent.Alignment.END, anotherButton);
        // Add components to the VerticalLayout
        add(title, description, buttonVerticalLayout);

        // Add a listener to a button to show interactivity (optional for layout example)
        clickMeButton.addClickListener(e -> {
            // In a real app, you might update other components or show a notification
            System.out.println("Button clicked!");
            clickMeButton.setText("Clicked!");
        });
    }
}
