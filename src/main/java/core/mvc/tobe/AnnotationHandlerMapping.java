package core.mvc.tobe;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import org.reflections.Reflections;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationHandlerMapping {
    private final Object[] basePackage;

    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public void initialize() {
        final Reflections reflections = new Reflections(basePackage);
        final Set<Method> methods = getRequestMappingMethods(reflections);

        for (Method method : methods) {
            final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            final HandlerKey handlerKey = new HandlerKey(requestMapping.value(), requestMapping.method());
            final HandlerExecution handlerExecution = new HandlerExecution(method);
            handlerExecutions.put(handlerKey, handlerExecution);
        }
    }

    public HandlerExecution getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod().toUpperCase());
        return getHandlerExecution(requestUri, requestMethod);
    }

    private Set<Method> getRequestMappingMethods(Reflections reflections) {
        return reflections.getTypesAnnotatedWith(Controller.class)
                .stream()
                .flatMap(clazz -> Arrays.stream(clazz.getMethods()))
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toSet());
    }

    private HandlerExecution getHandlerExecution(String requestUri, RequestMethod requestMethod) {
        final HandlerExecution handlerExecution = handlerExecutions.get(new HandlerKey(requestUri, requestMethod));
        if (handlerExecution != null) {
            return handlerExecution;
        }
        return handlerExecutions.get(new HandlerKey(requestUri, RequestMethod.ALL));
    }
}
