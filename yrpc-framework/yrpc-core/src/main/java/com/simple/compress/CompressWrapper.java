package com.simple.compress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hongbin BAO
 * @Date 2024/1/13 17:09
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompressWrapper {
    private byte code;
    private String type;
    private Compressor compressor;
}