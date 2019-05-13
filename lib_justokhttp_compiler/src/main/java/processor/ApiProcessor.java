package processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import annotation.API;

/**
 * Created by zhang on 2019/4/11 10:03
 */
@AutoService(Processor.class)
public final class ApiProcessor extends AbstractProcessor {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> anntations = new LinkedHashSet<>();
        anntations.add(API.class.getCanonicalName());
        return anntations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty())
        {
            info(">>> set is null... <<<");
            return true;
        }

        info(">>> Found field, start... <<<");

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(API.class);

        if (elements == null || elements.isEmpty())
        {
            info(">>> elements is null... <<<");
            return true;
        }
        for (Element annotatedElement : elements) {
            ElementKind kind = annotatedElement.getKind();
            if (kind != ElementKind.INTERFACE && kind != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s",
                        API.class.getSimpleName());
                return true;
            }

            analysisAnnotated(annotatedElement);
        }
        return false;
    }

    private void analysisAnnotated(Element element) {

        API api = element.getAnnotation(API.class);
        String packageName = api.packageName();
        String className = api.name().isEmpty()? api.getClass().getName() + "Factory": api.name();

        FieldSpec fieldHost = FieldSpec.builder(String.class, "host").build();
        MethodSpec methodGetField = MethodSpec.methodBuilder("getHost")
                .addParameter(String.class, "s")
                .returns(String.class)
                .build();
        TypeSpec typeApi = TypeSpec.annotationBuilder(className)
                .addMethod(methodGetField)
                .build();
//        TypeSpec typeApi = TypeSpec.classBuilder(className)
//                .addModifiers(Modifier.PUBLIC)
//                .addField(fieldHost)
//                .addMethod(methodGetField)
//                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeApi).build();

        try {
            String targetDirector = "E:/Android/app_wyt_geling_education/lib_justokhttp/src/main/java/com/wyt/lib_justokhttp";
            File dir = new File(targetDirector);
            if (!dir.exists()){
                dir.mkdirs();
            }
            javaFile.writeTo(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }
}
