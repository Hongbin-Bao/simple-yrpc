package com.simple.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hongbin BAO
 * @Date 2024/1/11 20:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SerializerWrapper {
    private byte code;
    private String type;
    private Serializer serializer;
}