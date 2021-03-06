package com.robohorse.robopojogenerator.generator.processors;

import com.robohorse.robopojogenerator.generator.ClassItem;
import com.robohorse.robopojogenerator.generator.consts.AnnotationItem;
import com.robohorse.robopojogenerator.generator.consts.ClassTemplate;
import com.robohorse.robopojogenerator.generator.consts.Imports;
import com.robohorse.robopojogenerator.generator.consts.PojoAnnotations;
import com.robohorse.robopojogenerator.generator.utils.ClassGenerateHelper;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Created by vadim on 25.09.16.
 */
public class ClassPostProcessor {
    @Inject
    ClassGenerateHelper generateHelper;
    @Inject
    ClassTemplateProcessor classTemplateProcessor;

    @Inject
    public ClassPostProcessor() {
    }

    public String proceed(ClassItem classItem, AnnotationItem annotationItem) {
        applyAnnotations(annotationItem, classItem);
        return proceedClass(classItem);
    }

    private String proceedClass(ClassItem classItem) {
        final String classBody = proceedClassBody(classItem);
        final String classTemplate = classTemplateProcessor.createClassBody(classItem, classBody);
        final Set<String> imports = classItem.getClassImports();
        final StringBuilder importsBuilder = new StringBuilder();
        for (String importItem : imports) {
            importsBuilder.append(importItem);
            importsBuilder.append(ClassTemplate.NEW_LINE);
        }
        return classTemplateProcessor.createClassItem(
                classItem.getPackagePath(),
                importsBuilder.toString(),
                classTemplate);
    }

    private String proceedClassBody(ClassItem classItem) {
        final StringBuilder classBodyBuilder = new StringBuilder();
        final StringBuilder classMethodBuilder = new StringBuilder();
        final Map<String, String> classFields = classItem.getClassFields();
        for (String objectName : classFields.keySet()) {
            classBodyBuilder.append(classTemplateProcessor
                    .createFiled(classFields.get(objectName), objectName, classItem.getAnnotation()));

            classMethodBuilder.append(ClassTemplate.NEW_LINE);

            classMethodBuilder.append(classTemplateProcessor
                    .createSetter(objectName, classFields.get(objectName)));

            classMethodBuilder.append(ClassTemplate.NEW_LINE);

            classMethodBuilder.append(classTemplateProcessor
                    .createGetter(objectName, classFields.get(objectName)));
        }
        classBodyBuilder.append(classMethodBuilder);
        return classBodyBuilder.toString();
    }

    private void applyAnnotations(AnnotationItem item, ClassItem classItem) {
        switch (item) {
            case GSON: {
                generateHelper.setAnnotations(classItem,
                        PojoAnnotations.GSON.CLASS_ANNOTATION,
                        PojoAnnotations.GSON.ANNOTATION,
                        Imports.GSON.IMPORTS);
                break;
            }
            case LOGAN_SQUARE: {
                generateHelper.setAnnotations(classItem,
                        PojoAnnotations.LOGAN_SQUARE.CLASS_ANNOTATION,
                        PojoAnnotations.LOGAN_SQUARE.ANNOTATION,
                        Imports.LOGAN_SQUARE.IMPORTS);
                break;
            }
            case JACKSON: {
                generateHelper.setAnnotations(classItem,
                        PojoAnnotations.JACKSON.CLASS_ANNOTATION,
                        PojoAnnotations.JACKSON.ANNOTATION,
                        Imports.JACKSON.IMPORTS);
                break;
            }
        }
    }
}
