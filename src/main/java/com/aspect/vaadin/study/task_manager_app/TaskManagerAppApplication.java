package com.aspect.vaadin.study.task_manager_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
public class TaskManagerAppApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerAppApplication.class, args);
    }
}
