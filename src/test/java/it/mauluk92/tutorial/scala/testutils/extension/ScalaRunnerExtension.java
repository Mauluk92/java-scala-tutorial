package it.mauluk92.tutorial.scala.testutils.extension;

import it.mauluk92.tutorial.scala.testutils.ScalaFacade;
import it.mauluk92.tutorial.scala.testutils.extension.annotation.ExecuteScalaProgram;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ScalaRunnerExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Integer.class) && parameterContext.isAnnotated(ExecuteScalaProgram.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        String outputDirpath = context.getStore(ExtensionContext.Namespace.GLOBAL).get("outputDir", String.class);
        try {
            if (parameterContext.isAnnotated(ExecuteScalaProgram.class)) {
                ExecuteScalaProgram annotation = parameterContext.getParameter().getAnnotation(ExecuteScalaProgram.class);
                String separatorClassPath = System.getProperty("os.name").contains("Windows") ? ";" : ":";
                File[] resources = new File[(int) (Arrays.stream(annotation.classPath()).filter(s -> !s.isEmpty()).count() + 1)];
                for (int i = 0; i < annotation.classPath().length; i++) {
                    resources[i] = new ClassPathResource(annotation.classPath()[i]).getFile();
                }
                resources[resources.length - 1] = new File(outputDirpath);
                String classPath = Arrays.stream(resources).map(File::getAbsolutePath).collect(Collectors.joining(separatorClassPath));
                String[] commandLineArgs = annotation.commandLineArguments();
                String mainClassPath = annotation.mainClass();
                String modulePath = annotation.modulePath();
                String moduleName = annotation.moduleName();
                if(!moduleName.isEmpty()){
                    modulePath = modulePath.isEmpty() ? outputDirpath : modulePath;
                }
                return ScalaFacade.run(Arrays.asList(commandLineArgs), outputDirpath, classPath, mainClassPath, modulePath, moduleName);
            }
        } catch (IOException e) {
            throw new ParameterResolutionException("Could not locate a path", e);
        }
        return -1;
    }
}
