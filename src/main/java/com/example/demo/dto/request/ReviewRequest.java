package com.example.demo.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private String reviewerName;
    private String comment;
    private Integer rating;
    private String productId;
}