package com.xhd.easy_butterknife_processer;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.xhd.easy_butterknife_annotations.BindView;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by XHD on 2020/05/14
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, Map<String, Set<Element>>> elementMap = parseElements(roundEnv.getElementsAnnotatedWith(BindView.class));
        generateJavaFile(elementMap);
        return true;
    }


    private Map<String, Map<String, Set<Element>>> parseElements(Set<? extends Element> elements) {
        Map<String, Map<String, Set<Element>>> elementMap = new LinkedHashMap<>();
        // 遍历全部元素
        for (Element element : elements) {
            if (!element.getKind().isField()) {
                continue;
            }

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            String typeName = typeElement.getSimpleName().toString();
            PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();

            Map<String, Set<Element>> typeElementMap = elementMap.get(packageName);
            if (typeElementMap == null) {
                typeElementMap = new LinkedHashMap<>();
            }

            Set<Element> variableElements = typeElementMap.get(typeName);
            if (variableElements == null) {
                variableElements = new LinkedHashSet<>();
            }
            variableElements.add(element);

            typeElementMap.put(typeName, variableElements);
            elementMap.put(packageName, typeElementMap);
        }

        return elementMap;
    }

    private void generateJavaFile(Map<String, Map<String, Set<Element>>> elementMap) {
        Set<Map.Entry<String, Map<String, Set<Element>>>> packageElements = elementMap.entrySet();
        for (Map.Entry<String, Map<String, Set<Element>>> packageEntry : packageElements) {

            String packageName = packageEntry.getKey();
            Map<String, Set<Element>> typeElementMap = packageEntry.getValue();

            Set<Map.Entry<String, Set<Element>>> typeElements = typeElementMap.entrySet();
            for (Map.Entry<String, Set<Element>> typeEntry : typeElements) {

                String typeName = typeEntry.getKey();
                Set<Element> variableElements = typeEntry.getValue();

                ClassName className = ClassName.get(packageName, typeName);

                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(className, "target", Modifier.PRIVATE);

                MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(className, "activity")
                        .addStatement("target = activity");

                MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC);

                for (Element element : variableElements) {

                    VariableElement variableElement = (VariableElement) element;

                    String variableName = variableElement.getSimpleName().toString();
                    BindView bindView = variableElement.getAnnotation(BindView.class);

                    constructMethodBuilder.addStatement("target." + variableName + " = activity.findViewById(" + bindView.value() + ")");
                    unbindMethodBuilder.addStatement("target." + variableName + " = null");
                }

                unbindMethodBuilder.addStatement("target = null");

                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(typeName + "_ViewBinding")
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ClassName.get("com.xhd.easy_butterknife", "UnBinder"))
                        .addField(fieldSpecBuilder.build())
                        .addMethod(constructMethodBuilder.build())
                        .addMethod(unbindMethodBuilder.build());

                JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build()).build();

                try {
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
