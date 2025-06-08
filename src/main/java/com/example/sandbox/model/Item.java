package com.example.sandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Item implements Serializable {

    @Serial
    private static final long serialVersionUID = -7388709040409834599L;

    private String name;
}
