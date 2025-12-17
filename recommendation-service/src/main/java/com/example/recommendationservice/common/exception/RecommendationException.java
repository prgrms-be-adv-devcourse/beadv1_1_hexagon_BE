package com.example.recommendationservice.common.exception;

import com.example.recommendationservice.common.exception.status.ResponseStatusCode;

public class RecommendationException extends CustomException {

    public RecommendationException(ResponseStatusCode status) {
        super(status);
    }

}
