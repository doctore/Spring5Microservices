package com.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@EqualsAndHashCode(of = {"code"})
@Data
@NoArgsConstructor
public class Order implements IModel, Serializable {

    private static final long serialVersionUID = -779236471;

    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String code;

    @NotNull
    private Timestamp created;

    @Override
    public boolean isNew() {
        return null == id;
    }

}
