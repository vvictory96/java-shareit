package ru.practicum.shareit.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.validation.ValidationException;
import java.util.Objects;

public class PagingArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PagingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        PagingParam attr = parameter.getParameterAnnotation(PagingParam.class);
        int[] value = Objects.requireNonNull(attr).value();

        return setValues(webRequest.getParameter("from"), webRequest.getParameter("size"), value);
    }

    private Paging setValues(String from, String size, int[] value) {
        Paging paging = new Paging();

        if (from == null) {
            paging.setFrom(value[0]);
        } else {
            paging.setFrom(Integer.parseInt(from));
        }

        if (size == null) {
            paging.setSize(value[1]);
        } else {
            paging.setSize(Integer.parseInt(size));
        }

        validatePaging(paging);

        return paging;
    }

    private void validatePaging(Paging paging) {
        if (paging.getFrom() < 0 || paging.getSize() <= 0) {
            throw new ValidationException();
        }
    }
}
