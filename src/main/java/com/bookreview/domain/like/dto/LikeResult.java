package com.bookreview.domain.like.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LikeResult {

    private final boolean liked;
    private final int likeCount;
}
