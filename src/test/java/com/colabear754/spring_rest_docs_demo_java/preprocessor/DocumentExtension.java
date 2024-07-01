package com.colabear754.spring_rest_docs_demo_java.preprocessor;

import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class DocumentExtension {
    public static OperationRequestPreprocessor requestPreprocessor(OperationPreprocessor... preprocessors) {
        ArrayList<OperationPreprocessor> preprocessorList = new ArrayList<>(List.of(preprocessors));
        preprocessorList.add(prettyPrint());
        return preprocessRequest(preprocessorList.toArray(new OperationPreprocessor[0]));
    }

    public static OperationResponsePreprocessor responsePreprocessor(OperationPreprocessor... preprocessors) {
        ArrayList<OperationPreprocessor> preprocessorList = new ArrayList<>(List.of(preprocessors));
        preprocessorList.add(prettyPrint());
        preprocessorList.add(modifyHeaders()
                .remove("X-Content-Type-Options")
                .remove("X-XSS-Protection")
                .remove("X-Frame-Options")
                .remove("Strict-Transport-Security")
                .remove("Cache-Control")
                .remove("Pragma")
                .remove("Expires"));
        return preprocessResponse(preprocessorList.toArray(new OperationPreprocessor[0]));
    }
}
