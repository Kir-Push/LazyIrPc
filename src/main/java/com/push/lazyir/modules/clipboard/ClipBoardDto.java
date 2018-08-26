package com.push.lazyir.modules.clipboard;

import com.push.lazyir.api.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClipBoardDto implements Dto {
    private String command;
    private String text;
}
