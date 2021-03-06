package com.robohorse.robopojogenerator.listeners;

import com.robohorse.robopojogenerator.errors.RoboPluginException;
import com.robohorse.robopojogenerator.generator.consts.AnnotationItem;
import com.robohorse.robopojogenerator.generator.utils.ClassGenerateHelper;
import com.robohorse.robopojogenerator.injections.Injector;
import com.robohorse.robopojogenerator.models.GenerationModel;
import com.robohorse.robopojogenerator.services.MessageService;
import com.robohorse.robopojogenerator.view.GeneratorVew;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Created by vadim on 24.09.16.
 */
public class GenerateActionListener implements ActionListener {
    @Inject
    MessageService messageService;
    @Inject
    ClassGenerateHelper classGenerateHelper;

    private GuiFormEventListener eventListener;
    private GeneratorVew generatorVew;

    public GenerateActionListener(GeneratorVew generatorVew,
                                  GuiFormEventListener eventListener) {
        this.generatorVew = generatorVew;
        this.eventListener = eventListener;
        Injector.getAppComponent().inject(this);
    }

    public void actionPerformed(ActionEvent e) {
        final JTextArea textArea = generatorVew.getTextArea();
        final JTextField textField = generatorVew.getClassNameTextField();

        final AnnotationItem annotationItem = resolveAnnotationItem();
        final boolean rewriteClasses = generatorVew.getRewriteExistingClassesCheckBox().isSelected();
        final String content = textArea.getText();
        final String className = textField.getText();
        try {
            classGenerateHelper.validateClassName(className);
            classGenerateHelper.validateJsonContent(content);
            eventListener.onJsonDataObtained(new GenerationModel
                    .Builder()
                    .setAnnotationItem(annotationItem)
                    .setContent(content)
                    .setRootClassName(className)
                    .setRewriteClasses(rewriteClasses)
                    .build());

        } catch (RoboPluginException exception) {
            messageService.onPluginExceptionHandled(exception);
        }
    }

    private AnnotationItem resolveAnnotationItem() {
        final ButtonGroup buttonGroup = generatorVew.getTypeButtonGroup();
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements(); ) {
            final AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                for (AnnotationItem annotationItem : AnnotationItem.values()) {
                    if (annotationItem.getText().equals(button.getText())) {
                        return annotationItem;
                    }
                }
            }
        }
        return AnnotationItem.NONE;
    }
}
