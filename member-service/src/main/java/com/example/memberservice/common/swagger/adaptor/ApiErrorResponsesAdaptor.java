package com.example.memberservice.common.swagger.adaptor;

import static com.example.memberservice.common.exception.ResponseDtoMapper.getErrorResponse;

import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.swagger.model.vo.ExampleHolder;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.examples.Example;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiErrorResponsesAdaptor {

    public void generateErrorCodeResponseExample(Operation operation, ErrorCode[] exceptions) {
        ApiResponses responses = operation.getResponses();

        // statusCode별 ExampleHolder 리스트
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(exceptions)
            .map(ex -> {
                try {
                    ResponseDto<Empty> res = getErrorResponse(ex);
                    return new ExampleHolder(
                        getSwaggerExample(res),
                        res.code(),
                        res.httpStatus()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.groupingBy(ExampleHolder::httpStatusCode));


        addExamplesToResponses(responses, statusWithExampleHolders);
    }


    public void generateErrorCodeResponseExample(Operation operation, ErrorCode instance) {
        ApiResponses responses = operation.getResponses();

        try {
            ResponseDto<Empty> res = getErrorResponse(instance);

            ExampleHolder exampleHolder = new ExampleHolder(
                getSwaggerExample(res),
                res.code(),
                res.httpStatus()
            );

            addExamplesToResponses(responses, exampleHolder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Example getSwaggerExample(ResponseDto<Empty> errorResponseDto) {
        Example example = new Example();
        example.setValue(errorResponseDto);
        return example;
    }


    private void addExamplesToResponses(
        ApiResponses responses,
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders
    ) {
        statusWithExampleHolders.forEach((status, holders) -> {
            Content content = new Content();
            MediaType mediaType = new MediaType();
            ApiResponse apiResponse = new ApiResponse();

            holders.forEach(holder -> {
                mediaType.addExamples(String.valueOf(holder.code()), holder.holder());
            });

            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);
            responses.addApiResponse(String.valueOf(status), apiResponse);
        });
    }


    private void addExamplesToResponses(ApiResponses responses, ExampleHolder exampleHolder) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        ApiResponse apiResponse = new ApiResponse();

        mediaType.addExamples(String.valueOf(exampleHolder.code()), exampleHolder.holder());
        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);

        responses.addApiResponse(String.valueOf(exampleHolder.httpStatusCode()), apiResponse);
    }
}
