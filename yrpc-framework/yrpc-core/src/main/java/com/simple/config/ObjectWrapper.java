package com.simple.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hongbin BAO
 * @Date 2024/1/15 17:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectWrapper<T> {
    private Byte code;
    private String name;
    private T impl;
}
